
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ElementPopupQry extends PersonBasePage
{
    /**
     * @Function: queryDiscntInfo
     * @Description: 查询优惠编码优惠名称
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 下午7:38:22 2014-3-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0 TODO:
     */
    public void queryDiscnts(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        long getDataCount = 0;
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryDiscnts", data, getPagination("taskNav"));
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的数据！");
        }
        else
        {
            setTipInfo("双击任意一条查询结果返回！");
            getDataCount = Long.parseLong(output.getData().getData(0).getString("TOTAL"));
        }
        setTaskInfos(output.getData());
        setBatchTaskListCount(getDataCount);
    }

    public void queryElements(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String queryType = data.getString("cond_QUERY_TYPE", "");
        if ("1".equals(queryType))
        {
            queryDiscnts(cycle);
        }
        else if ("2".equals(queryType))
        {
            queryServices(cycle);
        }
        else
        {
            setTipInfo("双击任意一条查询结果返回！");
        }
    }

    /**
     * @Function: queryServiceInfo
     * @Description:
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 下午7:38:22 2014-3-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0
     */
    public void queryServices(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        long getDataCount = 0;
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryServices", data, getPagination("taskNav"));
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的数据！");
        }
        else
        {
            setTipInfo("双击任意一条查询结果返回！");
            getDataCount = Long.parseLong(output.getData().getData(0).getString("TOTAL"));
        }
        setTaskInfos(output.getData());
        setBatchTaskListCount(getDataCount);
    }

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setCondition(IData info);

    public abstract void setTaskInfo(IData taskInfo);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);
}
