
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class VipTradeData extends BaseTradeData
{
    private String approvalDesc;

    private String approvalFlag;

    private String approvalStaffId;

    private String approvalTime;

    private String assignDate;

    private String assignStaffId;

    private String birthday;

    private String birthdayFlag;

    private String birthdayLunar;

    private String brandCode;

    private String cancelTag;

    private String checkNo;

    private String cityCode;

    private String classChangeDate;

    private String classChangeDateB;

    private String clubId;

    private String creditClass;

    private String creditValue;

    private String ctagSet;

    private String custId;

    private String custManagerAppr;

    private String custManagerId;

    private String custManagerIdB;

    private String custName;

    private String eparchyCode;

    private String firstCallTime;

    private String groupBrandCode;

    private String groupCustName;

    private String groupId;

    private String hvalueTag;

    private String identityChkDate;

    private String identityChkScore;

    private String identityEffDate;

    private String identityExpDate;

    private String identityPri;

    private String innetNum;

    private String joinDate;

    private String joinDateB;

    private String joinDepartId;

    private String joinStaffId;

    private String joinType;

    private String lastStopTime;

    private String lastVipClassId;

    private String lastVipClassIdB;

    private String lastVipTypeCode;

    private String lastVipTypeCodeB;

    private String monthFee;

    private String netTypeCode;

    private String openDate;

    private String productId;

    private String remark;

    private String removeDate;

    private String removeReason;

    private String removeStaffId;

    private String removeTag;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvDate4;

    private String rsrvDate5;

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

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvTag4;

    private String rsrvTag5;

    private String scoreValue;

    private String serialNumber;

    private String svcCycleCode;

    private String svcModeCode;

    private String svcNum;

    private String svcNumB;

    private String syncTime;

    private String tradeLogId;

    private String tradeTypeCode;

    private String usecustId;

    private String usecustName;

    private String usephone;

    private String usepostAddr;

    private String usepsptAddr;

    private String usepsptEndDate;

    private String usepsptId;

    private String usepsptTypeCode;

    private String userId;

    private String userStateCodeset;

    private String userTypeCode;

    private String vipCardChangeDate;

    private String vipCardChangeReason;

    private String vipCardEndDate;

    private String vipCardInfo;

    private String vipCardNo;

    private String vipCardPasswd;

    private String vipCardPostAddr;

    private String vipCardSendDate;

    private String vipCardSendType;

    private String vipCardSpell;

    private String vipCardStartDate;

    private String vipCardState;

    private String vipCardType;

    private String vipClassId;

    private String vipClassIdB;

    private String vipId;

    private String vipTypeCode;

    private String vipTypeCodeB;

    private String visitNum;

    public VipTradeData()
    {

    }

    public VipTradeData(IData data)
    {
        this.approvalDesc = data.getString("APPROVAL_DESC");
        this.approvalFlag = data.getString("APPROVAL_FLAG");
        this.approvalStaffId = data.getString("APPROVAL_STAFF_ID");
        this.approvalTime = data.getString("APPROVAL_TIME");
        this.assignDate = data.getString("ASSIGN_DATE");
        this.assignStaffId = data.getString("ASSIGN_STAFF_ID");
        this.birthday = data.getString("BIRTHDAY");
        this.birthdayFlag = data.getString("BIRTHDAY_FLAG");
        this.birthdayLunar = data.getString("BIRTHDAY_LUNAR");
        this.brandCode = data.getString("BRAND_CODE");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.checkNo = data.getString("CHECK_NO");
        this.cityCode = data.getString("CITY_CODE");
        this.classChangeDate = data.getString("CLASS_CHANGE_DATE");
        this.classChangeDateB = data.getString("CLASS_CHANGE_DATE_B");
        this.clubId = data.getString("CLUB_ID");
        this.creditClass = data.getString("CREDIT_CLASS");
        this.creditValue = data.getString("CREDIT_VALUE");
        this.ctagSet = data.getString("CTAG_SET");
        this.custId = data.getString("CUST_ID");
        this.custManagerAppr = data.getString("CUST_MANAGER_APPR");
        this.custManagerId = data.getString("CUST_MANAGER_ID");
        this.custManagerIdB = data.getString("CUST_MANAGER_ID_B");
        this.custName = data.getString("CUST_NAME");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.firstCallTime = data.getString("FIRST_CALL_TIME");
        this.groupBrandCode = data.getString("GROUP_BRAND_CODE");
        this.groupCustName = data.getString("GROUP_CUST_NAME");
        this.groupId = data.getString("GROUP_ID");
        this.hvalueTag = data.getString("HVALUE_TAG");
        this.identityChkDate = data.getString("IDENTITY_CHK_DATE");
        this.identityChkScore = data.getString("IDENTITY_CHK_SCORE");
        this.identityEffDate = data.getString("IDENTITY_EFF_DATE");
        this.identityExpDate = data.getString("IDENTITY_EXP_DATE");
        this.identityPri = data.getString("IDENTITY_PRI");
        this.innetNum = data.getString("INNET_NUM");
        this.joinDate = data.getString("JOIN_DATE");
        this.joinDateB = data.getString("JOIN_DATE_B");
        this.joinDepartId = data.getString("JOIN_DEPART_ID");
        this.joinStaffId = data.getString("JOIN_STAFF_ID");
        this.joinType = data.getString("JOIN_TYPE");
        this.lastStopTime = data.getString("LAST_STOP_TIME");
        this.lastVipClassId = data.getString("LAST_VIP_CLASS_ID");
        this.lastVipClassIdB = data.getString("LAST_VIP_CLASS_ID_B");
        this.lastVipTypeCode = data.getString("LAST_VIP_TYPE_CODE");
        this.lastVipTypeCodeB = data.getString("LAST_VIP_TYPE_CODE_B");
        this.monthFee = data.getString("MONTH_FEE");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.openDate = data.getString("OPEN_DATE");
        this.productId = data.getString("PRODUCT_ID");
        this.remark = data.getString("REMARK");
        this.removeDate = data.getString("REMOVE_DATE");
        this.removeReason = data.getString("REMOVE_REASON");
        this.removeStaffId = data.getString("REMOVE_STAFF_ID");
        this.removeTag = data.getString("REMOVE_TAG");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvDate4 = data.getString("RSRV_DATE4");
        this.rsrvDate5 = data.getString("RSRV_DATE5");
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
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvTag4 = data.getString("RSRV_TAG4");
        this.rsrvTag5 = data.getString("RSRV_TAG5");
        this.scoreValue = data.getString("SCORE_VALUE");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.svcCycleCode = data.getString("SVC_CYCLE_CODE");
        this.svcModeCode = data.getString("SVC_MODE_CODE");
        this.svcNum = data.getString("SVC_NUM");
        this.svcNumB = data.getString("SVC_NUM_B");
        this.syncTime = data.getString("SYNC_TIME");
        this.tradeLogId = data.getString("TRADE_LOG_ID");
        this.tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        this.usecustId = data.getString("USECUST_ID");
        this.usecustName = data.getString("USECUST_NAME");
        this.usephone = data.getString("USEPHONE");
        this.usepostAddr = data.getString("USEPOST_ADDR");
        this.usepsptAddr = data.getString("USEPSPT_ADDR");
        this.usepsptEndDate = data.getString("USEPSPT_END_DATE");
        this.usepsptId = data.getString("USEPSPT_ID");
        this.usepsptTypeCode = data.getString("USEPSPT_TYPE_CODE");
        this.userId = data.getString("USER_ID");
        this.userStateCodeset = data.getString("USER_STATE_CODESET");
        this.userTypeCode = data.getString("USER_TYPE_CODE");
        this.vipCardChangeDate = data.getString("VIP_CARD_CHANGE_DATE");
        this.vipCardChangeReason = data.getString("VIP_CARD_CHANGE_REASON");
        this.vipCardEndDate = data.getString("VIP_CARD_END_DATE");
        this.vipCardInfo = data.getString("VIP_CARD_INFO");
        this.vipCardNo = data.getString("VIP_CARD_NO");
        this.vipCardPasswd = data.getString("VIP_CARD_PASSWD");
        this.vipCardPostAddr = data.getString("VIP_CARD_POST_ADDR");
        this.vipCardSendDate = data.getString("VIP_CARD_SEND_DATE");
        this.vipCardSendType = data.getString("VIP_CARD_SEND_TYPE");
        this.vipCardSpell = data.getString("VIP_CARD_SPELL");
        this.vipCardStartDate = data.getString("VIP_CARD_START_DATE");
        this.vipCardState = data.getString("VIP_CARD_STATE");
        this.vipCardType = data.getString("VIP_CARD_TYPE");
        this.vipClassId = data.getString("VIP_CLASS_ID");
        this.vipClassIdB = data.getString("VIP_CLASS_ID_B");
        this.vipId = data.getString("VIP_ID");
        this.vipTypeCode = data.getString("VIP_TYPE_CODE");
        this.vipTypeCodeB = data.getString("VIP_TYPE_CODE_B");
        this.visitNum = data.getString("VISIT_NUM");
    }

    public VipTradeData clone()
    {
        VipTradeData vipTradeData = new VipTradeData();
        vipTradeData.setApprovalDesc(this.getApprovalDesc());
        vipTradeData.setApprovalFlag(this.getApprovalFlag());
        vipTradeData.setApprovalStaffId(this.getApprovalStaffId());
        vipTradeData.setApprovalTime(this.getApprovalTime());
        vipTradeData.setAssignDate(this.getAssignDate());
        vipTradeData.setAssignStaffId(this.getAssignStaffId());
        vipTradeData.setBirthday(this.getBirthday());
        vipTradeData.setBirthdayFlag(this.getBirthdayFlag());
        vipTradeData.setBirthdayLunar(this.getBirthdayLunar());
        vipTradeData.setBrandCode(this.getBrandCode());
        vipTradeData.setCancelTag(this.getCancelTag());
        vipTradeData.setCheckNo(this.getCheckNo());
        vipTradeData.setCityCode(this.getCityCode());
        vipTradeData.setClassChangeDate(this.getClassChangeDate());
        vipTradeData.setClassChangeDateB(this.getClassChangeDateB());
        vipTradeData.setClubId(this.getClubId());
        vipTradeData.setCreditClass(this.getCreditClass());
        vipTradeData.setCreditValue(this.getCreditValue());
        vipTradeData.setCtagSet(this.getCtagSet());
        vipTradeData.setCustId(this.getCustId());
        vipTradeData.setCustManagerAppr(this.getCustManagerAppr());
        vipTradeData.setCustManagerId(this.getCustManagerId());
        vipTradeData.setCustManagerIdB(this.getCustManagerIdB());
        vipTradeData.setCustName(this.getCustName());
        vipTradeData.setEparchyCode(this.getEparchyCode());
        vipTradeData.setFirstCallTime(this.getFirstCallTime());
        vipTradeData.setGroupBrandCode(this.getGroupBrandCode());
        vipTradeData.setGroupCustName(this.getGroupCustName());
        vipTradeData.setGroupId(this.getGroupId());
        vipTradeData.setHvalueTag(this.getHvalueTag());
        vipTradeData.setIdentityChkDate(this.getIdentityChkDate());
        vipTradeData.setIdentityChkScore(this.getIdentityChkScore());
        vipTradeData.setIdentityEffDate(this.getIdentityEffDate());
        vipTradeData.setIdentityExpDate(this.getIdentityExpDate());
        vipTradeData.setIdentityPri(this.getIdentityPri());
        vipTradeData.setInnetNum(this.getInnetNum());
        vipTradeData.setJoinDate(this.getJoinDate());
        vipTradeData.setJoinDateB(this.getJoinDateB());
        vipTradeData.setJoinDepartId(this.getJoinDepartId());
        vipTradeData.setJoinStaffId(this.getJoinStaffId());
        vipTradeData.setJoinType(this.getJoinType());
        vipTradeData.setLastStopTime(this.getLastStopTime());
        vipTradeData.setLastVipClassId(this.getLastVipClassId());
        vipTradeData.setLastVipClassIdB(this.getLastVipClassIdB());
        vipTradeData.setLastVipTypeCode(this.getLastVipTypeCode());
        vipTradeData.setLastVipTypeCodeB(this.getLastVipTypeCodeB());
        vipTradeData.setMonthFee(this.getMonthFee());
        vipTradeData.setNetTypeCode(this.getNetTypeCode());
        vipTradeData.setOpenDate(this.getOpenDate());
        vipTradeData.setProductId(this.getProductId());
        vipTradeData.setRemark(this.getRemark());
        vipTradeData.setRemoveDate(this.getRemoveDate());
        vipTradeData.setRemoveReason(this.getRemoveReason());
        vipTradeData.setRemoveStaffId(this.getRemoveStaffId());
        vipTradeData.setRemoveTag(this.getRemoveTag());
        vipTradeData.setRsrvDate1(this.getRsrvDate1());
        vipTradeData.setRsrvDate2(this.getRsrvDate2());
        vipTradeData.setRsrvDate3(this.getRsrvDate3());
        vipTradeData.setRsrvDate4(this.getRsrvDate4());
        vipTradeData.setRsrvDate5(this.getRsrvDate5());
        vipTradeData.setRsrvNum1(this.getRsrvNum1());
        vipTradeData.setRsrvNum2(this.getRsrvNum2());
        vipTradeData.setRsrvNum3(this.getRsrvNum3());
        vipTradeData.setRsrvNum4(this.getRsrvNum4());
        vipTradeData.setRsrvNum5(this.getRsrvNum5());
        vipTradeData.setRsrvStr1(this.getRsrvStr1());
        vipTradeData.setRsrvStr2(this.getRsrvStr2());
        vipTradeData.setRsrvStr3(this.getRsrvStr3());
        vipTradeData.setRsrvStr4(this.getRsrvStr4());
        vipTradeData.setRsrvStr5(this.getRsrvStr5());
        vipTradeData.setRsrvStr6(this.getRsrvStr6());
        vipTradeData.setRsrvStr7(this.getRsrvStr7());
        vipTradeData.setRsrvStr8(this.getRsrvStr8());
        vipTradeData.setRsrvTag1(this.getRsrvTag1());
        vipTradeData.setRsrvTag2(this.getRsrvTag2());
        vipTradeData.setRsrvTag3(this.getRsrvTag3());
        vipTradeData.setRsrvTag4(this.getRsrvTag4());
        vipTradeData.setRsrvTag5(this.getRsrvTag5());
        vipTradeData.setScoreValue(this.getScoreValue());
        vipTradeData.setSerialNumber(this.getSerialNumber());
        vipTradeData.setSvcCycleCode(this.getSvcCycleCode());
        vipTradeData.setSvcModeCode(this.getSvcModeCode());
        vipTradeData.setSvcNum(this.getSvcNum());
        vipTradeData.setSvcNumB(this.getSvcNumB());
        vipTradeData.setSyncTime(this.getSyncTime());
        vipTradeData.setTradeLogId(this.getTradeLogId());
        vipTradeData.setTradeTypeCode(this.getTradeTypeCode());
        vipTradeData.setUsecustId(this.getUsecustId());
        vipTradeData.setUsecustName(this.getUsecustName());
        vipTradeData.setUsephone(this.getUsephone());
        vipTradeData.setUsepostAddr(this.getUsepostAddr());
        vipTradeData.setUsepsptAddr(this.getUsepsptAddr());
        vipTradeData.setUsepsptEndDate(this.getUsepsptEndDate());
        vipTradeData.setUsepsptId(this.getUsepsptId());
        vipTradeData.setUsepsptTypeCode(this.getUsepsptTypeCode());
        vipTradeData.setUserId(this.getUserId());
        vipTradeData.setUserStateCodeset(this.getUserStateCodeset());
        vipTradeData.setUserTypeCode(this.getUserTypeCode());
        vipTradeData.setVipCardChangeDate(this.getVipCardChangeDate());
        vipTradeData.setVipCardChangeReason(this.getVipCardChangeReason());
        vipTradeData.setVipCardEndDate(this.getVipCardEndDate());
        vipTradeData.setVipCardInfo(this.getVipCardInfo());
        vipTradeData.setVipCardNo(this.getVipCardNo());
        vipTradeData.setVipCardPasswd(this.getVipCardPasswd());
        vipTradeData.setVipCardPostAddr(this.getVipCardPostAddr());
        vipTradeData.setVipCardSendDate(this.getVipCardSendDate());
        vipTradeData.setVipCardSendType(this.getVipCardSendType());
        vipTradeData.setVipCardSpell(this.getVipCardSpell());
        vipTradeData.setVipCardStartDate(this.getVipCardStartDate());
        vipTradeData.setVipCardState(this.getVipCardState());
        vipTradeData.setVipCardType(this.getVipCardType());
        vipTradeData.setVipClassId(this.getVipClassId());
        vipTradeData.setVipClassIdB(this.getVipClassIdB());
        vipTradeData.setVipId(this.getVipId());
        vipTradeData.setVipTypeCode(this.getVipTypeCode());
        vipTradeData.setVipTypeCodeB(this.getVipTypeCodeB());
        vipTradeData.setVisitNum(this.getVisitNum());
        return vipTradeData;
    }

    public String getApprovalDesc()
    {
        return approvalDesc;
    }

    public String getApprovalFlag()
    {
        return approvalFlag;
    }

    public String getApprovalStaffId()
    {
        return approvalStaffId;
    }

    public String getApprovalTime()
    {
        return approvalTime;
    }

    public String getAssignDate()
    {
        return assignDate;
    }

    public String getAssignStaffId()
    {
        return assignStaffId;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public String getBirthdayFlag()
    {
        return birthdayFlag;
    }

    public String getBirthdayLunar()
    {
        return birthdayLunar;
    }

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getCheckNo()
    {
        return checkNo;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getClassChangeDate()
    {
        return classChangeDate;
    }

    public String getClassChangeDateB()
    {
        return classChangeDateB;
    }

    public String getClubId()
    {
        return clubId;
    }

    public String getCreditClass()
    {
        return creditClass;
    }

    public String getCreditValue()
    {
        return creditValue;
    }

    public String getCtagSet()
    {
        return ctagSet;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getCustManagerAppr()
    {
        return custManagerAppr;
    }

    public String getCustManagerId()
    {
        return custManagerId;
    }

    public String getCustManagerIdB()
    {
        return custManagerIdB;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getFirstCallTime()
    {
        return firstCallTime;
    }

    public String getGroupBrandCode()
    {
        return groupBrandCode;
    }

    public String getGroupCustName()
    {
        return groupCustName;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getHvalueTag()
    {
        return hvalueTag;
    }

    public String getIdentityChkDate()
    {
        return identityChkDate;
    }

    public String getIdentityChkScore()
    {
        return identityChkScore;
    }

    public String getIdentityEffDate()
    {
        return identityEffDate;
    }

    public String getIdentityExpDate()
    {
        return identityExpDate;
    }

    public String getIdentityPri()
    {
        return identityPri;
    }

    public String getInnetNum()
    {
        return innetNum;
    }

    public String getJoinDate()
    {
        return joinDate;
    }

    public String getJoinDateB()
    {
        return joinDateB;
    }

    public String getJoinDepartId()
    {
        return joinDepartId;
    }

    public String getJoinStaffId()
    {
        return joinStaffId;
    }

    public String getJoinType()
    {
        return joinType;
    }

    public String getLastStopTime()
    {
        return lastStopTime;
    }

    public String getLastVipClassId()
    {
        return lastVipClassId;
    }

    public String getLastVipClassIdB()
    {
        return lastVipClassIdB;
    }

    public String getLastVipTypeCode()
    {
        return lastVipTypeCode;
    }

    public String getLastVipTypeCodeB()
    {
        return lastVipTypeCodeB;
    }

    public String getMonthFee()
    {
        return monthFee;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getOpenDate()
    {
        return openDate;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveDate()
    {
        return removeDate;
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

    public String getRsrvDate4()
    {
        return rsrvDate4;
    }

    public String getRsrvDate5()
    {
        return rsrvDate5;
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

    public String getRsrvTag4()
    {
        return rsrvTag4;
    }

    public String getRsrvTag5()
    {
        return rsrvTag5;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSvcCycleCode()
    {
        return svcCycleCode;
    }

    public String getSvcModeCode()
    {
        return svcModeCode;
    }

    public String getSvcNum()
    {
        return svcNum;
    }

    public String getSvcNumB()
    {
        return svcNumB;
    }

    public String getSyncTime()
    {
        return syncTime;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_VIP";
    }

    public String getTradeLogId()
    {
        return tradeLogId;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public String getUsecustId()
    {
        return usecustId;
    }

    public String getUsecustName()
    {
        return usecustName;
    }

    public String getUsephone()
    {
        return usephone;
    }

    public String getUsepostAddr()
    {
        return usepostAddr;
    }

    public String getUsepsptAddr()
    {
        return usepsptAddr;
    }

    public String getUsepsptEndDate()
    {
        return usepsptEndDate;
    }

    public String getUsepsptId()
    {
        return usepsptId;
    }

    public String getUsepsptTypeCode()
    {
        return usepsptTypeCode;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserStateCodeset()
    {
        return userStateCodeset;
    }

    public String getUserTypeCode()
    {
        return userTypeCode;
    }

    public String getVipCardChangeDate()
    {
        return vipCardChangeDate;
    }

    public String getVipCardChangeReason()
    {
        return vipCardChangeReason;
    }

    public String getVipCardEndDate()
    {
        return vipCardEndDate;
    }

    public String getVipCardInfo()
    {
        return vipCardInfo;
    }

    public String getVipCardNo()
    {
        return vipCardNo;
    }

    public String getVipCardPasswd()
    {
        return vipCardPasswd;
    }

    public String getVipCardPostAddr()
    {
        return vipCardPostAddr;
    }

    public String getVipCardSendDate()
    {
        return vipCardSendDate;
    }

    public String getVipCardSendType()
    {
        return vipCardSendType;
    }

    public String getVipCardSpell()
    {
        return vipCardSpell;
    }

    public String getVipCardStartDate()
    {
        return vipCardStartDate;
    }

    public String getVipCardState()
    {
        return vipCardState;
    }

    public String getVipCardType()
    {
        return vipCardType;
    }

    public String getVipClassId()
    {
        return vipClassId;
    }

    public String getVipClassIdB()
    {
        return vipClassIdB;
    }

    public String getVipId()
    {
        return vipId;
    }

    public String getVipTypeCode()
    {
        return vipTypeCode;
    }

    public String getVipTypeCodeB()
    {
        return vipTypeCodeB;
    }

    public String getVisitNum()
    {
        return visitNum;
    }

    public void setApprovalDesc(String approvalDesc)
    {
        this.approvalDesc = approvalDesc;
    }

    public void setApprovalFlag(String approvalFlag)
    {
        this.approvalFlag = approvalFlag;
    }

    public void setApprovalStaffId(String approvalStaffId)
    {
        this.approvalStaffId = approvalStaffId;
    }

    public void setApprovalTime(String approvalTime)
    {
        this.approvalTime = approvalTime;
    }

    public void setAssignDate(String assignDate)
    {
        this.assignDate = assignDate;
    }

    public void setAssignStaffId(String assignStaffId)
    {
        this.assignStaffId = assignStaffId;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public void setBirthdayFlag(String birthdayFlag)
    {
        this.birthdayFlag = birthdayFlag;
    }

    public void setBirthdayLunar(String birthdayLunar)
    {
        this.birthdayLunar = birthdayLunar;
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setCheckNo(String checkNo)
    {
        this.checkNo = checkNo;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setClassChangeDate(String classChangeDate)
    {
        this.classChangeDate = classChangeDate;
    }

    public void setClassChangeDateB(String classChangeDateB)
    {
        this.classChangeDateB = classChangeDateB;
    }

    public void setClubId(String clubId)
    {
        this.clubId = clubId;
    }

    public void setCreditClass(String creditClass)
    {
        this.creditClass = creditClass;
    }

    public void setCreditValue(String creditValue)
    {
        this.creditValue = creditValue;
    }

    public void setCtagSet(String ctagSet)
    {
        this.ctagSet = ctagSet;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setCustManagerAppr(String custManagerAppr)
    {
        this.custManagerAppr = custManagerAppr;
    }

    public void setCustManagerId(String custManagerId)
    {
        this.custManagerId = custManagerId;
    }

    public void setCustManagerIdB(String custManagerIdB)
    {
        this.custManagerIdB = custManagerIdB;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setFirstCallTime(String firstCallTime)
    {
        this.firstCallTime = firstCallTime;
    }

    public void setGroupBrandCode(String groupBrandCode)
    {
        this.groupBrandCode = groupBrandCode;
    }

    public void setGroupCustName(String groupCustName)
    {
        this.groupCustName = groupCustName;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setHvalueTag(String hvalueTag)
    {
        this.hvalueTag = hvalueTag;
    }

    public void setIdentityChkDate(String identityChkDate)
    {
        this.identityChkDate = identityChkDate;
    }

    public void setIdentityChkScore(String identityChkScore)
    {
        this.identityChkScore = identityChkScore;
    }

    public void setIdentityEffDate(String identityEffDate)
    {
        this.identityEffDate = identityEffDate;
    }

    public void setIdentityExpDate(String identityExpDate)
    {
        this.identityExpDate = identityExpDate;
    }

    public void setIdentityPri(String identityPri)
    {
        this.identityPri = identityPri;
    }

    public void setInnetNum(String innetNum)
    {
        this.innetNum = innetNum;
    }

    public void setJoinDate(String joinDate)
    {
        this.joinDate = joinDate;
    }

    public void setJoinDateB(String joinDateB)
    {
        this.joinDateB = joinDateB;
    }

    public void setJoinDepartId(String joinDepartId)
    {
        this.joinDepartId = joinDepartId;
    }

    public void setJoinStaffId(String joinStaffId)
    {
        this.joinStaffId = joinStaffId;
    }

    public void setJoinType(String joinType)
    {
        this.joinType = joinType;
    }

    public void setLastStopTime(String lastStopTime)
    {
        this.lastStopTime = lastStopTime;
    }

    public void setLastVipClassId(String lastVipClassId)
    {
        this.lastVipClassId = lastVipClassId;
    }

    public void setLastVipClassIdB(String lastVipClassIdB)
    {
        this.lastVipClassIdB = lastVipClassIdB;
    }

    public void setLastVipTypeCode(String lastVipTypeCode)
    {
        this.lastVipTypeCode = lastVipTypeCode;
    }

    public void setLastVipTypeCodeB(String lastVipTypeCodeB)
    {
        this.lastVipTypeCodeB = lastVipTypeCodeB;
    }

    public void setMonthFee(String monthFee)
    {
        this.monthFee = monthFee;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
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

    public void setRsrvDate4(String rsrvDate4)
    {
        this.rsrvDate4 = rsrvDate4;
    }

    public void setRsrvDate5(String rsrvDate5)
    {
        this.rsrvDate5 = rsrvDate5;
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

    public void setRsrvTag4(String rsrvTag4)
    {
        this.rsrvTag4 = rsrvTag4;
    }

    public void setRsrvTag5(String rsrvTag5)
    {
        this.rsrvTag5 = rsrvTag5;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSvcCycleCode(String svcCycleCode)
    {
        this.svcCycleCode = svcCycleCode;
    }

    public void setSvcModeCode(String svcModeCode)
    {
        this.svcModeCode = svcModeCode;
    }

    public void setSvcNum(String svcNum)
    {
        this.svcNum = svcNum;
    }

    public void setSvcNumB(String svcNumB)
    {
        this.svcNumB = svcNumB;
    }

    public void setSyncTime(String syncTime)
    {
        this.syncTime = syncTime;
    }

    public void setTradeLogId(String tradeLogId)
    {
        this.tradeLogId = tradeLogId;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setUsecustId(String usecustId)
    {
        this.usecustId = usecustId;
    }

    public void setUsecustName(String usecustName)
    {
        this.usecustName = usecustName;
    }

    public void setUsephone(String usephone)
    {
        this.usephone = usephone;
    }

    public void setUsepostAddr(String usepostAddr)
    {
        this.usepostAddr = usepostAddr;
    }

    public void setUsepsptAddr(String usepsptAddr)
    {
        this.usepsptAddr = usepsptAddr;
    }

    public void setUsepsptEndDate(String usepsptEndDate)
    {
        this.usepsptEndDate = usepsptEndDate;
    }

    public void setUsepsptId(String usepsptId)
    {
        this.usepsptId = usepsptId;
    }

    public void setUsepsptTypeCode(String usepsptTypeCode)
    {
        this.usepsptTypeCode = usepsptTypeCode;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public void setUserStateCodeset(String userStateCodeset)
    {
        this.userStateCodeset = userStateCodeset;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    public void setVipCardChangeDate(String vipCardChangeDate)
    {
        this.vipCardChangeDate = vipCardChangeDate;
    }

    public void setVipCardChangeReason(String vipCardChangeReason)
    {
        this.vipCardChangeReason = vipCardChangeReason;
    }

    public void setVipCardEndDate(String vipCardEndDate)
    {
        this.vipCardEndDate = vipCardEndDate;
    }

    public void setVipCardInfo(String vipCardInfo)
    {
        this.vipCardInfo = vipCardInfo;
    }

    public void setVipCardNo(String vipCardNo)
    {
        this.vipCardNo = vipCardNo;
    }

    public void setVipCardPasswd(String vipCardPasswd)
    {
        this.vipCardPasswd = vipCardPasswd;
    }

    public void setVipCardPostAddr(String vipCardPostAddr)
    {
        this.vipCardPostAddr = vipCardPostAddr;
    }

    public void setVipCardSendDate(String vipCardSendDate)
    {
        this.vipCardSendDate = vipCardSendDate;
    }

    public void setVipCardSendType(String vipCardSendType)
    {
        this.vipCardSendType = vipCardSendType;
    }

    public void setVipCardSpell(String vipCardSpell)
    {
        this.vipCardSpell = vipCardSpell;
    }

    public void setVipCardStartDate(String vipCardStartDate)
    {
        this.vipCardStartDate = vipCardStartDate;
    }

    public void setVipCardState(String vipCardState)
    {
        this.vipCardState = vipCardState;
    }

    public void setVipCardType(String vipCardType)
    {
        this.vipCardType = vipCardType;
    }

    public void setVipClassId(String vipClassId)
    {
        this.vipClassId = vipClassId;
    }

    public void setVipClassIdB(String vipClassIdB)
    {
        this.vipClassIdB = vipClassIdB;
    }

    public void setVipId(String vipId)
    {
        this.vipId = vipId;
    }

    public void setVipTypeCode(String vipTypeCode)
    {
        this.vipTypeCode = vipTypeCode;
    }

    public void setVipTypeCodeB(String vipTypeCodeB)
    {
        this.vipTypeCodeB = vipTypeCodeB;
    }

    public void setVisitNum(String visitNum)
    {
        this.visitNum = visitNum;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("APPROVAL_DESC", this.approvalDesc);
        data.put("APPROVAL_FLAG", this.approvalFlag);
        data.put("APPROVAL_STAFF_ID", this.approvalStaffId);
        data.put("APPROVAL_TIME", this.approvalTime);
        data.put("ASSIGN_DATE", this.assignDate);
        data.put("ASSIGN_STAFF_ID", this.assignStaffId);
        data.put("BIRTHDAY", this.birthday);
        data.put("BIRTHDAY_FLAG", this.birthdayFlag);
        data.put("BIRTHDAY_LUNAR", this.birthdayLunar);
        data.put("BRAND_CODE", this.brandCode);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("CHECK_NO", this.checkNo);
        data.put("CITY_CODE", this.cityCode);
        data.put("CLASS_CHANGE_DATE", this.classChangeDate);
        data.put("CLASS_CHANGE_DATE_B", this.classChangeDateB);
        data.put("CLUB_ID", this.clubId);
        data.put("CREDIT_CLASS", this.creditClass);
        data.put("CREDIT_VALUE", this.creditValue);
        data.put("CTAG_SET", this.ctagSet);
        data.put("CUST_ID", this.custId);
        data.put("CUST_MANAGER_APPR", this.custManagerAppr);
        data.put("CUST_MANAGER_ID", this.custManagerId);
        data.put("CUST_MANAGER_ID_B", this.custManagerIdB);
        data.put("CUST_NAME", this.custName);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("FIRST_CALL_TIME", this.firstCallTime);
        data.put("GROUP_BRAND_CODE", this.groupBrandCode);
        data.put("GROUP_CUST_NAME", this.groupCustName);
        data.put("GROUP_ID", this.groupId);
        data.put("HVALUE_TAG", this.hvalueTag);
        data.put("IDENTITY_CHK_DATE", this.identityChkDate);
        data.put("IDENTITY_CHK_SCORE", this.identityChkScore);
        data.put("IDENTITY_EFF_DATE", this.identityEffDate);
        data.put("IDENTITY_EXP_DATE", this.identityExpDate);
        data.put("IDENTITY_PRI", this.identityPri);
        data.put("INNET_NUM", this.innetNum);
        data.put("JOIN_DATE", this.joinDate);
        data.put("JOIN_DATE_B", this.joinDateB);
        data.put("JOIN_DEPART_ID", this.joinDepartId);
        data.put("JOIN_STAFF_ID", this.joinStaffId);
        data.put("JOIN_TYPE", this.joinType);
        data.put("LAST_STOP_TIME", this.lastStopTime);
        data.put("LAST_VIP_CLASS_ID", this.lastVipClassId);
        data.put("LAST_VIP_CLASS_ID_B", this.lastVipClassIdB);
        data.put("LAST_VIP_TYPE_CODE", this.lastVipTypeCode);
        data.put("LAST_VIP_TYPE_CODE_B", this.lastVipTypeCodeB);
        data.put("MONTH_FEE", this.monthFee);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("OPEN_DATE", this.openDate);
        data.put("PRODUCT_ID", this.productId);
        data.put("REMARK", this.remark);
        data.put("REMOVE_DATE", this.removeDate);
        data.put("REMOVE_REASON", this.removeReason);
        data.put("REMOVE_STAFF_ID", this.removeStaffId);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_DATE4", this.rsrvDate4);
        data.put("RSRV_DATE5", this.rsrvDate5);
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
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_TAG4", this.rsrvTag4);
        data.put("RSRV_TAG5", this.rsrvTag5);
        data.put("SCORE_VALUE", this.scoreValue);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("SVC_CYCLE_CODE", this.svcCycleCode);
        data.put("SVC_MODE_CODE", this.svcModeCode);
        data.put("SVC_NUM", this.svcNum);
        data.put("SVC_NUM_B", this.svcNumB);
        data.put("SYNC_TIME", this.syncTime);
        data.put("TRADE_LOG_ID", this.tradeLogId);
        data.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        data.put("USECUST_ID", this.usecustId);
        data.put("USECUST_NAME", this.usecustName);
        data.put("USEPHONE", this.usephone);
        data.put("USEPOST_ADDR", this.usepostAddr);
        data.put("USEPSPT_ADDR", this.usepsptAddr);
        data.put("USEPSPT_END_DATE", this.usepsptEndDate);
        data.put("USEPSPT_ID", this.usepsptId);
        data.put("USEPSPT_TYPE_CODE", this.usepsptTypeCode);
        data.put("USER_ID", this.userId);
        data.put("USER_STATE_CODESET", this.userStateCodeset);
        data.put("USER_TYPE_CODE", this.userTypeCode);
        data.put("VIP_CARD_CHANGE_DATE", this.vipCardChangeDate);
        data.put("VIP_CARD_CHANGE_REASON", this.vipCardChangeReason);
        data.put("VIP_CARD_END_DATE", this.vipCardEndDate);
        data.put("VIP_CARD_INFO", this.vipCardInfo);
        data.put("VIP_CARD_NO", this.vipCardNo);
        data.put("VIP_CARD_PASSWD", this.vipCardPasswd);
        data.put("VIP_CARD_POST_ADDR", this.vipCardPostAddr);
        data.put("VIP_CARD_SEND_DATE", this.vipCardSendDate);
        data.put("VIP_CARD_SEND_TYPE", this.vipCardSendType);
        data.put("VIP_CARD_SPELL", this.vipCardSpell);
        data.put("VIP_CARD_START_DATE", this.vipCardStartDate);
        data.put("VIP_CARD_STATE", this.vipCardState);
        data.put("VIP_CARD_TYPE", this.vipCardType);
        data.put("VIP_CLASS_ID", this.vipClassId);
        data.put("VIP_CLASS_ID_B", this.vipClassIdB);
        data.put("VIP_ID", this.vipId);
        data.put("VIP_TYPE_CODE", this.vipTypeCode);
        data.put("VIP_TYPE_CODE_B", this.vipTypeCodeB);
        data.put("VISIT_NUM", this.visitNum);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
