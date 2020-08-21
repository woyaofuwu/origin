
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade;

public class NpConst
{
    public static final String ACT_RESP_NEW = "ACT_RESP_NEW";// 授权码生效响应
	
    public static final String AUTHCODE_RESP = "AUTHCODE_RESP";// 授权码响应
    
    public static final String AUTH_RESP = "AUTH_RESP";// 查验响应
	
	public static final String ACT_NOTIFY_NEW = "ACT_NOTIFY_NEW";//申请结果告知新
	
	public static final String ACT_REQ_NEW = "ACT_REQ_NEW"; //新生效请求
	
	public static final String AUTHCODE_REQ = "AUTHCODE_REQ";//授权码请求
	
	public static final String AUTH_REQ = "AUTH_REQ";//查验请求
	
    public static final String APPLY_REQ = "APPLY_REQ";// 申请请求

    public static final String APPLY_RESP = "APPLY_RESP";// 申请响应

    public static final String APPLY_NOTIFY = "APPLY_NOTIFY";// 申请结果告知

    public static final String ACT_REQ = "ACT_REQ";// 生效请求

    public static final String ACT_RESP = "ACT_RESP";// 生效响应

    public static final String ACT_BROADCAST_IND = "ACT_BROADCAST_IND";// 生效广播指示

    public static final String ACT_BROADCAST_CONFIRM = "ACT_BROADCAST_CONFIRM";// 生效广播确认

    public static final String ACT_BROADCAST_REVERSE_IND = "ACT_BROADCAST_REVERSE_IND";// 生效取消广播指示

    public static final String ACT_BROADCAST_REVERSE_CONFIRM = "ACT_BROADCAST_REVERSE_CONFIRM";// 生效取消广播确认

    public static final String ACT_NOTIFY = "ACT_NOTIFY";// 生效结果告知

    public static final String DACT_REQ = "DACT_REQ";// 注销请求

    public static final String DACT_RESP = "DACT_RESP";// 注销响应

    public static final String DACT_NOTIFY = "DACT_NOTIFY";// 注销结果告知

    public static final String DACT_BROADCAST_IND = "DACT_BROADCAST_IND";// 注销广播指示

    public static final String DACT_BROADCAST_CONFIRM = "DACT_BROADCAST_CONFIRM";// 注销广播确认

    public static final String RETURN_NOTIFY = "RETURN_NOTIFY";// 号码归还告知

    public static final String CANCEL_REQ = "CANCEL_REQ";// 申请取消请求

    public static final String CANCEL_RESP = "CANCEL_RESP";// 申请取消响应

    public static final String CANCEL_IND = "CANCEL_IND";// 申请取消指示

    public static final String CANCEL_CONFIRM = "CANCEL_CONFIRM";// 申请取消确认

    public static final String CANCEL_NOTIFY = "CANCEL_NOTIFY";// 申请取消结果告知

    public static final String SUSPEND_REQ = "SUSPEND_REQ";// 欠费停机请求

    public static final String SUSPEND_RESP = "SUSPEND_RESP";// 欠费停机响应

    public static final String SUSPEND_NOTIFY = "SUSPEND_NOTIFY";// 欠费停机结果告知

    public static final String RESUME_REQ = "RESUME_REQ";// 恢复业务请求

    public static final String RESUME_RESP = "RESUME_RESP";// 恢复业务响应

    public static final String RESUME_NOTIFY = "RESUME_NOTIFY";// 恢复业务结果告知

    public static final String AUDIT_IND = "AUDIT_IND";// 按时间审计指示

    public static final String AUDIT_CONFIRM = "AUDIT_CONFIRM";// 按时间审计确认

    public static final String AUDIT_REQ = "AUDIT_REQ";// 审计请求

    public static final String AUDIT_RESP = "AUDIT_RESP";// 审计响应

    public static final String AUDITREPAIR_IND = "AUDITREPAIR_IND";// 审计恢复指示

    public static final String AUDITREPAIR_CONFIRM = "AUDITREPAIR_CONFIRM";// 审计恢复确认

    public static final String AUDITAPPLY_NOTIFY = "AUDITAPPLY_NOTIFY";// 审计结果告知

    public static final String REMOVE_TAG_CARRY_OUT_CANCELED = "7";// 携出已销 USER表 remove_tag

    public static final String REMOVE_TAG_CARRY_OUT_OWE_FEE_CANCELED = "8";// 携出欠费销号 USER表 remove_tag

    // --------------USER表 USER_TAG_SET
    public static final String USER_TAG_SET_NOT_CARRY_OUT = "0";// 未携出

    public static final String USER_TAG_SET_CARRY_INTO = "1";// 已携入

    public static final String USER_TAG_SET_CARRY_INTO_CANCELED = "2";// 携入已销

    public static final String USER_TAG_SET_CARRY_INTO_ING = "3";// 携入已中

    public static final String USER_TAG_SET_CARRY_OUT = "4";// 已携出

    public static final String USER_TAG_SET_CARRY_OUT_CANCELED = "5";// 携出已销

    public static final String USER_TAG_SET_CARRY_RETURN = "6";// 携回

    public static final String USER_TAG_SET_CARRY_RETURN_CANCELED = "7";// 携回已销

    // --------------TF_F_USER_SVCSTATE表 state_code
    public static final String USER_STATE_CODE_CARRY_OUT_CANCELED = "X";// 携出已销

    public static final String USER_STATE_CODE_CARRY_OUT_OWE_FEE_CANCELED = "Z";// 携出欠费销号

    public static final String USER_STATE_CODE_CARRY_OUT_OWE_FEE_STOP = "Y";// 携出欠费停机

    public static final String TRADE_TYPE_CODE_CARRY_INTO_OPEN = "40";// 携入开户

}
