package com.asiainfo.veris.crm.order.soa.person.busi.replacecard;

import java.util.Map;

import net.sf.json.JSONObject;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class ReplaceCardOrderSVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public IData synOrderInfo(IData data) throws Exception{
		
		//IDataUtil.chkParam(data, "ORDER_ID");
		
		
		IData result = new DataMap();
		//0成功1失败
	    result.put("RESUL_CODE", "0");
	    result.put("RESULT_INFO", "OK");
	    
		//data = new DataMap(data.toString());
	    Map xmListList = (Map) data.get("order_detail");
	    JSONObject json = JSONObject.fromObject(xmListList);
	       IData orderDetails = DataMap.fromJSONObject(json);
	       data.remove("order_detail");
	       data.put("order_detail", orderDetails);
		
		IData orderDetail=data.getData("order_detail");
		//校验orderId是否为空
		IDataUtil.chkParam(orderDetail, "order_id");
		//order_detail表入参
		IData syncOrder = new DataMap();
		syncOrder.put("ORDER_ID", orderDetail.getString("order_id"));
		
		syncOrder.put("ORDER_TYPE", orderDetail.getString("order_type"));
		syncOrder.put("ORDER_CHANNEL", orderDetail.getString("order_channel"));
		syncOrder.put("ORDER_WAY", orderDetail.getString("order_way"));
		syncOrder.put("MERCHANT_ID", orderDetail.getString("merchant_id"));
		syncOrder.put("USER_ID", orderDetail.getString("user_id"));
		syncOrder.put("ORDER_SALE_CONTRACT_TYPE", orderDetail.getString("order_sale_contract_type"));
		syncOrder.put("USER_NAME", orderDetail.getString("user_name"));
		syncOrder.put("SPECIAL_REMARKS", orderDetail.getString("special_remarks"));
		syncOrder.put("FEE", orderDetail.getString("fee"));
		syncOrder.put("FREIGHT", orderDetail.getString("freight"));
		syncOrder.put("SUM", orderDetail.getString("sum"));
		syncOrder.put("PRE_PAY", orderDetail.getString("pre_pay"));
		syncOrder.put("CREATE_TIME", orderDetail.getString("create_time"));
		syncOrder.put("STATE", orderDetail.getString("state"));
		syncOrder.put("PAY_WAY", orderDetail.getString("pay_way"));
		syncOrder.put("PAY_CHANNEL", orderDetail.getString("pay_channel"));
		syncOrder.put("GATEWAY_ID", orderDetail.getString("gateway_id"));
		syncOrder.put("PAY_STATE", orderDetail.getString("pay_state"));
		syncOrder.put("PAY_TIME", orderDetail.getString("pay_time"));
		syncOrder.put("DELIVER_WAY", orderDetail.getString("deliver_way"));
		syncOrder.put("PAY_TYPE", orderDetail.getString("pay_type"));
		syncOrder.put("RATE", orderDetail.getString("rate"));
		syncOrder.put("SERVICE_CHARGE", orderDetail.getString("service_charge"));
		syncOrder.put("NET_AMOUNT", orderDetail.getString("net_amount"));
		syncOrder.put("S_PAY_SUM", orderDetail.getString("s_pay_sum"));
		syncOrder.put("PRE_STATE", orderDetail.getString("pre_state"));
		syncOrder.put("RETURN_ORDER_NUM", orderDetail.getString("return_order_num"));
		syncOrder.put("REFUND_ORDER_NUM", orderDetail.getString("refund_order_num"));
		syncOrder.put("VALUE_ADDED_PRICE_SUM", orderDetail.getString("value_added_price_sum"));
		syncOrder.put("CHANNEL_CODE", orderDetail.getString("channel_code"));
		syncOrder.put("A_PAY_SUM", orderDetail.getString("a_pay_sum"));
		syncOrder.put("IS_WITH_COUPON", orderDetail.getString("is_with_coupon"));
		syncOrder.put("LOGIN_LESS_Q_CODE", orderDetail.getString("login_less_q_code"));
		syncOrder.put("IS_WITH_ACTIVITY", orderDetail.getString("is_with_activity"));
		syncOrder.put("COUPON_CHANNEL", orderDetail.getString("coupon_channel"));
		syncOrder.put("UPDATE_TIME", orderDetail.getString("update_time"));
		syncOrder.put("START_TIME", SysDateMgr.getSysTime());
		syncOrder.put("IMSI", orderDetail.getString("IMSI"));
		syncOrder.put("ICCID", orderDetail.getString("ICCID"));
		syncOrder.put("OPERA_DEPART", orderDetail.getString("opera_depart"));
		syncOrder.put("OPERA_STAFF", orderDetail.getString("opera_staff"));
		syncOrder.put("BLANKCARD_NUM", orderDetail.getString("blankcard_num"));
		syncOrder.put("RSRV_STR1", "1");
		if(!ReplaceCardOrderBean.synOrderInfo(syncOrder)){
	          result.put("RESUL_CODE", "1");
	          result.put("RESULT_INFO", syncOrder.getString("ORDER_ID")+"订单信息操作有误");
	    }
		
		
		
		IData deliveryInfo=orderDetail.getData("delivery_info");
		//order_delivery表入参
		IData syncOrderDelivery = new DataMap();
		syncOrderDelivery.put("ORDER_ID", orderDetail.getString("order_id"));
		syncOrderDelivery.put("CONSIGNEE", deliveryInfo.getString("consignee"));
		syncOrderDelivery.put("CREDENTIALS", deliveryInfo.getString("credentials"));
		syncOrderDelivery.put("CREDENTIALS_NO", deliveryInfo.getString("credentials_no"));
		syncOrderDelivery.put("AREA_ID", deliveryInfo.getString("area_id"));
		syncOrderDelivery.put("ADDR", deliveryInfo.getString("add"));
		syncOrderDelivery.put("POSTCODE", deliveryInfo.getString("postcode"));
		syncOrderDelivery.put("MOBILE", deliveryInfo.getString("mobile"));
		syncOrderDelivery.put("TEL", deliveryInfo.getString("tel"));
		syncOrderDelivery.put("EMAIL", deliveryInfo.getString("email"));
		syncOrderDelivery.put("PREFER_TIME", deliveryInfo.getString("prefer_time"));
		syncOrderDelivery.put("PROVINCE_ID", deliveryInfo.getString("province_id"));
		syncOrderDelivery.put("PROVINCE_NAME", deliveryInfo.getString("province_name"));
		syncOrderDelivery.put("CITY_ID", deliveryInfo.getString("city_id"));
		syncOrderDelivery.put("CITY_NAME", deliveryInfo.getString("city_name"));
		syncOrderDelivery.put("TOWN_ID", deliveryInfo.getString("town_id"));
		syncOrderDelivery.put("TOWN_NAME", deliveryInfo.getString("town_name"));
		syncOrderDelivery.put("COUNTRY_NAME", deliveryInfo.getString("country_name"));
		syncOrderDelivery.put("BUSIHALL_GOODS_ID", deliveryInfo.getString("busihall_goods_id"));
		syncOrderDelivery.put("BUSIHALL_MODEL_ID", deliveryInfo.getString("busihall_model_id"));
		if(!ReplaceCardOrderBean.synOrderDelivery(syncOrderDelivery)){
	          result.put("RESUL_CODE", "1");
	          result.put("RESULT_INFO", syncOrder.getString("ORDER_ID")+"订单信息操作有误");
	    }
		
		//order_item表入参
		IData syncOrderItem = new DataMap();
		IDataset orderItems = new DatasetList(orderDetail.getString("order_items"));
		//IDataset orderItems=orderDetail.getDataset("order_iterms");
		for (int i = 0; i < orderItems.size(); i++) {
			
			IData orderItem=orderItems.getData(i).getData("order_item");
			//校验ORDER_ITEM_ID是否为空
			IDataUtil.chkParam(orderItem, "order_item_id");
			System.out.println("orderItem取值====="+orderItem);
			syncOrderItem.put("ORDER_ID", orderDetail.getString("order_id"));
			syncOrderItem.put("ORDER_ITEM_ID", Long.parseLong(orderItem.getString("order_item_id")));
			syncOrderItem.put("GOODS_TYPE", orderItem.getString("goods_type"));
			syncOrderItem.put("GOODS_ID", orderItem.getString("goods_id"));
			syncOrderItem.put("GOODS_NAME", orderItem.getString("goods_name"));
			syncOrderItem.put("GOODS_VERSION", orderItem.getString("goods_version"));
			syncOrderItem.put("GOODS_DETAIL_URL", orderItem.getString("goods_detail_url"));
			syncOrderItem.put("MODEL_ID", orderItem.getString("model_id"));
			syncOrderItem.put("THIRD_PARTYID", orderItem.getString("third_partyId"));
			syncOrderItem.put("SALE_TIME_BEGIN", orderItem.getString("sale_time_begin"));
			syncOrderItem.put("SALE_TIME_END", orderItem.getString("sale_time_end"));
			syncOrderItem.put("WITH_CONTRACT", orderItem.getString("with_contract"));
			syncOrderItem.put("SALE_TYPE", orderItem.getString("sale_type"));
			syncOrderItem.put("UNIT_PRICE", orderItem.getString("unit_price"));
			syncOrderItem.put("UNIT", orderItem.getString("unit"));
			syncOrderItem.put("QUANTITY", orderItem.getString("quantity"));
			syncOrderItem.put("LIST_PRICE", orderItem.getString("list_price"));
			syncOrderItem.put("PRICE", orderItem.getString("price"));
			syncOrderItem.put("MERCHANT_ID", orderItem.getString("merchant_id"));
			syncOrderItem.put("MERCHANT_NAME", orderItem.getString("merchant_name"));
			syncOrderItem.put("MERCHANT_URL", orderItem.getString("merchant_url"));
			syncOrderItem.put("PAY_TIME_BEGIN", orderItem.getString("pay_time_begin"));
			syncOrderItem.put("PAY_TIME_END", orderItem.getString("pay_time_end"));
			syncOrderItem.put("PLAN_DELIVER_TIME", orderItem.getString("plan_deliver_time"));
			syncOrderItem.put("MEMO", orderItem.getString("memo"));
			syncOrderItem.put("RELATIONSHIP", orderItem.getString("relationship"));
			syncOrderItem.put("TICKET_NO", orderItem.getString("ticket_no"));
			syncOrderItem.put("CON_PRICE", orderItem.getString("con_price"));
			syncOrderItem.put("REDUCE_PRICE", orderItem.getString("reduce_price"));
			syncOrderItem.put("F_CODE", orderItem.getString("f_code"));
			syncOrderItem.put("SKU_FLAG", orderItem.getString("sku_flag"));
			syncOrderItem.put("SKU_NAME", orderItem.getString("sku_name"));
			syncOrderItem.put("NUMCARD_TYPE", orderItem.getString("numcard_type"));
			
			IData subscriber=orderItem.getData("subscriber");
			syncOrderItem.put("MOBILE", subscriber.getString("mobile"));
			syncOrderItem.put("CITY", subscriber.getString("city"));
			syncOrderItem.put("S_R_NAME", subscriber.getString("s_r_name"));
			syncOrderItem.put("CREDENTIALS", subscriber.getString("credentials"));
			syncOrderItem.put("CREDENTIALS_NO", subscriber.getString("credentials_no"));
			syncOrderItem.put("NUM_ACTIVE_STATUS", subscriber.getString("num_active_status"));
			syncOrderItem.put("ACTIVE_STATUS_DATE", subscriber.getString("active_status_date"));
			syncOrderItem.put("IMAGES", subscriber.getString("images"));
			if(!ReplaceCardOrderBean.synOrderItem(syncOrderItem)){
		          result.put("RESUL_CODE", "1");
		          result.put("RESULT_INFO", syncOrder.getString("ORDER_ID")+"订单信息操作有误");
		    }
			
		}
		
		return result;
		
	}
	/**
	 * 查询订单
	 */
	public IDataset qryOrder(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IDataset results = orderInfoQryBean.qryOrderInfo(param,getPagination());
		return results;
	}
	/**
	 * 确认订单
	 */
	public IData conFirm(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IData results = orderInfoQryBean.conFirm(param);
		return results;
	}
	/**
	 * 备货查询qryReadyGood
	 */
	public IDataset qryReadyGood(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IDataset results = orderInfoQryBean.qryReadyGood(param);
		return results;
	}
	/**
	 * 备货修改状态readyGoodUpdate
	 */
	public IData readyGoodUpdate(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IData results = orderInfoQryBean.readyGoodUpdate(param);
		return results;
	}
	/**
	 * 发货查询qrysendGood
	 */
	public IDataset qrysendGood(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IDataset results = orderInfoQryBean.qrysendGood(param);
		return results;
	}
	/**
	 * 发货修改状态sendGoodUpdate
	 */
	public IData sendGoodUpdate(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IData results = orderInfoQryBean.sendGoodUpdate(param);
		return results;
	}
	/**
	 * 完成修改状态completeUpdate
	 */
	public IData completeUpdate(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IData results = orderInfoQryBean.completeUpdate(param);
		return results;
	}
	/**
	 * 写卡完成writeCardUpdate
	 */
	public IData writeCardUpdate(IData param) throws Exception {
		ReplaceCardOrderBean orderInfoQryBean = BeanManager.createBean(ReplaceCardOrderBean.class);
		IData results = orderInfoQryBean.writeCardUpdate(param);
		//调补换卡写卡工单
		String tag1 = BizEnv.getEnvString("replace.card.tag1");
		if(StringUtils.isEmpty(tag1)||"1".equals(tag1)){
			CSAppCall.call("SS.ReplaceCardTradeSVC.tradeReg", param);
		}
		
		return results;
	}
	
	
	
	
}
