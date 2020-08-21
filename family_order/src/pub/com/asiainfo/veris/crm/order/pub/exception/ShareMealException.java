
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ShareMealException implements IBusiException // 员工异常
{
    CRM_SHARE_1("该用户已经添加到别的共享，不可以成为主卡号码!"), //
    CRM_SHARE_2("该用户不是4G自选套餐或者4G流量用户不可共享!"), CRM_SHARE_3("该号码没有可共享的资费!"), CRM_SHARE_4("数据类终端不能参与多终端共享！"), CRM_SHARE_5("未激活用户不能参与多终端共享！"), CRM_SHARE_6("新增成员号码不能与主卡号码一致！"), CRM_SHARE_7("该号码帐期与主卡不一致，不允许添加成员,请先办理帐期变更业务。"),
    CRM_SHARE_8("成员归属地州和主卡地州不一致！"), CRM_SHARE_9("该用户已经添加到别的共享，不可以多次添加共享！"), CRM_SHARE_10("成员归属地州和主卡地州不一致！"), CRM_SHARE_11("根据INST_ID[%s]查询共享关系无数据"), CRM_SHARE_12("此成员关系到本账期末结束，不需要再次取消！"), CRM_SHARE_13("此成员共享关系里主卡信息异常，请核查！"), CRM_SHARE_14(
            "该用户是主卡用户，请到流量共享页面处理相关业务！"), CRM_SHARE_15("共享主卡号码不能为空！"), CRM_SHARE_16("共享副卡号码不能为空！"), CRM_SHARE_17("处理类型不能为空！"), CRM_SHARE_18("共享主卡号码不能和副卡相同！"), CRM_SHARE_19("此成员共享信息有误，请核查！"), CRM_SHARE_20("没有输入手机号码，请核查！"), CRM_SHARE_21(
            "主卡当前有4个副卡共享成员,不能再新增共享成员！"),CRM_SHARE_22("主号[%s]办理了JTZ套餐（270），无法添加多终端共享业务成员！"),
    CRM_SHARE_23("新入网或携入[%s]天内的客户，才可作为“多业务共享”副卡！"),
    CRM_SHARE_24("主卡当前有[%s]个副卡共享成员,不能再新增共享成员！"),
    CRM_SHARE_25("号码当前有流量冲浪包、4G流量套餐等，不满足办理条件！"),
    CRM_SHARE_26("主卡存在预约下月生效的不可共享套餐，不能办理共享业务，请先取消预约的套餐。")
            ;

    private final String value;

    private ShareMealException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
