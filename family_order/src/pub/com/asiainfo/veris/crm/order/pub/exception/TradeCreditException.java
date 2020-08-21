
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeCreditException implements IBusiException
{
    CRM_TRADECREDIT_1("根据订单标识[TRADE_ID=%s]查询信誉度子台账不存在"), CRM_TRADECREDIT_999("系统未完工单积压较多,暂停执行！"); // 999是和信控约定好的错误编码，请勿随意改动

    private final String value;

    private TradeCreditException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
