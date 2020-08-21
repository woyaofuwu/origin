
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum UserDeductException implements IBusiException
{
    CRM_USERDEDUCT_1("查询代扣资料系统异常！"), // 210039
    CRM_USERDEDUCT_2("该用户已经是代扣关系用户！"), // 210039
    CRM_USERDEDUCT_200("该成员服务号码[%s]不是指定集团的VPMN成员！"), // 210039
    CRM_USERDEDUCT_3("该代扣用户不存在，台帐登记失败！"), // 
    CRM_USERDEDUCT_4("获取用户代扣资料无数据！"), //
    CRM_USERDEDUCT_5("该号码【%s】没有有效的批扣信息！");

    private final String value;

    private UserDeductException(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
