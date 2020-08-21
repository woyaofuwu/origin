
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifyUserPwdInfoReqData extends BaseReqData
{

    private String newPasswd = "";// 新密码

    private String passwdType = "";// 变更类型

    private String managemode = "";// 修改密码类型for接口X_MANAGEMODE

    public String getManagemode()
    {
        return managemode;
    }

    public String getNewPasswd()
    {
        return newPasswd;
    }

    public String getPasswdType()
    {
        return passwdType;
    }

    public void setManagemode(String managemode)
    {
        this.managemode = managemode;
    }

    public void setNewPasswd(String newPasswd)
    {
        this.newPasswd = newPasswd;
    }

    public void setPasswdType(String passwdType)
    {
        this.passwdType = passwdType;
    }

}
