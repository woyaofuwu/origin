
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class CustFamilyMebTradeData extends BaseTradeData
{

    private String homeCustId;

    private String homeId;

    private String memberCustId;

    private String netTypeCode;

    private String serialNumber;

    private String custName;

    private String nickName;

    private String brandCode;

    private String productId;

    private String memberBelong;

    private String memberRole;

    private String memberKind;

    private String scoreValue;

    private String shortCode;

    private String memberInterestCode;

    private String joinDate;

    private String joinStaffId;

    private String joinDepartId;

    private String removeTag;

    private String removeDate;

    private String removeStaffId;

    private String removeDepartId;

    private String removeReason;

    private String modifyTag;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvStr10;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String instId;

    public CustFamilyMebTradeData()
    {

    }

    public CustFamilyMebTradeData(IData data)
    {
        this.homeCustId = data.getString("HOME_CUST_ID");
        this.homeId = data.getString("HOME_ID");
        this.memberCustId = data.getString("MEMBER_CUST_ID");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.custName = data.getString("CUST_NAME");
        this.nickName = data.getString("NICK_NAME");
        this.brandCode = data.getString("BRAND_CODE");
        this.productId = data.getString("PRODUCT_ID");
        this.memberBelong = data.getString("MEMBER_BELONG");
        this.memberRole = data.getString("MEMBER_ROLE");
        this.memberKind = data.getString("MEMBER_KIND");
        this.scoreValue = data.getString("SCORE_VALUE");
        this.shortCode = data.getString("SHORT_CODE");
        this.memberInterestCode = data.getString("MEMBER_INTEREST_CODE");
        this.joinDate = data.getString("JOIN_DATE");
        this.joinStaffId = data.getString("JOIN_STAFF_ID");
        this.joinDepartId = data.getString("JOIN_DEPART_ID");
        this.removeTag = data.getString("REMOVE_TAG");
        this.removeDate = data.getString("REMOVE_DATE");
        this.removeStaffId = data.getString("REMOVE_STAFF_ID");
        this.removeDepartId = data.getString("REMOVE_DEPART_ID");
        this.removeReason = data.getString("REMOVE_REASON");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.instId = data.getString("INST_ID");
    }

    public CustFamilyMebTradeData clone()
    {
        CustFamilyMebTradeData custFamilyMebTradeData = new CustFamilyMebTradeData();
        custFamilyMebTradeData.setHomeCustId(this.getHomeCustId());
        custFamilyMebTradeData.setHomeId(this.getHomeId());
        custFamilyMebTradeData.setMemberCustId(this.getMemberCustId());
        custFamilyMebTradeData.setNetTypeCode(this.getNetTypeCode());
        custFamilyMebTradeData.setSerialNumber(this.getSerialNumber());
        custFamilyMebTradeData.setCustName(this.getCustName());
        custFamilyMebTradeData.setNickName(this.getNickName());
        custFamilyMebTradeData.setBrandCode(this.getBrandCode());
        custFamilyMebTradeData.setProductId(this.getProductId());
        custFamilyMebTradeData.setMemberBelong(this.getMemberBelong());
        custFamilyMebTradeData.setMemberRole(this.getMemberRole());
        custFamilyMebTradeData.setMemberKind(this.getMemberKind());
        custFamilyMebTradeData.setScoreValue(this.getScoreValue());
        custFamilyMebTradeData.setShortCode(this.getShortCode());
        custFamilyMebTradeData.setMemberInterestCode(this.getMemberInterestCode());
        custFamilyMebTradeData.setJoinDate(this.getJoinDate());
        custFamilyMebTradeData.setJoinStaffId(this.getJoinStaffId());
        custFamilyMebTradeData.setJoinDepartId(this.getJoinDepartId());
        custFamilyMebTradeData.setRemoveTag(this.getRemoveTag());
        custFamilyMebTradeData.setRemoveDate(this.getRemoveDate());
        custFamilyMebTradeData.setRemoveStaffId(this.getRemoveStaffId());
        custFamilyMebTradeData.setRemoveDepartId(this.getRemoveDepartId());
        custFamilyMebTradeData.setRemoveReason(this.getRemoveReason());
        custFamilyMebTradeData.setModifyTag(this.getModifyTag());
        custFamilyMebTradeData.setRsrvTag2(this.getRsrvTag2());
        custFamilyMebTradeData.setRsrvTag3(this.getRsrvTag3());
        custFamilyMebTradeData.setRsrvNum1(this.getRsrvNum1());
        custFamilyMebTradeData.setRsrvNum2(this.getRsrvNum2());
        custFamilyMebTradeData.setRsrvNum3(this.getRsrvNum3());
        custFamilyMebTradeData.setRsrvStr1(this.getRsrvStr1());
        custFamilyMebTradeData.setRsrvStr2(this.getRsrvStr2());
        custFamilyMebTradeData.setRsrvStr3(this.getRsrvStr3());
        custFamilyMebTradeData.setRsrvStr4(this.getRsrvStr4());
        custFamilyMebTradeData.setRsrvStr5(this.getRsrvStr5());
        custFamilyMebTradeData.setRsrvStr6(this.getRsrvStr6());
        custFamilyMebTradeData.setRsrvStr7(this.getRsrvStr7());
        custFamilyMebTradeData.setRsrvStr8(this.getRsrvStr8());
        custFamilyMebTradeData.setRsrvStr9(this.getRsrvStr9());
        custFamilyMebTradeData.setRsrvStr10(this.getRsrvStr10());
        custFamilyMebTradeData.setRsrvDate1(this.getRsrvDate1());
        custFamilyMebTradeData.setRsrvDate2(this.getRsrvDate2());
        custFamilyMebTradeData.setRsrvDate3(this.getRsrvDate3());
        custFamilyMebTradeData.setRsrvTag1(this.getRsrvTag1());
        custFamilyMebTradeData.setInstId(this.getInstId());

        return custFamilyMebTradeData;
    }

    public String getBrandCode()
    {
        return this.brandCode;
    }

    public String getCustName()
    {
        return this.custName;
    }

    public String getHomeCustId()
    {
        return this.homeCustId;
    }

    public String getHomeId()
    {
        return this.homeId;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getJoinDate()
    {
        return this.joinDate;
    }

    public String getJoinDepartId()
    {
        return this.joinDepartId;
    }

    public String getJoinStaffId()
    {
        return this.joinStaffId;
    }

    public String getMemberBelong()
    {
        return this.memberBelong;
    }

    public String getMemberCustId()
    {
        return this.memberCustId;
    }

    public String getMemberInterestCode()
    {
        return this.memberInterestCode;
    }

    public String getMemberKind()
    {
        return this.memberKind;
    }

    public String getMemberRole()
    {
        return this.memberRole;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getNetTypeCode()
    {
        return this.netTypeCode;
    }

    public String getNickName()
    {
        return this.nickName;
    }

    public String getProductId()
    {
        return this.productId;
    }

    public String getRemoveDate()
    {
        return this.removeDate;
    }

    public String getRemoveDepartId()
    {
        return this.removeDepartId;
    }

    public String getRemoveReason()
    {
        return this.removeReason;
    }

    public String getRemoveStaffId()
    {
        return this.removeStaffId;
    }

    public String getRemoveTag()
    {
        return this.removeTag;
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

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return this.rsrvStr10;
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

    public String getRsrvStr6()
    {
        return this.rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return this.rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return this.rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return this.rsrvStr9;
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

    public String getScoreValue()
    {
        return this.scoreValue;
    }

    public String getSerialNumber()
    {
        return this.serialNumber;
    }

    public String getShortCode()
    {
        return this.shortCode;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_CUST_FAMILYMEB";
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setHomeCustId(String homeCustId)
    {
        this.homeCustId = homeCustId;
    }

    public void setHomeId(String homeId)
    {
        this.homeId = homeId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setJoinDate(String joinDate)
    {
        this.joinDate = joinDate;
    }

    public void setJoinDepartId(String joinDepartId)
    {
        this.joinDepartId = joinDepartId;
    }

    public void setJoinStaffId(String joinStaffId)
    {
        this.joinStaffId = joinStaffId;
    }

    public void setMemberBelong(String memberBelong)
    {
        this.memberBelong = memberBelong;
    }

    public void setMemberCustId(String memberCustId)
    {
        this.memberCustId = memberCustId;
    }

    public void setMemberInterestCode(String memberInterestCode)
    {
        this.memberInterestCode = memberInterestCode;
    }

    public void setMemberKind(String memberKind)
    {
        this.memberKind = memberKind;
    }

    public void setMemberRole(String memberRole)
    {
        this.memberRole = memberRole;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
    }

    public void setRemoveDepartId(String removeDepartId)
    {
        this.removeDepartId = removeDepartId;
    }

    public void setRemoveReason(String removeReason)
    {
        this.removeReason = removeReason;
    }

    public void setRemoveStaffId(String removeStaffId)
    {
        this.removeStaffId = removeStaffId;
    }

    public void setRemoveTag(String removeTag)
    {
        this.removeTag = removeTag;
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

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
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

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
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

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("HOME_CUST_ID", this.homeCustId);
        data.put("HOME_ID", this.homeId);
        data.put("MEMBER_CUST_ID", this.memberCustId);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("CUST_NAME", this.custName);
        data.put("NICK_NAME", this.nickName);
        data.put("BRAND_CODE", this.brandCode);
        data.put("PRODUCT_ID", this.productId);
        data.put("MEMBER_BELONG", this.memberBelong);
        data.put("MEMBER_ROLE", this.memberRole);
        data.put("MEMBER_KIND", this.memberKind);
        data.put("SCORE_VALUE", this.scoreValue);
        data.put("SHORT_CODE", this.shortCode);
        data.put("MEMBER_INTEREST_CODE", this.memberInterestCode);
        data.put("JOIN_DATE", this.joinDate);
        data.put("JOIN_STAFF_ID", this.joinStaffId);
        data.put("JOIN_DEPART_ID", this.joinDepartId);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("REMOVE_DATE", this.removeDate);
        data.put("REMOVE_STAFF_ID", this.removeStaffId);
        data.put("REMOVE_DEPART_ID", this.removeDepartId);
        data.put("REMOVE_REASON", this.removeReason);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("INST_ID", this.instId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
