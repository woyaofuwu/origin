
package com.asiainfo.veris.crm.order.soa.person.busi.tempphonereturn.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class PhoneReturnRequestData extends BaseReqData
{
    private String simCardNo;

    private String serialNumber;

    private String flg;

    private String imis;

    private String opc;

    private String emptyCard;

    private String ki;

    public String getEmptyCard()
    {
        return emptyCard;
    }

    public String getFlg()
    {
        return flg;
    }

    public String getImis()
    {
        return imis;
    }

    public String getKi()
    {
        return ki;
    }

    public String getOpc()
    {
        return opc;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public void setEmptyCard(String emptyCard)
    {
        this.emptyCard = emptyCard;
    }

    public void setFlg(String flg)
    {
        this.flg = flg;
    }

    public void setImis(String imis)
    {
        this.imis = imis;
    }

    public void setKi(String ki)
    {
        this.ki = ki;
    }

    public void setOpc(String opc)
    {
        this.opc = opc;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
    }

}
