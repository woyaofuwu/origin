
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum AutoPayException implements IBusiException
{
    CRM_AUTO_1("获取该用户资料失败！"); //

    private final String value;

    private AutoPayException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
