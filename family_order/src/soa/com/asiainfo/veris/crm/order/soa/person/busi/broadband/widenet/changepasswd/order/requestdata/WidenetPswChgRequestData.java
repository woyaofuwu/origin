
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class WidenetPswChgRequestData extends BaseReqData
{
    private String newPasswd;// 新密码

    private String queryType;// 密码变更方式

    public String getNewPasswd()
    {
        return newPasswd;
    }

    public String getQueryType()
    {
        return queryType;
    }

    public void setNewPasswd(String newPasswd)
    {
        this.newPasswd = newPasswd;
    }

    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }
}
