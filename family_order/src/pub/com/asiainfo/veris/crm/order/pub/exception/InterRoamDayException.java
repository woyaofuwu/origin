
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum InterRoamDayException implements IBusiException // 多账期异常
{
    CRM_INTERROAMDAY_2("退订当天不允许办理国漫套餐！"), CRM_INTERROAMDAY_1("该用户没有开通国际漫游服务！"),
    CRM_INTERROAMDAY_300001("该用户没有开通国际漫游服务！"),CRM_INTERROAMDAY_300002("用户存在套餐%s，无法再叠加定向套餐!"),
    CRM_INTERROAMDAY_300003("用户已订购此方向套餐，相同方向的定向套餐在同一时间段内不可叠加!"),CRM_INTERROAMDAY_300004("用户存在%s的定向套餐，无法再叠加其他定向套餐!"),
    CRM_INTERROAMDAY_300005("客户余额不足,需要%s元才能办理！"),CRM_INTERROAMDAY_300006("用户存在定向套餐，无法办理套餐%!"),
    CRM_INTERROAMDAY_300007("对不起，您本月已享受两次一带一路产品优惠，请下月再办理。感谢您的支持与理解!"),
    CRM_INTERROAMDAY_2996("对应的优惠编码不存在！"),
    CRM_INTERROAMDAY_2997("不能重复申请此套餐！"),CRM_INTERROAMDAY_2998("本次服务开通失败!")//
    ;

    private final String value;

    private InterRoamDayException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
