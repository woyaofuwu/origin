
package com.asiainfo.veris.crm.order.soa.person.busi.altsnmgr.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.altsnmgr.order.requestdata.GenScoreHisReqData;

public class GenScoreHisTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        GenScoreHisReqData reqData = (GenScoreHisReqData) bd.getRD();

        createTradeScore(bd);

    }

    private void createTradeScore(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        GenScoreHisReqData reqData = (GenScoreHisReqData) bd.getRD();
        String tradeId = bd.getTradeId();
        ScoreTradeData scoreTradeData = new ScoreTradeData();

        scoreTradeData.setUserId(reqData.getUca().getUserId());
        scoreTradeData.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreTradeData.setIdType("0");
        scoreTradeData.setScoreTypeCode("ZZ");
        scoreTradeData.setYearId("ZZZZ");
        scoreTradeData.setStartCycleId("-1");
        scoreTradeData.setEndCycleId("-1");
        scoreTradeData.setScore(reqData.getScoreValue());
        scoreTradeData.setScoreChanged(reqData.getScoreChanged());
        scoreTradeData.setValueChanged(reqData.getValueChanged());
        scoreTradeData.setScoreTag("2");// 不需要变更积分
        scoreTradeData.setRuleId("");
        scoreTradeData.setActionCount("");
        scoreTradeData.setResId("");
        scoreTradeData.setGoodsName("");
        scoreTradeData.setCancelTag("0");
        scoreTradeData.setRemark("帐务写改号积分转移轨迹");
        scoreTradeData.setRsrvStr1("");

        bd.add(reqData.getUca().getSerialNumber(), scoreTradeData);

    }

}
