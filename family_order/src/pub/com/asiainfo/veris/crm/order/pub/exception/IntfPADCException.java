
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum IntfPADCException implements IBusiException // 品牌异常
{
    CRM_SAGM_01("操作代码错误"), //
    CRM_SAGM_02("产品代码错误"), //
    CRM_SAGM_03("用户号码错误"), //
    CRM_SAGM_04("生效时间错误"), //
    CRM_SAGM_05("用户已在白名单内"), //
    CRM_SAGM_06("用户不在白名单内"), //
    CRM_SAGM_07("用户已在黑名单内"), //
    CRM_SAGM_08("用户不在黑名单内"), //
    CRM_SAGM_09("增值业务用户，不能进行黑白名单操作"), //
    CRM_SAGM_10("员工订购的产品不在其EC所订购的产品(包)中"), //
    CRM_SAGM_98("落地方内部错误"), //
    CRM_SAGM_99("其它错误: %s"), //
    CRM_SAGM_11("异网号码变更传入后的异网号码资费POINT_CODE为空"), //
    CRM_SAGM_12("已经订购教师套餐，不能反向办理"); //

    private final String value;

    private IntfPADCException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
