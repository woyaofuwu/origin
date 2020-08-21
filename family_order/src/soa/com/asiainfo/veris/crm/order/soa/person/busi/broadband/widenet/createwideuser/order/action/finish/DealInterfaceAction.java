package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInterfaceInfoQry;

/**
 * 完工前将ti_b_trade_interface中Y状态的置为0状态
 * 
 * @author songlm
 */
public class DealInterfaceAction implements ITradeFinishAction
{	 
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID");
    	IData param = new DataMap();
    	param.put("TRADE_ID", tradeId);
    	TradeInterfaceInfoQry.updateInterfaceStateByTradeId(param);
    }
}
