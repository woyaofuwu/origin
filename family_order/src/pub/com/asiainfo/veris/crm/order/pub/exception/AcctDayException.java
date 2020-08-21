
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum AcctDayException implements IBusiException // 多账期异常
{
    CRM_ACCTDAY_1("计费账期不能为空"), //
    CRM_ACCTDAY_10("用户账期数据为空!"), //
    CRM_ACCTDAY_11("传入用户服务号码[%s]用户账期数据为空!"), //
    CRM_ACCTDAY_12("当前用户多条有效的账期！"), //
    CRM_ACCTDAY_13("当前用户无有效的账期,请联系系统管理员！"), //
    CRM_ACCTDAY_15("该服务号码[%s]不是自然月出账,不容许办理业务!"), //
    CRM_ACCTDAY_16("该服务号码[%s]用户账期信息不存在！"), //
    CRM_ACCTDAY_17("该用户[%s]账期信息不存在！"), //
    CRM_ACCTDAY_18("获取新的账期信息出错!"), //
    CRM_ACCTDAY_19("获取账期结账日参数信息出错!"), //
    CRM_ACCTDAY_2("获取用户新的账期日信息失败！"), //
    CRM_ACCTDAY_20("获取个人业务账期生效方式参数失败!"), //
    CRM_ACCTDAY_21("获取静态参数【BORDER_DATE】失败!"), //
    CRM_ACCTDAY_22("获取帐户帐期表信息出错!"), //
    CRM_ACCTDAY_23("用户存在预约账期,请在账期生效后来办理！"), //
    CRM_ACCTDAY_24("获取用户新的账期结账日为空,请设置!"), //
    CRM_ACCTDAY_25("无法获取用户号码[%s]账期数据!"), //
    CRM_ACCTDAY_26("该服务号码[%s]存在未生效的账期变更业务, 请在[%s]号账期生效后办理业务!"), //
    CRM_ACCTDAY_27("该服务号码[%s]存在未生效的账期变更业务, 请在[%s]号后将账期变更为自然月可办理业务!"), //
    CRM_ACCTDAY_28("该服务号码[%s]为非自然月账期, 须将账期变更为自然月账期才可办理业务!"), //
    CRM_ACCTDAY_29("该服务号码[%s]为非自然月账期号码,请重新输入!"), //
    CRM_ACCTDAY_3("获取帐户帐期表信息出错"), //
    CRM_ACCTDAY_4("获取账期日信息失败！"), //
    CRM_ACCTDAY_5("没有获取到上月帐期!"), //
    CRM_ACCTDAY_6("起始账期不能大于结束账期，请重新输入！"), //
    CRM_ACCTDAY_7("起始账期不能小于当前的账期，请重新输入！"), //
    CRM_ACCTDAY_8("起始账期大于结束账期，数据错误！"), //
    CRM_ACCTDAY_9("起始账期小于当前的账期，数据错误！"), CRM_ACCTDAY_30("该服务号码[%s]存在未生效的账期变更业务,请在[%s]号账期生效后办理业务"), CRM_ACCTDAY_31("该服务号码[%s]存在未生效的账期变更业务,请在[%s]号后将账期变更为自然月可办理业务"), CRM_ACCTDAY_32("该服务号码[%s]不是自然月出账,须将账期改为自然月才可办理改业务！"), ;

    private final String value;

    private AcctDayException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
