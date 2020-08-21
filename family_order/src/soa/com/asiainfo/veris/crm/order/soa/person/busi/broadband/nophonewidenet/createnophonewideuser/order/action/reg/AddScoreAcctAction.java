
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScorePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * Copyright: Copyright 2014 Asiainfo
 * 
 * @ClassName: AddScoreAcctAction.java
 * @Description: 个人产品新增积分计划action
 * @version: v1.0.0
 */
public class AddScoreAcctAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        UserTradeData userTradeData = uca.getUser();
        if (userTradeData != null)
        {
            if (BofConst.MODIFY_TAG_ADD.equals(userTradeData.getModifyTag()))
            {
                // 插TF_B_TRADE_INTEGRALACCT TF_B_TRADE_SCORERELATION 和 TF_B_TRADE_INTEGRALPLAN
                String integralAcctId = SeqMgr.getAcctId();
                IntegralAcctTradeData integralAcctTradeData = new IntegralAcctTradeData();
                integralAcctTradeData.setIntegralAcctId(integralAcctId);
                integralAcctTradeData.setIntegralAccountType("0");
                integralAcctTradeData.setName(uca.getCustomer().getCustName());
                integralAcctTradeData.setUserId(uca.getUserId());
                integralAcctTradeData.setContractPhone(uca.getSerialNumber());
                integralAcctTradeData.setPsptId(uca.getCustomer().getPsptId());
                integralAcctTradeData.setUseLimit("0");
                integralAcctTradeData.setStartDate(btd.getRD().getAcceptTime());
                integralAcctTradeData.setEndDate(SysDateMgr.getTheLastTime());
                integralAcctTradeData.setStatus("10A");
                integralAcctTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                btd.add(uca.getSerialNumber(), integralAcctTradeData);

                // TF_B_TRADE_SCORERELATION
                ScoreRelationTradeData scoreRelationTradeData = new ScoreRelationTradeData();

                scoreRelationTradeData.setPayrelationId(SeqMgr.getAcctId());
                scoreRelationTradeData.setUserId(uca.getUserId());
                scoreRelationTradeData.setLimitType("0");
                scoreRelationTradeData.setIntegralAcctId(integralAcctId);
                scoreRelationTradeData.setDefaultTag("1");
                scoreRelationTradeData.setActTag("1");// 1生效 0失效
                scoreRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                scoreRelationTradeData.setStartDate(SysDateMgr.decodeTimestamp(btd.getRD().getAcceptTime(), "yyyyMMdd"));
                scoreRelationTradeData.setEndDate(SysDateMgr.decodeTimestamp(SysDateMgr.getTheLastTime(), "yyyyMMdd"));

                btd.add(uca.getSerialNumber(), scoreRelationTradeData);

                // TF_B_TRADE_INTEGRALPLAN 默认订购一个积分计划
                ScorePlanTradeData scorePlanTradeData = new ScorePlanTradeData();

                scorePlanTradeData.setUserId(uca.getUserId());
                scorePlanTradeData.setStartDate(btd.getRD().getAcceptTime());
                scorePlanTradeData.setEndDate(SysDateMgr.getTheLastTime());
                scorePlanTradeData.setStatus("10A");
                scorePlanTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                scorePlanTradeData.setIntegralAcctId(integralAcctId);
                scorePlanTradeData.setIntegralPlanId("-1");// 积分计划标识 -1表示默认
                scorePlanTradeData.setIntegralPlanInstId(SeqMgr.getInstId());

                btd.add(uca.getSerialNumber(), scorePlanTradeData);
            }
        }
    }
}
