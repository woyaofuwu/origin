
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class CustGroupTradeData extends BaseTradeData
{
    private String acceptChannel;

    private String agreement;

    private String assignDate;

    private String assignStaffId;

    private String auditDate;

    private String auditNote;

    private String auditStaffId;

    private String auditState;

    private String bankAcct;

    private String bankName;

    private String baseAccessNo;

    private String baseAccessNoKind;

    private String bossFeeSum;

    private String busiLicenceNo;

    private String busiLicenceType;

    private String busiLicenceValidDate;

    private String busiType;

    private String callingAreaCode;

    private String callingPolicyForce;

    private String callingTypeCode;

    private String callType;

    private String chipAreaCode;

    private String cityCode;

    private String cityCodeU;

    private String classChangeDate;

    private String classId;

    private String classId2;

    private String commBudget;

    private String consume;

    private String custAim;

    private String custClassType;

    private String custId;

    private String custManagerAppr;

    private String custManagerId;

    private String custName;

    private String custServNbr;

    private IData datamap = null;

    private String doyenStaffId;

    private String earningOrder;

    private String ecCode;

    private String email;

    private String employeeArpu;

    private String empLsave;

    private String empNumAll;

    private String empNumChina;

    private String empNumLocal;

    private String enterpriseScope;

    private String enterpriseSizeCode;

    private String enterpriseTypeCode;

    private String eparchyCode;

    private String faxNbr;

    private String financeEarning;

    private String groupAddr;

    private String groupAdversary;

    private String groupAttr;

    private String groupContactPhone;

    private String groupId;

    private String groupMemo;

    private String groupMgrCustName;

    private String groupMgrSn;

    private String groupMgrUserId;

    private String groupPayMode;

    private String groupRole;

    private String groupSource;

    private String groupStatus;

    private String groupSumScore;

    private String groupType;

    private String groupValidScore;

    private String gtelBudget;

    private String ifShortPin;

    private String inDate;

    private String inDepartId;

    private String inStaffId;

    private String isProductGroup;

    private String juristicCustId;

    private String juristicName;

    private String juristicTypeCode;

    private String lastClassId;

    private String latencyFeeSum;

    private String likeDiscntMode;

    private String likeMobileTrade;

    private String linkmanDutyId;

    private String linkmanEmailAddr;

    private String linkmanHomeAddr;

    private String linkmanHomePhone;

    private String linkmanHomePost;

    private String ltelBudget;

    private String mainBusi;

    private String mainTrade;

    private String mobileNumChinago;

    private String mobileNumGlobal;

    private String mobileNumLocal;

    private String mobileNumMzone;

    private String mobilePayout;

    private String mpGroupCustCode;

    private String netrentPayout;

    private String newtradeComment;

    private String orgStructCode;

    private String orgTypeA;

    private String orgTypeB;

    private String orgTypeC;

    private String outDate;

    private String payforWayCode;

    private String photoTag;

    private String pnationalGroupId;

    private String pnationalGroupName;

    private String poloticalVillageId;

    private String postCode;

    private String productNumLocal;

    private String productNumOther;

    private String productNumUse;

    private String productTypeId;

    private String provinceCode;

    private String regDate;

    private String regMoney;

    private String remark;

    private String removeChange;

    private String removeDate;

    private String removeFlag;

    private String removeMethod;

    private String removeReasonCode;

    private String removeStaffId;

    private String removeTag;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

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

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String scope;

    private String servLevel;

    private String subCallingTypeCode;

    private String subclassId;

    private String superGroupId;

    private String superGroupName;

    private String telecomNumGh;

    private String telecomNumXlt;

    private String telecomPayoutXlt;

    private String townId;

    private String turnover;

    private String unicomNumC;

    private String unicomNumG;

    private String unicomNumGc;

    private String unicomPayout;

    private String unifyPayCode;

    private String updateDepartId;

    private String updateStaffId;

    private String updateTime;

    private String userNum;

    private String userNumFullfree;

    private String userNumWriteoff;

    private String vpmnGroupId;

    private String vpmnNum;

    private String website;

    private String writefeeCount;

    private String writefeeSum;

    private String yearGain;

    public CustGroupTradeData()
    {

    }

    public CustGroupTradeData(IData data)
    {
        this.setBaseAccessNoKind(data.getString("BASE_ACCESS_NO_KIND"));
        this.setBaseAccessNo(data.getString("BASE_ACCESS_NO"));
        this.setGroupMgrCustName(data.getString("GROUP_MGR_CUST_NAME"));
        this.setGroupMgrUserId(data.getString("GROUP_MGR_USER_ID"));
        this.setGroupMgrSn(data.getString("GROUP_MGR_SN"));
        this.setGroupSumScore(data.getString("GROUP_SUM_SCORE"));
        this.setGroupValidScore(data.getString("GROUP_VALID_SCORE"));
        this.setPostCode(data.getString("POST_CODE"));
        this.setEmail(data.getString("EMAIL"));
        this.setFaxNbr(data.getString("FAX_NBR"));
        this.setWebsite(data.getString("WEBSITE"));
        this.setSubclassId(data.getString("SUBCLASS_ID"));
        this.setCallingPolicyForce(data.getString("CALLING_POLICY_FORCE"));
        this.setEarningOrder(data.getString("EARNING_ORDER"));
        this.setFinanceEarning(data.getString("FINANCE_EARNING"));
        this.setLikeDiscntMode(data.getString("LIKE_DISCNT_MODE"));
        this.setLikeMobileTrade(data.getString("LIKE_MOBILE_TRADE"));
        this.setNewtradeComment(data.getString("NEWTRADE_COMMENT"));
        this.setDoyenStaffId(data.getString("DOYEN_STAFF_ID"));
        this.setBossFeeSum(data.getString("BOSS_FEE_SUM"));
        this.setUserNumWriteoff(data.getString("USER_NUM_WRITEOFF"));
        this.setUserNumFullfree(data.getString("USER_NUM_FULLFREE"));
        this.setWritefeeSum(data.getString("WRITEFEE_SUM"));
        this.setWritefeeCount(data.getString("WRITEFEE_COUNT"));
        this.setPayforWayCode(data.getString("PAYFOR_WAY_CODE"));
        this.setGroupPayMode(data.getString("GROUP_PAY_MODE"));
        this.setTelecomPayoutXlt(data.getString("TELECOM_PAYOUT_XLT"));
        this.setUnicomPayout(data.getString("UNICOM_PAYOUT"));
        this.setMobilePayout(data.getString("MOBILE_PAYOUT"));
        this.setNetrentPayout(data.getString("NETRENT_PAYOUT"));
        this.setEmployeeArpu(data.getString("EMPLOYEE_ARPU"));
        this.setProductNumUse(data.getString("PRODUCT_NUM_USE"));
        this.setProductNumOther(data.getString("PRODUCT_NUM_OTHER"));
        this.setProductNumLocal(data.getString("PRODUCT_NUM_LOCAL"));
        this.setUnicomNumGc(data.getString("UNICOM_NUM_GC"));
        this.setUnicomNumC(data.getString("UNICOM_NUM_C"));
        this.setUnicomNumG(data.getString("UNICOM_NUM_G"));
        this.setMobileNumLocal(data.getString("MOBILE_NUM_LOCAL"));
        this.setMobileNumMzone(data.getString("MOBILE_NUM_MZONE"));
        this.setMobileNumGlobal(data.getString("MOBILE_NUM_GLOBAL"));
        this.setMobileNumChinago(data.getString("MOBILE_NUM_CHINAGO"));
        this.setTelecomNumXlt(data.getString("TELECOM_NUM_XLT"));
        this.setTelecomNumGh(data.getString("TELECOM_NUM_GH"));
        this.setEmpNumAll(data.getString("EMP_NUM_ALL"));
        this.setEmpNumChina(data.getString("EMP_NUM_CHINA"));
        this.setEmpNumLocal(data.getString("EMP_NUM_LOCAL"));
        this.setUserNum(data.getString("USER_NUM"));
        this.setVpmnNum(data.getString("VPMN_NUM"));
        this.setVpmnGroupId(data.getString("VPMN_GROUP_ID"));
        this.setGroupAdversary(data.getString("GROUP_ADVERSARY"));
        this.setLtelBudget(data.getString("LTEL_BUDGET"));
        this.setGtelBudget(data.getString("GTEL_BUDGET"));
        this.setCommBudget(data.getString("COMM_BUDGET"));
        this.setConsume(data.getString("CONSUME"));
        this.setTurnover(data.getString("TURNOVER"));
        this.setYearGain(data.getString("YEAR_GAIN"));
        this.setLatencyFeeSum(data.getString("LATENCY_FEE_SUM"));
        this.setEmpLsave(data.getString("EMP_LSAVE"));
        this.setMainTrade(data.getString("MAIN_TRADE"));
        this.setMainBusi(data.getString("MAIN_BUSI"));
        this.setScope(data.getString("SCOPE"));
        this.setCustAim(data.getString("CUST_AIM"));
        this.setRegDate(data.getString("REG_DATE"));
        this.setRegMoney(data.getString("REG_MONEY"));
        this.setBankName(data.getString("BANK_NAME"));
        this.setBankAcct(data.getString("BANK_ACCT"));
        this.setGroupMemo(data.getString("GROUP_MEMO"));
        this.setBusiLicenceValidDate(data.getString("BUSI_LICENCE_VALID_DATE"));
        this.setBusiLicenceNo(data.getString("BUSI_LICENCE_NO"));
        this.setBusiLicenceType(data.getString("BUSI_LICENCE_TYPE"));
        this.setJuristicName(data.getString("JURISTIC_NAME"));
        this.setJuristicCustId(data.getString("JURISTIC_CUST_ID"));
        this.setJuristicTypeCode(data.getString("JURISTIC_TYPE_CODE"));
        this.setEnterpriseScope(data.getString("ENTERPRISE_SCOPE"));
        this.setEnterpriseSizeCode(data.getString("ENTERPRISE_SIZE_CODE"));
        this.setEnterpriseTypeCode(data.getString("ENTERPRISE_TYPE_CODE"));
        this.setGroupContactPhone(data.getString("GROUP_CONTACT_PHONE"));
        this.setBusiType(data.getString("BUSI_TYPE"));
        this.setAgreement(data.getString("AGREEMENT"));
        this.setAcceptChannel(data.getString("ACCEPT_CHANNEL"));
        this.setCallType(data.getString("CALL_TYPE"));
        this.setCallingAreaCode(data.getString("CALLING_AREA_CODE"));
        this.setSubCallingTypeCode(data.getString("SUB_CALLING_TYPE_CODE"));
        this.setCallingTypeCode(data.getString("CALLING_TYPE_CODE"));
        this.setOrgTypeC(data.getString("ORG_TYPE_C"));
        this.setOrgTypeB(data.getString("ORG_TYPE_B"));
        this.setOrgTypeA(data.getString("ORG_TYPE_A"));
        this.setAssignStaffId(data.getString("ASSIGN_STAFF_ID"));
        this.setAssignDate(data.getString("ASSIGN_DATE"));
        this.setCustManagerAppr(data.getString("CUST_MANAGER_APPR"));
        this.setCustManagerId(data.getString("CUST_MANAGER_ID"));
        this.setOrgStructCode(data.getString("ORG_STRUCT_CODE"));
        this.setUnifyPayCode(data.getString("UNIFY_PAY_CODE"));
        this.setMpGroupCustCode(data.getString("MP_GROUP_CUST_CODE"));
        this.setPnationalGroupName(data.getString("PNATIONAL_GROUP_NAME"));
        this.setPnationalGroupId(data.getString("PNATIONAL_GROUP_ID"));
        this.setSuperGroupName(data.getString("SUPER_GROUP_NAME"));
        this.setSuperGroupId(data.getString("SUPER_GROUP_ID"));
        this.setCityCodeU(data.getString("CITY_CODE_U"));
        this.setCityCode(data.getString("CITY_CODE"));
        this.setEparchyCode(data.getString("EPARCHY_CODE"));
        this.setProvinceCode(data.getString("PROVINCE_CODE"));
        this.setGroupSource(data.getString("GROUP_SOURCE"));
        this.setGroupAddr(data.getString("GROUP_ADDR"));
        this.setGroupStatus(data.getString("GROUP_STATUS"));
        this.setGroupAttr(data.getString("GROUP_ATTR"));
        this.setCustClassType(data.getString("CUST_CLASS_TYPE"));
        this.setClassChangeDate(data.getString("CLASS_CHANGE_DATE"));
        this.setLastClassId(data.getString("LAST_CLASS_ID"));
        this.setClassId2(data.getString("CLASS_ID2"));
        this.setClassId(data.getString("CLASS_ID"));
        this.setGroupRole(data.getString("GROUP_ROLE"));
        this.setGroupType(data.getString("GROUP_TYPE"));
        this.setCustName(data.getString("CUST_NAME"));
        this.setGroupId(data.getString("GROUP_ID"));
        this.setCustId(data.getString("CUST_ID"));
        this.setLinkmanDutyId(data.getString("LINKMAN_DUTY_ID"));
        this.setLinkmanHomePhone(data.getString("LINKMAN_HOME_PHONE"));
        this.setLinkmanEmailAddr(data.getString("LINKMAN_EMAIL_ADDR"));
        this.setLinkmanHomeAddr(data.getString("LINKMAN_HOME_ADDR"));
        this.setLinkmanHomePost(data.getString("LINKMAN_HOME_POST"));
        this.setProductTypeId(data.getString("PRODUCT_TYPE_ID"));
        this.setIsProductGroup(data.getString("IS_PRODUCT_GROUP"));
        this.setPhotoTag(data.getString("PHOTO_TAG"));
        this.setServLevel(data.getString("SERV_LEVEL"));
        this.setChipAreaCode(data.getString("CHIP_AREA_CODE"));
        this.setTownId(data.getString("TOWN_ID"));
        this.setPoloticalVillageId(data.getString("POLOTICAL_VILLAGE_ID"));
        this.setRsrvTag3(data.getString("RSRV_TAG3"));
        this.setRsrvTag2(data.getString("RSRV_TAG2"));
        this.setRsrvTag1(data.getString("RSRV_TAG1"));
        this.setRsrvDate3(data.getString("RSRV_DATE3"));
        this.setRsrvDate2(data.getString("RSRV_DATE2"));
        this.setRsrvDate1(data.getString("RSRV_DATE1"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvNum3(data.getString("RSRV_NUM3"));
        this.setRsrvNum2(data.getString("RSRV_NUM2"));
        this.setRsrvNum1(data.getString("RSRV_NUM1"));
        this.setRemark(data.getString("REMARK"));
        this.setUpdateDepartId(data.getString("UPDATE_DEPART_ID"));
        this.setUpdateStaffId(data.getString("UPDATE_STAFF_ID"));
        this.setUpdateTime(data.getString("UPDATE_TIME"));
        this.setRemoveStaffId(data.getString("REMOVE_STAFF_ID"));
        this.setRemoveDate(data.getString("REMOVE_DATE"));
        this.setRemoveChange(data.getString("REMOVE_CHANGE"));
        this.setRemoveReasonCode(data.getString("REMOVE_REASON_CODE"));
        this.setRemoveMethod(data.getString("REMOVE_METHOD"));
        this.setRemoveFlag(data.getString("REMOVE_FLAG"));
        this.setRemoveTag(data.getString("REMOVE_TAG"));
        this.setOutDate(data.getString("OUT_DATE"));
        this.setInDepartId(data.getString("IN_DEPART_ID"));
        this.setInStaffId(data.getString("IN_STAFF_ID"));
        this.setInDate(data.getString("IN_DATE"));
        this.setAuditNote(data.getString("AUDIT_NOTE"));
        this.setAuditStaffId(data.getString("AUDIT_STAFF_ID"));
        this.setAuditDate(data.getString("AUDIT_DATE"));
        this.setAuditState(data.getString("AUDIT_STATE"));
        this.setIfShortPin(data.getString("IF_SHORT_PIN"));
        this.setEcCode(data.getString("EC_CODE"));
        this.setCustServNbr(data.getString("CUST_SERV_NBR"));

        toData_();
    }

    @Override
    public CustGroupTradeData clone()
    {
        CustGroupTradeData custGroupTradeData = new CustGroupTradeData();
        custGroupTradeData.setBaseAccessNoKind(this.getBaseAccessNoKind());
        custGroupTradeData.setBaseAccessNo(this.getBaseAccessNo());
        custGroupTradeData.setGroupMgrCustName(this.getGroupMgrCustName());
        custGroupTradeData.setGroupMgrUserId(this.getGroupMgrUserId());
        custGroupTradeData.setGroupMgrSn(this.getGroupMgrSn());
        custGroupTradeData.setGroupSumScore(this.getGroupSumScore());
        custGroupTradeData.setGroupValidScore(this.getGroupValidScore());
        custGroupTradeData.setPostCode(this.getPostCode());
        custGroupTradeData.setEmail(this.getEmail());
        custGroupTradeData.setFaxNbr(this.getFaxNbr());
        custGroupTradeData.setWebsite(this.getWebsite());
        custGroupTradeData.setSubclassId(this.getSubclassId());
        custGroupTradeData.setCallingPolicyForce(this.getCallingPolicyForce());
        custGroupTradeData.setEarningOrder(this.getEarningOrder());
        custGroupTradeData.setFinanceEarning(this.getFinanceEarning());
        custGroupTradeData.setLikeDiscntMode(this.getLikeDiscntMode());
        custGroupTradeData.setLikeMobileTrade(this.getLikeMobileTrade());
        custGroupTradeData.setNewtradeComment(this.getNewtradeComment());
        custGroupTradeData.setDoyenStaffId(this.getDoyenStaffId());
        custGroupTradeData.setBossFeeSum(this.getBossFeeSum());
        custGroupTradeData.setUserNumWriteoff(this.getUserNumWriteoff());
        custGroupTradeData.setUserNumFullfree(this.getUserNumFullfree());
        custGroupTradeData.setWritefeeSum(this.getWritefeeSum());
        custGroupTradeData.setWritefeeCount(this.getWritefeeCount());
        custGroupTradeData.setPayforWayCode(this.getPayforWayCode());
        custGroupTradeData.setGroupPayMode(this.getGroupPayMode());
        custGroupTradeData.setTelecomPayoutXlt(this.getTelecomPayoutXlt());
        custGroupTradeData.setUnicomPayout(this.getUnicomPayout());
        custGroupTradeData.setMobilePayout(this.getMobilePayout());
        custGroupTradeData.setNetrentPayout(this.getNetrentPayout());
        custGroupTradeData.setEmployeeArpu(this.getEmployeeArpu());
        custGroupTradeData.setProductNumUse(this.getProductNumUse());
        custGroupTradeData.setProductNumOther(this.getProductNumOther());
        custGroupTradeData.setProductNumLocal(this.getProductNumLocal());
        custGroupTradeData.setUnicomNumGc(this.getUnicomNumGc());
        custGroupTradeData.setUnicomNumC(this.getUnicomNumC());
        custGroupTradeData.setUnicomNumG(this.getUnicomNumG());
        custGroupTradeData.setMobileNumLocal(this.getMobileNumLocal());
        custGroupTradeData.setMobileNumMzone(this.getMobileNumMzone());
        custGroupTradeData.setMobileNumGlobal(this.getMobileNumGlobal());
        custGroupTradeData.setMobileNumChinago(this.getMobileNumChinago());
        custGroupTradeData.setTelecomNumXlt(this.getTelecomNumXlt());
        custGroupTradeData.setTelecomNumGh(this.getTelecomNumGh());
        custGroupTradeData.setEmpNumAll(this.getEmpNumAll());
        custGroupTradeData.setEmpNumChina(this.getEmpNumChina());
        custGroupTradeData.setEmpNumLocal(this.getEmpNumLocal());
        custGroupTradeData.setUserNum(this.getUserNum());
        custGroupTradeData.setVpmnNum(this.getVpmnNum());
        custGroupTradeData.setVpmnGroupId(this.getVpmnGroupId());
        custGroupTradeData.setGroupAdversary(this.getGroupAdversary());
        custGroupTradeData.setLtelBudget(this.getLtelBudget());
        custGroupTradeData.setGtelBudget(this.getGtelBudget());
        custGroupTradeData.setCommBudget(this.getCommBudget());
        custGroupTradeData.setConsume(this.getConsume());
        custGroupTradeData.setTurnover(this.getTurnover());
        custGroupTradeData.setYearGain(this.getYearGain());
        custGroupTradeData.setLatencyFeeSum(this.getLatencyFeeSum());
        custGroupTradeData.setEmpLsave(this.getEmpLsave());
        custGroupTradeData.setMainTrade(this.getMainTrade());
        custGroupTradeData.setMainBusi(this.getMainBusi());
        custGroupTradeData.setScope(this.getScope());
        custGroupTradeData.setCustAim(this.getCustAim());
        custGroupTradeData.setRegDate(this.getRegDate());
        custGroupTradeData.setRegMoney(this.getRegMoney());
        custGroupTradeData.setBankName(this.getBankName());
        custGroupTradeData.setBankAcct(this.getBankAcct());
        custGroupTradeData.setGroupMemo(this.getGroupMemo());
        custGroupTradeData.setBusiLicenceValidDate(this.getBusiLicenceValidDate());
        custGroupTradeData.setBusiLicenceNo(this.getBusiLicenceNo());
        custGroupTradeData.setBusiLicenceType(this.getBusiLicenceType());
        custGroupTradeData.setJuristicName(this.getJuristicName());
        custGroupTradeData.setJuristicCustId(this.getJuristicCustId());
        custGroupTradeData.setJuristicTypeCode(this.getJuristicTypeCode());
        custGroupTradeData.setEnterpriseScope(this.getEnterpriseScope());
        custGroupTradeData.setEnterpriseSizeCode(this.getEnterpriseSizeCode());
        custGroupTradeData.setEnterpriseTypeCode(this.getEnterpriseTypeCode());
        custGroupTradeData.setGroupContactPhone(this.getGroupContactPhone());
        custGroupTradeData.setBusiType(this.getBusiType());
        custGroupTradeData.setAgreement(this.getAgreement());
        custGroupTradeData.setAcceptChannel(this.getAcceptChannel());
        custGroupTradeData.setCallType(this.getCallType());
        custGroupTradeData.setCallingAreaCode(this.getCallingAreaCode());
        custGroupTradeData.setSubCallingTypeCode(this.getSubCallingTypeCode());
        custGroupTradeData.setCallingTypeCode(this.getCallingTypeCode());
        custGroupTradeData.setOrgTypeC(this.getOrgTypeC());
        custGroupTradeData.setOrgTypeB(this.getOrgTypeB());
        custGroupTradeData.setOrgTypeA(this.getOrgTypeA());
        custGroupTradeData.setAssignStaffId(this.getAssignStaffId());
        custGroupTradeData.setAssignDate(this.getAssignDate());
        custGroupTradeData.setCustManagerAppr(this.getCustManagerAppr());
        custGroupTradeData.setCustManagerId(this.getCustManagerId());
        custGroupTradeData.setOrgStructCode(this.getOrgStructCode());
        custGroupTradeData.setUnifyPayCode(this.getUnifyPayCode());
        custGroupTradeData.setMpGroupCustCode(this.getMpGroupCustCode());
        custGroupTradeData.setPnationalGroupName(this.getPnationalGroupName());
        custGroupTradeData.setPnationalGroupId(this.getPnationalGroupId());
        custGroupTradeData.setSuperGroupName(this.getSuperGroupName());
        custGroupTradeData.setSuperGroupId(this.getSuperGroupId());
        custGroupTradeData.setCityCodeU(this.getCityCodeU());
        custGroupTradeData.setCityCode(this.getCityCode());
        custGroupTradeData.setEparchyCode(this.getEparchyCode());
        custGroupTradeData.setProvinceCode(this.getProvinceCode());
        custGroupTradeData.setGroupSource(this.getGroupSource());
        custGroupTradeData.setGroupAddr(this.getGroupAddr());
        custGroupTradeData.setGroupStatus(this.getGroupStatus());
        custGroupTradeData.setGroupAttr(this.getGroupAttr());
        custGroupTradeData.setCustClassType(this.getCustClassType());
        custGroupTradeData.setClassChangeDate(this.getClassChangeDate());
        custGroupTradeData.setLastClassId(this.getLastClassId());
        custGroupTradeData.setClassId2(this.getClassId2());
        custGroupTradeData.setClassId(this.getClassId());
        custGroupTradeData.setGroupRole(this.getGroupRole());
        custGroupTradeData.setGroupType(this.getGroupType());
        custGroupTradeData.setCustName(this.getCustName());
        custGroupTradeData.setGroupId(this.getGroupId());
        custGroupTradeData.setCustId(this.getCustId());
        custGroupTradeData.setLinkmanDutyId(this.getLinkmanDutyId());
        custGroupTradeData.setLinkmanHomePhone(this.getLinkmanHomePhone());
        custGroupTradeData.setLinkmanEmailAddr(this.getLinkmanEmailAddr());
        custGroupTradeData.setLinkmanHomeAddr(this.getLinkmanHomeAddr());
        custGroupTradeData.setLinkmanHomePost(this.getLinkmanHomePost());
        custGroupTradeData.setProductTypeId(this.getProductTypeId());
        custGroupTradeData.setIsProductGroup(this.getIsProductGroup());
        custGroupTradeData.setPhotoTag(this.getPhotoTag());
        custGroupTradeData.setServLevel(this.getServLevel());
        custGroupTradeData.setChipAreaCode(this.getChipAreaCode());
        custGroupTradeData.setTownId(this.getTownId());
        custGroupTradeData.setPoloticalVillageId(this.getPoloticalVillageId());
        custGroupTradeData.setRsrvTag3(this.getRsrvTag3());
        custGroupTradeData.setRsrvTag2(this.getRsrvTag2());
        custGroupTradeData.setRsrvTag1(this.getRsrvTag1());
        custGroupTradeData.setRsrvDate3(this.getRsrvDate3());
        custGroupTradeData.setRsrvDate2(this.getRsrvDate2());
        custGroupTradeData.setRsrvDate1(this.getRsrvDate1());
        custGroupTradeData.setRsrvStr8(this.getRsrvStr8());
        custGroupTradeData.setRsrvStr7(this.getRsrvStr7());
        custGroupTradeData.setRsrvStr6(this.getRsrvStr6());
        custGroupTradeData.setRsrvStr5(this.getRsrvStr5());
        custGroupTradeData.setRsrvStr4(this.getRsrvStr4());
        custGroupTradeData.setRsrvStr3(this.getRsrvStr3());
        custGroupTradeData.setRsrvStr2(this.getRsrvStr2());
        custGroupTradeData.setRsrvStr1(this.getRsrvStr1());
        custGroupTradeData.setRsrvNum3(this.getRsrvNum3());
        custGroupTradeData.setRsrvNum2(this.getRsrvNum2());
        custGroupTradeData.setRsrvNum1(this.getRsrvNum1());
        custGroupTradeData.setRemark(this.getRemark());
        custGroupTradeData.setUpdateDepartId(this.getUpdateDepartId());
        custGroupTradeData.setUpdateStaffId(this.getUpdateStaffId());
        custGroupTradeData.setUpdateTime(this.getUpdateTime());
        custGroupTradeData.setRemoveStaffId(this.getRemoveStaffId());
        custGroupTradeData.setRemoveDate(this.getRemoveDate());
        custGroupTradeData.setRemoveChange(this.getRemoveChange());
        custGroupTradeData.setRemoveReasonCode(this.getRemoveReasonCode());
        custGroupTradeData.setRemoveMethod(this.getRemoveMethod());
        custGroupTradeData.setRemoveFlag(this.getRemoveFlag());
        custGroupTradeData.setRemoveTag(this.getRemoveTag());
        custGroupTradeData.setOutDate(this.getOutDate());
        custGroupTradeData.setInDepartId(this.getInDepartId());
        custGroupTradeData.setInStaffId(this.getInStaffId());
        custGroupTradeData.setInDate(this.getInDate());
        custGroupTradeData.setAuditNote(this.getAuditNote());
        custGroupTradeData.setAuditStaffId(this.getAuditStaffId());
        custGroupTradeData.setAuditDate(this.getAuditDate());
        custGroupTradeData.setAuditState(this.getAuditState());
        custGroupTradeData.setIfShortPin(this.getIfShortPin());
        custGroupTradeData.setEcCode(this.getEcCode());
        custGroupTradeData.setCustServNbr(this.getCustServNbr());

        custGroupTradeData.toData_();

        return custGroupTradeData;
    }

    public String getAcceptChannel()
    {
        return acceptChannel;
    }

    public String getAgreement()
    {
        return agreement;
    }

    public String getAssignDate()
    {
        return assignDate;
    }

    public String getAssignStaffId()
    {
        return assignStaffId;
    }

    public String getAuditDate()
    {
        return auditDate;
    }

    public String getAuditNote()
    {
        return auditNote;
    }

    public String getAuditStaffId()
    {
        return auditStaffId;
    }

    public String getAuditState()
    {
        return auditState;
    }

    public String getBankAcct()
    {
        return bankAcct;
    }

    public String getBankName()
    {
        return bankName;
    }

    public String getBaseAccessNo()
    {
        return baseAccessNo;
    }

    public String getBaseAccessNoKind()
    {
        return baseAccessNoKind;
    }

    public String getBossFeeSum()
    {
        return bossFeeSum;
    }

    public String getBusiLicenceNo()
    {
        return busiLicenceNo;
    }

    public String getBusiLicenceType()
    {
        return busiLicenceType;
    }

    public String getBusiLicenceValidDate()
    {
        return busiLicenceValidDate;
    }

    public String getBusiType()
    {
        return busiType;
    }

    public String getCallingAreaCode()
    {
        return callingAreaCode;
    }

    public String getCallingPolicyForce()
    {
        return callingPolicyForce;
    }

    public String getCallingTypeCode()
    {
        return callingTypeCode;
    }

    public String getCallType()
    {
        return callType;
    }

    public String getChipAreaCode()
    {
        return chipAreaCode;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCityCodeU()
    {
        return cityCodeU;
    }

    public String getClassChangeDate()
    {
        return classChangeDate;
    }

    public String getClassId()
    {
        return classId;
    }

    public String getClassId2()
    {
        return classId2;
    }

    public String getCommBudget()
    {
        return commBudget;
    }

    public String getConsume()
    {
        return consume;
    }

    public String getCustAim()
    {
        return custAim;
    }

    public String getCustClassType()
    {
        return custClassType;
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

    public String getCustName()
    {
        return custName;
    }

    public String getCustServNbr()
    {
        return custServNbr;
    }

    public String getDoyenStaffId()
    {
        return doyenStaffId;
    }

    public String getEarningOrder()
    {
        return earningOrder;
    }

    public String getEcCode()
    {
        return ecCode;
    }

    public String getEmail()
    {
        return email;
    }

    public String getEmployeeArpu()
    {
        return employeeArpu;
    }

    public String getEmpLsave()
    {
        return empLsave;
    }

    public String getEmpNumAll()
    {
        return empNumAll;
    }

    public String getEmpNumChina()
    {
        return empNumChina;
    }

    public String getEmpNumLocal()
    {
        return empNumLocal;
    }

    public String getEnterpriseScope()
    {
        return enterpriseScope;
    }

    public String getEnterpriseSizeCode()
    {
        return enterpriseSizeCode;
    }

    public String getEnterpriseTypeCode()
    {
        return enterpriseTypeCode;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getFaxNbr()
    {
        return faxNbr;
    }

    public String getFinanceEarning()
    {
        return financeEarning;
    }

    public String getGroupAddr()
    {
        return groupAddr;
    }

    public String getGroupAdversary()
    {
        return groupAdversary;
    }

    public String getGroupAttr()
    {
        return groupAttr;
    }

    public String getGroupContactPhone()
    {
        return groupContactPhone;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getGroupMemo()
    {
        return groupMemo;
    }

    public String getGroupMgrCustName()
    {
        return groupMgrCustName;
    }

    public String getGroupMgrSn()
    {
        return groupMgrSn;
    }

    public String getGroupMgrUserId()
    {
        return groupMgrUserId;
    }

    public String getGroupPayMode()
    {
        return groupPayMode;
    }

    public String getGroupRole()
    {
        return groupRole;
    }

    public String getGroupSource()
    {
        return groupSource;
    }

    public String getGroupStatus()
    {
        return groupStatus;
    }

    public String getGroupSumScore()
    {
        return groupSumScore;
    }

    public String getGroupType()
    {
        return groupType;
    }

    public String getGroupValidScore()
    {
        return groupValidScore;
    }

    public String getGtelBudget()
    {
        return gtelBudget;
    }

    public String getIfShortPin()
    {
        return ifShortPin;
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

    public String getIsProductGroup()
    {
        return isProductGroup;
    }

    public String getJuristicCustId()
    {
        return juristicCustId;
    }

    public String getJuristicName()
    {
        return juristicName;
    }

    public String getJuristicTypeCode()
    {
        return juristicTypeCode;
    }

    public String getLastClassId()
    {
        return lastClassId;
    }

    public String getLatencyFeeSum()
    {
        return latencyFeeSum;
    }

    public String getLikeDiscntMode()
    {
        return likeDiscntMode;
    }

    public String getLikeMobileTrade()
    {
        return likeMobileTrade;
    }

    public String getLinkmanDutyId()
    {
        return linkmanDutyId;
    }

    public String getLinkmanEmailAddr()
    {
        return linkmanEmailAddr;
    }

    public String getLinkmanHomeAddr()
    {
        return linkmanHomeAddr;
    }

    public String getLinkmanHomePhone()
    {
        return linkmanHomePhone;
    }

    public String getLinkmanHomePost()
    {
        return linkmanHomePost;
    }

    public String getLtelBudget()
    {
        return ltelBudget;
    }

    public String getMainBusi()
    {
        return mainBusi;
    }

    public String getMainTrade()
    {
        return mainTrade;
    }

    public String getMobileNumChinago()
    {
        return mobileNumChinago;
    }

    public String getMobileNumGlobal()
    {
        return mobileNumGlobal;
    }

    public String getMobileNumLocal()
    {
        return mobileNumLocal;
    }

    public String getMobileNumMzone()
    {
        return mobileNumMzone;
    }

    public String getMobilePayout()
    {
        return mobilePayout;
    }

    public String getMpGroupCustCode()
    {
        return mpGroupCustCode;
    }

    public String getNetrentPayout()
    {
        return netrentPayout;
    }

    public String getNewtradeComment()
    {
        return newtradeComment;
    }

    public String getOrgStructCode()
    {
        return orgStructCode;
    }

    public String getOrgTypeA()
    {
        return orgTypeA;
    }

    public String getOrgTypeB()
    {
        return orgTypeB;
    }

    public String getOrgTypeC()
    {
        return orgTypeC;
    }

    public String getOutDate()
    {
        return outDate;
    }

    public String getPayforWayCode()
    {
        return payforWayCode;
    }

    public String getPhotoTag()
    {
        return photoTag;
    }

    public String getPnationalGroupId()
    {
        return pnationalGroupId;
    }

    public String getPnationalGroupName()
    {
        return pnationalGroupName;
    }

    public String getPoloticalVillageId()
    {
        return poloticalVillageId;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getProductNumLocal()
    {
        return productNumLocal;
    }

    public String getProductNumOther()
    {
        return productNumOther;
    }

    public String getProductNumUse()
    {
        return productNumUse;
    }

    public String getProductTypeId()
    {
        return productTypeId;
    }

    public String getProvinceCode()
    {
        return provinceCode;
    }

    public String getRegDate()
    {
        return regDate;
    }

    public String getRegMoney()
    {
        return regMoney;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveChange()
    {
        return removeChange;
    }

    public String getRemoveDate()
    {
        return removeDate;
    }

    public String getRemoveFlag()
    {
        return removeFlag;
    }

    public String getRemoveMethod()
    {
        return removeMethod;
    }

    public String getRemoveReasonCode()
    {
        return removeReasonCode;
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

    public String getScope()
    {
        return scope;
    }

    public String getServLevel()
    {
        return servLevel;
    }

    public String getSubCallingTypeCode()
    {
        return subCallingTypeCode;
    }

    public String getSubclassId()
    {
        return subclassId;
    }

    public String getSuperGroupId()
    {
        return superGroupId;
    }

    public String getSuperGroupName()
    {
        return superGroupName;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_CUST_GROUP";
    }

    public String getTelecomNumGh()
    {
        return telecomNumGh;
    }

    public String getTelecomNumXlt()
    {
        return telecomNumXlt;
    }

    public String getTelecomPayoutXlt()
    {
        return telecomPayoutXlt;
    }

    public String getTownId()
    {
        return townId;
    }

    public String getTurnover()
    {
        return turnover;
    }

    public String getUnicomNumC()
    {
        return unicomNumC;
    }

    public String getUnicomNumG()
    {
        return unicomNumG;
    }

    public String getUnicomNumGc()
    {
        return unicomNumGc;
    }

    public String getUnicomPayout()
    {
        return unicomPayout;
    }

    public String getUnifyPayCode()
    {
        return unifyPayCode;
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

    public String getUserNum()
    {
        return userNum;
    }

    public String getUserNumFullfree()
    {
        return userNumFullfree;
    }

    public String getUserNumWriteoff()
    {
        return userNumWriteoff;
    }

    public String getVpmnGroupId()
    {
        return vpmnGroupId;
    }

    public String getVpmnNum()
    {
        return vpmnNum;
    }

    public String getWebsite()
    {
        return website;
    }

    public String getWritefeeCount()
    {
        return writefeeCount;
    }

    public String getWritefeeSum()
    {
        return writefeeSum;
    }

    public String getYearGain()
    {
        return yearGain;
    }

    public void setAcceptChannel(String acceptChannel)
    {
        this.acceptChannel = acceptChannel;
    }

    public void setAgreement(String agreement)
    {
        this.agreement = agreement;
    }

    public void setAssignDate(String assignDate)
    {
        this.assignDate = assignDate;
    }

    public void setAssignStaffId(String assignStaffId)
    {
        this.assignStaffId = assignStaffId;
    }

    public void setAuditDate(String auditDate)
    {
        this.auditDate = auditDate;
    }

    public void setAuditNote(String auditNote)
    {
        this.auditNote = auditNote;
    }

    public void setAuditStaffId(String auditStaffId)
    {
        this.auditStaffId = auditStaffId;
    }

    public void setAuditState(String auditState)
    {
        this.auditState = auditState;
    }

    public void setBankAcct(String bankAcct)
    {
        this.bankAcct = bankAcct;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public void setBaseAccessNo(String baseAccessNo)
    {
        this.baseAccessNo = baseAccessNo;
    }

    public void setBaseAccessNoKind(String baseAccessNoKind)
    {
        this.baseAccessNoKind = baseAccessNoKind;
    }

    public void setBossFeeSum(String bossFeeSum)
    {
        this.bossFeeSum = bossFeeSum;
    }

    public void setBusiLicenceNo(String busiLicenceNo)
    {
        this.busiLicenceNo = busiLicenceNo;
    }

    public void setBusiLicenceType(String busiLicenceType)
    {
        this.busiLicenceType = busiLicenceType;
    }

    public void setBusiLicenceValidDate(String busiLicenceValidDate)
    {
        this.busiLicenceValidDate = busiLicenceValidDate;
    }

    public void setBusiType(String busiType)
    {
        this.busiType = busiType;
    }

    public void setCallingAreaCode(String callingAreaCode)
    {
        this.callingAreaCode = callingAreaCode;
    }

    public void setCallingPolicyForce(String callingPolicyForce)
    {
        this.callingPolicyForce = callingPolicyForce;
    }

    public void setCallingTypeCode(String callingTypeCode)
    {
        this.callingTypeCode = callingTypeCode;
    }

    public void setCallType(String callType)
    {
        this.callType = callType;
    }

    public void setChipAreaCode(String chipAreaCode)
    {
        this.chipAreaCode = chipAreaCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCityCodeU(String cityCodeU)
    {
        this.cityCodeU = cityCodeU;
    }

    public void setClassChangeDate(String classChangeDate)
    {
        this.classChangeDate = classChangeDate;
    }

    public void setClassId(String classId)
    {
        this.classId = classId;
    }

    public void setClassId2(String classId2)
    {
        this.classId2 = classId2;
    }

    public void setCommBudget(String commBudget)
    {
        this.commBudget = commBudget;
    }

    public void setConsume(String consume)
    {
        this.consume = consume;
    }

    public void setCustAim(String custAim)
    {
        this.custAim = custAim;
    }

    public void setCustClassType(String custClassType)
    {
        this.custClassType = custClassType;
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

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setCustServNbr(String custServNbr)
    {
        this.custServNbr = custServNbr;
    }

    public void setDoyenStaffId(String doyenStaffId)
    {
        this.doyenStaffId = doyenStaffId;
    }

    public void setEarningOrder(String earningOrder)
    {
        this.earningOrder = earningOrder;
    }

    public void setEcCode(String ecCode)
    {
        this.ecCode = ecCode;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setEmployeeArpu(String employeeArpu)
    {
        this.employeeArpu = employeeArpu;
    }

    public void setEmpLsave(String empLsave)
    {
        this.empLsave = empLsave;
    }

    public void setEmpNumAll(String empNumAll)
    {
        this.empNumAll = empNumAll;
    }

    public void setEmpNumChina(String empNumChina)
    {
        this.empNumChina = empNumChina;
    }

    public void setEmpNumLocal(String empNumLocal)
    {
        this.empNumLocal = empNumLocal;
    }

    public void setEnterpriseScope(String enterpriseScope)
    {
        this.enterpriseScope = enterpriseScope;
    }

    public void setEnterpriseSizeCode(String enterpriseSizeCode)
    {
        this.enterpriseSizeCode = enterpriseSizeCode;
    }

    public void setEnterpriseTypeCode(String enterpriseTypeCode)
    {
        this.enterpriseTypeCode = enterpriseTypeCode;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setFaxNbr(String faxNbr)
    {
        this.faxNbr = faxNbr;
    }

    public void setFinanceEarning(String financeEarning)
    {
        this.financeEarning = financeEarning;
    }

    public void setGroupAddr(String groupAddr)
    {
        this.groupAddr = groupAddr;
    }

    public void setGroupAdversary(String groupAdversary)
    {
        this.groupAdversary = groupAdversary;
    }

    public void setGroupAttr(String groupAttr)
    {
        this.groupAttr = groupAttr;
    }

    public void setGroupContactPhone(String groupContactPhone)
    {
        this.groupContactPhone = groupContactPhone;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setGroupMemo(String groupMemo)
    {
        this.groupMemo = groupMemo;
    }

    public void setGroupMgrCustName(String groupMgrCustName)
    {
        this.groupMgrCustName = groupMgrCustName;
    }

    public void setGroupMgrSn(String groupMgrSn)
    {
        this.groupMgrSn = groupMgrSn;
    }

    public void setGroupMgrUserId(String groupMgrUserId)
    {
        this.groupMgrUserId = groupMgrUserId;
    }

    public void setGroupPayMode(String groupPayMode)
    {
        this.groupPayMode = groupPayMode;
    }

    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }

    public void setGroupSource(String groupSource)
    {
        this.groupSource = groupSource;
    }

    public void setGroupStatus(String groupStatus)
    {
        this.groupStatus = groupStatus;
    }

    public void setGroupSumScore(String groupSumScore)
    {
        this.groupSumScore = groupSumScore;
    }

    public void setGroupType(String groupType)
    {
        this.groupType = groupType;
    }

    public void setGroupValidScore(String groupValidScore)
    {
        this.groupValidScore = groupValidScore;
    }

    public void setGtelBudget(String gtelBudget)
    {
        this.gtelBudget = gtelBudget;
    }

    public void setIfShortPin(String ifShortPin)
    {
        this.ifShortPin = ifShortPin;
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

    public void setIsProductGroup(String isProductGroup)
    {
        this.isProductGroup = isProductGroup;
    }

    public void setJuristicCustId(String juristicCustId)
    {
        this.juristicCustId = juristicCustId;
    }

    public void setJuristicName(String juristicName)
    {
        this.juristicName = juristicName;
    }

    public void setJuristicTypeCode(String juristicTypeCode)
    {
        this.juristicTypeCode = juristicTypeCode;
    }

    public void setLastClassId(String lastClassId)
    {
        this.lastClassId = lastClassId;
    }

    public void setLatencyFeeSum(String latencyFeeSum)
    {
        this.latencyFeeSum = latencyFeeSum;
    }

    public void setLikeDiscntMode(String likeDiscntMode)
    {
        this.likeDiscntMode = likeDiscntMode;
    }

    public void setLikeMobileTrade(String likeMobileTrade)
    {
        this.likeMobileTrade = likeMobileTrade;
    }

    public void setLinkmanDutyId(String linkmanDutyId)
    {
        this.linkmanDutyId = linkmanDutyId;
    }

    public void setLinkmanEmailAddr(String linkmanEmailAddr)
    {
        this.linkmanEmailAddr = linkmanEmailAddr;
    }

    public void setLinkmanHomeAddr(String linkmanHomeAddr)
    {
        this.linkmanHomeAddr = linkmanHomeAddr;
    }

    public void setLinkmanHomePhone(String linkmanHomePhone)
    {
        this.linkmanHomePhone = linkmanHomePhone;
    }

    public void setLinkmanHomePost(String linkmanHomePost)
    {
        this.linkmanHomePost = linkmanHomePost;
    }

    public void setLtelBudget(String ltelBudget)
    {
        this.ltelBudget = ltelBudget;
    }

    public void setMainBusi(String mainBusi)
    {
        this.mainBusi = mainBusi;
    }

    public void setMainTrade(String mainTrade)
    {
        this.mainTrade = mainTrade;
    }

    public void setMobileNumChinago(String mobileNumChinago)
    {
        this.mobileNumChinago = mobileNumChinago;
    }

    public void setMobileNumGlobal(String mobileNumGlobal)
    {
        this.mobileNumGlobal = mobileNumGlobal;
    }

    public void setMobileNumLocal(String mobileNumLocal)
    {
        this.mobileNumLocal = mobileNumLocal;
    }

    public void setMobileNumMzone(String mobileNumMzone)
    {
        this.mobileNumMzone = mobileNumMzone;
    }

    public void setMobilePayout(String mobilePayout)
    {
        this.mobilePayout = mobilePayout;
    }

    public void setMpGroupCustCode(String mpGroupCustCode)
    {
        this.mpGroupCustCode = mpGroupCustCode;
    }

    public void setNetrentPayout(String netrentPayout)
    {
        this.netrentPayout = netrentPayout;
    }

    public void setNewtradeComment(String newtradeComment)
    {
        this.newtradeComment = newtradeComment;
    }

    public void setOrgStructCode(String orgStructCode)
    {
        this.orgStructCode = orgStructCode;
    }

    public void setOrgTypeA(String orgTypeA)
    {
        this.orgTypeA = orgTypeA;
    }

    public void setOrgTypeB(String orgTypeB)
    {
        this.orgTypeB = orgTypeB;
    }

    public void setOrgTypeC(String orgTypeC)
    {
        this.orgTypeC = orgTypeC;
    }

    public void setOutDate(String outDate)
    {
        this.outDate = outDate;
    }

    public void setPayforWayCode(String payforWayCode)
    {
        this.payforWayCode = payforWayCode;
    }

    public void setPhotoTag(String photoTag)
    {
        this.photoTag = photoTag;
    }

    public void setPnationalGroupId(String pnationalGroupId)
    {
        this.pnationalGroupId = pnationalGroupId;
    }

    public void setPnationalGroupName(String pnationalGroupName)
    {
        this.pnationalGroupName = pnationalGroupName;
    }

    public void setPoloticalVillageId(String poloticalVillageId)
    {
        this.poloticalVillageId = poloticalVillageId;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setProductNumLocal(String productNumLocal)
    {
        this.productNumLocal = productNumLocal;
    }

    public void setProductNumOther(String productNumOther)
    {
        this.productNumOther = productNumOther;
    }

    public void setProductNumUse(String productNumUse)
    {
        this.productNumUse = productNumUse;
    }

    public void setProductTypeId(String productTypeId)
    {
        this.productTypeId = productTypeId;
    }

    public void setProvinceCode(String provinceCode)
    {
        this.provinceCode = provinceCode;
    }

    public void setRegDate(String regDate)
    {
        this.regDate = regDate;
    }

    public void setRegMoney(String regMoney)
    {
        this.regMoney = regMoney;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRemoveChange(String removeChange)
    {
        this.removeChange = removeChange;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
    }

    public void setRemoveFlag(String removeFlag)
    {
        this.removeFlag = removeFlag;
    }

    public void setRemoveMethod(String removeMethod)
    {
        this.removeMethod = removeMethod;
    }

    public void setRemoveReasonCode(String removeReasonCode)
    {
        this.removeReasonCode = removeReasonCode;
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

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public void setServLevel(String servLevel)
    {
        this.servLevel = servLevel;
    }

    public void setSubCallingTypeCode(String subCallingTypeCode)
    {
        this.subCallingTypeCode = subCallingTypeCode;
    }

    public void setSubclassId(String subclassId)
    {
        this.subclassId = subclassId;
    }

    public void setSuperGroupId(String superGroupId)
    {
        this.superGroupId = superGroupId;
    }

    public void setSuperGroupName(String superGroupName)
    {
        this.superGroupName = superGroupName;
    }

    public void setTelecomNumGh(String telecomNumGh)
    {
        this.telecomNumGh = telecomNumGh;
    }

    public void setTelecomNumXlt(String telecomNumXlt)
    {
        this.telecomNumXlt = telecomNumXlt;
    }

    public void setTelecomPayoutXlt(String telecomPayoutXlt)
    {
        this.telecomPayoutXlt = telecomPayoutXlt;
    }

    public void setTownId(String townId)
    {
        this.townId = townId;
    }

    public void setTurnover(String turnover)
    {
        this.turnover = turnover;
    }

    public void setUnicomNumC(String unicomNumC)
    {
        this.unicomNumC = unicomNumC;
    }

    public void setUnicomNumG(String unicomNumG)
    {
        this.unicomNumG = unicomNumG;
    }

    public void setUnicomNumGc(String unicomNumGc)
    {
        this.unicomNumGc = unicomNumGc;
    }

    public void setUnicomPayout(String unicomPayout)
    {
        this.unicomPayout = unicomPayout;
    }

    public void setUnifyPayCode(String unifyPayCode)
    {
        this.unifyPayCode = unifyPayCode;
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

    public void setUserNum(String userNum)
    {
        this.userNum = userNum;
    }

    public void setUserNumFullfree(String userNumFullfree)
    {
        this.userNumFullfree = userNumFullfree;
    }

    public void setUserNumWriteoff(String userNumWriteoff)
    {
        this.userNumWriteoff = userNumWriteoff;
    }

    public void setVpmnGroupId(String vpmnGroupId)
    {
        this.vpmnGroupId = vpmnGroupId;
    }

    public void setVpmnNum(String vpmnNum)
    {
        this.vpmnNum = vpmnNum;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public void setWritefeeCount(String writefeeCount)
    {
        this.writefeeCount = writefeeCount;
    }

    public void setWritefeeSum(String writefeeSum)
    {
        this.writefeeSum = writefeeSum;
    }

    public void setYearGain(String yearGain)
    {
        this.yearGain = yearGain;
    }

    @Override
    public IData toData()
    {
        return datamap;
    }

    private void toData_()
    {
        datamap = new DataMap();

        datamap.put("BASE_ACCESS_NO_KIND", this.baseAccessNoKind);
        datamap.put("BASE_ACCESS_NO", this.baseAccessNo);
        datamap.put("GROUP_MGR_CUST_NAME", this.groupMgrCustName);
        datamap.put("GROUP_MGR_USER_ID", this.groupMgrUserId);
        datamap.put("GROUP_MGR_SN", this.groupMgrSn);
        datamap.put("GROUP_SUM_SCORE", this.groupSumScore);
        datamap.put("GROUP_VALID_SCORE", this.groupValidScore);
        datamap.put("POST_CODE", this.postCode);
        datamap.put("EMAIL", this.email);
        datamap.put("FAX_NBR", this.faxNbr);
        datamap.put("WEBSITE", this.website);
        datamap.put("SUBCLASS_ID", this.subclassId);
        datamap.put("CALLING_POLICY_FORCE", this.callingPolicyForce);
        datamap.put("EARNING_ORDER", this.earningOrder);
        datamap.put("FINANCE_EARNING", this.financeEarning);
        datamap.put("LIKE_DISCNT_MODE", this.likeDiscntMode);
        datamap.put("LIKE_MOBILE_TRADE", this.likeMobileTrade);
        datamap.put("NEWTRADE_COMMENT", this.newtradeComment);
        datamap.put("DOYEN_STAFF_ID", this.doyenStaffId);
        datamap.put("BOSS_FEE_SUM", this.bossFeeSum);
        datamap.put("USER_NUM_WRITEOFF", this.userNumWriteoff);
        datamap.put("USER_NUM_FULLFREE", this.userNumFullfree);
        datamap.put("WRITEFEE_SUM", this.writefeeSum);
        datamap.put("WRITEFEE_COUNT", this.writefeeCount);
        datamap.put("PAYFOR_WAY_CODE", this.payforWayCode);
        datamap.put("GROUP_PAY_MODE", this.groupPayMode);
        datamap.put("TELECOM_PAYOUT_XLT", this.telecomPayoutXlt);
        datamap.put("UNICOM_PAYOUT", this.unicomPayout);
        datamap.put("MOBILE_PAYOUT", this.mobilePayout);
        datamap.put("NETRENT_PAYOUT", this.netrentPayout);
        datamap.put("EMPLOYEE_ARPU", this.employeeArpu);
        datamap.put("PRODUCT_NUM_USE", this.productNumUse);
        datamap.put("PRODUCT_NUM_OTHER", this.productNumOther);
        datamap.put("PRODUCT_NUM_LOCAL", this.productNumLocal);
        datamap.put("UNICOM_NUM_GC", this.unicomNumGc);
        datamap.put("UNICOM_NUM_C", this.unicomNumC);
        datamap.put("UNICOM_NUM_G", this.unicomNumG);
        datamap.put("MOBILE_NUM_LOCAL", this.mobileNumLocal);
        datamap.put("MOBILE_NUM_MZONE", this.mobileNumMzone);
        datamap.put("MOBILE_NUM_GLOBAL", this.mobileNumGlobal);
        datamap.put("MOBILE_NUM_CHINAGO", this.mobileNumChinago);
        datamap.put("TELECOM_NUM_XLT", this.telecomNumXlt);
        datamap.put("TELECOM_NUM_GH", this.telecomNumGh);
        datamap.put("EMP_NUM_ALL", this.empNumAll);
        datamap.put("EMP_NUM_CHINA", this.empNumChina);
        datamap.put("EMP_NUM_LOCAL", this.empNumLocal);
        datamap.put("USER_NUM", this.userNum);
        datamap.put("VPMN_NUM", this.vpmnNum);
        datamap.put("VPMN_GROUP_ID", this.vpmnGroupId);
        datamap.put("GROUP_ADVERSARY", this.groupAdversary);
        datamap.put("LTEL_BUDGET", this.ltelBudget);
        datamap.put("GTEL_BUDGET", this.gtelBudget);
        datamap.put("COMM_BUDGET", this.commBudget);
        datamap.put("CONSUME", this.consume);
        datamap.put("TURNOVER", this.turnover);
        datamap.put("YEAR_GAIN", this.yearGain);
        datamap.put("LATENCY_FEE_SUM", this.latencyFeeSum);
        datamap.put("EMP_LSAVE", this.empLsave);
        datamap.put("MAIN_TRADE", this.mainTrade);
        datamap.put("MAIN_BUSI", this.mainBusi);
        datamap.put("SCOPE", this.scope);
        datamap.put("CUST_AIM", this.custAim);
        datamap.put("REG_DATE", this.regDate);
        datamap.put("REG_MONEY", this.regMoney);
        datamap.put("BANK_NAME", this.bankName);
        datamap.put("BANK_ACCT", this.bankAcct);
        datamap.put("GROUP_MEMO", this.groupMemo);
        datamap.put("BUSI_LICENCE_VALID_DATE", this.busiLicenceValidDate);
        datamap.put("BUSI_LICENCE_NO", this.busiLicenceNo);
        datamap.put("BUSI_LICENCE_TYPE", this.busiLicenceType);
        datamap.put("JURISTIC_NAME", this.juristicName);
        datamap.put("JURISTIC_CUST_ID", this.juristicCustId);
        datamap.put("JURISTIC_TYPE_CODE", this.juristicTypeCode);
        datamap.put("ENTERPRISE_SCOPE", this.enterpriseScope);
        datamap.put("ENTERPRISE_SIZE_CODE", this.enterpriseSizeCode);
        datamap.put("ENTERPRISE_TYPE_CODE", this.enterpriseTypeCode);
        datamap.put("GROUP_CONTACT_PHONE", this.groupContactPhone);
        datamap.put("BUSI_TYPE", this.busiType);
        datamap.put("AGREEMENT", this.agreement);
        datamap.put("ACCEPT_CHANNEL", this.acceptChannel);
        datamap.put("CALL_TYPE", this.callType);
        datamap.put("CALLING_AREA_CODE", this.callingAreaCode);
        datamap.put("SUB_CALLING_TYPE_CODE", this.subCallingTypeCode);
        datamap.put("CALLING_TYPE_CODE", this.callingTypeCode);
        datamap.put("ORG_TYPE_C", this.orgTypeC);
        datamap.put("ORG_TYPE_B", this.orgTypeB);
        datamap.put("ORG_TYPE_A", this.orgTypeA);
        datamap.put("ASSIGN_STAFF_ID", this.assignStaffId);
        datamap.put("ASSIGN_DATE", this.assignDate);
        datamap.put("CUST_MANAGER_APPR", this.custManagerAppr);
        datamap.put("CUST_MANAGER_ID", this.custManagerId);
        datamap.put("ORG_STRUCT_CODE", this.orgStructCode);
        datamap.put("UNIFY_PAY_CODE", this.unifyPayCode);
        datamap.put("MP_GROUP_CUST_CODE", this.mpGroupCustCode);
        datamap.put("PNATIONAL_GROUP_NAME", this.pnationalGroupName);
        datamap.put("PNATIONAL_GROUP_ID", this.pnationalGroupId);
        datamap.put("SUPER_GROUP_NAME", this.superGroupName);
        datamap.put("SUPER_GROUP_ID", this.superGroupId);
        datamap.put("CITY_CODE_U", this.cityCodeU);
        datamap.put("CITY_CODE", this.cityCode);
        datamap.put("EPARCHY_CODE", this.eparchyCode);
        datamap.put("PROVINCE_CODE", this.provinceCode);
        datamap.put("GROUP_SOURCE", this.groupSource);
        datamap.put("GROUP_ADDR", this.groupAddr);
        datamap.put("GROUP_STATUS", this.groupStatus);
        datamap.put("GROUP_ATTR", this.groupAttr);
        datamap.put("CUST_CLASS_TYPE", this.custClassType);
        datamap.put("CLASS_CHANGE_DATE", this.classChangeDate);
        datamap.put("LAST_CLASS_ID", this.lastClassId);
        datamap.put("CLASS_ID2", this.classId2);
        datamap.put("CLASS_ID", this.classId);
        datamap.put("GROUP_ROLE", this.groupRole);
        datamap.put("GROUP_TYPE", this.groupType);
        datamap.put("CUST_NAME", this.custName);
        datamap.put("GROUP_ID", this.groupId);
        datamap.put("CUST_ID", this.custId);
        datamap.put("LINKMAN_DUTY_ID", this.linkmanDutyId);
        datamap.put("LINKMAN_HOME_PHONE", this.linkmanHomePhone);
        datamap.put("LINKMAN_EMAIL_ADDR", this.linkmanEmailAddr);
        datamap.put("LINKMAN_HOME_ADDR", this.linkmanHomeAddr);
        datamap.put("LINKMAN_HOME_POST", this.linkmanHomePost);
        datamap.put("PRODUCT_TYPE_ID", this.productTypeId);
        datamap.put("IS_PRODUCT_GROUP", this.isProductGroup);
        datamap.put("PHOTO_TAG", this.photoTag);
        datamap.put("SERV_LEVEL", this.servLevel);
        datamap.put("CHIP_AREA_CODE", this.chipAreaCode);
        datamap.put("TOWN_ID", this.townId);
        datamap.put("POLOTICAL_VILLAGE_ID", this.poloticalVillageId);
        datamap.put("RSRV_TAG3", this.rsrvTag3);
        datamap.put("RSRV_TAG2", this.rsrvTag2);
        datamap.put("RSRV_TAG1", this.rsrvTag1);
        datamap.put("RSRV_DATE3", this.rsrvDate3);
        datamap.put("RSRV_DATE2", this.rsrvDate2);
        datamap.put("RSRV_DATE1", this.rsrvDate1);
        datamap.put("RSRV_STR8", this.rsrvStr8);
        datamap.put("RSRV_STR7", this.rsrvStr7);
        datamap.put("RSRV_STR6", this.rsrvStr6);
        datamap.put("RSRV_STR5", this.rsrvStr5);
        datamap.put("RSRV_STR4", this.rsrvStr4);
        datamap.put("RSRV_STR3", this.rsrvStr3);
        datamap.put("RSRV_STR2", this.rsrvStr2);
        datamap.put("RSRV_STR1", this.rsrvStr1);
        datamap.put("RSRV_NUM3", this.rsrvNum3);
        datamap.put("RSRV_NUM2", this.rsrvNum2);
        datamap.put("RSRV_NUM1", this.rsrvNum1);
        datamap.put("REMARK", this.remark);
        datamap.put("UPDATE_DEPART_ID", this.updateDepartId);
        datamap.put("UPDATE_STAFF_ID", this.updateStaffId);
        datamap.put("UPDATE_TIME", this.updateTime);
        datamap.put("REMOVE_STAFF_ID", this.removeStaffId);
        datamap.put("REMOVE_DATE", this.removeDate);
        datamap.put("REMOVE_CHANGE", this.removeChange);
        datamap.put("REMOVE_REASON_CODE", this.removeReasonCode);
        datamap.put("REMOVE_METHOD", this.removeMethod);
        datamap.put("REMOVE_FLAG", this.removeFlag);
        datamap.put("REMOVE_TAG", this.removeTag);
        datamap.put("OUT_DATE", this.outDate);
        datamap.put("IN_DEPART_ID", this.inDepartId);
        datamap.put("IN_STAFF_ID", this.inStaffId);
        datamap.put("IN_DATE", this.inDate);
        datamap.put("AUDIT_NOTE", this.auditNote);
        datamap.put("AUDIT_STAFF_ID", this.auditStaffId);
        datamap.put("AUDIT_DATE", this.auditDate);
        datamap.put("AUDIT_STATE", this.auditState);
        datamap.put("IF_SHORT_PIN", this.ifShortPin);
        datamap.put("EC_CODE", this.ecCode);
        datamap.put("CUST_SERV_NBR", this.custServNbr);
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
