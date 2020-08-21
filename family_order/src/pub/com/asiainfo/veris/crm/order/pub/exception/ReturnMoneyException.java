
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ReturnMoneyException implements IBusiException // 订单异常
{
    CRM_ReturnInfo_01("该用户办理信息中无开户历史，请核实！"), //
    CRM_ReturnInfo_02("该用户已经办理了酬金返还业务！"), //
    CRM_ReturnInfo_03("调用一级BOSS出错！"), //
    CRM_ReturnInfo_04("没有佣金返回特权，不允许跨天操作酬金返还业务！"), //
    CRM_ReturnInfo_05("请选择酬金返还金额！"), //
    CRM_ReturnInfo_06("请选择酬金返还参与原因！");

    private final String value;

    private ReturnMoneyException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
