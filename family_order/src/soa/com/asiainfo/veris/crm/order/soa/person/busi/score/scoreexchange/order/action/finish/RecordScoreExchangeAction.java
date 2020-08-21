package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;

public class RecordScoreExchangeAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId=mainTrade.getString("TRADE_ID");
		String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
		String acceptDate=mainTrade.getString("ACCEPT_DATE");
		String acceptMonth=mainTrade.getString("ACCEPT_MONTH");
		String userId=mainTrade.getString("USER_ID");
		String eparchyCode=mainTrade.getString("EPARCHY_CODE");
		String ruleId="";
		
		IDataset set=TradeScoreInfoQry.queryTradeScoreJoinExchangeRule(tradeId);
		if(IDataUtil.isNotEmpty(set)){
			
			String tradeName="";
			if(tradeTypeCode.equals("330")){
				tradeName="积分兑换";
			}
			
			ruleId=set.getData(0).getString("RULE_ID","");
			
			//针对积分营销活动
			if(!ruleId.equals("")){
				IData param=new DataMap();
				param.put("TRADE_ID",tradeId);
				param.put("TRADE_NAME",tradeName);
				param.put("TRADE_TYPE",tradeTypeCode);
				param.put("ACCEPT_MONTH",acceptMonth);
				param.put("TRADE_ELEMENT",ruleId);
				param.put("USER_ID",userId);
				param.put("CURRENT_AMOUNT","");
				param.put("MAX_AMOUNT","");
				param.put("ACCEPT_TIME",acceptDate);
				param.put("EPARCHY_CODE",eparchyCode);
				param.put("RSRV_STR1","");
				param.put("RSRV_STR2","");
				param.put("RSRV_STR3","");
				param.put("RSRV_STR4","");
				param.put("RSRV_STR5","");
				
				TradeScoreInfoQry.saveTradeScoreRecode(param);
			}
		}
		
	}
	
}
