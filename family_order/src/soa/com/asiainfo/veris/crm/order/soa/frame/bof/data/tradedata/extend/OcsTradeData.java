
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class OcsTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String userTypeCode;

    private String monitorType;

    private String serialNumber;

    private String status;

    private String bizType;

    private String monitorFlag;

    private String monitorRuleCode;

    private String startDate;

    private String endDate;

    private String modifyTag;

    private String remark;

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

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String eparchyCode;

    public OcsTradeData()
    {
    }

    public OcsTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.userId = data.getString("USER_ID");
        this.userTypeCode = data.getString("USER_TYPE_CODE");
        this.monitorType = data.getString("MONITOR_TYPE");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.status = data.getString("STATUS");
        this.bizType = data.getString("BIZ_TYPE");
        this.monitorFlag = data.getString("MONITOR_FLAG");
        this.monitorRuleCode = data.getString("MONITOR_RULE_CODE");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.remark = data.getString("REMARK");
        this.eparchyCode = data.getString("EPARCHY_CODE");
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
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
    }

    @Override
    public OcsTradeData clone()
    {
        OcsTradeData ocsTradeData = new OcsTradeData();
        ocsTradeData.setInstId(this.getInstId());
        ocsTradeData.setUserId(this.getUserId());
        ocsTradeData.setUserTypeCode(this.getUserTypeCode());
        ocsTradeData.setMonitorType(this.getMonitorType());
        ocsTradeData.setSerialNumber(this.getSerialNumber());
        ocsTradeData.setStatus(this.getStatus());
        ocsTradeData.setBizType(this.getBizType());
        ocsTradeData.setMonitorFlag(this.getMonitorFlag());
        ocsTradeData.setMonitorRuleCode(this.getMonitorRuleCode());
        ocsTradeData.setStartDate(this.getStartDate());
        ocsTradeData.setEndDate(this.getEndDate());
        ocsTradeData.setModifyTag(this.getModifyTag());
        ocsTradeData.setRemark(this.getRemark());
        ocsTradeData.setRsrvNum1(this.getRsrvNum1());
        ocsTradeData.setRsrvNum2(this.getRsrvNum2());
        ocsTradeData.setRsrvNum3(this.getRsrvNum3());
        ocsTradeData.setRsrvNum4(this.getRsrvNum4());
        ocsTradeData.setRsrvNum5(this.getRsrvNum5());
        ocsTradeData.setRsrvStr1(this.getRsrvStr1());
        ocsTradeData.setRsrvStr2(this.getRsrvStr2());
        ocsTradeData.setRsrvStr3(this.getRsrvStr3());
        ocsTradeData.setRsrvStr4(this.getRsrvStr4());
        ocsTradeData.setRsrvStr5(this.getRsrvStr5());
        ocsTradeData.setRsrvDate1(this.getRsrvDate1());
        ocsTradeData.setRsrvDate2(this.getRsrvDate2());
        ocsTradeData.setRsrvDate3(this.getRsrvDate3());
        ocsTradeData.setRsrvTag1(this.getRsrvTag1());
        ocsTradeData.setRsrvTag2(this.getRsrvTag2());
        ocsTradeData.setRsrvTag3(this.getRsrvTag3());
        ocsTradeData.setEparchyCode(this.getEparchyCode());

        return ocsTradeData;
    }

    public String getBizType()
    {
        return this.bizType;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getMonitorFlag()
    {
        return this.monitorFlag;
    }

    public String getMonitorRuleCode()
    {
        return this.monitorRuleCode;
    }

    public String getMonitorType()
    {
        return this.monitorType;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRsrvDate1()
    {
        return this.rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return this.rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return this.rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return this.rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return this.rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return this.rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return this.rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return this.rsrvNum5;
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

    public String getRsrvTag1()
    {
        return this.rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return this.rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return this.rsrvTag3;
    }

    public String getSerialNumber()
    {
        return this.serialNumber;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getStatus()
    {
        return this.status;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_OCS";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public String getUserTypeCode()
    {
        return this.userTypeCode;
    }

    public void setBizType(String bizType)
    {
        this.bizType = bizType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setMonitorFlag(String monitorFlag)
    {
        this.monitorFlag = monitorFlag;
    }

    public void setMonitorRuleCode(String monitorRuleCode)
    {
        this.monitorRuleCode = monitorRuleCode;
    }

    public void setMonitorType(String monitorType)
    {
        this.monitorType = monitorType;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();
        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("USER_TYPE_CODE", this.userTypeCode);
        data.put("MONITOR_TYPE", this.monitorType);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("STATUS", this.status);
        data.put("BIZ_TYPE", this.bizType);
        data.put("MONITOR_FLAG", this.monitorFlag);
        data.put("MONITOR_RULE_CODE", this.monitorRuleCode);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("REMARK", this.remark);
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
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("EPARCHY_CODE", this.eparchyCode);

        return data;
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
