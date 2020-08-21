/* $Id: ChangeTeleNbr.java,v 1.2 2010/12/07 09:33:15 xiongj Exp $ */

package com.asiainfo.veris.crm.order.web.person.changetelenbr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangeTeleNbr extends PersonBasePage
{

    /**
     * 查询改号通知费
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxChooseNotice(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        // ChangeTeleNbrBean bean = new ChangeTeleNbrBean();
        // IDataset dataset = bean.ajaxChooseNotice(pd, data);
        IDataset dataset = CSViewCall.call(this, "SS.ChangeTeleNbr.chooseTeleInfo", data);

        if (dataset == null || dataset.isEmpty())
        {
            dataset = new DatasetList();
        }

        this.setAjax(dataset);
    }

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
        IDataset result = CSViewCall.call(this, "SS.ChangTeleNbr.tradeReg", data);
        this.setAjax(result);
    }

    public abstract void setInfo(IData info);
}
