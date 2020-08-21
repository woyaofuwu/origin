
package com.asiainfo.veris.crm.order.web.person.np.createnpusertrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChooseIdlePhone extends PersonBasePage
{

    /**
     * 省内异地开户选号
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryIDlePhone(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("AREA_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.queryIDlePhone", data);
        setPhoneList(dataset);
    }

    public abstract void setAreaChoose(IDataset areaChoose);

    public abstract void setPhoneData(IData info);

    public abstract void setPhoneList(IDataset infos);
}
