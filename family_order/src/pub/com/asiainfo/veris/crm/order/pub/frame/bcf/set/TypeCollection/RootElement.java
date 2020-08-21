/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.TypeCollection;

import org.dom4j.Element;

import com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit.TextElement;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RootElement.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:00 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class RootElement extends TextElement
{

    private static final long serialVersionUID = 1L;

    public static String _tagName = "root-element";

    public static RootElement unmarshal(Element elem)
    {
        RootElement __objRootElement = (RootElement) TextElement.unmarshal(elem, new RootElement());
        return __objRootElement;
    }

    public RootElement()
    {
    }

    public RootElement(String text)
    {
        super(text);
    }

    public String get_TagName()
    {
        return _tagName;
    }

}
