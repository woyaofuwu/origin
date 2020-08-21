
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata;

public class RoamEncPreDataRsp
{
    private String encPresetData;

    private String seqNo;

    private String resultCode;

    private String resultMessage;

    private String random;

    private String encPresetDataK;

    private String encPresetDataOPc;

    private String signature;

    public String getEncPresetData()
    {
        return encPresetData;
    }

    public String getEncPresetDataK()
    {
        return encPresetDataK;
    }

    public String getEncPresetDataOPc()
    {
        return encPresetDataOPc;
    }

    public String getRandom()
    {
        return random;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public String getResultMessage()
    {
        return resultMessage;
    }

    public String getSeqNo()
    {
        return seqNo;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setEncPresetData(String encPresetData)
    {
        this.encPresetData = encPresetData;
    }

    public void setEncPresetDataK(String encPresetDataK)
    {
        this.encPresetDataK = encPresetDataK;
    }

    public void setEncPresetDataOPc(String encPresetDataOPc)
    {
        this.encPresetDataOPc = encPresetDataOPc;
    }

    public void setRandom(String random)
    {
        this.random = random;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    public void setSeqNo(String seqNo)
    {
        this.seqNo = seqNo;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

}
