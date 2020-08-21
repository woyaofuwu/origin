
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.filter;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;

public class ReturnActiveTradeNewCheckOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        List<ScoreTradeData> scoreTrade = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);

        String flag = input.getString("FLAG", "3");
        if ("3".equals(flag))
        {
            input.put("REMAIN_NUM", scoreTrade.get(0).getRsrvStr3());
            input.put("SCORE_VALUE", scoreTrade.get(0).getScore());
        }

        return input;
    }

}
