
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class CreateVpnGroupMemberReqData extends CreateGroupMemberReqData
{

    private String IF_SMS; // 是否发短信

    public String getIF_SMS()
    {
        return IF_SMS;
    }

    public void setIF_SMS(String iF_SMS)
    {
        IF_SMS = iF_SMS;
    }

}
