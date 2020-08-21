/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.TextElement;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ID.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:38:01 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class ID extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "ID";

    public static ID unmarshal(Element elem)
    {
        ID __objID = (ID) TextElement.unmarshal(elem, new ID());
        return __objID;
    }

    public ID()
    {
    }

    public ID(String text)
    {
        super(text);
    }

    public String get_TagName()
    {
        return _tagName;
    }

}
