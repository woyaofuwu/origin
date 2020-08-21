package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;


public class SysEspMebSvcInfoAction implements ITradeAction {
	private static final Logger logger = Logger.getLogger(SysEspMebSvcInfoAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
			String userId=btd.getRD().getUca().getUserId();
			String serialNumber=btd.getRD().getUca().getSerialNumber();
			IDataset usersvcs = UserSvcInfoQry.getSvcUserId(userId,"468011");
			if(IDataUtil.isEmpty(usersvcs)){
		        return;
		     }else{
		        for(int i=0;i<usersvcs.size();i++){
		        	IData svcInfo=usersvcs.getData(i);
		        	SvcTradeData TradeData = new SvcTradeData(svcInfo);
		        	TradeData.setModifyTag("2");
		            btd.add(serialNumber, TradeData);        	  
		        	}
		        }
	}

}
