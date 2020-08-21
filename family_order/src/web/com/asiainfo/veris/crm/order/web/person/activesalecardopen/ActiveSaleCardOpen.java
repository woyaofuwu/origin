
package com.asiainfo.veris.crm.order.web.person.activesalecardopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ActiveSaleCardOpen extends PersonBasePage
{
    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        CSViewCall.call(this, "SS.ActiveSaleCardOpenSVC.checkInfo", data);
    }

    /**
     * 重写提交方法
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ActiveSaleCardOpenRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);
}
