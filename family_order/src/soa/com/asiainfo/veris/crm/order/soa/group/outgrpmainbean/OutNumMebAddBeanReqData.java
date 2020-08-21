
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class OutNumMebAddBeanReqData extends MemberReqData
{
    private String USER_ID_OUTPHONE; // 网外号码user_id

    private String OUT_GRP_NUM; // 网外号码sn

    public String getOUT_GRP_NUM()
    {
        return OUT_GRP_NUM;
    }

    public String getUSER_ID_OUTPHONE()
    {
        return USER_ID_OUTPHONE;
    }

    public void setOUT_GRP_NUM(String out_grp_num)
    {
        OUT_GRP_NUM = out_grp_num;
    }

    public void setUSER_ID_OUTPHONE(String user_id_outphone)
    {
        USER_ID_OUTPHONE = user_id_outphone;
    }
}
