
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ModifySaleActiveIMEIException implements IBusiException // 多账期异常
{
    CRM_MODIFYSALEACTIVEIMEI_1("用户无购机活动数据！"), //
    CRM_MODIFYSALEACTIVEIMEI_2("用户无对应的购机活动数据，更新失败！"), ;

    private final String value;

    private ModifySaleActiveIMEIException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
