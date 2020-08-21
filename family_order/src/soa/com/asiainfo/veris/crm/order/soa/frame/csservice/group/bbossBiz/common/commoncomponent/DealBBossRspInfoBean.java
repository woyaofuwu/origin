
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;


import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.BbossTradeQueryBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @description 该类用于处理归档报文，报文集团业务归档报文和成员业务的归档报文
 * @author xunyl
 * @date 2013-09-27
 */

public class DealBBossRspInfoBean
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 集团客户一点支付的暂停与恢复的场合，暂停或者恢复成员的付费关系
     * @author xunyl
     */
    protected static void createBatOnePayMem(IData map) throws Exception
    {
        // 1- 拼装服务参数
        IData memCond = new DataMap();

        IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", map); // 产品订购关系ID
        String productOfferingId = rsrvstr4Set.get(0).toString();
        String productUserId = GrpCommonBean.getMerchpUserIdByProdId(productOfferingId);
        memCond.put("USER_ID", productUserId);

        IDataset productNumberSet = IDataUtil.getDatasetSpecl("PRSRV_STR10", map); // 多条产品规格编号
        String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(0));
        String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
        memCond.put("PRODUCT_ID", productId);

        IData productUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(productUserId);
        String custId = productUserInfo.getString("CUST_ID");
        memCond.put("CUST_ID", custId);

        String groupId = UcaInfoQry.qryGrpInfoByCustId(custId).getString("GROUP_ID");
        memCond.put("GROUP_ID", groupId);

        IData inparams = new DataMap();
        inparams.put("ID", productUserId);
        IData payRelation = PayRelaInfoQry.getPayRelation(inparams);
        String acctId = payRelation.getString("ACCT_ID");
        memCond.put("ACCT_ID", acctId);

        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType))
        {
            merchOperType = "pause";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType))
        {
            merchOperType = "back";
        }
        else
        {
            merchOperType = "destroy";
        }
        memCond.put("OPER_CODE", merchOperType);

        memCond.put("TRADE_STAFF_ID", map.getString("TRADE_STAFF_ID"));
        memCond.put("TRADE_DEPART_ID", map.getString("TRADE_DEPART_ID"));
        memCond.put("TRADE_CITY_CODE", map.getString("TRADE_CITY_CODE"));
        memCond.put("TRADE_EPARCHY_CODE", map.getString("TRADE_EPARCHY_CODE"));
        memCond.put("TRADE_TYPE_CODE", "4691");// 暂停与恢复都属于集团变更

        // 2- 拼装存储过程参数
        IData paramValue = new DataMap();
        paramValue.put("v_User_Id", productUserId);
        paramValue.put("v_Staff_Id", map.getString("TRADE_STAFF_ID"));
        paramValue.put("v_Depart_Id", map.getString("TRADE_DEPART_ID"));
        paramValue.put("v_City_Code", map.getString("TRADE_CITY_CODE"));
        paramValue.put("v_Eparchy_Code", map.getString("TRADE_EPARCHY_CODE"));
        paramValue.put("v_GrpUser_AcctId", acctId);
        paramValue.put("v_MemConding", memCond.toString());
        paramValue.put("v_Act_Tag", merchOperType.equals("pause") ? "1" : "2");

        // 3- 调用存储过程，批量恢复或者暂停成员的代付关系
        String[] paramName =
                { "v_User_Id", "v_GrpUser_AcctId", "v_Staff_Id", "v_Depart_Id", "v_City_Code", "v_Eparchy_Code", "v_MemConding", "v_Act_Tag", "v_Result_Code", "v_Result_Info" };
        Dao.callProc("P_CRM_GRP_PAYRELA_AUTO", paramName, paramValue, Route.CONN_CRM_CEN);
    }

    /*
     * @description 集团业务归档
     * @author xunyl
     * @date 2013-09-12
     */
    public static IData dealGrpRspFile(IData map) throws Exception
    {
    	
        // 1- 定义预受理标志
        boolean isPreDeal = false;

        // 2- 获取归档报文中的一级信息(归档报文按照规范大体分为3层，第一层为商、产品基本信息，二层为资费和产品参数等信息、三层为资费参数等信息)
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 3- 更新产品参数台帐信息
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        String orderId = "";
        String tradeId = "";
        String merchTradeId = "";// 商品台帐编码
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 3-1 获取归档报文的二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);
            
            // 3-2 获取产品操作类型，根绝产品操作类型判断归档报文是否预受理归档
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(productOperCode))
            {
                isPreDeal = true;
            }

            // 3-3 收集产品参数信息
            String proNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchToProduct(proNumber, 2, null);
            IData productParam = new DataMap();
            IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            
            // 3-4 获取台账编号
            IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
            String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i));
            IDataset merchpInfoList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);
            IData merchpInfo = merchpInfoList.getData(0);
            tradeId = merchpInfo.getString("TRADE_ID");
            orderId = merchpInfo.getString("ORDER_ID");
            map.put("ORDER_ID", orderId);// 一单多线 查询订单用
            
            // 3-5 更新产品参数台账信息
            UpdateAttrInfoBean.dealAttrTradeInfo(productParamInfoList, tradeId);

            // 3-6 更新其它信息
            modifyTradeInfo(isPreDeal, false, merchpInfo, map, productParamInfoList, productOperCode, productOfferingId);

        }

        // 4- 处理特殊业务的资费问题，包括功能费和一次性费用
        // DealBBossDiscntBean.dealSpecialBizDiscnt(map, "0");
        
        // 5- 处理特殊业务的资费问题(跨省专线2.0，预受理转正式受理时调用反向接口增加资费信息 add by wangzc7)
        DealBBossDiscntBean.dealSpecialBizDiscnt2(map, "0");

        // 6- 更新商品台账信息(像专线业务，只要有一个产品归档，则商品台账信息就移历史了)
        String merchOfferId = map.getString("RSRV_STR2", "");
        IDataset merchInfoList = TradeGrpMerchInfoQry.qryMerchOnlineInfoByMerchOfferId(merchOfferId, null);
        if (IDataUtil.isNotEmpty(merchInfoList))
        {
            IData merchInfo = merchInfoList.getData(0);
            merchTradeId = merchInfo.getString("TRADE_ID");
            modifyTradeInfo(isPreDeal, true, merchInfo, map, null, null, null);

            // 更新STR1字段 SKIPSENDESOP 完工时候不调esop接口
            IData inparams = new DataMap();
            inparams.put("RSRV_STR1", "SKIPSENDESOP");
            inparams.put("TRADE_ID", merchTradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_RSRV_STR1", inparams, Route.getJourDb(Route.CONN_CRM_CG));
        }

        // 6- 其它特殊情况处理(典型场景，集团客户一点支付的暂停与恢复的场合，需要暂停或者恢复成员的付费关系)
        dealSpecialBiz(map);

        // 7- 调用ESOP接口(如果是一单多线的场合，需要判断是否为最后一个归档的产品，只有最后归档的产品才调用该接口)
        // 是否走esop流程
        if (BizEnv.getEnvBoolean("isesop", false))
        {
            sendEsop(merchTradeId, orderId, isPreDeal, map);
        }

        // 8- 返回受理成功标志
        IData dealResult = new DataMap();
        dealResult.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        dealResult.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        dealResult.put("RSPCODE", "00");
        dealResult.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        dealResult.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        dealResult.put("EC_SERIAL_NUMBER", IDataUtil.chkParam(map, "EC_SERIAL_NUMBER"));
        dealResult.put("SUBSCRIBE_ID", IDataUtil.chkParam(map, "SUBSCRIBE_ID"));
        return dealResult;
    }

    /*
     * @description 成员业务归档
     * @author xunyl
     * @date 2013-09-12
     */
    public static IData dealMebRspFile(IData map) throws Exception
    {
        // 1- 获取产品订购编号
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", map);
        String productOfferingId = (String) productIdList.get(0);

        // 2- 根据产品订购关系编号查询相应的台账信息
        String memUserId = MebCommonBean.getMemberUserId(map);
        BbossTradeQueryBean bbossQueryBean = new BbossTradeQueryBean();
        IDataset merchpInfoList = bbossQueryBean.getMerchpMebTradeInfoByUserId(memUserId, productOfferingId, CSBizBean.getUserEparchyCode());
        IData merchpInfo = merchpInfoList.getData(0);
        String tradeId = merchpInfo.getString("TRADE_ID");

        // 3- 更新产品主台账表状态
        // 3.1 查出集团产品台账
        IDataset grpMerchpInfo = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferingId);
        String product_id = "";
        String grpUserId = "";
        IData param = new DataMap();
        if (IDataUtil.isNotEmpty(grpMerchpInfo))
        {
            product_id = grpMerchpInfo.getData(0).getString("PRODUCT_SPEC_CODE");
            grpUserId = grpMerchpInfo.getData(0).getString("USER_ID");
        }
        // 跨省V网对SCP和GSCP的改造
        if(StringUtils.equals("993804", product_id))
        {
            insertBbossFilse(map);
        }
        IData again_pf = StaticInfoQry.getStaticInfoByTypeIdDataId("AGAIN_PF", product_id);

        // 3.2- 判断是否要再次发送服开 通知网元
        if (null != again_pf && IDataUtil.isNotEmpty(again_pf))
        {
            if(StringUtils.equals("9101101", product_id)){//和对讲业务，发送APN服务
                // 新增服务表记录
                //DealPocBizBean.rigistApnSeviceTrd(memUserId,tradeId);
            }

            // 修改台账
            param.clear();
            param.put("SUBSCRIBE_STATE", "0");
            param.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", param, Route.getJourDb(CSBizBean.getUserEparchyCode()));
            // 修改订单
            IDataset trades = TradeInfoQry.getMainTradeByTradeId(tradeId, CSBizBean.getUserEparchyCode());
            if (null != trades && IDataUtil.isNotEmpty(trades))
            {

                String order_id = trades.getData(0).getString("ORDER_ID");
                param.clear();
                param.put("ORDER_ID", order_id);
                param.put("APP_TYPE", "c");
                param.put("HQ_TAG", "r");
                Dao.executeUpdateByCodeCode("TF_B_ORDER", "UP_ORSTAHQBYORID", param, Route.getJourDb(BizRoute.getRouteId()));

            }

        }
        // 针对行业网关云MAS业务，需要重新发送服开通知行业网关
//        else if (BbossIAGWCloudMASDealBean.isCloudMasRspMeb(map.getString("SERIAL_NUMBER"), grpUserId, memUserId, GrpCommonBean.merchToProduct(product_id, 2, product_id)))
//        {
//            param.clear();
//            param.put("SUBSCRIBE_STATE", "0");
//            param.put("TRADEID", tradeId);
//            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", param, CSBizBean.getUserEparchyCode());
//            // 修改订单
//            IDataset trades = TradeInfoQry.getMainTradeByTradeId(tradeId, CSBizBean.getUserEparchyCode());
//            if (IDataUtil.isNotEmpty(trades))
//            {
//                String order_id = trades.getData(0).getString("ORDER_ID");
//                param.clear();
//                param.put("ORDER_ID", order_id);
//                param.put("APP_TYPE", "C");
//                param.put("HQ_TAG", "");
//                Dao.executeUpdateByCodeCode("TF_B_ORDER", "UP_ORSTAHQBYORID", param, BizRoute.getRouteId());
//            }
//        }
        else
        {

            IData inparams = new DataMap();
            inparams.put("SUBSCRIBE_STATE", "P");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", inparams, Route.getJourDb(BizRoute.getRouteId()));

        }

        // 4- 返回受理成功标志
        IData dealResult = new DataMap();
        dealResult.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        dealResult.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        dealResult.put("RSPCODE", "00");
        dealResult.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        return dealResult;
    }

    /*
     * @description 集团业务归档时的特殊操作(典型场景，集团客户一点支付的暂停与恢复的场合，需要暂停或者恢复成员的付费关系)
     * @author xunyl
     * @date 2013-11-05
     */
    protected static void dealSpecialBiz(IData map) throws Exception
    {
        // 1- 集团客户一点支付的暂停与恢复的场合，暂停或者恢复成员的付费关系
        String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0); // BBOSS商品规格编号
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if ("010190002".equals(poNumber)
                && (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperType)))
        {
            // createBatOnePayMem(map); 用存储过程有风险，故先手工，不用存储过程。
        }

        // 2- 省行业网关云MAS,在bboss下发归档的情况下，需要重新发送服务开通侧，由服务开通侧发指令通知省行业网关
//        if ("010101016".equals(poNumber) || "010101017".equals(poNumber))
//        {
//            String orderId = map.getString("ORDER_ID");
//            BbossIAGWCloudMASDealBean.dealTradeOlcomTagByOrder(orderId);
//            IData orderData = new DataMap();
//            orderData.put("ORDER_ID", orderId);
//            orderData.put("APP_TYPE", "C");
//            orderData.put("HQ_TAG", "");
//            Dao.executeUpdateByCodeCode("TF_B_ORDER", "UP_ORSTAHQBYORID", orderData, BizRoute.getRouteId());
//        }
    }

    /*
     * @description 判断是否为集团的归档报文
     * @author xunyl
     * @date 2013-09-09
     */
    protected static boolean isBBossGrpRspFile(IData map) throws Exception
    {
        // 1- 定义返回结果
        boolean returnValue = false;

        // 2- 如果操作类型为21(合同变更)，没有产品订购关系编号，只能根据商品订购关系编号判断是否为归档报文
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if(StringUtils.equals("21", merchOperType)){
            IDataset merchOfferIdList = IDataUtil.getDatasetSpecl("RSRV_STR2", map);
            String merchOfferId = (String) merchOfferIdList.get(0);
            IDataset merchInfoList = TradeGrpMerchInfoQry.qryMerchOnlineInfoByMerchOfferId(merchOfferId,null);
            if (IDataUtil.isNotEmpty(merchInfoList))
            {
                return true;
            }else{
                return returnValue;
            }
        }

        // 3- 获取产品订购编号(由于一单多线，商品台账可能会提前挪历史，商品订购关系编号不一定能找到台账)
        IDataset productIdList = IDataUtil.getDatasetSpecl("RSRV_STR4", map);
        String productOfferingId = (String) productIdList.get(0);

        // 4- 根据成员用户编号、产品订购关系编号查询相应的台账信息
        IDataset merchpInfoList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);

        // 5- 判断查询结果，存在台账信息则为报文归档，否则不是归档报文
        if (null != merchpInfoList && merchpInfoList.size() > 0)
        {
            returnValue = true;
        }

        // 6- 返回结果
        return returnValue;
    }

    /*
     * @description 判断是否为成员的归档报文
     * @author xunyl
     * @date 2013-09-09
     */
    protected static boolean isBBossMebRspFile(IData map) throws Exception
    {
        // 1- 定义返回结果
        boolean returnValue = false;

        // 2- 获取产品订购编号
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", map);
        String productOfferingId = (String) productIdList.get(0);

        // 3- 获取成员用户编号,用户编码找不到，肯定非归档报文，直接返回
        String memUserId = MebCommonBean.getMemberUserId(map);
        if(StringUtils.isBlank(memUserId)){
            return returnValue;
        }

        // 4- 根据产品订购关系编号查询相应的台账信息
        IDataset merchpInfoList = TradeGrpMerchMebInfoQry.qryMerchMebInfoByUserIdOfferIdRouteId(memUserId, productOfferingId, CSBizBean.getUserEparchyCode());

        // 5- 判断查询结果，存在台账信息则为报文归档，否则不是归档报文
        if (null != merchpInfoList && merchpInfoList.size() > 0)
        {
            returnValue = true;
        }

        // 6- 返回结果
        return returnValue;
    }

    /*
     * @description 判断是否为归档报文
     * @author xunyl
     * @date 2013-09-09
     */
    public static boolean isBBossRspFile(IData map, boolean isGrp) throws Exception
    {
        // 1- 定义返回结果
        boolean returnValue = false;

        // 2- 获取产品订购编号(集团归档与成员归档的报文字段不一致)
        if (isGrp)
        {
            returnValue = isBBossGrpRspFile(map);
        }
        else
        {
            returnValue = isBBossMebRspFile(map);
        }

        // 3- 返回结果
        return returnValue;
    }

    /*
     * @descripiton 预受理相应后，省BOSS修改台账表信息，例如修改操作类型、相应状态等
     * @author xunyl
     * @date 2014-03-06
     */
    public static void modifyPreTradeState(String tradeId) throws Exception
    {
        // 1- 更新主台账表
        IData inparams = new DataMap();
        IDataset tradeInfoList = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData tradeInfo = tradeInfoList.getData(0);
        if (StringUtils.equals("6", tradeInfo.getString("IN_MODE_CODE")))
        {// 配合省(业务落地省)
            inparams.put("FLAG", "");
            inparams.put("DESC", "预受理BBOSS已响应");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BBOSS_INFO", inparams, Route.getJourDb(Route.CONN_CRM_CG));

        }
        else
        {// 主办省（业务发起省）
            inparams.put("FLAG", "1");
            inparams.put("DESC", "预受理BBOSS已响应");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BBOSS_INFO", inparams, Route.getJourDb(Route.CONN_CRM_CG));
        }

        // 2- 更新产品参数表和产品资费表(区分哪些记录需要发送服务开通，预受理转正式受理时默认这两张表都不发送服开)
        TradeGrpMerchpDiscntInfoQry.updateMechpDiscntIsSendPfByTradeid(tradeId);
        TradeAttrInfoQry.updIsSendPfTradeid(tradeId);

        // 3- 更新OTHER表OPER_CODE为 06
        inparams.clear();
        inparams.put("TRADEID", tradeId);
        inparams.put("RSRV_VALUE_CODE", "BBSS");
        inparams.put("OPER_CODE", "06");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_OPER_CODE_BY_TRADE_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));

        // 4- 更新MERCHP表RSRV_STR1为 1
        inparams.clear();
        inparams.put("TRADEID", tradeId);
        inparams.put("RSRV_STR1", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_GRP_MERCHP", "UPD_RSRV_STR1_BY_TRADE_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * @description 更新trade表数据
     * @author xunyl
     * @date 2013-09-17
     */
    public static void modifyTrade(String tradeId, String orderId, String sysDate, String timeStamp) throws Exception
    {
        // 1- 设置查询参数
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SYS_DATE", sysDate);
        param.put("ORDER_ID", orderId);
        param.put("RSRV_DATE1", timeStamp);

        // 2- 更新优惠表的开始和结束时间
        StringBuilder buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 3- 更新服务表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVC T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVC T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 4- 更新付费关系表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
        buf.append(" SET T.start_cycle_id=to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" WHERE T.start_cycle_id < to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
        buf.append(" SET T.end_cycle_id=to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" WHERE T.end_cycle_id < to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 5- 更新服务状态表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 6- 更新产品参数表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ATTR T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ATTR T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 7- 更新产品表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 8- 更新BB关系表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_RELATION_BB T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_RELATION_BB T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 9- 更新BBOSS侧商品资费表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 10- 更新BBOSS侧产品资费表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 11- 更新用户表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_USER T ");
        buf.append(" SET T.OPEN_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.OPEN_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_USER T ");
        buf.append(" SET T.DESTROY_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.DESTROY_TIME < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 12- 更新订单表的执行时间(由于存在一单多台账的情况，可能会重复执行)
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_ORDER T ");
        buf.append(" SET T.EXEC_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.ORDER_ID=TO_NUMBER(:ORDER_ID)");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 13- 更新BBOSS侧商品信息表的归档时间(RSRV_DATE1)、开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
        buf.append(" SET T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') ");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='0' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),");
        buf.append(" T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='1' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
        buf.append(" SET T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='2' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 14- 更新BBOSS侧产品信息表的归档时间(RSRV_DATE1)、开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        buf.append(" AND T.MODIFY_TAG='0' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='1' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 15- 更新台账表的执行时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE T ");
        buf.append(" SET T.EXEC_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        Dao.executeUpdate(buf, param,Route.getJourDb());
        
        //16-订单中心修改start 更新 定价计划订单项
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRICE_PLAN T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRICE_PLAN T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());
        
        //17- 更新 商品关系订单项
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_OFFER_REL T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_OFFER_REL T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

    }

    /**
     * @param
     * @desciption 集团业务归档，更新各台账表信息 (修改)
     * @author fanti
     * @version 创建时间：2014年9月3日 下午10:25:50
     */
    protected static void modifyTradeInfo(boolean isPreDeal, boolean isMerch, IData BbossInfo, IData map, IDataset productParamInfoList, String productOperCode, String productOfferingId) throws Exception
    {

        String orderId = BbossInfo.getString("ORDER_ID");
        String merchOrderId = IDataUtil.chkParam(map, "SUBSCRIBE_ID");
        String timeStamp = map.getString("TIME_STAMP", "");
        String tradeId = BbossInfo.getString("TRADE_ID");

        // 获取台账表intf_id，受理下发失败通知时可以用该备份的初始数据重新做受理
        IData odlTradeInfos = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(odlTradeInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }
        String intfId = odlTradeInfos.getString("INTF_ID");

        if (isPreDeal)
        {
            // 更新主台账表
            IData inparams = new DataMap();
            inparams.put("MERCH_ORDER_ID", merchOrderId);
            inparams.put("TIME_STAMP", timeStamp);
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE_GRP_MERCH", "UPD_TRADE_MERCH_ORDER_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));

            // 复制台账数据，将预受理单子与受理单子区分开,copy的台账为备份数据，受理单子失败之后，再copy这次复制的内容
            CopySucPreTradeInfoBean.copyTradeInfo(tradeId, intfId, true, false);

            // 预受理相应后，省BOSS修改台账表信息
            modifyPreTradeState(tradeId);

        }
        else
        {
            // 1- 取BBOSS商品规格编号
            String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0);

            // 更新主台账表状态为P，订单生成资料
            IData inparams = new DataMap();
            inparams.put("SUBSCRIBE_STATE", "P");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", inparams, Route.getJourDb(Route.CONN_CRM_CG));

            // 更新其它台账表生失效时间
            modifyTrade(tradeId, orderId, SysDateMgr.getSysDate(), timeStamp);

            // 如果为统付业务 则需更新cenpay表
            String product_spec_code = BbossInfo.getString("PRODUCT_SPEC_CODE");
            if (("99904".equals(product_spec_code) || "99905".equals(product_spec_code) || "99908".equals(product_spec_code) || "99909".equals(product_spec_code))
                    && GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperCode)){
                BbossPayBizInfoDealbean.UpdateGrpCenpay(map, productParamInfoList, tradeId, productOfferingId);
            }

            // 针对预受理归档时备份了台账数据的场景，受理归档时删除预受理归档备份台账
            if (isMerch)
            {
                String bizMode = map.getString("SI_BIZ_MODE", "");

                IData param = new DataMap();
                param.put("TRADE_ID", tradeId);
                param.put("BIZ_MODE", bizMode);

                StringBuilder buf = new StringBuilder();
                buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
                buf.append(" SET T.BIZ_MODE=:BIZ_MODE");
                buf.append(" WHERE T.MODIFY_TAG='0' ");
                buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
                buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
                Dao.executeUpdate(buf, param,Route.getJourDb());

                IDataset pOAttTypeInfoList = IDataUtil.getDataset("POATT_TYPE", map);
                IDataset pOAttCodeInfoList = IDataUtil.getDataset("POATT_CODE", map);// 合同编码
                if (IDataUtil.isNotEmpty(pOAttTypeInfoList)) 
                {
                    for (int i = 0; i < pOAttTypeInfoList.size(); i++) 
                    {
                        String pOAttType = pOAttTypeInfoList.get(i).toString();
                        if (StringUtils.equals("1", pOAttType)) 
                        {// 合同附件
                            String contractId = pOAttCodeInfoList.get(i).toString();
                            param.clear();
                            param.put("TRADE_ID", tradeId);
                            param.put("CONTRACT_ID", contractId);
                            buf = new StringBuilder();
                            buf.append(" UPDATE TF_B_TRADE_USER T ");
                            buf.append(" SET T.CONTRACT_ID=:CONTRACT_ID");
                            buf.append(" WHERE T.MODIFY_TAG='0'");
                            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
                            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
                            Dao.executeUpdate(buf, param,Route.getJourDb(BizRoute.getTradeEparchyCode()));
                            IData custInfo = geCustInfoByEcNumber(map);
                            String custId = custInfo.getString("CUST_ID");
                            String proPoNumber = GrpCommonBean.merchToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
                            GrpCommonBean.sendAttInfoListToCliMng(map,custId,proPoNumber);
                            break;
                        }
                    }
                }
                String merchOfferId = BbossInfo.getString("MERCH_OFFER_ID");
                IDataset tradeInfos = TradeGrpMerchInfoQry.qryMerchInfoByMerchOfferId("F" + merchOfferId);

                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    DeleteTradeInfoBean.deleteTradeInfo(tradeInfos.getData(0).getString("TRADE_ID"), intfId, true);
                }
            }
            else
            {
                String productOfferId = BbossInfo.getString("PRODUCT_OFFER_ID");
                IDataset tradeInfos = TradeGrpMerchpInfoQry.qryMerchpInfoByProductOfferId("F" + productOfferId);

                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    DeleteTradeInfoBean.deleteTradeInfo(tradeInfos.getData(0).getString("TRADE_ID"), intfId, true);
                }
            }

        }
    }

    /*
     * @description 根据EC客户编号获取本省客户编号和集团编号
     * @author xunyl
     * @date 2013-06-21
     */
    protected static IData geCustInfoByEcNumber(IData map) throws Exception
    {
        // 1- 定义客户信息对象
        IData custInfo = new DataMap();

        // 2- 获取EC客户编号
        String ecCustNumber = map.getString("EC_SERIAL_NUMBER", "");

        // 3- 根据EC客户编号获取集团客户信息
        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(ecCustNumber, null);

        // 4- 获取集团客户信息为空的情况，抛出异常
        if (groupInf == null || groupInf.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_899, ecCustNumber);
        }

        // 5- 返回客户信息
        custInfo.put("CUST_ID", groupInf.getData(0).getString("CUST_ID"));
        custInfo.put("GROUP_ID", groupInf.getData(0).getString("GROUP_ID"));
        custInfo.put("CUST_NAME", groupInf.getData(0).getString("CUST_NAME"));
        return custInfo;
    }

    /*
     * 特殊
     * @description 校验本次归档的产品是否为该商品下最后一个归档的产品
     * @author xunyl
     * @date 2013-10-10
     */
    protected static void sendEsop(String tradeId, String orderId, boolean isPreDeal, IData map) throws Exception
    {
        // 1- 预受理归档是一次到位的，因此可以直接调用ESOP接口
        if (isPreDeal)
        {
            SendDataToEsopBean.synEsopData(tradeId, map);
            return;
        }

        // 2- 根据orderId查询相应的trade记录数
        IDataset tradeInfoList = UTradeInfoQry.qryTradeByOrderId(orderId, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(tradeInfoList))
        {
            return;
        }

        String merchpTradeId = "";
        // 3- 台账数据的状态如果不等于"P"则说明还有未归档的数据，不发ESOP
        for (int i = 0; i < tradeInfoList.size(); i++)
        {
            IData tradeInfo = tradeInfoList.getData(i);
            String sribeState = tradeInfo.getString("SUBSCRIBE_STATE");
            if (!"P".equals(sribeState))
            {
                return;
            }
            merchpTradeId = tradeInfo.getString("TRADE_ID");
        }

        // 4- 发送归档结果至ESOP
        SendDataToEsopBean.synEsopData(tradeId, map);

        // 5更新最后一笔产品台帐的STR1和STR2字段 STR1字段 SENDEOS STR2字段商品的trade_id 框架完工掉esop处理
        // TradeFinishCallEOSAction.java对此段逻辑进行处理
        IData inparams = new DataMap();
        inparams.put("RSRV_STR1", "SENDEOS");
        inparams.put("RSRV_STR2", map.getString("MERCH_TRADE_ID"));
        inparams.put("TRADE_ID", merchpTradeId);
        Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADESTR1STR2_BYPK", inparams, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * @author chengjian
     * @param map
     * 2014-08-15
     */
    private  static void insertBbossFilse(IData map) throws Exception
    {
        IDataset numberList = IDataUtil.getDatasetSpecl("ACCESSNUMBER", map);
        for (int i  = 0; i < numberList.size(); i++)
        {
            IData numberMap = numberList.getData(i);

            IData dataMap= new DataMap();

            dataMap.put("MP_GROUP_ID", map.getString("PROVICE_GROUP_ID"));

            dataMap.put("DATA_TYPE", numberMap.getString("VPN_CODE"));

            dataMap.put("STATUS", map.getString("FLAG"));

            dataMap.put("UPDATE_TIME", SysDateMgr.getSysDate());

            Dao.insert("TI_O_BBOSS_FILES", dataMap, Route.CONN_CRM_CG);
        }

    }
    
    //集客大厅
    public static boolean isJKDTBBossRspFile(IData map, boolean isGrp) throws Exception
    {
    	// 1- 定义返回结果
        boolean returnValue = false;

        // 2- 如果操作类型为21(合同变更)，没有产品订购关系编号，只能根据商品订购关系编号判断是否为归档报文
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if(StringUtils.equals("21", merchOperType)){
            IDataset merchOfferIdList = IDataUtil.getDatasetSpecl("RSRV_STR2", map);
            String merchOfferId = (String) merchOfferIdList.get(0);
            IDataset merchInfoList = TradeGrpMerchInfoQry.qryJKDTMerchOnlineInfoByMerchOfferId(merchOfferId,null);
            if (IDataUtil.isNotEmpty(merchInfoList))
            {
                return true;
            }else{
                return returnValue;
            }
        }

        // 3- 获取产品订购编号(由于一单多线，商品台账可能会提前挪历史，商品订购关系编号不一定能找到台账)
        IDataset productIdList = IDataUtil.getDatasetSpecl("RSRV_STR4", map);
        String productOfferingId = (String) productIdList.get(0);

        // 4- 根据成员用户编号、产品订购关系编号查询相应的台账信息
        IDataset merchpInfoList = TradeGrpMerchpInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferingId, null);

        // 5- 判断查询结果，存在台账信息则为报文归档，否则不是归档报文
        if (null != merchpInfoList && merchpInfoList.size() > 0)
        {
            returnValue = true;
        }

        // 6- 返回结果
        return returnValue;
    }
    
    //集客大厅
    public static IData dealJKDTGrpRspFile(IData map) throws Exception
    {
    	
        // 1- 定义预受理标志
        boolean isPreDeal = false;

        // 2- 获取归档报文中的一级信息(归档报文按照规范大体分为3层，第一层为商、产品基本信息，二层为资费和产品参数等信息、三层为资费参数等信息)
        IData firstProductInfo = new DataMap();
        
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 3- 更新产品参数台帐信息
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        String orderId = "";
        String tradeId = "";
        String merchTradeId = "";// 商品台帐编码
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 3-1 获取归档报文的二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);
            
            // 3-2 获取产品操作类型，根绝产品操作类型判断归档报文是否预受理归档
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue().equals(productOperCode))
            {
                isPreDeal = true;
            }

            // 3-3 收集产品参数信息
            String proNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            
            //这里会对全网编码进行转换，现在的逻辑是要查产商品的接口，不再是查询td_b_attr_biz表，在这里开了个口子，但暂时没提供产商品接口，提供后加上即可
            String productId = GrpCommonBean.merchJKDTToProduct(proNumber,1, null);
            
            IData productParam = new DataMap();
            IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            
            // 3-4 获取台账编号
            IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
            String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i));
            IDataset merchpInfoList = TradeGrpMerchpInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferingId, null);
            IData merchpInfo = merchpInfoList.getData(0);
            tradeId = merchpInfo.getString("TRADE_ID");
            orderId = merchpInfo.getString("ORDER_ID");
            map.put("ORDER_ID", orderId);// 一单多线 查询订单用
            
            // 3-5 更新产品参数台账信息
            UpdateAttrInfoBean.dealJKDTAttrTradeInfo(productParamInfoList, tradeId);

            // 3-6 更新其它信息 集客大厅
            modifyJKDTTradeInfo(isPreDeal, false, merchpInfo, map, productParamInfoList, productOperCode, productOfferingId);

        }

        // 4- 处理特殊业务的资费问题，包括功能费和一次性费用
        // DealBBossDiscntBean.dealSpecialBizDiscnt(map, "0");
        
        // 5- 处理特殊业务的资费问题(跨省专线2.0，预受理转正式受理时调用反向接口增加资费信息 add by wangzc7)
        DealBBossDiscntBean.dealJKDTSpecialBizDiscnt2(map, "0");

        // 6- 更新商品台账信息(像专线业务，只要有一个产品归档，则商品台账信息就移历史了)
        String merchOfferId = map.getString("RSRV_STR2", "");
        IDataset merchInfoList = TradeGrpMerchInfoQry.qryJKDTMerchOnlineInfoByMerchOfferId(merchOfferId, null);
        if (IDataUtil.isNotEmpty(merchInfoList))
        {
            IData merchInfo = merchInfoList.getData(0);
            merchTradeId = merchInfo.getString("TRADE_ID");
            modifyJKDTTradeInfo(isPreDeal, true, merchInfo, map, null, null, null);

            // 更新STR1字段 SKIPSENDESOP 完工时候不调esop接口
            IData inparams = new DataMap();
            inparams.put("RSRV_STR1", "SKIPSENDESOP");
            inparams.put("TRADE_ID", merchTradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_RSRV_STR1", inparams, Route.getJourDb(Route.CONN_CRM_CG));
        }

        // 6- 其它特殊情况处理(典型场景，集团客户一点支付的暂停与恢复的场合，需要暂停或者恢复成员的付费关系)
        dealSpecialBiz(map);

        // 7- 调用ESOP接口(如果是一单多线的场合，需要判断是否为最后一个归档的产品，只有最后归档的产品才调用该接口)
        // 是否走esop流程
        if (BizEnv.getEnvBoolean("isesop", false))
        {
            sendEsop(merchTradeId, orderId, isPreDeal, map);
        }

        // 8- 返回受理成功标志
        IData dealResult = new DataMap();
        dealResult.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        dealResult.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        dealResult.put("RSPCODE", "00");
        dealResult.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        dealResult.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        dealResult.put("EC_SERIAL_NUMBER", IDataUtil.chkParam(map, "EC_SERIAL_NUMBER"));
        dealResult.put("SUBSCRIBE_ID", IDataUtil.chkParam(map, "SUBSCRIBE_ID"));
        return dealResult;
    }
    
    protected static void modifyJKDTTradeInfo(boolean isPreDeal, boolean isMerch, IData BbossInfo, IData map, IDataset productParamInfoList, String productOperCode, String productOfferingId) throws Exception
    {

        String orderId = BbossInfo.getString("ORDER_ID");
        String merchOrderId = IDataUtil.chkParam(map, "SUBSCRIBE_ID");
        String timeStamp = map.getString("TIME_STAMP", "");
        String tradeId = BbossInfo.getString("TRADE_ID");

        // 获取台账表intf_id，受理下发失败通知时可以用该备份的初始数据重新做受理
        IData odlTradeInfos = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", Route.CONN_CRM_CG);

        if (IDataUtil.isEmpty(odlTradeInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_209);
        }
        String intfId = odlTradeInfos.getString("INTF_ID");

        if (isPreDeal)
        {
            // 更新主台账表
            IData inparams = new DataMap();
            inparams.put("MERCH_ORDER_ID", merchOrderId);
            inparams.put("TIME_STAMP", timeStamp);
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE_ECRECEP_OFFER", "UPD_TRADE_MERCH_ORDER_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));

            // 复制台账数据，将预受理单子与受理单子区分开,copy的台账为备份数据，受理单子失败之后，再copy这次复制的内容
            CopySucPreTradeInfoBean.copyJKDTTradeInfo(tradeId, intfId, true, false);

            // 预受理相应后，省BOSS修改台账表信息 集客大厅
            modifyJKDTPreTradeState(tradeId);

        }
        else
        {
            // 1- 取BBOSS商品规格编号
            String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0);

            // 更新主台账表状态为P，订单生成资料
            IData inparams = new DataMap();
            inparams.put("SUBSCRIBE_STATE", "P");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", inparams, Route.getJourDb(Route.CONN_CRM_CG));

            // 更新其它台账表生失效时间(待修改)
            modifyJKDTTrade(tradeId, orderId, SysDateMgr.getSysDate(), timeStamp);

            // 如果为统付业务 则需更新cenpay表
            String product_spec_code = BbossInfo.getString("PRODUCT_SPEC_CODE");
            if (("99904".equals(product_spec_code) || "99905".equals(product_spec_code) || "99908".equals(product_spec_code) || "99909".equals(product_spec_code))
                    && GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperCode)){
                BbossPayBizInfoDealbean.UpdateGrpCenpay(map, productParamInfoList, tradeId, productOfferingId);
            }

            // 针对预受理归档时备份了台账数据的场景，受理归档时删除预受理归档备份台账
            if (isMerch)
            {
                String bizMode = map.getString("SI_BIZ_MODE", "");

                IData param = new DataMap();
                param.put("TRADE_ID", tradeId);
                param.put("BIZ_MODE", bizMode);

                StringBuilder buf = new StringBuilder();
                buf.append(" UPDATE TF_B_TRADE_ECRECEP_OFFER T ");
                buf.append(" SET T.BIZ_MODE=:BIZ_MODE");
                buf.append(" WHERE T.MODIFY_TAG='0' ");
                buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
                buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
                Dao.executeUpdate(buf, param,Route.getJourDb());

                IDataset pOAttTypeInfoList = IDataUtil.getDataset("POATT_TYPE", map);
                IDataset pOAttCodeInfoList = IDataUtil.getDataset("POATT_CODE", map);// 合同编码
                if (IDataUtil.isNotEmpty(pOAttTypeInfoList)) 
                {
                    for (int i = 0; i < pOAttTypeInfoList.size(); i++) 
                    {
                        String pOAttType = pOAttTypeInfoList.get(i).toString();
                        if (StringUtils.equals("1", pOAttType)) 
                        {// 合同附件
                            String contractId = pOAttCodeInfoList.get(i).toString();
                            param.clear();
                            param.put("TRADE_ID", tradeId);
                            param.put("CONTRACT_ID", contractId);
                            buf = new StringBuilder();
                            buf.append(" UPDATE TF_B_TRADE_USER T ");
                            buf.append(" SET T.CONTRACT_ID=:CONTRACT_ID");
                            buf.append(" WHERE T.MODIFY_TAG='0'");
                            buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
                            buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
                            Dao.executeUpdate(buf, param,Route.getJourDb(BizRoute.getTradeEparchyCode()));
                            IData custInfo = geCustInfoByEcNumber(map);
                            String custId = custInfo.getString("CUST_ID");
                            String proPoNumber = GrpCommonBean.merchJKDTToProduct(poNumber,0, null);// 商品编号转化为本地产品编号
                            GrpCommonBean.sendAttInfoListToCliMng(map,custId,proPoNumber);
                            break;
                        }
                    }
                }
                String merchOfferId = BbossInfo.getString("MERCH_OFFER_ID");
                IDataset tradeInfos = TradeGrpMerchInfoQry.qryJKDTMerchInfoByMerchOfferId("F" + merchOfferId);

                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    DeleteTradeInfoBean.deleteTradeInfo(tradeInfos.getData(0).getString("TRADE_ID"), intfId, true);
                }
            }
            else
            {
                String productOfferId = BbossInfo.getString("PRODUCT_OFFER_ID");
                IDataset tradeInfos = TradeGrpMerchpInfoQry.qryJKDTMerchpInfoByProductOfferId("F" + productOfferId);

                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    DeleteTradeInfoBean.deleteTradeInfo(tradeInfos.getData(0).getString("TRADE_ID"), intfId, true);
                }
            }

        }
    }
    
    //集客大厅
    public static void modifyJKDTPreTradeState(String tradeId) throws Exception
    {
        // 1- 更新主台账表
        IData inparams = new DataMap();
        IDataset tradeInfoList = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData tradeInfo = tradeInfoList.getData(0);
        if (StringUtils.equals("6", tradeInfo.getString("IN_MODE_CODE")))
        {// 配合省(业务落地省)
            inparams.put("FLAG", "");
            inparams.put("DESC", "预受理BBOSS已响应");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BBOSS_INFO", inparams, Route.getJourDb(Route.CONN_CRM_CG));

        }
        else
        {// 主办省（业务发起省）
            inparams.put("FLAG", "1");
            inparams.put("DESC", "预受理BBOSS已响应");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BBOSS_INFO", inparams, Route.getJourDb(Route.CONN_CRM_CG));
        }

        // 2- 更新产品参数表和产品资费表(区分哪些记录需要发送服务开通，预受理转正式受理时默认这两张表都不发送服开)
        TradeGrpMerchpDiscntInfoQry.updateMechpDiscntIsSendPfByTradeid(tradeId);
        TradeAttrInfoQry.updIsSendPfTradeid(tradeId);

        // 3- 更新OTHER表OPER_CODE为 06
        inparams.clear();
        inparams.put("TRADEID", tradeId);
        inparams.put("RSRV_VALUE_CODE", "BBSS");
        inparams.put("OPER_CODE", "06");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_OPER_CODE_BY_TRADE_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));

        // 4- 更新MERCHP表RSRV_STR1为 1
        inparams.clear();
        inparams.put("TRADEID", tradeId);
        inparams.put("RSRV_STR1", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ECRECEP_PRODUCT", "UPD_RSRV_STR1_BY_TRADE_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    //集客大厅
    public static void modifyJKDTTrade(String tradeId, String orderId, String sysDate, String timeStamp) throws Exception
    {
        // 1- 设置查询参数
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SYS_DATE", sysDate);
        param.put("ORDER_ID", orderId);
        param.put("RSRV_DATE1", timeStamp);

        // 2- 更新优惠表的开始和结束时间
        StringBuilder buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 3- 更新服务表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVC T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVC T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 4- 更新付费关系表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
        buf.append(" SET T.start_cycle_id=to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" WHERE T.start_cycle_id < to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
        buf.append(" SET T.end_cycle_id=to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" WHERE T.end_cycle_id < to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 5- 更新服务状态表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 6- 更新产品参数表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ATTR T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ATTR T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 7- 更新产品表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 8- 更新BB关系表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_RELATION_BB T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_RELATION_BB T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 9- 更新BBOSS侧商品资费表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 10- 更新BBOSS侧产品资费表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 11- 更新用户表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_USER T ");
        buf.append(" SET T.OPEN_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.OPEN_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_USER T ");
        buf.append(" SET T.DESTROY_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.DESTROY_TIME < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 12- 更新订单表的执行时间(由于存在一单多台账的情况，可能会重复执行)
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_ORDER T ");
        buf.append(" SET T.EXEC_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.ORDER_ID=TO_NUMBER(:ORDER_ID)");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 13- 更新BBOSS侧商品信息表的归档时间(RSRV_DATE1)、开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_OFFER T ");
        buf.append(" SET T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') ");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='0' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_OFFER T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),");
        buf.append(" T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='1' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_OFFER T ");
        buf.append(" SET T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='2' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 14- 更新BBOSS侧产品信息表的归档时间(RSRV_DATE1)、开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_PRODUCT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        buf.append(" AND T.MODIFY_TAG='0' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_PRODUCT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='1' ");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        // 15- 更新台账表的执行时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE T ");
        buf.append(" SET T.EXEC_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        Dao.executeUpdate(buf, param,Route.getJourDb());
        
        //16-订单中心修改start 更新 定价计划订单项
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRICE_PLAN T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRICE_PLAN T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());
        
        //17- 更新 商品关系订单项
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_OFFER_REL T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_OFFER_REL T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param,Route.getJourDb());

    }
}
