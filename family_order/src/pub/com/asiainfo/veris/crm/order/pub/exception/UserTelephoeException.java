
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum UserTelephoeException implements IBusiException
{
    CRM_TELEPHOE_1("对不起，该用户不是千群百号代表号，不能办理此业务！"), CRM_TELEPHOE_2("对不起，新代表号是非正常号码，不能作为新代表号！"), CRM_TELEPHOE_3("对不起，新代表号不是此群中的号码，不能作为新代表号！"), CRM_TELEPHOE_4("原代表号%s无固话装机信息，请确认资料是否正确！");
    private final String value;

    private UserTelephoeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
