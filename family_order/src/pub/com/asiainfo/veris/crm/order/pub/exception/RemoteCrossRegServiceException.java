
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum RemoteCrossRegServiceException implements IBusiException // 员工异常
{
    CRM_CROSSREGSERVICE_1("跨区服务失败！<br> %s"), //
    CRM_CROSSREGSERVICE_2("用户信息错误！"), //
    CRM_CROSSREGSERVICE_3("操作失败！<br> %s"), //
    CRM_CROSSREGSERVICE_4("该用户上次办理跨区入网时间还不到一年，暂时不能再次办理跨区入网！"), CRM_CROSSREGSERVICE_5("该号码已经做过【跨区入网资料同步】业务！"), CRM_CROSSREGSERVICE_6("跨区号码积分类型不存在！"), CRM_CROSSREGSERVICE_7("用户积分类型与跨区积分类型不相同，跨区积分类型为【%s】"), CRM_CROSSREGSERVICE_8("跨区号码客户级别不存在！"),
    CRM_CROSSREGSERVICE_9("该登录员工不是大客户经理，请营业员联系大客户经理办理跨区入网资料同步业务。"), CRM_CROSSREGSERVICE_10("该号码没有办理跨区入网服务业务，请确认。"), CRM_CROSSREGSERVICE_11("该号码已经办理过跨区入网资料同步业务！"), CRM_CROSSREGSERVICE_12("该号码开户时间超过了三个月，不能办理该业务！"), CRM_CROSSREGSERVICE_13(
            "该用户已经是大客户，不能办理该业务！"), CRM_CROSSREGSERVICE_14("VIP类型必须是 0,1,2,3或者5！"), CRM_CROSSREGSERVICE_15("VIP类型为0，VIP级别必须是数字1-4！"), CRM_CROSSREGSERVICE_16("VIP类型为1，VIP级别必须是数字1-4！"), CRM_CROSSREGSERVICE_17("VIP类型为2，VIP级别必须是数字0-4！"),
    CRM_CROSSREGSERVICE_18("VIP类型为3，VIP级别必须是数字5！"), CRM_CROSSREGSERVICE_19("VIP类型为3，VIP级别必须是数字3-4！"), CRM_CROSSREGSERVICE_20("获取VIP卡号失败！"), CRM_CROSSREGSERVICE_21("但由于同步VIP数据到发卡平台失败,发电子卡失败！请到【大客户电子卡批量发卡】模块进行发卡操作！");

    private final String value;

    private RemoteCrossRegServiceException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
