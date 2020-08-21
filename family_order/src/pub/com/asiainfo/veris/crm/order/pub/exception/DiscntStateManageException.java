
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum DiscntStateManageException implements IBusiException
{

    CRM_DiscntStateManage_1("工号没有SYS_OPER_DISCNTSTATE操作权限！"), //
    CRM_DiscntStateManage_2("不支持的类型转换！"), //
    CRM_DiscntStateManage_3("没有对应优惠信息！"); //

    private final String value;

    private DiscntStateManageException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
