
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class NoPhoneWideRenewRequestData extends BaseChangeProductReqData
{
    private String productId; 
    
    private String packageId;
    
    private String discntId; 
    
    private String startDate;
    
    private String endDate;
    
    private String wideYearFee;
    
    private String wideFirstMonFee;
    
    private String stopOpenTag;
    
    public String getStopOpenTag()
    {
        return stopOpenTag;
    }

    public void setStopOpenTag(String stopOpenTag)
    {
        this.stopOpenTag = stopOpenTag;
    }
    
    public String getWideYearFee()
    {
        return wideYearFee;
    }

    public void setWideYearFee(String wideYearFee)
    {
        this.wideYearFee = wideYearFee;
    }
    
    public String getWideFirstMonFee()
    {
        return wideFirstMonFee;
    }

    public void setWideFirstMonFee(String wideFirstMonFee)
    {
        this.wideFirstMonFee = wideFirstMonFee;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }
    
    public String getPackageId()
    {
        return packageId;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public String getDiscntId()
    {
        return discntId;
    }

    public void setDiscntId(String discntId)
    {
        this.discntId = discntId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getendDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }
    
}
