package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther : lixx9
 * @createDate :  2020/7/20
 * @describe :
 */
public class GroupShareRelationReqData  extends BaseReqData {

    private String familySerialNumber; //家庭用户号码

    private String memberRoleCode; //成员角色

    private String memberRelInstId;//成员实例ID

    private String tag;//0--新增   1--删除

    public String getFamilySerialNumber() {
        return familySerialNumber;
    }

    public void setFamilySerialNumber(String familySerialNumber) {
        this.familySerialNumber = familySerialNumber;
    }

    public String getMemberRoleCode() {
        return memberRoleCode;
    }

    public void setMemberRoleCode(String memberRoleCode) {
        this.memberRoleCode = memberRoleCode;
    }

    public String getMemberRelInstId() {
        return memberRelInstId;
    }

    public void setMemberRelInstId(String memberRelInstId) {
        this.memberRelInstId = memberRelInstId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
