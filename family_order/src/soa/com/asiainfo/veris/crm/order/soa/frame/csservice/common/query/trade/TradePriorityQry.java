package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradePriorityQry 
{

	  public static int getPriorityOrdersCountByState(String state) throws Exception
	  {
	        IData params = new DataMap();
	        params.put("SUBSCRIBE_STATE", state);
	        IDataset priorities = Dao.qryByCode("TF_B_TRADE_PRIORITY", "SEL_NOTSAME_ORDES_BY_STATE", params,Route.getJourDb());
	        return priorities.getData(0).getInt("CNT");
	  }
	  
	  public static IDataset getPriorityOrders(String orderId,String rOrderId) throws Exception
	  {
		  IData params = new DataMap();
		  params.put("ORDER_ID",orderId);
		  params.put("RELAT_ORDER_ID", rOrderId);
		  IDataset priorities = Dao.qryByCode("TF_B_TRADE_PRIORITY", "SEL_PRIORITY_ORDER_BY_PK", params,Route.getJourDb());
		  return priorities;
		  
	  }
	  
	  public static IDataset getEarlyTrades(ArrayList<String> userids, String execTime, String orderId) throws Exception
	  {
		   	if(userids.size() == 0)
		   	{
		   		return null;
		   	}
		   	
		   	String suids = "";
		   	for(int i=0; i<userids.size(); i++)
		   	{
		   		if(i<userids.size()-1)
		   		{
		   			suids = suids + userids.get(i) + ",";
		   		}
		   		else
		   		{
		   			suids = suids + userids.get(i) ;
		   		}
		   	}
		   	
		  	IData param = new DataMap();
			param.put("EXEC_TIME", execTime);
			param.put("ORDER_ID", orderId);

			StringBuilder sql = new StringBuilder(1000);

			sql.append("SELECT * ");
			sql.append("FROM TF_B_TRADE T ");
			sql.append("WHERE T.USER_ID IN (" + suids + ") ");
			sql.append("AND T.EXEC_TIME < TO_DATE(:EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
			//sql.append("AND T.OLCOM_TAG = '1' ");
			sql.append("AND T.SUBSCRIBE_STATE IN ('0', 'F', 'M') ");
			sql.append("AND T.ORDER_ID <> :ORDER_ID ");
			sql.append("AND T.SUBSCRIBE_TYPE <> '600' ");
			sql.append("ORDER BY T.EXEC_TIME");

			return Dao.qryBySql(sql, param);
	  }

	public static void insTradePriority(String fOrderId, String fTradeId,
			String fTradeTypeCode, String fAcceptMon, String fExecTime,
			String rOrderId, String rTradeId, String rTradeTypeCode, String iNIT)  throws Exception {
		
		 IData params = new DataMap();
	     params.put("ORDER_ID", fOrderId);
	     params.put("TRADE_ID", fTradeId);
	     params.put("TRADE_TYPE_CODE", fTradeTypeCode);
	     params.put("ACCEPT_MONTH", fAcceptMon);
	     params.put("EXEC_TIME", fExecTime);
	     params.put("RELAT_ORDER_ID", rOrderId);
	     params.put("RELAT_TRADE_ID", rTradeId);
	     params.put("RELAT_TRADE_TYPE_CODE", rTradeTypeCode);
	     params.put("SUBSCRIBE_STATE", iNIT);
	     
	     Dao.executeUpdateByCodeCode("TF_B_TRADE_PRIORITY", "INS_PRIORITY", params,Route.getJourDb());
	}

	public static void insH(String rtradeId)  throws Exception 
	{
		
		 IData params = new DataMap();
	     params.put("RELAT_TRADE_ID", rtradeId);
	     Dao.executeUpdateByCodeCode("TF_B_TRADE_PRIORITY", "INS_H_PRIORITY_BY_RELAT_TRADE", params,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	public static void del(String rtradeId) throws Exception 
	{
		IData params = new DataMap();
	     params.put("RELAT_TRADE_ID", rtradeId);
	     Dao.executeUpdateByCodeCode("TF_B_TRADE_PRIORITY", "DEL_PRIORITY_BY_RELAT_TRADE", params,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}
	 
	
	public static void updStateByTrade(String tradeId, String state)  throws Exception 
	{
		IData params = new DataMap();
	    params.put("RELAT_TRADE_ID", tradeId);
	    params.put("SUBSCRIBE_STATE", state);
	    Dao.executeUpdateByCodeCode("TF_B_TRADE_PRIORITY", "UPD_STATE_BY_TRADE", params,Route.getJourDb());
	}
	  
	public static void updStateByOrder(String orderId, String state)  throws Exception 
	{
		IData params = new DataMap();
	    params.put("ORDER_ID", orderId);
	    params.put("SUBSCRIBE_STATE", state);
	    Dao.executeUpdateByCodeCode("TF_B_TRADE_PRIORITY", "UPD_STATE_BY_ORDER", params,Route.getJourDb());
	}
	  
}
