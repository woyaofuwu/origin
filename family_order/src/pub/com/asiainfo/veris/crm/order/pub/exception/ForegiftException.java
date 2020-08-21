
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ForegiftException implements IBusiException
{

    CRM_FOREGIFT_1("新增用户押金资料出错!"), //
    CRM_FOREGIFT_2("更新押金发票资料失败!"), //
    CRM_FOREGIFT_3("用户其他服务资料金额总数不对!");

    private final String value;

    private ForegiftException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
