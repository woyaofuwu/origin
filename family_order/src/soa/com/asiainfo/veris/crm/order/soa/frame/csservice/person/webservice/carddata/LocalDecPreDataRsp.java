
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata;

public class LocalDecPreDataRsp
{

    private String seqNo;

    private String resultCode;

    private String resultMessage;

    private EncPresetData presetData;

    public EncPresetData getPresetData()
    {
        return presetData;
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

    public void setPresetData(EncPresetData presetData)
    {
        this.presetData = presetData;
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

}
