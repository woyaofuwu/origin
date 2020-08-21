
package com.asiainfo.veris.crm.order.web.person.mevltest;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MevlTest extends PersonBasePage
{
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset a = CSViewCall.call(this, "CS.MvelTestSVC.testMvel", data);
        IData info = a.getData(0);
        info.put("RESULT", info.getString("RESULT"));

        this.setAjax(info);
    }
}
