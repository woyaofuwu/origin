
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeAuditException implements IBusiException // 员工异常
{
    CRM_TRADEAUDIT_1("获取稽核批次号信息出错！"), //

    CRM_TRADEAUDIT_2("插入工单稽核信息失败!"); //

    private final String value;

    private TradeAuditException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
