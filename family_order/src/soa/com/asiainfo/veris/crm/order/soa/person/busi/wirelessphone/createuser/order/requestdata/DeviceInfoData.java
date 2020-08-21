/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DeviceInfoData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-9 上午10:31:49 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
 */

public class DeviceInfoData
{

    private String deviceTypeCode;

    private String deviceCode;

    private String saleTypeCode;

    private String saleTypeDesc;

    private String deviceKindCode;

    private String useTypeCode;

    private String devicePrice;

    public String getDeviceCode()
    {
        return deviceCode;
    }

    public String getDeviceKindCode()
    {
        return deviceKindCode;
    }

    public String getDevicePrice()
    {
        return devicePrice;
    }

    public String getDeviceTypeCode()
    {
        return deviceTypeCode;
    }

    public String getSaleTypeCode()
    {
        return saleTypeCode;
    }

    public String getSaleTypeDesc()
    {
        return saleTypeDesc;
    }

    public String getUseTypeCode()
    {
        return useTypeCode;
    }

    public void setDeviceCode(String deviceCode)
    {
        this.deviceCode = deviceCode;
    }

    public void setDeviceKindCode(String deviceKindCode)
    {
        this.deviceKindCode = deviceKindCode;
    }

    public void setDevicePrice(String devicePrice)
    {
        this.devicePrice = devicePrice;
    }

    public void setDeviceTypeCode(String deviceTypeCode)
    {
        this.deviceTypeCode = deviceTypeCode;
    }

    public void setSaleTypeCode(String saleTypeCode)
    {
        this.saleTypeCode = saleTypeCode;
    }

    public void setSaleTypeDesc(String saleTypeDesc)
    {
        this.saleTypeDesc = saleTypeDesc;
    }

    public void setUseTypeCode(String useTypeCode)
    {
        this.useTypeCode = useTypeCode;
    }

}
