package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDeductReqData;

public class DeductIBossOutFilter implements IFilterOut
{
	 @Override
	    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
	    {
		    ScoreDeductReqData reqData = (ScoreDeductReqData) btd.getRD();
		 	IData succData = new DataMap();
		 	succData.put("ORDER_ID", reqData.getXOrderId());
		 	succData.put("X_RESULTCODE","00" );
		 	return succData;
	    }
}
