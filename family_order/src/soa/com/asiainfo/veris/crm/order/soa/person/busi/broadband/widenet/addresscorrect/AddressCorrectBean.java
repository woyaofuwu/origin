package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.addresscorrect;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class AddressCorrectBean extends CSBizBean{

	public void onTradeSubmit(IData input) throws Exception {
		IData param = new DataMap();
		param.put("DEVICE_ID",input.getString("DEVICE_ID"));  //设备ID   
	    param.put("BACK_USER",input.getString("BACK_USER")); //反馈人   
		param.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));//反馈人电话 
		//param.put("dateTime",input.getString("dateTime"));      
		param.put("ORIGIN_ADDRESS",input.getString("origAddress")); //原地址  
		param.put("NEW_ADDRESS",input.getString("newAddress"));    //新地址
		param.put("REMARK",input.getString("remark"));//备注
		
		param.put("STAFF_NAME",input.getString("STAFF_NAME"));//新增人
		param.put("STAFF_ID", input.getString("STAFF_ID")); //新增人ID
		param.put("DEPT_NAME",input.getString("DEPT_NAME"));//新增人部门
		param.put("DEPT_ID", input.getString("DEPT_ID"));//新增人部门ID
		param.put("STAFF_SERIAL_NUMBER", input.getString("STAFF_SERIAL_NUMBER"));//新增人电话号码
		param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));//业务区域
		param.put("CITY_CODE", input.getString("CITY_CODE"));//城市编码
		
		//调用固网资源接口
		//IDataset dataOutput = CSAppCall.call("PB.WideNetAddressOprSVC.standAddressModify", param, false);
		IDataset dataOutput = CSAppCall.call("PB.AddrCorrect.addAddrCorrect", param, false);
		IData result = dataOutput.getData(0);
        
    	String resultCode = result.getString("code_Result", "-1");
        if (!"0".equals(resultCode))
        {
            String resultInfo = result.getString("resultMessage");
            CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
        }
	}
}
