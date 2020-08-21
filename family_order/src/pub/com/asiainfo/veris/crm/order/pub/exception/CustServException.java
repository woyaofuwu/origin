
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CustServException implements IBusiException // 客服异常
{
    CRM_CUSTSERV_1("IVR拨打记录查询操作失败！<br>%s"); //

    private final String value;

    private CustServException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
