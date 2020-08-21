
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CreatePersonUserException implements IBusiException // 多账期异常
{
    CREATEPERSONUSER_1("电话号码和SIM卡不能都为空！"), //
    CREATEPERSONUSER_2("导入的号码为无线固话专用号码，不能办理批量预开业务！！"), CREATEPERSONUSER_3("该号码[%s]只能选择产品[%s]!"), CREATEPERSONUSER_4("该号码[%s]不能选择产品[%s]!"), CREATEPERSONUSER_5("该号码在用户资料表可能存在正常记录"), CREATEPERSONUSER_6("该用户已经办理了相同的业务还没有完工，目前不能再进行该业务的办理了！"),
    CREATEPERSONUSER_7("该号码[%s]没有取到预配的sim卡，请检查配置！"), CREATEPERSONUSER_8("该sim卡[%s]没有取到预配的号码，请检查配置！"), ;

    private final String value;

    private CreatePersonUserException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
