/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ListDataSourceDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:33:20 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface ListDataSourceDB extends DataSourceDB
{

    public abstract String getListener();

    public abstract String getTextAttr();

    public abstract String getValueAttr();

    public abstract void setListener(String listener);

    public abstract void setTextAttr(String textAttr);

    public abstract void setValueAttr(String valueAttr);
}
