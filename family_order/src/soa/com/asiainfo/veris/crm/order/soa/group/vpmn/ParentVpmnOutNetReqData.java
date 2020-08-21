
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class ParentVpmnOutNetReqData extends MemberReqData
{
    private String serialNumber;

    private String outSerialNumber;

    private String outShortCode;

    public String getOutSerialNumber()
    {
        return outSerialNumber;
    }

    public String getOutShortCode()
    {
        return outShortCode;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setOutSerialNumber(String outSerialNumber)
    {
        this.outSerialNumber = outSerialNumber;
    }

    public void setOutShortCode(String outShortCode)
    {
        this.outShortCode = outShortCode;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

}
