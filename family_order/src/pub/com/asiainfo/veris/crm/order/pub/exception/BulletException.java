
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BulletException implements IBusiException // 品牌异常
{
    CRM_BULLET_1("参数缺失:需要传入公告主题"), //
    CRM_BULLET_2("参数缺失:需要传入登录员工ID"), //
    CRM_BULLET_3("参数缺失:需要传入公告内容"), //
    CRM_BULLET_4("参数缺失:需要传入公告接收对象"), CRM_BULLET_5("发布工号[%s]不存在，请确认！");

    private final String value;

    private BulletException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
