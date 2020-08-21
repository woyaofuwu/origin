
package com.asiainfo.veris.crm.order.web.person.sundryquery.interboss.dmbusisel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DMBusiCardSel extends PersonBasePage
{
    public void queryDMBusiCard(IRequestCycle cycle) throws Exception
    {
        IData conParams = getData("cond", true);

        IDataset spinfos = CSViewCall.call(this, "SS.DMBusiCommonQurySVC.queryDMBusiCard", conParams);

        setAjax(spinfos);
        setAccountsInfos(spinfos);
        setCondition(conParams);
    }

    public abstract void setAccountsInfos(IDataset accountsInfos);

    public abstract void setCondition(IData condition);
}
