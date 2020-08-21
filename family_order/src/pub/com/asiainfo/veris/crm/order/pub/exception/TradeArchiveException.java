
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeArchiveException implements IBusiException // 多账期异常
{
    CRM_TRADEARCHIVE_1("业务类型不能为空"), //
    CRM_TRADEARCHIVE_2("您没有查询此类业务数据的权限"), //
    CRM_TRADEARCHIVE_3("此用户已经归档!"), //
    CRM_TRADEARCHIVE_4("无购机资料!"), //
    CRM_TRADEARCHIVE_5("请在[网上预约工单归档]中完成此功能"), //
    CRM_TRADEARCHIVE_6("归档号码不能超过%s"), //
    CRM_TRADEARCHIVE_7("购机资料超过一条");

    private final String value;

    private TradeArchiveException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
