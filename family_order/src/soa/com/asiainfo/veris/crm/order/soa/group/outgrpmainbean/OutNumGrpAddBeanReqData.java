
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class OutNumGrpAddBeanReqData extends MemberReqData
{
    private String vpn_no; // 集团vpn_no

    public String getVpn_no()
    {
        return vpn_no;
    }

    public void setVpn_no(String vpn_no)
    {
        this.vpn_no = vpn_no;
    }
}
