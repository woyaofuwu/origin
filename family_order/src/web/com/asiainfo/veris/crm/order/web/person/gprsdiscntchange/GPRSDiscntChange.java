
package com.asiainfo.veris.crm.order.web.person.gprsdiscntchange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GPRSDiscntChange extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        CSViewCall.call(this, "SS.GPRSDiscntChangeSVC.CheckUserGPRSInfo", userInfo);

        IDataset userGPRSInfos = CSViewCall.call(this, "SS.GPRSDiscntChangeSVC.QryUserGPRSDiscntInfos", userInfo);

        IDataset allGPRSInfos = CSViewCall.call(this, "SS.GPRSDiscntChangeSVC.QryAllGPRSDiscntInfos", userInfo);

        setUserInfo(userInfo);
        setAllGPRSInfos(allGPRSInfos);
        setUserGPRSInfos(userGPRSInfos);
        setAjax(userInfo);
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

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.put("REMARK", getData().getString("REMARK"));
        param.putAll(data);

        IDataset dataset = CSViewCall.call(this, "SS.GPRSDiscntChangeRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setAllGPRSInfos(IDataset allGPRSInfos);

    public abstract void setUserGPRSInfos(IDataset userGPRSInfos);

    public abstract void setUserInfo(IData userInfo);
}
