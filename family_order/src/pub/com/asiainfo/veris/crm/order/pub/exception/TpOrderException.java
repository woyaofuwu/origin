package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum TpOrderException implements IBusiException {
	TP_ORDER_40000("TPORDER400000：[%s]"),
	TP_ORDER_40001("TPORDER400001：[%s]不能为空！"),
	TP_ORDER_40002("TPORDER400002：作用对象和模板编码都为空！"),
	TP_ORDER_40003("TPORDER400003：根据甩单号[%s]获取甩单明细为空！"),
	TP_ORDER_40004("TPORDER400004：传入用户号码为空！"),
	TP_ORDER_40005("TPORDER400005：传入客户名称为空！"),
	TP_ORDER_40006("TPORDER400006：传入要甩单的业务类型为空！"), 
	TP_ORDER_40007("TPORDER400007：传入事件源类型为空！"),
	TP_ORDER_40008("TPORDER400008：传入事件源对象为空！"),
	TP_ORDER_40009("TPORDER400009：传入渠道编码为空！"),
	TP_ORDER_400010("TPORDER4000010：根据甩单号[%s],获取甩单信息为空！"),
	TP_ORDER_400011("TPORDER4000011：工单号不能为空！"),
	TP_ORDER_400012("TPORDER4000012：该工单号不能进行审核！"),
	TP_ORDER_400013("TPORDER4000013：工单号状态不为待处理，不能直接归档！"),
	TP_ORDER_400014("TPORDER4000014：传入事件列表[OBJ_LITS]为空！"),
	TP_ORDER_400015("TPORDER4000015：必须选优惠【%s】不允许取消！"),
	TP_ORDER_400016("TPORDER4000016：没匹配【%s】到用户已订购的优惠！"),
	TP_ORDER_400017("TPORDER4000017：根据虚拟用户标识USER_ID_A【%s】关系类型%s】没有查询到成员信息！"),
	TP_ORDER_400018("TPORDER4000018：根据虚拟用户标识USER_ID_A【%s】成员号码【%s】关系类型%s】没有查询到信息！"),
	TP_ORDER_400019("TPORDER4000019：根据虚拟用户标识USER_ID_A【%s】关系类型%s】没有查询到主号信息！"),

	TP_ORDER_400020("TPORDER4000020：模板编码【%s】或者作用对象编码【%s】对应甩单模板不存在！"),
	TP_ORDER_400021("TPORDER4000021：模板配置的处理方式【%s】无法识别！"),
	TP_ORDER_400022("TPORDER4000022：模板配置的工单方式【%s】无法识别！"),
	TP_ORDER_400023("TPORDER4000023：直接调度失败！"),
	TP_ORDER_400024("TPORDER4000024：甩单数据处理失败！"),

	TP_ORDER_400025("TPORDER4000025：查询用户可取消订单无数据！"),
	TP_ORDER_400026("TPORDER4000026：操作员【%s】没有【%s】的甩单权限！"),
	TP_ORDER_400027("TPORDER4000027：没有查询到可退订的集团优惠,用户标识【%s】关系类型【%s】！"),
	TP_ORDER_400028("TPORDER4000028：没有查询到绑定有效的固话！"),
	TP_ORDER_400029("TPORDER4000029：无规限制，可直接进行业务办理！"),
	TP_ORDER_400030("TPORDER4000030：有阻挡性规则，无法创建甩单工单！"),
	TP_ORDER_400031("TPORDER4000031：有跳转性规则，无法创建甩单工单！"),
	TP_ORDER_400032("TPORDER4000032：没有甩单工单号！"),
	TP_ORDER_400033("TPORDER4000033：该号码可直接办理业务，无需甩单！"),

	TP_ORDER_400034("TPORDER4000034：甩单【%s】无对应业务工单！"),
	;

	private final String value;

    private TpOrderException(String value)
    {
        this.value = value;
    }
	    
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
