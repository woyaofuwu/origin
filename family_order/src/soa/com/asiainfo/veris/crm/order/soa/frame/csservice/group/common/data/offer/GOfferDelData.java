package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import com.ailk.common.data.IData;

public class GOfferDelData extends OfferData
{
    private String offerInsId;

    private String expireDate;

    private String OperCode;
    
    private String offerType;
    
    private String remark;
    
    public String getExpireDate()
    {
        return expireDate;
    }

    public String getOfferInsId()
    {
        return offerInsId;
    }

    public void setOfferInsId(String offerInsId)
    {
        this.offerInsId = offerInsId;
    }

    public String getOperCode()
    {
        return OperCode;
    }

    public void setOperCode(String OperCode)
    {
        this.OperCode = OperCode;
    }

    public String getOfferType()
    {
        return offerType;
    }

    public void setOfferType(String offerType)
    {
        this.offerType = offerType;
    }
    
    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public static GOfferDelData getInstance(IData offerData)
    {
        GOfferDelData od = new GOfferDelData();
        if (offerData == null)
            return od;

        od.setOfferId(offerData.getString("OFFER_ID"));
        od.setOfferInsId(offerData.getString("OFFER_INS_ID"));
        od.setExpireDate(offerData.getString("EXPIRE_DATE"));
        od.setOperCode(offerData.getString("OPER_CODE"));
        od.setOfferType(offerData.getString("OFFER_TYPE"));
        od.setRemark(offerData.getString("REMARK"));
        
        return od;

    }
}
