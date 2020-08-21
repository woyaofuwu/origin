
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum GetSaleActiveGiftException implements IBusiException // 多账期异常
{
    CRM_GETSALEACTIVEGIFT_1("用户无可领取的礼品！"), //
    CRM_GETSALEACTIVEGIFT_2("该礼品无法领取，请重新查询后再选择！"), CRM_GETSALEACTIVEGIFT_3("该用户尚未办理预存话费送礼品业务！"), CRM_GETSALEACTIVEGIFT_4("未选择礼品或关联订单为空！"), ;

    private final String value;

    private GetSaleActiveGiftException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
