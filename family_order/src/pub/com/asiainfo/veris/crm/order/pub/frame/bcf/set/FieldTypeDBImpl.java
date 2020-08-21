/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldTypeDBImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:32:31 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class FieldTypeDBImpl implements FieldTypeDB
{

    private String name = "";

    private String title = "";

    private EditTypeDB defaultEditType = null;

    private DataTypeDB dataType = null;

    private boolean isNull = false;

    private boolean isPk = false;

    private String defaultValue = "";

    private String displayName = "";

    private String displayValue = "";

    private boolean isEnabled = true;
    
    private boolean isWrap = false;

    private int displaySeq = 100;

    private boolean isGridVisible = true;

    private ListDataSourceDBImpl listSourceImpl = null;

    private TextDataSourceDB textSource = null;

    public DataTypeDB getDataType()
    {
        return this.dataType;
    }

    public EditTypeDB getDefaultEditType()
    {
        return this.defaultEditType;
    }

    public String getDefaultValue()
    {
        return this.defaultValue;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public boolean isWrap() {
		return isWrap;
	}

	public void setWrap(boolean isWrap) {
		this.isWrap = isWrap;
	}

	public int getDisplaySeq()
    {
        return this.displaySeq;
    }

    public String getDisplayValue()
    {
        return this.displayValue;
    }

    public boolean getIsEnabled()
    {
        return this.isEnabled;
    }

    public boolean getIsNull()
    {
        return this.isNull;
    }

    public boolean getIsPk()
    {
        return this.isPk;
    }

    public int getListDataParaCount()
    {
        if (this.listSourceImpl != null)
            return this.listSourceImpl.getParaCount();
        return 0;
    }

    public String getListDataParaName(int i)
    {
        if (this.listSourceImpl != null)
            return this.listSourceImpl.getParaName(i);
        return "";
    }

    public String getListDataParaType(int i)
    {
        if (this.listSourceImpl != null)
            return this.listSourceImpl.getParaType(i);
        return "";
    }

    public String getListDataParaValue(int i)
    {
        if (this.listSourceImpl != null)
            return this.listSourceImpl.getParaValue(i);
        return "";
    }

    public ListDataSourceDB getListDataSource()
    {
        return this.listSourceImpl;
    }

    public String getName()
    {
        return this.name.toUpperCase();
    }

    public String getOrignialTitle()
    {
        return this.title;
    }

    public TextDataSourceDB getTextDataSource()
    {
        return this.textSource;
    }

    public String getTitle()
    {
        return this.title;
    }

    public boolean isGridVisible()
    {
        return this.isGridVisible;
    }

    public void setDataType(DataTypeDB aDataType)
    {
        this.dataType = aDataType;
    }

    public void setDataType(String aTypeName, String aMaxLen, String aDec)
    {
        this.dataType = new DataTypeDBImpl(aTypeName, aMaxLen, aDec);
    }

    public void setDefaultEditType(EditTypeDB aEditType)
    {
        this.defaultEditType = aEditType;
    }

    public boolean setDefaultEditType(String aEditTypeName)
    {
        this.defaultEditType = new EditTypeDBImpl(aEditTypeName);
        return true;
    }

    public void setDefaultValue(String aValue)
    {
        this.defaultValue = ((aValue == null) ? "" : aValue);
    }

    public void setDisplayName(String aAttrName)
    {
        this.displayName = ((aAttrName == null) ? "" : aAttrName);
    }

    public void setDisplaySeq(int aSeq)
    {
        this.displaySeq = aSeq;
    }

    public void setDisplayValue(String aDisplayValue)
    {
        this.displayValue = ((aDisplayValue == null) ? "" : aDisplayValue);
    }

    public void setGridVisible(boolean aVisibleFlag)
    {
        this.isGridVisible = aVisibleFlag;
    }

    public void setIsEnabled(boolean aFlag)
    {
        this.isEnabled = aFlag;
    }

    public void setIsNull(boolean aFlag)
    {
        this.isNull = aFlag;
    }

    public void setIsPk(boolean aFlag)
    {
        this.isPk = aFlag;
    }

    public void setListDataSource(ListDataSourceDB aListSource)
    {
        if (aListSource != null)
            this.listSourceImpl = ((ListDataSourceDBImpl) aListSource);
        else
            this.listSourceImpl = null;
    }

    public void setName(String aName)
    {
        this.name = ((aName == null) ? "" : aName);
    }

    public void setTextDataSource(TextDataSourceDB textDataSource)
    {
        this.textSource = textDataSource;
    }

    public void setTitle(String aTitle)
    {
        this.title = ((aTitle == null) ? "" : aTitle);
    }

    public String toXmlString()
    {
        String sRe = "";
        return sRe;
    }

}
