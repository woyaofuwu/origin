
package com.asiainfo.veris.crm.order.web.person.fixedtelephone.trunktrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeTrunkMainUser extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        CSViewCall.call(this, "SS.ChangeTrunkMainUserSVC.checkInfo", data);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        if (StringUtils.isBlank(data.getString("SERIAL_NUMBER")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.ChangeTrunkMainUserRegSVC.tradeReg", data);
        this.setAjax(result);
    }
}
