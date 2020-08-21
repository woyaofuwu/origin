
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum IpExpressException implements IBusiException
{

    CRM_IPEXPRESS_1("531002:该号码已经开通了IP业务!"), //
    CRM_IPEXPRESS_2("531003:该号码已经开通了IP业务!绑定的手机号码为: %s"), //
    CRM_IPEXPRESS_3("531005:请先选择IP直通车产品！"), //
    CRM_IPEXPRESS_4("531006:获取产品服务无数据！"), //
    CRM_IPEXPRESS_5(" 获取家庭虚拟用户账期数据错误!"), CRM_IPEXPRESS_6("无法获取已开通的固定号码的服务数据！"), CRM_IPEXPRESS_7("请先选择IP直通车产品！"), CRM_IPEXPRESS_8("获取产品服务无数据！"), CRM_IPEXPRESS_9("该号码已经开通了IP业务!"), CRM_IPEXPRESS_10("该号码已经开通了IP业务!绑定的手机号码为: %s"), CRM_IPEXPRESS_11(
            "未能获取此次操作数据！"), CRM_IPEXPRESS_12("本次操作未变更任何信息，无需提交！"), ;

    private final String value;

    private IpExpressException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
