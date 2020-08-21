
package com.asiainfo.veris.crm.order.web.person.custmanagermaintain;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustManagerMainTain extends PersonBasePage
{

    public void exportCustManagerInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset output = CSViewCall.call(this, "SS.CustManagerMainTainSVC.exportCustManagerInfos", data);
        setAjax(output.getData(0));
    }

    public void getCustManagerInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.CustManagerMainTainSVC.refreshClick", data, getPagination("custManagerInfoNav"));
        this.setInfos(output.getData());
        this.setCount(output.getDataCount());
    }

    public void importData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData set = new DataMap();
        IDataset sets = CSViewCall.call(this, "SS.CustManagerMainTainSVC.importData", data);
        if (IDataUtil.isNotEmpty(sets))
        {
            set = sets.getData(0);
            IDataset succds = set.getDataset("SUCCESS");
            IData failds = set.getData("FAILDS");
            this.setSelectList(succds);
            setAjax(failds);
        }
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

        // 传递参数：stopOpenRelation buildSelectJsonData
        String routeEparchyCode = this.getParameter(Route.ROUTE_EPARCHY_CODE);
        if (routeEparchyCode == null)
        {
            routeEparchyCode = getVisit().getStaffEparchyCode();
        }
        data.put(Route.ROUTE_EPARCHY_CODE, routeEparchyCode);

        IDataset dataset = CSViewCall.call(this, "SS.CustManagerMainTainSVC.sureClick", data);
        setAjax(dataset.getData(0));

    }

    public abstract void setCommInfo(IDataset commInfo);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setSelectList(IDataset selectList);

}
