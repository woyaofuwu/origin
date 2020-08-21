
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class CloseGrpDelBeanReqData extends MemberReqData
{
    private String VPN_NO; // vpmn编码

    private String CLOSE_MAX_LIMIT; // 闭合群最大用户数

    public String getCLOSE_MAX_LIMIT()
    {
        return CLOSE_MAX_LIMIT;
    }

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public void setCLOSE_MAX_LIMIT(String close_max_limit)
    {
        CLOSE_MAX_LIMIT = close_max_limit;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }
}
