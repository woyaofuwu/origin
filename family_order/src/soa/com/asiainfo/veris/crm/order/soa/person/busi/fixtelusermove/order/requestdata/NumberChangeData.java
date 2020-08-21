
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.requestdata;

public class NumberChangeData
{
    private String serialNumber;

    private String resTypeCode;

    private String resKindCode;

    private String switchId;

    private String switchType;

    private String month;

    public String getMonth()
    {
        return month;
    }

    public String getResKindCode()
    {
        return resKindCode;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSwitchId()
    {
        return switchId;
    }

    public String getSwitchType()
    {
        return switchType;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSwitchId(String switchId)
    {
        this.switchId = switchId;
    }

    public void setSwitchType(String switchType)
    {
        this.switchType = switchType;
    }
}
