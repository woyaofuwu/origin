
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeEntry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeOtherFee;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.TradeLogUtil;

public final class TradeFinish
{
	final static Logger logger = Logger.getLogger(TradeFinish.class);

	public static String LIMIT_TYPE_ALL = "0";

	public static  String LIMIT_TYPE_PAT = "1";

	public static void finish(String tradeId, String acceptMonth, String canceltag, String routeId) throws Exception
	{
		// 查询订单信息
		IDataset tradeset = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, canceltag);

		if (IDataUtil.isEmpty(tradeset))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_65, tradeId);
		}

		// 得到订单信息
		IData mainTrade = tradeset.getData(0);

		// 设置订单路由
		mainTrade.put(Route.ROUTE_EPARCHY_CODE, routeId);

		// 是否完工挂起
		long start = System.currentTimeMillis();
		boolean fh = finishHang(mainTrade);
		TradeLogUtil.log(tradeId, "finishHang", System.currentTimeMillis() - start);

		if (fh)
		{
			// 更新订单状态
			TradeMag.updTradeStateByTradeId(tradeId, acceptMonth, canceltag, "B", "订单完工等待", routeId);

			return;
		}

		// 执行完工
		finishExec(mainTrade);

		// 查找依赖我的订单，删除所有依赖我的关系，修改依赖我的订单的状态B为P
		start = System.currentTimeMillis();
		IDataset limitedTrades = new DatasetList();

		// 完全依赖关系 处理
		IDataset limited0Trades = TradeLimitInfoQry.getLimitByTradeId(tradeId, LIMIT_TYPE_ALL);
		if (IDataUtil.isNotEmpty(limited0Trades))
		{
			limitedTrades.addAll(limited0Trades);
			TradeMag.delLimit0ByTradeId(tradeId);
		}

		// 部分 依赖关系 处理
		IDataset limited1Trades = TradeLimitInfoQry.getLimitByTradeId(tradeId, LIMIT_TYPE_PAT);
		if (IDataUtil.isNotEmpty(limited1Trades))
		{
			limitedTrades.addAll(limited1Trades);
			String L1Trade = limited1Trades.getData(0).getString("TRADE_ID");
			TradeMag.delLimit1ByTradeId(L1Trade);
		}

		if (IDataUtil.isNotEmpty(limitedTrades))
		{
			// 修改依赖我的订单状态B为P
			for (int i = 0, size = limitedTrades.size(); i < size; i++)
			{
				IData ylData = limitedTrades.getData(i);

				String ylTradeId = ylData.getString("TRADE_ID", "");
				String ylAcceptMonth = StrUtil.getAcceptMonthById(ylTradeId);
				String ylCancelTag = "0";
				String ylRouteId = ylData.getString("ROUTE_ID", "");

				TradeMag.updLimitTradeStateByTradeId(ylTradeId, ylAcceptMonth, ylCancelTag, "P", "订单依赖解除，进入订单完工等待", ylRouteId);
			}
		}
		
		// tradelimit
		TradeLogUtil.log(tradeId, "finishLimit", System.currentTimeMillis() - start);

		// 订单标识
		String orderId = mainTrade.getString("ORDER_ID");

		// 后续订单激活
		String userId = mainTrade.getString("USER_ID");

		TradeMag.actNextTradeByUserId(userId);
		//订单优先表数据搬迁
        TradePriority.moveTrades(tradeId);

		// 记录完工轨迹
		TradeMag.traceLog(orderId, tradeId, acceptMonth, null, "finishTrade", "0", "ok");

		// 批量业务标识
		String batchId = mainTrade.getString("BATCH_ID");

		if (StringUtils.isNotBlank(batchId))
		{
			// 回写批量表状态，已完工
			//TradeMag.updTradeBatDealByBatchId(batchId, "9", "订单已完工");
			IData data = new DataMap();
			String bacceptMonth = StrUtil.getAcceptMonthById(batchId);
			data.put("OPERATE_ID", batchId);
			data.put("TRADE_ID", orderId);
			data.put("ACCEPT_MONTH", bacceptMonth);
			data.put("ROUTE_EPARCHY_CODE", routeId);
			data.put("DEAL_STATE", "9");
			data.put("DEAL_RESULT", "订单已完工");
			TradeMag.updTradeBatDealByBatchId(data);
		}
		
		notificationPfMonOrderAction(mainTrade, BofConst.PF_COMPLAIN_VISUALIZATION_FINISH_SUCCESS);

		return;
	}
	
	
	/**
	 * pf客户投诉可视化采集订单数据
	 * 
	 * @param mainTrade
	 * @param state     false:完工失败，true:正常完工
	 * @throws Exception
	 */
	protected static void notificationPfMonOrderAction(IData mainTrade, boolean state) throws Exception {
		String tradeTypeCode = mainTrade.getString(KeyConstants.TRADE_TYPE_CODE);
		String[] tradeTypes = BizEnv.getEnvStringArray(BofConst.PF_COMPLAIN_VISUALIZATION_TYPE_ID);
		boolean isNotification = false;
		if (tradeTypes != null && tradeTypes.length > 0) {
			for (String tradeType : tradeTypes) {
				if (tradeTypeCode.equals(tradeType)) {
					isNotification = true;
					break;
				}
			}
		}
		if (isNotification) {
			String orderId = mainTrade.getString(KeyConstants.ORDER_ID);
			String tradeId = mainTrade.getString(KeyConstants.TRADE_ID);
			String cancelTag = mainTrade.getString(KeyConstants.CANCEL_TAG);
			IData inParam = new DataMap();
			inParam.put(KeyConstants.ORDER_ID, orderId);
			inParam.put(KeyConstants.TRADE_ID, tradeId);
			// 正常完工：0，正常返销：2，完工失败：1
			String action = !state ? BofConst.PF_COMPLAIN_VISUALIZATION_ACTION_FINISH_FAIL
					: (BofConst.CANCEL_TAG_NO.equals(cancelTag)
							? BofConst.PF_COMPLAIN_VISUALIZATION_ACTION_FINISH_SUCCESS
							: BofConst.PF_COMPLAIN_VISUALIZATION_ACTION_CANCEL);
			inParam.put(KeyConstants.ACTION, action);// 正常完工：0
			try {
				CSAppCall.callNGPf("PF_MON_ORDER_ACTION", inParam);
			} catch (Exception e) {
			}
		}
	}

	private static void finishAfterAction(IData mainTrade) throws Exception
	{
		// 客管接口
		TradeEntry.entry(mainTrade);

		// 得到返销标记
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		// 业务费用接口
		TradeOtherFee.finishOtherFee(mainTrade);

		if (cancelTag.equals("2"))
		{
			// 订单之后
			TradeAction.action(mainTrade, "undofinish");
		}
		else if (cancelTag.equals("3"))
		{
			TradeAction.action(mainTrade, "cancelfinish");
		}
		else
		{
			// 订单之后
			TradeAction.action(mainTrade, "finish");
		}
	}

	private static void finishAPI_full(IData mainTrade) throws Exception
	{
		// 开通标记
		String olcomTag = mainTrade.getString("OLCOM_TAG", "");
		String tradeId = mainTrade.getString("TRADE_ID");

		// 如果OLCOM_TAG=3只发服务开通,不完工不搬历史
		if ("3".equals(olcomTag))
			return;

		// 如果OLCOM_TAG=2，只发服开不用完工
		if (!("2".equals(olcomTag)))
		{
			// 订单之前动作
			long start = System.currentTimeMillis();
			finishBeforeAction(mainTrade);
			long end1 = System.currentTimeMillis() - start;

			// 订单备份
			start = System.currentTimeMillis();
			TradeBack.back(mainTrade);
			long end2 = System.currentTimeMillis() - start;

			// 订单归档
			start = System.currentTimeMillis();
			TradeArch.arch(mainTrade);
			long end3 = System.currentTimeMillis() - start;

			// 订单后续动作
			start = System.currentTimeMillis();
			finishAfterAction(mainTrade);
			long end4 = System.currentTimeMillis() - start;

			// 订单同步
			start = System.currentTimeMillis();
			TradeSync.sync(mainTrade);
			long end5 = System.currentTimeMillis() - start;
			
			TradeLogUtil.log(tradeId, String.format("finishBeforeAction=%sms, finishTradeBack=%sms, finishTradeArch=%sms, finishAfterAction=%sms, finishTradeSync=%sms", String.valueOf(end1), String.valueOf(end2), String.valueOf(end3), String.valueOf(end4), String.valueOf(end5)));
		}

		// 订单标识
		String orderId = mainTrade.getString("ORDER_ID");

		// 受理月份
		String acceptMonth = mainTrade.getString("ACCETP_MONTH");

		// 订单搬迁
		TradeMove.move(mainTrade);
	}
    private static void finishAPIWithoutArchSync(IData mainTrade) throws Exception {
        // 开通标记
        String olcomTag = mainTrade.getString("OLCOM_TAG", "");
        String tradeId = mainTrade.getString("TRADE_ID");

        // 如果OLCOM_TAG=3只发服务开通,不完工不搬历史
        if ("3".equals(olcomTag))
            return;

        // 如果OLCOM_TAG=2，只发服开不用完工
        if (!("2".equals(olcomTag))) {
            // 订单之前动作
            long start = System.currentTimeMillis();
            finishBeforeAction(mainTrade);
            long end1 = System.currentTimeMillis() - start;

            // 订单备份
            start = System.currentTimeMillis();
            TradeBack.back(mainTrade);
            long end2 = System.currentTimeMillis() - start;

            // 订单后续动作
            start = System.currentTimeMillis();
            finishAfterAction(mainTrade);
            long end3 = System.currentTimeMillis() - start;

            TradeLogUtil.log(tradeId, String.format("finishBeforeAction=%sms, finishTradeBack=%sms, finishAfterAction=%sms", String.valueOf(end1), String.valueOf(end2), String.valueOf(end3)));
        }

        // 订单标识
        String orderId = mainTrade.getString("ORDER_ID");

        // 受理月份
        String acceptMonth = mainTrade.getString("ACCETP_MONTH");

        // 订单搬迁
        TradeMove.move(mainTrade);
    }

	private static void finishAPI_fullcancel(IData mainTrade) throws Exception
	{
		// 订单备份
		// TradeBack.back(mainTrade);

		// 订单归档
		// TradeArch.archUndo(mainTrade);

		// 订单后续动作
		finishAfterAction(mainTrade);

		// 订单同步
		// TradeSync.sync(mainTrade);

		// 订单搬迁
		TradeMove.move(mainTrade);
	}

	private static void finishAPI_fullundo(IData mainTrade) throws Exception
	{
		// 订单备份
		// TradeBack.back(mainTrade);

		// 订单归档
		TradeArch.archUndo(mainTrade);

		// 订单后续动作
		finishAfterAction(mainTrade);

		// 订单同步
		TradeSync.sync(mainTrade);

		// 订单搬迁
		TradeMove.move(mainTrade);
	}

	private static void finishAPI_move(IData mainTrade) throws Exception
	{
		// 订单搬迁
		TradeMove.move(mainTrade);
	}

	private static void finishBeforeAction(IData mainTrade) throws Exception
	{
		TradeAction.action(mainTrade, "beforeFinish");
	}

	private static void finishExec(IData mainTrade) throws Exception
	{
		// 得到返销标记
		String cancelTag = mainTrade.getString("CANCEL_TAG");

		if (cancelTag.equals("2"))
		{
			finishAPI_fullundo(mainTrade);
		}
		else if (cancelTag.equals("3"))
		{
			finishAPI_fullcancel(mainTrade);
		}
		else
		{
			// 得到完工流程
			String bpmCode = getBpmCode(mainTrade);

			if (bpmCode.equals("TCS_TradeFinish"))
			{
				// 标准完工流程API 完整版
				finishAPI_full(mainTrade);
			}
			else if (bpmCode.equals("TCS_TradeMove"))
			{
				// 标准完工流程API 搬迁版
				finishAPI_move(mainTrade);
			}
			else if (bpmCode.equals("TCS_TradeFinishWithoutArchSync"))
			{
				// 标准完工流程API 无归档同步版
				finishAPIWithoutArchSync(mainTrade);
			}
			else
			{
				CSAppException.apperr(TradeException.CRM_TRADE_65c, bpmCode);

				// 非标准完工流程LCU版
				// finishLCU(mainTrade);
			}
		}

		return;
	}

	private static boolean finishHang(IData mainTrade) throws Exception
	{
		// 判断是否有自身依赖
		String userId = mainTrade.getString("USER_ID");
		String execTime = mainTrade.getString("EXEC_TIME");
		String tradeId = mainTrade.getString("TRADE_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		
		IDataset out = TradeInfoQry.getFinishHangByPk(tradeId, userId, execTime, tradeTypeCode);

		IData map = out.getData(0);

		if (IDataUtil.isNotEmpty(map))
		{
			String count = map.getString("COUNT", "0");

			if (count.equals("1")) // 有挂起
			{
				return true;
			}
		}

		// 判断自己是否其它依赖（需要等待其他订单)
		IDataset limitTrades = TradeLimitInfoQry.getLimitedByTradeId(tradeId);

		if (IDataUtil.isNotEmpty(limitTrades))
		{
			return true;
		}

		return false;
	}

	private static String getBpmCode(IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String tradeEparchyCode = mainTrade.getString("EPARCHY_CODE");

		// 得到业务类型参数
		IData tradeTypePara = UTradeTypeInfoQry.getTradeType(tradeTypeCode, tradeEparchyCode);

		// 得到完工流程
		String bpmCode = tradeTypePara.getString("BPM_CODE", "");

		if (StringUtils.isBlank(bpmCode))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_65a, tradeId, tradeTypeCode);
		}

		mainTrade.put("BPM_CODE", bpmCode);

		return bpmCode;
	}

	public static IDataset getUcakey(String tradeId, String acceptMonth, String userId, String tradeTypeCode, String routeId) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("tradeId=[" + tradeId + "] routeId=[" + routeId + "]");
		}

		// 得到缓存对象
		IDataset datas = TradeInfoQry.getUcaKeyByPk(tradeId, acceptMonth, userId, tradeTypeCode);

		if (IDataUtil.isEmpty(datas))
		{
			return null;
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("datas=" + datas.toString());
		}

		IDataset delUCAKey = new DatasetList();

		IData map = null;
		String idType = "";
		String idValue = "";
		String snValue = "";

		for (int row = 0, size = datas.size(); row < size; row++)
		{
			map = datas.getData(row);

			idType = map.getString("ID_TYPE");
			idValue = map.getString("ID_VALUE");
			snValue = map.getString("SN_VALUE");

			if ("CUSTOMER".equals(idType)) // 客户核心
			{
				map = new DataMap();
				map.put("ID_TYPE", "CustomerByCustId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
			else if ("PERSON".equals(idType)) // 个人客户
			{
				map = new DataMap();
				map.put("ID_TYPE", "PersonByCustId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
			else if ("USER".equals(idType)) // 用户
			{
				map = new DataMap();
				map.put("ID_TYPE", "UserBySn");
				map.put("ID_VALUE", snValue);

				delUCAKey.add(map);

				map = new DataMap();
				map.put("ID_TYPE", "UserByUserId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
			else if ("PROD".equals(idType)) // 产品
			{
				map = new DataMap();
				map.put("ID_TYPE", "MainProdByUserId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
			else if ("ACCT".equals(idType)) // 账户
			{
				map = new DataMap();
				map.put("ID_TYPE", "ActByAcctId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
			else if ("DEFPAY".equals(idType)) // 付费关系
			{
				map = new DataMap();
				map.put("ID_TYPE", "DefaultPayRelaByUserId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
			else if ("ACCTDAYS".equals(idType)) // 用户账期
			{

				map = new DataMap();
				map.put("ID_TYPE", "AcctDaysByUserId");
				map.put("ID_VALUE", idValue);

				delUCAKey.add(map);
			}
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("delDatas=" + delUCAKey.toString());
		}

		String cacheKey = "";

		for (int row = 0, size = delUCAKey.size(); row < size; row++)
		{
			map = delUCAKey.getData(row);

			idType = map.getString("ID_TYPE");
			idValue = map.getString("ID_VALUE");

			// 得到缓存key
			cacheKey = CacheKey.getUcaCacheKey(idValue, idType, routeId);

			if (logger.isDebugEnabled())
			{
				logger.debug("get cacheKey=[" + cacheKey + "]");
			}

			// 保存缓存key，destroy时用
			map.put("ID_KEY", cacheKey);
		}

		return delUCAKey;
	}

	public static void finishAPIWithOutMove(IData mainTrade) throws Exception
	{
		// 开通标记
		String olcomTag = mainTrade.getString("OLCOM_TAG", "");

		// 如果OLCOM_TAG=3只发服务开通,不完工不搬历史
		if ("3".equals(olcomTag))
			return;

		// 如果OLCOM_TAG=2，只发服开不用完工
		if (!("2".equals(olcomTag)))
		{
			// 订单之前动作
			finishBeforeAction(mainTrade);

			// 订单备份
			TradeBack.back(mainTrade, Route.getJourDb());

			// 订单归档
			TradeArch.arch(mainTrade, Route.getJourDb());

			// 订单后续动作
			finishAfterAction(mainTrade);

			// 订单同步
			TradeSync.sync(mainTrade, Route.getJourDb());
		}
	}
}
