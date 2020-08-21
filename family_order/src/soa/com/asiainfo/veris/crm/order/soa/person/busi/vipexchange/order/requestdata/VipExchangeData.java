
package com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.requestdata;

public class VipExchangeData
{

    private String giftTypeCode; // 兑换礼品类别

    private String giftName; // 兑换礼品名称

    private String giftId; // 兑换礼品编码

    private String giftFee; // 兑换礼品金额

    public String getGiftFee()
    {
        return giftFee;
    }

    public String getGiftId()
    {
        return giftId;
    }

    public String getGiftName()
    {
        return giftName;
    }

    public String getGiftTypeCode()
    {
        return giftTypeCode;
    }

    public void setGiftFee(String giftFee)
    {
        this.giftFee = giftFee;
    }

    public void setGiftId(String giftId)
    {
        this.giftId = giftId;
    }

    public void setGiftName(String giftName)
    {
        this.giftName = giftName;
    }

    public void setGiftTypeCode(String giftTypeCode)
    {
        this.giftTypeCode = giftTypeCode;
    }

}
