
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
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreTradeReviseRequestData;

public class ScoreTradeReviseTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ScoreTradeReviseRequestData reqData = (ScoreTradeReviseRequestData) bd.getRD();
        // 获取客户可用积分余额
        String score = "";
        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isNotEmpty(scoreInfo))
        {
            score = scoreInfo.getData(0).getString("SCORE");
        }
        else
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String brandCode = reqData.getUca().getBrandCode();
        String scoreTypeCode = "b";
        if ("G010".equals(brandCode))
        {
            scoreTypeCode = "e";
        }
        String userStateCodeset = reqData.getUca().getUser().getUserStateCodeset();
        if (!"0".equals(userStateCodeset))
        {
            // 用户状态不正常冲正失败!
        }

        // 主台账
        MainTradeData mainList = bd.getMainTradeData();
        mainList.setRsrvStr1(score);// 用户原积分
        mainList.setRsrvStr2(reqData.getScoreValue());// 冲正积分
        mainList.setRsrvStr3(scoreTypeCode);
        mainList.setRsrvStr8(reqData.getOrderId());
        mainList.setRsrvStr9(reqData.getSubscribeId());
        mainList.setRsrvStr10(reqData.getOprt());
        mainList.setRemark("一级Boss冲正交易");

        // 登记积分台账
        IData param = new DataMap();
        param.put("SCORE", score);
        param.put("SCORE_TYPE_CODE", scoreTypeCode);
        createScoreTradeData(bd, reqData, param);
    }

    public void createScoreTradeData(BusiTradeData bd, ScoreTradeReviseRequestData reqData, IData inparam) throws Exception
    {
        ScoreTradeData scoreData = new ScoreTradeData();

        scoreData.setUserId(reqData.getUca().getUserId());
        scoreData.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreData.setIdType("0");
        scoreData.setScoreTypeCode(inparam.getString("SCORE_TYPE_CODE"));
        scoreData.setYearId("ZZZZ");
        scoreData.setEndCycleId("-1");
        scoreData.setStartCycleId("-1");
        scoreData.setScore(inparam.getString("SCORE"));
        scoreData.setScoreChanged(reqData.getScoreValue());
        scoreData.setValueChanged("0");
        scoreData.setScoreTag("1");
        scoreData.setCancelTag("0");
        scoreData.setRemark("一级Boss冲正交易");
        scoreData.setRsrvStr6("03");// 冲正设为03
        scoreData.setRsrvStr8(reqData.getOrderId());
        scoreData.setRsrvStr9(reqData.getSubscribeId());
        scoreData.setRsrvStr10(reqData.getOprt());
        bd.add(reqData.getUca().getSerialNumber(), scoreData);
    }
}
