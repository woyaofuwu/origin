
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BlissCardException implements IBusiException // 员工异常
{
    CRM_BLISSCARD_1("同一张福佑卡不能重复登记，请核实"), //
    CRM_BLISSCARD_2("同一个号码最多只能获得5张福佑卡!"), //
    CRM_BLISSCARD_3("没有可以登记的福佑卡信息！如果没有按[确认]按钮,请先按确认后再提交."), //
    CRM_BLISSCARD_4("此张福佑卡[%s]已经有用户使用!"), CRM_BLISSCARD_5("此张福佑卡[%s]已经有用户办理了业务!");

    private final String value;

    private BlissCardException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
