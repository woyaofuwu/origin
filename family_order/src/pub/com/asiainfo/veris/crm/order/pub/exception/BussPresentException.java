
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BussPresentException implements IBusiException // 礼品赠送异常类
{
    CRM_BUSSPRESENT_1("该电子卷编码已不可用!"), CRM_BUSSPRESENT_2("该电子卷活动可兑换金额不足"), CRM_BUSSPRESENT_3("电子卷活动查询报错[%s]"), CRM_BUSSPRESENT_4("该电子卷已使用或已失效，无法返销");

    private final String value;

    private BussPresentException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
