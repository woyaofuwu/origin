
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum FuncrightException implements IBusiException // 权限异常
{
    CRM_FUNCRIGHT_1("调用端对端校验失败反馈接口失败[%s]：%s"), //
    CRM_FUNCRIGHT_10("没有配置家庭套餐或操作员[%s]没有该权限"), //
    CRM_FUNCRIGHT_11("没有配置IMS自选群套餐或操作员[%s]没有该权限"), //
    CRM_FUNCRIGHT_2("你无权选择绝对生效时间类型"), //
    CRM_FUNCRIGHT_3("根据PRODUCT_ID[%s]，在配置表信息不存在！"), //
    CRM_FUNCRIGHT_4("请为员工staff_id[%s]配置操作该批量菜单权限！"), //
    CRM_FUNCRIGHT_5("请为员工staff_id[%s]配置操作该批量权限！"), //
    CRM_FUNCRIGHT_6("您无权修改费用,应付%s元,实际付%s元"), //
    CRM_FUNCRIGHT_7("当前员工[%s]无权限办理非抢号188号码开户!"), //
    CRM_FUNCRIGHT_8("该客户经理编码[%s]不存在！"), //
    CRM_FUNCRIGHT_9("没有长途拨打权限的主号码[%s]，不能加该外地号码[%s]作为副号码！"); //

    private final String value;

    private FuncrightException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
