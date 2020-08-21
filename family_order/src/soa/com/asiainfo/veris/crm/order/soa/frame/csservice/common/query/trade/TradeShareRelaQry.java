
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeShareRelaQry
{
	
	public static IDataset getTradeShareRelaByTradeId(String tradeId) throws Exception
    {
    	IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_TRADE_SHARE_RELA", "SEL_BY_PK", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
}
