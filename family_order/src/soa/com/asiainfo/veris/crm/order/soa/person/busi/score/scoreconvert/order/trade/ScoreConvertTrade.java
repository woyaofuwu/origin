
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.UpmsQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata.ScoreConvertRequestData;

public class ScoreConvertTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ScoreConvertRequestData reqData = (ScoreConvertRequestData) btd.getRD();
        MainTradeData mainTrade = btd.getMainTradeData();
        // 再查一次积分
        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String score = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分

        // 查询礼品信息
        IDataset upmsGiftInfos = UpmsQry.queryUpmsGiftByPK(reqData.getItemId());
        if (IDataUtil.isEmpty(upmsGiftInfos))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_43, reqData.getItemId());
        }
        String itemScore = upmsGiftInfos.getData(0).getString("SCORE_VALUE");
        // 积分不足扣减
        int scoreSum = Integer.parseInt(itemScore) * Integer.parseInt(reqData.getItemNum());
        if (StringUtils.isBlank(score) || (Integer.parseInt(score) < scoreSum))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_782, score, scoreSum);
        }
        mainTrade.setRsrvStr5(score);// 用户可用积分
        mainTrade.setRsrvStr6(String.valueOf(scoreSum));// 兑换礼品积分
        mainTrade.setRsrvStr7(upmsGiftInfos.getData(0).getString("ITEM_NAME"));// 兑换礼品名称
        mainTrade.setRsrvStr8(reqData.getItemNum());// 兑换礼品数量
    }
}
