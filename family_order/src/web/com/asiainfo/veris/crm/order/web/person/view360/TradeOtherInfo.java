
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TradeOtherInfo extends PersonBasePage
{
    /**
     * 业务历史信息 -- 其他台账信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String trade_id = data.getString("TRADE_ID", "").trim();
        if (StringUtils.isBlank(trade_id) || "null".equals(trade_id))
        {
            return;
        }
        IDataset tradeOtherInfo = CSViewCall.call(this, "SS.GetUser360THViewSVC.qryTradeOtherInfo", data);

        setInfos(tradeOtherInfo);
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);

}
