package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.requestdata;

/**
 * @auther : lixx9
 * @createDate :  2020/7/20
 * @describe :
 */
public class FamilyMemberDatas {

    private String memberSerialNumber; //成员号码

    private String modifyTag;// 操作标识

    private String memberRoleCode; //成员角色类型 familyConstants.ROLE

    public String getMemberSerialNumber() {
        return memberSerialNumber;
    }

    public void setMemberSerialNumber(String memberSerialNumber) {
        this.memberSerialNumber = memberSerialNumber;
    }

    public String getModifyTag() {
        return modifyTag;
    }

    public void setModifyTag(String modifyTag) {
        this.modifyTag = modifyTag;
    }

    public String getMemberRoleCode() {
        return memberRoleCode;
    }

    public void setMemberRoleCode(String memberRoleCode) {
        this.memberRoleCode = memberRoleCode;
    }
}
