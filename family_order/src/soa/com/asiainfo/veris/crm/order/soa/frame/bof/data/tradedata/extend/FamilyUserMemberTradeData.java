
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @Description 家庭成员关系台账类
 * @Auther: zhenggang
 * @Date: 2020/7/15 11:40
 * @version: V1.0
 */
public class FamilyUserMemberTradeData extends BaseTradeData
{
    private String familyUserId;

    private String familySerialNum;

    private String mainSerialNum;

    private String memberUserId;

    private String memberSerialNum;

    private String memberRoleCode;

    private String memberRoleType;

    private String instId;

    private String modifyTag;

    private String startDate;

    private String endDate;

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

    public FamilyUserMemberTradeData()
    {

    }

    public FamilyUserMemberTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.familyUserId = data.getString("FAMILY_USER_ID");
        this.mainSerialNum = data.getString("MAIN_SERIAL_NUM");
        this.memberUserId = data.getString("MEMBER_USER_ID");
        this.familySerialNum = data.getString("FAMILY_SERIAL_NUM");
        this.memberSerialNum = data.getString("MEMBER_SERIAL_NUM");
        this.memberRoleCode = data.getString("MEMBER_ROLE_CODE");
        this.memberRoleType = data.getString("MEMBER_ROLE_TYPE");
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

    }

    public FamilyUserMemberTradeData clone()
    {
        FamilyUserMemberTradeData TradeData = new FamilyUserMemberTradeData();
        TradeData.setInstId(this.getInstId());
        TradeData.setModifyTag(this.getModifyTag());
        TradeData.setFamilySerialNum(this.getFamilySerialNum());
        TradeData.setMainSerialNum(this.getMainSerialNum());
        TradeData.setMemberSerialNum(this.getMemberSerialNum());
        TradeData.setMemberRoleCode(this.getMemberRoleCode());
        TradeData.setMemberRoleType(this.getMemberRoleType());
        TradeData.setFamilyUserId(this.getFamilyUserId());
        TradeData.setMemberUserId(this.getMemberUserId());
        TradeData.setUpdateTime(this.getUpdateTime());
        TradeData.setUpdateStaffId(this.getUpdateStaffId());
        TradeData.setUpdateDepartId(this.getUpdateDepartId());
        TradeData.setStartDate(this.getStartDate());
        TradeData.setEndDate(this.getEndDate());
        TradeData.setRemark(this.getRemark());
        TradeData.setRsrvDate1(this.getRsrvDate1());
        TradeData.setRsrvDate2(this.getRsrvDate2());
        TradeData.setRsrvDate3(this.getRsrvDate3());
        TradeData.setRsrvNum1(this.getRsrvNum1());
        TradeData.setRsrvNum2(this.getRsrvNum2());
        TradeData.setRsrvNum3(this.getRsrvNum3());
        TradeData.setRsrvNum4(this.getRsrvNum4());
        TradeData.setRsrvNum5(this.getRsrvNum5());
        TradeData.setRsrvStr1(this.getRsrvStr1());
        TradeData.setRsrvStr2(this.getRsrvStr2());
        TradeData.setRsrvStr3(this.getRsrvStr3());
        TradeData.setRsrvStr4(this.getRsrvStr4());
        TradeData.setRsrvStr5(this.getRsrvStr5());
        TradeData.setRsrvTag1(this.getRsrvTag1());
        TradeData.setRsrvTag2(this.getRsrvTag2());
        TradeData.setRsrvTag3(this.getRsrvTag3());

        return TradeData;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("MEMBER_SERIAL_NUM", this.memberSerialNum);
        data.put("FAMILY_SERIAL_NUM", this.familySerialNum);
        data.put("MAIN_SERIAL_NUM", this.mainSerialNum);
        data.put("FAMILY_USER_ID", this.familyUserId);
        data.put("MEMBER_USER_ID", this.memberUserId);
        data.put("MEMBER_ROLE_CODE", this.memberRoleCode);
        data.put("MEMBER_ROLE_TYPE", this.memberRoleType);
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
        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
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

    public String getFamilySerialNum()
    {
        return familySerialNum;
    }

    public void setFamilySerialNum(String familySerialNum)
    {
        this.familySerialNum = familySerialNum;
    }

    public String getMemberSerialNum()
    {
        return memberSerialNum;
    }

    public void setMemberSerialNum(String memberSerialNum)
    {
        this.memberSerialNum = memberSerialNum;
    }

    public String getMemberRoleCode()
    {
        return memberRoleCode;
    }

    public void setMemberRoleCode(String memberRoleCode)
    {
        this.memberRoleCode = memberRoleCode;
    }

    public String getMemberRoleType()
    {
        return memberRoleType;
    }

    public void setMemberRoleType(String memberRoleType)
    {
        this.memberRoleType = memberRoleType;
    }

    public void getMemberRoleType(String familyUserId)
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

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
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
        return "TF_B_TRADE_FAMILY_USER_MEMBER";
    }

    public String getMainSerialNum()
    {
        return mainSerialNum;
    }

    public void setMainSerialNum(String mainSerialNum)
    {
        this.mainSerialNum = mainSerialNum;
    }
}
