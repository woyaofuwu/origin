package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;

public class MerchShoppingCartQry extends CSBizBean {
	
	/**
	 * 获取购物车的订单信息
	 * @param staffId
	 * @param userId
	 * @param state
	 * @param serialNumber
	 * @param flag  为true:查询购物车里面的宽带, 为false: 则不查
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static IDataset getShoppingSetByStaffUserId(String staffId, String userId, String state, String serialNumber,
			boolean flag) throws Exception {
		IData param = new DataMap();
		param.put("TRADE_STAFF_ID", staffId);
		param.put("USER_ID", userId);
		param.put("STATE", state);
		param.put("SERIAL_NUMBER", serialNumber);
		if (flag)
			return Dao.qryByCode("TF_B_SHOPPING_CART", "SEL_BY_STAFF_USER_IDS_101", param,
					Route.getJourDb(BizRoute.getRouteId()));
		else
			return Dao.qryByCode("TF_B_SHOPPING_CART", "SEL_BY_STAFF_USER_IDS", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 查询购物车的明细信息
	 * 
	 * @param shoppingCartId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static IDataset getShoppingDetailById(String shoppingCartId, String eparchyCode) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shoppingCartId);
		param.put("TRADE_STAFF_ID", getVisit().getStaffId());
		param.put("EPARCHY_CODE", eparchyCode);
		IDataset detailInfos =  Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_BY_ID", param, Route.getJourDb(eparchyCode));
		if(IDataUtil.isNotEmpty(detailInfos))
		{
			for(int i=0;i<detailInfos.size();i++)
			{
				IData detailInfo = detailInfos.getData(i);
				String detailTypeCode = detailInfo.getString("DETAIL_TYPE_CODE");
				detailInfo.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(detailTypeCode, eparchyCode));
				detailInfo.put("TRADE_TYPE_CODE", detailTypeCode);
			}
		}
		
		return detailInfos;
	}
	
	/**
	 * 根据OrderId删除购物车的预约数据
	 * 
	 * @param orderId
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void deleteTradeByOrderId(String orderId) throws Exception {
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "DEL_TRADE_BY_ORDER", param,
				Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 更新购物车明细表里面的 状态
	 * 
	 * @param detailOrderId
	 * @param stateCode
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void updateCartDetailByStateCode(String detailOrderId, String stateCode) throws Exception {
		IData param = new DataMap();
		param.put("DETAIL_ORDER_ID", detailOrderId);
		param.put("DETAIL_STATE_CODE", stateCode);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "UPD_BY_DORID_STATE_CODE", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 搬到历史表
	 * 
	 * @param shopCartId
	 * @param detailOrderid
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void insertShopDetailHisByShopIdOrderId(String shopCartId, String detailOrderid) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shopCartId);
		param.put("DETAIL_ORDER_ID", detailOrderid);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "INS_SHOPCART_HIS_BY_CARTID_ORDERID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 删除B表的数据
	 * 
	 * @param shopCartId
	 * @param detailOrderid
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void deleteShopDetailByCartIdOrderid(String shopCartId, String detailOrderid) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shopCartId);
		param.put("DETAIL_ORDER_ID", detailOrderid);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "DEL_SHOPCART_BY_CARTID_ORDERID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 根据shoppingCartId查询所有的购物车明细
	 * 
	 * @param shoppingCartId
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static IDataset getAllShoppingDetailByCartId(String shoppingCartId) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shoppingCartId);
		return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_ALL_BY_ID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 更新shoppingCartId表里面的状态
	 * 
	 * @param shoppingCartId
	 * @param orderId
	 * @param state
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void updateShoppingCartState(String shoppingCartId, String orderId, String state) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shoppingCartId);
		param.put("SHOPPING_ORDER_ID", orderId);
		param.put("STATE", state);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART", "UPD_CART_STATE", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * shoppingCart搬到历史表
	 * 
	 * @param shopCartId
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void insertShopCartHisByShopCartId(String shopCartId) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shopCartId);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART", "INS_SHOPCART_HIS_BY_CARTID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 删除B表的数据
	 * 
	 * @param shopCartId
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void deleteShopCartByCartId(String shopCartId) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shopCartId);
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART", "DEL_SHOPCART_BY_CARTID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 根据shoppingCartId查询所有订单
	 * 
	 * @param shoppingCartId
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static IDataset getEnableShoppingDetailByCartId(String shoppingCartId) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shoppingCartId);
		return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_ENABLE_BY_ID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 清理购物车订单
	 * 
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static void delShopCart() throws Exception {
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART", "DEL_SHOPCART", new DataMap(), Route.getJourDb(BizRoute.getRouteId()));
	}

	public static void delShopCartDetail() throws Exception {
		Dao.executeUpdateByCodeCode("TF_B_SHOPPING_CART_DETAIL", "DEL_SHOPCART_DETAIL", new DataMap(), Route.getJourDb(BizRoute.getRouteId()));
	}

	public static void delShopCartTrade() throws Exception {
		Dao.executeUpdateByCodeCode("TF_B_TRADE", "DEL_TRADE_BY_SUBSCRIBE_TYPE", new DataMap(), Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 根据detailOrderId获取 订单详细信息
	 * @param detailORderId
	 * @param stateCode
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public static IDataset getShoppingDetailInfoByOrderId(String shopCartId, String detailORderId, String stateCode) throws Exception {
		IData param = new DataMap();
		param.put("SHOPPING_CART_ID", shopCartId);
		param.put("DETAIL_ORDER_ID", detailORderId);
		param.put("DETAIL_STATE_CODE", stateCode);
		return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_SHOPCART_DETAIL_BY_DETAILORDERID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset getProductDateByOrderId(String detailORderId, String modifyTag) throws Exception {
		IData param = new DataMap();
		param.put("ORDER_ID", detailORderId);
		param.put("MODIFY_TAG", modifyTag);
		return Dao.qryByCode("TF_B_SHOPPING_CART_DETAIL", "SEL_PRODUCT_DATE_BYORDERID", param, Route.getJourDbDefault());
	}
	
	public static IDataset getShoppingInfoByStaffUserIds(String staffId, String custId, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_STAFF_ID", staffId);
        param.put("USER_ID", custId);
        param.put("STATE", state);
        return Dao.qryByCode("TF_B_SHOPPING_CART", "SEL_BY_STAFF_USER_IDS", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
