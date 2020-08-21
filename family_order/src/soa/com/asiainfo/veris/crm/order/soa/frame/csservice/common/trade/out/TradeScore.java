
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.score.scoreexchange.tradecancel.ScoreExchangeTradeCancel;

public final class TradeScore
{
    public static void tradeScore(IData mainTrade) throws Exception
    {
        String intfId = mainTrade.getString("INTF_ID", "");

        if (StringUtils.isNotEmpty(intfId) && intfId.indexOf("TF_B_TRADE_SCORE,") == -1)
        {
            return;
        }

        // 调账务积分接口
        String tradeId = mainTrade.getString("TRADE_ID");

        IDataset tradeScore = TradeScoreInfoQry.qryTradeScoreInfos(tradeId, "0");

        if (IDataUtil.isEmpty(tradeScore))
        {
            return;
        }

        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String userId = mainTrade.getString("USER_ID");
        String cancelTag = mainTrade.getString("CANCEL_TAG");

        String yearId = "";
        String scoreTypeCode = "";

        if ("0".equals(cancelTag))// 0-未返销
        {
            int scoreChanged = 0;

            for (int i = 0; i < tradeScore.size(); i++)
            {
                String scoreTag = tradeScore.getData(i).getString("SCORE_TAG");

                if ("0".equals(scoreTag))// 0积分清零
                {
                    // 调积分清零接口
                    AcctCall.cancelScoreValue(tradeTypeCode, tradeId, userId);

                    return;
                }
                else if ("1".equals(scoreTag))// 1 积分变更
                {
                    yearId = tradeScore.getData(i).getString("YEAR_ID");
                    scoreTypeCode = tradeScore.getData(i).getString("SCORE_TYPE_CODE");

                    int myScore = Integer.parseInt(tradeScore.getData(i).getString("SCORE_CHANGED"));

                    // 积分转赠要每条数据执行一次，因为每条数据userId不同
                    if ("340".equals(tradeTypeCode))
                    {
                        String modUesrid = tradeScore.getData(i).getString("USER_ID");

                        // 调积分扣减转赠接口
                        AcctCall.userScoreModify(modUesrid, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId);
                    }
                    else
                    {
                        scoreChanged = scoreChanged + myScore;
                    }
                }
            }

            if (scoreChanged != 0)
            {
                // 调积分扣减转赠接口
                AcctCall.userScoreModify(userId, yearId, scoreTypeCode, tradeTypeCode, scoreChanged, tradeId);
            }
        }
        else if ("2".equals(cancelTag))// 2-返销
        {
        	//songlm 20141120 观影体育返销
            for (int i = 0; i < tradeScore.size(); i++)
            {
	        	String moviesTag = tradeScore.getData(i).getString("RSRV_STR6");
	        	if("movies".equals(moviesTag)){//如果是观影体育需求的积分子台帐
	        		updScoreTradeRsrv(tradeId);
	        	}
            }
            //end
            
            //REQ201611210014关于优化积分兑换和包电子券业务流程的需求-songxw-BOSS多只返销积分不返销电子券
            String upayCancelScore = mainTrade.getString("UPAY_CANCEL_SCORE","0");
            if(!"1".equals(upayCancelScore)){
	            //songlm 20150119 REQ201501040004 关于增加和包电子券撤销功能的需求
	            ScoreExchangeTradeCancel.cancelHeTickets(mainTrade);
	            //end
            }
            
            AcctCall.scoreCancel(tradeId);// 积分返销接口
        }
    }
    
    /**
     * songlm 观影体育返销 修改
     * */
    private static int updScoreTradeRsrv(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_SCORE", "UPD_SCORETRADE_RSRV_STR6", param,Route.getJourDb(BizRoute.getRouteId()));//改为jour用户,duhj
    }
}
