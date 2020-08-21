
package com.asiainfo.veris.crm.order.soa.group.groupspecialopen;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;

public class GroupSpecialOpenReqData extends ChangeUserElementReqData
{
    private String sercheck;

    private String serialNumber;

    private String narmalTime;

    public String getNarmalTime()
    {
        return narmalTime;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getserverid()
    {
        return sercheck;
    }

    public void setNarmalTime(String narmalTime)
    {
        this.narmalTime = narmalTime;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setserverid(String sercheck)
    {
        this.sercheck = sercheck;
    }
}
