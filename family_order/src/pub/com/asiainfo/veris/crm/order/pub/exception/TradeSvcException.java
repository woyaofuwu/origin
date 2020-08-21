
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeSvcException implements IBusiException
{
    CRM_TRADESVC_1("获取台帐服务资料:没有该笔业务[%s]！"), //
    CRM_TRADESVC_2("获取用户服务出错！"), //
    CRM_TRADESVC_3("稽核用户服务信息：没有该服务！"), //
    CRM_TRADESVC_4("用户原基本产品不包含现在已经生效的通话或漫游级别！");

    private final String value;

    private TradeSvcException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
