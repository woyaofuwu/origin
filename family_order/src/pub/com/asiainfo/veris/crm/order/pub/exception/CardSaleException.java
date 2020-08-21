
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum CardSaleException implements IBusiException // 订单异常
{
    CRM_CardSaleInfo_01("输入批售号码或导入号码文件都为空，请检查文件或输入号码！"), //  
    CRM_CardSaleInfo_02("批售号码不能超过  %s"), //
    CRM_CardSaleInfo_03("不能存在空白行!"), //
    CRM_CardSaleInfo_04("%s 有重复!"), //
    CRM_CardSaleInfo_05("业务类型不能为空"), //
    CRM_CardSaleInfo_06("TD_S_COMMPARA配置不正确！"), // 
    CRM_CardSaleInfo_07("TD_S_COMMPARA参数表配置错误！"), //
    CRM_CardSaleInfo_08("号码[%s]无正常用户资料，请确认后重新导入！"), //
    CRM_CardSaleInfo_09("号码[%s]不属于所选熟卡产品，请确认后重新导入！"), //
    CRM_CardSaleInfo_1("导入文件只支持EXCEL！"), //
    CRM_CardSaleInfo_10("号码[%s]不属于该工号所属部门开户的号码，不能进行佣金座扣！"), //
    CRM_CardSaleInfo_11("号码[%s]开户时间为 %s，不能进行跨月座扣！"), //
    CRM_CardSaleInfo_12("号码[%s]已销售，不能重复销售，请重新导入！"), //
    CRM_CardSaleInfo_13("TD_S_COMMPARA配置不正确,请确认该参数是否已失效！"), //
    CRM_CardSaleInfo_14("已卖出的号码不能再次销售！"), //
    CRM_CardSaleInfo_15("必填信息不能为空！"), //
    CRM_CardSaleInfo_16("查询该订单类型已销售拜年卡信息无记录！"), //
    CRM_CardSaleInfo_17("导入数据为空，请检查文件！"), //
    CRM_CardSaleInfo_18("批售号码不能超过300！"), //
    CRM_CardSaleInfo_19("操作数据入参为空！"), CRM_CardSaleInfo_20("同一工单不能重复登记，请核实！"), CRM_CardSaleInfo_21("一个号码在一个月内只能录入一次，请核实！"), CRM_CardSaleInfo_22("获取充值卡WebService配置数据错误！"), CRM_CardSaleInfo_23("充值卡充值下发短信错误！"), CRM_CardSaleInfo_24("记录充值卡充值日志错误！");

    private final String value;

    private CardSaleException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
