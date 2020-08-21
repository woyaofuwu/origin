
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum WirelessPhoneException implements IBusiException // 无线座机异常
{
    CRM_WIRELESSPHONE_1("铁通无线座机充值失败！ 原因：%s"), //
    CRM_WIRELESSPHONE_2("无线座机修改价格计划！ 原因：%s"), //
    CRM_WIRELESSPHONE_3("铁通无线座机冲正失败！ 原因：%s"), //
    CRM_WIRELESSPHONE_4("铁通无线座机用户信息查询失败！ 原因：%s"), //
    CRM_WIRELESSPHONE_5("无线座复机通过营业厅办理失败, 原因: %s"), //
    CRM_WIRELESSPHONE_6("无线座机冲正失败！ 原因：%s"), //
    CRM_WIRELESSPHONE_7("无线座机开户业务办理失败, 原因: %s"), //
    CRM_WIRELESSPHONE_8("无线座机开户业务办理失败！ 原因：%s"), //
    CRM_WIRELESSPHONE_9("无线座机停机通过营业厅办理失败, 原因: %s"), //
    CRM_WIRELESSPHONE_10("无线座机销户通过营业厅办理失败, 原因: %s"); //

    private final String value;

    private WirelessPhoneException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
