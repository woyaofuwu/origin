
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum WapException implements IBusiException
{

    CRM_WAP_100001("输入参数%s是必须的！"), CRM_WAP_100002("输入参数%s是无效的！"), CRM_WAP_100003("产品信息不存在！"), CRM_WAP_100004("输入参数%s是无效的，在参数表中找不到此参数的配置信息！"), CRM_WAP_200001("获取用户主体服务状态信息无数据！"), CRM_WAP_200002("用户当前状态为：%s，不允许办理此业务！"), CRM_WAP_200003("获取用户信息无数据！"),
    CRM_WAP_200004("黑名单用户不能办理！"), CRM_WAP_200005("未进行实名登记用户不可以办理！"), CRM_WAP_200006("用户不存在，或者已经销户!"), CRM_WAP_700001("参数中缺少必填参数:%s"), CRM_WAP_700002("WAP用户信息不存在"), CRM_WAP_700003("用户登录凭证不存在"), CRM_WAP_700004("用户凭证与系统现存凭证不匹配"), CRM_WAP_700005(
            "CustContactTraceLog 日志插入失败,参数数据类型不匹配或数据不正确"), CRM_WAP_700006("鉴权登出失败，该用户身份凭证已经失效或不存在")

    ;

    private final String value;

    private WapException(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }

}
