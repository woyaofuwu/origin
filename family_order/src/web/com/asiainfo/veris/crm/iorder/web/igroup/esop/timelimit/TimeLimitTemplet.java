package com.asiainfo.veris.crm.iorder.web.igroup.esop.timelimit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class TimeLimitTemplet extends CSBasePage 
{
    public abstract void setCondition(IData condition);
    public abstract void setInfos(IDataset infos);
	
    /**
     * 查询
     * @param cycle
     * @throws Exception
     */
    public void queryAllOverTimer(IRequestCycle cycle) throws Exception{
    	IData condData = getData();
    	IDataset timerDatas = CSViewCall.call(this, "SS.WorkformTimerTaskSVC.qryInfoByTimerId", condData);
    	setInfos(timerDatas);
    	this.setAjax(timerDatas);
    }
    
    /**
     * 更新
     * @param cycle
     * @throws Exception
     */
    public void updateAllOverTimer(IRequestCycle cycle) throws Exception{
    	IData input = getData();
    	CSViewCall.call(this, "SS.WorkformTimerTaskSVC.updByTimerId", input);
    }
    
    /**
     * 新增
     * @param cycle
     * @throws Exception
     */
    public void insertAllOverTimer(IRequestCycle cycle) throws Exception{
    	IData input = getData();
    	CSViewCall.call(this, "SS.WorkformTimerTaskSVC.insertAllOverTimer", input);
    }
    
    /**
     * 删除
     * @param cycle
     * @throws Exception
     */
    public void delAllOverTimer(IRequestCycle cycle) throws Exception{
    	IData input = getData();
    	CSViewCall.call(this, "SS.WorkformTimerTaskSVC.delByTimerId", input);
    }
}
