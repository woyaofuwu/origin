
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum OpenUserException implements IBusiException // 开户异常
{
    CRM_OPENUSER_1("后付费开户业务办理失败！ 原因：%s"); //

    private final String value;

    private OpenUserException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
