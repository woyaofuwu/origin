
package com.asiainfo.veris.crm.order.web.person.sundryquery.hiservice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryHIService extends PersonBasePage
{
    /**
     * 交换机信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryHIService(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
        data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        IDataOutput orderInfos = CSViewCall.callPage(this, "SS.QueryHIServiceSVC.queryHIService", data, getPagination("navt"));
        if (orderInfos != null)
        {
            setCount(orderInfos.getDataCount());
        }
        setInfos(orderInfos.getData());
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset viceInfos);
}
