
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SaleDepsoitException implements IBusiException // 营销物资异常
{
    CRM_SALEDEPOSIT_1("赠送号码不能与受理号码相同"), //
    CRM_SALEDEPOSIT_2("根据ID[%s]找不到预存赠送元素"), //
    CRM_SALEDEPOSIT_3("当前版本仅支持一个自由预存");

    private final String value;

    private SaleDepsoitException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
