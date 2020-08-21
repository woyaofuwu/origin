package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;

/**
 * 拆分sql取数据
 * 对应 :TF_B_TRADE_DISCNT,TF_F_USER_DISCNT
 * @author duhj
 *
 */
public class GetTradeDiscntPwlw implements ICallPfDeal
{
	
	private static transient Logger logger = Logger.getLogger(GetTradeDiscntPwlw.class);
	
	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		logger.info("GetTradeDiscntPwlw--executeAction--input--toString--" + input.toString());
		String strTradeId = input.getString("TRADE_ID");
		String strUserId = input.getString("USER_ID");
		IDataset tradeDiscnts = new DatasetList();
		IDataset idsTradeDiscnt = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_PF_PWLW", input, Route.getJourDb());
		if(IDataUtil.isNotEmpty(idsTradeDiscnt))
		{
			logger.info("GetTradeDiscntPwlw--executeAction--idsTradeDiscnt--toString--" + idsTradeDiscnt.toString());
			IData idInput = new DataMap();
			idInput.put("USER_ID", strUserId);
			IDataset idsUserDiscnt = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_PF_PWLW", idInput, BizRoute.getRouteId());
			if(IDataUtil.isNotEmpty(idsUserDiscnt))
			{
				logger.info("GetTradeDiscntPwlw--executeAction--idsUserDiscnt--toString--" + idsUserDiscnt.toString());
				for (int i = 0; i < idsUserDiscnt.size(); i++) 
				{
					IData idUserDiscnt = idsUserDiscnt.getData(i);
					String strInstId = idUserDiscnt.getString("INST_ID", "");
					String strDiscntCode = idUserDiscnt.getString("DISCNT_CODE", "");
					for (int j = 0; j < idsTradeDiscnt.size(); j++) 
					{
						IData idAdd = idsTradeDiscnt.getData(j);
						String strTInstId = idAdd.getString("INST_ID", "");
						String strTDiscntCode = idAdd.getString("DISCNT_CODE", "");
						if(strInstId.equals(strTInstId) && strDiscntCode.equals(strTDiscntCode))
						{
							logger.info("GetTradeDiscntPwlw--executeAction--idAdd--toString--" + idAdd.toString());
							tradeDiscnts.add(idAdd);
						}
					}
				}
			}
		}
		logger.info("GetTradeDiscntPwlw--executeAction--tradeDiscnts--toString--" + tradeDiscnts.toString());
		IDataset tradeOfferRels = TradeOfferRelInfoQry.getOfferRelByTradeId(strTradeId);
		logger.info("GetTradeDiscntPwlw--executeAction--tradeOfferRels--toString--" + tradeOfferRels.toString());
		IDataset elements = OfferUtil.fillStructAndFilterForPf(tradeDiscnts, tradeOfferRels);
		logger.info("GetTradeDiscntPwlw--executeAction--elements--toString--" + elements.toString());
		return elements;
	}
}
