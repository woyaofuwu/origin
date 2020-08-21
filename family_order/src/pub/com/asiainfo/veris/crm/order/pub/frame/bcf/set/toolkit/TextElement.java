/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit;

import org.dom4j.Element;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TextElement.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:28 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public abstract class TextElement extends XmlObject
{

    private static final long serialVersionUID = 1L;

    public static TextElement unmarshal(Element paramElement, TextElement paramTextElement)
    {
        if ((paramElement == null) || (!(paramElement.getName().equals(paramTextElement.get_TagName()))))
        {
            return null;
        }
        paramTextElement.setText(paramElement.getText());
        return paramTextElement;
    }

    private String text;

    public TextElement()
    {
    }

    public TextElement(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String paramString)
    {
        this.text = paramString;
    }
}
