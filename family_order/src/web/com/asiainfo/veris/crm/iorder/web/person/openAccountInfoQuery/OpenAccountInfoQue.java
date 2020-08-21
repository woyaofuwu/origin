package com.asiainfo.veris.crm.iorder.web.person.openAccountInfoQuery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OpenAccountInfoQue extends PersonBasePage{ 

	public abstract void setCondition(IData condition);
	public abstract void setInfos(IDataset infos);
	public abstract void setCount(long count);

    
    public void queryOpenAccountInfo(IRequestCycle cycle) throws Exception
	{
    	String alertInfo = "";
    	IData data = getData("cond", true);
    	data.put("RSRV_STR7", this.getVisit().getCityCode());
        data.put("RSRV_STR8", this.getVisit().getDepartCode());
        System.out.print(data);
        IDataOutput infos = CSViewCall.callPage(this, "SS.OpenAccountInfoQuerySVC.queryOpenAccountInfo", data, getPagination("recordNav"));
        IDataset info = infos.getData();              
        if (IDataUtil.isEmpty(info))
        {
            alertInfo = "没有符合查询条件的数据!";
        }
		this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
		//
		setCount(infos.getDataCount());
        setInfos(info);
        
	}
    /**
     * 初始化方法
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        // 设置起止时间
        String endDate = SysDateMgr.getSysDate();// 格式为YYYY-MM-DD
        String firstData = SysDateMgr.getFirstDayOfThisMonth4WEB();//endDate;
        IData data = getData("cond", true);
        data.put("cond_START_DATE", firstData);
        data.put("cond_END_DATE", endDate);        
        data.put("cond_RSRV_STR5", this.getVisit().getCityCode());
        this.setCondition(data);
    }
}
