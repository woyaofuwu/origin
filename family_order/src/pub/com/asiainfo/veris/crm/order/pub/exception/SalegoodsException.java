
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SalegoodsException implements IBusiException // 营销物资异常
{
    CRM_SALEGOODS_1("%s购机资料超过一条"), //
    CRM_SALEGOODS_2("根据条件SERIAL_NUMBER:%s,REMOVE_TAG:%s无法找到预登记的礼品赠送记录"), //
    CRM_SALEGOODS_3("您已经领取了%s次奖品,无法领取!"), //
    CRM_SALEGOODS_4("您已经领取了%s张刮刮卡,无法再对奖!"), //
    CRM_SALEGOODS_5("实物扣减失败，请重新选择：%s"), //
    CRM_SALEGOODS_6("无购机资料!"), //
    CRM_SALEGOODS_7("修改终端购机款失败！"), //
    CRM_SALEGOODS_8("物品不足！只剩：%s个！"), //
    CRM_SALEGOODS_9("用户积分不够，不能选择该购机业务!"), //
    CRM_SALEGOODS_10("兑换物品【%s】数量为0，请重新输入！"), //
    CRM_SALEGOODS_11("用户兑换的物品【%s】目前库存为[%s]，不足本次兑换，请重新输入!"), //
    CRM_SALEGOODS_12("用户处在购机期间，不能修改用户名称！"), //
    CRM_SALEGOODS_13("本月已销售数据大于可销售数据,暂不能办理该业务.可销售%s 台,已销售%s 台."), //
    CRM_SALEGOODS_14("当前业务区无购机类型资料！"), //
    CRM_SALEGOODS_15("当前用户没有任何可以办理的购机业务！"), //
    CRM_SALEGOODS_16("请先输入异网手机号码再进行校验！"), //
    CRM_SALEGOODS_17("串号不能为空或长度不能小于14位！"), //
    CRM_SALEGOODS_18("该串码在终端串码库，限制办理当前业务！"), //
    CRM_SALEGOODS_19("该号码不在手机号码库，限制办理当前业务！"), //
    CRM_SALEGOODS_20("输入错误，该客户不属于目标客户，限制办理当前业务！"), //
    CRM_SALEGOODS_21("串号不能为空！"), //
    CRM_SALEGOODS_22("串号对应机型不在该营销包中！"), //
    CRM_SALEGOODS_23("没有获取到可兑换的物品信息"), //
    CRM_SALEGOODS_24("缺少物品ID"), //
    CRM_SALEGOODS_25("缺少资源号码"), //
    CRM_SALEGOODS_26("缺少产品编码"), //
    CRM_SALEGOODS_27("缺少包编码"), //
    CRM_SALEGOODS_28("请传入IMEI号！"), //
    CRM_SALEGOODS_29("请传入手机号码信息！"), //
    CRM_SALEGOODS_30("获取营销活动实物台帐IMEI资料出错，无数据！"), CRM_SALEGOODS_31("该机型库存数量已经小于等于网上终端预占数量，不能再进行销售!"), CRM_SALEGOODS_32("换机修改用户IMEI失败！"), CRM_SALEGOODS_33("根据OLD_IMEI在TF_F_USER_SALE_GOODS未找到记录！");

    private final String value;

    private SalegoodsException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
