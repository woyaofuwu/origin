
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum BrandException implements IBusiException // 品牌异常
{
    CRM_BRAND_1("获取用户品牌失败"), //
    CRM_BRAND_11("获取用户品牌无数据!"), //
    CRM_BRAND_12("根据用户表BRAND_CODE查询不到品牌！"), //
    CRM_BRAND_13("转赠号码的品牌和被转增号码的品牌不一致!"), //
    CRM_BRAND_15("兑换物所属品牌与该用户所属品牌不符！"), //
    CRM_BRAND_16("业务受理条件判断：您当前的品牌是公话业务(G011)，不能办理此业务！"), // 
    CRM_BRAND_17("业务受理条件判断：您当前的品牌是随E行(G005)，不能办理此业务！"), //
    CRM_BRAND_18("业务受理条件判断：您当前的品牌是公话业务(G011)，不能办理此业务！"), //
    CRM_BRAND_19("业务受理条件判断：您当前的品牌是随E行(G005)，不能办理此业务！"), //
    CRM_BRAND_2("该品牌下没有产品或您没有该权限！"), //
    CRM_BRAND_20("根据品牌没有获取到产品列表"), //
    CRM_BRAND_21("用户主号码不可以为此品牌"), //
    CRM_BRAND_22("用户主号码不可以为此品牌的此产品"), //
    CRM_BRAND_23("该用户产品不能进行积分兑换，用户品牌必须为全球通或动感地带或神州行！"), //
    CRM_BRAND_24("用户副号码不可以为此品牌"), //
    CRM_BRAND_25("用户主号码不可以为此品牌的此产品"), //
    CRM_BRAND_26("主号码品牌级别不能小于副号码品牌级别"), //
    CRM_BRAND_27("业务受理前条件判断：该用户品牌不能办理积分兑奖业务！"), //
    CRM_BRAND_29("本界面不受理非个人品牌用户销户业务"), //
    CRM_BRAND_3("无品牌信息！"), //
    CRM_BRAND_30("得到新主产品品牌失败！"), //
    CRM_BRAND_31("得到主产品品牌失败！"), //
    CRM_BRAND_32("兑换物所属地州与该用户所属地州不符！"), //
    CRM_BRAND_33("兑换物所属品牌与该用户所属品牌不符！"), //
    CRM_BRAND_35("该用户品牌为神州行，不能开通WLAN国际漫游功能"), //
    CRM_BRAND_36("获取个人品牌列表无数据!"), //
    CRM_BRAND_37("获取用户品牌信息出错！"), //
    CRM_BRAND_38("没有查询到品牌! "), //
    CRM_BRAND_39("您输入的随e行手机号码不是随E行品牌，请检查!"), //
    CRM_BRAND_40("品牌不存在，无法发送基础套餐资费沟通信息"), //
    CRM_BRAND_41("请选择品牌"), //
    CRM_BRAND_42("无法得到用户新品牌！"), //
    CRM_BRAND_43("无法得到用户原有品牌！"), //
    CRM_BRAND_44("用户品牌参数表信息不全！"), //
    CRM_BRAND_46("用户品牌信息不全！"), //
    CRM_BRAND_48("不是全球通用户，不允许办理国际长途和国际漫游业务！"), //
    CRM_BRAND_49("根据品牌编码查不到有效的品牌信息"), //
    CRM_BRAND_50("该集团产品存在代付关系，请先取消，再进行此操作！"), //
    CRM_BRAND_5("本界面仅受理G品牌个人用户的销号！"), //
    CRM_BRAND_6("获取亲情品牌出错%s"), //
    CRM_BRAND_7("获取亲情通品牌出错%s"), //
    CRM_BRAND_8("不受理非个人品牌用户复机"), //
    CRM_BRAND_9("不受理非宽带集团用户停开机"); //

    private final String value;

    private BrandException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
