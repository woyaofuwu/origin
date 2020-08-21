
package com.asiainfo.veris.crm.order.web.person.changepassword;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserCheckIdentity extends PersonBasePage
{

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData input = getData();
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        IDataset result = CSViewCall.call(this, "SS.UserCheckIdentitySVC.logUserCheckInfo", input);
        setAjax(result.getData(0));
    }
}
