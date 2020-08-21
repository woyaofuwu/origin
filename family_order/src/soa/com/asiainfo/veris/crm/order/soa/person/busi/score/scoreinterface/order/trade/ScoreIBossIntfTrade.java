
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDeductReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateRollBackRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreGiftReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScorePayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScorePayRollBackReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreReviseNReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreRollBackRequestData;

public class ScoreIBossIntfTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        String xTradeCode = btd.getRD().getXTransCode();// 服务名称
        IData data = AcctCall.queryUserScoreone(btd.getRD().getUca().getUserId());
        String score = "0";// 用户积分
        if (IDataUtil.isNotEmpty(data))
        {
            score = data.getString("SCORE");
        }
        // 根据服务名称判断业务
        //关于下发近期积分商城相关省级配合改造的通知   zhouyl5
        if ("SS.ScoreGiftRegSVC.tradeReg".equals(xTradeCode))// 积分赠送
        {
            createGiftMain(btd,  score+"^"+data.getString("SUM_SCORE",score));
            createGiftScore(btd, score);
        }
        /*add by ouyang start*/
        else if ("SS.ScoreDeductRegSVC.tradeReg".equals(xTradeCode))// 积分扣减
        {
            createDeductMain(btd, score);
            createDeductScore(btd, score);
        }
        /*add by ouyang end*/
        else if ("SS.ScorePayRegSVC.tradeReg".equals(xTradeCode) || "SS.ScoreDeductRegSVC.tradeRegOutter".equals(xTradeCode))// 积分支付
        {
            createPayMain(btd, score+"^"+data.getString("SUM_SCORE",score));
            createPayScore(btd, score);
        }
        else if ("SS.ScorePayRollBackRegSVC.tradeReg".equals(xTradeCode))// 积分支付回退
        {
            createPayRollBackMain(btd, score+"^"+data.getString("SUM_SCORE",score));
            createPayRollBackScore(btd, score);
        }
        else if ("SS.ScoreReviseNRegSVC.tradeReg".equals(xTradeCode))// 积分冲正
        {
            createReviseNMain(btd, score);
            createReviseNScore(btd, score);
        }else if ("SS.ScoreRollBackIntfRegSVC.TradeReg".equals(xTradeCode))// 积分回退
        {
            createRollBackMain(btd, score);
            createRollBackScore(btd, score);
        }else if ("SS.ScoreDonateRollBackRegSVC.tradeReg".equals(xTradeCode))// 积分转赠回退
        {
            createDonateRollBackMain(btd, score);
            createDonateRollBackScore(btd, score);
        }
    }

    private void createDeductMain(BusiTradeData btd, String score) throws Exception
    {
        ScoreDeductReqData reqData = (ScoreDeductReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);// 用户原积分
        mainList.setRsrvStr2("-" + reqData.getScoreValue());// 扣减积分值
        mainList.setRsrvStr3("00");// 积分类型
        mainList.setRsrvStr8(reqData.getXOrderId());// 大订单编号
        mainList.setRsrvStr10(reqData.getOprt());// 客户订购时间
        mainList.setRemark("一级Boss积分扣减");
    }

    private void createDeductScore(BusiTradeData btd, String score) throws Exception
    {
        ScoreTradeData scoreTD = new ScoreTradeData();
        ScoreDeductReqData reqData = (ScoreDeductReqData) btd.getRD();

        scoreTD.setUserId(reqData.getUca().getUserId());
        scoreTD.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreTD.setIdType("0");
        scoreTD.setScoreTypeCode("ZZ");
        scoreTD.setYearId("ZZZZ");
        scoreTD.setStartCycleId("-1");
        scoreTD.setEndCycleId("-1");
        scoreTD.setScore(score);
        scoreTD.setScoreChanged("-" + reqData.getScoreValue());// 扣减积分值
        scoreTD.setValueChanged("0");
        scoreTD.setScoreTag("1");
        scoreTD.setRuleId("");
        scoreTD.setActionCount("");
        scoreTD.setResId("");
        scoreTD.setGoodsName("");
        scoreTD.setCancelTag("0");
        scoreTD.setRemark("一级Boss积分扣减");
        scoreTD.setRsrvStr6("031");// 积分扣减设为01,以备交易超时查询时用
        scoreTD.setRsrvStr8(reqData.getXOrderId());// 大订单编号
        scoreTD.setRsrvStr10(reqData.getOprt());

        btd.add(reqData.getUca().getSerialNumber(), scoreTD);

    }
    
    private void createGiftMain(BusiTradeData btd, String score) throws Exception
    {
        ScoreGiftReqData reqData = (ScoreGiftReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);// 用户原积分
        mainList.setRsrvStr2(reqData.getADD_POINT());// 赠送分值
        mainList.setRsrvStr3("00");// 积分类型
        mainList.setRsrvStr4(reqData.getORGID());//渠道标识
        mainList.setRsrvStr5(reqData.getACTION_TYPE());// 活动类型
        mainList.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        mainList.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
        mainList.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        mainList.setRemark(reqData.getCOMMENTS());
    }
    private void createGiftScore(BusiTradeData btd, String score) throws Exception
    {
        ScoreTradeData scoreTD = new ScoreTradeData();
        ScoreGiftReqData reqData = (ScoreGiftReqData) btd.getRD();
        scoreTD.setUserId(reqData.getUca().getUserId());
        scoreTD.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreTD.setIdType("0");
        scoreTD.setScoreTypeCode("01");
        scoreTD.setYearId("ZZZZ");
        scoreTD.setStartCycleId("-1");
        scoreTD.setEndCycleId("-1");
        scoreTD.setScore(score);
        scoreTD.setScoreChanged(reqData.getADD_POINT());// 回退积分值
        scoreTD.setValueChanged("0");
        scoreTD.setScoreTag("1");
        scoreTD.setRuleId("");
        scoreTD.setActionCount("");
        scoreTD.setResId("");
        scoreTD.setGoodsName("");
        scoreTD.setCancelTag("0");
        scoreTD.setRemark("一级Boss积分赠送");
        scoreTD.setRsrvStr6("030");// 积分回退设为02,以备交易超时查询时用
        scoreTD.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        scoreTD.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
        scoreTD.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        scoreTD.setRsrvStr3(reqData.getVALIDATE_TIME());//积分有效期
		if("02".equals(reqData.getPOINT_TYPE())){
			 scoreTD.setScoreTypeCode("05");
		}
        /**
         * 合版本 duhj 2017/5/2
         * REQ201702080017_关于积分业务的若干优化需求
         * <br/>
         * 用RsrvStr5来标志，为后面finishaction判断使用
         * @author zhuoyingzhi
         * @date 20170307
         */
        scoreTD.setRsrvStr5("BIP5A032");
        /**********end*******************/
        btd.add(reqData.getUca().getSerialNumber(), scoreTD);
    }
    private void createPayMain(BusiTradeData btd, String score) throws Exception
    {
    	ScorePayReqData reqData = (ScorePayReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);// 用户原积分
        mainList.setRsrvStr2(reqData.getPAY_POINT());// 支付积分值
       // mainList.setRsrvStr3("00");// 积分类型
        mainList.setRsrvStr4(reqData.getORGID());//渠道标识
        mainList.setRsrvStr5(reqData.getACTION_TYPE());// 活动类型
        mainList.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        mainList.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
        mainList.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        mainList.setRemark("一级Boss积分赠送");
    }
    private void createPayScore(BusiTradeData btd, String score) throws Exception
    {
    	IDataset pointTypeInfo = btd.getRD().getPageRequestData().getDataset("POINT_TYPE_INFO");
    	if("SS.ScoreDeductRegSVC.tradeRegOutter".equals(btd.getRD().getXTransCode())){         	
        	for(int i=0; i < pointTypeInfo.size(); i++){
        		ScoreTradeData scoreTD = new ScoreTradeData();
                ScorePayReqData reqData = (ScorePayReqData) btd.getRD();
                scoreTD.setUserId(reqData.getUca().getUserId());
                scoreTD.setSerialNumber(reqData.getUca().getSerialNumber());
                scoreTD.setIdType("0");
                scoreTD.setScoreTypeCode(pointTypeInfo.getData(i).getString("POINT_TYPE"));
                scoreTD.setYearId("ZZZZ");
                scoreTD.setStartCycleId("-1");
                scoreTD.setEndCycleId("-1");
                scoreTD.setScore(score);
                scoreTD.setScoreChanged("-"+pointTypeInfo.getData(i).getString("PAY_POINT"));// 支付积分值
                scoreTD.setValueChanged("0");
                scoreTD.setScoreTag("1");
                scoreTD.setRuleId("");
                scoreTD.setActionCount("");
                scoreTD.setResId("");
                scoreTD.setGoodsName("外部积分商城");
                scoreTD.setCancelTag("0");
                scoreTD.setRemark("一级Boss积分支付");
                scoreTD.setRsrvStr6("031");// 积分回退设为02,以备交易超时查询时用
                scoreTD.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
                scoreTD.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
                scoreTD.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
                
                /**
                 * 合版本 duhj 2017/5/2
                 * REQ201702080017_关于积分业务的若干优化需求
                 * <br/>
                 * 用RsrvStr5来标志，为后面finishaction判断使用
                 * @author zhuoyingzhi
                 * @date 20170307
                 */
                scoreTD.setRsrvStr5("BIP5A056");
                /**********end*******************/ 
                btd.add(reqData.getUca().getSerialNumber(), scoreTD);
        	}
        	 
    	}else{
    		ScoreTradeData scoreTD = new ScoreTradeData();
            ScorePayReqData reqData = (ScorePayReqData) btd.getRD();
            scoreTD.setUserId(reqData.getUca().getUserId());
            scoreTD.setSerialNumber(reqData.getUca().getSerialNumber());
            scoreTD.setIdType("0");
            scoreTD.setScoreTypeCode("ZZ");
            scoreTD.setYearId("ZZZZ");
            scoreTD.setStartCycleId("-1");
            scoreTD.setEndCycleId("-1");
            scoreTD.setScore(score);
            scoreTD.setScoreChanged("-"+reqData.getPAY_POINT());// 支付积分值
            scoreTD.setValueChanged("0");
            scoreTD.setScoreTag("1");
            scoreTD.setRuleId("");
            scoreTD.setActionCount("");
            scoreTD.setResId("");
            scoreTD.setGoodsName("");
            scoreTD.setCancelTag("0");
            scoreTD.setRemark("一级Boss积分支付");
            scoreTD.setRsrvStr6("031");// 积分回退设为02,以备交易超时查询时用
            scoreTD.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
            scoreTD.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
            scoreTD.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
            
            /**
             * 合版本 duhj 2017/5/2
             * REQ201702080017_关于积分业务的若干优化需求
             * <br/>
             * 用RsrvStr5来标志，为后面finishaction判断使用
             * @author zhuoyingzhi
             * @date 20170307
             */
            scoreTD.setRsrvStr5("BIP5A056");
            /**********end*******************/ 
            btd.add(reqData.getUca().getSerialNumber(), scoreTD);
    	}
    	
    }
    private void createPayRollBackMain(BusiTradeData btd, String score) throws Exception
    {
        ScorePayRollBackReqData reqData = (ScorePayRollBackReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);// 用户原积分
        mainList.setRsrvStr2(reqData.getREFUND_POINT());// 支付回退积分值
       // mainList.setRsrvStr3("00");// 积分类型
        mainList.setRsrvStr4(reqData.getORGID());//渠道标识
        mainList.setRsrvStr5(reqData.getP_TRADE_SEQ());// 活动类型
        mainList.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        mainList.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
        mainList.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        mainList.setRemark("一级Boss积分回退");
    }
    private void createPayRollBackScore(BusiTradeData btd, String score) throws Exception
    {
        ScoreTradeData scoreTD = new ScoreTradeData();
        ScorePayRollBackReqData reqData = (ScorePayRollBackReqData) btd.getRD();
        scoreTD.setUserId(reqData.getUca().getUserId());
        scoreTD.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreTD.setIdType("0");
        scoreTD.setScoreTypeCode("ZZ");
        scoreTD.setYearId("ZZZZ");
        scoreTD.setStartCycleId("-1");
        scoreTD.setEndCycleId("-1");
        scoreTD.setScore(score);
        scoreTD.setScoreChanged(reqData.getREFUND_POINT());// 回退积分值
        scoreTD.setValueChanged("0");
        scoreTD.setScoreTag("1");
        scoreTD.setRuleId("");
        scoreTD.setActionCount("");
        scoreTD.setResId("");
        scoreTD.setGoodsName("");
        scoreTD.setCancelTag("0");
        scoreTD.setRemark("一级Boss积分支付回退");
        scoreTD.setRsrvStr6("032");// 积分回退设为02,以备交易超时查询时用
        scoreTD.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        scoreTD.setRsrvStr9(reqData.getTRADE_ID());// 商户生成的交易流水号
        scoreTD.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        /**
         * 合版本 duhj 2017/5/2
         * REQ201702080017_关于积分业务的若干优化需求
         * <br/>
         * 用RsrvStr5来标志，为后面finishaction判断使用
         * @author zhuoyingzhi
         * @date 20170307
         */
        scoreTD.setRsrvStr5("BIP5A034");
        /**********end*******************/
        btd.add(reqData.getUca().getSerialNumber(), scoreTD);
    }
    private void createReviseNMain(BusiTradeData btd, String score) throws Exception
    {
        ScoreReviseNReqData reqData = (ScoreReviseNReqData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);// 用户原积分
        mainList.setRsrvStr2(reqData.getREVISE_POINT());// 支付回退积分值
        mainList.setRsrvStr3("ZZ");// 积分类型
        mainList.setRsrvStr4(reqData.getORGID());//渠道标识
        mainList.setRsrvStr5(reqData.getOPR_TYPE());// 活动类型
        mainList.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        mainList.setRsrvStr9(reqData.getTRADE_ID());// 交易流水号
        mainList.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        mainList.setRemark("一级Boss积分标价类冲正交易");
    }
    private void createReviseNScore(BusiTradeData btd, String score) throws Exception
    {
        ScoreTradeData scoreTD = new ScoreTradeData();
        ScoreReviseNReqData reqData = (ScoreReviseNReqData) btd.getRD();
        scoreTD.setUserId(reqData.getUca().getUserId());
        scoreTD.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreTD.setIdType("0");
        scoreTD.setScoreTypeCode("ZZ");
        scoreTD.setYearId("ZZZZ");
        scoreTD.setStartCycleId("-1");
        scoreTD.setEndCycleId("-1");
        scoreTD.setScore(score);
        if("01".equals(reqData.getOPR_TYPE())){
        	 scoreTD.setScoreChanged(reqData.getREVISE_POINT());// 回退积分值
        }else{
        	 scoreTD.setScoreChanged("-"+reqData.getREVISE_POINT());// 回退积分值
        }
        scoreTD.setValueChanged("0");
        scoreTD.setScoreTag("1");
        scoreTD.setRuleId("");
        scoreTD.setActionCount("");
        scoreTD.setResId("");
        scoreTD.setGoodsName("");
        scoreTD.setCancelTag("0");
        scoreTD.setRemark("一级Boss积分标价类冲正交易");
        scoreTD.setRsrvStr6("033");// 积分回退设为02,以备交易超时查询时用
        scoreTD.setRsrvStr8(reqData.getTRADE_SEQ());// 交易流水号 由积分统一管理平台生成
        scoreTD.setRsrvStr9(reqData.getOPR_TYPE());// 商户生成的交易流水号
        scoreTD.setRsrvStr10(reqData.getTRADE_TIME());// 交易时间
        /**
         * 合版本 duhj 2017/5/2
         * REQ201702080017_关于积分业务的若干优化需求
         * <br/>
         * 用RsrvStr5来标志，为后面finishaction判断使用
         * @author zhuoyingzhi
         * @date 20170307
         */
        scoreTD.setRsrvStr5("BIP5A036");
        /**********end*******************/  
        btd.add(reqData.getUca().getSerialNumber(), scoreTD);
    }
    private void createRollBackMain(BusiTradeData btd, String score) throws Exception
    {
    	ScoreRollBackRequestData reqData = (ScoreRollBackRequestData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);
        mainList.setRsrvStr2(reqData.getRollScore());
        mainList.setRsrvStr3("ZZ");
        mainList.setRsrvStr8(reqData.getOrderNo());
        mainList.setRsrvStr9(reqData.getSubscribeId());
        mainList.setRsrvStr10(reqData.getOprt());
        mainList.setRemark("一级Boss积分回退");
    }
    private void createRollBackScore(BusiTradeData btd, String score) throws Exception
    {
    	ScoreTradeData scoreData = new ScoreTradeData();
    	ScoreRollBackRequestData reqData = (ScoreRollBackRequestData) btd.getRD();
        scoreData.setUserId(reqData.getUca().getUserId());
        scoreData.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreData.setIdType("0");
        scoreData.setScoreTypeCode("ZZ");
        scoreData.setYearId("ZZZZ");
        scoreData.setEndCycleId("-1");
        scoreData.setStartCycleId("-1");
        scoreData.setScore(score);
        scoreData.setScoreChanged(reqData.getRollScore());
        scoreData.setValueChanged("0");
        scoreData.setScoreTag("1");
        scoreData.setCancelTag("0");
        scoreData.setRemark("一级Boss积分回退");
        scoreData.setRsrvStr6("02");// 积分回退设为02,以备交易超时查询时用
        scoreData.setRsrvStr8(reqData.getOrderId());
        scoreData.setRsrvStr9(reqData.getSubscribeId());
        scoreData.setRsrvStr10(reqData.getOprt());
        btd.add(reqData.getUca().getSerialNumber(), scoreData);
    }
    private void createDonateRollBackMain(BusiTradeData btd, String score) throws Exception
    {
    	ScoreDonateRollBackRequestData reqData = (ScoreDonateRollBackRequestData) btd.getRD();
        MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(score);
        mainList.setRsrvStr2(reqData.getSCORE_VALUE());
        mainList.setRsrvStr3("ZZ");
        mainList.setRsrvStr8(reqData.getTRADE_SEQ());
        mainList.setRsrvStr9(reqData.getTRADE_ID());
        mainList.setRemark("一级Boss积分转赠回退");
    }
    private void createDonateRollBackScore(BusiTradeData btd, String score) throws Exception
    {
    	ScoreTradeData scoreData = new ScoreTradeData();
    	ScoreDonateRollBackRequestData reqData = (ScoreDonateRollBackRequestData) btd.getRD();
        scoreData.setUserId(reqData.getUca().getUserId());
        scoreData.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreData.setIdType("0");
        scoreData.setScoreTypeCode("ZZ");
        scoreData.setYearId("ZZZZ");
        scoreData.setEndCycleId("-1");
        scoreData.setStartCycleId("-1");
        scoreData.setScore(score);
        scoreData.setScoreChanged(reqData.getSCORE_VALUE());
        scoreData.setValueChanged("0");
        scoreData.setScoreTag("1");
        scoreData.setCancelTag("0");
        scoreData.setRemark("一级Boss积分回退");
        scoreData.setRsrvStr6("040");// 积分转赠回退设
        scoreData.setRsrvStr8(reqData.getTRADE_SEQ());
        scoreData.setRsrvStr9(reqData.getTRADE_ID());
        btd.add(reqData.getUca().getSerialNumber(), scoreData);
    }
}
