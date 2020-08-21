/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.util;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ITreeNodeInfo.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:34:59 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public interface ITreeNodeInfo
{

    public abstract int getChildCount();

    public abstract String getChildRowIndexs();

    public abstract String getId();

    public abstract int getLevel();

    public abstract String getParentId();

    public abstract void setChildCount(int paramInt);

    public abstract void setChildRowIndexs(String paramString);

    public abstract void setLevel(int paramInt);
}
