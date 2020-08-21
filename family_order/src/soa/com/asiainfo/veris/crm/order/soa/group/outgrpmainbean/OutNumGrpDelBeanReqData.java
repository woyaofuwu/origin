
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class OutNumGrpDelBeanReqData extends MemberReqData
{
    private String VPN_NO; // 集团vpn_no

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }
}
