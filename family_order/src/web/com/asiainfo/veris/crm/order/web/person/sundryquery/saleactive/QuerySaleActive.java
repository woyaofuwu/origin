
package com.asiainfo.veris.crm.order.web.person.sundryquery.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QuerySaleActive extends PersonBasePage
{

    public void onInit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String qryType = data.getString("QRY_TYPE");
        if (StringUtils.isNotBlank(qryType))
        {
            setQryType(qryType);
        }
    }

    public void querySaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QuerySaleActiveSVC.querySaleActive", data, getPagination("nav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
        String qryType = data.getString("QRY_TYPE");
        if (StringUtils.isNotBlank(qryType))
        {
            setQryType(qryType);
        }
    }

    public abstract void setCount(long count);

    public abstract void setDeposit(IData deposit);

    public abstract void setDeposits(IDataset deposits);

    public abstract void setDiscnt(IData discnt);

    public abstract void setDiscnts(IDataset discnts);

    public abstract void setGood(IData good);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setQryType(String qryType);

    public void showDetail(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData result = CSViewCall.call(this, "SS.QuerySaleActiveSVC.queryActiveDetail", data).getData(0);
        if (result.containsKey("DISCNTS"))
        {
            setDiscnts(result.getDataset("DISCNTS"));
        }
        if (result.containsKey("GOODS"))
        {
            setGoods(result.getDataset("GOODS"));
        }
        if (result.containsKey("DEPOSITS"))
        {
            setDeposits(result.getDataset("DEPOSITS"));
        }
    }
}
