
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SvcException implements IBusiException
{
    CRM_SVC_1("获取台帐服务子表信息没有数据！"), //
    CRM_SVC_2("无850服务，请新增！"), //
    CRM_SVC_3("获取当前成员的服务信息失败，缺少主体[%s]服务！"), //
    CRM_SVC_4("您未办理全时通业务，不能办理全时通遇忙提醒业务！"), CRM_SVC_2001("没有查询到SERVICE_ID为[%s]对应的服务信息！"), CRM_SVC_5("获取用户主体服务状态信息无数据！"), CRM_SVC_6("用户当前状态为：%1$s，不允许办理此业务！"), CRM_SVC_7("服务【%s】没有关联的优惠。")

    ;//

    private final String value;

    private SvcException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
