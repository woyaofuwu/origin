
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseCreateWalletCardReqData extends BaseReqData
{

    private String applicationNum; // 申请单编号

    private String authorizationType; // 授权类型

    private String rspCode; // 返回消息

    private String idcardDepartment; // 签发机关

    private String yearIncome; // 宣传年收入水平

    private String bossId; // boss流水号

    private String checkTag;// 实名验证通过标志

    public String getApplicationNum()
    {
        return applicationNum;
    }

    public String getAuthorizationType()
    {
        return authorizationType;
    }

    public String getBossId()
    {
        return bossId;
    }

    public String getCheckTag()
    {
        return checkTag;
    }

    public String getIdcardDepartment()
    {
        return idcardDepartment;
    }

    public String getRspCode()
    {
        return rspCode;
    }

    public String getYearIncome()
    {
        return yearIncome;
    }

    public void setApplicationNum(String applicationNum)
    {
        this.applicationNum = applicationNum;
    }

    public void setAuthorizationType(String authorizationType)
    {
        this.authorizationType = authorizationType;
    }

    public void setBossId(String bossId)
    {
        this.bossId = bossId;
    }

    public void setCheckTag(String checkTag)
    {
        this.checkTag = checkTag;
    }

    public void setIdcardDepartment(String idcardDepartment)
    {
        this.idcardDepartment = idcardDepartment;
    }

    public void setRspCode(String rspCode)
    {
        this.rspCode = rspCode;
    }

    public void setYearIncome(String yearIncome)
    {
        this.yearIncome = yearIncome;
    }

}
