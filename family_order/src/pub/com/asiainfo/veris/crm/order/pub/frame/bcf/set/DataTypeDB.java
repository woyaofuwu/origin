/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DataTypeDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 上午11:08:57 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface DataTypeDB
{

    public abstract DataTypeDB clones();

    public abstract String getDecimal();

    public abstract String getJavaDataType();

    public abstract String getMaxLength();

    public abstract String getName();

    public abstract String toXmlString();
}
