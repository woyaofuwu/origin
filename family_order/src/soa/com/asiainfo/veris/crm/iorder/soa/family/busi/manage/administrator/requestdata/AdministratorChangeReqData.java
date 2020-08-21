package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.administrator.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @auther : lixx9
 * @createDate :  2020/8/3
 * @describe :
 */
public class AdministratorChangeReqData extends BaseReqData {

    private String memberSerialNumber; //成员号码

    private String familyUserId; //家庭用户编码

    public String getMemberSerialNumber() {
        return memberSerialNumber;
    }

    public void setMemberSerialNumber(String memberSerialNumber) {
        this.memberSerialNumber = memberSerialNumber;
    }

    public String getFamilyUserId() {
        return familyUserId;
    }

    public void setFamilyUserId(String familyUserId) {
        this.familyUserId = familyUserId;
    }
}
