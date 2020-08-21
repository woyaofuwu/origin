package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum WeChatException implements IBusiException
{
	CRM_WECHAT_0("%s"),
    CRM_WECHAT_1("用户不存在！"), 
    CRM_WECHAT_2("活动不存在！"), 
    CRM_WECHAT_3("不在活动有效期！"), 
    CRM_WECHAT_4("号码不在活动业务区！"), 
    CRM_WECHAT_5("不是目标星级用户！"), 
    CRM_WECHAT_6("用户积分余额不满足要求！"), 
    CRM_WECHAT_7("%s"),
    CRM_WECHAT_8("已达年度单用户总赠送次数！"),
    CRM_WECHAT_9("已达活动能支持最大的用户数！"),
	CRM_WECHAT_10("移动员工或代理商！"),
	CRM_WECHAT_11("已达每天活动名额能支持最大的用户数！"),
	CRM_WECHAT_12("已达每月活动名额能支持最大的用户数！");	

    private final String value;

    private WeChatException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
