
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class RemoveGrpMemReqData extends MemberReqData
{
    private String reason;

    private String reason_code;

    public String getReason()
    {
        return reason;
    }

    public String getReason_code()
    {
        return reason_code;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public void setReason_code(String reason_code)
    {
        this.reason_code = reason_code;
    }

}
