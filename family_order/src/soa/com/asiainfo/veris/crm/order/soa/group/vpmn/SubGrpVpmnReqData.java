
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class SubGrpVpmnReqData extends MemberReqData
{

    private String parentSerialNumber;

    private String serialNumber;

    public String getParentSerialNumber()
    {
        return parentSerialNumber;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setParentSerialNumber(String parentSerialNumber)
    {
        this.parentSerialNumber = parentSerialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

}
