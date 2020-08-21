
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

/**
 * @真情回馈类业务异常
 */
public enum UECLotteryException implements IBusiException
{

    CRM_UECLOTTERY_1("获取不到用户的中奖信息"), CRM_UECLOTTERY_2("该用户已经领取过奖品"), CRM_UECLOTTERY_3("根据TRADE_ID=[%s]找不到中奖纪录"), CRM_UECLOTTERY_4("根据TRADE_ID=[%s]找到的记录未中奖或已领奖"), CRM_UECLOTTERY_5("领取的礼品名称填写不一致，请填写其中的[%s]某个礼品信息");

    private final String value;

    private UECLotteryException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
