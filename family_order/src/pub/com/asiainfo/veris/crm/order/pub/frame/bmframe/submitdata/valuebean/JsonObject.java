/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.IJsonObject;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: JsonObject.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-25 上午11:35:23 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
 */

public class JsonObject implements IJsonObject
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String name;

    private String jsonStr;

    public String getName()
    {
        return this.name;
    }

    public IData getValue()
    {
        return new DataMap(this.jsonStr);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(String value)
    {
        this.jsonStr = value;
    }
}
