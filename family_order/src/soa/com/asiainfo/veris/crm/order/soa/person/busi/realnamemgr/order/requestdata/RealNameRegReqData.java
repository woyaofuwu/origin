
package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class RealNameRegReqData extends BaseReqData
{
    private String custName;// 客户名称

    private String psptTypeCode;// 证件类型

    private String psptId;// 证件号码

    private String psptAddr;// 证件地址

    private String phone;// 联系电话

    public String getCustName()
    {
        return custName;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getPsptAddr()
    {
        return psptAddr;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPsptAddr(String psptAddr)
    {
        this.psptAddr = psptAddr;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

}
