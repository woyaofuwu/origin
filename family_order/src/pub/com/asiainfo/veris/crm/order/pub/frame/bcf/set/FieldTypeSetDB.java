/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldTypeSetDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:32:40 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface FieldTypeSetDB
{

    public abstract boolean addField(FieldTypeDB aField);

    public abstract FieldTypeDB getField(int paramInt);

    public abstract FieldTypeDB getField(String paramString);

    public abstract int getFieldCount();

    public abstract FieldTypeDB[] getFieldList();

    public abstract String getMainField();

    public abstract String getName();

    public abstract FieldTypeDB[] getPkField();

    public abstract boolean isFKFieldExist();
}
