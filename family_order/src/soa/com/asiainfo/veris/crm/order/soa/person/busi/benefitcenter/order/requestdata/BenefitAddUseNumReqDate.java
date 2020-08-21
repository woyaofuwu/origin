package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/15 20:16
 */
public class BenefitAddUseNumReqDate extends BaseReqData {

    private String relId;

    private String discntCode;

    private String rightId;

    private String modifyTag;

    private String addUseNum;

    private String addUseNumType;

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

    public String getAddUseNum() {
        return addUseNum;
    }

    public void setAddUseNum(String addUseNum) {
        this.addUseNum = addUseNum;
    }

    public String getAddUseNumType() {
        return addUseNumType;
    }

    public void setAddUseNumType(String addUseNumType) {
        this.addUseNumType = addUseNumType;
    }
}
