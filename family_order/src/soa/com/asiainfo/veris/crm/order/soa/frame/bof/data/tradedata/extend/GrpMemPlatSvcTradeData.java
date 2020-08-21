
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class GrpMemPlatSvcTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String serialNumber;

    private String ecUserId;

    private String ecSerialNumber;

    private String ServiceId;

    private String ServCode;

    private String bizCode;

    private String bizName;

    private String startDate;

    private String endDate;

    private String modifyTag;

    private String updateTime;

    private String updateStaffId;

    private String updateDepartId;

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

    public GrpMemPlatSvcTradeData()
    {

    }

    public GrpMemPlatSvcTradeData(IData data)
    {
        this.setInstId(data.getString("INST_ID"));
        this.setUserId(data.getString("USER_ID"));
        this.setSerialNumber(data.getString("SERIAL_NUMBER"));
        this.setEcUserId(data.getString("EC_USER_ID"));
        this.setEcSerialNumber(data.getString("EC_SERIAL_NUMBER"));
        this.setServiceId(data.getString("SERVICE_ID"));
        this.setServCode(data.getString("SERV_CODE"));
        this.setBizCode(data.getString("BIZ_CODE"));
        this.setBizName(data.getString("BIZ_NAME"));
        this.setStartDate(data.getString("START_DATE"));
        this.setEndDate(data.getString("END_DATE"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setUpdateTime(data.getString("UPDATE_TIME"));
        this.setUpdateStaffId(data.getString("UPDATE_STAFF_ID"));
        this.setUpdateDepartId(data.getString("UPDATE_DEPART_ID"));
        this.setRemark(data.getString("REMARK"));
        this.setRsrvNum1(data.getString("RSRV_NUM1"));
        this.setRsrvNum2(data.getString("RSRV_NUM2"));
        this.setRsrvNum3(data.getString("RSRV_NUM3"));
        this.setRsrvNum4(data.getString("RSRV_NUM4"));
        this.setRsrvNum5(data.getString("RSRV_NUM5"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvDate1(data.getString("RSRV_DATE1"));
        this.setRsrvDate2(data.getString("RSRV_DATE2"));
        this.setRsrvDate3(data.getString("RSRV_DATE3"));
        this.setRsrvTag1(data.getString("RSRV_TAG1"));
        this.setRsrvTag2(data.getString("RSRV_TAG2"));
        this.setRsrvTag3(data.getString("RSRV_TAG3"));
    }

    public GrpMemPlatSvcTradeData clone()
    {
        GrpMemPlatSvcTradeData grpMemPlatSvcTradeData = new GrpMemPlatSvcTradeData();
        grpMemPlatSvcTradeData.setInstId(this.getInstId());
        grpMemPlatSvcTradeData.setUserId(this.getUserId());
        grpMemPlatSvcTradeData.setSerialNumber(this.getSerialNumber());
        grpMemPlatSvcTradeData.setEcUserId(this.getEcUserId());
        grpMemPlatSvcTradeData.setEcSerialNumber(this.getEcSerialNumber());
        grpMemPlatSvcTradeData.setServiceId(this.getServiceId());
        grpMemPlatSvcTradeData.setServCode(this.getServCode());
        grpMemPlatSvcTradeData.setBizCode(this.getBizCode());
        grpMemPlatSvcTradeData.setBizName(this.getBizName());
        grpMemPlatSvcTradeData.setStartDate(this.getStartDate());
        grpMemPlatSvcTradeData.setEndDate(this.getEndDate());
        grpMemPlatSvcTradeData.setModifyTag(this.getModifyTag());
        grpMemPlatSvcTradeData.setUpdateTime(this.getUpdateTime());
        grpMemPlatSvcTradeData.setUpdateStaffId(this.getUpdateStaffId());
        grpMemPlatSvcTradeData.setUpdateDepartId(this.getUpdateDepartId());
        grpMemPlatSvcTradeData.setRemark(this.getRemark());
        grpMemPlatSvcTradeData.setRsrvNum1(this.getRsrvNum1());
        grpMemPlatSvcTradeData.setRsrvNum2(this.getRsrvNum2());
        grpMemPlatSvcTradeData.setRsrvNum3(this.getRsrvNum3());
        grpMemPlatSvcTradeData.setRsrvNum4(this.getRsrvNum4());
        grpMemPlatSvcTradeData.setRsrvNum5(this.getRsrvNum5());
        grpMemPlatSvcTradeData.setRsrvStr1(this.getRsrvStr1());
        grpMemPlatSvcTradeData.setRsrvStr2(this.getRsrvStr2());
        grpMemPlatSvcTradeData.setRsrvStr3(this.getRsrvStr3());
        grpMemPlatSvcTradeData.setRsrvStr4(this.getRsrvStr4());
        grpMemPlatSvcTradeData.setRsrvStr5(this.getRsrvStr5());
        grpMemPlatSvcTradeData.setRsrvDate1(this.getRsrvDate1());
        grpMemPlatSvcTradeData.setRsrvDate2(this.getRsrvDate2());
        grpMemPlatSvcTradeData.setRsrvDate3(this.getRsrvDate3());
        grpMemPlatSvcTradeData.setRsrvTag1(this.getRsrvTag1());
        grpMemPlatSvcTradeData.setRsrvTag2(this.getRsrvTag2());
        grpMemPlatSvcTradeData.setRsrvTag3(this.getRsrvTag3());

        return grpMemPlatSvcTradeData;
    }

    public String getBizCode()
    {
        return bizCode;
    }

    public String getBizName()
    {
        return bizName;
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

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
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
        return ServCode;
    }

    public String getServiceId()
    {
        return ServiceId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_GRP_MEB_PLATSVC";
    }

    public String getUpdateDepartId()
    {
        return updateDepartId;
    }

    public String getUpdateStaffId()
    {
        return updateStaffId;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }

    public void setBizName(String bizName)
    {
        this.bizName = bizName;
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

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
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

    public void setServCode(String ServCode)
    {
        this.ServCode = ServCode;
    }

    public void setServiceId(String ServiceId)
    {
        this.ServiceId = ServiceId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUpdateDepartId(String updateDepartId)
    {
        this.updateDepartId = updateDepartId;
    }

    public void setUpdateStaffId(String updateStaffId)
    {
        this.updateStaffId = updateStaffId;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("EC_USER_ID", this.ecUserId);
        data.put("EC_SERIAL_NUMBER", this.ecSerialNumber);
        data.put("SERVICE_ID", this.ServiceId);
        data.put("SERV_CODE", this.ServCode);
        data.put("BIZ_CODE", this.bizCode);
        data.put("BIZ_NAME", this.bizName);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("UPDATE_TIME", this.updateTime);
        data.put("UPDATE_STAFF_ID", this.updateStaffId);
        data.put("UPDATE_DEPART_ID", this.updateDepartId);
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
