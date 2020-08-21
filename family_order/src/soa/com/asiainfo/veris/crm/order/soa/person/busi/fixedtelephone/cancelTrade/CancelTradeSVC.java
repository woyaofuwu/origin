/**
 *  
 */

package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.cancelTrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.canceltrade.CancelTradeBean;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelWidenetTradeService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-24 上午09:00:29 Modification History: Date Author Version
 *        Description
 *        ------------------------------------------------------------*
 *        2014-5-24 chengxf2 v1.0.0 修改原因
 */

public class CancelTradeSVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @date: 2014-5-24 上午09:01:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-5-24 chengxf2 v1.0.0 修改原因
	 */
	public IDataset queryUserCancelTrade(IData inData) throws Exception
	{
		String serialNumber = inData.getString("SERIAL_NUMBER");
		String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
		return TradeInfoQry.getMainTradeBySN(serialNumber, tradeTypeCode);
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @date: 2014-6-20 上午09:09:48 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-20 chengxf2 v1.0.0 修改原因
	 */
	public IData queryTradeBackFee(IData input) throws Exception
	{
		IData feeInfo = new DataMap();
		IData trade = new DataMap(input.getString("TRADE_INFO"));
		double operFee = trade.getDouble("OPER_FEE", 0.0);
		double foregift = trade.getDouble("FOREGIFT", 0.0);
		double advancePay = trade.getDouble("ADVANCE_PAY", 0.0);
		double backFee = operFee + foregift + advancePay;
		if (trade.getString("TRADE_TYPE_CODE").equals("600"))
		{
			String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
			IDataset tagInfos = TagInfoQry.getTagInfosByTagCode(tradeEparchyCode, "WIDENET_PREFEE_TAG", "CSM", "0");
			if (IDataUtil.isEmpty(tagInfos))
			{
				CSAppException.apperr(ParamException.CRM_PARAM_502);
			}
			if (tagInfos.getData(0).getString("TAG_INFO").compareTo(trade.getString("ACCEPT_DATE")) <= 0)
			{
				advancePay = 0.0;
				backFee = operFee + foregift;
			}
		}
		feeInfo.put("backOperFee", operFee / 100);
		feeInfo.put("backForeGift", foregift / 100);
		feeInfo.put("backAdvancePay", advancePay / 100);
		feeInfo.put("backFee", backFee / 100);
		return feeInfo;
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @date: 2014-6-19 下午05:56:19 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	public IDataset cancelTradeReg(IData input) throws Exception
	{
		String tradeId = input.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");
		if (IDataUtil.isEmpty(tradeInfos))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
		}

		/**************************** 数据准备 *************************/
		IData tradeInfo = tradeInfos.getData(0);// 当前界面选择的台账信息
		IData pubData = this.getPublicData(tradeInfo);// 操作员/trade_id/cancel_tag等相关信息

		// 如果存在发票需要冲红,用户需冲红发票后才能进行返销处理
		String needCHReceipt = StateTaxUtil.needCHReceipt(tradeInfo.getString("TRADE_ID", ""));
		if (StringUtils.isNotEmpty(needCHReceipt))
			CSAppException.apperr(TicketException.CRM_TICKET_14, needCHReceipt);

		/**************************** 规则校验 *************************/
		this.checkTrade(input, tradeInfo);

		/********************** 相关资料处理 **************************/
		dealTradeCancel(pubData, input, tradeInfo);

		/**************************** 发送PBOSS撤单申请 *************************/
		String cancelType = input.getString("CANCEL_TYPE", "0");
		IDataOutput dataOutput = applyCancelPbossTrade(pubData, tradeInfo, cancelType);
		IData head = dataOutput.getHead();// 得到头
		String resultCode = head.getString("X_RESULTCODE", "-1");
		if (!"0".equals(resultCode))
		{
			String resultInfo = head.getString("X_RESULTINFO");

			CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
		}
		IDataset result = dataOutput.getData();
		if (IDataUtil.isNotEmpty(result))
		{
			IData tmpData = result.getData(0);
			String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
			if (!"0".equals(xResultCode))
			{
				String resultInfo = tmpData.getString("X_RESULTINFO");
				CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
			}
		}

		// 返销票据
		CancelTradeBean bean = BeanManager.createBean(CancelTradeBean.class);
		bean.cancelTicketTrade(tradeInfo.getString("TRADE_ID", ""));

		// 若用户存在电子发票，则直接冲红
		bean.cancelEInvoice(tradeInfo);// 新增:电子发票返销
		return null;
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @date: 2014-6-19 下午09:39:48 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	private IDataOutput applyCancelPbossTrade(IData pubData, IData tradeInfo, String cancelType) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = tradeInfo.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		return PBossCall.orderCancelApply(tradeId, "0", acceptMonth, cancelType);
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-6-19 下午09:37:05 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	private void dealTradeCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception
	{
		dealOrderCancel(pubData, pgData, tradeInfo); // 处理TF_B_ORDER
		createHisTrade(pubData, pgData, tradeInfo);
		createCancelStaffTrade(pubData, pgData, tradeInfo);

		String[] keys = new String[3];
		keys[0] = "TRADE_ID";
		keys[1] = "ACCEPT_MONTH";
		keys[2] = "CANCEL_TAG";
		if (!Dao.delete("TF_B_TRADE", tradeInfo, keys, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_306, pubData.getString("TRADE_ID"));
		}
		createNewTrade(pubData, pgData, tradeInfo);
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-8-8 下午07:38:47 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-8-8 chengxf2 v1.0.0 修改原因
	 */
	private void dealOrderCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception
	{
		String orderId = tradeInfo.getString("ORDER_ID");
		IData orderInfo = UOrderInfoQry.qryOrderAllByOrderId(orderId);
		createHisOrder(pubData, pgData, orderInfo);

		String[] keys = new String[3];
		keys[0] = "ORDER_ID";
		keys[1] = "ACCEPT_MONTH";
		keys[2] = "CANCEL_TAG";
		if (!Dao.delete("TF_B_ORDER", orderInfo, keys, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			String msg = "删除订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
		createNewOrder(pubData, pgData, orderInfo);
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-8-8 下午07:53:35 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-8-8 chengxf2 v1.0.0 修改原因
	 */
	private void createHisOrder(IData pubData, IData pgData, IData orderInfo) throws Exception
	{
		IData orderData = new DataMap();
		orderData.putAll(orderInfo);
		orderData.put("CANCEL_TAG", "1");
		orderData.put("SUBSCRIBE_STATE", "A");
		orderData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
		orderData.put("CANCEL_TAG", "1");
		orderData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
		orderData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		orderData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		orderData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		orderData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		if (!Dao.insert("TF_BH_ORDER", orderData, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			String msg = "搬迁订单【" + orderInfo.getString("ORDER_ID") + "】至订单历史表失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}

	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-8-8 下午07:23:29 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-8-8 chengxf2 v1.0.0 修改原因
	 */
	private void createNewOrder(IData pubData, IData pgData, IData orderInfo) throws Exception
	{
		IData orderData = new DataMap();
		orderData.putAll(orderInfo);
		String newOrderId = pubData.getString("NEW_ORDER_ID");
		orderData.put("ORDER_ID", newOrderId);
		orderData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
		orderData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
		orderData.put("CANCEL_TAG", "3"); // 撤销
		orderData.put("ORDER_STATE", "0");
		orderData.put("CANCEL_STAFF_ID", orderInfo.getString("TRADE_STAFF_ID"));
		orderData.put("CANCEL_DEPART_ID", orderInfo.getString("TRADE_DEPART_ID"));
		orderData.put("CANCEL_CITY_CODE", orderInfo.getString("TRADE_CITY_CODE"));
		orderData.put("CANCEL_EPARCHY_CODE", orderInfo.getString("TRADE_EPARCHY_CODE"));
		long lOperFee = -(long) pgData.getDouble("OPER_FEE", 0);
		long lAdvancePay = -(long) pgData.getDouble("ADVANCE_PAY", 0);
		long lforegift = -(long) pgData.getDouble("FOREGIFT", 0);
		String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
		// long lSubscribeType = orderData.getLong("SUBSCRIBE_TYPE", 0);
		// orderData.put("SUBSCRIBE_TYPE", String.valueOf((lSubscribeType / 10)
		// * 10)); // 原订单类型转为相应的立即执行类型
		orderData.put("NEXT_DEAL_TAG", "0");
		orderData.put("OPER_FEE", String.valueOf(lOperFee * 100));
		orderData.put("ADVANCE_PAY", String.valueOf(lAdvancePay * 100));
		orderData.put("FOREGIFT", String.valueOf(lforegift * 100));
		orderData.put("FEE_STATE", strFeeState);
		orderData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
		orderData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("TRADE_STAFF_ID"));
		orderData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		orderData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		orderData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		orderData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		orderData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
		orderData.put("SUBSCRIBE_TYPE", "0");
		if (!Dao.insert("TF_B_ORDER", orderData, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			String msg = "生成返销订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-6-19 下午09:47:30 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	private void createNewTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
	{
		IData tradeData = new DataMap();
		tradeData.putAll(tradeInfo);
		tradeData.put("ORDER_ID", pubData.getString("NEW_ORDER_ID"));
		tradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
		tradeData.put("ACCEPT_MONTH", StringUtils.substring(pubData.getString("SYS_TIME"), 5, 7));
		tradeData.put("CANCEL_TAG", "3"); // 撤销
		tradeData.put("SUBSCRIBE_STATE", "0");
		tradeData.put("CANCEL_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
		tradeData.put("CANCEL_DEPART_ID", tradeInfo.getString("TRADE_DEPART_ID"));
		tradeData.put("CANCEL_CITY_CODE", tradeInfo.getString("TRADE_CITY_CODE"));
		tradeData.put("CANCEL_EPARCHY_CODE", tradeInfo.getString("TRADE_EPARCHY_CODE"));
		// long lOperFee = -pgData.getLong("OPER_FEE", 0);
		// long lAdvancePay = -pgData.getLong("ADVANCE_PAY", 0);
		// long lforegift = -pgData.getLong("FOREGIFT", 0);
		long lOperFee = -(long) pgData.getDouble("OPER_FEE", 0);
		long lAdvancePay = -(long) pgData.getDouble("ADVANCE_PAY", 0);
		long lforegift = -(long) pgData.getDouble("FOREGIFT", 0);
		String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
		// long lSubscribeType = tradeInfo.getLong("SUBSCRIBE_TYPE", 0);
		// tradeData.put("SUBSCRIBE_TYPE", String.valueOf((lSubscribeType / 10)
		// * 10)); // 原订单类型转为相应的立即执行类型
		tradeData.put("OPER_FEE", String.valueOf(lOperFee * 100));
		tradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay * 100));
		tradeData.put("FOREGIFT", String.valueOf(lforegift * 100));
		tradeData.put("FEE_STATE", strFeeState);
		tradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
		tradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("TRADE_STAFF_ID"));
		tradeData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		tradeData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		tradeData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		tradeData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		tradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
		if (!Dao.insert("TF_B_TRADE", tradeData, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_306, pubData.getString("TRADE_ID"));
		}
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-6-19 下午09:45:49 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	public void createHisTradeStaff(IData pubData, IData pgData, IData tradeInfo) throws Exception
	{
		IData tradeData = new DataMap();
		tradeData.putAll(tradeInfo);
		tradeData.put("CANCEL_TAG", "1");
		tradeData.put("SUBSCRIBE_STATE", "A");
		String tradeId = tradeInfo.getString("TRADE_ID");
		int day = 0;
		if (StringUtils.isNotBlank(tradeId))
		{
			String dayString = StringUtils.substring(tradeId, 6, 8);
			day = Integer.parseInt(dayString);
		}
		tradeData.put("DAY", String.valueOf(day));
		tradeData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
		tradeData.put("CANCEL_TAG", "1");
		tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
		tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		tradeData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		if (!Dao.insert("TF_BH_TRADE_STAFF", tradeData, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_306, pubData.getString("TRADE_ID"));
		}
	}

	public void createCancelStaffTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
	{
		String tradeId = pubData.getString("TRADE_ID");
		IDataset tradeStaffInfos = TradeStaffInfoQry.queryTradeStaffByTradeId(tradeId, "0");

		if (IDataUtil.isEmpty(tradeStaffInfos))
		{
			return;
		}

		// 修改原单据的状态
		IData param = new DataMap();

		param.put("TRADE_ID", tradeId);
		param.put("DAY", tradeStaffInfos.getData(0).getString("DAY"));
		param.put("CANCEL_TAG", "1");// 被返销
		param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
		param.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		param.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		param.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		param.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		// 更新失败不报错
		Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_STAFF_CANCEL_TAG", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));

		// 新增一条cancel_tag=2的单据
		IData newData = tradeStaffInfos.getData(0);

		if (newData.getLong("OPER_FEE", 0) > 0)
		{
			long lOperFee = -(long) pgData.getDouble("OPER_FEE", 0);
			newData.put("OPER_FEE", String.valueOf(lOperFee * 100));
		}

		if (newData.getLong("FOREGIFT", 0) > 0)
		{
			long lforegift = -(long) pgData.getDouble("FOREGIFT", 0);
			newData.put("FOREGIFT", String.valueOf(lforegift * 100));
		}

		if (newData.getLong("ADVANCE_PAY", 0) > 0)
		{
			long lAdvancePay = -(long) pgData.getDouble("ADVANCE_PAY", 0);
			newData.put("ADVANCE_PAY", String.valueOf(lAdvancePay * 100));
		}

		newData.put("ORDER_ID", pubData.getString("NEW_ORDER_ID"));
		newData.put("CANCEL_TAG", "3");
		newData.put("EXEC_TIME", pubData.getString("SYS_TIME"));

		// 新单据cancel字段记录的是原单据的 受理相关信息
		newData.put("CANCEL_DATE", newData.getString("ACCEPT_DATE"));
		newData.put("CANCEL_STAFF_ID", newData.getString("TRADE_STAFF_ID"));
		newData.put("CANCEL_DEPART_ID", newData.getString("TRADE_DEPART_ID"));
		newData.put("CANCEL_CITY_CODE", newData.getString("TRADE_CITY_CODE"));
		newData.put("CANCEL_EPARCHY_CODE", newData.getString("TRADE_EPARCHY_CODE"));
		newData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
		newData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		newData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		newData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		newData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));

		// 分区号
		newData.put("DAY", SysDateMgr.getSysDate("dd"));

		Dao.insert("TF_BH_TRADE_STAFF", newData, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @throws Exception
	 * @date: 2014-6-19 下午09:45:15 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	private void createHisTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
	{
		IData tradeData = new DataMap();
		tradeData.putAll(tradeInfo);
		tradeData.put("CANCEL_TAG", "1");
		tradeData.put("SUBSCRIBE_STATE", "A");
		tradeData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
		tradeData.put("CANCEL_TAG", "1");
		tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
		tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		tradeData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		if (!Dao.insert("TF_BH_TRADE", tradeData, Route.getJourDb(CSBizBean.getTradeEparchyCode())))
		{
			CSAppException.apperr(TradeException.CRM_TRADE_306, pubData.getString("TRADE_ID"));
		}
	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @date: 2014-6-19 下午08:01:45 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	private void chkTradeBeforeCancel(IData pubData, IData tradeInfo) throws Exception
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @Function:
	 * @Description: 该方法的描述
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: chengxf2
	 * @date: 2014-6-19 下午08:01:14 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-6-19 chengxf2 v1.0.0 修改原因
	 */
	private IData getPublicData(IData tradeInfo) throws Exception
	{
		IData pubData = new DataMap();
		pubData.put("ORDER_ID", tradeInfo.getString("ORDER_ID", ""));
		pubData.put("TRADE_ID", tradeInfo.getString("TRADE_ID", ""));
		pubData.put("NEW_ORDER_ID", SeqMgr.getOrderId());
		pubData.put("SYS_TIME", SysDateMgr.getSysTime());
		pubData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		pubData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		pubData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		pubData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		return pubData;
	}

	public void checkTrade(IData tradeData, IData tradeInfo) throws Exception
	{
		if (tradeData.getString("TRADE_TYPE_CODE").equals("9701"))
		{
			IData pbossResData = null;
			IData servParam = new DataMap();
			servParam.put("HOME_DOMAIN", "PCRM");
			servParam.put("ORIG_DOMAIN", "PBOS");
			servParam.put("ACTIVITYCODE", "T4022008");
			servParam.put("BIPCODE", "IOM2B008");

			IData svcCont = new DataMap();
			IData reqInfo = new DataMap();
			reqInfo.put("SUBSCRIBE_ID", tradeData.getString("TRADE_ID"));
			svcCont.put("REQUEST_INFO", reqInfo);
			servParam.put("SVC_CONT", svcCont);

			String resTradeCode = "IRes_Fixed_MphoneRelease";
			String xGetMode = "2";
			String resNo = tradeData.getString("SERIAL_NUMBER");
			String resTypeCode = "N";
			PBossCall.ResOccupyUseTT(resTradeCode, xGetMode, resNo, resTypeCode);
			/*
			 * Object pbossObj = HttpHelper.callHttpSvc(pd, "TCS_CallPbossPf",
			 * servParam, true); if(pbossObj==null) {
			 * common.error("系统异常：PBOSS接口返回NULL，请确认PBOSS系统正常！"); } pbossResData
			 * = (IData)pbossObj;
			 * if(pbossResData.getData("X_RESULEINFO").getString
			 * ("X_RESULT_CODE").trim().equals("0")) { } else { common.error
			 * ("该用户订单【"
			 * +tradeData.getString("TRADE_ID")+"】不能撤销:"+pbossResData.getData
			 * ("X_RESULEINFO").getString ("X_RESULT_DESC")); }
			 */
			String cancelType = tradeData.getString("CANCEL_TYPE", "0");
			IData pubData = this.getPublicData(tradeInfo);
			IDataOutput dataOutput = applyCancelPbossTrade(pubData, tradeInfo, cancelType);
			IData head = dataOutput.getHead();// 得到头
			String resultCode = head.getString("X_RESULTCODE", "-1");
			if (!"0".equals(resultCode))
			{
				String resultInfo = head.getString("X_RESULTINFO");

				CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
			}
			IDataset result = dataOutput.getData();
			if (IDataUtil.isNotEmpty(result))
			{
				IData tmpData = result.getData(0);
				String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
				if (!"0".equals(xResultCode))
				{
					String resultInfo = tmpData.getString("X_RESULTINFO");
					CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
				}
			}

			IDataset tradeResSet = getTradeResInfo(tradeData);
			String hintStr = "";
			int phoneNum = 0;
			int countNum = 0;
			if (tradeResSet.size() > 1)
			{
				for (int i = 0; i < tradeResSet.size(); i++)
				{
					if (tradeResSet.getData(i).getString("RSRV_STR4", "").equals("01"))
					{
						phoneNum++;
					} else if (tradeResSet.getData(i).getString("RSRV_STR4", "").equals("04"))
					{
						countNum++;
					}
				}
			}

			if (phoneNum > 0 || countNum > 0)
			{
				hintStr = "改用户需要返还";
				if (phoneNum > 0)
				{
					hintStr = hintStr + phoneNum + "个固话座机";
				}
				if (countNum > 0)
				{
					if (phoneNum > 0)
						hintStr = hintStr + "," + countNum + "个固话计费器";
					else
						hintStr = hintStr + countNum + "个固话计费器";
				}

				tradeData.put("HINT_INFO", hintStr);
			}

		} else if (tradeData.getString("TRADE_TYPE_CODE").equals("9702"))
		{
			String tradeId = tradeData.getString("TRADE_ID");

			IDataset pbossTradeSet = TradeInfoQry.getPBossFinishByTradeId(tradeId);

			if (pbossTradeSet == null || pbossTradeSet.size() <= 0)
			{
				return; // 尚未发到PBOSS，可以直接撤单
			}

			IData pbossTrade = pbossTradeSet.getData(0);
			if (!pbossTrade.getString("RSRV_STR1").equals("0") || pbossTrade.getString("RSRV_STR2", "").equals(""))
			{
				return; // 发给PBOSS失败或PBOSS尚未返回，可以直接撤单
			}

			if (!pbossTrade.getString("RSRV_STR2").equals("0") && !pbossTrade.getString("RSRV_STR2").equals("1") && !pbossTrade.getString("RSRV_STR2").equals("2"))
			{
				return;// PBOSS执行失败，可以直接撤单
			}

			if (tradeData.getString("EXEC_TIME").compareTo(SysDateMgr.getSysDate()) <= 0)
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户订单【" + tradeData.getString("TRADE_ID") + "】已经运行，不能撤销！");
			}
		}
	}

	public IDataset getTradeResInfo(IData data) throws Exception
	{
		String tradeId = data.getString("TRADE_ID");
		IDataset dataset = new DatasetList();
		dataset = TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
		return dataset;
	}

}
