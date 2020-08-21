
package com.asiainfo.veris.crm.order.web.group.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class QuerySaleActiveDetail extends CSBasePage
{
    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setPageCounts(long pageCounts);

    public void querySaleActDetail(IRequestCycle cycle) throws Exception
    {
        IDataOutput saleactOutput = CSViewCall.callPage(this, "CS.UserSaleActiveInfoQrySVC.querySaleActDetail", getData(), getPagination("pageNav"));
        setPageCounts(saleactOutput.getDataCount());
        setInfos(saleactOutput.getData());
        setCondition(getData());
    }
}
