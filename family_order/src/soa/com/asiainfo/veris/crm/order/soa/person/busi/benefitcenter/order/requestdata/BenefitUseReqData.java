package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/10 9:55
 */
public class BenefitUseReqData extends BaseReqData {
    /**
     * 权益关联的标识
     * 场景1、停车场，可以是车牌号
     * 场景2、中石油加油站，加油卡的编码或会员号。
     * 作用是该字段与BOSS系统的手机用户建立一一对于关系
     */
    private String relId;
    /**
     * 权益实例编码
     * 由BOSS提供的套餐编码
     */
    private String discntCode;
    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 权益类型ID
     * 例如：
     * 1：机场权益 2：加油站权益
     * 3：酒店权益 4：电影院权益
     * 5：购物中心权益
     */
    private String rightId;

    private String modifyTag;

    private IData rightUseInfo;


    public IData getRightUseInfo() {
        return rightUseInfo;
    }

    public void setRightUseInfo(IData rightUseInfo) {
        this.rightUseInfo = rightUseInfo;
    }

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
}
