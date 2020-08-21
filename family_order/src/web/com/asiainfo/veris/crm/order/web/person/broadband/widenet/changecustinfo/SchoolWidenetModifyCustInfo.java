
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.changecustinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 */
public abstract class SchoolWidenetModifyCustInfo extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        setWideInfo(dataset.getData(0));
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
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.WidenetModifyCustInfoRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setInfo(IData info);

    public abstract void setWideInfo(IData wideInfo);

}
