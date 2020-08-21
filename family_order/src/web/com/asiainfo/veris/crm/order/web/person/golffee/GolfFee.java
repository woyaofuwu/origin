
package com.asiainfo.veris.crm.order.web.person.golffee;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GolfFee extends PersonBasePage
{
    public abstract IData getInfo();

    /**
     * 加载业务展示数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadTradeInfo(IRequestCycle cycle) throws Exception
    {
        setInfo(getData());
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
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        data.remove("AUTH_SERIAL_NUMBER");
        IDataset dataset = CSViewCall.call(this, "SS.GolfFeeRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setInfo(IData info);

}
