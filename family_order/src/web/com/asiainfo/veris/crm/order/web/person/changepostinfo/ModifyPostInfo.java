
package com.asiainfo.veris.crm.order.web.person.changepostinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifyPostInfo extends PersonBasePage
{
    private void getPostInfo(IData allInfo) throws Exception
    {
        IData data = CSViewCall.callone(this, "SS.ModifyPostInfoSVC.getPostInfo", allInfo);
        setOldPostInfo(data);
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData dataset = new DataMap();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        dataset.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        dataset.put("USER_INFO", userInfo);

        // 查询子业务信息
        getPostInfo(dataset);

    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("postinfo", true);

        IData param = new DataMap();
        IData data2 = getData();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.putAll(data);
        param.putAll(data2);

        IDataset dataset = CSViewCall.call(this, "SS.ModifyPostInfoRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setOldPostInfo(IData oldPostInfo);
}
