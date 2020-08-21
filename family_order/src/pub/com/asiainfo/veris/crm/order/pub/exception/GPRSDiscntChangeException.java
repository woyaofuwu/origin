
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum GPRSDiscntChangeException implements IBusiException // 多账期异常
{
    CRM_GPRSDISCNTCHANGE_1("该用户没有开通GPRS服务！"), //
    CRM_GPRSDISCNTCHANGE_2("获取用户GPRS优惠无数据！"), CRM_GPRSDISCNTCHANGE_3("用户状态为[%s],不能正常使用GPRS功能！"), CRM_GPRSDISCNTCHANGE_4("无可变更的GPRS优惠数据！"), CRM_GPRSDISCNTCHANGE_5("新增GPRS优惠数据错误，请重新选择后提交！"), ;

    private final String value;

    private GPRSDiscntChangeException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
