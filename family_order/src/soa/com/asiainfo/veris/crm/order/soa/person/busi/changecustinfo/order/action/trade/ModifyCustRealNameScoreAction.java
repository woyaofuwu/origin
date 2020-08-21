
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData;

/**
 * 修改客户资料：实名制用户赠送费积分处理
 * 
 * @author liutt
 */
public class ModifyCustRealNameScoreAction implements ITradeAction
{

    // 比较用户的开户时间，并返回赠送的积分数
    private int compareOpenDate(BusiTradeData btd) throws Exception
    {
        // 查询配置的时间和赠送的积分数
        IDataset commset = CommparaInfoQry.getCommparaAllCol("CSM", "666", "realNameScore", btd.getMainTradeData().getEparchyCode());
        if (IDataUtil.isNotEmpty(commset))
        {
            int commparamOpenMonth = commset.getData(0).getInt("PARA_CODE1", 0);// 配置的开户时间大于的月分数
            int commparamAddScore = commset.getData(0).getInt("PARA_CODE2", 0);// 需要赠送的积分
            String openDateStr = btd.getRD().getUca().getUser().getOpenDate();// 开户日期

            // 开户月份大于等于配置的月份数
            if (SysDateMgr.monthInterval(openDateStr, btd.getRD().getAcceptTime()) >= commparamOpenMonth)
            {
                return commparamAddScore;// 返回需要赠送的积分数
            }
        }
        return -1;
    }

    /**
     * 拼积分台账表
     * 
     * @param btd
     * @param exchangeScore
     *            可兑换积分
     * @param addScore
     *            赠送积分
     * @throws Exception
     */
    private void createTradeScoreData(BusiTradeData btd, int exchangeScore, int addScore) throws Exception
    {
        ScoreTradeData scoreTrade = new ScoreTradeData();
        scoreTrade.setUserId(btd.getRD().getUca().getUserId());
        scoreTrade.setSerialNumber(btd.getRD().getUca().getUser().getSerialNumber());
        scoreTrade.setIdType("0");
        scoreTrade.setScoreTypeCode("05");
        scoreTrade.setYearId("ZZZZ");
        scoreTrade.setEndCycleId("-1");
        scoreTrade.setScore("" + exchangeScore);
        scoreTrade.setScoreChanged("" + addScore);
        scoreTrade.setValueChanged("0");
        scoreTrade.setScoreTag("1");
        scoreTrade.setCancelTag("0");
        scoreTrade.setRemark("办理实名制，赠送" + addScore + "积分");
        scoreTrade.setRsrvStr6("realNameScore");
        btd.add(btd.getRD().getUca().getSerialNumber(), scoreTrade);
    }

    public void executeAction(BusiTradeData btd) throws Exception
    {
        ModifyCustInfoReqData rd = (ModifyCustInfoReqData) btd.getRD();
        // 原来不是实名制、本次做了实名制
        if (!StringUtils.equals("1", rd.getUca().getUserOriginalData().getCustomer().getIsRealName()) && StringUtils.equals("1", rd.getIsRealName()))
        {
            // 检查开户时间是否大于13个月，并返回需要赠送的积分数
            int addScore = this.compareOpenDate(btd);//
            if (addScore > 0)// 需要赠送积分
            {
                // 查询以前是否有做过实名制积分赠送
                IDataset realScoreSet = TradeScoreInfoQry.getRealNameTradeScore(rd.getUca().getUserId());
                if (IDataUtil.isEmpty(realScoreSet))
                {// 没有赠送过
                    IData scoreInfo = AcctCall.queryUserScoreone(rd.getUca().getUserId());// 获取用户积分信息
                    String exchangeScore = scoreInfo.getString("SUM_SCORE", "0"); // 用户可兑换积分
                    int scoreCount = Integer.parseInt(exchangeScore) + addScore;
                    btd.getMainTradeData().setRsrvStr5("realNameScore");
                    btd.getMainTradeData().setRsrvStr6(String.valueOf(scoreCount));// 赠送之后的总积分数
                    btd.getMainTradeData().setRsrvStr7(String.valueOf(addScore));// 赠送的积分
                    this.createTradeScoreData(btd, Integer.parseInt(exchangeScore), addScore);
                }
            }
        }
    }
}
