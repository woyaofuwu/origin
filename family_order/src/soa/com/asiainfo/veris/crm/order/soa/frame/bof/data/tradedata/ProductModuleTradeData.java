
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;

public abstract class ProductModuleTradeData extends BaseTradeData
{
    private ProductModuleData pmd;

    protected String userId;

    protected String elementId;

    protected String elementType;

    protected String campnId;

    protected String instId;

    protected String modifyTag;

    protected String startDate;

    protected String endDate;

    protected String productId;

    protected String packageId;

    protected List<AttrTradeData> attrTradeDatas = new ArrayList<AttrTradeData>();

    public void addAttrTradeDatas(AttrTradeData attrTradeData)
    {
        this.attrTradeDatas.add(attrTradeData);
    }

    public ProductModuleTradeData clone()
    {
        return null;
    }

    public List<AttrTradeData> getAttrTradeDatas()
    {
        return attrTradeDatas;
    }

    public String getCampnId()
    {
        return campnId;
    }

    public String getElementId()
    {
        return elementId;
    }

    public String getElementType()
    {
        return elementType;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public ProductModuleData getPmd()
    {
        return pmd;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAttrTradeDatas(List<AttrTradeData> attrTradeDatas)
    {
        this.attrTradeDatas = attrTradeDatas;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setElementType(String elementType)
    {
        this.elementType = elementType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setPmd(ProductModuleData pmd)
    {
        this.pmd = pmd;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
