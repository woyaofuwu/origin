package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.requestdata.NoPhoneWidenetMoveRequestData;

public class CheckMoveResAction implements ITradeAction
{
	private static transient Logger logger = Logger.getLogger(CheckMoveResAction.class);
    /**
     * 提交业务时，再调下PBOSS资源提供的接口，判断此时是否还有空闲的端口号。
     */
    @SuppressWarnings("unchecked")
	public void executeAction(BusiTradeData btd) throws Exception
    {
    	String serialNumber = btd.getMainTradeData().getSerialNumber();
    	String tradeId = btd.getTradeId();
    	String deviceId = ((NoPhoneWidenetMoveRequestData)btd.getRD()).getDeviceId();
    	IData param = new DataMap();
    	param.put("DEVICE_ID", deviceId);
    	param.put("TRADE_ID", tradeId);
    	param.put("SERIAL_NUMBER", serialNumber);
    	IDataset result = CSAppCall.call("PB.AddressManageSvc.queryAvailableDevice", param, false);
    	
    	if(result==null | result.size()==0){
	    	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该地址下无空闲端口！");
	    }
    }
}
