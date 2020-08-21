
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TerminalException implements IBusiException // 多账期异常
{
    CRM_TERMINAL_1("该串号不存在社会渠道分销库中，请确认!"), //
    CRM_TERMINAL_2("该串号已经销售，不能再次销售!"), //
    CRM_TERMINAL_3("该串号目前为不可销售状态!"), //
    CRM_TERMINAL_4("此终端不属于该部门，不能办理"), //
    CRM_TERMINAL_5("该串号已经在撮合采购串码库销售，不能再次销售!"), //
    CRM_TERMINAL_6("该串号不存在终端库中，请确认!"), //
    CRM_TERMINAL_7("该串号对应的终端类型编码与选择办理的终端类型编码不一致，不能办理!"), CRM_TERMINAL_8("用户换机更新串号失败!"); //

    private final String value;

    private TerminalException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
