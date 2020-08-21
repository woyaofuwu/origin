/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlAttribute;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlObject;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: Field.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:37:32 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class Field extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "Field";

    @SuppressWarnings("unchecked")
    public static Field unmarshal(Element elem)
    {
        if (elem == null)
        {
            return null;
        }
        Field __objField = new Field();
        if (__objField != null)
        {
            __objField.GridVisible.setValue(elem.attributeValue("GridVisible"));
            __objField.DisplaySeq.setValue(elem.attributeValue("DisplaySeq"));
            __objField.IsEnabled.setValue(elem.attributeValue("IsEnabled"));
        }

        Iterator it = elem.elementIterator();
        while (it.hasNext())
        {
            Object __obj = it.next();
            if (__obj instanceof Element)
            {
                Element __e = (Element) __obj;
                String __name = __e.getName();
                if (__name.equals(Name._tagName))
                {
                    Name __objName = Name.unmarshal(__e);
                    __objField.setName(__objName);
                }
                if (__name.equals(DisplayName._tagName))
                {
                    DisplayName __objDisplayName = DisplayName.unmarshal(__e);
                    __objField.setDisplayName(__objDisplayName);
                }
                if (__name.equals(Title._tagName))
                {
                    Title __objTitle = Title.unmarshal(__e);
                    __objField.setTitle(__objTitle);
                }
                if (__name.equals(EditType._tagName))
                {
                    EditType __objEditType = EditType.unmarshal(__e);
                    __objField.setEditType(__objEditType);
                }
                if (__name.equals(DataType._tagName))
                {
                    DataType __objDataType = DataType.unmarshal(__e);
                    __objField.setDataType(__objDataType);
                }
                if (__name.equals(IsNull._tagName))
                {
                    IsNull __objIsNull = IsNull.unmarshal(__e);
                    __objField.setIsNull(__objIsNull);
                }
                if (__name.equals(IsPk._tagName))
                {
                    IsPk __objIsPk = IsPk.unmarshal(__e);
                    __objField.setIsPk(__objIsPk);
                }
                if (__name.equals(ListDataSource._tagName))
                {
                    ListDataSource __objListDataSource = ListDataSource.unmarshal(__e);
                    __objField.setListDataSource(__objListDataSource);
                }
                if (__name.equals(ChildListDataSource._tagName))
                {
                    ChildListDataSource __objChildListDataSource = ChildListDataSource.unmarshal(__e);
                    __objField.setChildListDataSource(__objChildListDataSource);
                }
                if (__name.equals(DefaultValue._tagName))
                {
                    DefaultValue __objDefaultValue = DefaultValue.unmarshal(__e);
                    __objField.setDefaultValue(__objDefaultValue);
                }
            }
        }
        return __objField;
    }

    public XmlAttribute GridVisible = new XmlAttribute("GridVisible", "NMTOKEN", "IMPLIED", "");

    public XmlAttribute DisplaySeq = new XmlAttribute("DisplaySeq", "NMTOKEN", "IMPLIED", "");

    public XmlAttribute IsEnabled = new XmlAttribute("IsEnabled", "NMTOKEN", "IMPLIED", "");

    protected Name _objName;

    protected DisplayName _objDisplayName;

    protected Title _objTitle;

    protected EditType _objEditType;

    protected DataType _objDataType;

    protected IsNull _objIsNull;

    protected IsPk _objIsPk;

    protected DefaultValue _objDefaultValue;

    protected ListDataSource _objListDataSource;

    protected ChildListDataSource _objChildListDataSource;

    public List<Object> _getChildren()
    {
        List<Object> children = new ArrayList<Object>();

        if (this._objName != null)
        {
            children.add(this._objName);
        }
        if (this._objDisplayName != null)
        {
            children.add(this._objDisplayName);
        }
        if (this._objTitle != null)
        {
            children.add(this._objTitle);
        }
        if (this._objEditType != null)
        {
            children.add(this._objEditType);
        }
        if (this._objDataType != null)
        {
            children.add(this._objDataType);
        }
        if (this._objIsNull != null)
        {
            children.add(this._objIsNull);
        }
        if (this._objIsPk != null)
        {
            children.add(this._objIsPk);
        }
        if (this._objListDataSource != null)
        {
            children.add(this._objListDataSource);
        }
        if (this._objChildListDataSource != null)
        {
            children.add(this._objChildListDataSource);
        }
        if (this._objDefaultValue != null)
        {
            children.add(this._objDefaultValue);
        }
        return children;
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public ChildListDataSource getChildListDataSource()
    {
        return this._objChildListDataSource;
    }

    public DataType getDataType()
    {
        return this._objDataType;
    }

    public String getDataTypeText()
    {
        return ((this._objDataType == null) ? null : this._objDataType.getText());
    }

    public DefaultValue getDefaultValue()
    {
        return this._objDefaultValue;
    }

    public String getDefaultValueText()
    {
        return ((this._objDefaultValue == null) ? null : this._objDefaultValue.getText());
    }

    public DisplayName getDisplayName()
    {
        return this._objDisplayName;
    }

    public String getDisplayNameText()
    {
        return ((this._objDisplayName == null) ? null : this._objDisplayName.getText());
    }

    public String getDisplaySeq()
    {
        return this.DisplaySeq.getValue();
    }

    public EditType getEditType()
    {
        return this._objEditType;
    }

    public String getEditTypeText()
    {
        return ((this._objEditType == null) ? null : this._objEditType.getText());
    }

    public String getGridVisible()
    {
        return this.GridVisible.getValue();
    }

    public String getIsEnabled()
    {
        return this.IsEnabled.getValue();
    }

    public IsNull getIsNull()
    {
        return this._objIsNull;
    }

    public String getIsNullText()
    {
        return ((this._objIsNull == null) ? null : this._objIsNull.getText());
    }

    public IsPk getIsPk()
    {
        return this._objIsPk;
    }

    public String getIsPkText()
    {
        return ((this._objIsPk == null) ? null : this._objIsPk.getText());
    }

    public ListDataSource getListDataSource()
    {
        return this._objListDataSource;
    }

    public Name getName()
    {
        return this._objName;
    }

    public String getNameText()
    {
        return ((this._objName == null) ? null : this._objName.getText());
    }

    public Title getTitle()
    {
        return this._objTitle;
    }

    public String getTitleText()
    {
        return ((this._objTitle == null) ? null : this._objTitle.getText());
    }

    public Element marshal()
    {
        return null;
    }

    public void setChildListDataSource(ChildListDataSource obj)
    {
        this._objChildListDataSource = obj;
    }

    public void setDataType(DataType obj)
    {
        this._objDataType = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setDataTypeText(String text)
    {
        if (text == null)
        {
            this._objDataType = null;
            return;
        }

        if (this._objDataType == null)
        {
            this._objDataType = new DataType();
        }
        this._objDataType.setText(text);
        this._objDataType._setParent(this);
    }

    public void setDefaultValue(DefaultValue obj)
    {
        this._objDefaultValue = obj;
        if (obj == null)
        {
            return;
        }
    }

    public void setDefaultValueText(String text)
    {
        if (text == null)
        {
            this._objDefaultValue = null;
            return;
        }

        if (this._objDefaultValue == null)
        {
            this._objDefaultValue = new DefaultValue();
        }
        this._objDefaultValue.setText(text);
    }

    public void setDisplayName(DisplayName obj)
    {
        this._objDisplayName = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setDisplayNameText(String text)
    {
        if (text == null)
        {
            this._objDisplayName = null;
            return;
        }

        if (this._objDisplayName == null)
        {
            this._objDisplayName = new DisplayName();
        }
        this._objDisplayName.setText(text);
        this._objDisplayName._setParent(this);
    }

    public void setDisplaySeq(String value_)
    {
        this.DisplaySeq.setValue(value_);
    }

    public void setEditType(EditType obj)
    {
        this._objEditType = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setEditTypeText(String text)
    {
        if (text == null)
        {
            this._objEditType = null;
            return;
        }

        if (this._objEditType == null)
        {
            this._objEditType = new EditType();
        }
        this._objEditType.setText(text);
        this._objEditType._setParent(this);
    }

    public void setGridVisible(String value_)
    {
        this.GridVisible.setValue(value_);
    }

    public void setIsEnabled(String value_)
    {
        this.IsEnabled.setValue(value_);
    }

    public void setIsNull(IsNull obj)
    {
        this._objIsNull = obj;
        if (obj == null)
        {
            return;
        }
    }

    public void setIsNullText(String text)
    {
        if (text == null)
        {
            this._objIsNull = null;
            return;
        }

        if (this._objIsNull == null)
        {
            this._objIsNull = new IsNull();
        }
        this._objIsNull.setText(text);
        this._objIsNull._setParent(this);
    }

    public void setIsPk(IsPk obj)
    {
        this._objIsPk = obj;
    }

    public void setIsPkText(String text)
    {
        if (text == null)
        {
            this._objIsPk = null;
            return;
        }

        if (this._objIsPk == null)
        {
            this._objIsPk = new IsPk();
        }
        this._objIsPk.setText(text);
    }

    public void setListDataSource(ListDataSource obj)
    {
        this._objListDataSource = obj;
    }

    public void setName(Name obj)
    {
        this._objName = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setNameText(String text)
    {
        if (text == null)
        {
            this._objName = null;
            return;
        }

        if (this._objName == null)
        {
            this._objName = new Name();
        }
        this._objName.setText(text.toUpperCase());
    }

    public void setTitle(Title obj)
    {
        this._objTitle = obj;
        if (obj == null)
        {
            return;
        }
        obj._setParent(this);
    }

    public void setTitleText(String text)
    {
        if (text == null)
        {
            this._objTitle = null;
            return;
        }

        if (this._objTitle == null)
        {
            this._objTitle = new Title();
        }
        this._objTitle.setText(text);
        this._objTitle._setParent(this);
    }

}
