package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd.order.requestdata.RemoteResetPswdRequestData;

public class RemoteResetPswdTrade  extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		this.createUserTradeData(btd);
		this.createCustomerTradeData(btd);
		this.createAcctTradeData(btd);
		this.createCustPersonTradeData(btd);
	}
	
	private void createUserTradeData(BusiTradeData btd) throws Exception
	{
		RemoteResetPswdRequestData remoteResetPswdRequestData = (RemoteResetPswdRequestData)btd.getRD();
		UserTradeData userTD = remoteResetPswdRequestData.getUca().getUser().clone();
		btd.add(remoteResetPswdRequestData.getUca().getSerialNumber(), userTD);		
	}

	private void createCustomerTradeData(BusiTradeData btd) throws Exception
	{
		RemoteResetPswdRequestData remoteResetPswdRequestData = (RemoteResetPswdRequestData)btd.getRD();
		CustomerTradeData customerTD = remoteResetPswdRequestData.getUca().getCustomer().clone();
		//--------add by lixy13_r_20170920-----------------------------------------------
		int i=0;
		int j=0;
		try{
			i = Integer.parseInt(customerTD.getCreditClass()); 
		}
		catch(Exception e)
		{
			i=-1;
		}
		try{
			j = Integer.parseInt(customerTD.getCreditValue()); 
		}
		catch(Exception e)
		{
			j=0;
		}
		customerTD.setCreditClass(Integer.toString(i));
		customerTD.setCreditValue(Integer.toString(j));
		//-------------------------------------------------------
		btd.add(remoteResetPswdRequestData.getUca().getSerialNumber(), customerTD);

	}

	private void createAcctTradeData(BusiTradeData btd) throws Exception
	{
		RemoteResetPswdRequestData remoteResetPswdRequestData = (RemoteResetPswdRequestData)btd.getRD();
		AccountTradeData acctTD = remoteResetPswdRequestData.getUca().getAccount().clone();
		btd.add(remoteResetPswdRequestData.getUca().getSerialNumber(), acctTD);

	}
	
	private void createCustPersonTradeData(BusiTradeData btd) throws Exception
	{
		RemoteResetPswdRequestData remoteResetPswdRequestData = (RemoteResetPswdRequestData)btd.getRD();
		CustPersonTradeData custpersonTD = remoteResetPswdRequestData.getUca().getCustPerson().clone();
        btd.add(remoteResetPswdRequestData.getUca().getSerialNumber(), custpersonTD);		
	}
}
