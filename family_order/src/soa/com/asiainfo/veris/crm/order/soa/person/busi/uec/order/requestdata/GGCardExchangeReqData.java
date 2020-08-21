
package com.asiainfo.veris.crm.order.soa.person.busi.uec.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class GGCardExchangeReqData extends BaseReqData
{

    private String serialNumber; // 兑奖号码

    private String serialNumber_b; // 发送短信号码

    private String cardTypecode; // 刮刮卡类型

    private String cardNo; // 刮刮卡号

    private int paracode1; // 发短信的号码用户当月发送短信次数(上限值)

    private int paracode2; // 兑奖号码用户当月已中奖已达到次数(上限值)

    private String channelId; // 缴费渠道编码

    private String paymentId; // 存折编码

    private String payfeeModecode; // 付费方式

    public String getCardNo()
    {
        return cardNo;
    }

    public String getCardTypecode()
    {
        return cardTypecode;
    }

    public String getChannelId()
    {
        return channelId;
    }

    public int getParacode1()
    {
        return paracode1;
    }

    public int getParacode2()
    {
        return paracode2;
    }

    public String getPayfeeModecode()
    {
        return payfeeModecode;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSerialNumber_b()
    {
        return serialNumber_b;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }

    public void setCardTypecode(String cardTypecode)
    {
        this.cardTypecode = cardTypecode;
    }

    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    public void setParacode1(int paracode1)
    {
        this.paracode1 = paracode1;
    }

    public void setParacode2(int paracode2)
    {
        this.paracode2 = paracode2;
    }

    public void setPayfeeModecode(String payfeeModecode)
    {
        this.payfeeModecode = payfeeModecode;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSerialNumber_b(String serialNumber_b)
    {
        this.serialNumber_b = serialNumber_b;
    }

}
