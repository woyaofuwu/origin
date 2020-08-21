
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class CustFamilyTradeData extends BaseTradeData
{
    private String childAge;

    private String cityCode;

    private String custName;

    private String email;

    private String eparchyCode;

    private String faxNbr;

    private String hasCar;

    private String hasChild;

    private String hasFetion;

    private String headCustId;

    private String headPsptId;

    private String headTypeCode;

    private String homeAddress;

    private String homeCustId;

    private String homeCustScore;

    private String homeId;

    private String homeName;

    private String homePhone;

    private String homePostCode;

    private String homeRegion;

    private String homeState;

    private String interestCode;

    private String inDate;

    private String inDepartId;

    private String inStaffId;

    private String memberNum;

    private String modifyTag;

    private String msn;

    private String phone;

    private String qq;

    private String removeDate;

    private String removeDepartId;

    private String removeReason;

    private String removeStaffId;

    private String removeTag;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String serialNumber;

    private String unifyPayCode;

    private String workAddress;

    private String workName;

    public CustFamilyTradeData()
    {

    }

    public CustFamilyTradeData(IData data)
    {
        this.childAge = data.getString("CHILD_AGE");
        this.cityCode = data.getString("CITY_CODE");
        this.custName = data.getString("CUST_NAME");
        this.email = data.getString("EMAIL");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.faxNbr = data.getString("FAX_NBR");
        this.hasCar = data.getString("HAS_CAR");
        this.hasChild = data.getString("HAS_CHILD");
        this.hasFetion = data.getString("HAS_FETION");
        this.headCustId = data.getString("HEAD_CUST_ID");
        this.headPsptId = data.getString("HEAD_PSPT_ID");
        this.headTypeCode = data.getString("HEAD_TYPE_CODE");
        this.homeAddress = data.getString("HOME_ADDRESS");
        this.homeCustId = data.getString("HOME_CUST_ID");
        this.homeCustScore = data.getString("HOME_CUST_SCORE");
        this.homeId = data.getString("HOME_ID");
        this.homeName = data.getString("HOME_NAME");
        this.homePhone = data.getString("HOME_PHONE");
        this.homePostCode = data.getString("HOME_POST_CODE");
        this.homeRegion = data.getString("HOME_REGION");
        this.homeState = data.getString("HOME_STATE");
        this.interestCode = data.getString("INTEREST_CODE");
        this.inDate = data.getString("IN_DATE");
        this.inDepartId = data.getString("IN_DEPART_ID");
        this.inStaffId = data.getString("IN_STAFF_ID");
        this.memberNum = data.getString("MEMBER_NUM");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.msn = data.getString("MSN");
        this.phone = data.getString("PHONE");
        this.qq = data.getString("QQ");
        this.removeDate = data.getString("REMOVE_DATE");
        this.removeDepartId = data.getString("REMOVE_DEPART_ID");
        this.removeReason = data.getString("REMOVE_REASON");
        this.removeStaffId = data.getString("REMOVE_STAFF_ID");
        this.removeTag = data.getString("REMOVE_TAG");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.unifyPayCode = data.getString("UNIFY_PAY_CODE");
        this.workAddress = data.getString("WORK_ADDRESS");
        this.workName = data.getString("WORK_NAME");
    }

    public CustFamilyTradeData clone()
    {
        CustFamilyTradeData custFamilyTradeData = new CustFamilyTradeData();
        custFamilyTradeData.setChildAge(this.getChildAge());
        custFamilyTradeData.setCityCode(this.getCityCode());
        custFamilyTradeData.setCustName(this.getCustName());
        custFamilyTradeData.setEmail(this.getEmail());
        custFamilyTradeData.setEparchyCode(this.getEparchyCode());
        custFamilyTradeData.setFaxNbr(this.getFaxNbr());
        custFamilyTradeData.setHasCar(this.getHasCar());
        custFamilyTradeData.setHasChild(this.getHasChild());
        custFamilyTradeData.setHasFetion(this.getHasFetion());
        custFamilyTradeData.setHeadCustId(this.getHeadCustId());
        custFamilyTradeData.setHeadPsptId(this.getHeadPsptId());
        custFamilyTradeData.setHeadTypeCode(this.getHeadTypeCode());
        custFamilyTradeData.setHomeAddress(this.getHomeAddress());
        custFamilyTradeData.setHomeCustId(this.getHomeCustId());
        custFamilyTradeData.setHomeCustScore(this.getHomeCustScore());
        custFamilyTradeData.setHomeId(this.getHomeId());
        custFamilyTradeData.setHomeName(this.getHomeName());
        custFamilyTradeData.setHomePhone(this.getHomePhone());
        custFamilyTradeData.setHomePostCode(this.getHomePostCode());
        custFamilyTradeData.setHomeRegion(this.getHomeRegion());
        custFamilyTradeData.setHomeState(this.getHomeState());
        custFamilyTradeData.setInterestCode(this.getInterestCode());
        custFamilyTradeData.setInDate(this.getInDate());
        custFamilyTradeData.setInDepartId(this.getInDepartId());
        custFamilyTradeData.setInStaffId(this.getInStaffId());
        custFamilyTradeData.setMemberNum(this.getMemberNum());
        custFamilyTradeData.setModifyTag(this.getModifyTag());
        custFamilyTradeData.setMsn(this.getMsn());
        custFamilyTradeData.setPhone(this.getPhone());
        custFamilyTradeData.setQq(this.getQq());
        custFamilyTradeData.setRemoveDate(this.getRemoveDate());
        custFamilyTradeData.setRemoveDepartId(this.getRemoveDepartId());
        custFamilyTradeData.setRemoveReason(this.getRemoveReason());
        custFamilyTradeData.setRemoveStaffId(this.getRemoveStaffId());
        custFamilyTradeData.setRemoveTag(this.getRemoveTag());
        custFamilyTradeData.setRsrvDate1(this.getRsrvDate1());
        custFamilyTradeData.setRsrvDate2(this.getRsrvDate2());
        custFamilyTradeData.setRsrvDate3(this.getRsrvDate3());
        custFamilyTradeData.setRsrvNum1(this.getRsrvNum1());
        custFamilyTradeData.setRsrvNum2(this.getRsrvNum2());
        custFamilyTradeData.setRsrvNum3(this.getRsrvNum3());
        custFamilyTradeData.setRsrvStr1(this.getRsrvStr1());
        custFamilyTradeData.setRsrvStr10(this.getRsrvStr10());
        custFamilyTradeData.setRsrvStr2(this.getRsrvStr2());
        custFamilyTradeData.setRsrvStr3(this.getRsrvStr3());
        custFamilyTradeData.setRsrvStr4(this.getRsrvStr4());
        custFamilyTradeData.setRsrvStr5(this.getRsrvStr5());
        custFamilyTradeData.setRsrvStr6(this.getRsrvStr6());
        custFamilyTradeData.setRsrvStr7(this.getRsrvStr7());
        custFamilyTradeData.setRsrvStr8(this.getRsrvStr8());
        custFamilyTradeData.setRsrvStr9(this.getRsrvStr9());
        custFamilyTradeData.setRsrvTag1(this.getRsrvTag1());
        custFamilyTradeData.setRsrvTag2(this.getRsrvTag2());
        custFamilyTradeData.setRsrvTag3(this.getRsrvTag3());
        custFamilyTradeData.setSerialNumber(this.getSerialNumber());
        custFamilyTradeData.setUnifyPayCode(this.getUnifyPayCode());
        custFamilyTradeData.setWorkAddress(this.getWorkAddress());
        custFamilyTradeData.setWorkName(this.getWorkName());
        return custFamilyTradeData;
    }

    public String getChildAge()
    {
        return childAge;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getFaxNbr()
    {
        return faxNbr;
    }

    public String getHasCar()
    {
        return hasCar;
    }

    public String getHasChild()
    {
        return hasChild;
    }

    public String getHasFetion()
    {
        return hasFetion;
    }

    public String getHeadCustId()
    {
        return headCustId;
    }

    public String getHeadPsptId()
    {
        return headPsptId;
    }

    public String getHeadTypeCode()
    {
        return headTypeCode;
    }

    public String getHomeAddress()
    {
        return homeAddress;
    }

    public String getHomeCustId()
    {
        return homeCustId;
    }

    public String getHomeCustScore()
    {
        return homeCustScore;
    }

    public String getHomeId()
    {
        return homeId;
    }

    public String getHomeName()
    {
        return homeName;
    }

    public String getHomePhone()
    {
        return homePhone;
    }

    public String getHomePostCode()
    {
        return homePostCode;
    }

    public String getHomeRegion()
    {
        return homeRegion;
    }

    public String getHomeState()
    {
        return homeState;
    }

    public String getInDate()
    {
        return inDate;
    }

    public String getInDepartId()
    {
        return inDepartId;
    }

    public String getInStaffId()
    {
        return inStaffId;
    }

    public String getInterestCode()
    {
        return interestCode;
    }

    public String getMemberNum()
    {
        return memberNum;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getMsn()
    {
        return msn;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getQq()
    {
        return qq;
    }

    public String getRemoveDate()
    {
        return removeDate;
    }

    public String getRemoveDepartId()
    {
        return removeDepartId;
    }

    public String getRemoveReason()
    {
        return removeReason;
    }

    public String getRemoveStaffId()
    {
        return removeStaffId;
    }

    public String getRemoveTag()
    {
        return removeTag;
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

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
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

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
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

    public String getTableName()
    {
        return "TF_B_TRADE_CUST_FAMILY";
    }

    public String getUnifyPayCode()
    {
        return unifyPayCode;
    }

    public String getWorkAddress()
    {
        return workAddress;
    }

    public String getWorkName()
    {
        return workName;
    }

    public void setChildAge(String childAge)
    {
        this.childAge = childAge;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setFaxNbr(String faxNbr)
    {
        this.faxNbr = faxNbr;
    }

    public void setHasCar(String hasCar)
    {
        this.hasCar = hasCar;
    }

    public void setHasChild(String hasChild)
    {
        this.hasChild = hasChild;
    }

    public void setHasFetion(String hasFetion)
    {
        this.hasFetion = hasFetion;
    }

    public void setHeadCustId(String headCustId)
    {
        this.headCustId = headCustId;
    }

    public void setHeadPsptId(String headPsptId)
    {
        this.headPsptId = headPsptId;
    }

    public void setHeadTypeCode(String headTypeCode)
    {
        this.headTypeCode = headTypeCode;
    }

    public void setHomeAddress(String homeAddress)
    {
        this.homeAddress = homeAddress;
    }

    public void setHomeCustId(String homeCustId)
    {
        this.homeCustId = homeCustId;
    }

    public void setHomeCustScore(String homeCustScore)
    {
        this.homeCustScore = homeCustScore;
    }

    public void setHomeId(String homeId)
    {
        this.homeId = homeId;
    }

    public void setHomeName(String homeName)
    {
        this.homeName = homeName;
    }

    public void setHomePhone(String homePhone)
    {
        this.homePhone = homePhone;
    }

    public void setHomePostCode(String homePostCode)
    {
        this.homePostCode = homePostCode;
    }

    public void setHomeRegion(String homeRegion)
    {
        this.homeRegion = homeRegion;
    }

    public void setHomeState(String homeState)
    {
        this.homeState = homeState;
    }

    public void setInDate(String inDate)
    {
        this.inDate = inDate;
    }

    public void setInDepartId(String inDepartId)
    {
        this.inDepartId = inDepartId;
    }

    public void setInStaffId(String inStaffId)
    {
        this.inStaffId = inStaffId;
    }

    public void setInterestCode(String interestCode)
    {
        this.interestCode = interestCode;
    }

    public void setMemberNum(String memberNum)
    {
        this.memberNum = memberNum;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setMsn(String msn)
    {
        this.msn = msn;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setQq(String qq)
    {
        this.qq = qq;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setUnifyPayCode(String unifyPayCode)
    {
        this.unifyPayCode = unifyPayCode;
    }

    public void setWorkAddress(String workAddress)
    {
        this.workAddress = workAddress;
    }

    public void setWorkName(String workName)
    {
        this.workName = workName;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("CHILD_AGE", this.childAge);
        data.put("CITY_CODE", this.cityCode);
        data.put("CUST_NAME", this.custName);
        data.put("EMAIL", this.email);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("FAX_NBR", this.faxNbr);
        data.put("HAS_CAR", this.hasCar);
        data.put("HAS_CHILD", this.hasChild);
        data.put("HAS_FETION", this.hasFetion);
        data.put("HEAD_CUST_ID", this.headCustId);
        data.put("HEAD_PSPT_ID", this.headPsptId);
        data.put("HEAD_TYPE_CODE", this.headTypeCode);
        data.put("HOME_ADDRESS", this.homeAddress);
        data.put("HOME_CUST_ID", this.homeCustId);
        data.put("HOME_CUST_SCORE", this.homeCustScore);
        data.put("HOME_ID", this.homeId);
        data.put("HOME_NAME", this.homeName);
        data.put("HOME_PHONE", this.homePhone);
        data.put("HOME_POST_CODE", this.homePostCode);
        data.put("HOME_REGION", this.homeRegion);
        data.put("HOME_STATE", this.homeState);
        data.put("INTEREST_CODE", this.interestCode);
        data.put("IN_DATE", this.inDate);
        data.put("IN_DEPART_ID", this.inDepartId);
        data.put("IN_STAFF_ID", this.inStaffId);
        data.put("MEMBER_NUM", this.memberNum);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("MSN", this.msn);
        data.put("PHONE", this.phone);
        data.put("QQ", this.qq);
        data.put("REMOVE_DATE", this.removeDate);
        data.put("REMOVE_DEPART_ID", this.removeDepartId);
        data.put("REMOVE_REASON", this.removeReason);
        data.put("REMOVE_STAFF_ID", this.removeStaffId);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("UNIFY_PAY_CODE", this.unifyPayCode);
        data.put("WORK_ADDRESS", this.workAddress);
        data.put("WORK_NAME", this.workName);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
