
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMemberReqData;

public class DestroyVpnGroupMemberReqData extends DestroyGroupMemberReqData
{

    private String JION_IN; // 加入客官

    private String IF_SMS; // 是否短信

    public String getIF_SMS()
    {
        return IF_SMS;
    }

    public String getJION_IN()
    {
        return JION_IN;
    }

    public void setIF_SMS(String iF_SMS)
    {
        IF_SMS = iF_SMS;
    }

    public void setJION_IN(String jION_IN)
    {
        JION_IN = jION_IN;
    }

}
