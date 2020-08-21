
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SelfHelpException implements IBusiException
{

    CRM_SELFHELP_1("设备资产编码[%s]已经使用，不能重复新增！请尝试使用其它设备资产编码。"),
    CRM_SELFHELP_2("自助终端工号[%s]已经使用，不能重复新增！请尝试使用其它自助终端工号。"),
    CRM_SELFHELP_3("新增失败！"),
    CRM_SELFHELP_4("自助终端工号[%s]显示工号的第5位必须为S，且需与渠道编码对应，请点击重置，重新添加。");

    private final String value;

    private SelfHelpException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
