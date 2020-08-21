
package com.asiainfo.veris.crm.iorder.pub.welfare.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum WelfareException implements IBusiException
{
    CRM_WELFARE_1("权益自有商品集合[ELEMENTS]或[DEL_WELFARE_OFFER_CODE]不能同时为空！"),
    CRM_WELFARE_2("商品[%s]生效时间[START_DATE]不能为空！"),
    CRM_WELFARE_3("商品[%s]失效时间[END_DATE]不能为空！"),
    CRM_WELFARE_4("权益订单流水[WELFARE_ORDER_ID]不能为空！"),
    CRM_WELFARE_5("前置订单流水[ADVANCE_TRADE_ID]不能为空！"),
    CRM_WELFARE_6("商品 %s 非权益关联订购，不能退订！"),
    CRM_WELFARE_7("权益自有商品集合[ELEMENTS]中[WELFARE_OFFER_CODE]不能为空！"),
    CRM_WELFARE_8("权益包退订[MOD_END_DATE]不能为空！"),
    CRM_WELFARE_9("权益包失效时间[END_DATE]不能小于生效时间[START_DATE]！"),
    CRM_WELFARE_10("[%s]"),;

    private final String value;

    WelfareException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
