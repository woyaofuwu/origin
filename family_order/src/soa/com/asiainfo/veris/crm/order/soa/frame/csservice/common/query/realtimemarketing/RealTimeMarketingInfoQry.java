package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.realtimemarketing;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RealTimeMarketingInfoQry
{

	public static IDataset getRealTimeMarketingBySToS(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_STAFF_SERIAL", data);
	}

	public static IDataset getRealTimeMarketingBySToSS(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_STAFF_SERIALS", data);
	}
	
	public static IDataset getRealTimeMarketingBytradeId(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINGTRADEID", data);
	}
	
	public static IDataset getRealTimeMarketingTrade(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINGTRADE", data);
	}

	public static IDataset getRealTimeMarketingTradeByRID(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINGRID", data);
	}

	public static IDataset getRealTimeMarketingTradeByRID2(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINGRID2", data);
	}

	public static IDataset getRealTimeMarketingByUST(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINGUST", data);
	}
	public static IDataset getRealTimeMarketingByUST_1(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINGUST_1", data);
	}
	
	public static IDataset getRealTimeMarketingByHUST(IData data) throws Exception
	{
		return Dao.qryByCodeParser("TL_O_REALTIMEMARKETING", "SEL_BY_REALTIMEMARKETINHGUST", data);
	}
	
	public static void uprealtimemarketinG(IData data) throws Exception
	{
		Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "UP_PROCESSTAG_REQID", data);
	}
	
	public static void uprealtimemarketinG2(IData data) throws Exception
	{
		Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "UP_PROCESSTAG2_REQID", data);
	}

	public static void uprealtimemarketinG3(IData data) throws Exception
	{
		Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "UP_PROCESSTAG3_REQID", data);
	}
	
	public static void uprealtimemarketinTrade(IData data) throws Exception
	{
		Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "UP_RTMKT_REQID", data);
		if ("1".equals(data.getString("STATUS")))
		{
			Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "UP_RTMKT_REQIDRSRV1", data);
		} else if ("2".equals(data.getString("STATUS")))
		{
			Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "UP_RTMKT_REQIDRSRV2", data);
		}
	}
	
}
