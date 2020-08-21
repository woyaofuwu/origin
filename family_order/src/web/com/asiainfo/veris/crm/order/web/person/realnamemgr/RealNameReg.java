
package com.asiainfo.veris.crm.order.web.person.realnamemgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RealNameReg extends PersonBasePage
{
    /**
     * 重写提交方法
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put("PSPT_TYPE_CODE", getData().getString("custInfo_PSPT_TYPE_CODE"));
        data.put("CUST_NAME", getData().getString("custInfo_CUST_NAME"));
        data.put("PSPT_ID", getData().getString("custInfo_PSPT_ID"));
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.RealNameRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);
}
