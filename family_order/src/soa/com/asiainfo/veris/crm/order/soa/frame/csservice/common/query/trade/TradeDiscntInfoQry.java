package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class TradeDiscntInfoQry
{
	private static final Logger log = Logger.getLogger(TradeDiscntInfoQry.class);

	public static IDataset checkFetionScoreExchange(String serialNumber, String userId) throws Exception
	{
		IDataset results = new DatasetList();

		IData param = new DataMap();
		// param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);

		// return Dao.qryByCode("TF_B_TRADE_DISCNT",
		// "SELECT_FETION_SCORE_EXCHANGE", param); 原sql

		// 拆分sql modify by duhj
		IDataset discnts = Dao.qryByCode("TF_B_TRADE_DISCNT", "SELECT_FETION_SCORE_EXCHANGE_NEW", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		IDataset para_codes = CommparaInfoQry.queryUserFetion();
		if (IDataUtil.isNotEmpty(discnts))
		{

			for (int i = 0; i < discnts.size(); i++)
			{
				IData discnt = discnts.getData(i);
				for (int j = 0; j < para_codes.size(); i++)
				{
					IData para_code4s = para_codes.getData(j);

					String discnt_code = discnt.getString("DISCNT_CODE");
					String para_code4 = para_code4s.getString("PARA_CODE4");

					if (StringUtils.equals(discnt_code, para_code4))
					{
						results.add(discnt);
					}
				}

			}
		}

		return results;
	}

	/**
	 * 根据tradeId查询所有的用户优惠备份数据
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllTradeBakDiscntByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT_BAK", "SEL_ALL_BAK_BY_TRADE", params);
	}

	public static IDataset getGprsDoning(String vmodify_tag) throws Exception
	{
		IData param = new DataMap();
		param.put("VMODIFY_TAG", vmodify_tag);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_COUNT_GPRS_DONING", param);
	}

	/**
	 * 根据tradeId查询所有的用户优惠台账
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeDiscntByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_TRD_ID", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 根据tradeId查询所有的用户物联网测试期优惠台账shenhai
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeDiscntWlwByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_COMMPARAWLW_BY_TRADEID", params);
	}

	/**
	 * @param inparams
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeDiscntCodeProUserPkg(IData inparams) throws Exception
	{
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_DISCNT_CODE_PRO_USER_PACKAGE", inparams, Route.CONN_CRM_CG);
	}

	public static IDataset getTradeDiscntInfoByTradeId(String strTradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", strTradeId);

		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_PACKAGE_UNFORCE_DISCNT", param);
	}

	/**
	 * 获取 TF_B_TRADE_DISCNT 表数据 <BR>
	 * 
	 * @param trade_id
	 * @param discnt_code
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getTradeDiscntInfosByDiscntCode(String trade_id, String discnt_code) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("DISCNT_CODE", discnt_code);
		inParams.put("TRADE_ID", trade_id);

		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_DISCNTCODE", inParams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * @description 通过台账编号、资费编号及modify_tag查找资费对应的台账信息
	 * @author xunyl
	 * @date 2015-08-10
	 */
	public static IDataset getTradeDiscntInfoListForAdd(String trade_id, String discnt_code, String modifyTag) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("TRADE_ID", trade_id);
		inparams.put("DISCNT_CODE", discnt_code);
		inparams.put("MODIFY_TAG", modifyTag);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("Select to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, ");
		sql.append("to_char(USER_ID_A) USER_ID_A, PACKAGE_ID, PRODUCT_ID, DISCNT_CODE, SPEC_TAG, ");
		sql.append("RELATION_TYPE_CODE, to_char(INST_ID) INST_ID, to_char(CAMPN_ID) CAMPN_ID, ");
		sql.append("to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, MODIFY_TAG, ");
		sql.append("to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, ");
		sql.append("UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, ");
		sql.append("to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
		sql.append("to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sql.append("to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
		sql.append("RSRV_TAG2, RSRV_TAG3, MODIFY_TAG mod_type ");
		sql.append("FROM tf_b_trade_discnt ");
		sql.append("WHERE trade_id=TO_NUMBER(:TRADE_ID) ");
		sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) ");
		sql.append("AND to_char(discnt_code) = :DISCNT_CODE ");
		sql.append("AND MODIFY_TAG = :MODIFY_TAG ");

		return Dao.qryBySql(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
	}

	// 取消预约产品变更获取优惠变更信息
	public static IDataset getTradeDiscntInfosByTradeId(String tradeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
		inparams.put("TRADE_ID", tradeId);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_CANCLE_BY_TRADEID", inparams, Route.getJourDb());
	}

	/**
	 * @param inparams
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTradeDiscntProUser(IData inparams) throws Exception
	{
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_TRADE_PRO_USER", inparams, Route.CONN_CRM_CG);
	}

	/**
	 * chenyi 2014-3-04 将资费参数插入表中
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static void insertDiscntInfo(IData param) throws Exception
	{

		Dao.insert("TF_B_TRADE_DISCNT", param, Route.getJourDb());
	}

	/**
	 * 根据业务流水号，修改标志查询业务台帐资费子表
	 * 
	 * @param trade_id
	 * @param modify_tag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTradeDiscntInfos(String trade_id, String modify_tag, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("MODIFY_TAG", modify_tag);

		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_TAG", param, pagination, Route.getJourDb());
		return infos;
	}

	/**
	 * chenyi 查询tradeDistinctInfo信息
	 * 
	 * @param trade_id
	 * @param modify_tag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTradeDiscntInfosByPK(String trade_id, String user_id, String discnt_code, String package_id) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("USER_ID", user_id);
		param.put("DISCNT_CODE", discnt_code);
		param.put("PACKAGE_ID", package_id);

		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_DISTINCINFO_BY_PK", param);
		return infos;
	}

	public static IDataset qryTradeDiscntTransInfos(String tradeId, String modifyTag, String startDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("MODIFY_TAG", modifyTag);
		param.put("START_DATE", startDate);
		param.put("END_DATE", endDate);

		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_TRANS_DISCNTS", param);
		return infos;
	}

	public static IDataset queryCountByUidDiscntCode(IData param) throws Exception
	{
		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_COUNT_BY_UID_DISCNTCODE", param);
		return infos;
	}

	public static IDataset queryCountByUidDiscntCode(String userId, String discntCode, String modifytag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		param.put("MODIFY_TAG", modifytag);
		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_COUNT_BY_UID_DISCNTCODE", param, Route.getJourDb());
		return infos;
	}

	/**
	 * 查询备份的优惠属性
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntAttrFromBakByTradeId(String tradeId, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_B_TRADE_DISCNT_BAK", "SEL_TRADEATTR_DISCNT_FROM_BAK", param);
	}

	public static IDataset queryDiscntByUserIdAndDiscntCode(IData param) throws Exception
	{
		IDataset infos = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_NOW_VALID", param);
		return infos;
	}

	// todo
	/**
	 * @Description: 根据TRADE USER_ID查询出TF_B_TRADE_DISCNT 内关联TD_B_DISTINCT 信息
	 * @author jch
	 * @date
	 * @param
	 * @param
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntTrade(String tradeId, String userId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select  ");
		parser.addSQL(" a.trade_id,a.accept_month,a.user_id,a.discnt_code, a.modify_tag, decode(a.modify_tag,'0','新增','1','删除','2','修改','U','继承') X_CODE_NAME_SET_0,");
		parser.addSQL(" a.start_date,a.end_date ");
		parser.addSQL(" from tf_b_trade_discnt a ");
		parser.addSQL(" where  a.trade_id = :TRADE_ID ");
		// parser.addSQL(" and a.discnt_code   = b.discnt_code ");
		parser.addSQL(" and a.user_id=:USER_ID ");

		return Dao.qryByParse(parser, pagination, Route.getJourDb(BizRoute.getRouteId()));
	}

	/**
	 * 作用：根据TRADE_ID查询优惠台账
	 * 
	 * @param trade_id
	 *            业务流水号
	 * @param accept_month
	 *            受理月份
	 * @param eparchyCode
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntTradeByTradeId(String trade_id, String accept_month, String eparchyCode, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("ACCEPT_MONTH", accept_month);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID", param, pagination, eparchyCode);
	}

	/**
	 * 作用：根据TRADE_ID查询优惠台账
	 * 
	 * @param trade_id
	 *            业务流水号
	 * @param accept_month
	 *            受理月份
	 * @param eparchyCode
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntTradeByTradeId(String trade_id, String accept_month, String eparchyCode) throws Exception
	{

		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("ACCEPT_MONTH", accept_month);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID", param, eparchyCode);
	}

	/**
	 * 原短信翻译
	 * 
	 * @param trade_id
	 * @param modify_tag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPlatsvcDiscnt(String trade_id, String user_id, String eparchy_code, String service_id, String para_code3) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("USER_ID", user_id);
		param.put("EPARCHY_CODE", eparchy_code);
		param.put("SERVICE_ID", service_id);
		param.put("PARA_CODE3", para_code3);

		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_TAG", param);
	}

	public static IDataset queryRelaDiscntAFromBakByTradeId(String tradeId, String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_B_TRADE_DISCNT_BAK", "SEL_ALL_BAK_BY_TRADEID_RELADISCNT_A", param);
	}

	// todo 无SQL
	/**
	 * 作用：根据TRADE_ID查询优惠台账
	 * 
	 * @param tradeId
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeDiscnt(String tradeId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "TRADE_DISCNT_SEL", param, pagination);
	}

	/**
	 * 根据trade_id,modify_tag查询当前台账操作的非“购机绑定新增礼品包(param_attr=970)”资费
	 * gaoyuan3@asiainfo-linkage.com @ 2012-2-10 上午10:45:54
	 * 
	 * @param strTradeId
	 * @param strModifyTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeDiscntByAdd(String strTradeId, String strModifyTag, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.clear();
		param.put("TRADE_ID", strTradeId);
		param.put("MODIFY_TAG", strModifyTag);

		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADE_MTAG_2", param, pagination);
	}

	/**
	 * 根据TRADE_ID,MODIFY_TAG,USER_ID查询优惠台账
	 * 
	 * @author chenzm
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeDiscntByTradeIdAndTag(String trade_id, String modify_tag, String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("MODIFY_TAG", modify_tag);
		param.put("USER_ID", user_id);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADE_USER", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * @Function: queryTradeDiscntByTradeIdUserId
	 * @Description: 台账优费资料
	 * @param: @param tradeId
	 * @param: @param userId
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 3:13:30 PM Jul 27, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* Jul 27,
	 *        2013 longtian3 v1.0.0 TODO:
	 */
	public static IDataset queryTradeDiscntByTradeIdAndUserId(String tradeId, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_USERID_DISCNT_INFO", param);
	}

	/**
	 * 查询备份的优惠属性
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeDiscntFromBak(String tradeId, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_TRADE_DISCNT_FROM_BAK", param);
	}

	/**
	 * 查询备份的优惠属性
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeDiscntFromBakOther(String tradeId, String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_TRADE_DISCNT_FROM_BAK_1", param);
	}

	/**
	 * 根据用户标识查询优惠台账表
	 * 
	 * @param userId
	 *            用户标识
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTradeDiscntsByUserId(String userId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_USER", param, pagination);
	}

	public static IDataset selectPbossEndData(String strTradeId, String finishDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", strTradeId);
		param.put("START_DATE", finishDate);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_ENDDATE_BY_TRADEID", param);
	}

	/**
	 * chenyi 2014-3-4 将已有的资费状态修改成新增
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static void updateModefytag(IData param) throws Exception
	{

		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_MODIFY_TAG", param, Route.getJourDb());
	}

	/**
	 * 更新资费开始时间
	 * 
	 * @author chenzm
	 * @param trade_id
	 * @param start_date
	 * @throws Exception
	 */
	public static void updateStartDate(String trade_id, String start_date) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("START_DATE", start_date);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
	
	/**
	 * 更新资费结束时间
	 * 
	 * @author chenzm
	 * @param trade_id
	 * @param end_date
	 * @param discnt_code
	 * @throws Exception
	 */
	public static void updateEndDate(String trade_id, String end_date, String discnt_code) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("END_DATE", end_date);
		param.put("DISCNT_CODE", discnt_code);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_ENDDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 更新宽带保底资费开始时间
	 * 
	 * @author chenzm
	 * @param trade_id
	 * @param start_date
	 * @throws Exception
	 */
	public static void updateStartDate2(String trade_id, String start_date) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("START_DATE", start_date);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_STARTDATE2", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	public static void updateStartEndDate(String strTradeId, String finishDate, String newEndDate) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", strTradeId);
		param.put("START_DATE", finishDate);
		param.put("END_DATE", newEndDate);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPDATE_STARTDATE_ENDDATE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 更新资费开始结束时间
	 * 
	 * @author chenzm
	 * @param trade_id
	 * @param inst_id
	 * @param start_date
	 * @param end_date
	 * @throws Exception
	 */
	public static void updateStartEndDate(String trade_id, String inst_id, String start_date, String end_date) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("INST_ID", inst_id);
		param.put("START_DATE", start_date);
		param.put("END_DATE", end_date);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_STARTENDDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 更新tradeDistinct表状态
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static void uptradeDistinctState(IData param) throws Exception
	{

		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPDATE_TRADEDISTINCT_STATE", param, Route.getJourDb());

	}

	/**
	 * 更新tradeDistinct表状态
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static void uptradeDistinctState1(String tradeId, String discntCode, String delFlag, String isNeedPf) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("TRADE_ID", tradeId);
		inparams.put("DISCNT_CODE", discntCode);
		inparams.put("RSRV_STR3", delFlag);
		inparams.put("IS_NEED_PF", isNeedPf);

		StringBuilder sql = new StringBuilder(1000);
		sql.append("UPDATE ");
		sql.append("tf_b_trade_discnt ");
		sql.append("set  rsrv_str3= :RSRV_STR3, ");
		sql.append("is_need_pf= :IS_NEED_PF ");
		sql.append("WHERE trade_id=TO_NUMBER(:TRADE_ID) ");
		sql.append("AND accept_month=TO_NUMBER(substr(:TRADE_ID,5,2)) ");
		sql.append("AND discnt_code=:DISCNT_CODE ");

		Dao.executeUpdate(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
	}

	// todo
	/**
	 * 获取优惠台帐表
	 * 
	 * @param iData
	 * @return
	 */
	public IDataset getTradeDiscntInfo(String tradeId, Pagination pagination) throws Exception
	{

		if (tradeId == null || "".equals(tradeId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103);
		}
		IData params = new DataMap();
		params.put("VTRADE_ID", tradeId);
		try
		{
			IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_FINISH", params, Route.CONN_CRM_CEN);
			if (!IDataUtil.isNotEmpty(iDataset))
			{
				((IData) iDataset.get(0)).put("X_RECORDNUM", iDataset.size());
			}
			return iDataset;
		} catch (Exception e)
		{
			e.printStackTrace();
			CSAppException.apperr(TradeException.CRM_TRADE_40);
			return null;
		}
	}

	/**
	 * 根据tradeId、productId、packageId查询用户备份的、指定活动内的、有效的优惠数据
	 * 
	 * @param tradeId
	 *            、productId、packageId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getValidSaleActiveDiscntBakByTradeId(String tradeId, String productId, String packageId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		/*
		 * params.put("PRODUCT_ID", productId); params.put("PACKAGE_ID",
		 * packageId);
		 */
		return Dao.qryByCode("TF_B_TRADE_DISCNT_BAK", "SEL_VALID_TRADESALEACTIVE_DISCNT_BAK", params);
	}

	public static void saveDiscntTradeForTopSetOpen(IData param) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "INSERT_TRADE_DISCNT_TOPSET_OPEN", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * add by duhj 2017/03/21
	 * 
	 * @param userid
	 * @param modifyTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCountGprsDoning(String userid, String modifyTag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userid);
		param.put("MODIFY_TAG", modifyTag);
		return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_COUNT_GPRS_DONING", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

	}

	public static IDataset getValidTradeBakDiscntByTradeId(String tradeId) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT_BAK", "SEL_VALID_BAK_BY_TRADE", params);
	}

	/**
	 * 根据tradeId查询所有的用户优惠
	 * 
	 * @author fangwz
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntTradeByTradeIdAndModifyTag(String tradeId, String modyfyTag, String routeid) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		params.put("MODIFY_TAG", modyfyTag);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_TAG_1", params, routeid);
	}

	/**
	 * 根据tradeId userid 查询所有的用户优惠
	 * 
	 * @author fangwz
	 * @param tradeId
	 *            userid
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntByTradeIdAndUseridAndTag(String tradeId, String modyfyTag, String userid, String param, String epachycode, String routeid) throws Exception
	{
		IData params = new DataMap();
		params.put("TRADE_ID", tradeId);
		params.put("MODIFY_TAG", modyfyTag);
		params.put("USER_ID", userid);
		params.put("PARAM_CODE", param);
		params.put("EPARCHY_CODE", epachycode);
		return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_MODIFY_1", params, routeid);
	}
	
	public static void updateIMSStartDate(String trade_id, String start_date) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("START_DATE", start_date);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_IMSSTARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
    public static IDataset queryCountByUidDiscntCode(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_UID_DISCNTCODE", param, Route.getJourDb());
		return infos;
	}
    public static IDataset queryCountByUidDiscntCode2(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		IDataset infos = Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_UID_DISCNTCODE2", param, Route.getJourDb());
		return infos;
	}
    public static IDataset getTradeByUserIdAndDiscntCode(IData input) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", input.getString("USER_ID"));
        params.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        params.put("INST_ID", input.getString("INST_ID"));
        return Dao.qryByCodeParser("TF_B_TRADE_DISCNT", "SEL_BY_USERID_DISCNT", params, Route.getJourDb(BizRoute.getRouteId()));
    }

	/**
	 * BUG20200609104330修复智能组网调测费问题
	 * 更新资费开始结束时间和备注
	 * @param trade_id
	 * @param inst_id
	 * @param start_date
	 * @param end_date
	 * @param remark
	 * @throws Exception
	 */
	public static void updateStartEndDateAndRemark(String trade_id, String inst_id, String start_date, String end_date,String remark) throws Exception{
		IData param = new DataMap();
		param.put("TRADE_ID", trade_id);
		param.put("INST_ID", inst_id);
		param.put("START_DATE", start_date);
		param.put("END_DATE", end_date);
		param.put("REMARK", remark);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_STARTENDDATE_REMARK", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
}
