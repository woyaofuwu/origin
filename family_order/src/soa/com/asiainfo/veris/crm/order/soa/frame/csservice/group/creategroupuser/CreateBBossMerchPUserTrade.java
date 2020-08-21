
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class CreateBBossMerchPUserTrade extends CreateGroupUser
{
    /*
     * @description 获取一次性费用比例
     * @author xunyl
     * @date 2013-10-20
     */
    protected static IData getOneFeePercentInfo(IDataset productParamInfoList) throws Exception
    {
        // 1- 定义返回数据
        IData onceFeePercentParam = new DataMap();

        // 2- 循环获取一次性费用比例参数
        for (int i = 0; i < productParamInfoList.size(); i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String attrCode = productParamInfo.getString("ATTR_CODE");
            if (("ONCEFEEPERCENT").equals(attrCode))
            {
                onceFeePercentParam = productParamInfo;
                break;
            }
        }

        // 3- 返回结果
        return onceFeePercentParam;
    }

    protected CreateBBossUserReqData reqData = null;

    /**
     * BBOSS建立产品台账信息时，由于账户信息已经存在，所以无需再次插账户信息表
     *
     * @author xunyl
     */
    public void actTradeAcct() throws Exception
    {
        return;
    }

    /*
     * @description 将一次性费用同步计费和账务(典型场景：跨省专线业务中的一次性费用项)
     * @author xunyl
     * @date 2013-10-20
     */
    private void actTradeFeeDefer() throws Exception
    {
        // 1- 获取产品编号
        String productId = reqData.getUca().getProductId();

        // 2- 根据产品编号获取产品参数
        IDataset productParamInfoList = reqData.cd.getProductParamList(productId);
        if (IDataUtil.isEmpty(productParamInfoList))
        {
            return;
        }

        // 3- 循环产品参数，将一次性费用插入账务表
        IDataset oneFeeParamList = new DatasetList();
        IData onceFeePercentParam = getOneFeePercentInfo(productParamInfoList); // 新增的一次性费用支付比例属性
        int onceFeePercent = (IDataUtil.isEmpty(onceFeePercentParam)) ? 100 : Integer.parseInt(onceFeePercentParam.getString("PARAM_VALUE"));
        for (int i = 0; i < productParamInfoList.size(); i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String attrCode = productParamInfo.getString("ATTR_CODE");
            String attrValue = productParamInfo.getString("ATTR_VALUE", "");
            String attrName = productParamInfo.getString("");
            String state = productParamInfo.getString("STATE", "");
            if (!state.equals("ADD") || !attrCode.startsWith("ONCEFEE_") || "".equals(attrValue) || isNoNeedOnceFee(productId))
            {
                continue;
            }
            String feeTypeCode = dealOnceFeeTypeCode(attrCode);
            IData onceFeeData = new DataMap();
            onceFeeData.put("FEE_MODE", "0");// 费用类型：0-营业费用，1-押金，2-预存
            onceFeeData.put("FEE_TYPE_CODE", attrCode.substring(8));// 费用类型编码：可以为营业费用项、押金或存折类型的编码
            onceFeeData.put("DEFER_CYCLE_ID", "-1");
            onceFeeData.put("DEFER_ITEM_CODE", feeTypeCode);
            int money = (Integer.parseInt(attrValue) * onceFeePercent) / 100;
            onceFeeData.put("MONEY", money); // 实缴金额
            onceFeeData.put("ACT_TAG", "1");
            onceFeeData.put("USER_ID", reqData.getUca().getUserId());
            onceFeeData.put("REMARK", "BBOSS一次性费用");
            onceFeeData.put("RSRV_STR1", "ONCEFEE"); // 标识为BBOSS一次性费用
            onceFeeData.put("RSRV_STR2", attrName); // 一次性费用名称
            onceFeeData.put("RSRV_STR3", onceFeePercent);// 一次性费用省内支付比例
            onceFeeData.put("RSRV_STR4", attrValue);// 一次性费用全额
            oneFeeParamList.add(onceFeeData);
        }
        if (IDataUtil.isNotEmpty(oneFeeParamList))
        {
            super.addTradefeeDefer(oneFeeParamList);
        }
    }

    private String dealOnceFeeTypeCode(String attrCode) throws Exception
    {
        String onceFeeTypeCode = "";
        IDataset onceInfo = StaticUtil.getStaticList("BBOSS_ONCEFEE");
        for (int i = 0; i < onceInfo.size(); i++)
        {
            IData temp = onceInfo.getData(i);
            if (StringUtils.equals(attrCode.substring(8), temp.getString("DATA_NAME")) )
            {
                onceFeeTypeCode = temp.getString("DATA_ID");
            }
        }
        if (StringUtils.isEmpty(onceFeeTypeCode))
        {
//            CSAppException.apperr(CrmCommException.CRM_COMM_579,"一次性费用配置缺失");
            onceFeeTypeCode = "999";
        }
        return onceFeeTypeCode;
    }
    
    /*
     * @description 配置需要同步计费和账务的产品(该方法用来过滤一次性费用通过两极账单收
     * 但是受理报文中还是会下发一次性费用节点，故进行过滤掉)
     * @author songxw
     * @date 2018-4-24
     */
    private Boolean isNoNeedOnceFee(String productId) throws Exception
    {
    	IDataset onceInfo = StaticUtil.getStaticList("BBOSS_ONCEFEE_NONEED",productId);
        if (IDataUtil.isNotEmpty(onceInfo))
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }

    /*
     * @description 登记产品子表
     * @remark 该方法的作用是等级产品及参数台帐信息，正常情况由基类来实现，这里重写该方法是BBOSS的属性需要登记在自己的表中， 因此产品信息的登记保持跟基类的一致，特别需要注意的是基类方法更改的情况下，这里就要随之更改
     * @author xunyl
     * @date 2013-05-25
     */
    public void actTradePrdAndPrdParams() throws Exception
    {
        IData productIdset = reqData.cd.getProductIdSet();

        // 添加主产品信息
        productIdset.put(reqData.getUca().getProductId(), TRADE_MODIFY_TAG.Add.getValue());

        IDataset productInfoset = new DatasetList();
        Iterator<String> iterator = productIdset.keySet().iterator();
        while (iterator.hasNext())
        {
            String productId = iterator.next();

            String productMode = UProductInfoQry.getProductModeByProductId(productId);

            if (GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue().equals(productMode) || GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue().equals(productMode))
            {
                IData productPlus = new DataMap();

                productPlus.put("PRODUCT_ID", productId); // 产品标识
                productPlus.put("PRODUCT_MODE", productMode); // 产品的模式

                String instId = SeqMgr.getInstId();

                productPlus.put("INST_ID", instId); // 实例标识
                productPlus.put("START_DATE", getAcceptTime()); // 开始时间
                productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                productPlus.put("USER_ID", reqData.getUca().getUser().getUserId());
                // 如果是集团产品，则需要设置
                productPlus.put("MAIN_TAG", productPlus.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
                productInfoset.add(productPlus);

                // 产品参数
                if (GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue().equals(productMode))
                {
                    IDataset productParam = reqData.cd.getProductParamList(productId);

                    if (productParam != null && productParam.size() > 0)
                    {
                        IDataset dataset = new DatasetList();

                        for (int i = 0; i < productParam.size(); i++)
                        {
                            IData paramData = productParam.getData(i);
                            String attrCode = paramData.getString("ATTR_CODE");
                            String attrValue = paramData.getString("ATTR_VALUE", "");

                            IData map = new DataMap();
                            map.put("INST_TYPE", "P");
                            map.put("RELA_INST_ID", instId);
                            map.put("INST_ID", SeqMgr.getInstId());
                            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            map.put("ATTR_CODE", attrCode);
                            // 文件上传成功的情况下，给文件名添加后缀
                            map.put("ATTR_VALUE", checkParamFileUpLoad(attrCode, attrValue));
                            map.put("START_DATE", getAcceptTime());
                            map.put("END_DATE", SysDateMgr.getTheLastTime());
                            map.put("USER_ID", reqData.getUca().getUser().getUserId());
                            // 设置INX下标值和属性组编号，INX作为主键使用，用于唯一性判断
                            map.put("RSRV_STR4", paramData.getString("ATTR_GROUP"));
                            map.put("RSRV_STR3", paramData.getString("ATTR_NAME"));
                            map.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧参数状态，服开拼报文用
                            dataset.add(map);
                        }

                        super.addTradeAttr(dataset);
                    }
                }
            }
        }

        super.addTradeProduct(productInfoset);
    }

    /**
     * 生成BB关系表及BBOSS侧的产品信息和资费信息表
     *
     * @author xunyl
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记BB关系表的的台账信息
        IData reletionData = this.getReletionBBMap();
        super.addTradeRelationBb(reletionData);

        // 3- 登记业务台帐BBOSS产品用户订购表子表、优惠子表
        this.infoRegDataEntireMerchP();
        super.addTradeGrpMerchp(reqData.getMerchp());

        // 4- 登记other表，服务开通侧用
        this.infoRegDataOther();

        // 5- 登记other表，计费用
        this.infoRegDataOtherToBilling();

        // 6- 登记一次性费用
        this.actTradeFeeDefer();

        // 7-一些业务需要登记特殊表
        infoRegDataSpecial();
    }

    protected void actTradeAccountInfo() throws Exception
    {

    }

    /*
     * @description 校验产品参数附件是否上传
     * @author xunyl
     * @date 2013-12-03
     */
    protected String checkParamFileUpLoad(String attrCode, String attrValue) throws Exception
    {
        // 1- 检查参数是否为附件类型
        String paramType = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_BBOSS_ATTR", new String[]
                { "ATTR_CODE" }, "EDIT_TYPE", new String[]
                { attrCode });
        if (StringUtils.equals("UPLOAD", paramType) && (!"6".equals(CSBizBean.getVisit().getInModeCode())))
        {
            attrValue = GrpCommonBean.checkFileState(attrValue);
        }
        return attrValue;
    }

    /*
     * @description 处理产品资费信息
     * @auhtor xunyl
     * @date 2013-06-25
     */
    protected IDataset dealProductRateInfo(IData merchPInfo) throws Exception
    {
        // 1- 定义返回数据
        IDataset productDstInfo = new DatasetList();

        // 2- 获取产品资费信息
        IDataset merchPDsts = reqData.cd.getDiscnt();

        // 3- 处理无需入表的情况
        if (merchPDsts == null || merchPDsts.size() == 0)
        {
            return productDstInfo;
        }

        // 4- 处理BBOSS侧资费表信息
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = merchPDsts.getData(i);

            // 4-1 资费状态为删除或者资费编码为空串皆属于无效资费，不进行处理
            if ("DEL".equals(merchPDst.getString("MODIFY_TAG")) || "".equals(merchPDst.getString("DISCNT_CODE", "")))
            {
                continue;
            }

            // 4-2 将资费编码转化为BBOSS侧的资费编号，转化后的资费编号不存在或者为空皆属于无效资费，不进行处理
            String discntCode = merchPDst.getString("DISCNT_CODE", "");// 本地ELEMENT_ID
            String productDiscntCode = GrpCommonBean.productToMerch(discntCode, 1);
            if (productDiscntCode == null || "".equals(productDiscntCode))
            {
                continue;
            }

            // 4-3 拼装BBOSS侧资费表信息
            merchPDst.put("USER_ID", merchPInfo.getString("USER_ID"));
            merchPDst.put("MERCH_SPEC_CODE", reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE"));
            merchPDst.put("PRODUCT_ORDER_ID", merchPInfo.getString("PRODUCT_ORDER_ID"));
            merchPDst.put("PRODUCT_OFFER_ID", merchPInfo.getString("PRODUCT_OFFER_ID", ""));
            merchPDst.put("PRODUCT_SPEC_CODE", merchPInfo.getString("PRODUCT_SPEC_CODE"));
            merchPDst.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            merchPDst.put("START_DATE", merchPDst.getString("START_DATE", "").equals("") ? getAcceptTime() : merchPDst.getString("START_DATE"));
            merchPDst.put("END_DATE", SysDateMgr.getTheLastTime());
            merchPDst.put("PRODUCT_DISCNT_CODE", productDiscntCode);
            merchPDst.put("INST_ID", SeqMgr.getInstId());
            merchPDst.put("RELA_INST_ID", getInstIdByDiscntCode(discntCode));
            merchPDst.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧参数状态，服开拼报文用

            // 4-4 将处理后的资费表信息添加到返回数据集中
            productDstInfo.add(merchPDst);
        }

        // 5- 返回处理结果
        return productDstInfo;
    }

    /*
     * @description 根据资费编码到trade_discnt表查找inst_id,用于与资费参数进行关联
     * @author xunyl
     * @date 2013-09-03
     */
    protected String getInstIdByDiscntCode(String discntCode) throws Exception
    {
        // 1- 定义返回对象
        String instId = "";

        // 2- 获取资费对象
        IDataset merchPDsts = bizData.getTradeDiscnt();

        // 2- 循环产品资费信息，获取相应的inst_id
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = merchPDsts.getData(i);
            String elementId = merchPDst.getString("DISCNT_CODE");
            if (discntCode.equals(elementId))
            {
                instId = merchPDst.getString("INST_ID");
                break;
            }
        }

        // 3- 返回数据
        return instId;
    }

    /*
     * @description 创建商品与子产品间的BB关系
     * @author xunyl
     * @date 2013-04-16
     */
    protected IData getReletionBBMap() throws Exception
    {
        // 1- 商品台帐反馈信息为空，属于异常情形，直接退出处理
        IData outMerchInfo = reqData.getOUT_MERCH_INFO();
        if (IDataUtil.isEmpty(outMerchInfo))
        {
            return null;
        }

        // 2- 获取BB关系类型(此种取值方式有些绕，为了商产品使用不同的realtion_type_code的场合)
        String merchpId = reqData.getUca().getProductId();
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", merchpId, true);

        // 3- 封装BB关系表数据
        IData map = new DataMap();
        map.put("USER_ID_A", reqData.getOUT_MERCH_INFO().getString("USER_ID"));
        map.put("SERIAL_NUMBER_A", reqData.getOUT_MERCH_INFO().getString("SERIAL_NUMBER"));
        map.put("USER_ID_B", reqData.getUca().getUser().getUserId());
        map.put("SERIAL_NUMBER_B", reqData.getUca().getUser().getSerialNumber());
        map.put("RELATION_TYPE_CODE", merchRelationTypeCode);
        map.put("ROLE_CODE_A", "0");
        map.put("ROLE_CODE_B", "0");
        map.put("INST_ID", SeqMgr.getInstId());
        map.put("START_DATE", getAcceptTime());
        map.put("END_DATE", SysDateMgr.getTheLastTime());
        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 4- 返回BB关系表对象
        return map;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateBBossUserReqData();
    }

    /*
     * @description 根据接口规范的产品操作编号获取服务开通侧配置的产品操作编号(供服务开通用)
     * @author xunyl
     * @date 2013-08-26
     */
    protected String getServMerchpOpType(String merchpOpType) throws Exception
    {
        // 1- 定义返回值
        String servMerchpOpType = "";

        // 2-获取对应的产品编号
        if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue()))
        {
            servMerchpOpType = "06";
        }
        else if (merchpOpType.equals(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue()))
        {
            servMerchpOpType = "10";
        }

        // 3- 返回服务开通侧商品操作编号
        return servMerchpOpType;
    }

    /**
     * 业务台帐BBOSS产品用户订购表子表
     *
     * @throws Exception
     */
    public void infoRegDataEntireMerchP() throws Exception
    {
        // 1- 处理子产品信息
        IData merchPInfo = (IData) Clone.deepClone(reqData.getBBOSS_PRODUCT_INFO());
        merchPInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        merchPInfo.put("PRODUCT_SPEC_CODE", GrpCommonBean.productToMerch(merchPInfo.getString("PRODUCT_ID"), 0));
        merchPInfo.put("PRODUCT_ORDER_ID", SeqMgr.getBBossProductIdForGrp());
        merchPInfo.put("MERCH_SPEC_CODE", reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE"));
        merchPInfo.put("STATUS", "A");
        merchPInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        merchPInfo.put("START_DATE", getAcceptTime());
        merchPInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        IDataset productInfoDataset = bizData.getTradeProduct();
        IData productInfoset = productInfoDataset.getData(0);
        String instID = productInfoset.getString("INST_ID");
        merchPInfo.put("INST_ID", instID);
        merchPInfo.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId()); // 集团编码
        merchPInfo.put("RSRV_STR1", reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OPER_CODE"));// 产品操作类型
        String merchpOpType = reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OPER_CODE");

        // 渠道类型为IBOSS 则存入BBOSS下发的订单号
        if ("6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            merchPInfo.put("PRODUCT_OFFER_ID", reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OFFER_ID"));
            merchPInfo.put("PRODUCT_ORDER_ID", reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_ORDER_ID")); // 产品订单号
        }

        if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(merchpOpType))
        {
            merchPInfo.put("RSRV_STR2", "0");// 业务流程标志位，"0"代表预受发起
        }
        reqData.setMerchp(merchPInfo);

        // 2- 处理产品资费
        IDataset merchPDsts = this.dealProductRateInfo(merchPInfo);
        if (merchPDsts == null || merchPDsts.size() == 0)
        {
            return;
        }
        super.addTradeGrpMerchpDiscnt(merchPDsts);
    }

    /*
     * @description 登记other表信息，供服务开通用
     * @author xunyl
     * @date 2013-08-24
     */
    protected void infoRegDataOther() throws Exception
    {
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "7810");// 服务开通侧集团service_id对应为7810
        String merchpOpType = reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OPER_CODE");
        serviceInfo.put("OPER_CODE", getServMerchpOpType(merchpOpType));
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        this.addTradeOther(serviceInfo);
    }

    /*
     * @description 产品订购关系通过OTHER表传给计费帐务，注意要配置td_b_crmtobilling
     * @author liaoyi
     * @date 2013-12-14
     * @modifyBy chenkh
     * @date 2015-4-23
     */
    protected void infoRegDataOtherToBilling() throws Exception
    {
        String merch_spec_code = reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE");

        // 根据配置，确定哪些数据需要发送计费,并准备数据同步计费
        IData isToBilling = StaticInfoQry.getStaticInfoByTypeIdDataId("BBOSS_OTHERTOBILLING", merch_spec_code);
        if (IDataUtil.isNotEmpty(isToBilling))
        {
            String offerId = reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OFFER_ID");
            IData otherInfo = new DataMap();
            otherInfo.put("RSRV_VALUE_CODE", isToBilling.getString("PDATA_ID"));
            otherInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
            otherInfo.put("RSRV_VALUE", offerId);
            otherInfo.put("RSRV_STR1", offerId);
            otherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            otherInfo.put("START_DATE", getAcceptTime());
            otherInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            otherInfo.put("INST_ID", SeqMgr.getInstId());
            otherInfo.put("IS_NEED_PF", 0);// 1或者是空： 发指令

            if("010190007".equals(merch_spec_code)){//WLAN统付业务
                IData productParamInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
                otherInfo.put("RSRV_STR1", "999074020");
                otherInfo.put("RSRV_VALUE", productParamInfos.getString("999074020",""));
            }

            this.addTradeOther(otherInfo);

        }
    }

    /**
     * chenyi 2014-7-14 特殊业务登记特殊表
     *
     * @throws Exception
     */
    protected void infoRegDataSpecial() throws Exception
    {
        // 流量统付业务登记tf_b_trade_GRP_CENPAY 同步字段给账务
        boolean isFlux = BbossPayBizInfoDealbean.isFluxTFBusiness(reqData.getGrpProductId());// 判断是否统付业务
        // 如果为流量统付产品，则需要同步字段给账务
        if (isFlux)
        {
            String product_spec_code = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
            String merch_spec_code = reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE");
            String productOfferingId = reqData.getBBOSS_PRODUCT_INFO().getString("PRODUCT_OFFER_ID", "123456");// 正向生成订单没有默认放123456，此值归档时候更新，反向生成订单有此值
            String groupId = reqData.getUca().getCustGroup().getGroupId();
            String product_id = reqData.getUca().getProductId();
            String ec_serial_number = reqData.getUca().getCustGroup().getMpGroupCustCode();
            String biz_mode = reqData.getBBOSS_PRODUCT_INFO().getString("BIZ_MODE");// 正向需要在归档时候更新此值
            String userid = reqData.getUca().getUserId();
            IData productParamInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
            IData grpCenPayData = new DataMap();
            
            //国际流量统付
            if ("99910".equals(product_spec_code))
            {
            	grpCenPayData = BbossPayBizInfoDealbean.insertGrpCenpayForInternational(productParamInfos, product_spec_code, merch_spec_code, productOfferingId, groupId, product_id, ec_serial_number, biz_mode, userid);
        	}
            else
        	{
        		grpCenPayData = BbossPayBizInfoDealbean.insertGrpCenpay(productParamInfos, product_spec_code, merch_spec_code, productOfferingId, groupId, product_id, ec_serial_number, biz_mode, userid);
        	}

            this.addTradeGrpCenpay(grpCenPayData);
        }

        // 行业网关云MAS业务需要等级TF_B_TRADE_GRP_PLATSVC 同步给服务开通
//        if (BbossIAGWCloudMASDealBean.isIAGWCloudMAS(GrpCommonBean.productToMerch(reqData.getGrpProductId(), 0)))
//        {
//            String userid = reqData.getUca().getUserId();
//            String operState = "01"; // 01:订购 02:注销 04:暂停 05:恢复 08:用户信息变更
//            IData productParamInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
//            String serialNumber = reqData.getUca().getUser().getSerialNumber();
//            String product_spec_code = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
//            String group_id = reqData.getUca().getCustGroup().getGroupId();
//
//            IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState, product_spec_code, productParamInfos, serialNumber);
//            this.addTradeGrpPlatsvc(grpPlatSVCData);
//
//            // 判断集团资料是否需要同步行业网关，如需调用客管接口。
////            BbossIAGWCloudMASDealBean.synGroupDataToIAGW(group_id, product_spec_code, CSBizBean.getVisit().getStaffEparchyCode(), CSBizBean.getVisit().getCityCode(), CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getDepartId());
//        }

        // 和对讲产品
        if(StringUtils.equals("9101101", GrpCommonBean.productToMerch(reqData.getGrpProductId(),0))){
            //2-1 获取合同结束时间及是否到期自动续约的参数值
            String contractEndDate = "";
            String isRenew = "";
            IDataset productParamInfoList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
            if (IDataUtil.isEmpty(productParamInfoList))
            {
                return;
            }
            for(int i=0,sizeI=productParamInfoList.size();i<sizeI;i++){
                IData param=productParamInfoList.getData(i);
                if ("91011010001".equals(param.getString("ATTR_CODE"))){
                    contractEndDate = param.getString("ATTR_VALUE");
                }else if ("91011010002".equals(param.getString("ATTR_CODE"))){
                    isRenew = param.getString("ATTR_VALUE");
                }
            }
            if(StringUtils.equals("1", isRenew)){
                String remark = "和对讲业务，到期不自动续约，需要系统自动注销";
                rigistDestyTable(remark,contractEndDate);
            }
        }
        //爱流量统付产品
        if(StringUtils.equals("9001201", GrpCommonBean.productToMerch(reqData.getUca().getProductId(),0))){
            //获取合同到期日属性
            String contractEndDate = "";
            IDataset productParamInfoList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
            if (IDataUtil.isEmpty(productParamInfoList))
            {
                return;
            }
            for(int i=0,sizeI=productParamInfoList.size();i<sizeI;i++){
                IData param=productParamInfoList.getData(i);
                if ("90012014012".equals(param.getString("ATTR_CODE"))){
                    contractEndDate = param.getString("ATTR_VALUE");
                }
            }
            String remark = "爱流量统付业务，合同到期日自动注销";
            rigistDestyTable(remark,contractEndDate);
        }
        //BBOSS产品配置化发送二次白名单给SIMS平台
        if (BbossIAGWCloudMASDealBean.isIAGWTOSIMS(GrpCommonBean.productToMerch(reqData.getGrpProductId(), 0)))
        {
            String userid = reqData.getUca().getUserId();
            String operState = "01"; // 01:订购 02:注销 04:暂停 05:恢复 08:用户信息变更
            IData productParamInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
            String serialNumber = reqData.getUca().getUser().getSerialNumber();
            String product_spec_code = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
            String group_id = reqData.getUca().getCustGroup().getGroupId();
            productParamInfos.put("PRODUCT_ID", reqData.getUca().getProductId());
            IData grpPlatSVCData = BbossIAGWCloudMASDealBean.makDataForGrpPlatSVC(userid, group_id, operState, product_spec_code, productParamInfos, serialNumber);

            setEcBusiChgInfo(grpPlatSVCData);
        }
    }
    /**
     * 作用：IBOSS同步数据表记录
     * 
     * @author zhangbo18 2017-04-05
     * @param data
     * @throws Exception
     */
    public void setEcBusiChgInfo(IData data) throws Exception
    {
    	IData ecBusiInfo = new DataMap();
	 	String insid = SeqMgr.getInstId();
	 	ecBusiInfo.put("INST_ID", insid);
    	ecBusiInfo.put("EC_ID", data.getString("GROUP_ID"));
    	ecBusiInfo.put("EC_OPR_CODE", "04");
    	ecBusiInfo.put("PRODUCT_NAME", data.getString("BIZ_NAME"));
    	ecBusiInfo.put("PRODUCT_ID", data.getString("INST_ID"));
    	ecBusiInfo.put("BIZ_CODE", data.getString("BIZ_CODE"));
    	ecBusiInfo.put("BASE_ACCESSNO", data.getString("EC_BASE_IN_CODE"));
    	ecBusiInfo.put("SERV_CODE", data.getString("SERV_CODE"));
    	ecBusiInfo.put("SERV_TYPE", data.getString("SERV_TYPE"));
    	ecBusiInfo.put("SOURCE", "02");
    	ecBusiInfo.put("OPER_STATE", data.getString("OPER_STATE"));
    	ecBusiInfo.put("BIZ_STATUS", data.getString("BIZ_STATUS"));
    	ecBusiInfo.put("RB_LIST", data.getString("BIZ_ATTR"));
    	ecBusiInfo.put("CONFIRMFLAG", data.getString("CONFIRMFLAG"));
    	ecBusiInfo.put("ACCESSMODEL", data.getString("ACCESS_MODE"));
    	ecBusiInfo.put("TEXT_SIGN_FLAG", data.getString("IS_TEXT_ECGN"));
    	ecBusiInfo.put("TEXT_SIGN_DEFAULT", data.getString("DEFAULT_ECGN_LANG"));
    	ecBusiInfo.put("TEXT_SIGN_ZH", data.getString("TEXT_ECGN_ZH"));
    	ecBusiInfo.put("TEXT_SIGN_EN", data.getString("TEXT_ECGN_EN"));
    	ecBusiInfo.put("BIZ_OPR_CODE", data.getString("OPER_STATE"));
    	
    	CSAppCall.call("SS.GroupInfoChgSVC.synChgEcBusiInfo", ecBusiInfo);
    }

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-02
     */
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateBBossUserReqData) getBaseReqData();
    }

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-04-03
     */
    public void makBBossReqData(IData map) throws Exception
    {
        // 设置商品创建时的反馈信息
        reqData.setOUT_MERCH_INFO(map.getData("OUT_MERCH_INFO"));

        // 将产品信息保存至RD中，创建tf_f_user_grp_merchp表数据时用
        reqData.setBBOSS_PRODUCT_INFO(map.getData("BBOSS_PRODUCT_INFO"));
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.csservice.group.base.creategroupuser.CreateGroupUser#makReqData()
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-02
     */
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        makBBossReqData(map);
    }

    protected void makUca(IData map) throws Exception
    {
        // 将商品传递过来的ACCT_INFO放置到产品中
        map.put("ACCT_INFO", map.getData("OUT_MERCH_INFO").getData("ACCT_INFO"));

        super.makUca(map);
    }

    /*
     * @descripiton 重写基类的登记属性方法，登记ICB参数的名称
     * @author zhangcheng
     * @date 2013-08-21
     */
    protected void setTradeAttr(IData map) throws Exception
    {
        // 调用基类方法处理
        super.setTradeAttr(map);

        // 如果是资费参数
        if ("D".equals(map.getString("INST_TYPE", "")))
        {
            // 查询是否是ICB参数
            String number = map.getString("ATTR_CODE", "");
            IDataset IcbSet = PoRatePlanIcbQry.getIcbsByParameterNumber(number);
            if (IcbSet != null && IcbSet.size() > 0)
            {
                map.put("RSRV_STR3", IcbSet.getData(0).getString("PARAMETERNAME", ""));
            }
            else
            {
                map.put("IS_NEED_PF", "0");// 是否需要发服开 0为不发，1或"" 是要发服开
            }
        }
    }

    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        String specProductId = GrpCommonBean.productToMerch(reqData.getUca().getProductId(), 0);
        IData data = bizData.getTrade();
        if ("6".equals(CSBizBean.getVisit().getInModeCode()))
        {// 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }

        // 3- 如果是管理流程节点中的集团受理，不能直接完工，需要将台账表状态设置为"W"
        if (reqData.getBBOSS_PRODUCT_INFO().getBoolean("BBOSS_MANAGE_CREATE"))
        {
            data.put("SUBSCRIBE_STATE", "W");
        }
    }

    /*
     * @descripiton 产品用户表中的USER_ID_A对应为商品用户编号(商品用户表中的的USER_ID_A对应为-1)
     * @author weixp
     * @date 2013-09-02
     */
    protected void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);

        map.put("USER_ID_A", reqData.getOUT_MERCH_INFO().getString("USER_ID"));
    }

    /*
     * @descrption 设置BBOSS独有的用户表数据
     * @author xunyl
     * @see com.ailk.csservice.group.base.creategroupuser.CreateGroupUser#setTradeUser(com.ailk.common.data.IData)
     */
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        // 设置产品状态
        map.put("RSRV_STR5", "A");
    }

    /**
     * @descripiton 重写用户资料表，用于更改计费标志
     * @author xunyl
     * @date 2014-04.29
     */
    protected void actTradeUser() throws Exception
    {
        //1- 调用基类方法注入值
        super.actTradeUser();

        //2- 获取主办省
        String hostCompany = reqData.getOUT_MERCH_INFO().getString("HOST_COMPANY");

        //3- 获取业务开展模式
        String bizMode = reqData.getOUT_MERCH_INFO().getString("BIZ_MODE");

        //5- 获取商品编码
        String merchSpecCode =  reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE");

        //6- 根据商品编码、主办省、业务开展模式获取ATTR_BIZ表配置的计费项
        String companyTag = hostCompany;
        if(!StringUtils.equals(ProvinceUtil.getProvinceCodeGrpCorp(), hostCompany)){
            //非主办省以外的其它省份省编码统一使用999
            companyTag = "999";
        }
        IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrCount(merchSpecCode,"D","ACCTTAG",companyTag,bizMode);

        //7- 如果找不到对应的配置，则按照默认的计费方式计费
        if(IDataUtil.isEmpty(attrBizInfoList)){
            setAcctTag(companyTag,bizMode);
            return;
        }

        //8- 有配置，则根据配置判断是否需要计费
        IData attrbizInfo = attrBizInfoList.getData(0);
        String acctTag =attrbizInfo.getString("ATTR_NAME");
        IDataset tradeUserInfoList = bizData.getTradeUser();
        if(IDataUtil.isEmpty(tradeUserInfoList)){
            return;
        }
        for(int i=0;i<tradeUserInfoList.size();i++){
            tradeUserInfoList.getData(i).put("ACCT_TAG", acctTag);
        }

        // 9- 处理预付费模式
        String productId = reqData.getUca().getProductId();
        String prepayTag = BbossPayBizInfoDealbean.getFluxPrepayTag(productId, reqData.cd.getProductParamMap(productId));// 判断是否统付业务
        if (StringUtils.isNotEmpty(prepayTag) && "1".equals(prepayTag))
        {
            for(int i=0;i<tradeUserInfoList.size();i++)
            {
                tradeUserInfoList.getData(i).put("PREPAY_TAG", prepayTag);
            }
        }
    }

    /*
     * @description BBOSS默认计费方式，根据主办省和业务开展模式决定是否本省计费
     * @author xunyl
     * @date 2015-04-29
     */
    private void setAcctTag(String hostCompany,String bizMode)throws Exception{
        //1- 获取用户台帐信息列表
        IDataset tradeUserInfoList = bizData.getTradeUser();

        //2- 用户信息不存在，直接退出，该情况基本不可能出现
        if(IDataUtil.isEmpty(tradeUserInfoList)){
            return;
        }

        //3- 如果业务开展省为本省，并且业务开展模式为主办省一点受理、一点支付或者本省受理、本省支付则计费，否则不计费
        if(StringUtils.equals(ProvinceUtil.getProvinceCodeGrpCorp(), hostCompany) &&
                (StringUtils.equals("3", bizMode) || StringUtils.equals("5", bizMode))){
            for(int i=0;i<tradeUserInfoList.size();i++){
                tradeUserInfoList.getData(i).put("ACCT_TAG", "0");
            }
        }else{
            for(int i=0;i<tradeUserInfoList.size();i++){
                tradeUserInfoList.getData(i).put("ACCT_TAG", "Z");
            }
        }
    }

    /**
     * @description 重写基类方法，反向订购用于正确标注用户归属(账户归属在商品中已经更改了，产品中沿用商品的，无需更改)
     * @author xunyl
     * @date 2015-12-24
     */
    protected void makUcaForGrpOpen(IData map) throws Exception
    {
        //1- 调用基类处理
        super.makUcaForGrpOpen(map);

        //2- 判断当前操作是否为反向订购，否则直接调用基类处理即可
        if (!"6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            return;
        }

        //3- 获取city_code字段(主办省场合为客户归属省，配和省场合读取表配置)
        String cityCode = "";
        String hostCompany = map.getData("OUT_MERCH_INFO").getString("HOST_COMPANY");
        if(StringUtils.equals(ProvinceUtil.getProvinceCodeGrpCorp(), hostCompany)){
            cityCode = reqData.getUca().getCustGroup().getCityCode();
        }else{
            String productId = map.getString("PRODUCT_ID");
            cityCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
                    { "TYPE_ID", "DATA_ID", "SUBSYS_CODE" }, "DATA_NAME", new java.lang.String[]
                    { "BBOSS_CDNTCPN_CITYCODE", productId, "-1" });
        }

        //4- 更换用户表的CITY_CODE字段(如果配合省的场合表中没有配置，则采用后台接口过来的默认配置)
        if(StringUtils.isBlank(cityCode)){
            return;
        }
        IData userInfo = reqData.getUca().getUser().toData();
        userInfo.put("CITY_CODE", cityCode);
        UserTradeData utd = new UserTradeData(userInfo);
        reqData.getUca().setUser(utd);
        DataBusManager.getDataBus().setUca(reqData.getUca());
    }

    /**
     * @description 登记一键注销表
     * @author xunyl
     * @date 2016-01-20
     */
    private void rigistDestyTable(String remark,String destroyTime)throws Exception{
        IData destroyTableInfo = new DataMap();
        destroyTableInfo.put("USER_ID", reqData.getOUT_MERCH_INFO().getString("USER_ID"));
        destroyTableInfo.put("DESTROY_TYPE", "1");//1为集团注销
        destroyTableInfo.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
        String merchId = reqData.getOUT_MERCH_INFO().getString("MERCH_SPEC_CODE");
        String productId = GrpCommonBean.merchToProduct(merchId, 0, null);// 商品编号转化为本地产品编号
        destroyTableInfo.put("PRODUCT_ID", productId);
        destroyTableInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        destroyTableInfo.put("REMARK", remark);
        destroyTableInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        destroyTableInfo.put("ANTI_INTF_FLAG","1");//反向接口
        destroyTableInfo.put("DESTROY_TIME",destroyTime);
        destroyTableInfo.put("UPDATE_TIEM",SysDateMgr.getSysTime());
        Dao.insert("TF_TP_BBOSS_DESTROY_INFO", destroyTableInfo,Route.CONN_CRM_CEN);
    }
}