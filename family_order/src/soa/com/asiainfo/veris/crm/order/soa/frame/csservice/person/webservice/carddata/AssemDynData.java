
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata;

import java.util.List;

public class AssemDynData
{
    private String cardInfo;

    private String SeqNo;

    private String cardRsp;

    private String chanelflg;

    private List<EncAssemDynData> enc;

    public String getCardInfo()
    {
        return cardInfo;
    }

    public String getCardRsp()
    {
        return cardRsp;
    }

    public String getChanelflg()
    {
        return chanelflg;
    }

    public List<EncAssemDynData> getEnc()
    {
        return enc;
    }

    public String getSeqNo()
    {
        return SeqNo;
    }

    public void setCardInfo(String cardInfo)
    {
        this.cardInfo = cardInfo;
    }

    public void setCardRsp(String cardRsp)
    {
        this.cardRsp = cardRsp;
    }

    public void setChanelflg(String chanelflg)
    {
        this.chanelflg = chanelflg;
    }

    public void setEnc(List<EncAssemDynData> enc)
    {
        this.enc = enc;
    }

    public void setSeqNo(String seqNo)
    {
        SeqNo = seqNo;
    }

}
