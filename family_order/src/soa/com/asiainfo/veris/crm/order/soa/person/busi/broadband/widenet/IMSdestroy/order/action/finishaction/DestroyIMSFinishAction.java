package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.action.finishaction;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;


/**
 * IMS固话拆机，解除IMS固话号码实占
 * @author xuzh5
 *	2018-9-17 9:48:59
 */
public class DestroyIMSFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String serial_number=mainTrade.getString("SERIAL_NUMBER");
		//IMS固话拆机销户接口
		ResCall.destroyRealeaseMphone(serial_number, "2");
	}

}
