package com.asiainfo.veris.crm.iorder.web.person.highvalueuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class HighValueUserEntryNew extends PersonBasePage{ 

	public abstract void setCondition(IData condition);
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData params);
	public abstract void setCityInfo(IDataset dataset);
	public abstract void setCount(long count);

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    { 
        IData data = getData("cond", true);     
 		CSViewCall.call(this, "SS.HighValueUserEntrySVC.insertHighUser", data);     	
    }    
    public void updateHighValueUser(IRequestCycle cycle) throws Exception
    { 
        IData data = getData();   
        CSViewCall.call(this, "SS.HighValueUserEntrySVC.updateHighUser", data);  
    }    
    public void queryHighValueUser(IRequestCycle cycle) throws Exception
	{
        String alertInfo = "";
		IData data = getData("cond", true);
		data.put("START_DATE", data.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER);
		data.put("FINISH_DATE", data.getString("FINISH_DATE") + SysDateMgr.END_DATE);
		IDataOutput infos = CSViewCall.callPage(this, "SS.HighValueUserEntrySVC.queryHighValueUser", data, getPagination("recordNav"));
		IDataset info = infos.getData();
		if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		setCount(infos.getDataCount());
		setInfos(info);	        
	}
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = SysDateMgr.getFirstDayOfThisMonth4WEB();//endDate;
        IData data = getData("cond", true);
        data.put("cond_START_DATE", firstData);
        data.put("cond_FINISH_DATE", endDate);
        data.put("cond_TRADE_STAFF_ID", this.getVisit().getStaffId());
        data.put("cond_TRADE_CITY_CODE", this.getVisit().getCityCode());
        this.setCondition(data);
    }
}