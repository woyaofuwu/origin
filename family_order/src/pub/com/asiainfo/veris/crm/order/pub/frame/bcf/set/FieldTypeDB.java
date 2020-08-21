/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldTypeDB.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:32:21 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface FieldTypeDB
{

    public abstract DataTypeDB getDataType();

    public abstract EditTypeDB getDefaultEditType();

    public abstract String getDefaultValue();

    public abstract String getDisplayName();

    public abstract int getDisplaySeq();

    public abstract String getDisplayValue();

    public abstract boolean getIsEnabled();
    
    public abstract boolean isWrap();

    public abstract boolean getIsNull();

    public abstract boolean getIsPk();

    public abstract ListDataSourceDB getListDataSource();

    public abstract String getName();

    public abstract TextDataSourceDB getTextDataSource();

    public abstract String getTitle();

    public abstract boolean isGridVisible();

    public abstract boolean setDefaultEditType(String paramString);

    public abstract String toXmlString();
}
