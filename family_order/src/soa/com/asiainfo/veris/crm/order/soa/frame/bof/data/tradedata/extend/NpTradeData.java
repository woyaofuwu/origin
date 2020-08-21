
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class NpTradeData extends BaseTradeData
{

    private String userId;

    private String tradeTypeCode;

    private String npServiceType;

    private String serialNumber;

    private String flowId;

    private String messageId;

    private String brcId;

    private String msgCmdCode;

    private String md5;

    private String portOutNetid;

    private String portInNetid;

    private String homeNetid;

    private String bNpCardType;

    private String aNpCardType;

    private String custName;

    private String credType;

    private String psptId;

    private String phone;

    private String actorCustName;

    private String actorCredType;

    private String actorPsptId;

    private String npStartDate;

    private String createTime;

    private String bookSendTime;

    private String sendTimes;

    private String resultCode;

    private String resultMessage;

    private String errorMessage;

    private String cancelTag;

    private String state;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String npTag;

    private String applyDate;

    private String npDestroyTime;

    private String portInDate;

    private String portOutDate;

    private String modifyTag;
    
    private String authCode;
    
    private String authCodeExpired;
    
    private String updateDepartId;
    
	private String updateStaffId;
    
    private String eparchyCode;

    public NpTradeData()
    {

    }

    public NpTradeData(IData data)
    {
        this.userId = data.getString("USER_ID");
        this.tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        this.npServiceType = data.getString("NP_SERVICE_TYPE");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.flowId = data.getString("FLOW_ID");
        this.messageId = data.getString("MESSAGE_ID");
        this.brcId = data.getString("BRC_ID");
        this.msgCmdCode = data.getString("MSG_CMD_CODE");
        this.md5 = data.getString("MD5");
        this.portOutNetid = data.getString("PORT_OUT_NETID");
        this.portInNetid = data.getString("PORT_IN_NETID");
        this.homeNetid = data.getString("HOME_NETID");
        this.bNpCardType = data.getString("B_NP_CARD_TYPE");
        this.aNpCardType = data.getString("A_NP_CARD_TYPE");
        this.custName = data.getString("CUST_NAME");
        this.credType = data.getString("CRED_TYPE");
        this.psptId = data.getString("PSPT_ID");
        this.phone = data.getString("PHONE");
        this.actorCustName = data.getString("ACTOR_CUST_NAME");
        this.actorCredType = data.getString("ACTOR_CRED_TYPE");
        this.actorPsptId = data.getString("ACTOR_PSPT_ID");
        this.npStartDate = data.getString("NP_START_DATE");
        this.createTime = data.getString("CREATE_TIME");
        this.bookSendTime = data.getString("BOOK_SEND_TIME");
        this.sendTimes = data.getString("SEND_TIMES");
        this.resultCode = data.getString("RESULT_CODE");
        this.resultMessage = data.getString("RESULT_MESSAGE");
        this.errorMessage = data.getString("ERROR_MESSAGE");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.state = data.getString("STATE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.npTag = data.getString("NP_TAG");
        this.applyDate = data.getString("APPLY_DATE");
        this.npDestroyTime = data.getString("NP_DESTROY_TIME");
        this.portInDate = data.getString("PORT_IN_DATE");
        this.portOutDate = data.getString("PORT_OUT_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.authCode = data.getString("AUTH_CODE");
        this.authCodeExpired = data.getString("AUTH_CODE_EXPIRED");
        this.updateDepartId = data.getString("UPDATE_DEPART_ID");
        this.updateStaffId = data.getString("UPDATE_STAFF_ID");
        this.eparchyCode = data.getString("EPARCHY_CODE");
    }

    public NpTradeData clone()
    {
        NpTradeData npTradeData = new NpTradeData();
        npTradeData.setUserId(this.getUserId());
        npTradeData.setTradeTypeCode(this.getTradeTypeCode());
        npTradeData.setNpServiceType(this.getNpServiceType());
        npTradeData.setSerialNumber(this.getSerialNumber());
        npTradeData.setFlowId(this.getFlowId());
        npTradeData.setMessageId(this.getMessageId());
        npTradeData.setBrcId(this.getBrcId());
        npTradeData.setMsgCmdCode(this.getMsgCmdCode());
        npTradeData.setMd5(this.getMd5());
        npTradeData.setPortOutNetid(this.getPortOutNetid());
        npTradeData.setPortInNetid(this.getPortInNetid());
        npTradeData.setHomeNetid(this.getHomeNetid());
        npTradeData.setBNpCardType(this.getBNpCardType());
        npTradeData.setANpCardType(this.getANpCardType());
        npTradeData.setCustName(this.getCustName());
        npTradeData.setCredType(this.getCredType());
        npTradeData.setPsptId(this.getPsptId());
        npTradeData.setPhone(this.getPhone());
        npTradeData.setActorCustName(this.getActorCustName());
        npTradeData.setActorCredType(this.getActorCredType());
        npTradeData.setActorPsptId(this.getActorPsptId());
        npTradeData.setNpStartDate(this.getNpStartDate());
        npTradeData.setCreateTime(this.getCreateTime());
        npTradeData.setBookSendTime(this.getBookSendTime());
        npTradeData.setSendTimes(this.getSendTimes());
        npTradeData.setResultCode(this.getResultCode());
        npTradeData.setResultMessage(this.getResultMessage());
        npTradeData.setErrorMessage(this.getErrorMessage());
        npTradeData.setCancelTag(this.getCancelTag());
        npTradeData.setState(this.getState());
        npTradeData.setRemark(this.getRemark());
        npTradeData.setRsrvStr1(this.getRsrvStr1());
        npTradeData.setRsrvStr2(this.getRsrvStr2());
        npTradeData.setRsrvStr3(this.getRsrvStr3());
        npTradeData.setRsrvStr4(this.getRsrvStr4());
        npTradeData.setRsrvStr5(this.getRsrvStr5());
        npTradeData.setNpTag(this.getNpTag());
        npTradeData.setApplyDate(this.getApplyDate());
        npTradeData.setNpDestroyTime(this.getNpDestroyTime());
        npTradeData.setPortInDate(this.getPortInDate());
        npTradeData.setPortOutDate(this.getPortOutDate());
        npTradeData.setModifyTag(this.getModifyTag());
        npTradeData.setAuthCode(this.getAuthCode());
        npTradeData.setAuthCodeExpired(this.getAuthCodeExpired());
        npTradeData.setUpdateDepartId(this.getUpdateDepartId());
        npTradeData.setUpdateStaffId(this.getUpdateStaffId());
        npTradeData.setEparchyCode(this.getEparchyCode());
        
        return npTradeData;
    }

    public String getUpdateDepartId() {
		return updateDepartId;
	}

	public void setUpdateDepartId(String updateDepartId) {
		this.updateDepartId = updateDepartId;
	}

	public String getUpdateStaffId() {
		return updateStaffId;
	}

	public void setUpdateStaffId(String updateStaffId) {
		this.updateStaffId = updateStaffId;
	}

	public String getEparchyCode() {
		return eparchyCode;
	}

	public void setEparchyCode(String eparchyCode) {
		this.eparchyCode = eparchyCode;
	}
    
    public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	
	public String getAuthCodeExpired() {
		return authCodeExpired;
	}

	public void setAuthCodeExpired(String authCodeExpired) {
		this.authCodeExpired = authCodeExpired;
	}
    
    public String getActorCredType()
    {
        return this.actorCredType;
    }

    public String getActorCustName()
    {
        return this.actorCustName;
    }

    public String getActorPsptId()
    {
        return this.actorPsptId;
    }

    public String getANpCardType()
    {
        return this.aNpCardType;
    }

    public String getApplyDate()
    {
        return this.applyDate;
    }

    public String getBNpCardType()
    {
        return this.bNpCardType;
    }

    public String getBookSendTime()
    {
        return this.bookSendTime;
    }

    public String getBrcId()
    {
        return this.brcId;
    }

    public String getCancelTag()
    {
        return this.cancelTag;
    }

    public String getCreateTime()
    {
        return this.createTime;
    }

    public String getCredType()
    {
        return this.credType;
    }

    public String getCustName()
    {
        return this.custName;
    }

    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    public String getFlowId()
    {
        return this.flowId;
    }

    public String getHomeNetid()
    {
        return this.homeNetid;
    }

    public String getMd5()
    {
        return this.md5;
    }

    public String getMessageId()
    {
        return this.messageId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getMsgCmdCode()
    {
        return this.msgCmdCode;
    }

    public String getNpDestroyTime()
    {
        return this.npDestroyTime;
    }

    public String getNpServiceType()
    {
        return this.npServiceType;
    }

    public String getNpStartDate()
    {
        return this.npStartDate;
    }

    public String getNpTag()
    {
        return this.npTag;
    }

    public String getPhone()
    {
        return this.phone;
    }

    public String getPortInDate()
    {
        return this.portInDate;
    }

    public String getPortInNetid()
    {
        return this.portInNetid;
    }

    public String getPortOutDate()
    {
        return this.portOutDate;
    }

    public String getPortOutNetid()
    {
        return this.portOutNetid;
    }

    public String getPsptId()
    {
        return this.psptId;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getResultCode()
    {
        return this.resultCode;
    }

    public String getResultMessage()
    {
        return this.resultMessage;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return this.rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return this.rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return this.rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return this.rsrvStr5;
    }

    public String getSendTimes()
    {
        return this.sendTimes;
    }

    public String getSerialNumber()
    {
        return this.serialNumber;
    }

    public String getState()
    {
        return this.state;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_NP";
    }

    public String getTradeTypeCode()
    {
        return this.tradeTypeCode;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setActorCredType(String actorCredType)
    {
        this.actorCredType = actorCredType;
    }

    public void setActorCustName(String actorCustName)
    {
        this.actorCustName = actorCustName;
    }

    public void setActorPsptId(String actorPsptId)
    {
        this.actorPsptId = actorPsptId;
    }

    public void setANpCardType(String aNpCardType)
    {
        this.aNpCardType = aNpCardType;
    }

    public void setApplyDate(String applyDate)
    {
        this.applyDate = applyDate;
    }

    public void setBNpCardType(String bNpCardType)
    {
        this.bNpCardType = bNpCardType;
    }

    public void setBookSendTime(String bookSendTime)
    {
        this.bookSendTime = bookSendTime;
    }

    public void setBrcId(String brcId)
    {
        this.brcId = brcId;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public void setCredType(String credType)
    {
        this.credType = credType;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public void setFlowId(String flowId)
    {
        this.flowId = flowId;
    }

    public void setHomeNetid(String homeNetid)
    {
        this.homeNetid = homeNetid;
    }

    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setMsgCmdCode(String msgCmdCode)
    {
        this.msgCmdCode = msgCmdCode;
    }

    public void setNpDestroyTime(String npDestroyTime)
    {
        this.npDestroyTime = npDestroyTime;
    }

    public void setNpServiceType(String npServiceType)
    {
        this.npServiceType = npServiceType;
    }

    public void setNpStartDate(String npStartDate)
    {
        this.npStartDate = npStartDate;
    }

    public void setNpTag(String npTag)
    {
        this.npTag = npTag;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPortInDate(String portInDate)
    {
        this.portInDate = portInDate;
    }

    public void setPortInNetid(String portInNetid)
    {
        this.portInNetid = portInNetid;
    }

    public void setPortOutDate(String portOutDate)
    {
        this.portOutDate = portOutDate;
    }

    public void setPortOutNetid(String portOutNetid)
    {
        this.portOutNetid = portOutNetid;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setSendTimes(String sendTimes)
    {
        this.sendTimes = sendTimes;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("USER_ID", this.userId);
        data.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        data.put("NP_SERVICE_TYPE", this.npServiceType);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("FLOW_ID", this.flowId);
        data.put("MESSAGE_ID", this.messageId);
        data.put("BRC_ID", this.brcId);
        data.put("MSG_CMD_CODE", this.msgCmdCode);
        data.put("MD5", this.md5);
        data.put("PORT_OUT_NETID", this.portOutNetid);
        data.put("PORT_IN_NETID", this.portInNetid);
        data.put("HOME_NETID", this.homeNetid);
        data.put("B_NP_CARD_TYPE", this.bNpCardType);
        data.put("A_NP_CARD_TYPE", this.aNpCardType);
        data.put("CUST_NAME", this.custName);
        data.put("CRED_TYPE", this.credType);
        data.put("PSPT_ID", this.psptId);
        data.put("PHONE", this.phone);
        data.put("ACTOR_CUST_NAME", this.actorCustName);
        data.put("ACTOR_CRED_TYPE", this.actorCredType);
        data.put("ACTOR_PSPT_ID", this.actorPsptId);
        data.put("NP_START_DATE", this.npStartDate);
        data.put("CREATE_TIME", this.createTime);
        data.put("BOOK_SEND_TIME", this.bookSendTime);
        data.put("SEND_TIMES", this.sendTimes);
        data.put("RESULT_CODE", this.resultCode);
        data.put("RESULT_MESSAGE", this.resultMessage);
        data.put("ERROR_MESSAGE", this.errorMessage);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("STATE", this.state);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("NP_TAG", this.npTag);
        data.put("APPLY_DATE", this.applyDate);
        data.put("NP_DESTROY_TIME", this.npDestroyTime);
        data.put("PORT_IN_DATE", this.portInDate);
        data.put("PORT_OUT_DATE", this.portOutDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("AUTH_CODE", this.authCode);
        data.put("AUTH_CODE_EXPIRED", this.authCodeExpired);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
