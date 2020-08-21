package com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.text.StrBuilder;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.BatDealStateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpBaseAudiInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * @author Administrator
 */
public class BatDealBean extends CSBizBean
{
	private static Logger logger = Logger.getLogger(BatDealBean.class);
	/**
	 * @Function: batTaskNowRunForGrp
	 * @Description: 批量任务立即启动
	 * @param：
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: penghb@asiainfo-linkage.com
	 * @date: 下午3:32:50 2013-10-23 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-12 huanghui v1.0.0
	 */
	public static boolean batTaskNowRunForGrp(IData input) throws Exception
	{
		IData param = new DataMap();
		param.put("ACTIVE_FLAG", "1");
		param.put("ACTIVE_TIME", SysDateMgr.getSysTime());
		String batchId = input.getString("BATCH_ID");
		if (StringUtils.isBlank(batchId))
		{
			CSAppException.apperr(BatException.CRM_BAT_80, "BATCH_ID");
		}
		param.put("BATCH_ID", batchId);

		runUpTradeBat(param);

		IData paramB = new DataMap();
		paramB.put("BATCH_ID", batchId);
		paramB.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_1);
		paramB.put("DEAL_TIME", getBatDealDate());// 设置处理时间

		runUpTradeBatDeal(paramB);

		return true;
	}

	/**
	 * 将JSON串转换成WADE的串
	 * 
	 * @param strCoding
	 * @return
	 * @throws Exception
	 */
	public static String codingtoWadeString(String strCoding) throws Exception
	{

		if (strCoding.indexOf("[") == 0)
		{
			IDataset set = new DatasetList(strCoding);
			return set.toString();
		} else if (strCoding.indexOf("{") == 0)
		{
			IData data = new DataMap(strCoding);

			// return IDataUtil.toWadeString(data);
			return data.toString();
		}

		return strCoding;
	}

	/**
	 * 创建批量信息
	 * 
	 * @param inParam
	 * @param batDealList
	 * @return
	 * @throws Exception
	 */
	public static String createBat(IData inParam, IDataset batDealList) throws Exception
	{
		// 创建批量任务
		String batchTaskId = createBatTask(inParam);

		inParam.put("BATCH_TASK_ID", batchTaskId);

		// 创建批量和批量明细信息
		String batchId = createBatAndBatDeal(inParam, batDealList);

		return batchId;
	}

	/**
	 * 插入批次表
	 * 
	 * @param inParam
	 * @return BATCH_ID 返回批次号
	 * @throws Exception
	 */
	public static String createBatAndBatDeal(IData inParam, IDataset batDealList) throws Exception
	{
		String batchTaskId = inParam.getString("BATCH_TASK_ID"); // 批量任务号

		if (StringUtils.isEmpty(batchTaskId))
		{
			CSAppException.apperr(BatException.CRM_BAT_72);
		}

		if (IDataUtil.isEmpty(batDealList)) // 批次明细表
		{
			CSAppException.apperr(BatException.CRM_BAT_73);
		}

		String batchOperType = inParam.getString("BATCH_OPER_TYPE");
		String priority = inParam.getString("PRIORITY");
		String cancelTag = inParam.getString("CANCEL_TAG", "0");
		String dealState = inParam.getString("DEAL_STATE", BatDealStateUtils.DEAL_STATE_0);

		String batchId = SeqMgr.getBatchId();

		// 批次表信息
		IData batData = new DataMap();
		batData.put("BATCH_TASK_ID", batchTaskId);
		batData.put("BATCH_ID", batchId);
		batData.put("BATCH_OPER_TYPE", batchOperType);
		batData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
		batData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		batData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
		batData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
		batData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		batData.put("TERM_IP", CSBizBean.getVisit().getLoginIP());
		// batData.put("IN_MODE_CODE", "0"); // 和湖南保持一致 插0
		batData.put("IN_MODE_CODE", inParam.getString("IN_MODE_CODE", "0"));
		batData.put("BATCH_COUNT", batDealList.size());
		batData.put("REMOVE_TAG", "0");
		batData.put("ACTIVE_FLAG", inParam.getString("ACTIVE_FLAG", "0"));
		batData.put("ACTIVE_TIME", inParam.getString("ACTIVE_TIME"));
		batData.put("AUDIT_STATE", inParam.getString("AUDIT_STATE", "0"));
		batData.put("AUDIT_REMARK", inParam.getString("BATCH_TASK_NAME"));
		batData.put("AUDIT_DATE", inParam.getString("AUDIT_DATE"));
		batData.put("AUDIT_STAFF_ID", inParam.getString("AUDIT_STAFF_ID"));
		batData.put("AUDIT_DEPART_ID", inParam.getString("AUDIT_DEPART_ID"));
		batData.put("AUDIT_INFO", inParam.getString("AUDIT_INFO"));
		batData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
		batData.put("DST_ONE_KEY_FLAG", inParam.getString("DST_ONE_KEY_FLAG"));

		String execTime = inParam.getString("EXEC_TIME", SysDateMgr.getSysTime());
		// 批量明细表信息
		for (int i = 0, row = batDealList.size(); i < row; i++)
		{
			IData batDealData = batDealList.getData(i);
			String serialnum = batDealData.getString("SERIAL_NUMBER_B");
			batDealData.put("BATCH_TASK_ID", batchTaskId);
			batDealData.put("BATCH_ID", batchId);
			batDealData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
			batDealData.put("OPERATE_ID", SeqMgr.getBatchId());
			batDealData.put("BATCH_OPER_TYPE", batchOperType);
			batDealData.put("PRIORITY", priority);
			batDealData.put("REFER_TIME", SysDateMgr.getSysTime());
			batDealData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
			batDealData.put("EXEC_TIME", execTime);
			batDealData.put("CANCEL_TAG", cancelTag);
			batDealData.put("DEAL_STATE", dealState);
			batDealData.put("DEAL_TIME", inParam.getString("DEAL_TIME", ""));
			if(StringUtils.isEmpty(serialnum))
			{
				continue;
			}
			else
			{
				batDealData.put("SERIAL_NUMBER",serialnum);
			}
		}

		// 插入数据
		Dao.insert("TF_B_TRADE_BAT", batData, Route.getJourDb(Route.CONN_CRM_CG));
		Dao.insert("TF_B_TRADE_BATDEAL", batDealList, Route.getJourDb(Route.CONN_CRM_CG));

		return batchId;
	}

	/**
	 * 创建批次关系表
	 * 
	 * @param batchId
	 *            批次号
	 * @param relaBatchId
	 *            关联批次号
	 * @param relaTypeCode
	 *            关联类型编码
	 * @return
	 * @throws Exception
	 */
	public static boolean createBatRealtion(String batchId, String relaBatchId, String relaTypeCode) throws Exception
	{
		IData batRelaData = new DataMap();

		batRelaData.put("BATCH_ID", batchId);
		batRelaData.put("RELATION_BATCH_ID", relaBatchId);
		batRelaData.put("RELATION_TYPE_CODE", relaTypeCode);
		batRelaData.put("START_DATE", SysDateMgr.getSysTime());
		batRelaData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
		batRelaData.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_0);
		batRelaData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));

		return Dao.insert("TF_B_TRADE_BAT_RELATION", batRelaData, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public static boolean createBatRealtion(String batchId, String relaBatchId, String relaTypeCode, String dealState) throws Exception
	{
		IData batRelaData = new DataMap();

		batRelaData.put("BATCH_ID", batchId);
		batRelaData.put("RELATION_BATCH_ID", relaBatchId);
		batRelaData.put("RELATION_TYPE_CODE", relaTypeCode);
		batRelaData.put("START_DATE", SysDateMgr.getSysTime());
		batRelaData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
		batRelaData.put("DEAL_STATE", dealState);
		batRelaData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));

		return Dao.insert("TF_B_TRADE_BAT_RELATION", batRelaData, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 创建集团批量任务表[TF_B_TRADE_BAT_TASK]
	 * 
	 * @param inParam
	 * @return BATCH_TASK_ID[批量任务编号]
	 * @throws Exception
	 */
	public static String createBatTask(IData inParam) throws Exception
	{
		String batchOperType = inParam.getString("BATCH_OPER_TYPE");

		if (StringUtils.isEmpty(batchOperType))
		{
			CSAppException.apperr(BatException.CRM_BAT_74);
		}

		// 查询批量类型信息
		IDataset batchTypeList = BatchTypeInfoQry.qryBatchTypeByOperType(batchOperType);

		if (IDataUtil.isEmpty(batchTypeList))
		{
			CSAppException.apperr(BatException.CRM_BAT_37);
		}

		IData batchTypeData = batchTypeList.getData(0);
		inParam.put("PRIORITY", batchTypeData.getString("PRIORITY"));

		// 如果存在弹出窗口则批量条件不能为空
		if (StringUtils.isNotBlank(batchTypeData.getString("COMP_POPACTION")) && StringUtils.isBlank(inParam.getString("CODING_STR")))
		{
			CSAppException.apperr(BatException.CRM_BAT_2);
		}

		// 条件判断
		String condStr = inParam.getString("CODING_STR");

		if (StringUtils.isNotBlank(condStr))
		{
			condStr = codingtoWadeString(condStr);
		}

		IDataset condList = StrUtil.StringSubsection(condStr, Integer.parseInt("2000"));

		if (condList.size() > Integer.parseInt("5"))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购条件字符串太长!");
		}

		// BBOSS产品需要特殊校验
		if (batchOperType.equals("BATADDBBOSSMEMBER") || batchOperType.equals("BATDELBBOSSMEMBER") || batchOperType.equals("BATCONBBOSSMEMBER") || batchOperType.equals("BATPASBBOSSMEMBER") || batchOperType.equals("BATMODBBOSSMEMBER"))
		{
			specialCheckForBBoss(condStr);
		}

		// 判断任务开始时间和结束时间年月跨度
		String startDate = inParam.getString("START_DATE", SysDateMgr.getSysTime());
		String endDate = inParam.getString("END_DATE", SysDateMgr.getLastDateThisMonth());

		if (!SysDateMgr.decodeTimestamp(startDate, "yyyy-MM").equals(SysDateMgr.decodeTimestamp(endDate, "yyyy-MM")))
		{
			CSAppException.apperr(TimeException.CRM_TIME_55);
		}

		// 批量任务号
		String batchTaskId = inParam.getString("BATCH_TASK_ID", SeqMgr.getBatchId());

		IData batTaskData = new DataMap();

		batTaskData.put("BATCH_TASK_ID", batchTaskId);
		batTaskData.put("BATCH_TASK_NAME", inParam.getString("BATCH_TASK_NAME"));
		batTaskData.put("BATCH_OPER_CODE", batchOperType);
		batTaskData.put("BATCH_OPER_NAME", batchTypeData.getString("BATCH_OPER_NAME"));
		batTaskData.put("CREATE_TIME", SysDateMgr.getSysTime());
		batTaskData.put("CREATE_STAFF_ID", inParam.getString("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()));
		batTaskData.put("CREATE_DEPART_ID", inParam.getString("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
		batTaskData.put("CREATE_CITY_CODE", inParam.getString("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()));
		batTaskData.put("CREATE_EPARCHY_CODE", inParam.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
		batTaskData.put("START_DATE", startDate);
		batTaskData.put("END_DATE", endDate);
		batTaskData.put("SMS_FLAG", inParam.getString("SMS_FLAG", "0"));
		batTaskData.put("REMARK", inParam.getString("REMARK", ""));
		batTaskData.put("AUDIT_NO", inParam.getString("AUDIT_NO", ""));
		batTaskData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchTaskId));
		String activeFlag = inParam.getString("ACTIVE_FLAG", "0");
		batTaskData.put("ACTIVE_FLAG", activeFlag);
		if ("1".equals(activeFlag))
		{
			batTaskData.put("ACTIVE_TIME", SysDateMgr.getSysTime());
		}

		// 批量条件, 分割插入
		for (int i = 0, size = condList.size(); i < size; i++)
		{
			batTaskData.put("CODING_STR" + (i + 1), condList.get(i));
		}


		// 条件判断2  --------REQ201910140021_关于和教育互动业务成员批量变更归属学校的需求
		if (batchOperType.equals("HEEDUFORSCHOOLS")){
			String condStrHe=inParam.getString("CODING_STR_HE");//新增  和教育互动业务用户批量变更归属学校

			if (StringUtils.isNotBlank(condStrHe))
			{
				condStrHe = codingtoWadeString(condStrHe);
			}

			IDataset condHeList = StrUtil.StringSubsection(condStrHe, Integer.parseInt("2000"));
			if (condHeList.size() > Integer.parseInt("5"))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "和教育互动业务用户批量变更归属学校 条件字符串太长!");
			}
			// 条件判断2  --------和教育互动业务用户批量变更归属学校
			for (int i = 0, size = condHeList.size(); i < size; i++)
			{
				batTaskData.put("CONDITION" + (i + Integer.parseInt("4")), condHeList.get(i));//和教育批量条件2 放在CODING_STR3中
			}
			// 批量条件2, 分割插入--------和教育互动业务用户批量变更归属学校
		}
		// 条件判断2  --------REQ201910140021_关于和教育互动业务成员批量变更归属学校的需求
		 //插入稽核人员工号	 
        if(!inParam.getString("AUDIT_STAFF_ID","0").equals("0")){	 	 
            batTaskData.put("CONDITION1", inParam.getString("AUDIT_STAFF_ID",""));	 	 
        }	 	 
        //插入凭证文件id	 	 
        if(!inParam.getString("MEB_VOUCHER_FILE_LIST","0").equals("0")){	 	 
            batTaskData.put("CONDITION2", inParam.getString("MEB_VOUCHER_FILE_LIST",""));	 	 
        }

		// 插入表
		boolean success = Dao.insert("TF_B_TRADE_BAT_TASK", batTaskData, Route.getJourDb(Route.CONN_CRM_CG));

		if (success == false)
		{
			CSAppException.apperr(BatException.CRM_BAT_71);
		}

		return batchTaskId;
	}

	/**
	 * @descripiton BBOSS产品成员批量需要检验集团的基本信息
	 * @author xunyl
	 * @date 2015-12-22
	 */
	private static void specialCheckForBBoss(String condStr) throws Exception
	{
		// 1- 批量条件非空校验
		if (StringUtils.isEmpty(condStr))
		{
			CSAppException.apperr(BatException.CRM_BAT_30);
		}

		// 2- 转化条件串
		IData condInfo = new DataMap(condStr);

		// 3- 获取用户编号、产品编号和集团编号
		String grpUserId = condInfo.getString("USER_ID", "");
		String productId = condInfo.getString("PRODUCT_ID", "");
		String groupId = condInfo.getString("GROUP_ID", "");

		// 4- 获取集团产品信息
		String merchPCode = AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(productId, "PRO");
		String merchCode = UpcCall.queryPospecnumberByProductspecnumber(merchPCode);
		IDataset merchpInfo = UserGrpMerchpInfoQry.qryMerchpInfoByGroupIdMerchScProductScUserId(grpUserId, groupId, merchCode, merchPCode);

		// 5- 集团产品信息不存在，异常提示
		if (IDataUtil.isEmpty(merchpInfo) || merchpInfo.size() > 1)
		{
			CSAppException.apperr(GrpException.CRM_GRP_401, groupId, merchPCode);
		}

		// 6- 集团产品订购关系不存在，异常提示
		if ("".equals(merchpInfo.getData(0).getString("PRODUCT_OFFER_ID", "")))
		{
			CSAppException.apperr(GrpException.CRM_GRP_21, groupId, merchPCode);
		}
	}

	/**
	 * 设置批量只在晚上20点到早上7点执行
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getBatDealDate() throws Exception
	{
		String nowDatetime = SysDateMgr.getSysTime();

		boolean nowRunFlag = BizEnv.getEnvBoolean("crm.bat.nowrun", false); // 批量业务是否立即启动开关

		if (nowRunFlag)
		{
			return nowDatetime;
		}

		String todayDateTime07 = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 7);// 早上7点
		String todayDateTime20 = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 20);// 晚上20

		if (nowDatetime.compareTo(todayDateTime07) < 0)
		{
			return nowDatetime;
		} else if (nowDatetime.compareTo(todayDateTime07) > 0 && nowDatetime.compareTo(todayDateTime20) < 0)
		{
			return todayDateTime20;
		} else
		{
			return nowDatetime;
		}
	}

	/**
	 * 获取指定日期日、月导入数量
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getNowDayCount(String batch_oper_type, String accept_date) throws Exception
	{

		return BatInfoQry.getNowDayCount(batch_oper_type, accept_date);
	}

	/**
	 * 查询批量任务
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static IData queryBatTask(IData data) throws Exception
	{

		IData taskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(data.getString("BATCH_TASK_ID"));

		taskInfo.put("BATCH_OPER_TYPE", taskInfo.getString("BATCH_OPER_CODE"));

		IData importedCount = getNowDayCount(taskInfo.getString("BATCH_OPER_CODE"), null);

		taskInfo.put("CLASS_MONTH_COUNT", importedCount.getString("MONTH_SUM", "0"));

		return taskInfo;
	}

	/**
	 * @Function: runUpTradeBat
	 * @Description: 启动批量任务更新批次表TF_B_TRADE_BAT
	 * @param：
	 * @return：void
	 * @throws：
	 * @version: v1.0.0
	 * @author: penghb@asiainfo-linkage.com
	 * @date: 下午7:20:49 2013-10-23 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-12 huanghui v1.0.0
	 */
	public static void runUpTradeBat(IData data) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE TF_B_TRADE_BAT T");
		sql.append(" SET T.ACTIVE_FLAG = :ACTIVE_FLAG, T.ACTIVE_TIME = TO_DATE(:ACTIVE_TIME,'yyyy-mm-dd hh24:mi:ss')");
		sql.append(" WHERE 1=1");
		sql.append(" AND T.BATCH_ID = TO_NUMBER(:BATCH_ID)");
		sql.append(" AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * @Function: runUpTradeBatDeal
	 * @Description: 启动批量任务更新处理表TF_B_TRADE_BATDEAL
	 * @param：
	 * @return：void
	 * @throws：
	 * @version: v1.0.0
	 * @author: penghb@asiainfo-linkage.com
	 * @date: 下午7:20:49 2013-10-23 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-12 huanghui v1.0.0
	 */
	public static void runUpTradeBatDeal(IData data) throws Exception
	{
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE TF_B_TRADE_BATDEAL T");
		sql.append(" SET T.DEAL_STATE = :DEAL_STATE, T.DEAL_TIME = DECODE(T.BATCH_OPER_TYPE,'SMSBUSINESSHANDING',SYSDATE+(ROWNUM-1)/24/60,TO_DATE(:DEAL_TIME,'yyyy-mm-dd hh24:mi:ss')) ");
		sql.append(" WHERE 1=1");
		sql.append(" AND T.BATCH_ID = TO_NUMBER(:BATCH_ID)");
		sql.append(" AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 对明细中错单进行重跑 deal_state = 6:接口调用失败 ,B,D add by xieyuan
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static void updateBatDealStartToRun(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_desc = :DEAL_DESC, a.deal_result = :DEAL_DESC, a.deal_time = DECODE(A.BATCH_OPER_TYPE,'SMSBUSINESSHANDING',SYSDATE+(ROWNUM-1)/24/60,sysdate) ");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.deal_state in ( ");
		sql.append(data.getString("DEAL_STATE_STR"));
		sql.append(" ) ");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 集团批量号码判重
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static void updateBatTradesSerialNumber(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_result = :DEAL_RESULT");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.serial_number in (select b.serial_number from tf_b_trade_batdeal b where 1=1 ");
		sql.append(" and b.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and b.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" group by b.serial_number having count(b.serial_number)>'1')");
		if (!"BATADDBBOSSMEMBER".equals(data.getString("BATCH_OPER_TYPE", "")))
			sql.append(" and a.deal_state = '0'");
		else
			sql.append(" and a.deal_state = '6'");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * Vpmn短号码判重
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static void updateBatTradesShortCode(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_result = :DEAL_RESULT");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.data5 in (select b.data5 from tf_b_trade_batdeal b where 1=1 ");
		sql.append(" and b.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and b.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and b.data1 = a.data1");
		sql.append(" group by b.data5 having count(b.data5)>'1')");
		sql.append(" and a.deal_state = '0'");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * v网和总机排重方式
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static void updateBatVpmnSuperTelSerialNumber(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_result = :DEAL_RESULT");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.serial_number in (select b.serial_number from tf_b_trade_batdeal b where 1=1 ");
		sql.append(" and b.deal_state in ('0','1','2','4')");
		sql.append(" group by b.serial_number having count(b.serial_number)>'1')");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 集团批量排重
	 * 
	 * @description
	 * @author xiaozp
	 * @date Sep 24, 2009
	 * @version 1.0.0
	 * @param data
	 * @throws Exception
	 */
	public static void updateDuplicate(IData data) throws Exception
	{

		// String batch_oper_type = data.getString("BATCH_OPER_TYPE", "");

		updateBatTradesSerialNumber(data);

		/*
		 * if ("BATADDVPMNMEM".equals(batch_oper_type) ||
		 * "BATADDSUPTELMEM".equals(batch_oper_type)) { // VPMN业务类型
		 * data.put("DEAL_RESULT", "在同类型批次中已经存在些号码");
		 * updateBatVpmnSuperTelSerialNumber(data); data.put("DEAL_RESULT",
		 * "此批次内该短号重复"); updateBatTradesShortCode(data); }
		 */
	}

	public void addBatchCount(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE tf_b_trade_bat a");
		sql.append(" SET a.BATCH_COUNT = a.BATCH_COUNT+1");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	// 批量任务删除
	public boolean batTaskNowDelete(IData input) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		param.put("REMOVE_TAG", "1");
		String batchId = input.getString("BATCH_ID");
		if (StringUtils.isBlank(batchId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "BATCH_ID");
		}
		param.put("BATCH_ID", batchId);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BAT", "UPD_BAT_REMOVETAG", param, Route.getJourDb(Route.CONN_CRM_CG));
		IData paramB = new DataMap();
		paramB.clear();
		paramB.put("BATCH_ID", batchId);
		paramB.put("CANCEL_TAG", "1");
		paramB.put("CANCEL_DATE", SysDateMgr.getSysTime());
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_BATDEAL_CANCELTAG", paramB, Route.getJourDb(Route.CONN_CRM_CG));
		return true;
	}

	/**
	 * @Function: batTaskNowRun
	 * @Description: 批量任务立即启动
	 * @param：
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 下午3:32:50 2013-7-12 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-12 huanghui v1.0.0
	 */
	public boolean batTaskNowRun(IData input) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		String batchId = input.getString("BATCH_ID");
		String batchTaskId = input.getString("BATCH_TASK_ID");
		if (StringUtils.isBlank(batchId))
		{
			CSAppException.apperr(BatException.CRM_BAT_66);
		}
		IDataset checkTaskLimitTime = CommparaInfoQry.getCommparaAllCol("CSM", "2001", input.getString("BATCH_OPER_TYPE"), CSBizBean.getTradeEparchyCode());
		if (checkTaskLimitTime != null && checkTaskLimitTime.size() > 0)
		{
			if ("1".equals(((IData) (checkTaskLimitTime.get(0))).getString("PARA_CODE3")))
			{
				IData temp = (IData) (checkTaskLimitTime.get(0));
				String day = SysDateMgr.getCurDay();
				if ((Integer.parseInt(temp.getString("PARA_CODE1")) <= Integer.parseInt(day)) && (Integer.parseInt(temp.getString("PARA_CODE2")) >= Integer.parseInt(day)))
				{
					CSAppException.apperr(BatException.CRM_BAT_65); // 当前时间不允许办理批量业务
				}
			}
		}
		IData attrParam = new DataMap();
		attrParam.clear();
		attrParam.put("BATCH_ID", batchId);
		// IDataset checkTask = CommparaInfoQry.queryTaskByBat(attrParam); //
		// 检查批量任务是否失效
		IDataset checkTask = new DatasetList();
		if (checkTask != null && checkTask.size() > 0)
		{
			if (SysDateMgr.getSysTime().compareTo(checkTask.getData(0).getString("END_DATE")) > 0)
			{
				CSAppException.apperr(BatException.CRM_BAT_67); // 该任务已失效
			}
		}
		IDataset checkBat = queryBatTaskBatchInfo(attrParam);
		if (!checkBat.getData(0).getString("AUDIT_STATE").equals("2") && !checkBat.getData(0).getString("AUDIT_STATE").equals("0"))
		{
			CSAppException.apperr(BatException.CRM_BAT_69);
		}
		param.put("ACTIVE_FLAG", "1");
		param.put("ACTIVE_TIME", SysDateMgr.getSysTime());
		param.put("BATCH_ID", batchId);
		param.put("BATCH_TASK_ID", batchTaskId);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BAT", "UPD_BAT_ACTIVEINFOS", param, Route.getJourDb(Route.CONN_CRM_CG));
		IData paramB = new DataMap();
		paramB.clear();
		paramB.put("BATCH_ID", batchId);
		paramB.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_1);
		paramB.put("DEAL_TIME", SysDateMgr.getSysTime());
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_BATDEAL_DEALINFOS", paramB, Route.getJourDb(Route.CONN_CRM_CG));
		return true;
	}

	/**
	 * @Function: batTaskOnTimeRun
	 * @Description: 批量任务预约启动
	 * @param： IData
	 * @return：boolean
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 下午3:32:50 2013-7-12 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-12 huanghui v1.0.0
	 */
	public boolean batTaskOnTimeRun(IData input) throws Exception
	{

		String activetime = input.getString("START_DATE") + " " + input.getString("START_TIME");
		if (StringUtils.isBlank(activetime))
		{
			CSAppException.apperr(BatException.CRM_BAT_68); // 预约启动时间不能为空
		}
		IData param = new DataMap();
		param.clear();
		String batchId = input.getString("BATCH_ID");
		String batchTaskId = input.getString("BATCH_TASK_ID");
		if (StringUtils.isBlank(batchId))
		{
			CSAppException.apperr(BatException.CRM_BAT_66);
		}
		IDataset checkTaskLimitTime = CommparaInfoQry.getCommparaAllCol("CSM", "2001", input.getString("BATCH_OPER_TYPE"), CSBizBean.getTradeEparchyCode());
		if (checkTaskLimitTime != null && checkTaskLimitTime.size() > 0)
		{
			if ("1".equals(((IData) (checkTaskLimitTime.get(0))).getString("PARA_CODE3")))
			{
				IData temp = (IData) (checkTaskLimitTime.get(0));
				String day = SysDateMgr.getCurDay();
				if ((Integer.parseInt(temp.getString("PARA_CODE1")) <= Integer.parseInt(day)) && (Integer.parseInt(temp.getString("PARA_CODE2")) >= Integer.parseInt(day)))
				{
					CSAppException.apperr(BatException.CRM_BAT_65); // 当前时间不允许办理批量业务
				}
			}
		}
		IData attrParam = new DataMap();
		attrParam.clear();
		attrParam.put("BATCH_ID", batchId);
		// IDataset checkTask = CommparaInfoQry.queryTaskByBat(attrParam); //
		// 检查批量任务是否失效
		IDataset checkTask = new DatasetList();
		if (checkTask != null && checkTask.size() > 0)
		{
			if (SysDateMgr.getSysTime().compareTo(checkTask.getData(0).getString("END_DATE")) > 0)
			{
				CSAppException.apperr(BatException.CRM_BAT_67); // 该任务已失效
			}
		}
		IDataset checkBat = queryBatTaskBatchInfo(attrParam);
		if (!checkBat.getData(0).getString("AUDIT_STATE").equals("2"))
		{
			CSAppException.apperr(BatException.CRM_BAT_69);
		}
		param.put("ACTIVE_FLAG", "0");
		param.put("ACTIVE_TIME", activetime);
		param.put("BATCH_ID", batchId);
		param.put("BATCH_TASK_ID", batchTaskId);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BAT", "UPD_BAT_ACTIVEINFOS", param, Route.getJourDb(Route.CONN_CRM_CG));
		IData paramB = new DataMap();
		paramB.clear();
		paramB.put("BATCH_ID", batchId);
		paramB.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_1);
		paramB.put("DEAL_TIME", SysDateMgr.getSysTime());
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_BATDEAL_DEALINFOS", paramB, Route.getJourDb(Route.CONN_CRM_CG));
		return true;
	}

	// 根据批次信息返销
	public int cancelByBatchid(IData input, Pagination pagination) throws Exception
	{
		return Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_CANCEL_BY_BATCH", input, Route.getJourDb(Route.CONN_CRM_CG));
	}

	// 根据服务号码信息返销
	public int cancelBySerialNum(IData input, Pagination pagination) throws Exception
	{
		return Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_CANCEL_BY_PK", input, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	// 根据批次信息返销，添加对实名制激活的校验
	public int cancelByBatid(IData input, Pagination pagination) throws Exception
	{
		return Dao.executeUpdateByCodeCode("TF_B_TRADE_BATDEAL", "UPD_CANCEL_BY_BATCHID", input, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	/**
	 * 批量排重
	 * 
	 * @description
	 * @author xiaozp
	 * @date Sep 24, 2009
	 * @version 1.0.0
	 * @param data
	 * @throws Exception
	 */
	public void checkDuplicate(String batch_id, String oper_type_code) throws Exception
	{

		IData data = new DataMap();
		data.put("BATCH_ID", batch_id);
		data.put("BATCH_OPER_TYPE", oper_type_code);
		data.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_6);
		data.put("DEAL_RESULT", "此次批量业务中存在重复服务号码");

		updateDuplicate(data);
	}
	
	private void checkUserDiscnt20170524(String in_coding_str) throws Exception
	{
		IDataset listSet = new DatasetList();
		IData codingdata = new DataMap(in_coding_str);
		listSet = codingdata.getDataset("SELECTED_ELEMENTS");
		int sum = 188;
		//是否订购20170524【0元送50G国内流量套餐】
		boolean isadd=false;
		//此次办理的可叠加的优惠
		int tradeaddcount = 0;    
		IDataset discntConfig = CommparaInfoQry.getCommparaAllColByParser("CSM","8888","20170524","0898");
		if(IDataUtil.isNotEmpty(discntConfig)&&discntConfig.size() > 0 ){
			sum = discntConfig.getData(0).getInt("PARA_CODE1",0);	//188！
		}
		if (logger.isDebugEnabled())
        	logger.debug(" >>>>>>>>>进入 BatDealBean(zx)>>>>>>>>> listSet:"+listSet);
		if (IDataUtil.isNotEmpty(listSet))
		{
			for (int i = 0; i < listSet.size(); i++)
			{
				IData data = listSet.getData(i);     
                String elementType = data.getString("ELEMENT_TYPE_CODE", "");     
                String modifyTag = data.getString("MODIFY_TAG", "");     
                String eleId = data.getString("ELEMENT_ID", "");
                if (logger.isDebugEnabled())
                	logger.debug(" >>>>>>>>>进入 BatDealBean(zx)>>>>>>>>> eleId:"+eleId+",modifyTag:"+modifyTag+",elementType:"+elementType);
				if ("D".equals(elementType) && "0".equals(modifyTag) && !"".equals(eleId))
				{
					IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM",eleId,"20170524","0898");
            		if(IDataUtil.isNotEmpty(discntConfigList)&&discntConfigList.size() > 0 ){

            			tradeaddcount += discntConfigList.getData(0).getInt("PARA_CODE1",0);
    	                
            		}
	                if ("20170524".equals(eleId))
	                {
	                	isadd = true;
	                }

				}
			}
		}
		if (logger.isDebugEnabled())
        	logger.debug(" >>>>>>>>>进入 BatDealBean(2)>>>>>>>>> isadd:"+isadd+",tradeaddcount:"+tradeaddcount);
		if ( isadd && tradeaddcount < sum )
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"您不满足订购【20170524】优惠,请选择对应套餐或取消订购!");  
		}
	}
	
	private String dealWeekendDiscnt(String acceptDate,String in_coding_str) throws Exception
	{
		IDataset listSet = new DatasetList();
		IData codingdata = new DataMap(in_coding_str);
		listSet = codingdata.getDataset("SELECTED_ELEMENTS");
		String startdate= this.getSaturday(acceptDate)+" 00:00:00";
		String enddate= this.getSunday(acceptDate)+" 23:59:59";
		boolean weekend = this.isWeekend(acceptDate);
		if(weekend)
		{
			startdate = acceptDate;
		}
		if (logger.isDebugEnabled())
        	logger.debug(" >>>>>>>>>进入 dealWeekendDiscnt(1)>>>>>>>>> listSet:"+listSet);
		if (IDataUtil.isNotEmpty(listSet))
		{
			for (int i = 0; i < listSet.size(); i++)
			{
				IData data = listSet.getData(i);     
                String elementType = data.getString("ELEMENT_TYPE_CODE", "");     
                String modifyTag = data.getString("MODIFY_TAG", "");     
                String eleId = data.getString("ELEMENT_ID", "");
                if ("D".equals(elementType) &&  modifyTag.equals(BofConst.MODIFY_TAG_ADD) && !"".equals(eleId))
				{
					IDataset weekendDiscnts=CommparaInfoQry.getCommPkInfo("CSM", "7615", eleId,CSBizBean.getUserEparchyCode());
					
					if (IDataUtil.isNotEmpty(weekendDiscnts)) {
						listSet.getData(i).put("START_DATE", startdate);
						listSet.getData(i).put("END_DATE", enddate);
						codingdata.put("SELECTED_ELEMENTS", listSet);
					}
				}
			}
		}
		if (logger.isDebugEnabled())
            logger.debug(">>>>> 进入 dealWeekendDiscnt(j)>>>>>codingdata.toString():"+codingdata.toString());	
		return codingdata.toString();
	}

	private void checkUserDiscntSvc(String in_coding_str) throws Exception
	{
		IDataset listSet = new DatasetList();
		IData codingdata = new DataMap(in_coding_str);
		listSet = codingdata.getDataset("SELECT_ELEMENTS");

		if (IDataUtil.isNotEmpty(listSet))
		{
			for (int i = 0; i < listSet.size(); i++)
			{
				IData data = listSet.getData(i);
				IDataset elements = data.getDataset("ELEMENTS");
				if (elements != null && elements.size() > 0)
				{
					for (int j = 0; j < elements.size(); j++)
					{
						IData element = elements.getData(j);

						String elementType = element.getString("ELEMENT_TYPE_CODE", "");
						String state = element.getString("STATE", "");
						String eleId = element.getString("ELEMENT_ID", "");
						String elementName = element.getString("ELEMENT_NAME", "");

						if ("D".equals(elementType) && "ADD".equals(state) && !"".equals(eleId))
						{
							IData param = new DataMap();
							param.put("DISCNT_CODE", eleId);

							IData result = UDiscntInfoQry.getDiscntInfoByPk(eleId);
							String discntType = "";
							if (result != null && result.size() > 0)
							{
								discntType = result.getString("DISCNT_TYPE_CODE");
							}

							if ("H".equals(discntType))
							{
								IDataset existSet = CommparaInfoQry.getCommparaAllCol("CSM", "913", eleId, "0898");
								if (existSet != null && existSet.size() > 0)
								{
									// log.error("589000","批量预约开户不允许办理该首月优惠套餐【"+elementName+"!】");
									CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量预约开户不允许办理该首月优惠套餐【" + elementName + "!】");
								}
							}
						}
					}
				}
			}
		}
	}

	// 批量导入部分待返销数据检查
	public IData fileImportCheck(IDataset dataset) throws Exception
	{
		IDataset succds = new DatasetList();
		IDataset failds = new DatasetList();
		for (int i = 0; i < dataset.size(); i++)
		{
			IData data = dataset.getData(i);

			boolean importResult = data.getBoolean("IMPORT_RESULT", true);
			if (!importResult)
			{
				failds.add(data);
				continue;
			}
			if (!StringUtils.isNumeric(data.getString("BATCH_ID")))
			{
				data.put("IMPORT_ERROR", "批次号格式不对!");
				failds.add(data);
				continue;
			}
			if (!StringUtils.isNumeric(data.getString("SERIAL_NUMBER")))
			{
				data.put("IMPORT_ERROR", "服务号码格式不对!");
				failds.add(data);
				continue;
			}

			succds.add(data);
		}

		IData returnData = new DataMap();
		returnData.put("SUCCDS", succds);
		returnData.put("FAILDS", failds);

		return returnData;
	}

	public IDataset getAdvanceFees(IData data) throws Exception
	{
		IDataset advanceFees = StaticUtil.getStaticList("ADVANCE_FEE");
		IDataset advanceFeeP = new DatasetList();
		if (IDataUtil.isNotEmpty(advanceFees))
		{
			for (int i = 0; i < advanceFees.size(); i++)
			{
				IData advanceFee = advanceFees.getData(i);
				String feeValue = advanceFee.getString("DATA_ID");
				String privKey = "ADVANCEFEE_" + feeValue;
				if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), privKey))
				{
					advanceFeeP.add(advanceFee);
				}
			}
		}

		return advanceFeeP;
	}

	/**
	 * 校验参数
	 * 
	 * @param inParam
	 * @param opertype
	 * @return
	 * @throws Exception
	 */
	public String getCheckParam(IData inParam, String opertype) throws Exception
	{
		String result = "";

		String batch_task_id = inParam.getString("BATCH_TASK_ID", "");
		if (StringUtils.isBlank(batch_task_id))
		{
			CSAppException.apperr(BatException.CRM_BAT_72);
		}

		String condStr = BatTradeInfoQry.getTaskCondString(batch_task_id);
		if (StringUtils.isBlank(condStr))
		{
			CSAppException.apperr(BatException.CRM_BAT_11);
		}

		IData datapara = new DataMap(condStr);

		if (StringUtils.equals("BATADDVPMNMEM", opertype))
		{
			result = datapara.getString("USER_ID", "");
			if (StringUtils.isBlank(result))
			{
				CSAppException.apperr(GrpException.CRM_GRP_485);
			}
			return result;
		}

		if (StringUtils.equals("MUSICRINGMEM", opertype))
		{
			result = datapara.getString("GROUP_ID", "");
			if (StringUtils.isBlank(result))
			{
				CSAppException.apperr(GrpException.CRM_GRP_483);
			}
			return result;
		}

		return result;
	}

	public IDataset getCommparaInfoEx(IData input) throws Exception
	{
		IDataset results = BatDealInfoQry.getCommparaInfoEx(input);
		return results;
	}

	public IDataset getDiscntCode(IData input) throws Exception
	{
		IDataset results = BatDealInfoQry.getDiscntCode(input);
		return results;
	}

	public IDataset getElementInfo(IData input) throws Exception
	{
		IData idata = new DataMap();
		idata.clear();
		idata.put("PACKAGE_ID", input.getString("PACKAGE_ID", ""));
		idata.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		idata.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		idata.put("ELEMENT_TYPE_CODE", "D");
		IDataset results = BatDealInfoQry.getElementInfo(idata);
		return results;
	}

	public IDataset getJoinCause(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		String strAwardTypeCode = input.getString("PARA_CODE2", "");
		IData param = new DataMap();
		param.clear();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "1019");
		param.put("PARA_CODE1", "LPZS");
		param.put("PARA_CODE2", strAwardTypeCode);
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		results = BatTradeInfoQry.getJoinCause(param);
		return results;
	}

	public IDataset getOperTypeBySpAndBiz(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		results = BatTradeInfoQry.getOperTypeBySpAndBiz(input);
		return results;
	}

	public IDataset getPackageInfo(IData input) throws Exception
	{
		IData idata = new DataMap();
		idata.clear();
		idata.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
		idata.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		idata.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		IDataset results = BatDealInfoQry.getPackageInfo(idata);
		return results;
	}

	private IDataset getSpiltSet(String source, int byteLength)
	{
		byte[] sByte = source.getBytes();
		char[] sChar = source.toCharArray();

		IDataset dataset = new DatasetList();

		if (sByte.length <= byteLength)
		{
			dataset.add(source);
		} else
		{
			int byleCount = 0;
			int first = 0;
			for (int i = 0; i < sChar.length; i++)
			{
				if ((int) sChar[i] > 0x80)
				{
					byleCount += 2;
				} else
				{
					byleCount += 1;
				}
				if (byleCount == byteLength)
				{
					if (first == 0)
					{
						dataset.add(new String(sChar, first, i + 1));
					} else
					{
						dataset.add(new String(sChar, first, i - first + 1));
					}
					first = i + 1;
					byleCount = 0;
				}
				if (byleCount == byteLength + 1)
				{
					if (first == 0)
					{
						dataset.add(new String(sChar, first, i));
						first = i;
					}

					else
					{
						dataset.add(new String(sChar, first, i - first));
						first = i;
					}

					i -= 1;
					byleCount = 0;
				}
			}
			if (byleCount != 0)
			{
				dataset.add(new String(sChar, first, sChar.length - first));
			}

		}

		return dataset;
	}

	/**
	 * 插批量主表信息
	 * 
	 * @param inParam
	 * @throws Exception
	 */
	public void importBatMain(IData inParam) throws Exception
	{

		IData data = new DataMap();

		data.put("AUDIT_REMARK", inParam.getString("AUDIT_REMARK", inParam.getString("BATCH_TASK_NAME")));
		data.put("ACTIVE_FLAG", inParam.getString("ACTIVE_FLAG", "0"));
		data.put("AUDIT_STATE", inParam.getString("AUDIT_STATE"));
		data.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID"));
		data.put("BATCH_ID", inParam.getString("BATCH_ID"));
		data.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_TYPE"));
		data.put("BATCH_OPER_NAME", inParam.getString("BATCH_OPER_NAME"));
		data.put("BATCH_COUNT", inParam.getString("BATCH_COUNT"));
		data.put("ACCEPT_DATE", SysDateMgr.getSysTime());
		data.put("STAFF_ID", inParam.getString("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()));
		data.put("DEPART_ID", inParam.getString("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
		data.put("CITY_CODE", inParam.getString("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()));
		data.put("EPARCHY_CODE", inParam.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
		data.put("TERM_IP", inParam.getString("TERM_IP", CSBizBean.getVisit().getRemoteAddr()));
		data.put("REMOVE_TAG", "0");
		data.put("AUDIT_STAFF_ID", inParam.getString("AUDIT_STAFF_ID", ""));
		data.put("AUDIT_INFO", inParam.getString("AUDIT_INFO"));
		data.put("IN_MODE_CODE", "0"); // 和湖南保持一致 插0
		data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(inParam.getString("BATCH_ID")));
		data.put("REMARK", inParam.getString("REMARK"));
		Dao.insert("TF_B_TRADE_BAT", data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 插批量主表信息 电子渠道统一查询退订用
	 * 
	 * @param inParam
	 * @throws Exception
	 */
	public void importBatMainForImp(IData inParam) throws Exception
	{

		IData data = new DataMap();

		data.put("AUDIT_REMARK", inParam.getString("BATCH_TASK_NAME"));
		data.put("ACTIVE_FLAG", inParam.getString("ACTIVE_FLAG", "0"));
		data.put("AUDIT_STATE", inParam.getString("AUDIT_STATE"));
		data.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID"));
		data.put("BATCH_ID", inParam.getString("BATCH_ID"));
		data.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_TYPE"));
		data.put("BATCH_OPER_NAME", inParam.getString("BATCH_OPER_NAME"));
		data.put("BATCH_COUNT", inParam.getString("BATCH_COUNT"));
		data.put("ACCEPT_DATE", SysDateMgr.getSysDate());
		data.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		data.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
		data.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
		data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		data.put("REMOVE_TAG", "0");
		data.put("AUDIT_STAFF_ID", inParam.getString("AUDIT_STAFF_ID", ""));
		data.put("AUDIT_INFO", inParam.getString("AUDIT_INFO"));
		data.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_1);
		data.put("ACTIVE_FLAG", "1");
		data.put("ACTIVE_TIME", SysDateMgr.getSysTime());
		data.put("IN_MODE_CODE", "0"); // 和湖南保持一致 插0
		data.put("REMARK", inParam.getString("REMARK"));
		data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(inParam.getString("BATCH_ID")));
		Dao.insert("TF_B_TRADE_BAT", data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 导入批量产品变更明细
	 * 
	 * @param dataset
	 * @param inParam
	 * @throws Exception
	 */
	public void importData(IDataset dataset, IData inParam) throws Exception
	{
		// 生成批次号
		String batchId = SeqMgr.getBatchId();

		inParam.put("BATCH_ID", batchId);

		String batch_oper_type = inParam.getString("BATCH_OPER_TYPE");

		inParam.put("DEAL_STATE", inParam.getString("DEAL_STATE", BatDealStateUtils.DEAL_STATE_0));

		inParam.put("DEAL_RESULT", inParam.getString("DEAL_RESULT", "导入成功"));

		// 导入批量操作明细
		inpDataBatDeal(dataset, inParam);

		// 导入排重
		this.checkDuplicate(batchId, batch_oper_type);

		// VPMN短号排重
		if (batch_oper_type.equals("BATADDVPMNMEM") || batch_oper_type.equals("VPMNCHANGEDSHORTCODE") || batch_oper_type.equals("SPECVPMNCHGSHORTCODE")
				|| batch_oper_type.equals("BATADDIMSVPMNMEBER"))
		{
			this.updateBatTradesVpmnShortCode(batchId, batch_oper_type);
		}

		//IMS固话绑定\IMS固话修改绑定的宽带账号排重
		if (batch_oper_type.equals("BATBINDKDFORDESKMEB") || batch_oper_type.equals("BATCBINDKDFORDESKMEB"))
		{
			this.updateBatTradesBroadBand(batchId, batch_oper_type);
		}
		
		//集团成员开户,做校验拦截
		if(batch_oper_type.equals("BATOPENGROUPMEM"))
		{
			String condStr= inParam.getString("CODING_STR","");
			if(StringUtils.isNotBlank(condStr))
			{
				IData codingData = new DataMap(condStr);
				String productId = codingData.getString("PRODUCT_ID","");
				if(StringUtils.isNotBlank(productId) && StringUtils.equals("801110", productId))
				{
					IData memCustInfo = codingData.getData("MEM_CUST_INFO");
					this.checkGrpCustPsptId(memCustInfo,dataset.size());
				}
			}
		}
		
		
		/** 插批量主表信息 */
		inParam.put("BATCH_COUNT", dataset.size());

		importBatMain(inParam);

		// -----------当批量业务是从ESOP系统调入时，更新批量明细为启动状态--start---------
		String esopTag = inParam.getString("ESOP_TAG", "");

		if ("ESOP".equals(esopTag))
		{
			IData data = new DataMap();

			data.put("BATCH_ID", batchId);

			data.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_1);

			data.put("DEAL_DESC", "等待预处理");

			this.updateBatDealByBatchIdSn(data);
		}
		// ------------------ end -----------------------------

		//在批量提交时登记稽核信息
		actGrpBizBaseAudit(inParam);
		
	}

	/**
	 * 导入批量产品变更明细 电子渠道统一查询退订用
	 * 
	 * @param dataset
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public String importDataForImp(IDataset dataset, IData inParam) throws Exception
	{

		// 生成批次号
		String batchId = SeqMgr.getBatchId();
		inParam.put("BATCH_ID", batchId);
		inParam.put("DEAL_STATE", inParam.getString("DEAL_STATE", BatDealStateUtils.DEAL_STATE_1));
		inParam.put("DEAL_RESULT", inParam.getString("DEAL_RESULT", "导入成功"));

		// 导入批量操作明细
		inpDataBatDeal(dataset, inParam);

		/** 插批量主表信息 */
		inParam.put("BATCH_COUNT", dataset.size());
		importBatMainForImp(inParam);
		return batchId;
	}

	// 本省企业服务代码信息导入 @yanwu
	public IDataset importSpInfoCSData(IData input) throws Exception
	{
		String provinceCode = input.getString("PROV_CODE");
		IDataset dataset = new DatasetList(); // 上传excel文件内容明细
		String fileId = input.getString("cond_STICK_LIST"); // 上传excel文件的编号
		String[] fileIds = fileId.split(",");
		ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
		IDataset failds = new DatasetList();
		int allCount = 0;
		for (String strfileId : fileIds)
		{
			IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/BATSPINFOCS.xml"));
			IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
			IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
			dataset.addAll(suc[0]);
			failds.addAll(err[0]);
			if (IDataUtil.isNotEmpty(dataset))
			{
				allCount = dataset.size() + failds.size();
			} else
			{
				allCount = failds.size();
			}
		}

		IDataset succds = new DatasetList();
		// IDataset failds = new DatasetList();
		StrBuilder strsBIZ_SP = new StrBuilder();
		for (int i = 0; i < dataset.size(); i++)
		{
			IData data = dataset.getData(i);

			String strIMPORT_ERROR = "";
			String strBC = data.getString("SERIAL_NUMBER");
			String strSC = data.getString("DATA2");

			IData result = new DataMap();
			result.clear();
			result.put("PROV_CODE", provinceCode);
			result.put("BIZ_CODE", strBC);
			result.put("SP_CODE", strSC);

			String strBIZ_SP = strBC + strSC;
			if (strsBIZ_SP.indexOf(strBIZ_SP) >= 0)
			{
				data.put("IMPORT_ERROR", "此企业信息导入列表重复!");
				failds.add(data);
				continue;
			}
			// strBIZ_SP = strBIZ_SP + ",";
			strsBIZ_SP.append(strBIZ_SP + ",");

			IDataset dtSet = CSAppCall.call("SS.SpInfoCSSVC.querySpInfoCSByPkRecordStatus", result);
			if (IDataUtil.isNotEmpty(dtSet))
			{
				data.put("IMPORT_ERROR", "此企业信息已存在!");
				failds.add(data);
				continue;
			}

			result.put("PROVINCE_CODE", provinceCode);
			result.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			result.put("UPDATE_DEPART_ID", getVisit().getDepartId());
			result.put("UPDATE_TIME", SysDateMgr.getSysTime());
			result.put("RECORD_DATE", SysDateMgr.getSysTime());
			result.put("RECORD_STATUS", "01");// 新增
			result.put("REMARK", "");

			result.put("RSRV_STR1", "");// data.getString("DATA19", "")
			result.put("RSRV_STR2", "");
			result.put("RSRV_STR3", "");
			result.put("RSRV_STR4", "");
			result.put("RSRV_STR5", "");
			result.put("RSRV_STR6", "");
			result.put("RSRV_STR7", "");
			result.put("RSRV_STR8", "");
			result.put("RSRV_STR9", "");
			result.put("RSRV_STR10", "");

			String strDATA = data.getString("DATA4", "");
			String strSP_TYPE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SPINFO_CS_SP_TYPE", strDATA });

			if (StringUtils.isEmpty(strSP_TYPE))
			{
				strIMPORT_ERROR = "代码类型错误";
			}

			strDATA = data.getString("DATA5", "");
			String strSP_ATTR = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SPINFO_CS_SP_ATTR", strDATA });

			if (StringUtils.isEmpty(strSP_ATTR))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",代码接入方式错误";
			}

			strDATA = data.getString("DATA7", "");
			String strSP_STATUS = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SPINFO_CS_SP_STATUS", strDATA });

			if (StringUtils.isEmpty(strSP_STATUS))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",企业状态错误";
			}

			strDATA = data.getString("DATA8", "");
			String strPROVINCE_NO = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SYMTHESIS_PROVINCE_CODE", strDATA });

			if (StringUtils.isEmpty(strPROVINCE_NO))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",代码接入省错误";
			}

			strDATA = data.getString("DATA9", "");
			String strOPE_CODE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_OPE_CODE", strDATA });

			if (StringUtils.isEmpty(strOPE_CODE))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",操作类型错误";
			}

			strDATA = data.getString("DATA10", "");
			if (strDATA.length() > 14)
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",上报时间错误,最大长度14位";
			}

			strDATA = data.getString("DATA11", "");
			String strTYPE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_TYPE", strDATA });

			if (StringUtils.isEmpty(strTYPE))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",类型错误";
			}

			strDATA = data.getString("DATA12", "");
			String strTRADE_TYPE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_TRADE_TYPE", strDATA });

			if (StringUtils.isEmpty(strTRADE_TYPE))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",集团客户行业类别错误";
			}

			strDATA = data.getString("DATA13", "");
			String strCLIENT_GRADE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_CLIENT_GRADE", strDATA });

			if (StringUtils.isEmpty(strCLIENT_GRADE))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",客户分级错误";
			}

			strDATA = data.getString("DATA14", "");
			String strCLIENT_ATTR = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_CLIENT_ATTR", strDATA });

			if (StringUtils.isEmpty(strCLIENT_ATTR))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",客户属性错误";
			}

			strDATA = data.getString("DATA15", "");
			String strBUSI_SCOPE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_BUSI_SCOPE", strDATA });

			if (StringUtils.isEmpty(strBUSI_SCOPE))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",业务范围错误";
			}

			strDATA = data.getString("DATA16", "");
			String strBIZ_STATUS = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_SPBIZ_STATUS", strDATA });

			if (StringUtils.isEmpty(strBIZ_STATUS))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",是否支持企业签名错误";
			}

			strDATA = data.getString("DATA18", "");
			String strSEND_TYPE = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
			{ "SP_SEND_TYPE", strDATA });

			if (StringUtils.isEmpty(strSEND_TYPE))
			{
				strIMPORT_ERROR = strIMPORT_ERROR + ",发送信息类型错误";
			}

			/*
			 * strDATA = data.getString("DATA19", ""); String strRSRV_STR1 =
			 * StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC",
			 * new String []{"TYPE_ID", "DATA_NAME"}, "DATA_ID", new String
			 * []{"SP_PORT_TYPE", strDATA});
			 * 
			 * if(StringUtils.isEmpty(strRSRV_STR1)){ strIMPORT_ERROR =
			 * strIMPORT_ERROR + ",端口业务类型错误"; }
			 */

			if (StringUtils.isNotEmpty(strIMPORT_ERROR))
			{
				data.put("IMPORT_ERROR", strIMPORT_ERROR);
				failds.add(data);
				continue;
			}

			result.put("BIZ_NAME", data.getString("DATA1"));
			result.put("SP_NAME", data.getString("DATA3"));
			result.put("SP_TYPE", strSP_TYPE);// data.getString("DATA4")
			result.put("SP_ATTR", strSP_ATTR);// data.getString("DATA5")
			result.put("SP_DESC", data.getString("DATA6"));
			result.put("SP_STATUS", strSP_STATUS);// data.getString("DATA7")
			result.put("PROVINCE_NO", strPROVINCE_NO);// data.getString("DATA8")
			result.put("OPE_CODE", strOPE_CODE);// data.getString("DATA9")
			result.put("REPORT_TIME", data.getString("DATA10"));
			result.put("TYPE", strTYPE);// data.getString("DATA11")
			result.put("TRADE_TYPE", strTRADE_TYPE);// data.getString("DATA12")
			result.put("CLIENT_GRADE", strCLIENT_GRADE);// data.getString("DATA13")
			result.put("CLIENT_ATTR", strCLIENT_ATTR);// data.getString("DATA14")
			result.put("BUSI_SCOPE", strBUSI_SCOPE);// data.getString("DATA15")
			result.put("BIZ_STATUS", strBIZ_STATUS);// data.getString("DATA16")
			result.put("CH_SIGN", data.getString("DATA17"));
			result.put("SEND_TYPE", strSEND_TYPE);// data.getString("DATA18")

			succds.add(result);
			// succds.add(result);
		}
		if (IDataUtil.isNotEmpty(succds))
		{
			// Dao.insert("TD_M_SPINFO_CS", succds);
			IData param = new DataMap();
			param.put("INDATA", succds);
			UpcCall.saveSpInfoCs(param);
		}

		// 如果效验结果是超过了月限量，则会返回提示信息
		String hint_message = "";

		IData returnInfo = new DataMap();
		returnInfo.put("DEAL_TYPE", "2");
		// returnInfo.put("BATCH_ID", "1234");
		returnInfo.put("HINT_MESSAGE", hint_message);

		returnInfo.put("DATASET_SIZE", allCount);
		returnInfo.put("SUCC_SIZE", succds.size());
		returnInfo.put("FAILD_SIZE", failds.size());
		if (failds != null && failds.size() > 0)
		{
			String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
			String fileName = "本省企业服务代码信息" + "导入失败文件.xls";
			File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
			{ failds }, "personserv", fileIdE, null, "import/bat/" + "BATSPINFOCS.xml");
			String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/import", fileName);
			String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

			returnInfo.put("FAILED_TYPE", "1");
			returnInfo.put("FILE_ID", errorFileId);
			returnInfo.put("ERROR_URL", errorUrl);
		}

		IDataset returnInfos = new DatasetList();
		returnInfos.add(returnInfo);
		return returnInfos;
	}

	// 批量OCS导入
	public void importOcsData(IData input) throws Exception
	{
		IDataset set = new DatasetList(); // 上传excel文件内容明细
		IDataset results = new DatasetList();
		String fileId = input.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
		String[] fileIds = fileId.split(",");
		ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
		for (String strfileId : fileIds)
		{
			IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/OcsUserImport.xml"));
			IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
			set.addAll(suc[0]);
		}

		String gprsBusi = input.getString("gprsBusi"); // GPRS业务
		String smsBusi = input.getString("smsBusi"); // 短信业务
		String wapBusi = input.getString("wapBusi"); // 梦网业务
		String voiceBusi = input.getString("voiceBusi"); // 语音业务
		String batchId = input.getString("BATCH_ID"); // 批次ID
		String remark = input.getString("REMARK"); // 备注
		String writeType = input.getString("cond_RADIO_CODE"); // 签约
		String monitorFlag = input.getString("cond_JKBZ_CODE"); // 监控标志
		String monitorRuleCode = input.getString("cond_JKGZ_CODE", "0000"); // 监控规则
		if (StringUtils.isEmpty(monitorRuleCode))
		{
			monitorRuleCode = "0000";
		}
		String enableTag = input.getString("cond_SXQX_CODE"); // 生效期限
		// String bizType = input.getString("BIZ_TYPE");//
		// 监控类型：1-语音，2-短信，3-GPRS，默认-0（全部业务）

		for (int i = 0; i < set.size(); i++)
		{
			IData result = new DataMap();
			result.clear();
			result.put("BATCH_ID", batchId);
			result.put("DEAL_ID", SeqMgr.getBatchId());
			result.put("SERIAL_NUMBER", set.getData(i).getString("SERIAL_NUMBER"));
			result.put("CHANNEL_NO", result.getString("SERIAL_NUMBER").substring(result.getString("SERIAL_NUMBER").length() - 1));
			result.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			result.put("WRITE_TYPE", writeType);
			result.put("ACCEPT_STAFF_ID", getVisit().getStaffId());
			result.put("ACCEPT_DEPART_ID", getVisit().getDepartId());
			result.put("ACCEPT_DATE", SysDateMgr.getSysTime());
			result.put("ACCEPT_MODE", "1"); // 默认插入1
			result.put("MONITOR_TYPE", "0");
			result.put("ENABLE_TAG", enableTag); // 生效方式
			result.put("DEAL_STATE", "0"); // 默认插入0
			result.put("REMARK", remark); // 备注
			result.put("USER_ID", set.getData(i).getString("DATA3"));

			if ("0".equals(enableTag))
			{
				result.put("START_DATE", SysDateMgr.getSysTime());
				result.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
			} else
			{
				result.put("START_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate()) + SysDateMgr.START_DATE_FOREVER);
				result.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
			}

			if ("0".equals(writeType) || "1".equals(writeType))
			{
				result.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
				result.put("MONITOR_FLAG", monitorFlag);
				result.put("MONITOR_RULE_CODE", monitorRuleCode);
				if (StringUtils.isNotBlank(gprsBusi))
				{
					String dealIdGprs = SeqMgr.getBatchId();
					IData tempA = new DataMap();
					tempA.putAll(result);
					tempA.put("DEAL_ID", dealIdGprs);
					tempA.put("BIZ_TYPE", gprsBusi);
					results.add(tempA);
				}
				if (StringUtils.isNotBlank(smsBusi))
				{
					String dealIdSms = SeqMgr.getBatchId();
					IData tempB = new DataMap();
					tempB.putAll(result);
					tempB.put("DEAL_ID", dealIdSms);
					tempB.put("BIZ_TYPE", smsBusi);
					results.add(tempB);
				}
				if (StringUtils.isNotBlank(wapBusi))
				{
					String dealIdWap = SeqMgr.getBatchId();
					IData tempC = new DataMap();
					;
					tempC.putAll(result);
					tempC.put("DEAL_ID", dealIdWap);
					tempC.put("BIZ_TYPE", wapBusi);
					results.add(tempC);
				}
				if (StringUtils.isNotBlank(voiceBusi))
				{
					String dealIdVoice = SeqMgr.getBatchId();
					IData tempD = new DataMap();
					tempD.putAll(result);
					tempD.put("DEAL_ID", dealIdVoice);
					tempD.put("BIZ_TYPE", voiceBusi);
					results.add(tempD);
				}
			}
		}

		Dao.insert("TF_B_OCS_BATDEAL", results);
	}

	// 批量不需要监控OCS导入
	public void importOcsNoNeedMonitorData(IData input) throws Exception
	{
		IDataset set = new DatasetList(); // 上传excel文件内容明细
		IDataset results = new DatasetList();
		String fileId = input.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
		String[] fileIds = fileId.split(",");
		ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
		for (String strfileId : fileIds)
		{
			IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/OCSUSERLIMIT.xml"));
			IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
			set.addAll(suc[0]);
		}

		String batchId = input.getString("BATCH_ID"); // 批次ID
		String remark = input.getString("REMARK");
		if (IDataUtil.isNotEmpty(set))
		{
			for (int i = 0; i < set.size(); i++)
			{
				String dealId = SeqMgr.getBatchId();
				IData result = new DataMap();
				result.clear();
				String serialNumber = set.getData(i).getString("SERIAL_NUMBER");
				// String serv_type = set.getData(i).getString("SERV_TYPE");
				// if("1".equals(serv_type)){
				// result.put("MONITOR_TYPE", "3");
				// }else if("0".equals(serv_type)){
				// result.put("MONITOR_TYPE", "1");
				// }else{
				// result.put("MONITOR_TYPE", "0");
				// }
				result.put("DEAL_ID", dealId);
				result.put("BATCH_ID", batchId);
				result.put("SERIAL_NUMBER", serialNumber);
				result.put("CHANNEL_NO", serialNumber.substring(serialNumber.length() - 1));
				result.put("ACCEPT_DATE", SysDateMgr.getSysTime());
				result.put("ACCEPT_STAFF_ID", getVisit().getStaffId());
				result.put("ACCEPT_DEPART_ID", getVisit().getDepartId());
				result.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(result.getString("BATCH_ID")));
				result.put("ACCEPT_MODE", "1"); // 默认插入1
				result.put("MONITOR_TYPE", "0"); // 监控类型：1-语音，2-短信，3-GPRS，默认-0（全部业务）
				result.put("DEAL_STATE", "0"); // 处理标志：0-等待处理，1-正在处理，2-处理成功，3-处理失败
				result.put("WRITE_TYPE", "2"); // 签约类型：0-签约，1-去签约，2-不需监控（白名单）
				result.put("ENABLE_TAG", "0"); // 默认立即生效
				String startDate = set.getData(i).getString("START_DATE");
				String endDate = set.getData(i).getString("END_DATE");
				result.put("START_DATE", startDate + SysDateMgr.getFirstTime00000());
				result.put("END_DATE", endDate + SysDateMgr.getEndTime235959());
				result.put("DEAL_STATE", "0"); // 默认插入0
				result.put("REMARK", remark); // 备注
				results.add(result);
			}
		}
		Dao.insert("TF_B_OCS_BATDEAL", results);
	}

	public IDataset initBatchId(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		IData param = new DataMap();
		String batchId = SeqMgr.getBatchId();
		param.put("BATCH_ID", batchId);
		results.add(param);
		return results;
	}

	/**
	 * 导入批量操作明细
	 * 
	 * @param dataset
	 * @param inParam
	 * @throws Exception
	 */
	public void inpDataBatDeal(IDataset dataset, IData inParam) throws Exception
	{

		IDataset batDealDatas = new DatasetList();

		String batch_oper_type = inParam.getString("BATCH_OPER_TYPE");
		String userId = getCheckParam(inParam, batch_oper_type);

		for (int i = 0, sz = dataset.size(); i < sz; i++)
		{

			IData data = dataset.getData(i);

			IData batDealData = new DataMap();

			String dateNow = SysDateMgr.getSysTime();// 当前时间

			batDealData.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID", SeqMgr.getBatchId()));
			batDealData.put("BATCH_ID", inParam.getString("BATCH_ID", SeqMgr.getBatchId()));
			batDealData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(inParam.getString("BATCH_ID")));
			batDealData.put("OPERATE_ID", inParam.getString("OPERATE_ID", SeqMgr.getBatchId()));
			batDealData.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_TYPE", ""));
			batDealData.put("PRIORITY", inParam.getString("PRIORITY", "500"));
			batDealData.put("REFER_TIME", data.getString("REFER_TIME", dateNow));
			batDealData.put("EXEC_TIME", data.getString("EXEC_TIME", dateNow));
			batDealData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
			batDealData.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode()));
			batDealData.put("DB_SOURCE", data.getString("DB_SOURCE", ""));
			batDealData.put("DATA1", data.getString("DATA1", ""));
			batDealData.put("DATA2", data.getString("DATA2", ""));
			batDealData.put("DATA3", data.getString("DATA3", ""));
			batDealData.put("DATA4", data.getString("DATA4", ""));
			batDealData.put("DATA5", data.getString("DATA5", ""));
			batDealData.put("DATA6", data.getString("DATA6", ""));
			batDealData.put("DATA7", data.getString("DATA7", ""));
			batDealData.put("DATA8", data.getString("DATA8", ""));
			batDealData.put("DATA9", data.getString("DATA9", ""));
			batDealData.put("DATA10", data.getString("DATA10", ""));
			batDealData.put("DATA11", data.getString("DATA11", ""));
			batDealData.put("DATA12", data.getString("DATA12", ""));
			batDealData.put("DATA13", data.getString("DATA13", ""));
			batDealData.put("DATA14", data.getString("DATA14", ""));
			batDealData.put("DATA15", data.getString("DATA15", ""));
			batDealData.put("DATA16", data.getString("DATA16", ""));
			batDealData.put("DATA17", data.getString("DATA17", ""));
			batDealData.put("DATA18", data.getString("DATA18", ""));
			batDealData.put("DATA19", data.getString("DATA19", ""));
			batDealData.put("DATA20", data.getString("DATA20", userId));
			batDealData.put("CANCEL_TAG", data.getString("CANCEL_TAG", "0"));
			batDealData.put("CANCEL_DATE", data.getString("CANCEL_DATE", ""));
			batDealData.put("CANCEL_STAFF_ID", data.getString("CANCEL_STAFF_ID", ""));
			batDealData.put("CANCEL_DEPART_ID", data.getString("CANCEL_DEPART_ID", ""));
			batDealData.put("CANCEL_CITY_CODE", data.getString("CANCEL_CITY_CODE", ""));
			batDealData.put("CANCEL_EPARCHY_CODE", data.getString("CANCEL_EPARCHY_CODE", ""));
			batDealData.put("DEAL_STATE", inParam.getString("DEAL_STATE", BatDealStateUtils.DEAL_STATE_0));
			batDealData.put("DEAL_TIME", data.getString("DEAL_TIME", dateNow));
			batDealData.put("DEAL_RESULT", inParam.getString("DEAL_RESULT", ""));
			batDealData.put("DEAL_DESC", data.getString("DEAL_DESC", ""));
			batDealData.put("TRADE_ID", data.getString("TRADE_ID", ""));

			batDealDatas.add(batDealData);
		}

		Dao.insert("TF_B_TRADE_BATDEAL", batDealDatas, Route.getJourDb(Route.CONN_CRM_CG));
	}

	public IDataset qryTaskDetial(IData data, Pagination pagination) throws Exception
	{
		IDataset set = BatInfoQry.qryTaskDetial(data, pagination);
		return set;
	}

	public IDataset queryAllBanks(IData data, Pagination pagination) throws Exception
	{
		IDataset set = BankInfoQry.getBankInfoNotCash(pagination);
		return set;
	}

	public IDataset queryAttrsByElement(IData cond, Pagination pagination) throws Exception
	{
		IDataset set = AttrItemInfoQry.getAttrItemAByIDTO(cond, null);
		for (int i = 0; i < set.size(); i++)
		{
			IData attra = set.getData(i);
			IDataset attrbs = AttrItemInfoQry.getItembByIdAndType(attra.getString("ID"), attra.getString("ID_TYPE"), attra.getString("ATTR_CODE"), CSBizBean.getTradeEparchyCode());
			attra.put("ATTRB", attrbs);
		}

		return set;
	}

	public IDataset queryBanks(IData data, Pagination pagination) throws Exception
	{
		String EPARCHY_CODE = data.getString("EPARCHY_CODE");
		String SUPER_BANK_CODE = data.getString("SUPER_BANK_CODE");
		String BANK_CODE = data.getString("BANK_CODE", "");
		String BANK = data.getString("BANK", "");
		if (BANK_CODE != null && (BANK == null || "".equals(BANK)))
		{
			BANK = BANK_CODE;
		}
		if (BANK != null && (BANK_CODE == null || "".equals(BANK_CODE)))
		{
			BANK_CODE = BANK;
		}
		IDataset set = BankInfoQry.getBankByBank(EPARCHY_CODE, SUPER_BANK_CODE, BANK_CODE, BANK, pagination);
		return set;
	}

	/**
	 * @Function: queryBatchInfo
	 * @Description: 批量任务信息查询
	 * @param： IData
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 上午10:46:07 2013-8-28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-28 huanghui v1.0.0
	 */
	public IDataset queryBatchInfo(IData cond, Pagination pagination) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryBatchInfo(cond, pagination);
		return returnResult;
	}

	public IDataset queryBatchInfoByTaskId(IData input, Pagination pagination) throws Exception
	{
		return BatTradeInfoQry.queryBatchInfo(input, pagination);
	}

	/**
	 * @Function: queryBatchTaskList
	 * @Description:
	 * @param: @param cond
	 * @param: @param pagination
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 5:53:07 PM May 29, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* May 29,
	 *        2013 tangxy v1.0.0 新建函数
	 */
	public IDataset queryBatchTaskList(IData cond, Pagination pagination) throws Exception
	{
		String checkPrivFlag = cond.getString("CHECK_PRIV_FLAG", "0");
		if (checkPrivFlag.equals("1"))
		{
			// 地洲权限
			// getVisit().hasPriv("BATTASK_EPARCHY")
			if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BATTASK_EPARCHY"))
			{
				cond.put("CREATE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			}
			// 业务区权限
			// getVisit().hasPriv("BATTASK_CITY")
			else if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BATTASK_CITY"))
			{
				cond.put("CREATE_CITY_CODE", getVisit().getCityCode());
			}
			// 工号权限
			// getVisit().hasPriv("BATTASK_STAFF")
			else if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BATTASK_STAFF"))
			{
				cond.put("CREATE_STAFF_ID", getVisit().getStaffId());
			}
			// 默认权限
			else
			{
				IDataset taginfos = TagInfoQry.getTagInfo(CSBizBean.getTradeEparchyCode(), "BATTASK_EPARCHYDEFAULTPRIV", "0", null);
				IData taginfo = new DataMap();
				if (IDataUtil.isNotEmpty(taginfos))
				{
					taginfo = taginfos.getData(0);
				}
				if (taginfo.getString("TAG_INFO", "-1").equals("BATTASK_EPARCHY"))
				{
					cond.put("CREATE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
				} else if (taginfo.getString("TAG_INFO", "-1").equals("BATTASK_CITY"))
				{
					cond.put("CREATE_CITY_CODE", getVisit().getCityCode());
				} else if (taginfo.getString("TAG_INFO", "-1").equals("BATTASK_STAFF"))
				{
					cond.put("CREATE_STAFF_ID", getVisit().getStaffId());
				} else
				{
					CSAppException.apperr(BatException.CRM_BAT_18);
				}
			}
		}
		IDataset returnResult = BatTradeInfoQry.queryBatchTaskList(cond, pagination);
		IDataset batDeleteReturnResult = new DatasetList();
		String batType = cond.getString("BAT_TYPE_PARAM");
		if (StringUtils.isNotBlank(batType) && "BATDELETE".equals(batType) && returnResult.size() > 0)
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData param = new DataMap();
				param.clear();
				param.put("BATCH_TASK_ID", ((IData) returnResult.get(i)).getString("BATCH_TASK_ID"));
				IDataset isRun = BatTradeInfoQry.queryBatchDealNotRunInfo(param);
				if (IDataUtil.isEmpty(isRun))
				{
					batDeleteReturnResult.add((IData) (returnResult.get(i)));
				}
			}
			return batDeleteReturnResult;
		}
		return returnResult;
	}

	// 查询可返销的业务类型
	public IDataset queryBatchType(IData input, Pagination pagination) throws Exception
	{
		return BatDealInfoQry.queryBatchType(input, pagination);
	}
	 public IDataset queryTaskId(IData input) throws Exception		 
    {	 	 
        return BatDealInfoQry.queryTaskId(input);	 	 
    }	 	 
    public void insertOrder(IData input) throws Exception	 	 
    {	 	 
         BatDealInfoQry.insertOrder(input);	 	 
    }	 	 
    public IDataset queryGroupName(IData input) throws Exception	 	 
    {	 	 
        return BatDealInfoQry.queryGroupName(input);	 	 
    }	 	 
    public IDataset queryGroupSn(IData input) throws Exception	 	 
    {	 	 
        return BatDealInfoQry.queryGroupSn(input);	 	 
    }	 	 
    public IDataset queryTypeCode(IData input) throws Exception	 	 
    {	 	 
        return BatDealInfoQry.queryTypeCode(input);	 	 
       	 	 
    }	 	 
   	 	 
    public IDataset queryTypeCodeByType(IData input) throws Exception	 	 
    {	 	 
        return BatDealInfoQry.queryTypeCodeByType(input);	 	 
       	 	 
    }

	public IDataset queryBatchTypeByCode(IData input) throws Exception
	{
		@SuppressWarnings("unused")
		String batchTypeCode = input.getString("BATCH_TYPE_CODE", "");
		IData batchType = BatTradeInfoQry.queryBatTypeByPK(input);

		IDataset rtSet = new DatasetList();
		rtSet.add(batchType);

		return rtSet;
	}

	public IDataset queryBatDeal(IData data, Pagination page) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryBatDeal(data, page);
		return returnResult;
	}

	public IDataset queryBatDealByBatchId(IData data, Pagination page) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryBatDealByBatchId(data, page);
		return returnResult;
	}

	/**
	 * @Function: queryBatDealBySN
	 * @Description: 批量结果信息查询
	 * @param： IData Pagination
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 上午10:48:51 2013-8-28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-28 huanghui v1.0.0
	 */
	public IDataset queryBatDealBySN(IData cond, Pagination pagination) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryBatDealBySN(cond, pagination);
		return returnResult;
	}

	/**
	 * @Function: queryBatTaskBatchInfo
	 * @Description: 查询批量信息批次信息
	 * @param： IData
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 下午5:21:05 2013-10-25 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-10-25 huanghui v1.0.0
	 */
	public IDataset queryBatTaskBatchInfo(IData input) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryBatTaskBatchInfo(input);
		return returnResult;
	}

	public IDataset queryBatTaskByPK(String batchTaskId) throws Exception
	{
		return BatTradeInfoQry.queryBatTasks(batchTaskId, null);
	}

	/**
	 * @Function: submitBatTask
	 * @Description:
	 * @param: @param input
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @throws Exception
	 * @date: 5:53:24 PM May 29, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* May 29,
	 *        2013 tangxy v1.0.0 新建函数
	 */
	public IDataset queryBatTaskDelete(IData input, Pagination pagination) throws Exception
	{
		input.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		input.put("STAFF_ID", input.getString("CREATE_STAFF_ID"));
		input.put("ACTIVE_FLAG", "1");
		return BatTradeInfoQry.queryNeedDeleteBatTradeC(input, pagination);
	}

	/**
	 * @Function: submitBatTask
	 * @Description:
	 * @param: @param input
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @throws Exception
	 * @date: 5:53:24 PM May 29, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* May 29,
	 *        2013 tangxy v1.0.0 新建函数
	 */
	public IDataset queryBatTaskStart(IData input, Pagination pagination) throws Exception
	{
		input.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		input.put("STAFF_ID", input.getString("CREATE_STAFF_ID"));
		return BatTradeInfoQry.queryNeedStartBatTradeC(input, pagination);
	}

	// 根据批次信息查询待返销订单
	public IDataset queryCancelBatByBatchInfo(IData input, Pagination pagination) throws Exception
	{
		return BatDealInfoQry.queryCancelBatByBatchInfo(input, pagination);
	}
	
	// 根据批次信息查询待返销订单,增加对实名制激活的校验
	public IDataset queryCancelBatByBatchInfos(IData input, Pagination pagination) throws Exception
	{
		return BatDealInfoQry.queryCancelBatByBatchInfos(input, pagination);
	}

	// 根据服务号码信息查询待返销订单
	public IDataset queryCancelBatBySerialInfo(IData input, Pagination pagination) throws Exception
	{
		return BatDealInfoQry.queryCancelBatBySerialInfo(input, pagination);
	}
	
	// 根据服务号码信息查询待返销订单,添加对实名制激活的校验
	public IDataset queryCancelBatBySerialInfos(IData input, Pagination pagination) throws Exception
	{
		return BatDealInfoQry.queryCancelBatBySerialInfos(input, pagination);
	}

	// 查询返销标识
	public IDataset queryCancelTag(IData input, Pagination pagination) throws Exception
	{
		return BatDealInfoQry.queryCancelTag(input, pagination);
	}

	public IDataset queryCommpara(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		IDataset returnResults = new DatasetList();
		IData param = new DataMap();
		param.clear();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "1017");
		param.put("PARA_CODE1", "LPZS");
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		results = BatTradeInfoQry.queryCommpara(param);
		if (IDataUtil.isNotEmpty(results))
		{
			for (int i = 0; i < results.size(); i++)
			{
				param = new DataMap();
				param.put("PARAM_CODE", ((IData) results.get(i)).getString("PARAM_CODE"));
				param.put("PARAM_NAME", ((IData) results.get(i)).getString("PARAM_NAME"));
				returnResults.add(param);
			}
		}
		return results;
	}

	/**
	 * @Function: queryDiscntInfo
	 * @Description: 批量优惠变更 优惠查询
	 * @param： IData
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 上午10:46:07 2013-8-28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-28 huanghui v1.0.0
	 */
	public IDataset queryDiscntInfo(IData cond, Pagination pagination) throws Exception
	{
		// IDataset returnResult = BatTradeInfoQry.queryDiscntInfo(cond,
		// pagination);
		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("D", cond.getString("DISCNT_CODE", ""), cond.getString("DISCNT_NAME", ""));
		IDataset returnds = new DatasetList();
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("DISCNT_CODE", data.getString("OFFER_CODE"));
				data.put("DISCNT_NAME", data.getString("OFFER_NAME"));
				if (IDataUtil.isNotEmpty(CommparaInfoQry.getCommNetInfo("CSM", "96", data.getString("OFFER_CODE"))))
				{
					returnds.add(data);
				}
			}

		}
		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnds.size();

		/*
		 * if (total > 0) { returnds.getData(0).put("TOTAL", total); }
		 */

		List<Object> list = returnds.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		if (IDataUtil.isNotEmpty(ds))
		{
			ds.getData(0).put("TOTAL", total);
		}
		return ds;
	}

	/**
	 * @Function: queryDiscntSpecInfo
	 * @Description: 批量优惠变更 优惠查询
	 * @param： IData
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 上午10:46:07 2013-8-28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-28 huanghui v1.0.0
	 */
	public IDataset queryDiscntSpecInfo(IData cond, Pagination pagination) throws Exception
	{
		// IDataset returnResult = BatTradeInfoQry.queryDiscntSpecInfo(cond,
		// pagination);
		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("D", cond.getString("DISCNT_CODE", ""), cond.getString("DISCNT_NAME", ""));
		IDataset returnds = new DatasetList();
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("DISCNT_CODE", data.getString("OFFER_CODE"));
				data.put("DISCNT_NAME", data.getString("OFFER_NAME"));
				if (IDataUtil.isNotEmpty(StaticInfoQry.getStaticInfoByTypeIdDataId("UNLIMITDISCNTCHG", data.getString("DISCNT_CODE"))))
				{
					returnds.add(data);
				}
			}

		}
		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnds.size();

		/*
		 * if (total > 0) { returnds.getData(0).put("TOTAL", total); }
		 */

		List<Object> list = returnds.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		if (IDataUtil.isNotEmpty(ds))
		{
			ds.getData(0).put("TOTAL", total);
		}
		return ds;
	}

	public IDataset queryDiscnts(IData cond, Pagination pagination) throws Exception
	{
		// IDataset returnResult = BatTradeInfoQry.queryDiscnts(cond,
		// pagination);
		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("D", cond.getString("ELEMENT_ID", ""), cond.getString("ELEMENT_NAME", ""));
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("ELEMENT_ID", data.getString("OFFER_CODE"));
				data.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
				data.put("ELEMENT_TYPE", "1");
				data.put("TOTAL", returnResult.size());
			}
		}
		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnResult.size();

		List<Object> list = returnResult.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);

		return ds;
	}

	public IDataset queryFaildInfo(IData data) throws Exception
	{
		return BatDealInfoQry.queryFaildInfo(data);
	}

	public IDataset queryG3NetCardType(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		IData inparam = new DataMap();
		inparam.clear();
		inparam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
		inparam.put("SUBSYS_CODE", "CSM");
		inparam.put("PARAM_ATTR", "1715");
		results = BatTradeInfoQry.queryG3NetCardType(inparam);
		return results;
	}

	/**
	 * @Function: queryNeedApproveBatTrades
	 * @Description:
	 * @param: @param input
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @throws Exception
	 * @date: 5:53:24 PM May 29, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* May 29,
	 *        2013 tangxy v1.0.0 新建函数
	 */
	public IDataset queryNeedApproveBatTrades(IData input, Pagination pagination) throws Exception
	{
		input.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		input.put("STAFF_ID", input.getString("CREATE_STAFF_ID"));
		return BatTradeInfoQry.queryNeedApproveBatTrades(input, pagination);
	}

	/**
	 * @Function: queryNeedDeleteBatTradeC
	 * @Description:
	 * @param: @param input
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @throws Exception
	 * @date: 5:53:24 PM May 29, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* May 29,
	 *        2013 tangxy v1.0.0 新建函数
	 */
	public IDataset queryNeedDeleteBatTradeC(IData input, Pagination pagination) throws Exception
	{
		input.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		input.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		input.put("STAFF_ID", input.getString("CREATE_STAFF_ID"));
		return BatTradeInfoQry.queryNeedDeleteBatTradeC(input, pagination);
	}

	public IDataset queryOcsDealInfo(IData input, Pagination pagination) throws Exception
	{
		IDataset results = new DatasetList();
		IData inparam = new DataMap();
		inparam.clear();
		String startDate = input.getString("START_DATE");
		String endDate = input.getString("END_DATE");
		String msisdn = input.getString("SERIAL_NUMBER");
		String batchId = input.getString("BATCH_ID");
		// String bizType = input.getString("BIZ_TYPE");
		inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		inparam.put("START_DATE", startDate);
		inparam.put("END_DATE", endDate);
		if (StringUtils.isNotBlank(msisdn))
		{
			inparam.put("SERIAL_NUMBER", msisdn);
		}
		if (StringUtils.isNotBlank(batchId))
		{
			inparam.put("BATCH_ID", batchId);
		}
		// if (StringUtils.isNotBlank(bizType))
		// {
		// inparam.put("BIZ_TYPE", bizType);
		// }
		results = BatDealInfoQry.queryOcsDealInfo(inparam, pagination);
		return results;
	}

	public IDataset queryPhoneByProduct(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		IData inparam = new DataMap();
		inparam.clear();
		String productId = input.getString("PRODUCT_ID", "");
		inparam.put("PRODUCT_ID", productId);
		inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
		inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		results = BatTradeInfoQry.queryG3PhoneByProductId(inparam);
		return results;
	}

	/**
	 * @Function: queryPlatInfo
	 * @Description: 批量平台业务代码查询
	 * @param：
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 下午1:56:37 2013-9-20 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-20 huanghui v1.0.0
	 */
	public IDataset queryPlatInfo(IData cond, Pagination pagination) throws Exception
	{
		// TODO UPC.Out.SpQueryFSV.querySpInfoBySpCodeAndBizTypeCodeAndSpName
		String spCode = cond.getString("SP_CODE");
		String spName = cond.getString("SP_NAME");
		String bizTypeCode = cond.getString("BIZ_TYPE_CODE");
		IDataset returnResult = UpcCall.querySpInfoBySpCodeAndBizTypeCodeAndSpName(spCode, spName, bizTypeCode, null);
		// IDataset returnResult = BatTradeInfoQry.queryPlatInfo(cond,
		// pagination);
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("TOTAL", returnResult.size());
			}
		}

		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnResult.size();

		List<Object> list = returnResult.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		return ds;
	}

	/**
	 * @Function: queryPopuTaskInfoBySn
	 * @Description: 根据号码查询批量任务信息
	 * @param： IData
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 上午10:46:07 2013-8-28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-28 huanghui v1.0.0
	 */
	public IDataset queryPopuTaskInfoBySn(IData cond, Pagination pagination) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryPopuTaskInfoBySn(cond, pagination);
		return returnResult;
	}

	/**
	 * @Function: queryPopuTaskInfoByTaskId
	 * @Description: 根据TAKSID查询批量任务信息
	 * @param： IData
	 * @return：IDataset
	 * @throws：Exception
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 上午10:46:07 2013-8-28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-28 huanghui v1.0.0
	 */
	public IDataset queryPopuTaskInfoByTaskId(IData cond, Pagination pagination) throws Exception
	{
		IDataset returnResult = BatTradeInfoQry.queryPopuTaskInfoByTaskId(cond, pagination);
		return returnResult;
	}

	public IDataset queryProductListNoLimit(IData input) throws Exception
	{
		input.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		input.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		IDataset results = BatDealInfoQry.queryProductListNoLimit(input);
		return results;
	}

	public IDataset queryResId(IData input) throws Exception
	{
		IDataset results = new DatasetList();
		IData inparam = new DataMap();
		inparam.clear();
		String packageId = input.getString("PACKAGE_ID", "");
		inparam.put("PACKAGE_ID", packageId);
		inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
		inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		results = BatTradeInfoQry.queryResId(inparam);
		return results;
	}

	/**
	 * @Function: querySerivceInfo
	 * @Description: 查询服务名称和编码
	 * @param：
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 下午3:18:23 2013-9-9 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-9 huanghui v1.0.0
	 */
	public IDataset querySerivceInfo(IData cond, Pagination pagination) throws Exception
	{
		// IDataset returnResult = BatTradeInfoQry.querySerivceInfo(cond,
		// pagination);
		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("S", cond.getString("SERVICE_ID", ""), cond.getString("SERVICE_NAME", ""));
		IDataset returnds = new DatasetList();
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("SERVICE_ID", data.getString("OFFER_CODE"));
				data.put("SERVICE_NAME", data.getString("OFFER_NAME"));
				if (IDataUtil.isNotEmpty(CommparaInfoQry.getCommparaByAttrCode1("CSM", "991", data.getString("SERVICE_ID"), "0898", null)))
				{
					returnds.add(data);
				}
			}

		}
		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnds.size();

		/*
		 * if (total > 0) { returnds.getData(0).put("TOTAL", total); }
		 */

		List<Object> list = returnds.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		if (IDataUtil.isNotEmpty(ds))
		{
			ds.getData(0).put("TOTAL", total);
		}
		return ds;
	}

	public IDataset querySerivceSpecInfo(IData cond, Pagination pagination) throws Exception
	{
		// IDataset returnResult = BatTradeInfoQry.querySerivceSpecInfo(cond,
		// pagination);
		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("S", cond.getString("SERVICE_ID", ""), cond.getString("SERVICE_NAME", ""));
		IDataset returnds = new DatasetList();
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("DATA_ID", data.getString("OFFER_CODE"));
				data.put("DATA_NAME", data.getString("OFFER_NAME"));
				if (IDataUtil.isNotEmpty(StaticInfoQry.getStaticInfoByTypeIdDataId("UNLIMITSERVICECHG", data.getString("DATA_ID"))))
				{
					returnds.add(data);
				}
			}

		}
		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnds.size();

		/*
		 * if (total > 0) { returnds.getData(0).put("TOTAL", total); }
		 */

		List<Object> list = returnds.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		if (IDataUtil.isNotEmpty(ds))
		{
			ds.getData(0).put("TOTAL", total);
		}
		return ds;
	}

	public IDataset queryServiceInfoForPlat(IData cond, Pagination pagination) throws Exception
	{
		// TODO UPC.Out.SpQueryFSV.querySpServiceAndOfferByCond
		String serviceId = cond.getString("SERVICE_ID");
		String serviceName = cond.getString("SERVICE_NAME");
		String bizTypeCode = cond.getString("BIZ_TYPE_CODE");
		String spCode = cond.getString("SP_CODE");

		IDataset returnResult = new DatasetList();
		// IDataset returnResult = BatTradeInfoQry.queryServiceInfoForPlat(cond,
		// pagination);

		returnResult = UpcCall.querySpServiceAndOfferByCond(serviceId, serviceName, bizTypeCode, spCode, null);

		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("TOTAL", returnResult.size());
			}
		}

		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnResult.size();

		List<Object> list = returnResult.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		return ds;
	}

	public IDataset queryServices(IData cond, Pagination pagination) throws Exception
	{
		// IDataset returnResult = BatTradeInfoQry.queryServices(cond,
		// pagination);
		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("S", cond.getString("ELEMENT_ID", ""), cond.getString("ELEMENT_NAME", ""));
		if (IDataUtil.isNotEmpty(returnResult))
		{
			for (int i = 0; i < returnResult.size(); i++)
			{
				IData data = returnResult.getData(i);
				data.put("ELEMENT_ID", data.getString("OFFER_CODE"));
				data.put("ELEMENT_NAME", data.getString("OFFER_NAME"));
				data.put("ELEMENT_TYPE", "2");
				data.put("TOTAL", returnResult.size());
			}
		}

		int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = returnResult.size();

		List<Object> list = returnResult.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		return ds;

	}

	public IDataset queryUsersByBank(IData data, Pagination pagination) throws Exception
	{
		IDataset set = AcctInfoQry.queryUsersByBank(data, pagination);
		for (int i = 0; i < set.size(); i++)
		{
			IData temp = set.getData(i);
			temp.put("BANK_CODE", data.getString("BANK_CODE"));
			temp.put("BANK_ACCT_NO", data.getString("BANK_ACCT_NO"));
		}
		return set;
	}

	public IDataset queryUsersByBank2(IData data, Pagination pagination) throws Exception
	{
		IDataset set = AcctInfoQry.queryUsersByBank2(data, pagination);
		for (int i = 0; i < set.size(); i++)
		{
			IData temp = set.getData(i);
			temp.put("BANK_CODE", data.getString("BANK_CODE"));
			temp.put("BANK_ACCT_NO", data.getString("BANK_ACCT_NO"));
		}

		return set;
	}

	@SuppressWarnings("static-access")
	public boolean setOcsBusi(IData input) throws Exception
	{
		String msisdn = input.getString("SERIAL_NUMBER"); // 手机号码
		String channelNo = input.getString("CHANNEL_NO"); // 渠道号
		String staffId = input.getString("ACCEPT_STAFF_ID"); // 雇员ID
		String departId = input.getString("ACCEPT_DEPART_ID"); // 部门ID
		String writeType = input.getString("WRITE_TYPE"); // 签约模式
		String enableTag = input.getString("ENABLE_TAG"); // 生效模式
		String monitorFlag = input.getString("MONITOR_FLAG", ""); // 监控标志
		String monitorRuleCode = input.getString("MONITOR_RULE_CODE", ""); // 监控规则
		IData bizTypes = input.getData("BIZ_TYPES");
		SeqMgr batchSeq = new SeqMgr();
		String batchId = batchSeq.getBatchId();
		IData params = new DataMap();
		params.clear();
		params.put("BATCH_ID", batchId);
		params.put("SERIAL_NUMBER", msisdn);
		params.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		params.put("WRITE_TYPE", writeType);
		params.put("ACCEPT_STAFF_ID", staffId);
		params.put("ACCEPT_DEPART_ID", departId);
		params.put("ACCEPT_DATE", SysDateMgr.getSysTime());
		params.put("DEAL_STATE", "0"); // 默认插入0
		params.put("CHANNEL_NO", channelNo); // 默认插入0
		params.put("ACCEPT_MODE", "1"); // 默认插入1
		params.put("MONITOR_TYPE", "1");
		params.put("ENABLE_TAG", enableTag); // 生效模式
		if ("0".equals(writeType))
		{
			params.put("MONITOR_FLAG", monitorFlag);
			params.put("MONITOR_RULE_CODE", monitorRuleCode);
		}
		if (IDataUtil.isEmpty(bizTypes))
		{
			SeqMgr dealSeq = new SeqMgr();
			String dealId = dealSeq.getBatchId();
			params.put("DEAL_ID", dealId);
			params.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(dealId));
			Dao.insert("TF_B_OCS_BATDEAL", params);
			return true;
		}
		IDataset results = new DatasetList();
		if (IDataUtil.isNotEmpty(bizTypes))
		{
			if (StringUtils.isNotBlank(bizTypes.getString("VOICEBUSI")) && "1".equals(bizTypes.getString("VOICEBUSI")))
			{
				SeqMgr dealSeqA = new SeqMgr();
				String dealIdVoice = dealSeqA.getBatchId();
				IData tempA = new DataMap();
				tempA.putAll(params);
				tempA.put("DEAL_ID", dealIdVoice);
				tempA.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(dealIdVoice));
				tempA.put("BIZ_TYPE", "1");
				results.add(tempA);
			}
			if (StringUtils.isNotBlank(bizTypes.getString("SMSBUSI")) && "2".equals(bizTypes.getString("SMSBUSI")))
			{
				SeqMgr dealSeqB = new SeqMgr();
				String dealIdSms = dealSeqB.getBatchId();
				IData tempB = new DataMap();
				tempB.putAll(params);
				tempB.put("DEAL_ID", dealIdSms);
				tempB.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(dealIdSms));
				tempB.put("BIZ_TYPE", "2");
				results.add(tempB);
			}
			if (StringUtils.isNotBlank(bizTypes.getString("GPRSBUSI")) && "3".equals(bizTypes.getString("GPRSBUSI")))
			{
				SeqMgr dealSeqC = new SeqMgr();
				String dealIdGprs = dealSeqC.getBatchId();
				IData tempC = new DataMap();
				tempC.putAll(params);
				tempC.put("DEAL_ID", dealIdGprs);
				tempC.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(dealIdGprs));
				tempC.put("BIZ_TYPE", "3");
				results.add(tempC);
			}
			if (StringUtils.isNotBlank(bizTypes.getString("WAPBUSI")) && "4".equals(bizTypes.getString("WAPBUSI")))
			{
				SeqMgr dealSeqD = new SeqMgr();
				String dealIdWap = dealSeqD.getBatchId();
				IData tempD = new DataMap();
				tempD.putAll(params);
				tempD.put("DEAL_ID", dealIdWap);
				tempD.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(dealIdWap));
				tempD.put("BIZ_TYPE", "4");
				results.add(tempD);
			}
		}
		Dao.insert("TF_B_OCS_BATDEAL", results);
		return true;
	}

	/**
	 * @Function: batTaskNowAudit
	 * @Description: 批量任务审核
	 * @param：
	 * @return：boolean
	 * @throws：
	 * @version: v1.0.0
	 * @author: huanghui@asiainfo-linkage.com
	 * @date: 下午3:01:45 2013-8-24 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-8-24 huanghui v1.0.0
	 */
	public boolean subAudit(IData input) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		param.put("BATCH_ID", input.getString("BATCH_ID"));
		String audit_state = "";
		String auditInfo = input.getString("AUDIT_INFO");
		String remark = input.getString("AUDIT_REMARK");
		String batchId = input.getString("BATCH_ID");
		String batchTaskId = input.getString("BATCH_TASK_ID");
		if (auditInfo.equals("0"))
		{
			audit_state = "3"; // 不通过
		} else
		{
			audit_state = "2"; // 通过
		}
		// 如果审批通过，提示无需审批
		IDataset checkBat = queryBatTaskBatchInfo(param);
		if (IDataUtil.isNotEmpty(param))
		{
			if ("2".equals(((IData) (checkBat.get(0))).getString("AUDIT_STATE")))
			{
				CSAppException.apperr(BatException.CRM_BAT_70);
			}
		}
		String audit_date = SysDateMgr.getSysTime();
		String audit_depart_id = CSBizBean.getVisit().getDepartId();
		IData temp = new DataMap();
		temp.put("AUDIT_STAFF_ID", CSBizBean.getVisit().getStaffId());
		temp.put("AUDIT_INFO", remark);
		temp.put("BATCH_ID", batchId);
		temp.put("AUDIT_STATE", audit_state);
		temp.put("AUDIT_DATE", audit_date);
		temp.put("AUDIT_DEPART_ID", audit_depart_id);
		temp.put("BATCH_TASK_ID", batchTaskId);
		Dao.executeUpdateByCodeCode("TF_B_TRADE_BAT", "UPD_BAT_AUDITINFOS", temp, Route.getJourDb(Route.CONN_CRM_CG));
		return true;
	}

	public IDataset submitBatTask(IData input) throws Exception
	{
		String codeStr = input.getString("CODEINGSTR", "");
		String conding2 = input.getString("CONDITION2", "");
		IDataset results = new DatasetList();
		IData result = new DataMap();
		IData data = new DataMap();
		data.put("BATCH_TASK_ID", SeqMgr.getBatchId());
		data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("BATCH_TASK_ID")));
		// data.put("CREATE_TIME", SysDateMgr.getSysDate());
		data.put("CREATE_TIME", SysDateMgr.getSysTime());
		data.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		data.put("CREATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		data.put("CREATE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		data.put("BATCH_OPER_CODE", input.getString("BATCH_OPER_CODE", ""));
		data.put("BATCH_OPER_NAME", input.getString("BATCH_OPER_NAME", ""));
		data.put("CREATE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
		data.put("BATCH_TASK_NAME", input.getString("BATCH_TASK_NAME", ""));
		data.put("START_DATE", input.getString("START_DATE", ""));
		String endDate = input.getString("END_DATE", "");
		if (!"".equals(endDate))
		{
			endDate += SysDateMgr.getEndTime235959();
		}
		data.put("END_DATE", endDate);
		data.put("REMARK", input.getString("REMARK", ""));
		data.put("AUDIT_NO", input.getString("AUDIT_NO", ""));
		data.put("SMS_FLAG", data.getString("SMS_FLAG", "0"));// 默认不发短信
		data.put("CONDITION1", input.getString("PRODUCT_ID", ""));
		data.put("CONDITION2", conding2);

		String batchOperCode = data.getString("BATCH_OPER_CODE", "");
		/**********************************************************/
		// add by zhangxing3 at 20170621
		// REQ201705150024关于流量周末惠开发的需求
		if ( "CREATEPREUSER".equals(batchOperCode))
		{
			if (data != null && data.size() > 0)
			{
				codeStr=this.dealWeekendDiscnt(data.getString("CREATE_TIME", ""),codeStr);
			}
		}
		/***************************************************************************************/
		if (codeStr != null && codeStr.length() > 0)
		{
			int ilen = 2000;
			IDataset dataset = getSpiltSet(codeStr, ilen);

			if (dataset.size() > 5)
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入串长度过大： CODING_STR > 10000!!");// common.error("传入串长度过大： CODING_STR > 10000!!");
				// //
			}
			for (int i = 0; i < dataset.size(); i++)
			{
				data.put("CODING_STR" + (i + 1), dataset.get(i));
			}
		}
		/**************************************** ADD BY XYC ***********************************************/
		// add by wangyf6 at 20130819
		// 批量预开户首月优惠的拦截
		
		if ("CREATEPREUSER".equals(batchOperCode))
		{
			if (data != null && data.size() > 0)
			{
				this.checkUserDiscntSvc(codeStr);
			}
		}
		/***************************************************************************************/
		// add by zhangxing3 at 20170621
		// REQ201705020028批量预开户超套限速不限量校验
		if ("10004445".equals(input.getString("PRODUCT_ID", "")) && "CREATEPREUSER".equals(batchOperCode))
		{
			if (data != null && data.size() > 0)
			{
				this.checkUserDiscnt20170524(codeStr);
			}
		}
		/***************************************************************************************/

		Dao.insert("TF_B_TRADE_BAT_TASK", data, Route.getJourDb(Route.CONN_CRM_CG)); // 新建任务插入CEN库
		// modify
		// by
		// xiangyc
		result.put("BATCH_TASK_ID", data.getString("BATCH_TASK_ID"));
		results.add(result);
		return results;
	}

	public void updateBatDealByBatchIdAndSn(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_time = SYSDATE");
		sql.append(" ,a.DEAL_RESULT = :DEAL_RESULT");
		sql.append(" ,a.DEAL_DESC = :DEAL_DESC");
		sql.append(" ,a.trade_id = :TRADE_ID");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.serial_number = :SERIAL_NUMBER");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * 通过BatchID、SerialNumber更新未启动批量详情表,更新deal_state/deal_result add by xieyuan
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void updateBatDealByBatchIdSn(IData data) throws Exception
	{

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_time = SYSDATE");
		sql.append(" ,a.DEAL_RESULT = :DEAL_RESULT");
		sql.append(" ,a.DEAL_DESC = :DEAL_DESC");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.deal_state = '0'");
		sql.append(" and a.serial_number = :SERIAL_NUMBER");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}

	/**
	 * VPMN短号排重
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void updateBatTradesVpmnShortCode(String batchId, String batch_oper_type) throws Exception
	{
		IData data = new DataMap();
		data.put("BATCH_ID", batchId);
		data.put("BATCH_OPER_TYPE", batch_oper_type);
		data.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_6); // B状态被占用 改成6状态
		data.put("DEAL_RESULT", "此次批量业务中存在重复短号码");

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_result = :DEAL_RESULT");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.data1 in (select b.data1 from tf_b_trade_batdeal b where 1=1 ");
		sql.append(" and b.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and b.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" group by b.data1 having count(b.data1)>'1')");
		sql.append(" and a.deal_state = '0'");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}
	public String getSaturday(String acceptDate)throws Exception
	{
		Calendar c1 = Calendar.getInstance();
		c1.setTime(SysDateMgr.string2Date(acceptDate,SysDateMgr.PATTERN_STAND_YYYYMMDD));
		int week= c1.get(Calendar.DAY_OF_WEEK);		
		if(week==1)
		{			
			c1.add(Calendar.DATE, -1);
			return SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		}
		else{ 

			c1.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
			return SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		}
	}
	public String getSunday(String acceptDate)throws Exception
	{
		Calendar c1 = Calendar.getInstance();
		c1.setTime(SysDateMgr.string2Date(acceptDate,SysDateMgr.PATTERN_STAND_YYYYMMDD));
		int week= c1.get(Calendar.DAY_OF_WEEK);
		c1.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		if (week==1)
		{
			return SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		}
		else
			return SysDateMgr.addDays(SysDateMgr.date2String(c1.getTime(),SysDateMgr.PATTERN_STAND_YYYYMMDD),7);
	}
	
	public boolean isWeekend(String acceptDate)throws Exception
	{
		Calendar c1 = Calendar.getInstance();
		c1.setTime(SysDateMgr.string2Date(acceptDate,SysDateMgr.PATTERN_STAND_YYYYMMDD));
		int week= c1.get(Calendar.DAY_OF_WEEK);
		if ( week >=2 && week <= 6 )
		    return false;
		else
			return true;
	}
	
	
	/**
	 * 判断批量是否已经打印
	 * @param data
	 * @return
	 * @throws Exception
	 * @author  zhuoyingzhi
	 * @date 20180724
	 */
	public IData checkBatTask(IData data) throws Exception
	{
		String runInfo = "";
		IDataset reqInfo = new DatasetList();
		if(data.get("PARAM")!=null){
			runInfo = data.get("PARAM").toString();
			reqInfo = new DatasetList(runInfo);
		}else {
			//集团批量任务
			reqInfo.add(data);
		}
        
        IData  result=new DataMap();
        String msg="";
        String printTag="";
		for (int i = 0; i < reqInfo.size(); i++) {
			IData param = new DataMap();
			IData info = (IData) reqInfo.get(i);
			String batchId = info.getString("BATCH_ID");
			// 批量当中,TRADE_ID对应的是batchId
			param.put("TRADE_ID", batchId);

			IDataset list = new DatasetList();
			// code_code 已经存在
			list = Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO","QRY_CNOTE_BY_TRADE_ID", param, Route.getJourDb());
			if (IDataUtil.isNotEmpty(list)) {
				IData obj = list.getData(0);
				String rsrvTag2 = obj.getString("RSRV_TAG2", "");
				if ("1".equals(rsrvTag2)) {
					// 已经打印
					printTag = printTag+"|1";
				} else {
					msg = msg + "批次号：" + batchId + ",未打印电子工单或东软未回调接口.";
					printTag = printTag+"|0";
				}
			}else{
				//801110	IMS 语音,除了这个产品，其他产品都不打印电子工单
				printTag = printTag+"|1";
			}
		}
		
		
		if(printTag.indexOf("|0") >= 0){
			//存在 未打印或打印信息不存在的
			result.put("printTag", "0");
		}else{
			result.put("printTag", "1");
		}
		
		result.put("msgInfo", msg);
		
	  return result;
	}	
	
	/*
     * REQ201807240010++新增批量开户界面人像比对、受理单打印优化需求 by mqx 20190117
     * 记录信息到tf_b_trade_cnote_info表
     */
	public void insertIntoTradeCnoteInfoBat(IData data) throws Exception
	{
		IData param = new DataMap();
        
		
        //获取批次号
        String batchTaskId=data.getString("BATCH_TASK_ID", "");
        String batOperType=data.getString("BATCH_OPER_TYPE", "");
        
        IDataset result = BatInfoQry.qryBatByBatchTaskId(batchTaskId);
        String batchId=result.getData(0).getString("BATCH_ID","");
        //在打印电子受理单的时候billid传给东软的值也是  批量次号
        param.put("TRADE_ID", batchId);
        
        
        String acceptTime = SysDateMgr.getSysTime();
        String acceptMonth = acceptTime.substring(5, 7);
        
        param.put("ACCEPT_MONTH", acceptMonth);
        
        param.put("NOTE_TYPE", "1");//票类型
        
        
        String receiptInfo1 = "受理员工："+CSBizBean.getVisit().getStaffId()+"   业务受理时间："+acceptTime;
        String receiptInfo2="";
        
        String remark="";
		

        if("BATOPENGROUPMEM".equals(batOperType)){
        	//IMS批量开户
        	remark="IMS批量开户,打印电子工单";
        	
        	receiptInfo1 =receiptInfo1 + "~~业务类型：IMS批量开户 ";
        	
        }
        
        param.put("RECEIPT_INFO1", receiptInfo1);
        param.put("RECEIPT_INFO2", receiptInfo2);
        
        
        param.put("UPDATE_TIME", acceptTime);
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        param.put("ACCEPT_DATE", acceptTime);
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        
        param.put("REMARK", remark);
        
        Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "INSERTINTO_TRADECNOTEINFO_BAT", param,Route.getCrmDefaultDb());		

	}
	
	/**
	 * IMS固话绑定\IMS固话修改绑定的宽带账号排重
	 * @param batchId
	 * @param batch_oper_type
	 * @throws Exception
	 */
	private void updateBatTradesBroadBand(String batchId, String batch_oper_type) throws Exception
	{
		IData data = new DataMap();
		data.put("BATCH_ID", batchId);
		data.put("BATCH_OPER_TYPE", batch_oper_type);
		data.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_6); // B状态被占用 改成6状态
		data.put("DEAL_RESULT", "此次批量业务中存在重复的宽带账号");

		StringBuilder sql = new StringBuilder();

		sql.append(" UPDATE tf_b_trade_batdeal a");
		sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_result = :DEAL_RESULT");
		sql.append(" where 1=1");
		sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" and a.data1 in (select b.data1 from tf_b_trade_batdeal b where 1=1 ");
		sql.append(" and b.batch_id = TO_NUMBER(:BATCH_ID)");
		sql.append(" and b.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		sql.append(" group by b.data1 having count(b.data1)>'1')");
		sql.append(" and a.deal_state = '0'");

		Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	private void actGrpBizBaseAudit(IData inParam) throws Exception
    {
		boolean actVoucherFlag = BizEnv.getEnvBoolean("grp.biz.audit", false);
    	if(actVoucherFlag)
    	{
    		String batchOperType = inParam.getString("BATCH_OPER_TYPE");
    		if(StringUtils.isNotBlank(batchOperType))
    		{
    			IDataset resultInfos = ParamInfoQry.getCommparaByParamattr("CSM", "841", batchOperType, "0898");
    			if(IDataUtil.isNotEmpty(resultInfos))
    			{
    				String tradeTypeCode = "";
    				tradeTypeCode = resultInfos.getData(0).getString("PARA_CODE1","");
    				createGrpMebBizBaseAudit(inParam,tradeTypeCode);
    			}
    			
    		}
    	}
    }
	
	/**
	 * 处理集团成员批量新增时的稽核登记
	 * @param inParam
	 * @param tradeTypeCode
	 * @throws Exception
	 */
	private void createGrpMebBizBaseAudit(IData inParam,String tradeTypeCode) throws Exception
	{
		String condStr = inParam.getString("CODING_STR");
		String batchId = inParam.getString("BATCH_ID","");
		if(StringUtils.isNotBlank(condStr))
		{
			IData codingData = new DataMap(condStr);
			if(IDataUtil.isNotEmpty(codingData))
			{
				//成员业务上传凭证信息则生成集团业务稽核工单
				String voucherFileList = codingData.getString("MEB_VOUCHER_FILE_LIST", "");
				String auditStaffId = codingData.getString("AUDIT_STAFF_ID", "");
				String auditId = batchId; 
				if(StringUtils.isNotBlank(voucherFileList) && StringUtils.isNotBlank(auditId))
				{
					
					IDataset tradeDiscnts = codingData.getDataset("ELEMENT_INFO");
					String addDisncts = "";
					String delDiscnts = "";
					String modDiscnts = "";
					if(IDataUtil.isNotEmpty(tradeDiscnts))
					{
						for(int i=0;i<tradeDiscnts.size();i++)
						{
							IData each = tradeDiscnts.getData(i);
							String elementTypeCode = each.getString("ELEMENT_TYPE_CODE", "");
							if(!"D".equals(elementTypeCode))
							{
								continue;
							}
							String modifyTag = each.getString("MODIFY_TAG", "");
							String discntCode = each.getString("DISCNT_CODE", "");
							if(TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
							{
								addDisncts += StringUtils.isNotBlank(addDisncts) ? ","+discntCode : discntCode;
							}
							else if(TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
							{
								delDiscnts += StringUtils.isNotBlank(delDiscnts) ? ","+discntCode : discntCode;
							}
							else if(TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag))
							{
								modDiscnts += StringUtils.isNotBlank(modDiscnts) ? ","+discntCode : discntCode;
							}
						}
					}
					
					String productId = codingData.getString("PRODUCT_ID","");
					if("0".equals(tradeTypeCode) && StringUtils.isNotBlank(productId))
					{
						String attrValue = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
						  { "ID", "ATTR_OBJ", "ATTR_CODE"}, "ATTR_VALUE", new String[]
						  { productId, "CrtMb", "TradeTypeCode"});
						
						if(StringUtils.isNotBlank(attrValue))
						{
							tradeTypeCode = attrValue;
						}
					}
					
					boolean pageSelectedTC = codingData.getBoolean("PAGE_SELECTED_TC",false);
					String groupId = codingData.getString("GROUP_ID","");
					String grpSn = codingData.getString("GRP_SN","");
					String custName = "";
					if(StringUtils.isNotBlank(groupId))
					{
						IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);
						if(IDataUtil.isNotEmpty(custData))
						{
							custName = custData.getString("CUST_NAME","");
						}
					}
					
					
					IData param = new DataMap();
					param.put("AUDIT_ID", auditId);													//批量业务的批次号或业务流水号trade_id
					param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(auditId));					//受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
					param.put("BIZ_TYPE","2");			//业务工单类型：1-单条，2-批量业务
					param.put("TRADE_TYPE_CODE", tradeTypeCode);	//业务类型编码：见参数表TD_S_TRADETYPE
				   	param.put("GROUP_ID", groupId);		//集团客户编码
				   	param.put("CUST_NAME", custName);	//集团客户名称
				   	param.put("GRP_SN", grpSn);					//集团产品编码
				   	param.put("CONTRACT_ID", "");													//合同编号
				   	param.put("VOUCHER_FILE_LIST", voucherFileList);									//凭证信息上传文件ID
				   	param.put("ADD_DISCNTS", addDisncts);											//新增优惠
				   	param.put("DEL_DISCNTS", delDiscnts);											//删除优惠
				   	param.put("MOD_DISCNTS", modDiscnts);											//变更优惠
				   	param.put("STATE", "0");															//稽核单状态:0-初始，1-稽核通过，2-稽核不通过
				   	param.put("IN_DATE", SysDateMgr.getSysTime());									//提交时间
				   	param.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());						//提交工号
				   	param.put("AUDIT_STAFF_ID", auditStaffId);										//稽核人工号
				   	
				   	if(pageSelectedTC)
				   	{
				   		param.put("RSRV_TAG1", "1");	//界面选择了二次短信确认
				   	}
				   	else 
				   	{
				   		param.put("RSRV_TAG1", "0");	
				   	}
				   	
				   	GrpBaseAudiInfoQry.addGrpBaseAuditInfo(param);
					   
				}
				   
			}
		}
	}
	
	private void checkGrpCustPsptId(IData memCustInfo,int batCount) throws Exception 
    {
		if(IDataUtil.isNotEmpty(memCustInfo))
		{
			String psptTypeCode = memCustInfo.getString("PSPT_TYPE_CODE","");
			String psptId = memCustInfo.getString("PSPT_ID","");
			String custName = memCustInfo.getString("CUST_NAME","");
			if("E".equals(psptTypeCode) || "M".equals(psptTypeCode) ||
    				"G".equals(psptTypeCode) || "D".equals(psptTypeCode))
    		{
    			if(StringUtils.isNotBlank(custName) && StringUtils.isNotBlank(psptId))
    			{
    				//阀值
        			int setCount = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, "1");
        			
        			//开户的数量
        			int openCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, "1");
        			
        			//比较
        			if(setCount < openCount + batCount)
        			{
        				String errMsg = "该证件号码" + psptId + "的开户数量(" + openCount + ")与本次批量的数量(" + batCount + ")和已经达到了设置的阀值(" + setCount + ").";
        				CSAppException.apperr(GrpException.CRM_GRP_713, errMsg);
        			}
    			}
    		}
		}
    }
	
}
