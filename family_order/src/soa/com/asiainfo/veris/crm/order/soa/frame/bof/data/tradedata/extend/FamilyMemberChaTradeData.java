
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @Description 家庭成员扩展属性类
 * @Auther: zhenggang
 * @Date: 2020/7/15 11:40
 * @version: V1.0
 */
public class FamilyMemberChaTradeData extends BaseTradeData
{
    private String chaType;

    private String modifyTag;

    private String familyUserId;

    private String memberUserId;

    private String instId;

    private String relInstId;

    private String chaCode;

    private String chaValue;

    private String startDate;

    private String endDate;

    private String updateTime;

    private String updateStaffId;

    private String updateDepartId;

    private String remark;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    public FamilyMemberChaTradeData()
    {

    }

    public FamilyMemberChaTradeData(IData data)
    {
        this.chaType = data.getString("CHA_TYPE");
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.familyUserId = data.getString("FAMILY_USER_ID");
        this.memberUserId = data.getString("MEMBER_USER_ID");
        this.relInstId = data.getString("REL_INST_ID");
        this.chaCode = data.getString("CHA_CODE");
        this.chaValue = data.getString("CHA_VALUE");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.updateTime = data.getString("UPDATE_TIME");
        this.updateStaffId = data.getString("UPDATE_STAFF_ID");
        this.updateDepartId = data.getString("UPDATE_DEPART_ID");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");

    }

    public FamilyMemberChaTradeData clone()
    {
        FamilyMemberChaTradeData charTradeData = new FamilyMemberChaTradeData();
        charTradeData.setInstId(this.getInstId());
        charTradeData.setModifyTag(this.getModifyTag());
        charTradeData.setRelInstId(this.getRelInstId());
        charTradeData.setChaType(this.getChaType());
        charTradeData.setChaCode(this.getChaCode());
        charTradeData.setChaValue(this.getChaValue());
        charTradeData.setFamilyUserId(this.getFamilyUserId());
        charTradeData.setMemberUserId(this.getMemberUserId());
        charTradeData.setUpdateTime(this.getUpdateTime());
        charTradeData.setUpdateStaffId(this.getUpdateStaffId());
        charTradeData.setUpdateDepartId(this.getUpdateDepartId());
        charTradeData.setStartDate(this.getStartDate());
        charTradeData.setEndDate(this.getEndDate());
        charTradeData.setRemark(this.getRemark());
        charTradeData.setRsrvDate1(this.getRsrvDate1());
        charTradeData.setRsrvDate2(this.getRsrvDate2());
        charTradeData.setRsrvDate3(this.getRsrvDate3());
        charTradeData.setRsrvNum1(this.getRsrvNum1());
        charTradeData.setRsrvNum2(this.getRsrvNum2());
        charTradeData.setRsrvNum3(this.getRsrvNum3());
        charTradeData.setRsrvStr1(this.getRsrvStr1());
        charTradeData.setRsrvStr2(this.getRsrvStr2());
        charTradeData.setRsrvStr3(this.getRsrvStr3());
        charTradeData.setRsrvTag1(this.getRsrvTag1());
        charTradeData.setRsrvTag2(this.getRsrvTag2());
        charTradeData.setRsrvTag3(this.getRsrvTag3());

        return charTradeData;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("CHA_TYPE", this.chaType);
        data.put("REL_INST_ID", this.relInstId);
        data.put("FAMILY_USER_ID", this.familyUserId);
        data.put("MEMBER_USER_ID", this.memberUserId);
        data.put("CHA_CODE", this.chaCode);
        data.put("CHA_VALUE", this.chaValue);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("UPDATE_TIME", this.updateTime);
        data.put("UPDATE_STAFF_ID", this.updateStaffId);
        data.put("UPDATE_DEPART_ID", this.updateDepartId);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }

    public String getChaType()
    {
        return chaType;
    }

    public void setChaType(String chaType)
    {
        this.chaType = chaType;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public String getFamilyUserId()
    {
        return familyUserId;
    }

    public void setFamilyUserId(String familyUserId)
    {
        this.familyUserId = familyUserId;
    }

    public String getMemberUserId()
    {
        return memberUserId;
    }

    public void setMemberUserId(String memberUserId)
    {
        this.memberUserId = memberUserId;
    }

    public String getInstId()
    {
        return instId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public String getRelInstId()
    {
        return relInstId;
    }

    public void setRelInstId(String relInstId)
    {
        this.relInstId = relInstId;
    }

    public String getChaCode()
    {
        return chaCode;
    }

    public void setChaCode(String chaCode)
    {
        this.chaCode = chaCode;
    }

    public String getChaValue()
    {
        return chaValue;
    }

    public void setChaValue(String chaValue)
    {
        this.chaValue = chaValue;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getUpdateStaffId()
    {
        return updateStaffId;
    }

    public void setUpdateStaffId(String updateStaffId)
    {
        this.updateStaffId = updateStaffId;
    }

    public String getUpdateDepartId()
    {
        return updateDepartId;
    }

    public void setUpdateDepartId(String updateDepartId)
    {
        this.updateDepartId = updateDepartId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_FAMILY_MEMBER_CHA";
    }
}
