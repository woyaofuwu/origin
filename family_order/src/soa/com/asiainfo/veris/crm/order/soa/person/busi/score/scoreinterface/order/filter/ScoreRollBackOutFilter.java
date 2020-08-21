
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreRollBackRequestData;

/**
 * 积分回退输出转换
 * 
 * @author huangsl
 */
public class ScoreRollBackOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {

        // 计算用户可用积分余额
        int scoreValue = 0;
        List<ScoreTradeData> scoreDatas = new ArrayList<ScoreTradeData>();
        scoreDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);
        if (scoreDatas.size() > 0)
        {
            ScoreTradeData scoreData = scoreDatas.get(0);
            if (StringUtils.isNotBlank(scoreData.getScore()))
            {
                scoreValue = Integer.parseInt(scoreData.getScore());
            }
        }
        int score = Integer.parseInt(input.getString("RSRV_STR3"));
        int score_value = score + scoreValue;

        ScoreRollBackRequestData reqData = (ScoreRollBackRequestData)btd.getRD();
        IData resultInfo = new DataMap();
        resultInfo.put("RSRV_STR1", input.getString("RSRV_STR1"));
        resultInfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        resultInfo.put("ORDER_ID", reqData.getOrderNo());
        resultInfo.put("SUBSCRIBE_ID", input.getString("SUBSCRIBE_ID"));
        resultInfo.put("SCORE_VALUE", score_value);
        return resultInfo;
    }

}
