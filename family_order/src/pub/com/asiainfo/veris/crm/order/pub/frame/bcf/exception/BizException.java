
package com.asiainfo.veris.crm.order.pub.frame.bcf.exception;

public enum BizException implements IBusiException // 框架异常
{
    CRM_GRP_713("[%s]"), //
    CRM_BIZ_1("该地州[%s]未配置业务类型[%s]参数"), //
    CRM_BIZ_2("CgToCrm查询同步数据不存在"), //
    CRM_BIZ_3("模版数据不存在"), //
    CRM_BIZ_4("字段%s不能转换为数字！"), //
    CRM_BIZ_5("业务错误，%s"), //
    CRM_BIZ_6("调用WebService接口异常[%s]"), //
    CRM_BIZ_7("您无异地办理业务权限"), //
    CRM_BIZ_650("服务路由ID不能为空"), //
    CRM_BIZ_651("服务路由类型未设置"), //
    CRM_BIZ_652("服务路由类型不可识别"), //
    CRM_BIZ_653("用户归属地州[%s]不可识别"), //
    CRM_BIZ_654("根据号码查询用户信息失败,该服务号码[%s]路由信息不存在!"), //
    CRM_BIZ_655("路由服务号码[SERIAL_NUMBER]不能为空"), //
    CRM_BIZ_656("员工交易地州为空"), //
    CRM_BIZ_657("用户归属地州为空"), //
    CRM_BIZ_167("调账务接口报错[%s,%s]"), //
    CRM_BIZ_168("调ESOP接口报错[%s,%s]"), //
    CRM_BIZ_169("调客服接口报错[%s]"), //
    CRM_BIZ_170("调终端接口报错[%s,%s]"), //
    CRM_BIZ_171("调接口报错[%s,%s]"), //
    CRM_BIZ_255("其他错误,KEY[%s]值不能为空!"), //
    CRM_BIZ_66("调用服务开通接口失败,%s"), //
    CRM_BIZ_65("根据订单标识[TRADE_ID=%s]查询订单信息不存在"), //
    CRM_BIZ_76("根据ORDER_ID[%s],查询订单信息不存在"), //
    CRM_BIZ_52("根据ORDERID[%s]未查询到融合子订单"), //
    CRM_BIZ_8("模板标识TEMPLATE_ID格式定义错误!"), //
    CRM_BIZ_9("模板标识TEMPLATE_ID前缀没有预先定义,请查看TD_S_STATIC表中TYPE_ID='BMC_TEMPLATE_ID_KEY'数据!"), //
    CRM_BIZ_10("当前数据源[%s]所对应的短信ID前2位编码没定义,请查看TD_S_STATIC表中TYPE_ID='BMC_SMSID_PRE2'数据!"), //
    CRM_BIZ_11("二次确认短信没有配置3位服务编码对应关系,请查看TD_S_STATIC表中TYPE_ID='BMC_TWOCHECK_CODE3'数据!");//

    private final String value;

    private BizException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
