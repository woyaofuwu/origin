package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class VipSimBakAppReqData extends BaseReqData
{
    private String bakCardNo; // SIM备卡

    private String KI;

    private String IMSI;

    private String simTypeCode;
 
    private String rsrvStr4; // SIM卡类型

    private String bookDate; // 发卡时间

    private String userTag; // 用户级别标志

    private String vipClassId; // vip级别

    private String simBakLimit;// 申请卡数量限制

    private String vipId;

    private String feeTag;  //买断卡费用表示
    
    private String creditFeeTag;  //3至5星级是否免费申请备卡标志
    
    private String saleMoney;//买断费用
    
    private String saleFeeTag ;//白卡买断卡费用表示
    
    
    public String getCreditFeeTag()
    {
        return creditFeeTag;
    }

    public void setCreditFeeTag(String creditFeeTag)
    {
        this.creditFeeTag = creditFeeTag;
    }
    
    public String getSaleFeeTag()
    {
        return saleFeeTag;
    }

    public void setSaleFeeTag(String saleFeeTag)
    {
        this.saleFeeTag = saleFeeTag;
    }

    public String getFeeTag()
    {
        return feeTag;
    }

    public void setFeeTag(String feeTag)
    {
        this.feeTag = feeTag;
    }

    public String getSaleMoney()
    {
        return saleMoney;
    }

    public void setSaleMoney(String saleMoney)
    {
        this.saleMoney = saleMoney;
    }

    public String getBakCardNo()
    {
        return bakCardNo;
    }

    public String getBookDate()
    {
        return bookDate;
    }

    public String getIMSI()
    {
        return IMSI;
    }

    public String getKI()
    {
        return KI;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getSimBakLimit()
    {
        return simBakLimit;
    }

    public String getSimTypeCode()
    {
        return simTypeCode;
    }

    public String getUserTag()
    {
        return userTag;
    }

    public String getVipClassId()
    {
        return vipClassId;
    }

    public String getVipId()
    {
        return vipId;
    }

    public void setBakCardNo(String bakCardNo)
    {
        this.bakCardNo = bakCardNo;
    }

    public void setBookDate(String bookDate)
    {
        this.bookDate = bookDate;
    }

    public void setIMSI(String iMSI)
    {
        IMSI = iMSI;
    }

    public void setKI(String kI)
    {
        KI = kI;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setSimBakLimit(String simBakLimit)
    {
        this.simBakLimit = simBakLimit;
    }

    public void setSimTypeCode(String simTypeCode)
    {
        this.simTypeCode = simTypeCode;
    }

    public void setUserTag(String userTag)
    {
        this.userTag = userTag;
    }

    public void setVipClassId(String vipClassId)
    {
        this.vipClassId = vipClassId;
    }

    public void setVipId(String vipId)
    {
        this.vipId = vipId;
    }

}
