
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class CloseGrpAddBeanReqData extends MemberReqData
{
    private String CLOSE_MAX_LIMIT; // 闭合群最大用户数

    private String CLOSE_GRP_NAME; // 闭合群名称

    private String VPN_NO; // vpn_no

    private String DISCNT_CODE; // 优惠编码

    public String getCLOSE_GRP_NAME()
    {
        return CLOSE_GRP_NAME;
    }

    public String getCLOSE_MAX_LIMIT()
    {
        return CLOSE_MAX_LIMIT;
    }

    public String getDISCNT_CODE()
    {
        return DISCNT_CODE;
    }

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public void setCLOSE_GRP_NAME(String close_grp_name)
    {
        CLOSE_GRP_NAME = close_grp_name;
    }

    public void setCLOSE_MAX_LIMIT(String close_max_limit)
    {
        CLOSE_MAX_LIMIT = close_max_limit;
    }

    public void setDISCNT_CODE(String discnt_code)
    {
        DISCNT_CODE = discnt_code;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }

}
