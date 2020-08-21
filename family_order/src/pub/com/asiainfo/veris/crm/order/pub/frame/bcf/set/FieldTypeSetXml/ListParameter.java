/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.EmptyElement;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.XmlAttribute;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ListParameter.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:38:34 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class ListParameter extends EmptyElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "ListParameter";

    public static ListParameter unmarshal(Element elem)
    {
        ListParameter __objListParameter = (ListParameter) EmptyElement.unmarshal(elem, new ListParameter());
        if (__objListParameter != null)
        {
            __objListParameter.LSPRemark.setValue(elem.attributeValue("LSPRemark"));
            __objListParameter.LSPName.setValue(elem.attributeValue("LSPName"));
            __objListParameter.LSPValue.setValue(elem.attributeValue("LSPValue"));
            __objListParameter.LSPType.setValue(elem.attributeValue("LSPType"));
        }
        return __objListParameter;
    }

    public XmlAttribute LSPRemark = new XmlAttribute("LSPRemark", "NMTOKEN", "REQUIRED", "");

    public XmlAttribute LSPName = new XmlAttribute("LSPName", "CDATA", "REQUIRED", "");

    public XmlAttribute LSPValue = new XmlAttribute("LSPValue", "NMTOKEN", "REQUIRED", "");

    public XmlAttribute LSPType = new XmlAttribute("LSPType", "NMTOKEN", "REQUIRED", "");

    public ListParameter()
    {
    }

    public ListParameter(boolean state)
    {
        super(state);
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public String getLSPName()
    {
        return this.LSPName.getValue();
    }

    public String getLSPRemark()
    {
        return this.LSPRemark.getValue();
    }

    public String getLSPType()
    {
        return this.LSPType.getValue();
    }

    public String getLSPValue()
    {
        return this.LSPValue.getValue();
    }

    public Element marshal()
    {
        return null;
    }

    public void setLSPName(String value_)
    {
        this.LSPName.setValue(value_);
    }

    public void setLSPRemark(String value_)
    {
        this.LSPRemark.setValue(value_);
    }

    public void setLSPType(String value_)
    {
        this.LSPType.setValue(value_);
    }

    public void setLSPValue(String value_)
    {
        this.LSPValue.setValue(value_);
    }

}
