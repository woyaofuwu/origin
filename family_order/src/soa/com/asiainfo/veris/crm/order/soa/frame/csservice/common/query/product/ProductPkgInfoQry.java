package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import org.apache.axis.utils.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ProductPkgInfoQry extends CSBizBean
{
	/**
	 * 查产品下所有的必选包
	 * 
	 * @author shixb
	 * @version 创建时间：2009-11-3 下午04:37:44
	 */
	public static IDataset getAllForcePackageByProId(String productId) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);

		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_ALL_FORCEPACKAGE", data, Route.CONN_CRM_CEN);
		return dataset;
	}

	public static IDataset getDefaultAndForceDiscntInPro(String productId) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ELEMENT_INIT_DISCNT", data, Route.CONN_CRM_CEN);
	}

	public static IDataset getDefaultLongRoam(String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_LONG_ROAM", param);
	}

	/**
	 * 根据产品查询相应优惠
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByPId(IData data) throws Exception
	{
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_FOR_MCARD_DISCNT", data, Route.CONN_CRM_CEN);
	}

	/**
	 * 通过PRODUCT_ID PACKAGE_ID 来查限制订购次数 RSRV_TAG1
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author awx
	 * @date 2009-7-28
	 */
	public static IDataset getLimitByProductPackage(String packageId, String productId) throws Exception
	{
		IData data = new DataMap();
		data.put("PACKAGE_ID", packageId);
		data.put("PRODUCT_ID", productId);

		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_ONCE_BY_PRODUCT_PACKAGE", data, Route.CONN_CRM_CEN);
		return dataset;
	}

	/**
	 * 查询某集团产品下的成员必选包
	 * 
	 * @param productId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMebForcePackageByGrpProId(String productId, String eparchyCode) throws Exception
	{
		String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
		if (StringUtils.isEmpty(mebProductId))
			return new DatasetList();

		IDataset packageList = UPackageInfoQry.getPackagesByProductId(mebProductId);
		if (IDataUtil.isNotEmpty(packageList))
		{
			for (int i = packageList.size() - 1; i >= 0; i--)
			{
				IData packageData = packageList.getData(i);
				if (!"1".equals(packageData.getString("FORCE_TAG")))
				{
					packageList.remove(i);
				}
			}
		}
		return packageList;
	}

	/**
	 * 根据PRODUCT_ID查询产品下的所有包
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author anwx
	 */
	public static IDataset getPackageByProductId(String productId, String eparchyCode) throws Exception
	{
		return UPackageInfoQry.getPackagesByProductId(productId);
	}

	/**
	 * 根据PRODUCT_ID查询产品下的所有包 用于开户营销活动
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author yangkx
	 */
	public static IDataset getPackageByProductIdForOpen(String productId) throws Exception
	{

		IDataset dataset = UpcCall.qryOffersByCatalogId(productId);
		// return Dao.qryByCode("TD_B_PRODUCT_PACKAGE",
		// "SEL_SPECPACKAGE_BY_PID", data, Route.CONN_CRM_CEN);
		return dataset;
	}

	/**
	 * 查询产品下的必选包
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author Xiajj
	 */
	public static IDataset getPackageByProId(String productId, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("EPARCHY_CODE", eparchyCode);

		// IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE",
		// "SEL_FORCE_PACKAGE_AFTER_TRADE", data, Route.CONN_CRM_CEN);
		IDataset dataset = UPackageInfoQry.getForcePackagesByProductId(productId);
		return dataset;
	}

	/**
	 * 根据PRODUCT_ID查询产品下的所有包
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author yangkx
	 */
	public static IDataset getPackageNodateByProductId(String productId) throws Exception
	{

		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PK_NO_DATE", data, Route.CONN_CRM_CEN);
	}

	public static IDataset getPackInfoByproduct(String productId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select  *   FROM TD_B_PRODUCT_PACKAGE us ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and us.PRODUCT_ID = to_number(:PRODUCT_ID) ");
		parser.addSQL(" AND us.START_DATE<=SYSDATE ");
		parser.addSQL(" AND us.END_DATE>SYSDATE ");
		return Dao.qryByParse(parser, pagination);
	}

	public static IDataset getPrkBydiscntCode(String productId, String eparchyCode, String elementId) throws Exception
	{
		IData params = new DataMap();
		params.put("PRODUCT_ID", productId);
		params.put("EPARCHY_CODE", eparchyCode);
		params.put("ELEMENT_ID", elementId);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_BY_DISCNTCODE", params, Route.CONN_CRM_CEN);
	}

	public static IDataset getPrkByPrkCode(String packageId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PACKAGE_ID", packageId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_BY_PRKID", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据PRODUCT_ID、EPARCHY_CODE、ELEMENT_TYPE_CODE查询产品下的所有有效元素
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author yangkx
	 */
	public static IDataset getProductElement(String productId, String eparchyCode, String element_type_code) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("EPARCHY_CODE", eparchyCode);
		data.put("ELEMENT_TYPE_CODE", element_type_code);

		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ELEMENT", data, Route.CONN_CRM_CEN);
	}

	public static IDataset getProductElementByID(IData param, String reGex, String replaceMent) throws Exception
	{
		return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_ELEMENT_BY_PACKAGE_ID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset getProductPackageByID(IData param, String reGex, String replaceMent) throws Exception
	{
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_FORCE_PACKAGE_BY_PRODUCT_ID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset getProductPackageElementsByProductId(String productId) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_DEFAULTSERVERID_BY_PROID", data, Route.CONN_CRM_CEN);
	}

	public static IDataset getProductPackageForSale(String productId, String packageId, String strEparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("PACKAGE_ID", packageId);
		data.put("EPARCHY_CODE", strEparchyCode);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_PACKAGE_FOR_SALE", data, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据PRODUCT_ID PACKAGE_ID EPARCHY_CODE查询产品类型信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getProductPackageRel(String productId, String packageId, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("PACKAGE_ID", packageId);
		data.put("EPARCHY_CODE", eparchyCode);
		data.put("TRADE_STAFF_ID", getVisit().getStaffId());

		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_PACKAGE", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	public static IData getProductPackageRelContainDemode(String productId, String packageId, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("PACKAGE_ID", packageId);
		data.put("EPARCHY_CODE", eparchyCode);
		data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_PACKAGE_CONTAIN_DEMODE", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	/**
	 * 根据PRODUCT_ID PACKAGE_ID EPARCHY_CODE查询产品类型信息，包括过时的包配置 不判断权限
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author zhouquan
	 * @date 2011-03-17
	 */
	public static IData getProductPackageRelContainDemodeNoPriv(String productId, String packageId, String eparchyCode) throws Exception
	{

		IData data = new DataMap();
		data.put("PRODUCT_ID", productId);
		data.put("PACKAGE_ID", packageId);
		data.put("EPARCHY_CODE", eparchyCode);

		IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_PACKAGE_CONTAIN_DEMODE_NO_PRIV", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	/**
	 * 根据PRODUCT_ID PACKAGE_ID EPARCHY_CODE查询产品类型信息 不判断权限
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getProductPackageRelNoPriv(String productId, String packageId, String eparchyCode) throws Exception
	{
		return UPackageInfoQry.getPackageByProductIdAndPackageId(productId, packageId);
	}

	public static IDataset getProductPackagesForSpec(String productId) throws Exception
	{
		IDataset results = UpcCall.queryOfferGroupRelOfferId("P", productId);
		IDataset lastresults = new DatasetList();
		if (IDataUtil.isNotEmpty(results))
		{
			for (int i = 0; i < results.size(); i++)
			{
				if ("0".equals(results.getData(i).getString("SELECT_FLAG")) && "1".equals(results.getData(i).getString("MAX_NUM")) && "1".equals(results.getData(i).getString("MIN_NUM")))
				{
					lastresults.add(results.getData(i));
				}
			}
		}
		return lastresults;
	}

	public static IDataset qryActiveByPId(String productId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		return UpcCall.qryOfferExtChasByCatalogIdOfferId(productId);
	}

	public static IDataset qryActiveTerminalByPId(String productId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_ACTIVE_TERMINAL_BY_PID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset qryNoTerminalActiveByPid(String campnTypde, String productId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("CAMPN_TYPE", campnTypde);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_NO_TERMINAL_ACTIVE_BY_PID", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 查询元素配置信息
	 * 
	 * @param productId
	 * @param packageId
	 * @param elementId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryPackageElement(String productId, String packageId, String elementId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("ELEMENT_ID", elementId);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PRO_PKG_ELEMENTID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset qryTerminalActiveByPidPkIdRid(String campnType, String productId, String packageId, String resId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("CAMPN_TYPE", campnType);
		param.put("RES_ID", resId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_ACTIVE_BY_PID_PKID_RID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset qryTerminalActiveByPidRid(String campnType, String productId, String resId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("CAMPN_TYPE", campnType);
		param.put("RES_ID", resId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_TERMINAL_ACTIVE_BY_PID_RID", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据主号优惠，查询副号优惠
	 * 
	 * @param Mdiscnt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryFDiscnt(IData data) throws Exception
	{
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_FOR_FCARD_DISCNT", data, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据PRODUCT_ID查询该产品下必选元素
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryForceDiscntByProductId(String productId) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		param.put("PRODUCT_ID", productId);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ELEMENT_INIT_DISCNT", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据PRODUCT_ID,DISCNT_CODE查询该产品下包id
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPackageByProDis(String productId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		param.put("PRODUCT_ID", productId);
		param.put("DISCNT_CODE", discntCode);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PACKAGE_BY_PROD_DISC", param, Route.CONN_CRM_CEN);
	}

	public static IDataset queryPackageElementByPackageId(String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("PACKAGE_ID", packageId);
		return Dao.qryByCodeParser("TD_B_PACKAGE_ELEMENT", "SEL_ELEMENT_BY_PACKAGE", param);
	}

	public static IDataset queryPackagesByProductId(String productId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("PRODUCT_ID", productId);

		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PACKAGE_BY_PRODUCT", cond, Route.CONN_CRM_CEN);
	}

	public static IDataset queryProductPackageByDiscntCode(String discntCode, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("DISCNT_CODE", discntCode);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_PACKAGE_BY_DISCNT_CODE", param, page);
	}

	public static IDataset queryProductPackageByGoodsId(String resId, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("RES_ID", resId);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_PACKAGE_BY_GOODSID", param, page);
	}

	public static IDataset queryProductPackageByIdName(String productId, String productName, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("PRODUCT_NAME", productName);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_PACKAGE_BY_ID_NAME", param, page);
	}

	public static IDataset queryProductPackageByPackageId(String packageId, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("PACKAGE_ID", packageId);
		return Dao.qryByCodeParser("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_PACKAGE_BY_PACKAGE_ID", param, page);
	}

	public static IDataset querySpecByProductId(String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		IDataset dataset = UPackageInfoQry.getPackagesByProductId(productId);
		;// Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_FOR_SPEC",
		// param, Route.CONN_CRM_CEN);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData packageInfo = dataset.getData(i);
				if (!"1".equals(packageInfo.getString("MIN_NUMBER", "")) && !"1".equals(packageInfo.getString("MAX_NUMBER", "")) && !"D".equals(packageInfo.getString("LIMIT_TYPE", "")) && !"1".equals(packageInfo.getString("FORCE_TAG", "")))
				{
					dataset.remove(i);
				}
			}

		}
		return dataset;
	}

	public static IDataset queryUserPackage(String PRODUCT_ID, String ELEMENT_ID, String ELEMENT_TYPE_CODE) throws Exception
	{
		IDataset results = UpcCall.getPackageElementInfoByPorductIdElementId(PRODUCT_ID, "P", ELEMENT_ID, ELEMENT_TYPE_CODE);
		if (IDataUtil.isNotEmpty(results))
		{
			for (int i = 0; i < results.size(); i++)
			{
				IData data = results.getData(i);
				data.put("PRODUCT_ID", PRODUCT_ID);
				data.put("PACKAGE_ID", data.getString("OFFER_CODE", "-1"));
				data.put("ELEMENT_TYPE_CODE", ELEMENT_TYPE_CODE);
				data.put("ELEMENT_ID", ELEMENT_ID);
			}
		}
		return results;
	}

	public static IDataset queryProductByPackageID(String packageid) throws Exception
	{// kangyt 2015-5-10
		IData param = new DataMap();
		param.put("PACKAGE_ID", packageid);
		return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_BY_PACKID", param, Route.CONN_CRM_CEN);
	}
}
