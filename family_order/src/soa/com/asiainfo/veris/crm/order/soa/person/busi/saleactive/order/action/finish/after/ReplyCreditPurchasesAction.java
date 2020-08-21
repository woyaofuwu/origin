package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;

/**
 * 套餐办理结果通知
 * 
 */
public class ReplyCreditPurchasesAction  implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String tradeId = mainTrade.getString("TRADE_ID","");
		IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(tradeId, "CREDIT_PURCHASES");
		if(DataUtils.isEmpty(otherDatas)){
			return ;
		}
		
		String serialNum = mainTrade.getString("SERIAL_NUMBER");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		String orderId = mainTrade.getString("ORDER_ID","");
		IData orderInfo=TradeInfoQry.getOrderByOrderId(orderId);
		

		String mplOrdNo = otherDatas.getData(0).getString("RSRV_STR1");
		String mplOrdDt = otherDatas.getData(0).getString("RSRV_STR2");
		String seq = otherDatas.getData(0).getString("RSRV_STR9");

		if(IDataUtil.isNotEmpty(orderInfo)){
			IData param = new DataMap();
			param.put("SEQ", seq);
			param.put("CUS_MBL_NO", serialNum);
			param.put("MPL_ORD_NO", mplOrdNo);
			param.put("MPL_ORD_DT", mplOrdDt);
			param.put("ACP_TM", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			param.put("ACP_TYP","P");//操作类型 P:办理 R:取消
			IDataset goods=TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(tradeId);
			if(IDataUtil.isNotEmpty(goods)){
				param.put("GOODS_CODE",goods.getData(0).getString("RES_CODE"));
			}
			IDataset ids=IBossCall.replyCreditPurchases(param);
			if(IDataUtil.isNotEmpty(ids)){
				
				IDataset responseInfos=ids.getData(0).getDataset("REG_ORD_RSP");
	        	String rspCode = responseInfos.getData(0).getString("RSP_CODE");
	        	String rspInfo = responseInfos.getData(0).getString("RSP_INFO");
	        	if(!StringUtils.equals(rspCode, "0000")){
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103, rspInfo);     
	        	}
				param.put("RSRV_STR5", "P");
				param.put("TRADE_ID", tradeId);
				param.put("RSRV_VALUE_CODE", "CREDIT_PURCHASES");

				Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_RSRV_STR5_BY_TRADE_ID", param, Route.getJourDb(eparchyCode));
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "套餐办理结果通知失败");
			}
		}
	}

}
