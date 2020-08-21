
package com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public final class UcaDataFactory
{
	/**
	 * 根据IData构建UCA信息，用于新开户等业务该号码还没有三户资料的情况
	 * 
	 * @author anwx@asiainfo-linkage.com @ 2013-4-14
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static UcaData buildNewUca(IData param, boolean group) throws Exception
	{
		UcaData ucaData = new UcaData();
		ucaData.setUser(new UserTradeData(param));
		ucaData.setCustomer(new CustomerTradeData(param));
		if (group)
		{
			ucaData.setCustgroup(new CustGroupTradeData(param));
		}
		else
		{
			ucaData.setCustPerson(new CustPersonTradeData(param));
		}

		DataBusManager.getDataBus().setUca(ucaData);
		return ucaData;
	}

	public static UcaData getImproperUca(String sn) throws Exception
	{
		UcaData ucaData = DataBusManager.getDataBus().getUca(sn);
		if (ucaData != null)
		{
			return ucaData;
		}
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);

		ucaData = new UcaData();

		IDataset userInfos = BofQuery.getImproperUserInfoBySn(sn, RouteInfoQry.getEparchyCodeBySn(sn));

		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(BofException.CRM_BOF_002);
		}

		IData userInfo = userInfos.getData(0);
		ucaData.setUser(new UserTradeData(userInfo));

		// 查询用户最近的主产品信息
		IDataset mainProducts = UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
		if (IDataUtil.isNotEmpty(mainProducts))
		{
			ucaData.setBrandCode(mainProducts.getData(0).getString("BRAND_CODE"));
			ucaData.setProductId(mainProducts.getData(0).getString("PRODUCT_ID"));
		}

		setCustAcct(ucaData);
		DataBusManager.getDataBus().setUca(ucaData);
		return ucaData;
	}

	/**
	 * 获取正常用户的三户资料，主要用于个人业务，如果传入的号码是集团的SERIAL_NUMBER，则三户资料中的客户资料为集团客户资料，根据手机号码的路由信息改变上下文对象的路由地州
	 * 
	 * @param pd
	 *            上下文对象
	 * @param param
	 *            必须包含SERIAL_NUMBER的key
	 * @return 正常用户三户资料对象
	 * @throws Exception
	 */
	public static UcaData getNormalUca(String sn) throws Exception
	{
		return getNormalUca(sn, true, true);
	}

	public static UcaData getNormalUca(String sn, boolean isQry) throws Exception
	{
		if (isQry)
		{
			return getNormalUcaByQry(sn, true, true);
		}
		else
		{
			return getNormalUca(sn, true, true);
		}
	}

	/**
	 * 获取正常用户的三户资料，主要用于个人业务，如果传入的号码是集团的SERIAL_NUMBER，则三户资料中的客户资料为集团客户资料，根据手机号码的路由信息改变上下文对象的路由地州
	 * 
	 * @param pd
	 *            上下文对象
	 * @param param
	 *            必须包含SERIAL_NUMBER的key
	 * @return 正常用户三户资料对象
	 * @throws Exception
	 */
	public static UcaData getNormalUca(String sn, boolean queryCustInfo, boolean queryAcctInfo) throws Exception
	{
		UcaData ucaData = DataBusManager.getDataBus().getUca(sn);

		if (ucaData != null)
		{
			AcctTimeEnv env = new AcctTimeEnv(ucaData.getAcctDay(), ucaData.getFirstDate(), ucaData.getNextAcctDay(), ucaData.getNextFirstDate(), ucaData.getAcctDayStartDate(), ucaData.getNextAcctDayStartDate());
			AcctTimeEnvManager.setAcctTimeEnv(env);
			return ucaData;
		}

		return UcaDataFactory.getNormalUcaByQry(sn, queryCustInfo, queryAcctInfo);
	}

	// ---------------------------集团查询-------------------------------
	public static UcaData getNormalUcaByCustIdForGrp(IData param) throws Exception
	{
		UcaData ucaData = new UcaData();

		qryGroupInfo(ucaData, param);

		// 对于账户acct_id选择的情况
		boolean acctIdAdd = param.getBoolean("ACCT_IS_ADD");

		if (!acctIdAdd)
		{
			qryAcctInfoByAcctIdForGrp(ucaData, param);
		}

		return ucaData;
	}

	public static UcaData getNormalUcaByQry(String sn, boolean queryCustInfo, boolean queryAcctInfo) throws Exception
	{
		UcaData ucaData = new UcaData();

		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(sn, RouteInfoQry.getEparchyCodeBySn(sn));

		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_002);
		}

		ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
		ucaData.setProductId(userInfo.getString("PRODUCT_ID"));
		ucaData.setUser(new UserTradeData(userInfo));
		ucaData.getUserOriginalData().setUser(new UserTradeData());

		setUserAcctDay(ucaData);

		// setCustAcct(ucaData);
		if (queryCustInfo)
		{
			qryCustInfo(ucaData);
		}

		if (queryAcctInfo)
		{
			qryAcctInfo(ucaData);
		}

		if (DataBusManager.isHasDataBus())
		{
			DataBusManager.getDataBus().setUca(ucaData);
		}

		return ucaData;
	}

	public static UcaData getNormalUcaBySnForGrp(IData param) throws Exception
	{
		UcaData ucaData = new UcaData();

		String sn = param.getString("SERIAL_NUMBER");

		if (StringUtils.isBlank(sn))
		{
			CSAppException.apperr(BofException.CRM_BOF_013);
		}

		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(sn);

		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_017, sn);
		}

		ucaData.setUser(new UserTradeData(userInfo));

		ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
		ucaData.setProductId(userInfo.getString("PRODUCT_ID"));

		setCustAcct(ucaData, true);

		return ucaData;
	}

	public static UcaData getNormalUcaByUserIdForGrp(IData param) throws Exception
	{
		UcaData ucaData = new UcaData();

		String userId = param.getString("USER_ID", "");

		if (StringUtils.isBlank(userId))
		{
			CSAppException.apperr(BofException.CRM_BOF_008);
		}

		IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_011, userId);
		}

		ucaData.setUser(new UserTradeData(userInfo));

		ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
		ucaData.setProductId(userInfo.getString("PRODUCT_ID"));

		setCustAcct(ucaData, true);

		return ucaData;
	}

	/**
	 * 根据服务号码获取三户信息, 对于集团成员用户(集团成员用户包括网外号码)
	 * 
	 * @param sn
	 * @return
	 * @throws Exception
	 */
	public static UcaData getNormalUcaForGrp(String sn) throws Exception
	{
		return getNormalUcaForGrp(sn, true, true);
	}

	public static UcaData getNormalUcaForGrp(String sn, boolean queryCustInfo, boolean queryAcctInfo) throws Exception
	{
		UcaData ucaData = DataBusManager.getDataBus().getUca(sn);

		if (ucaData != null)
		{
			return ucaData;
		}
		ucaData = new UcaData();
		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(sn, RouteInfoQry.getEparchyCodeBySnForCrm(sn));

		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_017, sn);
		}

		ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
		ucaData.setProductId(userInfo.getString("PRODUCT_ID"));
		ucaData.setUser(new UserTradeData(userInfo));

		// setCustAcct(ucaData);
		if (queryCustInfo)
		{
			qryCustInfo(ucaData);
		}

		if (queryAcctInfo)
		{
			qryAcctInfo(ucaData);
		}

		DataBusManager.getDataBus().setUca(ucaData);
		return ucaData;
	}

	public static UcaData getUcaByUserId(String userId) throws Exception
	{
		IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId, BizRoute.getRouteId());
		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_002);
		}

		UcaData ucaData = DataBusManager.getDataBus().getUca(userInfo.getString("SERIAL_NUMBER"));

		if (ucaData != null)
		{
			return ucaData;
		}

		ucaData = new UcaData();
		ucaData.setUser(new UserTradeData(userInfo));
		ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
		ucaData.setProductId(userInfo.getString("PRODUCT_ID"));

		setUserAcctDay(ucaData);

		qryCustInfo(ucaData);
		qryAcctInfo(ucaData);

		if (DataBusManager.isHasDataBus())
		{
			DataBusManager.getDataBus().setUca(ucaData);
		}
		return ucaData;
	}

	/**
	 * 查询CG虚拟用户三户资料信息
	 * 
	 * @param sn
	 * @param queryCustInfo
	 * @param queryAcctInfo
	 * @return UcaData
	 * @throws Exception
	 *             wangjx 2013-8-28
	 */
	public static UcaData getVirtualUca(String sn, boolean queryCustInfo, boolean queryAcctInfo) throws Exception
	{
		UcaData ucaData = DataBusManager.getDataBus().getUca(sn);

		if (ucaData != null)
		{
			return ucaData;
		}

		ucaData = new UcaData();

		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(sn, Route.CONN_CRM_CG);

		if (IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_002);
		}

		ucaData.setBrandCode(userInfo.getString("BRAND_CODE"));
		ucaData.setProductId(userInfo.getString("PRODUCT_ID"));
		ucaData.setUser(new UserTradeData(userInfo));

		if (queryCustInfo)
		{
			qryVirtualCustInfo(ucaData);
		}

		if (queryAcctInfo)
		{
			qryVirtualAcctInfo(ucaData);
		}

		DataBusManager.getDataBus().setUca(ucaData);

		return ucaData;
	}

	/**
	 * 根据User_id查询默认付费的帐户信息
	 * 
	 * @param pd
	 *            上下文对象
	 * @param param
	 *            必须包含USER_ID的key
	 * @throws Exception
	 */
	private static void qryAcctInfo(UcaData uca) throws Exception
	{
		// 查有效付费关系
		IData payrela = UcaInfoQry.qryDefaultPayRelaByUserId(uca.getUserId(), uca.getUserEparchyCode());

		if (IDataUtil.isEmpty(payrela))
		{
			payrela = UcaInfoQry.qryLastPayRelaByUserId(uca.getUserId(), uca.getUserEparchyCode());

			if (IDataUtil.isEmpty(payrela))
			{
				CSAppException.apperr(BofException.CRM_BOF_003);
			}
		}

		IData acct = UcaInfoQry.qryAcctInfoByAcctId(payrela.getString("ACCT_ID"), uca.getUserEparchyCode());
		if (IDataUtil.isEmpty(acct))
		{
			CSAppException.apperr(BofException.CRM_BOF_004);
		}

		uca.setAccount(new AccountTradeData(acct));
		uca.getUserOriginalData().setAccount(new AccountTradeData(acct));
	}

	private static void qryAcctInfoByAcctIdForGrp(UcaData ucaData, IData param) throws Exception
	{
		String acctId = param.getString("ACCT_ID");

		IData acct = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acctId);

		if (IDataUtil.isEmpty(acct))
		{
			CSAppException.apperr(BofException.CRM_BOF_012);
		}

		ucaData.setAccount(new AccountTradeData(acct));
	}

	/**
	 * 根据User_id查询默认付费的帐户信息
	 * 
	 * @param pd
	 *            上下文对象
	 * @param param
	 *            必须包含USER_ID的key
	 * @throws Exception
	 */
	private static void qryAcctInfoForGrp(UcaData uca) throws Exception
	{
		// 查有效付费关系
		IData payrela = UcaInfoQry.qryPayRelaByUserIdForGrp(uca.getUserId());

		if (IDataUtil.isEmpty(payrela))
		{
			CSAppException.apperr(BofException.CRM_BOF_003);
		}

		String acctId = payrela.getString("ACCT_ID");
		IData acct = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acctId);

		if (IDataUtil.isEmpty(acct))
		{
			CSAppException.apperr(BofException.CRM_BOF_004);
		}

		uca.setAccount(new AccountTradeData(acct));
	}

	/**
	 * 获取客户资料信息，如果查询不到个人客户信息，则会查询集团客户信息
	 * 
	 * @param pd
	 *            上下文对象
	 * @param param
	 *            必须包含CUST_ID的key
	 * @throws Exception
	 */
	private static void qryCustInfo(UcaData uca) throws Exception
	{
		String custId = uca.getUser().getCustId();

		IData customer = UcaInfoQry.qryCustomerInfoByCustId(custId, uca.getUserEparchyCode());

		if (IDataUtil.isEmpty(customer))
		{
			CSAppException.apperr(BofException.CRM_BOF_005);
		}

		uca.setCustomer(new CustomerTradeData(customer));
		uca.getUserOriginalData().setCustomer(new CustomerTradeData(customer));

		// 查询个人客户信息
		IData custInfo = UcaInfoQry.qryPerInfoByCustId(custId, uca.getUserEparchyCode());

		if (IDataUtil.isNotEmpty(custInfo))
		{
			uca.setCustPerson(new CustPersonTradeData(custInfo));
			uca.getUserOriginalData().setCustPerson(new CustPersonTradeData(custInfo));
			return;
		}

		// 如果没查到则查集团客户信息
		custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);

		if (IDataUtil.isNotEmpty(custInfo))
		{
			// CSAppException.apperr(BofException.CRM_BOF_006);
			uca.setCustgroup(new CustGroupTradeData(custInfo));
		}

		// uca.setCustgroup(new CustGroupTradeData(custInfo));
	}

	private static void qryGroupInfo(UcaData ucaData, IData param) throws Exception
	{
		if (!param.containsKey("CUST_ID"))
		{
			CSAppException.apperr(BofException.CRM_BOF_009);
		}

		String custId = param.getString("CUST_ID");
		IData customerInfo = UcaInfoQry.qryCustomerInfoByCustIdForGrp(custId);

		if (IDataUtil.isEmpty(customerInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_005);
		}

		ucaData.setCustomer(new CustomerTradeData(customerInfo));

		IData groupInfo = UcaInfoQry.qryGrpInfoByCustId(custId);

		if (IDataUtil.isEmpty(groupInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_010);
		}

		ucaData.setCustgroup(new CustGroupTradeData(groupInfo));
	}

	/**
	 * 查虚拟用户的有效付费关系
	 * 
	 * @param uca
	 * @throws Exception
	 *             wangjx 2013-8-28
	 */
	private static void qryVirtualAcctInfo(UcaData uca) throws Exception
	{
		IData payrela = UcaInfoQry.qryPayRelaByUserId(uca.getUserId(), Route.CONN_CRM_CG);

		if (IDataUtil.isEmpty(payrela))
		{
			CSAppException.apperr(BofException.CRM_BOF_003);
		}

		String acctId = payrela.getString("ACCT_ID");

		IData acctInfo = UcaInfoQry.qryAcctInfoByAcctId(acctId, Route.CONN_CRM_CG);

		if (IDataUtil.isEmpty(acctInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_004);
		}

		uca.setAccount(new AccountTradeData(acctInfo));
	}

	/**
	 * 查询虚拟客户资料时不查询CUST_PERSON、CUST_GROUP
	 * 
	 * @param uca
	 * @throws Exception
	 *             wangjx 2013-8-28
	 */
	private static void qryVirtualCustInfo(UcaData uca) throws Exception
	{
		String custId = uca.getUser().getCustId();

		IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(custId, Route.CONN_CRM_CG);

		if (IDataUtil.isEmpty(customerInfo))
		{
			CSAppException.apperr(BofException.CRM_BOF_005);
		}

		uca.setCustomer(new CustomerTradeData(customerInfo));
	}

	/**
	 * 私有方法，用于补充三户资料对象中的客户信息和帐户信息
	 * 
	 * @param pd
	 *            上下文对象
	 * @param ucaData
	 *            含有用户信息的三户资料对象
	 * @param isGroup
	 *            是否集团客户
	 * @throws Exception
	 */
	private static void setCustAcct(UcaData ucaData) throws Exception
	{
		setCustAcct(ucaData, false);
	}

	/**
	 * 私有方法，用于补充三户资料对象中的客户信息和帐户信息 上下文对象
	 * 
	 * @param ucaData
	 *            含有用户信息的三户资料对象
	 * @param isGroup
	 *            是否集团客户
	 * @throws Exception
	 */
	private static void setCustAcct(UcaData ucaData, boolean isGroup) throws Exception
	{

		IData custParam = new DataMap();
		custParam.put("CUST_ID", ucaData.getUser().getCustId());

		if (isGroup)
		{
			qryGroupInfo(ucaData, custParam);
			qryAcctInfoForGrp(ucaData);
		}
		else
		{
			qryCustInfo(ucaData);
			qryAcctInfo(ucaData);
		}

	}

	public static void setUserAcctDay(UcaData ucaData) throws Exception
	{
		IDataset userAcctDays = UcaInfoQry.qryUserAcctDaysByUserId(ucaData.getUserId(), ucaData.getUserEparchyCode());
		String startDate = "";
		String nextStartDate = "";
		if (IDataUtil.isNotEmpty(userAcctDays))
		{
			int size = userAcctDays.size();
			IData userAcctDay = userAcctDays.getData(0);
			ucaData.setAcctDay(userAcctDay.getString("ACCT_DAY"));
			ucaData.setFirstDate(userAcctDay.getString("FIRST_DATE"));
			startDate = userAcctDay.getString("START_DATE");
			ucaData.setAcctDayStartDate(startDate);
			if (size > 1)
			{
				IData userNextAcctDay = userAcctDays.getData(1);
				ucaData.setNextAcctDay(userNextAcctDay.getString("ACCT_DAY"));
				ucaData.setNextFirstDate(userNextAcctDay.getString("FIRST_DATE"));
				nextStartDate = userNextAcctDay.getString("START_DATE");
				ucaData.setNextAcctDayStartDate(nextStartDate);
			}
			if (StringUtils.isNotBlank(ucaData.getFirstDate()) && ucaData.getFirstDate().length() > 10)
			{
				ucaData.setFirstDate(ucaData.getFirstDate().substring(0, 10));
			}
			if (StringUtils.isNotBlank(ucaData.getNextFirstDate()) && ucaData.getNextFirstDate().length() > 10)
			{
				ucaData.setNextFirstDate(ucaData.getNextFirstDate().substring(0, 10));
			}
		}
		else
		{
			return;
		}
		AcctTimeEnv env = new AcctTimeEnv(ucaData.getAcctDay(), ucaData.getFirstDate(), ucaData.getNextAcctDay(), ucaData.getNextFirstDate(), startDate, nextStartDate);
		AcctTimeEnvManager.setAcctTimeEnv(env);
	}

	/**
	 * @methodName: getDestroyUserUcaByUserId
	 * @Description: 复机时获取已销户用户的UCA数据
	 * @version: v1.0.0
	 * @author: xiaozb
	 * @date: 2014-5-15 下午4:26:14
	 */
	public static UcaData getDestroyUcaByUserId(String userId) throws Exception
	{
		UcaData ucaData = new UcaData();

		IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);

		if (IDataUtil.isEmpty(userInfo))
		{
			userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(userId);
			if (IDataUtil.isEmpty(userInfo))
			{
				CSAppException.apperr(BofException.CRM_BOF_002);
			}
		}
		else
		{
			String removeTag = userInfo.getString("REMOVE_TAG", "");
			if (StringUtils.isEmpty(removeTag) || "0".equals(removeTag) || "5".equals(removeTag))
			{
				CSAppException.apperr(BofException.CRM_BOF_020);
			}
		}

		ucaData.setUser(new UserTradeData(userInfo));

		// 查询用户最近的主产品信息
		IData productData = new DataMap();
		IDataset mainProducts = UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
		if (IDataUtil.isNotEmpty(mainProducts))
		{
			productData = mainProducts.getData(0);
		}
		else
		{
			productData = UserProductInfoQry.qryLasterMainProdInfoByUserIdFromHis(userId);
			if (IDataUtil.isEmpty(productData))
			{
				CSAppException.apperr(CrmUserException.CRM_USER_631);
			}
		}

		ucaData.setBrandCode(productData.getString("BRAND_CODE"));
		ucaData.setProductId(productData.getString("PRODUCT_ID"));

		qryCustInfo(ucaData);

		// 查有效付费关系
		String acctId = "";
		IData payrela = UcaInfoQry.qryDefaultPayRelaByUserId(ucaData.getUserId(), ucaData.getUserEparchyCode());
		if (IDataUtil.isEmpty(payrela))
		{
			payrela = UcaInfoQry.qryLastPayRelaByUserId(ucaData.getUserId(), ucaData.getUserEparchyCode());
			if (IDataUtil.isNotEmpty(payrela))
			{
				acctId = payrela.getString("ACCT_ID");
			}
			else
			{
				IDataset acctDataset = UAcctInfoQry.qryAcctInfoByCustId(ucaData.getCustId());
				if (IDataUtil.isNotEmpty(acctDataset))
				{
					acctId = acctDataset.getData(0).getString("ACCT_ID");
				}
			}
		}
		else
		{
			acctId = payrela.getString("ACCT_ID");
		}

		IData acct = UcaInfoQry.qryAcctInfoByAcctId(acctId, ucaData.getUserEparchyCode());
		if (IDataUtil.isEmpty(acct))
		{
			CSAppException.apperr(BofException.CRM_BOF_004);
		}

		ucaData.setAccount(new AccountTradeData(acct));
		ucaData.getUserOriginalData().setAccount(new AccountTradeData(acct));

		DataBusManager.getDataBus().setUca(ucaData);
		return ucaData;
	}
}
