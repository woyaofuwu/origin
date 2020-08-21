
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifypwd.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CttBroadBandModifyPWDReqData extends BaseReqData
{
    private String userPassword = "";// 用户密码

    private String oldPasswd = "";// 输入的原密码

    private String newPasswd = "";// 新密码

    private String newPasswdAgain = "";// 新密码

    private String passwdType = "";// 变更类型

    private String randomPassWD = "";// 随机密码

    public String getNewPasswd()
    {
        return newPasswd;
    }

    public String getNewPasswdAgain()
    {
        return newPasswdAgain;
    }

    public String getOldPasswd()
    {
        return oldPasswd;
    }

    public String getPasswdType()
    {
        return passwdType;
    }

    public String getRandomPassWD()
    {
        return randomPassWD;
    }

    public String getUserPassword()
    {
        return userPassword;
    }

    public void setNewPasswd(String newPasswd)
    {
        this.newPasswd = newPasswd;
    }

    public void setNewPasswdAgain(String newPasswdAgain)
    {
        this.newPasswdAgain = newPasswdAgain;
    }

    public void setOldPasswd(String oldPasswd)
    {
        this.oldPasswd = oldPasswd;
    }

    public void setPasswdType(String passwdType)
    {
        this.passwdType = passwdType;
    }

    public void setRandomPassWD(String randomPassWD)
    {
        this.randomPassWD = randomPassWD;
    }

    public void setUserPassword(String userPassword)
    {
        this.userPassword = userPassword;
    }

}
