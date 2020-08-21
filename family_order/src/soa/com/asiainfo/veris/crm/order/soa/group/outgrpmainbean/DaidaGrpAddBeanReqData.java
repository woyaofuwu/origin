
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class DaidaGrpAddBeanReqData extends MemberReqData
{
    private String ACCESS_CODE; // 代答码

    private String VPN_NO; // 集团vpn_no

    public String getACCESS_CODE()
    {
        return ACCESS_CODE;
    }

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public void setACCESS_CODE(String access_code)
    {
        ACCESS_CODE = access_code;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }

}
