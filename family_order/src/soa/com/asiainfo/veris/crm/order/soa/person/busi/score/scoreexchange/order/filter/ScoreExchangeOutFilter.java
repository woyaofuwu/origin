
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.filter;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;

/**
 * 积分兑换输出转换
 * 
 * @author huangsl
 */
public class ScoreExchangeOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        String score1 = "";// SCORE1扣减积分
        int score2 = 0;// SCORE2剩余积分
        String score3 = "";// SCORE3扣减前积分
        String paramValue = "";// PARAM_VALUE兑换物品
        List<ScoreTradeData> scoreDatas = new ArrayList<ScoreTradeData>();
        scoreDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);
        if (scoreDatas.size() > 0)
        {
            ScoreTradeData scoreData = scoreDatas.get(0);
            if (StringUtils.isNotBlank(scoreData.getScore()))
            {
                score1 = scoreData.getScoreChanged();
                score3 = scoreData.getScore();
                paramValue = scoreData.getGoodsName();
            }
        }
        score2 = Integer.parseInt(score3) + Integer.parseInt(score1);
        IData resultInfo = new DataMap();
        resultInfo.put("SCORE1", score1);
        resultInfo.put("SCORE2", String.valueOf(score2));
        resultInfo.put("SCORE3", score3);
        resultInfo.put("PARAM_VALUE", paramValue);
        return resultInfo;
    }

}
