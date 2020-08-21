package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author think
 */
public class DestroyUserNowRequestData extends BaseReqData {

    private String remove_reason_code = "";// 销户原因

    private String remove_reason = "";

    private String score = "";

    private boolean isYHFHUser = false;// 是否影号副号

    private boolean isNetNpUser = false;// 是否携转号码

    private String serialNumberA = ""; // 宽带对应的手机号码

    private String modemFee = "";// 光猫押金金额

    private String modermReturn = "0"; // 是否退光猫 0 不退 1退

    private String isWideType = "1";// 宽带类型，默认GPON宽带

    private String modemMode = "0";// 光猫申领模式，默认 租赁, 申领模式 0租赁，1购买，2赠送，3自备

    private String modemfeestate = "0";// 光猫押金状态，默认正常, 押金状态 0,押金、1,已转移、2已退还、3,已沉淀

    /**
     * REQ201609280002 宽带功能优化 chenxy3 2016-11-29
     * */
    private String destoryReason = "";// 销户原因

    private String reasonElse = "";// 销户原因-其他

    private String IMSTag; // IMS固话标识

    private String IMSSerialNumber;

    private String IMSBrand;

    private String IMSProduct;

    // 中小企业受理传参

    private String ecUserId;

    private String ecSerialNumber;

    private String ibsysId;

    private String nodeId;

    private String recordNum;

    private String busiformId;

    public String getEcUserId() {
        return ecUserId;
    }

    public void setEcUserId(String ecUserId) {
        this.ecUserId = ecUserId;
    }

    public String getEcSerialNumber() {
        return ecSerialNumber;
    }

    public void setEcSerialNumber(String ecSerialNumber) {
        this.ecSerialNumber = ecSerialNumber;
    }

    public String getBusiformId() {
        return busiformId;
    }

    public void setBusiformId(String busiformId) {
        this.busiformId = busiformId;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public String getIbsysId() {
        return ibsysId;
    }

    public void setIbsysId(String ibsysId) {
        this.ibsysId = ibsysId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getDestoryReason() {
        return destoryReason;
    }

    public void setDestoryReason(String destoryReason) {
        this.destoryReason = destoryReason;
    }

    public String getReasonElse() {
        return reasonElse;
    }

    public void setReasonElse(String reasonElse) {
        this.reasonElse = reasonElse;
    }

    public String getModemFeeState() {
        return modemfeestate;
    }

    public void setModemFeeState(String feestate) {
        this.modemfeestate = feestate;
    }

    public String getModemMode() {
        return modemMode;
    }

    public void setModemMode(String modemmode) {
        this.modemMode = modemmode;
    }

    public String getWideType() {
        return isWideType;
    }

    public void setWideType(String widetype) {
        this.isWideType = widetype;
    }

    public String getModermReturn() {
        return modermReturn;
    }

    public void setModermReturn(String modermReturn) {
        this.modermReturn = modermReturn;
    }

    public String getSerialNumberA() {
        return serialNumberA;
    }

    public void setSerialNumberA(String serialNumberA) {
        this.serialNumberA = serialNumberA;
    }

    public String getModemFee() {
        return modemFee;
    }

    public void setModemFee(String modemFee) {
        this.modemFee = modemFee;
    }

    public String getRemove_reason() {
        return remove_reason;
    }

    public String getRemove_reason_code() {
        return remove_reason_code;
    }

    public String getScore() {
        return score;
    }

    public boolean isNetNpUser() {
        return isNetNpUser;
    }

    public boolean isYHFHUser() {
        return isYHFHUser;
    }

    public void setNetNpUser(boolean isNetNpUser) {
        this.isNetNpUser = isNetNpUser;
    }

    public void setRemove_reason(String remove_reason) {
        this.remove_reason = remove_reason;
    }

    public void setRemove_reason_code(String remove_reason_code) {
        this.remove_reason_code = remove_reason_code;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setYHFH(boolean isYHFHUser) {
        this.isYHFHUser = isYHFHUser;
    }

    public String getIMSTag() {
        return IMSTag;
    }

    public void setIMSTag(String iMSTag) {
        IMSTag = iMSTag;
    }

    public String getIMSSerialNumber() {
        return IMSSerialNumber;
    }

    public void setIMSSerialNumber(String iMSSerialNumber) {
        IMSSerialNumber = iMSSerialNumber;
    }

    public String getIMSBrand() {
        return IMSBrand;
    }

    public void setIMSBrand(String iMSBrand) {
        IMSBrand = iMSBrand;
    }

    public String getIMSProduct() {
        return IMSProduct;
    }

    public void setIMSProduct(String iMSProduct) {
        IMSProduct = iMSProduct;
    }
}
