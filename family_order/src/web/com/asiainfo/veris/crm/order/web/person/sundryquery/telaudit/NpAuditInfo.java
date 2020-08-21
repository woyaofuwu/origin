
package com.asiainfo.veris.crm.order.web.person.sundryquery.telaudit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpAuditInfo extends PersonBasePage
{

    public void getInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setInfo(data);
    }

    public abstract void setInfo(IData info);
}
