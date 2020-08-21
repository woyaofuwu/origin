package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;

public class TradeHistoryInfoQry
{

	private static final Logger log = Logger.getLogger(TradeHistoryInfoQry.class);

	public static IDataset getDestroyMaxTradeIdByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_DESTROYMAXTRADEID_BY_USERID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 获取用户是否已经办理过某个业务
	 * 
	 * @param tradeTypeCode
	 * @param serialNumber
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getInfosBySnTradeTypeCode(String tradeTypeCode, String serialNumber, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_TRADETYPEOCDE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}
	
	/**
	 * 获取用户最后一笔完工工单
	 * 
	 * @param tradeTypeCode
	 * @param serialNumber
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getInfosOrderByDesc(String serialNumber, String userId,String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_BY_SN_ORDERBYDESC", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	public static IDataset getInfosBySnTradeTypeCodeAcceptDate(String tradeTypeCode, String serialNumber, String userId, String acceptDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);
		param.put("ACCEPT_DATE", acceptDate);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADETYPEOCDE_DATE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 查指定用户和业务的办理历史记录
	 * 
	 * @param tradeTypeCode
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset getInfosByUserIdTradeTypeCode(String tradeTypeCode, String userId, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("USER_ID", userId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADETYPEOCDE_EXECTIME", param, Route.getJourDb(BizRoute.getRouteId()));
	}

	public static IDataset getInfosByUserIdTradeTypeCode(String tradeTypeCode, String userId, String startDate, String endDate, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("USER_ID", userId);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADETYPEOCDE_EXECTIME", param, Route.getJourDb(routeId));
	}

	/**
	 * @Description 获取可打印的预存款记录
	 * @param serialNumber
	 * @param tradeId
	 * @param acceptMonth
	 * @param acceptTime
	 * @param tradeStaffId
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getPrintAdvancePay(String tradeId, String serialNumber, String acceptMonth, String acceptTime, String tradeStaffId) throws Exception
	{
		IData qryParam = new DataMap();
		qryParam.put("TRADE_ID", tradeId);
		qryParam.put("SERIAL_NUMBER", serialNumber);
		qryParam.put("ACCEPT_MONTH", acceptMonth);
		qryParam.put("ACCEPT_TIME", acceptTime);
		qryParam.put("TRADE_STAFF_ID", tradeStaffId);
		return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_PRINT_ADVANCE_PAY", qryParam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * @Description 获取可打印的营业费记录
	 * @param serialNumber
	 * @param tradeId
	 * @param acceptMonth
	 * @param acceptTime
	 * @param tradeStaffId
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getPrintFee(String tradeId, String serialNumber, String acceptMonth, String acceptTime, String tradeStaffId) throws Exception
	{
		IDataset printTrades = new DatasetList();
		IData qryParam = new DataMap();
		qryParam.put("TRADE_ID", tradeId);
		qryParam.put("SERIAL_NUMBER", serialNumber);
		qryParam.put("ACCEPT_MONTH", acceptMonth);
		qryParam.put("ACCEPT_TIME", acceptTime);
		qryParam.put("TRADE_STAFF_ID", tradeStaffId);
		IDataset printFees = Dao.qryByCodeParser("TF_BH_TRADE", "SEL_PRINT_FEE", qryParam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		printTrades.addAll(printFees);

		// 关于关于一级自有电渠开发流量直充、流量赠送及积分兑换流量产品的需求 增加流量直充业务发票补打信息 lihb3 2016907
		IDataset flowTrades = Dao.qryByCodeParser("TF_BH_TRADE", "SEL_PRINT_FEE_FLOW", qryParam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		if (IDataUtil.isNotEmpty(flowTrades))
		{
			for (int i = 0; i < flowTrades.size(); i++)
			{
				flowTrades.getData(i).put("TRADE_TYPE_CODE", "111");
			}
		}
		printTrades.addAll(flowTrades);

		return printTrades;
	}

	public static IDataset getTradeHisInfos(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_LAST_TRADEID_BY_SN_NPTRADETYPECODE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	// todo
	/**
	 * 获取用户指定业务最近一次办理记录 changed by gongp@20140714
	 * 
	 * @param iData
	 * @return
	 */
	public static IData getTradeInfoByUserTrade(String userId, String tradeTypeCode) throws Exception
	{

		if (StringUtils.isBlank(userId))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_582);
		}
		if (StringUtils.isBlank(tradeTypeCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "tradeTypeCode 不能为空");
		}
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("TRADE_TYPE_CODE", tradeTypeCode);
		params.put("CANCEL_TAG", "0");
		IData outIData = new DataMap();// 输出
		try
		{
			IDataset iDataset = Dao.qryByCodeParser("TF_BH_TRADE", "SEL_LASTTRADE_BY_USERID", params, Route.getJourDb(Route.CONN_CRM_CG));
			if (IDataUtil.isNotEmpty(iDataset) && iDataset.size() == 1)
			{
				String tradeId = iDataset.getData(0).getString("TRADE_ID");
				if (StringUtils.isEmpty(tradeId))
				{
					outIData.put("X_RESULTCODE", 0);
					outIData.put("X_RECORDNUM", 0);
					outIData.put("X_RESULTINFO", "该用户未做过指定业务！");
					return outIData;
				}
				IData tradeParams = new DataMap();
				tradeParams.put("TRADE_ID", tradeId);
				tradeParams.put("CANCEL_TAG", "0");
				IData tradeIDataset = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);
				if (IDataUtil.isNotEmpty(tradeIDataset) && tradeIDataset.size() == 1)
				{
					outIData.putAll(tradeIDataset);
					outIData.put("X_RESULTCODE", "0");
					outIData.put("X_RECORDNUM", "1");
					outIData.put("X_RESULTINFO", "获取用户相关台帐资料成功！");
				} else
				{
					outIData.put("X_RESULTCODE", "0");
					outIData.put("X_RECORDNUM", "0");
					outIData.put("X_RESULTINFO", "该用户未做过指定业务！");
				}
			} else
			{
				outIData.put("X_RESULTCODE", 0);
				outIData.put("X_RECORDNUM", 0);
				outIData.put("X_RESULTINFO", "该用户未做过指定业务！");
			}
			return outIData;
		} catch (Exception e)
		{
			// e.printStackTrace();
			log.equals(e);
			CSAppException.apperr(TradeException.CRM_TRADE_94);
			return null;
		}
	}

	public static IDataset qrySpecialOpenTrade(String userId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_MAX_FORSPECIALOPEN_HAIN", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 根据订单编号查找台账
	 * 
	 * @param orderId
	 * @param cancelTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTradeByOrderId(String orderId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", cancelTag);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_ORDER", param, Route.getJourDb(routeId));
	}

	/**
	 * 查询预约产品变更的台账信息(海南最新逻辑)
	 * 
	 * @param acceptMonth
	 * @param userId
	 * @param tradeTypeCode
	 * @param instId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryBookedProductTrade(String userId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_CHG_PRODUCT_TRADE_HAIN", param, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
	}

	/**
	 * 获取能够返销的订单数据
	 * 
	 * @param serialNumber
	 * @param tradeTypeCode
	 * @param tradeEaprchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCanBackTradeBySnAndTypeCode(String userId, String tradeTypeCode, String tradeEaprchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("TRADE_EPARCHY_CODE", tradeEaprchyCode);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BACK_TRADE_BY_SN", param, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 获取指定时间段内能够返销的订单数据
	 * 
	 * @param serialNumber
	 * @param tradeTypeCode
	 * @param tradeEaprchyCode
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCanBackTradeBySnAndTypeCodeAndDate(String userId, String tradeTypeCode, String tradeEaprchyCode, String beginDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, tradeEaprchyCode);
		String newTradeTypeCode = tradeTypeInfo.getString("RSRV_STR3");
		String processTagSet1 = tradeTypeInfo.getString("RSRV_STR4");
		if (StringUtils.isNotEmpty(newTradeTypeCode) && StringUtils.isNotEmpty(processTagSet1))
		{
			param.put("TRADE_TYPE_CODE", newTradeTypeCode);
			param.put("PROCESS_TAG_SET_1", processTagSet1);
		}
		param.put("TRADE_EPARCHY_CODE", tradeEaprchyCode);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_BACK_TRADE_BY_SN_DATE", param, Route.getJourDb(tradeEaprchyCode));
	}

	/**
	 * 141异地写卡返销
	 * 
	 * @param serialNumber
	 * @param tradeTypeCode
	 * @param tradeEaprchyCode
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCanBackTradeYDXK(String serialNumber, String tradeTypeCode, String beginDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_BACK_TRADE_BY_SN_YDXK", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 查询预约产品变更的台账信息
	 * 
	 * @param acceptMonth
	 * @param userId
	 * @param tradeTypeCode
	 * @param instId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryChgProductTrade(String userId, String tradeTypeCode, String instId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("INST_ID", instId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_CHG_PRODUCT_TRADE", param, Route.getJourDb());
	}

	/**
	 * 是否存在某个时间点之后的历史工单信息
	 * 
	 * @param userId
	 * @param tradeTypeCode
	 * @param acceptDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryHisTradeAfterAcceptDate(String userId, String tradeTypeCode, String acceptDate) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("ACCEPT_DATE", acceptDate);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT trade_id ");
		sb.append("FROM tf_bh_trade a");
		sb.append(" where a.trade_type_code = :TRADE_TYPE_CODE ");
		sb.append(" and a.user_id = :USER_ID ");
		sb.append(" and a.accept_date >TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS')");
		sb.append(" and a.cancel_tag = 0 ");
		sb.append(" AND subscribe_state='9'");
		return Dao.qryBySql(sb, param);
	}
	public static IDataset queryHisTradeAfterAcceptDateAndMonth(String userId, String tradeTypeCode, String acceptDate, String month) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("ACCEPT_DATE", acceptDate);
		param.put("MONTH",month);

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT trade_id ");
		sb.append("FROM tf_bh_trade a");
		sb.append(" where a.trade_type_code = :TRADE_TYPE_CODE ");
		sb.append(" and a.user_id = :USER_ID ");
		sb.append(" and a.accept_date >TO_DATE(:ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS')");
		sb.append(" and a.accept_month = :MONTH ");
		sb.append(" and a.cancel_tag = 0 ");
		sb.append(" AND subscribe_state='9'");
		return Dao.qryBySql(sb, param);
	}
	// todo
	public static IDataset queryHisTradeFee(String tradeTypeCode, String userId, String tradeStaffId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("USER_ID", userId);
		param.put("TRADE_STAFF_ID", tradeStaffId);

		IDataset result = null;
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT t.trade_id,s.fee_type_code,(s.fee/100) fee,s.fee fact_fee, ");
		parser.addSQL(" s.fee_mode,t.accept_date,t.trade_staff_id  ");
		parser.addSQL(" FROM tf_bh_trade t, tf_b_tradefee_sub s ");
		parser.addSQL(" WHERE t.trade_type_code=:TRADE_TYPE_CODE ");
		parser.addSQL(" AND t.user_id=:USER_ID");
		parser.addSQL(" AND t.trade_id=s.trade_id ");
		parser.addSQL(" AND t.trade_staff_id=:TRADE_STAFF_ID ");
		parser.addSQL(" AND t.cancel_tag='0' ");
		result = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
		return result;
	}

	// todo
	public static IDataset queryHisTradeFee_cg(String tradeTypeCode, String tradeStaffId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("TRADE_STAFF_ID", tradeStaffId);

		IDataset result = null;
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT t.trade_id,s.fee_type_code,(s.fee/100) fee,s.fee fact_fee, ");
		parser.addSQL(" s.fee_mode,t.accept_date,t.trade_staff_id  ");
		parser.addSQL(" FROM tf_bh_trade t, tf_b_tradefee_sub s ");
		parser.addSQL(" WHERE t.trade_type_code=:TRADE_TYPE_CODE ");
		parser.addSQL(" AND t.user_id=:USER_ID");
		parser.addSQL(" AND t.trade_id=s.trade_id ");
		parser.addSQL(" AND t.trade_staff_id=:TRADE_STAFF_ID ");
		parser.addSQL(" AND t.cancel_tag='0' ");
		result = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
		return result;
	}

	/**
	 * 根据userId查询最近的一笔销户记录 liuke
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryLastDestroyTradeByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_LAST_DESTROY_TRADEID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 根据订单编号查找台账
	 * 
	 * @param orderId
	 * @param cancelTag
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeByOrerId(String orderId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", cancelTag);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_ORDER", param, routeId);
	}

	/**
	 * 返销时查询返销限制数据
	 * 
	 * @param serialNumber
	 * @param acceptDate
	 * @param tradeTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeCancelCheckAttr(String serialNumber, String acceptDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("ACCEPT_DATE", acceptDate);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_CHECK_TRADE_ATTR", param, Route.getJourDb());
	}

	/**
	 * 返销时查询返销限制数据
	 * 
	 * @param serialNumber
	 * @param acceptDate
	 * @param tradeTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeCancelLimitTrade(String serialNumber, String acceptDate, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("ACCEPT_DATE", acceptDate);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_CHECK_TRADE_CANCEL_LIMIT", param);
	}

	public static IDataset queryTradeHisInfo(String serialNumber, String tradeTypeCode, String cancelTag) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("CANCEL_TAG", cancelTag);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_MAXTRADEID_BY_SN", param, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static IDataset queryTradeHisInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_TRADETYPE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 根据CUST_ID、TRADETYPECODE等条件查询历史台帐信息
	 * 
	 * @param cust_id
	 * @param trade_type_code
	 * @param serial_number
	 * @param cancel_tag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeInfos(String cust_id, String trade_type_code, String serial_number, String cancel_tag, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("TRADE_TYPE_CODE", trade_type_code);
		param.put("SERIAL_NUMBER", serial_number);
		param.put("CANCEL_TAG", cancel_tag);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_CUSTID_TRADETYPECODE", param, pagination);
	}

	// todo
	/**
	 * MAS费用返销的权限控制，data_code 为GROUP998，right_class 取值说明： 1 隔笔返销 2 当日返销域权 3
	 * 隔日返销域权 4 隔月 T 验证权限，不受时间限制
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeInfosWithPopedom(String provGrpCancelFee, String custId, String serialNumber, String cancelTag, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("CANCEL_TAG", cancelTag);

		SQLParser parser = new SQLParser(param);

		String sqlparam = "";
		if (StringUtils.isEmpty(provGrpCancelFee))
		{
			sqlparam = " and accept_date > trunc(sysdate) ";
		} else if ("2".equals(provGrpCancelFee))
		{
			sqlparam = " and accept_date > trunc(sysdate) ";
		} else if ("3".equals(provGrpCancelFee))
		{
			sqlparam = " and  accept_date > trunc(sysdate-1) ";
		} else if ("4".equals(provGrpCancelFee))
		{
			sqlparam = "  and  trunc(accept_date, 'dd') BETWEEN trunc(add_months(SYSDATE,-1), 'dd') AND trunc(SYSDATE, 'dd') ";
		} else if ("5".equals(provGrpCancelFee))
		{
			sqlparam = "  and  trunc(t.accept_date, 'dd') BETWEEN trunc(add_months(SYSDATE,-3), 'dd') AND trunc(SYSDATE, 'dd') ";
		} else if ("T".equals(provGrpCancelFee))
		{
			sqlparam = "  and  accept_date < sysdate ";
		}
		parser.addSQL("SELECT TRADE_ID,ACCEPT_MONTH,BATCH_ID,ORDER_ID,PROD_ORDER_ID,BPM_ID,CAMPN_ID,TRADE_TYPE_CODE,PRIORITY,SUBSCRIBE_TYPE,SUBSCRIBE_STATE,NEXT_DEAL_TAG,IN_MODE_CODE,CUST_ID,CUST_NAME,USER_ID,ACCT_ID,SERIAL_NUMBER,NET_TYPE_CODE,EPARCHY_CODE,CITY_CODE,PRODUCT_ID,BRAND_CODE,CUST_ID_B,USER_ID_B,ACCT_ID_B,SERIAL_NUMBER_B,CUST_CONTACT_ID,SERV_REQ_ID,INTF_ID,ACCEPT_DATE,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,TERM_IP,OPER_FEE,FOREGIFT,ADVANCE_PAY,INVOICE_NO,FEE_STATE,FEE_TIME,FEE_STAFF_ID,PROCESS_TAG_SET,OLCOM_TAG,FINISH_DATE,EXEC_TIME,EXEC_ACTION,EXEC_RESULT,EXEC_DESC,CANCEL_TAG,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10");
		parser.addSQL("  FROM TF_BH_TRADE t ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL("   AND t.trade_type_code in (3575,3577) ");
		parser.addSQL("   AND t.cust_id = :CUST_ID");
		parser.addSQL("   AND t.serial_number = :SERIAL_NUMBER ");
		parser.addSQL("   AND t.cancel_tag = :CANCEL_TAG ");
		parser.addSQL(sqlparam);
		parser.addSQL(" ORDER BY CANCEL_DATE desc ");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据user_id查询用户是否办理过付费关系变更
	 * 
	 * @param userId
	 * @return
	 */
	public static IDataset queryTradePayRelationInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_PAYRELAINFO_BY_USERID", param);
	}

	// todo
	/**
	 * 根据客户ID查询历史台帐信息
	 * 
	 * @throws Exception
	 */
	public static IDataset queryTradesByCustId(String custId, String cancelStaffId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);
		param.put("CANCEL_STAFF_ID", cancelStaffId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("select TRADE_ID,SERIAL_NUMBER,TRADE_TYPE_CODE,OPER_FEE,CANCEL_DATE,CANCEL_STAFF_ID FROM TF_BH_TRADE t where 1=1 ");
		parser.addSQL(" AND cancel_tag = 1 ");
		parser.addSQL(" AND trade_type_code in (3575,3577) ");
		parser.addSQL(" AND cust_id = :CUST_ID");
		parser.addSQL(" AND cancel_staff_id = :CANCEL_STAFF_ID");
		parser.addSQL(" AND trunc(t.accept_date, 'dd') BETWEEN trunc(add_months(SYSDATE, -3), 'dd') AND trunc(SYSDATE, 'dd') ");
		parser.addSQL(" order by CANCEL_DATE desc ");
		return Dao.qryByParse(parser, pagination);
	}

	public static IDataset queryUserCancelTradeIntf(String serialNumber, String tradeEparchyCode, String beginDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);

		return Dao.qryByCode("TF_BH_TRADE", "SEL_BACKINFO_BY_SN_AC_INTF", param, Route.getJourDb(tradeEparchyCode));
	}

	/**
	 * 查询用户是否存在未完工的可返销业务(写死了240业务类型)
	 * 
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserUnFinishTrade(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_UNFINISHTRADE_BY_USERID", param);
	}

	/**
	 * 查询用户是否存在未完工的可返销酬金返还业务
	 * 
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserUnFinishTradeCj(String userId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_UNFINISHCJ_BY_USERID", param);
	}

	/**
	 * 根据userId查询当月最近的一笔销户记录 songlm
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCurrentMonthLastDestroyTradeByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_CURRENT_MONTH_LAST_DESTROY_TRADEID", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 根据手机号查询230(预约营销活动受理 TF_B_TRADE_SALEACTIVE_BOOK)业务类型详情记录 by liquan
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset query_TF_B_TRADE_SALEACTIVE_BOOK_ByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALEACTIVE_BOOK", "SEL_ALLBOOK_BY_TRADE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 查询240(营销活动受理 TF_B_TRADE_SALE_ACTIVE)业务类型详情记录 by liquan
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset query_TF_B_TRADE_SALE_ACTIVE_ByTradeId1(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_ALL_BY_TRADE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 查询330、3301(积分兑换 TF_B_TRADE_SCORE)业务类型详情记录 by liquan
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset query_TF_B_TRADE_SALE_ACTIVE_ByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_SCORE", "SEL_BY_TRADE_ID_1", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	public static IDataset query_EXCHANGE_TYPE(String exchaTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("EXCHANGE_TYPE_CODE", exchaTypeCode);
		param.put("EPARCHY_CODE", "0898");

		return Dao.qryByCode("TD_B_SCORE_EXCHANGE_TYPE", "SEL_SCORE_EXCHANGE_TYPE_1", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 查询330、3301(积分兑换 TF_B_TRADE_SCORE)业务类型详情记录 by liquan
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset query_TF_B_TRADE_ByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADE_ID", param, Route.getJourDb());
	}

	public static IDataset queryByTradeId(String tradeId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("CANCEL_TAG", 0);//REQ201707130010  关于增加营业工单打印完成时间的需求
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADEID_CANCEL_TAG", param, Route.getJourDb(routeId));
	}

	public static IDataset queryByTradeIdCancelTag(String tradeId, String cancelTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("CANCEL_TAG", tradeId);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADEID_CANCEL_TAG", param, Route.getJourDb(routeId));
	}

	/**
	 * REQ201611070013 宽带产品变更取消功能优化 chenxy3 2017-2-13
	 * */
	public static IDataset qryTradeByTradeTypeOrderId(String orderId, String cancelTag, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("CANCEL_TAG", cancelTag);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADE_TYPE_ORDER", param, Route.getJourDbDefault());
	}

	public static IDataset getInfosByTradeTypeCode(String tradeTypeCode, String psptTypeCode, String psptId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("PSPT_TYPE_CODE", psptTypeCode);
		param.put("PSPT_ID", psptId);
		return Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADETYPECODE_1", param, Route.getJourDbDefault());
	}

	/**
	 * 根据手机号码、受理员工工号和接触时间，查询接触时间前1小时内的个人和宽带历史工单数据
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
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_CT", param, Route.getJourDb());
	}
	/**
	 * 查询151历史台账by-huangmx5
	 * @param sn
	 * @param tradeStaffId
	 * @param contactTime
	 * @return
	 * @author zhaohj3
	 * @throws Exception
	 */
	public static IDataset queryMinTradeByUserIdAndTradeTypeCode(String userId, String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_USER_TRADETYPECODE_MIN", param, Route.getJourDb());
	}
}
