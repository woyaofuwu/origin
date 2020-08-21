package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.builder;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.TradeCancelQry;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeCancelParamData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeCancelReqData;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class TradeCancelReqDataBuilder {
	
	/**
	 * @desc 构建请求对象
	 * @param param
	 * @return
	 * @throws Exception
	 * @author danglt
	 */
	public static TradeCancelReqData buildReqData(IData param) throws Exception {
		
		String[] strColNames = {"CANCEL_REASON", "CANCEL_ORDER_TYPE", "OLD_ORDER_ID"};
		//TradeCancelUtils.chkParams(param, strColNames);
		
		String cancelOrderType = param.getString("CANCEL_ORDER_TYPE");
		String oldOrderId = param.getString("OLD_ORDER_ID");

		IDataset cancelTradeList = param.getDataset("CANCEL_TRADE_LIST", new DatasetList());
		if (IDataUtil.isEmpty(cancelTradeList)) {
			CSAppException.apperr(TradeException.CRM_TRADE_95, "您提交的撤销订单数据不能为空！");
		}

		TradeCancelReqData reqData = new TradeCancelReqData();
		reqData.setCancelReason(param.getString("CANCEL_REASON"));
		reqData.setCancelOrderType(cancelOrderType);
		reqData.setOldOrderId(oldOrderId);
		reqData.setRemarks(param.getString("REMARKS", ""));
		reqData.setCancelOrderId(SeqMgr.getNewOrderIdFromDb(SysDateMgr.getSysTime()));
		reqData.setAcceptTime(SysDateMgr.getSysTime());
		reqData.setCancelAll(checkCancelAllOrder(cancelTradeList, cancelOrderType));
		reqData.setCancelTradeList(cancelTradeList);
		reqData.setMultiOrder(false);
		
		IData orderData = TradeCancelQry.getMainOrderData(oldOrderId);
        String orderKindCode = orderData.getString("ORDER_KIND_CODE", "");
        if (BofConst.ORDER_KIND_CODE_MUTIL_TRADE.equals(orderKindCode)) {
        	reqData.setMultiOrder(true);
        }
        
		return reqData;
	}

	private static boolean checkCancelAllOrder(IDataset cancelTradeList, String cancelTradeType) throws Exception {
		// 通过filter方法判断是否存在主工单
		IDataset mainTradeData = DataHelper.filter(cancelTradeList, "TRADE_TYPE_CODE=" + cancelTradeType);
		if (IDataUtil.isEmpty(mainTradeData)) {
			return false;
		}
		return true;
	}

	public static TradeCancelParamData buildParamData(TradeCancelReqData reqData, IData tradeData)
			throws Exception {
		return new TradeCancelParamData(reqData, tradeData);
	}

}

