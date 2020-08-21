
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub;

public class ReturnActiveExchangeCardData
{

    private String cardCode;// 刮刮卡序列号

    private String cardPassWord;// 刮刮卡校验码

    private String remark;// 备注

    private String giftCode;// 奖品编码

    private String resCode;// 奖品终端串码

    public String getCardCode()
    {
        return cardCode;
    }

    public String getCardPassWord()
    {
        return cardPassWord;
    }

    public String getGiftCode()
    {
        return giftCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getResCode()
    {
        return resCode;
    }

    public void setCardCode(String cardCode)
    {
        this.cardCode = cardCode;
    }

    public void setCardPassWord(String cardPassWord)
    {
        this.cardPassWord = cardPassWord;
    }

    public void setGiftCode(String giftCode)
    {
        this.giftCode = giftCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

}
