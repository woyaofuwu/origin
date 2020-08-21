
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossmange;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoRatePlanIcbQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeMag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class BbossManage
{

    /**
     * chenyi 2014-6-17 新增资费
     * 
     * @param tradeDistinctInfo
     *            台帐信息
     * @param userDistinctInfo
     *            资料信息
     * @param inparam
     * @param tempData
     *            资费
     * @param cFlowInfo
     *            是否是预受
     * @throws Exception
     */
    protected static void addTradeDisticnt(IDataset tradeDistinctInfo, IDataset userDistinctInfo, IData inparam, IData tempData, String cFlowInfo) throws Exception
    {

        IData inparm = new DataMap();
        String tradeId = inparam.getString("TRADE_ID");
        String userId = inparam.getString("BBOSS_USER_ID");
        String discnt_code = tempData.getString("ELEMENT_ID");
        String package_id = tempData.getString("PACKAGE_ID");

        // 如果台帐表和资料都没数据则直接新增 直接入资料表
        if (IDataUtil.isEmpty(tradeDistinctInfo) && IDataUtil.isEmpty(userDistinctInfo))
        {
            insertUserDist(inparam, tempData, cFlowInfo);
        }
        // 资料表的数据为有效，且变更时候台帐表为del 管理节点再次新增此资费
        else
        {
            // 台帐表有数据（资料表也有相关）
            // 则将台帐表数据改为新增 并在回单时回收此数据
            // 将原来数据设置为 modify_tag=1，RSRV_STR3 结束日期改成当前时间 // TF_B_TRADE_DISCNT
            inparm.put("TRADE_ID", tradeId);
            inparm.put("DISCNT_CODE", discnt_code);
            inparm.put("PACKAGE_ID", package_id);
            inparm.put("USER_ID", userId);
            inparm.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            inparm.put("RSRV_STR3", "M");// 回收标识
            inparm.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            inparm.put("START_DATE", SysDateMgr.getSysDate(SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")));
            inparm.put("RSRV_TAG1", "2");

            IDataset ids = AttrBizInfoQry.getBizAttr("1", "B", "DIS", tempData.getString("ELEMENT_ID"), null);

            // 预受理资费处理（本地资费入表，非本地资费不入表）
            if ("0".equals(cFlowInfo))
            {
                if (IDataUtil.isEmpty(ids))
                {
                    TradeDiscntInfoQry.uptradeDistinctState(inparm);
                }

            }
            else
            {
                // 非预受理资费
                // 集团资费
                // 更新merchpDistinct表数据，并在回单时候回收

                if (IDataUtil.isNotEmpty(ids) && !"0".equals(cFlowInfo))// 预受理不对集团资费进行操作
                {
                    inparm.put("RSRV_STR2", "M");// 回收标记
                    inparm.put("PRODUCT_DISCNT_CODE", ids.getData(0).getString("ATTR_VALUE"));
                    inparm.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());//
                    inparm.put("IS_NEED_PF", "1");

                    TradeGrpMerchpDiscntInfoQry.updateMerchpDistinctState(inparm);
                }

                // 本地资费
                TradeDiscntInfoQry.uptradeDistinctState(inparm);
            }

            // 处理资费参数
            if (null != tempData.getDataset("ATTR_PARAM") && !tempData.getDataset("ATTR_PARAM").isEmpty())
            {

                // 如果为预受理且为集团资费，则对资费参数不进行处理
                if ("0".equals(cFlowInfo) && IDataUtil.isNotEmpty(ids))
                    return;

                // 获取ICB参数名字
                getDistinctAttrName(tempData.getDataset("ATTR_PARAM"));

                for (int i = 0, size = tempData.getDataset("ATTR_PARAM").size(); i < size; i++)
                {
                    IData attrInfo = tempData.getDataset("ATTR_PARAM").getData(i);
                    // 获取trade表资费属性
                    IDataset olDataset = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, attrInfo.getString("ATTR_CODE"), null);
                    if (IDataUtil.isNotEmpty(olDataset))
                    {
                        IData oldAattrInfo = olDataset.getData(0);
                        String attrOldValue = oldAattrInfo.getString("ATTR_VALUE");
                        attrInfo.put("PARAM_OLD_VALUE", attrOldValue);
                        attrInfo.put("RELA_INST_ID", oldAattrInfo.getString("RELA_INST_ID"));
                    }
                    else
                    {
                        attrInfo.put("PARAM_OLD_VALUE", "");
                        attrInfo.put("RELA_INST_ID", tradeDistinctInfo.getData(0).getString("INST_ID"));
                    }
                    attrInfo.put("MODIFY_TAG", "ADD");
                    attrInfo.put("INST_TYPE", "D");
                    attrInfo.put("ELEMENT_ID", discnt_code);
                    changeTradeAttr(inparam, attrInfo);
                }
            }
        }
    }

    /**
     * 修改台账表中的数据
     * 
     * @param trade_id
     *            台账ID
     * @param mproduct_id
     *            商品ID
     * @param order_id
     *            订单ID
     * @author weixb3
     * @throws Exception
     */
    public static void changeStatusForTrade(String trade_id, String mproduct_id, String order_id, String merch_trade_id) throws Exception
    {

        // 将台帐表改成非W状态,让AEE扫描
        // TradeInfoQry.updateTradeStateByPK(order_id, "0");
        TradeInfoQry.updateSubstate(trade_id, "0");

        // 更新商品主台帐状态
        TradeInfoQry.updateSubstate(merch_trade_id, "0");

        // 更新order表orderstate改成0状态 HQ_tag 改成r app_type 为M
        TradeMag.updateStateHq(order_id, "r", "M", "0");

    }

    /**
     * 属性变更 chenyi
     * 
     * @throws Exception
     */
    protected static void changeTradeAttr(IData param, IData parm_temp) throws Exception
    {

        // 查看资料表是否有数据
        IDataset userAttrInfoDataset = UserAttrInfoQry.getUserAttrInfoByUserId(param.getString("BBOSS_USER_ID"), parm_temp.getString("ATTR_CODE"), parm_temp.getString("ATTR_GROUP"));

        // 1- 如果没有变化就不修改
        if ("EXIST".equals(parm_temp.getString("MODIFY_TAG")))
        {
            return;

        }

        // 变更新增，在tf_f_user表里面没有相关信息
        if ("ADD".equals(parm_temp.getString("MODIFY_TAG")))
        {
            if (StringUtils.isBlank(parm_temp.getString("PARAM_OLD_VALUE")) && StringUtils.isBlank(parm_temp.getString("ATTR_VALUE", "")))
            {
                return;
            }

            // 变更
            if (StringUtils.isNotBlank(parm_temp.getString("PARAM_OLD_VALUE")) && StringUtils.isNotBlank(parm_temp.getString("ATTR_VALUE", ""))) // 说明是修改更新
            {
                String userID = param.getString("BBOSS_USER_ID");
                String attrCode = parm_temp.getString("ATTR_CODE");

                // 变更新增 从一个值变为另一个值 资料表没值
                if (IDataUtil.isEmpty(userAttrInfoDataset))
                {
                    // 将原来属性置为无效 并回收
                    parm_temp.put("RSRV_STR1", "M");
                    updateAttrInfo(param, parm_temp);
                    // 将新值插入表中 不回收

                    parm_temp.put("RSRV_STR1", "");
                    inserAttr(param, parm_temp);

                }
                else
                {
                    // 资料表有数据
                    String oldUerAttrValue = userAttrInfoDataset.getData(0).getString("ATTR_VALUE");// 获取资料表数据

                    // 新变更的值与以前资料表值一样，则资料变无变化，只需将变更数据发服开，在回单接口回收此数据
                    if (oldUerAttrValue.equals(parm_temp.getString("ATTR_VALUE", "")))
                    {
                        // 将变更时候台帐表属性置为无效 并在回单接口回收
                        parm_temp.put("RSRV_STR1", "M");
                        updateAttrInfo(param, parm_temp);
                        // 将管理节点新值插入表中告诉服开新增此属性 防止入资料表时报错需在回单接口回收此
                        parm_temp.put("RSRV_STR1", "M");
                        inserAttr(param, parm_temp);

                        // 将变更时的产生的删除属性置为回收状态 不发服开
                        TradeAttrInfoQry.updateBbossAttrStateByUID("M", userID, attrCode, "5", oldUerAttrValue);
                    }
                    else
                    {
                        // 管理节点变更的值与资料表里面的值不一致
                        // 将变更时候产生的台帐属性置为无效 并回收
                        parm_temp.put("RSRV_STR1", "M");
                        updateAttrInfo(param, parm_temp);

                        // 将新值插入表中 不回收
                        parm_temp.put("RSRV_STR1", "");
                        inserAttr(param, parm_temp);

                    }

                }

            }
            // 管理节点做删除
            if (null != parm_temp.getString("PARAM_OLD_VALUE") && "".equals(parm_temp.getString("ATTR_VALUE", "")))
            {
                // 将变更时候产生的台帐置为无效 并回收
                parm_temp.put("RSRV_STR1", "M");
                updateAttrInfo(param, parm_temp);

                // 将新空值插入表中 回收（发送服开 当需要删除一条属性时需将原来属性置为无效，再发送一条空值）
                parm_temp.put("RSRV_STR1", "M");
                inserAttr(param, parm_temp);
            }

            // 以前台帐表没数据切资料表也没数据就完全新增
            if (null == parm_temp.getString("PARAM_OLD_VALUE") || "".equals(parm_temp.getString("PARAM_OLD_VALUE"))) // 说明是新增的属性,重新插入
            {

                inserAttr(param, parm_temp);

            }

        }
        else if ("DEL".equals(parm_temp.getString("MODIFY_TAG")) && StringUtils.isNotBlank(parm_temp.getString("ATTR_GROUP")))
        {// 属性组的删除

            // 如果资料表有该数据就不回收
            if (IDataUtil.isNotEmpty(userAttrInfoDataset))
            {

                // 将变更时候台帐表属性置为无效 并在回单接口回收

                updateAttrInfo(param, parm_temp);
            }
            else
            {
                // 资料表没该属性就需回收
                // 将变更时候台帐表属性置为无效 并在回单接口回收
                parm_temp.put("RSRV_STR1", "M");
                updateAttrInfo(param, parm_temp);
            }
        }

    }

    /**
     * chenyi 管理节点资费变更
     * 
     * @param inparam
     * @param tempData
     * @param cFlowInfo
     * @throws Exception
     */
    protected static void changeTradeDistinct(IData inparam, IData tempData, String cFlowInfo) throws Exception
    {

        String tradeId = inparam.getString("TRADE_ID");
        String userId = inparam.getString("BBOSS_USER_ID");
        String discnt_code = tempData.getString("ELEMENT_ID");
        String package_id = tempData.getString("PACKAGE_ID");

        // 查看trade表是否有数据
        IDataset tradeDistinctInfo = TradeDiscntInfoQry.qryTradeDiscntInfosByPK(tradeId, userId, discnt_code, package_id);

        // 查看资料表是否有有效的资费数据
        IDataset userDistinctInfo = UserDiscntInfoQry.getAllDiscntByUserId(userId, discnt_code);

        if ("EXIST".equals(tempData.getString("MODIFY_TAG")) && "D".equals(tempData.get("ELEMENT_TYPE_CODE")))
        {
            return;

        }

        /**
         * 新增场景 1. 第一次新增此资费，即台帐表和资料表都没数据 2. 在集团受理时tf_f_user_discnt存在有效资费，但在集团变更时
         * 删除资费，即在tf_b_trade_discnt存在del状态的资费，再做管理节点时又新增此资费
         */
        if ("D".equals(tempData.get("ELEMENT_TYPE_CODE")) && TRADE_MODIFY_TAG.Add.getValue().equals(tempData.getString("MODIFY_TAG")))
        {

            addTradeDisticnt(tradeDistinctInfo, userDistinctInfo, inparam, tempData, cFlowInfo);

        }

        /**
         * 删除场景 只有资料表或台帐表存在add状态的资费才能做删除 1. 资料表存在有效数据，台帐表不存在有效数据 2. 资料表无有效数据，台帐表存在有效数据
         */
        // 台帐表或者资料表有数据的情况下 管理节点才能做删除操作
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(tempData.getString("MODIFY_TAG")) && "D".equals(tempData.get("ELEMENT_TYPE_CODE")))
        {
            delTradeDiscnt(inparam, tempData, userDistinctInfo, tradeDistinctInfo, cFlowInfo);
        }
        // 只修改资费参数 则属性都新增一条和删除一条
        /**
         * 只修改资费参数 则对应的资费 也需要新增一条删除一条
         */
        if (TRADE_MODIFY_TAG.MODI.getValue().equals(tempData.getString("MODIFY_TAG")) && "D".equals(tempData.get("ELEMENT_TYPE_CODE")))
        {
            modefyTradeDiscnt(inparam, tempData, userDistinctInfo, tradeDistinctInfo, cFlowInfo);
        }
        // 将TF_B_TRADE_GRP_MERCHP_DISCNT,TF_B_TRADE_DISCNT加入TF_B_TRADE的INTF_ID中
        GrpCommonBean.dealTradeIntfId(tradeId);
    }

    /**
     * 管理节点数据处理总入口
     * 
     * @param inputData
     * @throws Exception
     */
    public static void dealManageInfo(IData inputData) throws Exception
    {

        // 合同附件
        IData poattachment_data = inputData.getData("POATTACHMENT_DATA");

        if (IDataUtil.isNotEmpty(poattachment_data))
        {
            BbossManage.infoRegDataOther(poattachment_data);
        }

        // 更新产品属性和资费
        IData attr_dins_info = inputData.getData("ATTR_DINS_INFO");

        if (IDataUtil.isNotEmpty(attr_dins_info))
        {
            BbossManage.updateDistinctAndAttrInfo(attr_dins_info);
        }

        // 处里省BBOSS反馈信息

        IData manage_info = inputData.getData("MANAGE_INFO");
        if (IDataUtil.isNotEmpty(manage_info))
        {
            BbossManage.updateBbossOtherFlag(inputData.getData("MANAGE_INFO"));
        }

        String order_id = inputData.getData("TRADE_INFO").getString("ORDER_ID");
        String trade_id = inputData.getData("TRADE_INFO").getString("TRADE_ID");
        String merch_trade_id = inputData.getData("TRADE_INFO").getString("MERCH_TRADE_ID");

        // 针对一单多线拆单的情况，管理流程阶段merch.merch_order_id更新为merchp.product_order_id的前16位
        IDataset merchpList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(trade_id);
        if (IDataUtil.isNotEmpty(merchpList))
        {

            String merchpOrderId = merchpList.getData(0).getString("PRODUCT_ORDER_ID");
            merchpOrderId = merchpOrderId.substring(0, 16); // 取产品product_order_id的前16位

            TradeGrpMerchInfoQry.updateMerchOrderIdByTradeId(merch_trade_id, merchpOrderId);
        }

        // 更改主台帐标记为受理状态
        changeStatusForTrade(trade_id, "", order_id, merch_trade_id);

        TradeInfoQry.updateTradeRsrvStr10(inputData.getData("TRADE_INFO"));

        // 走esop 加开关
        boolean sendpf = BizEnv.getEnvBoolean("isesop", false);
        IData esop = inputData.getData("ESOP");
        if (sendpf && IDataUtil.isNotEmpty(esop))
        {
            ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", esop);
        }

    }

    /**
     * chenyi 2014-6-17 删除资费
     * 
     * @param tradeDistinctInfo
     *            台帐信息
     * @param userDistinctInfo
     *            资料信息
     * @param inparam
     * @param tempData
     *            资费
     * @param cFlowInfo
     *            是否是预受
     * @throws Exception
     */
    protected static void delTradeDiscnt(IData inparam, IData tempData, IDataset userDistinctInfo, IDataset tradeDistinctInfo, String cFlowInfo) throws Exception
    {

        IData inparm = new DataMap();
        String tradeId = inparam.getString("TRADE_ID");
        String userId = inparam.getString("BBOSS_USER_ID");
        String discnt_code = tempData.getString("ELEMENT_ID");
        String package_id = tempData.getString("PACKAGE_ID");

        // 资料表有数据 台帐表没数据 变更时候没有对资费进行操作，管理节点直接删除资料表数据 需要插入删除的资费数据 不回收
        if (IDataUtil.isNotEmpty(userDistinctInfo))
        {

            insertDelUserDist(userDistinctInfo, userId, tempData, cFlowInfo);

        }

        // 资料表没数据，台帐表有数据
        if (IDataUtil.isNotEmpty(tradeDistinctInfo))
        {
            // 将变更时候产生台帐数据变为无效 在回单接口回收此数据
            // 将原来数据设置为 modify_tag=1，RSRV_STR3 结束日期改成当前时间 // TF_B_TRADE_DISCNT

            inparm.put("TRADE_ID", tradeId);
            inparm.put("DISCNT_CODE", discnt_code);
            inparm.put("PACKAGE_ID", package_id);
            inparm.put("USER_ID", userId);
            inparm.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            inparm.put("RSRV_STR3", "M");// 回收标识
            inparm.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            inparm.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            inparm.put("RSRV_TAG1", "2");
            inparm.put("IS_NEED_PF", "1");

            IDataset ids = AttrBizInfoQry.getBizAttr("1", "B", "DIS", tempData.getString("ELEMENT_ID"), null);
            // 预受理资费处理（本地资费入表，非本地资费不入表）
            if ("0".equals(cFlowInfo))
            {
                if (IDataUtil.isEmpty(ids))
                {

                    TradeDiscntInfoQry.uptradeDistinctState(inparm);

                }

            }
            else
            {
                // 非预受理资费
                // 更新merchp_distinct

                // 更新merchpDistinct表数据，并在回单时候回收
                if (IDataUtil.isNotEmpty(ids) && !"0".equals(cFlowInfo))// 预受理不对集团资费进行操作
                {
                    inparm.put("RSRV_STR2", "M");// 回收标记
                    inparm.put("PRODUCT_DISCNT_CODE", ids.getData(0).getString("ATTR_VALUE"));
                    inparm.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());//
                    inparm.put("IS_NEED_PF", "1");

                    TradeGrpMerchpDiscntInfoQry.updateMerchpDistinctState(inparm);
                }
                // 本地资费入表
                TradeDiscntInfoQry.uptradeDistinctState(inparm);
            }

            //
            // 资费参数
            if (null != tempData.getDataset("ATTR_PARAM") && !tempData.getDataset("ATTR_PARAM").isEmpty())
            {

                // 如果为预受理且为集团资费，则对资费参数不进行处理
                if ("0".equals(cFlowInfo) && IDataUtil.isNotEmpty(ids))
                    return;
                // 获取ICB参数名字
                getDistinctAttrName(tempData.getDataset("ATTR_PARAM"));
                for (int i = 0, size = tempData.getDataset("ATTR_PARAM").size(); i < size; i++)
                {
                    IData attrInfo = tempData.getDataset("ATTR_PARAM").getData(i);
                    // 获取trade表资费属性 如果台帐表有资费参数就做处理
                    IDataset olDataset = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, attrInfo.getString("ATTR_CODE"), null);
                    if (IDataUtil.isNotEmpty(olDataset))
                    {
                        IData oldAattrInfo = olDataset.getData(0);
                        String attrOldValue = oldAattrInfo.getString("ATTR_VALUE");
                        attrInfo.put("PARAM_OLD_VALUE", attrOldValue);
                        attrInfo.put("RELA_INST_ID", oldAattrInfo.getString("RELA_INST_ID"));
                        attrInfo.put("MODIFY_TAG", "ADD");
                        attrInfo.put("INST_TYPE", "D");
                        changeTradeAttr(inparam, attrInfo);
                    }

                }
            }

        }
    }

    /**
     * chenyi 12-10-28 查询ICB参数的名字
     * 
     * @param distinctAttr
     * @return
     * @throws Exception
     */
    public static IDataset getDistinctAttrName(IDataset distinctAttr) throws Exception
    {

        IData map = new DataMap();
        for (int i = 0, sizeI = distinctAttr.size(); i < sizeI; i++)
        {

            map = distinctAttr.getData(i);
            // 查询是否是ICB参数
            String number = map.getString("ATTR_CODE", "");
            IDataset IcbSet = PoRatePlanIcbQry.getIcbsByParameterNumber(number);
            if (IcbSet != null && IcbSet.size() > 0)
            {
                map.put("ATTR_NAME", IcbSet.getData(0).getString("PARAMETERNAME", ""));
            }
            else
            {
                map.put("IS_NEED_PF", "0");// 是否需要发服开 0为不发，1或"" 是要发服开
            }

        }
        return distinctAttr;
    }

    /**
     * 合同信息插Other表
     * 
     * @param pd
     * @param commData
     * @throws Exception
     */
    public static void infoRegDataOther(IData param) throws Exception
    {// String

        String trade_id = param.getString("TRADE_ID");
        String systime = SysDateMgr.getSysDate();
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", trade_id);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("MODIFY_TAG", "0");
        inparam.put("USER_ID", param.getString("BBOSS_USER_ID"));
        inparam.put("RSRV_VALUE_CODE", "ATT_INFOS");
        inparam.put("RSRV_VALUE", "合同信息");
        inparam.put("RSRV_STR5", param.getString("pOAttachment"));
        inparam.put("START_DATE", systime);
        inparam.put("END_DATE", SysDateMgr.getTheLastTime());
        inparam.put("UPDATE_TIME", systime);
        TradeOtherInfoQry.inserOtherInfo(inparam);
    }

    /**
     * 将数据插入attr表 chenyi
     * 
     * @param param
     * @param parm_temp
     * @throws Exception
     */
    protected static void inserAttr(IData param, IData parm_temp) throws Exception
    {
        IDataset productInfo = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(param.getString("TRADE_ID"));
        if (IDataUtil.isEmpty(productInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_853);
        }

        String tradeId = param.getString("TRADE_ID");
        String productUserId = param.getString("BBOSS_USER_ID");
        String rela_inst_id = productInfo.getData(0).getString("INST_ID");
        String relaInstId = param.getString("RELA_INST_ID", rela_inst_id);
        String attrCode = parm_temp.getString("ATTR_CODE");
        String attrGroup = parm_temp.getString("ATTR_GROUP");
        String attrName = parm_temp.getString("ATTR_NAME");
        String attrValue = parm_temp.getString("ATTR_VALUE");
        String endDate = SysDateMgr.getTheLastTime();
        String instType = parm_temp.getString("INST_TYPE", "P");
        String is_need_pf = parm_temp.getString("IS_NEED_PF", "1");
        IData newAttrTradeInfo = GrpCommonBean.getAttrTradeInfo(tradeId, productUserId, relaInstId, attrCode, attrGroup, attrName, attrValue, is_need_pf, endDate, instType);

        newAttrTradeInfo.put("RSRV_TAG1", "1");// 表示是管理流程插入
        newAttrTradeInfo.put("RSRV_STR1", parm_temp.getString("RSRV_STR1"));// 回收标记
        newAttrTradeInfo.put("ELEMENT_ID", parm_temp.getString("ELEMENT_ID"));// 资费与服务参数有值，产品参数没值

        // 将数据插入到数据库
        TradeAttrInfoQry.insertAttrInfo(newAttrTradeInfo);
    }

    /**
     * chenyi 13-10-30 插入删除资料表的数据 userDistinctInfo资料表资费信息 userAttrInfoDataset 资料表属性信息 tempData 需要插入属性 cFlowInfo 管理借点编码
     * 
     * @throws Exception
     */
    protected static void insertDelUserDist(IDataset userDistinctInfo, String userId, IData tempData, String cFlowInfo) throws Exception
    {
        IData inparam = new DataMap();
        // 新增删除资料表的数据
        // 删除user_distinct
        inparam.put("INST_ID", userDistinctInfo.getData(0).getString("INST_ID"));// 获取资料表INST_ID
        inparam.put("MODIFY_TAG", "1"); // 新增
        inparam.put("ELEMENT_ID", tempData.getString("ELEMENT_ID"));
        inparam.put("PACKAGE_ID", tempData.getString("PACKAGE_ID"));
        inparam.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));

        IData inparamDistinct = spellTradeDiscnt(inparam);
        inparamDistinct.put("START_DATE", userDistinctInfo.getData(0).getString("START_DATE"));// 获取资料表START_DATE
        IDataset ids = AttrBizInfoQry.getBizAttr("1", "B", "DIS", tempData.getString("ELEMENT_ID"), null);

        // 预受理资费处理（本地资费入表，非本地资费不入表）
        if ("0".equals(cFlowInfo))
        {
            if (IDataUtil.isEmpty(ids))
            {

                TradeDiscntInfoQry.insertDiscntInfo(inparamDistinct);
            }

        }
        else
        {
            // 非预受理资费
            if (IDataUtil.isNotEmpty(ids) && !"0".equals(cFlowInfo))
            {
                inparam.put("MODIFY_TAG", "1"); // 删除
                inparam.put("PRODUCT_DISCNT_CODE", ids.getData(0).getString("ATTR_VALUE"));
                inparam.put("INST_ID", userDistinctInfo.getData(0).getString("INST_ID"));
                inparam.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
                inparam.put("BBOSS_MODIFY_TAG", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
                inparamDistinct.clear();
                inparamDistinct = spellMerchpDiscnt(inparam);
                inparamDistinct.put("START_DATE", userDistinctInfo.getData(0).getString("START_DATE"));// 获取资料表START_DATE
                TradeGrpMerchpDiscntInfoQry.insertMerchpDiscntInfo(inparamDistinct);
            }
            // 本地资费
            TradeDiscntInfoQry.insertDiscntInfo(inparamDistinct);
        }

        // 删除user_attr（如果资料表有属性）
        if (null != tempData.getDataset("ATTR_PARAM") && !tempData.getDataset("ATTR_PARAM").isEmpty())
        {

            // 如果为预受理且为集团资费，则对资费参数不进行处理
            if ("0".equals(cFlowInfo) && IDataUtil.isNotEmpty(ids))
                return;
            // 获取ICB参数名字
            getDistinctAttrName(tempData.getDataset("ATTR_PARAM"));

            inparam.put("ATTR_PARAM", tempData.getDataset("ATTR_PARAM"));
            inparam.put("BBOSS_MODIFY_TAG", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());
            inparam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            inparam.put("RELA_INST_ID", userDistinctInfo.getData(0).getString("INST_ID"));
            inparam.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            // 拼装参数
            IDataset datas = spellTradeAttr(inparam);

            for (int j = 0, sizej = datas.size(); j < sizej; j++)
            {
                // 查询资料
                IDataset userAttrInfoDataset = UserAttrInfoQry.getUserAttrByUserId(userId, tempData.getString("ATTR_CODE"));
                if (IDataUtil.isNotEmpty(userAttrInfoDataset))
                {
                    datas.getData(j).put("START_DATE", userAttrInfoDataset.getData(0).getString("START_DATE"));
                    datas.getData(j).put("INST_ID", userAttrInfoDataset.getData(0).getString("INST_ID"));
                    TradeAttrInfoQry.insertAttrInfo(datas.getData(j));
                }

            }
        }

    }

    /**
     * 直接新增有效的资费 不回收 cheyi 13-10-30
     * 
     * @throws Exception
     */
    protected static void insertUserDist(IData inparam, IData tempData, String cFlowInfo) throws Exception
    {

        String inst_id = SeqMgr.getInstId();
        inparam.put("INST_ID", inst_id);
        inparam.put("MODIFY_TAG", "0"); // 新增
        inparam.put("ELEMENT_ID", tempData.getString("ELEMENT_ID"));
        inparam.put("PACKAGE_ID", tempData.getString("PACKAGE_ID"));
        inparam.put("END_DATE", SysDateMgr.END_TIME_FOREVER);

        IData inparamDistinct = spellTradeDiscnt(inparam);

        // 插merchp_discnt
        IDataset ids = AttrBizInfoQry.getBizAttr("1", "B", "DIS", tempData.getString("ELEMENT_ID"), null);

        // 预受理资费处理（本地资费入表，非本地资费不入表）
        if ("0".equals(cFlowInfo))
        {
            if (IDataUtil.isEmpty(ids))
            {
                TradeDiscntInfoQry.insertDiscntInfo(inparamDistinct);
            }

        }
        else
        {
            // 非预受理资费
            // 集团资费入表
            if (IDataUtil.isNotEmpty(ids))
            {
                inparam.put("MODIFY_TAG", "0"); // 新增
                inparam.put("PRODUCT_DISCNT_CODE", ids.getData(0).getString("ATTR_VALUE"));
                inparam.put("INST_ID", inst_id);
                inparam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                inparam.put("BBOSS_MODIFY_TAG", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());
                inparamDistinct.clear();
                inparamDistinct = spellMerchpDiscnt(inparam);
                TradeGrpMerchpDiscntInfoQry.insertMerchpDiscntInfo(inparamDistinct);

            }
            // 本地资费入表
            TradeDiscntInfoQry.insertDiscntInfo(inparamDistinct);
        }

        // 添加inst——id有问题
        if (null != tempData.getDataset("ATTR_PARAM") && !tempData.getDataset("ATTR_PARAM").isEmpty())
        {
            // 如果为预受理且为集团资费，则对资费参数不进行处理
            if ("0".equals(cFlowInfo) && IDataUtil.isNotEmpty(ids))
                return;
            // 获取ICB参数名字
            getDistinctAttrName(tempData.getDataset("ATTR_PARAM"));

            inparam.put("ATTR_PARAM", tempData.getDataset("ATTR_PARAM"));
            inparam.put("BBOSS_MODIFY_TAG", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());
            inparam.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            inparam.put("RELA_INST_ID", inst_id);
            inparam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            // 拼装参数
            IDataset datas = spellTradeAttr(inparam);
            for (int j = 0, sizej = datas.size(); j < sizej; j++)
            {
                datas.getData(j).put("ELEMENT_ID", tempData.getString("ELEMENT_ID"));

                // 为预受理时候，防止本地资费属性发服开
                if ("0".equals(cFlowInfo))
                {
                    datas.getData(j).put("IS_NEED_PF", "0");
                }
                TradeAttrInfoQry.insertAttrInfo(datas.getData(j));
            }
        }

    }

    /**
     * chenyi 2014-6-17 修改资费参数
     * 
     * @param tradeDistinctInfo
     *            台帐信息
     * @param userDistinctInfo
     *            资料信息
     * @param inparam
     * @param tempData
     *            资费
     * @param cFlowInfo
     *            是否是预受
     * @throws Exception
     */
    protected static void modefyTradeDiscnt(IData inparam, IData tempData, IDataset userDistinctInfo, IDataset tradeDistinctInfo, String cFlowInfo) throws Exception
    {

        IData inparm = new DataMap();
        String tradeId = inparam.getString("TRADE_ID");
        String userId = inparam.getString("BBOSS_USER_ID");
        String discnt_code = tempData.getString("ELEMENT_ID");
        String package_id = tempData.getString("PACKAGE_ID");
        // 获取ICB参数名字
        getDistinctAttrName(tempData.getDataset("ATTR_PARAM"));

        // 资料表有数据 台帐表没有（插入一条del资料表的数据，再新增一条add的数据，且都不回收）
        if (IDataUtil.isNotEmpty(userDistinctInfo))
        {

            // 增添删除资料表值
            insertDelUserDist(userDistinctInfo, userId, tempData, cFlowInfo);
            // 增添新值
            insertUserDist(inparam, tempData, cFlowInfo);
        }
        // 台帐表有数据资料表没有（将台帐表的数据全部置为无效，再新增资费和属性）
        if (IDataUtil.isNotEmpty(tradeDistinctInfo))
        {
            // 将台帐表里的资费和属性置为无效 并回收

            inparm.put("TRADE_ID", tradeId);
            inparm.put("DISCNT_CODE", discnt_code);
            inparm.put("PACKAGE_ID", package_id);
            inparm.put("USER_ID", userId);
            inparm.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            inparm.put("RSRV_STR3", "M");// 回收标识
            inparm.put("END_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            inparm.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            inparm.put("RSRV_TAG1", "2");
            inparm.put("IS_NEED_PF", "1");

            IDataset ids = AttrBizInfoQry.getBizAttr("1", "B", "DIS", tempData.getString("ELEMENT_ID"), null);
            // 预受理资费处理（本地资费入表，非本地资费不入表）
            if ("0".equals(cFlowInfo))
            {
                if (IDataUtil.isEmpty(ids))
                {
                    TradeDiscntInfoQry.uptradeDistinctState(inparm);
                }

            }
            else
            {
                // 非预受理资费
                // 更新merchpDistinct表数据，并在回单时候回收

                if (IDataUtil.isNotEmpty(ids) && !"0".equals(cFlowInfo))// 预受理不对集团资费进行操作
                {
                    inparm.put("RSRV_STR2", "M");// 回收标记
                    inparm.put("PRODUCT_DISCNT_CODE", ids.getData(0).getString("ATTR_VALUE"));
                    inparm.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());//
                    inparm.put("IS_NEED_PF", "1");

                    TradeGrpMerchpDiscntInfoQry.updateMerchpDistinctState(inparm);
                }
                // 本地资费入表
                TradeDiscntInfoQry.uptradeDistinctState(inparm);
            }

            // 资费参数
            if (null != tempData.getDataset("ATTR_PARAM"))
            {
                // 如果为预受理且为集团资费，则对资费参数不进行处理
                if ("0".equals(cFlowInfo) && IDataUtil.isNotEmpty(ids))
                    return;
                // 获取ICB参数名字
                getDistinctAttrName(tempData.getDataset("ATTR_PARAM"));
                for (int i = 0, size = tempData.getDataset("ATTR_PARAM").size(); i < size; i++)
                {
                    IData attrInfo = tempData.getDataset("ATTR_PARAM").getData(i);

                    IDataset olDataset = TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(tradeId, attrInfo.getString("ATTR_CODE"), null);

                    attrInfo.put("RSRV_STR1", "M");
                    attrInfo.put("INST_TYPE", "D");
                    if (IDataUtil.isNotEmpty(olDataset))
                    {
                        attrInfo.put("PARAM_OLD_VALUE", olDataset.getData(0).getString("ATTR_VALUE"));
                    }

                    updateAttrInfo(inparam, attrInfo);
                }
            }
            // 插入新值到trade表
            insertUserDist(inparam, tempData, cFlowInfo);
        }
    }

    /**
     * 拼装discnt数据 用来插到MERCHP_DISCNT表
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    protected static IData spellMerchpDiscnt(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("MERCH_SPEC_CODE", param.getString("MERCH_SPEC_CODE"));
        data.put("PRODUCT_ORDER_ID", param.getString("PRODUCT_ORDER_ID"));
        data.put("PRODUCT_OFFER_ID", param.getString("PRODUCT_OFFER_ID"));
        data.put("PRODUCT_SPEC_CODE", param.getString("PRODUCT_SPEC_CODE"));
        data.put("PRODUCT_DISCNT_CODE", param.getString("PRODUCT_DISCNT_CODE"));
        data.put("USER_ID", param.getString("BBOSS_USER_ID"));
        data.put("TRADE_ID", param.getString("TRADE_ID"));
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(param.getString("TRADE_ID")));
        data.put("MODIFY_TAG", param.getString("MODIFY_TAG"));
        data.put("RSRV_STR1", param.getString("BBOSS_MODIFY_TAG"));// BBOSS侧状态
        data.put("INST_ID", param.getString("INST_ID"));
        data.put("PACKAGE_ID", param.getString("PACKAGE_ID"));
        data.put("PRODUCT_ID", param.getString("PRODUCT_NUMBER"));
        data.put("DISCNT_CODE", param.getString("ELEMENT_ID"));
        data.put("USER_ID_A", "-1");
        data.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("END_DATE", param.getString("END_DATE"));
        data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("IS_NEED_PF", "1");

        return data;
    }

    /**
     * 拼装discnt数据 用来插到TRADE_ATTR表
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    protected static IDataset spellTradeAttr(IData param) throws Exception
    {
        IDataset datas = new DatasetList();
        for (int i = 0, size = param.getDataset("ATTR_PARAM").size(); i < size; i++)
        {
            IData data = new DataMap();

            // 新增一条资费数据 将资费数据插入到TF_B_TRADE_ATTR
            data.put("PRODUCT_ID", param.getString("PRODUCT_NUMBER"));
            data.put("TRADE_ID", param.getString("TRADE_ID"));
            data.put("ATTR_CODE", param.getString("ATTR_CODE"));
            data.put("ATTR_VALUE", param.getString("ATTR_VALUE"));
            data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(param.getString("TRADE_ID")));
            data.put("USER_ID", param.getString("BBOSS_USER_ID"));
            data.put("MODIFY_TAG", param.getString("MODIFY_TAG"));// 新增
            data.put("INST_ID", SeqMgr.getInstId());

            data.put("RSRV_STR5", param.getString("BBOSS_MODIFY_TAG"));// BBOSS侧状态
            data.put("RELA_INST_ID", param.getString("RELA_INST_ID"));
            data.put("INST_TYPE", "D");
            data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            data.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
            data.put("END_DATE", param.getString("END_DATE"));
            data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());

            String attr_code = param.getDataset("ATTR_PARAM").getData(i).getString("ATTR_CODE");
            String attr_val = param.getDataset("ATTR_PARAM").getData(i).getString("ATTR_VALUE");
            String attr_name = param.getDataset("ATTR_PARAM").getData(i).getString("ATTR_NAME");
            data.put("ATTR_CODE", attr_code);
            data.put("ATTR_VALUE", attr_val);
            data.put("RSRV_STR3", attr_name);
            data.put("IS_NEED_PF", data.getString("IS_NEED_PF", "1"));

            datas.add(data);
        }
        return datas;

    }

    /**
     * 拼装discnt数据 用来插到TRADE_DISCNT表
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    protected static IData spellTradeDiscnt(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_ID", param.getString("TRADE_ID"));
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(param.getString("TRADE_ID")));
        data.put("MODIFY_TAG", param.getString("MODIFY_TAG"));
        data.put("USER_ID", param.getString("BBOSS_USER_ID"));
        data.put("PRODUCT_ID", param.getString("PRODUCT_NUMBER"));
        data.put("PACKAGE_ID", param.getString("PACKAGE_ID"));
        data.put("DISCNT_CODE", param.getString("ELEMENT_ID"));
        data.put("SPEC_TAG", param.getString("SPEC_TAG", "2"));
        data.put("RELATION_TYPE_CODE", param.getString("RELATION_TYPE_CODE", "97"));
        data.put("INST_ID", param.getString("INST_ID"));

        data.put("START_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("END_DATE", param.getString("END_DATE"));
        data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("USER_ID_A", "-1");
        data.put("IS_NEED_PF", "1");
        return data;
    }

    /**
     * 更新老数据 chenyi 2014-6-19
     * 
     * @param param
     * @param parm_temp
     * @throws Exception
     */
    protected static void updateAttrInfo(IData param, IData parm_temp) throws Exception
    {

        IData inparm = new DataMap();
        inparm.put("TRADE_ID", param.getString("TRADE_ID"));
        inparm.put("ATTR_CODE", parm_temp.getString("ATTR_CODE"));
        inparm.put("ATTR_NAME", parm_temp.getString("ATTR_NAME"));
        inparm.put("ATTR_VALUE", parm_temp.getString("PARAM_OLD_VALUE"));
        inparm.put("INST_TYPE", parm_temp.getString("INST_TYPE", "P"));
        inparm.put("RSRV_STR1", parm_temp.getString("RSRV_STR1"));// 回收标记
        inparm.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
        inparm.put("RSRV_STR5", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());// BBOSS侧参数状态
        inparm.put("IS_NEED_PF", parm_temp.getString("IS_NEED_PF", "1"));// 1或者是空： 发指令
        // 将原有属性值修改为del状态 rsrv_str1 为m状态

        // 属性组状态更新
        if (StringUtils.isNotBlank(parm_temp.getString("ATTR_GROUP")))
        {
            inparm.put("RSRV_STR4", parm_temp.getString("ATTR_GROUP"));// 属性组标识
            inparm.put("ATTR_VALUE", parm_temp.getString("ATTR_VALUE"));
            TradeAttrInfoQry.updateBbossGroupAttrState(inparm);
        }
        else
        {
            TradeAttrInfoQry.updateBbossAttrState(inparm);
        }

    }

    /**
     * 对产品产数的数据库更改 如果修改了产品产数，update数据库属性
     * 
     * @author chenyi
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public static void updateBbossAttr(IData param, IDataset porduct_parms) throws Exception
    {
        // 获取产品台账编号
        String productTradeId = param.getString("TRADE_ID");

        // 根据产品台账编号获取产品操作类型
        IDataset tradeGrpMerchpInfoList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(productTradeId);
        if (IDataUtil.isEmpty(tradeGrpMerchpInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData tradeGrpMerchpInfo = tradeGrpMerchpInfoList.getData(0);
        String productOpType = tradeGrpMerchpInfo.getString("RSRV_STR1");

        // 根据产品操作类型判断当前操作所处的阶段位(预受理、受理、资源变更、资费变更)
        String cFlowInfo = "";
        if ("10".equals(productOpType))
        {
            cFlowInfo = "0";
        }
        else if ("1".equals(productOpType))
        {
            cFlowInfo = "1";
        }
        else if ("9".equals(productOpType) || "6".equals(productOpType) || "13".equals(productOpType))
        {
            cFlowInfo = "2";
        }
        else if ("5".equals(productOpType))
        {
            cFlowInfo = "3";
        }

        // 更新属性台账
        if ("0".equals(cFlowInfo) || "1".equals(cFlowInfo) || "2".equals(cFlowInfo) || "3".equals(cFlowInfo))
        {
            for (int i = 0; i < porduct_parms.size(); i++)
            {
                IData parm_temp = porduct_parms.getData(i);
                changeTradeAttr(param, parm_temp);
            }
        }
    }

    /**
     * 更改TF_B_TRADE_OTHER的标志位,和将省BBOSS反馈信息插入
     * 
     * @author jch
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public static void updateBbossOtherFlag(IData param) throws Exception
    {

        IDataset manage_info = param.getDataset("MANAGE_INFO");
        // 先查出TF_B_TRADE_OTHER表数据

        String flowInfo = param.getString("FLOWPOINT").substring(6);

        IDataset other = TradeOtherInfoQry.getTradeOtherByTradeId(param.getString("TRADE_ID"), "BBOSS_" + flowInfo, param.getString("BBOSS_USER_ID"));
        IData putData = new DataMap();

        if (other != null && other.size() > 0)
        {
            putData = other.getData(0);
            putData.put("START_DATE", other.getData(0).getString("START_DATE").substring(0, 19));
            putData.put("END_DATE", other.getData(0).getString("END_DATE").substring(0, 19));
            putData.put("USER_ID", param.getString("BBOSS_USER_ID"));
            putData.put("TRADE_ID", param.getString("TRADE_ID"));

        }

        if (manage_info != null && manage_info.size() > 0)
        {

            for (int i = 0; i < manage_info.size(); i++)
            {
                IData tempData = manage_info.getData(i);
                putData.put("TRADE_ID", param.getString("TRADE_ID"));
                putData.put("RSRV_STR12", tempData.getString("PARAM_CODE", ""));
                putData.put("RSRV_STR13", tempData.getString("PARAM_NAME", ""));
                putData.put("RSRV_STR14", tempData.getString("PARAM_VALUE", ""));
                putData.put("RSRV_VALUE_CODE", "BBOSS_MANAGE_" + flowInfo);// 标志是返回的审核信息
                putData.put("RSRV_VALUE", "管理流程审核信息");// RSRV_VALUE必须有值，否则同步计费账务报错
                putData.put("INST_ID", SeqMgr.getInstId());
                putData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(param.getString("TRADE_ID")));
                putData.put("USER_ID", param.getString("BBOSS_USER_ID"));
                putData.put("RSRV_NUM10", flowInfo);// 节点操作码
                putData.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                putData.put("START_DATE", SysDateMgr.getSysTime());
                putData.put("END_DATE", SysDateMgr.getTheLastTime());
                putData.put("IS_NEED_PF", "1");// 是否需要发服开 0为不发，1或"" 是要发服开
                // 将数据插入
                TradeOtherInfoQry.insTradeOther(putData);
            }
        }

        IData inparm = new DataMap();
        inparm.put("TRADE_ID", param.getString("TRADE_ID"));
        inparm.put("USER_ID", param.getString("BBOSS_USER_ID"));

        TradeOtherInfoQry.udpateBbossOtherFlag(inparm);

    }

    /**
     * 对产品资费的数据更改 如果修改了产品资费，update数据库属性
     * 
     * @author jch
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    protected static void updateBbssDistinct(IData inparam, IDataset distinct_parms) throws Exception
    {

        // 获取产品台账编号
        String productTradeId = inparam.getString("TRADE_ID");

        // 根据产品台账编号获取产品操作类型
        IDataset tradeGrpMerchpInfoList = TradeGrpMerchpInfoQry.qryMerchpInfoByTradeId(productTradeId);
        if (IDataUtil.isEmpty(tradeGrpMerchpInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData tradeGrpMerchpInfo = tradeGrpMerchpInfoList.getData(0);
        String productOpType = tradeGrpMerchpInfo.getString("RSRV_STR1");

        // 根据产品操作类型判断当前操作所处的阶段位(预受理、受理、资源变更、资费变更)
        String cFlowInfo = "";
        if ("10".equals(productOpType))
        {
            cFlowInfo = "0";
        }
        else if ("1".equals(productOpType))
        {
            cFlowInfo = "1";
        }
        else if ("9".equals(productOpType) || "6".equals(productOpType) || "13".equals(productOpType))
        {
            cFlowInfo = "2";
        }
        else if ("5".equals(productOpType))
        {
            cFlowInfo = "3";
        }

        // 更新属性台账
        if ("0".equals(cFlowInfo) || "1".equals(cFlowInfo) || "2".equals(cFlowInfo) || "3".equals(cFlowInfo))
        {
            for (int i = 0; i < distinct_parms.size(); i++)
            {
                IData tempData = distinct_parms.getData(i);

                changeTradeDistinct(inparam, tempData, cFlowInfo);
            }
        }

    }

    /**
     * 更新资费和属性 chenyi
     * 
     * @param param
     * @param idata
     * @throws Exception
     */
    public static void updateDistinctAndAttrInfo(IData param) throws Exception
    {

        // 更新属性
        if (IDataUtil.isNotEmpty(param.getDataset("PRODUCT_PARMS")))
        {
            updateBbossAttr(param, param.getDataset("PRODUCT_PARMS"));
        }
        if (IDataUtil.isNotEmpty(param.getDataset("MERCHPDISCNTS")))
        {
            // 更新资费
            updateBbssDistinct(param, param.getDataset("MERCHPDISCNTS"));
        }

    }

}
