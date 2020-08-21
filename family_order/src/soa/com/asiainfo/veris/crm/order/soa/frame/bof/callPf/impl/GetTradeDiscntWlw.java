package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;

/**
 * 拆分sql取数据
 * 对应 :TF_B_TRADE_DISCNT_WLW
 * @author duhj
 *
 */
public class GetTradeDiscntWlw implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = input.getString("TRADE_ID");
		
		IDataset tradeDiscnts = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_PF", input, Route.getJourDb());
		IDataset tradeOfferRels = TradeOfferRelInfoQry.getOfferRelByTradeId(tradeId);
		OfferUtil.fillTradeOffersWithUserOfferRel(tradeDiscnts, tradeOfferRels);
		return OfferUtil.fillStructAndFilterForPf(tradeDiscnts, tradeOfferRels);
	}
}
