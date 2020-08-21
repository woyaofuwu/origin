
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum Qry360UserViewException implements IBusiException // 360视图异常
{
    CRM_UserView_1("解析服务状态:未输入用户标识！"), //
    CRM_UserView_2("手机号码，用户标识，客户标识都为空，无法查询，请输入查询条件！"), //
    CRM_UserView_3("获取用户资料异常！"), //
    CRM_UserView_4("获取付费关系资料异常！"), //
    CRM_UserView_5("查询用户资料:未知获取类型！"), //
    CRM_UserView_6("获取用户资料无数据！"); //

    private final String value;

    private Qry360UserViewException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
