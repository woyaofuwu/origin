
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.grouptradefee;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUserReqData;

public class GroupTradeFeeCancelReqData extends CreateGroupUserReqData
{
    private String tradeID;

    /**
     * @return the tradeID
     */
    public String getTradeID()
    {
        return tradeID;
    }

    /**
     * @param tradeID
     *            the tradeID to set
     */
    public void setTradeID(String tradeID)
    {
        this.tradeID = tradeID;
    }
}
