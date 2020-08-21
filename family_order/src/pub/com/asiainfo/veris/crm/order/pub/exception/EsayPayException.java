
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum EsayPayException implements IBusiException
{

    CRM_EsayPay_1("主副号码不能相同！"), //
    CRM_EsayPay_2("该手机号码不存在,请使用有效的湖南移动号码！"), //
    CRM_EsayPay_3("该副号码已输入，请重核实！"), //
    CRM_EsayPay_4("添加限制，批量添加副号码不能超过10条！"), CRM_EsayPay_5("【检验副号码失败】%s"), CRM_EsayPay_6("该副号码组合已设置！"), CRM_EsayPay_7("没有可以登记的副号码！"), CRM_EsayPay_8("【添加副号码失败！】%s"), CRM_EsayPay_9("【WebService解析错误，请联系管理员！】%s"), CRM_EsayPay_s_1("易支付交易参数有误！"),
    CRM_EsayPay_s_2("获取短信验证码失败：%s"), CRM_EsayPay_s_3("获取短信验证码失败：商户验签中心平台失败"), CRM_EsayPay_s_4("银行卡签约缴费绑定关系查询失败：%s"), CRM_EsayPay_s_5("银行卡签约缴费绑定关系查询失败：商户验签中心平台失败"), CRM_EsayPay_s_6("银行卡签约失败：%s"), CRM_EsayPay_s_7(" 银行卡签约失败：商户验签中心平台失败"),
    CRM_EsayPay_d_1("银行卡签约缴费解约失败：%s"), CRM_EsayPay_d_2("银行卡签约缴费解约失败：商户验签中心平台失败"), CRM_EsayPay_q_1("无银行卡签约信息！");

    private final String value;

    private EsayPayException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
