
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.TrainScoreDeductRequestData;

public class TrainScoreDeductTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        TrainScoreDeductRequestData reqData = (TrainScoreDeductRequestData) bd.getRD();
        String inModeCode = this.getVisit().getInModeCode();
        if (StringUtils.isNotBlank(inModeCode) && ("SL".equals(inModeCode) || "WG".equals(inModeCode) || "ES".equals(inModeCode)))
        {
            IDataset results = UserInfoQry.qryUserScoreLimit(reqData.getUca().getSerialNumber());
            if (IDataUtil.isNotEmpty(results))
            {
                // 您的积分已作限制，仅能兑换VIP贵宾厅服务。本次办理不成功。");
                CSAppException.apperr(CrmUserException.CRM_USER_1111);
            }
        }

        String score = "0";
        // 查用户积分
        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            // 获取用户积分无数据!
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        score = scoreInfo.getData(0).getString("SUM_SCORE");

        // 判断用户状态 是否正常
        String userStateCodeset = reqData.getUca().getUser().getUserStateCodeset();
        // 积分商城办理积分扣减时如果用户是特开状态（N）时系统报用户状态不正常扣减失败，请修改，N也是正常的用户状态，允许办理积分业务
        if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset))
        {
            // 用户状态不正常扣减失败!
            CSAppException.apperr(CrmUserException.CRM_USER_406);
        }
        if (Integer.parseInt(reqData.getScoreValue()) > Integer.parseInt(score))
        {
            // 用户积分不足扣减失败!
            CSAppException.apperr(CrmUserException.CRM_USER_407);
        }
        //银联积分消费需求效验用户是否已绑定银联卡
        if(StringUtils.isNotBlank(inModeCode) && "40".equals(inModeCode))
        {
            IData params = new DataMap();
            // 判断当前是否有有效的签约
            params.put("RSRV_VALUE_CODE","BANKBIND");
            params.put("USER_ID",reqData.getUca().getUserId());
            IDataset BankInfos = UserBankMainSignInfoQry.querySnBindBankByCardNo(params);
            if (IDataUtil.isEmpty(BankInfos))
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户未绑定，请先绑定银联卡，再进行积分消费！");
            }
        }

        // 主台账
        MainTradeData mainList = bd.getMainTradeData();
        mainList.setRsrvStr1(score); // 用户原积分
        mainList.setRsrvStr2(reqData.getScoreValue());// 扣减积分值
        mainList.setRsrvStr3("ZZ");// 积分类型
        mainList.setRsrvStr4(reqData.getOrderNo()); // 大订单编号
        mainList.setRsrvStr5(reqData.getAcceptTime());// 扣减日期和时间
        mainList.setRsrvStr6(reqData.getUca().getSerialNumber());// 客户手机号码
        mainList.setRsrvStr7(reqData.getChannelTradeId());// 流水
        mainList.setRsrvStr8(reqData.getChannelAcceptTime());// 订单日期和时间
        if (StringUtils.isNotBlank(inModeCode) && "WG".equals(inModeCode))
        {
            mainList.setRsrvStr9("WG");
        }
        else if (StringUtils.isNotBlank(inModeCode) && "ES".equals(inModeCode))
        {
            mainList.setRsrvStr9("ES");
        }
        else
        {
            mainList.setRsrvStr9("Z");
        }
        mainList.setRsrvStr10(reqData.getOprt());// 客户订购时间
        if (StringUtils.isNotBlank(inModeCode) && "WG".equals(inModeCode))
        {
            mainList.setRemark("网购平台积分兑换");
        }
        else if (StringUtils.isNotBlank(inModeCode) && "40".equals(inModeCode))
        {
            mainList.setRemark("积分兑换(银联积分消费)");
            mainList.setServReqId(reqData.getPara3());//用主台帐serv_req_id字段存放商户名称，下发短信用
        }
        else
        {
            mainList.setRemark("积分兑换(本地业务平台)");
        }
        // 登记积分台账
        createScoreTradeData(bd, reqData, score);
    }

    public void createScoreTradeData(BusiTradeData bd, TrainScoreDeductRequestData reqData, String score) throws Exception
    {
        ScoreTradeData scoreData = new ScoreTradeData();

        scoreData.setUserId(reqData.getUca().getUserId());
        scoreData.setSerialNumber(reqData.getUca().getSerialNumber());
        scoreData.setIdType("0");
        scoreData.setScoreTypeCode("ZZ");
        scoreData.setYearId("ZZZZ");
        scoreData.setEndCycleId("-1");
        scoreData.setStartCycleId("-1");
        scoreData.setScore(score);
        scoreData.setScoreChanged("-" + reqData.getScoreValue());// 扣减积分值
        scoreData.setValueChanged("0");
        scoreData.setScoreTag("1");
        scoreData.setCancelTag("0");
        scoreData.setRsrvStr3("Z");
        scoreData.setRsrvStr4(reqData.getPara1());
        scoreData.setRsrvStr5(reqData.getPara2());
        scoreData.setRsrvStr6(reqData.getPara3());
        scoreData.setRsrvStr7(reqData.getPara4());
        scoreData.setRsrvStr8(reqData.getPara5());
        scoreData.setRsrvStr9(reqData.getPara6());
        scoreData.setRsrvStr10(reqData.getScoreValue());
        String inModeCode = this.getVisit().getInModeCode();
        if (StringUtils.isNotBlank(inModeCode) && "WG".equals(inModeCode))
        {
            scoreData.setRemark("网购平台积分兑换");
        }
        else if (StringUtils.isNotBlank(inModeCode) && "40".equals(inModeCode))
        {
        	scoreData.setRemark("积分兑换(银联积分消费)");
        }
        else
        {
            scoreData.setRemark("积分兑换(本地业务平台)");
        }

        bd.add(reqData.getUca().getSerialNumber(), scoreData);
    }
}
