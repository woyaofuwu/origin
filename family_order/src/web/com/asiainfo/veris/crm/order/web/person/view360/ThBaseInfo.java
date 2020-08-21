
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThBaseInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 用户基本信息
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

        IDataset userInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThBaseInfo", data);

        setCond(data);
        setBaseInfo(userInfo.first());

    }

    public abstract void setBaseInfo(IData baseInfo);

    public abstract void setCond(IData cond);
}
