
package com.asiainfo.veris.crm.order.soa.person.busi.changeTeleNber.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangeTeleNbrReqData extends BaseReqData
{
    private String serialNumber;

    private String newSerialNumber;

    private String remarks;

    private String switchId;

    private String switchType;

    private String resKindCode;

    private String changteleNotice;

    public String getChangteleNotice()
    {
        return changteleNotice;
    }

    public String getNewSerialNumber()
    {
        return newSerialNumber;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public String getResKindCode()
    {
        return resKindCode;
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

    public void setChangteleNotice(String changteleNotice)
    {
        this.changteleNotice = changteleNotice;
    }

    public void setNewSerialNumber(String newSerialNumber)
    {
        this.newSerialNumber = newSerialNumber;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public void setResKindCode(String resTypeCode)
    {
        this.resKindCode = resTypeCode;
    }

    public void setSerialNumber(String serial_number)
    {
        this.serialNumber = serial_number;
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
