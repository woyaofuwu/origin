/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TextDataSourceDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-2 上午10:49:56 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-2 chengxf2 v1.0.0 修改原因
 */

public interface TextDataSourceDB extends DataSourceDB
{

    public abstract String getMethodName();

    public abstract void setMethodName(String methodName);

}
