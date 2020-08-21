
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata.RailWayVipServerReqData;

public class RailWayVipServerTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        RailWayVipServerReqData reqData = (RailWayVipServerReqData) btd.getRD();

        createTradeMain(btd, reqData);

        createTradeScore(btd, reqData);

        createTradeOther(btd, reqData);

    }

    public void createTradeMain(BusiTradeData btd, RailWayVipServerReqData reqData) throws Exception
    {

        String noticeContent = "";
        String consumeScore = reqData.getConsumeScore();
        noticeContent = "尊贵的全球通VIP客户，欢迎您使用全球通VIP俱乐部火车站贵宾服务，您本次服务扣取积分" + consumeScore + "分，祝您旅途平安！";

        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1("0"); // 固定值0
        mainTradeData.setRsrvStr2("-" + consumeScore); // 实扣积分框中值的负数
        mainTradeData.setRsrvStr3(reqData.getThisSvccount()); // 随行人数+本人
        mainTradeData.setRsrvStr4(reqData.getSvcLevel()); // 服务类别值
        mainTradeData.setRsrvStr5(reqData.getAttendants()); // 随行人总数框值
        mainTradeData.setRsrvStr6(reqData.getVerifyMode()); // 校验方式值
        mainTradeData.setRsrvStr7(reqData.getAcceptNum()); // 受理单编号的值
        mainTradeData.setRsrvStr8(reqData.getScore());
        mainTradeData.setRsrvStr9(noticeContent);

    }

    /**
     * 拼其它台帐
     * 
     * @author zhuyu
     * @param btd
     * @param reqData
     * @throws Exception
     */
    protected void createTradeOther(BusiTradeData btd, RailWayVipServerReqData reqData) throws Exception
    {
        UcaData ucaData = reqData.getUca();
        String userId = ucaData.getUserId();
        IDataset infos = UserOtherInfoQry.getUserOther(userId, "AREM");
        if (IDataUtil.isNotEmpty(infos))
        {
            int consume_score = Integer.valueOf(reqData.getConsumeScore()); // 需要扣减的积分
            int svcScore = Integer.valueOf(reqData.getSvcScore()); // 服务积分
            // 剩余的服务积分
            int serviceScore = svcScore - consume_score;
            OtherTradeData otherTrade = new OtherTradeData();
            otherTrade.setInstId(SeqMgr.getInstId());
            otherTrade.setStartDate(SysDateMgr.getSysTime());
            otherTrade.setEndDate(SysDateMgr.getLastDayOfThisYear());
            otherTrade.setUserId(userId);
            otherTrade.setRsrvValue("0"); // 默认先扣服务积分
            otherTrade.setRsrvValueCode("FWJF");
            otherTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            if (serviceScore > 0)
            {
                otherTrade.setRsrvStr1(serviceScore + "");
            }
            else
            {
                otherTrade.setRsrvStr1(svcScore + ""); // 如果服务积分不够直接扣掉全部的服务积分
            }
            otherTrade.setRsrvStr6(CSBizBean.getVisit().getStaffId());
            otherTrade.setRsrvStr7(CSBizBean.getVisit().getDepartId());
            btd.add(ucaData.getSerialNumber(), otherTrade);
        }

    }

    /**
     * 拼积分台帐
     * 
     * @author zhuyu
     * @param btd
     * @param reqData
     * @throws Exception
     */
    public void createTradeScore(BusiTradeData btd, RailWayVipServerReqData reqData) throws Exception
    {
        String svcScore = reqData.getSvcScore(); // 服务积分
        String consumeScore = reqData.getConsumeScore(); // 需要扣减的积分

        if (Integer.valueOf(svcScore) - Integer.valueOf(consumeScore) > 0)// 如果有足够的服务积分则不需要扣实际积分
        {
        }
        else
        {
            ScoreTradeData scoreTradeData = new ScoreTradeData();
            UcaData ucaData = reqData.getUca();
            scoreTradeData.setUserId(ucaData.getUserId());
            scoreTradeData.setSerialNumber(ucaData.getSerialNumber());
            scoreTradeData.setScore(reqData.getScore()); // 用户积分
            scoreTradeData.setScoreChanged("-" + consumeScore); // 应扣积分（积分异动）
            scoreTradeData.setValueChanged("-" + consumeScore);
            scoreTradeData.setStartCycleId(SysDateMgr.getNowCyc());
            scoreTradeData.setEndCycleId(SysDateMgr.getLastCycle());
            scoreTradeData.setYearId(SysDateMgr.getNowYear());
            scoreTradeData.setScoreTag("0");
            scoreTradeData.setRuleId("0");
            scoreTradeData.setActionCount("0");
            scoreTradeData.setIdType("0");
            scoreTradeData.setCancelTag("0");
            scoreTradeData.setScoreTypeCode("00"); // 用户消费积分td_s_scoretype
            scoreTradeData.setRemark("火车站VIP服务积分变更");
            btd.add(ucaData.getSerialNumber(), scoreTradeData);
        }

    }

}
