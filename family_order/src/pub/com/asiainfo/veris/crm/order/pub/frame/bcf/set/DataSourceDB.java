/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DataSourceDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-3 上午08:28:23 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-3 chengxf2 v1.0.0 修改原因
 */

public interface DataSourceDB
{

    public abstract void addPara(String aName, String aType, String aValue);

    public abstract int getParaCount();

    public abstract String getParaName(int i);

    public abstract String getParaType(int i);

    public abstract String getParaValue(int i);
}
