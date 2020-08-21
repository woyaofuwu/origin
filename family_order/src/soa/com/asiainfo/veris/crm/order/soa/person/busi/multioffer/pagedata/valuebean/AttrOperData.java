/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean;

import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.ivalues.IPageData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: AttrOperData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-21 下午04:39:17 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-21 chengxf2 v1.0.0 修改原因
 */

public class AttrOperData implements IPageData
{

    private String attrCode;

    private String attrName;

    private String itemType = "属性";

    private IPageData parentOperData;

    public String getAttrCode()
    {
        return attrCode;
    }

    public String getAttrName()
    {
        return attrName;
    }

    public String getItemType()
    {
        return this.itemType;
    }

    public IPageData getParentPageData()
    {
        return this.parentOperData;
    }

    public void setAttrCode(String attrCode)
    {
        this.attrCode = attrCode;
    }

    public void setAttrName(String attrName)
    {
        this.attrName = attrName;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setParentPageData(IPageData parent)
    {
        this.parentOperData = parent;
    }

}
