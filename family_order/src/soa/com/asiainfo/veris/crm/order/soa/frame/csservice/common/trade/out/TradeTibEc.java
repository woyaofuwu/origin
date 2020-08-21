
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeEcInfoQry;

public class TradeTibEc implements ITradeFinishAction
{

    private final static Logger logger = Logger.getLogger(TradeTibEc.class);

    // adcmas反向添加黑白名单 完工时修改TI_B_EC表的状态
    public void executeAction(IData mainTrade) throws Exception
    {
        String orderId = mainTrade.getString("ORDER_ID");

        // 完工时修改TI_B_EC表的状态
        TradeEcInfoQry.updateAdcMasEc(orderId);
    }
}
