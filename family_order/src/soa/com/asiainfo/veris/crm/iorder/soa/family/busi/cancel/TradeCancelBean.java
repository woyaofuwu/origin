package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel;


import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeCancelParamData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeCancelReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeCancelReqData.CancelTradeData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeDependRelaData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.builder.TradeCancelReqDataBuilder;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class TradeCancelBean extends CSBizBean {

	private static final transient Logger logger = Logger.getLogger(TradeCancelBean.class);

	public IDataset getUserUnfinishedTradeList(IData param) throws Exception {
		String serialNum = IDataUtil.chkParam(param, "SERIAL_NUMBER");
		IDataset unfinishedTradeList = new DatasetList();

		this.checkBeforeQuery(serialNum);

		IDataset config = CommparaInfoQry.getCommByParaAttr("CSM", "1540", CSBizBean.getTradeEparchyCode());
		if (IDataUtil.isEmpty(config)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1210, "1540");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("撤单配置信息：" + config.toString());
		}

		for (int i = 0, s = config.size(); i < s; i++) {
			IData data = config.getData(i);
			String tradeType = data.getString("PARAM_CODE");
			String className = data.getString("PARA_CODE25").trim();
			param.put("TRADE_TYPE_CODE", tradeType);
			//ITradeCancel tradeCancel = (ITradeCancel) TradeCancelConfig.getTradeCancelClassMap().get(className);
			//unfinishedTradeList.addAll(tradeCancel.getUserUnfinishedTradeList(param));
		}

		String startDate = param.getString("START_DATE");
		String endDate = param.getString("END_DATE");
		String orderId = param.getString("ORDER_ID");
		TradeCancelQry tradeCancelQry = new TradeCancelQry();
		IDataset dataset = tradeCancelQry.queryTradeBySerialNumberAndTradeType(serialNum, "261", startDate, endDate, orderId);

		unfinishedTradeList.addAll(dataset);

		if (IDataUtil.isEmpty(unfinishedTradeList)) {
			CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户没有可以撤销的未完工工单信息！");
		}



		// TODO 确认是否只有前台页面需要验证是否同地州同员工办理
		// 过滤非同员工非同地州的工单
//		if ("0".equals(CSBizBean.getVisit().getInModeCode())) {
//			String filter = "TRADE_STAFF_ID=" + CSBizBean.getVisit().getStaffId() + ",TRADE_EPARCHY_CODE="
//					+ CSBizBean.getVisit().getLoginEparchyCode();
//			unfinishedTradeList = DataHelper.filter(unfinishedTradeList, filter);
//			if (IDataUtil.isEmpty(unfinishedTradeList)) {
//				CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户没有当前操作员可以撤销的未完工工单信息！");
//			}
//		}

		DataHelper.distinct(unfinishedTradeList,"TRADE_ID",null);

 		DataHelper.sort(unfinishedTradeList, "ACCEPT_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

		return unfinishedTradeList;
	}

	public IDataset getECUserUnfinishedTradeList(IData param) throws Exception {
		IDataset unfinishedTradeList = new DatasetList();
		IDataset config = CommparaInfoQry.getCommByParaAttr("CSM", "1640", CSBizBean.getTradeEparchyCode());
		if (IDataUtil.isEmpty(config)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1210, "1640");
		}
		for (int i = 0, s = config.size(); i < s; i++) {
			IData data = config.getData(i);
			String tradeType = data.getString("PARAM_CODE");
			String className = data.getString("PARA_CODE25").trim();
			param.put("TRADE_TYPE_CODE", tradeType);
			//ITradeCancel tradeCancel = (ITradeCancel) ECTradeCancelConfig.getTradeCancelClassMap().get(className);
			//unfinishedTradeList.addAll(tradeCancel.getUserUnfinishedTradeList(param));
		}

		if (IDataUtil.isEmpty(unfinishedTradeList)) {
			CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户没有可以撤销的未完工工单信息！");
		}
		DataHelper.sort(unfinishedTradeList, "ACCEPT_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

		return unfinishedTradeList;
	}
	public void checkBeforeQuery(String serialNum) throws Exception {

		if(StringUtils.isNotEmpty(serialNum) && serialNum.startsWith("0")){
			return;
		}

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNum);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_126, serialNum);
		}

		String userId = userInfo.getString("USER_ID");
		IDataset userSvcStates = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
		if (userSvcStates.size() == 1) {
			if (!"0".equals(userSvcStates.first().getString("STATE_CODE"))) {
				CSAppException.apperr(CrmUserException.CRM_USER_190,
						userSvcStates.first().getString("STATE_NAME", "非正常"));
			}
		} else {
			//CSAppException.apperr(UserSvcStateException.CRM_USER_SVCSTATE_1);
		}
	}

	
	
	public IDataset getCancelTradeDetails(IData param) throws Exception {
		String orderId = IDataUtil.chkParam(param, "ORDER_ID");
		IData mainOrder = TradeCancelQry.getMainOrderData(orderId);
		String orderTypeCode = mainOrder.getString("ORDER_TYPE_CODE");

		IDataset cancelTradeList = TradeCancelQry.queryTradeInfosByOrderId(orderId, "0");

		if (logger.isDebugEnabled()) {
			logger.debug("撤单所属的订单信息：" + cancelTradeList.toString());
		}

		IData mainTrade = null;
		for (int i = 0, s = cancelTradeList.size(); i < s; i++) {
			IData tempData = cancelTradeList.getData(i);
			if (orderTypeCode.equals(tempData.getString("TRADE_TYPE_CODE"))) {
				mainTrade = tempData;
				break;
			}
		}
		
		TradeDependRelaData tradeDependRelaData = TradeDependRelaData.queryLimitTrades(mainTrade, 1, cancelTradeList);
		IDataset tradeDataset = new DatasetList();
		tradeDependRelaData.parse(tradeDataset);

		if (logger.isDebugEnabled()) {
			logger.debug("注入依赖关系的订单信息：" + tradeDataset.toString());
		}

		return tradeDataset;
	}

	
	public IData getPageData(IData param) throws Exception {
		String orderId = IDataUtil.chkParam(param, "ORDER_ID");
		IData mainOrder = TradeCancelQry.getMainOrderData(orderId);
		String orderTypeCode = mainOrder.getString("ORDER_TYPE_CODE");
		IData mainTrade = TradeCancelQry.getMainTradeData(orderId, orderTypeCode);
		String productId = mainTrade.getString("PRODUCT_ID");
		
		IData offerChasData = new DataMap();
		IDataset offerComChas = UpcCall.queryOfferComChaByCond("P", productId, null);
        if (IDataUtil.isNotEmpty(offerComChas)) {
        	for (int i = 0, s = offerComChas.size(); i < s; i++) {
    			IData offerComCha = offerComChas.getData(i);
    			offerChasData.put(offerComCha.getString("FIELD_NAME"), offerComCha.getString("FIELD_VALUE"));
    		}
        }
        
		return offerChasData;
	}

	
	public IDataset getTradeFeeList(String tradeId, String tradeTypeCode) throws Exception {
		IDataset tradeFeeList = new DatasetList();
		IDataset tradeFees =null;// TradeMgrQry.queryOrderFee(tradeId);
		if (IDataUtil.isNotEmpty(tradeFees)) {
			for (int j = 0, n = tradeFees.size(); j < n; j++) {
				IData feeData = new DataMap();
				feeData.put("FEE", tradeFees.getData(j).getInt("FEE"));
				feeData.put("FEE_MODE", tradeFees.getData(j).getInt("FEE_MODE"));
				feeData.put("FEE_TYPE_CODE", tradeFees.getData(j).getInt("FEE_TYPE_CODE"));
				feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
				feeData.put("PAY_TRADE_ID", tradeId);
				tradeFeeList.add(feeData);
			}
		}
		
		return tradeFeeList;
	}
	
	
	public void cancelTradeSubmit(TradeCancelReqData reqData) throws Exception {
		//BaseTradeCancelHandler baseHandler = null;
		for (CancelTradeData cancelTrade : reqData.getCancelTradeList()) {
			IData originalTradeData = null;
			
			if (cancelTrade.getFinishFlag() == FamilyConstants.TRADE_UNFINISHED) {
				reqData.setNewOrderId(reqData.getCancelOrderId());
				originalTradeData = UTradeInfoQry.qryTradeByTradeId(cancelTrade.getTradeId(), BofConst.CANCEL_TAG_NO, BizRoute.getRouteId());

				if (IDataUtil.isEmpty(originalTradeData)) {
					CSAppException.apperr(TradeException.CRM_TRADE_67, cancelTrade.getTradeId());
				}
				originalTradeData.put("TABLE_NAME", "TF_B_TRADE");
				//baseHandler = new TradeRevokeHandler();
				
			} else if (cancelTrade.getFinishFlag() == FamilyConstants.TRADE_FINISHED) {
				if (StringUtils.isEmpty(reqData.getUndoOrderId())) {
					reqData.setUndoOrderId(SeqMgr.getNewOrderIdFromDb(SysDateMgr.getSysTime()));
				}
				reqData.setNewOrderId(reqData.getUndoOrderId());

				originalTradeData = UTradeHisInfoQry.qryTradeHisByPk(cancelTrade.getTradeId(), BofConst.CANCEL_TAG_NO, BizRoute.getRouteId());

				if (IDataUtil.isEmpty(originalTradeData)) {
					CSAppException.apperr(TradeException.CRM_TRADE_75);
				}
				originalTradeData.put("TABLE_NAME", "TF_BH_TRADE");
				//baseHandler = new TradeBackSellHandler();
			}

			TradeCancelParamData paramData = TradeCancelReqDataBuilder.buildParamData(reqData, originalTradeData);
			//baseHandler.cancelTradeProcess(paramData);
		}
		
		cancelOrderReg(reqData);
	}

	public void cancelOrderReg(TradeCancelReqData reqData) throws Exception {
		String orderId = reqData.getOldOrderId();
		String cancelOrderId = reqData.getCancelOrderId();
		String undoOrderId = reqData.getUndoOrderId();
		String acceptTime = reqData.getAcceptTime();
		boolean hisFlag = false;//未完工
		
		IData orderData = UOrderInfoQry.qryOrderByOrderId(orderId, BizRoute.getRouteId());
        if (IDataUtil.isEmpty(orderData)) {
            orderData = UOrderHisInfoQry.qryOrderHisByOrderId(orderId);
            hisFlag = true;//完工
        }
        
		//当主工单被撤销时，更新ORDER
		if (reqData.isCancelAll()) {
			if (hisFlag) {
				updateOrderHisData(orderId, acceptTime);
			} else {
				moveOrderToHis(orderData, acceptTime);
			}
		}

		createCancelOrderData(orderData, cancelOrderId, acceptTime, BofConst.CANCEL_TAG_CANCEL);
		if (StringUtils.isNotEmpty(undoOrderId)) {
			createCancelOrderData(orderData, undoOrderId, acceptTime, BofConst.CANCEL_TAG_UNDO);
		}
	}

	
	private void updateOrderHisData(String orderId, String acceptTime) throws Exception {
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_DATE", acceptTime);
		param.put("CANCEL_TAG", BofConst.CANCEL_TAG_CANCELED);
		param.put("CANCEL_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("CANCEL_DEPART_ID", CSBizBean.getVisit().getDepartId());
		param.put("CANCEL_CITY_CODE", CSBizBean.getVisit().getCityCode());
		param.put("CANCEL_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		Dao.executeUpdateByCodeCode("TF_BH_ORDER", "UPD_HISORDER_CANCEL_TAG", param, Route.getJourDbDefault());
	}

	
	private void createCancelOrderData(IData orderInfo, String newOrderId, String acceptTime, String cancelTag) throws Exception {
		IData orderData = new DataMap(orderInfo);
		orderData.put("ORDER_ID", newOrderId);
		orderData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
		orderData.put("ACCEPT_DATE", acceptTime);
		orderData.put("ORDER_STATE", "0");
		orderData.put("ORDER_KIND_CODE", "0");
		orderData.put("NEXT_DEAL_TAG", "0");
		orderData.put("CANCEL_TAG", cancelTag);
		orderData.put("SUBSCRIBE_TYPE", "0");
		orderData.put("APP_TYPE", "300");
		orderData.put("EXEC_TIME", acceptTime);
		orderData.put("ACCEPT_DATE", acceptTime);
		orderData.put("FINISH_DATE", "");
		orderData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

		orderData.put("CANCEL_DATE", orderInfo.getString("ACCEPT_DATE"));
		orderData.put("CANCEL_STAFF_ID", orderInfo.getString("TRADE_STAFF_ID"));
		orderData.put("CANCEL_DEPART_ID", orderInfo.getString("TRADE_DEPART_ID"));
		orderData.put("CANCEL_CITY_CODE", orderInfo.getString("TRADE_CITY_CODE"));
		orderData.put("CANCEL_EPARCHY_CODE", orderInfo.getString("TRADE_EPARCHY_CODE"));

		orderData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		orderData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		orderData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		orderData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

		orderData.put("UPDATE_TIME", acceptTime);
		orderData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		orderData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		orderData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

		if (!Dao.insert("TF_B_ORDER", orderData, Route.getJourDbDefault())) {
			String msg = "订单数据ORDER_ID【" + newOrderId + "】插入表TF_B_ORDER操作失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
	}
	
	private void moveOrderToHis(IData orderInfo, String acceptTime) throws Exception {
        IData orderData = new DataMap();
        orderData.putAll(orderInfo);
        orderData.put("FINISH_DATE", acceptTime);
        orderData.put("CANCEL_DATE", acceptTime);
        orderData.put("CANCEL_TAG", BofConst.CANCEL_TAG_CANCELED);
        orderData.put("CANCEL_STAFF_ID", CSBizBean.getVisit().getStaffId());
        orderData.put("CANCEL_DEPART_ID", CSBizBean.getVisit().getDepartId());
        orderData.put("CANCEL_CITY_CODE", CSBizBean.getVisit().getCityCode());
        orderData.put("CANCEL_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        if (!Dao.insert("TF_BH_ORDER", orderData, Route.getJourDb(BizRoute.getRouteId()))) {
            String msg = "搬迁订单【" + orderInfo.getString("ORDER_ID") + "】至订单历史表失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
        
        if (!Dao.delete("TF_B_ORDER", orderInfo, Route.getJourDb(BizRoute.getRouteId()))) {
			String msg = "删除订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
    }

}

