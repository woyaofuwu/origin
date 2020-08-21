
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class CreatePushemailGroupMemberReqData extends CreateGroupMemberReqData
{

    private String bizpwd;// 业务密码;

    public String getBizpwd()
    {
        return bizpwd;
    }

    public void setBizpwd(String bizpwd)
    {
        this.bizpwd = bizpwd;
    }
}
