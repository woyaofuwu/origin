
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class UserTradeData extends BaseTradeData
{
    private String acctTag;

    private String assureCustId;

    private String assureDate;

    private String assureTypeCode;

    private String changeuserDate;

    private String cityCode;

    private String cityCodeA;

    private String contractId;

    private String custId;

    private String destroyTime;

    private String developCityCode;

    private String developDate;

    private String developDepartId;

    private String developEparchyCode;

    private String developNo;

    private String developStaffId;

    private String eparchyCode;

    private String firstCallTime;

    private String inDate;

    private String inDepartId;

    private String inNetMode;

    private String inStaffId;

    private String lastStopTime;

    private String modifyTag;

    private String mputeDate;

    private String mputeMonthFee;

    private String netTypeCode;

    private String openDate;

    private String openDepartId;

    private String openMode;

    private String openStaffId;

    private String prepayTag;

    private String preDestroyTime;

    private String remark;

    private String removeCityCode;

    private String removeDepartId;

    private String removeEparchyCode;

    private String removeReasonCode;

    private String removeTag;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

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

    private String usecustId;

    private String userDiffCode;

    private String userId;

    private String userPasswd;

    private String userStateCodeset;

    private String userTagSet;

    private String userTypeCode;

    private String state;

    public UserTradeData()
    {

    }

    public UserTradeData(IData data)
    {
        this.acctTag = data.getString("ACCT_TAG");
        this.assureCustId = data.getString("ASSURE_CUST_ID");
        this.assureDate = data.getString("ASSURE_DATE");
        this.assureTypeCode = data.getString("ASSURE_TYPE_CODE");
        this.changeuserDate = data.getString("CHANGEUSER_DATE");
        this.cityCode = data.getString("CITY_CODE");
        this.cityCodeA = data.getString("CITY_CODE_A");
        this.contractId = data.getString("CONTRACT_ID");
        this.custId = data.getString("CUST_ID");
        this.destroyTime = data.getString("DESTROY_TIME");
        this.developCityCode = data.getString("DEVELOP_CITY_CODE");
        this.developDate = data.getString("DEVELOP_DATE");
        this.developDepartId = data.getString("DEVELOP_DEPART_ID");
        this.developEparchyCode = data.getString("DEVELOP_EPARCHY_CODE");
        this.developNo = data.getString("DEVELOP_NO");
        this.developStaffId = data.getString("DEVELOP_STAFF_ID");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.firstCallTime = data.getString("FIRST_CALL_TIME");
        this.inDate = data.getString("IN_DATE");
        this.inDepartId = data.getString("IN_DEPART_ID");
        this.inNetMode = data.getString("IN_NET_MODE");
        this.inStaffId = data.getString("IN_STAFF_ID");
        this.lastStopTime = data.getString("LAST_STOP_TIME");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.mputeDate = data.getString("MPUTE_DATE");
        this.mputeMonthFee = data.getString("MPUTE_MONTH_FEE");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.openDate = data.getString("OPEN_DATE");
        this.openDepartId = data.getString("OPEN_DEPART_ID");
        this.openMode = data.getString("OPEN_MODE");
        this.openStaffId = data.getString("OPEN_STAFF_ID");
        this.prepayTag = data.getString("PREPAY_TAG");
        this.preDestroyTime = data.getString("PRE_DESTROY_TIME");
        this.remark = data.getString("REMARK");
        this.removeCityCode = data.getString("REMOVE_CITY_CODE");
        this.removeDepartId = data.getString("REMOVE_DEPART_ID");
        this.removeEparchyCode = data.getString("REMOVE_EPARCHY_CODE");
        this.removeReasonCode = data.getString("REMOVE_REASON_CODE");
        this.removeTag = data.getString("REMOVE_TAG");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
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
        this.usecustId = data.getString("USECUST_ID");
        this.userDiffCode = data.getString("USER_DIFF_CODE");
        this.userId = data.getString("USER_ID");
        this.userPasswd = data.getString("USER_PASSWD");
        this.userStateCodeset = data.getString("USER_STATE_CODESET");
        this.userTagSet = data.getString("USER_TAG_SET");
        this.userTypeCode = data.getString("USER_TYPE_CODE");

        this.state = data.getString("STATE");
    }

    @Override
    public UserTradeData clone()
    {
        UserTradeData userTradeData = new UserTradeData();
        userTradeData.setAcctTag(this.getAcctTag());
        userTradeData.setAssureCustId(this.getAssureCustId());
        userTradeData.setAssureDate(this.getAssureDate());
        userTradeData.setAssureTypeCode(this.getAssureTypeCode());
        userTradeData.setChangeuserDate(this.getChangeuserDate());
        userTradeData.setCityCode(this.getCityCode());
        userTradeData.setCityCodeA(this.getCityCodeA());
        userTradeData.setContractId(this.getContractId());
        userTradeData.setCustId(this.getCustId());
        userTradeData.setDestroyTime(this.getDestroyTime());
        userTradeData.setDevelopCityCode(this.getDevelopCityCode());
        userTradeData.setDevelopDate(this.getDevelopDate());
        userTradeData.setDevelopDepartId(this.getDevelopDepartId());
        userTradeData.setDevelopEparchyCode(this.getDevelopEparchyCode());
        userTradeData.setDevelopNo(this.getDevelopNo());
        userTradeData.setDevelopStaffId(this.getDevelopStaffId());
        userTradeData.setEparchyCode(this.getEparchyCode());
        userTradeData.setFirstCallTime(this.getFirstCallTime());
        userTradeData.setInDate(this.getInDate());
        userTradeData.setInDepartId(this.getInDepartId());
        userTradeData.setInNetMode(this.getInNetMode());
        userTradeData.setInStaffId(this.getInStaffId());
        userTradeData.setLastStopTime(this.getLastStopTime());
        userTradeData.setModifyTag(this.getModifyTag());
        userTradeData.setMputeDate(this.getMputeDate());
        userTradeData.setMputeMonthFee(this.getMputeMonthFee());
        userTradeData.setNetTypeCode(this.getNetTypeCode());
        userTradeData.setOpenDate(this.getOpenDate());
        userTradeData.setOpenDepartId(this.getOpenDepartId());
        userTradeData.setOpenMode(this.getOpenMode());
        userTradeData.setOpenStaffId(this.getOpenStaffId());
        userTradeData.setPrepayTag(this.getPrepayTag());
        userTradeData.setPreDestroyTime(this.getPreDestroyTime());
        userTradeData.setRemark(this.getRemark());
        userTradeData.setRemoveCityCode(this.getRemoveCityCode());
        userTradeData.setRemoveDepartId(this.getRemoveDepartId());
        userTradeData.setRemoveEparchyCode(this.getRemoveEparchyCode());
        userTradeData.setRemoveReasonCode(this.getRemoveReasonCode());
        userTradeData.setRemoveTag(this.getRemoveTag());
        userTradeData.setRsrvDate1(this.getRsrvDate1());
        userTradeData.setRsrvDate2(this.getRsrvDate2());
        userTradeData.setRsrvDate3(this.getRsrvDate3());
        userTradeData.setRsrvNum1(this.getRsrvNum1());
        userTradeData.setRsrvNum2(this.getRsrvNum2());
        userTradeData.setRsrvNum3(this.getRsrvNum3());
        userTradeData.setRsrvNum4(this.getRsrvNum4());
        userTradeData.setRsrvNum5(this.getRsrvNum5());
        userTradeData.setRsrvStr1(this.getRsrvStr1());
        userTradeData.setRsrvStr10(this.getRsrvStr10());
        userTradeData.setRsrvStr2(this.getRsrvStr2());
        userTradeData.setRsrvStr3(this.getRsrvStr3());
        userTradeData.setRsrvStr4(this.getRsrvStr4());
        userTradeData.setRsrvStr5(this.getRsrvStr5());
        userTradeData.setRsrvStr6(this.getRsrvStr6());
        userTradeData.setRsrvStr7(this.getRsrvStr7());
        userTradeData.setRsrvStr8(this.getRsrvStr8());
        userTradeData.setRsrvStr9(this.getRsrvStr9());
        userTradeData.setRsrvTag1(this.getRsrvTag1());
        userTradeData.setRsrvTag2(this.getRsrvTag2());
        userTradeData.setRsrvTag3(this.getRsrvTag3());
        userTradeData.setSerialNumber(this.getSerialNumber());
        userTradeData.setUsecustId(this.getUsecustId());
        userTradeData.setUserDiffCode(this.getUserDiffCode());
        userTradeData.setUserId(this.getUserId());
        userTradeData.setUserPasswd(this.getUserPasswd());
        userTradeData.setUserStateCodeset(this.getUserStateCodeset());
        userTradeData.setUserTagSet(this.getUserTagSet());
        userTradeData.setUserTypeCode(this.getUserTypeCode());

        userTradeData.setState(this.getState());

        return userTradeData;
    }

    public String getAcctTag()
    {
        return acctTag;
    }

    public String getAssureCustId()
    {
        return assureCustId;
    }

    public String getAssureDate()
    {
        return assureDate;
    }

    public String getAssureTypeCode()
    {
        return assureTypeCode;
    }

    public String getChangeuserDate()
    {
        return changeuserDate;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCityCodeA()
    {
        return cityCodeA;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getDestroyTime()
    {
        return destroyTime;
    }

    public String getDevelopCityCode()
    {
        return developCityCode;
    }

    public String getDevelopDate()
    {
        return developDate;
    }

    public String getDevelopDepartId()
    {
        return developDepartId;
    }

    public String getDevelopEparchyCode()
    {
        return developEparchyCode;
    }

    public String getDevelopNo()
    {
        return developNo;
    }

    public String getDevelopStaffId()
    {
        return developStaffId;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getFirstCallTime()
    {
        return firstCallTime;
    }

    public String getInDate()
    {
        return inDate;
    }

    public String getInDepartId()
    {
        return inDepartId;
    }

    public String getInNetMode()
    {
        return inNetMode;
    }

    public String getInStaffId()
    {
        return inStaffId;
    }

    public String getLastStopTime()
    {
        return lastStopTime;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getMputeDate()
    {
        return mputeDate;
    }

    public String getMputeMonthFee()
    {
        return mputeMonthFee;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getOpenDate()
    {
        return openDate;
    }

    public String getOpenDepartId()
    {
        return openDepartId;
    }

    public String getOpenMode()
    {
        return openMode;
    }

    public String getOpenStaffId()
    {
        return openStaffId;
    }

    public String getPreDestroyTime()
    {
        return preDestroyTime;
    }

    public String getPrepayTag()
    {
        return prepayTag;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveCityCode()
    {
        return removeCityCode;
    }

    public String getRemoveDepartId()
    {
        return removeDepartId;
    }

    public String getRemoveEparchyCode()
    {
        return removeEparchyCode;
    }

    public String getRemoveReasonCode()
    {
        return removeReasonCode;
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

    public String getState()
    {
        return state;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_USER";
    }

    public String getUsecustId()
    {
        return usecustId;
    }

    public String getUserDiffCode()
    {
        return userDiffCode;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserPasswd()
    {
        return userPasswd;
    }

    public String getUserStateCodeset()
    {
        return userStateCodeset;
    }

    public String getUserTagSet()
    {
        return userTagSet;
    }

    public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public void setAcctTag(String acctTag)
    {
        this.acctTag = acctTag;
    }

    public void setAssureCustId(String assureCustId)
    {
        this.assureCustId = assureCustId;
    }

    public void setAssureDate(String assureDate)
    {
        this.assureDate = assureDate;
    }

    public void setAssureTypeCode(String assureTypeCode)
    {
        this.assureTypeCode = assureTypeCode;
    }

    public void setChangeuserDate(String changeuserDate)
    {
        this.changeuserDate = changeuserDate;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCityCodeA(String cityCodeA)
    {
        this.cityCodeA = cityCodeA;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setDestroyTime(String destroyTime)
    {
        this.destroyTime = destroyTime;
    }

    public void setDevelopCityCode(String developCityCode)
    {
        this.developCityCode = developCityCode;
    }

    public void setDevelopDate(String developDate)
    {
        this.developDate = developDate;
    }

    public void setDevelopDepartId(String developDepartId)
    {
        this.developDepartId = developDepartId;
    }

    public void setDevelopEparchyCode(String developEparchyCode)
    {
        this.developEparchyCode = developEparchyCode;
    }

    public void setDevelopNo(String developNo)
    {
        this.developNo = developNo;
    }

    public void setDevelopStaffId(String developStaffId)
    {
        this.developStaffId = developStaffId;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setFirstCallTime(String firstCallTime)
    {
        this.firstCallTime = firstCallTime;
    }

    public void setInDate(String inDate)
    {
        this.inDate = inDate;
    }

    public void setInDepartId(String inDepartId)
    {
        this.inDepartId = inDepartId;
    }

    public void setInNetMode(String inNetMode)
    {
        this.inNetMode = inNetMode;
    }

    public void setInStaffId(String inStaffId)
    {
        this.inStaffId = inStaffId;
    }

    public void setLastStopTime(String lastStopTime)
    {
        this.lastStopTime = lastStopTime;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setMputeDate(String mputeDate)
    {
        this.mputeDate = mputeDate;
    }

    public void setMputeMonthFee(String mputeMonthFee)
    {
        this.mputeMonthFee = mputeMonthFee;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public void setOpenDepartId(String openDepartId)
    {
        this.openDepartId = openDepartId;
    }

    public void setOpenMode(String openMode)
    {
        this.openMode = openMode;
    }

    public void setOpenStaffId(String openStaffId)
    {
        this.openStaffId = openStaffId;
    }

    public void setPreDestroyTime(String preDestroyTime)
    {
        this.preDestroyTime = preDestroyTime;
    }

    public void setPrepayTag(String prepayTag)
    {
        this.prepayTag = prepayTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRemoveCityCode(String removeCityCode)
    {
        this.removeCityCode = removeCityCode;
    }

    public void setRemoveDepartId(String removeDepartId)
    {
        this.removeDepartId = removeDepartId;
    }

    public void setRemoveEparchyCode(String removeEparchyCode)
    {
        this.removeEparchyCode = removeEparchyCode;
    }

    public void setRemoveReasonCode(String removeReasonCode)
    {
        this.removeReasonCode = removeReasonCode;
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

    public void setState(String state)
    {
        this.state = state;
    }

    public void setUsecustId(String usecustId)
    {
        this.usecustId = usecustId;
    }

    public void setUserDiffCode(String userDiffCode)
    {
        this.userDiffCode = userDiffCode;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserPasswd(String userPasswd)
    {
        this.userPasswd = userPasswd;
    }

    public void setUserStateCodeset(String userStateCodeset)
    {
        this.userStateCodeset = userStateCodeset;
    }

    public void setUserTagSet(String userTagSet)
    {
        this.userTagSet = userTagSet;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_TAG", this.acctTag);
        data.put("ASSURE_CUST_ID", this.assureCustId);
        data.put("ASSURE_DATE", this.assureDate);
        data.put("ASSURE_TYPE_CODE", this.assureTypeCode);
        data.put("CHANGEUSER_DATE", this.changeuserDate);
        data.put("CITY_CODE", this.cityCode);
        data.put("CITY_CODE_A", this.cityCodeA);
        data.put("CONTRACT_ID", this.contractId);
        data.put("CUST_ID", this.custId);
        data.put("DESTROY_TIME", this.destroyTime);
        data.put("DEVELOP_CITY_CODE", this.developCityCode);
        data.put("DEVELOP_DATE", this.developDate);
        data.put("DEVELOP_DEPART_ID", this.developDepartId);
        data.put("DEVELOP_EPARCHY_CODE", this.developEparchyCode);
        data.put("DEVELOP_NO", this.developNo);
        data.put("DEVELOP_STAFF_ID", this.developStaffId);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("FIRST_CALL_TIME", this.firstCallTime);
        data.put("IN_DATE", this.inDate);
        data.put("IN_DEPART_ID", this.inDepartId);
        data.put("IN_NET_MODE", this.inNetMode);
        data.put("IN_STAFF_ID", this.inStaffId);
        data.put("LAST_STOP_TIME", this.lastStopTime);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("MPUTE_DATE", this.mputeDate);
        data.put("MPUTE_MONTH_FEE", this.mputeMonthFee);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("OPEN_DATE", this.openDate);
        data.put("OPEN_DEPART_ID", this.openDepartId);
        data.put("OPEN_MODE", this.openMode);
        data.put("OPEN_STAFF_ID", this.openStaffId);
        data.put("PREPAY_TAG", this.prepayTag);
        data.put("PRE_DESTROY_TIME", this.preDestroyTime);
        data.put("REMARK", this.remark);
        data.put("REMOVE_CITY_CODE", this.removeCityCode);
        data.put("REMOVE_DEPART_ID", this.removeDepartId);
        data.put("REMOVE_EPARCHY_CODE", this.removeEparchyCode);
        data.put("REMOVE_REASON_CODE", this.removeReasonCode);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
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
        data.put("USECUST_ID", this.usecustId);
        data.put("USER_DIFF_CODE", this.userDiffCode);
        data.put("USER_ID", this.userId);
        data.put("USER_PASSWD", this.userPasswd);
        data.put("USER_STATE_CODESET", this.userStateCodeset);
        data.put("USER_TAG_SET", this.userTagSet);
        data.put("USER_TYPE_CODE", this.userTypeCode);

        data.put("STATE", this.state);

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
