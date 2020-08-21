
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TicketException implements IBusiException // 票价异常
{
    CRM_TICKET_1("查询门票定单号码为空，请返回！%s"), //
    CRM_TICKET_2("预订门票失败->%s"), //
    CRM_TICKET_3("电子发票编码不能为空"), //
    CRM_TICKET_4("流水号不能为空"), //
    CRM_TICKET_5("根据PRINT_ID[PRINT_ID=%s]查询票据打印信息不存在"), 
    CRM_TICKET_6("更新[TRADE_ID=%s,FEE_MODE=%s,TICKET_STATE_CODE=%s,TICKET_TYPE_CODE=%s]票据信息出错"),
    CRM_TICKET_7("票据操作时台账信息为空"),
    CRM_TICKET_8("FEE_MODE不能为空"),
    CRM_TICKET_9("票据操作时票据信息为空"), //
    CRM_TICKET_10("发票号已经存在,请重新输入!"), //
    CRM_TICKET_11("根据TRADE_ID[%s]没有获取发票打印信息!"),
    CRM_TICKET_12("根据TRADE_ID[%s]没有获取票据打印信息!"),
    CRM_TICKET_13("票据作废错误：%s"),
    CRM_TICKET_14("请先冲红原发票再做返销,发票号为:%s"),
    CRM_TICKET_15("占用票据错误：%s"); 

    private final String value;

    private TicketException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
