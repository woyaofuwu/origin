
package com.asiainfo.veris.crm.order.soa.frame.bre.base;

/**
 * Copyright: Copyright 2014/6/28 Asiainfo-Linkage
 * 
 * @ClassName: BreBaseService
 * @Description: 规则引擎
 * @version: v1.0.0
 * @author: xiaocl
 */
public class BreBaseService implements IBREBaseService
{
    private static final long serialVersionUID = 1L;

    private String resultCode;

    private String resultInfo;

    private long resultCount;

    public BreBaseService()
    {
        resultCode = "0";
        resultInfo = "ok";
        resultCount = 0L;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public long getResultCount()
    {
        return resultCount;
    }

    public String getResultInfo()
    {
        return resultInfo;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public void setResultCount(long resultCount)
    {
        this.resultCount = resultCount;
    }

    public void setResultInfo(String resultInfo)
    {
        this.resultInfo = resultInfo;
    }

}
