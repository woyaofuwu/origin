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
 * @ClassName: EditType.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:37:23 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class EditType extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "EditType";

    public static EditType unmarshal(Element elem)
    {
        EditType __objEditType = (EditType) TextElement.unmarshal(elem, new EditType());
        if (__objEditType != null)
        {
            __objEditType.CheckValue.setValue(elem.attributeValue("CheckValue"));
            __objEditType.UnCheckValue.setValue(elem.attributeValue("UnCheckValue"));
        }
        return __objEditType;
    }

    public XmlAttribute CheckValue = new XmlAttribute("CheckValue", "CDATA", "IMPLIED", "");

    public XmlAttribute UnCheckValue = new XmlAttribute("UnCheckValue", "CDATA", "IMPLIED", "");

    public EditType()
    {
    }

    public EditType(String text)
    {
        super(text);
    }

    public String get_TagName()
    {
        return _tagName;
    }

    public String getCheckValue()
    {
        return this.CheckValue.getValue();
    }

    public String getUnCheckValue()
    {
        return this.UnCheckValue.getValue();
    }

    public Element marshal()
    {
        return null;
    }

    public void setCheckValue(String value_)
    {
        this.CheckValue.setValue(value_);
    }

    public void setUnCheckValue(String value_)
    {
        this.UnCheckValue.setValue(value_);
    }

}
