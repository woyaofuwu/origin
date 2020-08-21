
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctday.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.changeacctday.order.requestdata.ChangeAcctDayRequestData;

public class ChangeAcctDayTrade extends BaseTrade implements ITrade
{
    /**
     * 创建业务台账
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ChangeAcctDayRequestData reqData = (ChangeAcctDayRequestData) btd.getRD();

        // 设置需要变更的账期List值
        btd.addChangeAcctDayData(reqData.getUca().getUserId(), reqData.getNewAcctDay());
    }
}
