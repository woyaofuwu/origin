
package com.asiainfo.veris.crm.order.web.person.sundryquery.platinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class PlatQueryCond extends PersonQueryPage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        setEditInfo(data);

    }

    public abstract void setEditInfo(IData editInfo);
}
