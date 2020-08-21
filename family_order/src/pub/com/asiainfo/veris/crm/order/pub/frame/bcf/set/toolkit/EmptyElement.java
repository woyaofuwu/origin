/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit;

import org.dom4j.Element;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EmptyElement.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:21 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public abstract class EmptyElement extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static EmptyElement unmarshal(Element paramElement, EmptyElement paramEmptyElement)
    {
        if (paramElement == null || !paramElement.getName().equals(paramEmptyElement.get_TagName()))
        {
            return null;
        }
        paramEmptyElement.setState(true);
        return paramEmptyElement;
    }

    private boolean state;

    public EmptyElement()
    {
        this.state = true;
    }

    public EmptyElement(boolean paramBoolean)
    {
        this.state = paramBoolean;
    }

    public boolean isOn()
    {
        return this.state;
    }

    public void setState(boolean paramBoolean)
    {
        this.state = paramBoolean;
    }
}
