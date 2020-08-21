
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmtradeupd;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DMTradeUpd extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(DMTradeUpd.class);

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        IData custInfo = new DataMap(pageData.getString("CUST_INFO"));
        IData vipInfo = new DataMap(pageData.getString("VIP_INFO"));

        IData data = new DataMap();
        data.put("VIP_INFO", vipInfo);
        data.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        data.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
        data.put("USER_ID", userInfo.getString("USER_ID"));

        IDataset result = CSViewCall.call(this, "SS.DMCommTradeSVC.loadUpdChildTradeInfo", data);

        String oldRes = result.getData(0).getString("RES_CODE");

        userInfo.put("OLD_RES_CODE", oldRes);

        setVipInfo(vipInfo);
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
        IData pageData = getData();
        String serialNum = pageData.getString("AUTH_SERIAL_NUMBER");
        String resCode = pageData.getString("RES_CODE");

        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNum);
        data.put("RES_CODE", resCode);
        data.put("UPDATE_TAG", "01");// 01-根据号码变更IMEI号
        data.put("DM_TAG", "DMUPD");

        IDataset result = CSViewCall.call(this, "SS.DMTradeBusiRegSVC.tradeReg", data);

        setAjax(result);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setVipInfo(IData vipInfo);
}
