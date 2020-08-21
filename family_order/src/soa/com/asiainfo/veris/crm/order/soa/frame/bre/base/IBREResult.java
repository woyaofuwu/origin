
package com.asiainfo.veris.crm.order.soa.frame.bre.base;

/**
 * Copyright: Copyright 2014/6/27 Asiainfo-Linkage
 * 
 * @ClassName: IBREResult
 * @Description: 规则引擎
 * @version: v1.0.0
 * @author: xiaocl
 */
public interface IBREResult
{
    public abstract String getResultCode();

    public abstract long getResultCount();

    public abstract String getResultInfo();

    public abstract void setResultCode(String s);

    public abstract void setResultCount(long l);

    public abstract void setResultInfo(String s);
}
