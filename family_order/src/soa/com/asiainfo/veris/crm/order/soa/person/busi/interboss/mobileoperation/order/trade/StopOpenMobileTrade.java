package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.requestdata.StopOpenMobileReqData;

public class StopOpenMobileTrade extends BaseTrade implements ITrade {
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception {
        this.createUserTradeData(btd);
        this.createCustomerTradeData(btd);
        this.createAcctTradeData(btd);
    }

    private void createUserTradeData(BusiTradeData btd) throws Exception
    {
        StopOpenMobileReqData remoteDesUserReqData = (StopOpenMobileReqData)btd.getRD();
        UserTradeData userTD = remoteDesUserReqData.getUca().getUser().clone();
        btd.add(remoteDesUserReqData.getUca().getSerialNumber(), userTD);
    }

    private void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        StopOpenMobileReqData remoteDesUserReqData = (StopOpenMobileReqData)btd.getRD();
        CustomerTradeData customerTD = remoteDesUserReqData.getUca().getCustomer().clone();
        btd.add(remoteDesUserReqData.getUca().getSerialNumber(), customerTD);

    }

    private void createAcctTradeData(BusiTradeData btd) throws Exception
    {
        StopOpenMobileReqData remoteWriteCardReqData = (StopOpenMobileReqData)btd.getRD();
        AccountTradeData acctTD = remoteWriteCardReqData.getUca().getAccount().clone();
        btd.add(remoteWriteCardReqData.getUca().getSerialNumber(), acctTD);

    }
}
