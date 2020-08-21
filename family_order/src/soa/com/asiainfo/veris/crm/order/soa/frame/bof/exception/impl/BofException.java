
package com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BofException implements IBusiException
{
    CRM_BOF_001("无法获取TradeType数据   TRADE_TYPE_CODE=%s"), //
    CRM_BOF_002("根据手机号码未找到用户资料"), //
    CRM_BOF_003("该号码无默认付费账户！"), //
    CRM_BOF_004("该号码默认付费账户信息不存在，业务不能继续"), //
    CRM_BOF_005("客户资料不存在"), //
    CRM_BOF_006("根据cust_id查询客户信息无数据"), //
    CRM_BOF_007("对不起，您的积分不够"), //
    CRM_BOF_008("未传入用户ID"), //
    CRM_BOF_009("未查询到客户信息请检查"), //
    CRM_BOF_010("查询客户信息不存在【TF_F_CUST_GROUP】"), //
    CRM_BOF_011("根据集团用户标识USER_ID:[%s]找不到对应的集团用户！"), //
    CRM_BOF_012("该号码无默认付费账户信息不存在，业务不能继续"), //
    CRM_BOF_013("未传入服务号码"), //
    CRM_BOF_014("根据当前TRADE_TYPE_CODE[%s]找不到符合条件的BuilderRequestData"), //
    CRM_BOF_015("查询局数据时必传参数未传"), //
    CRM_BOF_016("局数据不存在"), //
    CRM_BOF_017("根据服务号码[%s]查询用户资料不存在!"), //
    CRM_BOF_018("根据USER_ID查不到信用度信息"), //
    CRM_BOF_019("根据USER_ID查不到积分信息"), CRM_BOF_020("该用户不是销户销户！"), CRM_BOF_021("根据业务类型[%s]找不到TRADECONFIG数据"),
    CRM_BOF_022("费用异常，总金额应有%s,实际只有%s"),
    CRM_BOF_023("找不到对应的销售品配置【%s】"),
    CRM_BOF_024("传入的手机号码与user_id不对应"),
    CRM_BOF_025("该成员还没有激活,不能办理和校园业务"),
    CRM_BOF_026("该成员还没有实名制,不能办理和校园业务");
    private final String value;

    private BofException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
