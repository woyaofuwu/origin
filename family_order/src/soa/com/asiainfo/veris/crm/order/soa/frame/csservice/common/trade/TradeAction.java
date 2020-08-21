
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.TradeActionConfig;

public final class TradeAction
{
    private final static Logger logger = Logger.getLogger(TradeAction.class);

    public static void action(IData mainTrade, String actType) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("订单[" + actType + "]动作开始");
        }

        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        List<ITradeFinishAction> tfa = TradeActionConfig.getTradeFinishActions(tradeTypeCode, actType);

        if (tfa == null || tfa.size() == 0)
        {
            return;
        }

        for (int i = 0, isize = tfa.size(); i < isize; i++)
        {
            ITradeFinishAction action = tfa.get(i);

            action.executeAction(mainTrade);
        }

        return;
    }
}
