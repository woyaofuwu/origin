
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThContactInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 联系信息
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
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        IDataset contactInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThContactInfo", data);
        IDataset assureInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThAssureInfo", data);

        setContactInfo(contactInfo.first());
        setAssureInfo(assureInfo.first());
    }

    public abstract void setAssureInfo(IData assureInfo);

    public abstract void setContactInfo(IData contactInfo);
}
