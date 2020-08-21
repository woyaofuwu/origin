package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.BookTradeQueryDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class OrderInfoQueryBean extends CSBizBean {

	private static final Logger logger= Logger.getLogger(OrderInfoQueryBean.class);

	public IData orderInfoQuery(IData inParams) throws Exception
	{
		//  入参需要 orderId和 tradeId 同时查询免得出现遗漏
		if(logger.isDebugEnabled()) {
			logger.debug("===========入参=========="+inParams);
		}
		String orderId = IDataUtil.chkParam(inParams, "orderId");
		String tradeId = inParams.getString( "TRADE_ID");
		IData ordersInfo = new DataMap();
		IData input = new DataMap();
		input.put("ORDER_ID", orderId);
		IDataset set  = Dao.qryByCode("TF_B_TRADE","SEL_BY_ORDER_ID",input );
		if( CollectionUtils.isNotEmpty(set)) {
			set = tradeListFilterByTradeId( set ,tradeId );
			ordersInfo.putAll( transferDatas( set ) );
		}else{
			IDataset seth  = Dao.qryByCode("TF_BH_TRADE","SEL_BY_ORDER_ID",input);
			if( CollectionUtils.isEmpty(seth)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据订单编号:" + orderId + "未找到订单信息！");
			}
			seth = tradeListFilterByTradeId( seth ,tradeId );
			ordersInfo.putAll( transferDatas( seth ) );
		}
		ordersInfo.put("orderNumber", orderId);
		return ordersInfo;
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
		IData trade = tradeSet.first();
		String orderStatus ="0";
		for (Object object : tradeSet) {
			IData temp = (IData) object;
			if( "6".equals( temp.getString("SUBSCRIBE_STATE") )) {
				orderStatus = "1";
				break;
			}
		}
		ordersInfo.put("customerCode", trade.getString("CUST_ID") );
		ordersInfo.put("businessNumber", trade.getString("TRADE_ID") );
		ordersInfo.put("acceptNumber", trade.getString("SERIAL_NUMBER") );
		ordersInfo.put("acceptTime", trade.getString("ACCEPT_DATE").replaceAll("-", "").replace(" ", "") );
		ordersInfo.put("tradeId", trade.getString("TRADE_ID") );
		ordersInfo.put("customName", trade.getString("CUST_NAME"));
		String inModeCode = trade.getString("IN_MODE_CODE");
		IData channelDatas =  StaticInfoQry.getStaticInfoByTypeIdDataId("IN_MODE_CODE",inModeCode);
		ordersInfo.put("acceptChannel", channelDatas.getString("DATA_NAME"));
		ordersInfo.put("orderType", dealSubcribeType(trade.getString("SUBSCRIBE_TYPE")));
		ordersInfo.put("acceptStaffNO", trade.getString("TRADE_STAFF_ID"));
		IDataset tradeUserSet = BookTradeQueryDAO.queryTradeUserInfo(trade);
		if( CollectionUtils.isNotEmpty(tradeUserSet))
			ordersInfo.put("relatedContractNo", tradeUserSet.first().getString("CONTRACT_ID"));
		else
			ordersInfo.put("relatedContractNo", "");
		ordersInfo.put("orderItem", new DatasetList() );

		if( StringUtils.isBlank( orderStatus) )
			ordersInfo.put("orderStatus", "0" );
		else
			ordersInfo.put("orderStatus", orderStatus );
		return ordersInfo;
	}
//	/**
//	 * 转换出参
//	 * @param tradeSet
//	 * @return
//	 * @throws Exception
//	 */
//	private IData transferDatas(IDataset tradeSet ,String tradeId ) throws Exception
//	{
//		IData ordersInfo = new DataMap();
//		String orderStatus ="0";
//		for (Object object : tradeSet) {
//			IData temp = (IData) object;
//			if( StringUtils.equals(temp.getString("TRADE_ID"), tradeId ) ){
//				if( "6".equals( temp.getString("SUBSCRIBE_STATE") )) {
//					orderStatus = "1";
//					break;
//				}
//				IData trade = temp ;
//				ordersInfo.put("customerCode", trade.getString("CUST_ID") );
//				ordersInfo.put("businessNumber", trade.getString("TRADE_ID") );
//				ordersInfo.put("acceptNumber", trade.getString("SERIAL_NUMBER") );
//				ordersInfo.put("acceptTime", trade.getString("ACCEPT_DATE").replaceAll("-", "").replace(" ", "") );
//				ordersInfo.put("tradeId", trade.getString("TRADE_ID") );
//				ordersInfo.put("customName", trade.getString("CUST_NAME"));
//				String inModeCode = trade.getString("IN_MODE_CODE");
//				IData channelDatas =  StaticInfoQry.getStaticInfoByTypeIdDataId("IN_MODE_CODE",inModeCode);
//				ordersInfo.put("acceptChannel", channelDatas.getString("DATA_NAME"));
//				ordersInfo.put("orderType", dealSubcribeType(trade.getString("SUBSCRIBE_TYPE")));
//				ordersInfo.put("acceptStaffNO", trade.getString("TRADE_STAFF_ID"));
//				IDataset tradeUserSet = BookTradeQueryDAO.queryTradeUserInfo(trade);
//				if( CollectionUtils.isNotEmpty(tradeUserSet))
//					ordersInfo.put("relatedContractNo", tradeUserSet.first().getString("CONTRACT_ID"));
//				else
//					ordersInfo.put("relatedContractNo", "");
//				ordersInfo.put("orderItem", new DatasetList() );
//
//				if( StringUtils.isBlank( orderStatus) )
//					ordersInfo.put("orderStatus", "0" );
//				else
//					ordersInfo.put("orderStatus", orderStatus );
//			}
//		}
//		return ordersInfo;
//	}

	/**
	 * 根据tradeId 过滤掉
	 * @param tradeList
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset tradeListFilterByTradeId( IDataset tradeList ,String tradeId) throws Exception{
		for (Object object : tradeList) {
			IData temp = (IData) object;
			if( StringUtils.equals(temp.getString("TRADE_ID"), tradeId ) ){
				return new DatasetList( temp );
			}
		}
		return new DatasetList(  );
	}

	/**
	 *
	 * @param subscribeType
	 * @return
	 * @throws Exception
	 */
	public static String dealSubcribeType(String subscribeType) throws Exception
	{
		//0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，150-批量报文，200-信控执行，201-信控开机';
		String deal = "";
		if( subscribeType.equals("0")) {
			deal = "1"; //普通立即执行
		}else if( subscribeType.equals("1")) {
			deal = "2"; //普通预约执行
		}else if( subscribeType.equals("100")) {
			deal = "3"; //批量立即执行
		}else if( subscribeType.equals("101")) {
			deal = "4"; //批量预约执行
		}else  {
			deal = "5"; //信控
		}

		return deal;
	}
}
