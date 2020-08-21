package com.asiainfo.veris.crm.order.soa.person.busi.replacecard.order.action;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class EmpryCardOutfinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		//白卡出库
		String emptyCardId=mainTrade.getString("RSRV_STR5");
		String simCardNo=mainTrade.getString("RSRV_STR3");
		String sn=mainTrade.getString("SERIAL_NUMBER");
		ResCall.occupyEmptyCard(emptyCardId, simCardNo, sn, "1");
	}

}
