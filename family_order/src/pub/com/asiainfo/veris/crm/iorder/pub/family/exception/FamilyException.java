
package com.asiainfo.veris.crm.iorder.pub.family.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum FamilyException implements IBusiException // 家庭业务异常
{
    CRM_FAMILY_1("家庭业务融合业务工单依赖失败！"),
    CRM_FAMILY_2("申请加入的主卡号码还未开通家庭共享功能！"),
    CRM_FAMILY_3("根据USER_ID[%s]查询家庭共享关系无数据"),
    CRM_FAMILY_4("服务号码[%s]不是家庭关系中的管理员用户"),
    CRM_FAMILY_5("根据USER_ID[%s]查询存在多条家庭关系！"),
    CRM_FAMILY_6("号码[%s]不是该家庭管理员，不能办理该业务！"),
    CRM_FAMILY_7("家庭商品[%s]配置信息不存在，请检查配置！"),
    CRM_FAMILY_8("查询营销活动产品无信息！"),
    CRM_FAMILY_9("商品[%s]配置信息不存在！"),
    CRM_FAMILY_10("商品[%s]的自身生效失效参数未配置！"),
    CRM_FAMILY_11("家庭中未添加手机成员,请先添加手机成员！"),
    CRM_FAMILY_12("[%s]存在预约的产品变更，且预约生效产品和家庭手机角色配置产品不一致，不能办理！"),
    CRM_FAMILY_13("[%s]存在预约的产品变更，且预约生效时间大于家庭手机角色配置产品生效时间，不能办理！"),
    CRM_FAMILY_14("家庭中没有号码【%s】的成员，不能办理该业务！"),
    CRM_FAMILY_15("家庭中没有办理家庭语音/流量共享业务的手机成员！"),
    CRM_FAMILY_16("根据USER_ID[%s]查询家庭资料无数据"),
    CRM_FAMILY_17("未获取到所传入参，请检查！"),
	CRM_FAMILY_18("根据INST_ID[%s]查询家庭资料无数据"),
    CRM_FAMILY_19("非家庭用户不能受理该业务"),
    CRM_FAMILY_20("入参商品[%s]非家庭组合商品，不能办理家庭业务"),
    CRM_FAMILY_21("管理员[%s]不在手机成员列表中，请检查"),
    CRM_FAMILY_22("根据家庭产品配置，最小添加[%s]个[%s]成员"),
    CRM_FAMILY_23("根据家庭产品配置，最多添加[%s]个[%s]成员"),
    CRM_FAMILY_24("每个手机角色成员[%s]下，最多添加1个[%s]成员"),
    ;


    private final String value;

    private FamilyException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
