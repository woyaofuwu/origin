
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class FamilyMemberData {

    private UcaData uca;// 成员三户资料

    private String modifyTag;// 操作标识

    private String instId;// 实例标识

    private DiscntData discntData;// 副卡优惠

    private DiscntData appDiscntData;// 副卡可选优惠

    private String shortCode;// 副卡短号

    private String nickName;// 昵称

    private String memberRole;// 角色

    private String memberKind;// 类型

    private String effectNow;// 是否立即生效

    private String mebVerifyMode;// 界面互联网改造，副卡校验方式： 0-密码；1-短信校验；2-免密码

    public DiscntData getAppDiscntData() {
        return appDiscntData;
    }

    public DiscntData getDiscntData() {
        return discntData;
    }

    public String getEffectNow() {
        return effectNow;
    }

    public String getInstId() {
        return instId;
    }

    public String getMemberKind() {
        return memberKind;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public String getModifyTag() {
        return modifyTag;
    }

    public String getNickName() {
        return nickName;
    }

    public String getShortCode() {
        return shortCode;
    }

    public UcaData getUca() {
        return uca;
    }

    public String getMebVerifyMode() {
        return mebVerifyMode;
    }

    public void setAppDiscntData(DiscntData appDiscntData) {
        this.appDiscntData = appDiscntData;
    }

    public void setDiscntData(DiscntData discntData) {
        this.discntData = discntData;
    }

    public void setEffectNow(String effectNow) {
        this.effectNow = effectNow;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public void setMemberKind(String memberKind) {
        this.memberKind = memberKind;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public void setModifyTag(String modifyTag) {
        this.modifyTag = modifyTag;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setUca(UcaData uca) {
        this.uca = uca;
    }

    public void setMebVerifyMode(String mebVerifyMode) {
        this.mebVerifyMode = mebVerifyMode;
    }
}
