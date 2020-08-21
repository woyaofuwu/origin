
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

public class CreateMasGroupUserReqData extends CreateGroupUserReqData
{
    private String FIRST_DATE;// 开通时间

    public String getFIRST_DATE()
    {
        return FIRST_DATE;
    }

    public void setFIRST_DATE(String FIRST_DATE)
    {
        this.FIRST_DATE = FIRST_DATE;
    }
}
