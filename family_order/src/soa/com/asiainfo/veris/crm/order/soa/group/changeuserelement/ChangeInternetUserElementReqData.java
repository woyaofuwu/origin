package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;

public class ChangeInternetUserElementReqData extends ChangeUserElementReqData {
    private IData interData;

    private IData zjData;

    private IDataset attrData;

    private IDataset commonData;

    private IData dataline;

    private IDataset elementInfo;

    private String cancelTag;

    private String insertTime;

    private String changegrpTag;

    public String getChangegrpTag() {
        return changegrpTag;
    }

    public void setChangegrpTag(String changegrpTag) {
        this.changegrpTag = changegrpTag;
    }

    public String getCancelTag() {
        return cancelTag;
    }

    public void setCancelTag(String cancelTag) {
        this.cancelTag = cancelTag;
    }

    public IDataset getCommonData() {
        return commonData;
    }

    public IDataset getElementInfo() {
        return elementInfo;
    }

    public void setElementInfo(IDataset elementInfo) {
        this.elementInfo = elementInfo;
    }

    public void setCommonData(IDataset commonData) {
        this.commonData = commonData;
    }

    public IData getDataline() {
        return dataline;
    }

    public void setDataline(IData dataline) {
        this.dataline = dataline;
    }

    public IDataset getAttrData() {
        return attrData;
    }

    public void setAttrData(IDataset attrData) {
        this.attrData = attrData;
    }

    public IData getInterData() {
        return interData;
    }

    public void setInterData(IData interData) {
        this.interData = interData;
    }

    public IData getZjData() {
        return zjData;
    }

    public void setZjData(IData zjData) {
        this.zjData = zjData;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }
}
