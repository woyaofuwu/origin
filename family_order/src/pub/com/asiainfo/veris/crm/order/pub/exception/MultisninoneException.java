
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum MultisninoneException implements IBusiException
{

    CRM_MULTISNINONE_1("副号码数量不能为空！"), ;

    private final String value;

    private MultisninoneException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
