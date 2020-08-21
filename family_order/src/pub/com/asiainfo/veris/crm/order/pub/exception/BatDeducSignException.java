
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BatDeducSignException implements IBusiException // 批扣异常
{
    CRM_BATDEDUCSIGN_1("查询用户批扣历史信息失败！接口返回信息为：%s"), //
    CRM_BATDEDUCSIGN_2("查询用户批扣签约信息失败！接口返回信息为：%s"); //

    private final String value;

    private BatDeducSignException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
