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
 * @CREATED by gongp@2014-5-14 修改历史 Revision 2014-5-14 下午03:23:24
 */
public abstract class ChangEntityCard extends PersonBasePage
{

    public void checkNewEntityCard(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.EntityCardSVC.checkNewEntityCard", data);

        // this.setAjax(results.getData(0));

    }

    public void checkOldEntityCard(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());

        data.put("CARD_NO", data.getString("OLD_ENTITY_CARD_NO"));

        IDataset results = CSViewCall.call(this, "SS.EntityCardSVC.QueryEntityCard", data);

        this.setAjax(results.getData(0));
    }

    public void LockEntityCard(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());

        data.put("CARD_NO", data.getString("OLD_CARD_NO"));

        IDataset results = CSViewCall.call(this, "SS.EntityCardSVC.lockEntityCard", data);

        this.setAjax(results.getData(0));

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.ChangEntityCardRegSVC.tradeReg", data);
        setAjax(dataset);

    }

}
