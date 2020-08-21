package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class WidenetProductRequestData extends BaseChangeProductReqData {
    private String wideActivePayFee;

    private String yearDiscntRemainFee;

    private String returnyearDiscntRemainFee;

    private String remainFee;

    private String acctReainFee;

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

    public String getWideActivePayFee() {
        return wideActivePayFee;
    }

    public void setWideActivePayFee(String wideActivePayFee) {
        this.wideActivePayFee = wideActivePayFee;
    }

    public String getYearDiscntRemainFee() {
        return yearDiscntRemainFee;
    }

    public String getReturnYearDiscntRemainFee() {
        return returnyearDiscntRemainFee;
    }

    public void setYearDiscntRemainFee(String yearDiscntRemainFee) {
        this.yearDiscntRemainFee = yearDiscntRemainFee;
    }

    public void setReturnYearDiscntRemainFee(String returnyearDiscntRemainFee) {
        this.returnyearDiscntRemainFee = returnyearDiscntRemainFee;
    }

    public String getRemainFee() {
        return remainFee;
    }

    public void setRemainFee(String remainFee) {
        this.remainFee = remainFee;
    }

    public String getAcctReainFee() {
        return acctReainFee;
    }

    public void setAcctReainFee(String acctReainFee) {
        this.acctReainFee = acctReainFee;
    }

}
