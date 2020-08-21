
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum VipExchangeException implements IBusiException
{

    CRM_VIP_EXCHANGE_1("您不是VIP大客户"), CRM_VIP_EXCHANGE_2("您已办理大客户礼品兑换业务"), CRM_VIP_EXCHANGE_3("获取礼品配置信息无数据"), CRM_VIP_EXCHANGE_4("根据GIFT_TYPE_CODE和GIFT_ID获取礼品配置信息无数据"), CRM_VIP_EXCHANGE_5("您要兑换的礼品与您目前所属的大客户级别不一致"), CRM_VIP_EXCHANGE_6(
            "您上月的消费情况[%s]元与您所兑换的礼品要求消费情况不一致"), CRM_VIP_EXCHANGE_7("礼品兑换数据为空"), CRM_VIP_EXCHANGE_8("从参数表获取RSRV_VALUE_CODE或者SERVICE_ID为空");

    private final String value;

    private VipExchangeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
