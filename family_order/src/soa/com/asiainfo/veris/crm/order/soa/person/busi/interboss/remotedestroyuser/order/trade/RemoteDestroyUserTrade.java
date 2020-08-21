package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser.order.requestdata.RemoteDestroyUserRequestData;

public class RemoteDestroyUserTrade extends BaseTrade implements ITrade
{
    /**
     * 跨区销户业务的具体业务台账
     * 
     * @author
     * @param btd
     * @throws Exception
     */
	public void createBusiTradeData(BusiTradeData btd) throws Exception
	{
		this.extendBusiMainTradeData(btd);
		this.createUserTradeData(btd);
		this.createCustomerTradeData(btd);
		this.createAcctTradeData(btd);
		this.createOtherTradeData(btd);
	}

	private void extendBusiMainTradeData(BusiTradeData btd) throws Exception {
		RemoteDestroyUserRequestData remoteDesUserReqData = (RemoteDestroyUserRequestData)btd.getRD();
		btd.getMainTradeData().setRsrvStr1(remoteDesUserReqData.getHomeProvName());
		btd.getMainTradeData().setRsrvStr2(remoteDesUserReqData.getContactName());
		btd.getMainTradeData().setRsrvStr3(remoteDesUserReqData.getContactPhone());
		btd.getMainTradeData().setRsrvStr4(remoteDesUserReqData.getGiftSerialNumber());
		btd.getMainTradeData().setRsrvStr5(remoteDesUserReqData.getGiftCustName());
		btd.getMainTradeData().setRsrvStr6(remoteDesUserReqData.getRemoteOrderId());
		btd.getMainTradeData().setRsrvStr7(remoteDesUserReqData.getGiftSerialNumberB());
		btd.getMainTradeData().setRsrvStr8(remoteDesUserReqData.getGiftCustNameB());
	}

	private void createUserTradeData(BusiTradeData btd) throws Exception
	{
		RemoteDestroyUserRequestData remoteDesUserReqData = (RemoteDestroyUserRequestData)btd.getRD();
		UserTradeData userTD = remoteDesUserReqData.getUca().getUser().clone();
		btd.add(remoteDesUserReqData.getUca().getSerialNumber(), userTD);		
	}

	private void createCustomerTradeData(BusiTradeData btd) throws Exception
	{
		RemoteDestroyUserRequestData remoteDesUserReqData = (RemoteDestroyUserRequestData)btd.getRD();
		CustomerTradeData customerTD = remoteDesUserReqData.getUca().getCustomer().clone();
		btd.add(remoteDesUserReqData.getUca().getSerialNumber(), customerTD);

	}

	private void createAcctTradeData(BusiTradeData btd) throws Exception
	{
		RemoteDestroyUserRequestData remoteWriteCardReqData = (RemoteDestroyUserRequestData)btd.getRD();
		AccountTradeData acctTD = remoteWriteCardReqData.getUca().getAccount().clone();
		btd.add(remoteWriteCardReqData.getUca().getSerialNumber(), acctTD);

	}
	
	private void createOtherTradeData(BusiTradeData btd) throws Exception{
		OrderDataBus dataBus = DataBusManager.getDataBus();
		RemoteDestroyUserRequestData remoteDesUserReqData = (RemoteDestroyUserRequestData)btd.getRD();
		String tradeId = remoteDesUserReqData.getTradeId();
		IData data = new DataMap();
		data.put("TRADE_ID", tradeId);
		data.put("CONTACT_PHONE", remoteDesUserReqData.getContactPhone());
		data.put("CONTACT_NAME", remoteDesUserReqData.getContactName());
		data.put("GIFT_SERIAL_NUMBER", remoteDesUserReqData.getGiftSerialNumber());
		data.put("GIFT_CUST_NAME", remoteDesUserReqData.getGiftCustName());
		data.put("GIFT_SERIAL_NUMBER_B", remoteDesUserReqData.getGiftSerialNumberB());
		data.put("GIFT_CUST_NAME_B", remoteDesUserReqData.getGiftCustNameB());
		data.put("CREATE_PHONE", remoteDesUserReqData.getCreatePhone());
		data.put("CREATE_CONTACT", remoteDesUserReqData.getCreateContact());
		data.put("CREATE_ORG_NAME", remoteDesUserReqData.getCreateOrgName());
		data.put("RELATION_ORDER_ID", "");//派单关联流水
		
		data.put("REMOTE_USER_ID", remoteDesUserReqData.getUca().getUserId());
		data.put("REMOTE_SERIAL_NUMBER", remoteDesUserReqData.getUca().getSerialNumber());
		data.put("REMOTE_CUST_NAME", remoteDesUserReqData.getUca().getCustomer().getCustName());
		
		data.put("CREATE_DATE", dataBus.getAcceptTime());
		data.put("CREATE_STAFF_ID",CSBizBean.getVisit().getStaffId() );
		data.put("CREATE_DEPART_ID",CSBizBean.getVisit().getDepartId() );
		data.put("CREATE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		data.put("CREATE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		data.put("UPDATE_TIME", dataBus.getAcceptTime());
		data.put("REMARKS", remoteDesUserReqData.getRemark());
	}

}



