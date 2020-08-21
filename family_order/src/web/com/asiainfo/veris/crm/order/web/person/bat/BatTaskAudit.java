
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatTaskAudit extends PersonBasePage
{

    public void initBatTaskAudit(IRequestCycle cycle)
    {

    }

    // 查询待审核批次任务
    public void queryBatTaskByAudit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData cond = new DataMap();
        cond.put("PARAM", "AUDIT");
        cond.put("BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatTaskAudit", cond, getPagination("taskNav"));
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的【未完工工单】数据~");
        }
        setTaskInfos(output.getData());
        setBatchTaskListCount(output.getDataCount());
    }

    public abstract void setBatchOperTypes(IDataset batchOperTypes);

    public abstract void setBatchTaskListCount(long count);

    public abstract void setComp(IData comp);

    public abstract void setInfo(IData info);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);

    // 批次任务审核
    public void subAudit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String runInfo = data.get("PARAM").toString();
        IDataset reqInfo = new DatasetList(runInfo);
        String audiInfo = data.getString("AUDIT_INFO");
        String remark = data.getString("AUDIT_REMARK");
        for (int i = 0; i < reqInfo.size(); i++)
        {
            IData info = reqInfo.getData(i);
            info.put("AUDIT_INFO", audiInfo);
            if (StringUtils.isNotBlank(remark))
            {
                info.put("AUDIT_REMARK", remark);
            }
            CSViewCall.call(this, "CS.BatDealSVC.subAudit", info);
        }

    }

}
