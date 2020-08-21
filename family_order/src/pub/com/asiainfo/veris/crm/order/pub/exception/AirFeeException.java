
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum AirFeeException implements IBusiException // 空充异常
{
    CRM_AIRFEE_1("用户无空中充值或手机支付交易"), //
    CRM_AIRFEE_10("空中充值代理商身份效验不成功！返回信息为：%s"), //
    CRM_AIRFEE_11("空中充值手机支付绑定不成功！返回信息为：%s"), // 
    CRM_AIRFEE_12("手机支付平台内部错误,等待平台故障恢复后再继续交易！"), //
    CRM_AIRFEE_13("空中充值手机支付查询不成功！返回信息为：%s"), //
    CRM_AIRFEE_14("空中充值手机支付解除绑定不成功！返回信息为：%s"), //
    CRM_AIRFEE_15("您已经办理了：手机支付业务，取消后才可申请备卡！"), //
    CRM_AIRFEE_16("用户无手机支付业务，不能通过手机票购票，请开通后申请！"), //
    CRM_AIRFEE_17("您已经办理了:手机支付业务，取消后才可激活备卡！"), //
    CRM_AIRFEE_18("操作失败！<br>手机支付（手机钱包）缴费单笔不得超过1000元"), //
    CRM_AIRFEE_19("操作失败！<br>手机支付（手机钱包）缴费金额系统每日不得超过3万元"), //
    CRM_AIRFEE_2("调用手机支付批扣接口无返回值,请检查输入参数是否正确！"), //
    CRM_AIRFEE_20("调用空中充值代理商身份效验接口无返回值！"), //
    CRM_AIRFEE_21("调用空中充值手机支付绑定接口无返回值！"), //
    CRM_AIRFEE_22("调用空中充值手机支付查询接口无返回值！"), //
    CRM_AIRFEE_23("调用空中充值手机支付解除绑定接口无返回值！"), //
    CRM_AIRFEE_3("调用手机支付批扣接口无返回值！"), //
    CRM_AIRFEE_4("该笔业务为积分兑换手机支付红包,不允许返销"), //
    CRM_AIRFEE_5("该用户的手机支付业务已处于暂停状态,不能充值"), //
    CRM_AIRFEE_6("该用户没有开通手机支付主账户，请先开户！"), //
    CRM_AIRFEE_7("该用户已经订购了手机支付业务，请先去注销手机支付业务后再来办理[改号]业务。"), //
    CRM_AIRFEE_8("空中充值代理商部门不能为空"); //

    private final String value;

    private AirFeeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
