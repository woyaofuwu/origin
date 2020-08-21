
package com.asiainfo.veris.crm.order.soa.frame.bof.data.consts;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: SecSmsTypeEnum.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午08:49:16 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-14 liuke v1.0.0 修改原因
 */
public enum SecSmsTypeEnum
{

    // 记录每种二次短信确认业务的 回复值是XXX时才需要调用具体的服务，
    // 注：为空代表不控制，无论回复什么都调用
    // 如果某个业务支持多个恢回复值，可用用 | 分割
    ExpRemind(""), // 到期提醒
    PayRemind(""), // 扣费提醒
    PlatSvcSec("1|是"), // 平台业务二次确认
    WlanPreCard(""), // wlan预售卡
    EntityCard("Y"), // 实体卡
    SpecSvcSec("是"), // 特殊元素变更二次确认
    OneCardMultiSnSec("是"), // 影号业务
    GrpBussSec("是"),// 集团业务二次确认
    MOSP_CANCEL("Y|y"), // 0000和多号退订二次确认
    ORDER_PLAT("是");//0000订购增值服务
    
    private final String value;

    private SecSmsTypeEnum(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
