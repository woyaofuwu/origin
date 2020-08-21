package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class TradeCancelQry {

	/**
	 * @desc 根据条件查询ORDER信息
	 * @param orderId
	 * @return
	 * @throws Exception
	 * @author danglt
	 */
	public static IData getMainOrderData(String orderId) throws Exception {
		String acceptMonth = StrUtil.getAcceptMonthById(orderId);
		IData mainOrder = UOrderInfoQry.qryOrderByPk(orderId, acceptMonth, BofConst.CANCEL_TAG_NO);
		if (IDataUtil.isEmpty(mainOrder)) {
			mainOrder = UOrderInfoQry.qryOrderByPk(orderId, acceptMonth, "C");
		}
		
		if (IDataUtil.isEmpty(mainOrder)) {
			CSAppException.apperr(TradeException.CRM_TRADE_76, orderId);
		}
		
		return mainOrder;
	}
	
	/**
	 * @desc 根据条件查询主TRADE信息
	 * @param orderId
	 * @param orderTypeCode
	 * @return
	 * @throws Exception
	 * @author danglt
	 */
	public static IData getMainTradeData(String orderId, String orderTypeCode) throws Exception {
		IDataset mainTrades =null; // TradeInfoQry.queryTradeByOrerIdAndTradeType(orderId, orderTypeCode, BofConst.CANCEL_TAG_NO);
		if (IDataUtil.isEmpty(mainTrades)) {
			mainTrades =null; // TradeInfoQry.queryBHTradeByOrerIdAndTradeType(orderId, orderTypeCode, BofConst.CANCEL_TAG_NO);
		}
		
		if (IDataUtil.isEmpty(mainTrades)) {
			CSAppException.apperr(TradeException.CRM_TRADE_65o, orderId);
		}
		
		return mainTrades.first();
	}
	
	
	/**
	 * @desc 查询所有工单信息
	 * @param orderId
	 * @param cancelTag
	 * @return
	 * @throws Exception
	 * @author danglt
	 */
	public static IDataset queryTradeInfosByOrderId(String orderId, String cancelTag) throws Exception {
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));
		param.put("CANCEL_TAG", cancelTag);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT T.*, DECODE(NVL(R.ROLE_CODE_B, '0'), '0', DECODE(T.TRADE_TYPE_CODE, '64', '9', '65', '9', '308', '9', '0'), R.ROLE_CODE_B) ROLE_CODE ");
		parser.addSQL("FROM (SELECT C.*, '0' FINISH_FLAG ");
		parser.addSQL("FROM TF_B_TRADE C ");
		parser.addSQL("WHERE C.ORDER_ID = :ORDER_ID ");
		parser.addSQL("AND C.ACCEPT_MONTH = :ACCEPT_MONTH ");
		parser.addSQL("AND C.CANCEL_TAG = :CANCEL_TAG ");
		parser.addSQL("UNION ALL ");
		parser.addSQL("SELECT C.*, '1' FINISH_FLAG ");
		parser.addSQL("FROM TF_BH_TRADE C ");
		parser.addSQL("WHERE C.ORDER_ID = :ORDER_ID ");
		parser.addSQL("AND C.ACCEPT_MONTH = :ACCEPT_MONTH ");
		parser.addSQL("AND C.CANCEL_TAG = :CANCEL_TAG) T, ");
		parser.addSQL("TF_B_TRADE_RELATION R ");
		parser.addSQL("WHERE R.TRADE_ID(+) = T.TRADE_ID ");
		parser.addSQL("AND R.MODIFY_TAG(+) = '0' ");
		parser.addSQL("AND R.RSRV_STR1(+) = 'NEW_RH' ");//除了融合业务不需要ROLE_CODE信息

		return Dao.qryByParse(parser, Route.getJourDbDefault());
	}
	
	
	/**
	 * @desc 根据RSRV_STR10查询未完工工单
	 * @param serialNum 手机号码
	 * @param tradeType 业务类型
	 * @return
	 * @throws Exception
	 * @author danglt
	 */
	public static IDataset queryTradeByRsrvAndTradeType(String serialNum, String tradeType, String startDate, String endDate, String orderId) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNum);
		param.put("TRADE_TYPE_CODE", tradeType);
		param.put("CANCEL_TAG", "0");
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("ORDER_ID", orderId);
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_TRADE_BY_RSRV_TRADETYPE", param, Route.getJourDbDefault());
	}

	public static IDataset queryTradeBySerialNumberAndTradeType(String serialNum, String tradeType, String startDate, String endDate, String orderId) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNum);
		param.put("TRADE_TYPE_CODE", tradeType);
		param.put("CANCEL_TAG", "0");
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("ORDER_ID", orderId);
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_TRADE_BY_SERIAL_NUMBER", param, Route.getJourDbDefault());
	}
	
	
	public static IDataset queryTradeBySnAndTradeType(String serialNum, String tradeType, String startDate, String endDate, String orderId) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNum);
		param.put("TRADE_TYPE_CODE", tradeType);
		param.put("CANCEL_TAG", "0");
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("ORDER_ID", orderId);
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_TRADE_BY_SN_TRADETYPE", param, Route.getJourDbDefault());
	}



	
	/**
	 * @desc 获取新融合主卡的融合关系数据
	 * @param userId
	 * @param isCatch
	 * @return
	 * @throws Exception
	 * @author danglt
	 */
	public static IData getFusionVirtualInfo(String userId, boolean isCatch) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("ROLE_CODE_B", "1");
		IDataset results = Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_FUSION_RELATION_BY_USER_ID_B", param, BizRoute.getRouteId());
		if (IDataUtil.isEmpty(results) && isCatch) {
			String msg = "根据USER_ID=【" + userId + "】查询主卡用户融合关系无数据！";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
		return IDataUtil.isEmpty(results) ? new DataMap() : results.first();
	}
}
