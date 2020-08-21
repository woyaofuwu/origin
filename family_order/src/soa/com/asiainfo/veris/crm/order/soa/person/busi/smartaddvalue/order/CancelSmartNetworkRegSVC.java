package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartKindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.SmartNetWorkTradeQuery;

public class CancelSmartNetworkRegSVC extends CSBizService {

	private Logger log = Logger.getLogger(CancelSmartNetworkRegSVC.class);

	private static final long serialVersionUID = 1L;

	private IDataOutput applyCancelPbossTrade(IData pubData, IData tradeInfo, String cancelType, String cancelReasonType, String cancelsedReasonType, String remark) throws Exception {
		String tradeId = tradeInfo.getString("TRADE_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		// REQ201811010006关于NGBOSS系统装机撤单原因推送至装机系统的需求 wuhao5
		return PBossCall.orderCancelApply(tradeId, "0", acceptMonth, cancelType, cancelReasonType, cancelsedReasonType, remark);
	}

	public IDataset cancelTradeReg(IData input) throws Exception {

		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx55 " + input);
		String tradeId = input.getString("TRADE_ID");
		String cityCode = getVisit().getCityCode();
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth_1(tradeId, acceptMonth);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx60 " + tradeInfos);
		
		/**************************** 数据准备 *************************/
		IData tradeInfo = tradeInfos.getData(0);// 当前界面选择的台账信息
		IData pubData = this.getPublicData(tradeInfo);// 操作员/trade_id/cancel_tag等相关信息
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx65 " + pubData);

		/**************************** 规则校验 *************************/
		// chkTradeBeforeCancel(pubData, tradeInfo);

		String tradeTypeCode = tradeInfo.getString("TRADE_TYPE_CODE", "870");

		if (IDataUtil.isNotEmpty(tradeInfos) && tradeTypeCode.equals("870")) {
			String userid = tradeInfos.getData(0).getString("USER_ID", "").trim();
			// 如存在值，即终端出库了，则不可以撤单。
			IDataset othersInfos = UserOtherInfoQry.getUserOthersInfoByUseridValuecodeSTR2(userid, "ZNZW", tradeId);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx76 " + othersInfos);

			if (IDataUtil.isNotEmpty(othersInfos)) {
				// 测试暂时屏蔽，生产要放开
				CSAppException.appError("-1", "该笔业务已不能撤单![" + tradeId + "]");
			}
		}

		/********************** 相关资料处理 **************************/
		dealTradeCancel(pubData, input, tradeInfo);
 
		/**************************** 发送PBOSS撤单申请 *************************/
		String cancelType = input.getString("CANCEL_TYPE", "0");
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx88 " + cancelType);

		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx90_pubData " + pubData);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx90_tradeInfo " + tradeInfo);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx90_cancelType " + cancelType);

		String cancelReasonType = input.getString("CANCEL_REASON_ONE", "").trim();
		String cancesedReasonType = input.getString("CANCEL_REASON_TWO", "").trim();
		String remark = input.getString("REMARK", "").trim();
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx96_cancelReasonType " + cancelReasonType);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx96_cancesedReasonType " + cancesedReasonType);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx96_remark " + remark);

		IDataOutput dataOutput = applyCancelPbossTrade(pubData, tradeInfo, cancelType, cancelReasonType, cancesedReasonType, remark);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx91 " + dataOutput);
		// {"head":{"X_RECORDNUM":"1","X_NODENAME":"pf-node01-ngpfserver1","X_PAGINCURRENT":"0","X_RESULTSIZE":"1","X_RESULTCOUNT":"0","X_PAGINCOUNT":"0","X_RSPDESC":"ok","X_RSPTYPE":"0","X_RESULTCODE":"0","X_RESULTINFO":"ok","X_PAGINSIZE":"0"},
		// "data":[{"ORDER_ID":"1118112609096170","X_RESULTCODE":"0000","X_RESULTTYPE":"0","X_RESULTINFO":"SUCCESS"}]}

		IData head = dataOutput.getHead();// 服开返回报文头
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx94 " + head);

		IDataset dataresutl = dataOutput.getData();// 服开返回报文体
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx97 " + dataresutl);

		String resultCode = head.getString("X_RESULTCODE", "-1");
		if (!"0".equals(resultCode)) {
			String resultInfo = head.getString("X_RESULTINFO");
			CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
		}
		IDataset result = dataOutput.getData();
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx105 " + result);

		if (IDataUtil.isNotEmpty(result)) {
			IData tmpData = result.getData(0);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx109 " + tmpData);

			String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
			if (!"0".equals(xResultCode)) {
				String resultInfo = tmpData.getString("X_RESULTINFO");
				CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
			}
		}
		 
		String staffcityCode = null;
		String custName = null;
		String installAddr = null;
		String recvObject = null;
		String serialNumberbak = null;

		/*
		 * IDataset dataset = this.queryWidenet(input); if
		 * (IDataUtil.isNotEmpty(dataset)) { custName =
		 * dataset.first().getString("CUST_NAME"); installAddr =
		 * dataset.first().getString("DETAIL_ADDRESS"); }
		 */
		/*
		 * if ("1".equals(canceltype)) { staffcityCode =
		 * input.getString("STAFF_CITY_CODE"); recvObject =
		 * input.getString("STAFF_PHONE"); serialNumberbak =
		 * input.getString("SERIAL_NUMBER"); } else {
		 */
		if (IDataUtil.isNotEmpty(dataresutl)) {
			staffcityCode = dataresutl.first().getString("STAFF_CITY_CODE");
			recvObject = dataresutl.first().getString("STAFF_PHONE");
			serialNumberbak = dataresutl.first().getString("SERIAL_NUMBER");
		}
		// }

		if ("HNKF".equals(cityCode) && (recvObject != null && !"".equals(recvObject))) {
			IData smsData = new DataMap();
			String noticeContent = "手机号码：" + serialNumberbak + "; 用户名：" + custName + "; 装机地址：" + installAddr + "。 10086已撤单，如果该工单已派发至安装师傅，请及时通知装机师傅无须上门，请知晓! " + "撤单时间：" + SysDateMgr.getSysTime();
			smsData.put("RECV_OBJECT", recvObject);
			smsData.put("NOTICE_CONTENT", noticeContent);
			smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(recvObject).getString("USER_ID"));
			smsData.put("PRIORITY", "50");
			smsData.put("REMARK", "10086撤单");
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx150 " + smsData);

			SmsSend.insSms(smsData);
			// 增加撤单短信发送给B角员工.TD_M_STAFF_B
			IData Bdata = new DataMap();
			Bdata.put("CITY_CODE", staffcityCode);
			IDataset Bdataset = this.queryStaffB(Bdata);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx157 " + Bdataset);

			if (!IDataUtil.isEmpty(Bdataset)) {
				for (int i = 0; i < Bdataset.size(); i++) {
					IData data = Bdataset.getData(i);
					noticeContent = "手机号码：" + serialNumberbak + "; 用户名：" + custName + "; 装机地址：" + installAddr + "。 10086已撤单，如果该工单已派发至安装师傅，请及时通知装机师傅无须上门，请知晓! " + "撤单时间：" + SysDateMgr.getSysTime();
					smsData.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
					smsData.put("NOTICE_CONTENT", noticeContent);
					smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(recvObject).getString("USER_ID"));
					smsData.put("PRIORITY", "50");
					smsData.put("REMARK", "10086撤单");
					System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx168 " + smsData);

					SmsSend.insSms(smsData);
				}
			}
		}
		if (!"1".equals(cancelType)) {
			// CRM撤单需要短信通知客户
			smsNotifyCustomer(tradeInfo);
		}
		dataOutput.getData().getData(0).put("NEW_ORDER_ID", pubData.getString("NEW_ORDER_ID"));
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx179 " + dataOutput.getData());		 
		
		return dataOutput.getData();
	}

	private void createHisOrder(IData pubData, IData pgData, IData orderInfo) throws Exception {
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
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx200 " + orderData);

		if (!Dao.insert("TF_BH_ORDER", orderData, Route.getJourDb(BizRoute.getTradeEparchyCode()))) {
			String msg = "搬迁订单【" + orderInfo.getString("ORDER_ID") + "】至订单历史表失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
	}

	private void createHisTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception {
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
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx220 " + tradeData);

		if (!Dao.insert("TF_BH_TRADE", tradeData, Route.getJourDb(BizRoute.getTradeEparchyCode()))) {
			String msg = "搬迁订单【" + tradeInfo.getString("TRADE_ID") + "】至历史表失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
	}

	/*
	 * public void createHisTradeStaff(IData pubData, IData pgData, IData
	 * tradeInfo) throws Exception { IData tradeData = new DataMap();
	 * tradeData.putAll(tradeInfo); tradeData.put("CANCEL_TAG", "1");
	 * tradeData.put("SUBSCRIBE_STATE", "A"); tradeData.put("FINISH_DATE",
	 * pubData.getString("SYS_TIME")); tradeData.put("CANCEL_TAG", "1");
	 * tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
	 * tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
	 * tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
	 * tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
	 * tradeData.put("CANCEL_EPARCHY_CODE",
	 * pubData.getString("TRADE_EPARCHY_CODE")); tradeData.put("DAY",
	 * tradeInfo.getString("TRADE_ID").substring(6, 8));// 为了bh_trade_staff表用 if
	 * (!Dao.insert("TF_BH_TRADE_STAFF", tradeData,
	 * Route.getJourDb(BizRoute.getTradeEparchyCode()))) { String msg =
	 * "搬迁员工订单【" + tradeInfo.getString("TRADE_ID") + "】到历史表失败!";
	 * CSAppException.apperr(TradeException.CRM_TRADE_95, msg); } }
	 */

	public void createCancelStaffTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception {
		String tradeId = pubData.getString("TRADE_ID");
		IDataset tradeStaffInfos = TradeStaffInfoQry.queryTradeStaffByTradeId(tradeId, "0");
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx250 " + tradeStaffInfos);

		if (IDataUtil.isEmpty(tradeStaffInfos)) {
			return;
		}
		
		//修改原单据的状态
		IData param = new DataMap();

		param.put("TRADE_ID", tradeId);
		param.put("DAY", tradeStaffInfos.getData(0).getString("DAY"));
		param.put("CANCEL_TAG", "1");// 被返销
		param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
		param.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		param.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		param.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		param.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx267 " + param);

		// 更新失败不报错
		Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_STAFF_CANCEL_TAG", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		
		// 新增一条cancel_tag=2的单据
		IData newData = tradeStaffInfos.getData(0);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx274 " + newData);

		if (newData.getLong("OPER_FEE", 0) > 0) {
			long lOperFee = -(long) pgData.getDouble("OPER_FEE", 0);
			newData.put("OPER_FEE", String.valueOf(lOperFee * 100));
		}
		
		if (newData.getLong("FOREGIFT", 0) > 0) {
			long lforegift = -(long) pgData.getDouble("FOREGIFT", 0);
			newData.put("FOREGIFT", String.valueOf(lforegift * 100));
		}
		
		if (newData.getLong("ADVANCE_PAY", 0) > 0) {
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
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx309 " + newData);

		Dao.insert("TF_BH_TRADE_STAFF", newData, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}
	
	private void createNewOrder(IData pubData, IData pgData, IData orderInfo) throws Exception {
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
		orderData.put("NEXT_DEAL_TAG", "0");
		orderData.put("OPER_FEE", "0");
		orderData.put("ADVANCE_PAY", "0");
		orderData.put("FOREGIFT", "0");
		orderData.put("FEE_STATE", "0");
		orderData.put("FEE_TIME", "");
		orderData.put("FEE_STAFF_ID", "");
		orderData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		orderData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		orderData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		orderData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		orderData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
		orderData.put("SUBSCRIBE_TYPE", "0");
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx333 " + orderData);

		if (!Dao.insert("TF_B_ORDER", orderData, Route.getJourDb(getTradeEparchyCode()))) {
			String msg = "生成返销订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
	}

	private void createNewTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception {
		IData tradeData = new DataMap();
		tradeData.putAll(tradeInfo);
		tradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
		tradeData.put("ACCEPT_MONTH", pubData.getString("NEW_ORDER_ID").substring(4, 6)); // 修改跨月撤单月份不一致的问题
		tradeData.put("ORDER_ID", pubData.getString("NEW_ORDER_ID"));
		tradeData.put("CANCEL_TAG", "3"); // 撤销
		tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME")); // 撤单时间
		tradeData.put("SUBSCRIBE_STATE", "0");
		tradeData.put("CANCEL_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
		tradeData.put("CANCEL_DEPART_ID", tradeInfo.getString("TRADE_DEPART_ID"));
		tradeData.put("CANCEL_CITY_CODE", tradeInfo.getString("TRADE_CITY_CODE"));
		tradeData.put("CANCEL_EPARCHY_CODE", tradeInfo.getString("TRADE_EPARCHY_CODE"));
		tradeData.put("OPER_FEE", "0");
		tradeData.put("ADVANCE_PAY", "0");
		tradeData.put("FOREGIFT", "0");
		tradeData.put("FEE_STATE", "0");
		tradeData.put("FEE_TIME", "");
		tradeData.put("FEE_STAFF_ID", "");
		tradeData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
		tradeData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
		tradeData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
		tradeData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
		tradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
		tradeData.put("REMARK", pgData.getString("CANCEL_REASON_ONE", "") + "|" + pgData.getString("CANCEL_REASON_TWO", "") + "|" + pgData.getString("REMARK", ""));
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx373 " + tradeData);

		if (!Dao.insert("TF_B_TRADE", tradeData, Route.getJourDb(getTradeEparchyCode()))) {
			String msg = "生成返销订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
	}

	private void dealOrderCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception {
		String orderId = tradeInfo.getString("ORDER_ID");
		IData orderInfo = UOrderInfoQry.qryOrderAllByOrderId(orderId);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx372 " + orderInfo);

		createHisOrder(pubData, pgData, orderInfo);

		String[] keys = new String[3];
		keys[0] = "ORDER_ID";
		keys[1] = "ACCEPT_MONTH";
		keys[2] = "CANCEL_TAG";

		boolean delFlag = Dao.delete("TF_B_ORDER", orderInfo, keys, Route.getJourDb(getTradeEparchyCode()));
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx384 " + delFlag);

		/*
		 * if (!delFlag) { //
		 * 融合业务，如果一笔为长流程，一笔为短流程，则短流程完工会搬迁ORDER到历史表中，撤单时，如果还删除ORDER表此处就会报错 //
		 * 查询历史订单 IData orderInfoHis =
		 * UOrderHisInfoQry.qryOrderHisByOrderId(orderId); if
		 * (IDataUtil.isEmpty(orderInfoHis)) { // 如果历史表也没有说明该订单不存在 String msg =
		 * "删除订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
		 * CSAppException.apperr(TradeException.CRM_TRADE_95, msg); } }
		 * 
		 * String orderKindCode = orderInfo.getString("ORDER_KIND_CODE", ""); if
		 * ("1".equals(orderKindCode)) { // 融合业务撤单时，单条撤单，作为单笔业务处理
		 * orderInfo.put("ORDER_KIND_CODE", ""); }
		 */
		createNewOrder(pubData, pgData, orderInfo);
	}

	private void dealTradeCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception {
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx401_pubData " + pubData);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx401_pgData " + pgData);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx401_tradeInfo " + tradeInfo);

		dealOrderCancel(pubData, pgData, tradeInfo); // 处理TF_B_ORDER
		createHisTrade(pubData, pgData, tradeInfo);
		createCancelStaffTrade(pubData, pgData, tradeInfo);
		
		String[] keys = new String[3];
		keys[0] = "TRADE_ID";
		keys[1] = "ACCEPT_MONTH";
		keys[2] = "CANCEL_TAG";

		if (!Dao.delete("TF_B_TRADE", tradeInfo, keys, Route.getJourDb(BizRoute.getTradeEparchyCode()))) {
			String msg = "删除订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
			CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
		}
		createNewTrade(pubData, pgData, tradeInfo);
	}

	private IData getPublicData(IData tradeInfo) throws Exception {
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

	public IData queryTradeBackFee(IData input) throws Exception {

		/*
		 * String tradeId = input.getString("TRADE_ID"); String acceptMonth =
		 * StrUtil.getAcceptMonthById(tradeId); IDataset tradeInfos =
		 * UTradeInfoQry.qryTradeByTradeIdMonth_1(tradeId, acceptMonth);
		 * System.out
		 * .println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxxxxx426 "+tradeInfos);
		 * 
		 * if (IDataUtil.isEmpty(tradeInfos)) {
		 * CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);//
		 * 在TF_B_TRADE未找到有效记录,TRADE_ID:%s } IData trade =
		 * tradeInfos.getData(0);// 当前界面选择的台账信息 double operFee =
		 * trade.getDouble("OPER_FEE", 0.0); double foregift =
		 * trade.getDouble("FOREGIFT", 0.0); double advancePay =
		 * trade.getDouble("ADVANCE_PAY", 0.0); double backFee = operFee +
		 * foregift + advancePay; if
		 * (trade.getString("TRADE_TYPE_CODE").equals("1100")) { String
		 * tradeEparchyCode = CSBizBean.getTradeEparchyCode(); IDataset tagInfos
		 * = TagInfoQry.getTagInfosByTagCode(tradeEparchyCode,
		 * "WIDENET_PREFEE_TAG", "CSM", "0");
		 * System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxxxxx440 "
		 * +tagInfos);
		 * 
		 * if (IDataUtil.isEmpty(tagInfos)) {
		 * CSAppException.apperr(ParamException.CRM_PARAM_502); } if
		 * (tagInfos.getData
		 * (0).getString("TAG_INFO").compareTo(trade.getString("ACCEPT_DATE"))
		 * <= 0) { advancePay = 0.0; backFee = operFee + foregift; } }
		 */
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxxxxx487 " + input);

		IData feeInfo = new DataMap();
		feeInfo.put("FIRSTFEE", 0);
		feeInfo.put("TWOFEE", 0);
		feeInfo.put("THREEFEE", 0);

		int backFee = 0;
		/*
		 * IDataset discntDs =
		 * TradeDiscntInfoQry.getTradeDiscntByTradeId(input.getString
		 * ("TRADE_ID", "").trim()); for (int j = 0; j < discntDs.size(); j++) {
		 * String discntCode = discntDs.getData(j).getString("DISCNT_CODE",
		 * "").trim();
		 * 
		 * // 从工单表查询用户的优惠台账 TF_B_TRADE_DISCNT IDataset commparaInfos9211 =
		 * CommparaInfoQry.getCommNetInfo("CSM", "9211", discntCode); if
		 * (commparaInfos9211 != null && commparaInfos9211.size() > 0) { backFee
		 * += commparaInfos9211.getData(0).getInt("PARA_CODE4", 0);
		 * 
		 * } }
		 */

		String serialNumber = input.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);// 取用户的总积分
		String userid = uca.getUserId();
		IDataset othersTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeId(input.getString("TRADE_ID"));
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx514 " + othersTradeInfos);

		// tf_f_user_other 表
		// RSRV_VALUE_CODE = ZNZW_ACCT_IN
		// RSRV_NUM1 = 费用
		// RSRV_STR1 = 开通时的trade_id，即tf_b_trade表的trade_id
		// RSRV_STR2 = 存临时账户的流水号OUTER_TRADE_ID
		// RSRV_STR3 = 退回的现金存折编码
		// RSRV_STR4 = 优惠编码（discntCode）
		// RSRV_STR5 = 优惠名称（discntName）
		// RSRV_STR6 = 第几类，即td_s_commpara表的9211配置的para_code1字段值

		for (int j = 0; j < othersTradeInfos.size(); j++) {
			IData otherinfo = othersTradeInfos.getData(j);
			String rsrv_str6 = otherinfo.getString("RSRV_STR6", "").trim();
			int rsrv_num1 = otherinfo.getInt("RSRV_NUM1", 0);

			if (rsrv_str6.equals("1")) {
				backFee += rsrv_num1;
				feeInfo.put("FIRSTFEE", rsrv_num1 / 100);
			}
			if (rsrv_str6.equals("2")) {
				backFee += rsrv_num1;
				feeInfo.put("TWOFEE", rsrv_num1 / 100);
			}
			if (rsrv_str6.equals("3")) {
				backFee += rsrv_num1;
				feeInfo.put("THREEFEE", rsrv_num1 / 100);
			}
		}

		/*
		 * feeInfo.put("backOperFee", 100 / 100); feeInfo.put("backForeGift",
		 * 100 / 100); feeInfo.put("backAdvancePay", 100 / 100);
		 */
		feeInfo.put("backFee", backFee / 100);
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxxxxx506 " + feeInfo);

		return feeInfo;
	}

	public IDataset queryTradeInfo(IData inData) throws Exception {
		String tradeId = inData.getString("TRADE_ID");

		IDataset tradeInfos = TradeInfoQry.queryTradeSet(tradeId, null);

		// 查询不到就从历史表里面查
		if (IDataUtil.isEmpty(tradeInfos)) {
			IData tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", getTradeEparchyCode());

			tradeInfos.add(tradeInfo);
		}

		return tradeInfos;
	}

	public IDataset queryUserCancelTrade(IData inData) throws Exception {
		String serialNumber = inData.getString("SERIAL_NUMBER");
		String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
		return SmartNetWorkTradeQuery.queryUserCancelTrade("KD_" + serialNumber, tradeTypeCode);
	}

	/*
	 * public IDataset queryNetTVUserCancelTrade(IData inData) throws Exception
	 * { String serialNumber = inData.getString("SERIAL_NUMBER"); String
	 * tradeTypeCode = inData.getString("TRADE_TYPE_CODE"); return
	 * SmartNetWorkTradeQuery.queryUserCancelTrade(serialNumber, tradeTypeCode);
	 * }
	 */

	/*
	 * public IDataset queryWidenet(IData inData) throws Exception { String
	 * tradeId = inData.getString("TRADE_ID"); return
	 * SmartNetWorkTradeQuery.queryWidenet(tradeId); }
	 */

	/*
	 * public IData queryAcctType(IData input) throws Exception {
	 * System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxxxx901 " +
	 * input); String isMasterAcct = ""; String roleCodeB = ""; String userId =
	 * ""; String serialNumber = input.getString("SERIAL_NUMBER"); IData
	 * userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber); if
	 * (IDataUtil.isNotEmpty(userInfo)) { userId =
	 * userInfo.getString("USER_ID"); } IDataset dataset =
	 * RelaUUInfoQry.isMasterAccount(userId, "77"); if
	 * (IDataUtil.isNotEmpty(dataset)) { roleCodeB =
	 * dataset.getData(0).getString("ROLE_CODE_B"); } if
	 * (StringUtils.isBlank(roleCodeB)) { dataset =
	 * RelaUUInfoQry.isMasterAccount(userId, "78"); if
	 * (IDataUtil.isNotEmpty(dataset)) { roleCodeB =
	 * dataset.getData(0).getString("ROLE_CODE_B"); } } if
	 * ("1".equals(roleCodeB)) { isMasterAcct = "主账号"; } else if
	 * ("2".equals(roleCodeB)) { isMasterAcct = "子账号"; } else { isMasterAcct =
	 * "普通账号"; } IData result = new DataMap(); result.put("isMasterAcct",
	 * isMasterAcct); return result; }
	 */

	/*
	 * public void saveUserRes(String tradeId) throws Exception { IDataset
	 * resTradeDatas = TradeResInfoQry.queryAllTradeResByTradeId(tradeId); if
	 * (IDataUtil.isNotEmpty(resTradeDatas)) { if (resTradeDatas.size() == 1) {
	 * IData resTradeData = resTradeDatas.getData(0); String modifyTag =
	 * resTradeData.getString("MODIFY_TAG"); // 如果是新增机顶盒 if
	 * (modifyTag.equals(BofConst.MODIFY_TAG_ADD)) {
	 * UserResInfoQry.saveUserRes(tradeId); } } } }
	 */

	/*
	 * public IDataset queryWidenetCancelTradeInfo(IData input) throws Exception
	 * { return TradeInfoQry.queryWidenetCancelTrade(input,
	 * this.getPagination());
	 * 
	 * }
	 */

	/**
	 * 短信通知客户已撤单
	 */
	public void smsNotifyCustomer(IData input) throws Exception {
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx558 " + input);

		String serialNumber = input.getString("SERIAL_NUMBER", "");
		String userId = input.getString("USER_ID", "");
		String strContent1 = "尊敬的客户，您好！您申请办理智能组网撤单业务系统已经受理。";

		if (serialNumber.startsWith("KD_")) {
			serialNumber = serialNumber.substring(3, serialNumber.length());
		}

		String forceObject1 = "10086235" + serialNumber;
		IData smsData = new DataMap();
		smsData.put("TRADE_ID", input.getString("TRADE_ID"));
		smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		smsData.put("SMS_PRIORITY", "5000");
		smsData.put("CANCEL_TAG", "3");
		smsData.put("REMARK", "智能组网撤单短信通知");
		smsData.put("NOTICE_CONTENT_TYPE", "0");
		smsData.put("SMS_TYPE_CODE", "I0");

		if (StringUtils.isNotBlank(strContent1)) {

			smsData.put("RECV_OBJECT", serialNumber);
			smsData.put("RECV_ID", userId);
			smsData.put("FORCE_OBJECT", forceObject1);
			smsData.put("NOTICE_CONTENT", strContent1);

			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx586 " + smsData);

			SmsSend.insSms(smsData);
		}
	}

	public IDataset queryUserCancelTradeMerge(IData inData) throws Exception {
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxxxx1034 " + inData);

		String serialNumber = inData.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);// 取用户的总积分
		String userid = uca.getUserId();

		// 支持前台输入KD_也能查询到
		if ("KD_".equals(serialNumber.substring(0, 3))) {
			serialNumber = serialNumber.substring(3);
		}

		String tradeTypeCode = inData.getString("TRADE_TYPE_CODE","870");
		IDataset cancelTradeInfos = new DatasetList();
		IDataset tradeInfos = SmartNetWorkTradeQuery.queryUserCancelTrade(serialNumber, tradeTypeCode);

		IDataset deparKindCodeTypeDataset = UDepartKindInfoQry.qryDeparKindByDepartId(getTradeEparchyCode(), CSBizBean.getVisit().getDepartId());

		// 如果没查询到则不过滤，正常不存在这种情况
		if (IDataUtil.isNotEmpty(deparKindCodeTypeDataset)) {
			String codeTypeCode = deparKindCodeTypeDataset.getData(0).getString("CODE_TYPE_CODE");

			// CODE_TYPE_CODE 0为代理商，代理商需要过滤非本厅的宽带订单
			if ("0".equals(codeTypeCode)) {
				String tradeDepartId = CSBizBean.getVisit().getDepartId();
				for (int i = 0; i < tradeInfos.size(); i++) {
					if (tradeDepartId.equals(tradeInfos.getData(i).getString("TRADE_DEPART_ID"))) {
						cancelTradeInfos.add(tradeInfos.getData(i));
					}
				}
			} else {
				cancelTradeInfos = tradeInfos;
			}
		} else {
			cancelTradeInfos = tradeInfos;
		}
		
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx669 " + cancelTradeInfos);
		
		for (int i = 0; i < cancelTradeInfos.size(); i++) {
			IData tradeData = cancelTradeInfos.getData(i);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx670 " + tradeData);
			
			/*String remark = tradeData.getString("REMARK", "").trim();
			if (remark.length() == 0) {
				continue;
			}
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx673 " + remark);

			String onereason = remark.split("\\|")[0];// WIDE_CANCEL_REASON
			String tworeason = remark.split("\\|")[1];// WIDE_CANCEL_REASON_RELATION
			String writerreason = remark.split("\\|")[2];// WIDE_CANCEL_REASON_RELATION
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx681onereason " + onereason);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx681tworeason " + tworeason);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx681writerreason " + writerreason);

			String onestr = StaticUtil.getStaticValue("WIDE_CANCEL_REASON", onereason);
			String twostr = StaticUtil.getStaticValue("WIDE_CANCEL_REASON_RELATION", tworeason);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx687 " + onestr);
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx688 " + twostr);
			*/
			IDataset othersTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeId(tradeData.getString("TRADE_ID"));
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx683 " + othersTradeInfos);

			// tf_f_user_other 表
			// RSRV_VALUE_CODE = ZNZW_ACCT_IN
			// RSRV_NUM1 = 费用
			// RSRV_STR1 = 开通时的trade_id，即tf_b_trade表的trade_id
			// RSRV_STR2 = 存临时账户的流水号OUTER_TRADE_ID
			// RSRV_STR3 = 退回的现金存折编码
			// RSRV_STR4 = 优惠编码（discntCode）
			// RSRV_STR5 = 优惠名称（discntName）
			// RSRV_STR6 = 第几类，即td_s_commpara表的9211配置的para_code1字段值
			tradeData.put("FIRSTFEE", 0);
			tradeData.put("TWOFEE", 0);
			tradeData.put("THREEFEE", 0);
			
			for (int j = 0; j < othersTradeInfos.size(); j++) {
				IData otherinfo = othersTradeInfos.getData(j);
				String rsrv_str6 = otherinfo.getString("RSRV_STR6", "").trim();
				int rsrv_num1 = otherinfo.getInt("RSRV_NUM1", 0);
								
				if (rsrv_str6.equals("1")) {
					tradeData.put("FIRSTFEE", rsrv_num1 / 100);
				}
				if (rsrv_str6.equals("2")) {
					tradeData.put("TWOFEE", rsrv_num1 / 100);
				}
				if (rsrv_str6.equals("3")) {
					tradeData.put("THREEFEE", rsrv_num1 / 100);
				}
			}

			tradeData.put("BACKFEE", tradeData.getInt("FIRSTFEE")+tradeData.getInt("TWOFEE")+tradeData.getInt("THREEFEE"));
			if(tradeData.getString("FEE_STATE","").trim().equals("0")){
				tradeData.put("FEE_STATE", "未收费");
			}else if(tradeData.getString("FEE_STATE","").trim().equals("1")){
				tradeData.put("FEE_STATE", "已收费");
			}
			
			
/*			String changeremark = "";
			if (onestr != null) {
				changeremark += onestr + "|";
			}
			if (twostr != null) {
				changeremark += twostr + "|";
			}
			changeremark += writerreason;
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx715 " + changeremark);

			tradeData.put("REMARK", changeremark);*/
			
			System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx719 " + tradeData);
		}
		System.out.println("CancelSmartNetworkRegSVCxxxxxxxxxxxxxx723 " + cancelTradeInfos);

		return cancelTradeInfos;
	}

	public IDataset queryStaffB(IData inData) throws Exception {
		return SmartNetWorkTradeQuery.queryStaffB(inData, this.getPagination());
	}

	/*
	 * public IDataset queryStaffInfo(IData inData) throws Exception { return
	 * SmartNetWorkTradeQuery.queryStaffInfo(inData); }
	 */
	/*
	 * public IDataset deleteStaffB(IData inData) throws Exception { boolean
	 * flag = false; String[] keys = { "STAFF_ID" }; Dao.delete("TD_M_STAFF_B",
	 * inData, keys, Route.CONN_CRM_CEN); flag = true; IDataset result = new
	 * DatasetList(); IData data = new DataMap(); data.put("SUCCESS", flag);
	 * result.add(data); return result; }
	 */
	/*
	 * public IDataset saveStaffB(IData inparams) throws Exception { boolean
	 * flag = false; // 新增数据 if (inparams.getBoolean("createFlag")) { flag =
	 * Dao.insert("TD_M_STAFF_B", inparams, Route.CONN_CRM_CEN); } else {
	 * Dao.executeUpdateByCodeCode("TD_M_STAFF", "UPD_BSTAFFINFO", inparams,
	 * Route.CONN_CRM_CEN); } flag = true; IDataset result = new DatasetList();
	 * IData data = new DataMap(); data.put("SUCCESS", flag); result.add(data);
	 * return result; }
	 */

	/*
	 * public IDataset getCancelReasonTwo(IData data) throws Exception { String
	 * reportType = data.getString("CANCEL_REASON_ONE"); return
	 * StaticUtil.getStaticListByParent("WIDE_CANCEL_REASON_RELATION",
	 * reportType); }
	 */

	/**
	 * 提供给外部的撤单接口
	 */
	public IData cancelTradeRegIntf(IData input) throws Exception {
		// 流水号
		IDataUtil.chkParam(input, "TRADE_ID");
		// 服务号码
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		//
		// IDataUtil.chkParam(input, "CITY_CODE");
		// 取消原因
		// IDataUtil.chkParam(input, "CANCEL_REASON");		
		IDataUtil.chkParam(input, "CANCEL_REASON_ONE");
		IDataUtil.chkParam(input, "CANCEL_REASON_TWO");
		
		IData data = new DataMap();
		data.put("X_RESULTCODE", "2999");
		data.put("X_RESULTINFO", "业务受理异常");
		IDataset ds = cancelTradeReg(input);
		if (ds != null && ds.size() > 0) {
			if (ds.getData(0).getString("NEW_ORDER_ID") != null) {
				data.put("X_RESULTCODE", "0");
				data.put("X_RESULTINFO", "业务受理成功");
			}
		}
		return data;
	}

	/*
	 * private void chkTradeBeforeCancel(IData pubData, IData tradeInfo) throws
	 * Exception { IData inRuleParam = new DataMap();
	 * inRuleParam.put("TRADE_TYPE_CODE",
	 * tradeInfo.getString("TRADE_TYPE_CODE")); inRuleParam.put("PROVINCE_CODE",
	 * CSBizBean.getVisit().getProvinceCode());
	 * inRuleParam.put("TRADE_EPARCHY_CODE",
	 * pubData.getString("LOGIN_EPARCHY_CODE"));
	 * inRuleParam.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
	 * inRuleParam.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
	 * inRuleParam.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
	 * inRuleParam.put("TRADE_ID", pubData.getString("TRADE_ID"));
	 * inRuleParam.put("RULE_BIZ_TYPE_CODE", "TradeCheckBeforeWidenetCancel");
	 * inRuleParam.put("RULE_BIZ_KIND_CODE", "TradeCheckBeforeWidenetCancel");
	 * inRuleParam.put("ACTION_TYPE", "TradeCheckBeforeWidenetCancel");
	 * inRuleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
	 * inRuleParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	 * inRuleParam.put("UNDO_TIME", pubData.getString("SYS_TIME"));// 返销时间
	 * inRuleParam.put("TRADE_INFO", tradeInfo);// 将trade信息传入 IData data =
	 * BizRule.bre4SuperLimit(inRuleParam); CSAppException.breerr(data);
	 * 
	 * }
	 */

	/*
	 * private void topsetboxReleaseTempOccupy(String tradeId, String
	 * serialNumber) throws Exception { IDataset checkDatas =
	 * TradeResInfoQry.getTradeRes(tradeId, "4", BofConst.MODIFY_TAG_ADD); if
	 * (IDataUtil.isNotEmpty(checkDatas)) { IData checkData =
	 * checkDatas.getData(0);
	 * 
	 * String imei = checkData.getString("IMSI");
	 * 
	 * IData data = new DataMap(); data.put("RES_NO", imei);
	 * data.put("SERIAL_NUMBER", serialNumber); IDataset retDataset =
	 * HwTerminalCall.releaseResTempOccupy(data);
	 * 
	 * if (!(IDataUtil.isNotEmpty(retDataset) &&
	 * StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))) {
	 * String resultInfo = retDataset.first().getString("X_RESULTINFO",
	 * "华为释放预占终端接口调用异常！"); CSAppException.apperr(CrmCommException.CRM_COMM_103,
	 * resultInfo); // 接口返回异常 }
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * private void dealImsTradeCancel(IData pubData, IData pgData, String
	 * tradeId) throws Exception { String acceptMonth =
	 * StrUtil.getAcceptMonthById(tradeId); IDataset imstradeInfos =
	 * UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");
	 * 
	 * if (IDataUtil.isNotEmpty(imstradeInfos)) { IData imsTradeInfo =
	 * imstradeInfos.getData(0);
	 * 
	 * IData imsPubData = this.getPublicData(imsTradeInfo);
	 * 
	 * imsPubData.put("NEW_ORDER_ID", pubData.getString("NEW_ORDER_ID"));
	 * imsPubData.put("SYS_TIME", pubData.getString("SYS_TIME"));
	 * 
	 * createHisTrade(imsPubData, imsTradeInfo, imsTradeInfo);
	 * createCancelStaffTrade(imsPubData, imsTradeInfo, imsTradeInfo);
	 * 
	 * String[] keys = new String[3]; keys[0] = "TRADE_ID"; keys[1] =
	 * "ACCEPT_MONTH"; keys[2] = "CANCEL_TAG";
	 * 
	 * if (!Dao.delete("TF_B_TRADE", imsTradeInfo, keys,
	 * Route.getJourDb(BizRoute.getTradeEparchyCode()))) { String msg =
	 * "删除IMS固话订单【" + imsTradeInfo.getString("TRADE_ID") + "】失败!";
	 * CSAppException.apperr(TradeException.CRM_TRADE_95, msg); }
	 * createNewTrade(imsPubData, pgData, imsTradeInfo); }
	 * 
	 * }
	 */

}
