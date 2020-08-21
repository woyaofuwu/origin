package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateZCRequestData;

public class ScoreDonateZCTrade extends BaseTrade implements ITrade{
	
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception{
		createDonateZCMain(btd);
		createDonateZCScore(btd);
	}
	
	private void createDonateZCMain(BusiTradeData btd) throws Exception{
		ScoreDonateZCRequestData reqData = (ScoreDonateZCRequestData)btd.getRD();
		MainTradeData mainList = btd.getMainTradeData();
		mainList.setRsrvStr5(reqData.getOBJECT_SERIAL_NUMBER());
		mainList.setRsrvStr6(reqData.getDONATE_SCORE());
		mainList.setRsrvStr7(reqData.getVALID_DATE());
		mainList.setRsrvStr8(reqData.getSCORE_TYPE_CODE());
		mainList.setRsrvStr9(reqData.getA_TRADE_TYPE_CODE());
		mainList.setRsrvStr10(reqData.getB_TRADE_TYPE_CODE());
	}
	
	private void createDonateZCScore(BusiTradeData btd) throws Exception{
		ScoreDonateZCRequestData reqData = (ScoreDonateZCRequestData)btd.getRD();
		ScoreTradeData scoreTD = new ScoreTradeData();
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		String scoreTypeCode = reqData.getSCORE_TYPE_CODE(); // 获取积分类型
		scoreTD.setUserId(reqData.getUca().getUser().getUserId());
        scoreTD.setSerialNumber(reqData.getUca().getUser().getSerialNumber());
        scoreTD.setIdType("0");
        scoreTD.setScoreTypeCode(scoreTypeCode);
        scoreTD.setYearId("ZZZZ");
        scoreTD.setStartCycleId("-1");
        scoreTD.setEndCycleId("-1");
        scoreTD.setScore(reqData.getSCORE());
        scoreTD.setScoreChanged("-" + reqData.getDONATE_SCORE());
        scoreTD.setValueChanged("0");
        scoreTD.setScoreTag("1");
        scoreTD.setRuleId("");
        scoreTD.setActionCount("");
        scoreTD.setResId("");
        scoreTD.setGoodsName("");
        scoreTD.setCancelTag("0");
        scoreTD.setRemark(reqData.getRemark());
        btd.add(serialNumber, scoreTD);
	}
}