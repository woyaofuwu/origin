package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum OneKeyStopMobileException implements IBusiException
{

    CRM_CHANGEPHONE_1("号码[%s]没有用户信息！"),CRM_CHANGEPHONE_2("号码[%s]没有客户信息！"),CRM_CHANGEPHONE_3("号码[%s]没有账户信息！");

    private final String value;

    private OneKeyStopMobileException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
