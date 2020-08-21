package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IPrintFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.TradeActionConfig;

public final class PrintAction
{
	private final static Logger logger = Logger.getLogger(PrintAction.class);

    public static void action(IData mainTrade) throws Exception
    {

        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        List<IPrintFinishAction> tfa = TradeActionConfig.getPrintFinishActions(tradeTypeCode);

        if (tfa == null || tfa.size() == 0)
        {
            return;
        }

        for (int i = 0, isize = tfa.size(); i < isize; i++)
        {
        	IPrintFinishAction action = tfa.get(i);

            action.executeAction(mainTrade);
        }

        return;
    }
}
