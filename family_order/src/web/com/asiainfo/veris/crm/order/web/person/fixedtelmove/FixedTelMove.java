
package com.asiainfo.veris.crm.order.web.person.fixedtelmove;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FixedTelMove extends PersonBasePage
{

    public void loadTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("USER_ID", data.getString("USER_ID"));
        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));

        IData installInfo = CSViewCall.call(this, "SS.FixTelUserMoveSVC.loadTradeInfo", param).getData(0);
        installInfo.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        setInfo(installInfo);
    }

    public void onSubmitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset result = CSViewCall.call(this, "SS.FixTelUserMoveRegSVC.tradeReg", data);
        this.setAjax(result);
    }

    public abstract void setInfo(IData info);
}
