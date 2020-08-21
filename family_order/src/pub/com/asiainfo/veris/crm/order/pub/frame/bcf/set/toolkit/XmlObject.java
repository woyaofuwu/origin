/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.toolkit;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: XmlObject.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:36:46 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public abstract class XmlObject implements Serializable
{

    private static final long serialVersionUID = 1L;

    private XmlObject parent;

    public XmlObject _getParent()
    {
        return this.parent;
    }

    public void _setParent(XmlObject paramXmlObject)
    {
        this.parent = paramXmlObject;
    }

    public abstract String get_TagName();
}
