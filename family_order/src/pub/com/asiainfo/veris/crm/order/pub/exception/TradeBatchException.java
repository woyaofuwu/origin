
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TradeBatchException implements IBusiException
{
    CRM_TRADEBATCH_1("业务错误:重复数据，在同一批次或同一任务中已存在相同的操作数据(OPERATE_ID=[%s])！"), //
    CRM_TRADEBATCH_2("程序错误:调用存储过程时传入的BATCH_OPER_TYPE值为空!"), //
    CRM_TRADEBATCH_3("程序错误:执行存储过程%s失败,%s"), //
    CRM_TRADEBATCH_4("业务错误:获取批量处理数据失败！(OPERATE_ID=[%s])"), //
    CRM_TRADEBATCH_5("业务错误:所归属的处理批次已被标识为删除！"), //
    CRM_TRADEBATCH_6("业务错误:所归属的处理批次尚未激活！"), //
    CRM_TRADEBATCH_7("业务错误:已成功登记过订单，不能重复登记！"), //
    CRM_TRADEBATCH_8("业务错误:所归属的处理批次已被标识为删除！"), //
    CRM_TRADEBATCH_9("业务错误:获取批量处理类型参数失败！"), //
    CRM_TRADEBATCH_10("业务错误:此批量处理类型不允许返销！"), //
    CRM_TRADEBATCH_11("获取RECORD_NO=[%s]的数据失败！"), //
    CRM_TRADEBATCH_12("获取BATCH_TASK_ID=[%s]的的批量任务数据失败！"), //
    CRM_TRADEBATCH_13("程序错误:无法获取路由地州！"), //
    CRM_TRADEBATCH_14("程序错误:更新OPERATE_ID=[%s]的处理信息失败！"), //
    CRM_TRADEBATCH_15("程序错误:更新OPERATE_ID=[%s]的处理信息失败！"), //
    CRM_TRADEBATCH_16("业务错误:未获取到SIM_CARD_NO=[%s]对应的预配号码！"), //
    CRM_TRADEBATCH_18("业务错误:未获取到SERIAL_NUMBER=[%s]对应的预配SIM卡！"), //
    CRM_TRADEBATCH_19("业务错误:更新OPERATE_ID=[%s]的预配SIM卡失败！"), //
    CRM_TRADEBATCH_20("电话号码和SIM卡不能都为空！"), //
    CRM_TRADEBATCH_21("获取SIM信息失败！"), //
    CRM_TRADEBATCH_22("该SIM卡=[%s]的资料错误。请检查卡状态、预配标志、预配号码或归属部门！"), //
    CRM_TRADEBATCH_24("获取号码信息失败！"), //
    CRM_TRADEBATCH_25("该SIM卡=[%s]对应IMSI和预配号码对应的预配IMSI不一致！"), //
    CRM_TRADEBATCH_26("该号码和预配IMSI对应的预配号码不一致！"), //
    CRM_TRADEBATCH_27("该号码在用户资料表可能存在正常记录！"), //
    CRM_TRADEBATCH_28("该用户已经办理了相同的业务还没有完工，目前不能再进行该业务的办理了！"), //
    CRM_TRADEBATCH_29("该手机号已经被占用，不能办理当前业务！"), //
    CRM_TRADEBATCH_30("无权批开归属其他地市的号！"), //
    CRM_TRADEBATCH_31("无权批开归属其他员工的卡！"), //
    CRM_TRADEBATCH_32("获取SERIAL_NUMBER=[%s]的用户数据失败！"), //
    CRM_TRADEBATCH_33("该号码已经有对应的销售员工！"), //
    CRM_TRADEBATCH_34("新增用户直销信息失败！"), //
    CRM_TRADEBATCH_35("获取IMSI=[%s]的SIM卡数据失败！"), //
    CRM_TRADEBATCH_36("SIM卡类型与用户品牌不符，不能办理预开！"), //
    CRM_TRADEBATCH_37("号码归属校验错误：部门级校验！"), //
    CRM_TRADEBATCH_38("号码归属校验错误：代理商级校验！"), //
    CRM_TRADEBATCH_39("号码归属校验错误：业务区级校验！"), //
    CRM_TRADEBATCH_40("号码归属校验错误：地州级校验！"), //
    CRM_TRADEBATCH_41("该号码资料错误。请检查号码资源表的号状态、预配标志、移动局号！"), //
    CRM_TRADEBATCH_42("该号码资料错误。请检查号码资源表的号状态、预配标志、移动局号或归属业务区！"), //
    CRM_TRADEBATCH_43("该号码资料错误。请检查号码资源表的号状态、预配标志、移动局号或归属地州！"), //
    CRM_TRADEBATCH_44("该号码资料错误。请检查号码资源表的号状态、预配标志、移动局号或归属部门！"), //
    CRM_TRADEBATCH_45("该号码资料错误。请检查号码资源表的号状态、预配标志、移动局号！"), //
    CRM_TRADEBATCH_46("该SIM卡资料错误。请检查号码资源表的号状态、预配标志、移动局号！"), //
    CRM_TRADEBATCH_47("该SIM卡资料错误。请检查号码资源表的号状态、预配标志、移动局号或归属业务区！"), //
    CRM_TRADEBATCH_48("该SIM卡资料错误。请检查号码资源表的号状态、预配标志、移动局号或归属地州！"), //
    CRM_TRADEBATCH_49("该SIM卡资料错误。请检查号码资源表的号状态、预配标志、移动局号或归属部门！"), //
    CRM_TRADEBATCH_50("根据trade_type没有找到对应的批量铃音接口！"), //
    CRM_TRADEBATCH_51("获取号码对应的交换机信息发生异常！"), //
    CRM_TRADEBATCH_52("获取卡对应的交换机信息发生异常！"), //
    CRM_TRADEBATCH_53("号、卡的对应交换机不一致！"), //
    CRM_TRADEBATCH_54("新增用户提示信息失败！");

    private final String value;

    private TradeBatchException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
