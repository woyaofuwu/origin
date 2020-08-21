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
 * @ClassName: DiscntOperData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-29 上午09:51:48 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
 */

public class DiscntOperData implements IPageData
{

    private String elementId;

    private String elementName;

    private String elementType;

    private String itemType = "元素";

    private IPageData parentOperData;

    private List<AttrOperData> attrOperList = new ArrayList<AttrOperData>();

    public List<AttrOperData> getAttrOperList()
    {
        return attrOperList;
    }

    public String getElementId()
    {
        return elementId;
    }

    public String getElementName()
    {
        return elementName;
    }

    public String getElementType()
    {
        return elementType;
    }

    public String getItemType()
    {
        return itemType;
    }

    public IPageData getParentPageData()
    {
        return parentOperData;
    }

    public void setAttrOperList(List<AttrOperData> attrOperList)
    {
        this.attrOperList = attrOperList;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }

    public void setElementType(String elementType)
    {
        this.elementType = elementType;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setParentPageData(IPageData parentOperData)
    {
        this.parentOperData = parentOperData;
    }

}
