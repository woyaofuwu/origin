
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatTaskDelete extends PersonBasePage
{

    /**
     * @Function: batTaskNowDelete
     * @Description: 批量任务删除
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午7:38:41 2013-7-29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-29 huanghui v1.0.0 TODO:
     */
    public void batTaskNowDelete(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String runInfo = data.get("PARAM").toString();
        IDataset reqInfo = new DatasetList(runInfo);
        for (int i = 0; i < reqInfo.size(); i++)
        {
            IData info = (IData) reqInfo.get(i);
            CSViewCall.call(this, "CS.BatDealSVC.batTaskNowDelete", info);
        }
    }

    public void initBatTaskDelete(IRequestCycle cycle)
    {

    }

    /**
     * @Function: queryBatTaskByPK
     * @Description: 根据TASKID查询相关批量任务批次表TF_B_TRADE_BAT 和 批量任务明细处理表TF_B_TRADE_BATDEAL
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午7:38:22 2013-8-23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-8-23 huanghui v1.0.0 TODO:
     */
    public void queryBatTaskByDelete(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData cond = new DataMap();
        cond.put("BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatTaskDelete", cond, getPagination("taskNav"));
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

    public abstract void setTaskInfo(IData taskInfo);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);

}
