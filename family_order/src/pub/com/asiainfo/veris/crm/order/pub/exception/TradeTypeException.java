
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeTypeException implements IBusiException
{
    CRM_TRADETYPE_1("业务类型[%s]不存在！"), CRM_TRADETYPE_2("AuthCheck 没有可用的密码验证方式!  请检查TD_S_TRADETYPE表的IDENTITY_CHECK_TAG字段");

    private final String value;

    private TradeTypeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
