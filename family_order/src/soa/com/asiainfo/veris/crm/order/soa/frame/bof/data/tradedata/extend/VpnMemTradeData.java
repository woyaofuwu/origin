
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class VpnMemTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String userIda;

    private String linkManTag;

    private String telphonistTag;

    private String mainTag;

    private String adminFlag;

    private String searialNumber;

    private String userPin;

    private String shortCode;

    private String innetCallPwd;

    private String outnetCallPed;

    private String monFeeLimit;

    private String callNetType;

    private String callApeaType;

    private String overFeeTag;

    private String limfeeTypeCode;

    private String sinwordTypeCode;

    private String lockTag;

    private String callDispMode;

    private String perfeePlayBack;

    private String notBatchfeeTag;

    private String pkgStartDate;

    private String pkgType;

    private String sellDepart;

    private String overRightTag;

    private String callRoamType;

    private String bycallRoamType;

    private String outnetgrpUseType;

    private String outgrp;

    private String maxPoutNum;

    private String funcTlags;

    private String memberKind;

    private String isTx;

    private String vpnType;

    private String extFuncTlags;

    private String openDate;

    private String removeTag;

    private String removeDate;

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

    public VpnMemTradeData()
    {

    }

    public VpnMemTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.userId = data.getString("USER_ID");
        this.userIda = data.getString("USER_ID_A");
        this.linkManTag = data.getString("LINK_MAN_TAG");
        this.telphonistTag = data.getString("TELPHONIST_TAG");
        this.mainTag = data.getString("MAIN_TAG");
        this.adminFlag = data.getString("ADMIN_FLAG");
        this.searialNumber = data.getString("SERIAL_NUMBER");
        this.userPin = data.getString("USER_PIN");
        this.shortCode = data.getString("SHORT_CODE");
        this.innetCallPwd = data.getString("INNET_CALL_PWD");
        this.outnetCallPed = data.getString("OUTNET_CALL_PWD");
        this.monFeeLimit = data.getString("MON_FEE_LIMIT");
        this.callNetType = data.getString("CALL_NET_TYPE");
        this.callApeaType = data.getString("CALL_AREA_TYPE");
        this.overFeeTag = data.getString("OVER_FEE_TAG");
        this.limfeeTypeCode = data.getString("LIMFEE_TYPE_CODE");
        this.sinwordTypeCode = data.getString("SINWORD_TYPE_CODE");
        this.lockTag = data.getString("LOCK_TAG");
        this.callDispMode = data.getString("CALL_DISP_MODE");
        this.perfeePlayBack = data.getString("PERFEE_PLAY_BACK");
        this.notBatchfeeTag = data.getString("NOT_BATCHFEE_TAG");
        this.pkgStartDate = data.getString("PKG_START_DATE");
        this.pkgType = data.getString("PKG_TYPE");
        this.sellDepart = data.getString("SELL_DEPART");
        this.overRightTag = data.getString("OVER_RIGHT_TAG");
        this.callRoamType = data.getString("CALL_ROAM_TYPE");
        this.bycallRoamType = data.getString("BYCALL_ROAM_TYPE");
        this.outnetgrpUseType = data.getString("OUTNETGRP_USE_TYPE");
        this.outgrp = data.getString("OUTGRP");
        this.maxPoutNum = data.getString("MAX_POUT_NUM");
        this.funcTlags = data.getString("FUNC_TLAGS");
        this.memberKind = data.getString("MEMBER_KIND");
        this.isTx = data.getString("IS_TX");
        this.vpnType = data.getString("VPN_TYPE");
        this.extFuncTlags = data.getString("EXT_FUNC_TLAGS");
        this.openDate = data.getString("OPEN_DATE");
        this.removeTag = data.getString("REMOVE_TAG");
        this.removeDate = data.getString("REMOVE_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.updateTime = data.getString("UPDATE_TIME");
        this.updateStaffId = data.getString("UPDATE_STAFF_ID");
        this.updateDepartId = data.getString("UPDATE_DEPART_ID");
        this.remark = data.getString("REMARK");
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

    public VpnMemTradeData clone()
    {
        VpnMemTradeData vpnMemTradeData = new VpnMemTradeData();
        vpnMemTradeData.setInstId(this.getInstId());
        vpnMemTradeData.setUserId(this.getUserId());
        vpnMemTradeData.setUserIda(this.getUserIda());
        vpnMemTradeData.setLinkManTag(this.getLinkManTag());
        vpnMemTradeData.setTelphonistTag(this.getTelphonistTag());
        vpnMemTradeData.setMainTag(this.getMainTag());
        vpnMemTradeData.setAdminFlag(this.getAdminFlag());
        vpnMemTradeData.setSearialNumber(this.getSearialNumber());
        vpnMemTradeData.setUserPin(this.getUserPin());
        vpnMemTradeData.setShortCode(this.getShortCode());
        vpnMemTradeData.setInnetCallPwd(this.getInnetCallPwd());
        vpnMemTradeData.setOutnetCallPed(this.getOutnetCallPed());
        vpnMemTradeData.setMonFeeLimit(this.getMonFeeLimit());
        vpnMemTradeData.setCallNetType(this.getCallNetType());
        vpnMemTradeData.setCallApeaType(this.getCallApeaType());
        vpnMemTradeData.setOverFeeTag(this.getOverFeeTag());
        vpnMemTradeData.setLimfeeTypeCode(this.getLimfeeTypeCode());
        vpnMemTradeData.setSinwordTypeCode(this.getSinwordTypeCode());
        vpnMemTradeData.setLockTag(this.getLockTag());
        vpnMemTradeData.setCallDispMode(this.getCallDispMode());
        vpnMemTradeData.setPerfeePlayBack(this.getPerfeePlayBack());
        vpnMemTradeData.setNotBatchfeeTag(this.getNotBatchfeeTag());
        vpnMemTradeData.setPkgStartDate(this.getPkgStartDate());
        vpnMemTradeData.setPkgType(this.getPkgType());
        vpnMemTradeData.setSellDepart(this.getSellDepart());
        vpnMemTradeData.setOverRightTag(this.getOverRightTag());
        vpnMemTradeData.setCallRoamType(this.getCallRoamType());
        vpnMemTradeData.setBycallRoamType(this.getBycallRoamType());
        vpnMemTradeData.setOutnetgrpUseType(this.getOutnetgrpUseType());
        vpnMemTradeData.setOutgrp(this.getOutgrp());
        vpnMemTradeData.setMaxPoutNum(this.getMaxPoutNum());
        vpnMemTradeData.setFuncTlags(this.getFuncTlags());
        vpnMemTradeData.setMemberKind(this.getMemberKind());
        vpnMemTradeData.setIsTx(this.getIsTx());
        vpnMemTradeData.setVpnType(this.getVpnType());
        vpnMemTradeData.setExtFuncTlags(this.getExtFuncTlags());
        vpnMemTradeData.setOpenDate(this.getOpenDate());
        vpnMemTradeData.setRemoveTag(this.getRemoveTag());
        vpnMemTradeData.setRemoveDate(this.getRemoveDate());
        vpnMemTradeData.setModifyTag(this.getModifyTag());
        vpnMemTradeData.setUpdateTime(this.getUpdateTime());
        vpnMemTradeData.setUpdateStaffId(this.getUpdateStaffId());
        vpnMemTradeData.setUpdateDepartId(this.getUpdateDepartId());
        vpnMemTradeData.setRemark(this.getRemark());
        vpnMemTradeData.setRsrvNum1(this.getRsrvNum1());
        vpnMemTradeData.setRsrvNum2(this.getRsrvNum2());
        vpnMemTradeData.setRsrvNum3(this.getRsrvNum3());
        vpnMemTradeData.setRsrvNum4(this.getRsrvNum4());
        vpnMemTradeData.setRsrvNum5(this.getRsrvNum5());
        vpnMemTradeData.setRsrvStr1(this.getRsrvStr1());
        vpnMemTradeData.setRsrvStr2(this.getRsrvStr2());
        vpnMemTradeData.setRsrvStr3(this.getRsrvStr3());
        vpnMemTradeData.setRsrvStr4(this.getRsrvStr4());
        vpnMemTradeData.setRsrvStr5(this.getRsrvStr5());
        vpnMemTradeData.setRsrvDate1(this.getRsrvDate1());
        vpnMemTradeData.setRsrvDate2(this.getRsrvDate2());
        vpnMemTradeData.setRsrvDate3(this.getRsrvDate3());
        vpnMemTradeData.setRsrvTag1(this.getRsrvTag1());
        vpnMemTradeData.setRsrvTag2(this.getRsrvTag2());
        vpnMemTradeData.setRsrvTag3(this.getRsrvTag3());
        return vpnMemTradeData;

    }

    public String getAdminFlag()
    {
        return adminFlag;
    }

    public String getBycallRoamType()
    {
        return bycallRoamType;
    }

    public String getCallApeaType()
    {
        return callApeaType;
    }

    public String getCallDispMode()
    {
        return callDispMode;
    }

    public String getCallNetType()
    {
        return callNetType;
    }

    public String getCallRoamType()
    {
        return callRoamType;
    }

    public String getExtFuncTlags()
    {
        return extFuncTlags;
    }

    public String getFuncTlags()
    {
        return funcTlags;
    }

    public String getInnetCallPwd()
    {
        return innetCallPwd;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getIsTx()
    {
        return isTx;
    }

    public String getLimfeeTypeCode()
    {
        return limfeeTypeCode;
    }

    public String getLinkManTag()
    {
        return linkManTag;
    }

    public String getLockTag()
    {
        return lockTag;
    }

    public String getMainTag()
    {
        return mainTag;
    }

    public String getMaxPoutNum()
    {
        return maxPoutNum;
    }

    public String getMemberKind()
    {
        return memberKind;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getMonFeeLimit()
    {
        return monFeeLimit;
    }

    public String getNotBatchfeeTag()
    {
        return notBatchfeeTag;
    }

    public String getOpenDate()
    {
        return openDate;
    }

    public String getOutgrp()
    {
        return outgrp;
    }

    public String getOutnetCallPed()
    {
        return outnetCallPed;
    }

    public String getOutnetgrpUseType()
    {
        return outnetgrpUseType;
    }

    public String getOverFeeTag()
    {
        return overFeeTag;
    }

    public String getOverRightTag()
    {
        return overRightTag;
    }

    public String getPerfeePlayBack()
    {
        return perfeePlayBack;
    }

    public String getPkgStartDate()
    {
        return pkgStartDate;
    }

    public String getPkgType()
    {
        return pkgType;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveDate()
    {
        return removeDate;
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

    public String getSearialNumber()
    {
        return searialNumber;
    }

    public String getSellDepart()
    {
        return sellDepart;
    }

    public String getShortCode()
    {
        return shortCode;
    }

    public String getSinwordTypeCode()
    {
        return sinwordTypeCode;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_VPN_MEB";
    }

    public String getTelphonistTag()
    {
        return telphonistTag;
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

    public String getUserIda()
    {
        return userIda;
    }

    public String getUserPin()
    {
        return userPin;
    }

    public String getVpnType()
    {
        return vpnType;
    }

    public void setAdminFlag(String adminFlag)
    {
        this.adminFlag = adminFlag;
    }

    public void setBycallRoamType(String bycallRoamType)
    {
        this.bycallRoamType = bycallRoamType;
    }

    public void setCallApeaType(String callApeaType)
    {
        this.callApeaType = callApeaType;
    }

    public void setCallDispMode(String callDispMode)
    {
        this.callDispMode = callDispMode;
    }

    public void setCallNetType(String callNetType)
    {
        this.callNetType = callNetType;
    }

    public void setCallRoamType(String callRoamType)
    {
        this.callRoamType = callRoamType;
    }

    public void setExtFuncTlags(String extFuncTlags)
    {
        this.extFuncTlags = extFuncTlags;
    }

    public void setFuncTlags(String funcTlags)
    {
        this.funcTlags = funcTlags;
    }

    public void setInnetCallPwd(String innetCallPwd)
    {
        this.innetCallPwd = innetCallPwd;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setIsTx(String isTx)
    {
        this.isTx = isTx;
    }

    public void setLimfeeTypeCode(String limfeeTypeCode)
    {
        this.limfeeTypeCode = limfeeTypeCode;
    }

    public void setLinkManTag(String linkManTag)
    {
        this.linkManTag = linkManTag;
    }

    public void setLockTag(String lockTag)
    {
        this.lockTag = lockTag;
    }

    public void setMainTag(String mainTag)
    {
        this.mainTag = mainTag;
    }

    public void setMaxPoutNum(String maxPoutNum)
    {
        this.maxPoutNum = maxPoutNum;
    }

    public void setMemberKind(String memberKind)
    {
        this.memberKind = memberKind;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setMonFeeLimit(String monFeeLimit)
    {
        this.monFeeLimit = monFeeLimit;
    }

    public void setNotBatchfeeTag(String notBatchfeeTag)
    {
        this.notBatchfeeTag = notBatchfeeTag;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public void setOutgrp(String outgrp)
    {
        this.outgrp = outgrp;
    }

    public void setOutnetCallPed(String outnetCallPed)
    {
        this.outnetCallPed = outnetCallPed;
    }

    public void setOutnetgrpUseType(String outnetgrpUseType)
    {
        this.outnetgrpUseType = outnetgrpUseType;
    }

    public void setOverFeeTag(String overFeeTag)
    {
        this.overFeeTag = overFeeTag;
    }

    public void setOverRightTag(String overRightTag)
    {
        this.overRightTag = overRightTag;
    }

    public void setPerfeePlayBack(String perfeePlayBack)
    {
        this.perfeePlayBack = perfeePlayBack;
    }

    public void setPkgStartDate(String pkgStartDate)
    {
        this.pkgStartDate = pkgStartDate;
    }

    public void setPkgType(String pkgType)
    {
        this.pkgType = pkgType;
    }

    public void setRemark(String remark)
    {

        this.remark = remark;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
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

    public void setSearialNumber(String searialNumber)
    {
        this.searialNumber = searialNumber;
    }

    public void setSellDepart(String sellDepart)
    {
        this.sellDepart = sellDepart;
    }

    public void setShortCode(String shortCode)
    {
        this.shortCode = shortCode;
    }

    public void setSinwordTypeCode(String sinwordTypeCode)
    {
        this.sinwordTypeCode = sinwordTypeCode;
    }

    public void setTelphonistTag(String telphonistTag)
    {
        this.telphonistTag = telphonistTag;
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

    public void setUserIda(String userIda)
    {
        this.userIda = userIda;
    }

    public void setUserPin(String userPin)
    {
        this.userPin = userPin;
    }

    public void setVpnType(String vpnType)
    {
        this.vpnType = vpnType;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("USER_ID_A", this.userIda);
        data.put("LINK_MAN_TAG", this.linkManTag);
        data.put("TELPHONIST_TAG", this.telphonistTag);
        data.put("MAIN_TAG", this.mainTag);
        data.put("ADMIN_FLAG", this.adminFlag);
        data.put("SERIAL_NUMBER", this.searialNumber);
        data.put("USER_PIN", this.userPin);
        data.put("SHORT_CODE", this.shortCode);
        data.put("INNET_CALL_PWD", this.innetCallPwd);
        data.put("OUTNET_CALL_PWD", this.outnetCallPed);
        data.put("MON_FEE_LIMIT", this.monFeeLimit);
        data.put("CALL_NET_TYPE", this.callNetType);
        data.put("CALL_AREA_TYPE", this.callApeaType);
        data.put("OVER_FEE_TAG", this.overFeeTag);
        data.put("LIMFEE_TYPE_CODE", this.limfeeTypeCode);
        data.put("SINWORD_TYPE_CODE", this.sinwordTypeCode);
        data.put("LOCK_TAG", this.lockTag);
        data.put("CALL_DISP_MODE", this.callDispMode);
        data.put("PERFEE_PLAY_BACK", this.perfeePlayBack);
        data.put("NOT_BATCHFEE_TAG", this.notBatchfeeTag);
        data.put("PKG_START_DATE", this.pkgStartDate);
        data.put("PKG_TYPE", this.pkgType);
        data.put("SELL_DEPART", this.sellDepart);
        data.put("OVER_RIGHT_TAG", this.overRightTag);
        data.put("CALL_ROAM_TYPE", this.callRoamType);
        data.put("BYCALL_ROAM_TYPE", this.bycallRoamType);
        data.put("OUTNETGRP_USE_TYPE", this.outnetgrpUseType);
        data.put("OUTGRP", this.outgrp);
        data.put("MAX_POUT_NUM", this.maxPoutNum);
        data.put("FUNC_TLAGS", this.funcTlags);
        data.put("MEMBER_KIND", this.memberKind);
        data.put("IS_TX", this.isTx);
        data.put("VPN_TYPE", this.vpnType);
        data.put("EXT_FUNC_TLAGS", this.extFuncTlags);
        data.put("OPEN_DATE", this.openDate);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("REMOVE_DATE", this.removeDate);
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

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
