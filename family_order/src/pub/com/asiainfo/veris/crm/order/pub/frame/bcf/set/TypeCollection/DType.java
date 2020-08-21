/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.TypeCollection;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.TextElement;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlAttribute;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DType.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:35:34 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class DType extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "DType";

    public static DType unmarshal(Element elem)
    {
        DType __objDType = (DType) TextElement.unmarshal(elem, new DType());
        if (__objDType != null)
        {
            __objDType.MaxLen.setValue(elem.attributeValue("MaxLen"));
            __objDType.JavaDataType.setValue(elem.attributeValue("JavaDataType"));
            __objDType.Decimal.setValue(elem.attributeValue("Decimal"));
        }
        return __objDType;
    }

    public XmlAttribute MaxLen = new XmlAttribute("MaxLen", "NMTOKEN", "REQUIRED", "");

    public XmlAttribute JavaDataType = new XmlAttribute("JavaDataType", "CDATA", "REQUIRED", "");

    public XmlAttribute Decimal = new XmlAttribute("Decimal", "CDATA", "FIXED", "");

    public DType()
    {
    }

    public DType(String text)
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

    public String getMaxLen()
    {
        return this.MaxLen.getValue();
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

    public void setMaxLen(String value_)
    {
        this.MaxLen.setValue(value_);
    }

}
