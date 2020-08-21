
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum PayRelationTradeException implements IBusiException
{

    CRM_PAYRELATION_1("生成普通付费关系异常!"), //
    CRM_PAYRELATION_2("普通付费关系特殊处理异常!"), //
    CRM_PAYRELATION_4("指定的第%d条ModifyTag无效!"), //
    CRM_PAYRELATION_5("获取台帐资料出错!"), //
    CRM_PAYRELATION_6("获取宽带用户付费关系台帐资料出错！"), //
    CRM_PAYRELATION_7("终止宽带用户付费关系出错!"), //
    CRM_PAYRELATION_8("插入其它号码原付费关系付费的新记录出错!"), //
    CRM_PAYRELATION_9("插入新记录出错!"), //
    CRM_PAYRELATION_10("修改当前有效记录终止时间出错!"), //
    CRM_PAYRELATION_11("修改当前有效记录终止时间出错!"), //
    CRM_PAYRELATION_12("获取主卡账户标示无有效数据!"), //
    CRM_PAYRELATION_13("获取副卡账户标示无有效数据!"), //
    CRM_PAYRELATION_14("生成帐务资料失败!"), //
    CRM_PAYRELATION_15("调用帐务接口函数〖aslib::TransAcct(...)〗：预存转帐出错!"), //
    CRM_PAYRELATION_16("虚拟销户准备转账数据查找主卡用户标识失败!"), //
    CRM_PAYRELATION_17("虚拟销户准备转账数据查找付费关系变更台账丢失!"), //
    CRM_PAYRELATION_18("无效的操作类型!"), //
    CRM_PAYRELATION_19("无默认付费帐户!"), //
    CRM_PAYRELATION_20("获取台帐流水异常!"), //
    CRM_PAYRELATION_21("无高级付费关系信息，不能进行集团统付彩铃属性维护操作!"), 
    CRM_PAYRELATION_22("该成员号码已经与此账户【%s】已经绑定了付费关系，业务不能继续"), 
    CRM_PAYRELATION_23("获取用户普通付费关系无记录!"),
    CRM_PAYRELATION_24("该用户【%s】存在未生效的集团产品账户拆分"),
    CRM_PAYRELATION_25("该账户【%s】只存在一个付费集团产品，不能进行拆分"),
    CRM_PAYRELATION_26("该集团产品【%s】对应的账户【%s】只存在一个付费集团产品，不能进行拆分"),
    CRM_PAYRELATION_27("该用户已经与此账户【%s】已经绑定了付费关系，业务不能继续") ;

    private final String value;

    private PayRelationTradeException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }

}
