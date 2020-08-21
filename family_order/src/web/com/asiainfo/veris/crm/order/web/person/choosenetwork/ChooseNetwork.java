
package com.asiainfo.veris.crm.order.web.person.choosenetwork;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChooseNetwork extends PersonBasePage
{
    /**
     * 点击查询后加载页面所需的信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData params = getData();

        IDataset results = CSViewCall.call(this, "SS.ChooseNetworkSVC.getCommpara", params);

        setCommInfo(results.getData(0));
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
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.ChooseNetworkRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData comminfo);

    public abstract void setInfo(IData info);
}
