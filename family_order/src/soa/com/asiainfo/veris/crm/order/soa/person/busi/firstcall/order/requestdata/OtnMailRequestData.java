
package com.asiainfo.veris.crm.order.soa.person.busi.firstcall.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class OtnMailRequestData extends BaseReqData
{
    private String elementId;

    private String userId;

    private String productId;

    private String packageId;

    private String serialNumber;

    public String getElementId()
    {
        return elementId;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

}
