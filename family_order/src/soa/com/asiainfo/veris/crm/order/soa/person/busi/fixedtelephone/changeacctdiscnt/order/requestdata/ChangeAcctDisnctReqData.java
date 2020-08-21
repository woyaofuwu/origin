
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changeacctdiscnt.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangeAcctDisnctReqData extends BaseReqData
{

    private String acctDisnctCode;

    private String oldacctDisnctCode;

    public String getAcctDisnctCode()
    {
        return acctDisnctCode;
    }

    public String getOldacctDisnctCode()
    {
        return oldacctDisnctCode;
    }

    public void setAcctDisnctCode(String acctDisnctCode)
    {
        this.acctDisnctCode = acctDisnctCode;
    }

    public void setOldacctDisnctCode(String oldacctDisnctCode)
    {
        this.oldacctDisnctCode = oldacctDisnctCode;
    }

}
