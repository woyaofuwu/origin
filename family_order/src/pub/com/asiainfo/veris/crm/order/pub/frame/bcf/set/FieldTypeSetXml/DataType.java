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
 * @ClassName: DataType.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:58 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class DataType extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "DataType";

    public static DataType unmarshal(Element elem)
    {
        DataType __objDataType = (DataType) TextElement.unmarshal(elem, new DataType());
        if (__objDataType != null)
        {
            __objDataType.JavaDataType.setValue(elem.attributeValue("JavaDataType"));
            __objDataType.MaxLength.setValue(elem.attributeValue("MaxLength"));
            __objDataType.Decimal.setValue(elem.attributeValue("Decimal"));
        }
        return __objDataType;
    }

    public XmlAttribute JavaDataType = new XmlAttribute("JavaDataType", "NMTOKEN", "IMPLIED", "");

    public XmlAttribute MaxLength = new XmlAttribute("MaxLength", "NMTOKEN", "IMPLIED", "");

    public XmlAttribute Decimal = new XmlAttribute("Decimal", "NMTOKEN", "IMPLIED", "");

    public DataType()
    {
    }

    public DataType(String text)
    {
        super(text);
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public String getDecimal()
    {
        return this.Decimal.getValue();
    }

    public String getJavaDataType()
    {
        return this.JavaDataType.getValue();
    }

    public String getMaxLength()
    {
        return this.MaxLength.getValue();
    }

    public Element marshal()
    {
        return null;
    }

    public void setDecimal(String value_)
    {
        this.Decimal.setValue(value_);
    }

    public void setJavaDataType(String value_)
    {
        this.JavaDataType.setValue(value_);
    }

    public void setMaxLength(String value_)
    {
        this.MaxLength.setValue(value_);
    }

}
