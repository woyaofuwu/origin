package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.widenetconstruction;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * 
 * @author think
 *
 */

public abstract class QueryWidenetConstructionNew extends CSBasePage
{

    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void initQueryConstruction(IRequestCycle cycle) throws Exception
    {
		String startDate = TimeUtil.decodeTimestamp(TimeUtil.getFirstDayOfThisMonth(), "yyyy-MM-dd");
		String endDate = TimeUtil.decodeTimestamp(TimeUtil.getLastDateThisMonth(), "yyyy-MM-dd");
		IData condition = new DataMap();
		condition.put("START_DATE", startDate);
		condition.put("END_DATE", endDate);
		setCondition(condition);
    }

    public void queryWidenetConsts(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //IData temp = getData("cond", true);
        //data.putAll(temp);

        IDataOutput output = CSViewCall.callPage(this, "SS.QueryWidenetConstructionSVC.queryWidenetConstsInfo", data, getPagination("LogNav"));

        setInfos(output.getData());
        setLogCount(output.getDataCount());
    }
    
    public void updateConstsPass(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData pageData = getData("cond", true);

        String remark = pageData.getString("REMARK", "");
        IData inputData = new DataMap();
        
        
        IDataset numberList = new DatasetList(data.getString("NUMBER_LIST"));
        if(IDataUtil.isNotEmpty(numberList)){
        	inputData.put("NUMBER_LIST", numberList);
        }
        
        inputData.put("REMARK", remark);
        IDataset infos = CSViewCall.call(this, "SS.QueryWidenetConstructionSVC.updateConstsPass", inputData);

        setCondition(pageData);
        setAjax(infos);
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void updateConstsNoPass(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData pageData = getData("cond", true);

        String remark = pageData.getString("REMARK", "");
        IData inputData = new DataMap();
        
        
        IDataset numberList = new DatasetList(data.getString("NUMBER_LIST"));
        if(IDataUtil.isNotEmpty(numberList)){
        	inputData.put("NUMBER_LIST", numberList);
        }
        
        inputData.put("REMARK", remark);
        IDataset infos = CSViewCall.call(this, "SS.QueryWidenetConstructionSVC.updateConstsNoPass", inputData);

        setCondition(pageData);
        setAjax(infos);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setLogCount(long logCount);

}
