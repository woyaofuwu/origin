
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.requestdata;

public class TelephoneChangeData
{
    private String standAddress;

    private String detailAddress;

    private String signPath;

    private String remark;

    private String standAddressCode;

    public String getDetailAddress()
    {
        return detailAddress;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getSignPath()
    {
        return signPath;
    }

    public String getStandAddress()
    {
        return standAddress;
    }

    public String getStandAddressCode()
    {
        return standAddressCode;
    }

    public void setDetailAddress(String detailAddress)
    {
        this.detailAddress = detailAddress;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setSignPath(String signPath)
    {
        this.signPath = signPath;
    }

    public void setStandAddress(String standAddress)
    {
        this.standAddress = standAddress;
    }

    public void setStandAddressCode(String standAddressCode)
    {
        this.standAddressCode = standAddressCode;
    }
}
