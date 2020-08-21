package com.asiainfo.veris.crm.order.web.group.grpflow;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GprsCompareQuery extends GroupBasePage
{
	public abstract void setCondition(IData condition);
    public abstract void setCount(long count);
    public abstract void setInfos(IDataset infos);
    /**
     * 查询费用信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryGrpsCompareLogs(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        IData svcData = new DataMap();
        svcData.put("GROUP_ID", condData.getString("cond_GROUP_ID"));
        svcData.put("START_DATE", condData.getString("cond_START_DATE"));
        svcData.put("END_DATE", condData.getString("cond_END_DATE"));
        //调用服务
        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GprsSaleForGrpSVC.qryGrpsCompareLogs", svcData, getPagination("pageNavConti"));
        IDataset infos = dataOutput.getData();
        setInfos(infos);
        setCount(dataOutput.getDataCount());
    }

    
}