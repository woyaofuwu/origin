
package com.asiainfo.veris.crm.order.web.person.MobileSpecialepay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MobileSpecialepay extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData Info = CSViewCall.callone(this, "SS.MobileSpecialepaySVC.getInfo", data);

        setInfo(Info);

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

        IDataset dataset = CSViewCall.call(this, "SS.MobileSpecialepayRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    

    public abstract void setInfo(IData custInfo);

}
