package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;



import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;

/**
 * 一卡多号
 */
@SuppressWarnings("unchecked")
public class OneCardMultiNoAction implements ITradeAction {
	public void executeAction(BusiTradeData btd) throws Exception {
		OneCardMultiNoReqData oneCardMultiNoReqData = (OneCardMultiNoReqData) btd.getRD();
		MainTradeData mainTrade = btd.getMainTradeData();
		mainTrade.setRemark(oneCardMultiNoReqData.getRemark());
	}
}