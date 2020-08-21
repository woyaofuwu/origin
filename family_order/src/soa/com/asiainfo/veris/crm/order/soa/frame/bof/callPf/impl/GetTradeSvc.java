package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;

/**
 * 
 * :TF_B_TRADE_SVC
 *
 */
public class GetTradeSvc implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = input.getString("TRADE_ID");
		
		IDataset tradeSvcs = Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_PF", input, Route.getJourDb());
		IDataset tradeOfferRels = TradeOfferRelInfoQry.getOfferRelByTradeId(tradeId);
		OfferUtil.fillTradeOffersWithUserOfferRel(tradeSvcs, tradeOfferRels);
		return OfferUtil.fillStructAndFilterForPf(tradeSvcs, tradeOfferRels);
	}

}
