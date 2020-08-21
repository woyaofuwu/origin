
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata;

public class LocalDecPreData
{

    private String seqNo;

    private String EncPresetDataK;

    private String EncPresetDataOPc;

    private String Signature;

    private String LocalProvCode;

    private String encPresetData;

    public String getEncPresetData()
    {
        return encPresetData;
    }

    public String getEncPresetDataK()
    {
        return EncPresetDataK;
    }

    public String getEncPresetDataOPc()
    {
        return EncPresetDataOPc;
    }

    public String getLocalProvCode()
    {
        return LocalProvCode;
    }

    public String getSeqNo()
    {
        return seqNo;
    }

    public String getSignature()
    {
        return Signature;
    }

    public void setEncPresetData(String encPresetData)
    {
        this.encPresetData = encPresetData;
    }

    public void setEncPresetDataK(String encPresetDataK)
    {
        EncPresetDataK = encPresetDataK;
    }

    public void setEncPresetDataOPc(String encPresetDataOPc)
    {
        EncPresetDataOPc = encPresetDataOPc;
    }

    public void setLocalProvCode(String localProvCode)
    {
        LocalProvCode = localProvCode;
    }

    public void setSeqNo(String seqNo)
    {
        this.seqNo = seqNo;
    }

    public void setSignature(String signature)
    {
        Signature = signature;
    }

}
