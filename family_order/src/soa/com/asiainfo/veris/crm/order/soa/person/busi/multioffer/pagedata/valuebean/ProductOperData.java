/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.ivalues.IPageData;

public class ProductOperData implements IPageData
{

    private String productId;

    private String productName;

    private String effictDate;

    private String expireDate;

    private String itemType = "产品";

    private List<RoleOperData> roleOperList = new ArrayList<RoleOperData>();

    public String getEffictDate()
    {
        return effictDate;
    }

    public String getExpireDate()
    {
        return expireDate;
    }

    public String getItemType()
    {
        return this.itemType;
    }

    public IPageData getParentPageData()
    {
        return null;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getProductName()
    {
        return productName;
    }

    public List<RoleOperData> getRoleOperList()
    {
        return roleOperList;
    }

    public void setEffictDate(String effictDate)
    {
        this.effictDate = effictDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setParentPageData(IPageData parent)
    {

    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public void setRoleOperList(List<RoleOperData> roleOperList)
    {
        this.roleOperList = roleOperList;
    }

}
