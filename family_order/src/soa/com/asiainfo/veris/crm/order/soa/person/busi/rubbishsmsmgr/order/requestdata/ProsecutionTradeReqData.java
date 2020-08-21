
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ProsecutionTradeReqData extends BaseReqData
{
    private String prosecutionWay;

    private String prosecutionNumber;

    private String smsContent;

    private String inModeCode;

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getProsecutionNumber()
    {
        return prosecutionNumber;
    }

    public String getProsecutionWay()
    {
        return prosecutionWay;
    }

    public String getSmsContent()
    {
        return smsContent;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setProsecutionNumber(String prosecutionNumber)
    {
        this.prosecutionNumber = prosecutionNumber;
    }

    public void setProsecutionWay(String prosecutionWay)
    {
        this.prosecutionWay = prosecutionWay;
    }

    public void setSmsContent(String smsContent)
    {
        this.smsContent = smsContent;
    }
}
