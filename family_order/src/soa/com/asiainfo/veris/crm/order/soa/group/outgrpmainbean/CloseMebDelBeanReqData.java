
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class CloseMebDelBeanReqData extends MemberReqData
{
    private String USER_ID_A; // 闭合群user_id

    private String SERIAL_NUMBER_A; // 闭合群sn

    private String DISCNT_CODE; // 优惠编码

    private String VPN_NO; // vpmn编码

    public String getDISCNT_CODE()
    {
        return DISCNT_CODE;
    }

    public String getSERIAL_NUMBER_A()
    {
        return SERIAL_NUMBER_A;
    }

    public String getUSER_ID_A()
    {
        return USER_ID_A;
    }

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public void setDISCNT_CODE(String discnt_code)
    {
        DISCNT_CODE = discnt_code;
    }

    public void setSERIAL_NUMBER_A(String serial_number_a)
    {
        SERIAL_NUMBER_A = serial_number_a;
    }

    public void setUSER_ID_A(String user_id_a)
    {
        USER_ID_A = user_id_a;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }
}
