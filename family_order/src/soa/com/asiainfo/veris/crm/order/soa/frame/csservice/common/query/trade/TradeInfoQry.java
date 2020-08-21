package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class TradeInfoQry
{

	/**
	 * @description 批量开户，判断是否有未完工的订单
	 * @param inparamso
	 * @author sunxin
	 * @return
	 * @throws Exception
	 */
	public static IDataset CheckIsExistNotFinishedTrade(String serialNumber) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_COUNT_BY_SN_FOR_BATCREATEUSER", inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
    public static IDataset queryUnfinishTradeByTradeTypeCodeAndSerialNumber(String serialNumber,String tradeTypeCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", serialNumber);
        inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT TRADE_ID,ORDER_ID,TRADE_TYPE_CODE,SERIAL_NUMBER FROM TF_B_TRADE ");
        sql.append(" WHERE TRADE_TYPE_CODE = :TRADE_TYPE_CODE AND SERIAL_NUMBER = :SERIAL_NUMBER");
        return Dao.qryBySql(sql, inparams, Route.getJourDb(BizRoute.getRouteId()));
    }

	/**
	 * bboss外网号码判断未完工订单
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset CheckIsExistNotBBSSFinishedTrade(String serialNumber) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCodeAllJour("TF_B_TRADE", "SEL_COUNT_BY_SN", inparams, false);
	}

	/**
	 * @description 批量固话新装，判断是否有未完工的订单
	 * @param inparams
	 * @author yuezy
	 * @return
	 * @throws Exception
	 */
	public static IDataset CheckIsExistNotGHFinishedTrade(String serialNumber) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_COUNT_BY_SN", inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * @param order_id
	 * @param page
	 * @author weixb3
	 * @return
	 * @throws Exception
	 */
	public static int delTradeRsrvTag10(String order_id, String rsrv_str10) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", order_id);
		param.put("RSRV_STR10", rsrv_str10);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("UPDATE TF_B_TRADE T SET T.RSRV_STR10=:RSRV_STR10 WHERE T.ORDER_ID= :ORDER_ID");
		return Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * @Function: getAgentBusiSumByDepartId
	 * @Description: 代理商业务量
	 * @param departId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2014年7月25日 上午11:16:29
	 */
	public static IDataset getAgentBusiSumByDepartId(String departId, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("DEPART_ID", departId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		// return Dao.qryByCode("TF_B_TRADE", "SEL_AGENT_BUSI_SUM", param);
		// 拆分sql by duhj

		IDataset res = Dao.qryByCode("TF_B_TRADE", "SEL_AGENT_BUSI_SUM_NEW", param, Route.getJourDb(BizRoute.getRouteId()));
		if (IDataUtil.isNotEmpty(res))
		{
			for (int i = 0; i < res.size(); i++)
			{
				IData data = res.getData(i);
				String in_mode_code = data.getString("IN_MODE_CODE");
				String inMode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", in_mode_code);
				data.put("IN_MODE", inMode);

			}
		}
		return res;

	}

	/**
	 * @Function: getAgentReleaseSnSumByDepartId
	 * @Description: 代理商放号量
	 * @param departId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2014年7月25日 上午11:16:59
	 */
	public static IDataset getAgentReleaseSnSumByDepartId(String departId, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("DEPART_ID", departId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		// return Dao.qryByCode("TF_B_TRADE", "SEL_AGENT_SN", param);//原sql

		// 拆分sql by duhj
		IDataset res = Dao.qryByCode("TF_B_TRADE", "SEL_AGENT_SN_NEW", param, Route.getJourDb(BizRoute.getRouteId()));
		if (IDataUtil.isNotEmpty(res))
		{
			for (int i = 0; i < res.size(); i++)
			{
				IData data = res.getData(i);
				String in_mode_code = data.getString("IN_MODE_CODE");
				String brandCode = data.getString("BRAND_CODE");
				String inMode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", in_mode_code);
				String brand = UBrandInfoQry.getBrandNameByBrandCode(brandCode);

				data.put("IN_MODE", inMode);
				data.put("BRAND", brand);

			}
		}
		return res;
	}

	// todo
	/**
	 * 从资料和台账表查询全部分省支付相关属性的值
	 * 
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllProvPayAttr(String USER_ID, String PRODUCT_ID, String EPARCHY_CODE, String TRADE_ID) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", USER_ID);
		inparam.put("PRODUCT_ID", PRODUCT_ID);
		inparam.put("EPARCHY_CODE", EPARCHY_CODE);
		inparam.put("TRADE_ID", TRADE_ID);

		SQLParser sp = new SQLParser(inparam);

		sp.addSQL("SELECT UA.ATTR_CODE AS USER_ATTR_CODE, UA.ATTR_VALUE AS USER_ATTR_VALUE, '' AS TRADE_ATTR_CODE, '' AS TRADE_ATTR_VALUE ");
		sp.addSQL("FROM TF_F_USER_ATTR UA, TD_S_COMMPARA C ");
		sp.addSQL("WHERE (UA.USER_ID = :USER_ID AND :USER_ID IS NOT NULL) ");
		sp.addSQL("AND UA.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		sp.addSQL("AND UA.ATTR_CODE = C.PARA_CODE1 ");
		sp.addSQL("AND SYSDATE BETWEEN UA.START_DATE AND UA.END_DATE ");
		sp.addSQL("AND C.SUBSYS_CODE = 'CSM' ");
		sp.addSQL("AND C.PARAM_ATTR = '3526' ");
		sp.addSQL("AND C.PARAM_CODE = '2' ");
		sp.addSQL("AND C.PARA_CODE4 = :PRODUCT_ID ");
		sp.addSQL("AND C.PARA_CODE21 IS NOT NULL ");
		sp.addSQL("AND (C.EPARCHY_CODE = :EPARCHY_CODE OR :EPARCHY_CODE = 'ZZZZ') ");
		sp.addSQL("AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE ");
		sp.addSQL("UNION ");
		sp.addSQL("SELECT '' AS ATTR_CODE, '' AS ATTR_VALUE, TA.ATTR_CODE AS TRADE_ATTR_CODE, TA.ATTR_VALUE AS TRADE_ATTR_VALUE ");
		sp.addSQL("FROM TF_B_TRADE_ATTR TA, TF_B_TRADE T, TD_S_COMMPARA C ");
		sp.addSQL("WHERE (TA.TRADE_ID = :TRADE_ID AND :TRADE_ID IS NOT NULL) ");
		sp.addSQL("AND TA.TRADE_ID = T.TRADE_ID ");
		sp.addSQL("AND TA.USER_ID = :USER_ID ");
		sp.addSQL("AND TA.ACCEPT_MONTH = SUBSTR(TO_CHAR(:TRADE_ID), 5, 2) ");
		sp.addSQL("AND TA.INST_TYPE = 'P' ");
		sp.addSQL("AND TA.MODIFY_TAG != '1' ");
		sp.addSQL("AND TA.ATTR_CODE = C.PARA_CODE1 ");
		sp.addSQL("AND C.SUBSYS_CODE = 'CSM' ");
		sp.addSQL("AND C.PARAM_ATTR = '3526' ");
		sp.addSQL("AND C.PARAM_CODE = '2' ");
		sp.addSQL("AND C.PARA_CODE4 = :PRODUCT_ID ");
		sp.addSQL("AND C.PARA_CODE21 IS NOT NULL ");
		sp.addSQL("AND (C.EPARCHY_CODE = :EPARCHY_CODE OR :EPARCHY_CODE = 'ZZZZ') ");
		sp.addSQL("AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE ");

		return Dao.qryByParse(sp, Route.CONN_CRM_CG);
	}

	public static IDataset getBHTradeSecond(String tradeId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("CANCEL_TAG", cancelTag);
		return Dao.qryByCode("TF_BH_TRADE_SECOND", "SEL_BY_TRADEID", param, Route.getJourDb(routeId));
	}

	// todo 无SQL
	/**
	 * 根据CUST_ID,PRODUCT_ID,TRADE_TYPE_CODE查询集团客户订单信息
	 */
	public static IDataset getByUserIdUserIdb(IData inparams) throws Exception
	{

		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_BY_USERID_USERIDB", inparams);
	}

	// todo
	/**
	 * 获取数据指令详细信息
	 */
	public static IDataset getDataCommand(String TRADE_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", TRADE_ID);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT T.* ");
		parser.addSQL(" FROM TL_B_IBPLAT_SYN T ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND T.SUBSCRIBE_ID =:TRADE_ID");
		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}

	/**
	 * @description 拆机工单查询
	 * @author chenzm
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDestoryTradeWidenetInfos(String userId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_DESTROYWIDE_TRADE_BY_USERID", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	// todo
	/**
	 * 根据tradeID查询trade详细信息
	 */
	public static IDataset getDetailTradeByTradeID(String TRADE_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", TRADE_ID);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT TRADE_ID,SERIAL_NUMBER,SUBSCRIBE_STATE,TRADE_TYPE_CODE,USER_ID,TRADE_STAFF_ID,TRADE_DEPART_ID,OLCOM_TAG,to_char(ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		parser.addSQL(" to_char(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,to_char(FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE ");
		parser.addSQL(" FROM TF_B_TRADE  ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND TRADE_ID =:TRADE_ID");
		IDataset iDataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		if (iDataset.size() <= 0)
		{
			SQLParser parser2 = new SQLParser(param);
			parser2.addSQL(" SELECT TRADE_ID,SERIAL_NUMBER,SUBSCRIBE_STATE,TRADE_TYPE_CODE,USER_ID,TRADE_STAFF_ID,TRADE_DEPART_ID,OLCOM_TAG,to_char(ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
			parser2.addSQL(" to_char(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,to_char(FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE ");
			parser2.addSQL(" FROM TF_BH_TRADE  ");
			parser2.addSQL(" WHERE 1=1 ");
			parser2.addSQL(" AND TRADE_ID =:TRADE_ID");
			iDataset = Dao.qryByParse(parser2, Route.CONN_CRM_CG);
		}
		return iDataset;
	}

	/**
	 * @description 宽带子账号开户 判断子账号是否有未完工的订单
	 * @param inparams
	 * @author huangsl
	 * @return
	 * @throws Exception
	 */
	public static IDataset getExistTrade(String relaTypeCode, String userIdA, String roleCodeB) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("RELATION_TYPE_CODE", relaTypeCode);
		inparams.put("USER_ID_A", userIdA);
		inparams.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_IDROLEA", inparams);
	}

	// todo

	/**
	 * @description 宽带子账号开户 判断子账号是否有未完工的订单
	 * @param inparams
	 * @author huangsl
	 * @return
	 * @throws Exception
	 */
	public static IDataset getExistUser(String relaTypeCode, String userIdB, String roleCodeB) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("RELATION_TYPE_CODE", relaTypeCode);
		inparams.put("USER_ID_B", userIdB);
		inparams.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_IDROLE", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	public static IDataset getFinishHangByPk(String tradeId, String userId, String execTime, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);
		param.put("EXEC_TIME", execTime);
		param.put("TRADE_ID", tradeId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT COUNT(1) COUNT ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE 1=1 ");
		sql.append("AND T.TRADE_ID <> :TRADE_ID ");
		sql.append("AND T.USER_ID = :USER_ID ");
		sql.append("AND EXEC_TIME < TO_DATE(:EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
		sql.append("AND T.SUBSCRIBE_STATE <> '9' ");
		sql.append("AND T.SUBSCRIBE_TYPE <> '600' ");
		sql.append("AND NOT EXISTS (SELECT 1 FROM TD_B_FINISH_NODEPAND_CFG C WHERE C.TRADE_TYPE_CODE_A = :TRADE_TYPE_CODE AND C.TRADE_TYPE_CODE_B = T.TRADE_TYPE_CODE AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE)");
		sql.append("AND ROWNUM < 2 ");

		return Dao.qryBySql(sql, param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	// todo
	/**
	 * 根据执行时间查询集团客户订单信息
	 */
	public static IDataset getGrpBookTrade(String START_DATE, String END_DATE, String SERIAL_NUMBER, String TRADE_ID, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("START_DATE", START_DATE);
		param.put("END_DATE", END_DATE);
		param.put("SERIAL_NUMBER", SERIAL_NUMBER);
		param.put("TRADE_ID", TRADE_ID);

		SQLParser parser = new SQLParser(param);

		parser.addSQL("Select  to_char(TRADE_ID) TRADE_ID, trade_type_code, eparchy_code, ");
		parser.addSQL("SERIAL_NUMBER, to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TRADE_STAFF_ID,TRADE_DEPART_ID,CUST_NAME,REMARK ");
		parser.addSQL("From TF_B_TRADE A ");
		parser.addSQL("Where TRADE_TYPE_CODE>=2000 ");
		parser.addSQL("And TRADE_TYPE_CODE<=4137 ");
		parser.addSQL("And EXEC_TIME>=TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL("And EXEC_TIME<=TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL("AND subscribe_state='0' ");
		parser.addSQL("AND SUBSTR(PROCESS_TAG_SET,40,1)='0' ");
		parser.addSQL("And EXEC_TIME>SYSDATE ");
		parser.addSQL("And SERIAL_NUMBER=:SERIAL_NUMBER ");
		parser.addSQL("And TRADE_ID=:TRADE_ID ");
		parser.addSQL("And ACCEPT_MONTH=SUBSTR(:TRADE_ID,5,2) ");
		parser.addSQL("And CANCEL_TAG='0' ");

		IDataset ids = Dao.qryByParse(parser, page);

		if (IDataUtil.isEmpty(ids))
		{
			return ids;
		}

		for (int i = 0, sz = ids.size(); i < sz; i++)
		{
			IData idata = ids.getData(i);
			String TRADE_TYPE_CODE = idata.getString("TRADE_TYPE_CODE");

			String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(TRADE_TYPE_CODE);

			idata.put("TRADE_TYPE_NAME", tradeTypeName);
		}

		return Dao.qryByParse(parser, page);
	}

	/**
	 * 根据tab_name,sql_ref,eparchy_code查询主台帐历史表
	 */
	public static IDataset getHisMainTrade(IData inparams, Pagination page) throws Exception
	{

		return Dao.qryByCode("TF_BH_TRADE", "SEL_FINISH_TRADE", inparams, page, Route.getJourDb());
	}

	public static IDataset getHisMainTradeByOrderId(String orderId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", cancelTag);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
		sql.append("TO_CHAR(A.BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(A.ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
		sql.append("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, ");
		sql.append("A.PRIORITY, A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, ");
		sql.append("A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
		sql.append("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, ");
		sql.append("TO_CHAR(A.USER_ID) USER_ID, ");
		sql.append("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, ");
		sql.append("A.NET_TYPE_CODE, A.EPARCHY_CODE, A.CITY_CODE, ");
		sql.append("A.PRODUCT_ID, A.BRAND_CODE, ");
		sql.append("TO_CHAR(A.CUST_ID_B) CUST_ID_B, ");
		sql.append("TO_CHAR(A.USER_ID_B) USER_ID_B, ");
		sql.append("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, ");
		sql.append("A.CUST_CONTACT_ID, A.SERV_REQ_ID, A.INTF_ID, ");
		sql.append("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
		sql.append("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, ");
		sql.append("A.TRADE_CITY_CODE, A.TRADE_EPARCHY_CODE, A.TERM_IP, ");
		sql.append("TO_CHAR(A.OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(A.FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, A.INVOICE_NO, ");
		sql.append("A.FEE_STATE, ");
		sql.append("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, ");
		sql.append("A.FEE_STAFF_ID, A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
		sql.append("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
		sql.append("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
		sql.append("A.EXEC_ACTION, A.EXEC_RESULT, A.EXEC_DESC, ");
		sql.append("A.CANCEL_TAG, ");
		sql.append("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
		sql.append("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, ");
		sql.append("A.CANCEL_CITY_CODE, A.CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
		sql.append("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, ");
		sql.append("A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, ");
		sql.append("A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, ");
		sql.append("A.RSRV_STR9, A.RSRV_STR10 ");
		sql.append("FROM TF_BH_TRADE A ");
		sql.append("WHERE A.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
		sql.append("AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");
		sql.append("AND A.CANCEL_TAG = :CANCEL_TAG ");

		return Dao.qryBySql(sql, param, Route.getJourDb(routeId));
	}

	public static IDataset getHisMainTradeByUserIdAndDate(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADETYPEOCDE_EXECTIME", inparams, Route.getJourDb());
	}

	// todo
	public static IDataset getIdcTradeInfo(String TRADE_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", TRADE_ID);
		param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(param.getString("TRADE_ID")));
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select b.trade_id,b.order_id ");
		parser.addSQL(" from tf_b_trade b ");
		parser.addSQL(" where b.trade_id =:TRADE_ID ");
		parser.addSQL(" and   b.accept_month =:ACCEPT_MONTH ");
		return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

	}

	/**
	 * 查询有状态变化的未完工单
	 * 
	 * @param user_id
	 * @param accept_month
	 * @return
	 * @throws Exception
	 */
	public static IDataset getLastSvcstateChgTrade(String user_id, String accept_month) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", user_id);
		inparams.put("ACCEPT_MONTH", accept_month);
		return Dao.qryByCode("TF_B_TRADE", "SEL_WIDENET_LAST_SVCSTATECHG_TRADE", inparams);
	}

	public static IDataset getMainTradeByAcctIdAB(IData data) throws Exception
	{
		return Dao.qryByCode("TF_B_TRADE", "SEL_BOOK_BY_ACCTIDAB", data);
	}

	// todo
	public static IDataset getMainTradeByCond(String user_id_a, String user_id_b, String trade_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);
		param.put("TRADE_TYPE_CODE", trade_type_code);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
		parser.addSQL("TO_CHAR(A.BATCH_ID) BATCH_ID, ");
		parser.addSQL("TO_CHAR(A.ORDER_ID) ORDER_ID, ");
		parser.addSQL("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
		parser.addSQL("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, ");
		parser.addSQL("A.PRIORITY, A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, ");
		parser.addSQL("A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
		parser.addSQL("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, ");
		parser.addSQL("TO_CHAR(A.USER_ID) USER_ID, ");
		parser.addSQL("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, ");
		parser.addSQL("A.NET_TYPE_CODE, A.EPARCHY_CODE, A.CITY_CODE, ");
		parser.addSQL("A.PRODUCT_ID, A.BRAND_CODE, ");
		parser.addSQL("TO_CHAR(A.CUST_ID_B) CUST_ID_B, ");
		parser.addSQL("TO_CHAR(A.USER_ID_B) USER_ID_B, ");
		parser.addSQL("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, ");
		parser.addSQL("A.CUST_CONTACT_ID, A.SERV_REQ_ID, A.INTF_ID, ");
		parser.addSQL("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
		parser.addSQL("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, ");
		parser.addSQL("A.TRADE_CITY_CODE, A.TRADE_EPARCHY_CODE, A.TERM_IP, ");
		parser.addSQL("TO_CHAR(A.OPER_FEE) OPER_FEE, ");
		parser.addSQL("TO_CHAR(A.FOREGIFT) FOREGIFT, ");
		parser.addSQL("TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, A.INVOICE_NO, ");
		parser.addSQL("A.FEE_STATE, ");
		parser.addSQL("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, ");
		parser.addSQL("A.FEE_STAFF_ID, A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
		parser.addSQL("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
		parser.addSQL("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
		parser.addSQL("A.EXEC_ACTION, A.EXEC_RESULT, A.EXEC_DESC, ");
		parser.addSQL("A.CANCEL_TAG, ");
		parser.addSQL("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
		parser.addSQL("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, ");
		parser.addSQL("A.CANCEL_CITY_CODE, A.CANCEL_EPARCHY_CODE, ");
		parser.addSQL("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
		parser.addSQL("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, ");
		parser.addSQL("A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, ");
		parser.addSQL("A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, ");
		parser.addSQL("A.RSRV_STR9, A.RSRV_STR10 ");
		parser.addSQL(" FROM TF_B_TRADE A ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND A.USER_ID =:USER_ID_A");
		parser.addSQL(" AND A.USER_ID_B =:USER_ID_B");
		parser.addSQL(" AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE");
		return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static IDataset getMainTradeByOrderId(String orderId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", cancelTag);
		param.put("ROUTE_ID", Route.getJourDb(routeId)); // 给批量发报文用
		param.put("DB_SRC", Route.getJourDb(routeId)); // 传服务开通需要路由信息

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.ACCEPT_MONTH, ");
		sql.append("TO_CHAR(A.BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(A.ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(A.PROD_ORDER_ID) PROD_ORDER_ID, A.BPM_ID, ");
		sql.append("TO_CHAR(A.CAMPN_ID) CAMPN_ID, A.TRADE_TYPE_CODE, ");
		sql.append("A.PRIORITY, A.SUBSCRIBE_TYPE, A.SUBSCRIBE_STATE, ");
		sql.append("A.NEXT_DEAL_TAG, A.IN_MODE_CODE, ");
		sql.append("TO_CHAR(A.CUST_ID) CUST_ID, A.CUST_NAME, ");
		sql.append("TO_CHAR(A.USER_ID) USER_ID, ");
		sql.append("TO_CHAR(A.ACCT_ID) ACCT_ID, A.SERIAL_NUMBER, ");
		sql.append("A.NET_TYPE_CODE, A.EPARCHY_CODE, A.CITY_CODE, ");
		sql.append("A.PRODUCT_ID, A.BRAND_CODE, ");
		sql.append("TO_CHAR(A.CUST_ID_B) CUST_ID_B, ");
		sql.append("TO_CHAR(A.USER_ID_B) USER_ID_B, ");
		sql.append("TO_CHAR(A.ACCT_ID_B) ACCT_ID_B, A.SERIAL_NUMBER_B, ");
		sql.append("A.CUST_CONTACT_ID, A.SERV_REQ_ID, A.INTF_ID, ");
		sql.append("TO_CHAR(A.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE, ");
		sql.append("A.TRADE_STAFF_ID, A.TRADE_DEPART_ID, ");
		sql.append("A.TRADE_CITY_CODE, A.TRADE_EPARCHY_CODE, A.TERM_IP, ");
		sql.append("TO_CHAR(A.OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(A.FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(A.ADVANCE_PAY) ADVANCE_PAY, A.INVOICE_NO, ");
		sql.append("A.FEE_STATE, ");
		sql.append("TO_CHAR(A.FEE_TIME, 'YYYY-MM-DD HH24:MI:SS') FEE_TIME, ");
		sql.append("A.FEE_STAFF_ID, A.PROCESS_TAG_SET, A.OLCOM_TAG, ");
		sql.append("TO_CHAR(A.FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS') FINISH_DATE, ");
		sql.append("TO_CHAR(A.EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
		sql.append("A.EXEC_ACTION, A.EXEC_RESULT, A.EXEC_DESC, ");
		sql.append("A.CANCEL_TAG, ");
		sql.append("TO_CHAR(A.CANCEL_DATE, 'YYYY-MM-DD HH24:MI:SS') CANCEL_DATE, ");
		sql.append("A.CANCEL_STAFF_ID, A.CANCEL_DEPART_ID, ");
		sql.append("A.CANCEL_CITY_CODE, A.CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
		sql.append("A.UPDATE_STAFF_ID, A.UPDATE_DEPART_ID, A.REMARK, ");
		sql.append("A.RSRV_STR1, A.RSRV_STR2, A.RSRV_STR3, A.RSRV_STR4, ");
		sql.append("A.RSRV_STR5, A.RSRV_STR6, A.RSRV_STR7, A.RSRV_STR8, ");
		sql.append("A.RSRV_STR9, A.RSRV_STR10 ,PF_WAIT,OLCOM_TAG, :DB_SRC DB_SRC, :ROUTE_ID ROUTE_ID ");
		// 为了割弃前业务返销重新组织数据送服务开通,cancle_tag='C';
		if (StringUtils.equals("C", cancelTag))
		{
			sql.append("FROM TF_BH_TRADE A ");
		} else
		{
			sql.append("FROM TF_B_TRADE A ");
		}
		sql.append("WHERE A.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
		sql.append("AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");
		if (!StringUtils.equals("C", cancelTag))
		{
			sql.append("AND A.CANCEL_TAG = :CANCEL_TAG ");
		}

		return Dao.qryBySql(sql, param, Route.getJourDb(routeId));
	}

	public static IDataset getMainTradeBySn(String sn, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SN", sn);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, TRADE_TYPE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
		sql.append("PRODUCT_ID, BRAND_CODE, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10,PF_WAIT ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE T.SERIAL_NUMBER = :SN ");
		sql.append(" AND T.SUBSCRIBE_TYPE <> '600' ");

		return Dao.qryBySql(sql, inparams, Route.getJourDbDefault());
	}

	/**
	 * 查询用户未完工工单
	 * 
	 * @param serialNumber
	 * @param tradeTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMainTradeBySN(String serialNumber, String tradeTypeCode) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", serialNumber);
		inparam.put("TRADE_TYPE_CODE", tradeTypeCode);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADETYPECODE_SN", inparam, Route.getJourDb(BizRoute.getTradeEparchyCode()));

	}

	public static IDataset getMainTradeByTradeId(String tradeId) throws Exception
	{
		return getMainTradeByTradeId(tradeId, BizRoute.getTradeEparchyCode());
	}

	public static IDataset getMainTradeByTradeId(String tradeId, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("TRADE_ID", tradeId);
		inparams.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, TRADE_TYPE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
		sql.append("PRODUCT_ID, BRAND_CODE, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, PF_WAIT ,INTF_ID ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		sql.append("AND T.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH) ");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(routeId));
	}

	/**
	 * 根据TRADEID查询用户主台帐信息
	 */
	public static IDataset getMainTradeByTradeIdForGrp(String tradeId) throws Exception
	{
		return getMainTradeByTradeId(tradeId, Route.CONN_CRM_CG);
	}

	public static IDataset getMainTradeByUserId(String user_id) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", user_id);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, TRADE_TYPE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
		sql.append("PRODUCT_ID, BRAND_CODE, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10 ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE T.USER_ID = :USER_ID AND T.subscribe_type<>'600'");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	public static IDataset getMainTradeByUserIdTypeCode(String userId, String tradeTypeCode) throws Exception
	{
		return getMainTradeByUserIdTypeCode(userId, tradeTypeCode, null);
	}

	public static IDataset getMainTradeByUserIdTypeCode(String user_id, String trade_type_code, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", user_id);
		inparams.put("TRADE_TYPE_CODE", trade_type_code);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, TO_CHAR(BPM_ID) BPM_ID, ");
		sql.append("TRADE_TYPE_CODE, IN_MODE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_STATE, NEXT_DEAL_TAG, PRODUCT_ID, ");
		sql.append("BRAND_CODE, TO_CHAR(USER_ID) USER_ID, ");
		sql.append("TO_CHAR(CUST_ID) CUST_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, CUST_NAME, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("ACCEPT_MONTH, TRADE_STAFF_ID, TRADE_DEPART_ID, ");
		sql.append("TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("EPARCHY_CODE, CITY_CODE, OLCOM_TAG, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, PROCESS_TAG_SET, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, REMARK ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE T.USER_ID = :USER_ID ");
		sql.append("AND T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");

		if (StringUtils.isNotBlank(routeId))
		{
			routeId = Route.getJourDb(routeId);
		} else
		{
			routeId = Route.getJourDb(BizRoute.getRouteId());
		}
		return Dao.qryBySql(sql, inparams, Route.getJourDb(routeId));
	}

	public static IDataset getTradeByUserIdProductId(String userId, String productId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("PRODUCT_ID", productId);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, TO_CHAR(BPM_ID) BPM_ID, ");
		sql.append("TRADE_TYPE_CODE, IN_MODE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_STATE, NEXT_DEAL_TAG, PRODUCT_ID, ");
		sql.append("BRAND_CODE, TO_CHAR(USER_ID) USER_ID, ");
		sql.append("TO_CHAR(CUST_ID) CUST_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, CUST_NAME, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("ACCEPT_MONTH, TRADE_STAFF_ID, TRADE_DEPART_ID, ");
		sql.append("TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("EPARCHY_CODE, CITY_CODE, OLCOM_TAG, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, PROCESS_TAG_SET, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, REMARK ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE T.USER_ID = :USER_ID ");
		sql.append("AND T.PRODUCT_ID = :PRODUCT_ID ");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static IDataset getMainTradeByUserIdTypeCodeForGrp(String userId, String tradeTypeCode) throws Exception
	{
		return getMainTradeByUserIdTypeCode(userId, tradeTypeCode, Route.CONN_CRM_CG);
	}

	public static boolean getMaxAdvancePayTradeInfoByTid(String tradeId, String advancePay) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ADVANCE_PAY", advancePay);
		IDataset tradeDataset = Dao.qryByCode("TF_B_TRADE", "SEL_MAX_ADVPAY_TRADE_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		if (IDataUtil.isNotEmpty(tradeDataset))
		{
			return true;
		}
		return false;
	}

	public static String getMaxOpenTradeInfoBySn(String serialNumber, String maxDay) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("MAX_DAY", maxDay);
		IDataset tradeDataset = Dao.qryByCode("TF_B_TRADE", "SEL_MAX_OPEN_TRADE_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		if (IDataUtil.isNotEmpty(tradeDataset))
		{
			return tradeDataset.getData(0).getString("TRADE_ID");
		}
		return null;
	}

	// todo
	/**
	 * @Description:根据用户的user_id 找商品关系台帐编码
	 * @author hud
	 * @date
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMerchMebTradeInfo(String SERIAL_NUMBER) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", SERIAL_NUMBER);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select a.* from tf_b_trade a, tf_f_user u ");
		parser.addSQL(" where a.user_id = u.user_id ");
		parser.addSQL(" and a.product_id = u.product_id ");
		parser.addSQL(" and a.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and u.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and  u.remove_tag = '0'");
		parser.addSQL(" and rownum = 1");
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询集团成员未完工业务限制
	 * 
	 * @author liaolc
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getNoTrade(IData data) throws Exception
	{
		IDataset dataset = Dao.qryByCode("TD_S_TRADETYPE_LIMIT", "SEL_EXISTS_LIMIT_TRADETYPECODE", data);
		return dataset;
	}

	public static IDataset getNoTradeJour(String tradeTypeCode, String userId, String brandCode, String limitAttr, String limitTag, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", tradeTypeCode);
		data.put("USER_ID", userId);
		data.put("BRAND_CODE", brandCode);
		data.put("LIMIT_ATTR", limitAttr);
		data.put("LIMIT_TAG", limitTag);
		data.put("EPARCHY_CODE", eparchyCode);
		IDataset dataset = Dao.qryByCodeParser("TD_S_TRADETYPE_LIMIT", "SEL_EXISTS_LIMIT_TRADETYPECODE", data, Route.getJourDb(eparchyCode));
		return dataset;
	}

	public static IDataset getNoTrade(String tradeTypeCode, String userId, String brandCode, String limitAttr, String limitTag, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", tradeTypeCode);
		data.put("USER_ID", userId);
		data.put("BRAND_CODE", brandCode);
		data.put("LIMIT_ATTR", limitAttr);
		data.put("LIMIT_TAG", limitTag);
		data.put("EPARCHY_CODE", eparchyCode);
		IDataset dataset = Dao.qryByCodeParser("TD_S_TRADETYPE_LIMIT", "SEL_EXISTS_LIMIT_TRADETYPECODE", data, Route.CONN_CRM_CG);
		return dataset;
	}

	public static IDataset getNoTradeByTradeId(String tradeTypeCode, String userId, String brandCode, String limitAttr, String limitTag, String eparchyCode, String strTradeId) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", tradeTypeCode);
		data.put("USER_ID", userId);
		data.put("BRAND_CODE", brandCode);
		data.put("LIMIT_ATTR", limitAttr);
		data.put("LIMIT_TAG", limitTag);
		data.put("EPARCHY_CODE", eparchyCode);
		data.put("TRADE_ID", strTradeId);
		IDataset dataset = Dao.qryByCodeParser("TD_S_TRADETYPE_LIMIT", "SEL_EXISTS_LIMIT_TRADETYPECODE_A", data, Route.getJourDb(BizRoute.getRouteId()));
		return dataset;
	}

	public static IDataset getPBossFinishByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_PBOSS_FINISH", "SEL_BY_TRADEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));

	}

	public static IDataset getPosTrade(String tradePosId, String status, String transType) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_POS_ID", tradePosId);
		param.put("STATUS", status); // 正常状态
		param.put("TRANS_TYPE", transType); // 缴费
		return Dao.qryByCode("TF_B_TRADE_POS", "SEL_BY_TRADEPOSID", param);
	}

	public static IDataset getSpecElementByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADE_SPEC_ELEMENT_1", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	public static IDataset getSpecElementByTradeIdProductId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADE_SPEC_ELEMENT_2", param, Route.getJourDb(BizRoute.getRouteId()));
	}

	/**
	 * @description 工单查询
	 * @author chenzm
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeAndBHTradeByTradeId(String tradeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADE_ID_WIDENET", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 查询用户预约产品资料
	 * 
	 * @param user_id
	 * @param trade_type_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeBookByUserId(String user_id, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", user_id);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BOOK_BY_USERID", inparams, Route.getJourDb(routeId));
	}

	/**
	 * 查询预约工单
	 * 
	 * @param user_id
	 * @param trade_type_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeBookByUserIdTradeType(String user_id, String trade_type_code) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", user_id);
		inparams.put("TRADE_TYPE_CODE", trade_type_code);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BOOK_BY_USERID_CODE", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 查询网上预约登记台账信息
	 * 
	 * @param month
	 * @param bookDate
	 * @param tradeDeptId
	 * @param bookStatus
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeBookInfos(String month, String bookDate, String tradeDeptId, String bookStatus) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_DEPART_ID", tradeDeptId);
		param.put("ACCEPT_MONTH", month);
		param.put("BOOK_DATE", bookDate);
		param.put("BOOK_STATUS", bookStatus);
		return Dao.qryByCode("TF_B_TRADEBOOK", "SEL_COUNT", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据CUST_ID,PRODUCT_ID查询集团客户订单信息
	 * 
	 * @param cust_id
	 * @param product_id
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeByCustIdProductId(String cust_id, String product_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_CUSTID_PRODUCTID", param, pagination);
	}

	/**
	 * 根据CUST_ID,PRODUCT_ID,TRADE_TYPE_CODE查询集团客户订单信息
	 * 
	 * @param cust_id
	 * @param product_id
	 * @param trade_type_code
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeByCustIdProductIdTradetype(String cust_id, String product_id, String trade_type_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);
		param.put("TRADE_TYPE_CODE", trade_type_code);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADETYPE_PRODUCTID_CUSTID", param, pagination);
	}

	/**
	 * 根据CUST_ID,PRODUCT_ID,TRADE_TYPE_CODE查询集团客户订单信息
	 */
	public static IDataset getTradeByCustIdProductIdTradetypeParserForGrp(String cust_id, String product_id, String trade_type_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);
		param.put("TRADE_TYPE_CODE", trade_type_code);
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_BY_TRADETYPE_PRODUCTID_CUSTID", param, pagination, Route.CONN_CRM_CG);
	}

	/**
	 * 根据SQL_REF查台账表
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeBySql(IData inparams, Pagination page) throws Exception
	{

		return Dao.qryByCode("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_FEEMOD", inparams);
	}

	/**
	 * 根据ATTR_VALUE查询TF_B_TRADE_EXT信息
	 */
	public static IDataset getTradeBysubscribeIdForEsop(String attr_value) throws Exception
	{
		IData param = new DataMap();
		param.put("ATTR_VALUE", attr_value);
		return Dao.qryByCode("TF_B_TRADE_EXT", "SEL_BY_SUBSCRIBE_ID", param, Route.getJourDb());
	}

	/**
	 * 根据USER_ID,PRODUCT_ID,TRADE_TYPE_CODE查询集团客户订单信息
	 */
	public static IDataset getTradeByUserIdProductIdTradetype(String user_id, String product_id, String trade_type_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		param.put("TRADE_TYPE_CODE", trade_type_code);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_USERID_TRADETYPE_PRODUCTID", param, pagination);
	}

	/**
	 * 根据user_id，trade_type_code查询主台帐表信息
	 * 
	 * @param user_id
	 * @param trade_type_code
	 * @return
	 * @throws Exception
	 */
	public static IData getTradeByUserIdTradeType(String user_id, String trade_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("TRADE_TYPE_CODE", trade_type_code);
		IDataset tradeInfos = Dao.qryByCode("TF_B_TRADE", "SEL_BY_USER_TRADETYPECODE", param);
		if (tradeInfos != null && tradeInfos.size() > 0)
		{
			return tradeInfos.getData(0);
		} else
		{
			return null;
		}
	}

	// todo

	/**
	 * @description 查询ESOP侧的台账信息
	 * @author xunyl
	 * @date 2013-09-26
	 */
	public static IDataset getTradeForGrpBBoss(String tradeId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("TRADE_ID", tradeId);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT to_char(trade_id) trade_id, ");
		sql.append("accept_month, ");
		sql.append("ATTR_CODE, ");
		sql.append("ATTR_VALUE, ");
		sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, ");
		sql.append("UPDATE_DEPART_ID, ");
		sql.append("REMARK, ");
		sql.append("rsrv_str1, ");
		sql.append("rsrv_str2, ");
		sql.append("rsrv_str3, ");
		sql.append("rsrv_str4, ");
		sql.append("rsrv_str5, ");
		sql.append("rsrv_str6, ");
		sql.append("rsrv_str7, ");
		sql.append("rsrv_str8, ");
		sql.append("rsrv_str9, ");
		sql.append("rsrv_str10 ");
		sql.append("FROM tf_b_trade_ext ");
		sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
		sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
		sql.append("AND attr_code = 'ESOP' ");

		return Dao.qryBySql(sql, inparam, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static IData getTradeInfo(String tradeId, String modifyTag) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("MODIFY_TAG", modifyTag);
		IDataset results = Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_BY_TRADE_ID", param, Route.getJourDb());
		IData chkDataInfo = results.size() > 0 ? (IData) results.getData(0) : null;
		return chkDataInfo;
	}

	public static IDataset getTradeInfo(String serialNumber, String tradeId, String orderId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("TRADE_ID", tradeId);
		param.put("ORDER_ID", orderId);

		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BYSN", param, Route.getJourDb(routeId));
	}

	/**
	 * chenyi 根据order_id查询tradeInfo
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeInfobyOrd(String orderId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("ORDER_ID", orderId);
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_BY_ORDERID_PF", inparams);
	}

	/**
	 * chenyi 根据order_id,str10查询管理受理未发送tradeInfo 13-10-25
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeInfobyOrdStr10(String orderId, String rsrv_str10) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("ORDER_ID", orderId);
		inparams.put("RSRV_STR10", rsrv_str10);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT TO_CHAR(a.trade_id) trade_id, ");
		sql.append("a.accept_month, ");
		sql.append("TO_CHAR(a.batch_id) batch_id, ");
		sql.append("TO_CHAR(a.order_id) order_id, ");
		sql.append("TO_CHAR(a.prod_order_id) prod_order_id, ");
		sql.append("a.bpm_id, ");
		sql.append("TO_CHAR(a.campn_id) campn_id, ");
		sql.append("a.trade_type_code, ");
		sql.append("a.priority, ");
		sql.append("a.subscribe_type, ");
		sql.append("a.subscribe_state, ");
		sql.append("a.next_deal_tag, ");
		sql.append("a.in_mode_code, ");
		sql.append("TO_CHAR(a.cust_id) cust_id, ");
		sql.append("a.cust_name, ");
		sql.append("TO_CHAR(a.user_id) user_id, ");
		sql.append("TO_CHAR(a.acct_id) acct_id, ");
		sql.append("a.serial_number, ");
		sql.append("a.net_type_code, ");
		sql.append("a.eparchy_code, ");
		sql.append("a.city_code, ");
		sql.append("a.product_id, ");
		sql.append("a.brand_code, ");
		sql.append("TO_CHAR(a.cust_id_b) cust_id_b, ");
		sql.append("TO_CHAR(a.user_id_b) user_id_b, ");
		sql.append("TO_CHAR(a.acct_id_b) acct_id_b, ");
		sql.append("a.serial_number_b, ");
		sql.append("a.cust_contact_id, ");
		sql.append("a.serv_req_id, ");
		sql.append("a.intf_id, ");
		sql.append("TO_CHAR(a.accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date, ");
		sql.append("a.trade_staff_id, ");
		sql.append("a.trade_depart_id, ");
		sql.append("a.trade_city_code, ");
		sql.append("a.trade_eparchy_code, ");
		sql.append("a.term_ip, ");
		sql.append("TO_CHAR(a.oper_fee) oper_fee, ");
		sql.append("TO_CHAR(a.foregift) foregift, ");
		sql.append("TO_CHAR(a.advance_pay) advance_pay, ");
		sql.append("a.invoice_no, ");
		sql.append("a.fee_state, ");
		sql.append("TO_CHAR(a.fee_time, 'YYYY-MM-DD HH24:MI:SS') fee_time, ");
		sql.append("a.fee_staff_id, ");
		sql.append("a.process_tag_set, ");
		sql.append("a.olcom_tag, ");
		sql.append("TO_CHAR(a.finish_date, 'YYYY-MM-DD HH24:MI:SS') finish_date, ");
		sql.append("TO_CHAR(a.exec_time, 'YYYY-MM-DD HH24:MI:SS') exec_time, ");
		sql.append("a.exec_action, ");
		sql.append("a.exec_result, ");
		sql.append("a.exec_desc, ");
		sql.append("a.cancel_tag, ");
		sql.append("TO_CHAR(a.cancel_date, 'YYYY-MM-DD HH24:MI:SS') cancel_date, ");
		sql.append("a.cancel_staff_id, ");
		sql.append("a.cancel_depart_id, ");
		sql.append("a.cancel_city_code, ");
		sql.append("a.cancel_eparchy_code, ");
		sql.append("TO_CHAR(a.update_time, 'YYYY-MM-DD HH24:MI:SS') update_time, ");
		sql.append("a.update_staff_id, ");
		sql.append("a.update_depart_id, ");
		sql.append("a.remark, ");
		sql.append("a.rsrv_str1, ");
		sql.append("a.rsrv_str2, ");
		sql.append("a.rsrv_str3, ");
		sql.append("a.rsrv_str4, ");
		sql.append("a.rsrv_str5, ");
		sql.append("a.rsrv_str6, ");
		sql.append("a.rsrv_str7, ");
		sql.append("a.rsrv_str8, ");
		sql.append("a.rsrv_str9, ");
		sql.append("a.rsrv_str10 ");
		sql.append("FROM tf_b_trade a ");
		sql.append("WHERE a.order_id = TO_NUMBER(:ORDER_ID) ");
		sql.append("and a.rsrv_str10 = :RSRV_STR10 ");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	public static IDataset getTradeInfobyOrd2(String orderId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("ORDER_ID", orderId);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT TO_CHAR(a.trade_id) trade_id, ");
		sql.append("a.accept_month, ");
		sql.append("TO_CHAR(a.batch_id) batch_id, ");
		sql.append("TO_CHAR(a.order_id) order_id, ");
		sql.append("TO_CHAR(a.prod_order_id) prod_order_id, ");
		sql.append("a.bpm_id, ");
		sql.append("TO_CHAR(a.campn_id) campn_id, ");
		sql.append("a.trade_type_code, ");
		sql.append("a.priority, ");
		sql.append("a.subscribe_type, ");
		sql.append("a.subscribe_state, ");
		sql.append("a.next_deal_tag, ");
		sql.append("a.in_mode_code, ");
		sql.append("TO_CHAR(a.cust_id) cust_id, ");
		sql.append("a.cust_name, ");
		sql.append("TO_CHAR(a.user_id) user_id, ");
		sql.append("TO_CHAR(a.acct_id) acct_id, ");
		sql.append("a.serial_number, ");
		sql.append("a.net_type_code, ");
		sql.append("a.eparchy_code, ");
		sql.append("a.city_code, ");
		sql.append("a.product_id, ");
		sql.append("a.brand_code, ");
		sql.append("TO_CHAR(a.cust_id_b) cust_id_b, ");
		sql.append("TO_CHAR(a.user_id_b) user_id_b, ");
		sql.append("TO_CHAR(a.acct_id_b) acct_id_b, ");
		sql.append("a.serial_number_b, ");
		sql.append("a.cust_contact_id, ");
		sql.append("a.serv_req_id, ");
		sql.append("a.intf_id, ");
		sql.append("TO_CHAR(a.accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date, ");
		sql.append("a.trade_staff_id, ");
		sql.append("a.trade_depart_id, ");
		sql.append("a.trade_city_code, ");
		sql.append("a.trade_eparchy_code, ");
		sql.append("a.term_ip, ");
		sql.append("TO_CHAR(a.oper_fee) oper_fee, ");
		sql.append("TO_CHAR(a.foregift) foregift, ");
		sql.append("TO_CHAR(a.advance_pay) advance_pay, ");
		sql.append("a.invoice_no, ");
		sql.append("a.fee_state, ");
		sql.append("TO_CHAR(a.fee_time, 'YYYY-MM-DD HH24:MI:SS') fee_time, ");
		sql.append("a.fee_staff_id, ");
		sql.append("a.process_tag_set, ");
		sql.append("a.olcom_tag, ");
		sql.append("TO_CHAR(a.finish_date, 'YYYY-MM-DD HH24:MI:SS') finish_date, ");
		sql.append("TO_CHAR(a.exec_time, 'YYYY-MM-DD HH24:MI:SS') exec_time, ");
		sql.append("a.exec_action, ");
		sql.append("a.exec_result, ");
		sql.append("a.exec_desc, ");
		sql.append("a.cancel_tag, ");
		sql.append("TO_CHAR(a.cancel_date, 'YYYY-MM-DD HH24:MI:SS') cancel_date, ");
		sql.append("a.cancel_staff_id, ");
		sql.append("a.cancel_depart_id, ");
		sql.append("a.cancel_city_code, ");
		sql.append("a.cancel_eparchy_code, ");
		sql.append("TO_CHAR(a.update_time, 'YYYY-MM-DD HH24:MI:SS') update_time, ");
		sql.append("a.update_staff_id, ");
		sql.append("a.update_depart_id, ");
		sql.append("a.remark, ");
		sql.append("a.rsrv_str1, ");
		sql.append("a.rsrv_str2, ");
		sql.append("a.rsrv_str3, ");
		sql.append("a.rsrv_str4, ");
		sql.append("a.rsrv_str5, ");
		sql.append("a.rsrv_str6, ");
		sql.append("a.rsrv_str7, ");
		sql.append("a.rsrv_str8, ");
		sql.append("a.rsrv_str9, ");
		sql.append("a.rsrv_str10 ");
		sql.append("FROM tf_b_trade a ");
		sql.append("WHERE a.order_id = TO_NUMBER(:ORDER_ID) ");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static IDataset getTradeInfoBySelByCheckNofinish(String userId, String custID, String TradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("CUST_ID", custID);
		param.put("TRADE_TYPE_CODE", TradeTypeCode);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_CHECK_NOFINISH", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 获取未完工业务
	 * 
	 * @author chenzm
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeInfoBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_TRADETYPECODE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * chenyi 根据tradeid查询tradeInfo 13-10-25
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeInfobyTradeId(String tradeId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("TRADE_ID", tradeId);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ");
		sql.append("ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, ");
		sql.append("BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
		sql.append("TRADE_TYPE_CODE, ");
		sql.append("PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, ");
		sql.append("SUBSCRIBE_STATE, ");
		sql.append("NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, ");
		sql.append("TO_CHAR(CUST_ID) CUST_ID, ");
		sql.append("CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, ");
		sql.append("TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, ");
		sql.append("NET_TYPE_CODE, ");
		sql.append("EPARCHY_CODE, ");
		sql.append("CITY_CODE, ");
		sql.append("PRODUCT_ID, ");
		sql.append("BRAND_CODE, ");
		sql.append("TO_CHAR(CUST_ID_B) CUST_ID_B, ");
		sql.append("TO_CHAR(USER_ID_B) USER_ID_B, ");
		sql.append("TO_CHAR(ACCT_ID_B) ACCT_ID_B, ");
		sql.append("TO_CHAR(SERIAL_NUMBER_B) SERIAL_NUMBER_B, ");
		sql.append("TO_CHAR(CUST_CONTACT_ID) CUST_CONTACT_ID, ");
		sql.append("TO_CHAR(SERV_REQ_ID) SERV_REQ_ID, ");
		sql.append("TO_CHAR(INTF_ID) INTF_ID, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, ");
		sql.append("TRADE_DEPART_ID, ");
		sql.append("TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, ");
		sql.append("TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, ");
		sql.append("INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, ");
		sql.append("PROCESS_TAG_SET, ");
		sql.append("OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, ");
		sql.append("EXEC_RESULT, ");
		sql.append("EXEC_DESC, ");
		sql.append("CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, ");
		sql.append("CANCEL_DEPART_ID, ");
		sql.append("CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, ");
		sql.append("UPDATE_DEPART_ID, ");
		sql.append("REMARK, ");
		sql.append("RSRV_STR1, ");
		sql.append("RSRV_STR2, ");
		sql.append("RSRV_STR3, ");
		sql.append("RSRV_STR4, ");
		sql.append("RSRV_STR5, ");
		sql.append("RSRV_STR6, ");
		sql.append("RSRV_STR7, ");
		sql.append("RSRV_STR8, ");
		sql.append("RSRV_STR9, ");
		sql.append("RSRV_STR10, ");
		sql.append("PF_WAIT ");
		sql.append("FROM TF_B_TRADE ");
		sql.append("WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		sql.append("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	public static IDataset getTradeInfobyTradeId(String tradeId, String acceptMonth) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("TRADE_ID", tradeId);
		inparams.put("ACCEPT_MONTH", acceptMonth);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ");
		sql.append("ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, ");
		sql.append("BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
		sql.append("TRADE_TYPE_CODE, ");
		sql.append("PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, ");
		sql.append("SUBSCRIBE_STATE, ");
		sql.append("NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, ");
		sql.append("TO_CHAR(CUST_ID) CUST_ID, ");
		sql.append("CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, ");
		sql.append("TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, ");
		sql.append("NET_TYPE_CODE, ");
		sql.append("EPARCHY_CODE, ");
		sql.append("CITY_CODE, ");
		sql.append("PRODUCT_ID, ");
		sql.append("BRAND_CODE, ");
		sql.append("TO_CHAR(CUST_ID_B) CUST_ID_B, ");
		sql.append("TO_CHAR(USER_ID_B) USER_ID_B, ");
		sql.append("TO_CHAR(ACCT_ID_B) ACCT_ID_B, ");
		sql.append("TO_CHAR(SERIAL_NUMBER_B) SERIAL_NUMBER_B, ");
		sql.append("TO_CHAR(CUST_CONTACT_ID) CUST_CONTACT_ID, ");
		sql.append("TO_CHAR(SERV_REQ_ID) SERV_REQ_ID, ");
		sql.append("TO_CHAR(INTF_ID) INTF_ID, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, ");
		sql.append("TRADE_DEPART_ID, ");
		sql.append("TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, ");
		sql.append("TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, ");
		sql.append("INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, ");
		sql.append("PROCESS_TAG_SET, ");
		sql.append("OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, ");
		sql.append("EXEC_RESULT, ");
		sql.append("EXEC_DESC, ");
		sql.append("CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, ");
		sql.append("CANCEL_DEPART_ID, ");
		sql.append("CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, ");
		sql.append("UPDATE_DEPART_ID, ");
		sql.append("REMARK, ");
		sql.append("RSRV_STR1, ");
		sql.append("RSRV_STR2, ");
		sql.append("RSRV_STR3, ");
		sql.append("RSRV_STR4, ");
		sql.append("RSRV_STR5, ");
		sql.append("RSRV_STR6, ");
		sql.append("RSRV_STR7, ");
		sql.append("RSRV_STR8, ");
		sql.append("RSRV_STR9, ");
		sql.append("RSRV_STR10, ");
		sql.append("PF_WAIT ");
		sql.append("FROM TF_B_TRADE ");
		sql.append("WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		sql.append("AND ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH)");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 根据工号查询办理的一单清打印相关业务
	 * 
	 * @param staff_id
	 * @param user_id
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeInfos(String staff_id, String user_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("STAFF_ID", staff_id);
		param.put("USER_ID", user_id);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADESTAFF", param, pagination);
	}

	public static IDataset getTradeInfos(String orderId, String tradeStaffId, String serialNumber, String tradeTypeCode) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		inparams.put("ORDER_ID", orderId);
		inparams.put("TRADE_STAFF_ID", tradeStaffId);
		inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADEID_BY_ORDERID", inparams, Route.getJourDb());
	}

	public static IDataset getTradeInfosByCancelTag(String userId, String cancelTag, String tradeTypeCode) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("CANCEL_TAG", cancelTag);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_USERID_CANCELTAG", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	public static IDataset getTradeInfosBySelTradeByUserIdCode(String tradeTypeCode, String userId, String cancelTag) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("USER_ID", userId);
		param.put("CANCEL_TAG", cancelTag);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BY_USERID_CODE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));

	}

	public static IDataset getTradeInfosBySnEparchyCode(String serialNumber, String eparchy_code, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_EPARCHYCODE", param, page);
	}

	public static IDataset getTradeInfosBySubscribeState(String tradeId, String cancelTag, String tradeTypeCode, String subscribeState) throws Exception
	{

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("CANCEL_TAG", cancelTag);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("SUBSCRIBE_STATE", subscribeState);
		return Dao.qryByCode("TF_B_TRADE", "SEL_NORUN_BY_TRADEID", param, Route.getJourDb());
	}

	public static IDataset getTradeInfosByUserIdCancelTag(String userId, String cancel_tag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("CANCEL_TAG", cancel_tag);
		return Dao.qryByCode("TF_B_TRADE", "SEL_LAST_TRADEID_BY_SN_NPTRADETYPECODE2", param);
	}

	/**
	 * 查询停短信、停语音历史记录
	 */

	public static IDataset getTradeMainHisInfo(IData data) throws Exception
	{
		String serialNumber = data.getString("SERIAL_NUMBER");

		if (!data.containsKey("X_GETMODE"))
		{
			CSAppException.apperr(ParamException.CRM_PARAM_2);// -1:X_GETMODE不存在
		}
		if (data.getString("TRADE_TYPE_CODE", "").equals(""))
		{
			CSAppException.apperr(ParamException.CRM_PARAM_52);// TRADE_TYPE_CODE字段没传
		}

		IDataset res = new DatasetList();
		if (0 == data.getInt("X_GETMODE"))
		{// 正常用户
			res = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(serialNumber));
		} else if (5 == data.getInt("X_GETMODE"))
		{// 最后销号用户
			res = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
		}

		if (res == null || res.size() < 1)
		{
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}
		String userid = ((IData) res.get(0)).getString("USER_ID");
		String eparchyCode = ((IData) res.get(0)).getString("EPARCHY_CODE");
		data.put("EPARCHY_CODE", eparchyCode);
		data.put("USER_ID", userid);

		String sql = "SELECT a.trade_id,a.order_id,a.bpm_id,a.trade_type_code,a.in_mode_code," + "a.priority,a.next_deal_tag,a.product_id,a.brand_code,a.user_id,a.cust_id," + "a.acct_id,nvl(a.serial_number,:SERIAL_NUMBER) serial_number,a.cust_name," + "to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,a.accept_month," + "a.trade_staff_id,a.trade_depart_id,a.trade_city_code,a.trade_eparchy_code," + "a.term_ip,a.eparchy_code,a.city_code,a.olcom_tag," + "to_char(a.exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time," + "to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,a.oper_fee,a.foregift," + "a.advance_pay,a.Invoice_No,a.fee_state," + "to_char(a.fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,a.fee_staff_id,a.cancel_tag," + "a.process_tag_set,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5," + "a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,a.remark,tb.trade_type " + "FROM tf_bh_trade a, td_s_tradetype tb " + "WHERE a.trade_type_code = tb.trade_type_code and tb.eparchy_code = :EPARCHY_CODE  and a.user_id = to_number(:USER_ID) " + "AND (a.trade_type_code = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = '-1') " + "AND NOT EXISTS (SELECT 1 FROM TF_BH_TRADE t  where t.TRADE_TYPE_CODE = '1170'" + "AND t.IN_MODE_CODE = '1' AND a.trade_id=t.trade_id and a.accept_month = t.accept_month )" + "AND (a.accept_date >= to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') OR :START_DATE IS NULL) " + "AND (a.accept_date <= to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')+1 OR :END_DATE IS NULL)" + "AND a.trade_type_code<>'2101'" + "ORDER BY accept_date desc";
		IDataset result = Dao.qryBySql(new StringBuilder(sql), data, eparchyCode);

		return result;
	}

	public static IDataset getTradeStatistic(IData params) throws Exception
	{
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_COUNT", params);
	}

	public static IDataset getTradeWaitByUser(String userId, String execTime, String orderId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EXEC_TIME", execTime);
		param.put("ORDER_ID", orderId);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT COUNT(1) COUNT ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE T.USER_ID = :USER_ID ");
		sql.append("AND EXEC_TIME < TO_DATE(:EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
		// sql.append("AND T.OLCOM_TAG = '1' ");
		sql.append("AND T.SUBSCRIBE_STATE IN ('0', '3') ");
		sql.append("AND T.ORDER_ID <> :ORDER_ID ");
		sql.append("AND T.SUBSCRIBE_TYPE <> '600' ");
		sql.append("AND ROWNUM < 2 ");

		return Dao.qryBySql(sql, param, Route.getJourDb(routeId));
	}

	public static IDataset getTrainTradeInfo(String serialNumber, String channelTradeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		inparams.put("CHANNEL_TRADE_ID", channelTradeId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRAIN_TRADE_INFO", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	// todo
	/**
	 * 获取二次确认台账信息
	 * 
	 * @param iData
	 * @return
	 */
	public static IDataset getTwoCheckTrade(String TRADE_ID) throws Exception
	{

		// String tradeId = iData.getString("TRADE_ID");
		if (TRADE_ID == null || "".equals(TRADE_ID))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103);
		}
		IData params = new DataMap();
		params.put("VTRADE_ID", TRADE_ID);
		try
		{
			IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE", "SEL_BY_TWOCHECK_TRADE_ID", params, Route.CONN_CRM_CEN);
			return iDataset;
		} catch (Exception e)
		{
			//e.printStackTrace();
			CSAppException.apperr(TimeException.CRM_TIME_30);
			return null;
		}
	}

	public static IDataset getUcaKeyByPk(String tradeId, String acceptMonth, String userId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		IDataset results = new DatasetList();
		IDataset result = new DatasetList();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT DISTINCT 'CUSTOMER' ID_TYPE, TO_CHAR(T.CUST_ID) ID_VALUE, '' SN_VALUE ");
		sql.append("FROM TF_B_TRADE_CUSTOMER T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("UNION ALL ");
		sql.append("SELECT DISTINCT 'PERSON' ID_TYPE, TO_CHAR(T.CUST_ID) ID_VALUE, '' SN_VALUE ");
		sql.append("FROM TF_B_TRADE_CUST_PERSON T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("UNION ALL ");
		sql.append("SELECT DISTINCT 'USER' ID_TYPE, TO_CHAR(T.USER_ID) ID_VALUE, T.SERIAL_NUMBER SN_VALUE ");
		sql.append("FROM TF_B_TRADE_USER T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		// 改号业务需要从TF_F_USER取老的SN
		String snChangedS = BizEnv.getEnvString("crm.personserv.snChanged", "");
		String[] snChangedArray = StringUtils.split(snChangedS, ",");

		int SnChangedIndex = StringUtils.strAtArray(tradeTypeCode, snChangedArray);
		if (SnChangedIndex > -1)
		{
			StringBuilder sql1 = new StringBuilder(2500);
			sql1.append("SELECT DISTINCT 'USER' ID_TYPE, TO_CHAR(T.USER_ID) ID_VALUE, T.SERIAL_NUMBER SN_VALUE ");
			sql1.append("FROM TF_F_USER T ");
			sql1.append("WHERE T.USER_ID = :USER_ID ");
			sql1.append("AND T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
			sql1.append("AND T.REMOVE_TAG = '0' ");
			result = Dao.qryBySql(sql1, param);
		}
		sql.append("UNION ALL ");
		sql.append("SELECT DISTINCT 'PROD' ID_TYPE, TO_CHAR(T.USER_ID) ID_VALUE, '' SN_VALUE ");
		sql.append("FROM TF_B_TRADE_PRODUCT T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("UNION ALL ");
		sql.append("SELECT DISTINCT 'ACCT' ID_TYPE, TO_CHAR(T.ACCT_ID) ID_VALUE, '' SN_VALUE ");
		sql.append("FROM TF_B_TRADE_ACCOUNT T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("UNION ALL ");
		sql.append("SELECT DISTINCT 'DEFPAY' ID_TYPE, TO_CHAR(T.USER_ID) ID_VALUE, '' SN_VALUE ");
		sql.append("FROM TF_B_TRADE_PAYRELATION T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("UNION ALL ");
		sql.append("SELECT DISTINCT 'ACCTDAYS' ID_TYPE, TO_CHAR(T.USER_ID) ID_VALUE, '' SN_VALUE ");
		sql.append("FROM TF_B_TRADE_USER_ACCTDAY T ");
		sql.append("WHERE T.TRADE_ID = :TRADE_ID ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		results = Dao.qryBySql(sql, param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		if (IDataUtil.isNotEmpty(result))
		{
			results.addAll(result);
		}
		return results;
	}

	/**
	 * 查询用户是否有某服务的未完工工单
	 * 
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUncompleteTrade(String userId, String serviceId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_PLATSVC_UNDO_ORDER", param, pagination);
	}

	/**
	 * 查询是否有未完工的工单
	 * 
	 * @param param
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserTradeByUserID(String TRADE_TYPE_CODE, String USER_ID, String EXEC_TIME, String CANCEL_TAG, String eparchyCode, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
		param.put("USER_ID", USER_ID);
		param.put("EXEC_TIME", EXEC_TIME);
		param.put("CANCEL_TAG", CANCEL_TAG);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_USER", param, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 根据cust_id,product_id查询未完工的trade_user信息
	 */
	public static IDataset getUserTradeNotFinish(String cust_id, String product_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);

		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_USER_BY_CUSTID_PRODUCTID", param, pagination);
	}

	public static IDataset getWindTradeInfoBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_INTRADETYPECODE_SN", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

	}

	/**
	 * 查询某集团下台账表BBOSS产品已预受理的台账
	 * 
	 * @param custId
	 * @param productId
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryBbossAheadTrade(String custId, String productId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();

		param.put("CUST_ID", custId);
		param.put("PRODUCT_ID", productId);

		BizCtrlInfo bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);
		param.put("ADD_TYPE_CODE", bizCtrlInfo.getTradeTypeCode());

		bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeUserDis);
		param.put("MOD_TYPE_CODE", bizCtrlInfo.getTradeTypeCode());

		SQLParser parser = new SQLParser(param);

		parser.addSQL("SELECT T.*");
		parser.addSQL("  FROM TF_B_TRADE T");
		parser.addSQL(" WHERE 1 = 1");
		parser.addSQL("  AND t.RSRV_STR10 = '1'"); // 预受理已响应BBOSS下发的标志
		parser.addSQL("  AND T.CUST_ID = :CUST_ID");
		parser.addSQL("  AND (T.TRADE_TYPE_CODE = :ADD_TYPE_CODE OR T.TRADE_TYPE_CODE = :MOD_TYPE_CODE)"); // 用户新增业务类型和用户更新业务类型(分批受理多个产品时),
		// 会生成预受理台账
		parser.addSQL("  AND EXISTS (SELECT 1");
		parser.addSQL("         FROM TF_B_TRADE_PRODUCT P");
		parser.addSQL("        WHERE P.TRADE_ID = T.TRADE_ID");
		parser.addSQL("          AND P.ACCEPT_MONTH = T.ACCEPT_MONTH");
		parser.addSQL("          AND P.PRODUCT_ID = :PRODUCT_ID)");

		return Dao.qryByParse(parser, pagination);
	}

	public static String qryErrorMsg(String tradeId, String orderId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ORDER_ID", orderId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT F_CS_GETTRADEERR(:TRADE_ID,:ORDER_ID) ERR FROM DUAL ");

		IDataset ids = Dao.qryByParse(parser, Route.getJourDb(routeId));

		String errorMsg = "";

		if (IDataUtil.isNotEmpty(ids))
		{
			errorMsg = ids.getData(0).getString("ERR");
		}

		return errorMsg;
	}

	/**
	 * 通过USER_ID_B查询未完工工单
	 * 
	 * @param userId
	 * @param tradeTypeCode
	 * @param cancelTag
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2014-1-8
	 */
	public static IDataset qryTradeByUserIdB(String userId, String tradeTypeCode, String cancelTag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("CANCEL_TAG", cancelTag);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_USERIDB", param);
	}

	public static IDataset qryTradeInfo(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT * FROM TF_B_TRADE WHERE SERIAL_NUMBER=:SERIAL_NUMBER AND TRADE_TYPE_CODE IN ('7101', '7110')");
		IDataset tradeInfo = Dao.qryBySql(sBuilder, param);
		return tradeInfo;
	}

	public static IDataset qryTradeInfos(String trade_type_code, String accept_month, String cancel_tag, String rsrv_str1) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", trade_type_code);
		param.put("ACCEPT_MONTH", accept_month);
		param.put("CANCEL_TAG", cancel_tag);
		param.put("RSRV_STR1", rsrv_str1);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADETYPECODE_ACCEPTMONTH", param);
	}

	public static IDataset qryTradeInfosBySnTrade(String serialNumber, String cancelTag, String tradeTypeCode1, String tradeTypeCode2) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CANCEL_TAG", cancelTag);
		param.put("TRADE_TYPE_CODE1", tradeTypeCode1);
		param.put("TRADE_TYPE_CODE2", tradeTypeCode2);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_TRADE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 根据固话号码查询未完工或已完工的固话开户信息 根据主键查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTradeTelephoneInfosByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_TELEPHONE", "SEL_TRADEINFO_BY_TRADE", param);
	}

	/**
	 * 查询本地市未完工单数量
	 * 
	 * @return
	 * @throws Exception
	 */
	public static int qryUnFinishTradeCount() throws Exception
	{
		IData param = new DataMap();
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT count(1) RECORDNUM FROM tf_b_trade  where 1=1 and exec_time<=sysdate");
		IDataset dataInfos = Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
		if (IDataUtil.isEmpty(dataInfos))
		{
			return 0;
		} else
		{
			return dataInfos.getData(0).getInt("RECORDNUM", 0);
		}
	}

	public static IDataset qryWidenetAcctIdTrade(String rsrvStr1) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", rsrvStr1);
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("SELECT * FROM TF_B_TRADE WHERE RSRV_STR1=:SERIAL_NUMBER AND TRADE_TYPE_CODE ='9732' AND CANCEL_TAG='0'");
		IDataset tradeInfo = Dao.qryBySql(sBuilder, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		return tradeInfo;
	}

	/**
	 * chenyi 查看预受理阶段的trade数据
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static IDataset queryBbossTradeByEsop(String product_id, String group_id, String uids) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", product_id);
		param.put("GROUP_ID", group_id);
		param.put("UIDS", uids);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_GRPIDPID", param, Route.getJourDb());

	}

	/**
	 * 查询宽带绑定信息
	 * 
	 * @param sourceSerialnumber
	 * @param tradeTypeCode
	 * @param rsrvStr10
	 * @return
	 * @throws Exception
	 * @author wangww
	 */
	public static IDataset queryBundleChargesTradeInfo(String sourceSerialnumber, String tradeTypeCode, String rsrvStr10) throws Exception
	{

		IData param = new DataMap();
		param.put("RSRV_STR1", sourceSerialnumber);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("RSRV_STR10", rsrvStr10);
		return Dao.qryByCodeAllCrm("TF_B_TRADE", "SEL_BY_TRADE_TYPE_CODE_RSRV1", param, false);
	}

	/**
	 * 信控发起工单时 根据USER_ID,TRADE_TYPE_CODE查询未完工工单
	 * 
	 * @param user_id
	 * @param trade_type_code
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCreditTradeNotFinish(String user_id, String trade_type_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("TRADE_TYPE_CODE", trade_type_code);
		return Dao.qryByCode("TF_B_TRADE", "SEL_CREDITGRP_NOTFINISH", param, pagination);
	}

	/**
	 * 查询错单
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryErrorTrade(String serialNumber) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_B_TRADE", "SEL_ERRORTRADE_BY_SN", inparam, Route.getJourDb());
	}

	public static IDataset queryExistTradePro(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_LAST_TRADEID_BY_SN_NPTRADETYPECODE2", param);
	}

	/**
	 * @Function: queryExistWideTrade()
	 * @Description: 查询宽带在途工单
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: yxd
	 * @date: 2014-8-1 上午10:39:36 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-8-1 yxd v1.0.0 修改原因
	 */
	public static IDataset queryExistWideTrade(String wSerialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", wSerialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_WIDE_BY_SN", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));

	}

	// todo
	/**
	 * 根据传入的sql，从cg库查询数据 TODO
	 * 
	 * @param sqlStr
	 *            查询sql
	 * @param param
	 *            查询参数
	 * @return IDataset 查询结果
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	public static IDataset queryFromCg(String sqlStr, IData param) throws Exception
	{

		SQLParser parser = new SQLParser(param);
		parser.addSQL(sqlStr);
		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		return dataset;
	}

	public static IDataset queryInfosBhTrade(IData inParam, Pagination pagination) throws Exception
	{
		String groupid = inParam.getString("GROUP_ID");

		// 如果集团编码非空，则先查出集团客户资料
		if (StringUtils.isNotBlank(groupid))
		{
			// 根据集团编码查询集团信息
			IData ids = UcaInfoQry.qryGrpInfoByGrpId(groupid);

			if (IDataUtil.isEmpty(ids))
			{
				return null;
			}

			// 得到集团客户标识
			IData map = ids;
			String custId = map.getString("CUST_ID");

			inParam.put("CUST_ID", custId);
		}

		SQLParser parser = new SQLParser(inParam);

		parser.addSQL(" select b.*");
		parser.addSQL("  from tf_bh_trade b");
		parser.addSQL(" where 1 = 1");
		parser.addSQL("   and b.cust_id = :CUST_ID");
		parser.addSQL("   and b.serial_number = :SERIAL_NUMBER_A");
		parser.addSQL("   and b.trade_type_code = :TRADE_TYPE_CODE");
		parser.addSQL("   and b.exec_time <= to_date(:END_DATE, 'YYYY-MM-DD') + 1 ");
		parser.addSQL("   and b.exec_time > to_date(:START_DATE, 'YYYY-MM-DD') ");
		parser.addSQL("   and b.accept_month = :ACCEPT_MONTH");

		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	public static IDataset queryInfosTrade(IData data, Pagination pagination) throws Exception
	{

		String groupId = data.getString("GROUP_ID");

		// 如果集团编码非空，则先查出集团客户资料
		if (StringUtils.isNotBlank(groupId))
		{
			// 根据集团编码查询集团信息
			IData ids = UcaInfoQry.qryGrpInfoByGrpId(groupId);

			if (IDataUtil.isEmpty(ids))
			{
				return null;
			}

			// 得到集团客户标识
			IData map = ids;
			String custId = map.getString("CUST_ID");

			data.put("CUST_ID", custId);
		}

		SQLParser parser = new SQLParser(data);

		parser.addSQL(" select b.*");
		parser.addSQL("  from tf_b_trade b");
		parser.addSQL(" where 1 = 1");
		parser.addSQL("   and b.cust_id = :CUST_ID");
		parser.addSQL("   and b.serial_number = :SERIAL_NUMBER_A");
		parser.addSQL("   and b.trade_type_code = :TRADE_TYPE_CODE");
		parser.addSQL("   and b.subscribe_state = :SUBSCRIBE_STATE");
		parser.addSQL("   and b.exec_time <= to_date(:END_DATE, 'YYYY-MM-DD') + 1 ");
		parser.addSQL("   and b.exec_time > to_date(:START_DATE, 'YYYY-MM-DD') ");
		parser.addSQL("   and b.accept_month = :ACCEPT_MONTH");

		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	// todo
	/**
	 * 监测adc/mas未完成工单
	 * 
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryMasTradeNotFinishByUidAndPid(String user_id, String product_id) throws Exception
	{
		IData pamLimt = new DataMap();
		pamLimt.put("USER_ID", user_id);
		pamLimt.put("PRODUCT_ID", product_id);
		SQLParser parserLimt = new SQLParser(pamLimt);
		parserLimt.addSQL(" Select trade_id From tf_b_trade ");
		parserLimt.addSQL("Where 1=1 ");
		parserLimt.addSQL("and user_id=:USER_ID  ");
		parserLimt.addSQL("And trade_type_code in (SELECT t.attr_value FROM td_b_attr_biz t where t.id =:PRODUCT_ID  AND t.attr_obj = 'DstMb' AND t.attr_code = 'TradeTypeCode') ");
		return Dao.qryByParse(parserLimt);

	}

	// todo
	/**
	 * 根据当前的台账，查出同一订单下的其他台账
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOrderTradeByTrade(String TRADE_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", TRADE_ID);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select a.* from tf_b_trade a,TF_B_TRADE b ");
		parser.addSQL(" where a.cust_id = b.cust_id  ");
		parser.addSQL(" and a.product_id = b.product_id ");
		parser.addSQL(" and a.order_id = b.order_id ");
		parser.addSQL(" and b.trade_id =:TRADE_ID ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}

	/**
	 * 查询产品变更的预约工单
	 * 
	 * @param userId
	 * @param tradeType
	 * @return
	 * @throws Exception
	 */
	public static IData queryPreviousTrade(String userId, String tradeType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeType);
		IDataset result = Dao.qryByCode("TF_B_TRADE", "PRE_TRADE_SEL", param);

		return result.isEmpty() ? new DataMap() : result.getData(0);
	}

	/**
	 * 网上预约工单查询 获取网上预约工单资料列表
	 * 
	 * @param inparams
	 *            查询所需参数 地州编码
	 * @return IDataset 用户资料列表
	 * @throws Exception
	 */
	public static IDataset queryReserveInfo(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_B_TRADE_BOOK", "SEL_BY_MANY", inparams);
	}

	/**
	 * @description 判断是否有积分异动的未完工工单
	 * @param inparams
	 * @author huangsl
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryScoreChangeTrade(String serialNumber) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_SCORECHANGETRADE_BY_SN", inparams);
	}

	// todo
	public static IDataset queryTrade(String conn, String TRADE_ID, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", TRADE_ID);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT TO_CHAR(trade_id) trade_id, accept_month, TO_CHAR(batch_id) batch_id, TO_CHAR(order_id) order_id, TO_CHAR(prod_order_id) prod_order_id,");
		parser.addSQL(" bpm_id,  to_char(campn_id) campn_id, trade_type_code, priority, subscribe_type, subscribe_state, next_deal_tag, in_mode_code,");
		parser.addSQL(" TO_CHAR(cust_id) cust_id, cust_name, TO_CHAR(user_id) user_id, TO_CHAR(acct_id) acct_id, serial_number, net_type_code, eparchy_code,");
		parser.addSQL(" city_code, product_id, brand_code, TO_CHAR(accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date, trade_staff_id, trade_depart_id,");
		parser.addSQL(" trade_city_code, trade_eparchy_code, term_ip, TO_CHAR(oper_fee) oper_fee, TO_CHAR(foregift) foregift, TO_CHAR(advance_pay) advance_pay,");
		parser.addSQL(" invoice_no, fee_state, TO_CHAR(fee_time, 'yyyy-mm-dd hh24:mi:ss') fee_time, fee_staff_id, process_tag_set, olcom_tag,");
		parser.addSQL(" TO_CHAR(finish_date, 'yyyy-mm-dd hh24:mi:ss') finish_date, TO_CHAR(exec_time, 'yyyy-mm-dd hh24:mi:ss') exec_time, exec_action,");
		parser.addSQL(" exec_result, exec_desc, cancel_tag, TO_CHAR(cancel_date, 'yyyy-mm-dd hh24:mi:ss') cancel_date, cancel_staff_id, cancel_depart_id,");
		parser.addSQL(" cancel_city_code, cancel_eparchy_code, TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, update_staff_id, update_depart_id,");
		parser.addSQL(" remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10");
		parser.addSQL(" FROM tf_bh_trade");
		parser.addSQL(" WHERE 1 = 1");
		parser.addSQL(" AND trade_id = TO_NUMBER(:TRADE_ID)");
		parser.addSQL(" UNION ALL");
		parser.addSQL(" SELECT TO_CHAR(trade_id) trade_id, accept_month, TO_CHAR(batch_id) batch_id, TO_CHAR(order_id) order_id, TO_CHAR(prod_order_id) prod_order_id,");
		parser.addSQL(" bpm_id,  to_char(campn_id) campn_id, trade_type_code, priority, subscribe_type, subscribe_state, next_deal_tag, in_mode_code,");
		parser.addSQL(" TO_CHAR(cust_id) cust_id, cust_name, TO_CHAR(user_id) user_id, TO_CHAR(acct_id) acct_id, serial_number, net_type_code, eparchy_code,");
		parser.addSQL(" city_code, product_id, brand_code, TO_CHAR(accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date, trade_staff_id, trade_depart_id,");
		parser.addSQL(" trade_city_code, trade_eparchy_code, term_ip, TO_CHAR(oper_fee) oper_fee, TO_CHAR(foregift) foregift, TO_CHAR(advance_pay) advance_pay,");
		parser.addSQL(" invoice_no, fee_state, TO_CHAR(fee_time, 'yyyy-mm-dd hh24:mi:ss') fee_time, fee_staff_id, process_tag_set, olcom_tag,");
		parser.addSQL(" TO_CHAR(finish_date, 'yyyy-mm-dd hh24:mi:ss') finish_date, TO_CHAR(exec_time, 'yyyy-mm-dd hh24:mi:ss') exec_time, exec_action,");
		parser.addSQL(" exec_result, exec_desc, cancel_tag, TO_CHAR(cancel_date, 'yyyy-mm-dd hh24:mi:ss') cancel_date, cancel_staff_id, cancel_depart_id,");
		parser.addSQL(" cancel_city_code, cancel_eparchy_code, TO_CHAR(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, update_staff_id, update_depart_id,");
		parser.addSQL(" remark, rsrv_str1, rsrv_str2, rsrv_str3, rsrv_str4, rsrv_str5, rsrv_str6, rsrv_str7, rsrv_str8, rsrv_str9, rsrv_str10");
		parser.addSQL(" FROM tf_b_trade");
		parser.addSQL(" WHERE 1 = 1");
		parser.addSQL(" AND trade_id = TO_NUMBER(:TRADE_ID)");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据cust_id和product_id查询集团idc产品受理台账信息
	 * 
	 * @param cust_id
	 * @param product_id
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset QueryTrade(String cust_id, String product_id, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		// TODO param.put("TRADE_EPARCHY_CODE",
		// CSBizBean.getVisit().getStaffEparchyCode());
		// TODO param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("TRADE_TYPE_CODE", "3150");

		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_CUSTID_PRODUCTID", param, pagination, Route.CONN_CRM_CG);
	}

	/**
	 * 从台帐和历史台帐表中来判重
	 * 
	 * @param subOrderId
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeAndHistoryTrade(String tradeTypeCode, String subOrderId, String serialNumber) throws Exception
	{
		IData data = new DataMap();
		data.put("TRADE_TYPE_CODE", tradeTypeCode);
		data.put("SERIAL_NUMBER", serialNumber);
		data.put("SUB_ORDER_ID", subOrderId);

		return Dao.qryByCode("TF_B_TRADE", "SEL_IN_TRADE_BY_SERIAL_NUMBER", data, Route.getJourDb(CSBizBean.getTradeEparchyCode()));// modify
		// by
		// duhj
	}

	public static IDataset queryTradeByGroupCustId(IData param) throws Exception
	{

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_GROUP_CUSTID", param, Route.CONN_CRM_CG);
	}

	/**
	 * 根据订单编号和产品ID查找台账
	 * 
	 * @param orderId
	 * @param cancelTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeByOrderProduct(String orderId, String product_id, String cancelTag) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("PRODUCT_ID", product_id);
		param.put("CANCEL_TAG", cancelTag);
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT * FROM TF_B_TRADE ");
		buf.append(" WHERE 1=1 AND ORDER_ID = :ORDER_ID");
		buf.append(" AND PRODUCT_ID = :PRODUCT_ID");
		buf.append(" AND CANCEL_TAG = :CANCEL_TAG");
		return Dao.qryBySql(buf, param);
	}

	/**
	 * 根据订单编号查找台账
	 * 
	 * @param orderId
	 * @param cancelTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeByOrerId(String orderId, String cancelTag) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", cancelTag);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ORDER", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 根据ORDER_ID查询客户订单表
	 */
	public static IData getOrderByOrderId(String orderId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("ORDER_ID", orderId);
		inparams.put("CANCEL_TAG", "0");
		IDataset datas = Dao.qryByCode("TF_B_ORDER", "SEL_BY_ORDERID", inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));

		if (IDataUtil.isNotEmpty(datas))
		{
			return datas.getData(0);
		} else
		{
			return null;
		}
	}

	/**
	 * 根据集团服务号查询工单
	 * 
	 * @param trade_type_code
	 * @param serial_number
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeBySnGrp(String trade_type_code, String serial_number, Pagination pagination, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", trade_type_code);
		param.put("SERIAL_NUMBER", serial_number);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_GRP", param, pagination, Route.getJourDb(routeId));
	}

	/**
	 * 根据用户编码，业务类型、服务ID、状态用户服务状态订单数据
	 * 
	 * @param userId
	 * @param acceptMonth
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeBySvcIDAndStateCode(String userId, String tradeTypeCode, String serviceId, String stateCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("SERVICE_ID", serviceId);
		param.put("STATE_CODE", stateCode);
		return Dao.qryByCode("TF_B_TRADE", "SEL_COUNT_701", param);
	}

	/**
	 * 根据用户编码，受理月份获取用户服务状态订单数据
	 * 
	 * @param userId
	 * @param acceptMonth
	 * @return
	 * @throws Exception
	 *             TradeInfoQry
	 */
	public static IDataset queryTradeByUserAndAcceptMonth(String userId, String acceptMonth) throws Exception
	{
		IData param = new DataMap();
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_LAST_SVCSTATECHG_TRADE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	// todo 无SQL
	/**
	 * 作用：根据USERID,USERIDB,TradeTypeCode查询台账
	 * 
	 * @param inData
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeByUserIdAB(IData inData, String eparchyCode) throws Exception
	{

		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_BY_USERIDAB", inData, eparchyCode);
	}

	public static IDataset queryTradeInfo(IData inparam, Pagination pagination) throws Exception
	{

		inparam.put("TRADE_TYPE_CODE", "0");
		// if (inparam.getString("START_DATE", "").equals(""))
		// common.error("545002:起始时间不能为空！");

		StringBuilder sql = new StringBuilder();
		sql.append("(SELECT ");
		sql.append("a.trade_id, ");
		sql.append("a.serial_number, ");
		sql.append("a.trade_type_code, ");
		sql.append("(case when nvl(o.order_instance_state,0)='0'    then '未启动'                                         else ");
		sql.append("(case when nvl(o.order_instance_state,0)='R'   then 'PBOSS已竣工，延迟修改资料'                         else ");
		sql.append("(case when b.pbosspf_return_code='-1'       then '调PBOSS服务开通失败'                                 else ");
		sql.append("(case when b.done_intf_return_code='-1'     then '执行完工流程失败'                                        else ");
		sql.append("(case when c.PF_RESULTCODE='0'  and c.PFRTN_RESULTCODE is null then '等待PBOSS服务开通返回'            else ");
		sql.append("(case when c.PFRTN_RESULTCODE='1'           then '待装'                                                else ");
		sql.append("(case when c.PFRTN_RESULTCODE='-1'          then 'PBOSS服务开通失败'                                   else ");
		sql.append("(case when r.TYPE='1'                       then '依赖订单未完成'                                      else ");
		sql.append("'其他错误'     end) end) end) end)end) end) end) end)  trade_state, ");
		sql.append("a.cancel_tag, ");
		sql.append("to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss')   accept_date, ");
		sql.append("a.trade_staff_id, ");
		sql.append("a.trade_depart_id ");
		sql.append("FROM tf_b_trade  a,tf_b_trademgr_instance b ,tf_b_trade_pboss_finish c ,tf_b_order o ,TF_B_TRADE_RELA r ");
		sql.append("WHERE (a.trade_type_code=:TRADE_TYPE_CODE OR TO_NUMBER(:TRADE_TYPE_CODE)=0) ");
		sql.append("and (a.serial_number=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL) ");
		sql.append("and a.accept_date >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) ");
		sql.append("and (a.accept_date <TRUNC(TO_DATE(:FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1 OR :FINISH_DATE IS NULL) ");
		sql.append("and (a.trade_depart_id=:TRADE_DEPART_ID OR :TRADE_DEPART_ID IS NULL) ");
		sql.append("and (a.trade_staff_id=:TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL) ");
		sql.append("and (a.TRADE_EPARCHY_CODE=:TRADE_EPARCHY_CODE or 'SUPERUSR' = :STAFF_ID) ");
		sql.append("and (a.TRADE_ID=:TRADE_ID OR :TRADE_ID IS NULL) ");
		sql.append("and a.subscribe_type in (0,300) and a.trade_id=b.trade_id(+) and a.trade_id=c.trade_id(+)  and a.order_id=o.order_id  ");
		sql.append("and a.trade_id=r.trade_id_a(+))");
		sql.append("UNION ALL ");
		sql.append("(SELECT ");
		sql.append("a.trade_id, ");
		sql.append("a.serial_number, ");
		sql.append("a.trade_type_code, ");
		sql.append("'已归档' trade_state, ");
		sql.append("a.cancel_tag, ");
		sql.append("to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date, ");
		sql.append("a.trade_staff_id, ");
		sql.append("a.trade_depart_id ");
		sql.append("FROM tf_bh_trade a ");
		sql.append("WHERE (trade_type_code=:TRADE_TYPE_CODE OR TO_NUMBER(:TRADE_TYPE_CODE)=0) ");
		sql.append("and (a.serial_number=:SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL) ");
		sql.append("and accept_date >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')) ");
		sql.append("and (accept_date <TRUNC(TO_DATE(:FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1 OR :FINISH_DATE IS NULL) ");
		sql.append("and (trade_depart_id=:TRADE_DEPART_ID OR :TRADE_DEPART_ID IS NULL) ");
		sql.append("and (trade_staff_id=:TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL) ");
		sql.append("and (TRADE_EPARCHY_CODE=:TRADE_EPARCHY_CODE or 'SUPERUSR' = :STAFF_ID) ");
		sql.append("and (TRADE_ID=:TRADE_ID OR :TRADE_ID IS NULL) ");
		sql.append("and a.subscribe_type in (0,300))");
		sql.append("order by accept_date desc ");
		IDataset lastResults = new DatasetList();
		IDataset results = Dao.qryBySql(sql, inparam, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		int n = results.size();
		if (IDataUtil.isNotEmpty(results))
		{
			for (int i = 0; i < n; i++)
			{
				String rsrvtag1 = "";
				IDataset staffset = StaffInfoQry.qryStaffInfoByStaffId(results.getData(i).getString("TRADE_STAFF_ID"));
				if (IDataUtil.isNotEmpty(staffset))
				{
					rsrvtag1 = staffset.getData(0).getString("RSRV_TAG1");
					if ("1".equals(rsrvtag1))
					{
						lastResults.add(results.getData(i));
					}
				}
			}
		}
		return lastResults;
	}

	// todo 无SQL
	/**
	 * 查询trade信息
	 */
	public static IData queryTradeInfo(String tradeId) throws Exception
	{

		IData trade = null;
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		IDataset tradeSet = Dao.qryByCode("TF_B_TRADE", "SEL_FOR_COMMEND", param);
		if (tradeSet != null && tradeSet.size() > 0)
		{
			trade = tradeSet.getData(0);
		}
		return trade;
	}

	/**
	 * 根据表名和tradeID查找所在台账表的记录
	 * 
	 * @param tradeId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeInfoByTradeIdAndTableName(String tradeId, String tableName) throws Exception
	{

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

		StringBuilder sql = new StringBuilder(3000);

		sql.append(" SELECT T.* FROM ");
		sql.append(" " + tableName + " ");
		sql.append(" T ");
		sql.append(" WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		sql.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");

		return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * @Function: queryTradeMainHisByUserId
	 * @Description: 业务台帐主表信息查询
	 * @param: @param serialNumber
	 * @param: @param eparchyCode
	 * @param: @param userId
	 * @param: @param tradeTypeCode
	 * @param: @param startDate
	 * @param: @param endDate
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 11:33:31 AM Jul 27, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* Jul 27,
	 *        2013 longtian3 v1.0.0 TODO:
	 */
	public static IDataset queryTradeMainHisByUserId(String serialNumber, String eparchyCode, String userId, String tradeTypeCode, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_MAINHIS_TRADE", param);
	}

	/**
	 * 根据TRADE_TYPE_CODE 和 USER_ID 查询 业务受理日志
	 * 
	 * @param tradeTypeCode
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeMainHisInfo(String tradeTypeCode, String userId, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("USER_ID", userId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TF_B_AND_BH_BY_USERID", param, Route.getJourDb());

	}

	/**
	 * 根据TRADE_TYPE_CODE 和 USER_ID 查询 业务受理日志 不包含select * from
	 * ucr_cen1.td_s_static a where a.type_id = 'USER360VIEW_TRADENOQUERY';业务
	 * 
	 * @param tradeTypeCode
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeMainHisInfo2(String tradeTypeCode, String userId, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("USER_ID", userId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TF_B_AND_BH_BY_USERID2", param, Route.getJourDb());

	}

	/**
	 * 根据USER_ID,TRADE_TYPE_CODE查询未完工工单
	 * 
	 * @author tengg
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeNotFinish(String trade_type_code, String user_id, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", trade_type_code);
		param.put("USER_ID", user_id);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_USER_TRADETYPECODE", param, pagination, Route.getJourDb(BizRoute.getRouteId()));
	}

	// todo
	/**
	 * 未完工移动总机成员注销工单
	 * 
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeNotFinishBy2437(String USER_ID) throws Exception
	{
		IData pamLimt = new DataMap();
		pamLimt.put("USER_ID", USER_ID);
		SQLParser parserLimt = new SQLParser(pamLimt);
		parserLimt.addSQL(" Select trade_id From tf_b_trade ");
		parserLimt.addSQL("Where 1=1 ");
		parserLimt.addSQL("and user_id=:USER_ID  ");
		parserLimt.addSQL("And trade_type_code='2437' ");
		return Dao.qryByParse(parserLimt, Route.getJourDb());
	}

	// todo
	/**
	 * 未完工集团彩铃注销工单
	 * 
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeNotFinishBy2447(String USER_ID) throws Exception
	{
		IData pamLimt = new DataMap();
		pamLimt.put("USER_ID", USER_ID);
		SQLParser parserLimt = new SQLParser(pamLimt);
		parserLimt.addSQL(" Select trade_id From tf_b_trade ");
		parserLimt.addSQL("Where 1=1 ");
		parserLimt.addSQL("and user_id=:USER_ID  ");
		parserLimt.addSQL("And trade_type_code='2447' ");
		return Dao.qryByParse(parserLimt);
	}

	// todo
	/**
	 * 未完的集团VPMN成员注销工单
	 * 
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeNotFinishBy2527(String USER_ID) throws Exception
	{
		IData pamLimt = new DataMap();
		pamLimt.put("USER_ID", USER_ID);
		SQLParser parserLimt = new SQLParser(pamLimt);
		parserLimt.addSQL(" Select trade_id From tf_b_trade ");
		parserLimt.addSQL("Where 1=1 ");
		parserLimt.addSQL("and user_id=:USER_ID  ");
		parserLimt.addSQL("And trade_type_code='2527' ");
		return Dao.qryByParse(parserLimt);

	}

	/**
	 * 未完工农业信通业务工单
	 * 
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeNotFinishBy2837(String USER_ID) throws Exception
	{
		IData pamLimt = new DataMap();
		pamLimt.put("USER_ID", USER_ID);
		SQLParser parserLimt = new SQLParser(pamLimt);
		parserLimt.addSQL(" Select trade_id From tf_b_trade ");
		parserLimt.addSQL("Where 1=1 ");
		parserLimt.addSQL("and user_id=:USER_ID  ");
		parserLimt.addSQL("And (trade_type_code='2837' or trade_type_code='2834') ");
		return Dao.qryByParse(parserLimt);
	}

	// todo
	/**
	 * 查询用户为完成注销工单
	 * 
	 * @author fengsl
	 * @date 2013-02-26
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */

	public static IDataset queryTradeNotFinishByUserID(String USER_ID) throws Exception
	{
		IData pamLimt = new DataMap();
		pamLimt.put("USER_ID", USER_ID);
		SQLParser parserLimt = new SQLParser(pamLimt);
		parserLimt.addSQL(" Select trade_id From tf_b_trade ");
		parserLimt.addSQL("Where 1=1 ");
		parserLimt.addSQL("and user_id=:USER_ID  ");
		parserLimt.addSQL("And trade_type_code='2527' ");
		return Dao.qryByParse(parserLimt, Route.CONN_CRM_CG);
	}

	/**
	 * 根据USER_ID,TRADE_TYPE_CODE查询未完工工单 查询用户未完工工单
	 * 
	 * @param trade_type_code
	 * @param user_id
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeNotFinishForCredit(String trade_type_code, String user_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", trade_type_code);
		param.put("USER_ID", user_id);
		return Dao.qryByCode("TF_B_TRADE", "SEL_GRP_NOTFINISH_FORGRPCREDIT", param, pagination);
	}

	// todo
	/**
	 * 查工单
	 * 
	 * @param pamLimt
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeSet(String trade_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		return Dao.qryByCode("TF_B_TRADE", "SELECT_ALL_BY_TRADEID", param, pagination, Route.getJourDb());
	}

	public static IDataset queryTradeByTradeId(String trade_id, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		return Dao.qryByCode("TF_B_TRADE", "SELECT_ALL_BY_TRADEID", param, Route.getJourDb(routeId));
	}

	/**
	 * 执行校园宽带费用转移查询
	 * 
	 * @author chenzm
	 * @param accept_month
	 * @param start_staff_id
	 * @param end_staff_id
	 * @param start_date
	 * @param end_date
	 * @param trans_type
	 * @param campus_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTransFee(String start_staff_id, String end_staff_id, String start_date, String end_date, String trans_type, String campus_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("START_STAFF_ID", start_staff_id);
		param.put("END_STAFF_ID", end_staff_id);
		param.put("START_DATE", start_date);
		param.put("END_DATE", end_date);
		param.put("TRANS_TYPE", trans_type);
		param.put("CAMPUS_ID", campus_id);
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_CAMPUS_BY_DATE_STAFFID_QRY", param, pagination);
	}

	/**
	 * 未完工工单查询 获取未完工业务资料列表
	 * 
	 * @param inparam
	 *            查询所需参数 地州编码
	 * @return IDataset 用户资料列表
	 * @throws Exception
	 */
	public static IDataset queryUnfinishInfo(String SERIAL_NUMBER, String START_DATE, String FINISH_DATE, String TRADE_DEPART_ID, String TRADE_STAFF_ID, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		inparam.put("TRADE_TYPE_CODE", "0");
		inparam.put("SERIAL_NUMBER", SERIAL_NUMBER);
		inparam.put("START_DATE", START_DATE);
		inparam.put("FINISH_DATE", FINISH_DATE);
		inparam.put("TRADE_DEPART_ID", TRADE_DEPART_ID);
		inparam.put("TRADE_STAFF_ID", TRADE_STAFF_ID);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ACCEPT_HX", inparam, pagination, Route.getJourDb());
	}

	/**
	 * 未完工工单查询 获取未完工业务资料列表
	 * 
	 * @param inparam
	 *            查询所需参数 地州编码
	 * @return IDataset 用户资料列表
	 * @throws Exception
	 */
	public static IDataset queryUnfinishInfo(String SERIAL_NUMBER, String START_DATE, String FINISH_DATE, String TRADE_DEPART_ID, String TRADE_STAFF_ID, Pagination pagination, String route) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		inparam.put("TRADE_TYPE_CODE", "0");
		inparam.put("SERIAL_NUMBER", SERIAL_NUMBER);
		inparam.put("START_DATE", START_DATE);
		inparam.put("FINISH_DATE", FINISH_DATE);
		inparam.put("TRADE_DEPART_ID", TRADE_DEPART_ID);
		inparam.put("TRADE_STAFF_ID", TRADE_STAFF_ID);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ACCEPT_HX", inparam, pagination, Route.getJourDb(route));
	}

	/**
	 * 未完工工单查询 获取未完工业务资料列表
	 * 
	 * @param inparam
	 *            查询所需参数 地州编码
	 * @return IDataset 用户资料列表
	 * @throws Exception
	 */
	public static IDataset queryUnfinishInfoCC(String SERIAL_NUMBER, String START_DATE, String FINISH_DATE, String TRADE_DEPART_ID, String TRADE_STAFF_ID, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		// inparam.put("TRADE_EPARCHY_CODE",
		// CSBizBean.getVisit().getStaffEparchyCode());
		inparam.put("TRADE_TYPE_CODE", "0");
		inparam.put("SERIAL_NUMBER", SERIAL_NUMBER);
		inparam.put("START_DATE", START_DATE);
		inparam.put("FINISH_DATE", FINISH_DATE);
		inparam.put("TRADE_DEPART_ID", TRADE_DEPART_ID);
		inparam.put("TRADE_STAFF_ID", TRADE_STAFF_ID);

		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ACCEPT_HXCC", inparam, pagination);
	}

	/**
	 * 查询用户可取消的业务
	 * 
	 * @param serialNumber
	 * @param subscribeType
	 * @param tradeEparchyCode
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserCancelTrade(String serialNumber, String subscribeType, String tradeEparchyCode, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", serialNumber);
		inparam.put("SUBSCRIBE_TYPE", subscribeType);
		inparam.put("TRADE_EPARCHY_CODE", tradeEparchyCode);

		return Dao.qryByCode("TF_B_TRADE", "SEL_CANCELINFO_BY_SN", inparam, pagination, Route.getJourDb());
	}

	// todo
	/** 查询用户的可返销业务 */
	public static IDataset queryUserCancelTradeByBhTrade(String SERIAL_NUMBER, String TRADE_TYPE_CODE) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", SERIAL_NUMBER);
		param.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT *");
		parser.addSQL("   FROM tf_bh_trade WHERE SUBSTR(trade_id,3) =");
		parser.addSQL("     (select MAX(SUBSTR(a.trade_id,3))");
		parser.addSQL("        FROM tf_bh_trade a, td_s_tradetype b");
		parser.addSQL("       WHERE a.serial_number = :SERIAL_NUMBER");
		parser.addSQL("         AND a.cancel_tag = '0'");
		parser.addSQL("         AND a.trade_type_code = :TRADE_TYPE_CODE");
		parser.addSQL("         AND b.trade_type_code = a.trade_type_code");
		parser.addSQL("         AND b.back_tag = '1'");
		parser.addSQL("         AND b.eparchy_code = a.eparchy_code");
		parser.addSQL("         AND SYSDATE BETWEEN b.start_date AND b.end_date)");
		parser.addSQL("     AND serial_number=:SERIAL_NUMBER AND cancel_tag='0' AND ROWNUM<2");

		return Dao.qryByParse(parser, Route.getJourDb());
	}

	public static IDataset queryWidenetUserOpenTradeid(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		StringBuilder buidler = new StringBuilder();
		buidler.append(" Select trade_id From tf_b_trade ");
		buidler.append("Where SERIAL_NUMBER = :SERIAL_NUMBER ");
		buidler.append("And trade_type_code in ('600','612','613') And Cancel_Tag = '0'");
		return Dao.qryBySql(buidler, param, Route.getJourDb());
	}

	/**
	 * 修改订单状态
	 * 
	 * @author ft
	 * @param trade_type_code
	 * @param accept_month
	 * @param cancel_tag
	 * @param rsrv_str1
	 * @return
	 * @throws Exception
	 */
	public static void updateSubstate(String trade_id, String subscribe_state) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SUBSCRIBE_STATE", subscribe_state);

		StringBuilder sql = new StringBuilder(100);

		sql.append("UPDATE TF_B_TRADE T ");
		sql.append("SET T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE, ");
		sql.append("T.olcom_tag       = '1', ");
		sql.append("T.update_time     = SYSDATE ");
		sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		sql.append("AND t.accept_month= TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");

		Dao.executeUpdate(sql, param, Route.getJourDb());
	}

	/**
	 * 更新主台帐表的intf_id chenyi 2014-3-4
	 * 
	 * @param inparam
	 * @throws Exception
	 */
	public static void updateTradeIntfID(IData inparam) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPDATE_INTFID_BYTRADEID", inparam, Route.getJourDb());
	}

	/**
	 * 预受理转受理报文发送，更新rsrv_str8 和 rsrv_str10
	 * 
	 * @param inparams
	 * @throws Exception
	 */
	public static void updateTradeRsrvStr10(IData inparams) throws Exception
	{
		StringBuilder sql = new StringBuilder(100);

		sql.append("UPDATE tf_b_trade ");
		sql.append("SET RSRV_STR8 = :RSRV_STR8, RSRV_STR10 = :RSRV_STR10 ");
		sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");

		Dao.executeUpdate(sql, inparams, Route.getJourDb());
	}

	public static void updateTradeState(String trade_id, String subscribe_type) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("SUBSCRIBE_TYPE", subscribe_type);

		StringBuilder sql = new StringBuilder(100);

		sql.append("UPDATE tf_b_trade ");
		sql.append("SET SUBSCRIBE_TYPE = :SUBSCRIBE_TYPE");
		sql.append("WHERE TRADE_ID = :TRADE_ID and ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");

		Dao.executeUpdate(sql, param, Route.getJourDb());
	}

	/**
	 * 根据主键修改台账状态
	 * 
	 * @description
	 * @author chenyi
	 * @date Apr 12, 2010
	 * @version 1.0.0
	 * @param pd
	 * @param inparams
	 * @throws Exception
	 */
	public static void updateTradeStateByPK(String order_id, String subscribe_state) throws Exception
	{

		IData param = new DataMap();
		param.put("ORDER_ID", order_id);
		param.put("SUBSCRIBE_STATE", subscribe_state);
		Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_SUBSCRIBE_BYORDER_ID", param);
	}

	public static int updateTradeStateByTradeId(String tradeId, String acceptMonth, String cancelTag, String subscribeState, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("CANCEL_TAG", cancelTag);
		param.put("SUBSCRIBE_STATE", subscribeState);

		StringBuilder sql = new StringBuilder(200);

		sql.append("UPDATE TF_B_TRADE T ");
		sql.append("SET T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE, T.UPDATE_TIME = SYSDATE ");
		sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		sql.append("AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
		sql.append("AND T.CANCEL_TAG = :CANCEL_TAG ");

		return Dao.executeUpdate(sql, param, Route.getJourDb(routeId));
	}

	public static IDataset qryNoPrintTrade(String sn, String tradeId, String staffId, String startDate, String endDate, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		param.put("TRADE_ID", tradeId);
		param.put("TRADE_STAFF_ID", staffId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("CANCEL_TAG", cancelTag);

		StringBuilder sql = new StringBuilder(500);
		sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.EPARCHY_CODE,A.TRADE_TYPE_CODE,A.CUST_ID ");
		sql.append("  FROM TF_B_TRADE A ");
		sql.append(" WHERE A.SUBSCRIBE_TYPE <> '600' ");
		sql.append("   and A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		sql.append("   and A.ACCEPT_DATE + 0 > TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
		sql.append("   and A.ACCEPT_DATE + 0 < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
		sql.append("   and A.CANCEL_TAG = :CANCEL_TAG ");
		if (StringUtils.isNotBlank(staffId))
		{
			sql.append("   and A.TRADE_STAFF_ID = :TRADE_STAFF_ID ");
		}
		if (StringUtils.isNotBlank(tradeId))
		{
			sql.append("   and A.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		}

		return Dao.qryBySql(sql, param, Route.getJourDb(routeId));
	}

	public static IDataset qryNoPrintHisTrade(String sn, String tradeId, String staffId, String startDate, String endDate, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		param.put("TRADE_ID", tradeId);
		param.put("TRADE_STAFF_ID", staffId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("CANCEL_TAG", cancelTag);

		StringBuilder sql = new StringBuilder(500);
		sql.append("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, A.EPARCHY_CODE,A.TRADE_TYPE_CODE,A.CUST_ID ");
		sql.append("  FROM TF_BH_TRADE A ");
		sql.append(" WHERE A.SUBSCRIBE_TYPE <> '600' ");
		sql.append("   and A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		sql.append("   and A.ACCEPT_DATE + 0 > TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
		sql.append("   and A.ACCEPT_DATE + 0 < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
		sql.append("   and A.CANCEL_TAG = :CANCEL_TAG ");
		if (StringUtils.isNotBlank(staffId))
		{
			sql.append("   and A.TRADE_STAFF_ID = :TRADE_STAFF_ID ");
		}
		if (StringUtils.isNotBlank(tradeId))
		{
			sql.append("   and A.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
		}

		return Dao.qryBySql(sql, param, Route.getJourDb(routeId));
	}

	/** 根据trade_id查询tf_b_trade和tf_bh_trade主台帐信息 */
	public static IDataset queryUserTradeByBTradeAndBhTrade(String TRADE_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", TRADE_ID);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT TO_CHAR(a.trade_id) trade_id, a.accept_month, TO_CHAR(a.batch_id) batch_id, TO_CHAR(a.order_id) order_id, TO_CHAR(a.prod_order_id) prod_order_id, a.bpm_id, TO_CHAR(a.campn_id) campn_id, a.trade_type_code, a.priority, a.subscribe_type, a.subscribe_state, a.next_deal_tag, a.in_mode_code, TO_CHAR(a.cust_id) cust_id, a.cust_name, TO_CHAR(a.user_id) user_id, TO_CHAR(a.acct_id) acct_id, a.serial_number, a.net_type_code, a.eparchy_code, a.city_code, a.product_id, a.brand_code, TO_CHAR(a.cust_id_b) cust_id_b, TO_CHAR(a.user_id_b) user_id_b, TO_CHAR(a.acct_id_b) acct_id_b, a.serial_number_b, a.cust_contact_id, a.serv_req_id, a.intf_id, TO_CHAR(a.accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date, a.trade_staff_id, a.trade_depart_id, a.trade_city_code, a.trade_eparchy_code, a.term_ip, TO_CHAR(a.oper_fee) oper_fee, TO_CHAR(a.foregift) foregift, TO_CHAR(a.advance_pay) advance_pay, a.invoice_no, a.fee_state, TO_CHAR(a.fee_time, 'YYYY-MM-DD HH24:MI:SS') fee_time, a.fee_staff_id, a.process_tag_set, a.olcom_tag, TO_CHAR(a.finish_date, 'YYYY-MM-DD HH24:MI:SS') finish_date, TO_CHAR(a.exec_time, 'YYYY-MM-DD HH24:MI:SS') exec_time, a.exec_action, a.exec_result, a.exec_desc, a.cancel_tag, TO_CHAR(a.cancel_date, 'YYYY-MM-DD HH24:MI:SS') cancel_date, a.cancel_staff_id, a.cancel_depart_id, a.cancel_city_code, a.cancel_eparchy_code, TO_CHAR(a.update_time, 'YYYY-MM-DD HH24:MI:SS') update_time, a.update_staff_id, a.update_depart_id, a.remark, a.rsrv_str1, a.rsrv_str2, a.rsrv_str3, a.rsrv_str4, a.rsrv_str5, a.rsrv_str6, a.rsrv_str7, a.rsrv_str8, a.rsrv_str9, a.rsrv_str10 ");
		parser.addSQL(" FROM tf_b_trade a WHERE 1=1 ");
		parser.addSQL(" AND a.trade_id = TO_NUMBER(:TRADE_ID) ");
		parser.addSQL(" UNION ");
		parser.addSQL(" SELECT TO_CHAR(b.trade_id) trade_id, b.accept_month, TO_CHAR(b.batch_id) batch_id, TO_CHAR(b.order_id) order_id, TO_CHAR(b.prod_order_id) prod_order_id, b.bpm_id, TO_CHAR(b.campn_id) campn_id, b.trade_type_code, b.priority, b.subscribe_type, b.subscribe_state, b.next_deal_tag, b.in_mode_code, TO_CHAR(b.cust_id) cust_id, b.cust_name, TO_CHAR(b.user_id) user_id, TO_CHAR(b.acct_id) acct_id, b.serial_number, b.net_type_code, b.eparchy_code, b.city_code, b.product_id, b.brand_code, TO_CHAR(b.cust_id_b) cust_id_b, TO_CHAR(b.user_id_b) user_id_b, TO_CHAR(b.acct_id_b) acct_id_b, b.serial_number_b, b.cust_contact_id, b.serv_req_id, b.intf_id, TO_CHAR(b.accept_date, 'YYYY-MM-DD HH24:MI:SS') accept_date, b.trade_staff_id, b.trade_depart_id, b.trade_city_code, b.trade_eparchy_code, b.term_ip, TO_CHAR(b.oper_fee) oper_fee, TO_CHAR(b.foregift) foregift, TO_CHAR(b.advance_pay) advance_pay, b.invoice_no, b.fee_state, TO_CHAR(b.fee_time, 'YYYY-MM-DD HH24:MI:SS') fee_time, b.fee_staff_id, b.process_tag_set, b.olcom_tag, TO_CHAR(b.finish_date, 'YYYY-MM-DD HH24:MI:SS') finish_date, TO_CHAR(b.exec_time, 'YYYY-MM-DD HH24:MI:SS') exec_time, b.exec_action, b.exec_result, b.exec_desc, b.cancel_tag, TO_CHAR(b.cancel_date, 'YYYY-MM-DD HH24:MI:SS') cancel_date, b.cancel_staff_id, b.cancel_depart_id, b.cancel_city_code, b.cancel_eparchy_code, TO_CHAR(b.update_time, 'YYYY-MM-DD HH24:MI:SS') update_time, b.update_staff_id, b.update_depart_id, b.remark, b.rsrv_str1, b.rsrv_str2, b.rsrv_str3, b.rsrv_str4, b.rsrv_str5, b.rsrv_str6, b.rsrv_str7, b.rsrv_str8, b.rsrv_str9, b.rsrv_str10 ");
		parser.addSQL(" FROM tf_bh_trade b WHERE 1=1 ");
		parser.addSQL(" AND b.trade_id = TO_NUMBER(:TRADE_ID) ");

		// return Dao.qryByParse(parser); add by hefeng 订单中心改造
		return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
	}

	public static IDataset getGrpBBossProductTradeByGrpCustId(String custId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("CUST_ID", custId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_GRPBBOSS_PRODUCT_BY_GRP_CUSTID", inparams, Route.getJourDb());
	}

	/**
	 * @Title: updOlcomTagByTradeIdCancelTag
	 * @Description: 根据tradeid和cancelTag来更新olcomtag
	 * @param tradeId
	 * @param cancelTag
	 * @throws Exception
	 * @return void
	 * @author chenkh
	 * @time 2015年7月14日
	 */
	public static void updOlcomTagByTradeIdCancelTag(String tradeId, String cancelTag) throws Exception
	{
		IData param = new DataMap();

		param.put("CANCEL_TAG", cancelTag);
		param.put("TRADE_ID", tradeId);

		Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_OLCOM_BY_PK", param, Route.getJourDb());
	}

	/**
	 * 根据USER_ID,TRADE_TYPE_CODE查询最近的一条记录
	 */
	public static IDataset getHisMainTradeLast(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BH_TRADE_LAST", inparam, Route.getJourDb());
	}

	/**
	 * 根据tradeId和业务类型查询工单信息
	 * 
	 * @param tradeId
	 * @param tradeTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IData queryTradeByTradeIdAndTradeType(String tradeId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);

		IDataset set = Dao.qryByCodeParser("TF_B_TRADE", "QRY_TRADE_BY_TRADE_ID_AND_TRADE_TYPE", param, Route.getJourDb());

		if (IDataUtil.isEmpty(set))
		{
			return null;
		} else
		{
			return set.getData(0);
		}
	}

	/**
	 * 
	 * @param productId
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGFSPUUCountByProductId(String productId, String custId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("PRODUCT_ID", productId);
		inparams.put("CUST_ID_B", custId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_COUNT_GFSPUU_BYPRODUCT_ID", inparams, Route.getJourDb());
	}

	/**
	 * 
	 * @param productId
	 * @param userId
	 * @param execTime
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryCreditTradeInfo(String userId, String execTime) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EXEC_TIME", execTime);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BY_EXEC_TIME", param, Route.getJourDb());
	}

	/**
	 * 
	 * @param productId
	 * @param userId
	 * @param execTime
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryCreditOrderInfo(String userId, String execTime) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EXEC_TIME", execTime);
		return Dao.qryByCode("TF_B_ORDER", "SEL_ORDER_BY_EXEC_TIME", param, Route.getJourDb());
	}

	public static IDataset query130Oper(String acceptMonth, String userId, String serviceId, String operCode, String sendFlag) throws Exception
	{
		IData param = new DataMap();
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);
		param.put("OPER_CODE", operCode);
		param.put("SEND_FLAG", sendFlag);

		return Dao.qryByCode("TF_BH_TRADE", "QRY_130_SUSPEND_OPER", param, Route.getJourDb());
	}

	public static void updateTradeIntfId(String tradeId, String addIntfId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("INTF_ID", addIntfId);

		Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_INTFID", param, Route.getJourDb());
	}

	public static IDataset queryWidenetCancelTrade(IData param, Pagination pagination) throws Exception
	{
		return Dao.qryByCodeParser("TF_BH_TRADE", "QRY_WIDENET_CANCEL_TRADES", param, pagination, Route.getJourDb());
	}

	public static IDataset queryOpeningTopsetboxTrade(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_TOP_SET_BOX_TRADE_OPEN", param, Route.getJourDb());
	}

	public static IDataset qryAccountScoreTradeInfo(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);

		return Dao.qryByCode("TF_B_TRADE", "QRY_SCORE_TRADE_ACCOUNT", param);
	}

	/**
	 * 
	 * @param orderId
	 * @param userId
	 * @author kangyt
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTradeByOrderIdTradeType(String userId, String orderId, String tradetypecode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("ORDER_ID", orderId);
		param.put("TRADE_TYPE_CODE", tradetypecode);
		param.put("CANCEL_TAG", "0");
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ORDERID_TRADETYPE", param, Route.getJourDb());
	}

	/**
	 * @author kangyt
	 * @param orderId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTradeByOrderIdAll(String userId, String orderId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", "0");
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ORDERID_ALL", param, Route.getJourDb());
	}

	/**
	 * 根据trade_type_code,accept_month,serial_number
	 * 查询tf_b_trade和tf_bh_trade主台帐信息
	 */
	public static IDataset queryTradeBySnAndTypeCode(IData inparam) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append("	SELECT trade_id,order_id,bpm_id,trade_type_code,in_mode_code,priority,subscribe_state,next_deal_tag,product_id,brand_code,user_id,cust_id,acct_id,serial_number,cust_name,accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,exec_time,finish_date,oper_fee,foregift,advance_pay,invoice_no,fee_state,fee_time,fee_staff_id,cancel_tag,cancel_date,cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark ");
		sql.append("	  FROM tf_b_trade ");
		sql.append("	 WHERE serial_number=:SERIAL_NUMBER ");
		sql.append("	   AND cancel_tag= :CANCEL_TAG ");
		sql.append("	   AND trade_type_code = :TRADE_TYPE_CODE ");
		sql.append("	   AND (accept_month = :ACCEPT_MONTH or :ACCEPT_MONTH is null) ");
		sql.append("	union all ");
		sql.append("	SELECT trade_id,order_id,bpm_id,trade_type_code,in_mode_code,priority,subscribe_state,next_deal_tag,product_id,brand_code,user_id,cust_id,acct_id,serial_number,cust_name,accept_date,accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,exec_time,finish_date,oper_fee,foregift,advance_pay,invoice_no,fee_state,fee_time,fee_staff_id,cancel_tag,cancel_date,cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark ");
		sql.append("	  FROM tf_bh_trade ");
		sql.append("	 WHERE serial_number=:SERIAL_NUMBER ");
		sql.append("	   AND cancel_tag= :CANCEL_TAG ");
		sql.append("	   AND trade_type_code = :TRADE_TYPE_CODE ");
		sql.append("	   AND (accept_month = :ACCEPT_MONTH or :ACCEPT_MONTH is null) ");
		return Dao.qryBySql(sql, inparam, Route.getJourDb());
	}

	public static void updTradeRsrvStr10(String tradeId, String rsrvStr10) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("RSRV_STR10", rsrvStr10);

		Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_RSRV_10", param, Route.getJourDb());
	}

	/**
	 * 根据业务类型和证件号码查询TF_B_TRADE记录
	 * 
	 * @param tradeTypeCode
	 * @param rsrvStr1
	 *            存放的证件号码
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public static IDataset queryTradeInfoByCodeAndRsrv1(String tradeTypeCode, String rsrvStr1) throws Exception
	{
		IData param = new DataMap();
		param.put("RSRV_STR1", rsrvStr1);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BY_TYPE_CODE_RSRV1", param, Route.getJourDb());
	}

	public static IDataset queryTradeByRsrvstr1(IData inparam) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append(" select p.user_id from tf_b_trade p  ");
		sql.append("    where p.RSRV_STR1=:SERV_CODE ");
		return Dao.qryBySql(sql, inparam, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static IDataset qryTradesByStaffIdDate(String tradeStaffId, String startDate, String endDate, String subscriberState, String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_STAFF_ID", tradeStaffId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		param.put("SUBSCRIBE_STATE", subscriberState);

		StringBuilder sql = new StringBuilder(500);
		sql.append(" SELECT B.RSRV_STR10,A.* FROM TF_B_TRADE A,TF_B_ORDER B ");
		sql.append(" WHERE A.ORDER_ID = B.ORDER_ID AND A.TRADE_STAFF_ID = :TRADE_STAFF_ID ");
		sql.append(" AND A.ACCEPT_DATE BETWEEN TO_DATE(:START_DATE,'YYYY-MM-DD') AND TO_DATE(:END_DATE,'YYYY-MM-DD')+1 ");
		sql.append(" AND A.SUBSCRIBE_STATE =:SUBSCRIBE_STATE ");
		sql.append(" AND A.CANCEL_TAG ='0' ");
		if (StringUtils.isNotBlank(serialNumber))
		{
			param.put("SERIAL_NUMBER", serialNumber);
			sql.append(" AND SERIAL_NUMBER = :SERIAL_NUMBER  ");
		}

		return Dao.qryBySql(sql, param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	public static int updateTradeByTradeId(String tradeId, String acceptMonth, String cancelTag, String staffId, String departId, String remark) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("CANCEL_TAG", cancelTag);
		param.put("UPDATE_STAFF_ID", staffId);
		param.put("UPDATE_DEPART_ID", departId);
		param.put("REMARK", remark);

		return Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_BY_TRADE_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	public static IDataset queryTradeBhByTradeIdAndTypeCode(String tradeId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);

		StringBuilder sql = new StringBuilder();
		sql.append(" select t.* from tf_bh_trade t  ");
		sql.append(" where t.TRADE_ID = :TRADE_ID ");
		sql.append(" and t.trade_type_code = :TRADE_TYPE_CODE ");
		sql.append(" and t.cancel_tag in ('0', '1') ");

		return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	/**
	 * 根据  servialnumber 和 inmodecode 查询 trade
	 * @param input
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset queryTradeInfoByIData(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_B_TRADE", "SEL_TRADE_BY_SER_INMODECODE", input, Route.getJourDb());
	}
	
	public static IDataset queryIMSUserOpenTradeid(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		StringBuilder buidler = new StringBuilder();
		buidler.append(" Select trade_id From tf_b_trade ");
		buidler.append("Where SERIAL_NUMBER = :SERIAL_NUMBER ");
		buidler.append("And trade_type_code = 6800 And Cancel_Tag = '0'");
		return Dao.qryBySql(buidler, param, Route.getJourDb());
	}
	public static IDataset queryTradeinfoBySN_RSRVSTR1(String serialNumber,String rsrvStr1) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("RSRV_STR1", rsrvStr1);

		StringBuilder buidler = new StringBuilder();
		buidler.append(" Select trade_id From tf_b_trade ");
		buidler.append("Where SERIAL_NUMBER = :SERIAL_NUMBER ");
		buidler.append("And trade_type_code = 240 And Cancel_Tag = '0' and RSRV_STR1= :RSRV_STR1");
		return Dao.qryBySql(buidler, param, Route.getJourDb());
	}

	/**
	 * 查询宽带完工工单
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 * xhzu5
	 * 2018-10-24 15:38:43
	 */
	public static IDataset getWindTradeInfo(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_B_TRADE", "SEL_CANCEL_BY_SN", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

	}

	/**
	 * 根据手机号码和业务类型查询有效台账信息
	 *
	 * @param serialNumber
	 * @param tradeTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeInfoByCodeAndSn(String serialNumber, String tradeTypeCode) throws Exception {
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, TRADE_TYPE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
		sql.append("PRODUCT_ID, BRAND_CODE, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, PF_WAIT ,INTF_ID ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE 1=1 ");
		sql.append("AND TRADE_TYPE_CODE = :TRADE_TYPE_CODE AND SERIAL_NUMBER = :SERIAL_NUMBER AND CANCEL_TAG = '0' ");
		return Dao.qryBySql(sql, inparams, Route.getJourDb());
	}

	/**
	 * 根据ORDER_ID。业务类型、CANCEL_TAG查询订单主表
	 *
	 * @param orderId
	 * @param tradeTypeCode
	 * @param cancelTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeInfoByOidAndCodeAndTag(String orderId, String tradeTypeCode, String cancelTag) throws Exception {
		IData inparams = new DataMap();
		inparams.put("ORDER_ID", orderId);
		inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
		inparams.put("CANCEL_TAG", cancelTag);
		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ACCEPT_MONTH, ");
		sql.append("TO_CHAR(BATCH_ID) BATCH_ID, ");
		sql.append("TO_CHAR(ORDER_ID) ORDER_ID, ");
		sql.append("TO_CHAR(PROD_ORDER_ID) PROD_ORDER_ID, BPM_ID, ");
		sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, TRADE_TYPE_CODE, PRIORITY, ");
		sql.append("SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
		sql.append("IN_MODE_CODE, TO_CHAR(CUST_ID) CUST_ID, CUST_NAME, ");
		sql.append("TO_CHAR(USER_ID) USER_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
		sql.append("SERIAL_NUMBER, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
		sql.append("PRODUCT_ID, BRAND_CODE, ");
		sql.append("TO_CHAR(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE, ");
		sql.append("TRADE_STAFF_ID, TRADE_DEPART_ID, TRADE_CITY_CODE, ");
		sql.append("TRADE_EPARCHY_CODE, TERM_IP, ");
		sql.append("TO_CHAR(OPER_FEE) OPER_FEE, ");
		sql.append("TO_CHAR(FOREGIFT) FOREGIFT, ");
		sql.append("TO_CHAR(ADVANCE_PAY) ADVANCE_PAY, INVOICE_NO, ");
		sql.append("FEE_STATE, ");
		sql.append("TO_CHAR(FEE_TIME, 'yyyy-mm-dd hh24:mi:ss') FEE_TIME, ");
		sql.append("FEE_STAFF_ID, PROCESS_TAG_SET, OLCOM_TAG, ");
		sql.append("TO_CHAR(FINISH_DATE, 'yyyy-mm-dd hh24:mi:ss') FINISH_DATE, ");
		sql.append("TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME, ");
		sql.append("EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
		sql.append("TO_CHAR(CANCEL_DATE, 'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE, ");
		sql.append("CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, ");
		sql.append("CANCEL_EPARCHY_CODE, ");
		sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, ");
		sql.append("RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, ");
		sql.append("RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10, PF_WAIT ,INTF_ID ");
		sql.append("FROM TF_B_TRADE T ");
		sql.append("WHERE 1=1 ");
		sql.append("AND TRADE_TYPE_CODE = :TRADE_TYPE_CODE AND ORDER_ID = :ORDER_ID AND CANCEL_TAG = '0' ");
		return Dao.qryBySql(sql, inparams, Route.getJourDb());
	}

	/**
	 * 搬迁订单表
	 * @param orderInfo
	 * @throws Exception
	 */
	public static void moveBhOrder(IData orderInfo) throws Exception {
		if (IDataUtil.isNotEmpty(orderInfo)) {
			orderInfo.put("CANCEL_TAG", "1");
			Dao.insert("TF_BH_ORDER", orderInfo, Route.getJourDb(BizRoute.getRouteId()));
			orderInfo.put("CANCEL_TAG", "0");
			String[] keys = new String[] {"ORDER_ID", "ACCEPT_MONTH", "CANCEL_TAG"};
			Dao.delete("TF_B_ORDER", orderInfo, keys, Route.getJourDb());
		}
	}

	/**
	 * 搬迁工单表
	 *
	 * @param tradeInfo
	 * @throws Exception
	 */
	public static void moveBhTrade(IData tradeInfo) throws Exception {
		if (IDataUtil.isNotEmpty(tradeInfo)) {
			tradeInfo.put("CANCEL_TAG", "1");
			Dao.insert("TF_BH_TRADE", tradeInfo, Route.getJourDb());
			tradeInfo.put("CANCEL_TAG", "0");
			String[] keys = new String[] {"TRADE_ID", "ACCEPT_MONTH", "CANCEL_TAG"};
			Dao.delete("TF_B_TRADE", tradeInfo, keys, Route.getJourDb());
		}
	}
	
	public static IDataset qryTMSalePerson(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_TMSALEPERSON", input);
	}

	public static IDataset qryTMChangeBackPerson(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_TMCHANGEBACKPERSON", input);
	}
	
	public static IDataset qryWideInstall(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_WIDEINSTALL", input);
	}
	
	public static IDataset qryWideMove(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_WIDEMOVE", input);
	}
	
	public static IDataset qryWideRemove(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_WIDEREMOVE", input);
	}
	public static IDataset qryWideCompleted(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_WIDERECOMPLETED", input);
	}
	
	public static IDataset qryWideCancell(IData input) throws Exception{
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_TRADE_WIDECANCELL", input);
	}
	/*
	 * @panyu5
	 * @网间携号
	 */
	public static IDataset queryTradeInfoByPK(IData param) throws Exception {
        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_PK", param, Route.getJourDb());
    }
	
	/**
	 * 恢复统付关系k3
	 * @param userId
	 * @param serialNumber
	 * @param tradeTypeCode
	 * @param subscribeState
	 * @return
	 * @throws Exception
	 */
	public static IDataset getHisMainTradeByUserIdAndSn(String userId,String tradeTypeCode) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("CANCEL_TAG", '0');
		return Dao.qryByCode("TF_BH_TRADE", "SEL_LASTTRADE_BY_USERID", param, Route.getJourDb());
	}
	/**
	 * 报开报停历史记录查询k3
	 * @param input
	 * @param acceptMonth
	 * @return
	 * @throws Exception
	 */
	public static IDataset getReportStopAndOpen(IData input,String accept_month)throws Exception{
		SQLParser sql =new SQLParser(input);
		sql.addSQL("SELECT t.* from TF_BH_TRADE t ");
		sql.addSQL("WHERE t.SERIAL_NUMBER = :SERIAL_NUMBER ");
		sql.addSQL("AND t.TRADE_TYPE_CODE in (131,133) ");
		sql.addSQL("AND t.accept_date BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD') AND TO_DATE(:END_DATE, 'YYYY-MM-DD') ");
		sql.addSQL("AND t.accept_month in ("+accept_month+")");
		return Dao.qryByParse(sql,Route.getJourDb());
	}
	
	
	public static IDataset queryByTradeIdCancelTag(String tradeId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("CANCEL_TAG", tradeId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADEID_CANCEL_TAG", param, Route.getJourDb(routeId));
	}

	/**
	 * 根据手机号码、受理员工工号和接触时间，查询接触时间前1小时内的个人和宽带工单数据
	 * @param sn
	 * @param tradeStaffId
	 * @param contactTime
	 * @return
	 * @author zhaohj3
	 * @throws Exception
	 */
	public static IDataset queryBySnContactTimeForOL(String sn, String tradeStaffId, String contactTime) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		param.put("TRADE_STAFF_ID", tradeStaffId);
		param.put("CONTACT_TIME", contactTime);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_CT", param, Route.getJourDb());
	}
	 public static IDataset getTradeInfobyOrderId(String orderId) throws Exception {
	        
	        IData inparams = new DataMap();
	        inparams.put("ORDER_ID", orderId);
	        return Dao.qryByCodeParser("TF_B_TRADE", "SEL_TRADE_BY_ORDERID", inparams, Route.getJourDb());
	 }
	 
	/**
	 * 根据集团客户CUSTID查询集团商务宽带下是否有成员开户的工单
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeBroadbandMebByCustId(IData input) throws Exception
	{
		return Dao.qryByCode("TF_B_TRADE", "SEL_BROADBANDMEB_BY_CUSTID", input, Route.getJourDb());
	}
	
    /**
     * 根据orderID和tradeTypeCode查询trade表
     * @param orderId
     * @param tradeTypeCode
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset getHisTradeInfoByOrderId(String orderId, String tradeTypeCode, String cancelTag)throws Exception{
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("CANCEL_TAG", cancelTag);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_ORDER_TTYPE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
	 
	 /**
	  * 查询多媒体桌面电话集团未完成成员工单
	  * @param userIdB
	  * @return
	  * @throws Exception
	  */
	 public static IDataset queryUserIdB(String userIdB) throws Exception{
	        IData inparams = new DataMap();
	        inparams.put("USER_ID_B", userIdB);
	        StringBuilder sql = new StringBuilder(2500);
	        sql.append("SELECT  t.*,rowid FROM tf_b_trade t ");
	        sql.append(" where (t.trade_type_code='3884' or t.trade_type_code='3885' or t.trade_type_code='3842') ");
	        sql.append(" and t.USER_ID_B=:USER_ID_B ");
	        return Dao.qryBySql(sql, inparams, Route.getJourDb(BizRoute.getRouteId()));
	    }
	 /**
	 * 查询权益标识是否已被绑定,有未完工工单
	 * TRADE_TYPE_CODE = '712'
	 *  REQ201912140002 关于全球通美兰机场免费停车的需求
	 * @param relId
	 * @param discntCode
	 * @param rightId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeBenefitChange(String relId,String discntCode,String rightId) throws Exception{
		IData param = new DataMap();
		param.put("RSRV_STR3", relId);
		param.put("RSRV_STR2", discntCode);
		param.put("RSRV_STR1", rightId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BENEFIT_BY_RELID", param, Route.getJourDb());
	}


    /**
     * 根据客户名称查询客户信息
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeInfoByCustName(String custName) throws Exception{
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT  t.* FROM tf_b_trade t ");
        sql.append(" where 1=1");
        sql.append(" and t.CUST_NAME=:CUST_NAME ");
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }

}
