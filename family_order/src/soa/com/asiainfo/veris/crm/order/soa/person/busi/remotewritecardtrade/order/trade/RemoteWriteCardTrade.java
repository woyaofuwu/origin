
package com.asiainfo.veris.crm.order.soa.person.busi.remotewritecardtrade.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.remotewritecardtrade.order.requestdata.RemoteWriteCardRequestData;

public class RemoteWriteCardTrade extends BaseTrade implements ITrade
{
    /**
     * 创建开户省际跨区写卡发起业务的具体业务台账
     * 
     * @author zhongsr
     * @param btd
     * @throws Exception
     */
	public void createBusiTradeData(BusiTradeData btd) throws Exception
	{
		this.createUserTradeData(btd);
		this.createCustomerTradeData(btd);
		this.createCustPersonTradeData(btd);
		this.createAcctTradeData(btd);
		this.createResTradeData(btd);//构建数据到tf_b_trade_res表
		updateMainTradeData(btd);

	}
	
	private void createUserTradeData(BusiTradeData btd) throws Exception
	{
		RemoteWriteCardRequestData remoteWriteCardReqData = (RemoteWriteCardRequestData)btd.getRD();
		UserTradeData userTD = remoteWriteCardReqData.getUca().getUser().clone();
		btd.add(remoteWriteCardReqData.getUca().getSerialNumber(), userTD);		
	}

	private void createCustomerTradeData(BusiTradeData btd) throws Exception
	{
		RemoteWriteCardRequestData remoteWriteCardReqData = (RemoteWriteCardRequestData)btd.getRD();
		CustomerTradeData customerTD = remoteWriteCardReqData.getUca().getCustomer().clone();
		btd.add(remoteWriteCardReqData.getUca().getSerialNumber(), customerTD);

	}

	private void createCustPersonTradeData(BusiTradeData btd) throws Exception
	{
		RemoteWriteCardRequestData remoteWriteCardReqData = (RemoteWriteCardRequestData)btd.getRD();
		CustPersonTradeData custpersonTD = remoteWriteCardReqData.getUca().getCustPerson().clone();
        btd.add(remoteWriteCardReqData.getUca().getSerialNumber(), custpersonTD);		
	}
	
	private void createAcctTradeData(BusiTradeData btd) throws Exception
	{
		RemoteWriteCardRequestData remoteWriteCardReqData = (RemoteWriteCardRequestData)btd.getRD();
		AccountTradeData acctTD = remoteWriteCardReqData.getUca().getAccount().clone();
		btd.add(remoteWriteCardReqData.getUca().getSerialNumber(), acctTD);

	}
	
	private void createResTradeData(BusiTradeData btd) throws Exception
    {
		RemoteWriteCardRequestData remoteWriteCardReqData = (RemoteWriteCardRequestData)btd.getRD();
        String instId = SeqMgr.getInstId();
        
        ResTradeData resTradeDataNew = new ResTradeData();
        resTradeDataNew.setUserId(remoteWriteCardReqData.getUca().getUserId());
        resTradeDataNew.setUserIdA(remoteWriteCardReqData.getUca().getUserId());
        resTradeDataNew.setResTypeCode("1");
        resTradeDataNew.setResCode(remoteWriteCardReqData.getSimCardNo());
        resTradeDataNew.setInstId(instId);
        resTradeDataNew.setStartDate(SysDateMgr.getSysTime());
        resTradeDataNew.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeDataNew.setRsrvStr5(remoteWriteCardReqData.getEmptyCardId());

        BaseReqData brd = btd.getRD();
        btd.add(brd.getUca().getSerialNumber(), resTradeDataNew);
    }
	private void updateMainTradeData(BusiTradeData btd) throws Exception
    {

        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr8("跨区补卡USIM卡");
    }
}
