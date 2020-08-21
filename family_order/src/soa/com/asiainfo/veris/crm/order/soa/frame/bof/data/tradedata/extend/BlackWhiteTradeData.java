
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class BlackWhiteTradeData extends BaseTradeData
{
    private String billingType;

    private String bizCode;

    private String bizDesc;

    private String bizInCode;

    private String bizInCodeA;

    private String bizName;

    private String bookDate;

    private String contractId;

    private String ecSerialNumber;

    private String ecUserId;

    private String endDate;

    private String expectTime;

    private String groupId;

    private String modifyTag;

    private String operState;

    private String oprEffTime;

    private String oprSeqId;

    private String platSyncState;

    private String price;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String serialNumber;

    private String serviceId;

    private String servCode;

    private String startDate;

    private String userId;

    private String userTypeCode;

    private String instId;

    private String isNeedPf;

    public BlackWhiteTradeData()
    {

    }

    public BlackWhiteTradeData(IData data)
    {
        this.billingType = data.getString("BILLING_TYPE");
        this.bizCode = data.getString("BIZ_CODE");
        this.bizDesc = data.getString("BIZ_DESC");
        this.bizInCode = data.getString("BIZ_IN_CODE");
        this.bizInCodeA = data.getString("BIZ_IN_CODE_A");
        this.bizName = data.getString("BIZ_NAME");
        this.bookDate = data.getString("BOOK_DATE");
        this.contractId = data.getString("CONTRACT_ID");
        this.ecSerialNumber = data.getString("EC_SERIAL_NUMBER");
        this.ecUserId = data.getString("EC_USER_ID");
        this.endDate = data.getString("END_DATE");
        this.expectTime = data.getString("EXPECT_TIME");
        this.groupId = data.getString("GROUP_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.operState = data.getString("OPER_STATE");
        this.oprEffTime = data.getString("OPR_EFF_TIME");
        this.oprSeqId = data.getString("OPR_SEQ_ID");
        this.platSyncState = data.getString("PLAT_SYNC_STATE");
        this.price = data.getString("PRICE");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.serviceId = data.getString("SERVICE_ID");
        this.servCode = data.getString("SERV_CODE");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.userTypeCode = data.getString("USER_TYPE_CODE");
        this.instId = data.getString("INST_ID");
        this.isNeedPf = data.getString("IS_NEED_PF");
    }

    public BlackWhiteTradeData clone()
    {
        BlackWhiteTradeData blackWhiteTradeData = new BlackWhiteTradeData();

        blackWhiteTradeData.setBillingType(this.getBillingType());
        blackWhiteTradeData.setBizCode(this.getBizCode());
        blackWhiteTradeData.setBizDesc(this.getBizDesc());
        blackWhiteTradeData.setBizInCode(this.getBizInCode());
        blackWhiteTradeData.setBizInCodeA(this.getBizInCodeA());
        blackWhiteTradeData.setBizName(this.getBizName());
        blackWhiteTradeData.setBookDate(this.getBookDate());
        blackWhiteTradeData.setContractId(this.getContractId());
        blackWhiteTradeData.setEcSerialNumber(this.getEcSerialNumber());
        blackWhiteTradeData.setEcUserId(this.getEcUserId());
        blackWhiteTradeData.setEndDate(this.getEndDate());
        blackWhiteTradeData.setExpectTime(this.getExpectTime());
        blackWhiteTradeData.setGroupId(this.getGroupId());
        blackWhiteTradeData.setModifyTag(this.getModifyTag());
        blackWhiteTradeData.setOperState(this.getOperState());
        blackWhiteTradeData.setOprEffTime(this.getOprEffTime());
        blackWhiteTradeData.setOprSeqId(this.getOprSeqId());
        blackWhiteTradeData.setPlatSyncState(this.getPlatSyncState());
        blackWhiteTradeData.setPrice(this.getPrice());
        blackWhiteTradeData.setRemark(this.getRemark());
        blackWhiteTradeData.setRsrvDate1(this.getRsrvDate1());
        blackWhiteTradeData.setRsrvDate2(this.getRsrvDate2());
        blackWhiteTradeData.setRsrvDate3(this.getRsrvDate3());
        blackWhiteTradeData.setRsrvNum1(this.getRsrvNum1());
        blackWhiteTradeData.setRsrvNum2(this.getRsrvNum2());
        blackWhiteTradeData.setRsrvNum3(this.getRsrvNum3());
        blackWhiteTradeData.setRsrvNum4(this.getRsrvNum4());
        blackWhiteTradeData.setRsrvNum5(this.getRsrvNum5());
        blackWhiteTradeData.setRsrvStr1(this.getRsrvStr1());
        blackWhiteTradeData.setRsrvStr2(this.getRsrvStr2());
        blackWhiteTradeData.setRsrvStr3(this.getRsrvStr3());
        blackWhiteTradeData.setRsrvStr4(this.getRsrvStr4());
        blackWhiteTradeData.setRsrvStr5(this.getRsrvStr5());
        blackWhiteTradeData.setRsrvTag1(this.getRsrvTag1());
        blackWhiteTradeData.setRsrvTag2(this.getRsrvTag2());
        blackWhiteTradeData.setRsrvTag3(this.getRsrvTag3());
        blackWhiteTradeData.setSerialNumber(this.getSerialNumber());
        blackWhiteTradeData.setServiceId(this.getServiceId());
        blackWhiteTradeData.setServCode(this.getServCode());
        blackWhiteTradeData.setStartDate(this.getStartDate());
        blackWhiteTradeData.setUserId(this.getUserId());
        blackWhiteTradeData.setUserTypeCode(this.getUserTypeCode());
        blackWhiteTradeData.setInstId(this.getInstId()); 
        blackWhiteTradeData.setIsNeedPf(this.getisNeedPF()); 

        return blackWhiteTradeData;
    }

    public String getBillingType()
    {
        return billingType;
    }

    public String getisNeedPF()
    {
        return isNeedPf;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }
    
    public String getBizCode()
    {
        return bizCode;
    }

    public String getBizDesc()
    {
        return bizDesc;
    }

    public String getBizInCode()
    {
        return bizInCode;
    }

    public String getBizInCodeA()
    {
        return bizInCodeA;
    }

    public String getBizName()
    {
        return bizName;
    }

    public String getBookDate()
    {
        return bookDate;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getEcSerialNumber()
    {
        return ecSerialNumber;
    }

    public String getEcUserId()
    {
        return ecUserId;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getExpectTime()
    {
        return expectTime;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOperState()
    {
        return operState;
    }

    public String getOprEffTime()
    {
        return oprEffTime;
    }

    public String getOprSeqId()
    {
        return oprSeqId;
    }

    public String getPlatSyncState()
    {
        return platSyncState;
    }

    public String getPrice()
    {
        return price;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getServCode()
    {
        return servCode;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_BLACKWHITE";
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public void setBillingType(String billingType)
    {
        this.billingType = billingType;
    }

    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }

    public void setBizDesc(String bizDesc)
    {
        this.bizDesc = bizDesc;
    }

    public void setBizInCode(String bizInCode)
    {
        this.bizInCode = bizInCode;
    }

    public void setBizInCodeA(String bizInCodeA)
    {
        this.bizInCodeA = bizInCodeA;
    }

    public void setBizName(String bizName)
    {
        this.bizName = bizName;
    }

    public void setBookDate(String bookDate)
    {
        this.bookDate = bookDate;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setEcSerialNumber(String ecSerialNumber)
    {
        this.ecSerialNumber = ecSerialNumber;
    }

    public void setEcUserId(String ecUserId)
    {
        this.ecUserId = ecUserId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setExpectTime(String expectTime)
    {
        this.expectTime = expectTime;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOperState(String operState)
    {
        this.operState = operState;
    }

    public void setOprEffTime(String oprEffTime)
    {
        this.oprEffTime = oprEffTime;
    }

    public void setOprSeqId(String oprSeqId)
    {
        this.oprSeqId = oprSeqId;
    }

    public void setPlatSyncState(String platSyncState)
    {
        this.platSyncState = platSyncState;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setServCode(String servCode)
    {
        this.servCode = servCode;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("BILLING_TYPE", this.billingType);
        data.put("BIZ_CODE", this.bizCode);
        data.put("BIZ_DESC", this.bizDesc);
        data.put("BIZ_IN_CODE", this.bizInCode);
        data.put("BIZ_IN_CODE_A", this.bizInCodeA);
        data.put("BIZ_NAME", this.bizName);
        data.put("BOOK_DATE", this.bookDate);
        data.put("CONTRACT_ID", this.contractId);
        data.put("EC_SERIAL_NUMBER", this.ecSerialNumber);
        data.put("EC_USER_ID", this.ecUserId);
        data.put("END_DATE", this.endDate);
        data.put("EXPECT_TIME", this.expectTime);
        data.put("GROUP_ID", this.groupId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("OPER_STATE", this.operState);
        data.put("OPR_EFF_TIME", this.oprEffTime);
        data.put("OPR_SEQ_ID", this.oprSeqId);
        data.put("PLAT_SYNC_STATE", this.platSyncState);
        data.put("PRICE", this.price);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("SERVICE_ID", this.serviceId);
        data.put("SERV_CODE", this.servCode);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("USER_TYPE_CODE", this.userTypeCode);
        data.put("INST_ID", this.instId);
        data.put("IS_NEED_PF", this.isNeedPf);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
