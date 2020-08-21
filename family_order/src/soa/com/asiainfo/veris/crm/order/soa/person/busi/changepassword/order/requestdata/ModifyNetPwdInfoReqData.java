
package com.asiainfo.veris.crm.order.soa.person.busi.changepassword.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifyNetPwdInfoReqData extends BaseReqData
{

    private String newPasswd = "";// 新密码

    private String passwdType = "";// 变更类型

    private String randomPassWD = "";// 重置随机密码

    public String getNewPasswd()
    {
        return newPasswd;
    }

    public String getPasswdType()
    {
        return passwdType;
    }

    public String getRandomPassWD()
    {
        return randomPassWD;
    }

    public void setNewPasswd(String newPasswd)
    {
        this.newPasswd = newPasswd;
    }

    public void setPasswdType(String passwdType)
    {
        this.passwdType = passwdType;
    }

    public void setRandomPassWD(String randomPassWD)
    {
        this.randomPassWD = randomPassWD;
    }

}
