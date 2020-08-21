
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatDealQueryByBatchId extends PersonBasePage
{

    public void initPageBatch(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String batchId = data.getString("BATCH_ID", "");
        if (StringUtils.isNotBlank(batchId))
        {
            setCondition(data);
        }
    }

    public void queryBatDealByBatchId(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatDealByBatchId", cond, getPagination("taskNav"));

        IDataset result = output.getData();
        if (result == null || result.size() == 0)
        {
            setParams("没有符合查询条件的【未完工工单】数据~");
        }
        setInfos(result);
        setBatchTaskListCount(output.getDataCount());

        setCondition(cond);
    }

    public abstract void setBatchTaskListCount(long count);

    public abstract void setCondition(IData condition);

    public abstract void setDealInfo(IData data);

    public abstract void setDealInfos(IDataset dataset);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setParams(String params);

    public abstract void setRowIndex(int index);
}
