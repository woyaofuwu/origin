
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum WirelessFaxOpenException implements IBusiException // 订单异常
{
    CRM_WirelessFaxInfo_1("未配置副卡产品信息，请联系系统管理员！"), //
    CRM_WirelessFaxInfo_01("TD_S_COMMPARA配置不正确,请确认该参数是否已失效！"), //
    CRM_WirelessFaxInfo_02("未找到用户服务资料！"), //
    CRM_WirelessFaxInfo_03("没有获取到有效的集团信息！"), //
    CRM_WirelessFaxInfo_04("未找到用户服务资料！"), //
    CRM_WirelessFaxInfo_05("获取台帐副号关系资料异常:没有该笔业务 %s！"), //
    CRM_WirelessFaxInfo_06("获取台帐服务资料:没有该笔业务  %s！"), //
    CRM_WirelessFaxInfo_07("注销副号用户资料失败！"), //
    CRM_WirelessFaxInfo_08("查询用户IMSI号码为空！");

    private final String value;

    private WirelessFaxOpenException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
