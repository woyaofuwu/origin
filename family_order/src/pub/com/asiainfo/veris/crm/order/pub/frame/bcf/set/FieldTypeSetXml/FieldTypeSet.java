/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlAttribute;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlObject;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldTypeSet.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:37:52 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class FieldTypeSet extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "FieldTypeSet";

    @SuppressWarnings("unchecked")
    public static FieldTypeSet unmarshal(Element elem)
    {
        if (elem == null)
        {
            return null;
        }
        FieldTypeSet __objFieldTypeSet = new FieldTypeSet();
        if (__objFieldTypeSet != null)
        {
            __objFieldTypeSet.Name.setValue(elem.attributeValue("Name"));
            __objFieldTypeSet.MainField.setValue(elem.attributeValue("MainField"));
        }

        Iterator it = elem.elementIterator();
        while (it.hasNext())
        {
            Object __obj = it.next();
            if (__obj instanceof Element)
            {
                Element __e = (Element) __obj;
                String __name = __e.getName();
                if (__name.equals(FieldList._tagName))
                {
                    FieldList __objFieldList = FieldList.unmarshal(__e);
                    __objFieldTypeSet.setFieldList(__objFieldList);
                }
            }
        }
        return __objFieldTypeSet;
    }

    public static FieldTypeSet unmarshal(InputStream in) throws DocumentException
    {
        return unmarshal(new SAXReader().read(in).getRootElement());
    }

    public static FieldTypeSet unmarshal(Reader reader) throws DocumentException
    {
        return unmarshal(new SAXReader().read(reader).getRootElement());
    }

    public XmlAttribute Name = new XmlAttribute("Name", "NMTOKEN", "REQUIRED", "");

    public XmlAttribute MainField = new XmlAttribute("MainField", "NMTOKEN", "REQUIRED", "");

    protected FieldList _objFieldList;

    public List<FieldList> _getChildren()
    {
        List<FieldList> children = new ArrayList<FieldList>();

        if (this._objFieldList != null)
        {
            children.add(this._objFieldList);
        }
        return children;
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public FieldList getFieldList()
    {
        return this._objFieldList;
    }

    public String getMainField()
    {
        return this.MainField.getValue();
    }

    public String getName()
    {
        return this.Name.getValue();
    }

    public Element marshal()
    {
        return null;
    }

    public void setFieldList(FieldList obj)
    {
        this._objFieldList = obj;
        if (obj == null)
        {
            return;
        }
    }

    public void setMainField(String value_)
    {
        this.MainField.setValue(value_);
    }

    public void setName(String value_)
    {
        this.Name.setValue(value_);
    }

}
