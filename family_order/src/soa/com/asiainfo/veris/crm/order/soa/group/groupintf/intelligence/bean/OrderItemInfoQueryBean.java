package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.asiainfo.veris.crm.iorder.pub.consts.GroupStandardConstans;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class OrderItemInfoQueryBean extends CSBizBean {

	private static final Logger logger= Logger.getLogger(OrderItemInfoQueryBean.class);

	public IData orderItemInfoQuery(IData inParams) throws Exception
	{
		// 订单行 需要 trade order Product 三张表联查
		if(logger.isDebugEnabled()) {
			logger.debug("===========入参=========="+inParams);
		}
		String  tradeId = inParams.getString( "TRADE_ID");
		String  orderId = IDataUtil.chkParam(inParams, "orderId");
		IData input = new DataMap();
		input.put("ORDER_ID", orderId);
		String tradeTypeCode ="";
		IData ordersInfo = new DataMap();
		IDataset set  = Dao.qryByCode("TF_B_TRADE","SEL_BY_ORDER_ID",input );
		if( CollectionUtils.isNotEmpty(set)) {
			tradeTypeCode = set.first().getString("TRADE_TYPE_CODE");
			ordersInfo.putAll( transferDatas( OrderInfoQueryBean.tradeListFilterByTradeId( set ,tradeId) ) );
		}else{
			IDataset seth  = Dao.qryByCode("TF_BH_TRADE","SEL_BY_ORDER_ID",input);
			if( CollectionUtils.isEmpty(seth))
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据订单编号:" + orderId + "未找到订单信息！");
			tradeTypeCode = seth.first().getString("TRADE_TYPE_CODE");
			ordersInfo.putAll( transferDatas( OrderInfoQueryBean.tradeListFilterByTradeId (seth ,tradeId
			) ) );
		}
		//查询 配置信息
		input = new DataMap();
		input.put("ATTR_VALUE", tradeTypeCode);
		input.put("ATTR_CODE", GroupStandardConstans.TRADE_TYPE_CODE);
		IDataset attrSet  = Dao.qryByCode("TD_B_ATTR_BIZ","SEL_BIZTYPE_BY_CODE",input );
		if( CollectionUtils.isEmpty(attrSet))
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "TD_B_ATTR_BIZ表配置信息不存在！");
		String[] orderItemOperatorType = dealAttrObj( attrSet.first().getString("ATTR_OBJ") );

		ordersInfo.put("orderItemOperatorType", orderItemOperatorType[0] );
		ordersInfo.put("orderItemOperatorTypeName", orderItemOperatorType[1]);
		ordersInfo.put("orderItemID", orderId);
		return ordersInfo;
	}
	/**
	 * 将业务类型转换成新增删除修改
	 * @param attrObj
	 * @return
	 * @throws Exception
	 */
	private String[] dealAttrObj(String attrObj) throws Exception
	{
		String[] deal = new String[2];
		if( attrObj.startsWith("Crt")) {
			deal[0] = "0"; //新增
			deal[1] = "新增"; //
		}else if( attrObj.startsWith("Dst")) {
			deal[0] = "1";//修改
			deal[1] = "修改";//
		}else if( attrObj.startsWith("Chg")) {
			deal[0] = "2";//删除
			deal[1] = "删除";//
		}

		return deal;
	}


	/**
	 * 转换出参
	 * @param tradeSet
	 * @return
	 * @throws Exception
	 */
	private IData transferDatas(IDataset tradeSet) throws Exception
	{
		IData ordersInfo = new DataMap();
		IData trade = tradeSet.first();  //不能 取第一条了  应该 根据 TRADE_ID 来过滤
		IDataset goodsInfoList = new DatasetList();
		for (Object object : tradeSet) {
			IData goodsInfo = new DataMap();
			IData temp = (IData) object;
			String productId = temp.getString("PRODUCT_ID");
			String productName = UProductInfoQry.getProductNameByProductId( productId );
			goodsInfo.put("PRODUCT_ID", productId);
			goodsInfo.put("PRODUCT_NAME", productName);
			goodsInfo.put("OFFER_TYPE", "P");
			goodsInfoList.add(goodsInfo);

		}
		String orderItemStatus ="0";
		for (Object object : tradeSet) {
			IData temp = (IData) object;
			if( "6".equals( temp.getString("SUBSCRIBE_STATE") )) {
				orderItemStatus = "1";
				break;
			}
		}
		ordersInfo.put("customerID", trade.getString("CUST_ID") );
		ordersInfo.put("userID", trade.getString("USER_ID") );
		ordersInfo.put("serviceNumber", trade.getString("SERIAL_NUMBER") );
		ordersInfo.put("acceptTime", trade.getString("ACCEPT_DATE").replaceAll("-", "").replace(" ", "") );
		ordersInfo.put("orderNo", trade.getString("TRADE_ID") );
		ordersInfo.put("goodsInfoList", goodsInfoList);
		if( StringUtils.isBlank( orderItemStatus) )
			ordersInfo.put("orderItemStatus", "0" );
		else
			ordersInfo.put("orderItemStatus", orderItemStatus );
		return ordersInfo;
	}
}
