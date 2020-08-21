
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.createaccountunitegrp;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class CreateAccountUniteGrpReqData extends CreateGroupMemberReqData
{
    private String memSerialNumber;

    private String serialNumberB; // 新集团代表号码

    /**
     * @return the memSerialNumber
     */
    public String getMemSerialNumber()
    {
        return memSerialNumber;
    }

    /**
     * @return the serialNumberB
     */
    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    /**
     * @param memSerialNumber
     *            the memSerialNumber to set
     */
    public void setMemSerialNumber(String memSerialNumber)
    {
        this.memSerialNumber = memSerialNumber;
    }

    /**
     * @param serialNumberB
     *            the serialNumberB to set
     */
    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }
}
