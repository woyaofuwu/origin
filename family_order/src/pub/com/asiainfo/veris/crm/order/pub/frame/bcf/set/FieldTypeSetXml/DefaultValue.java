/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.TextElement;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlAttribute;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DefaultValue.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:37:06 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class DefaultValue extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "DefaultValue";

    public static DefaultValue unmarshal(Element elem)
    {
        DefaultValue __objDefaultValue = (DefaultValue) TextElement.unmarshal(elem, new DefaultValue());
        if (__objDefaultValue != null)
        {
            __objDefaultValue.ID.setValue(elem.attributeValue("ID"));
        }
        return __objDefaultValue;
    }

    public XmlAttribute ID = new XmlAttribute("ID", "NMTOKEN", "REQUIRED", "");

    public DefaultValue()
    {
    }

    public DefaultValue(String text)
    {
        super(text);
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public String getID()
    {
        return this.ID.getValue();
    }

    public Element marshal()
    {
        return null;
    }

    public void setID(String value_)
    {
        this.ID.setValue(value_);
    }

}
