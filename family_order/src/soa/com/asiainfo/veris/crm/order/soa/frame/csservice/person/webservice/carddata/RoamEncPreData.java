
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata;

public class RoamEncPreData
{
    private EncPresetData encPresetData;

    private String seqNo;

    private String localProvCode;

    public EncPresetData getEncPresetData()
    {
        return encPresetData;
    }

    public String getLocalProvCode()
    {
        return localProvCode;
    }

    public String getSeqNo()
    {
        return seqNo;
    }

    public void setEncPresetData(EncPresetData encPresetData)
    {
        this.encPresetData = encPresetData;
    }

    public void setLocalProvCode(String localProvCode)
    {
        this.localProvCode = localProvCode;
    }

    public void setSeqNo(String seqNo)
    {
        this.seqNo = seqNo;
    }

}
