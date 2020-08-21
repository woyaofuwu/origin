
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class SmsTradeData extends BaseTradeData
{
    private String brandCode;

    private String chanId;

    private String day;

    private String dealDepartid;

    private String dealStaffid;

    private String dealState;

    private String dealTime;

    private String eparchyCode;

    private String forceEndTime;

    private String forceObject;

    private String forceReferCount;

    private String forceStartTime;

    private String inModeCode;

    private String month;

    private String noticeContent;

    private String noticeContentType;

    private String recvId;

    private String recvObject;

    private String recvObjectType;

    private String referedCount;

    private String referDepartId;

    private String referStaffId;

    private String referTime;

    private String remark;

    private String revc1;

    private String revc2;

    private String revc3;

    private String revc4;

    private String sendCountCode;

    private String sendObjectCode;

    private String sendTimeCode;

    private String smsKindCode;

    private String smsNetTag;

    private String smsNoticeId;

    private String smsPriority;

    private String smsTypeCode;

    private String cancelTag;

    public SmsTradeData()
    {

    }

    public SmsTradeData(IData data)
    {
        this.setBrandCode(data.getString("BRAND_CODE"));
        this.setChanId(data.getString("CHAN_ID"));
        this.setDay(data.getString("DAY"));
        this.setDealDepartid(data.getString("DEAL_DEPARTID"));
        this.setDealStaffid(data.getString("DEAL_STAFFID"));
        this.setDealState(data.getString("DEAL_STATE"));
        this.setDealTime(data.getString("DEAL_TIME"));
        this.setEparchyCode(data.getString("EPARCHY_CODE"));
        this.setForceEndTime(data.getString("FORCE_END_TIME"));
        this.setForceObject(data.getString("FORCE_OBJECT"));
        this.setForceReferCount(data.getString("FORCE_REFER_COUNT"));
        this.setForceStartTime(data.getString("FORCE_START_TIME"));
        this.setInModeCode(data.getString("IN_MODE_CODE"));
        this.setMonth(data.getString("MONTH"));
        this.setNoticeContent(data.getString("NOTICE_CONTENT"));
        this.setNoticeContentType(data.getString("NOTICE_CONTENT_TYPE"));
        this.setRecvId(data.getString("RECV_ID"));
        this.setRecvObject(data.getString("RECV_OBJECT"));
        this.setRecvObjectType(data.getString("RECV_OBJECT_TYPE"));
        this.setReferedCount(data.getString("REFERED_COUNT"));
        this.setReferDepartId(data.getString("REFER_DEPART_ID"));
        this.setReferStaffId(data.getString("REFER_STAFF_ID"));
        this.setReferTime(data.getString("REFER_TIME"));
        this.setRemark(data.getString("REMARK"));
        this.setRevc1(data.getString("REVC1"));
        this.setRevc2(data.getString("REVC2"));
        this.setRevc3(data.getString("REVC3"));
        this.setRevc4(data.getString("REVC4"));
        this.setSendCountCode(data.getString("SEND_COUNT_CODE"));
        this.setSendObjectCode(data.getString("SEND_OBJECT_CODE"));
        this.setSendTimeCode(data.getString("SEND_TIME_CODE"));
        this.setSmsKindCode(data.getString("SMS_KIND_CODE"));
        this.setSmsNetTag(data.getString("SMS_NET_TAG"));
        this.setSmsNoticeId(data.getString("SMS_NOTICE_ID"));
        this.setSmsPriority(data.getString("SMS_PRIORITY"));
        this.setSmsTypeCode(data.getString("SMS_TYPE_CODE"));
        this.setCancelTag(data.getString("CANCEL_TAG"));
    }

    @Override
    public SmsTradeData clone()
    {
        SmsTradeData tradeSms = new SmsTradeData();
        tradeSms.setBrandCode(this.getBrandCode());
        tradeSms.setChanId(this.getChanId());
        tradeSms.setDay(this.getDay());
        tradeSms.setDealDepartid(this.getDealDepartid());
        tradeSms.setDealStaffid(this.getDealStaffid());
        tradeSms.setDealState(this.getDealState());
        tradeSms.setDealTime(this.getDealTime());
        tradeSms.setEparchyCode(this.getEparchyCode());
        tradeSms.setForceEndTime(this.getForceEndTime());
        tradeSms.setForceObject(this.getForceObject());
        tradeSms.setForceReferCount(this.getForceReferCount());
        tradeSms.setForceStartTime(this.getForceStartTime());
        tradeSms.setInModeCode(this.getInModeCode());
        tradeSms.setMonth(this.getMonth());
        tradeSms.setNoticeContent(this.getNoticeContent());
        tradeSms.setNoticeContentType(this.getNoticeContentType());
        tradeSms.setRecvId(this.getRecvId());
        tradeSms.setRecvObject(this.getRecvObject());
        tradeSms.setRecvObjectType(this.getRecvObjectType());
        tradeSms.setReferedCount(this.getReferedCount());
        tradeSms.setReferDepartId(this.getReferDepartId());
        tradeSms.setReferStaffId(this.getReferStaffId());
        tradeSms.setReferTime(this.getReferTime());
        tradeSms.setRemark(this.getRemark());
        tradeSms.setRevc1(this.getRevc1());
        tradeSms.setRevc2(this.getRevc2());
        tradeSms.setRevc3(this.getRevc3());
        tradeSms.setRevc4(this.getRevc4());
        tradeSms.setSendCountCode(this.getSendCountCode());
        tradeSms.setSendObjectCode(this.getSendObjectCode());
        tradeSms.setSendTimeCode(this.getSendTimeCode());
        tradeSms.setSmsKindCode(this.getSmsKindCode());
        tradeSms.setSmsNetTag(this.getSmsNetTag());
        tradeSms.setSmsNoticeId(this.getSmsNoticeId());
        tradeSms.setSmsPriority(this.getSmsPriority());
        tradeSms.setSmsTypeCode(this.getSmsTypeCode());
        tradeSms.setCancelTag(this.getCancelTag());
        return tradeSms;
    }

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getChanId()
    {
        return chanId;
    }

    public String getDay()
    {
        return day;
    }

    public String getDealDepartid()
    {
        return dealDepartid;
    }

    public String getDealStaffid()
    {
        return dealStaffid;
    }

    public String getDealState()
    {
        return dealState;
    }

    public String getDealTime()
    {
        return dealTime;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getForceEndTime()
    {
        return forceEndTime;
    }

    public String getForceObject()
    {
        return forceObject;
    }

    public String getForceReferCount()
    {
        return forceReferCount;
    }

    public String getForceStartTime()
    {
        return forceStartTime;
    }

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getMonth()
    {
        return month;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public String getNoticeContentType()
    {
        return noticeContentType;
    }

    public String getRecvId()
    {
        return recvId;
    }

    public String getRecvObject()
    {
        return recvObject;
    }

    public String getRecvObjectType()
    {
        return recvObjectType;
    }

    public String getReferDepartId()
    {
        return referDepartId;
    }

    public String getReferedCount()
    {
        return referedCount;
    }

    public String getReferStaffId()
    {
        return referStaffId;
    }

    public String getReferTime()
    {
        return referTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRevc1()
    {
        return revc1;
    }

    public String getRevc2()
    {
        return revc2;
    }

    public String getRevc3()
    {
        return revc3;
    }

    public String getRevc4()
    {
        return revc4;
    }

    public String getSendCountCode()
    {
        return sendCountCode;
    }

    public String getSendObjectCode()
    {
        return sendObjectCode;
    }

    public String getSendTimeCode()
    {
        return sendTimeCode;
    }

    public String getSmsKindCode()
    {
        return smsKindCode;
    }

    public String getSmsNetTag()
    {
        return smsNetTag;
    }

    public String getSmsNoticeId()
    {
        return smsNoticeId;
    }

    public String getSmsPriority()
    {
        return smsPriority;
    }

    public String getSmsTypeCode()
    {
        return smsTypeCode;
    }

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return "TF_B_TRADE_SMS";
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setChanId(String chanId)
    {
        this.chanId = chanId;
    }

    public void setDay(String day)
    {
        this.day = day;
    }

    public void setDealDepartid(String dealDepartid)
    {
        this.dealDepartid = dealDepartid;
    }

    public void setDealStaffid(String dealStaffid)
    {
        this.dealStaffid = dealStaffid;
    }

    public void setDealState(String dealState)
    {
        this.dealState = dealState;
    }

    public void setDealTime(String dealTime)
    {
        this.dealTime = dealTime;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setForceEndTime(String forceEndTime)
    {
        this.forceEndTime = forceEndTime;
    }

    public void setForceObject(String forceObject)
    {
        this.forceObject = forceObject;
    }

    public void setForceReferCount(String forceReferCount)
    {
        this.forceReferCount = forceReferCount;
    }

    public void setForceStartTime(String forceStartTime)
    {
        this.forceStartTime = forceStartTime;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public void setNoticeContentType(String noticeContentType)
    {
        this.noticeContentType = noticeContentType;
    }

    public void setRecvId(String recvId)
    {
        this.recvId = recvId;
    }

    public void setRecvObject(String recvObject)
    {
        this.recvObject = recvObject;
    }

    public void setRecvObjectType(String recvObjectType)
    {
        this.recvObjectType = recvObjectType;
    }

    public void setReferDepartId(String referDepartId)
    {
        this.referDepartId = referDepartId;
    }

    public void setReferedCount(String referedCount)
    {
        this.referedCount = referedCount;
    }

    public void setReferStaffId(String referStaffId)
    {
        this.referStaffId = referStaffId;
    }

    public void setReferTime(String referTime)
    {
        this.referTime = referTime;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRevc1(String revc1)
    {
        this.revc1 = revc1;
    }

    public void setRevc2(String revc2)
    {
        this.revc2 = revc2;
    }

    public void setRevc3(String revc3)
    {
        this.revc3 = revc3;
    }

    public void setRevc4(String revc4)
    {
        this.revc4 = revc4;
    }

    public void setSendCountCode(String sendCountCode)
    {
        this.sendCountCode = sendCountCode;
    }

    public void setSendObjectCode(String sendObjectCode)
    {
        this.sendObjectCode = sendObjectCode;
    }

    public void setSendTimeCode(String sendTimeCode)
    {
        this.sendTimeCode = sendTimeCode;
    }

    public void setSmsKindCode(String smsKindCode)
    {
        this.smsKindCode = smsKindCode;
    }

    public void setSmsNetTag(String smsNetTag)
    {
        this.smsNetTag = smsNetTag;
    }

    public void setSmsNoticeId(String smsNoticeId)
    {
        this.smsNoticeId = smsNoticeId;
    }

    public void setSmsPriority(String smsPriority)
    {
        this.smsPriority = smsPriority;
    }

    public void setSmsTypeCode(String smsTypeCode)
    {
        this.smsTypeCode = smsTypeCode;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("BRAND_CODE", this.brandCode);
        data.put("CHAN_ID", this.chanId);
        data.put("DAY", this.day);
        data.put("DEAL_DEPARTID", this.dealDepartid);
        data.put("DEAL_STAFFID", this.dealStaffid);
        data.put("DEAL_STATE", this.dealState);
        data.put("DEAL_TIME", this.dealTime);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("FORCE_END_TIME", this.forceEndTime);
        data.put("FORCE_OBJECT", this.forceObject);
        data.put("FORCE_REFER_COUNT", this.forceReferCount);
        data.put("FORCE_START_TIME", this.forceStartTime);
        data.put("IN_MODE_CODE", this.inModeCode);
        data.put("MONTH", this.month);
        data.put("NOTICE_CONTENT", this.noticeContent);
        data.put("NOTICE_CONTENT_TYPE", this.noticeContentType);
        data.put("RECV_ID", this.recvId);
        data.put("RECV_OBJECT", this.recvObject);
        data.put("RECV_OBJECT_TYPE", this.recvObjectType);
        data.put("REFERED_COUNT", this.referedCount);
        data.put("REFER_DEPART_ID", this.referDepartId);
        data.put("REFER_STAFF_ID", this.referStaffId);
        data.put("REFER_TIME", this.referTime);
        data.put("REMARK", this.remark);
        data.put("REVC1", this.revc1);
        data.put("REVC2", this.revc2);
        data.put("REVC3", this.revc3);
        data.put("REVC4", this.revc4);
        data.put("SEND_COUNT_CODE", this.sendCountCode);
        data.put("SEND_OBJECT_CODE", this.sendObjectCode);
        data.put("SEND_TIME_CODE", this.sendTimeCode);
        data.put("SMS_KIND_CODE", this.smsKindCode);
        data.put("SMS_NET_TAG", this.smsNetTag);
        data.put("SMS_NOTICE_ID", this.smsNoticeId);
        data.put("SMS_PRIORITY", this.smsPriority);
        data.put("SMS_TYPE_CODE", this.smsTypeCode);
        data.put("CANCEL_TAG", this.cancelTag);

        return data;
    }

}
