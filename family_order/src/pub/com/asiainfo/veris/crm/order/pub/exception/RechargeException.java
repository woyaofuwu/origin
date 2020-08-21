
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum RechargeException implements IBusiException
{

    CRM_RECHARGE_549112("手机号码长度必须为11位数字!"), CRM_RECHARGE_549113("充值卡密码只能为15-20位数字!"), CRM_RECHARGE_549114("手机号码长度必须为11位数字!"), CRM_RECHARGE_549115("充值卡密码只能为15-20位数字!"), CRM_RECHARGE_549116("没有配置密码错误限制参数!"), CRM_RECHARGE_1013("周期内密码输入次数超过限制！"),
    CRM_RECHARGE_549117("没有配置代充值次数限制参数！"),CRM_RECHARGE_549118("没有配置充值失败下发短信内容！"), CRM_RECHARGE_1006("周期内代充值次数超过限制！"), CRM_RECHARGE_800001("无法找到被充值号码资料！"), CRM_RECHARGE_800002("被充值号码非本省用户！"), CRM_RECHARGE_800003("【%s】"), CRM_RECHARGE_800004("【%s】"),
    CRM_RECHARGE_1001("充值卡不存在"),
    CRM_RECHARGE_1002("充值卡已充值"),
    CRM_RECHARGE_1003("充值卡状态异常（未激活、加锁、失效等）"),
    CRM_RECHARGE_1004("充值系统执行错误"),
    CRM_RECHARGE_1005("充值用户进入黑名单"),
    CRM_RECHARGE_1007("充值用户号段不存在"),
    CRM_RECHARGE_1008("boss返回超时"),
    CRM_RECHARGE_1009("操作不成功（包括boss返回失败）"),
    CRM_RECHARGE_1010("业务数据未配置"),
    CRM_RECHARGE_1011("映射表数据配置错误"),
    CRM_RECHARGE_1012("被充值用户归属SCP返回超时");

    private final String value;

    private RechargeException(String value)
    {

        this.value = value;
    }

    @Override
    public String getValue()
    {

        return value;
    }
}
