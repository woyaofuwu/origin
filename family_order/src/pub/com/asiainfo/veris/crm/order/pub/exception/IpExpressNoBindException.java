
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum IpExpressNoBindException implements IBusiException
{

    CRM_IPEXPRESSNOBIND_1("请使用固定电话号码办理,必须以[%s]开头！"), //
    CRM_IPEXPRESSNOBIND_2("该号码已经开通了IP业务!"), CRM_IPEXPRESSNOBIND_3("请使用主号码来办理业务！"), CRM_IPEXPRESSNOBIND_4("办理业务的号码必须在已开通号码列表中存在！"), CRM_IPEXPRESSNOBIND_5("还存在其他IP直通车号码时，不允许删除主号码！"), CRM_IPEXPRESSNOBIND_6("请先选择IP直通车产品！"), CRM_IPEXPRESSNOBIND_7(
            "获取产品服务无数据！"), ;

    private final String value;

    private IpExpressNoBindException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
