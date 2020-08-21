
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum RemoteMobileException implements IBusiException // 员工异常
{
    CRM_REMOTEMOBILE_1("该用户处于停机状态，不能办理此业务！"), //

    CRM_REMOTEMOBILE_2("只有正常在网才能办理此业务！"), //

    CRM_REMOTEMOBILE_3("查询数据错误！<br> %s"), //
    CRM_REMOTEMOBILE_4("用户信息错误！"), //
    CRM_REMOTEMOBILE_5("操作失败！<br> %s"), //
    CRM_REMOTEMOBILE_6("寄送地邮政编码、寄送地址、收件人姓名、联系电话 四个条件必须同时为空或者同时不为空！"), //
    CRM_REMOTEMOBILE_7("该用户处于开通状态，不能办理此业务！");

    private final String value;

    private RemoteMobileException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
