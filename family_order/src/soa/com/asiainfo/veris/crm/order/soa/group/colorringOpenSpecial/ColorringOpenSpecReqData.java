
package com.asiainfo.veris.crm.order.soa.group.colorringOpenSpecial;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class ColorringOpenSpecReqData extends MemberReqData
{
    private String serialNumber;

    private String endDate;

    public String getEndDate()
    {
        return endDate;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }
}
