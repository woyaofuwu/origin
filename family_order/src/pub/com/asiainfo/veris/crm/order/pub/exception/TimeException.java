
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TimeException implements IBusiException // 时间异常
{
    CRM_TIME_1("首次开始计费日期格式错误YYYYMMDD"), //
    CRM_TIME_2("选择的产品中有元素的参数配置有问题，参数不全，无法计算结束时间!"), //
    CRM_TIME_3("业务延期请选择需要延期的时间！"), //
    CRM_TIME_4("时间跨度超过规定"), //
    CRM_TIME_5("结束时间小于开始时间"), //
    CRM_TIME_6("开始时间不能小于当前系统时间！"), //
    CRM_TIME_7("终止时间不能小于当前系统时间！"), //
    CRM_TIME_8("选择的产品中有元素的参数配置有问题，参数不全，无法计算开始时间!"), //
    CRM_TIME_9("查询开始时间不能为空"), //
    CRM_TIME_10("查询结束时间不能为空"), //
    CRM_TIME_11("举报时间不能为空"), //
    CRM_TIME_12("用户可注销日期格式错误YYYYMMDD"), //
    CRM_TIME_13("查询开始时间与查询结束时间必须为同一月份"), //
    CRM_TIME_14("销户时间不能小于当前系统时间！"), //
    CRM_TIME_15("开户时间不能小于当前系统时间！"), //
    CRM_TIME_16("开户时间不能大于销户时间！"), //
    CRM_TIME_17("输入的预约时间必须大于或等于今天！"), //
    CRM_TIME_18("输入的预约时间必须在一个月内！"), //
    CRM_TIME_19("获取优惠生效，终止时间异常！"), //
    CRM_TIME_20("终止时间不能小于当前系统时间！"), //
    CRM_TIME_21("终止时间不能大于%s个月！"), //
    CRM_TIME_22("起始时间不能为空！"), //
    CRM_TIME_23("变更时获取开始时间出错：开始时间不能为空"), //
    CRM_TIME_24("日期格式转换出错"), //
    CRM_TIME_25("指定新中止日期不能小于当前日期，请重新指定新中止日期！"), //
    CRM_TIME_26("获取用户优惠开始时间出错！"), //
    CRM_TIME_27("用户查询时间为空！"), //
    CRM_TIME_28("业务前特殊限制表判断-获取限制时间失败！"), //
    CRM_TIME_29("业务前特殊限制表判断-获取限制时间失败！"), //
    CRM_TIME_30("[获取二次确认台账信息异常,用户回复时间超过48小时]"), //
    CRM_TIME_31("变更时获取开始时间出错：开始时间不能为空"), //
    CRM_TIME_32("当前时间不允许办理该批量业务"), //
    CRM_TIME_33("当前时间已经不在起始时间和终止时间之间，不能导入"), //
    CRM_TIME_34("起始时间与结束时间必须在同一个月内"), //
    CRM_TIME_35("得到主产品生效失效时间失败！"), //
    CRM_TIME_36("返销时间必须在该串号销售30天内!"), //
    CRM_TIME_37("该笔业务为非实物类礼品赠送或者此礼品赠送办理时间早于2010-12-20,不允许返销"), //
    CRM_TIME_38("获取结束时间错误！"), //
    CRM_TIME_39("获取开始时间错误！"), //
    CRM_TIME_40("获取优惠生效，终止时间异常！"), //
    CRM_TIME_41("结束时间不能为空！"), //
    CRM_TIME_42("结束时间小于开始时间"), //
    CRM_TIME_43("开始时间不能为空！"), //
    CRM_TIME_44("您已超过可返销的时间"), //
    CRM_TIME_45("退订时获取开始时间出错：开始时间不能为空"), //
    CRM_TIME_46("起始日期或结束日期格式错误"), //
    CRM_TIME_47("起始时间与结束时间必须在31天以内"), //
    CRM_TIME_48("起始时间与结束时间必须在同一个月内"), //
    CRM_TIME_49("取附加产品起始时间有误！"), //
    CRM_TIME_50("取附加产品终止时间有误！"), //
    CRM_TIME_51("日期不能为空"), //
    CRM_TIME_52("日期格式不匹配,必须为YYYYMMSSmmhhss"), //
    CRM_TIME_53("日期格式不匹配,必须为YYYYMMSSmmhhss,日期%s错误"), //
    CRM_TIME_54("生效时间不能为空"), //
    CRM_TIME_55("时间跨度超过规定"), //
    CRM_TIME_56("从用户产品表取产品生效日期失败！"), //
    CRM_TIME_57("受理时间为空或受时间格式错误！"), //
    CRM_TIME_58("输入的结束时间为空！"), //
    CRM_TIME_59("输入的开始时间为空！"), //
    CRM_TIME_60("退订时获取开始时间出错：开始时间不能为空"), //
    CRM_TIME_61("无赠送结束时间!"), //
    CRM_TIME_62("无赠送开始时间!"), //
    CRM_TIME_63("业务返销前判断:本业务已经超出可返销时间范围，不能被返销！"), //
    CRM_TIME_64("得到主产品生效失效时间失败！"), //
    CRM_TIME_65("取附加产品起始时间有误！"), //
    CRM_TIME_66("取附加产品终止时间有误！"), //
    CRM_TIME_67("预约时间不能小于今天!"), CRM_TIME_68("预约的日期和当前的日期间隔，不能超过6个月，包括当前月!");

    private final String value;

    private TimeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
