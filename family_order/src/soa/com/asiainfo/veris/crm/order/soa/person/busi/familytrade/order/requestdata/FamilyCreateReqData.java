
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;

import java.util.ArrayList;
import java.util.List;

public class FamilyCreateReqData extends BaseReqData {

    private List<FamilyMemberData> memberDataList = new ArrayList<FamilyMemberData>();

    private DiscntData discntData;// 主卡优惠

    private DiscntData appDiscntData;// 主卡可选优惠

    private String homeAddress;// 家庭地址

    private String homePhone;// 家庭电话

    private String homeName;// 家庭昵称

    private String shortCode;// 主卡短号

    private String verifyMode;// 副卡校验方式

    private String inTagNew;// 界面互联网改造，0表示界面互联网前台办理

    public void addMemberData(FamilyMemberData familyMemberData) {
        this.memberDataList.add(familyMemberData);
    }

    public DiscntData getAppDiscntData() {
        return appDiscntData;
    }

    public DiscntData getDiscntData() {
        return discntData;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getHomeName() {
        return homeName;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public List<FamilyMemberData> getMemberDataList() {
        return memberDataList;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getVerifyMode() {
        return verifyMode;
    }

    public String getInTagNew() {
        return inTagNew;
    }

    public void setAppDiscntData(DiscntData appDiscntData) {
        this.appDiscntData = appDiscntData;
    }

    public void setDiscntData(DiscntData discntData) {
        this.discntData = discntData;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public void setMemberDataList(List<FamilyMemberData> memberDataList) {
        this.memberDataList = memberDataList;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setVerifyMode(String verifyMode) {
        this.verifyMode = verifyMode;
    }

    public void setInTagNew(String inTagNew) {
        this.inTagNew = inTagNew;
    }
}
