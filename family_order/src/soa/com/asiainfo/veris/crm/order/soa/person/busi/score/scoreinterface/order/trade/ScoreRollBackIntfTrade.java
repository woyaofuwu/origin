
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreRollBackRequestData;

public class ScoreRollBackIntfTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ScoreRollBackRequestData reqData = (ScoreRollBackRequestData) bd.getRD();
        // 判断是否有积分异动的未完工工单
        IDataset scoreChangeData = TradeInfoQry.queryScoreChangeTrade(reqData.getUca().getSerialNumber());
        if (IDataUtil.isNotEmpty(scoreChangeData))
        {
            // 业务受理前条件判断-该用户还有未完工工单涉及积分异动，积分业务不能继续!CRM_USER_1122
            CSAppException.apperr(CrmUserException.CRM_USER_1122);
        }

        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String score = scoreInfo.getData(0).getString("SUM_SCORE");
        IData param = new DataMap();
        param.put("SCORE", score);

        // 主台账
        MainTradeData mainList = bd.getMainTradeData();
        mainList.setRsrvStr1(score);
        mainList.setRsrvStr2(reqData.getRollScore());
        mainList.setRsrvStr3("ZZ");
        mainList.setRsrvStr8(reqData.getOrderNo());
        mainList.setRsrvStr9(reqData.getSubscribeId());
        mainList.setRsrvStr10(reqData.getOprt());
        mainList.setRemark("一级Boss积分回退");

        // 登记积分台账
        createScoreTradeData(bd, reqData, param);
    }

    public void createScoreTradeData(BusiTradeData bd, ScoreRollBackRequestData reqData, IData inparam) throws Exception
    {
        ScoreTradeData scoreData = new ScoreTradeData();

        scoreData.setUserId(reqData.getUca().getUserId());
        scoreData.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreData.setIdType("0");
        scoreData.setScoreTypeCode("ZZ");
        scoreData.setYearId("ZZZZ");
        scoreData.setEndCycleId("-1");
        scoreData.setStartCycleId("-1");
        scoreData.setScore(inparam.getString("SCORE"));
        scoreData.setScoreChanged(reqData.getRollScore());
        scoreData.setValueChanged("0");
        scoreData.setScoreTag("1");
        scoreData.setCancelTag("0");
        scoreData.setRemark("一级Boss积分回退");
        scoreData.setRsrvStr6("02");// 积分回退设为02,以备交易超时查询时用
        scoreData.setRsrvStr8(reqData.getOrderId());
        scoreData.setRsrvStr9(reqData.getSubscribeId());
        scoreData.setRsrvStr10(reqData.getOprt());
        bd.add(reqData.getUca().getSerialNumber(), scoreData);
    }
}
