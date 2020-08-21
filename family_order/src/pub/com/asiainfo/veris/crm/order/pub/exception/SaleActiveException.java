
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum SaleActiveException implements IBusiException // 营销活动异常
{
	CRM_SALEACTIVE_0("[%s]"),CRM_SALEACTIVE_1("接口未传入活动产品编码！"), CRM_SALEACTIVE_2("接口未传入活动包编码！"), CRM_SALEACTIVE_3("接口未传入业务受理号码！"), CRM_SALEACTIVE_4("接口未传入接入渠道编码！"), CRM_SALEACTIVE_5("根据产品编码[%s]未获取到产品信息！"), CRM_SALEACTIVE_6("根据包编码[%s]未获取到包扩展信息！"), CRM_SALEACTIVE_7(
            "根据产品编码[%s]未获取到营销类型信息！"), CRM_SALEACTIVE_8("未获取到营销活动元素配置信息！"), CRM_SALEACTIVE_9("缺少参数,请输入本次操作流水号!"), CRM_SALEACTIVE_10("缺少参数,请输入业务类型!"), CRM_SALEACTIVE_11("缺少参数,请输入校验时间!"), CRM_SALEACTIVE_12("缺少参数,请输入客户订购订单号!"), CRM_SALEACTIVE_13(
            "缺少参数,请输入手机号!"), CRM_SALEACTIVE_14("缺少参数,请输入客户订购的营销案编码!"), CRM_SALEACTIVE_15("此营销方案编码【%s】不存在!"), CRM_SALEACTIVE_16("此营销方案编码【%S】未本地化!"), CRM_SALEACTIVE_17("缺少参数,请输入客户订购订单处理标识!"), CRM_SALEACTIVE_18("未找到此订单号【%s】记录!"), CRM_SALEACTIVE_19(
            "此订单号【%s】记录,已经处理!"), CRM_SALEACTIVE_20("在有效期内尚未办理营销活动的工单中,无此订单号:【%s】!"), CRM_SALEACTIVE_21("该ID已经重复！"), CRM_SALEACTIVE_22("根据[%s]获取活动产品信息无数据！"), CRM_SALEACTIVE_23("根据[%s]获取活动包信息无数据！"), CRM_SALEACTIVE_24("完工调用华为终端接口时，无数据返回！"),
    CRM_SALEACTIVE_25("调用新终端接口报错：%s"), CRM_SALEACTIVE_26("所传优惠不属于所办理的活动包的优惠！"), CRM_SALEACTIVE_27("该活动包要求至少选择[%s]个优惠元素，目前选择了[%s]个！"), CRM_SALEACTIVE_28("该活动包要求最多选择[%s]个优惠元素，目前选择了[%s]个！"), CRM_SALEACTIVE_29("在途状态的终端不允许优惠购机！"), CRM_SALEACTIVE_30(
            "已售状态的终端不允许优惠购机！"), CRM_SALEACTIVE_31("货到付款【待签收】状态订单下预占的手机串号不能参加！"), CRM_SALEACTIVE_32("根据转移活动的受理流水[%s]，未获取到转移活动可转移的元素信息！"), CRM_SALEACTIVE_33("缺少参数,请传入结束活动的办理流水!"), CRM_SALEACTIVE_34("接口参数检查，[%s]的值不存在!"), CRM_SALEACTIVE_35(
            "接口参数检查，此营销方案编码[%s]的值不存在!"), CRM_SALEACTIVE_36("未找到订单预受理记录！"), CRM_SALEACTIVE_37("调用资源礼品预占接口失败！"), CRM_SALEACTIVE_38("调用资源礼品释放接口失败！"), CRM_SALEACTIVE_39("终端状态不正常，不允许销售！"), CRM_SALEACTIVE_40("接口未传入机型编码！"), CRM_SALEACTIVE_41("接口未传入终端编码！"),
    CRM_SALEACTIVE_42("获取活动类型时，未传入活动类型编码！"), CRM_SALEACTIVE_43("您不具备办理本业务的岗位"), CRM_SALEACTIVE_44("您已经达到办理限额"), CRM_SALEACTIVE_45("根据手机号码[%s]和订单流水[%s]获取订单信息数据不正确"), CRM_SALEACTIVE_46("订单流水[%s]对应的订单信息状态已发生改变，不能做处理！"), CRM_SALEACTIVE_47("终端串码不能为空"),	CRM_SALEACTIVE_48("活动产品不存在，或者没有办理该产品下营销包的权限！"),
	CRM_SALEACTIVE_49("活动校验不通过：%s"),CRM_SALEACTIVE_55("在TD_B_SALE_GOODS_EXT表找不到对应地址的礼品配置信息，请到“优惠活动礼品配置”界面进行配置。")
	,CRM_SALEACTIVE_56("在PM_OFFER_GIFT表找不到对应数据。PACKAGE_ID=%s,PRICE_PLAN_ID=%s"),CRM_SALEACTIVE_57("活动校验不通过[p_csm_CheckForSaleActive]：%s：%s");

    private final String value;
    
    private SaleActiveException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
