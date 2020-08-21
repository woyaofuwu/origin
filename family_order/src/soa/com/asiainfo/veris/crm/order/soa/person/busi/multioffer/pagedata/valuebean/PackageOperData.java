/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.ivalues.IPageData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PackageOperData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-24 下午04:32:26 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
 */

public class PackageOperData implements IPageData
{

    private String packageId;

    private String packageName;

    private String itemType = "包";

    private String limitType;

    private String minValue;

    private String maxValue;

    private IPageData parent;

    private List<ProductOperData> prodOperList = new ArrayList<ProductOperData>();

    private List<ServiceOperData> svcOperList = new ArrayList<ServiceOperData>();

    private List<DiscntOperData> disOperList = new ArrayList<DiscntOperData>();

    public List<DiscntOperData> getDisOperList()
    {
        return disOperList;
    }

    public String getItemType()
    {
        return this.itemType;
    }

    public String getLimitType()
    {
        return limitType;
    }

    public String getMaxValue()
    {
        return maxValue;
    }

    public String getMinValue()
    {
        return minValue;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public IPageData getParentPageData()
    {
        return this.parent;
    }

    public List<ProductOperData> getProdOperList()
    {
        return prodOperList;
    }

    public List<ServiceOperData> getSvcOperList()
    {
        return svcOperList;
    }

    public void setDisOperList(List<DiscntOperData> disOperList)
    {
        this.disOperList = disOperList;
    }

    public void setEleOperList(List<ProductOperData> prodOperList)
    {
        this.prodOperList = prodOperList;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setLimitType(String limitType)
    {
        this.limitType = limitType;
    }

    public void setMaxValue(String maxValue)
    {
        this.maxValue = maxValue;
    }

    public void setMinValue(String minValue)
    {
        this.minValue = minValue;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public void setParentPageData(IPageData parent)
    {
        this.parent = parent;
    }

    public void setSvcOperList(List<ServiceOperData> svcOperList)
    {
        this.svcOperList = svcOperList;
    }

}
