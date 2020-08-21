
package com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

/**
 * @author think
 */
public class IMSChangeProductRequestData extends BaseChangeProductReqData
{
	private String imsProductName; 	
	private String mobileSerialNumber; 
	private String mobileProductId; 

    public String getIMSProductName()
    {
        return imsProductName;
    }
	public void setIMSProductName(String imsProductName)
    {
        this.imsProductName = imsProductName;
    }
    public String getMobileProductId()
    {
        return mobileProductId;
    }
	public void setMobileProductId(String mobileProductId)
    {
        this.mobileProductId = mobileProductId;
    }
    public String getMobileSerialNumber()
    {
        return mobileSerialNumber;
    }
	public void setMobileSerialNumber(String mobileSerialNumber)
    {
        this.mobileSerialNumber = mobileSerialNumber;
    }
}
