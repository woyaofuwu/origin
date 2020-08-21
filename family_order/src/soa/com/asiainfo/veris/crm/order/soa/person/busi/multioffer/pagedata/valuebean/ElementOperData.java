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
 * @ClassName: ElementOperData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-22 下午02:51:24 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-22 chengxf2 v1.0.0 修改原因
 */

public class ElementOperData implements IPageData
{

    private String elementId;

    private String elementName;

    private String elementType;

    private String itemType = "元素";

    private IPageData parentOperData;

    private boolean makeGroup;

    private String groupNo;

    private String groupName;

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

    public String getGroupName()
    {
        return groupName;
    }

    public String getGroupNo()
    {
        return groupNo;
    }

    public String getItemType()
    {
        return itemType;
    }

    public IPageData getParentPageData()
    {
        return parentOperData;
    }

    public boolean isMakeGroup()
    {
        return makeGroup;
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

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setGroupNo(String groupNo)
    {
        this.groupNo = groupNo;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setMakeGroup(boolean makeGroup)
    {
        this.makeGroup = makeGroup;
    }

    public void setParentPageData(IPageData parentOperData)
    {
        this.parentOperData = parentOperData;
    }

}
