package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeOtherFee;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.sync.TradeSyncFeeSub;

public final class TradeSync
{
	private final static Logger logger = Logger.getLogger(TradeSync.class);

	public static void sync(IData mainTrade) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("订单同步开始");
		}

		// 同步(主)
		syncMain(mainTrade, null);

		// 同步(其他)
		syncSub(mainTrade);
	}

	public static void sync(IData mainTrade, String routeId) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("订单同步开始");
		}

		// 同步(主)
		syncMain(mainTrade, routeId);

		// 同步(其他)
		syncSub(mainTrade);
	}

	private static void syncMain(IData mainTrade, String routeId) throws Exception
	{

		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = mainTrade.getString("ACCEPT_MONTH");
		String canceltag = mainTrade.getString("CANCEL_TAG");

		IDataset tradeset = TradeInfoQry.getTradeInfobyTradeId(tradeId, acceptMonth);
		if (IDataUtil.isEmpty(tradeset))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);
		}
		String intfId = tradeset.getData(0).getString("INTF_ID", "-1");// 重新查表取，防止在同步前修改INTF_ID，取不到修改后的值

		// 插同步主表 begin
		String syncId = SeqMgr.getSyncIncreId();
		mainTrade.put("SYNC_SEQUENCE", syncId);
		String syncDay = StrUtil.getAcceptDayById(syncId);

		IData syncInfo = new DataMap();
		syncInfo.put("SYNC_SEQUENCE", syncId);
		syncInfo.put("SYNC_DAY", syncDay);
		syncInfo.put("SYNC_TYPE", "0");
		syncInfo.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
		syncInfo.put("STATE", "0");
		syncInfo.put("CUST_ID", mainTrade.getString("CUST_ID", "-1"));
		syncInfo.put("USER_ID", mainTrade.getString("USER_ID", "-1"));
		syncInfo.put("ACCT_ID", mainTrade.getString("ACCT_ID", "-1"));
		syncInfo.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
		syncInfo.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE"));

		Dao.insert("TI_B_SYNCHINFO", syncInfo, Route.getJourDbDefault());
		// 插同步主表 end

		/*
		 * String[] paramName = { "IN_TRADE_ID", "IN_ACCEPT_MONTH",
		 * "IN_CANCEL_TAG", "OUT_SYNC_SEQUENCE", "OUT_RESULT_CODE",
		 * "OUT_RESULT_INFO" };
		 * 
		 * IData paramValue = new DataMap(); paramValue.put("IN_TRADE_ID",
		 * tradeId); paramValue.put("IN_ACCEPT_MONTH", acceptMonth);
		 * paramValue.put("IN_CANCEL_TAG", canceltag);
		 * 
		 * String procName = BizEnv.getEnvString("crm.pk.tradesync",
		 * "PK_CS_TRADESYNC.MAIN");
		 * 
		 * 
		 * Dao.callProc(procName, paramName, paramValue);
		 */

		boolean mputeFlag = false;
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_INTEGRALACCT,") > -1)
		{
			syncIntegralAcct(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_INTEGRALPLAN,") > -1)
		{
			syncIntegralPlan(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SCORERELATION,") > -1)
		{
			syncScoreRelation(mainTrade, syncId, syncDay);
		}
		// if("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_INTEGRALACCT,")
		// > -1)
		// {
		// syncIntegralAcct(mainTrade, syncId, syncDay);
		// }
		// if("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_INTEGRALACCT,")
		// > -1)
		// {
		// syncIntegralAcct(mainTrade, syncId, syncDay);
		// }
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SVC,") > -1)
		{
			syncSvc(mainTrade, syncId, syncDay);
			syncRemind(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_ACCT_CONSIGN,") > -1)
		{
			syncAccountConsign(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_ACCOUNT,") > -1)
		{
			syncAccount(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_DISCNT,") > -1)
		{
			syncDiscnt(mainTrade, syncId, syncDay);
			mputeFlag = true;
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_ACCT_DISCNT,") > -1)
		{
			syncAcctDiscnt(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_CUSTOMER,") > -1)
		{
			syncCustomer(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_CUST_PERSON,") > -1)
		{
			syncCustPerson(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_RELATION_AA,") > -1)
		{
			syncRelationAA(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_RELATION,") > -1)
		{
			syncRelation(mainTrade, syncId, syncDay);
			mputeFlag = true;
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_RELATION_BB,") > -1)
		{
			syncRelationBB(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_PAYRELATION,") > -1)
		{
			syncPayRelation(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_USER,") > -1)
		{
			syncUser(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_ATTR,") > -1)
		{
			syncAttr(mainTrade, syncId, syncDay);
			syncRemindAttr(mainTrade, syncId, syncDay);
			syncPlatAttr(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_GRP_MEB_PLATSVC,") > -1)
		{
			syncGrpMebPlatsvc(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_GRP_MERCH,") > -1)
		{
			syncGrpMerch(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_ECRECEP_OFFER,") > -1)
		{
			syncEcrecepOffer(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SHARE,") > -1)
		{
			syncShare(mainTrade, syncId, syncDay);
			mputeFlag = true;
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SHARE_INFO,") > -1)
		{
			syncShareInfo(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SHARE_RELA,") > -1)
		{
			syncShareRela(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_GRP_PLATSVC,") > -1)
		{
			syncGrpPlatsvc(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_IMPU,") > -1)
		{
			syncImpu(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_OTHER,") > -1)
		{
			syncOther(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SVCSTATE,") > -1)
		{
			syncSvcState(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_POST,") > -1)
		{
			syncPost(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_CUST_GROUP,") > -1)
		{
			syncCustGroup(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADEFEE_DEFER,") > -1)
		{
			syncDefer(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_PLATSVC,") > -1)
		{
			syncPlatsvc(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_RENT,") > -1)
		{
			syncRent(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_USER_ACCTDAY,") > -1)
		{
			syncUserAcctDay(mainTrade, syncId, syncDay);
			mputeFlag = true;
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_ACCOUNT_ACCTDAY,") > -1)
		{
			syncAccountAcctDay(mainTrade, syncId, syncDay);
			mputeFlag = true;
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_SMS,") > -1)
		{
			syncSms(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_OCS,") > -1)
		{
			syncOcs(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_GRP_CENPAY,") > -1)
		{
			syncGrpCenPay(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_MEB_CENPAY,") > -1)
		{
			syncMebCenPay(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_USER_SPECIALEPAY,") > -1)
		{
			syncUserSpecialEPay(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_CREDIT,") > -1)
		{
			syncCredit(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_RES,") > -1 || intfId.indexOf("TF_B_TRADE_PRODUCT,") > -1)
		{
			syncInfoChange(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_BANK_MAINSIGN,") > -1)
		{
			syncMainSign(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_BANK_SUBSIGN,") > -1)
		{
			syncSubSign(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADE_TRANS_PHONE,") > -1)
		{
			syncTransPhone(mainTrade, syncId, syncDay);
		}
		if ("-1".equals(intfId) || intfId.indexOf("TF_B_TRADEFEE_SUB,") > -1)
		{
			syncForegift(mainTrade, syncId, syncDay);
		}
		if (mputeFlag)
		{
			syncMpute(mainTrade, syncId, syncDay);
		}

		//特殊处理多媒体桌面电话账户优惠
		syncDesktopAttrAcctDiscnt(mainTrade, syncId, syncDay);
		
		//特殊处理集团营销活动受理账户优惠
		syncSaleActiveAttrAcctDiscnt(mainTrade, syncId, syncDay);
				
		// 是否成功
		String resultCode = "0";

		if (!"0".equals(resultCode))
		{
			String resultInfo = "失败";

			if (logger.isDebugEnabled())
			{
				logger.debug("订单同步返回，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
			}

			CSAppException.apperr(TradeException.CRM_TRADE_52c, resultCode, resultInfo);
		}
	}

	private static void syncMain(IData mainTrade) throws Exception
	{

		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = mainTrade.getString("ACCEPT_MONTH");
		String canceltag = mainTrade.getString("CANCEL_TAG");

		String[] paramName =
		{ "IN_TRADE_ID", "IN_ACCEPT_MONTH", "IN_CANCEL_TAG", "OUT_SYNC_SEQUENCE", "OUT_RESULT_CODE", "OUT_RESULT_INFO" };

		IData paramValue = new DataMap();
		paramValue.put("IN_TRADE_ID", tradeId);
		paramValue.put("IN_ACCEPT_MONTH", acceptMonth);
		paramValue.put("IN_CANCEL_TAG", canceltag);

		String procName = BizEnv.getEnvString("crm.pk.tradesync", "PK_CS_TRADESYNC.MAIN");

		Dao.callProc(procName, paramName, paramValue, Route.getJourDb(mainTrade.getString("TRADE_EPARCHY_CODE", "0898")));

		// 是否成功
		String resultCode = paramValue.getString("OUT_RESULT_CODE");

		if (!"0".equals(resultCode))
		{
			String resultInfo = paramValue.getString("OUT_RESULT_INFO");

			if (logger.isDebugEnabled())
			{
				logger.debug("订单同步返回，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
			}

			CSAppException.apperr(TradeException.CRM_TRADE_52c, resultCode, resultInfo);
		}

		String syncId = paramValue.getString("OUT_SYNC_SEQUENCE");

		mainTrade.put("SYNC_SEQUENCE", syncId);
	}

	private static int syncRecv(IData mainTrade) throws Exception
	{
		// 同步TI_A_SYNC_RECV数据
		return TradeSyncFeeSub.syncTradeFeeSub(mainTrade);
	}

	private static void syncSub(IData mainTrade) throws Exception
	{
		// 只有正向的业务才走同步处理，反向业务在登记时走实时接口返销处理
		if (!"0".equals(mainTrade.getString("CANCEL_TAG")))
		{
			return;
		}
		// add by chenzm 宽带开户(校园宽带开户除外)登记时已通过缴费方式将预存同步账务，完工时不同步
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		if ("600".equals(tradeTypeCode) || "613".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "611".equals(tradeTypeCode))
		{
			return;
		}

		boolean isMySync = false;

		// 同步过程没有返回同步序列，则其他同步子表为空，仅同步缴费接口表，并插入同步主表
		String syncId = mainTrade.getString("SYNC_SEQUENCE");

		if (StringUtils.isNotBlank(syncId) && "-1".equals(syncId))
		{
			// 生成同步序列
			syncId = SeqMgr.getSyncIncreId();
			mainTrade.put("SYNC_SEQUENCE", syncId);

			isMySync = true;
		}

		int iCnt = 0;

		// 同步(营业费)
		iCnt = syncRecv(mainTrade);

		// 同步(转账)
		iCnt = iCnt + syncTransFee(mainTrade);

		if (iCnt <= 0)
		{
			return;
		}

		if (isMySync)
		{

			IData syncInfo = new DataMap();

			syncInfo.put("SYNC_SEQUENCE", syncId);
			syncInfo.put("SYNC_DAY", StrUtil.getAcceptDayById(syncId));
			syncInfo.put("SYNC_TYPE", "0");
			syncInfo.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
			syncInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			syncInfo.put("STATE", "0");

			Dao.insert("TI_B_SYNCHINFO", syncInfo, Route.getJourDb(mainTrade.getString("TRADE_EPARCHY_CODE", "0898")));
		}
	}

	private static int syncTransFee(IData mainTrade) throws Exception
	{

		return TradeOtherFee.syncTransFee(mainTrade);
	}

	// -----------------------------分割线-------------------------
	// TI_B_INTEGRAL_ACCT
	private static void syncIntegralAcct(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_INTEGRAL_ACCT", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);

			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_INTEGRAL_ACCT_BY_USERID", param);
			if (IDataUtil.isNotEmpty(ids))
			{
				int[] rowArray = Dao.insert("TI_B_INTEGRAL_ACCT", ids, Route.getJourDbDefault());
				rowCount = getRowArrayCount(rowArray);
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("14", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode) || StringUtils.equals("110", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("TRADE_ID", tradeId);
				param.put("ACCEPT_MONTH", acceptMonth);
				param.put("SYNC_DAY", syncDay);
				param.put("SYNC_SEQUENCE", syncId);

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_INTEGRAL_ACCT_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	// TF_B_TRADE_INTEGRALPLAN
	private static void syncIntegralPlan(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_INTEGRALPLAN", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);

			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_INTEGRAL_PLAN_BY_USERID", param);
			if (IDataUtil.isNotEmpty(ids))
			{
				int[] rowArray = Dao.insert("TI_B_INTEGRAL_PLAN", ids, Route.getJourDbDefault());
				rowCount = getRowArrayCount(rowArray);
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("14", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode) || StringUtils.equals("110", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("TRADE_ID", tradeId);
				param.put("ACCEPT_MONTH", acceptMonth);
				param.put("SYNC_DAY", syncDay);
				param.put("SYNC_SEQUENCE", syncId);

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_INTEGRAL_PLAN_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	// TF_B_TRADE_SCORERELATION
	private static void syncScoreRelation(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_SCORERELATION", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);

			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_SCORERELATION_BY_USERID", param);
			if (IDataUtil.isNotEmpty(ids))
			{
				int[] rowArray = Dao.insert("TI_B_SCORERELATION", ids, Route.getJourDbDefault());
				rowCount = getRowArrayCount(rowArray);
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("14", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("TRADE_ID", tradeId);
				param.put("ACCEPT_MONTH", acceptMonth);
				param.put("SYNC_DAY", syncDay);
				param.put("SYNC_SEQUENCE", syncId);

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_SCORERELATION_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	// TF_B_TRADE_SVC
	private static void syncSvc(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SVC", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_SVC_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_SVC_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_USER_SVC", idataset, Route.getJourDbDefault());
					rowCount = getRowArrayCount(rowArray);
				}
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("14", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("TRADE_ID", tradeId);
				param.put("SYNC_DAY", syncDay);
				param.put("SYNC_SEQUENCE", syncId);
				param.put("USER_ID", userId);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SVC_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	// TODO:返销未完待续
	private static void syncRemind(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset reminds = Dao.qryByCode("BOF_SYNC", "SEL_USER_REMIND", param, Route.getJourDbDefault());
			for (Object obj : reminds)
			{
				IData remind = (IData) obj;
				String serviceId = remind.getString("SERVICE_ID");
				IDataset idataset = UpcCall.qryGroupInfoByGroupIdOfferId(BofConst.ELEMENT_TYPE_CODE_SVC, serviceId, "1000");
    			IDataset commParam = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9428", "REMINDCODE", serviceId);
				
				if (IDataUtil.isNotEmpty(idataset) || IDataUtil.isNotEmpty(commParam))
				{
					remind.put("REMIND_TYPE_CODE", serviceId);
					Dao.insert("TI_B_USER_REMIND", remind, Route.getJourDbDefault());
				}
			}
		} else
		{

		}
	}

	private static void syncAccountConsign(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String acctId = mainTrade.getString("ACCT_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_ACCOUNT_CONSIGN", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCT_ID", acctId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_ACCOUNT_CONSIGN_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("ACCT_ID");
					param.put("ACCT_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_ACCOUNT_CONSIGN_BY_ACCTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_ACCOUNT_CONSIGN", idataset, Route.getJourDbDefault());
					rowCount = getRowArrayCount(rowArray);
				}
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("SYNC_SEQUENCE", syncId);
				param.put("SYNC_DAY", syncDay);
				param.put("TRADE_ID", tradeId);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("ACCEPT_MONTH", acceptMonth);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_ACCOUNT_CONSIGN_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	private static void syncAccount(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String acctId = mainTrade.getString("ACCT_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_ACCOUNT", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("ACCT_ID", acctId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_ACCOUNT_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("ACCT_ID");
					param.put("ACCT_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_ACCOUNT_BY_ACCTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_ACCOUNT", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncDiscnt(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_DISCNT", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_DISCNT_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_DISCNT_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_USER_DISCNT", idataset, Route.getJourDbDefault());
					rowCount = getRowArrayCount(rowArray);
				}
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("14", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("SYNC_SEQUENCE", syncId);
				param.put("SYNC_DAY", syncDay);
				param.put("TRADE_ID", tradeId);
				param.put("USER_ID", userId);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_DISCNT_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	private static void syncAcctDiscnt(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String acctId = mainTrade.getString("ACCT_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_DISCNT_4ACCT", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("ACCT_ID", acctId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_ACCT_DISCNT_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("ACCT_ID");
					param.put("ACCT_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_ACCT_DISCNT_BY_ACCTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_USER_DISCNT", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncCustomer(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String custId = mainTrade.getString("CUST_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_CUSTOMER", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("CUST_ID", custId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_CUSTOMER_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("CUST_ID");
					param.put("CUST_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_CUSTOMER_BY_CUSTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_CUSTOMER", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncCustPerson(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String custId = mainTrade.getString("CUST_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_CUST_PERSON", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("CUST_ID", custId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_CUST_PERSON_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("CUST_ID");
					param.put("CUST_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_CUST_PERSON_BY_CUSTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_CUST_PERSON", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncRelationAA(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_RELATION_AA", param, Route.getJourDbDefault());
		}
	}

	private static void syncRelation(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_RELATION_UU", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("USER_ID_B", userId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_RELATION_UU_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID_B");
					param.put("USER_ID_B", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_RELATION_UU_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_RELATION_UU", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncRelationBB(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_RELATION_UU_4BB", param, Route.getJourDbDefault());
		}
	}

	private static void syncPayRelation(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_PAYRELATION", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_PAYRELATION_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_PAYRELATION_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_PAYRELATION", idataset, Route.getJourDbDefault());
					rowCount = getRowArrayCount(rowArray);
				}
			}
		}

		if (0 == rowCount)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode))
			{
				IData param = new DataMap();
				param.put("SYNC_SEQUENCE", syncId);
				param.put("SYNC_DAY", syncDay);
				param.put("TRADE_ID", tradeId);
				param.put("ACCEPT_MONTH", acceptMonth);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_PAYRELATION_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	// 特殊
	private static void syncUser(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		int result = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			result = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER", param, Route.getJourDbDefault());
			if (result == 0)
			{
				if (!"310".equals(tradeTypeCode))
				{
					IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_USER_BY_TRADEID", param, Route.getJourDbDefault());
					for (int i = 0, size = trades.size(); i < size; i++)
					{
						IData trade = trades.getData(i);

						trade.put("SCORE_VALUE", "0");
						trade.put("CREDIT_CLASS", "0");
						trade.put("BASIC_CREDIT_VALUE", "0");
						trade.put("CREDIT_VALUE", "0");

						String userId = trade.getString("USER_ID");
						param.clear();
						param.put("USER_ID", userId);
						IDataset tmps = Dao.qryByCode("BOF_SYNC", "SEL_USERMAINPROUDCT_BY_USERID", param);
						if (IDataUtil.isEmpty(tmps))
						{
							trade.put("PRODUCT_ID", "-1");
							trade.put("BRAND_CODE", "-1");
						} else
						{
							IData tmp = tmps.getData(0);
							trade.put("PRODUCT_ID", tmp.getString("PRODUCT_ID"));
							trade.put("BRAND_CODE", tmp.getString("BRAND_CODE"));
						}
					}

					int[] rowArray = Dao.insert("TI_B_USER", trades, Route.getJourDbDefault());
					result = getRowArrayCount(rowArray);
				} else
				{
					param.put("TRADE_ID", tradeId);
					param.put("ACCEPT_MONTH", acceptMonth);
					param.put("SYNC_DAY", syncDay);
					param.put("SYNC_SEQUENCE", syncId);

					result = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER", param, Route.getJourDbDefault());
				}
			}
		} else
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("USER_ID", mainTrade.getString("USER_ID"));
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_UNDO_USER_BY_TRADEID", param, Route.getJourDbDefault());
			IDataset idataset = new DatasetList();
			for (int i = 0, size = trades.size(); i < size; i++)
			{
				IData trade = trades.getData(i);

				IData temp = new DataMap();
				temp.put("TRADE_ID", tradeId);
				temp.put("USER_ID", trade.getString("USER_ID"));
				temp.put("ACCEPT_MONTH", acceptMonth);
				temp.put("SYNC_DAY", syncDay);
				temp.put("SYNC_SEQUENCE", syncId);
				temp.put("BRAND_CODE", trade.getString("BRAND_CODE"));
				temp.put("PRODUCT_ID", trade.getString("PRODUCT_ID"));

				IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_UNDO_USER_BY_USERID", temp);
				idataset.addAll(ids);
			}

			if (IDataUtil.isNotEmpty(idataset))
			{
				for (int i = 0, size = idataset.size(); i < size; i++)
				{
					IData idata = idataset.getData(i);

					idata.put("SCORE_VALUE", "0");
					idata.put("CREDIT_CLASS", "0");
					idata.put("BASIC_CREDIT_VALUE", "0");
					idata.put("CREDIT_VALUE", "0");
				}

				int[] rowArray = Dao.insert("TI_B_USER", idataset, Route.getJourDbDefault());
				result = getRowArrayCount(rowArray);
			}
		}

		if (result == 0)
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("USER_ID", mainTrade.getString("USER_ID"));
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_USER_BY_TRADEID_PRIV", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_USER_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					for (int i = 0, size = idataset.size(); i < size; i++)
					{
						IData idata = idataset.getData(i);

						idata.put("SCORE_VALUE", "0");
						idata.put("CREDIT_CLASS", "0");
						idata.put("BASIC_CREDIT_VALUE", "0");
						idata.put("CREDIT_VALUE", "0");
					}

					Dao.insert("TI_B_USER", idataset, Route.getJourDbDefault());
				}
			}

		}
	}

	// 特殊
	private static void syncAttr(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String userId = mainTrade.getString("USER_ID");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			int svcCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SVC_ATTR", param, Route.getJourDbDefault());
			int discntCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_DISCNT_ATTR", param, Route.getJourDbDefault());
			rowCount = svcCount + discntCount;
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("TRADE_ID", tradeId);

			IDataset svcTrades = Dao.qryByCode("BOF_SYNC", "SEL_SVCATTR_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(svcTrades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = svcTrades.size(); i < size; i++)
				{
					String id = svcTrades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_SVCATTR_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_USER_SVC_ATTR", idataset, Route.getJourDbDefault());
					int svcCount = getRowArrayCount(rowArray);
					rowCount = rowCount + svcCount;
				}
			}

			IDataset discntTrades = Dao.qryByCode("BOF_SYNC", "SEL_DISCNTATTR_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(discntTrades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = discntTrades.size(); i < size; i++)
				{
					String id = discntTrades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_DISCNTATTR_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_USER_DISCNT_ATTR", idataset, Route.getJourDbDefault());
					int discntCount = getRowArrayCount(rowArray);
					rowCount = rowCount + discntCount;
				}
			}
		}

		if (rowCount == 0)
		{
			if (StringUtils.equals(tradeTypeCode, "10") || StringUtils.equals(tradeTypeCode, "500") || StringUtils.equals(tradeTypeCode, "700"))
			{
				IData param = new DataMap();
				param.put("SYNC_SEQUENCE", syncId);
				param.put("SYNC_DAY", syncDay);
				param.put("TRADE_ID", tradeId);
				param.put("USER_ID", userId);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SVC_ATTR_PRIV", param, Route.getJourDbDefault());
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));
				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_DISCNT_ATTR_PRIV", param, Route.getJourDbDefault());
			}
		}
	}

	// TODO:未完待续
	private static void syncRemindAttr(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset reminds = Dao.qryByCode("BOF_SYNC", "SEL_USER_REMINDATTR", param, Route.getJourDbDefault());
			for (Object obj : reminds)
			{
				IData remind = (IData) obj;
				String elementId = remind.getString("ELEMENT_ID");
				IDataset idataset = UpcCall.qryGroupInfoByGroupIdOfferId(BofConst.ELEMENT_TYPE_CODE_SVC, elementId, "1000");
				if (IDataUtil.isNotEmpty(idataset))
				{
					String rsrvStr1 = idataset.getData(0).getString("RSRV_STR1");
					remind.put("REMIND_CODE", elementId);
					remind.put("RSRV_STR1", rsrvStr1);
					Dao.insert("TI_B_USER_REMINDATTR", remind, Route.getJourDbDefault());
				}
			}
		} else
		{

		}
	}

	private static void syncGrpMebPlatsvc(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_GRP_MEB_PLATSVC", param, Route.getJourDbDefault());
	}

	private static void syncShare(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SHARE", param, Route.getJourDbDefault());
	}

	private static void syncShareInfo(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SHARE_INFO", param, Route.getJourDbDefault());
	}

	private static void syncShareRela(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SHARE_RELA", param, Route.getJourDbDefault());
	}

	private static void syncGrpPlatsvc(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_GRP_PLATSVC", param, Route.getJourDbDefault());
	}

	private static void syncImpu(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_IMPU", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_IMPU_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_IMPU_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_USER_IMPU", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncOther(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_OTHER", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_OTHER_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_OTHER_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_USER_OTHER", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	// 特殊
	private static void syncSvcState(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset sertrackLogs = Dao.qryByCode("BOF_SYNC", "SEL_USER_SERTRACK_LOG", param);
			Dao.insert("TI_B_USER_SERTRACK_LOG", sertrackLogs, Route.getJourDbDefault());

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SVCSTATE", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_SVCSTATE_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_SVCSTATE_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_USER_SVCSTATE", idataset, Route.getJourDbDefault());
				}
			}

			IDataset sertrackLogs = Dao.qryByCode("BOF_SYNC", "SEL_USER_SERTRACK_LOG_ENDDATE", param);
			Dao.insert("TI_B_USER_SERTRACK_LOG", sertrackLogs, Route.getJourDbDefault());
		}
	}

	private static void syncPost(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_POST", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_POST_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("ID");
					param.put("ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_POST_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_POSTINFO", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncCustGroup(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_CUST_GROUP", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_CUST_GROUP_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("CUST_ID");
					param.put("CUST_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_CUST_GROUP_BY_CUSTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_CUST_GROUP", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncDefer(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_DEFER", param, Route.getJourDbDefault());
	}

	// 特殊 TODO:未完待续
	private static void syncPlatsvc(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset sertrackLogs = Dao.qryByCode("BOF_SYNC", "SEL_USER_SERTRACK_LOG", param);
			Dao.insert("TI_B_USER_SERTRACK_LOG", sertrackLogs, Route.getJourDbDefault());

			param.clear();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("SERIAL_NUMBER", serialNumber);

			IDataset traces = Dao.qryByCode("BOF_SYNC", "SEL_USERPLATSVCTRACE_BY_UIDTID", param);
			for (Object obj : traces)
			{
				IData trace = (IData) obj;
				String svcId = trace.getString("SERVICE_ID");
				IDataset ids = UpcCall.qryByServiceIdBillType(BofConst.ELEMENT_TYPE_CODE_PLATSVC, svcId);
				if (IDataUtil.isNotEmpty(ids))
				{
					trace.putAll(ids.getData(0));
					Dao.insert("TI_B_PLATSVC_ORDER", trace, Route.getJourDbDefault());
				}
			}

			//
			IDataset traces2 = Dao.qryByCode("BOF_SYNC", "SEL_USERPLATSVCTRACE_BY_UIDTID_4STATE", param);
			for (Object obj : traces2)
			{
				IData trace = (IData) obj;
				String svcId = trace.getString("SERVICE_ID");
				IDataset ids = UpcCall.qryByServiceIdBillType(BofConst.ELEMENT_TYPE_CODE_PLATSVC, svcId);
				if (IDataUtil.isNotEmpty(ids))
				{
					trace.putAll(ids.getData(0));
					Dao.insert("TI_B_PLATSVC_STATE", trace, Route.getJourDbDefault());
				}
			}

		} else
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset sertrackLogs = Dao.qryByCode("BOF_SYNC", "SEL_USER_SERTRACK_LOG_ENDDATE180", param);
			Dao.insert("TI_B_USER_SERTRACK_LOG", sertrackLogs, Route.getJourDbDefault());

			param.clear();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("USER_ID", userId);
			param.put("SERIAL_NUMBER", serialNumber);

			IDataset traces = Dao.qryByCode("BOF_SYNC", "SEL_USERPLATSVCTRACE_BY_UIDTID_ENDDATE180", param);
			for (Object obj : traces)
			{
				IData trace = (IData) obj;
				String svcId = trace.getString("SERVICE_ID");
				IDataset ids = UpcCall.qryByServiceIdBillType(BofConst.ELEMENT_TYPE_CODE_PLATSVC, svcId);
				if (IDataUtil.isNotEmpty(ids))
				{
					trace.putAll(ids.getData(0));
					Dao.insert("TI_B_PLATSVC_ORDER", trace, Route.getJourDbDefault());
				}
			}

			//
			IDataset traces2 = Dao.qryByCode("BOF_SYNC", "SEL_USERPLATSVCTRACE_BY_UIDTID_4STATE_ENDDATE180", param);
			for (Object obj : traces2)
			{
				IData trace = (IData) obj;
				String svcId = trace.getString("SERVICE_ID");
				IDataset ids = UpcCall.qryByServiceIdBillType(BofConst.ELEMENT_TYPE_CODE_PLATSVC, svcId);
				if (IDataUtil.isNotEmpty(ids))
				{
					trace.putAll(ids.getData(0));
					Dao.insert("TI_B_PLATSVC_STATE", trace, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncRent(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_RENT", param, Route.getJourDbDefault());
	}

	// 特殊
	private static void syncUserAcctDay(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String userId = mainTrade.getString("USER_ID");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_USER_ACCTDAY_BY_TID", param, Route.getJourDbDefault());
			for (int i = 0, size = trades.size(); i < size; i++)
			{
				IData trade = trades.getData(i);
				param.clear();
				param.put("USER_ID", trade.getString("USER_ID"));
				IDataset tmps = Dao.qryByCode("BOF_SYNC", "SEL_USERMAINPROUDCT_BY_USERID2", param);
				if (IDataUtil.isNotEmpty(tmps))
				{
					Dao.insert("TI_B_USER_ACCTDAY", trade, Route.getJourDbDefault());
				}
			}
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_ACCTDAY_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_ACCTDAY_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_USER_ACCTDAY", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncAccountAcctDay(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_ACCOUNT_ACCTDAY", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_ACCOUNT_ACCTDAY_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("ACCT_ID");
					param.put("ACCT_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_ACCOUNT_ACCTDAY_BY_ACCTID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_ACCOUNT_ACCTDAY", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncSms(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("CANCEL_TAG", "0");

			// Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_SMS", param,
			// Route.getJourDbDefault());
			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_SMS_BY_TRADEID", param, Route.getJourDbDefault());
			Dao.insert("TI_O_SMS", ids);
		} else
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("CANCEL_TAG", "2");

			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_SMS_BY_TRADEID", param, Route.getJourDbDefault());
			Dao.insert("TI_O_SMS", ids);
		}
	}

	private static void syncOcs(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_OCS", param, Route.getJourDbDefault());
		} else
		{
			IData param = new DataMap();
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);

			IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_OCS_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(trades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = trades.size(); i < size; i++)
				{
					String id = trades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_OCS_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					Dao.insert("TI_B_USER_OCS", idataset, Route.getJourDbDefault());
				}
			}
		}
	}

	private static void syncGrpCenPay(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_GRP_CENPAY", param, Route.getJourDbDefault());
	}

	private static void syncMebCenPay(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_MEB_CENPAY", param, Route.getJourDbDefault());
	}

	private static void syncUserSpecialEPay(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_SPECIALEPAY", param, Route.getJourDbDefault());
	}

	// 特殊
	private static void syncCredit(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("CANCEL_TAG", cancelTag);

		IDataset offers = UpcCall.qryOffersByFixedCatalogId();

		String offerCodes = "";
		if (IDataUtil.isNotEmpty(offers))
		{
			StringBuilder offerCodeSb = new StringBuilder();
			for (Object obj : offers)
			{
				IData offer = (IData) obj;
				String offerCode = offer.getString("OFFER_CODE");
				offerCodeSb.append("'").append(offerCode).append("',");
			}

			offerCodes = offerCodeSb.substring(0, offerCodeSb.length() - 1);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT A.ATTR_VALUE FROM TD_B_ATTR_BIZ A WHERE A.ID IN (").append(offerCodes).append(") ");
		sb.append(" and a.attr_obj = 'CrtUs' ");
		sb.append(" and a.attr_code = 'TradeTypeCode' ");
		sb.append(" and a.attr_value = :TRADE_TYPE_CODE ");
		sb.append(" and ROWNUM < 2 ");

		IDataset idataset = Dao.qryBySql(sb, param, Route.CONN_CRM_CEN);
		String attrValue = null;
		if (IDataUtil.isNotEmpty(idataset))
		{
			attrValue = idataset.getData(0).getString("ATTR_VALUE");
		}
		param.put("ATTR_VALUE", attrValue);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_YWTOCREDIT", param, Route.getJourDbDefault());
	}

	// 特殊
	private static void syncInfoChange(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);

		int rowCount = 0;
		IDataset trades = Dao.qryByCode("BOF_SYNC", "SEL_TRADE_4INFOCHANGE", param, Route.getJourDbDefault());
		for (int i = 0, size = trades.size(); i < size; i++)
		{
			IData trade = trades.getData(i);

			param.clear();
			param.put("TRADE_ID", tradeId);
			param.put("USER_ID", trade.getString("USER_ID"));
			param.put("PARTITION_ID", trade.getString("PARTITION_ID"));
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			IDataset userInfoChanges = Dao.qryByCode("BOF_SYNC", "SEL_USERINFOCHANGE", param);
			if (IDataUtil.isNotEmpty(userInfoChanges))
			{
				int[] rowArray = Dao.insert("TI_B_USER_INFOCHANGE", userInfoChanges, Route.getJourDbDefault());
				rowCount = rowCount + getRowArrayCount(rowArray);
			}
		}

		if (!StringUtils.equals("0", cancelTag) && rowCount == 0)
		{
			if (StringUtils.equals("10", tradeTypeCode) || StringUtils.equals("500", tradeTypeCode) || StringUtils.equals("700", tradeTypeCode))
			{
				param.clear();
				param.put("TRADE_ID", tradeId);
				param.put("USER_ID", mainTrade.getString("USER_ID"));
				param.put("TRADE_TYPE_CODE", tradeTypeCode);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("SYNC_DAY", syncDay);
				param.put("SYNC_SEQUENCE", syncId);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));
				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_INFOCHANGE_PRIV", param, Route.getJourDbDefault());
			}
		}

	}

	private static void syncMainSign(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_BANK_MAINSIGN", param, Route.getJourDbDefault());
	}

	private static void syncSubSign(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_BANK_SUBSIGN", param, Route.getJourDbDefault());
	}

	private static void syncTransPhone(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_TRANS_PHONE", param, Route.getJourDbDefault());
	}

	// 特殊
	private static void syncMpute(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_MPUTE", param, Route.getJourDbDefault());
	}

	// 特殊
	private static void syncForegift(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);

		IDataset feeSubs = Dao.qryByCode("BOF_SYNC", "SEL_TRADEFEESUB_BY_TID", param, Route.getJourDbDefault());
		for (int i = 0, size = feeSubs.size(); i < size; i++)
		{
			IData feeSub = feeSubs.getData(i);
			String userId = feeSub.getString("USER_ID");
			String partitionId = feeSub.getString("PARTITION_ID");

			param.clear();
			param.put("USER_ID", userId);
			param.put("PARTITION_ID", partitionId);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);
			IDataset userForgift = Dao.qryByCode("BOF_SYNC", "SEL_USERFOREGIFT", param);
			if (IDataUtil.isNotEmpty(userForgift))
			{
				Dao.insert("TI_B_USER_FOREGIFT", userForgift, Route.getJourDbDefault());
			}
		}
	}

	private static void syncGrpMerch(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_GRP_MERCH", param, Route.getJourDbDefault());
	}
	
	private static void syncEcrecepOffer(IData mainTrade, String syncId, String syncDay) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");

		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		param.put("SYNC_DAY", syncDay);
		param.put("SYNC_SEQUENCE", syncId);

		Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_ECRECEP_OFFER", param, Route.getJourDbDefault());
	}

	private static int getRowArrayCount(int[] rowArray) throws Exception
	{
		if (rowArray == null || rowArray.length == 0)
		{
			return 0;
		}

		int result = 0;
		for (int rowCount : rowArray)
		{
			result = result + rowCount;
		}
		return result;
	}

	private static IDataset distinct(IDataset users, IDataset trades, String fieldNames) throws Exception
	{
		if (IDataUtil.isEmpty(users))
		{
			return trades;
		}

		if (IDataUtil.isEmpty(trades))
		{
			return users;
		}

		users.addAll(trades);

		return DataHelper.distinct(users, fieldNames, null);
	}
	
	private static void syncDesktopAttrAcctDiscnt(IData mainTrade, String syncId, String syncDay) throws Exception
	{
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
        if("3880".equals(tradeTypeCode) || "3881".equals(tradeTypeCode) || "3883".equals(tradeTypeCode))
        {
        	String userId = mainTrade.getString("USER_ID","");//集团产品的user_id
    		String strTradeId = mainTrade.getString("TRADE_ID","");
    		
        	if("3880".equals(tradeTypeCode) && StringUtils.isNotBlank(strTradeId))
        	{
        		String defaultAcctId = "";//获取集团用户的默认账户
        		IDataset tradePayRelation = TradePayRelaInfoQry.getTradePayRelaByTradeId(strTradeId);
        		if(IDataUtil.isNotEmpty(tradePayRelation))
        		{
        			for (int i = 0; i < tradePayRelation.size(); i++)
        	        {
        				IData payRelation = tradePayRelation.getData(i);
        				if(IDataUtil.isNotEmpty(payRelation)){
        					String actTag = payRelation.getString("ACT_TAG","");
        					String defaultTag = payRelation.getString("DEFAULT_TAG","");
        					String acctId = payRelation.getString("ACCT_ID","");
        					if("1".equals(actTag) 
        							&& "1".equals(defaultTag) 
        							&& StringUtils.isNotBlank(acctId))
        					{
        						defaultAcctId = acctId;
        						break;
        					}
        				}
        	        }
        		}
        		
        		if(StringUtils.isBlank(defaultAcctId))
        		{
        			String errMessage = "工单完工时获取该集团的默认账户出错!!";
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
        		}
        		
            	IDataset tradeAttrInfo = TradeAttrInfoQry.getDeskTopGrpDiscntAttrByTradeId(strTradeId);
            	if(IDataUtil.isNotEmpty(tradeAttrInfo))
            	{                    
            		for (int i = 0; i < tradeAttrInfo.size(); i++)
        	        {
            			IData tradeAttr = tradeAttrInfo.getData(i);
            			tradeAttr.put("PARTITION_ID", StrUtil.getPartition4ById(defaultAcctId));
            			tradeAttr.put("USER_ID", defaultAcctId); //集团默认账户ID
            			tradeAttr.put("SYNC_SEQUENCE", syncId);
            			tradeAttr.put("SYNC_DAY", syncDay);
        	        }
            		
            		//同步账务
            		syncTib(syncId,strTradeId,tradeAttrInfo);
            	}
        	}
        	else if(("3881".equals(tradeTypeCode) || "3883".equals(tradeTypeCode)) && StringUtils.isNotBlank(strTradeId))
        	{
        		String defaultAcctId = "";
        		//获取集团用户的默认账户
            	IData payRelas = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
            	
            	if(IDataUtil.isEmpty(payRelas))
        		{
        			String errMessage = "未获取到该集团的默认账户！";
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
        		}
        		
        		defaultAcctId = payRelas.getString("ACCT_ID","");
        		
        		IDataset tradeAttrInfo = TradeAttrInfoQry.getDeskTopGrpDiscntAttrByTradeId(strTradeId);
        		if(IDataUtil.isNotEmpty(tradeAttrInfo))
            	{                    
            		for (int i = 0; i < tradeAttrInfo.size(); i++)
        	        {
            			IData tradeAttr = tradeAttrInfo.getData(i);
            			tradeAttr.put("PARTITION_ID", StrUtil.getPartition4ById(defaultAcctId));
            			tradeAttr.put("USER_ID", defaultAcctId); //集团默认账户ID
            			tradeAttr.put("SYNC_SEQUENCE", syncId);
            			tradeAttr.put("SYNC_DAY", syncDay);
        	        }
            		
            		//同步账务
            		syncTib(syncId,strTradeId,tradeAttrInfo);
            	}
        		
        	}
        }
	}
	
	private static void syncSaleActiveAttrAcctDiscnt(IData mainTrade, String syncId, String syncDay) throws Exception
	{
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
		if( "3606".equals(tradeTypeCode)  )
    	{
			String packageId = mainTrade.getString("RSRV_STR2","");
			if("84022210".equals(packageId)){
				String userId = mainTrade.getString("USER_ID","");//集团产品的user_id
				String strTradeId = mainTrade.getString("TRADE_ID","");
	    		String defaultAcctId = "";
	    		//获取集团用户的默认账户
	        	IData payRelas = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
	        	
	        	if(IDataUtil.isEmpty(payRelas))
	    		{
	    			String errMessage = "未获取到该集团的默认账户！";
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMessage);
	    		}
	    		
	    		defaultAcctId = payRelas.getString("ACCT_ID","");
	    		//借用多媒体桌面查询台账sql
	    		IDataset tradeAttrInfo = TradeAttrInfoQry.getDeskTopGrpDiscntAttrByTradeId(strTradeId);
	    		if(IDataUtil.isNotEmpty(tradeAttrInfo))
	        	{                    
	        		for (int i = 0; i < tradeAttrInfo.size(); i++)
	    	        {
	        			IData tradeAttr = tradeAttrInfo.getData(i);
	        			tradeAttr.put("PARTITION_ID", StrUtil.getPartition4ById(defaultAcctId));
	        			tradeAttr.put("USER_ID", defaultAcctId); //集团默认账户ID
	        			tradeAttr.put("SYNC_SEQUENCE", syncId);
	        			tradeAttr.put("SYNC_DAY", syncDay);
	    	        }
	        		
	        		//同步账务
	        		syncTib(syncId,strTradeId,tradeAttrInfo);
	        	}
			} 
			
    	}
	}
	
	/**
     * 同步账务
     * @param ivSyncSequence
     * @param tradeId
     * @param attrInfos
     * @throws Exception
     */
    private static void syncTib(String ivSyncSequence,String tradeId,IDataset attrInfos) throws Exception
    {
        Dao.insert("TI_B_USER_DISCNT_ATTR", attrInfos, Route.getJourDbDefault());
    }
    private static void syncPlatAttr(IData mainTrade, String syncId, String syncDay) throws Exception
	{

		String tradeId = mainTrade.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);// mainTrade.getString("ACCEPT_MONTH");
		String cancelTag = mainTrade.getString("CANCEL_TAG");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String userId = mainTrade.getString("USER_ID");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");

		int rowCount = 0;
		if ("0".equals(cancelTag))
		{
			IData param = new DataMap();
			param.put("TRADE_ID", tradeId);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("SYNC_DAY", syncDay);
			param.put("SYNC_SEQUENCE", syncId);

			rowCount = Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_PLATSVC_ATTR", param, Route.getJourDbDefault());
		}else
		{
			IData param = new DataMap();
			param.put("SYNC_SEQUENCE", syncId);
			param.put("SYNC_DAY", syncDay);
			param.put("ACCEPT_MONTH", acceptMonth);
			param.put("TRADE_ID", tradeId);
			

			IDataset svcTrades = Dao.qryByCode("BOF_SYNC", "SEL_PLATSVCATTR_BY_TRADEID", param, Route.getJourDbDefault());
			if (IDataUtil.isNotEmpty(svcTrades))
			{
				IDataset idataset = new DatasetList();
				for (int i = 0, size = svcTrades.size(); i < size; i++)
				{
					String id = svcTrades.getData(i).getString("USER_ID");
					param.put("USER_ID", id);
					IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_PLATSVCATTR_BY_USERID", param);
					idataset.addAll(ids);
				}

				if (IDataUtil.isNotEmpty(idataset))
				{
					int[] rowArray = Dao.insert("TI_B_USER_PLATSVC_ATTR", idataset, Route.getJourDbDefault());
					int svcCount = getRowArrayCount(rowArray);
					rowCount = rowCount + svcCount;
				}
			}
		}
		
		if (rowCount == 0)
		{
			if (StringUtils.equals(tradeTypeCode, "10") || StringUtils.equals(tradeTypeCode, "500") || StringUtils.equals(tradeTypeCode, "700"))
			{
				IData param = new DataMap();
				param.put("SYNC_SEQUENCE", syncId);
				param.put("SYNC_DAY", syncDay);
				param.put("TRADE_ID", tradeId);
				param.put("USER_ID", userId);
				param.put("EPARCHY_CODE", eparchyCode);
				param.put("INST_ID", SeqMgr.getInstId(eparchyCode));

				Dao.executeUpdateByCodeCode("BOF_SYNC", "INS_USER_PLATSVC_ATTR_PRIV", param, Route.getJourDbDefault());
			}
		}
	}
}
