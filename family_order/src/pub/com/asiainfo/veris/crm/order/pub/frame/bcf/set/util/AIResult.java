/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.util;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: AIResult.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:33:43 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface AIResult
{

    public abstract void close() throws Exception;

    public abstract int getChildCount();

    public abstract String getChildRowIndexs();

    public abstract String getId();

    public abstract int getLevel();

    public abstract int getLineNum();

    public abstract String getParentId();

    public abstract String getValue(String paramString) throws Exception;

    public abstract boolean ifLastChild();

    public abstract boolean ifOffspringOfRoot();

    public abstract boolean isFold();

    public abstract boolean lastNode();

    public abstract boolean next() throws Exception;
}
