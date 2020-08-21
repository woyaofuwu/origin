
package com.asiainfo.veris.crm.order.web.person.monitorinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ManageAllWhite extends PersonBasePage
{

    // 可放规则中
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap(data.getString("USER_INFO"));
        CSViewCall.call(this, "SS.ManageWhiteSVC.loadChildInfo", param);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        CSViewCall.call(this, "SS.ManageWhiteSVC.onTradeSubmit", data);
    }

    public abstract void setDealInfo(IData data);
}
