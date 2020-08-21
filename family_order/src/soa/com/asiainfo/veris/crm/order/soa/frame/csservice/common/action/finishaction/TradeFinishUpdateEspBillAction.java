package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class TradeFinishUpdateEspBillAction implements ITradeFinishAction {

	private static final Logger logger = Logger
			.getLogger(TradeFinishUpdateEspBillAction.class);

	public void executeAction(IData mainTrade) throws Exception {
		String tradeId = mainTrade.getString("TRADE_ID");
		String userid = mainTrade.getString("USER_ID");
		IDataset otherTrades = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
        if (IDataUtil.isEmpty(otherTrades))
            return;

        IData otherTrade = otherTrades.getData(0);
        String productSubsID = otherTrade.getString("RSRV_STR4");//产品实例ID
        if(StringUtils.isNotBlank(productSubsID)){
	        IData param = new DataMap();
	        param.put("USER_ID", userid);
	        param.put("RSRV_NUM3", productSubsID);
	        SQLParser parser = new SQLParser(param);
	        parser.addSQL("UPDATE TS_B_ESPDW_SRECVBILL SET USER_ID = :USER_ID, RSRV_INFO3='4684工单完工回写USER_ID' WHERE RSRV_NUM3 = :RSRV_NUM3");
			Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
        }
	}
}
