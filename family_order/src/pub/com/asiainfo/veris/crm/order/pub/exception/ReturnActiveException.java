
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

/**
 * @真情回馈类业务异常
 */
public enum ReturnActiveException implements IBusiException
{

    CRM_RETURNACTIVE_1("用户可参加次数[%s]小于输入的次数[%s](用户的话费余额已经改变，请刷新资料后再办理!)"), CRM_RETURNACTIVE_2("用户未领卡总数已经改变，请刷新资料后再办理！"), CRM_RETURNACTIVE_3("卡号[%s]长度小于6位"), CRM_RETURNACTIVE_4("根据卡号[%s]和密码[%s]查询不到卡信息"),

    CRM_RETURNACTIVE_10("取PAYMENT_ID[%s]转账存折的时候有问题！"), CRM_RETURNACTIVE_11("获取缴费记录出错！"), CRM_RETURNACTIVE_12("领卡数量[%s]必须等于可领卡数量[%s]！"), CRM_RETURNACTIVE_13("领卡数量[%s]不能超过可领卡数量[%s]！"), CRM_RETURNACTIVE_20("获取[%s]出错！"),

    CRM_RETURNACTIVE_14("输入卡号有不同类型的卡！[%s]"), CRM_RETURNACTIVE_15("输入卡号含有非刮刮卡！[%s]"), CRM_RETURNACTIVE_16("输入卡号有不同面值的卡！[%s]"), CRM_RETURNACTIVE_17("输入卡号有不同子类型的卡！[%s]"), CRM_RETURNACTIVE_18("密码错误"), CRM_RETURNACTIVE_19("根据奖品编码[%s]查询奖品信息查不到数据");

    private final String value;

    private ReturnActiveException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
