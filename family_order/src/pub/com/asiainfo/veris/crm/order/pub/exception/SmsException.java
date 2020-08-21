
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SmsException implements IBusiException
{
    CRM_SMS_1("发送短信通知:没有输入手机号码！"), //
    CRM_SMS_2("发送短信通知:没有输入短信内容！"), //
    CRM_SMS_3("二次确认短信请求序列号为空！"), //
    CRM_SMS_4("未找到对应的二次确认短信-FLOW_ID=[%s]！"), CRM_SMS_5("入参DEAL_STATE有误，请检查入参！"), CRM_SMS_6("入参SERIAL_NUMBER有误，请检查入参！"), CRM_SMS_7("执行二次短信回复服务异常[%s,%s]！"), CRM_SMS_8("执行修改二次确认预受理单状态异常[%s]！"), CRM_SMS_9("不能重复回复，预处理工单已经处理[%s]！");

    private final String value;

    private SmsException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
