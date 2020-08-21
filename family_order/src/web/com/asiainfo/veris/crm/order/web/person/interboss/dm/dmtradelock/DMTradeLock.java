
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmtradelock;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DMTradeLock extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(DMTradeLock.class);

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        IData custInfo = new DataMap(pageData.getString("CUST_INFO"));

        IData data = new DataMap();

        data.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        data.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
        data.put("USER_ID", userInfo.getString("USER_ID"));

        IDataset result = CSViewCall.call(this, "SS.DMTradeLockSVC.loadChildTradeInfo", data);

        data.put("IMEI", result.getData(0).getString("RES_CODE"));

        setBaseCommInfo(data);
        setUserInfo(userInfo);
        setCustInfo(custInfo);
    }

    /**
     * 提交时触发的方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData("baseCommInfo", true);

        String serialNum = getData().getString("AUTH_SERIAL_NUMBER");

        pageData.put("SERIAL_NUMBER", serialNum);

        IDataset ajaxDataset = CSViewCall.call(this, "SS.DMTradeLockSVC.submitTrade", pageData);

        setAjax(ajaxDataset);

    }

    public abstract void setBaseCommInfo(IData baseCommInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);
}
