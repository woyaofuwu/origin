
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum WidenetException implements IBusiException
{
    CRM_WIDENET_1("该用户尚未开通宽带业务，不能进行宽带密码变更！"), CRM_WIDENET_2("查询宽带速率资料异常！"), CRM_WIDENET_3("查询宽带地址资料不存在！"), CRM_WIDENET_4("根据提供服务号码未获取到任何宽带资料信息，请确认此用户已办理过宽带业务！"), CRM_WIDENET_5("未在TD_S_COMMPARA表配置特殊优惠编码![1128][D]！"), CRM_WIDENET_6(
            "未在TD_S_COMMPARA表配置特殊服务编码![1128][S]！"), CRM_WIDENET_7("请传入宽带账号！"), CRM_WIDENET_8("请传入业务区！"), CRM_WIDENET_9("请传入产品！"), CRM_WIDENET_10("请传入员工代码！"), CRM_WIDENET_11("主号码非GPON宽带,不能办理业务!"), CRM_WIDENET_12(
            "根据TRADE_ID=【%s】和ATTR_CODE=【%s】查询TF_B_TRADEMGRPBOSS_ATTR表发生异常！"), CRM_WIDENET_13("根据服开返回参数修改宽带台账内宽带地址异常！"), CRM_WIDENET_14("获取TF_B_TRADE_PBOSS_FINISH无数据！"), CRM_WIDENET_15("TF_B_TRADE_PBOSS_FINISH无竣工时间！"), CRM_WIDENET_16(
            "TD_S_TAG取移动宽带开户回单时间参数异常！"), CRM_WIDENET_17("该客户还没有办理校园宽带套餐，不能办理变更业务，请到【校园宽带套餐办理及退订】页面办理！"), CRM_WIDENET_18("该学生学号已经开过校园宽带，不允许再开！"), CRM_WIDENET_19("宽带账号[%s]已经存在！"), CRM_WIDENET_20("套餐开始日期不应早于用户激活日期!"), CRM_WIDENET_21(
            "应缴金额【%s】元与实缴金额【%s】元不符！"), CRM_WIDENET_22("查询用户宽带信息无记录！"), CRM_WIDENET_23("获取用户服务状态无数据!"), CRM_WIDENET_24("用户没有开通宽带, 不能办理该业务!"), CRM_WIDENET_25("宽带密码变更只能选择【随即密码】!"), CRM_WIDENET_26("投诉时间段跨度不能超过3个月!"), CRM_WIDENET_27("帐号已存在!"),
    CRM_WIDENET_30("输入的参数中没有MAIN_TAG!"), CRM_WIDENET_31("输入的参数中没有ACCT_ID!"), CRM_WIDENET_32("获取城市热点调用地址参数失败!"),
    CRM_WIDENET_33("您好！目前三亚学院宽带业务未开通密码变更短信指令，请携本人学生证及身份证至校园内指定宽带办理点进行校园宽带密码变更。");
    
    private final String value;

    private WidenetException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
