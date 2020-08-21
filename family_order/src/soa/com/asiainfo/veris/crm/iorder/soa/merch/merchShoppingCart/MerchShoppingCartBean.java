package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.order.requestdata.MerchShoppingDetailData;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.ShoppingCartConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.MerchShoppingCartQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class MerchShoppingCartBean extends CSBizBean {

	/**
	 * 获取购物车信息
	 * 
	 * @param tradeStaffId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public IData getShoppingSetByStaffUserId(IData param) throws Exception {
		// 默认查询先不查询宽带相关的订单
		IDataset shoppingCartSet = MerchShoppingCartQry.getShoppingSetByStaffUserId(param.getString("TRADE_STAFF_ID"),
				param.getString("USER_ID"), "0", param.getString("SERIAL_NUMBER"), false);
		if (IDataUtil.isEmpty(shoppingCartSet))
			return new DataMap();
		String shoppingCartId = shoppingCartSet.getData(0).getString("SHOPPING_CART_ID");
		IDataset orderSet = queryShoppingCartDetail(shoppingCartId, getVisit().getStaffEparchyCode());
		IData returnData = new DataMap();
		returnData.put("SHOPPING_CART_ID", shoppingCartId);
		returnData.put("ORDER_SET", orderSet);
		return returnData;
	}

	/**
	 * 获取购物车明细信息
	 * 
	 * @param shoppingCartId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IDataset queryShoppingCartDetail(String shoppingCartId, String eparchyCode) throws Exception {
		IDataset shoppingCartDetailSet = MerchShoppingCartQry.getShoppingDetailById(shoppingCartId, eparchyCode);
		return buildOrderInfos(shoppingCartDetailSet);
	}

	/**
	 * 构建购物车TRADE明细 (加入购物车的订单是一个order多个trade的时候，这个是以order为基准展示的)
	 * 
	 * @param shoppingCartDetailSet
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private IDataset buildOrderInfos(IDataset shoppingCartDetailSet) throws Exception {
		IDataset tradeSet = new DatasetList();
		if (IDataUtil.isEmpty(shoppingCartDetailSet))
			return tradeSet;
		for (int i = 0, size = shoppingCartDetailSet.size(); i < size; i++) {
			IData shoppingCartDetailData = shoppingCartDetailSet.getData(i);
			String orderId = shoppingCartDetailData.getString("DETAIL_ORDER_ID");
			IDataset tradeDataset = TradeInfoQry.queryTradeByOrerId(orderId, "0");
			if (IDataUtil.isEmpty(tradeDataset))
				continue;
			float totalFee = 0;
			IData tradeDetailData = new DataMap();
			IDataset tradeFeeSet = new DatasetList();
			IDataset elementTadeSet = new DatasetList();
			for (int j = 0, tsize = tradeDataset.size(); j < tsize; j++) {
				IData tradeData = tradeDataset.getData(j);
				// 费用
				float tradeFee = buildTradeFeeSet(tradeData.getString("TRADE_ID"),
						tradeData.getString("TRADE_TYPE_CODE"), tradeFeeSet);
				totalFee += tradeFee;
				// 展示元素
				buildTradeElements(tradeData.getString("TRADE_ID"), tradeData.getString("INTF_ID"), elementTadeSet);
			}
			if (IDataUtil.isNotEmpty(elementTadeSet))
				DataHelper.sort(elementTadeSet, "SORT", 3, 0);// 0: 升序, 1:降序
			tradeDetailData.put("ORDER_TYPE_CODE", shoppingCartDetailData.getString("DETAIL_TYPE_CODE"));
			String tradeType = buildTradeType(shoppingCartDetailData.getString("TRADE_TYPE_CODE"), "");
			tradeDetailData.put("ACCEPT_DATE", shoppingCartDetailData.getString("ACCEPT_DATE"));
			tradeDetailData.put("ORDER_ID", orderId);
			tradeDetailData.put("TRADE_TYPE", tradeType);
			tradeDetailData.put("FEE_SET", tradeFeeSet);
			tradeDetailData.put("ELEMENT_TRADE_SET", elementTadeSet);
			tradeDetailData.put("DETAIL_ORDER_ID", orderId);
			tradeDetailData.put("TOTAL_FEE", totalFee / 100);
			tradeSet.add(tradeDetailData);
		}
		return tradeSet;
	}

	/**
	 * 构建购物车TRADE明细 (加入购物车的订单是一个order多个trade的时候，这个是以trade为基准展示的)
	 * 
	 * @param shoppingCartDetailSet
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	// private IDataset buildTradeInfos(IDataset shoppingCartDetailSet) throws
	// Exception {
	// IDataset tradeSet = new DatasetList();
	// if (IDataUtil.isEmpty(shoppingCartDetailSet))
	// return tradeSet;
	// for (int i = 0, size = shoppingCartDetailSet.size(); i < size; i++) {
	// IData shoppingCartDetailData = shoppingCartDetailSet.getData(i);
	// String orderId = shoppingCartDetailData.getString("DETAIL_ORDER_ID");
	// IDataset tradeDataset = TradeInfoQry.queryTradeByOrerId(orderId, "0");
	// if(IDataUtil.isEmpty(tradeDataset))
	// continue;
	// float totalFee = 0;
	// for (int j = 0, tsize = tradeDataset.size(); j < tsize; j++) {
	// IData tradeDetailData = new DataMap();
	// IDataset tradeFeeSet = new DatasetList();
	// IDataset elementTadeSet = new DatasetList();
	// IData tradeData = tradeDataset.getData(j);
	// //费用
	// float tradeFee = buildTradeFeeSet(tradeData.getString("TRADE_ID"),
	// tradeData.getString("TRADE_TYPE_CODE"), tradeFeeSet);
	// totalFee += tradeFee;
	// //展示元素
	// buildTradeElements(tradeData.getString("TRADE_ID"),
	// tradeData.getString("INTF_ID"), elementTadeSet);
	// tradeDetailData.put("ORDER_TYPE_CODE",
	// tradeData.getString("TRADE_TYPE_CODE"));
	// String tradeType = buildTradeType(tradeData.getString("TRADE_TYPE_CODE"),
	// "");
	// tradeDetailData.put("ACCEPT_DATE", tradeData.getString("ACCEPT_DATE"));
	// tradeDetailData.put("TRADE_ID", tradeData.getString("TRADE_ID"));
	// tradeDetailData.put("TRADE_TYPE", tradeType);
	// tradeDetailData.put("FEE_SET", tradeFeeSet);
	// tradeDetailData.put("ELEMENT_TRADE_SET", elementTadeSet);
	// tradeDetailData.put("DETAIL_ORDER_ID", orderId);
	// tradeDetailData.put("TOTAL_FEE", totalFee/100);
	// tradeSet.add(tradeDetailData);
	// }
	// }
	// return tradeSet;
	// }

	/**
	 * 展示业务名称
	 * 
	 * @param tradeTypeCode
	 * @param tradeType
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public String buildTradeType(String tradeTypeCode, String tradeType) throws Exception {
		String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
		if (StringUtils.isNotBlank(tradeType))
			return tradeType + "<BR/>" + tradeTypeName;
		return tradeTypeName;
	}

	/**
	 * 构建费用插入的串
	 * 
	 * @param tradeId
	 * @param tradeTypeCode
	 * @param tradeFeeSet
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public float buildTradeFeeSet(String tradeId, String tradeTypeCode, IDataset tradeFeeSet) throws Exception {
		IDataset feeSet = TradefeeSubInfoQry.getFeeListByTrade(tradeId);
		if (IDataUtil.isEmpty(feeSet))
			return 0;
		float tradeFee = 0;
		for (int i = 0; i < feeSet.size(); i++) {
			IData temp = feeSet.getData(i);
			String feeMode = temp.getString("FEE_MODE");
			String feeTypeCode = temp.getString("FEE_TYPE_CODE");
			String feeTypeName = "";
			if("0".equals(feeMode))//营业费
			{
				feeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_B_FEEITEM", "FEEITEM_CODE", "FEEITEM_NAME", feeTypeCode);
			}else if("1".equals(feeMode))//押金
			{
				feeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_FOREGIFT", "FOREGIFT_CODE", "FOREGIFT_NAME", feeTypeCode);
			}else if("2".equals(feeMode))//预存
			{
				feeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_B_PAYMENT", "PAYMENT_ID", "PAYMENT", feeTypeCode);
			}
			
			temp.put("FEE_MODE_NAME", StaticUtil.getStaticValue("CS_FEE_MODE", feeMode));
			temp.put("FEE_TYPE_NAME", feeTypeName);
			temp.put("TRADE_TYPE_CODE", tradeTypeCode);
			temp.put("USER_ID", temp.getString("RSRV_STR7", ""));
			temp.put("MERCH_ID", temp.getString("RSRV_STR8", ""));
			temp.put("MERCH_NAME", temp.getString("RSRV_STR9", "未知_" + i));
			tradeFee += Float.parseFloat(temp.getString("FEE"));
		}
		tradeFeeSet.addAll(feeSet);
		return tradeFee;
	}

	/**
	 * 构建TRADE元素台帐明细
	 * 
	 * @param tradeId
	 * @param intfId
	 * @param elementTadeSet
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void buildTradeElements(String tradeId, String intfId, IDataset elementTadeSet) throws Exception {
		String tradeTableArr[] = intfId.split(",");
		for (int i = 0, length = tradeTableArr.length; i < length; i++) {
			if (StringUtils.equals("TF_B_TRADE_SVC", tradeTableArr[i])) {
				IDataset svcDatset = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
				buildSvcElements(svcDatset, elementTadeSet, true, 1);
			} else if (StringUtils.equals("TF_B_TRADE_DISCNT", tradeTableArr[i])) {
				IDataset disDatset = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
				buildDisElements(disDatset, elementTadeSet, 3);
			} else if (StringUtils.equals("TF_B_TRADE_SALE_ACTIVE", tradeTableArr[i])) {
				IDataset saleDatset = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
				buildSaleElements(saleDatset, elementTadeSet, 4);
			} else if (StringUtils.equals("TF_B_TRADE_PLATSVC", tradeTableArr[i])) {
				IDataset platSvcSet = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
				buildSvcElements(platSvcSet, elementTadeSet, false, 2);
			} else if (StringUtils.equals("TF_B_TRADE_PRODUCT", tradeTableArr[i])) {
				IDataset productSet = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
				buildProductElements(productSet, elementTadeSet, 0);
			}
		}
	}

	/**
	 * 组装服务数据
	 * 
	 * @param svcDatset
	 * @param returnDataset
	 * @param flag
	 * @param sort
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void buildSvcElements(IDataset svcDatset, IDataset returnDataset, boolean flag, int sort) throws Exception {
		if (IDataUtil.isEmpty(svcDatset))
			return;
		for (int i = 0, size = svcDatset.size(); i < size; i++) {
			IData returnSvcData = new DataMap();
			IData svcData = svcDatset.getData(i);
			returnSvcData.put("ELEMENT_ID", svcData.getString("SERVICE_ID"));
			returnSvcData.put("ELEMENT_NAME", flag ? USvcInfoQry.getSvcNameBySvcId(svcData.getString("SERVICE_ID"))
					: UPlatSvcInfoQry.getSvcNameBySvcId(svcData.getString("SERVICE_ID")));
			returnSvcData.put("ELEMENT_TYPE_CODE",
					flag ? BofConst.ELEMENT_TYPE_CODE_SVC : BofConst.ELEMENT_TYPE_CODE_PLATSVC);
			returnSvcData.put("MODIFY_TAG", svcData.getString("MODIFY_TAG"));
			returnSvcData.put("START_DATE", svcData.getString("START_DATE"));
			returnSvcData.put("END_DATE", svcData.getString("END_DATE"));
			returnSvcData.put("INST_ID", svcData.getString("INST_ID"));
			returnSvcData.put("SORT", sort);
			returnDataset.add(returnSvcData);
		}
	}

	/**
	 * 组装优惠数据
	 * 
	 * @param disDatset
	 * @param returnDataset
	 * @param sort
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void buildDisElements(IDataset disDatset, IDataset returnDataset, int sort) throws Exception {
		if (IDataUtil.isEmpty(disDatset))
			return;
		for (int i = 0, size = disDatset.size(); i < size; i++) {
			IData discntData = disDatset.getData(i);
			IData returnDiscntData = new DataMap();
			returnDiscntData.put("ELEMENT_ID", discntData.getString("DISCNT_CODE"));
			returnDiscntData.put("ELEMENT_NAME",
					UDiscntInfoQry.getDiscntNameByDiscntCode(discntData.getString("DISCNT_CODE")));
			returnDiscntData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
			returnDiscntData.put("MODIFY_TAG", discntData.getString("MODIFY_TAG"));
			returnDiscntData.put("START_DATE", discntData.getString("START_DATE"));
			returnDiscntData.put("END_DATE", discntData.getString("END_DATE"));
			returnDiscntData.put("INST_ID", discntData.getString("INST_ID"));
			returnDiscntData.put("SORT", sort);
			returnDataset.add(returnDiscntData);
		}
	}

	/**
	 * 组装营销活动数据
	 * 
	 * @param saleDatset
	 * @param returnDataset
	 * @param sort
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void buildSaleElements(IDataset saleDatset, IDataset returnDataset, int sort) throws Exception {
		if (IDataUtil.isEmpty(saleDatset))
			return;
		for (int i = 0, size = saleDatset.size(); i < size; i++) {
			IData returnSaleActiveData = new DataMap();
			IData saleActiveData = saleDatset.getData(i);
			returnSaleActiveData.put("ELEMENT_ID", saleActiveData.getString("PACKAGE_ID"));
			returnSaleActiveData.put("ELEMENT_NAME", saleActiveData.getString("PACKAGE_NAME"));
			returnSaleActiveData.put("ELEMENT_TYPE_CODE", saleActiveData.getString("CAMPN_TYPE"));
			returnSaleActiveData.put("MODIFY_TAG", saleActiveData.getString("MODIFY_TAG"));
			returnSaleActiveData.put("START_DATE", saleActiveData.getString("START_DATE"));
			returnSaleActiveData.put("END_DATE", saleActiveData.getString("END_DATE"));
			returnSaleActiveData.put("INST_ID", saleActiveData.getString("INST_ID"));
			returnSaleActiveData.put("SORT", sort);
			returnDataset.add(returnSaleActiveData);
		}
	}

	/**
	 * 组装产品数据
	 * 
	 * @param productDatset
	 * @param returnDataset
	 * @param sort
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void buildProductElements(IDataset productDatset, IDataset returnDataset, int sort) throws Exception {
		if (IDataUtil.isEmpty(productDatset))
			return;
		for (int i = 0, size = productDatset.size(); i < size; i++) {
			IData returnProductData = new DataMap();
			IData productData = productDatset.getData(i);
			returnProductData.put("ELEMENT_ID", productData.getString("PRODUCT_ID"));
			returnProductData.put("ELEMENT_NAME", UProductInfoQry.getProductNameByProductId(productData.getString("PRODUCT_ID")));
			returnProductData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			returnProductData.put("MODIFY_TAG", productData.getString("MODIFY_TAG"));
			returnProductData.put("START_DATE", productData.getString("START_DATE"));
			returnProductData.put("END_DATE", productData.getString("END_DATE"));
			returnProductData.put("MAIN_TAG", productData.getString("MAIN_TAG"));
			returnProductData.put("INST_ID", productData.getString("INST_ID"));
			returnProductData.put("SORT", sort);
			returnDataset.add(returnProductData);
		}
	}

	/**
	 * 购物车提交时，修改购物车及购物车明细、购物车台帐信息
	 * 
	 * @param shoppingCartId
	 * @param orderId
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void dealCartBySubmit(String shoppingCartId, String orderId, String detailOrderIds) throws Exception {
		IDataset shoppingCartDetailSet = MerchShoppingCartQry.getAllShoppingDetailByCartId(shoppingCartId);
		if (IDataUtil.isEmpty(shoppingCartDetailSet))
			return;
		// TF_B_SHOPPING_CART_DETAIL表里面记录是否全部处理完毕
		boolean flag = true;
		for (int x = 0, s0 = shoppingCartDetailSet.size(); x < s0; x++) {
			IData cartDetailData = shoppingCartDetailSet.getData(x);
			String detailOrderId = cartDetailData.getString("DETAIL_ORDER_ID");
			if (detailOrderIds.indexOf(detailOrderId) == -1) {
				flag = false;
				continue;
			}
			// 更新 TF_B_SHOPPING_CART_DETAIL 表中的状态
			MerchShoppingCartQry.updateCartDetailByStateCode(detailOrderId,
					ShoppingCartConst.SHOPPING_DETAIL_STATE_CODE_SUBMIT);
			MerchShoppingCartQry.insertShopDetailHisByShopIdOrderId(shoppingCartId, detailOrderId);
			MerchShoppingCartQry.deleteShopDetailByCartIdOrderid(shoppingCartId, detailOrderId);
			// 删除tf_b_trade表的购物车数据
			MerchShoppingCartQry.deleteTradeByOrderId(detailOrderId);
		}
		// 更新 TF_B_SHOPPING_CART 表中的状态
		if (flag) {
			MerchShoppingCartQry.updateShoppingCartState(shoppingCartId, orderId, "2");
			MerchShoppingCartQry.insertShopCartHisByShopCartId(shoppingCartId);
			MerchShoppingCartQry.deleteShopCartByCartId(shoppingCartId);
		}
	}

	/**
	 * 根据deatail_order_id删除购物车明细订单
	 * 
	 * @param detailOrderId
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void deleteOrder(String detailOrderId, String shopCartId) throws Exception {
		// 释放资源
		releaseResourseByTrade(shopCartId, detailOrderId);

		// 更新购物车明细表里面的状态
		MerchShoppingCartQry.updateCartDetailByStateCode(detailOrderId,
				ShoppingCartConst.SHOPPING_DETAIL_STATE_CODE_DEL);

		// 搬到历史表里面
		MerchShoppingCartQry.insertShopDetailHisByShopIdOrderId(shopCartId, detailOrderId);
		MerchShoppingCartQry.deleteShopDetailByCartIdOrderid(shopCartId, detailOrderId);

		// 如果已经子订单已经删除完了,则把TF_B_SHOPPING_CART表的数据也移到历史表里面去
		IDataset shopDetailSet = MerchShoppingCartQry.getAllShoppingDetailByCartId(shopCartId);
		if (IDataUtil.isEmpty(shopDetailSet)) {
			MerchShoppingCartQry.updateShoppingCartState(shopCartId, "", "1");
			MerchShoppingCartQry.insertShopCartHisByShopCartId(shopCartId);
			MerchShoppingCartQry.deleteShopCartByCartId(shopCartId);
		}

		// 删除tf_b_trade表的台账
		MerchShoppingCartQry.deleteTradeByOrderId(detailOrderId);
	}

	/**
	 * 释放资源
	 * 
	 * @param shopCartId
	 * @param detailOrderId
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private void releaseResourseByTrade(String shopCartId, String detailOrderId) throws Exception {
		IDataset detailSet = MerchShoppingCartQry.getShoppingDetailInfoByOrderId(shopCartId, detailOrderId,
				ShoppingCartConst.SHOPPING_DETAIL_STATE_CODE_NORMAL);
		if (IDataUtil.isEmpty(detailSet))
			return;
		IData detailData = detailSet.getData(0);
		String releaseSvcStr = detailData.getString("RELEALSE_SVC_DATA");
		if (StringUtils.isBlank(releaseSvcStr))
			return;

		IDataset releaseSvcSet = new DatasetList(releaseSvcStr);
		for (int i = 0, size = releaseSvcSet.size(); i < size; i++) {
			IData releaseData = releaseSvcSet.getData(i);
			String svcName = releaseData.getString("RELEALSE_SVC");
			CSAppCall.call(svcName, releaseData);
		}
	}

	/**
	 * 校验购物车的订单是否可以被取消 (有点麻烦)
	 * 
	 * @param detailOrderId
	 * @param shopCartId
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
//	public boolean checkIsuAllowCancel(String detailOrderId, String shopCartId) throws Exception {
//		IDataset shopDetailSet = MerchShoppingCartQry.getAllShoppingDetailByCartId(shopCartId);
//		if (IDataUtil.isEmpty(shopDetailSet) || shopDetailSet.size() == 1) {
//			return false;
//		}
//		// 1.新增的取消： 考虑是否被购物车已有的依赖 (也有可能依赖于用户已有的)
//		// 2.取消的回复: 要考虑与购物车已有的互斥
//		IDataset tradeSet = TradeInfoQry.queryTradeByOrerIdAndCancelTag(detailOrderId, "0");
//		if (IDataUtil.isEmpty(tradeSet))
//			return false;
//
//		return true;
//	}

	/**
	 * 清理购物车订单 (每天凌晨0点0分1秒点清理前一天的订单)
	 * 
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IData clearShoppingCartOrder() throws Exception {
		IData returnData = new DataMap();
		MerchShoppingCartQry.delShopCart();
		MerchShoppingCartQry.delShopCartDetail();
		MerchShoppingCartQry.delShopCartTrade();
		returnData.put("X_RESULT_CODE", "0");
		returnData.put("X_RESULT_INFO", "清理成功");
		return returnData;
	}

	/**
	 * 获取购物车里面的所有变更的元素
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IDataset getShoppingCartAllElements(IData param) throws Exception {
		IDataUtil.chkParam(param, "SERIAL_NUMBER");
		String sn = param.getString("SERIAL_NUMBER", "");
		String userId = param.getString("USER_ID", "");
		String staffId = StringUtils.isNotBlank(param.getString("TRADE_STAFF_ID")) ? param.getString("TRADE_STAFF_ID")
				: getVisit().getStaffId();
		if (StringUtils.isBlank(userId))
			userId = getUserIdBySn(sn);

		IDataset offerSet = new DatasetList();
		IDataset shoppingCartSet = MerchShoppingCartQry.getShoppingSetByStaffUserId(staffId, userId, "0", sn, false);
		if (IDataUtil.isEmpty(shoppingCartSet))
			return offerSet;
		String shoppingCartId = shoppingCartSet.getData(0).getString("SHOPPING_CART_ID");
		IDataset shoppingCartDetailSet = MerchShoppingCartQry.getShoppingDetailById(shoppingCartId,
				getVisit().getStaffEparchyCode());
		if (IDataUtil.isEmpty(shoppingCartDetailSet))
			return offerSet;

		IDataset allElemetnSet = qryElementSet(shoppingCartDetailSet);
		if (IDataUtil.isEmpty(allElemetnSet))
			return offerSet;

		transferDataSet(offerSet, allElemetnSet);
		return offerSet;
	}

	/**
	 * 转换数据
	 * 
	 * @param offerSet
	 * @param allElemetnSet
	 * @author liujian7
	 * @date
	 */
	public void transferDataSet(IDataset offerSet, IDataset allElemetnSet) {
		for (int i = 0, size = allElemetnSet.size(); i < size; i++) {
			IData elementData = allElemetnSet.getData(i);
			IData offerData = new DataMap();
			offerData.put("OFFER_CODE", elementData.getString("ELEMENT_ID"));
			offerData.put("OFFER_NAME", elementData.getString("ELEMENT_NAME"));
			offerData.put("OFFER_TYPE", elementData.getString("ELEMENT_TYPE_CODE"));
			offerData.put("MODIFY_TAG", elementData.getString("MODIFY_TAG"));
			offerData.put("START_DATE", elementData.getString("START_DATE"));
			offerData.put("END_DATE", elementData.getString("END_DATE"));
			if(BofConst.ELEMENT_TYPE_CODE_PRODUCT.equalsIgnoreCase(elementData.getString("ELEMENT_TYPE_CODE"))){
				offerData.put("MAIN_TAG", elementData.getString("MAIN_TAG", ""));
			}
			offerSet.add(offerData);
		}
	}

	/**
	 * 查询所有的元素集合
	 * 
	 * @param shoppingCartDetailSet
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private IDataset qryElementSet(IDataset shoppingCartDetailSet) throws Exception {
		IDataset allElemetnSet = new DatasetList();
		for (int i = 0, size = shoppingCartDetailSet.size(); i < size; i++) {
			IData shoppingCartDetailData = shoppingCartDetailSet.getData(i);
			String orderId = shoppingCartDetailData.getString("DETAIL_ORDER_ID");
			IDataset tradeDataset = TradeInfoQry.queryTradeByOrerId(orderId, "0");
			if (IDataUtil.isEmpty(tradeDataset))
				continue;
			IDataset elementTadeSet = new DatasetList();
			for (int j = 0, tsize = tradeDataset.size(); j < tsize; j++) {
				IData tradeData = tradeDataset.getData(j);
				buildTradeElements(tradeData.getString("TRADE_ID"), tradeData.getString("INTF_ID"), elementTadeSet);
			}
			allElemetnSet.addAll(elementTadeSet);
		}
		return allElemetnSet;
	}

	/**
	 * 获取用户的userId
	 * 
	 * @param sn
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private String getUserIdBySn(String sn) throws Exception {
		IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
		if (IDataUtil.isEmpty(userInfo))
			CSAppException.apperr(CrmUserException.CRM_USER_1);
		return userInfo.getString("USER_ID");
	}
	
	/**
	 * 提交前校验提示元素的修改时间
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IDataset submitShoppingCartBeforeCheck(IData param) throws Exception {
		IDataUtil.chkParamNoStr(param, "SHOPPING_CART_ID");
		IDataUtil.chkParamNoStr(param, "DETAIL_ORDER_IDS");
		String shoppingCartId = param.getString("SHOPPING_CART_ID");
		String detailShppingOrderIds = param.getString("DETAIL_ORDER_IDS");
		List<MerchShoppingDetailData> selectedShoppingList = new ArrayList<MerchShoppingDetailData>();
		IDataset shoppingDetailSet = MerchShoppingCartQry.getEnableShoppingDetailByCartId(shoppingCartId);
		if (IDataUtil.isNotEmpty(shoppingDetailSet))
        {
		    String userId = shoppingDetailSet.getData(0).getString("USER_ID");
		    UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		    String userProductId = uca.getProductId();
		    for (int i = 0, size = shoppingDetailSet.size(); i < size; i++) {
		        IData shoppingCartDetail = shoppingDetailSet.getData(i);
		        MerchShoppingDetailData detailData = new MerchShoppingDetailData(shoppingCartDetail);
		        String detailOrderId = detailData.getDetailOrderId();
		        if (detailShppingOrderIds.indexOf(detailOrderId) == -1 || IDataUtil.isEmpty(detailData.getDetailRequestData()))
		            continue;
		        IData shoppingdetailData = detailData.getDetailRequestData();
		        String newProductId = shoppingdetailData.getString("NEW_PRODUCT_ID");
		        if (StringUtils.isNotBlank(newProductId))
                {
		            IDataset selectedElements = new DatasetList(shoppingdetailData.getString("SELECTED_ELEMENTS", "[]"));
		            if (IDataUtil.isNotEmpty(selectedElements))
                    {
                        for (int j = 0; j < selectedElements.size(); j++)
                        {
                            IData selectedElement = selectedElements.getData(j);
                            String shoppingProductId = selectedElement.getString("PRODUCT_ID");
                            String shoppingModify = selectedElement.getString("MODIFY_TAG");
                            
                            if (StringUtils.equals(BofConst.MODIFY_TAG_DEL, shoppingModify)&&!StringUtils.equals(userProductId, shoppingProductId))
                            {
                                CSAppException.appError("-1", "购物车添加商品信息过期，购物车中用户变更前的主产品与用户现有的主产品不一致，不能继续办理，请清空购物车重新添加！");
                            }
                        }
                    }
                }
		        selectedShoppingList.add(detailData);
		    }
            
        }
		String tipInfo = modifyRequestData(selectedShoppingList, "");
		IDataset returnSet = new DatasetList();
		IData data = new DataMap();
		data.put("TIP_INFO", tipInfo);
		returnSet.add(data);
		return returnSet;
	}
	
	/**
	 * 修改服务优惠的product_id 和 package_id
	 * 
	 * @param shoppingDetailList
	 * @param btd
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public String modifyRequestData(List<MerchShoppingDetailData> shoppingDetailList, String userMainProductId)
			throws Exception {
		StringBuffer tipBuffer = new StringBuffer();
		IData productData = getNewProductId(shoppingDetailList, userMainProductId);
		String newProductId = productData.getString("NEW_PRODUCT_ID");
//		if(StringUtils.isBlank(newProductId))
//			return "";
		
		ProductTimeEnv env = new ProductTimeEnv();
		env.setBasicAbsoluteStartDate(productData.getString("NEW_PRODUCT_START_DATE"));
		env.setBasicAbsoluteCancelDate(productData.getString("OLD_PRODUCT_END_DATE"));
		
		int count = 1;
		for (int i = 0, size = shoppingDetailList.size(); i < size; i++) {
			MerchShoppingDetailData detailData = shoppingDetailList.get(i);
			String typeCode = detailData.getDetailTypeCode();
			IData requestData = detailData.getDetailRequestData();
			if("240".equals(typeCode)) 
			{
				String relPackageId = requestData.getString("REL_PACKAGE_ID");
				String netPackageId = requestData.getString("NET_PACKAGE_ID");
				if(StringUtils.isBlank(relPackageId) && StringUtils.isBlank(netPackageId))//没有顺延购物车的活动
				{
					String reqPackageId = requestData.getString("PACKAGE_ID");
					String reqStartDate = requestData.getString("START_DATE");
					String reqEndDate = requestData.getString("END_DATE");
					String reqNetStartDate = requestData.getString("ONNET_START_DATE");
					String reqNetEndDate = requestData.getString("ONNET_END_DATE");
					
					IData saleactive = UPackageExtInfoQry.qryPkgExtEnableByPackageId(reqPackageId);
			        saleactive.put("CAMPN_TYPE", requestData.getString("CAMPN_TYPE"));
			        saleactive.put("PRODUCT_ID", requestData.getString("PRODUCT_ID"));
			        saleactive.put("SERIAL_NUMBER", requestData.getString("SERIAL_NUMBER"));
			        saleactive.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
			        IDataset activeDates = CSAppCall.call("CS.SaleActiveDateSVC.callActiveStartEndDate", saleactive);
			        IData activeDate = activeDates.getData(0);
			        String callStartDate = activeDate.getString("START_DATE", "");
					String callEndDate = activeDate.getString("END_DATE", "");
					String callNetStartDate = activeDate.getString("ONNET_START_DATE", "");
					String callNetEndDate = activeDate.getString("ONNET_END_DATE", "");
					
					String reqPakcageName = UPackageInfoQry.getPackageNameByPackageId(reqPackageId);
			        if(!StringUtils.equals(reqStartDate.substring(0, 10), callStartDate.substring(0, 10)))
			        {	
			        	StringBuffer errorBuff = new StringBuffer();
			        	errorBuff.append("营销活动").append("【" + reqPackageId + "|" + reqPakcageName + "】").append("结算时，活动开始时间与加入购物车时的开始时间有变化，请先删除")
			        		.append("【" + reqPackageId + "|" + reqPakcageName + "】").append("活动，重新加入购物车后，再结算！").append("【" + callStartDate + "】");
			        	CSAppException.apperr(CrmCommException.CRM_COMM_103, errorBuff.toString());
			        }
			        else if(!StringUtils.equals(reqEndDate, callEndDate))
			        {
			        	StringBuffer errorBuff = new StringBuffer();
			        	errorBuff.append("营销活动").append("【" + reqPackageId + "|" + reqPakcageName + "】").append("结算时，活动结束时间与加入购物车时的结束时间有变化，请先删除")
			        		.append("【" + reqPackageId + "|" + reqPakcageName + "】").append("活动，重新加入购物车后，再结算！").append("【" + callEndDate + "】");
			        	CSAppException.apperr(CrmCommException.CRM_COMM_103, errorBuff.toString());
			        }
			        else if(!StringUtils.equals(reqNetStartDate, callNetStartDate))
			        {
			        	StringBuffer errorBuff = new StringBuffer();
			        	errorBuff.append("营销活动").append("【" + reqPackageId + "|" + reqPakcageName + "】").append("结算时，活动约定在网开始时间与加入购物车时的约定在网开始时间有变化，请先删除")
			        		.append("【" + reqPackageId + "|" + reqPakcageName + "】").append("活动，重新加入购物车后，再结算！").append("【" + callNetStartDate + "】");
			        	CSAppException.apperr(CrmCommException.CRM_COMM_103, errorBuff.toString());
			        }
			        else if(!StringUtils.equals(reqNetEndDate, callNetEndDate))
			        {
			        	StringBuffer errorBuff = new StringBuffer();
			        	errorBuff.append("营销活动").append("【" + reqPackageId + "|" + reqPakcageName + "】").append("结算时，活动约定在网结束时间与加入购物车时的约定在网结束时间有变化，请先删除")
			        		.append("【" + reqPackageId + "|" + reqPakcageName + "】").append("活动，重新加入购物车后，再结算！").append("【" + callNetEndDate + "】");
			        	CSAppException.apperr(CrmCommException.CRM_COMM_103, errorBuff.toString());
			        }	        	
//			        	tipBuffer.append("已存在有效的营销活动，此营销包只能顺延办理！活动顺延时间为:[");
//			        	tipBuffer.append(activeDate.getString("BOOK_DATE"));
//			        	tipBuffer.append("]；约定在网顺延时间为:[").append(activeDate.getString("ONNET_START_DATE"));
//			        	tipBuffer.append("]。是否继续办理?");
				}
			}else {
				if (!StringUtils.equals(typeCode, "110")
						|| StringUtils.isNotBlank(requestData.getString("NEW_PRODUCT_ID", "")))
					continue;
				IDataset selectElementSetTmp = new DatasetList(requestData.getString("SELECTED_ELEMENTS"));
				for (int j = 0, jsize = selectElementSetTmp.size(); j < jsize; j++) {
					IData selectedData = selectElementSetTmp.getData(j);
					String modifyTag = selectedData.getString("MODIFY_TAG");
					if (!StringUtils.equals(modifyTag, "0"))
						continue;
					IData elementData = ProductElementsCache.getElement(newProductId,
							selectedData.getString("ELEMENT_ID", ""), selectedData.getString("ELEMENT_TYPE_CODE", ""));
					if (IDataUtil.isEmpty(elementData)) {
						String productEndDate = productData.getString("OLD_PRODUCT_END_DATE");
						if(StringUtils.isBlank(productEndDate))
							continue;
						// 计算元素的结束时间
//						String endDate = cacElementDate(selectedData.getString("ELEMENT_ID", ""),
//								selectedData.getString("ELEMENT_TYPE_CODE", ""), selectedData.getString("END_DATE", ""),
//								productEndDate, newProductId);
						
						ProductModuleData pmd = new DiscntData(selectedData);
						String endDate = ProductModuleCalDate.calCancelDate(pmd, env);
						selectedData.put("END_DATE", endDate);
						
						tipBuffer.append(count + "：");
						tipBuffer.append("新套餐【" + newProductId + "】【" + UProductInfoQry.getProductNameByProductId(newProductId) + "】不能订购商品【" + selectedData.getString("ELEMENT_ID", "") + "】【" + selectedData.getString("ELEMENT_NAME", "") + "】,");
						tipBuffer.append("因此商品【" + selectedData.getString("ELEMENT_ID", "") + "】的结束时间将被改成" + endDate + "；");
						tipBuffer.append("<br/>");
						count++;
					} else {
						if(!StringUtils.equals(productData.getString("SYS_DATE"), productData.getString("NEW_PRODUCT_START_DATE").substring(0, 10)))
						{
							//根据新产品重新计算生失效时间
							this.dealElementDate(selectedData, productData, env);
							
							tipBuffer.append(count + "：");
							tipBuffer.append("此次结算存在预约套餐变更【" + newProductId + "】【" + UProductInfoQry.getProductNameByProductId(newProductId) + "】，");
							tipBuffer.append("因此商品【" + selectedData.getString("ELEMENT_ID", "") + "】【" + selectedData.getString("ELEMENT_NAME", "") + "】的开始时间/结束时间将被改成");
							tipBuffer.append(selectedData.getString("START_DATE", "") + "~");
							tipBuffer.append(selectedData.getString("END_DATE", "") + "；");
							tipBuffer.append("<br/>");
							
							count++;
						}
						
						selectedData.put("PRODUCT_ID", newProductId);
						selectedData.put("PACKAGE_ID", elementData.getString("PACKAGE_ID", "-1"));
					}
				}
				requestData.put("SELECTED_ELEMENTS", selectElementSetTmp.toString());
				detailData.setDetailRequestData(requestData);
			}
		}
		
		if(StringUtils.isNotBlank(tipBuffer)) 
		{
			tipBuffer.append("请告知用户！");
		}
		return tipBuffer.toString();
	}
	
	/**
	 * 获取用户变更后的产品ID 默认先从购物车里面获取，获取不到则去查询用户的 new_product_id
	 * 
	 * @param shoppingDetailList
	 * @param btd
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IData getNewProductId(List<MerchShoppingDetailData> shoppingDetailList, String userMainProductId)
			throws Exception {
		IData productData = new DataMap();
		String newProductId = "";
		for (int i = 0, size = shoppingDetailList.size(); i < size; i++) {
			MerchShoppingDetailData detailData = shoppingDetailList.get(i);
			// 就是这笔业务的order_type_code
			String typeCode = detailData.getDetailTypeCode();
			newProductId = detailData.getDetailRequestData().getString("NEW_PRODUCT_ID", "");
			if (!StringUtils.equals(typeCode, "110") || StringUtils.isBlank(newProductId))
				continue;
			
			productData.put("NEW_PRODUCT_START_DATE", detailData.getDetailRequestData().getString("BOOKING_DATE", ""));
			productData.put("SYS_DATE", SysDateMgr.getSysDate());
			String oldProductDate = getOldProductDate(detailData.getDetailOrderId(), "1");
			if(StringUtils.isNotBlank(oldProductDate))
				productData.put("OLD_PRODUCT_END_DATE", oldProductDate);
			break;
		}
//		productData.put("NEW_PRODUCT_ID", StringUtils.isNotBlank(newProductId) ? newProductId : userMainProductId);
		productData.put("NEW_PRODUCT_ID", newProductId);
		return productData;
	}
	
	/**
	 * 获取老产品的结束时间
	 * @param orderId
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	private String getOldProductDate(String orderId, String modifytag) throws Exception {
		IDataset oldProductSet = MerchShoppingCartQry.getProductDateByOrderId(orderId, modifytag);
		return IDataUtil.isNotEmpty(oldProductSet) ? oldProductSet.getData(0).getString("END_DATE") : "";
	}
	
	/**
	 * 判断 元素的时间和  老产品的结束时间， 取小的时间
	 * 
	 * @param elementId
	 * @param elementTypeCode
	 * @param elementEndDate
	 * @param productEndDate
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public String cacElementDate(String elementId, String elementTypeCode, String elementEndDate, String productEndDate, String newProductId)
			throws Exception {
		// 小时包的逻辑
		IDataset hourSet = ParamInfoQry.getCommparaByCode1("CSM", "9991", "DATA_PACK_HOUR_DISCNT", elementId, "ZZZZ");
		int result = SysDateMgr.compareTo(elementEndDate, productEndDate);
		if(IDataUtil.isNotEmpty(hourSet) && result > 0) {
			CSAppException.appError("-1", "套餐【" + newProductId +"】不能订购商品【" + elementId + "】");
		}
		return result >= 0 ? productEndDate : elementEndDate;
	}
	
	public IDataset checkBoxAction(IData input) throws Exception
	{
		String serialNumber = input.getString("SERIAL_NUMBER");
		IDataset selectedEls = new DatasetList(input.getString("SELECTED_ELEMENTS", "[]"));
		IData eles = new DataMap(input.getString("ELEMENTS", "{}"));
		String shoppingCartId = input.getString("SHOPPING_CART_ID");
		String shoppingOrderId = input.getString("DETAIL_ORDER_ID");
		boolean isMainChange = isMainProductChange(shoppingCartId,shoppingOrderId);
		
		String userProductId = null;
		String nextProductId = null;
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String acceptTime = SysDateMgr.getSysTime();
		DataBusManager.getDataBus().setAcceptTime(acceptTime);
		ProductTradeData userProduct = uca.getUserMainProduct();
		if (userProduct != null)
		{
			userProductId = userProduct.getProductId();
		}
		ProductTradeData nextProduct = uca.getUserNextMainProduct();
		if (nextProduct != null)
		{
			nextProductId = nextProduct.getProductId();
		}
		
		IDataset selectedElements = new DatasetList();
		if(IDataUtil.isNotEmpty(selectedEls))
		{
			for(int i=0;i<selectedEls.size();i++)
			{
				IData temp = selectedEls.getData(i);
				IDataset elementTradeSet = temp.getDataset("ELEMENT_TRADE_SET");
				if(IDataUtil.isNotEmpty(elementTradeSet))
				{
					selectedElements.addAll(elementTradeSet);
				}
			}
		}
		
		IDataset elements = new DatasetList();
		if(IDataUtil.isNotEmpty(eles))
		{
			IDataset elementTradeSet = eles.getDataset("ELEMENT_TRADE_SET");
			StringBuilder elementsBulider = new StringBuilder();
			String elementsStr ="";
			if(IDataUtil.isNotEmpty(elementTradeSet))
			{
			    
				for(int i=0;i<elementTradeSet.size();i++)
				{
					IData element = elementTradeSet.getData(i);
					elementsBulider = elementsBulider.append(element.getString("ELEMENT_TYPE_CODE")+"_"+element.getString("ELEMENT_ID")+"|");
					if("1".equals(element.getString("MODIFY_TAG")))
					{
						element.put("MODIFY_TAG", "0");
					}else {
						element.put("MODIFY_TAG", "1");
					}
//					element.put("MAIN_TAG", "0");
//					element.put("PRODUCT_ID", "0");
//					element.put("MAIN_TAG", "0");
				}
				elementsStr = elementsBulider.toString();
			}
			
			//变更主产品加购物车删除时，考虑用户订购但未加入的商品
            if (isMainChange)
            {
                IDataset userElements = new DatasetList();
                List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
                List<SvcTradeData> userSVcs = uca.getUserSvcs();
                if (null != userDiscnts)
                {
                    for (int j = 0; j < userDiscnts.size(); j++)
                    {
                        DiscntTradeData userDiscnt = userDiscnts.get(j);
                        String discntCode = userDiscnt.getDiscntCode();
                        if (!elementsStr.contains("D_" + discntCode))
                        {
                            IData discntEle = new DataMap();
                            discntEle.put("ELEMENT_ID", discntCode);
                            discntEle.put("ELEMENT_TYPE_CODE", "D");
                            discntEle.put("MODIFY_TAG", "1");
                            discntEle.put("START_DATE", userDiscnt.getStartDate());
                            discntEle.put("END_DATE", userDiscnt.getEndDate());
                            discntEle.put("ELEMENT_NAME", "用户优惠");
                            userElements.add(discntEle);
                        }
                    }
                }
                
                if (null != userSVcs)
                {
                    for (int j = 0; j < userSVcs.size(); j++)
                    {
                        SvcTradeData userSVc = userSVcs.get(j);
                        String serviceId = userSVc.getElementId();
                        if (!elementsStr.contains("S_" + serviceId))
                        {
                            IData serviceEle = new DataMap();
                            serviceEle.put("ELEMENT_ID", serviceId);
                            serviceEle.put("ELEMENT_TYPE_CODE", "S");
                            serviceEle.put("MODIFY_TAG", "1");
                            serviceEle.put("START_DATE", userSVc.getStartDate());
                            serviceEle.put("END_DATE", userSVc.getEndDate());
                            serviceEle.put("ELEMENT_NAME", "用户服务");
                            userElements.add(serviceEle);
                        }
                    }
                }
                elements.addAll(userElements);
            }
			elements.addAll(elementTradeSet);
		}
		
		if(IDataUtil.isNotEmpty(selectedElements))
		{
			for(int i=0;i<selectedElements.size();i++)
			{
				IData element = selectedElements.getData(i);
				if(StringUtils.equals("P", element.getString("ELEMENT_TYPE_CODE")) && StringUtils.equals("0", element.getString("MODIFY_TAG")))
				{
					nextProductId = element.getString("ELEMENT_ID");
					break;
				}
			}
		}
		
		//主套餐变更购物车删除暂时不做校验
		if(StringUtils.isNotBlank(nextProductId))
		{
			return elements;
		}
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_PRODUCT_ID", userProductId);
		param.put("NEXT_PRODUCT_ID", nextProductId);
		param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		param.put("ROUTE_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		param.put("TRADE_TYPE_CODE", "110");
		param.put("USER_ID", uca.getUserId());
		param.put("ELEMENTS", elements);
		param.put("SELECTED_ELEMENTS", selectedElements);
//		IDataset delElements = CSAppCall.call("CS.SelectedElementSVC.dealSelectedElementsForChg", param);
		this.checkProductTradeMgr(param, uca);
		
		return elements;
	}
	
	private boolean isMainProductChange(String shoppingCartId,String shoppingOrderId)throws Exception
    {
//	    IDataset shoppingCartData =  ShoppingCartQry.getShoppingOrderInfosByIds(shoppingCartId,shoppingOrderId);
	    IDataset shoppingCartData =  MerchShoppingCartQry.getShoppingDetailInfoByOrderId(shoppingCartId,shoppingOrderId,"0");
	    if (IDataUtil.isNotEmpty(shoppingCartData))
        {
            IData shoppingDataInfo = shoppingCartData.first();
            
            IData requestData = new DataMap(shoppingDataInfo.getString("REQUEST_DATA"))/*.getString("NEW_PRODUCT_ID")*/;
            String newProductId = requestData.getString("NEW_PRODUCT_ID");
            if (StringUtils.isNotBlank(newProductId))
            {
                return true;
            }
         }
            return false;
    }
	/**
	 * 购物车复选框互斥校验
	 * 模拟拼规则校验数据：
	 * 	1.购物车ELEMENTS商品加入TF_B_TRADE_XX,TF_F_USER_XX_AFTER;
	 * 	2.购物车SELECTED_ELEMENTS商品加入TF_F_USER_XX_AFTER,过滤1;
	 * 	3.查询资料表数据加入TF_F_USER_XX_AFTER,过滤1,2加入的;
	 * 	4.调用规则
	 * @param data
	 * @param elements
	 * @throws Exception
	 */
	private void checkProductTradeMgr(IData data, UcaData uca) throws Exception
	{
		String userId = data.getString("USER_ID");
		String eparchyCode = data.getString("EPARCHY_CODE");
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "110");
		IDataset selectedElements = data.getDataset("SELECTED_ELEMENTS");
		IDataset elements = data.getDataset("ELEMENTS");
		
		IDataset tradeSvcs = new DatasetList();
		IDataset tradeDiscnts = new DatasetList();
		IDataset userSvcs = new DatasetList();
		IDataset userDiscnts = new DatasetList();
		IDataset userAttrs = new DatasetList();
		
		List<String> offerArray = new ArrayList<String>();
		
		//1.购物车ELEMENTS商品加入TF_B_TRADE_XX,TF_F_USER_XX_AFTER;
		if(IDataUtil.isNotEmpty(elements))
		{
			for(int i=0;i<elements.size();i++)
			{
				IData ruleElement = elements.getData(i);
				String elementTypeCode = ruleElement.getString("ELEMENT_TYPE_CODE");
				String elementId = ruleElement.getString("ELEMENT_ID");
				String sysTime = SysDateMgr.getSysTime();
 				ruleElement.put("USER_ID_A", "-1");// 给规则用
				ruleElement.put("USER_ID", userId);
				if("1".equals(ruleElement.getString("MODIFY_TAG")))
				{
					ruleElement.put("END_DATE", sysTime);
				}else if("0".equals(ruleElement.getString("MODIFY_TAG")))
				{
				    ruleElement.put("START_DATE", SysDateMgr.getNextSecond(sysTime));
					ruleElement.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
				}
				
				if (ruleElement.getString("INST_ID", "").equals(""))
				{
					ruleElement.put("INST_ID", "" + i);
				}
				
				if(StringUtils.equals("D", elementTypeCode))
				{
					ruleElement.put("DISCNT_CODE", elementId);
					tradeDiscnts.add(ruleElement);
					userDiscnts.add(ruleElement);
				}else if(StringUtils.equals("S", elementTypeCode))
				{
					ruleElement.put("SERVICE_ID", elementId);
					tradeSvcs.add(ruleElement);
					userSvcs.add(ruleElement);
				}
				
				offerArray.add(elementId + "|" + elementTypeCode);
			}
		}
				
		//2.购物车SELECTED_ELEMENTS商品加入TF_F_USER_XX_AFTER,过滤1;
		if(IDataUtil.isNotEmpty(selectedElements))
		{
			for(int i=0;i<selectedElements.size();i++)
			{
				IData ruleElement = selectedElements.getData(i);
				String elementTypeCode = ruleElement.getString("ELEMENT_TYPE_CODE");
				String elementId = ruleElement.getString("ELEMENT_ID");
				if(offerArray.contains(elementId + "|" + elementTypeCode))
				{
					continue;
				}
				
				ruleElement.put("USER_ID_A", "-1");// 给规则用
				ruleElement.put("USER_ID", userId);
				if (ruleElement.getString("INST_ID", "").equals(""))
				{
					ruleElement.put("INST_ID", "" + i);
				}
				
				if(StringUtils.equals("D", elementTypeCode))
				{
					ruleElement.put("DISCNT_CODE", elementId);
					userDiscnts.add(ruleElement);
				}else if(StringUtils.equals("S", elementTypeCode))
				{
					ruleElement.put("SERVICE_ID", elementId);
					userSvcs.add(ruleElement);
				}
				
				offerArray.add(elementId + "|" + elementTypeCode);
			}
		}
		
		IDataset tradeElements = new DatasetList();
		tradeElements.addAll(tradeDiscnts);
		tradeElements.addAll(tradeSvcs);
		
		if(IDataUtil.isNotEmpty(tradeElements))
		{
			for(int m=0;m<tradeElements.size();m++)
			{
				IDataset tempTradeDiscnts = new DatasetList();
				IDataset tempTradeSvcs = new DatasetList();
				
				IData tradeElement = tradeElements.getData(m);
				if("S".equals(tradeElement.getString("ELEMENT_TYPE_CODE")))
				{
					tempTradeSvcs.add(tradeElement);
				}else if("D".equals(tradeElement.getString("ELEMENT_TYPE_CODE")))
				{
					tempTradeDiscnts.add(tradeElement);
				}
				
				if("0".equals(tradeElement.getString("MODIFY_TAG")))
				{
					//3.查询资料表数据加入TF_F_USER_XX_AFTER,过滤1,2加入的;
					IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
					if (IDataUtil.isNotEmpty(userDiscntElements))
					{
						for (int j = 0; j < userDiscntElements.size(); j++)
						{
							IData ruleElement = userDiscntElements.getData(j);
							if(offerArray.contains(ruleElement.getString("ELEMENT_ID") + "|D"))
							{
								continue;
							}
							ruleElement.put("USER_ID_A", "-1");// 给规则用
							ruleElement.put("USER_ID", userId);
							ruleElement.put("DISCNT_CODE", ruleElement.getString("ELEMENT_ID"));
							userDiscnts.add(ruleElement);
						}
					}
					IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
					if (IDataUtil.isNotEmpty(userSvcElements))
					{
						for (int j = 0; j < userSvcElements.size(); j++)
						{
							IData ruleElement = userSvcElements.getData(j);
							if(offerArray.contains(ruleElement.getString("ELEMENT_ID") + "|S"))
							{
								continue;
							}
							ruleElement.put("USER_ID_A", "-1");// 给规则用
							ruleElement.put("USER_ID", userId);
							ruleElement.put("SERVICE_ID", ruleElement.getString("ELEMENT_ID"));
							userSvcs.add(ruleElement);
						}
					}
				}
				
				String errorInfo = this.breProductLimitMgr(data, uca, tempTradeSvcs, tempTradeDiscnts, userSvcs, userDiscnts);
				if(StringUtils.isNotBlank(errorInfo))
				{
					elements.getData(0).put("ERROR_INFO", errorInfo);
					break;
				}
			}				
		}
	}
	
	private String breProductLimitMgr(IData data, UcaData uca, IDataset tradeSvcs, IDataset tradeDiscnts, IDataset userSvcs, IDataset userDiscnts) throws Exception
	{
		String errorInfo = null;
		String userId = data.getString("USER_ID");
		String eparchyCode = data.getString("EPARCHY_CODE");
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "110");
		
		IDataset userAttrs = new DatasetList();
		
		//模拟attr
		if(IDataUtil.isNotEmpty(userSvcs))
		{
			for(int i=0;i<userSvcs.size();i++)
			{
				IData userSvc = userSvcs.getData(i);
				List<AttrTradeData> attrslist = uca.getUserAttrsByRelaInstId(userSvc.getString("INST_ID"));
				if(attrslist!=null && attrslist.size()>0)
				{
					for(int j=0;j<attrslist.size();j++)
					{
						AttrTradeData attrTradeData = attrslist.get(j);
						userAttrs.add(attrTradeData.toData());
					}
				}
			}
		}
		
		if(IDataUtil.isNotEmpty(userDiscnts))
		{
			for(int i=0;i<userDiscnts.size();i++)
			{
				IData userDiscnt = userDiscnts.getData(i);
				List<AttrTradeData> attrslist = uca.getUserAttrsByRelaInstId(userDiscnt.getString("INST_ID"));
				if(attrslist!=null && attrslist.size()>0)
				{
					for(int j=0;j<attrslist.size();j++)
					{
						AttrTradeData attrTradeData = attrslist.get(j);
						userAttrs.add(attrTradeData.toData());
					}
				}
			}
		}
		
		// 模拟拼产品台账
		IDataset tradeProducts = getTradeProducts(data);

		// 规则调用
		IDataset tradeMains = new DatasetList();
		IData tradeMain = new DataMap();
		tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
		tradeMain.put("TRADE_TYPE_CODE", tradeTypeCode);
		tradeMain.put("IN_MODE_CODE", "0");
		tradeMain.put("USER_ID", userId);
		tradeMains.add(tradeMain);
		IData ruleParam = new DataMap();
		ruleParam.put("TF_B_TRADE", tradeMains);
		ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
		ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
		ruleParam.put("TF_F_USER_PRODUCT_AFTER", tradeProducts);
		ruleParam.put("TF_F_USER_SVC_AFTER", userSvcs);
		ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscnts);
		ruleParam.put("TF_F_USER_ATTR_AFTER", userAttrs);
		ruleParam.put("IS_COMPONENT", "true");
		
		IData result = BizRule.bre4ProductLimitNeedFormat(ruleParam);
		if (IDataUtil.isNotEmpty(result))
		{
			IDataset errors = result.getDataset("TIPS_TYPE_ERROR");
			if (IDataUtil.isNotEmpty(errors))
			{
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < errors.size(); j++)
				{
					sb.append(errors.getData(j).getString("TIPS_INFO"));
				}
				errorInfo = sb.toString();
			}
		}
		
		return errorInfo;
	}
	
	private IDataset getTradeProducts(IData data) throws Exception
	{
		String nextProductId = data.getString("NEXT_PRODUCT_ID");
		String userProductId = data.getString("USER_PRODUCT_ID");
		String effectNow = data.getString("EFFECT_NOW", "");

		IDataset tradeProducts = new DatasetList();
		UcaData uca = UcaDataFactory.getUcaByUserId(data.getString("USER_ID"));

		if (uca != null)
		{
			if (StringUtils.isEmpty(nextProductId))
			{
				List<ProductTradeData> productList = uca.getUserProduct(userProductId);
				ProductTradeData oldTradeProduct = null;
				if (productList != null && productList.size() > 0)
				{
					oldTradeProduct = productList.get(0);
				}
				else
				{
					CSAppException.apperr(ProductException.CRM_PRODUCT_19, userProductId);
				}
				tradeProducts.add(oldTradeProduct.toData());
			}
			else
			{
				List<ProductTradeData> productList = uca.getUserProduct(userProductId);
				ProductTradeData oldTradeProduct = new ProductTradeData();
				if (productList != null && productList.size() > 0)
				{
					oldTradeProduct = productList.get(0);
				}
				ProductTradeData newTradeProduct = oldTradeProduct.clone();

				if ("1".equals(effectNow))
				{
					String currentTime = SysDateMgr.getSysTime();
					oldTradeProduct.setEndDate(currentTime);

					newTradeProduct.setStartDate(SysDateMgr.addSecond(currentTime, 1));
					newTradeProduct.setEndDate(SysDateMgr.END_DATE_FOREVER);
					newTradeProduct.setProductId(nextProductId);
					newTradeProduct.setOldBrandCode(oldTradeProduct.getBrandCode());
					newTradeProduct.setOldProductId(oldTradeProduct.getProductId());
					newTradeProduct.setInstId(SeqMgr.getInstId());
				}
				else
				{
					String productChangeDate = null;
					if (ProductUtils.isBookingChange(data.getString("BOOKING_DATE")))
		            {
						productChangeDate = data.getString("BOOKING_DATE");
		            }
					else
					{
						productChangeDate = ProductUtils.getProductChangeDate(userProductId, nextProductId);
					}
					
					oldTradeProduct.setEndDate(SysDateMgr.addSecond(productChangeDate, -1));

					newTradeProduct.setStartDate(productChangeDate);
					newTradeProduct.setEndDate(SysDateMgr.END_DATE_FOREVER);
					newTradeProduct.setProductId(nextProductId);
					newTradeProduct.setOldBrandCode(oldTradeProduct.getBrandCode());
					newTradeProduct.setOldProductId(oldTradeProduct.getProductId());
					newTradeProduct.setInstId(SeqMgr.getInstId());
				}

				tradeProducts.add(oldTradeProduct.toData());
				tradeProducts.add(newTradeProduct.toData());
			}
		}
		return tradeProducts;
	}

	private IDataset getTradeElement(IData element, String userId, int i)
	{
		IDataset tradeElements = new DatasetList();
		if (null == element)
		{
			return null;
		}
		IData ruleElement = new DataMap();
		ruleElement.put("USER_ID_A", "-1");
		ruleElement.put("USER_ID", userId);
		ruleElement.putAll(element);
		if (ruleElement.getString("INST_ID", "").equals(""))
		{
			ruleElement.put("INST_ID", "" + i);
		}
		if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
		{
			ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
		}
		else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
		{
			ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
		}
		tradeElements.add(ruleElement);
		return tradeElements;
	}
	
	private void dealElementDate(IData element, IData productData, ProductTimeEnv env) throws Exception
	{
		String nextProductId = productData.getString("NEW_PRODUCT_ID");
		String bookingDate = productData.getString("BOOKING_DATE");
		String userProductId = productData.getString("USER_PRODUCT_ID");
		String eparchyCode = getUserEparchyCode();
		String sysDate = SysDateMgr.getSysTime();
		String bookFlag = "0"; // 超享套餐使用
		
		// 设置产品模型数据
		ProductModuleData pmd = null;
		if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
		{
			pmd = new SvcData(element);
		} else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(element.getString("ELEMENT_TYPE_CODE")))
		{
			if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")))
			{
				element.put("OPER_CODE", PlatConstants.OPER_ORDER);
			} else if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
			{
				element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
			}
			pmd = new PlatSvcData(element);
			pmd.setPkgElementConfig(element.getString("PACKAGE_ID"));
		} else
		{
			pmd = new DiscntData(element);
		}

		// 当存在预约产品时 绝对时间取预约产品生效时间【此时不存在预约情况】
		if (StringUtils.isNotBlank(nextProductId) && !ProductUtils.isBookingChange(bookingDate))
		{
			IDataset userOldProductElements = ProductInfoQry.getProductElements(userProductId, eparchyCode);

			IData oldConfig = this.getTransElement(userOldProductElements, pmd.getElementId(), pmd.getElementType());
			if (IDataUtil.isNotEmpty(oldConfig))// 如果元素存在老产品下面 以老产品配置方式计算
			{
				pmd.setPkgElementConfig(oldConfig.getString("PACKAGE_ID"));
				// 如果元素是删除 且开始时间大于系统时间 那么终止此元素的结束时间为开始时间的前一秒 其他走配置方式
				if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
				{
					pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
				}
			} else
			// 如果元素只存在新产品下 则设置元素绝对生效时间为产品的开始时间 设置绝对失效时间为产品生效时间前一秒
			{
				pmd.setEnableTag("4");
				pmd.setStartAbsoluteDate(productData.getString("NEW_PRODUCT_START_DATE"));
				pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(productData.getString("NEW_PRODUCT_START_DATE")));
			}
		}

		// 计算时间
		if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
		{
			if ("5".equals(pmd.getEnableTag()) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
			{
				if ("1".equals(bookFlag) && ("3370".equals(pmd.getElementId()) || "3372".equals(pmd.getElementId()) || "3377".equals(pmd.getElementId()) || "3378".equals(pmd.getElementId())))
				{// 超享卡特殊处理
					element.put("EFFECT_NOW_START_DATE", SysDateMgr.firstDayOfDate(bookingDate, 1));
					element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
				} else
				{
					element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
					element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
				}
			} else
			{
				element.put("EFFECT_NOW_START_DATE", sysDate);
				element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
			}
			pmd.setStartDate(null);
			String startDate = ProductModuleCalDate.calStartDate(pmd, env);
			element.put("START_DATE", startDate);
			pmd.setEndDate(null);
			String endDate = ProductModuleCalDate.calEndDate(pmd, startDate);
			element.put("END_DATE", endDate);
			// if("1".equals(bookFlag)&&("3370".equals(pmd.getElementId())||"3372".equals(pmd.getElementId())||"3377".equals(pmd.getElementId())||"3378".equals(pmd.getElementId()))){
			// //超享卡
			// element.put("OLD_EFFECT_NOW_START_DATE", startDate);
			// element.put("OLD_EFFECT_NOW_END_DATE", endDate);
			// element.put("START_DATE",
			// element.getString("EFFECT_NOW_START_DATE"));
			// element.put("END_DATE",
			// element.getString("EFFECT_NOW_END_DATE"));
			// }
			
			if ("3".equals(pmd.getEnableTag()) && StringUtils.isBlank(env.getBasicAbsoluteStartDate()))
			{
				element.put("CHOICE_START_DATE", "true");
			}
			if ("2".equals(pmd.getEndEnableTag())/*
												 * ||
												 * StaffPrivUtil.isFuncDataPriv
												 * (
												 * this.getVisit().getStaffId
												 * (),
												 * "SYS_CRM_DISCNTDATECHG")
												 */)
			{
				element.put("SELF_END_DATE", "true");// 自选时间
			}

			/*
			 * 节假日元素办理时间 获取元素的办理时间
			 */
			if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()) && pmd.getElementType().equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
			{
				IDataset elementSpecialTimeLimit = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "7686", pmd.getElementId(), pmd.getElementType());
				if (IDataUtil.isNotEmpty(elementSpecialTimeLimit))
				{
					// 获取时间配置
					IData dateLimitConfig = elementSpecialTimeLimit.getData(0);
					// 有效期配置
					String validBeginDate = dateLimitConfig.getString("PARA_CODE4", "");
					String validEndDate = dateLimitConfig.getString("PARA_CODE5", "");

					String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

					if (StringUtils.isNotBlank(validBeginDate) && StringUtils.isNotBlank(validEndDate))
					{
						// 如果在有效期内
						if (curDate.compareTo(validBeginDate) > 0 && curDate.compareTo(validEndDate) < 0)
						{
							/*
							 * 判断是在哪个规定的时间内办理 如果假期前办理，就在假期第一天开始生效
							 * 如果是假期当中办理，就是马上生效 终止时间都为假期的最后一天
							 */
							String holidayBeforeBegin = dateLimitConfig.getString("PARA_CODE6", "");
							String holidayBeforeEnd = dateLimitConfig.getString("PARA_CODE7", "");
							String holidayBeforeStartDate = dateLimitConfig.getString("PARA_CODE8", "");

							String holidayBegin = dateLimitConfig.getString("PARA_CODE9", "");
							String holidayEnd = dateLimitConfig.getString("PARA_CODE10", "");

							String allEndDay = dateLimitConfig.getString("PARA_CODE12", "");

							if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
							{
								if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
								{
									element.put("START_DATE", holidayBeforeStartDate);
								}
							}
							if (StringUtils.isNotBlank(holidayBegin) && StringUtils.isNotBlank(holidayEnd))
							{
								if (curDate.compareTo(holidayBegin) > 0 && curDate.compareTo(holidayEnd) < 0)
								{
									element.put("START_DATE", curDate);
								}
							}

							element.put("END_DATE", allEndDay);
						}
					}
				}
				
				
				IDataset elementHolidayTimeLimit = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "1806", pmd.getElementId(), pmd.getElementType());
				if (IDataUtil.isNotEmpty(elementHolidayTimeLimit))
				{						
					// 获取时间配置
					IData dateLimitConfig = elementHolidayTimeLimit.getData(0);
					// 有效期配置
					
					//本月第一天
					String validBeginDate = SysDateMgr.getFirstDayOfThisMonth();
					//本月最后一天
					String validEndDate = SysDateMgr.getLastDateThisMonth();

					String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

					if (StringUtils.isNotBlank(validBeginDate) && StringUtils.isNotBlank(validEndDate))
					{
						// 如果在有效期内
						if (curDate.compareTo(validBeginDate) > 0 && curDate.compareTo(validEndDate) < 0)
						{								
							//标志：1表示走本月减特定天数；2表示本月几号
							String timeFlag = dateLimitConfig.getString("PARA_CODE6", "1");
							if("1".equals(timeFlag))
							{
								String timeday = dateLimitConfig.getString("PARA_CODE7", "7");
								String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
								String holidayBeforeEnd = SysDateMgr.addDays(validEndDate, -Integer.parseInt(timeday));
								if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
								{
									if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
									{
										element.put("START_DATE", SysDateMgr.addDays(holidayBeforeEnd, 0));
									}
								}
							}else if("2".equals(timeFlag))
							{
								String timeday = dateLimitConfig.getString("PARA_CODE8", "23");
								String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
								//yyyy-MM-dd
								String timeend = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
								String holidayBeforeEnd = timeend.substring(0, 8) + timeday;
								if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
								{
									if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
									{
										element.put("START_DATE", SysDateMgr.addDays(holidayBeforeEnd, 0));
									}
								}
							}else
							{
								element.put("START_DATE", curDate);
							}
							
							if(StringUtils.isBlank(element.getString("START_DATE")))
							{
								element.put("START_DATE", curDate);
							}
							element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						}
					}
				}
			}

			// REQ201506020003 假日流量套餐开发需求 start by songlm 20150619
			// 6600该优惠元素配置生效失效时间类型为4，即使用绝对时间，用TD_B_PACKAGE_ELEMENT表中配置的生效、失效时间。但如果在生效与失效时间期间办理，需要立即生效。
			if ("6600".equals(pmd.getElementId()) && (sysDate.compareTo(startDate) > 0))
			{
				element.put("START_DATE", sysDate);
			}
			// end

		} else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()))
		{
			element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
			element.put("EFFECT_NOW_END_DATE", sysDate);
			String cancelTag = pmd.getCancelTag();
			if (StringUtils.isNotBlank(cancelTag) && !"4".equals(pmd.getCancelTag()))
			{
				pmd.setEndDate(null);
			}
			String cancelDate = ProductModuleCalDate.calCancelDate(pmd, env);
			element.put("END_DATE", cancelDate);
		}
	}
	
	private IData getTransElement(IDataset productElements, String elementId, String elementType)
	{
		if (productElements == null || productElements.size() <= 0)
		{
			return null;
		}
		int size = productElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				return element;
			}
		}
		return null;
	}
	
	
    public void checkSaleBoxAction(IData input) throws Exception
    {
        String eparchyCode = CSBizBean.getUserEparchyCode();
        String shoppingCartId = input.getString("SHOPPING_CART_ID");
        String shoppingOrderId = input.getString("DETAIL_ORDER_ID");
        String tradeTypeCode ="";
        String delePackageId="";

        //根据shoppingOrderId 获取本次删除的营销包编码
        IDataset shoppingCartData =  MerchShoppingCartQry.getShoppingDetailInfoByOrderId(shoppingCartId,shoppingOrderId,"0");
        if (IDataUtil.isNotEmpty(shoppingCartData))
        {
            IData shoppingDataInfo = shoppingCartData.first();
            tradeTypeCode = shoppingDataInfo.getString("DETAIL_TYPE_CODE");
            IData requestData = new DataMap(shoppingDataInfo.getString("REQUEST_DATA"));
            delePackageId = requestData.getString("PACKAGE_ID");
         }
        
         IDataset shoppingCartDetailSet = MerchShoppingCartQry.getShoppingDetailById(shoppingCartId,eparchyCode);
         if(IDataUtil.isNotEmpty(shoppingCartDetailSet)){
             for(int i=0;i<shoppingCartDetailSet.size();i++){
                 IData temp=shoppingCartDetailSet.getData(i);
                 IData requestData = new DataMap(temp.getString("REQUEST_DATA"));
                 String PackgeId=requestData.getString("PACKAGE_ID");
                 String relPakcgeId=requestData.getString("REL_PACKAGE_ID");
                 String netPakcgeId=requestData.getString("NET_PACKAGE_ID");

                 if(!"240".equals(tradeTypeCode) || delePackageId.equals(PackgeId)){//过滤掉非营销活动和本次删除的营销活动
                     continue;
                 }
                 else if(delePackageId.equals(relPakcgeId)){//本次删除的营销活动有所依赖的营销活动，请删除依赖延迟的营销活动
                     String pkgName = UPackageInfoQry.getPackageNameByPackageId(PackgeId);
                     CSAppException.appError("-1", "请先删除顺延的营销活动"+"【"+pkgName+"】");
                 }
                 else if(delePackageId.equals(netPakcgeId)){//对于再往时间延续的，必须要删除后面的延续活动，再去删当前的活动
                     String pkgName = UPackageInfoQry.getPackageNameByPackageId(PackgeId);
                     CSAppException.appError("-1", "请先删除在网时间顺延的营销活动"+"【"+pkgName+"】");
                 }
                 

             }
         }
          
    }
    
    public IDataset getShoppingCartElementsByTradeTypeCode(IData param, String tradeTypeCode) throws Exception
    {
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
		String sn = param.getString("SERIAL_NUMBER", "");
		String userId = param.getString("USER_ID", "");
		String staffId = StringUtils.isNotBlank(param.getString("TRADE_STAFF_ID")) ? param.getString("TRADE_STAFF_ID")
				: getVisit().getStaffId();
		if (StringUtils.isBlank(userId))
			userId = getUserIdBySn(sn);

		IDataset offerSet = new DatasetList();
		IDataset shoppingCartSet = MerchShoppingCartQry.getShoppingSetByStaffUserId(staffId, userId, "0", sn, false);
		if (IDataUtil.isEmpty(shoppingCartSet))
			return offerSet;
		String shoppingCartId = shoppingCartSet.getData(0).getString("SHOPPING_CART_ID");
		IDataset shoppingCartDetailSet = MerchShoppingCartQry.getShoppingDetailById(shoppingCartId,
				getVisit().getStaffEparchyCode());
		if (IDataUtil.isEmpty(shoppingCartDetailSet))
			return offerSet;

		IDataset allElemetnSet = qryElementSet(shoppingCartDetailSet, tradeTypeCode);
		if (IDataUtil.isEmpty(allElemetnSet))
			return offerSet;

		transferDataSet(offerSet, allElemetnSet);
		return offerSet;
    }
    
    private IDataset qryElementSet(IDataset shoppingCartDetailSet, String tradeTypeCode) throws Exception {
		IDataset allElemetnSet = new DatasetList();
		for (int i = 0, size = shoppingCartDetailSet.size(); i < size; i++) {
			IData shoppingCartDetailData = shoppingCartDetailSet.getData(i);
			String orderId = shoppingCartDetailData.getString("DETAIL_ORDER_ID");
			IDataset tradeDataset = TradeInfoQry.queryTradeByOrerId(orderId, "0");
			if (IDataUtil.isEmpty(tradeDataset))
				continue;
			IDataset elementTadeSet = new DatasetList();
			for (int j = 0, tsize = tradeDataset.size(); j < tsize; j++) {
				IData tradeData = tradeDataset.getData(j);
				if(StringUtils.equals(tradeTypeCode, tradeData.getString("TRADE_TYPE_CODE")))
				{
					buildTradeElements(tradeData.getString("TRADE_ID"), tradeData.getString("INTF_ID"), elementTadeSet);
				}
			}
			allElemetnSet.addAll(elementTadeSet);
		}
		return allElemetnSet;
	}
    
}
