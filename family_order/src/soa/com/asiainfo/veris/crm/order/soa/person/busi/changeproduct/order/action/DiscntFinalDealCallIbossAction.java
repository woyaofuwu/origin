package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class DiscntFinalDealCallIbossAction implements ITradeFinishAction {

	 private static transient final Logger log =Logger.getLogger(DiscntFinalDealCallIbossAction.class);
	public void executeAction(IData mainTrade) throws Exception {

		IDataset discntTradeList = TradeDiscntInfoQry
				.getTradeDiscntByTradeId(mainTrade.getString("TRADE_ID"));

		String userid = mainTrade.getString("USER_ID");
		String imsi = "";
		String EffTIMSI = "";
		IDataset userBResInfos = UserResInfoQry.queryUserResByUserIdResType(
				userid, "1");// 查sim卡
		if (IDataUtil.isNotEmpty(userBResInfos)) {// 虚拟用户是没有该资料的
			imsi = userBResInfos.getData(0).getString("IMSI", "0");
			EffTIMSI = userBResInfos.getData(0).getString("START_DATE", "0");
		}
		IData adddata = new DataMap();
		IData deldata = new DataMap();
		if (IDataUtil.isNotEmpty(discntTradeList)) {

			for (int i = 0; i < discntTradeList.size(); i++) {
				IData distrade = discntTradeList.getData(i);
				String discntcode = distrade.getString("DISCNT_CODE");

				IDataset discntSet = CommparaInfoQry.getCommpara("CSM", "1807",
						discntcode, "ZZZZ");
				if (IDataUtil.isNotEmpty(discntSet)) {
					String modifyTag = distrade.getString("MODIFY_TAG");
					
					if("U".equals(modifyTag))
					{
						continue;
					}
					
					String serialNumber = mainTrade.getString("SERIAL_NUMBER");
					if ("2".equals(mainTrade.getString("CANCEL_TAG"))) {
						if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {

							deldata.put("REMOVE_TAG", "3");// 返销操作
							deldata.put("SERIAL_NUMBER", serialNumber);
							deldata.put("INST_ID", distrade
									.getString("INST_ID"));
							deldata.put("PROD_ID", discntSet.getData(0)
									.getString("PARA_CODE1"));
							deldata.put("FEE", discntSet.getData(0).getString(
									"PARA_CODE2"));
							deldata.put("FEEACCESSMOD", mainTrade
									.getString("IN_MODE_CODE"));
							deldata.put("START_DATE", distrade
									.getString("START_DATE"));
							deldata.put("END_DATE", distrade
									.getString("START_DATE"));
							deldata.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
							deldata.put("IMSI", imsi);
							deldata.put("EFFT_IMSI", EffTIMSI);
						} else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag)) {

							adddata.put("SERIAL_NUMBER", serialNumber);
							adddata.put("INST_ID", distrade
									.getString("INST_ID"));
							adddata.put("PROD_ID", discntSet.getData(0)
									.getString("PARA_CODE1"));
							adddata.put("FEE", discntSet.getData(0).getString(
									"PARA_CODE2"));
							adddata.put("FEEACCESSMOD", mainTrade
									.getString("IN_MODE_CODE"));

							adddata.put("START_DATE", distrade
									.getString("START_DATE"));
							adddata
									.put("END_DATE",
											SysDateMgr.END_DATE_FOREVER);
							adddata.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
							adddata.put("IMSI", imsi);
							adddata.put("EFFT_IMSI", EffTIMSI);

						}

					} else if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {
						adddata.put("SERIAL_NUMBER", serialNumber);
						adddata.put("INST_ID", distrade.getString("INST_ID"));
						adddata.put("PROD_ID", discntSet.getData(0).getString(
								"PARA_CODE1"));
						adddata.put("FEE", discntSet.getData(0).getString(
								"PARA_CODE2"));
						adddata.put("FEEACCESSMOD", mainTrade
								.getString("IN_MODE_CODE"));

						adddata.put("START_DATE", distrade
								.getString("START_DATE"));
						adddata.put("END_DATE", distrade.getString("END_DATE"));
						adddata.put("MODIFY_TAG", modifyTag);
						adddata.put("IMSI", imsi);
						adddata.put("EFFT_IMSI", EffTIMSI);

					} else {
						if (IDataUtil.isNotEmpty(discntTradeList)) {
							String REMOVE_TAG = discntTradeList.getData(0)
									.getString("REMOVE_TAG", "");
							String MODIFY_TAG = discntTradeList.getData(0)
									.getString("MODIFY_TAG", "");
							if (!"0".equals(REMOVE_TAG)
									&& "1".equals(MODIFY_TAG)) {
								deldata.put("REMOVE_TAG", "2");// 销户操作
								IDataset resTradeList = TradeResInfoQry
								.getTradeRes(mainTrade.getString("TRADE_ID"),"1","1");	
								if(IDataUtil.isNotEmpty(resTradeList)){
									imsi=resTradeList.getData(0).getString("IMSI");
									EffTIMSI=resTradeList.getData(0).getString("START_DATE");
								}
							}
						}
						deldata.put("SERIAL_NUMBER", serialNumber);
						deldata.put("INST_ID", distrade.getString("INST_ID"));
						deldata.put("PROD_ID", discntSet.getData(0).getString(
								"PARA_CODE1"));
						deldata.put("FEE", discntSet.getData(0).getString(
								"PARA_CODE2"));
						deldata.put("FEEACCESSMOD", mainTrade
								.getString("IN_MODE_CODE"));
						deldata.put("START_DATE", distrade
								.getString("START_DATE"));
						deldata.put("END_DATE", distrade.getString("END_DATE"));
						deldata.put("MODIFY_TAG", modifyTag);
						deldata.put("IMSI", imsi);
						deldata.put("EFFT_IMSI", EffTIMSI);
					}

				}
			}
		}

		// 要求必须先发删除再发新增的
		if (IDataUtil.isNotEmpty(deldata)) {
			dealDiscntCallIboss(deldata);
		}
		if (IDataUtil.isNotEmpty(adddata)) {
			dealDiscntCallIboss(adddata);
		}
		
	}

	public void dealDiscntCallIboss(IData comm) throws Exception {

		String serialNumber = comm.getString("SERIAL_NUMBER");
		String modifyTag = comm.getString("MODIFY_TAG");
		IData iboss = new DataMap();
		iboss.put("MSISDN", serialNumber);
		if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {
			iboss.put("OPR_CODE", "01");
			iboss.put("ACTION_ID", "01");
			iboss.put("ORDER_RELATION_STAT", "01");
		} else {
			iboss.put("OPR_CODE", "02");
			iboss.put("ACTION_ID", "02");
			iboss.put("ORDER_RELATION_STAT", "02");
		}
		iboss.put("USER_TYPE", "00");
		iboss.put("PROV_CODE", "898");
		iboss.put("PROD_INSTID", comm.getString("INST_ID"));
		iboss.put("PROD_TYPE", "01");
		iboss.put("PROD_ID", comm.getString("PROD_ID"));
		iboss.put("EFFTIMSI", SysDateMgr.decodeTimestamp(comm
				.getString("START_DATE"), "yyyyMMddHHmmss"));
		iboss.put("EXPIRETIMSI", SysDateMgr.decodeTimestamp(comm
				.getString("END_DATE"), "yyyyMMddHHmmss"));
		iboss.put("IMSI", comm.getString("IMSI"));
		iboss.put("EFFT_IMSI", SysDateMgr.decodeTimestamp(comm
				.getString("EFFT_IMSI"), "yyyyMMddHHmmss"));
		iboss.put("UPDATETIMSI", SysDateMgr.decodeTimestamp(SysDateMgr
				.getSysTime(), "yyyyMMddHHmmss"));
		iboss.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		iboss.put("SERIAL_NUMBER", serialNumber);

		iboss.put("OPRTIMSI", SysDateMgr.decodeTimestamp(SysDateMgr
				.getSysTime(), "yyyyMMddHHmmss"));

		iboss.put("KIND_ID", "BIP3A319_T3000322_0_0");
		iboss.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		IDataset result = IBossCall.dealInvokeUrl("BIP3A319_T3000322_0_0",
				"IBOSS6", iboss);
		if (IDataUtil.isNotEmpty(result)) {
			if (!result.getData(0).getString("X_RSPCODE").equals("0000")) {
				/*CSAppException.apperr(IBossException.CRM_IBOSS_4, result
						.getData(0).getString("X_RSPCODE"), result.getData(0)
						.getString("X_RSPDESC"));*/

			} else {
				String startDate = comm.getString("START_DATE");
				if ("0".equals(modifyTag)) {
					iboss.put("PRODSTAT", "00");
				} else {
					iboss.put("PRODSTAT", "04");
				}
				if (SysDateMgr.compareTo(startDate, SysDateMgr.getSysTime()) > 0
						&& "0".equals(modifyTag)) {
					iboss.put("PRODSTAT", "00");
				} else if (SysDateMgr.compareTo(startDate, SysDateMgr
						.getSysTime()) <= 0
						&& "0".equals(modifyTag))
					iboss.put("PRODSTAT", "02");
			}
			if ("2".equals(comm.getString("REMOVE_TAG", ""))) {
				iboss.put("PRODSTAT", "05");
			}
			iboss.put("FEE", comm.getString("FEE", ""));
			iboss.put("FEEACCESSMOD", comm.getString("FEEACCESSMOD", ""));
			insertIrcnproorder(iboss);
		}

	}

	public static void insertIrcnproorder(IData data) throws Exception {
		IData ircndata = new DataMap();
		ircndata.put("PKGSEQ", SeqMgr.getTradeId());// 发起方的操作流水号
		ircndata.put("PRODUCTORDERRESULT", "00"); // 产品订购结果
		ircndata.put("PRODUCTORDERRESULTDESC", "订购成功");// 产品订购结果描述
		ircndata.put("MSISDN", data.getString("MSISDN", ""));// 手机号码
		ircndata.put("USERTYPE", data.getString("USER_TYPE", ""));// 用户类型
		ircndata.put("GROUPNAME", data.getString("GROUPNAME", ""));// 集团名称
		ircndata.put("PROVCODE", data.getString("PROV_CODE", ""));// 省代码

		ircndata.put("UPDATETIMSI", data.getString("UPDATETIMSI", ""));// 用户订购关系更新的时间戳
		ircndata.put("PRODINSTID", data.getString("PROD_INSTID", "")); // 产品订购流水号
		ircndata.put("PRODTYPE", data.getString("PROD_TYPE", "")); // 产品类型
		ircndata.put("PRODID", data.getString("PROD_ID", ""));// 产品ID
		ircndata.put("PRODSTAT", data.getString("PRODSTAT", "")); // 产品状态
		ircndata.put("EFFTIMSI", data.getString("EFFTIMSI", ""));// 订购生效时间
		ircndata.put("EXPIRETIMSI", data.getString("EXPIRETIMSI", ""));// 订购过期时间
		ircndata.put("FIRSTTIMSI", data.getString("EFFTIMSI", ""));// 产品激活时间
		ircndata.put("ENDTIMSI", data.getString("EXPIRETIMSI", ""));// 产品失效时间
		ircndata.put("ORDERRELATIONSTAT", data.getString("", ""));// 订购关系状态

		ircndata.put("FEETYPE", "01");// 计费类型
		ircndata.put("FEECYCLE", "3"); // 计费周期
		ircndata.put("FEE", data.getString("FEE", "")); // 计费金额

		ircndata.put("FEEACCESSMOD", data.getString("FEEACCESSMOD", ""));// 订购渠道
		ircndata.put("INSERTTIMER", SysDateMgr.getSysTime()); // 入表时间
		ircndata.put("UPDATETIMER", SysDateMgr.getSysTime()); // 计费金额
		ircndata.put("X_RESULT_CODE", "00");// 返回结果
		ircndata.put("STATE", "1"); // 状态 0-初始状态 1-处理成功 2-处理失败

		Dao.insert("TI_B_IRCNPROORDER", ircndata, Route.CONN_CRM_CEN);
	}
	


}
