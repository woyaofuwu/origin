
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum PrintException implements IBusiException // 打印异常
{
    CRM_PRINT_1("业务已办理成功！</br>但打印出错，如需要打印单据，请使用重个人化方式重新操作！</br>%s"), //
    CRM_PRINT_2("打印信息获取失败！"), //
    CRM_PRINT_3("打印出错：查询费用打印配置信息出错“无配置信息！”"), //
    CRM_PRINT_4("打印类型不能为空"), //
    CRM_PRINT_5("打印数据为空"), //
    CRM_PRINT_6("该打印类型无相关数据，请检查通用参数表配置！"), //
    CRM_PRINT_7("无打印内容配置信息,trade_type_code:%s"), //
    CRM_PRINT_8("打印出错：非法打印格式模板类型！"), //
    CRM_PRINT_9("获取配置格式错误！"), //
    CRM_PRINT_10("打印出错：非法费用打印合并类型！"), //
    CRM_PRINT_11("没有获取到要打印的一单清业务信息！"), //
    CRM_PRINT_12("生成受理单打印内容:业务受理信息参数表中没有配置该业务的受理单参数!"), //
    CRM_PRINT_13("没有传入票据类型！"), //
    CRM_PRINT_14("没有传入票据编码！"), //
    CRM_PRINT_15("没有传入税务登记号！"), //
    CRM_PRINT_16("没有获取到业务流水号！"), //
    CRM_PRINT_17("业务类型编码[%s]不存在,请配置!"), //
    CRM_PRINT_18("集团打印，获取主台账信息无数据，ORDER_ID=[%s]!"), //
    CRM_PRINT_19("打印出错: 营业费用总额与明细数据不匹配!"), //
    CRM_PRINT_20("打印出错：押金总额与明细数据不匹配!"), //
    CRM_PRINT_21("打印出错：预存总额与明细数据不匹配!"), //
    CRM_PRINT_22("根据TRADE_ID[%s]查询不到需要返销的记录!"), CRM_PRINT_23("获取员工票据号错误!"), CRM_PRINT_24("请选择需要打印的数据！!"), CRM_PRINT_25("校验票据错误：%s"), CRM_PRINT_26("占用票据错误：%s"), CRM_PRINT_27("打印发票操作员工信息与原录入信息员工不符！"), CRM_PRINT_28("票据作废错误：%s"); //

    private final String value;

    private PrintException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
