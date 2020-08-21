
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.cache;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class SaleTradeLimitObj
{
    private String campnType;

    private String productId;

    private String packageId;

    private String limitTag;

    private String limitType;

    private String limitCode;

    private String limitMsg;

    public static final String SLAE_TRADE_LIMIT_CHCHE_KEY = "SaleTradeLimitObj";

    public String getCampnType()
    {
        return campnType;
    }

    public String getLimitCode()
    {
        return limitCode;
    }

    public String getLimitMsg()
    {
        return limitMsg;
    }

    public String getLimitTag()
    {
        return limitTag;
    }

    public String getLimitType()
    {
        return limitType;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setCampnType(String campnType)
    {
        this.campnType = campnType;
    }

    public void setLimitCode(String limitCode)
    {
        this.limitCode = limitCode;
    }

    public void setLimitMsg(String limitMsg)
    {
        this.limitMsg = limitMsg;
    }

    public void setLimitTag(String limitTag)
    {
        this.limitTag = limitTag;
    }

    public void setLimitType(String limitType)
    {
        this.limitType = limitType;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public IData toData()
    {
        IData SaleTradeLimitData = new DataMap();
        SaleTradeLimitData.put("CAMPN_TYPE", this.campnType);
        SaleTradeLimitData.put("PRODUCT_ID", this.productId);
        SaleTradeLimitData.put("PACKAGE_ID", this.packageId);
        SaleTradeLimitData.put("LIMIT_TAG", this.limitTag);
        SaleTradeLimitData.put("LIMIT_TYPE", this.limitType);
        SaleTradeLimitData.put("LIMIT_CODE", this.limitCode);
        SaleTradeLimitData.put("LIMIT_MSG", this.limitMsg);
        return SaleTradeLimitData;
    }

}
