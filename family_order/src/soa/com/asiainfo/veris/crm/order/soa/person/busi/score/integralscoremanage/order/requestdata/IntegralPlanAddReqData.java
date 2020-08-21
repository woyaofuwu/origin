
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class IntegralPlanAddReqData extends BaseReqData
{

    private String IntegralPlanId;

    private String StartDate;

    private String EndDate;

    public String getEndDate()
    {
        return EndDate;
    }

    public String getIntegralPlanId()
    {
        return IntegralPlanId;
    }

    public String getStartDate()
    {
        return StartDate;
    }

    public void setEndDate(String endDate)
    {
        EndDate = endDate;
    }

    public void setIntegralPlanId(String integralPlanId)
    {
        IntegralPlanId = integralPlanId;
    }

    public void setStartDate(String startDate)
    {
        StartDate = startDate;
    }

}
