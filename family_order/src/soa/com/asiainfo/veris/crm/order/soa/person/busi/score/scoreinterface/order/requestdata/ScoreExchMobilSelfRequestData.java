
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ScoreExchMobilSelfRequestData extends BaseReqData
{
    private String orderNo;

    private String lMobile;// 用户兑换手机号码

    private String type;// 0：个人兑换 1：业务转赠

    private String provCode;

    private String ordOprTime;

    private List<StackPackageData> stackPackage;

    public String getlMobile()
    {
        return lMobile;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public String getOrdOprTime()
    {
        return ordOprTime;
    }

    public String getProvCode()
    {
        return provCode;
    }

    public List<StackPackageData> getStackPackage()
    {
        return stackPackage;
    }

    public String getType()
    {
        return type;
    }

    public void setlMobile(String lMobile)
    {
        this.lMobile = lMobile;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public void setOrdOprTime(String ordOprTime)
    {
        this.ordOprTime = ordOprTime;
    }

    public void setProvCode(String provCode)
    {
        this.provCode = provCode;
    }

    public void setStackPackage(List<StackPackageData> stackPackage)
    {
        this.stackPackage = stackPackage;
    }

    public void setType(String type)
    {
        this.type = type;
    }

}
