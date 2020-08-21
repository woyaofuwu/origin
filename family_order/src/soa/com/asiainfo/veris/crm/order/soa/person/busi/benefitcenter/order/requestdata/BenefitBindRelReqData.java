package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 10:23
 */
public class BenefitBindRelReqData extends BaseReqData {

    private String relId;

    private String discntCode;

    private String rightId;

    private String modifyTag;

    private String newRelId;

    private String startDate;

    private String endDate;



    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getDiscntCode() {
        return discntCode;
    }

    public void setDiscntCode(String discntCode) {
        this.discntCode = discntCode;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    public String getModifyTag() {
        return modifyTag;
    }

    public void setModifyTag(String modifyTag) {
        this.modifyTag = modifyTag;
    }

    public String getNewRelId() {
        return newRelId;
    }

    public void setNewRelId(String newRelId) {
        this.newRelId = newRelId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
