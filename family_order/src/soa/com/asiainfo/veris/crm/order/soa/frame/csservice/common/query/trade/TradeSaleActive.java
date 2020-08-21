
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeSaleActive
{
	/**
	 * 根据tradeId查询所有的用户活动备份数据
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllTradeBakSaleActiveByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE_BAK", "SEL_ALL_BAK_BY_TRADE", params);
	}

	/**
	 * 根据trade_id,user_id,modify_tag查询营销活动台帐主表
	 * 
	 * @param strTradeId
	 * @param userId
	 * @param modifyTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeSaleActive(String strTradeId, String userId, String modifyTag) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", strTradeId);
		param.put("USER_ID", userId);
		param.put("MODIFY_TAG", modifyTag);
		IDataset dataset = Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_BY_TRADEID_USERID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		return dataset;
	}

	public static IDataset getTradeSaleActiveBook(String strTradeId, String userId, String modifyTag) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", strTradeId);
		param.put("USER_ID", userId);
		param.put("MODIFY_TAG", modifyTag);
		IDataset dataset = Dao.qryByCode("TF_B_TRADE_SALEACTIVE_BOOK", "SEL_BY_TRADEID_USERID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		return dataset;
	}

	public static IDataset getTradeSaleActiveByTrade(String TradeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("TRADE_ID", TradeId);
		IDataset staticResSet = Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_BY_FINISH", inparams);

		return staticResSet;

	}

	/**
	 * 根据tradeId查询所有的用户活动台账
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeSaleActiveByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_ALL_BY_TRADE", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
	
	public static IDataset getTradeSaleActiveByRelationTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_ALL_BY_RELATIONTRADE", params);
	}

	public static IDataset getTradeSaleActiveBookByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALEACTIVE_BOOK", "SEL_ALLBOOK_BY_TRADE", params,Route.getJourDb());
	}

	public static IDataset qrySaleGoods(String strTradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", strTradeId);
		param.put("MODIFY_TAG", "0");
		param.put("RES_TYPE_CODE", "4"); // 手机终端
		IDataset dataset = Dao.qryByCode("TF_B_TRADE_SALE_GOODS", "SEL_BY_RESTYPECODE", param);
		return dataset;
	}
	
	public static IDataset queryTradePreSaleActive(String tradeId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		param.put("TRADE_ID", tradeId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.PRODUCT_ID, A.PRODUCT_NAME, A.PACKAGE_ID, A.PACKAGE_NAME, A.START_DATE, A.END_DATE FROM TF_F_USER_SALEACTIVE_BOOK A where 1=1");
		parser.addSQL(" and  A.PROCESS_TAG = '0' ");
		parser.addSQL(" and  A.DEAL_STATE_CODE = '0' ");
		parser.addSQL(" and  NVL(A.RSRV_DATE2, A.END_DATE) > SYSDATE ");
		parser.addSQL(" and  A.ACCEPT_TRADE_ID = :TRADE_ID ");
		return Dao.qryByParse(parser, pagination);
	}
	
	/**
	 * 根据tradeId查询用户备份有效活动数据
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getValidSaleActiveBakByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE_BAK", "SEL_VALID_TRADESALEACTIVE_BAK", params);
	}
	
	/**
	 * 根据tradeId更新saleactive表的开始时间和结束时间
	 * 
	 * @param tradeId
	 * @return
	 * @author kangyt
	 * @throws Exception
	 */
	public static int updateSaleActiveStartEndDate(String tradeId,String userId,String start_date,String end_date) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		params.put("USER_ID", userId);
		params.put("START_DATE", start_date);
		params.put("END_DATE", end_date);
		return Dao.executeUpdateByCodeCode("TF_B_TRADE_SALE_ACTIVE", "UPD_SALEACTIVE_START_END_DATE", params);
	}
}
