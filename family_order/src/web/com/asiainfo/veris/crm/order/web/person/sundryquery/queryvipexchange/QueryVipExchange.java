
package com.asiainfo.veris.crm.order.web.person.sundryquery.queryvipexchange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryVipExchange extends PersonBasePage
{
    /**
     * 大客户兑换信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryVipExchange(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);

        data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        data.put("UPDATE_TIME", data.getString("cond_UPDATE_TIME"));
        IDataOutput orderInfos = CSViewCall.callPage(this, "SS.QueryVipExchangeSVC.queryVipExchange", data, getPagination("navt"));
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
