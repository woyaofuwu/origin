
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class UserSaleActiveInfoQry
{
	/**
	 * @Function: cancelLimit
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:27:40 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static boolean cancelLimit(String cust_id, String relation_trade_id) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT t.* FROM tf_f_user_sale_active t  ");
		parser.addSQL("WHERE t.user_id=:CUST_ID ");
		parser.addSQL("AND t.relation_trade_id=:RELATION_TRADE_ID ");
		parser.addSQL("AND t.process_tag='0' ");
		parser.addSQL("and to_char(t.start_date,'mm')=to_char(SYSDATE,'mm') ");
		parser.addSQL("and to_char(t.start_date,'yyyy')=to_char(SYSDATE,'yyyy') ");
		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		if (dataset != null && dataset.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public static IDataset getBook2ValidSaleActiveByTradeId(String trade_id, String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SERIAL_NUMBER", serial_number);
		return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_BOOK2VALID_ACTIVE", param);
	}

	/**
	 * @Function: getEffectiveSaleActiveByUserId
	 * @Description: 营销活动历史查询，，，查询一段时间有效的营销活动
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-10-17 上午10:51:00 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-10-17 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getEffectiveSaleActiveByUserId(String user_id, String start_time, String end_time, Pagination page) throws Exception
	{
		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT t.SERIAL_NUMBER,t.PRODUCT_ID,t.PRODUCT_NAME,t.PACKAGE_ID,t.PACKAGE_NAME,(t.OPER_FEE + t.ADVANCE_PAY) / 100 CAMPN_FEE,t.TRADE_STAFF_ID,t.START_DATE,t.END_DATE,t.ACCEPT_DATE,t.REMARK ");
		sql.append("FROM TF_F_USER_SALE_ACTIVE t ");
		sql.append("WHERE t.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		sql.append("AND t.USER_ID = :USER_ID ");
		sql.append("AND t.ACCEPT_DATE >= to_date(:START_TIME,'yyyy-MM-dd') ");
		sql.append("AND t.ACCEPT_DATE <= to_date(:END_TIME,'yyyy-MM-dd')+1 ");
		sql.append("AND sysdate between t.START_DATE and t.END_DATE ");
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("START_TIME", start_time);
		param.put("END_TIME", end_time);
		return Dao.qryBySql(sql, param, page);

	}

	/**
	 * @Function: getExpirSaleActiveByUserId
	 * @Description: 营销活动历史查询，，，查询一段时间失效的营销活动
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-10-17 上午10:58:14 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-10-17 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getExpirSaleActiveByUserId(String user_id, String start_time, String end_time, Pagination page) throws Exception
	{
		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT t.SERIAL_NUMBER,t.PRODUCT_ID,t.PRODUCT_NAME,t.PACKAGE_ID,t.PACKAGE_NAME,(t.OPER_FEE + t.ADVANCE_PAY) / 100 CAMPN_FEE,t.TRADE_STAFF_ID,t.START_DATE,t.END_DATE,t.ACCEPT_DATE,t.REMARK ");
		sql.append("FROM TF_F_USER_SALE_ACTIVE t ");
		sql.append("WHERE t.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		sql.append("AND t.USER_ID = :USER_ID ");
		sql.append("AND t.ACCEPT_DATE >= to_date(:START_TIME,'yyyy-MM-dd') ");
		sql.append("AND t.ACCEPT_DATE <= to_date(:END_TIME,'yyyy-MM-dd')+1 ");
		sql.append("AND (sysdate > t.END_DATE or sysdate < t.START_DATE)");
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("START_TIME", start_time);
		param.put("END_TIME", end_time);
		return Dao.qryBySql(sql, param, page);

	}

	/**
	 * @Function: getGJHDGoodsInfoByUserId
	 * @Description: 根据userId查询购机活动信息
	 * @param: @param userId
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 */
	public static IDataset getGJHDGoodsInfoByUserId(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_SALEACTIVE_GOODS_INFOS", inparam);
	}

	/**
	 * 根据USER_ID, RELATION_TRADE_ID ,CAMPN_TYPE,RES_TYPE_CODE用户购机信息
	 * 
	 * @param pd
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author wangf 2009-04-10
	 */

	public static IDataset getGoodsInfoByMobPhoneService(IData param) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_TRADEID", param);
	}

	/**
	 * 根据USER_ID, RELATION_TRADE_ID ,RES_TYPE_CODE用户购机信息
	 * 
	 * @param pd
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author wangf 2009-04-10
	 */

	public static IDataset getGoodsInfoByMobPhoneService(String userId, String relationTradeId, String resTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TRADE_ID", relationTradeId);
		param.put("RES_TYPE_CODE", resTypeCode);
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_TRADEID", param);
	}

	/**
	 * @Function: getPurchaseInfoByUserId
	 * @Description: 根据用户标识USER_ID查询用户购机信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:29:00 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset getPurchaseInfoByUserId(String user_id, String goods_state, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("GOODS_STATE", goods_state);

		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_USERID_NOW", param, pagination);
	}

	public static IDataset getSaleActiveBookByTradeId(String trade_id, String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SERIAL_NUMBER", serial_number);
		return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_TRADEID_SN", param);
	}

	public static IDataset getSaleActiveByDate(String campnType, String startDate, String endDate, String eparchyCode, Pagination pagination) throws Exception
	{
		IData cond = new DataMap();
		cond.put("START_DATE", startDate);
		cond.put("END_DATE", endDate);
		cond.put("CAMPN_TYPE", campnType);
		cond.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_CAMPN_TYPE_DATE", cond, pagination);
	}

	public static IDataset getSaleActiveByProduct(String userId, String processTag, String campnType, String productId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("PROCESS_TAG", processTag);
		cond.put("CAMPN_TYPE", campnType);
		cond.put("PRODUCT_ID", productId);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PRODUCTID_ARCHIVE", cond);
	}

	public static IDataset getSaleActiveByProductCommPara(String userId, String campnType, String productId, String eparchyCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("CAMPN_TYPE", campnType);
		cond.put("PRODUCT_ID", productId);
		cond.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PRODUCTID_COMMPARA", cond);
	}

	public static IDataset getSaleActiveByUserIdProcessTag(String user_id, String process_tag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PROCESS_TAG", process_tag);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_PROCESSTAG", param);
	}

	/**
	 * 根据USER_ID, PROCESS_TAG,CAMPN_TYPE获取营销活动信息
	 * 
	 * @param pd
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author zhangmeiquan 2009-04-10
	 */
	public static IDataset getSaleActiveInfo(IData inparams) throws Exception
	{
		// inparams.put("CAMPN_TYPE", "YC");//YC：终端营销 HF: 预存营销
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_CAMPN_TYPE", inparams);
	}

	/**
	 * @Function: getSaleActiveInfo
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:30:44 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset getSaleActiveInfo(String user_id, String process_tag, String start_date, String end_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PROCESS_TAG", process_tag);
		param.put("START_DATE", start_date);
		param.put("END_DATE", end_date);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SALEACTIVE", param);
	}
public static IDataset getSaleActiveInfo2(String userId, String processTag, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", processTag);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SALEACTIVE_PA", param);
	}

	public static IDataset getSaleActiveInfo1(IData inparams) throws Exception
	{
		// inparams.put("CAMPN_TYPE", "YC");//YC：终端营销 HF: 预存营销
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_CAMPN_TYPE1", inparams);
	}

	/**
	 * 根据USER_ID, PROCESS_TAG,CAMPN_TYPE,PDATA_ID获取营销活动信息 PDATA_ID 可以根据static获取
	 * 
	 * @param pd
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author wangf 2009-04-10
	 */
	public static IDataset getSaleActiveInfoByMobPhoneService(IData inparams) throws Exception
	{
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_CAMPN_TYPEANDTIME", inparams);
	}

	/**
	 * 根据USER_ID, PROCESS_TAG,CAMPN_TYPE,PDATA_ID获取营销活动信息 PDATA_ID 可以根据static获取
	 * 
	 * @param pd
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author wangf 2009-04-10
	 */
	public static IDataset getSaleActiveInfoByMobPhoneService(String userId, String processTag, String campnType, String staticTypeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", processTag);
		param.put("CAMPN_TYPE", campnType);
		IDataset data = StaticInfoQry.getStaticValueByTypeId(staticTypeId);
		if (data != null && data.size() > 0)
		{
			param.put("PDATA_ID", data.getData(0).getString("PDATA_ID"));
		}
		IDataset res = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_CAMPN_TYPEANDTIME", param);
		return res;
	}

	public static IDataset getSaleActiveInfoOnInterface(IData params) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_PRODUCTID", params);

	}

	/**
	 * @Function: getSaleActiveTradeCount
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:31:31 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static int getSaleActiveTradeCount(String productId, String packageId) throws Exception
	{
		int count = 0;
		IData cond = new DataMap();
		cond.put("PACKAGE_ID", packageId);
		cond.put("PRODUCT_ID", productId);
		IDataset result = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_COUNT_BY_PRODUCT_PACKAGE", cond);
		if (IDataUtil.isNotEmpty(result))
		{
			count = result.getData(0).getInt("SALE_COUNT");
		}

		return count;
	}

	public static IDataset getUserSaleActive(String userId, String relTradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TRADE_ID", relTradeId);

		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_ACTIVE", param);

	}

	public static IDataset getUserSaleActiveBySelAallActiveReturn(String userId, String relTradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TRADE_ID", relTradeId);

		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_ACTIVE_RETURN", param);

	}

	/**
	 * 根据开始时间，结束时间查询
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserSaleActiveByStartEndDate(String userId, String startDate, String endDate) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("START_DATE", startDate);
		params.put("END_DATE", endDate);

		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_STARTDATE_ENDDATE", params);
	}

	public static IDataset getUserSaleActivesBySelbyUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID", param);

	}

	public static IDataset getUserSaleActivesBySelbyUserId(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID", param, eparchyCode);

	}

	public static IDataset getUserSaleActivesByUserAndProcessTag(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_AND_PROCESSTAG", param);

	}

	public static IDataset getUserSaleGoodsByUserId(String userId) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_USERID_DATE", iparam);

	}

	public static IDataset getUserSaleGoodsInfoByUserId(String userId, String eparchyCode) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_USERID", iparam, eparchyCode);

	}

	/**
	 * @Function: limitGroupSale
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:31:25 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static boolean limitGroupSale(String group_id) throws Exception
	{

		IData param = new DataMap();
		param.put("GROUP_ID", group_id);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT COUNT(*)  COUNTS from tf_f_user_sale_active t ");
		parser.addSQL("WHERE t.serial_number=:GROUP_ID ");
		parser.addSQL("AND t.process_tag='0' ");
		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		String fact_members = "";
		if (dataset != null && dataset.size() > 0)
		{
			fact_members = (dataset.getData(0)).getString("COUNTS", "0");
		}
		return (Integer.parseInt(fact_members)) > 0 ? true : false;
	}

	/**
	 * 营销活动综合查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset qrySaleActiveBySnStaffDeptCampnSDateEDate(String serialNumber, String staffId, String deptId, String campnType, String startDate, String endDate, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("STAFF_ID", staffId);
		param.put("DEPART_ID", deptId);
		param.put("CAMPN_TYPE", campnType);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		
		String productId = "";
		//查询集团营销活动产品
	    IDataset productInfo = UpcCall.queryLevelCatalogByUpCatalogId("K002","3");
	    for(int i=0;i<productInfo.size();i++){
	    	IData product = productInfo.getData(i);
	    	String catalogId = product.getString("CATALOG_ID");
	    	if(i==0)
	    	    productId += "'"+catalogId+"'";
	    	else
	    		productId +=",'"+catalogId+"'";
	    }
		
		SQLParser parser = new SQLParser(param);

		parser.addSQL("SELECT ");
        parser.addSQL("T.PARTITION_ID,");
        parser.addSQL("TO_CHAR(T.USER_ID) USER_ID,");
        parser.addSQL("T.SERIAL_NUMBER,");
        parser.addSQL("T.SERIAL_NUMBER_B,");
        parser.addSQL("T.PRODUCT_MODE,");
        parser.addSQL("T.PRODUCT_ID,");
        parser.addSQL("T.PRODUCT_NAME,");
        parser.addSQL("T.PACKAGE_ID,");
        parser.addSQL("T.PACKAGE_NAME,");
        parser.addSQL("TO_CHAR(T.CAMPN_ID) CAMPN_ID,");
        parser.addSQL("T.CAMPN_CODE,");
        parser.addSQL("T.CAMPN_NAME,");
        parser.addSQL("T.CAMPN_TYPE,");
        parser.addSQL("TO_CHAR(T.OPER_FEE) OPER_FEE,");
        parser.addSQL("TO_CHAR(T.FOREGIFT) FOREGIFT,");
        parser.addSQL("TO_CHAR(T.ADVANCE_PAY) ADVANCE_PAY,");
        parser.addSQL("TO_CHAR(T.SCORE_CHANGED) SCORE_CHANGED,");
        parser.addSQL("TO_CHAR(T.CREDIT_VALUE) CREDIT_VALUE,");
        parser.addSQL("T.MONTHS,");
        parser.addSQL("T.END_MODE,");
        parser.addSQL("T.ACTOR_PSPT_ID,");
        parser.addSQL("T.ACTOR_PSPT_TYPE_CODE,");
        parser.addSQL("T.ACTOR_PHONE,");
        parser.addSQL("T.ACTOR_NAME,");
        parser.addSQL("T.APPR_STAFF_ID,");
        parser.addSQL("TO_CHAR(T.APPR_TIME, 'yyyy-mm-dd hh24:mi:ss') APPR_TIME,");
        parser.addSQL("T.OUT_NET_PHONE,");
        parser.addSQL("T.CONTRACT_ID,");
        parser.addSQL("T.PROCESS_TAG,");
        parser.addSQL("TO_CHAR(T.ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,");
        parser.addSQL("T.TRADE_STAFF_ID,");
        parser.addSQL("TO_CHAR(T.RELATION_TRADE_ID) RELATION_TRADE_ID,");
        parser.addSQL("TO_CHAR(T.CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,");
        parser.addSQL("T.CANCEL_STAFF_ID,");
        parser.addSQL("T.CANCEL_CAUSE,");
        parser.addSQL("TO_CHAR(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        parser.addSQL("TO_CHAR(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,");
        parser.addSQL("TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL("T.UPDATE_STAFF_ID,");
        parser.addSQL("T.UPDATE_DEPART_ID,");
        parser.addSQL("T.REMARK,");
        parser.addSQL("T.RSRV_NUM1,");
        parser.addSQL("T.RSRV_NUM2,");
        parser.addSQL("T.RSRV_NUM3,");
        parser.addSQL("TO_CHAR(T.RSRV_NUM4) RSRV_NUM4,");
        parser.addSQL("TO_CHAR(T.RSRV_NUM5) RSRV_NUM5,");
        parser.addSQL("T.RSRV_STR1,");
        parser.addSQL("T.RSRV_STR2,");
        parser.addSQL("T.RSRV_STR3,");
        parser.addSQL("T.RSRV_STR4,");
        parser.addSQL("T.RSRV_STR5,");
        parser.addSQL("T.RSRV_STR6,");
        parser.addSQL("T.RSRV_STR7,");
        parser.addSQL("T.RSRV_STR8,");
        parser.addSQL("T.RSRV_STR9,");
        parser.addSQL("T.RSRV_STR10,");
        parser.addSQL("T.RSRV_STR11,");
        parser.addSQL("T.RSRV_STR12,");
        parser.addSQL("T.RSRV_STR13,");
        parser.addSQL("T.RSRV_STR14,");
        parser.addSQL("T.RSRV_STR15,");
        parser.addSQL("T.RSRV_STR16,");
        parser.addSQL("T.RSRV_STR17,");
        parser.addSQL("T.RSRV_STR18,");
        parser.addSQL("T.RSRV_STR19,");
        parser.addSQL("T.RSRV_STR20,");
        parser.addSQL("T.RSRV_STR21,");
        parser.addSQL("T.RSRV_STR22,");
        parser.addSQL("T.RSRV_STR23,");
        parser.addSQL("T.RSRV_STR24,");
        parser.addSQL("T.RSRV_STR25,");
        parser.addSQL("TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,");
        parser.addSQL("TO_CHAR(T.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,");
        parser.addSQL("TO_CHAR(T.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,");
        parser.addSQL("T.RSRV_TAG1,");
        parser.addSQL("T.RSRV_TAG2,");
        parser.addSQL("T.RSRV_TAG3 ");
        parser.addSQL(" FROM TF_F_USER_SALE_ACTIVE T,TF_F_USER_PRODUCT P ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND T.TRADE_STAFF_ID = :STAFF_ID ");
        parser.addSQL("		AND T.UPDATE_DEPART_ID = :DEPART_ID ");
        parser.addSQL("		AND T.CAMPN_TYPE = :CAMPN_TYPE ");
        parser.addSQL("		AND T.ACCEPT_DATE > TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')"); 
        parser.addSQL("		AND T.ACCEPT_DATE < TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("	AND T.USER_ID = P.USER_ID AND P.PRODUCT_MODE = '10' ");
        parser.addSQL("	AND T.PRODUCT_ID IN ( "+productId+" )");
        parser.addSQL("	ORDER BY T.ACCEPT_DATE");
		IDataset dataset = Dao.qryByParse(parser, page, Route.CONN_CRM_CG);
				//Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_BY_SN_STAFF_DEPT_CAMPN_SDATE_EDATE", param, page, Route.CONN_CRM_CG);
		
//		if(IDataUtil.isNotEmpty(dataset))
//		{
//			//查询集团营销活动产品
//		    IDataset productInfo = UpcCall.queryLevelCatalogByUpCatalogId("K002","3");
//		    int productSize =productInfo.size();
//		    for(int i=0;i<dataset.size();i++)
//		    {
//		    	IData data = dataset.getData(i);
//			    String product_id = data.getString("PRODUCT_ID");
//			    String package_id = data.getString("PACKAGE_ID");
//			    String package_name = data.getString("PACKAGE_NAME");
//			    String package_desc = "";
//			    IData packageInfo = UpcCall.queryOfferByOfferId("K", package_id);
//			    if(IDataUtil.isNotEmpty(packageInfo))
//			    {
//			    	package_desc = packageInfo.getString("DESCRIPTION", package_name);
//			    }else{
//			    	package_desc = package_name;
//			    }
//			    data.put("PACKAGE_DESC", package_desc);
//			    
//			    for(int j=0;j<productSize;j++)
//			    {
//			    	String catalogId = productInfo.getData(j).getString("CATALOG_ID");
//			    	if(catalogId.equals(product_id))
//			    	{
//			    		break;
//			    	}
//			    	else if(j==(productSize-1))
//			    	{
//			    		dataset.remove(i);
//			    		i--;
//			    	}
//			    }
//		    }
//		    
//		}
		return dataset;
	}

	public static IDataset qrySaleActiveByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_PRODID", param);
	}

	public static IDataset qryTERMINALORDERbyParams(String serialNumber, String processTag, String startDate, String endDate, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("PROCESS_TAG", processTag);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCodeParser("TF_F_TERMINALORDER", "SEL_BOOKSALEACTIVE1_NOW", param, pagination);
	}

	public static IDataset qryTERMINALORDERbyParams2(String serialNumber, String processTag, String startDate, String endDate, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("PROCESS_TAG", processTag);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCodeParser("TF_F_TERMINALORDER", "SEL_BOOKSALEACTIVE_NOW", param, pagination);
	}

	/**
	 * 查询所有的非签约类活动，包括失效的
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllNoQySaleActive(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		IDataset result1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_NOQYSALEACTIVE", params, pagination);
		IDataset result2 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_NOQYSALEACTIVE_HIS", params, pagination);
		if (IDataUtil.isEmpty(result1))
		{
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
		}

		return result1;
	}

	/**
	 * 查询所有的签约类活动，包括失效的
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllQySaleActive(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		IDataset result1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_QYSALEACTIVE", params, pagination);
		IDataset result2 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_QYSALEACTIVE_HIS", params, pagination);
		if (IDataUtil.isEmpty(result1))
		{
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
		}
		return result1;
	}

	/**
	 * 查询所有的活动，包括失效的
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllSaleActive(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_BY_UID", params, pagination);
	}

	public static IDataset queryAllSaleActiveByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_BY_USERID", param);
	}

	/**
	 * 产品变更取消营销活动
	 * 
	 * @param userId
	 * @param oldBrand
	 * @param newBrand
	 * @param oldProductId
	 * @param newProductId
	 * @param cancelDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCancelSaleActives(String userId, String oldBrand, String newBrand, String oldProductId, String newProductId, String cancelDate) throws Exception
	{
		IData param = new DataMap();
		param.clear();

		param.put("USER_ID", userId);
		param.put("CANCEL_DATE", cancelDate);
		param.put("OLD_PRODUCT_ID", oldProductId);
		param.put("NEW_PRODUCT_ID", newProductId);
		param.put("OLD_BRAND_CODE", oldBrand);
		param.put("NEW_BRAND_CODE", newBrand);

		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_CANCEL_SALEACTIVE", param);
	}

	/**
	 * 产品变更取消非签约类营销活动
	 * 
	 * @param userId
	 * @param oldBrand
	 * @param newBrand
	 * @param oldProductId
	 * @param newProductId
	 * @param cancelDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCancelSaleActivesNew(String userId, String oldBrand, String newBrand, String oldProductId, String newProductId, String cancelDate) throws Exception
	{
		IData param = new DataMap();
		param.clear();

		param.put("USER_ID", userId);
		param.put("CANCEL_DATE", cancelDate);
		param.put("OLD_PRODUCT_ID", oldProductId);
		param.put("NEW_PRODUCT_ID", newProductId);
		param.put("OLD_BRAND_CODE", oldBrand);
		param.put("NEW_BRAND_CODE", newBrand);

		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_CANCEL_SALENEW", param);
	}

	public static IDataset queryHdfkSaleActive(String serialNumber, String startDate, String endDate, String tradeStaffId, String tradeId, String dealStateCode, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", serialNumber);
		params.put("START_DATE", startDate);
		params.put("END_DATE", endDate);
		params.put("TRADE_STAFF_ID", tradeStaffId);
		params.put("TRADE_ID", tradeId);
		params.put("DEAL_STATE_CODE", dealStateCode);
		return Dao.qryByCodeParser("TF_F_USER_SALEACTIVE_BOOK", "SEL_HDFK_ACTIVE_TRADE", params, pagination);
	}

	public static IDataset queryInfosByUserIdProcessCampn(String userId, String processTag, String campnType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", processTag);
		param.put("CAMPN_TYPE", campnType);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_CAMPN_ID", param);
	}

	/**
	 * @Function: queryIsNeedBook
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:34:29 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset queryIsNeedBook(String userId, String paramAttr, String subsysCode, String eparchyCode, String productId, String packageId, String paramCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("SUBSYS_CODE", subsysCode);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("PARAM_CODE", paramCode);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ISNEED_BOOK", param);
	}

	public static IDataset queryIsNeedBookLimit(String userId, String paramAttr, String subsysCode, String eparchyCode, String productId, String packageId, String paramCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("SUBSYS_CODE", subsysCode);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("PARAM_CODE", paramCode);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ISNEED_BOOK_LIMIT", param);
	}

	public static IDataset queryMaxEndData(String strUserId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", strUserId);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_MAX_ENDDATE_BY_USERID", param);
	}

	public static IDataset queryNameByUserIdAndPackageId(String userId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT '02' PRODUCT_MODE,PACKAGE_NAME NAME,'' DISCNT_END_DATE FROM TF_F_USER_SALE_ACTIVE ");
		parser.addSQL("WHERE PACKAGE_ID = :PACKAGE_ID AND USER_ID = :USER_ID AND PARTITION_ID = MOD(:USER_ID, '10000') ");
		parser.addSQL("AND END_DATE > SYSDATE ");

		return Dao.qryByParse(parser);
	}

	/**
	 * 查询购机活动
	 * 
	 * @author tengg
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPurchaseInfo(IData param) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT a.* FROM tf_f_user_sale_active a");
		parser.addSQL(" where 1 = 1");
		parser.addSQL(" and a.user_id = :USER_ID");
		parser.addSQL(" and a.PARTITION_ID = mod(:USER_ID,10000)");
		parser.addSQL(" AND process_tag='0'");
		parser.addSQL("  AND a.ADVANCE_PAY = '0'");
		parser.addSQL(" AND sysdate < a.end_date");

		return Dao.qryByParse(parser);
	}

	/**
	 * 查询购机活动
	 * 
	 * @param userId
	 * @param eparchyCode
	 * @param processTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPurchaseInfo(String userId, String eparchyCode, String processTag) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("EPARCHY_CODE", eparchyCode);
		params.put("PROCESS_TAG", processTag);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_NOT_MODFIY_ACCT", params);
	}

	/**
	 * @Function: queryRecommendCount
	 * @Description: 查询同一营销活动对同一客户的推荐次数 used
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:35:33 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static int queryRecommendCount(String serial_number, String job_id) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("JOB_ID", job_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT JOB_ID COUT FROM TF_SM_USER_SALEINFO");
		parser.addSQL(" WHERE JOB_ID =:JOB_ID ");
		parser.addSQL(" AND SERIAL_NUMBER =:SERIAL_NUMBER");
		parser.addSQL(" AND RECV_TIME IS NOT NULL");
		return Dao.getCount(parser.getSQL(), param);
	}

	/**
	 * @Function: querySaleActDetail
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:37:40 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset querySaleActDetail(String user_id, String product_id, String package_id, String campn_id, String relation_trade_id, String start_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);
		param.put("PACKAGE_ID", package_id);
		param.put("CAMPN_ID", campn_id);
		param.put("PRODUCT_ID", product_id);
		param.put("START_DATE", start_date);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SALEACTIVE_PK", param);
	}

	/**
	 * 营销活动详细信息查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySaleActDetail(String user_id, String product_id, String package_id, String campn_id, String relation_trade_id, String start_date, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);
		param.put("PACKAGE_ID", package_id);
		param.put("CAMPN_ID", campn_id);
		param.put("PRODUCT_ID", product_id);
		param.put("START_DATE", start_date);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SALEACTIVE_PK", param, pg);
	}

	/**
	 * @Function: querySaleActive
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:38:58 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IData querySaleActive(String cust_id, String relation_trade_id) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);

		SQLParser parser = new SQLParser(param);
		IData datainfo = new DataMap();
		parser.addSQL("SELECT t.* FROM tf_f_user_sale_active t  ");
		parser.addSQL("WHERE t.user_id=:CUST_ID ");
		parser.addSQL("AND t.relation_trade_id=:RELATION_TRADE_ID ");
		parser.addSQL("AND t.process_tag='0' ");
		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		if (dataset != null && dataset.size() > 0)
		{
			datainfo = dataset.getData(0);
		}
		return datainfo;
	}

	/**
	 * @Function: querySaleActiveByPPIDuserId()
	 * @Description: 根据用户ID,产品ID，包ID查询营销活动信息
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: yxd
	 * @date: 2014-8-1 下午8:33:18 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
	 */
	public static IDataset querySaleActiveByPPIDuserId(String userId, String productId, String pKgID) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", pKgID);
		param.put("PROCESS_TAG", "0");
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_PPID", param);
	}

	/**
	 * 查询IPHONE用户是否办理移动终端营销活动
	 * 
	 * @param pd
	 * @param params
	 *            查询所需参数
	 * @return IDataset
	 * @throws Exception
	 *             comment：业务受理前规则校验中调用
	 */
	public static IDataset querySaleActiveByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", "0");
		param.put("CAMPN_TYPE", "YC");

		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_CAMPN_TYPE", param);
	}
	 
	
	/**
	 * 查询集团营销活动
	 * 
	 * @param user_id
	 * @param campn_id
	 * @param relation_trade_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySaleActiveByUserIdCampnIdTradeId(String user_id, String campn_id, String relation_trade_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("CAMPN_ID", campn_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_CAMPNID_RTRADEID", param, Route.CONN_CRM_CG);
	}

	public static IDataset querySaleActiveByUserIdPrdId(String userId, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_BY_USERID_PRDID_RSRVTAG", param);
	}

	public static IDataset querySaleActiveByUserIdProcess(String userId, String processTag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", processTag);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_USER_SALE_ACTIVE", param);
	}

	/**
	 * 查询规定时间内用户是否办理移动终端营销活动 手机损坏保障看是否有有效购机
	 * 
	 * @param params
	 *            查询所需参数
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset querySaleActiveByUserIdSH(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", "ZZZZ");
		param.put("CAMPN_TYPE", "YC");
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_PRODUCT_CAMPN", param);
	}

	public static IDataset querySaleActiveInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_SALE_ACTIVE_BY_USERID", param);
	}

	public static IDataset querySaleActiveStartDate(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" select t.user_id, TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, TO_CHAR(t.accept_date, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, t.product_id, t.package_id ");
		parser.addSQL("   from tf_f_user_sale_active t where partition_id = mod(to_number(:USER_ID), 10000) ");
		parser.addSQL(" and user_id = :USER_ID and process_tag = '0' ");
		parser.addSQL(" and end_date > start_date ");
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询活动资费信息
	 * 
	 * @param relation_trade_id
	 * @param campn_id
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySaleDiscnts(String relation_trade_id, String campn_id, String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TRADE_ID", relation_trade_id);
		param.put("CAMPN_ID", campn_id);
		param.put("USER_ID", user_id);
		IDataset discnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_SALEACTIVE", param);

		for (int i = 0; i < discnts.size(); i++)
		{
			IData discnt = (IData) discnts.get(i);
			Date end_date = SysDateMgr.string2Date(discnt.getString("END_DATE"), SysDateMgr.PATTERN_STAND_YYYYMMDD);
			Date systime = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
			if (systime.after(end_date))
			{
				discnt.put("DISCNT_INVALID_CODE", "0");
			}
			discnt.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discnt.getString("DISCNT_CODE")));
		}

		return discnts;
	}

	/**
	 * 查询用户办理终端营销活动里的终端机型是否为OPHONE终端类型
	 * 
	 * @param pd
	 * @param params
	 *            查询所需参数
	 * @return IDataset
	 * @throws Exception
	 *             comment：业务受理前规则校验中调用
	 */
	public static IDataset querySaleGoodByUserId(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_USERID_DATE", params);
	}

	public static IDataset querySaleInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" Select b.*,a.product_name,a.package_name,a.start_date active_start_Date ");
		parser.addSQL(" From Tf_f_User_Sale_Active a, Td_s_Commpara b ");
		parser.addSQL(" Where a.Product_Id = to_number(b.Param_Code) ");
		parser.addSQL(" And a.Package_Id = decode(b.Para_Code1,'-1',a.Package_Id,to_number(b.Para_Code1)) ");
		parser.addSQL(" And a.Partition_Id = Mod(:USER_ID, 10000) ");
		parser.addSQL(" And a.User_Id = :USER_ID ");
		parser.addSQL(" And b.Subsys_Code = 'CSM' ");
		parser.addSQL(" And b.Param_Attr = 1152 ");
		parser.addSQL(" And a.process_tag='0' ");
		parser.addSQL(" And a.End_Date > Sysdate ");
		parser.addSQL(" And Sysdate Between b.Start_Date And b.End_Date ");
		parser.addSQL(" Order By a.Accept_Date Asc ");
		return Dao.qryByParse(parser);
	}
	
	/**
	 * @author liwei29
	 * @param 根据user_id查询营销活动
	 * @return
	 */
	public static IDataset querySaleInfoByUserIdAndCommpara(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" Select a.* ");
		parser.addSQL(" From Tf_f_User_Sale_Active a, Td_s_Commpara b ");
		parser.addSQL(" Where a.Product_Id = to_number(b.para_code1) ");
		parser.addSQL(" And a.Package_Id = to_number(b.para_code2) ");
		parser.addSQL(" And a.Partition_Id = Mod(:USER_ID, 10000) ");
		parser.addSQL(" And a.User_Id = :USER_ID ");
		parser.addSQL(" And b.Subsys_Code = 'CSM' ");
		parser.addSQL(" And b.Param_Code = 'KDTS_RATE' ");
		parser.addSQL(" And a.End_Date > Sysdate ");
		return Dao.qryByParse(parser);

	}
	/**
	 * @Function: QuerySaleInfos
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:40:22 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset QuerySaleInfos(String cust_id, String relation_trade_id, String start_date, String end_date, String trade_staff_id) throws Exception
	{
		IData param = new DataMap();
		param.put("operType", cust_id);
		param.put("GROUP_ID", relation_trade_id);
		param.put("START_DATE", start_date);
		param.put("END_DATE", end_date);
		param.put("TRADE_STAFF_ID", trade_staff_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT t.serial_number,t.USER_ID,to_number(t.advance_pay)/100 advance_pay,t.accept_date,t.relation_trade_id,t.process_tag, ");
		parser.addSQL("t.trade_staff_id, ");
		parser.addSQL("CASE WHEN to_number(t.RSRV_STR2)<100 THEN t.RSRV_STR2||'%' ELSE '封顶金额：'||to_char(to_number(t.RSRV_STR2)/100)  ");
		parser.addSQL("END RSRV_STR2,t.RSRV_STR1,t.RSRV_STR3,t.RSRV_STR4 FROM tf_f_user_sale_active t  ");
		parser.addSQL("WHERE  1=1 ");
		parser.addSQL("and  t.process_tag=:operType ");
		parser.addSQL("AND    t.serial_number=:GROUP_ID ");
		parser.addSQL("AND    t.accept_date >=to_date(:START_DATE,'YYYY-MM-DD')");
		parser.addSQL("AND  t.accept_date<=to_date(:END_DATE,'YYYY-MM-DD')");
		parser.addSQL("AND    t.trade_staff_id=:TRADE_STAFF_ID ");
		return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
	}

	/**
	 * 查询活动服务信息
	 * 
	 * @param relation_trade_id
	 * @param campn_id
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySaleServs(String relation_trade_id, String campn_id, String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TRADE_ID", relation_trade_id);
		param.put("CAMPN_ID", campn_id);
		param.put("USER_ID", user_id);
		IDataset servs = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_BY_SALEACTIVE", param);

		for (int i = 0; i < servs.size(); i++)
		{
			IData serv = (IData) servs.get(i);
			Date end_date = SysDateMgr.string2Date(serv.getString("END_DATE"), SysDateMgr.PATTERN_STAND_YYYYMMDD);
			Date systime = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
			if (systime.after(end_date))
			{
				serv.put("SERV_INVALID_CODE", "0");
			}
			serv.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(serv.getString("SERVICE_ID")));
		}

		return servs;
	}

	public static IDataset queryTerminalBooks(String serialNumber, String processTag, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("PROCESS_TAG", processTag);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BOOKSALEACTIVE_ALL_BYPARAMS", param);
	}

	public static IDataset queryUngotGiftList(String productId, String packageId, String tradeStaffIdS, String tradeStaffIdE, String tradeCityCode, String tradeDepartId, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("TRADE_STAFF_ID_S", tradeStaffIdS);
		param.put("TRADE_STAFF_ID_E", tradeStaffIdE);
		param.put("TRADE_CITY_CODE", tradeCityCode);
		param.put("TRADE_DEPART_ID", tradeDepartId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_NOTGETGIFT", param);
	}

	public static IDataset queryUnGotSaleActiveGifts(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_USER_UNGOT_PAY_GIFT1", param);
	}

	public static IDataset queryUserAdvancedPayGifts(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_USER_ADVANCED_PAY_GIFT", param);
	}

	/**
	 * 查询用户是否通过营销活动订购了平台服务
	 * 
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserPlatSvcOrderBySaleActive(String userId, String serviceId, String idType, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("ID_TYPE", idType);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_QRYRULE", param);
	}

	/**
	 * @Function: queryUserSaleActiveById
	 * @Description: 查询营销活动
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-5-3 下午8:40:34 Modification History: Date Author Version Description
	 *        ---------------------------------------------------------* 2013-5-3 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserSaleActiveById(String userId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGE_ID", param);
	}

	/**
	 * 根据user_id获取有效的营销活动
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserSaleActiveByTag(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID_TAG", inparam);
	}

	/**
	 * @Function: queryUserSaleActiveByUserId
	 * @Description: 根据userId查询营销活动
	 * @param: @param userId
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 4:05:05 PM Jul 25, 2013 Modification History: Date Author Version Description
	 */
	public static IDataset queryUserSaleActiveByUserId(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_BY_USERID", inparam);
	}

	/**
	 * 查询有效的非签约类活动
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryValidNoQySaleActive(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		IDataset result1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_VALID_NOQYSALEACTIVE", params, pagination);
		IDataset result2 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_VALID_NOQYSALEACTIVE_HIS", params, pagination);
		if (IDataUtil.isEmpty(result1))
		{
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
		}

		return result1;
	}

	/**
	 * 查询有效的签约类活动
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryValidQySaleActive(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		IDataset result1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_VALID_QYSALEACTIVE", params, pagination);
		IDataset result2 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_VALID_QYSALEACTIVE_HIS", params, pagination);
		if (IDataUtil.isEmpty(result1))
		{
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
		}
		return result1;
	}

	public static IDataset queryVipUnGotSaleActiveGifts(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_USER_UNGOT_PAY_GIFT", param);
	}

	public static IDataset queryVpmnActiveRelationsByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		return Dao.qryByCode("TF_F_VPMNACTIVE_RELATION", "SEL_USER_IDB_ALL_FORBOTH", param);
	}

	public static void updateBook2ValidSaleActiveByAcceptTradeId(String accept_trade_id, String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", accept_trade_id);
		param.put("SERIAL_NUMBER", serial_number);
		Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_BOOKACTIVE_ACCEPT_TRADE", param);
	}
	/**
	 * 撤销营销活动预受理
	 * @param accept_trade_id
	 * @param relaTradeId
	 * @param serial_number
	 * @throws Exception
	 * @author yuyj3
	 */
	public static void updateSaleActiveBookToCancel(String accept_trade_id, String relaTradeId, String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", accept_trade_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("RELATION_TRADE_ID", relaTradeId);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_BOOKACTIVE_BY_RELATION_TRADE_ID", param);
    }

	public static void updateBook2ValidSaleActiveByTradeId(String trade_id, String serial_number, String product_id, String package_id, String trade_staff_id, String trade_depart_id, String intf_trade_id) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SERIAL_NUMBER", serial_number);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("TRADE_STAFF_ID", trade_staff_id);
		param.put("TRADE_DEPART_ID", trade_depart_id);
		param.put("INTF_TRADE_ID", intf_trade_id);
		Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_BOOKACTIVE_TO_DEAL", param);
	}
	
	public static void updateBook2ValidSaleActiveByTradeIdNew(String trade_id, String serial_number, String product_id, String package_id, String trade_staff_id, String trade_depart_id, String intf_trade_id, String deal_state_code) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SERIAL_NUMBER", serial_number);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("TRADE_STAFF_ID", trade_staff_id);
		param.put("TRADE_DEPART_ID", trade_depart_id);
		param.put("INTF_TRADE_ID", intf_trade_id);
		param.put("DEAL_STATE_CODE", deal_state_code);
		Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_BOOKACTIVE_TO_DEALNEW", param);
	}

	/**
	 * REQ202003180001 “共同战疫宽带助力”活动开发需求
	 * 不在活动时间内,预受理不转正式包RSRV_STR24更新原因
	 * @param trade_id
	 * @param serial_number
	 * @param product_id
	 * @param package_id
	 * @param trade_staff_id
	 * @param trade_depart_id
	 * @param remark
	 * @param deal_state_code
	 * @throws Exception
	 */
    public static void updateBookNotValidSaleActiveByTradeId(String trade_id, String serial_number, String product_id, String package_id, String trade_staff_id, String trade_depart_id, String remark, String deal_state_code) throws Exception{
        IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SERIAL_NUMBER", serial_number);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("TRADE_STAFF_ID", trade_staff_id);
		param.put("TRADE_DEPART_ID", trade_depart_id);
		param.put("RSRV_STR24", remark);
		param.put("DEAL_STATE_CODE", deal_state_code);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_BOOKACTIVE_TO_DEAL_NOT_VALID", param);
    }
	
	/**
	 * 查询宽带新装已办理的宽带1+营销活动
	 * @author chenzm
	 * @param userId
	 * @param product_id
	 * @param package_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryValidSaleActiveByUserIdAndProductId(String userId, String product_id, String package_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_VALIDSALEACTIVE_PRODUCTID", param);
	}
	
	/**
	 * 根据trade_id  查找到关联的营销活动记录
	 */
	public static IDataset queryRelationSaleActiveList(String user_id, String relation_trade_id) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_RELATION_TRADE_ID1", param);
	}
	
	/**
	 * 根据trade_id  查找到关联的所有营销活动记录，取PROCESS_TAG in ('0','1')的数据
	 */
	public static IDataset queryRelationAllSaleActive(String user_id, String relation_trade_id) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_RELATION_TRADE_ID", param);
	}

	/**
	 * @author yanwu
	 * @param user_id
	 * @param relation_trade_id
	 * @return 根据user_id和relation_trade_id查询用户所有营销活动记录，包括失效、生效、未生效的数据
	 * @throws Exception
	 */
	public static IDataset queryUserRelationAllSaleActive(String user_id, String relation_trade_id) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TRADE_ID", relation_trade_id);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_RELATIONTRADEID", param);
	}
	
	/**
	 * 查有效的生日送礼品卷营销活动
	 */
	public static IDataset queryVipBirthSaleActive(IData data) throws Exception {
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BS_BY_USERID_PID", data);
	}
	
	/**
	 * 为过户免填填，查询用户所有的有效的营销活动
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserValidActiveForChangeCust(String userId)throws Exception{
		IData param = new DataMap();
		IDataset dataset =new DatasetList();
		param.put("USER_ID", userId);
//		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_SALEACTIVE_VAILD_FOR_CHANGE_CUST", param);
		dataset=Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_VALIDSALEACTIVE", param);//第三代规范改造 add by hefeng
		if(IDataUtil.isNotEmpty(dataset)){
			for(int i=0; i<dataset.size(); i++)
        	{
        		IData temp = dataset.getData(i);
        		IData packageinfo=UPackageInfoQry.getPackageByPK(temp.getString("PACKAGE_ID"));
        		if(IDataUtil.isNotEmpty(packageinfo)){
        		    temp.put("PACKAGE_DESC", packageinfo.getString("DESCRIPTION",""));
        		}
        	}
		}
		return dataset;
	}
	
	public static IDataset queryUserSaleActiveProdId(String userId, String productId, String processTag)throws Exception{
		IData param=new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("PROCESS_TAG", processTag);
		
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "QRY_SALE_ACTIVE_PRODUCT_ID", param);
	}
	
	/**
	 * 查询用户订购的预处理营销活动
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserBookSaleActive(String userId, String productId, String packageId)
		throws Exception{
		IData param=new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		
		return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "QRY_SALEACTIVE_BOOK_BY_USER_ID_AND_PROD", param);
		
	}
	
	public static IDataset queryUserSaleActiveBookProdId(String userId, String productId)throws Exception{
		IData param=new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		
		return Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "QRY_SALE_ACTIVE_BOOK_PRODUCT_ID", param);
	}
	
	/**
	 * 查询所有的签约类活动，包括失效的
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryAllQySaleActiveWithGoods(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		IDataset result1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_QYSALEACTIVE_WITH_GOODS", params, pagination);
		IDataset result2 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ALL_QYSALEACTIVE_HIS_WITH_GOODS", params, pagination);
		if (IDataUtil.isEmpty(result1))
		{
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
		}
		return result1;
	}
	
	/**
	 * 查询有效的签约类活动
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryValidQySaleActiveWithGoods(String userId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		IDataset result1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_VALID_QYSALEACTIVE_WITH_GOODS", params, pagination);
		IDataset result2 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_VALID_QYSALEACTIVE_HIS_WITH_GOODS", params, pagination);
		if (IDataUtil.isEmpty(result1))
		{
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
		}
		return result1;
	}
	
	
	/**
	 * 营销活动畅享流量
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset qrySaleActiveBySnFLUX(String serialNumber, String campnType, String productid, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CAMPN_TYPE", campnType);
		param.put("PRODUCT_ID", productid);
		return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_BY_SN_FLUX", param, pg, Route.CONN_CRM_CG);
	}
	
	/**
	 * REQ201603280028 关于新增集团机惠专享积分购机活动的需求（优化规则及新增参数）
	 * 根据USER_ID 、PRODUCT_ID查询用户营销活动
	 * chenxy3 2016-04-06
	 * 由于product_id可能是多个值，不能用CODE_CODE做处理。
	 */
	public static IDataset querySaleActiveByUserIdProdId(String userId, String product_id) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", userId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select to_char(t.start_date,'yyyy-mm-dd hh24:mi:ss') START_DATE_A,t.* from Tf_f_User_Sale_Active t  ");
        parser.addSQL(" where t.user_id = to_number(:USER_ID)  ");
        parser.addSQL(" and t.product_id in ( "+product_id+" )  ");
        parser.addSQL(" and sysdate < t.end_date  ");
        parser.addSQL(" order by t.start_date desc ");
        return Dao.qryByParse(parser);
	}
	
	public static IDataset queryUserSaleActiveProdIdExcludeMonthEnd(String userId, String productId, String processTag)throws Exception{
		IData param=new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("PROCESS_TAG", processTag);
		
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "QRY_SALE_ACTIVE_PRODUCT_ID_EXCLUDE_MONTH", param);
	}
	/**
	 * 撤销营销活动预受理
	 * @param accept_trade_id
	 * @param relaTradeId
	 * @param serial_number
	 * @throws Exception
	 * @author yuyj3
	 */
	public static void updateBook2ValidSaleActiveByRelaTradeId(String accept_trade_id, String relaTradeId, String serial_number) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", accept_trade_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("RELATION_TRADE_ID", relaTradeId);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_BOOKACTIVE_BY_RELATION_TRADE_ID", param);
    }
	
	/**
     * 更新 新资费开始时间，这次增加的目的是用户宽带产品变更后更新营销活动的受理预约日期
     * 
     * @author kangyt
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updStartDateByUseridTradeid(String user_id,String trade_id, String start_date) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RELATION_TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_STARTDATE_BYTRADEID_USERID", param);
    }
	
	/**
     * 更新旧资费结束时间，这次增加的目的是用户宽带产品变更后更新营销活动的终止日期，更加预约日期计算
     * 
     * @author kangyt
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updEndDateByUseridTradeid(String user_id,String trade_id,String end_date) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        param.put("RELATION_TRADE_ID", trade_id);
        param.put("END_DATE", end_date);
        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_ENDDATE_BYTRADEID_USERID", param);
    }
    
    /**
     * 判断号码是否存在魔百和类营销活动
     */
    public static IDataset checkUserMoSaleactive(String serialNumber)throws Exception{
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "CHECK_USER_MO_SALEACTIVE", param);
	}
    
    /**
	 * 根据trade_id  查找到关联的营销活动记录
	 */
	public static IDataset queryRelationSaleDepositList(String userId, String relationTradeId) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TRADE_ID", relationTradeId);
		return Dao.qryByCode("TF_F_USER_SALE_DEPOSIT", "SEL_BY_USERID_TRADEID", param);
	}
	
	public static IDataset queryUserSaleActiveByInstId(String instId) throws Exception
	{
	    IData param = new DataMap();
        param.put("INST_ID", instId);
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_SALEACTIVE_BY_INSTID", param);
	}
	
	/**
     * 根据营销活动在网时间捞取
     * <p>Title: queryOnNetSaleActiveByPPIDuserId</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param userId
     * @param productId
     * @param pKgID
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2017-2-23 下午05:04:10
     */
    public static IDataset queryOnNetSaleActiveByPPIDuserId(String userId, String productId, String pKgID, String relationtradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", pKgID);
        param.put("PROCESS_TAG", "0");
        param.put("RELATION_TRADE_ID", relationtradeId);
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_PPIDONNET", param);
    }
    
    public static IDataset qrySaleActiveTotalNum() throws Exception
    {
    	IData param = new DataMap();
    	
    	return Dao.qryByCode("TL_B_SALE_ACTIVE_NUM", "SEL_TOTAL_BY_PRODUCTID", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset getUserSaleactiveByDay(String user_id, String limit_day, String catalog_id, String package_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("LIMIT_DAY", limit_day);
		param.put("PRODUCT_ID", catalog_id);
		param.put("PACKAGE_ID", package_id);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_ACTIVEDAY", param);
	}
    
    public static IDataset querySaleActiveByUserId(IData params) throws Exception
    {
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_COMMON", params);
	}
    /**
     * 判断号码是否存在魔百和类营销活动
     */
    public static IDataset isHasMobileTV(String serialNumber)throws Exception{
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "CHECK_USER_MOBILETV_SALEACTIVE", param);
	}
    
    
    /**
     * 根据用户ID获取魔百和营销活动
     */
    public static IDataset getTopSetBoxSaleActiveByUserId(String userId)throws Exception{
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TF_F_USER_SALE_ACTIVE A ");
		parser.addSQL("WHERE A.PRODUCT_ID IN (SELECT B.PARA_CODE1 FROM TD_S_COMMPARA B WHERE B.PARAM_ATTR = '178' AND B.PARAM_CODE = 'noDeposit')");
		parser.addSQL("AND A.PROCESS_TAG = '0' AND A.END_DATE > SYSDATE AND A.USER_ID = :USER_ID");

		return Dao.qryByParse(parser);
	}
    
    /**
     * 根据用户ID获取用户IMS固话营销活动
     */
    public static IDataset getImsSaleActiveByUserId(String userId)throws Exception{
    	
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TF_F_USER_SALE_ACTIVE A ");
		parser.addSQL("WHERE A.PRODUCT_ID IN (SELECT B.PARA_CODE4 FROM TD_S_COMMPARA B WHERE B.PARAM_ATTR = '178' AND B.PARAM_CODE = '6800')");
		parser.addSQL("AND A.PROCESS_TAG = '0' AND A.END_DATE > SYSDATE AND A.USER_ID = :USER_ID");

		return Dao.qryByParse(parser);
	}
    
    /**
     * 根据用户ID获取用户和目营销活动
     */
    public static IDataset getHeMuSaleActiveByUserId(String userId)throws Exception{
    	
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TF_F_USER_SALE_ACTIVE A ");
		parser.addSQL("WHERE A.PRODUCT_ID IN (SELECT B.PARA_CODE4 FROM TD_S_COMMPARA B WHERE B.PARAM_ATTR = '178' AND B.PARAM_CODE = 'HEMU')");
		parser.addSQL("AND A.PROCESS_TAG = '0' AND A.END_DATE > SYSDATE AND A.USER_ID = :USER_ID");

		return Dao.qryByParse(parser);
	}
    
    /**
     * 根据用户ID获取用户和目营销活动
     */
    public static IDataset getHeMuSaleActiveByUserId2(String userId)throws Exception{
    	
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TF_F_USER_SALE_ACTIVE A ");
		parser.addSQL("WHERE A.PRODUCT_ID IN (SELECT B.PARA_CODE4 FROM TD_S_COMMPARA B WHERE B.PARAM_ATTR = '178' AND B.PARAM_CODE = 'HEMU')");
		parser.addSQL("AND A.USER_ID = :USER_ID");

		return Dao.qryByParse(parser);
	}
    
    
    /**
     * 根据用户ID获取用户宽带类营销活动
     */
    public static IDataset getWideNetSaleActiveByUserId(String userId)throws Exception{
    	
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.* FROM TF_F_USER_SALE_ACTIVE A ");
		parser.addSQL("WHERE A.PRODUCT_ID IN (SELECT B.PARA_CODE1 FROM TD_S_COMMPARA B WHERE B.PARAM_ATTR = '173' AND B.PARAM_CODE = '601')");
		parser.addSQL("AND A.PROCESS_TAG = '0' AND A.END_DATE > SYSDATE AND A.USER_ID = :USER_ID");

		return Dao.qryByParse(parser);
	}
    
    /**
     * 获取宽带类营销活动
     */
    public static IDataset getAllWideNetSaleActive(IData param)throws Exception{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT B.* FROM TD_S_COMMPARA B WHERE B.PARAM_ATTR = '173' AND B.PARAM_CODE = '601' ");
		return Dao.qryByParse(parser);
	}
    
    /**
     * 根据用户ID获取用户候鸟营销活动
     */
    public static IDataset getHouNiaoSaleActiveByUserId(String userId)throws Exception{
    	
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_FH_USER_SALE_ACTIVE A ");
		parser.addSQL(" WHERE A.PRODUCT_ID ='66002202' ");
		parser.addSQL(" and A.USER_ID = :USER_ID ");	
		parser.addSQL(" and (A.RSRV_TAG3 is null or A.RSRV_TAG3 <> '1')  ");
		parser.addSQL(" union ");
		parser.addSQL(" SELECT A.* FROM TF_F_USER_SALE_ACTIVE A");
		parser.addSQL("  WHERE A.PRODUCT_ID = '66002202' ");
		parser.addSQL(" and A.USER_ID = :USER_ID ");
		parser.addSQL(" and (A.RSRV_TAG3 is null or A.RSRV_TAG3 <> '1') ");

		return Dao.qryByParse(parser);
	}
    
    /**
     * add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
     * 判断号码是否存在候鸟营销活动
     */
    public static IDataset checkUserHouniaoActive(String serialNumber)throws Exception{
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_SALE_ACTIVE A");
		parser.addSQL("  WHERE A.PRODUCT_ID in ('66002202','66004809') ");
		parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");
		return Dao.qryByParse(parser);
	}
    
    /**
     * @Function: getUserSaleActiveByUserIdPackageId
     * @Description: 查询用户当前生效活动
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-7-17 上午11:12:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-17 lijm3 v1.0.0 修改原因
     */
    public static IDataset getUserSaleActiveByUserIdPackageId(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", param);
    }
    
    /**
     * add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
     */
    public static IDataset checkUserDJKDActive(String userId)throws Exception{
		IData param=new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", "0");
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT * From tf_f_user_sale_active t   ");
		parser.addSQL("  Where user_id = :USER_ID And partition_id = Mod(:USER_ID, 10000) ");
		parser.addSQL(" And PROCESS_TAG = :PROCESS_TAG and t.product_id in ('66000602','69908001','67220428','66004809') ");
		parser.addSQL(" order by t.end_date desc ");
		return Dao.qryByParse(parser);
    }
       /**
     *  “庆国庆&庆移动20周年感恩献礼”活动 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getUserSaleActiveByProductIdAndPackageId(IData param)throws Exception{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_SALE_ACTIVE A");
		parser.addSQL(" WHERE A.PRODUCT_ID = :PRODUCT_ID ");
		parser.addSQL(" AND A.PACKAGE_ID = :PACKAGE_ID ");
		parser.addSQL(" AND A.USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		return Dao.qryByParse(parser);
	}
    /**
     *  “庆国庆&庆移动20周年感恩献礼”活动 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getUserSaleActiveByProductId(IData param)throws Exception{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_F_USER_SALE_ACTIVE A");
		parser.addSQL(" WHERE A.PRODUCT_ID = :PRODUCT_ID ");
		parser.addSQL(" AND A.USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		return Dao.qryByParse(parser);
	} 
    /**
     * BUS201907310012关于开发家庭终端调测费的需求
     * 根据用户ID获取宽带调测费活动
     */
    public static IDataset getWidenetCommissioningFeeByUserId(String userId,String productId )throws Exception{
    	
    	IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.* FROM TF_FH_USER_SALE_ACTIVE A ");
		parser.addSQL(" WHERE A.PRODUCT_ID = :PRODUCT_ID ");
		parser.addSQL(" and A.USER_ID = :USER_ID ");	
		parser.addSQL(" and (A.RSRV_TAG3 is null or A.RSRV_TAG3 <> '1')  ");
		parser.addSQL(" union ");
		parser.addSQL(" SELECT A.* FROM TF_F_USER_SALE_ACTIVE A");
		parser.addSQL("  WHERE A.PRODUCT_ID = :PRODUCT_ID ");
		parser.addSQL(" and A.USER_ID = :USER_ID ");
		parser.addSQL(" and (A.RSRV_TAG3 is null or A.RSRV_TAG3 <> '1') ");

		return Dao.qryByParse(parser);
	}
    
}