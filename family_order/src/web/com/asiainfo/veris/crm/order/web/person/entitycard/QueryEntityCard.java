/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.entitycard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-29 修改历史 Revision 2014-5-29 下午04:35:04
 */
public abstract class QueryEntityCard extends PersonBasePage
{
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void queryEntityCard(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.EntityCardSVC.QueryEntityCard", data);

        this.setInfos(results);

    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset dataset);

}
