
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class DestroyGroupUserReqData extends GroupReqData
{
    private boolean ifBooking = false;

    private String reasonCode = "";

    private String netTypeCode = "";

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public final String getReasonCode()
    {
        return reasonCode;
    }

    public final boolean isIfBooking()
    {
        return ifBooking;
    }

    public final void setIfBooking(boolean ifBooking)
    {
        this.ifBooking = ifBooking;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public final void setReasonCode(String reasonCode)
    {
        this.reasonCode = reasonCode;
    }
}
