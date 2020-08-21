package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePriorityQry;

public class TradePriority 
{
	
	public static String INIT                = "0";
	public static String PF_OK            = "1";
	public static String ARCH_OK       = "1";
	public static String PF_FAIL           = "3";
	public static String ARCH_FAIL      = "6";
	public static String BACK_PF_FAIL = "M";
	
	public static String LIMITED_ORDER_STATE = "0,L,N,B";
	
	
	public static boolean isOrderPriorityNumLimited(String state)  throws Exception
	{
		int priorityLen = TradeCtrl.getCtrlInt("-1", TradeCtrl.CTRL_TYPE.PRIORYTYQUELEN, 20);
		int actPriorityLen = TradePriorityQry.getPriorityOrdersCountByState(state);
		return actPriorityLen > priorityLen;
	}
	
	public static boolean isOrderPriorityNotNumLimited(String state)  throws Exception
	{
		return !isOrderPriorityNumLimited(state);
	}
	
	public static IDataset getFirstOrder(IData order, IDataset tradeAll)  throws Exception
	{
		if (IDataUtil.isEmpty(tradeAll))
        {
            return null;
        }
        String orderId    = order.getString("ORDER_ID");
        String execTime = order.getString("EXEC_TIME");
        ArrayList<String> userids = new ArrayList<String>();
        for (int i = 0, isize = tradeAll.size(); i < isize; i++)
        {
            userids.add(tradeAll.getData(i).getString("USER_ID"));
        }
		IDataset earlyTrades = TradePriorityQry.getEarlyTrades(userids, execTime, orderId);
		if (IDataUtil.isEmpty(earlyTrades))
        {
            return null;
        }
		return earlyTrades;
	}

	public static void insTradePriority(IData firstOrder, IDataset tradeAll) throws Exception
	{
		 for (int i = 0, isize = tradeAll.size(); i < isize; i++)
	     {
			 String fOrderId = firstOrder.getString("ORDER_ID");
			 String fTradeId = "";
			 String fTradeTypeCode = firstOrder.getString("TRADE_TYPE_CODE");
			 String fAcceptMon = firstOrder.getString("ACCEPT_MONTH");
			 String fExecTime = firstOrder.getString("EXEC_TIME");
			 String rOrderId = tradeAll.getData(i).getString("ORDER_ID");
			 String rTradeId = tradeAll.getData(i).getString("TRADE_ID");
			 String rTradeTypeCode = tradeAll.getData(i).getString("TRADE_TYPE_CODE");
	         TradePriorityQry.insTradePriority(fOrderId, fTradeId, fTradeTypeCode, fAcceptMon, fExecTime, rOrderId, rTradeId, rTradeTypeCode, TradePriority.INIT);
	     }
	}

	public static boolean isNotLimited(IData firstOrder)  throws Exception
	{
		IData order = TradeInfoQry.getOrderByOrderId(firstOrder.getString("ORDER_ID"));
		if (IDataUtil.isEmpty(order)){
			return false;
		}
		return LIMITED_ORDER_STATE.indexOf(order.getString("ORDER_STATE"))>-1 ;
	}
	public static boolean isNotExistsPriority(IData firstOrder,String rOrderId)  throws Exception
	{
		boolean flag = true;
		String orderId = firstOrder.getString("ORDER_ID");
		if (IDataUtil.isNotEmpty(TradePriorityQry.getPriorityOrders(orderId, rOrderId))){
			flag = false;
		}
		return  flag;
	}

	public static void moveTrades(String rtradeId) throws Exception
	{
		TradePriorityQry.insH(rtradeId);
		TradePriorityQry.del(rtradeId);
	}
	
	public static void updStateByOrder(String orderId, String state) throws Exception
	{
		TradePriorityQry.updStateByOrder(orderId, state);
	}
	
	public static void updStateByTrade(String rtradeId, String state) throws Exception
	{
		TradePriorityQry.updStateByTrade(rtradeId, state);
	}
	
}
