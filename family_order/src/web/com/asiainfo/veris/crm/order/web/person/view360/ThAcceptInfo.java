
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThAcceptInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 受理信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        // 判断TRADE_ID是否存在
        String trade_id = data.getString("TRADE_ID", "").trim();
        if (StringUtils.isBlank(trade_id) || "null".equals(trade_id))
        {
            return;
        }

        IDataset acceptInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThAcceptInfo", data);

        setCond(data);
        setAcceptInfo(acceptInfo.first());
    }

    public abstract void setAcceptInfo(IData acceptInfo);

    public abstract void setCond(IData cond);
}
