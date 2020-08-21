
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatBatchQuery extends PersonBasePage
{
    public void batchDetialQuery(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
//        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.batchDetialQuery", data, this.getPagination("taskNav"));
        //添加对实名制认证的校验
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.batchDetialQry", data, this.getPagination("taskNav"));
        this.setDealInfos(output.getData());
        this.setBatchTaskListCount(output.getDataCount());
    }

    public void initPageBatch(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String batchId = data.getString("BATCH_ID", "");
        if (StringUtils.isNotBlank(batchId))
        {
            setCondition(data);
        }
    }

    public void queryBatchInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String taksId = data.getString("BATCH_TASK_ID");
        String FROM_PAGE = data.getString("FROM_PAGE");
        if (StringUtils.isBlank(taksId))
        {
            setTipInfo("任务名称不能为空！");
            return;
        }
        IDataset taskInfo = CSViewCall.call(this, "CS.BatDealSVC.queryBatTaskByPK", data);
        if (IDataUtil.isEmpty(taskInfo))
        {
            setTipInfo("任务名称不能为空！");
            return;
        }
        else
        {
            String taskName = taskInfo.getData(0).getString("BATCH_TASK_NAME", "");
            data.put("BATCH_TASK_NAME", taskName);
        }
        if (FROM_PAGE != null && "BatTaskQuery".equals(FROM_PAGE))
        {
            data.put("cond_SXQX_CODE", "1");
            String cond_SXQX_NAME = StaticUtil.getStaticValue("BAT_QUERYTASK_TYPE", "1");
            data.put("cond_SXQX_NAME", cond_SXQX_NAME);
            setCondition(data);
        }
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatchInfo", data, getPagination("taskNav"));
        setBatchInfoCount(output.getDataCount());
        setBatchInfos(output.getData());
    }

    public void queryFaildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset failedBatchs = CSViewCall.call(this, "CS.BatDealSVC.queryFaildInfo", data);
        IData result = new DataMap();
        String resultStr = "0";
        if (IDataUtil.isEmpty(failedBatchs))
        {
            resultStr = "1";
        }
        result.put("RESULT", resultStr);
        setAjax(result);
    }

    public abstract void setBatchInfo(IData data);

    public abstract void setBatchInfoCount(long count);

    public abstract void setBatchInfos(IDataset dataset);

    public abstract void setBatchTaskListCount(long count);

    public abstract void setCondition(IData info);

    public abstract void setDealInfo(IData data);

    public abstract void setDealInfos(IDataset dataset);

    public abstract void setParams(String params);

    public abstract void setRowIndex(int index);

    public abstract void setTipInfo(String tipInfo);
}
