
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum RuleException implements IBusiException // 规则异常
{
    CRM_RULE_1("根据动作编码获取规则错误！"), //
    CRM_RULE_2("增加积分兑换规则失败!"), //
    CRM_RULE_3("[%s]规则不匹配, 继续下一条!"), //
    CRM_RULE_4("短信规则配置不存在，请检查配置"), //
    CRM_RULE_5("该地州[%s]没有配置账号规则"), //
    CRM_RULE_6("规则类型已存在,规则ID为 ：%s"), //
    CRM_RULE_7("规则引擎错误BRE_ERROR:规则配置有问题, 解析规则字符串出错!［%s］"), //
    CRM_RULE_8("规则引擎错误BRE_ERROR:规则数据解析错误getIDataByString[%s]"), //
    CRM_RULE_9("删除积分兑换规则失败!"), //
    CRM_RULE_10("修改积分兑换规则失败!"), //

    CRM_RULE_12("没有返销业务数据!"), //
    CRM_RULE_13("没有业务返销轨迹!"), //
    CRM_RULE_14("业务返销前条件判断:您没有权限进行业务返销!"), //
    CRM_RULE_15("业务返销前条件判断:您无权返销其它员工所做的业务!"), //
    CRM_RULE_16("业务返销前条件判断:您无权返销其它部门所做的业务!"), //
    CRM_RULE_17("业务返销前条件判断:您无权返销其它业务区所做的业务!"), //
    CRM_RULE_18("业务返销前条件判断:您无权返销其它地市所做的业务!"), //
    CRM_RULE_19("业务返销前条件判断:您没有权限进行业务返销!"), //	
    CRM_RULE_20("没有找到改交易类型信息!"), //	
    CRM_RULE_21("此业务类型不能返销!"), //
    CRM_RULE_22("业务返销前判断:本业务收取的押金已被清退，不能被返销!"), //

    CRM_RULE_23("业务返销前判断:本业务已经超出可返销时间范围，不能被返销!"), //
    CRM_RULE_24("获取用户付费关系信息无记录!"), //
    CRM_RULE_25("获取用户账户信息无数据!"), //
    CRM_RULE_26("特殊限制表判断:帐户[%s]不存在!"), //
    CRM_RULE_27("产品依赖互斥判断:不正确的产品元素类型标记%s!"), //

    CRM_RULE_28("传进来的翻译字段无效"), //
    CRM_RULE_29("获取用户信息无记录!"), //
    CRM_RULE_30("查询用户资料数据, 没有找到用户标识输入参数!"), //
    CRM_RULE_31("购机类型参数出错!"), //
    CRM_RULE_32("购机限制类型参数配置错误!"), //
    CRM_RULE_33("获取台帐主表预约资料出错!"), //
    CRM_RULE_34("特殊限制判断:用户业务受限!"), //
    CRM_RULE_35("%s:%s"), CRM_RULE_36("RULE_ID不存在,说明对应RULE不存在!");//

    private final String value;

    private RuleException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
