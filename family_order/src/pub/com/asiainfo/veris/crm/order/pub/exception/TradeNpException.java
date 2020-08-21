
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeNpException implements IBusiException
{
    CRM_TRADENP_1("根据号码：%s查询运营商归属失败！"), //
    CRM_TRADENP_1000("预留不能使用本异常编码"); //

    private final String value;

    private TradeNpException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
