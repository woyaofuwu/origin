
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeOtherFeeException implements IBusiException
{
    CRM_TRADEOTHERFEE_1("根据订单标识[TRADE_ID=%s]查询转账信息不存在"), //
    CRM_TRADEOTHERFEE_2("转帐必须输转入帐户标识(ACCT_ID_B)和转入用户标识(USER_ID_B)！");

    private final String value;

    private TradeOtherFeeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
