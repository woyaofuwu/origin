package com.asiainfo.veris.crm.iorder.web.person.selfhelpnew;

import org.apache.poi.util.SystemOutLogger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NewTerminalReconQuery extends PersonBasePage{ 

	public abstract void setCondition(IData condition);
	public abstract void setReconResults(IDataset reconResults);
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData params);
	public abstract void setCityInfo(IDataset dataset);
	public abstract void setCount(long count);
    
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    	IDataset reconResults = new DatasetList();
    	
    	String[] text = {"BOSS多","平台多","数据差异"};
    	for(int i = 0;i < text.length;i ++)
    	{
    		IData reconResult = new DataMap();
    		reconResult.put("TEXT", text[i]);
    		reconResult.put("TITLE", i + 1);
    		reconResult.put("VALUE", i + 1);
    		
    		reconResults.add(reconResult);
    	}
    	setReconResults(reconResults);
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = SysDateMgr.getFirstDayOfThisMonth4WEB();//endDate;
        IData data = getData("cond", true);
        data.put("cond_START_DATE", firstData);
        data.put("cond_FINISH_DATE", endDate);        
        this.setCondition(data);
    }
    public void queryPayOrder(IRequestCycle cycle) throws Exception
	{
        String alertInfo = "";
		IData data = getData("cond", true);
		data.put("START_DATE", data.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
		data.put("FINISH_DATE", data.getString("FINISH_DATE") + SysDateMgr.END_DATE);
		IDataOutput infos = CSViewCall.callPage(this, "SS.NewTerminalReconQuerySVC.queryPayOrder", data, getPagination("recordNav"));
		IDataset info = infos.getData();
		if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		setCount(infos.getDataCount());
		setInfos(info);	        
	}
    public void backFee(IRequestCycle cycle) throws Exception
	{
        String alertInfo = "";
		IData data = getData();
		IDataset infos = CSViewCall.call(this, "SS.SelfTerminalOperateSVC.backFee", data);
		IData info = new DataMap();
		if(IDataUtil.isNotEmpty(infos) && infos.size() > 0){
			info = infos.getData(0);
			String reCode = info.getString("RESULT_CODE");
			String reInfo = info.getString("RESULT_INFO");
			if ("0".equals(reCode))
	        {
	            alertInfo = "退款成功!";
	        }else{	        	
	        	if(reInfo.length() > 31)
	        	{
	        		alertInfo = reInfo.substring(0, 30);
	        	}else{
	        		alertInfo = reInfo;
	        	}        	
	        }
		}else{
			alertInfo = "调用退款接口异常,无返回值!";
		}

		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示        
	}
}
