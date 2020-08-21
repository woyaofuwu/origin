
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.UpmsQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata.ScoreConvertRequestData;

/**
 * IVR积分兑换输出转换
 * 
 * @author huangsl
 */
public class IVRScoreExchangeOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        IData result = new DataMap();
        ScoreConvertRequestData reqData = (ScoreConvertRequestData) btd.getRD();
        String ruleId = input.getString("RULE_ID");

        // 再查一次积分
        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String score = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分
        IDataset ruleInfos = UpmsQry.queryUpmsGiftInfoByItemId2(ruleId);
        IData ruleInfo = null;
        if (IDataUtil.isNotEmpty(ruleInfos))
        {
            ruleInfo = ruleInfos.getData(0);
        }
        if (IDataUtil.isNotEmpty(ruleInfo))
        {
            String score_change = ruleInfo.getString("SCORE_VALUE", "");
            int tempCount = Integer.parseInt(input.getString("COUNT"));
            int tempScore_change = Integer.parseInt(score_change) * tempCount;
            int tempScore = Integer.parseInt(score);
            int origScore2 = tempScore + tempScore_change;
            result.put("SCORE1", tempScore_change);//扣减积分
            result.put("SCORE2", tempScore);//剩余积分
            result.put("SCORE3", String.valueOf(origScore2));//原始积分
            result.put("PARAM_VALUE", ruleInfo.getString("ITEM_NAME"));
            result.put("X_RESULTCODE", "0");
        }
        else
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_44, ruleId);
        }
        return result;
    }

}
