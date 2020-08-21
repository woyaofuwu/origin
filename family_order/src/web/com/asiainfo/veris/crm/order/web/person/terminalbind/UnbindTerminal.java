package com.asiainfo.veris.crm.order.web.person.terminalbind;

  
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UnbindTerminal extends PersonBasePage
{

	public abstract void setCondition(IData condition);
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData params);
	public abstract void setCityInfo(IDataset dataset);
	public abstract void setCount(long count);
//	public abstract void setExport(IData data);

	/**
	 * 页面初始化设置
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void init(IRequestCycle cycle) throws Exception
	{
		IData params = getData();
		
		String loginStaffId = getVisit().getStaffId(); // 系统登录工号
		String cityCode = getVisit().getCityCode(); // 归属业务区
		
		if (checkRight(loginStaffId))
		{
			params.put("EPARCH_CODE", "0898");
		}
		else
		{
			params.put("EPARCH_CODE", cityCode);
		}
		//初始下拉框，借用原有的方法
		IDataset cityInfos = CSViewCall.call(this, "SS.TerminalManageSVC.queryCityInfos", params);
		setCityInfo(cityInfos);
		
		params.put("CITY_CODE", cityCode);
		setCondition(params);

	}

	/**
	 *   
	 * @param cycle
	 * @throws Exception
	 */
	public void queryTerminals(IRequestCycle cycle) throws Exception
	{

		IData data = getData("cond", true);
		data.put("RES_CODE", data.getString("TERMINAL_NUMBER", ""));
		data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
		data.put("CITY_CODE", data.getString("CITY_CODE", ""));
		Pagination page = getPagination("recordNav");

		IDataOutput terminalInfos = CSViewCall.callPage(this, "SS.TerminalBindSVC.queryTerminalBind", data, page);
		IDataset terminalInfo  =terminalInfos.getData();


 
		IDataset subInfo = new DatasetList();
		  if(terminalInfo!=null &&terminalInfo.size()>0){
	        	for(int k=0;k<terminalInfo.size();k++){
	        		String state=terminalInfo.getData(k).getString("BIND_FLAG");
	        		if("0".equals(state)){
	        			state="未解绑";
	        			terminalInfo.getData(k).put("UNBIND_TIME", "无"); 
	        			terminalInfo.getData(k).put("UNBIND_STAFF_ID", "无"); 
	        		}else if("1".equals(state)){
	        			state="已解绑";
	        			//terminalInfo.getData(k).put("BIND_TIME", "无"); 
	        		}
	        		 
	        		terminalInfo.getData(k).put("BIND_FLAG", state); 
	        	} 
	        }
		  subInfo = terminalInfo;

		
		setCount(terminalInfos.getDataCount());
		setCondition(data); // 保留查询条件
		setInfos(subInfo);
		
	}

	 

	 

	 
	public boolean checkRight(String staffid) throws Exception
	{
		boolean isTrue = false;
		
		if (staffid.substring(0, 4).equals("HNSJ") ||  staffid.substring(0, 4).equals("SUPE"))
		{
			isTrue = true;
		}
		
		return isTrue;
	}

	 
	
	/**
	 * 解绑操作提交
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
//		IData data = getData("cond", true);
//		IData info = CSViewCall.call(this, "SS.TerminalManageSVC.saveTerminalFee", data).getData(0);
//		setAjax(info);
		
		IData data = getData();
		IDataset tikInfos = new DatasetList(data.getString("TICKET_INFO", "")); 
    	IData tikInfo=new DataMap();
    	if(tikInfos!=null && tikInfos.size()>0){
    		tikInfo=tikInfos.getData(0);
    		tikInfo.put("BIND_FLAG", "1");
    	}
    	
    	String loginStaffId = getVisit().getStaffId(); // 系统登录工号
    	boolean highpriv = StaffPrivUtil.isFuncDataPriv(loginStaffId, "TERSNSJ_PRIV");
    	if(highpriv ){ 
    	  CSViewCall.call(this, "SS.TerminalBindSVC.updateTerminal", tikInfo); 
    	}else{
    	  CSViewException.apperr(CrmCommException.CRM_COMM_103,"用户无解绑数据权限!");
    	}
		 
	}
	 
}