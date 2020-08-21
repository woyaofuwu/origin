
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class VpnSpecialDistModiReqData extends ChangeMemElementReqData
{
    private String DISCNT_NAME;

    private String NOWACCT_FLAG;

    private String NEXT_ACCT_DISCODE;

    private String THIS_ACCT_DISCODE;

    public String getNEXT_ACCT_DISCODE()
    {
        return NEXT_ACCT_DISCODE;
    }

    public void setNEXT_ACCT_DISCODE(String nEXT_ACCT_DISCODE)
    {
        NEXT_ACCT_DISCODE = nEXT_ACCT_DISCODE;
    }

    public String getTHIS_ACCT_DISCODE()
    {
        return THIS_ACCT_DISCODE;
    }

    public void setTHIS_ACCT_DISCODE(String tHIS_ACCT_DISCODE)
    {
        THIS_ACCT_DISCODE = tHIS_ACCT_DISCODE;
    }

    public String getDISCNT_NAME()
    {
        return DISCNT_NAME;
    }

    public String getNOWACCT_FLAG()
    {
        return NOWACCT_FLAG;
    }

    public void setDISCNT_NAME(String dISCNT_NAME)
    {
        DISCNT_NAME = dISCNT_NAME;
    }

    public void setNOWACCT_FLAG(String nOWACCT_FLAG)
    {
        NOWACCT_FLAG = nOWACCT_FLAG;
    }

}
