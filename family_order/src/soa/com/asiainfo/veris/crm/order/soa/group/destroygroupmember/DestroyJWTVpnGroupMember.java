
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyJWTVpnGroupMember extends DestroyGroupMember
{
    public DestroyJWTVpnGroupMember()
    {

    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVpn();
    }

}
