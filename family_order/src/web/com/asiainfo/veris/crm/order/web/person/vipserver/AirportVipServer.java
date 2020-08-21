
package com.asiainfo.veris.crm.order.web.person.vipserver;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AirportVipServer extends PersonBasePage
{

    /**
     * 校验VIP用户身份是否合法
     * 
     * @date 2009-9-18
     * @param pd
     * @throws Exception
     */
    public void authVipUserInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.AirportVipServerSVC.authVipUserInfo", data);
        setAjax(dataset);

    }

    public IDataset getAuthType() throws Exception
    {

        IDataset dataset = new DatasetList();
        DataMap data2 = new DataMap();
        data2.put("AUTH_NAME", "VIP卡");
        data2.put("AUTH_VALUE", "1");
        dataset.add(data2);

        DataMap data1 = new DataMap();
        data1.put("AUTH_NAME", "身份证");
        data1.put("AUTH_VALUE", "0");
        dataset.add(data1);
        return dataset;
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        this.setCustInfo(custInfo);
        this.setUserInfo(userInfo);

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        IDataset vipServerInfos = CSViewCall.call(this, "SS.AirportVipServerSVC.getAirportVipInfos", param);

        IData vipData = vipServerInfos.getData(0);
        this.setVipInfo(vipData.getData("vipInfo"));
        this.setAirportInfos(vipData.getDataset("AIRPORT_INFOS"));
        this.setAirportServiceTypeInfos(vipData.getDataset("AIRPORT_SERVICE_TYPE"));
        this.setInfos(vipData.getDataset("INFOS"));
    }

    public void loadPrintData(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.AirportVipServerSVC.loadPrintData", data);
        setAjax(dataset);

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

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.AirportVipServerRegSVC.tradeReg", param);
        setAjax(dataset);

    }

    public abstract void setAirportInfos(IDataset airportInfos);

    public abstract void setAirportServiceTypeInfos(IDataset airportInfos);

    public abstract void setCommInfo(IDataset commInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setVipInfo(IData vipInfo);

}
