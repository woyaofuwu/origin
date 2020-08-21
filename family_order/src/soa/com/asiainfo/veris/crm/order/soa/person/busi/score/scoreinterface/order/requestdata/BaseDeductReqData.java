
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseDeductReqData extends BaseReqData
{
    private String scoreValue;

    private String rsrvStr1;

    private String xOrderId;

    private String oprt;

    private String userPasswd;

    public String getOprt()
    {
        return oprt;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public String getUserPasswd()
    {
        return userPasswd;
    }

    public String getXOrderId()
    {
        return xOrderId;
    }

    public void setOprt(String oprt)
    {
        this.oprt = oprt;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setUserPasswd(String userPasswd)
    {
        this.userPasswd = userPasswd;
    }

    public void setXOrderId(String orderId)
    {
        xOrderId = orderId;
    }

}
