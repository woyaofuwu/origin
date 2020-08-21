/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EditTypeDBImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:13:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class EditTypeDBImpl implements EditTypeDB
{

    private String name;

    private boolean m_isDBHtml = false;

    private String checkValue;

    private String unCheckValue;

    public EditTypeDBImpl()
    {
    }

    public EditTypeDBImpl(String aName)
    {
        this.name = aName;
        this.m_isDBHtml = "DBHtml".equalsIgnoreCase(aName);
    }

    public EditTypeDB clones()
    {
        EditTypeDB reObj = new EditTypeDBImpl();
        return reObj;
    }

    public String getCheckValue()
    {
        return this.checkValue;
    }

    public String getName()
    {
        return this.name;
    }

    public String getUnCheckValue()
    {
        return this.unCheckValue;
    }

    public boolean isDBHtml()
    {
        return this.m_isDBHtml;
    }

    public void setCheckValue(String aValue)
    {
        this.checkValue = aValue;
    }

    public boolean setName(String aName)
    {
        this.name = aName;
        this.m_isDBHtml = "DBHtml".equalsIgnoreCase(aName);
        return true;
    }

    public void setUnCheckValue(String aValue)
    {
        this.unCheckValue = aValue;
    }

    public String toXmlString()
    {
        return null;
    }

}
