
package com.asiainfo.veris.crm.iorder.web.person.sundryquery.usertradescore;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class PopupUserTradeScoreNew extends PersonQueryPage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData tradeInfo = new DataMap();
        tradeInfo.put("TRADE_ID", data.getString("TRADE_ID"));
        tradeInfo.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        setTradeInfo(tradeInfo);
    }

    /**
     * 查询明细项
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryDetailItem(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataOutput items = CSViewCall.callPage(this, "SS.QueryUserTradeScoreSVC.queryScoreDetailInfoByTradeId", data, getPagination("detailInfo"));
        setItems(items.getData());
        setItemsCount(items.getDataCount());

    }

    /**
     * 查询受益号码信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySnInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput sNs = CSViewCall.callPage(this, "SS.QueryUserTradeScoreSVC.querySNByTradeId", data, getPagination("snInfo"));
        setSNs(sNs.getData());
        setSNsCount(sNs.getDataCount());
    }

    public abstract void setItems(IDataset items);

    public abstract void setItemsCount(long count);

    public abstract void setSNs(IDataset sNs);

    public abstract void setSNsCount(long count);

    public abstract void setTradeInfo(IData tradeInfo);

}
