/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.familytrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-26 修改历史 Revision 2014-5-26 下午07:22:19
 */
public abstract class FamilyUnionPayQry extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();

        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.FamilyUnionPaySVC.getFamilyUnionPayInfos", pagedata);

        this.setInfos(results.getData(0).getDataset("QRY_MEMBER_LIST"));

        this.setCommInfo(results.getData(0).getData("COND_MEMBER_INFO"));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public abstract void setCommInfo(IData info);// 

    public abstract void setInfos(IDataset infos);// 

}
