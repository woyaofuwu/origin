
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata;

public class EncAssemDynDataRsp
{
    private String seqno;

    private String resultCode;

    private String resultMessage;

    private String issueData;

    public String getIssueData()
    {
        return issueData;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public String getResultMessage()
    {
        return resultMessage;
    }

    public String getSeqno()
    {
        return seqno;
    }

    public void setIssueData(String issueData)
    {
        this.issueData = issueData;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public void setSeqno(String seqno)
    {
        this.seqno = seqno;
    }
}
