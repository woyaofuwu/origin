
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum QueryIPExpressException implements IBusiException // 
{
    CRM_IPExpress_1("无IP直通车信息！"); //

    private final String value;

    private QueryIPExpressException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
