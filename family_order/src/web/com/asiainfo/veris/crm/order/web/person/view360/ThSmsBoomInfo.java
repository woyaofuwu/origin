package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ThSmsBoomInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 短信炸弹受理信息
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

        String trade_type_code = data.getString("TRADE_TYPE_CODE", "");

        if ("".equals(trade_type_code) || (!"2482".equals(trade_type_code) && !"2482".equals(trade_type_code) && !"2482".equals(trade_type_code)))
        {
            return;
        }

        IDataset ThSmsBoomInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryThSmsBoomInfo", data);

        setTradeTypeCode(trade_type_code);
        setCond(data);
        setInfos(ThSmsBoomInfo);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);

    public abstract void setTradeTypeCode(String tradeTypeCode);
}

