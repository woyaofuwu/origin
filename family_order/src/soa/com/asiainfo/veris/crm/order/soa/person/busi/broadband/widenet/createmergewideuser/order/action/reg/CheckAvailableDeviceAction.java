
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class CheckAvailableDeviceAction implements ITradeAction
{
	
    /**
     * 开户界面，点击提交业务时，调PBOSS资源提供的接口，判断是否还有空闲的端口号。
     * 
     * @author 
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String DEVICE_ID = ((MergeWideUserCreateRequestData)btd.getRD()).getDeviceId();
    	
    	String TRADE_ID = btd.getTradeId();
    	String serialNumber = btd.getMainTradeData().getSerialNumber();
    	IData param = new DataMap();
    	param.put("DEVICE_ID", DEVICE_ID);
    	param.put("TRADE_ID", TRADE_ID);
    	param.put("SERIAL_NUMBER", serialNumber);
    	
    	IDataset result = CSAppCall.call("PB.AddressManageSvc.queryAvailableDevice", param, false);
    	
    	if(result==null || result.size()==0)
    	{
    	    CSAppException.apperr(CrmCommException.CRM_COMM_103, "地址设备信息不存在！");
    	}
	    	
    }
}
