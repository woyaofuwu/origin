
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.CheckScoreDonateIntfSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateIBossRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateRequestData;

public class ScoreDonateIntfTrade extends BaseTrade implements ITrade
{
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	String xTradeCode = btd.getRD().getXTransCode();// 服务名称
   	    if (StringUtils.equals("SS.ScoreDonateIBossRegSVC.tradeReg",xTradeCode))
        {
   		    createDonateIBossMain(btd);
            createDonateIBossScore(btd);
        }else if(StringUtils.equals("SS.ScoreDonateRegSVC.TradeReg",xTradeCode) || StringUtils.equals("SS.ScoreDonateITFRegSVC.tradeReg",xTradeCode)){
        	ScoreDonateRequestData reqData = (ScoreDonateRequestData) btd.getRD();
            String serialNumber = reqData.getUca().getSerialNumber();
            String objectSerialNumber = reqData.getDonatedUca().getSerialNumber();
            String donatedScore = reqData.getDonateScore();

            CheckScoreDonateIntfSVC checkBean = new CheckScoreDonateIntfSVC();
            IData inparam = new DataMap();
            inparam.put("SERIAL_NUMBER", serialNumber);
            inparam.put("OBJECT_SERIAL_NUMBER", objectSerialNumber);
            inparam.put("DONATE_SCORE", donatedScore);
            checkBean.checkScoreDonate(inparam);

            IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
            IDataset objectScoreInfo = AcctCall.queryUserScore(reqData.getDonatedUca().getUserId());

            int donate_score = Integer.parseInt(donatedScore);
            int seScore = Integer.parseInt(scoreInfo.getData(0).getString("SUM_SCORE"));// 原号码原积分
            int objScore = Integer.parseInt(objectScoreInfo.getData(0).getString("SUM_SCORE"));// 目标号码原积分
            int objScore2 = objScore + donate_score;// 目标号码现积分
            int seScore2 = seScore - donate_score;// 原号码现积分
            // 主台账
            MainTradeData mainList = btd.getMainTradeData();
            mainList.setRsrvStr1(String.valueOf(seScore)); // 积分总额
            mainList.setRsrvStr2(String.valueOf(seScore2)); // 积分余额
            mainList.setRsrvStr3(String.valueOf(objScore2)); // 受赠人积分余额
            mainList.setRsrvStr5(objectSerialNumber); // 受赠人手机号码
//            mainList.setRsrvStr6(reqData.getDonatedUca().getUserId());
//            mainList.setRsrvStr7(donatedScore);
//            mainList.setRsrvStr8(objectSerialNumber);
//            mainList.setRsrvStr9(String.valueOf(seScore2));
            mainList.setRsrvStr10(String.valueOf(donatedScore)); // 转赠积分值
//            mainList.setRsrvStr10(String.valueOf(objScore2));
            mainList.setRemark("积分转赠");

            inparam.put("NEWSCORE", String.valueOf(objScore));
            inparam.put("SCORE", String.valueOf(seScore));

            // 创建积分台账
            createScoreTradeData(btd, inparam, reqData);
        }
    }
	private void createDonateIBossMain(BusiTradeData btd) throws Exception
    {
		ScoreDonateIBossRequestData reqData = (ScoreDonateIBossRequestData) btd.getRD();
		int scoreBalance = Integer.parseInt(reqData.getSCORE()) - Integer.parseInt(reqData.getTRANSFER_POINT()); // 转让人转赠后积分余额
		int objScoreBalance = Integer.parseInt(reqData.getOBJ_SCORE()) + Integer.parseInt(reqData.getTRANSFER_POINT()); // 受让人受赠后积分余额

		MainTradeData mainList = btd.getMainTradeData();
        mainList.setRsrvStr1(reqData.getSCORE()); // 转让人原积分总额
        mainList.setRsrvStr2(String.valueOf(scoreBalance)); // 转让人积分余额
        mainList.setRsrvStr3(String.valueOf(objScoreBalance)); // 受让人积分余额
        mainList.setRsrvStr5(reqData.getB_MOBILE());// 受让人手机号码
        //mainList.setRsrvStr8(reqData.getTRADE_SEQ());
        mainList.setRsrvStr10(reqData.getTRANSFER_POINT());// 转赠积分值
        mainList.setRemark(reqData.getCOMMENTS());    //备注说明
        /**
         * 合版本 duhj 2017/5/2
         * REQ201702080017_关于积分业务的若干优化需求
         * <br/>
         * 用RsrvStr4来标志，为后面finishaction判断使用
         * @author zhuoyingzhi
         * @date 20170308
         */
        mainList.setRsrvStr4("BIP5A053");
    }
    private void createDonateIBossScore(BusiTradeData btd) throws Exception
    {
    	ScoreDonateIBossRequestData reqData = (ScoreDonateIBossRequestData) btd.getRD();
        String serialNumber = btd.getRD().getUca().getSerialNumber();

        String scoreTypeCode = "ZZ"; // 获取积分类型

        ScoreTradeData scoreTD = new ScoreTradeData();

        scoreTD.setUserId(reqData.getUca().getUser().getUserId());
        scoreTD.setSerialNumber(reqData.getUca().getUser().getSerialNumber());
        scoreTD.setIdType("0");
        scoreTD.setScoreTypeCode(scoreTypeCode);
        scoreTD.setYearId("ZZZZ");
        scoreTD.setStartCycleId("-1");
        scoreTD.setEndCycleId("-1");
        scoreTD.setScore(reqData.getSCORE());
        scoreTD.setScoreChanged("-" + reqData.getTRANSFER_POINT());
        scoreTD.setValueChanged("0");
        scoreTD.setScoreTag("1");
        scoreTD.setRuleId("");
        scoreTD.setActionCount("");
        scoreTD.setResId("");
        scoreTD.setGoodsName("");
        scoreTD.setCancelTag("0");
        scoreTD.setRemark(reqData.getRemark());
        scoreTD.setRsrvStr8(reqData.getTRADE_SEQ());
        btd.add(serialNumber, scoreTD);

        ScoreTradeData scoreTD2 = new ScoreTradeData();

        scoreTD2.setUserId(reqData.getOBJ_USERID());
        scoreTD2.setSerialNumber(reqData.getB_MOBILE());
        scoreTD2.setIdType("0");
        scoreTD2.setScoreTypeCode(scoreTypeCode);
        scoreTD2.setYearId("ZZZZ");
        scoreTD2.setStartCycleId("-1");
        scoreTD2.setEndCycleId("-1");
        scoreTD2.setScore(reqData.getOBJ_SCORE());
        scoreTD2.setScoreChanged(reqData.getTRANSFER_POINT());
        scoreTD2.setValueChanged("0");
        scoreTD2.setScoreTag("1");
        scoreTD2.setRuleId("");
        scoreTD2.setActionCount("");
        scoreTD2.setResId("");
        scoreTD2.setGoodsName("");
        scoreTD2.setCancelTag("0");
        scoreTD2.setRemark(reqData.getRemark());
        scoreTD2.setRsrvStr8(reqData.getTRADE_SEQ());
        btd.add(serialNumber, scoreTD2);

    }
    
    public void createScoreTradeData(BusiTradeData bd, IData inparam, ScoreDonateRequestData reqData) throws Exception
    {
        ScoreTradeData scoreData1 = new ScoreTradeData();
        String userId1 = reqData.getUca().getUserId();
        IDataset integralAcctInfo = AcctInfoQry.qryIntegralAcctInfoByUserId(userId1, "10A");
        scoreData1.setUserId(userId1);
        scoreData1.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreData1.setIdType("0");
        scoreData1.setScoreTypeCode("ZZ");
        scoreData1.setYearId("ZZZZ");
        scoreData1.setStartCycleId("-1");
        scoreData1.setEndCycleId("-1");
        scoreData1.setScore(inparam.getString("SCORE"));
        scoreData1.setScoreChanged("-" + inparam.getString("DONATE_SCORE"));
        scoreData1.setValueChanged("0");
        scoreData1.setScoreTag("1");
        scoreData1.setCancelTag("0");
        scoreData1.setRemark("积分转赠");
        scoreData1.setRsrvStr6("ZS");
        if (IDataUtil.isNotEmpty(integralAcctInfo))
        {
            scoreData1.setRsrvStr1(integralAcctInfo.getData(0).getString("PSPT_TYPE_CODE", ""));// 积分联盟账户信息证件类型
            scoreData1.setRsrvStr2(integralAcctInfo.getData(0).getString("PSPT_ID", ""));
        }
        bd.add(reqData.getUca().getSerialNumber(), scoreData1);

        ScoreTradeData scoreData2 = new ScoreTradeData();
        String userId2 = reqData.getDonatedUca().getUserId();
        IDataset integralAcctInfo2 = AcctInfoQry.qryIntegralAcctInfoByUserId(userId2, "10A");
        scoreData2.setUserId(userId2);
        scoreData2.setSerialNumber(reqData.getDonatedUca().getSerialNumber());
        scoreData2.setIdType("0");
        scoreData2.setScoreTypeCode("ZZ");
        scoreData2.setYearId("ZZZZ");
        scoreData2.setStartCycleId("-1");
        scoreData2.setEndCycleId("-1");
        scoreData2.setScore(inparam.getString("NEWSCORE"));
        scoreData2.setScoreChanged(inparam.getString("DONATE_SCORE"));
        scoreData2.setValueChanged("0");
        scoreData2.setScoreTag("1");
        scoreData2.setCancelTag("0");
        scoreData2.setRemark("积分转赠");
        scoreData2.setRsrvStr6("BZS");
        if (IDataUtil.isNotEmpty(integralAcctInfo2))
        {
            scoreData2.setRsrvStr1(integralAcctInfo2.getData(0).getString("PSPT_TYPE_CODE", ""));// 积分联盟账户信息证件类型
            scoreData2.setRsrvStr2(integralAcctInfo2.getData(0).getString("PSPT_ID", ""));
        }
        bd.add(reqData.getDonatedUca().getSerialNumber(), scoreData2);
    }

}
