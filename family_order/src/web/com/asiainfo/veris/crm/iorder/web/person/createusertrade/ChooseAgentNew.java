
package com.asiainfo.veris.crm.iorder.web.person.createusertrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChooseAgentNew extends PersonBasePage
{

    /**
     * 开户代理商选择
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAgent(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.queryAgent", data);
        setAgentList(dataset);
    }

    public abstract void setAgentData(IData info);

    public abstract void setAgentList(IDataset infos);

}
