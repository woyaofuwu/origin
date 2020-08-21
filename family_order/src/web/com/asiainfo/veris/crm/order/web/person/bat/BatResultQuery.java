
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatResultQuery extends PersonBasePage
{
    public void initBatResultQuery(IRequestCycle cycle)
    {

    }

    /**
     * @Function: queryBatDealBySN
     * @Description: 批量任务结果查询信息
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 上午10:31:40 2013-8-28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-8-28 huanghui v1.0.0 TODO:
     */
    public void queryBatDealBySN(IRequestCycle cycle) throws Exception
    {
        IData cond = getData("cond");
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatDealBySN", cond, getPagination("taskNav"));
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
