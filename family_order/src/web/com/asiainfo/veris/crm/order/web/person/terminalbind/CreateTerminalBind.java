package com.asiainfo.veris.crm.order.web.person.terminalbind;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CreateTerminalBind extends PersonBasePage
{ 
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    { 
        IData data = getData();
        String serailNumber = data.getString("cond_SERIAL_NUMBER", "");
        String terminalNumber = data.getString("cond_TERMINAL_NUMBER", "");
         
        IData input = new DataMap();
        input.put("RES_CODE", terminalNumber);
        input.put("SERIAL_NUMBER", serailNumber);
 
         
 
    	// BIND_FLAG 0为绑定 1为解绑
    	 
     	IDataset res =  CSViewCall.call(this, "SS.TerminalBindSVC.queryTerminalBindForCreate", input);
		String bind_flag = "" ; 
		 
		if(IDataUtil.isNotEmpty(res) && res.size()>0 ){ 
			
			 IData idata =  (IData)res.get(0); 
			 bind_flag =  idata.getString("BIND_FLAG"); 
			 if("0".equals(bind_flag)){
				 CSViewException.apperr(CrmCommException.CRM_COMM_103,"用户数据已经绑定成功，请勿重复绑定!");
				 return;
			 }else{
				 String loginStaffId = getVisit().getStaffId(); // 系统登录工号
				 String oprStaffId = idata.getString("OPER_STAFF_ID");  // 系统登录工号
				 if(!loginStaffId.equals(oprStaffId)){
					 CSViewException.apperr(CrmCommException.CRM_COMM_103,"重新绑定解绑数据，应该使用初始绑定工号!");
					 return; 
				 }
			 }
				 
			
		}
		
		// BIND_FLAG 0为绑定 1为解绑
		// 插入数据为设为0的情况为，数据为空或者最新的数据，flag 为1?
		
    	if ( res.size() == 0 ){
    		verifyTerminalBind(input,"0");
    		IData idata = new DataMap();
    		idata.put("BIND_FLAG", "0");
    		idata.put("SERIAL_NUMBER", serailNumber);
    		idata.put("RES_CODE",  terminalNumber ); 
    		CSViewCall.call(this, "SS.TerminalBindSVC.insertTerminalBind", idata); 
    	}   
    	if("1".equals(bind_flag)){
    		verifyTerminalBind(input,"1");
    		IData idata = new DataMap();
    		idata.put("BIND_FLAG", "0");
    		idata.put("SERIAL_NUMBER", serailNumber);
    		idata.put("RES_CODE",  terminalNumber ); 
    		CSViewCall.call(this, "SS.TerminalBindSVC.updateTerminal", idata); 
    	}
    	
    }
    
    public void verifyTerminalBind(IData input, String string ) throws Exception
    { 
    	if("0".equals(string)){
    		CSViewCall.call(this, "SS.TerminalBindSVC.checkOpenDate", input);
    	}
        CSViewCall.call(this, "SS.TerminalBindSVC.checkSellDay", input);
        CSViewCall.call(this, "SS.TerminalBindSVC.checkTerminalStaff", input);
        CSViewCall.call(this, "SS.TerminalBindSVC.checkHaveBound", input);
    }
    
    
    public void checkSn(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER", ""));
    	IDataset dataset = CSViewCall.call(this, "SS.CouponsTradeSVC.checkSn", data);
        setAjax(dataset.getData(0));
    }
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
         
    }
    public abstract void setAuditInfos(IDataset dataset);
    public abstract void setInfo(IData data);
    
}