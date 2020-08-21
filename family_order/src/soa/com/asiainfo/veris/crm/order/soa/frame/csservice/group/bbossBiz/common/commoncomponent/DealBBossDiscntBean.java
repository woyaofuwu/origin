
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePricePlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * @description 处理特殊业务的资费问题，包括功能费和一次性费用
 * @author xunyl
 * @date 2014-06-11
 */
public class DealBBossDiscntBean
{

    private static final long serialVersionUID = 1L;

    private static final String PRO_WADEZ_CODE = "1112053320";// Z端对应省公司

    private static final String WADEA_MONTHFEE_PERCENT_ATTR_CODE = "1112054314";// 月功能费A端支付比例

    private static final String WADEA_ONCEFEE_PERCENT_ATTR_CODE = "1112054311";// 一次性费用A端支付比例

    private static final String WADEZ_ONCEFEE_PERCENT_ATTR_CODE = "1112054312";// 一次性费用Z端支付比例

    private static final String WADEZ_MONTHFEE_PERCENT_ATTR_CODE = "1112054315";// 月功能费Z端支付比例

    private static final String PRO_PAY_WAY = "1112053436";// 分省支付方式

    private static final String SPECIAL_LINE_MERCH_CODE = "01011301";// 跨省专线商品编号

    /**
     * @description 本省作为业务落地方时，往ICB参数中添加月功能费比例参数
     * @author xunyl
     * @date 2014-06-28
     */
    private static void addMonthFeePercent(String payPercent, IData secondProductInfo) throws Exception
    {

        // 1- 获取产品的资费计划列表
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        if (IDataUtil.isEmpty(ratePlanIdList))
        {
            return;
        }

        // 2- 专线业务的资费包括月功能费和免费资费，订购月功能费的场合需要新增支付比例参数(免费资费无需新增支付比例)
        for (int j = 0; j < ratePlanIdList.size(); j++)
        {
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
            if ("88".equals(ratePlanId))
            {
                IData thirdProductInfo = new DataMap();
                GrpCommonBean.getThirdProductInfo(j, secondProductInfo, thirdProductInfo);
                IDataset productIcbParamInfoList = new DatasetList();
                GrpCommonBean.getProductIcbParamInfo(thirdProductInfo, productIcbParamInfoList);
                IData productIcbParamInfo = new DataMap();
                productIcbParamInfo.put("ATTR_CODE", "MONTH_FEE_PERCENT");
                productIcbParamInfo.put("ATTR_VALUE", payPercent);
                productIcbParamInfo.put("ATTR_NAME", "月功能费本省支付比例");
                productIcbParamInfoList.add(productIcbParamInfo);
                break;
            }
        }
    }

    /**
     * @description 反向新增，添加一次性费用比例参数
     * @author xunyl
     * @date 2014-07-01
     */
    private static void addOnceFeePercent(int index, String payPercent, IData secondProductInfo, IData inparam) throws Exception
    {
        // 1- 判断当前产品是否包含了一次性费用
        IDataset ProductOrderChargecode = secondProductInfo.getDataset("PRODUCT_CHARGE_CODE");// 一次性费用编码
        IDataset ProductOrderChargeValue = secondProductInfo.getDataset("PRODUCT_CHARGE_VALUE");// 一次性费用属性值
        if (IDataUtil.isEmpty(ProductOrderChargecode) || IDataUtil.isEmpty(ProductOrderChargeValue))
        {
            return;
        }

        // 2- 添加一次性费用比例参数
        IDataset attrCodeList = IDataUtil.getDataset("RSRV_STR15", inparam);// 产品属性代码
        IDataset attrNameList = IDataUtil.getDataset("RSRV_STR17", inparam);// 产品属性名
        IDataset attrValueList = IDataUtil.getDataset("RSRV_STR16", inparam);// 产品属性值
        IDataset attrActionList = IDataUtil.getDataset("RSRV_STR18", inparam);// 产品属性操作代码
        IDataset attrCggroupList = IDataUtil.getDataset("CGROUP", inparam);// 产品属性组代码
        IDataset proAttrCodeList = IDataUtil.modiIDataset(attrCodeList.get(index), "RSRV_STR15");
        IDataset proAttrNameList = IDataUtil.modiIDataset(attrNameList.get(index), "RSRV_STR17");
        IDataset proAttrValueList = IDataUtil.modiIDataset(attrValueList.get(index), "RSRV_STR16");
        IDataset proAttrActionList = IDataUtil.modiIDataset(attrActionList.get(index), "RSRV_STR18");
        IDataset proAttrCggroupList = IDataUtil.modiIDataset(attrCggroupList.get(index), "CGROUP");
        proAttrCodeList.add("ONCE_FEE_PERCENT");
        proAttrNameList.add("一次性费用本省支付比例");
        proAttrValueList.add("payPercent");
        proAttrActionList.add(TRADE_MODIFY_TAG.Add.getValue());
        proAttrCggroupList.add("");
    }

    /**
     * @description 业务开展模式为有限公司一点受理，一点支付资费处理
     * @author xunyl
     * @date 2014-06-27
     */
    private static void dealBbossHandleBbossPay(String merchOperType, IData inparam) throws Exception
    {
        // 1- 商品操作类型非新增的场合，不做处理(么有考虑变更中预受理场景)
        if (!merchOperType.equals("1"))
        {
            return;
        }

        // 2- 反向新增，更改IBOSS入参，添加月功能费比例参数（该模式只可能是反向）
        addMonthFeePercent("0", inparam);
    }

    /**
     * @description 业务开展模式为主办省一点受理，一点支付资费处理
     * @author xunyl
     * @date 2014-06-27
     */
    private static void dealBossHandleBossPay(String merchOperType, String inModeCode, IData inparam) throws Exception
    {
        // 1- 商品操作类型非新增的场合，不做处理(么有考虑变更中预受理场景)
        if (!merchOperType.equals("1"))
        {
            return;
        }

        // 2- 获取产品列表
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(inparam, firstProductInfo);
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        if (IDataUtil.isEmpty(productNumberSet))
        {
            return;
        }

        // 3- 循环产品列表，为每个产品添加添加月功能费比例参数
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 3-1 校验产品是否订购了88套餐
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);
            if (!isOrdied88RatePlan(secondProductInfo))
            {
                continue;
            }

            // 3-2 往台帐表新增月功费比例数据
            IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
            String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i));
            String hostCompany = inparam.getString("PROVINCE", "");
            if ("0".equals(inModeCode) && ProvinceUtil.getProvinceCodeGrpCorp().equals(hostCompany))
            {// 正向新增，主办省场合
                insMonthFeePercent(productOfferingId, "100");
            }
            else if ("0".equals(inModeCode) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(hostCompany))
            {// 正向新增，配合省场合
                insMonthFeePercent(productOfferingId, "0");
                modOnceFeePercent(productOfferingId, "0");
            }
            else if ("6".equals(inModeCode) && ProvinceUtil.getProvinceCodeGrpCorp().equals(hostCompany))
            {// 反向新增，主办省场合
                addMonthFeePercent("100", secondProductInfo);
            }
            else if ("6".equals(inModeCode) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(hostCompany))
            {// 反向新增，配合省场合
                addMonthFeePercent("0", secondProductInfo);
                addOnceFeePercent(i, "0", secondProductInfo, inparam);
            }
        }
    }

    /**
     * @description 业务开展模式为分省支付资费处理
     * @author xunyl
     * @date 2014-06-30
     */
    private static void dealMixHandleMixPay(String merchOperType, String inModeCode, IData inparam) throws Exception
    {
        // 1- 商品操作类型为新增(opType=1)或者修改组成关系(opType =7)以外不做处理
        if (!merchOperType.equals("1") && !merchOperType.equals("7"))
        {
            return;
        }

        // 2- 获取产品列表
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(inparam, firstProductInfo);
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        if (IDataUtil.isEmpty(productNumberSet))
        {
            return;
        }

        // 3- 循环产品列表，分情况处理单个产品的月功能和一次性费用
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 3-1 分省支付，产品新增处理
            productUserOpen(i, inModeCode, firstProductInfo, inparam);

            // 3-2 分省支付，产品变更处理
            productUserChg(i, inModeCode, firstProductInfo);
        }
    }

    /**
     * @description 特殊商品资费处理
     * @author xunyl
     * @date 2014-06-27
     */
    public static void dealSpecialBizDiscnt(IData inparam, String inModeCode) throws Exception
    {
        // 1- 获取商品编号,非指定商品不做该处理
        String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", inparam).get(0);
        /*
        // 2- 跨省专线业务资费特殊处理
        if (poNumber.equals(SPECIAL_LINE_MERCH_CODE))
        {
            dealSpecialLineDiscnt(inparam, inModeCode);
        }
        */
        //3- 跨省专线2.0业务资费特殊处理
        if (poNumber.equals("01011306")){
            dealSpecialLineDiscnt(inparam, inModeCode);
        }
    }

    /**
     * @description 跨省专线业务资费特殊处理
     * @author xunyl
     * @date 2014-06-27
     */
    private static void dealSpecialLineDiscnt(IData inparam, String inModeCode) throws Exception
    {
        // 1- 获取业务开展模式
        String bizMode = inparam.getString("SI_BIZ_MODE", "");

        // 2- 获取商品操作类型
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", inparam, false).get(0);

        // 3- 业务开展模式为有限公司一点受理，一点支付
        if ("1".equals(bizMode))
        {
            dealBbossHandleBbossPay(merchOperType, inparam);
        }

        // 4- 业务开展模式为主办省一点受理一点支付
        if ("3".equals(bizMode))
        {
            dealBossHandleBossPay(merchOperType, inModeCode, inparam);
        }

        // 5- 业务开展模式为分省支付，2为有限公司一点受理分省支付，4为主办省一点受理，分省支付
        if ("2".equals(bizMode) && "4".equals(bizMode))
        {
            dealMixHandleMixPay(merchOperType, inModeCode, inparam);
        }
    }

    /**
     * @description 通过参数编号获取参数值
     * @author xunyl
     * @date 2014-07-01
     */
    private static String getAttrVaueByAttrCode(String attrCode, IData secondProductInfo) throws Exception
    {
        // 1- 定义参数值，初始化为""
        String attrValue = "";

        // 2- 获取产品对应的参数列表
        IDataset proAttrCodeList = secondProductInfo.getDataset("PARAM_CODE");// 产品属性代码
        IDataset attrValueList = secondProductInfo.getDataset("PARAM_VALUE");// 产品属性值
        if (IDataUtil.isEmpty(proAttrCodeList))
        {
            return attrValue;
        }

        // 3- 循环产品参数列表，获取对应的参数编号的值
        for (int i = 0; i < proAttrCodeList.size(); i++)
        {
            String proAttrCode = proAttrCodeList.get(i).toString();
            String proAttrValue = attrValueList.get(i).toString();
            if (proAttrCode.equals(attrCode))
            {
                attrValue = proAttrValue;
                break;
            }
        }

        // 4- 返回参数值
        return attrValue;
    }

    /**
     * @description 本省作为业务发起方时，往台帐表新增月功费比例数据
     * @author xunyl
     * @date 2014-06-28
     */
    private static void insMonthFeePercent(String productOfferingId, String payPercent) throws Exception
    {
        // 1- 获取产品的产品台帐信息
        IDataset merchpInfoList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);
        IData merchpInfo = merchpInfoList.getData(0);
        String productTradeId = merchpInfo.getString("TRADE_ID");

        // 2- 获取资费台帐信息
        IDataset discntInfoList = TradeDiscntInfoQry.getTradeDiscntInfosByDiscntCode(productTradeId, null);
        if (IDataUtil.isEmpty(discntInfoList))
        {
            return;
        }
        IData discntInfo = discntInfoList.getData(0);

        // 3- 拼装月功能费支付比例参数信息
        IData monthFeePercentParam = new DataMap();
        monthFeePercentParam.put("TRADE_ID", productTradeId);
        monthFeePercentParam.put("INST_TYPE", "D");
        monthFeePercentParam.put("RELA_INST_ID", discntInfo.getString("INST_ID"));
        monthFeePercentParam.put("INST_ID", SeqMgr.getInstId());
        monthFeePercentParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        monthFeePercentParam.put("ATTR_CODE", "MONTH_FEE_PERCENT");
        monthFeePercentParam.put("ATTR_VALUE", payPercent);
        monthFeePercentParam.put("START_DATE", discntInfo.getString("START_DATE"));
        monthFeePercentParam.put("END_DATE", discntInfo.getString("END_DATE"));
        monthFeePercentParam.put("USER_ID", discntInfo.getString("USER_ID"));
        monthFeePercentParam.put("ACCEPT_MONTH", discntInfo.getString("ACCEPT_MONTH"));
        monthFeePercentParam.put("UPDATE_DEPART_ID", discntInfo.getString("UPDATE_DEPART_ID"));
        monthFeePercentParam.put("UPDATE_STAFF_ID", discntInfo.getString("UPDATE_STAFF_ID"));
        monthFeePercentParam.put("UPDATE_TIME", discntInfo.getString("UPDATE_TIME"));

        // 4- 月功能费支付比例入表
        Dao.insert("TF_B_TRADE_ATTR", monthFeePercentParam,Route.getJourDb());
    }

    /**
     * @description 分省支付场合，判断是否需要终止月功能费比例处理
     * @author xunyl
     * @date 2014-06-30
     */
    private static boolean isMonthFeeNeedDeal(int index, String productOperType, IData firstProductInfo) throws Exception
    {
        // 1- 定义返回结果，默认为不终止
        boolean isMonthFeeNeedDeal = true;

        // 2- 产品操作类型为新增，没有订购月功能费的场合，处理终止
        IData secondProductInfo = new DataMap();
        GrpCommonBean.getSecondProductInfo(index, firstProductInfo, secondProductInfo);
        if (productOperType.equals("1"))
        {
            if (!isOrdied88RatePlan(secondProductInfo))
            {
                isMonthFeeNeedDeal = false;
                return isMonthFeeNeedDeal;
            }
        }

        // 3- 产品操作类型为产品参数变更，无A/Z端支付比例变化，处理终止
        if (productOperType.equals("9"))
        {
            // 3-1 获取产品参数列表
            IDataset proAttrCodeList = secondProductInfo.getDataset("PARAM_CODE");// 产品属性代码
            IDataset attrValueList = secondProductInfo.getDataset("PARAM_VALUE");// 产品属性值
            IDataset attrActionList = secondProductInfo.getDataset("PARAM_ACTION");
            if (IDataUtil.isEmpty(proAttrCodeList))
            {
                isMonthFeeNeedDeal = false;
                return isMonthFeeNeedDeal;
            }

            // 3-2 循环产品参数列表，获取A端支付比例的新值和老值(如果A端变化，Z端就会变化，因此只比较A端)
            String newValue = "";
            String oldValue = "";
            for (int i = 0; i < proAttrCodeList.size(); i++)
            {
                String attrCode = proAttrCodeList.get(i).toString();
                String attrAction = attrActionList.get(i).toString();
                String attrValue = attrValueList.get(i).toString();
                if (attrCode.equals(WADEZ_MONTHFEE_PERCENT_ATTR_CODE) && attrAction.equals("1"))
                {
                    newValue = attrValue;
                }
                else if (attrCode.equals(WADEZ_MONTHFEE_PERCENT_ATTR_CODE) && attrAction.equals("0"))
                {
                    oldValue = attrValue;
                }
            }

            // 3-3 新老值不一致的场合需要调整月功能费，否则处理终止
            if (StringUtils.equals(newValue, oldValue))
            {
                isMonthFeeNeedDeal = false;
                return isMonthFeeNeedDeal;
            }
        }

        // 4- 产品操作类型为产品资费变更，而且是删除月功能费的场合，处理终止
        if (productOperType.equals("5"))
        {
            // 4-1 获取资费信息列表
            IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
            IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");
            if (IDataUtil.isEmpty(ratePlanIdList))
            {
                isMonthFeeNeedDeal = false;
                return isMonthFeeNeedDeal;
            }

            // 4-2 循环资费信息列表，88套餐的状态为删除的场合，处理终止
            for (int j = 0; j < ratePlanIdList.size(); j++)
            {
                String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
                String ratePlanAction = GrpCommonBean.nullToString(ratePlanActionList.get(j));
                if ("88".equals(ratePlanId) && "0".equals(ratePlanAction))
                {
                    isMonthFeeNeedDeal = false;
                    return isMonthFeeNeedDeal;
                }
            }
        }

        // 5- 返回处理结果
        return isMonthFeeNeedDeal;
    }

    /**
     * @description 查询客户是否订购88套餐
     * @author xunyl
     * @date 2014-06-28
     */
    private static boolean isOrdied88RatePlan(IData secondProductInfo) throws Exception
    {
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        if (IDataUtil.isEmpty(ratePlanIdList))
        {
            return false;
        }
        for (int j = 0; j < ratePlanIdList.size(); j++)
        {
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
            if ("88".equals(ratePlanId))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @description 正向新增，插入台帐表一次性费用值
     * @author xunyl
     * @date 2014-07-01
     */
    private static void modOnceFeePercent(String productOfferingId, String payPercent) throws Exception
    {
        // 1- 获取产品的产品台帐信息
        IDataset merchpInfoList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);
        IData merchpInfo = merchpInfoList.getData(0);
        String productTradeId = merchpInfo.getString("TRADE_ID");

        // 2- 获取并修改一次性费用台帐信息
        IDataset tradeAttrInfoList = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(productTradeId, "ONCEFEE_08", null);
        if (IDataUtil.isNotEmpty(tradeAttrInfoList))
        {
            IData tradeAttrInfo = tradeAttrInfoList.getData(0);
            String monthfee = tradeAttrInfo.getString("RSRV_STR4");
            int money = (Integer.parseInt(monthfee) * Integer.parseInt(payPercent)) / 100;
            tradeAttrInfo.put("MONEY", money);
            Dao.update("TF_B_TRADE_ATTR", tradeAttrInfo);
        }
    }

    /**
     * @description 分省支付，产品变更处理
     * @author xunyl
     * @date 2014-06-30
     */
    private static void productUserChg(int index, String inModeCode, IData firstProductInfo) throws Exception
    {
        // 1- 获取产品二级信息
        IData secondProductInfo = new DataMap();
        GrpCommonBean.getSecondProductInfo(index, firstProductInfo, secondProductInfo);

        // 2- 获取产品台帐编号
        IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(index));
        IDataset merchpInfoList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);
        IData merchpInfo = merchpInfoList.getData(0);
        String productTradeId = merchpInfo.getString("TRADE_ID");

        // 3- 获取支付方式
        String payWay = "";
        IDataset tradeAttrInfoList = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(productTradeId, PRO_PAY_WAY, null);
        if (IDataUtil.isNotEmpty(tradeAttrInfoList))
        {
            payWay = tradeAttrInfoList.getData(0).getString("ATTR_VALUE");
        }

        // 4- 获取省BOSS所处专线的A端/Z端
        String wadeZ = "";
        tradeAttrInfoList = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(productTradeId, PRO_WADEZ_CODE, null);
        if (IDataUtil.isNotEmpty(tradeAttrInfoList))
        {
            wadeZ = tradeAttrInfoList.getData(0).getString("ATTR_VALUE");
        }

        // 5- 获取当前省得支付比例
        String monthFeePayPercent = "0";
        String onceFeePayPercent = "0";
        if ("3".equals(payWay) && ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// A/Z端分省支付&&省BOSS为Z端
            monthFeePayPercent = getAttrVaueByAttrCode(WADEZ_MONTHFEE_PERCENT_ATTR_CODE, secondProductInfo);
            onceFeePayPercent = getAttrVaueByAttrCode(WADEZ_ONCEFEE_PERCENT_ATTR_CODE, secondProductInfo);
        }
        else if ("3".equals(payWay) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// A/Z端分省支付&&省BOSS为A端
            monthFeePayPercent = getAttrVaueByAttrCode(WADEA_MONTHFEE_PERCENT_ATTR_CODE, secondProductInfo);
            onceFeePayPercent = getAttrVaueByAttrCode(WADEA_ONCEFEE_PERCENT_ATTR_CODE, secondProductInfo);
        }
        else
        {
            return;
        }

        // 6- 判断是否需要处理月租费比例
        IDataset productOperList = firstProductInfo.getDataset("PRODUCT_OPER");
        String productOperType = GrpCommonBean.nullToString(productOperList.get(index));
        boolean isMonthFeeNeedDeal = isMonthFeeNeedDeal(index, productOperType, firstProductInfo);

        // 3- 省BOSS为发起方场合(正向处理)
        String userId = merchpInfo.getString("USER_ID");
        IDataset userAttrInfoList = UserAttrInfoQry.getUserAttr(userId, "P", "MONTH_FEE_PERCENT", null);
        if (IDataUtil.isNotEmpty(userAttrInfoList) && isMonthFeeNeedDeal)
        {
            IData userAttrInfo = userAttrInfoList.getData(0);
            userAttrInfo.put("TRADE_ID", productTradeId);
            userAttrInfo.put("ATTR_VALUE", monthFeePayPercent);
            userAttrInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            userAttrInfo.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
            Dao.insert("TF_B_TRADE_ATTR", userAttrInfo,Route.getJourDb());
            modOnceFeePercent(productOfferingId, onceFeePayPercent);
        }
        else if (IDataUtil.isNotEmpty(userAttrInfoList))
        {
            modOnceFeePercent(productOfferingId, onceFeePayPercent);
        }

        // 4- 省BOSS为落地方场合(反向处理,该场景暂时无法实现)
    }

    /**
     * @description 分省支付，产品新增处理
     * @author xunyl
     * @date 2014-06-30
     */
    private static void productUserOpen(int index, String inModeCode, IData firstProductInfo, IData inparam) throws Exception
    {
        // 1- 获取产品二级信息
        IData secondProductInfo = new DataMap();
        GrpCommonBean.getSecondProductInfo(index, firstProductInfo, secondProductInfo);

        // 2- 获取支付方式、省BOSS所处专线的A端/Z端、A/Z端支付比例的参数值
        String payWay = getAttrVaueByAttrCode(PRO_PAY_WAY, secondProductInfo);
        String wadeZ = getAttrVaueByAttrCode(PRO_WADEZ_CODE, secondProductInfo);

        // 3- 根据支付方式和省BOSS所处专线的A端/Z端获取月功能费比例(没有合并判断条件是为了更加清晰)
        String monthFeePayPercent = "0";
        String onceFeePayPercent = "0";
        if ("1".equals(payWay) && ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// A端全额支付&&省BOSS为Z端
            monthFeePayPercent = "0";
            onceFeePayPercent = "0";
        }
        else if ("1".equals(payWay) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// A端全额支付&&省BOSS为A端
            monthFeePayPercent = "100";
            onceFeePayPercent = "100";
        }
        else if ("2".equals(payWay) && ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// Z端全额支付&&省BOSS为Z端
            monthFeePayPercent = "100";
            onceFeePayPercent = "100";
        }
        else if ("2".equals(payWay) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// Z端全额支付&&省BOSS为A端
            monthFeePayPercent = "0";
            onceFeePayPercent = "0";
        }
        else if ("3".equals(payWay) && ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// A/Z端分省支付&&省BOSS为Z端
            monthFeePayPercent = getAttrVaueByAttrCode(WADEZ_MONTHFEE_PERCENT_ATTR_CODE, secondProductInfo);
            onceFeePayPercent = getAttrVaueByAttrCode(WADEZ_ONCEFEE_PERCENT_ATTR_CODE, secondProductInfo);
        }
        else if ("3".equals(payWay) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(wadeZ))
        {// A/Z端分省支付&&省BOSS为A端
            monthFeePayPercent = getAttrVaueByAttrCode(WADEA_MONTHFEE_PERCENT_ATTR_CODE, secondProductInfo);
            onceFeePayPercent = getAttrVaueByAttrCode(WADEA_ONCEFEE_PERCENT_ATTR_CODE, secondProductInfo);
        }

        // 4- 判断是否需要处理月租费比例
        IDataset productOperList = firstProductInfo.getDataset("PRODUCT_OPER");
        String productOperType = GrpCommonBean.nullToString(productOperList.get(index));
        boolean isMonthFeeNeedDeal = isMonthFeeNeedDeal(index, productOperType, firstProductInfo);

        // 5- 省BOSS为发起方场合(正向处理)
        IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(index));
        if ("0".equals(inModeCode) && isMonthFeeNeedDeal)
        {
            insMonthFeePercent(productOfferingId, monthFeePayPercent);
            modOnceFeePercent(productOfferingId, onceFeePayPercent);
        }
        else if ("0".equals(inModeCode))
        {
            modOnceFeePercent(productOfferingId, onceFeePayPercent);
        }

        // 6- 省BOSS为落地方场合(反向处理)
        if ("6".equals(inModeCode) && isMonthFeeNeedDeal)
        {
            addMonthFeePercent(monthFeePayPercent, secondProductInfo);
            addOnceFeePercent(index, onceFeePayPercent, secondProductInfo, inparam);
        }
        else if ("6".equals(inModeCode))
        {
            addOnceFeePercent(index, onceFeePayPercent, secondProductInfo, inparam);
        }
    }
    
    /**
     * @description 跨省专线2.0，反向调用新增资费信息
     * @author wangzc7
     * @date 2017-07-10
     */
    public static void dealSpecialBizDiscnt2(IData map, String string)throws Exception {
		String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0);
        //判断是否为跨省专线2.0业务
        if(poNumber.equals("01011306")){
        	IData firstProductInfo = new DataMap();
            GrpCommonBean.getFirstProductInfo(map, firstProductInfo);
            
            //收集入表信息
            IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
            for (int i = 0; i < productNumberSet.size(); i++)
            {
                // 获取台账信息
                IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
                String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i));
                //获取orderId和productId信息
                IDataset orderIdList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);
                IData orderIdInfo = orderIdList.getData(0);
                String tradeId = orderIdInfo.getString("TRADE_ID");
                //获取merchpInfo信息
                IDataset merchpInfoList = TradeGrpMerchpInfoQry.qryGrpMerchpByTradeId(tradeId, null);
                IData merchpInfo = merchpInfoList.getData(0);
                //资费信息入表
                IData secondProductInfo = new DataMap();
                GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);
                insertDiscntInfo(secondProductInfo,merchpInfo,orderIdInfo);             
            }
        }
	}

    /**
     * @description 跨省专线2.0，资费信息入表
     * @author wangzc7
     * @date 2017-07-10
     */
	private static void insertDiscntInfo(IData secondProductInfo,IData merchpInfo,IData orderIdInfo)throws Exception{
		// 获取资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");// 资费计划标识
        IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");// 产品级资费操作代码
		
        //获取台账信息
        String tradeId = orderIdInfo.getString("TRADE_ID");
        //String orderId = orderIdInfo.getString("ORDER_ID");
        String productId = orderIdInfo.getString("PRODUCT_ID");
        String productUserId = orderIdInfo.getString("USER_ID");
        String merchSpecCode = merchpInfo.getString("MERCH_SPEC_CODE");
        String productOrderId = merchpInfo.getString("PRODUCT_ORDER_ID");
        String productSpecCode = merchpInfo.getString("PRODUCT_SPEC_CODE");
        String productOfferId = merchpInfo.getString("PRODUCT_OFFER_ID");
        String merchInstId = merchpInfo.getString("INST_ID");//商品INST_ID
        String endDate = merchpInfo.getString("END_DATE");
        
        //根据资费信息进行插表
        for (int i = 0; i < ratePlanIdList.size(); i++) {
        	String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(i));
        	String ratePlanAction = GrpCommonBean.nullToString(ratePlanActionList.get(i));
        	
        	//获取ICB参数信息
        	IData thirdProductInfo = new DataMap();
            GrpCommonBean.getThirdProductInfo(i, secondProductInfo, thirdProductInfo);
            IDataset icbCodeList = thirdProductInfo.getDataset("ICB_PARAM_CODE");//
            IDataset icbNameList = thirdProductInfo.getDataset("ICB_PARAM_NAME");// 产品级资费操作代码
            IDataset icbValueList = thirdProductInfo.getDataset("ICB_PARAM_VALUE");// 资费描述
            
        	//判断是否为新增
//        	if("1".equals(ratePlanAction)){
        		//获取省内的element_id
        		IDataset element = getElemetInfo(ratePlanId,productId);
            	String elementId = element.getData(0).getString("ATTR_CODE");
            	//获取对应的包信息
            	IDataset packageData = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId,elementId, "Y");
            	String packageId = "";
            	if (IDataUtil.isNotEmpty(packageData))
                {
                    packageId = packageData.getData(0).getString("PACKAGE_ID","");
                }
            			           	
            	//入表TF_B_TRADE_DISCNT
            	IData discntTradeInfo = GrpCommonBean.getDiscntTradeInfo(tradeId, productUserId, productId, packageId, elementId, endDate);
            	IDataset reteInfos=new DatasetList();
            	if("0".equals(ratePlanAction)){//删除
            		 reteInfos = UserDiscntInfoQry.getUserPlatDiscnt(productUserId, elementId);
            	    if (null == reteInfos || reteInfos.size() == 0)
            	    {
            	        return;
            	    }
            		discntTradeInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
            		discntTradeInfo.put("INST_ID",reteInfos.getData(0).getString("INST_ID"));
            		discntTradeInfo.put("END_DATE",SysDateMgr.getSysTime());
            	}       
            	TradeDiscntInfoQry.insertDiscntInfo(discntTradeInfo);
                
                //入表TF_B_TRADE_GRP_MERCHP_DISCNT
                String instId = discntTradeInfo.getString("INST_ID");
                IData merchpDiscntTradeInfo = GrpCommonBean.getMerchpDiscntTradeInfo(tradeId, merchSpecCode, productOrderId, productOfferId, productSpecCode, ratePlanId, productUserId, instId, endDate, "1");
                if("0".equals(ratePlanAction)){//删除
                	
            		IDataset merchpDiscntInfo = UserGrpMerchpDiscntInfoQry.qryMerchpDiscntByUseridMerchScPrdouctScProductDc(productUserId, merchSpecCode, productSpecCode, ratePlanId, null);
            	    if (null == merchpDiscntInfo || merchpDiscntInfo.size() == 0)
            	    {
            	        return;
            	    }
            	    merchpDiscntTradeInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
            	    merchpDiscntTradeInfo.put("INST_ID",merchpDiscntInfo.getData(0).getString("INST_ID"));
            	    merchpDiscntTradeInfo.put("RSRV_STR1",GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
            	    merchpDiscntTradeInfo.put("END_DATE",SysDateMgr.getSysTime());
            	}  
                TradeGrpMerchpDiscntInfoQry.insertMerchpDiscntInfo(merchpDiscntTradeInfo);
                
                //ICB信息入表TF_B_TRADE_ATTR
                for (int j = 0; j < icbCodeList.size(); j++)
                {
                    String relaInstId = discntTradeInfo.getString("INST_ID");
                    String attrCode = GrpCommonBean.nullToString(icbCodeList.get(j));
                    String attrName = GrpCommonBean.nullToString(icbNameList.get(j));
                    String attrValue = GrpCommonBean.nullToString(icbValueList.get(j));
                    String isneedpf = "";
                    IData icbTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, null, attrName, attrValue, isneedpf, endDate, "D");
                    if("0".equals(ratePlanAction)){//删除
                    	relaInstId=reteInfos.getData(0).getString("INST_ID");
                    	IData icbInfo = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(productUserId, relaInstId, attrCode, "");
                	    if (null == icbInfo || icbInfo.size() == 0)
                	    {
                	        return;
                	    }
                	    icbTradeInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                	    icbTradeInfo.put("INST_ID",icbInfo.getString("INST_ID"));
                	    icbTradeInfo.put("RSRV_STR5",GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
                	    icbTradeInfo.put("END_DATE",SysDateMgr.getSysTime());
                	    icbTradeInfo.put("RELA_INST_ID", relaInstId);
                	} 
                    TradeAttrInfoQry.insertAttrInfo(icbTradeInfo);
                }
                
                //入表TF_B_TRADE_PRICE_PLAN
                IDataset pricePlans = UpcCall.qryPricePlanInfoByOfferId(discntTradeInfo.getString("DISCNT_CODE"), BofConst.ELEMENT_TYPE_CODE_DISCNT);
                if (IDataUtil.isNotEmpty(pricePlans))
                {
                	for(Object obj : pricePlans)
                    {
                        IData pricePlan = (IData)obj;
                        String pricePlanType = pricePlan.getString("PRICE_PLAN_TYPE");                      
                        if(!"1".equals(pricePlanType))
                        {
                            continue;
                        }                        
                        IData tradePricePlanData = new DataMap();
                        tradePricePlanData.put("TRADE_ID", discntTradeInfo.getString("TRADE_ID"));
                        tradePricePlanData.put("ACCEPT_MONTH", discntTradeInfo.getString("TRADE_ID").substring(4, 6));
                        tradePricePlanData.put("USER_ID", discntTradeInfo.getString("USER_ID"));
                        tradePricePlanData.put("USER_ID_A","-1");
                        tradePricePlanData.put("PRICE_PLAN_ID", pricePlan.getString("PRICE_PLAN_ID"));
                        tradePricePlanData.put("INST_ID", SeqMgr.getInstId());
                        tradePricePlanData.put("BILLING_CODE", pricePlan.getString("REF_BILLING_ID"));
                        tradePricePlanData.put("PRICE_PLAN_TYPE", pricePlan.getString("PRICE_PLAN_TYPE"));
                        tradePricePlanData.put("FEE_TYPE_CODE", pricePlan.getString("FEE_TYPE_CODE"));
                        tradePricePlanData.put("FEE_TYPE", pricePlan.getString("FEE_TYPE"));
                        tradePricePlanData.put("FEE", pricePlan.getString("FEE"));
                        tradePricePlanData.put("PRICE_ID", pricePlan.getString("PRICE_ID"));
                        tradePricePlanData.put("RELATION_TYPE_CODE", discntTradeInfo.getString("RELATION_TYPE_CODE"));
                        tradePricePlanData.put("SPEC_TAG", discntTradeInfo.getString("SPEC_TAG"));
                        tradePricePlanData.put("START_DATE", discntTradeInfo.getString("START_DATE"));
                        tradePricePlanData.put("END_DATE", discntTradeInfo.getString("END_DATE"));
                        tradePricePlanData.put("OFFER_INS_ID", discntTradeInfo.getString("INST_ID"));
                        tradePricePlanData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                        tradePricePlanData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);                        
                        tradePricePlanData.put("UPDATE_TIME",discntTradeInfo.getString("UPDATE_TIME"));
                        tradePricePlanData.put("UPDATE_STAFF_ID",discntTradeInfo.getString("UPDATE_STAFF_ID"));
                        tradePricePlanData.put("UPDATE_DEPART_ID",discntTradeInfo.getString("UPDATE_DEPART_ID"));
                        tradePricePlanData.put("REMARK","跨省专线2.0！");
                        if("0".equals(ratePlanAction)){//删除
                        	IDataset tradePriceInfo=TradePricePlanInfoQry.getPricePlanByPriceId(discntTradeInfo.getString("USER_ID"),pricePlan.getString("PRICE_ID"),pricePlan.getString("PRICE_PLAN_ID"));
                        	 if (null == tradePriceInfo || tradePriceInfo.size() == 0)
                     	    {
                     	        return;
                     	    }
                        	tradePricePlanData.put("INST_ID", tradePriceInfo.getData(0).getString("INST_ID"));
                        	tradePricePlanData.put("END_DATE", SysDateMgr.getSysDate());
                        	tradePricePlanData.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL); 
                        }
                        Dao.insert("TF_B_TRADE_PRICE_PLAN", tradePricePlanData, Route.getJourDb());
                    }
                }
                
                //入表TF_B_TRADE_OFFER_REL
                IData offerRelInfos = new DataMap();
                offerRelInfos.put("TRADE_ID", tradeId);
                offerRelInfos.put("INST_ID", SeqMgr.getInstId());
                offerRelInfos.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
                offerRelInfos.put("OFFER_CODE", productId);
                offerRelInfos.put("OFFER_TYPE", "P");//产品
                offerRelInfos.put("OFFER_INS_ID", merchInstId);
                offerRelInfos.put("USER_ID", productUserId);
                offerRelInfos.put("GROUP_ID", packageId);
                offerRelInfos.put("REL_OFFER_CODE", elementId);
                offerRelInfos.put("REL_OFFER_TYPE", "D");//资费
                offerRelInfos.put("REL_OFFER_INS_ID", instId);
                offerRelInfos.put("REL_USER_ID", productUserId);
                offerRelInfos.put("REL_TYPE", "C");//关系类型 C-构成关系,组关系;L-连带关系
                offerRelInfos.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
                offerRelInfos.put("END_DATE", endDate);
                offerRelInfos.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                offerRelInfos.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
                offerRelInfos.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                offerRelInfos.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                offerRelInfos.put("REMARK", "跨省专线2.0");
                if("0".equals(ratePlanAction)){//删除
                	IDataset tradeOfferInfo=TradeOfferRelInfoQry.getOfferInfoByUserId(productUserId,elementId);
                	 if (null == tradeOfferInfo || tradeOfferInfo.size() == 0)
             	    {
             	        return;
             	    }
                	 offerRelInfos.put("INST_ID", tradeOfferInfo.getData(0).getString("INST_ID"));
                	 offerRelInfos.put("END_DATE", SysDateMgr.getSysDate());
                	 offerRelInfos.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL); 
                }
                Dao.insert("TF_B_TRADE_OFFER_REL", offerRelInfos, Route.getJourDb());            
                
                // 将TF_B_TRADE_GRP_MERCHP_DISCNT,TF_B_TRADE_DISCNT,TF_B_TRADE_OFFER_REL,TF_B_TRADE_PRICE_PLAN,TF_B_TRADE_ATTR
                //加入TF_B_TRADE的INTF_ID中
                dealTradeIntfId(tradeId);
        	}
//		}
	}

	/**
     * @description 跨省专线2.0，获取对应的资费信息
     * @author wangzc7
     * @date 2017-07-10
     */
	private static IDataset getElemetInfo(String rateId, String productId)throws Exception {
		 IDataset ids = new DatasetList();
	        IDataset localInfo = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "DIS", rateId, null);
	        if (IDataUtil.isEmpty(localInfo))
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_900, rateId);
	        }
	        for (int i = 0, sizeI = localInfo.size(); i < sizeI; i++)
	        {
	            IData data = localInfo.getData(i);
	            String attr_code = data.getString("ATTR_CODE");
	            // 场景1 根据集团资费查找本地packageId对应是集团套餐编码(attr_biz表的attr_value配置为对应商品的集团资费套餐编码package_id)
	            //为集团提供 为产品编码，包ID。查询包信息
	            IDataset packageInfos = UpcCall.queryOfferGroupRelOfferIdGroupId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId,attr_code, "Y");
	            if (IDataUtil.isNotEmpty(packageInfos))
	            {
	                ids.add(data);
	                break;
	            }
	            else
	            {
	                // 场景2 根据集团资费查找本地元素 element对应是集团资费编码(attr_biz表的attr_value配置为对应产品的集团资费编码element_id)
	            	//为集团提供 为产品编码，元素编码。查询元素信息
	                IDataset elementInfos = UpcCall.qryOfferByOfferIdRelOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, attr_code, BofConst.ELEMENT_TYPE_CODE_DISCNT);
	                if (IDataUtil.isNotEmpty(elementInfos))
	                {
	                    ids.add(data);
	                    break;
	                }
	            }
	        }
	        
	        return ids;
	}

	/**
     * @description 跨省专线2.0，更新TF_B_TRADE的INTF_ID
     * @author wangzc7
     * @date 2017-07-10
     */
	private static void dealTradeIntfId(String tradeId)throws Exception {
		IDataset tradeInfoSet = TradeInfoQry.getMainTradeByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeInfoSet))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData map = tradeInfoSet.getData(0);
        String intfId = map.getString("INTF_ID", "");
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_GRP_MERCHP_DISCNT") == -1)
        {
            intfId += "TF_B_TRADE_GRP_MERCHP_DISCNT,";
        }
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_DISCNT") == -1)
        {
            intfId += "TF_B_TRADE_DISCNT,";
        }
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_OFFER_REL") == -1)
        {
            intfId += "TF_B_TRADE_OFFER_REL,";
        }
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_PRICE_PLAN") == -1)
        {
            intfId += "TF_B_TRADE_PRICE_PLAN,";
        }
        if (StringUtils.indexOf(intfId, "TF_B_TRADE_ATTR") == -1)
        {
            intfId += "TF_B_TRADE_ATTR,";
        }
        map.clear();
        map.put("TRADE_ID", tradeId);
        map.put("INTF_ID", intfId);
        TradeInfoQry.updateTradeIntfID(map);
	}
	
    public static void dealJKDTSpecialBizDiscnt2(IData map, String string)throws Exception {
		String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0);
        //判断是否为跨省专线2.0业务
        if(poNumber.equals("01011306")){
        	IData firstProductInfo = new DataMap();
            GrpCommonBean.getFirstProductInfo(map, firstProductInfo);
            
            //收集入表信息
            IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
            for (int i = 0; i < productNumberSet.size(); i++)
            {
                // 获取台账信息
                IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
                String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i));
                //获取orderId和productId信息
                IDataset orderIdList = TradeGrpMerchpInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferingId, null);
                IData orderIdInfo = orderIdList.getData(0);
                String tradeId = orderIdInfo.getString("TRADE_ID");
                //获取merchpInfo信息
                IDataset merchpInfoList = TradeGrpMerchpInfoQry.qryJKDTMerchpInfoByTradeId(tradeId);
                IData merchpInfo = merchpInfoList.getData(0);
                //资费信息入表
                IData secondProductInfo = new DataMap();
                GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);
                insertDiscntInfo(secondProductInfo,merchpInfo,orderIdInfo);             
            }
        }
	}
}
