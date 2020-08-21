/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EditTypeDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:12:54 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface EditTypeDB
{

    public static final String TYPE_DBHTML = "DBHtml";

    public abstract EditTypeDB clones();

    public abstract String getCheckValue();

    public abstract String getName();

    public abstract String getUnCheckValue();

    public abstract boolean isDBHtml();

    public abstract String toXmlString();
}
