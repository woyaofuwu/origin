
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditMemberStateChg;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class MemberStateChgReqData extends MemberReqData
{
    private String DEAL_FLAG;

    public void setDealFlag(String dealFlag)
    {
        DEAL_FLAG = dealFlag;
    }

    public String getDealFlag()
    {
        return DEAL_FLAG;
    }
}
