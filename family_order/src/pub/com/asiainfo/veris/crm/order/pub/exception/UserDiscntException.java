
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum UserDiscntException implements IBusiException
{
    CRM_USER_DISCNT_1("没有符合查询条件的用户优惠数据！"), //
    CRM_USER_DISCNT_2("该集团已订购了警务通专网套餐(APN)[4013],则成员只能选择警务通成员专网APN套餐[4014]！"), //
    CRM_USER_DISCNT_3("获取成员资费信息失败！"), CRM_USER_DISCNT_4("营业员没有删除特殊优惠 [%s] 的权限,业务受理失败！"), CRM_USER_DISCNT_5("优惠【%s】不能删除!"), CRM_USER_DISCNT_6("优惠【%s】是包年优惠，未到期不能删除!"), CRM_USER_DISCNT_7("您要删除的优惠【%s】为主体优惠，不能删除!");

    private final String value;

    private UserDiscntException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
