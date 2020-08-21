
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum IBossException implements IBusiException // 一级BOSS异常
{
    CRM_IBOSS_1("%s,BUSI_SIGN报文类型不能为空!"), //
    CRM_IBOSS_2("%s一级boss确认接口出错！"), //
    CRM_IBOSS_3("一级boss确认接口出错！"), //
    CRM_IBOSS_4("调用一级BOSS出错，返回码：%s，返回信息：%s"), //
    CRM_IBOSS_5("调用IBOSS积分兑换接口失败:%s"), //
    CRM_IBOSS_6("调用服务失败！<br/>未调掉服务请与一级boss联系<br/>"), //
    CRM_IBOSS_7("订单撤销前获取子订单号异常!%s"), //
    CRM_IBOSS_8("订单撤销前调用IBOSS查询接口发生异常!"), //
    CRM_IBOSS_9("撤销失败!%s"), //
    CRM_IBOSS_10("调用IBOSS撤销积分兑换订单失败!"), //
    CRM_IBOSS_11("登记TF_F_USER_UPMS_ORDER用户礼品兑换记录异常!"), // ,
    CRM_IBOSS_12("登记TF_F_USER_UPMS_ORDER用户礼品超时兑换记录异常!"), //
    CRM_IBOSS_13("调用IBOSS积分兑换接口失败!"), //
    CRM_IBOSS_14("输入参数IN_MODE_CODE是必须的!"), //
    CRM_IBOSS_15("输入参数KIND_ID是必须的!"), //
    CRM_IBOSS_16("输入参数IDTYPE是必须的!"), //
    CRM_IBOSS_17("输入参数IDITEMRANGE是必须的!"), //
    CRM_IBOSS_18("调用IBOSS积分兑换接口异常！IBOSS未返回结果!"), //
    CRM_IBOSS_19("获取用户身份资料异常!"), //
    CRM_IBOSS_20("服务号码不正确"), //
    CRM_IBOSS_21("缺少参数,请输入手机号码"), //
    CRM_IBOSS_22("用户服务编码为 %s 的在BOSS侧未开通，无需同步，请重新发起HLR同步服务"), //
    CRM_IBOSS_23("用户服务编码为5的短信服务在BOSS侧已垃圾短信停机，不能HLR同步"), //
    CRM_IBOSS_24("用户服务编码为 %s 的服务在BOSS侧状态未激活，不能HLR同步"), //
    CRM_IBOSS_25("鉴权登出失败，该用户身份凭证已经失效或不存在!"), //
    CRM_IBOSS_26("鉴权延时失败，该用户身份凭证已经失效或不存在，需重新进行鉴权登录!"), //
    CRM_IBOSS_27("WAP用户开始接触日志插入失败!"), //
    CRM_IBOSS_29("WAP用户结束接触日志插入失败!"), //
    CRM_IBOSS_30("标识类型错误!"), //
    CRM_IBOSS_31("标识号码错误!"), //
    CRM_IBOSS_32("落地方没有找到有效有改号信息记录，不能取消!【%s】"), //
    CRM_IBOSS_33("该号码没有有效的改号预受理记录!"), //
    CRM_IBOSS_34("通知ESOP异常，请联系系统管理员！【%s】"), //
    CRM_IBOSS_35("该台账流水不存在!"), //
    CRM_IBOSS_36("办理用户不一致或办理的营销包不一致!"), //
    CRM_IBOSS_37("用户已经进行了转账处理!"), //
    CRM_IBOSS_38("用户没有进行转账处理!"), //
    CRM_IBOSS_39("该档次信息不存在，不允许转账!"), CRM_IBOSS_40("BUSI_SIGN报文类型与产品操作类型不一致!"), CRM_IBOSS_41("KIND_ID传入值错误!"), CRM_IBOSS_42("调用IBOSS接口查询礼品库存发生异常!"), CRM_IBOSS_43("查询库存失败：%s!"), CRM_IBOSS_44("获取参数【%s】有误,请检查TD_B_UPMS_GIFT处相应的兑换物存在否！"), CRM_IBOSS_45(
            "非客服和营业渠道不能办理积分兑换！"), CRM_IBOSS_46("10009：查询失败！%s"), CRM_IBOSS_47("10006：调用IBOSS查询积分兑换订单失败！"),
            CRM_IBOSS_48("未找到流水号【%s】流量充值对应记录!"),//
            CRM_IBOSS_49("未找到TRADE_ID=【%s】对应的充值记录!"),//
            CRM_IBOSS_50("根据全网编码【%s】未找到对应的优惠ID!");
    private final String value;

    private IBossException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
