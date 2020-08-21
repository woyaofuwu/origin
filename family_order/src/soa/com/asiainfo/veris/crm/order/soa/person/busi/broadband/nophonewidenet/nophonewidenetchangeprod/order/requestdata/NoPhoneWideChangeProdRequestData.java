
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class NoPhoneWideChangeProdRequestData extends BaseChangeProductReqData
{
    private String wideActivePayFee; 

    private String yearDiscntRemainFee; 
    
    private String remainFee;
    
    private String acctReainFee;

    public String getWideActivePayFee()
    {
        return wideActivePayFee;
    }

    public void setWideActivePayFee(String wideActivePayFee)
    {
        this.wideActivePayFee = wideActivePayFee;
    }

    public String getYearDiscntRemainFee()
    {
        return yearDiscntRemainFee;
    }

    public void setYearDiscntRemainFee(String yearDiscntRemainFee)
    {
        this.yearDiscntRemainFee = yearDiscntRemainFee;
    }

    public String getRemainFee()
    {
        return remainFee;
    }

    public void setRemainFee(String remainFee)
    {
        this.remainFee = remainFee;
    }

    public String getAcctReainFee()
    {
        return acctReainFee;
    }

    public void setAcctReainFee(String acctReainFee)
    {
        this.acctReainFee = acctReainFee;
    }
    
}
