
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class THTradePreSaleActive extends PersonBasePage
{
    /**
     * 业务历史信息 -- 查询预约营销活动信息
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
        IDataOutput saleifno = CSViewCall.callPage(this, "SS.GetUser360THViewSVC.queryTradePreSaleActive", data, getPagination("TradePreSaleInfoNav"));
        if(IDataUtil.isNotEmpty(saleifno.getData()))
        {
            setInfos(saleifno.getData());
            setInfosCount(saleifno.getDataCount());
        }

    }

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
