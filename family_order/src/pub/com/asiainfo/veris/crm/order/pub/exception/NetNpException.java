
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum NetNpException implements IBusiException
{

    CRM_NEPNP_1("生成业务台帐MainNp表异常!"), //
    CRM_NEPNP_2("登记台帐异常!"), //
    CRM_NEPNP_3("获取携转用户数据异常!"), //
    CRM_NEPNP_4("该用户没有登记携转!"), //
    CRM_NEPNP_7("该用户为携入用户，不允许办理该业务。"), //
    CRM_NEPNP_5("查询携出申请号码主台账无记录!"), CRM_NEPNP_6("查询该号码无携入方主台账记录!");

    private final String value;

    private NetNpException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
