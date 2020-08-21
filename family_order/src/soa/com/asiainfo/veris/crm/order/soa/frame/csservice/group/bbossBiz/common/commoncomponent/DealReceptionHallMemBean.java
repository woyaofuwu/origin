package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @program: order_hunan
 * @description:
 * @author: zhangchengzhi
 * @create: 2018-10-09 10:55
 **/

public class DealReceptionHallMemBean {

	private final static Logger logger = Logger.getLogger(DealReceptionHallMemBean.class);

    /*
     * @description 判断是否为归档报文
     * @author xunyl
     * @date 2013-09-09
     */
    public static boolean isReceptionHallRspFile(IData map, boolean isGrp) throws Exception {
        // 1- 定义返回结果
        boolean returnValue = false;

        // 2- 获取产品订购编号(集团归档与成员归档的报文字段不一致)
        if (isGrp) {
            returnValue = isJKDTGrpRspFile(map);
        } else {
            returnValue = isReceptionHallMebRspFile(map);
        }

        // 3- 返回结果
        return returnValue;
    }
    /*
     * @description 判断是否为集团的归档报文
     * @author xunyl
     * @date 2013-09-09
     */
    protected static boolean isJKDTGrpRspFile(IData map) throws Exception
    {
        // 1- 定义返回结果
        boolean returnValue = false;

        // 2- 如果操作类型为21(合同变更)，没有产品订购关系编号，只能根据商品订购关系编号判断是否为归档报文
        String merchOperType = (String) IDataUtil.getDataset("OPERA_TYPE", map, false).get(0);
        if(StringUtils.equals("21", merchOperType)){
            IDataset merchOfferIdList = IDataUtil.getDatasetSpecl("RSRV_STR2", map);
            String merchOfferId = (String) merchOfferIdList.get(0);
            IDataset merchInfoList = TradeEcrecepOfferInfoQry.qryJKDTMerchOnlineInfoByMerchOfferId(merchOfferId,null);
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
        IDataset merchpInfoList = TradeEcrecepProductInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferingId, null);

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
    protected static boolean isReceptionHallMebRspFile(IData map) throws Exception {
        // 1- 定义返回结果
        boolean returnValue = false;

        // 2- 获取产品订购编号
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", map);
        String productOfferingId = (String) productIdList.get(0);

        // 3- 根据产品订购关系编号查询相应的台账信息
        String memUserId = MebCommonBean.getJKDTMemberUserId(map);

        // 3-1 如果User_id是空，默认是开通报文，返回false
        if (StringUtils.isBlank(memUserId)) {
            return false;
        }

        IDataset merchpInfoList = TradeReceptionHallMebInfoQry.qryMerchMebInfoByUserIdOfferIdRouteId(memUserId, productOfferingId, CSBizBean.getUserEparchyCode());

        // 4- 判断查询结果，存在台账信息则为报文归档，否则不是归档报文
        if (null != merchpInfoList && merchpInfoList.size() > 0) {
            returnValue = true;
        }

        // 5- 返回结果
        return returnValue;
    }

    /*
     * @description 成员业务归档
     * @author xunyl
     * @date 2013-09-12
     */
    public static IData dealMebRspFile(IData map) throws Exception {
        // 1- 获取产品订购编号
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", map);
        String productOfferingId = (String) productIdList.get(0);

        // 2- 根据产品订购关系编号查询相应的台账信息
        String memUserId = MebCommonBean.getJKDTMemberUserId(map);
        IDataset merchpInfoList = TradeReceptionHallMebInfoQry.qryMerchMebInfoByUserIdOfferIdRouteId(memUserId, productOfferingId, CSBizBean.getUserEparchyCode());
        IData merchpInfo = merchpInfoList.getData(0);
        String tradeId = merchpInfo.getString("TRADE_ID");

        // 3- 更新产品主台账表状态
        // 3-1 查出集团产品台账
        IDataset grpMerchpInfo = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferingId);
        String product_id = "";
        String grpUserId = "";
        IData param = new DataMap();
        if (IDataUtil.isNotEmpty(grpMerchpInfo)) {
            product_id = grpMerchpInfo.getData(0).getString("PRODUCT_SPEC_CODE");
            grpUserId = grpMerchpInfo.getData(0).getString("USER_ID");
        }

//        // 跨省V网对SCP和GSCP的改造
//        if(StringUtils.equals("", product_id))
//        {
//        	insertBbossFilse(map);
//        }
        IData again_pf = StaticInfoQry.getStaticInfoByTypeIdDataId("AGAIN_PF", product_id);
        // 3-3 判断是否要再次发送服开 通知网元
        if (IDataUtil.isNotEmpty(again_pf)) {
            if (StringUtils.equals("9101101", product_id)) {//和对讲业务，发送APN服务
                // 新增服务表记录
                DealPocBizBean.rigistApnSeviceTrd(memUserId, tradeId);
            }
            // 修改主台账
            param.clear();
            param.put("SUBSCRIBE_STATE", "0");
            param.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", param, Route.getJourDb(CSBizBean.getUserEparchyCode()));

            // 修改订单表
            IDataset trades = TradeInfoQry.getMainTradeByTradeId(tradeId, CSBizBean.getUserEparchyCode());
            if (IDataUtil.isNotEmpty(trades)) {
                String order_id = trades.getData(0).getString("ORDER_ID");
                param.clear();
                param.put("ORDER_ID", order_id);
                param.put("APP_TYPE", "C");
                param.put("HQ_TAG", "");
                Dao.executeUpdateByCodeCode("TF_B_ORDER", "UP_ORSTAHQBYORID", param, Route.getJourDb(BizRoute.getRouteId()));
            }

        }
        // 针对行业网关云MAS业务，需要重新发送服开通知行业网关
        else if (BbossIAGWCloudMASDealBean.isCloudMasRspMeb(map.getString("SERIAL_NUMBER"), grpUserId, memUserId, GrpCommonBean.merchJKDTToProduct(product_id, 2, product_id))) {
            param.clear();
            param.put("SUBSCRIBE_STATE", "0");
            param.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", param, Route.getJourDb(CSBizBean.getUserEparchyCode()));
            // 修改订单
            IDataset trades = TradeInfoQry.getMainTradeByTradeId(tradeId, CSBizBean.getUserEparchyCode());
            if (IDataUtil.isNotEmpty(trades)) {
                String order_id = trades.getData(0).getString("ORDER_ID");
                param.clear();
                param.put("ORDER_ID", order_id);
                param.put("APP_TYPE", "C");
                param.put("HQ_TAG", "");
                Dao.executeUpdateByCodeCode("TF_B_ORDER", "UP_ORSTAHQBYORID", param, Route.getJourDb(BizRoute.getRouteId()));
            }
        } else {

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
            String productId = GrpCommonBean.merchJKDTToProduct(proNumber, 2,null);
            IData productParam = new DataMap();
            IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);

            //过滤没有变更的记录
            if(IDataUtil.isEmpty(productParamInfoList)){
                continue;
            }
            IDataset delAttrCodeList = new DatasetList();
            for(int j=0;j<productParamInfoList.size();j++){
                IData productParamInfo = productParamInfoList.getData(j);
                String paramState = productParamInfo.getString("STATE");
                if (StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue(), paramState)){
                    continue;
                }
                String attrCode = productParamInfo.getString("ATTR_CODE");
                IData qryDelAttrResult = getDelAttrInfo(productParamInfoList, productParamInfo);
                if(qryDelAttrResult.getBoolean("isExistDelAttr")){
                    delAttrCodeList.add(attrCode);
                }
            }
            for(int k =0;k<delAttrCodeList.size();k++){
                String delAttrCode = delAttrCodeList.get(k).toString();
                for(int y=0;y<productParamInfoList.size();y++){
                    IData productParamInfo = productParamInfoList.getData(y);
                    String  attrCode = productParamInfo.getString("ATTR_CODE");
                    if(StringUtils.equals(delAttrCode, attrCode)){
                        productParamInfoList.remove(y);
                        y--;
                    }
                }
            }

            // 3-4 获取台账编号
            IDataset productOfferingIdList = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
            //String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i));
            String productOfferingId = GrpCommonBean.nullToString(productOfferingIdList.get(i)).replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
            logger.info("productOfferingIdList(hzl)=="+productOfferingIdList);
            logger.info("productOfferingId(hzl)=="+productOfferingId);
            IDataset merchpInfoList = TradeEcrecepProductInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferingId,null);
            IData merchpInfo = merchpInfoList.getData(0);
            tradeId = merchpInfo.getString("TRADE_ID");
            orderId = merchpInfo.getString("ORDER_ID");
            map.put("ORDER_ID", orderId);// 一单多线 查询订单用
            logger.info("merchpInfo(hzl)=="+merchpInfo);


            // 3-5 更新产品参数台账信息dealJKDTAttrTradeInfo
            UpdateAttrInfoBean.dealJKDTAttrTradeInfo(productParamInfoList, tradeId);
            //处理资费台账--add by huangzl3
            dealProductRateInfo(merchpInfo,secondProductInfo);
            // 3-6 更新其它信息
            modifyTradeInfo(isPreDeal, false, merchpInfo, map, productParamInfoList, productOperCode, productOfferingId);

        }

        // 4-1 处理特殊业务的资费问题，包括功能费和一次性费用
        // 4-2 处理特殊业务的资费问题(跨省专线2.0，预受理转正式受理时调用反向接口增加资费信息 add by wangzc7)
        DealBBossDiscntBean.dealSpecialBizDiscnt(map, "0");

        // 5- 更新商品台账信息(像专线业务，只要有一个产品归档，则商品台账信息就移历史了)
        String merchOfferId = map.getString("RSRV_STR2", "");// 商品订购关系ID
		//处理商品台账不处理问题--add by huangzl3
        IDataset merchInfoList = TradeEcrecepOfferInfoQry.qryJKDTMerchInfoByMerchOfferId(merchOfferId);
        if (IDataUtil.isNotEmpty(merchInfoList))
        {
            IData merchInfo = merchInfoList.getData(0);
            merchTradeId = merchInfo.getString("TRADE_ID");
            orderId = merchInfo.getString("ORDER_ID");
            modifyTradeInfo(isPreDeal, true, merchInfo, map, null, null, null);

            // 更新STR1字段 SKIPSENDESOP 完工时候不调esop接口
            IData inparams = new DataMap();
            inparams.put("RSRV_STR1", "SKIPSENDESOP");
            inparams.put("TRADE_ID", merchTradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_RSRV_STR1", inparams, Route.getJourDb(Route.CONN_CRM_CG));
        }



        // 7- 调用ESOP接口(如果是一单多线的场合，需要判断是否为最后一个归档的产品，只有最后归档的产品才调用该接口)
        //是否走esop流程
        if(BizEnv.getEnvBoolean("isesop", false))
        {
            sendEsop(merchTradeId,orderId,isPreDeal,map);
        }

        // 8- 返回受理成功标志
        IData dealResult = new DataMap();
        dealResult.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        dealResult.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        dealResult.put("RSPCODE", "00");
        dealResult.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        dealResult.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        dealResult.put("EC_SERIAL_NUMBER", map.getString("EC_SERIAL_NUMBER", ""));// 商品订购关系ID);
        dealResult.put("SUBSCRIBE_ID", map.getString("SUBSCRIBE_ID", ""));
        return dealResult;
    }
    
    /*
     * @description 查询归档参数中该新增的参数是否存在有对应的被删除的记录且值相等
     * @author huangzl3
     * @date 2020-03-25
     */
    public static void dealProductRateInfo(IData merchPInfo,IData map) throws Exception
    {

        // 2- 获取产品资费信息
        IDataset merchPDsts = map.getDataset("RATE_PLAN_ID");
        IDataset actionlist = map.getDataset("RATE_PLAN_ACTION");
        logger.debug("merchPInfo==(hzl)"+merchPInfo);
        logger.debug("TRADE_ID==(hzl)"+merchPInfo.getString("TRADE_ID"));

        
        String poRatePolicyEffRule = map.getString("PO_RATE_POLICY_EFF_RULE");//套餐生效规则
        
        
       
        // 处理无需入表的情况
        if (merchPDsts == null || merchPDsts.size() == 0)
        {
        	return;
        }

        // 处理BBOSS侧资费表信息
        for (int i = 0; i < merchPDsts.size(); i++)
        {
            IData merchPDst = new DataMap();

            //资费状态为删除或者资费编码为空串皆属于无效资费，不进行处理
            if (StringUtils.equals("0", nullToString(actionlist.get(i))) || "".equals(nullToString(merchPDsts.get(i))))
            {
                continue;
            }
   
            // 拼装BBOSS侧资费表信息
            merchPDst.put("TRADE_ID", merchPInfo.getString("TRADE_ID"));
            merchPDst.put("USER_ID", merchPInfo.getString("USER_ID"));
            merchPDst.put("MERCH_SPEC_CODE", merchPInfo.getString("MERCH_SPEC_CODE"));
            merchPDst.put("PRODUCT_ORDER_ID", merchPInfo.getString("PRODUCT_ORDER_ID"));
            merchPDst.put("PRODUCT_OFFER_ID", merchPInfo.getString("PRODUCT_OFFER_ID", ""));
            merchPDst.put("PRODUCT_SPEC_CODE", merchPInfo.getString("PRODUCT_SPEC_CODE"));
            merchPDst.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
            if("2".equals(poRatePolicyEffRule)){ 
            	//下账期生效，下月第一天生效
            	merchPDst.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
            }else if("4".equals(poRatePolicyEffRule)){
            	//下一天生效
            	merchPDst.put("START_DATE", SysDateMgr.getTomorrowDate());
            }else {
            	//poRatePolicyEffRule为1，立即生效
            	merchPDst.put("START_DATE", SysDateMgr.getSysDate());
            }         
            merchPDst.put("END_DATE", SysDateMgr.getTheLastTime());
            merchPDst.put("PRODUCT_DISCNT_CODE", nullToString(merchPDsts.get(i)));
            merchPDst.put("INST_ID", SeqMgr.getInstId());
            merchPDst.put("UPDATE_STAFF_ID", "IBOSS000");
            merchPDst.put("UPDATE_DEPART_ID", "00309");
            merchPDst.put("RSRV_STR1", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());// BBOSS侧参数状态，服开拼报文用
            merchPDst.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(merchPInfo.getString("TRADE_ID")));
            logger.debug("merchPDsts==(hzl)"+merchPDst);
            
            // 将资费信息入资费台账子表
            Dao.insert("TF_B_TRADE_GRP_MERCHP_DISCNT", merchPDst,Route.getJourDb());
            dealTradeIntfId(merchPInfo.getString("TRADE_ID"));
        }
        
    }
    
    /**
     * @description，更新TF_B_TRADE的INTF_ID
     * @author huangzl3
     * @date 2020-03-25
     */
	private static void dealTradeIntfId(String tradeId)throws Exception {
		IDataset tradeInfoSet = TradeInfoQry.getMainTradeByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeInfoSet))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData map = tradeInfoSet.getData(0);
        String intfId = map.getString("INTF_ID", "");
        intfId += "TF_B_TRADE_GRP_MERCHP_DISCNT,";

        map.put("INTF_ID", intfId);
        TradeInfoQry.updateTradeIntfID(map);
	}
	
    
    /**
     * @description 将null转化为空串
     * @author huangzl3
     * @date 2020-03-25
     */
    public static String nullToString(Object obj) throws Exception
    {
        if (null == obj)
        {
            return "";
        }
        else
        {
            return obj.toString();
        }
    }

    /*
     * @description 查询归档参数中该新增的参数是否存在有对应的被删除的记录且值相等
     * @author xunyl
     * @date 2013-09-15
     */
    protected static IData getDelAttrInfo(IDataset productParamInfoList, IData addParamInfo) throws Exception
    {
        // 1- 定义返回数据
        IData qryResult = new DataMap();
        qryResult.put("isExistDelAttr", false);

        // 2- 获取参数编号
        String addAttrCode = addParamInfo.getString("ATTR_CODE");

        // 3- 获取参数值
        String addAttrValue = addParamInfo.getString("ATTR_VALUE");

        // 4- 获取参数的属性组编号
        String addAttrGroup = addParamInfo.getString("ATTR_GROUP");

        // 5- 查询归档参数中是否有该参数被删除的记录
        for (int i = 0, sizei = productParamInfoList.size(); i < sizei; i++)
        {
            IData productParamInfo = productParamInfoList.getData(i);
            String state = productParamInfo.getString("STATE");
            String attrCode = productParamInfo.getString("ATTR_CODE");
            String attrGroup = productParamInfo.getString("ATTR_GROUP");
            // 三种场景需要结束本次循环:(1)参数编号不相等，非同一属性 (2)产品参数编号非删除状态 (3)新增的参数属于属性组，但本次循环的参数不属于属性组或者两者属性组编号不等
            if (!StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue(), state) ||
                    !StringUtils.equals(addAttrCode,attrCode) ||
                    (!StringUtils.isEmpty(addAttrGroup) && !addAttrGroup.equals(attrGroup)))
            {
                continue;
            }
            String delAttrValue = productParamInfo.getString("ATTR_VALUE");
            if(StringUtils.equals(addAttrValue, delAttrValue)){
                qryResult.put("delIndex", i);
                qryResult.put("isExistDelAttr", true);
            }
            return qryResult;
        }
        return qryResult;
    }

    /*
     * @description 集团业务归档，更新各台账表信息
     * @author xunyl
     * @date 2013-09-16
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

        if(isPreDeal){
            // 更新主台账表
            IData inparams = new DataMap();
            inparams.put("MERCH_ORDER_ID", merchOrderId);
            inparams.put("TIME_STAMP", timeStamp);
            inparams.put("TRADEID", tradeId);
            TradeEcrecepOfferInfoQry.updateJKDTMerchInfoByMerchOfferId(merchOrderId,timeStamp,tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE_ECRECEP_OFFER", "UPD_TRADE_MERCH_ORDER_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));

            // 复制台账数据，将预受理单子与受理单子区分开,copy的台账为备份数据，受理单子失败之后，再CopySucPreTradeInfoBean.copyTradeInfo(tradeId, intfId, true, false);copy这次复制的内容
            CopySucPreTradeInfoBean.copyTradeInfo(tradeId, intfId, true, false);

            // 预受理相应后，省BOSS修改台账表信息
            modifyPreTradeState(tradeId);


        }
        else
        {
            // 1- 取BBOSS商品规格编号
            String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0);

            // 2- 省行业网关云MAS，如果是省boss办理，然后由bboss下发归档的，需要重新发服务开通，通知省行业网关
            if ("010101016".equals(poNumber) || "010101017".equals(poNumber))
            {
                // 2.1  更新主台账表状态为0，订单重发服开
                IData inparams = new DataMap();
                inparams.put("SUBSCRIBE_STATE", "0");
                inparams.put("TRADEID", tradeId);
                Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", inparams, Route.getJourDb(Route.CONN_CRM_CG));

                // 更新其它台账表生失效时间
                modifyTrade(tradeId, orderId, SysDateMgr.getSysDate(), timeStamp);
            }
            else
            {
                // 更新主台账表状态为P，订单生成资料
                IData inparams = new DataMap();
                inparams.put("SUBSCRIBE_STATE", "P");
                inparams.put("TRADEID", tradeId);
                Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_STATE", inparams, Route.getJourDb(Route.CONN_CRM_CG));

                // 更新其它台账表生失效时间
                modifyTrade(tradeId, orderId, SysDateMgr.getSysDate(), timeStamp);
            }
            // 如果为统付业务 则需更新cenpay表
            String product_spec_code = BbossInfo.getString("PRODUCT_SPEC_CODE");
            if (("99904".equals(product_spec_code) || "99905".equals(product_spec_code) || "99908".equals(product_spec_code) || "99909".equals(product_spec_code))
                    && GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperCode)){
                BbossPayBizInfoDealbean.UpdateGrpCenpay(map, productParamInfoList, tradeId, productOfferingId);
            }

            // 针对预受理归档时备份了台账数据的场景，受理归档时删除预受理归档备份台账
            if (isMerch)
            {
                String merchOfferId = BbossInfo.getString("MERCH_OFFER_ID");
                IDataset tradeInfos = TradeEcrecepOfferInfoQry.qryJKDTMerchInfoByMerchOfferId("F" + merchOfferId);

                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    DeleteTradeInfoBean.deleteTradeInfo(tradeInfos.getData(0).getString("TRADE_ID"), intfId, true);
                }
            }
            else
            {
                String productOfferId = BbossInfo.getString("PRODUCT_OFFER_ID");
                IDataset tradeInfos = TradeEcrecepProductInfoQry.qryJKDTMerchpInfoByProductOfferId("F" + productOfferId);

                if (IDataUtil.isNotEmpty(tradeInfos))
                {
                    DeleteTradeInfoBean.deleteTradeInfo(tradeInfos.getData(0).getString("TRADE_ID"), intfId, true);
                }
            }

        }
    }
    /*
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
    /*
     * @descripiton 预受理相应后，省BOSS修改台账表信息，例如修改操作类型、相应状态等
     * @author xunyl
     * @date 2014-03-06
     */
    public static void modifyPreTradeState(String tradeId)throws Exception{
        //1-  更新主台账表
        IData inparams = new DataMap();
        IDataset tradeInfoList = TradeInfoQry.getTradeInfobyTradeId(tradeId);
        if (IDataUtil.isEmpty(tradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData tradeInfo = tradeInfoList.getData(0);
        if(StringUtils.equals("6", tradeInfo.getString("IN_MODE_CODE"))){//配合省
            inparams.put("FLAG", "");
            inparams.put("DESC", "预受理BBOSS已响应");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BBOSS_INFO", inparams, Route.getJourDb(Route.CONN_CRM_CG));

        }else{//主办省
            inparams.put("FLAG", "1");
            inparams.put("DESC", "预受理BBOSS已响应");
            inparams.put("TRADEID", tradeId);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BBOSS_INFO", inparams, Route.getJourDb(Route.CONN_CRM_CG));
        }

        //2- 更新产品参数表和产品资费表(区分哪些记录需要发送服务开通，预受理转正式受理时默认这两张表都不发送服开)
        TradeGrpMerchpDiscntInfoQry.updateMechpDiscntIsSendPfByTradeid(tradeId);
        TradeAttrInfoQry.updIsSendPfTradeid(tradeId);

        //3- 更新OTHER表OPER_CODE为 06
        inparams.clear();
        inparams.put("TRADEID", tradeId);
        inparams.put("RSRV_VALUE_CODE", "BBSS");
        inparams.put("OPER_CODE", "06");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_OPER_CODE_BY_TRADE_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));

        //4- 更新MERCHP表RSRV_STR1为 1
        inparams.clear();
        inparams.put("TRADEID", tradeId);
        inparams.put("RSRV_STR1", "1");
        Dao.executeUpdateByCodeCode("TF_B_TRADE_ECRECEP_PRODUCT", "UPD_RSRV_STR1_BY_TRADE_ID", inparams, Route.getJourDb(Route.CONN_CRM_CG));
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
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 3- 更新服务表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVC T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVC T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 4- 更新付费关系表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
        buf.append(" SET T.start_cycle_id=to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" WHERE T.start_cycle_id < to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PAYRELATION T ");
        buf.append(" SET T.end_cycle_id=to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" WHERE T.end_cycle_id < to_char(TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 5- 更新服务状态表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDbDefault());

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_SVCSTATE T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDbDefault());

        // 6- 更新产品参数表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ATTR T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ATTR T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 7- 更新产品表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_PRODUCT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 8- 更新UU关系表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_RELATION T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_RELATION T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 9- 更新BBOSS侧商品资费表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 10- 更新BBOSS侧产品资费表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.END_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 11- 更新用户表的开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_USER T ");
        buf.append(" SET T.OPEN_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.OPEN_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='0' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_USER T ");
        buf.append(" SET T.DESTROY_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.DESTROY_TIME < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" AND T.MODIFY_TAG='1' ");
        buf.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 12- 更新订单表的执行时间(由于存在一单多台账的情况，可能会重复执行)
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_ORDER T ");
        buf.append(" SET T.EXEC_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.ORDER_ID=TO_NUMBER(:ORDER_ID)");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 13- 更新BBOSS侧商品信息表的归档时间(RSRV_DATE1)、开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
        buf.append(" SET T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') ");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='0' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS'),");
        buf.append(" T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='1' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_GRP_MERCH T ");
        buf.append(" SET T.RSRV_DATE1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='2' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 14- 更新BBOSS侧产品信息表的归档时间(RSRV_DATE1)、开始和结束时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_PRODUCT T ");
        buf.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");
        buf.append(" AND T.MODIFY_TAG='0' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE_ECRECEP_PRODUCT T ");
        buf.append(" SET T.END_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        buf.append(" AND T.MODIFY_TAG='1' ");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));

        // 15- 更新台账表的执行时间
        buf = new StringBuilder();
        buf.append(" UPDATE TF_B_TRADE T ");
        buf.append(" SET T.EXEC_TIME=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        buf.append(" WHERE T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        Dao.executeUpdate(buf, param, Route.getJourDb(BizRoute.getRouteId()));
    }

}
