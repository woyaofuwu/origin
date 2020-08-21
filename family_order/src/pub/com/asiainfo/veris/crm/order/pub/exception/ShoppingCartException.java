
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ShoppingCartException implements IBusiException
{
    CRM_SHOPPINGCART_1("购物车提交异常：没有可提交的购物车业务！"), CRM_SHOPPINGCART_2("二维码配置异常：未获取到有效的二维码信息！"), CRM_SHOPPINGCART_3("二维码配置异常：未获取到有效的二维码配置信息！");

    private final String value;

    private ShoppingCartException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
