package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetrescheduled.widenetqueryorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class WideNetQueryOrderIntfBean extends CSBizBean {

	//查询主台账表
	public IDataset queryOrderInfo(IData input) throws Exception {
		return TradeInfoQry.getMainTradeByTradeId(input.getString("TRADE_ID"));
	}
}
