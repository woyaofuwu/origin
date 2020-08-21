
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleActiveActivate4Group extends CSBasePage
{
    public void activateActives(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset selectedActives = new DatasetList(data.getString("SELECTED_ACTIVES"));
        for (int index = 0, size = selectedActives.size(); index < size; index++)
        {
            IData saleActiveData = selectedActives.getData(index);
            CSViewCall.call(this, "SS.SaleActiveActivateSVC.activateActives", saleActiveData);
        }
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.put("END_TIME", SysDateMgr.getSysDate());
        cond.put("START_TIME", SysDateMgr.addDays(-1));
        setCondition(cond);
        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, super.getTradeEparchyCode());
        IDataset activePackagesDataset = CSViewCall.call(this, "SS.SaleActiveActivateSVC.queryActivePackages", param);
        setActivePackages(activePackagesDataset);
    }

    public void queryActives(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        param.put(Route.ROUTE_EPARCHY_CODE, super.getTradeEparchyCode());
        IDataset activeDataset = CSViewCall.call(this, "SS.SaleActiveActivateSVC.queryActives", param, getPagination("queryNav"));
        setInfos(activeDataset);
    }

    public abstract void setActivePackages(IDataset activePackages);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
}
