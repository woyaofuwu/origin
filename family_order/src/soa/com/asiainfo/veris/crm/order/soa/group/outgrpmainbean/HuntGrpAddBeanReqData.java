
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class HuntGrpAddBeanReqData extends MemberReqData
{
    private String TEAM_TYPE; // 寻呼组类型 0：优先组 ；1：轮选组

    private String VPN_NO; // 集团vpn_no

    public String getTEAM_TYPE()
    {
        return TEAM_TYPE;
    }

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public void setTEAM_TYPE(String team_type)
    {
        TEAM_TYPE = team_type;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }
}
