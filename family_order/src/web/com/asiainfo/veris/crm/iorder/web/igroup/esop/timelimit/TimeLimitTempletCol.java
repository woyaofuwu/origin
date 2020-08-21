package com.asiainfo.veris.crm.iorder.web.igroup.esop.timelimit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class TimeLimitTempletCol extends CSBasePage 
{
    public abstract void setCondition(IData condition);
    public abstract void setInfos(IDataset infos);
	
	public void queryBpmTemplet(IRequestCycle cycle) throws Exception
	{
		IData condData = getData();
		String bpmTempletId = condData.getString("BPM_TEMPLET_ID", "");
		IData param = new DataMap();
		param.put("BPM_TEMPLET_ID", bpmTempletId);
		IData charInfo = new DataMap();
		IDataset chartInfos = CSViewCall.call(this, "SS.WorkformChartInfoSVC.qryTempletData", param);
		
		if(DataUtils.isNotEmpty(chartInfos)){
			IData resultCharInfo = chartInfos.getData(0);
			String xml = resultCharInfo.getString("XML_INFO", "");
			resultCharInfo.remove("XML_INFO");
			this.setCondition(resultCharInfo);
			charInfo.put("XML_INFO", xml);
		}
		this.setAjax(charInfo);
		
	}
	
	public void qryRelaId(IRequestCycle cycle) throws Exception
	{
		IData input = getData();
		IDataset chartInfos = CSViewCall.call(this, "SS.WorkformTimerTaskSVC.qryByBpmTempletNodePerNodeId", input);
		
		if(DataUtils.isNotEmpty(chartInfos)){
			IData resultCharInfo = chartInfos.getData(0);
			this.setAjax(resultCharInfo);
		}
	}
	
	public void submitTaskInfos(IRequestCycle cycle) throws Exception
	{
		IData input = getData();
		CSViewCall.call(this, "SS.WorkformTimerTaskSVC.insertNodeTaskRela", input);
	}
}
