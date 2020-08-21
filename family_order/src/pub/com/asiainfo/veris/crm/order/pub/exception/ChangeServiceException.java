
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ChangeServiceException implements IBusiException // 订单异常
{
    CRM_ChangeServiceInfo_01("此用户已经存在此预约服务未执行的预约记录，不能多次预约！"), //
    CRM_ChangeServiceInfo_02("未找到用户服务资料！"), //
    CRM_ChangeServiceInfo_03("没有获取到有效的集团信息！"), //
    CRM_ChangeServiceInfo_04("未找到用户服务资料！"), //
    CRM_ChangeServiceInfo_05("获取台帐副号关系资料异常:没有该笔业务 %s！"), //
    CRM_ChangeServiceInfo_06("获取台帐服务资料:没有该笔业务  %s！"), //
    CRM_ChangeServiceInfo_07("注销副号用户资料失败！"), //
    CRM_ChangeServiceInfo_08("查询用户IMSI号码为空！"), //
    CRM_ChangeServiceInfo_1("无效的用户！");

    private final String value;

    private ChangeServiceException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
