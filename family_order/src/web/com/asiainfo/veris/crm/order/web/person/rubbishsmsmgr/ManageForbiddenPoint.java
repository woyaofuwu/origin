
package com.asiainfo.veris.crm.order.web.person.rubbishsmsmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class ManageForbiddenPoint extends PersonQueryPage
{
    /**
     * 使记录状态变为失效
     * 
     * @param cycle
     * @throws Exception
     */
    public void disableData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        CSViewCall.call(this, "SS.ManageForbiddenPointSVC.disableData", data);
        // 重新查询结果
        this.queryForbiddenList(cycle);
    }

    /**
     * 查询列表
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryForbiddenList(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataOutput output = CSViewCall.callPage(this, "SS.ManageForbiddenPointSVC.queryForbiddenList", data, getPagination("forbiddenPoint"));
        IDataset result = output.getData();
        if (IDataUtil.isEmpty(result))
        {
            this.setAjax("ALERT_INFO", "满足条件的记录不存在!");
        }
        setInfos(result);
        setCount(output.getDataCount());
    }

    public abstract void setCount(long count);

    public abstract void setEditInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
