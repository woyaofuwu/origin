
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;

public class CreditCrucialPromptTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals(tradeTypeCode, "7506") // 关键时刻缴费提醒
                || StringUtils.equals(tradeTypeCode, "7507") // 关键时刻GPRS流量上限提醒
                || StringUtils.equals(tradeTypeCode, "7508"))
        {
            btd.getMainTradeData().setSubscribeType("200");
        }

        if (StringUtils.equals("7506", tradeTypeCode) || StringUtils.equals("7507", tradeTypeCode))
        {
            btd.getMainTradeData().setInModeCode("1");
        }
    }

}
