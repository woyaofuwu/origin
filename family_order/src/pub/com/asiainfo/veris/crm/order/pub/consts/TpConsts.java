package com.asiainfo.veris.crm.order.pub.consts;

public class TpConsts {
	/**
	 * 甩单操作类型定义
	 * @author fangyf3
	 *
	 */
	public class OperType{
		public static final String creatTpOrder = "0";//创建工单
		public static final String reminder = "1";//催单
		public static final String examine = "2";//审核
	}
	
	/**
	 * 甩单处理方式
	 * @author fangyf3
	 *
	 */
	public class DealType{
		public static final String auto = "0";//自动处理
		public static final String page = "1";//界面受理
		public static final String manualOper = "2";//人工操作
		public static final String specialType = "3";//特殊场景
	}
	
	/**
	 * 甩单工单处理方式
	 * @author fangyf3
	 *
	 */
	public class TradeMode{
		public static final String tradeMode0 = "0";//生成正式业务订单order、trade
		public static final String tradeMode1 = "1";//生成甩单工单
	}
	
	/**
	 * 甩单工单状态
	 * @author fangyf3
	 *
	 */
	public class OrderState{
		public static final String TPORDER_STATE_0 = "0";// 初始
		public static final String TPORDER_STATE_1 = "1";// 成功
		public static final String TPORDER_STATE_2 = "2";// 失败
		public static final String TPORDER_STATE_3 = "3";// 取消
	}
	
	/**
	 * 二次确认标识
	 * @author fangyf3
	 *
	 */
	public class SecConfirm{
		public static final String SEC_CONFIRM_0 = "0";// 否
		public static final String SEC_CONFIRM_1 = "1";// 是
	}
	
	/**
	 * 状态定义
	 * @author fangyf3
	 *
	 */
	public class State{
		public static final String vaild = "0";// 生效
		public static final String expired = "1";// 失效
	}
	
	/**
	 * 工单关系定义
	 * @author superUser
	 *
	 */
	public class relType{
		public static final String TPORDER_WITH_TPORDER = "1";// 甩单工单与甩单工单
		public static final String TPORDER_WITH_TRADE = "2";// 甩单工单与业务订单
	}

	/**
	 * 工单配置的作用类型
	 * @author superUser
	 */
	public class withType{
		public static final String WITH_TYPE0 = "0";// 业务类型
		public static final String WITH_TYPE1 = "1";// 规则编码
	}

	/**
	 * 常用字段
	 */
	public class comKey{
		public static final String accessNumber = "ACCESS_NUMBER";
		public static final String serialNumber = "SERIAL_NUMBER";
		public static final String userId = "USER_ID";
		public static final String custName = "CUST_NAME";
		public static final String tradeTypeCode = "TRADE_TYPE_CODE";
		public static final String productId = "PRODUCT_ID";
        public static final String state = "STATE";
		public static final String inTradeTypeCode = "IN_TRADE_TYPE_CODE";
	}

	/**
	 * 表名常量
	 */
	public class TableName{
		public static final String Tp_Order = "TP_ORDER";
		public static final String Tp_Order_Detail = "TP_ORDER_DETAIL";
		public static final String Tp_Order_Templ_Cfg = "TP_ORDER_TEMPL_CFG";
		public static final String Tp_Order_Rule_Route = "TP_ORDER_RULE_ROUTE";
	}

	/**
	 * 甩单工单状态
	 */
	public class TpOrderState{
		public static final String state0 = "0";//待处理
		public static final String state1 = "1";//已处理
		public static final String state2 = "2";//撤单
		public static final String state3 = "3";//直接归档
	}

	/**
	 * 甩单关系表单号类型
	 */
	public class OrderABType{
		public static final String tpOrder = "0";//甩单工单
		public static final String orderId = "1";//业务订单
	}
}
