package com.asiainfo.veris.crm.iorder.web.person.interroam.airline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class AirlinesRoamPercent extends PersonBasePage {
    /**
     * 查询用户绑定的资费信息
     *
     * @param cycle
     * @throws Exception
     */
    public void loadDiscntInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.AirlinesRoamPercentSVC.loadDiscntInfo", data);
        setDiscntInfos(dataset);
    }

    /**
     * 提交后处理函数loadDiscntInfo
     */
    public void onTradeSubmit(IRequestCycle requestCycle) throws Exception {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", ""))) {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = CSViewCall.call(this, "SS.AirlinesRoamPercentSVC.submit", data);
        setAjax(dataset);
    }

    public abstract void setDiscntInfos(IDataset infos);
}