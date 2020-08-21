
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class HdfkActiveTradeQuery extends PersonBasePage
{

    public void dealHdfkActiveTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        CSViewCall.call(this, "SS.HdfkActiveTradeQuerySVC.dealHdfkActiveTrade", data);

        IData ajaxData = new DataMap();
        ajaxData.put("INDEX", data.getString("INDEX"));
        ajaxData.put("OPER_TYPE_CODE", data.getString("OPER_TYPE_CODE"));

        setAjax(ajaxData);
    }

    public void queryTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataOutput dataCount = CSViewCall.callPage(this, "SS.HdfkActiveTradeQuerySVC.queryTrade", data, getPagination("nav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }

    public abstract void setCount(long count);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
