
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.sync;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeePayMoneyInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleDepositInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

import java.math.BigDecimal;

public final class TradeSyncFeeSub
{

	/**
	 * 插费用同步表
	 * 
	 * @param feeDataset
	 * @param tradeId
	 * @param syncId
	 * @throws Exception
	 */
	private static IDataset getTradeFeeSub(IData tradeMain) throws Exception
	{
		// 获取主台帐

		String userId = tradeMain.getString("USER_ID");
		String acctId = tradeMain.getString("ACCT_ID", "");

		String syncId = tradeMain.getString("SYNC_SEQUENCE");		
		String tradeId = tradeMain.getString("TRADE_ID");		
		String tradeTypeCode = tradeMain.getString("TRADE_TYPE_CODE");
		String inModeCode = tradeMain.getString("IN_MODE_CODE");
		/*
		 * 帐务约定： payment_op 储值操作类型 所有得缴费 传 16000 含义 储值 所有得退款 传 16001 含义 清退 所有得返销 传 16003 含义 返销 16004 转出 16005 转入 16002
		 * 调减 channel_id 渠道标识 所有都传 15000 含义 营业厅营业 PAYMENT_ID 储值方式 （可配置）
		 */
		int paymentOp = 16000;

		IDataset syncRecvDataset = new DatasetList();

		// 费用同步公用数据准备 - start
		IData syncRecvCommData = new DataMap();
		syncRecvCommData.put("SYNC_SEQUENCE", syncId);
		syncRecvCommData.put("OUTER_TRADE_ID", tradeId);		
		syncRecvCommData.put("BATCH_ID", tradeId);		
		syncRecvCommData.put("SERIAL_NUMBER", tradeMain.getString("SERIAL_NUMBER"));
		syncRecvCommData.put("NET_TYPE_CODE", tradeMain.getString("NET_TYPE_CODE", "00"));
		syncRecvCommData.put("RECV_TIME", tradeMain.getString("ACCEPT_DATE"));
		syncRecvCommData.put("USER_ID2", tradeMain.getString("USER_ID_B"));
		syncRecvCommData.put("ACCT_ID2", tradeMain.getString("ACCT_ID_B"));
		syncRecvCommData.put("RECV_EPARCHY_CODE", tradeMain.getString("TRADE_EPARCHY_CODE"));
		syncRecvCommData.put("RECV_CITY_CODE", tradeMain.getString("TRADE_CITY_CODE"));
		syncRecvCommData.put("RECV_STAFF_ID", tradeMain.getString("TRADE_STAFF_ID"));
		syncRecvCommData.put("RECV_DEPART_ID", tradeMain.getString("TRADE_DEPART_ID"));
		syncRecvCommData.put("CHANNEL_ID", "15000");
		syncRecvCommData.put("WRITEOFF_MODE", "1");
		syncRecvCommData.put("PRIORITY", "0");
		syncRecvCommData.put("PAYMENT_REASON_CODE", "0");
		syncRecvCommData.put("CANCEL_TAG", "0");
		syncRecvCommData.put("DEAL_TAG", "0");
		syncRecvCommData.put("RESULT_CODE", "0");
		syncRecvCommData.put("MONTHS", "240");
		syncRecvCommData.put("AMONTH", "0");
		syncRecvCommData.put("LIMIT_MONEY", "-1");
		syncRecvCommData.put("MODIFY_TAG", "0");
		syncRecvCommData.put("VALID_TAG", "0");
		syncRecvCommData.put("ACTION_CODE", "-1"); // 默认，后续修改
		syncRecvCommData.put("START_CYCLE_ID", "190001"); // 原账务逻辑写死
		syncRecvCommData.put("END_CYCLE_ID", "205001"); // 原账务逻辑写死

		// 以下数据先默认，后续修改
		syncRecvCommData.put("RSRV_DATE1", SysDateMgr.getSysTime());
		syncRecvCommData.put("RSRV_FEE1", "");
		syncRecvCommData.put("RSRV_FEE2", "");
		syncRecvCommData.put("RSRV_INFO1", "");
		syncRecvCommData.put("RSRV_INFO2", "");
		syncRecvCommData.put("RSRV_INFO3", "");
		syncRecvCommData.put("RSRV_INFO4", "");
		syncRecvCommData.put("RSRV_INFO5", "");
		syncRecvCommData.put("RSRV_NUM1", "");
		syncRecvCommData.put("RSRV_NUM2", "");
		syncRecvCommData.put("RSRV_NUM3", "");
		syncRecvCommData.put("RSRV_NUM4", "");
		syncRecvCommData.put("RSRV_NUM5", "");
		syncRecvCommData.put("RSRV_NUM6", "");
		syncRecvCommData.put("RSRV_NUM7", "");
		syncRecvCommData.put("RSRV_NUM8", "");
		syncRecvCommData.put("RSRV_NUM9", "");
		syncRecvCommData.put("RSRV_NUM10", "");
		// syncRecvCommData.put("PAY_FEE_MODE_CODE", "0");
		// 费用同步公用数据准备 - end

		// 财务化BOSS要求传付费方式给账务：要求传Td_Sd_Payfeemode对应的账务的付费方式给账务(Td_Sd_Payfeemode的数据由统计组提供)
		String payFeeModeCode = null;
		String orderId = tradeMain.getString("ORDER_ID");
		if (StringUtils.isNotBlank(orderId))
		{
			IDataset payMoneys = TradeFeePayMoneyInfoQry.getPayMoneyInfoByOrderId(orderId, tradeMain.getString("ACCEPT_MONTH"));
			if (IDataUtil.isNotEmpty(payMoneys))
			{
				IData payMoney = payMoneys.getData(0);
				String payMoneyCode = payMoney.getString("PAY_MONEY_CODE");
				if ("L".equals(payMoneyCode))
				{
					// 货到付款方式的不需要同步帐务
					return new DatasetList();
				}
				payFeeModeCode = TradeFeePayMoneyInfoQry.getPayFeeModeCode(payMoneyCode);
			}
		}

		IDataset tradefeeSubs = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, "2");
		if (IDataUtil.isNotEmpty(tradefeeSubs))
		{
			String userid = "";
			// 费用同步数据准备 - start
			for (Object obj : tradefeeSubs)
			{
				IData tradefeeSub = (IData) obj;
				IData syncRecvData = new DataMap();
				syncRecvData.putAll(syncRecvCommData);
				if (StringUtils.isNotBlank(payFeeModeCode))
				{
					syncRecvData.put("PAY_FEE_MODE_CODE", payFeeModeCode);
				}
				else
				{
					syncRecvData.put("PAY_FEE_MODE_CODE", tradefeeSub.getString("PAY_FEE_MODE_CODE"));
				}
				userid = tradefeeSub.getString("USER_ID");
				String fee = tradefeeSub.getString("FEE");

				// 费用小于0的清退不走TI_A_SYN_RECV表，在提交时通过BackFeeAction调用实时接口处理
				if (Integer.parseInt(fee) < 0)
				{
					continue;
				}

				if ("14".equals(tradeTypeCode) || "700".equals(tradeTypeCode))
				{
					syncRecvData.put("TRADE_TYPE_CODE", "7051");// 为了防止买断开户时缴费积压，特修改业务类型，帐务单独处理
				}
				else
				{
					syncRecvData.put("TRADE_TYPE_CODE", "7040"); // 营业缴费
				}

				// 存在同一笔tradeid里有多个用户缴费，如：赠款赠送他人
				syncRecvData.put("USER_ID", userid);

				// 取当前用户的默认付费账户
				IData payRelation = UcaInfoQry.qryDefaultPayRelaByUserId(userid);
				if (null != payRelation)
				{
					syncRecvData.put("ACCT_ID", payRelation.getString("ACCT_ID"));
				}
				else
				{
					CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_113, userid);
				}

				syncRecvData.put("ACT_TAG", "4");

				syncRecvData.put("PAYMENT_ID", tradefeeSub.getString("FEE_TYPE_CODE"));

				syncRecvData.put("PAYMENT_OP", paymentOp);

				syncRecvData.put("RECV_FEE", fee);

				if ("2".equals(inModeCode))
				{
					syncRecvData.put("CHANNEL_ID", "15001");
				}
				else if ("3".equals(inModeCode))
				{
					syncRecvData.put("CHANNEL_ID", "15004");
				}

				String chargeId = SeqMgr.getChargeId();
				
				/*
				 * REQ201611160018 营业缴费时由信息管理插入营业传来的TRADE_ID和账务自己生成流水重复
				 * 将字段trade_id的前两位数替换为70
				 * zhangxing3
				 */
				//syncRecvData.put("TRADE_ID", chargeId);
				syncRecvData.put("TRADE_ID", "70"+chargeId.substring(2));

				// 账务优惠ACTION_CODE - start
				int discntGiftId = tradefeeSub.getInt("DISCNT_GIFT_ID", 0);
				if (discntGiftId > 0)
				{
					IDataset saleDeposits = TradeSaleDepositInfoQry.getSaleDepositByTradeGiftMtag(tradeId, String.valueOf(discntGiftId), "0");
					if (IDataUtil.isNotEmpty(saleDeposits))
					{
						IData saleDeposit = saleDeposits.getData(0);

						syncRecvData.put("ACTION_CODE", saleDeposit.getString("A_DISCNT_CODE"));

						// 营销活动和合约计划处理开始时间及灵活预存
						if (tradeMain.getString("INTF_ID", "").indexOf("TF_B_TRADE_SALE_ACTIVE") >= 0 || tradeMain.getString("INTF_ID", "").indexOf("TF_B_TRADE_SALEACTIVE_BOOK") >= 0)
						{
							IDataset tradeSaleActives = TradeSaleActive.getTradeSaleActive(tradeId, userid, "0");
							if (IDataUtil.isNotEmpty(tradeSaleActives))
							{
								IData tradeSaleActive = tradeSaleActives.getData(0);
								syncRecvData.put("START_DATE", tradeSaleActive.getString("START_DATE"));
							}
							else
							{
								IDataset tradeSaleActiveBooks = TradeSaleActive.getTradeSaleActiveBook(tradeId, userid, "0");
								if (IDataUtil.isNotEmpty(tradeSaleActiveBooks))
								{
									syncRecvData.put("START_DATE", tradeSaleActiveBooks.getData(0).getString("START_DATE"));
								}
							}

							IDataset tradeAttrs = TradeAttrInfoQry.getTradeAttrByTradeId(tradeId);
							if (IDataUtil.isNotEmpty(tradeAttrs))
							{
								for (Object obj2 : tradeAttrs)
								{
									IData tradeAttr = (IData) obj2;
									String attrCode = tradeAttr.getString("ATTR_CODE");
									String attrValue = tradeAttr.getString("ATTR_VALUE");

									// 本金(预存总额)
									if ("ITEM_FEE".equals(attrCode))
									{
										syncRecvData.put("RECV_FEE", attrValue);
									}
									// 本月返还额度
									else if ("ITEM_MRTN_FEE".equals(attrCode))
									{
										syncRecvData.put("RSRV_NUM2", attrValue);
									}
									// 本金生效月份数
									else if ("ITEM_END_OFFSET".equals(attrCode))
									{
										syncRecvData.put("RSRV_NUM1", attrValue);
									}
									// 开始时间
									else if ("ITEM_START_DATE".equals(attrCode))
									{
										syncRecvData.put("START_DATE", attrValue);
									}
									// 赠送金额
									else if ("ITEM_GIFT_FEE".equals(attrCode))
									{
										syncRecvData.put("RSRV_FEE2", attrValue);
									}
									// 赠送生效月份数
									else if ("ITEM_GIFT_MONTHS".equals(attrCode))
									{
										syncRecvData.put("RSRV_NUM3", attrValue);
									}
									// 赠送每月返还额度
									else if ("ITEM_MGFT_FEE".equals(attrCode))
									{
										syncRecvData.put("RSRV_NUM4", attrValue);
									}
									// 预留字段
									else if ("ITEM_FOREGIFT_FEE".equals(attrCode))
									{
									}
								}
							}
						}
						// 合约计划灵活预存 - end
					}
					// 终端活动通过RSRV_INFO1传给帐务IMEI
					IDataset tradeSaleGoods = TradeSaleGoodsInfoQry.getTradeSaleGoodsByResTypeCode(tradeId);
					if (IDataUtil.isNotEmpty(tradeSaleGoods))
					{
						IData tradeSaleGood = tradeSaleGoods.getData(0);
						syncRecvData.put("RSRV_INFO1", tradeSaleGood.getString("RES_CODE"));
					}
					
					//REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
					String rsrvStr1 = tradeMain.getString("RSRV_STR1","");//活动产品ID
					//为了尽可能减少影响，先判断RSRV_STR1是否有值，有值才处理
					if(StringUtils.isNotBlank(rsrvStr1))
					{
						//判断是否属于531的特殊活动
						IDataset productInfos = CommparaInfoQry.getCommpara("CSM", "531", rsrvStr1, "0898");
						//为了尽可能减少影响，如果属于该活动，才继续处理
						if (IDataUtil.isNotEmpty(productInfos))
						{
							IDataset saleGoodsTradeInfo = TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(tradeId);
							if (IDataUtil.isNotEmpty(saleGoodsTradeInfo))
							{
								IData saleGoodsTrade = saleGoodsTradeInfo.getData(0);
								String resCode = saleGoodsTrade.getString("RES_CODE","");
								if(StringUtils.isNotBlank(resCode))
								{
									syncRecvData.put("RSRV_INFO1", resCode);
								}
							}
						}
					}
					//end
					
				}
				// 账务优惠ACTION_CODE - end
				syncRecvData.put("SYNC_DAY", syncId.substring(6, 8));

				syncRecvDataset.add(syncRecvData);
			}
			// 费用同步数据准备 - end
		}

		return syncRecvDataset;
	}

	/**
	 * 插费用同步表
	 * 
	 * @param syncRecvDataset
	 * @throws Exception
	 */
	public static void insSyncRecv(IDataset syncRecvDataset) throws Exception
	{
		Dao.insert("TI_A_SYNC_RECV", syncRecvDataset, Route.getJourDb(BizRoute.getTradeEparchyCode()));
	}

	/**
	 * 费用同步ti_a_sync_recv
	 * 
	 * @param tradeId
	 *            业务流水
	 * @param syncId
	 *            同步序列
	 * @throws Exception
	 */
	public static int syncTradeFeeSub(IData mainTrade) throws Exception
	{
		String intfId = mainTrade.getString("INTF_ID", "");

		if (StringUtils.isNotBlank(intfId) && intfId.indexOf("TF_B_TRADEFEE_GIFTFEE,") == -1 && intfId.indexOf("TF_B_TRADEFEE_SUB,") == -1)
		{
			return 0;
		}

		// 费用同步表数据准备
		IDataset syncRecvDataset = getTradeFeeSub(mainTrade);
		
		// 插费用同步表
		if (IDataUtil.isEmpty(syncRecvDataset))
		{
			return 0;
		}
		
		/**
		 * 用于处理trade_type_code=339 积分平台兑换自有产品
		 * 如果存在补缴话费的情况，需要将工单拆分成两个，分别计入到不同的存折中，满足账务的要求
		 */
		String tradeId=syncRecvDataset.getData(0).getString("BATCH_ID");
		IData hisTradeData=TradeInfoQry.queryTradeByTradeIdAndTradeType(tradeId, "339");

		if(hisTradeData!=null){
			String cancelTag=hisTradeData.getString("CANCEL_TAG");
			String rsrvStr8=hisTradeData.getString("RSRV_STR8","");
			if(cancelTag.equals("0")&&!rsrvStr8.trim().equals("")){		//如果不是返销的情况，且有补缴的情况
				Double offsetMoneyD=Double.parseDouble(rsrvStr8);

				if(offsetMoneyD>0){				
					BigDecimal bd = new BigDecimal(rsrvStr8);
					BigDecimal bd100 = new BigDecimal("100");
					long originalMoneyD=syncRecvDataset.getData(0).getLong("RECV_FEE", 0);
					long offetsetMoneyL=bd.multiply(bd100).longValue();
					long changeMoney=originalMoneyD-offetsetMoneyL;
					
					//改变原来的缴的钱
					syncRecvDataset.getData(0).put("RECV_FEE", changeMoney);
					
					//拆分的补缴的数据
					IData newSynData=new DataMap();
					newSynData.putAll(syncRecvDataset.getData(0));
					
					/*
					 * REQ201611160018 营业缴费时由信息管理插入营业传来的TRADE_ID和账务自己生成流水重复
					 * 将字段trade_id的前两位数替换为70
					 * zhangxing3
					 */
					//newSynData.put("TRADE_ID",SeqMgr.getChargeId());
					newSynData.put("TRADE_ID","70"+SeqMgr.getChargeId().substring(2));
					
					
					newSynData.put("PAY_FEE_MODE_CODE","23");
					newSynData.put("RECV_FEE", offetsetMoneyL);
					newSynData.put("PAYMENT_ID", "85");
					syncRecvDataset.add(newSynData);
				}
			}
		}
		
		insSyncRecv(syncRecvDataset);
		return syncRecvDataset.size();

	}
}
