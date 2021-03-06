
package com.asiainfo.veris.crm.order.web.person.interboss.dm.dmtrademodify;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DMTradeUpdByImei extends PersonBasePage
{
    /**
     * 子类重写，输入号码后，设置子类查询信息，不需要处理时可以留空
     * 
     * @param cycle
     * @throws Exception
     */
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

        IDataset result = CSViewCall.call(this, "SS.DMTradeUpdByImeiSVC.loadChildTradeInfo", data);

        if (result != null && result.size() > 0)
        {
            CSViewException.apperr(DMBusiException.CRM_DM_128);
        }

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
        IData baseCommInfo = getData("baseCommInfo", true);

        String serialNum = getData().getString("AUTH_SERIAL_NUMBER");

        IData data = new DataMap();

        data.put("RES_CODE", baseCommInfo.getString("IMEI"));
        data.put("SERIAL_NUMBER", serialNum);
        data.put("UPDATE_TAG", "02"); // 根据IMEI号变更手机号码
        data.put("DM_TAG", "DMUPD");

        IDataset result = CSViewCall.call(this, "SS.DMTradeBusiRegSVC.tradeReg", data);

        setAjax(result);
    }

    public abstract void setBaseCommInfo(IData baseCommInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setVipInfo(IData vipInfo);
}
