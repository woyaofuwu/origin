 
package com.asiainfo.veris.crm.order.web.person.sundryquery.realname;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ReleaseCertificateNum extends PersonBasePage
{
    public void releaseCampOn(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("LIST", new DatasetList(data.getString("LIST")));
        CSViewCall.call(this, "SS.NationalOpenLimitSVC.releaseCampOn", data);
    }

    public void queryCampOnList(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        cond.put("ID_CARD_TYPE", cond.getString("PSPT_TYPE_CODE", ""));
        cond.put("ID_CARD_NUM", cond.getString("PSPT_ID", ""));
        IDataset results = CSViewCall.call(this, "SS.NationalOpenLimitSVC.queryCampOnList", cond);
        setCount(results.size());
        setInfos(results);
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);
}
