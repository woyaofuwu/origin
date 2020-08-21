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
 * 对应 :USER_ID_A_SVC860
 * @author duhj
 *
 */
public class GetUserIdASvc860 implements ICallPfDeal{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = input.getString("TRADE_ID");
		
		IDataset tradeSvcs= Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_TRADE_SVC", input, Route.getJourDb());
		IDataset userOfferRels = TradeOfferRelInfoQry.getOfferRelByTradeId(tradeId);
		return OfferUtil.fillStructAndFilterForPf(tradeSvcs, userOfferRels);
	}
}
