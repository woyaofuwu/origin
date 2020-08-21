
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BroadBandException implements IBusiException
{
    CRM_BROADBAND_1("终端接口返回异常！"), CRM_BROADBAND_2("光猫校验失败，原因：%s"), CRM_BROADBAND_3("光猫占用失败，原因：%s"), CRM_BROADBAND_4("该宽带用户资料查询异常！"), CRM_BROADBAND_5("OLT厂商资料查询异常！"), CRM_BROADBAND_6("未找到跟速率对应的优惠，不允许进行提交操作！"), CRM_BROADBAND_7("光猫更换失败，原因：%s"),
    CRM_BROADBAND_8("查询光猫子台账记录异常！"), CRM_BROADBAND_9("光猫释放失败，原因：！"), CRM_BROADBAND_10("未查询到用户HOUSE_ID等信息！"), CRM_BROADBAND_11("宽带号码[%s]查询宽带资料异常！"), CRM_BROADBAND_12("宽带号码[%s]查询宽带速率异常！"), CRM_BROADBAND_13("宽带号码[%s]查询宽带地址异常！"), CRM_BROADBAND_14(
            "宽带号码[%s]查询宽带产品异常！"), CRM_BROADBAND_15("宽带号码[%s]查询宽带优惠异常！"), CRM_BROADBAND_16("手机号码[%s]已存在有效的宽带绑定用户，不能再次进行绑定开户的操作！"), CRM_BROADBAND_17("宽带号码[%s]查询用户资料异常！"), CRM_BROADBAND_18("宽带号码[%s]查询个人客户资料异常！"), CRM_BROADBAND_19(
            "通过证件号码[%s]查询宽带客户资料异常！"), CRM_BROADBAND_20("通过证件号码[%s]查询宽带个人客户资料异常！"), CRM_BROADBAND_21("通过证件号码[%s]查询宽带用户资料异常！"), CRM_BROADBAND_22("用户ID[%s]查询宽带资料异常！"), CRM_BROADBAND_23("用户ID[%s]查询宽带用户优惠资料异常！"), CRM_BROADBAND_24(
            "TRADE_ID[%s]查询宽带资源子台账数据异常！"), CRM_BROADBAND_25("手机号码[%s]存在未完工的移动宽带开户工单！"), CRM_BROADBAND_26("优惠[%s]未配置MODEM方式！"), CRM_BROADBAND_27("MODEM方式未配置！"), CRM_BROADBAND_28("通过号码[%s]获取宽带服务资料异常！"), CRM_BROADBAND_29("军区用户不能修改宽带账号！"),
    CRM_BROADBAND_100("PBOSS接口返回异常！"), CRM_BROADBAND_101("PBOSS实时撤单失败，原因：%s"), CRM_BROADBAND_102("该军区帐号的资源已被占用!"), CRM_BROADBAND_103("该帐号已被军区产品所占用，请重新输入！"), CRM_BROADBAND_104("所选择的优惠[%s]与选择的服务[%s]不匹配!"),
    CRM_BROADBAND_105("未知的宽带执行动作!");

    private final String value;

    private BroadBandException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
