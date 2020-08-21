
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeimspasswd;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class ChangeIMSpasswdReqData extends MemberReqData
{
    private IData IMPUINFO; // IMPU信息

    private boolean ECFETION_TAG; // 企业飞信订购信息

    private String user_passwd2; // 密码

    public boolean getECFETION_TAG()
    {
        return ECFETION_TAG;
    }

    public IData getIMPUINFO()
    {
        return IMPUINFO;
    }

    public String getUser_passwd2()
    {
        return user_passwd2;
    }

    public void setECFETION_TAG(boolean ecfetion_tag)
    {
        ECFETION_TAG = ecfetion_tag;
    }

    public void setIMPUINFO(IData impuinfo)
    {
        IMPUINFO = impuinfo;
    }

    public void setUser_passwd2(String user_passwd2)
    {
        this.user_passwd2 = user_passwd2;
    }
}
