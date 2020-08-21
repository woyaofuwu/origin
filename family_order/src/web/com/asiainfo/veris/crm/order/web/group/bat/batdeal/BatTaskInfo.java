
package com.asiainfo.veris.crm.order.web.group.bat.batdeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class BatTaskInfo extends CSBasePage
{

    /**
     * 查询批量任务信息
     * 
     * @param pd
     * @param data
     * @throws Exception
     */
    public void queryTaskInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put("BATCH_TASK_ID", getParameter("BATCH_TASK_ID", ""));

        IDataset dataset = CSViewCall.call(this, "CS.BatDealSVC.queryBatTask", data);

        if (dataset.size() == 0)
        {
            CSViewException.apperr(BatException.CRM_BAT_16);
        }

        IData task = dataset.getData(0);

        setInfo(task);
    }

    public abstract void setInfo(IData info);
}
