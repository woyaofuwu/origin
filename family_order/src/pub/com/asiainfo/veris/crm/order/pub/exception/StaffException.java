
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum StaffException implements IBusiException // 员工异常
{
    CRM_STAFF_1("%s,无法查询到员工[%s]的资料"), CRM_STAFF_2("员工工号和部门编号不能同时为空"), CRM_STAFF_3("员工工号[%s]无权操作产品[%s]的权限!");

    private final String value;

    private StaffException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
