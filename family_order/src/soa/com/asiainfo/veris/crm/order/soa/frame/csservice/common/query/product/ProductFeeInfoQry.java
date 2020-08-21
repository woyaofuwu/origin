package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ProductFeeInfoQry extends CSBizBean
{
	public static IDataset getElementFee(String trade_type_code, String in_mode_code, String vip_class_id, String element_type_code, String product_id, String package_id, String campn_id, String element_id, String eparchy_code, String trade_fee_type) throws Exception
	{
		// IData data = new DataMap();
		// data.put("TRADE_TYPE_CODE", trade_type_code);
		// data.put("IN_MODE_CODE", in_mode_code);
		// data.put("VIP_CLASS_ID", vip_class_id);
		// data.put("ELEMENT_TYPE_CODE", element_type_code);
		// data.put("PRODUCT_ID", product_id);
		// data.put("PACKAGE_ID", package_id);
		// data.put("CAMPN_ID", campn_id);
		// data.put("ELEMENT_ID", element_id);
		// data.put("EPARCHY_CODE", eparchy_code);
		// data.put("TRADE_FEE_TYPE", trade_fee_type);
		// IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE",
		// "SEL_TRADEFEE_BY_ALLPARA", data, Route.CONN_CRM_CEN);
		IDataset feeInfos = new DatasetList();
		if ("-1".equals(product_id) && "-1".equals(element_id) && "-1".equals(package_id))
		{
			feeInfos = ProductFeeInfoQry.getTradeFee(trade_type_code, trade_fee_type, eparchy_code);
		} else
		{
			feeInfos = UpcCall.qryDynamicPrice(product_id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, element_id, element_type_code, trade_type_code, in_mode_code, vip_class_id, package_id);

			if (IDataUtil.isNotEmpty(feeInfos))
			{
				for (Object obj : feeInfos)
				{
					IData feeInfo = (IData) obj;
					feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
					feeInfo.put("TRADE_TYPE_CODE", feeInfo.getString("TRADE_TYPE_CODE", trade_type_code));
					feeInfo.put("MODE", feeInfo.getString("FEE_MODE"));
					feeInfo.put("CODE", feeInfo.getString("FEE_TYPE_CODE"));
					feeInfo.put("FEE", feeInfo.getString("FEE"));
					feeInfo.put("ELEMENT_ID", element_id);
					feeInfo.put("IN_MODE_CODE", feeInfo.getString("IN_MODE_CODE", in_mode_code));
					feeInfo.put("VIP_CLASS_ID", feeInfo.getString("VIP_CLASS_ID", vip_class_id));
					feeInfo.put("PRODUCT_ID", product_id);
					feeInfo.put("PACKAGE_ID", package_id);
					feeInfo.put("CAMPN_ID", feeInfo.getString("CAMPN_ID"));
					feeInfo.put("TRADE_FEE_TYPE", feeInfo.getString("TRADE_FEE_TYPE"));
				}
			}
		}
		return feeInfos;
	}

	public static IDataset getElementFeeBySql(IData data) throws Exception
	{
		// return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE",
		// "SEL_TRADEFEE_BY_ALLPARA", data, Route.CONN_CRM_CEN);
		String product_id = data.getString("PRODUCT_ID", "");
		String element_id = data.getString("ELEMENT_ID", "");
		String element_type_code = data.getString("ELEMENT_TYPE_CODE", "");
		String trade_type_code = data.getString("ELEMENT_ID", "");
		String in_mode_code = data.getString("IN_MODE_CODE", "");
		String vip_class_id = data.getString("VIP_CLASS_ID", "");
		String package_id = data.getString("PACKAGE_ID", "");
		IDataset feeInfos = UpcCall.qryDynamicPrice(product_id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, element_id, element_type_code, trade_type_code, in_mode_code, vip_class_id, package_id);

		if (IDataUtil.isNotEmpty(feeInfos))
		{
			for (Object obj : feeInfos)
			{
				IData feeInfo = (IData) obj;
				feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
				feeInfo.put("TRADE_TYPE_CODE", feeInfo.getString("TRADE_TYPE_CODE", trade_type_code));
				feeInfo.put("MODE", feeInfo.getString("FEE_MODE"));
				feeInfo.put("CODE", feeInfo.getString("FEE_TYPE_CODE"));
				feeInfo.put("FEE", feeInfo.getString("FEE"));
				feeInfo.put("ELEMENT_ID", element_id);
				feeInfo.put("IN_MODE_CODE", feeInfo.getString("IN_MODE_CODE", in_mode_code));
				feeInfo.put("VIP_CLASS_ID", feeInfo.getString("VIP_CLASS_ID", vip_class_id));
				feeInfo.put("PRODUCT_ID", product_id);
				feeInfo.put("PACKAGE_ID", package_id);
				feeInfo.put("CAMPN_ID", feeInfo.getString("CAMPN_ID"));
				feeInfo.put("TRADE_FEE_TYPE", feeInfo.getString("TRADE_FEE_TYPE"));
			}
		}
		return feeInfos;
	}

	public static IDataset getElementFeeForTrans(String trade_type_code, String in_mode_code, String vip_class_id, String element_type_code, String product_id, String package_id, String campn_id, String element_id, String eparchy_code, String trade_fee_type) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", trade_type_code);
		data.put("IN_MODE_CODE", in_mode_code);
		data.put("VIP_CLASS_ID", vip_class_id);
		data.put("ELEMENT_TYPE_CODE", element_type_code);
		data.put("PRODUCT_ID", product_id);
		data.put("PACKAGE_ID", package_id);
		data.put("CAMPN_ID", campn_id);
		data.put("ELEMENT_ID", element_id);
		data.put("EPARCHY_CODE", eparchy_code);
		data.put("TRADE_FEE_TYPE", trade_fee_type);
		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADEFEE_BY_ALLPARA_TRANS", data, Route.CONN_CRM_CEN);
		return dataset;
	}

	public static IDataset getFee(String trade_type_code, String trade_fee_type, String fee_type_code) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", trade_type_code);
		data.put("TRADE_FEE_TYPE", trade_fee_type);
		data.put("FEE_TYPE_CODE", fee_type_code);

		SQLParser parser = new SQLParser(data);
		parser.addSQL("select * from td_b_product_tradefee ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
		parser.addSQL(" and TRADE_FEE_TYPE = :TRADE_FEE_TYPE ");
		parser.addSQL(" and FEE_TYPE_CODE = :FEE_TYPE_CODE ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据TRADE_TYPE_CODE、PRODUCT_ID、PACKAGE_ID、ELEMENT_ID、ELEMENT_TYPE_CODE、
	 * FEE_MODE、EPARCHY_CODE查询费用
	 * 
	 * @param tradeTypeCode
	 * @param productId
	 * @param elementTypeCode
	 * @param feeMode
	 * @param eparchyCode
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-11-2
	 */
	public static IDataset getFeeByTradeFeeMode(String tradeTypeCode, String productId, String packageId, String elementId, String elementTypeCode, String feeMode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("ELEMENT_ID", elementId);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		param.put("FEE_MODE", feeMode);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_BY_TRADETYPE", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 查询付费方式
	 * 
	 * @author tengg
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IData getFeePayMode(String trade_type_code, String product_id, String vip_class_id, String eparchy_code, String fee_type_code, String fee_mode) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", trade_type_code);
		data.put("PRODUCT_ID", product_id);
		data.put("VIP_CLASS_ID", vip_class_id);
		data.put("EPARCHY_CODE", eparchy_code);
		data.put("FEE_TYPE_CODE", fee_type_code);
		data.put("FEE_MODE", fee_mode);

		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_PRODTRADEFEE_BY_PAY_MODE", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	public static IDataset getGoodsTradeFee(IData params) throws Exception
	{
		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_GOODSFEE_BY_RESID", params, "cen");
	}

	/**
	 * 查集团费用
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpTradeTypeFee(IData param) throws Exception
	{
		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
		String productId = param.getString("PRODUCT_ID");
		String packageId = param.getString("PACKAGE_ID", "-1");
		String elementId = param.getString("ELEMENT_ID", "-1");
		String elementTypeCode = param.getString("ELEMENT_TYPE_CODE", "P");
		String tradeFeeType = param.getString("TRADE_FEE_TYPE", "0");
		if (tradeFeeType.equals("0"))
		{
			return getElementFeeList(tradeTypeCode, "P", productId, "-1", "-1", "-1");
		}
		if (tradeFeeType.equals("4"))
		{
			// return getElementFeeList(tradeTypeCode, "P", productId,
			// elementTypeCode, elementId, packageId);
			return new DatasetList();
		}

		return new DatasetList();
	}

	public static int getPackageSumFee(String tradeTypeCode, String vipClassId, String userEparchyCode, String campnId, String productId, String packageId, String tradeFeeType) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("VIP_CLASS_ID", vipClassId);
		param.put("EPARCHY_CODE", userEparchyCode);
		param.put("CAMPN_ID", campnId);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("TRADE_FEE_TYPE", tradeFeeType);
		IDataset result = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADEFEE_BY_ALLPARA_SUM", param, Route.CONN_CRM_CEN);

		int sumFee = result.getData(0).getInt("FEE");

		return sumFee;
	}
	
    /**
     * 查询产品费用
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductFeeInfo(String trade_type_code, String product_id, String package_id, String element_id, String element_type_code, String trade_fee_type, String eparchy_code) throws Exception
    {
        IDataset dataset = UpcCall.qryDynamicPrice(product_id, "P", element_id, element_type_code, trade_type_code, null, null, package_id);;
        if(IDataUtil.isNotEmpty(dataset))
        {
            for(Object obj : dataset)
            {
                IData feeInfo = (IData) obj;
                feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
            }
        }
        return dataset;
    }
    
	/**
	 * * 查询所有费用， 不包含配置转账的
	 * 
	 * @param tradeTypeCode
	 * @param vipClassId
	 * @param userEparchyCode
	 * @param campnId
	 * @param productId
	 * @param packageId
	 * @param tradeFeeType
	 * @return
	 * @throws Exception
	 */
	public static int getPackageSumFeeNoTrans(String tradeTypeCode, String vipClassId, String userEparchyCode, String campnId, String productId, String packageId, String tradeFeeType) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("VIP_CLASS_ID", vipClassId);
		param.put("EPARCHY_CODE", userEparchyCode);
		param.put("CAMPN_ID", campnId);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("TRADE_FEE_TYPE", tradeFeeType);
		IDataset result = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADEFEE_BY_ALLPARA_SUM_NO_TRANS", param, Route.CONN_CRM_CEN);

		int sumFee = result.getData(0).getInt("FEE");

		return sumFee;
	}

	public static IDataset getProductPackgeFee(String tradeTypeCode, String productId, String packageId, String tradeFeeType) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("TRADE_FEE_TYPE", tradeFeeType);
		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADEFEE_BY_PID_KID", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 获取产品营业费用
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeFee(IData param) throws Exception
	{
		// return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADETYPEFEE",
		// param, Route.CONN_CRM_CEN);
		String tradeTypeCode = param.getString("TRADE_TYPE_CODE", "");
		String productId = param.getString("PRODUCT_ID", "-1");
		String inModeCode = param.getString("VIP_CLASS_ID", "");
		String tradeFeeType = param.getString("TRADE_FEE_TYPE", "");
		IDataset feeInfos = UpcCall.qryDynamicPrice(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, null, null, tradeTypeCode, null, inModeCode, null);
		if (IDataUtil.isNotEmpty(feeInfos))
		{
			for (Object obj : feeInfos)
			{
				IData feeInfo = (IData) obj;
				feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
			}
		}
		return feeInfos;
	}

	public static IDataset getTradeFee(String tradeTypeCode, String tradeFeeType, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("TRADE_FEE_TYPE", tradeFeeType);
		param.put("EPARCHY_CODE", eparchyCode);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM TD_B_TRADEFEE WHERE TRADE_TYPE_CODE =:TRADE_TYPE_CODE ");
		sql.append("and (EPARCHY_CODE = :EPARCHY_CODE or EPARCHY_CODE = 'ZZZZ') ");
		sql.append("and TRADE_FEE_TYPE =:TRADE_FEE_TYPE ");
		sql.append("AND sysdate BETWEEN start_date AND end_date ");

		return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
	}

	/**
	 * 获取产品营业费用
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeFee4Grp(IData param) throws Exception
	{
		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADETYPEFEE_FOR_GRP", param, Route.CONN_CRM_CEN);
	}

	public static IDataset qryPayFeeByIds(String tradeTypeCode, String productId, String packageId, String elementTypeCode, String elementId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();

		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		param.put("ELEMENT_ID", elementId);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_TRADEFEE_BY_IDS", param, Route.CONN_CRM_CEN);
	}

	public static IDataset queryFee(String productId, String packageId, String tradeTypeCode, String feeMode, String eparchyCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("PRODUCT_ID", productId);
		cond.put("PACKAGE_ID", packageId);
		cond.put("TRADE_TYPE_CODE", tradeTypeCode);
		cond.put("FEE_MODE", feeMode);
		cond.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_EXISTS_FEE_NUMS", cond);
	}

	public static IDataset queryFeeByElementId(String elementId, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("ELEMENT_ID", elementId);
		data.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_FEE_BY_ELEMENT_ID", data, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据TRADE_TYPE_CODE,PRODUCT_ID,PACKAGE_ID,ELEMENT_ID,EPARCHY_CODE查询产品类型信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData getFeeByEle(String trade_type_code, String product_id, String package_id, String eparchy_code, String element_id) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", trade_type_code);
		data.put("PRODUCT_ID", product_id);
		data.put("PACKAGE_ID", package_id);
		data.put("EPARCHY_CODE", eparchy_code);
		data.put("ELEMENT_ID", element_id);
		data.put("TRADE_STAFF_ID", getVisit().getStaffId());
		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_BY_PROD_PACK_ELE_ID", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	/**
	 * 查询元素费用
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getFeeByPK(String trade_type_code, String product_id, String package_id, String eparchy_code, String element_id) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", trade_type_code);
		data.put("PRODUCT_ID", product_id);
		data.put("PACKAGE_ID", package_id);
		data.put("EPARCHY_CODE", eparchy_code);
		data.put("ELEMENT_ID", element_id);
		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_TRADEFEE", "SEL_PRODUCT_TRADEFEE", data, Route.CONN_CRM_CEN);
		return dataset;
	}


	/**
	 * 
	 * @Title: getSaleActiveFee
	 * @Description:
	 * @param tradeTypeCode
	 *            业务类型
	 * @param offerType
	 * @param offerCode
	 * @param relOfferType
	 *            原TD_B_PRODUCT_TRADEFEE表ELEMENT_ID
	 * @param relOfferCode
	 *            原TD_B_PRODUCT_TRADEFEE表ELEMENT_TYPE_CODE
	 * @return IDataset
	 * @throws Exception
	 * @author longtian3
	 */
	public static IDataset getSaleActiveFee(String tradeTypeCode, String offerType, String offerCode, String relOfferType, String relOfferCode, String catalogId) throws Exception
	{
		IDataset feeInfos = new DatasetList();
		IDataset prices = null;

		if ("A".equals(relOfferType))
		{
			// feeInfos = UpcCall.qrySaleActiveCompOfferPricPlan(null, null,
			// offerType, offerCode, tradeTypeCode, catalogId);
			prices = UpcCall.qryDynamicPrice(tradeTypeCode, catalogId, offerCode, offerType, "-1", null, null);
		} else
		{
			// feeInfos = UpcCall.qrySaleActiveCompOfferPricPlan(offerType,
			// offerCode, relOfferType, relOfferCode, tradeTypeCode, catalogId);
			prices = UpcCall.qryDynamicPrice(tradeTypeCode, catalogId, offerCode, offerType, relOfferCode, relOfferType, null);
		}

		if (IDataUtil.isNotEmpty(prices))
		{
			for (Object obj : prices)
			{
				IData price = (IData) obj;
				String pricePlanType = price.getString("PRICE_PLAN_TYPE");
				if (!"0".equals(pricePlanType))// 营业侧
				{
					continue;
				}
				price.put("FEE_MODE", price.getString("FEE_TYPE"));
				feeInfos.add(price);
			}
		}
		return feeInfos;
	}

	/**
	 * 
	 * @Title: getElementFee
	 * @Description:
	 * @param tradeTypeCode
	 *            业务类型
	 * @param offerType
	 *            原TD_B_PRODUCT_TRADEFEE表ELEMENT_ID
	 * @param offerCode
	 *            原TD_B_PRODUCT_TRADEFEE表ELEMENT_TYPE_CODE
	 * @param relOfferType
	 * @param relOfferCode
	 * @return IDataset
	 * @throws Exception
	 * @author lijun17
	 */
	public static IDataset getElementFeeList(String tradeTypeCode, String offerType, String offerCode, String relOfferType, String relOfferCode, String groupId) throws Exception
	{
		IDataset feeInfos = UpcCall.qryDynamicPrice(offerCode, offerType, relOfferCode, relOfferType, tradeTypeCode, null, null, groupId);

		if (IDataUtil.isNotEmpty(feeInfos))
		{
			for (Object obj : feeInfos)
			{
				IData feeInfo = (IData) obj;
				feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
			}
		}
		return feeInfos;
	}
	
	public static IDataset getSaleActiveFee(String tradeTypeCode, String offerType, String offerCode, String relOfferType, String relOfferCode, String catalogId, String payMode) throws Exception
    {
    	IDataset result = new DatasetList();
    	
    	IDataset feeInfos = getSaleActiveFee(tradeTypeCode, offerType, offerCode, relOfferType, relOfferCode, catalogId);
    	if(IDataUtil.isNotEmpty(feeInfos))
    	{
    		for(int i=0; i < feeInfos.size();i++)
    		{
    			IData feeInfo = feeInfos.getData(i);
    			String feePayMode = feeInfo.getString("PAY_MODE");
    			if(payMode.equals(feePayMode))
    			{
    				result.add(feeInfo);
    			}
    		}
    	}
    	
    	return result;
    }
}
