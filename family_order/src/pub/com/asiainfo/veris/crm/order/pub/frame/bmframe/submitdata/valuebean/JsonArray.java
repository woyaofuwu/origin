/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.IJsonArray;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: JsonArray.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-25 上午11:35:35 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
 */

public class JsonArray implements IJsonArray
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

    public IDataset getValue()
    {
        return new DatasetList(this.jsonStr);
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
