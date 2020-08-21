
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum HighDangerUserException implements IBusiException // 员工异常
{
    CRM_HIGHDANGERUSER_1("%s！！"), //
    CRM_HIGHDANGERUSER_2("新增高风险熟卡操作失败!服务号码【%s】用户资料不存在。");

    private final String value;

    private HighDangerUserException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
