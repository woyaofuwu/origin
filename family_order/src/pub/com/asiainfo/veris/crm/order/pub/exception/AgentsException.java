
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum AgentsException implements IBusiException // 代理商异常
{
    CRM_AGENTS_1("代理商补录不提供返销!"), //
    CRM_AGENTS_2("请先输入代理商自编码查询"), //
    CRM_AGENTS_3("请选择开户代理商!"), //
    CRM_AGENTS_4("受代理商预留数量限制，不能继续预留！"), //
    CRM_AGENTS_5("受该代理商当天开户数量限制，不能再开！"), //
    CRM_AGENTS_6("一次发送的代理商操作权限状态信息不能多于20个!"), //
    CRM_AGENTS_7("已经失效或终止的代理商信息不能修改！"), //
    CRM_AGENTS_8("无法查询到代理商归属"), //
    CRM_AGENTS_9("无法查询到代理商名称"), //
    CRM_AGENTS_10("查询代理商业务量报表异常"), //
    CRM_AGENTS_11("代理商编码不存在，请重新输入！"), //
    CRM_AGENTS_12("代理商补录不提供返销 ! "), //
    CRM_AGENTS_13("代理商为空，请重新输入！"), //
    CRM_AGENTS_14("代理商信息不存在，请关闭浏览器后重新登录!"), //
    CRM_AGENTS_15("该代理商ID已经存在！"), //
    CRM_AGENTS_16("代理商账户余额不足！"), //
    CRM_AGENTS_17("号码校验代理商错误！"), //
    CRM_AGENTS_18("代理商账户被冻结"), //
    CRM_AGENTS_19("输入的手机号码不正确！"), //
    CRM_AGENTS_20("输入的代理商编码不正确！"), CRM_AGENTS_21("选择的银行名称不存在！"), CRM_AGENTS_22("添加代理商编码已经存在!"), CRM_AGENTS_23("该代理商[%s]已有生效的银行账户信息！"), CRM_AGENTS_24("最大只能导入1000条 记录！"), CRM_AGENTS_118("代理商账户被冻结");
    private final String value;

    private AgentsException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
