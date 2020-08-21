
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ConfCrmException implements IBusiException 
{
    CONF_CRM_1("导入文件必填项不能为空"), //
    CONF_CRM_2("[%s]专线不在crm勘查工单内!"),
    CONF_CRM_3("[%s]专线办理过营销活动，月租费必须为0!"),
    CONF_CRM_4("[%s]专线办理过营销活动，一次性费用必须为0!"),
    CONF_CRM_5("[%s]专线办理过营销活动，IP地址使用费必须为0!"),
    CONF_CRM_6("[%s]专线办理过营销活动，软件应用服务费必须为0!"),
    CONF_CRM_7("[%s]专线办理过营销活动，技术支持服务费必须为0!"),
    CONF_CRM_8("[%s]专线办理过营销活动，语音通信费必须为0!"),
    CONF_CRM_9("[%s]专线办理过营销活动，SLA服务费必须为0!"),
	CONF_CRM_10("VOIP专线[%s]场景只允许变更月租费和一次性费用！"),
	CONF_CRM_11("VOIP专线[%s]场景不允许变更资费！"),
	CONF_CRM_12("VOIP专线[%s]场景只允许变更一次性费用！"),
	CONF_CRM_13("互联网专线[%s]场景只允许变更月租费和一次性费用！"),
	CONF_CRM_14("互联网专线[%s]场景不允许变更资费！"),
	CONF_CRM_15("互联网专线[%s]场景只允许变更 一次性费用、软件应用服务费、技术支持服务费！"),
	CONF_CRM_16("数据专线[%s]场景只允许变更月租费和一次性费用！"),
	CONF_CRM_17("数据专线[%s]场景只允许变更SLA服务费！"),
	CONF_CRM_18("数据专线[%s]场景只允许变更 一次性费用、软件应用服务费、技术支持服务费！"),
	CONF_CRM_19("[%s]专线带宽不能大于勘察单带宽！"),
	CONF_CRM_20("[%s]专线不在变更勘查工单内!"),
	CONF_CRM_21("调整场景为[%s],[%s]不允许修改!"),
	CONF_CRM_22("调整场景为[%s],带宽不能大于勘察单带宽!"),
	CONF_CRM_23("开通选择勘察单[%s]不允许修改!"),;

    private final String value;

    private ConfCrmException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
