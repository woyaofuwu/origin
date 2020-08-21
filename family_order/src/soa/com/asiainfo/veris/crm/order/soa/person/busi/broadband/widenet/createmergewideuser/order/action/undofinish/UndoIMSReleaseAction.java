package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * 宽带撤单释放IMS固话号码预占
 * @author xuzh5
 * 2018-9-27 10:02:45
 */
public class UndoIMSReleaseAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {

		String tradeId = mainTrade.getString("TRADE_ID","");
		String mode = "TRADE_CANCEL";
		
		IDataset tradeotherInfo=TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
		for(int i=0;tradeotherInfo.size()>i;i++){
			if("IMSTRADE".equals(tradeotherInfo.getData(i).getString("RSRV_VALUE_CODE"))){
				String sn=tradeotherInfo.getData(i).getString("RSRV_STR3");
				// 释放固话号码预占
				ResCall.releaseAllResByNo(sn, "0", tradeId + "订单取消", mode);
			}
		}
		
	}

}
