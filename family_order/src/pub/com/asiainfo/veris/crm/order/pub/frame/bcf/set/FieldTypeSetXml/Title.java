/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.TextElement;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: Title.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:38:49 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class Title extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "Title";

    public static Title unmarshal(Element elem)
    {
        Title __objTitle = (Title) TextElement.unmarshal(elem, new Title());
        return __objTitle;
    }

    public Title()
    {
    }

    public Title(String text)
    {
        super(text);
    }

    public String get_TagName()
    {
        return _tagName;
    }
}
