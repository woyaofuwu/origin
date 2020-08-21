
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeRelationException implements IBusiException
{
    CRM_TRADERELATION_1("获取台帐副号关系资料异常:没有该笔业务[%s]！"), //
    CRM_TRADERELATION_2("生成副号付费关系失败！"), //
    CRM_TRADERELATION_3("查询主号IMSI出错！"), //
    CRM_TRADERELATION_4("生成副号付费关系失败！"), //
    CRM_TRADERELATION_5("生成副号资源副号号码失败！"), //
    CRM_TRADERELATION_6("生成副号资源副号IMSI失败！"), //
    CRM_TRADERELATION_7("生成主号无线传真服务异常！"), //
    CRM_TRADERELATION_8("生成副号无线传真服务异常！"), //
    CRM_TRADERELATION_9("生成副号无线传真服务状态异常！"), //
    CRM_TRADERELATION_10("生成副号无线传真服务异常！"), //
    CRM_TRADERELATION_11("生成副号无线传真优惠异常！"), //
    CRM_TRADERELATION_12("获取产品台帐资料出错！"), //
    CRM_TRADERELATION_13("获取产品资料出错！"), //
    CRM_TRADERELATION_14("生成组合产品用户资料不成功！"), //
    CRM_TRADERELATION_15("该号码已存在正常用户资料,不能再次开户！"), //
    CRM_TRADERELATION_16("生成用户资料不成功!！"), //
    CRM_TRADERELATION_17("注销副号用户资料失败！"), //
    CRM_TRADERELATION_18("终止付费关系失败！"), //
    CRM_TRADERELATION_19("终止副卡重要信息失败！"), //
    CRM_TRADERELATION_20("终止副号资源失败！"), //
    CRM_TRADERELATION_21("终止主号无线传真服务异常！"), //
    CRM_TRADERELATION_22("修改用户基本资料出错！"), //
    CRM_TRADERELATION_23("获取产品资料出错！"), //
    CRM_TRADERELATION_24("获取用户有效资料出错！"), //
    CRM_TRADERELATION_25("获取用户台帐资料无数据！"), //
    CRM_TRADERELATION_26("获取台帐流水异常！"), //
    CRM_TRADERELATION_27("获取用户资料异常！"), //
    CRM_TRADERELATION_28("业务类型[%d]不存在！"), //
    CRM_TRADERELATION_29("客户ID非法!"), //
    CRM_TRADERELATION_30("更新用户资料表失败"), //
    CRM_TRADERELATION_31("没有需要恢复的用户基本资料！"), //
    CRM_TRADERELATION_32("业务类型[%d]不存在！");

    private final String value;

    private TradeRelationException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
