
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata.LockUserPwdReqData;

/**
 * 用户密码锁定写台账
 * 
 * @author liutt
 */
public class LockUserPwdTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        LockUserPwdReqData reqData = (LockUserPwdReqData) btd.getRD();
        createMaintrade(btd);
        createUserOther(btd);
        recordMessage(reqData);// 发送密码锁定提醒短信
    }

    private void createMaintrade(BusiTradeData btd) throws Exception
    {
        LockUserPwdReqData reqData = (LockUserPwdReqData) btd.getRD();
        btd.getMainTradeData().setRsrvStr1(reqData.getOldTradeTypeCode());
        btd.getMainTradeData().setInModeCode(reqData.getInModeCode());
        btd.getMainTradeData().setRsrvStr2(reqData.getCheckMode());// 本人证件号码 1=用户密码
        btd.getMainTradeData().setNetTypeCode(reqData.getNetTypeCode());
        btd.getMainTradeData().setRemark("锁定密码");
    }

    private void createUserOther(BusiTradeData btd) throws Exception
    {

        LockUserPwdReqData reqData = (LockUserPwdReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        String tradeId = btd.getTradeId();

        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("PARTITION_ID", Long.parseLong(userId) % 10000);
        inparams.put("USER_ID", userId);
        inparams.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        inparams.put("INST_ID", SeqMgr.getInstId());
        inparams.put("RSRV_VALUE_CODE", "PWDLOCK");
        inparams.put("RSRV_VALUE", tradeId);
        inparams.put("START_DATE", reqData.getAcceptTime());// 开始锁定时间为当前系统时间
        inparams.put("END_DATE", SysDateMgr.getLastSecond(SysDateMgr.getTomorrowDate()));// 锁定一个自然日，第二天自动解锁
        inparams.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparams.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparams.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        inparams.put("UPDATE_TIME", reqData.getAcceptTime());
        inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparams.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparams.put("REMARK", "密码连续3次输入错误锁定");
        Dao.insert("TF_F_USER_OTHER", inparams);

    }

    private void recordMessage(LockUserPwdReqData reqData) throws Exception
    {

        String noticeContent = "提醒服务：您好！您今日累计输入错误服务密码已达" + reqData.getErrorNum() + "次，请明天再试。服务密码关系您个人信息安全，请妥善保管。中国移动";
        IData param = new DataMap();
        param.put("IN_MODE_CODE", "3");// 海南老系统写死为3，why?
        param.put("NOTICE_CONTENT", noticeContent);
        param.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("RECV_OBJECT", reqData.getUca().getSerialNumber());
        param.put("RECV_ID", reqData.getUca().getUserId());// 被叫对象标识:传用户标识
        param.put("SMS_PRIORITY", 5000);// 短信优先级
        param.put("SMS_KIND_CODE", "08");
        param.put("CHAN_ID", "C006");
        param.put("DEAL_STATE", "15");
        param.put("REMARK", "发送密码锁定提醒短信");
        SmsSend.insSms(param);

    }
}
