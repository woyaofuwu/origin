
package com.asiainfo.veris.crm.order.web.person.sundryquery.interboss.dmbusisel;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class StaticInfoQuery extends PersonBasePage
{
    public void queryStaticInfo(IRequestCycle cycle) throws Exception
    {
        IData condtion = getData();

        IDataset staticInfo = CSViewCall.call(this, "SS.DMBusiCommonQurySVC.queryStaticInfo", condtion);

        setAjax(staticInfo);
        setInfos(staticInfo);
        setCondition(getData("cond", true));
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);
}
