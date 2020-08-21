package com.asiainfo.veris.crm.iorder.soa.family.common.action;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.requestdata.TopSetBoxRequestData;

/**
 *
 * 家庭魔百和新开依赖关系处理
 * @author zhangxi
 *
 */
public class DealFamilyTvRelyOnAction implements ITradeAction {

	@Override
	public void executeAction(@SuppressWarnings("rawtypes") BusiTradeData btd) throws Exception {

		TopSetBoxRequestData tsbReqData = (TopSetBoxRequestData) btd.getRD();
		String tradeId48 = tsbReqData.getWideTradeId();

		//1.先查4800那笔工单  是否有依赖的工单
		IDataset limitTrades = TradeLimitInfoQry.getLimitedByTradeId(tradeId48);

		//若4800有依赖工单，则插入3800依赖工单
		if (IDataUtil.isNotEmpty(limitTrades)){

			DataMap limitTrade = (DataMap) limitTrades.first();

			String tradeId38 = btd.getTradeId();
			String limitTradeId = limitTrade.getString("LIMIT_TRADE_ID", "");
			String eparchyCode = btd.getRD().getUca().getUserEparchyCode();

			FamilyBusiRegUtil.insertTradeLimit(tradeId38, limitTradeId, eparchyCode);
		}

	}

}
