
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.TrainScoreDeductRequestData;

/**
 * 本地积分兑换及网购平台积分兑换输出转换
 * 
 * @author huangsl
 */
public class TrainScoreDeductOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        TrainScoreDeductRequestData reqData = (TrainScoreDeductRequestData) btd.getRD();
        String score1 = "";// 扣减积分
        int score2 = 0;// 剩余积分
        String score3 = "";// 扣减前积分
        List<ScoreTradeData> scoreDatas = new ArrayList<ScoreTradeData>();
        scoreDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);
        if (scoreDatas.size() > 0)
        {
            ScoreTradeData scoreData = scoreDatas.get(0);
            if (StringUtils.isNotBlank(scoreData.getScore()))
            {
                score1 = scoreData.getScoreChanged();
                score3 = scoreData.getScore();
            }
        }
        score2 = Integer.parseInt(score3) + Integer.parseInt(score1);
        IData resultInfo = new DataMap();
        resultInfo.put("CHANNEL_TRADE_ID", reqData.getChannelTradeId());
        resultInfo.put("CHANNEL_ACCEPT_TIME", reqData.getChannelAcceptTime());
        resultInfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        resultInfo.put("ACCEPT_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
        resultInfo.put("SCORE", String.valueOf(score2));
        resultInfo.put("USER_ID", btd.getRD().getUca().getUser().getUserId());
        resultInfo.put("SCORE_VALUE", reqData.getScoreValue());
        resultInfo.put("PARA1", reqData.getPara1());
        resultInfo.put("PARA2", reqData.getPara2());
        resultInfo.put("PARA3", reqData.getPara3());
        resultInfo.put("PARA4", reqData.getPara4());
        resultInfo.put("PARA5", reqData.getPara5());
        resultInfo.put("PARA6", reqData.getPara6());
        return resultInfo;
    }

}
