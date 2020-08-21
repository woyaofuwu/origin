
package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.widenetinterface;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class WidenetInterfaceNew extends CSBasePage
{

    public void execInterface(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset infos = CSViewCall.call(this, "SS.WidenetInterfaceSVC.execInterface", data);
        if (IDataUtil.isEmpty(infos))
        {
            setAjax("ALERT_CODE", "0");
        }
        setInfos(infos);
    }

    public void getTradeInterface(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput infos = CSViewCall.callPage(this, "SS.WidenetInterfaceSVC.getTradeInterface", data, getPagination("olcomnav"));
        if (IDataUtil.isEmpty(infos.getData()))
        {
            setAjax("ALERT_CODE", "0");
        }
        setInfos(infos.getData());
        setInfosCount(infos.getDataCount());
    }

    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.WidenetInterfaceSVC.onInitTrade", data);
        setTradeTypeList(dataset);
    }

    public void restartInterface(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        CSViewCall.call(this, "SS.WidenetInterfaceSVC.restartInterface", data);
        IDataset infos = CSViewCall.call(this, "SS.WidenetInterfaceSVC.getTradeInterface", data);
        if (IDataUtil.isEmpty(infos))
        {
            setAjax("ALERT_CODE", "0");
        }
        setInfos(infos);

    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long count);

    public abstract void setTradeTypeList(IDataset tradeTypeList);
}
