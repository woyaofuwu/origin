
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.requestdata.WidenetMoveRequestData;

public class CheckMoveResAction implements ITradeAction
{
	private static transient Logger logger = Logger.getLogger(CheckMoveResAction.class);
    /**
     * 开户界面，点击提交业务时，调PBOSS资源提供的接口，判断是否还有空闲的端口号。
     * 
     * @author 
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String wideType = ((WidenetMoveRequestData)btd.getRD()).getWideType();
    	String rsrvStr2 = ((WidenetMoveRequestData)btd.getRD()).getRsrvStr2();
    	String serialNumber = btd.getMainTradeData().getSerialNumber();
    	String tradeTypeCode = btd.getTradeTypeCode();
    	
    	String tradeId = btd.getTradeId();
    	String deviceId = ((WidenetMoveRequestData)btd.getRD()).getDeviceId();
    	IData param = new DataMap();
    	param.put("DEVICE_ID", deviceId);
    	param.put("TRADE_ID", tradeId);
    	param.put("SERIAL_NUMBER", serialNumber);
    	//param.put("REGION_ID", REGION_ID);
    	//param.put("PORT_TYPE", PORT_TYPE);
    	//logger.info("===PB.ReginalyyManageSvc.queryAvailableReginal接口调用参数："+param);
    	//IDataset result = CSAppCall.call("PB.ReginalyyManageSvc.queryAvailableReginal", param, false);
    	IDataset result = CSAppCall.call("PB.AddressManageSvc.queryAvailableDevice", param, false);
    	
    	if(result==null | result.size()==0)
	    	CSAppException
			.apperr(CrmCommException.CRM_COMM_103, "该地址下无空闲端口！");
    }
}
