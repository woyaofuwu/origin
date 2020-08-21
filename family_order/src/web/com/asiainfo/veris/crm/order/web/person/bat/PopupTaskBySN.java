
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: PopupTaskBySN.java
 * @Description: 批量信息弹出窗口，批量任务查询
 * @version: v1.0.0
 * @author: huanghui
 * @date: 2013-9-10 下午10:28:33 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-9-10 huanghui v1.0.0 修改原因
 */

public abstract class PopupTaskBySN extends PersonBasePage
{

    public void initPage(IRequestCycle cycle) throws Exception
    {
    }

    /**
     * @Function: queryBatTaskInfo
     * @Description: 根据taskId或者服务号码查询批量任务信息
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午7:38:22 2013-8-23 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-8-23 huanghui v1.0.0 TODO:
     */
    public void queryPopuTaskInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData cond = new DataMap();
        IDataOutput output = new DataOutput();
        cond.clear();
        String param = data.getString("QUERY_PARAM");
        if ("1".equals(data.getString("TRADE_TYPE_CODE")))
        {
            cond.put("BATCH_TASK_ID", param);
            output = CSViewCall.callPage(this, "CS.BatDealSVC.queryPopuTaskInfoByTaskId", cond, getPagination("taskNav"));
        }
        else
        {
            cond.put("SERIAL_NUMBER", param);
            output = CSViewCall.callPage(this, "CS.BatDealSVC.queryPopuTaskInfoBySn", cond, getPagination("taskNav"));
        }
        setTaskInfos(output.getData());
        setBatchTaskListCount(output.getDataCount());
    }

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setTaskInfos(IDataset task);
}
