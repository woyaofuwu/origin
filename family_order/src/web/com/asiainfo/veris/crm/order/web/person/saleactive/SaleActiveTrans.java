
package com.asiainfo.veris.crm.order.web.person.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SaleActiveTrans extends PersonBasePage
{
    public void checkSourceUser(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("ACTIVE_STATE", data.getString("ACTIVE_STATE"));
        svcParam.put("END_DATE", data.getString("END_DATE"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        CSViewCall.call(this, "SS.SaleActiveTransCheckSVC.checkSourceUser", svcParam);
    }

    public void checkTargetUser(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("TARGET_SERIAL_NUMBER", data.getString("TARGET_SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveTransCheckSVC.checkTargetUser", svcParam);
        setInfos(saleActives);
    }

    public void loadBaseTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("USER_ID", data.getString("USER_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveTransSVC.queryActives", svcParam);
        setInfos(saleActives);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("SOURCE_SN", data.getString("SOURCE_SN"));
        svcParam.put("TARGET_SN", data.getString("TARGET_SN"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveTransRegSVC.tradeReg", svcParam);
        this.setAjax(saleActives);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
