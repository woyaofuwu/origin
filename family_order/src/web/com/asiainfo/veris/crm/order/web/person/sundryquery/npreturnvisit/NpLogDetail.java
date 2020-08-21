
package com.asiainfo.veris.crm.order.web.person.sundryquery.npreturnvisit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpLogDetail extends PersonBasePage
{
    public void queryNpLogDetail(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset ids = CSViewCall.call(this, "SS.NpReturnVisitSVC.qryNpLogDetail", data);
        setInfos(ids);

    }

    public abstract void setInfos(IDataset infos);
}
