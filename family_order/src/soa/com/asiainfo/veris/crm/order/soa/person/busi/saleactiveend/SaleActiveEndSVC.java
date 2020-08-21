package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.SaleActiveTransBean;

public class SaleActiveEndSVC extends CSBizService
{
	private static transient Logger logger = Logger.getLogger(SaleActiveEndSVC.class);
	private static final long serialVersionUID = 8989405331390912792L;
	public final static String PATTERN_STAND_YYYYMMDD = "yyyy-MM-dd";

	public IDataset queryCanEndSaleActives(IData params) throws Exception
	{
		String userId = params.getString("USER_ID");
		String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
		SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
		return saleActiveTransBean.queryEndSaleActives(userId, eparchyCode);
	}

	/**
	 * 携号转网背景下吉祥号码业务规则优化需求（上） by mengqx
	 * @throws Exception
	 */
	public IDataset queryCanEndBeautifulNumberSaleActives(IData params) throws Exception
	{
		String userId = params.getString("USER_ID");
		String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
		SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
		return saleActiveTransBean.queryCanEndBeautifulNumberSaleActives(userId, eparchyCode);
	}

	public IDataset getCommparaInfoSVC(IData inparams) throws Exception
	{
		String product_id = inparams.getString("PRODUCT_ID");
		String paramCode = inparams.getString("PARAM_CODE");
		IDataset ids = new DatasetList();
		if (product_id != null && !"".equals(product_id))
		{
			ids = CommparaInfoQry.getCommparaAllColByParser("CSM", paramCode, product_id, "0898");
		}
		return ids;
	}

	public IData querySaleActiveDetialInfos(IData params) throws Exception
	{
		String userId = params.getString("USER_ID");
		String productId = params.getString("PRODUCT_ID");
		String packageId = params.getString("PACKAGE_ID");
		String relationTradeId = params.getString("RELATION_TRADE_ID");
		String productMode = params.getString("PRODUCT_MODE");
		SaleActiveEndBean saleActiveEndBean = BeanManager.createBean(SaleActiveEndBean.class);
		return saleActiveEndBean.querySaleActiveDetailInfos(userId, productId, packageId, relationTradeId, productMode);
	}

	public IDataset queryCanEndOnNetSaleActives(IData params) throws Exception
	{
		String userId = params.getString("USER_ID");
		String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
		SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
		return saleActiveTransBean.queryOnNetEndSaleActives(userId, eparchyCode);
	}

	public IDataset closeOnNetSaleActives(IData params) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_USER_ON_NET_SALE_ACTIVE_END_DATE", params);
		return new DatasetList();
	}

	/**
	 * 计算终止营销活动的退费信息
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData calculateSaleActiveReturnMoney(IData params) throws Exception
	{
		String productId = params.getString("PRODUCT_ID");
		String packageId = params.getString("PACKAGE_ID");
		String userId = params.getString("USER_ID");
		String sn = params.getString("SERIAL_NUMBER");

		IData result = new DataMap();

		// if(true){
		// result.put("RESULT_TIP", "-1");
		// result.put("RESULT_TIP_INFO", "测试测试测试！");
		// result.put("REFUND_MONEY", "235");
		// return result;
		// }

		/*
		 * 0为成功， -1为失败
		 */
		result.put("RESULT_TIP", "0");
		result.put("RESULT_TIP_INFO", "");

		IDataset endSaleActiveInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "237", "PRODUCT_INFO", productId);

		if (IDataUtil.isNotEmpty(endSaleActiveInfos))
		{
			IData endSaleActiveInfo = endSaleActiveInfos.getData(0);
			// 获取活动类型，这个决定使用什么计算公式
			String endType = endSaleActiveInfo.getString("PARA_CODE2", "");

			IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfoById(packageId);
			/*
			 * 存话费送话费（预存话费一次到账，赠送话费分月返还、有最低约定消费）: （月约定最低消费金额-每月返还话费）*违约月份*20%
			 */
			if (endType.equals("1"))
			{
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					String monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{

						String relationTradeId = null;

						// 获取违约月份信息
						IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
						if (IDataUtil.isNotEmpty(orderSaleActives))
						{
							IData orderSaleActive = orderSaleActives.getData(0);
							// 合约规定的月份
							String months = orderSaleActive.getString("MONTHS", "");

							if (months.trim().equals(""))
							{
								result.put("RESULT_TIP", "-1");
								result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
								result.put("REFUND_MONEY", "0");
								return result;
							} else
							{
								relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

								// 计算违约的金额
								double violateReturnMoney = 0;
								// 从账务获取费用信息
								UcaData uca = UcaDataFactory.getUcaByUserId(userId);
								String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);
								if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
								{

									IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);
									if (IDataUtil.isEmpty(feeInfo))
									{
										result.put("RESULT_TIP", "-1");
										result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
										result.put("REFUND_MONEY", "0");
										return result;
									}
									String resultCode = feeInfo.getString("RESULT_CODE", "");
									if (!resultCode.equals("0"))
									{
										result.put("RESULT_TIP", "-1");
										result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
										result.put("REFUND_MONEY", "0");
										return result;
									}
									// 计算违约月份
									IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
									if (IDataUtil.isEmpty(returnFees))
									{
										result.put("RESULT_TIP", "-1");
										result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
										result.put("REFUND_MONEY", "0");
										return result;
									}

									violateReturnMoney = calculateViolateReturnFee(returnFees);
								}

								// 计算已经使用的月份
								String startDate = orderSaleActive.getString("START_DATE");
								String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
								// 计算出违约的月份
								int monthsInt = Integer.parseInt(months);

								int disObeyMonths = 0;

								if (startDate.compareTo(curDate) <= 0)
								{
									disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
								} else
								{
									disObeyMonths = monthsInt;
								}

								double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

								// 计算出最后的应收违约金
								double disObeyMoney = roundDealMoney(monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2);

								result.put("REFUND_MONEY", disObeyMoney);
							}

						} else
						{ // 如果未查询到用户已经订购营销活动，就直接返回报错
							result.put("RESULT_TIP", "-1");
							result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
							result.put("REFUND_MONEY", "0");
						}
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
				}
			}
			/*
			 * 星火礼包类活动（无预存款，赠送话费分月返还、有最低约定消费）: （月约定最低消费金额-每月返还话费）*违约月份*20%
			 */
			else if (endType.equals("2"))
			{
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					String monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						String relationTradeId = null;

						// 获取违约月份信息
						IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
						if (IDataUtil.isNotEmpty(orderSaleActives))
						{
							IData orderSaleActive = orderSaleActives.getData(0);
							// 合约规定的月份
							String months = orderSaleActive.getString("MONTHS", "");
							relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

							if (months.trim().equals(""))
							{
								result.put("RESULT_TIP", "-1");
								result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
								result.put("REFUND_MONEY", "0");
								return result;
							} else
							{

								// 计算违约的金额
								double violateReturnMoney = 0;
								// 从账务获取费用信息
								UcaData uca = UcaDataFactory.getUcaByUserId(userId);
								String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);
								if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
								{
									IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

									if (IDataUtil.isEmpty(feeInfo))
									{
										result.put("RESULT_TIP", "-1");
										result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
										result.put("REFUND_MONEY", "0");
										return result;
									}
									String resultCode = feeInfo.getString("RESULT_CODE", "");
									if (!resultCode.equals("0"))
									{
										result.put("RESULT_TIP", "-1");
										result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
										result.put("REFUND_MONEY", "0");
										return result;
									}
									// 计算违约月份
									IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
									if (IDataUtil.isEmpty(returnFees))
									{
										result.put("RESULT_TIP", "-1");
										result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
										result.put("REFUND_MONEY", "0");
										return result;
									}
									// 计算违约的金额
									violateReturnMoney = calculateViolateReturnFee(returnFees);
								}

								// 计算已经使用的月份
								String startDate = orderSaleActive.getString("START_DATE");
								String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
								// 计算出违约的月份
								int monthsInt = Integer.parseInt(months);

								int disObeyMonths = 0;

								if (startDate.compareTo(curDate) <= 0)
								{
									disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
								} else
								{
									disObeyMonths = monthsInt;
								}

								double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

								// 计算出最后的应收违约金
								double refundMoney = roundDealMoney(monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2);

								result.put("REFUND_MONEY", refundMoney);
							}

						} else
						{ // 如果未查询到用户已经订购营销活动，就直接返回报错
							result.put("RESULT_TIP", "-1");
							result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
							result.put("REFUND_MONEY", "0");
						}
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
				}
			}
			/*
			 * 存话费送话费（预存话费分期返还、赠送话费一次返还、有最低约定消费）:
			 * 活动总赠送话费*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余活动预存款
			 */
			else if (endType.equals("3"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 总赠送话费
				double sumGiftMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);
				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					// 计算违约的金额
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					// 总赠送话费
					sumGiftMoney = getFeeFromAcctReturnFee("1", "1", returnFees);
					// 剩余活动预存款
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumGiftMoney * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送实物（预存话费分期返还、有最低约定消费）:
			 * 活动赠送实物价值*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("4"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约的金额
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					// 剩余活动预存款
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
				}

				// 获取活动赠送实物价值
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (!resTypeCode.equals("4"))
						{
							String goodsValue = userSaleGood.getGoodsValue();
							String goodsNum = userSaleGood.getGoodsNum();
							if (goodsValue != null && !goodsValue.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double deviceCostD = (Double.parseDouble(goodsValue) * goodsNumD) / 100;
								sumDeviceCost = sumDeviceCost + deviceCostD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送实物（签约在网时间，预存话费一次返还）:
			 * 活动赠送实物价值*违约月份/总签约月份+（实物价值/总签约月份）*违约月份*20%
			 */
			else if (endType.equals("5"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 获取活动赠送实物价值
				double sumDeviceCost = 0;
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (!resTypeCode.equals("4"))
						{
							String goodsValue = userSaleGood.getGoodsValue();
							String goodsNum = userSaleGood.getGoodsNum();
							if (goodsValue != null && !goodsValue.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double deviceCostD = (Double.parseDouble(goodsValue) * goodsNumD) / 100;
								sumDeviceCost = sumDeviceCost + deviceCostD;
							}
						}

					}
				}

				int monthsInt = Integer.parseInt(months);

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + (sumDeviceCost / monthsInt) * disObeyMonths * 0.2);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送电子券（预存话费或部分预存话费分期返还、有月最低消费）:
			 * 活动总赠送电子券价值面额*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余活动预存款
			 */
			else if (endType.equals("6"))
			{
				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
				}

				// 活动总赠送电子券价值面额
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();

						// 计算活动总赠送电子券价值面额
						if (resTypeCode.equals("C"))
						{
							String deviceCost = userSaleGood.getDeviceCost();
							if (deviceCost != null && !deviceCost.trim().equals(""))
							{
								double deviceCostD = Double.parseDouble(deviceCost) / 100;
								sumDeviceCost = sumDeviceCost + deviceCostD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送/优惠购终端（有约定消费）: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("7"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);
			}
			/*
			 * 存话费送网卡: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("8"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送MIFI: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("9"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送魔百合: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("10"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送车务通: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("11"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送固话: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("12"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 约定消费送终端: 活动总优惠=（当时裸机价-购机价）
			 * 活动总优惠*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%
			 */
			else if (endType.equals("13"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String goodsNum = userSaleGood.getGoodsNum();
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double goodsNumD = Double.parseDouble(goodsNum);
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 买终端送礼品: 礼品进货价×客户违约月份/合约月份数+（月约定最低消费金额-每月返还金额）*违约月份*20%
			 */
			else if (endType.equals("14"))
			{
				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
				}

				// 获取活动赠送实物价值
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (!resTypeCode.equals("4"))
						{
							String deviceCost = userSaleGood.getDeviceCost();
							if (deviceCost != null && !deviceCost.trim().equals(""))
							{
								double deviceCostD = Double.parseDouble(deviceCost) / 100;
								sumDeviceCost = +deviceCostD;
							}
						}

					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 买终端送话费:（月约定最低消费金额-每月返还话费）*违约月份*20%
			 */
			else if (endType.equals("15"))
			{
				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份
				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
				}

				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 优惠购终端送话费（有终端优惠和有礼包送话费）:
			 * （裸机销售价-购机款）×客户违约月份/合约月份数+（月约定最低消费金额-每月返还话费）*违约月份*20%
			 */
			else if (endType.equals("16"))
			{

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);

				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD);
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 买终端送积分（积分分期返还）: 月约定最低消费金额*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("17"))
			{
				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份
				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
				}

				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(monthMinCostD * disObeyMonths * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 存话费送积分（预存话费分期返还、有约定消费）:
			 * 积分*积分单价*客户违约月份/合约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("18"))
			{
				// 获取积分单价
				double scorePrice = 0;

				IDataset scorePriceConfig = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "237", "SCORE_VALUE", "SCORE_VALUE");
				if (IDataUtil.isNotEmpty(scorePriceConfig))
				{
					String scorePriceStr = scorePriceConfig.getData(0).getString("PARA_CODE2", "");
					if (scorePriceStr.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取积分单价！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					scorePrice = Double.parseDouble(scorePriceStr);

				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取积分单价！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				// 获取总共的积分
				double sumValue = 0;
				IDataset allScoreValues = SaleScoreInfoQry.queryByPkgId(packageId);
				if (IDataUtil.isNotEmpty(allScoreValues))
				{
					for (int j = 0, sizej = allScoreValues.size(); j < sizej; j++)
					{
						IData allScoreValue = allScoreValues.getData(j);

						String scorevalue = allScoreValue.getString("SCORE_VALUE", "");

						if (!scorevalue.trim().equals(""))
						{
							double scorevalueD = Double.parseDouble(scorevalue);
							sumValue = sumValue + scorevalueD;
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney((scorePrice * sumValue / 100) * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);
			}
			/*
			 * 约定消费送积分（积分一次返还）:
			 * 积分*积分单价*客户违约月份/合约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("19"))
			{
				// 获取积分单价
				double scorePrice = 0;

				IDataset scorePriceConfig = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "237", productId, "SCORE_VALUE");
				if (IDataUtil.isNotEmpty(scorePriceConfig))
				{
					String scorePriceStr = scorePriceConfig.getData(0).getString("PARA_CODE2", "");
					if (scorePriceStr.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取积分单价！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					scorePrice = Double.parseDouble(scorePriceStr);

				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取积分单价！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				// 获取总共的（当时裸机价-购机价）
				double sumValue = 0;
				IDataset allScoreValues = SaleScoreInfoQry.queryByPkgId(packageId);
				if (IDataUtil.isNotEmpty(allScoreValues))
				{
					for (int j = 0, sizej = allScoreValues.size(); j < sizej; j++)
					{
						IData allScoreValue = allScoreValues.getData(j);

						String scorevalue = allScoreValue.getString("SCORE_VALUE", "");

						if (!scorevalue.trim().equals(""))
						{
							double scorevalueD = Double.parseDouble(scorevalue);
							sumValue = sumValue + scorevalueD;
						}
					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney((scorePrice * sumValue / 100) * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);
			}
			/*
			 * 存话费送有价卡: 有价卡面额*违约月份/总签约月份+（月约定最低消费金额-每月返还话费）*违约月份*20%-剩余预存款
			 */
			else if (endType.equals("20"))
			{
				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;
				// 剩余活动预存款
				double restSaleActiveDeposite = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					violateReturnMoney = calculateViolateReturnFee(returnFees);
					restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);

				}

				// 获取活动赠送实物价值
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("C"))
						{
							String deviceCost = userSaleGood.getGoodsValue();
							if (deviceCost != null && !deviceCost.trim().equals(""))
							{
								double deviceCostD = Double.parseDouble(deviceCost) / 100;
								sumDeviceCost = sumDeviceCost + deviceCostD;
							}
						}

					}
				}

				int monthsInt = Integer.parseInt(months);
				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(sumDeviceCost * disObeyMonths / monthsInt + monthMinCostD * disObeyMonths * 0.2 - violateReturnMoney * 0.2 - restSaleActiveDeposite);

				result.put("REFUND_MONEY", refundMoney);
			}
			/*
			 * 约定消费送宽带: 月最低约定消费金额*违约月份*20%
			 */
			else if (endType.equals("21"))
			{
				String months = "0"; // 总签约月份
				int disObeyMonths = 0; // 违约月份

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);

						if (startDate.compareTo(curDate) <= 0)
						{
							disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disObeyMonths = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				String monthMinCost = "0"; // 获取月约定最低消费金额

				// 获取月约定最低消费金额
				if (IDataUtil.isNotEmpty(packageExtInfos))
				{
					// 月约定最低消费金额
					monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

					if (monthMinCost.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
				} else
				{
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "无法获取月约定最低消费金额！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				double refundMoney = roundDealMoney(monthMinCostD * disObeyMonths * 0.2);

				result.put("REFUND_MONEY", refundMoney);
			}
			/*
			 * 无约定消费送业务类: 活动总优惠=（当时裸机价-购机价） 活动总优惠×履行合约月份/活动签约月份+月活动优惠金额×违约月份×20%
			 */
			else if (endType.equals("22"))
			{

				String months = "0"; // 总签约月份
				int userMonths = 0; // 履行合约月份
				int disobeyMonthes = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);
						userMonths = SysDateMgr.monthInterval(startDate, curDate);

						if (startDate.compareTo(curDate) <= 0)
						{
							disobeyMonthes = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
						} else
						{
							disobeyMonthes = monthsInt;
						}
					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 计算违约的金额
				double violateReturnMoney = 0;

				// 从账务获取费用信息
				UcaData uca = UcaDataFactory.getUcaByUserId(userId);
				String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);

				if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
				{
					IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

					if (IDataUtil.isEmpty(feeInfo))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：返回数据集为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}
					String resultCode = feeInfo.getString("RESULT_CODE", "");
					if (!resultCode.equals("0"))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息出错：" + feeInfo.getString("RESULT_INFO", ""));
						result.put("REFUND_MONEY", "0");
						return result;
					}
					// 计算违约月份
					IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
					if (IDataUtil.isEmpty(returnFees))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "从账务获取营销活动费用信息为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					}

					violateReturnMoney = calculateViolateReturnFee(returnFees);
				}

				// 获取总共的（当时裸机价-购机价）
				double sumDeviceCost = 0;
				List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
				if (userSaleGoods != null && userSaleGoods.size() > 0)
				{
					for (SaleGoodsTradeData userSaleGood : userSaleGoods)
					{
						String resTypeCode = userSaleGood.getResTypeCode();
						if (resTypeCode.equals("4"))
						{
							String devicePrice = userSaleGood.getRsrvStr6();
							String salePrice = userSaleGood.getRsrvNum5();
							if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
							{
								double devicePriceD = Double.parseDouble(devicePrice) / 100;
								double salePriceD = Double.parseDouble(salePrice) / 100;
								sumDeviceCost = +(devicePriceD - salePriceD);
							}
						}
					}
				}

				int monthsInt = Integer.parseInt(months);

				double refundMoney = roundDealMoney(sumDeviceCost * userMonths / monthsInt + violateReturnMoney * disobeyMonthes * 0.2);

				result.put("REFUND_MONEY", refundMoney);

			}
			/*
			 * 光宽带营销活动违约金:69900199 一、存量活动的违约金计算公式：
			 * 79900113\79900114\79900115\79900116\79900117\79900118
			 * 赠送话费+赠送话费/12*剩余月份*20%-客户预存话费未返还金额
			 * 
			 * 二、新增活动的违约金计算公司：
			 * 79900104\79900105\79900106\79900122\79900123\79900124 （1）办理包年
			 * 赠送话费+赠送话费/12*剩余月份*20%-对应速率包年费用/12*剩余月份
			 * 
			 * （2）办理宽带1+活动 赠送话费+赠送话费/12*剩余月份*20%
			 */
			else if (endType.equals("23"))
			{
				String months = "0"; // 总签约月份
				int userMonths = 0; // 履行合约月份
				int disobeyMonthes = 0; // 违约月份

				String relationTradeId = null;

				// 获取违约月份信息
				IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
				if (IDataUtil.isNotEmpty(orderSaleActives))
				{
					IData orderSaleActive = orderSaleActives.getData(0);
					// 合约规定的月份
					months = orderSaleActive.getString("MONTHS", "0");
					relationTradeId = orderSaleActive.getString("RELATION_TRADE_ID");

					if (months.trim().equals(""))
					{
						result.put("RESULT_TIP", "-1");
						result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
						result.put("REFUND_MONEY", "0");
						return result;
					} else
					{
						/*
						 * //计算已经使用的月份 //活动是立即赠送，按受理时间计算 String
						 * acceptDate=orderSaleActive.getString("ACCEPT_DATE");
						 * String
						 * curDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS
						 * ); //计算出违约的月份 int monthsInt=Integer.parseInt(months);
						 * userMonths=SysDateMgr.monthInterval(acceptDate,
						 * curDate);
						 * 
						 * if(userMonths>1){ int month = monthsInt-userMonths;
						 * if(month == 0) { disobeyMonthes=0;
						 * result.put("REFUND_MONEY", "0"); return result;
						 * }else{ disobeyMonthes=month; } }else{
						 * disobeyMonthes=monthsInt; }
						 */

						// 计算已经使用的月份
						String startDate = orderSaleActive.getString("START_DATE");
						String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
						// 计算出违约的月份
						int monthsInt = Integer.parseInt(months);
						userMonths = SysDateMgr.monthInterval(startDate, curDate);

						if (startDate.compareTo(curDate) <= 0)
						{
							disobeyMonthes = monthsInt - SysDateMgr.monthInterval(startDate, curDate);

							if (0 == disobeyMonthes)
							{
								result.put("REFUND_MONEY", "0");
								return result;
							}

						} else
						{
							disobeyMonthes = monthsInt;
						}

					}
				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
					result.put("REFUND_MONEY", "0");
					return result;
				}

				// 活动活动参数配置
				IDataset ds = CommparaInfoQry.getCommparaInfoByCode5("CSM", "1012", productId, packageId, null, null, "0898");
				if (IDataUtil.isNotEmpty(ds))
				{
					// 计算违约的金额
					double violateReturnMoney = 0;
					IData data = ds.getData(0);

					// 区分0存量、1新增标识
					String activeFlag = data.getString("PARA_CODE4", "");
					String givingFee = data.getString("PARA_CODE2", "");
					String yearFee = data.getString("PARA_CODE5", "");
					if ("0".equals(activeFlag))
					{
						// 获取客户预存话费未返还金额
						IDataset AcctDeposit = AcctCall.queryAccountDepositBySn(sn);
						if (IDataUtil.isNotEmpty(AcctDeposit))
						{
							for (int i = 0; i < AcctDeposit.size(); i++)
							{
								IData deposit = AcctDeposit.getData(i);
								String trade_fee = deposit.getString("DEPOSIT_BALANCE", "");
								String deposit_code = deposit.getString("DEPOSIT_CODE", "");
								if ("362".equals(deposit_code))
								{
									// 赠送话费+赠送话费/12*剩余月份*20%-客户预存话费未返还金额
									int monthsInt = Integer.parseInt(months);

									double givingFeeInt = Double.parseDouble(givingFee);
									double tradeFeeInt = Double.parseDouble(trade_fee) / 100;

									double refundMoney = roundDealMoney(givingFeeInt + givingFeeInt / monthsInt * disobeyMonthes * 0.2 - tradeFeeInt);
									if (refundMoney > 0)
									{
										result.put("REFUND_MONEY", refundMoney);
									} else
									{
										result.put("REFUND_MONEY", "0");
									}

								}
							}
						}
					}

					if ("1".equals(activeFlag))
					{
						IDataset YearSaleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userId, "67220428");
						if (IDataUtil.isNotEmpty(YearSaleActives))
						{
							// 赠送话费+赠送话费/12*剩余月份*20%-对应速率包年费用/12*包年剩余月份
							int monthsInt = Integer.parseInt(months);

							double givingFeeInt = Double.parseDouble(givingFee);
							double yearFeeInt = Double.parseDouble(yearFee);

							String startDate = YearSaleActives.getData(0).getString("START_DATE");
							String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
							int disobeyyearMonthes = 0; // 包年违约月份
							disobeyyearMonthes = monthsInt - SysDateMgr.monthInterval(startDate, curDate);

							double refundMoney = roundDealMoney(givingFeeInt + givingFeeInt / monthsInt * disobeyMonthes * 0.2 - yearFeeInt / monthsInt * disobeyyearMonthes);
							if (refundMoney > 0)
							{
								result.put("REFUND_MONEY", refundMoney);
							} else
							{
								result.put("REFUND_MONEY", "0");
							}

						}

						IDataset BroadSaleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userId, "69908001");
						if (IDataUtil.isNotEmpty(BroadSaleActives))
						{
							// 赠送话费+赠送话费/12*剩余月份*20%
							int monthsInt = Integer.parseInt(months);

							double givingFeeInt = Double.parseDouble(givingFee);

							double refundMoney = roundDealMoney(givingFeeInt + givingFeeInt / monthsInt * disobeyMonthes * 0.2);
							result.put("REFUND_MONEY", refundMoney);
						}
					}

				} else
				{ // 如果未查询到用户已经订购营销活动，就直接返回报错
					result.put("RESULT_TIP", "-1");
					result.put("RESULT_TIP_INFO", "查询不到此营销活动配置！");
					result.put("REFUND_MONEY", "0");
					return result;
				}
			}
		} else
		{
			result.put("RESULT_TIP", "-1");
			result.put("RESULT_TIP_INFO", "本活动无系统配置");
			result.put("REFUND_MONEY", "0");
		}

		return result;

	}

	/**
	 * 计算返还的
	 * 
	 * @param returnFee
	 * @return
	 * @throws Exception
	 */
	public double calculateViolateReturnFee(IDataset returnFees) throws Exception
	{
		double sumViolateReturnFee = 0;

		for (int i = 0, size = returnFees.size(); i < size; i++)
		{
			IData returnFee = returnFees.getData(i);

			double leftMoney = returnFee.getDouble("LEFT_MONEY", 0);
			if (leftMoney == -1d)
			{
				continue;
			}

			sumViolateReturnFee = sumViolateReturnFee + leftMoney / 100;
		}

		// String
		// curTime=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
		//    	
		// /*
		// * 首先确认返还金额的时间是否有重叠:
		// * 如果有重叠的话需呀
		// *
		// */
		// IData giftMoney=null;
		// IData depositeMoney=null;
		//    	
		// for(int i=0,size=returnFees.size();i<size;i++){
		// IData feeInfo=returnFees.getData(i);
		// String actionAttrAct=feeInfo.getString("ACTION_ATTR");
		//    		
		// if(actionAttrAct.equals("0")){ //预存
		// depositeMoney=feeInfo;
		// }else if(actionAttrAct.equals("1")){ //赠送
		// giftMoney=feeInfo;
		// }
		// }
		//    	
		// if(giftMoney!=null&&depositeMoney!=null){
		// /*
		// * 查看预存和赠送是否完全存在重叠，还是部分重叠
		// */
		// String giftMoneyStartDate=giftMoney.getString("START_DATE");
		// String giftMoneyEndDate=giftMoney.getString("END_DATE");
		//    		
		// String depositeMoneyStartDate=giftMoney.getString("START_DATE");
		// String depositeMoneyEndDate=giftMoney.getString("END_DATE");
		//    		
		// //如果是完全重叠，就按照赠送金额来计算
		// if(giftMoneyStartDate.compareTo(depositeMoneyStartDate)<0&&
		// giftMoneyEndDate.compareTo(depositeMoneyEndDate)>0){
		// for(int i=0,size=returnFees.size();i<size;i++){
		// IData returnFee=returnFees.getData(i);
		//            		
		// String actionAttrAct=returnFee.getString("ACTION_ATTR");
		// if(actionAttrAct.equals("0")){
		// continue;
		// }
		//            		
		// String startDate=transferTime(returnFee.getString("START_DATE"));
		// String endDate=transferTime(returnFee.getString("END_DATE"));
		// double sendMoney=returnFee.getDouble("SEND_MONEY", 0)/100;
		//            		
		// //如果在费用的期间
		// if(startDate.compareTo(curTime)<=0&&endDate.compareTo(curTime)>=0){
		// //计算违约月份信息
		// int violateMonths=SysDateMgr.monthInterval(curTime, endDate);
		//            			
		// sumViolateReturnFee=sumViolateReturnFee+sendMoney*(violateMonths-1);
		// //不算当前月份
		// }
		// //还未到达赠送期
		// else if(startDate.compareTo(curTime)>=0){
		// //计算违约月份信息
		// int violateMonths=SysDateMgr.monthInterval(startDate, endDate);
		// sumViolateReturnFee=sumViolateReturnFee+sendMoney*violateMonths;
		// }
		// }
		// }
		//    		
		// else {
		//    			
		// }
		//    		
		// }else{
		// for(int i=0,size=returnFees.size();i<size;i++){
		// IData returnFee=returnFees.getData(i);
		// String startDate=transferTime(returnFee.getString("START_DATE"));
		// String endDate=transferTime(returnFee.getString("END_DATE"));
		// double sendMoney=returnFee.getDouble("SEND_MONEY", 0)/100;
		//        		
		// //如果在费用的期间
		// if(startDate.compareTo(curTime)<=0&&endDate.compareTo(curTime)>=0){
		// //计算违约月份信息
		// int violateMonths=SysDateMgr.monthInterval(curTime, endDate);
		//        			
		// sumViolateReturnFee=sumViolateReturnFee+sendMoney*(violateMonths-1);
		// //不算当前月份
		// }
		// //还未到达赠送期
		// else if(startDate.compareTo(curTime)>=0){
		// //计算违约月份信息
		// int violateMonths=SysDateMgr.monthInterval(startDate, endDate);
		// sumViolateReturnFee=sumViolateReturnFee+sendMoney*violateMonths;
		// }
		// }
		// }

		return sumViolateReturnFee;
	}

	public String transferTime(String time) throws Exception
	{

		String year = time.substring(0, 4);
		String month = time.substring(4, 6);
		String day = time.substring(6, 8);

		return year + "-" + month + "-" + day;
	}

	public double calculateViolateAllReturnFee(IDataset returnFees) throws Exception
	{
		double sumViolateReturnFee = 0;

		for (int i = 0, size = returnFees.size(); i < size; i++)
		{
			IData returnFee = returnFees.getData(i);

			double leftMoney = returnFee.getDouble("ALL_MONEY", 0);
			if (leftMoney == -1d)
			{
				continue;
			}

			sumViolateReturnFee = sumViolateReturnFee + leftMoney / 100;
		}
		return sumViolateReturnFee;
	}

	/**
	 * 获取费用当中的信息
	 * 
	 * @param actionAttr
	 *            ：0 预存， 1 赠送
	 * @param feeType
	 *            ：1 总金额，2 剩余金额
	 * @param feeInfos
	 * @return
	 * @throws Exception
	 */
	public double getFeeFromAcctReturnFee(String actionAttr, String feeType, IDataset feeInfos) throws Exception
	{
		double result = 0;

		for (int i = 0, size = feeInfos.size(); i < size; i++)
		{
			IData feeInfo = feeInfos.getData(i);

			String actionAttrAct = feeInfo.getString("ACTION_ATTR");

			// 预存
			if (actionAttr.equals("0") && actionAttrAct.equals("0"))
			{
				if (feeType.equals("1"))
				{
					result = result + feeInfo.getDouble("ALL_MONEY", 0);
				} else if (feeType.equals("2"))
				{
					result = result + feeInfo.getDouble("LEFT_MONEY", 0);
				}
			}
			// 赠送
			else if (actionAttr.equals("1") && actionAttrAct.equals("1"))
			{
				if (feeType.equals("1"))
				{
					result = result + feeInfo.getDouble("ALL_MONEY", 0);
				} else if (feeType.equals("2"))
				{
					result = result + feeInfo.getDouble("LEFT_MONEY", 0);
				}
			}
		}

		return result / 100;
	}

	public double roundDealMoney(double money) throws Exception
	{
		BigDecimal bd = BigDecimal.valueOf(money);
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public IDataset getCommpara1530(IData params) throws Exception
	{
		String paramCode = params.getString("PARAM_CODE", "");
		String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
		return CommparaInfoQry.getCommpara("CSM", "1530", paramCode, eparchyCode);
	}

	public static Date string2Date(String strDate, String format) throws Exception
	{
		if (null == strDate)
		{
			throw new NullPointerException();
		}
		DateFormat df = new SimpleDateFormat(format);

		return df.parse(strDate);
	}

	/**
	 * 在网营销活动违约金公式
	 * <p>
	 * Title: calculateSaleActiveEndOnNetReturnMoney
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-2-23 上午10:05:14
	 */
	public IData calculateSaleActiveEndOnNetReturnMoney(IData params) throws Exception
	{

		String productId = params.getString("PRODUCT_ID");
		String packageId = params.getString("PACKAGE_ID");
		String userId = params.getString("USER_ID");
		String sn = params.getString("SERIAL_NUMBER");
		String relationtradeId = params.getString("RELATION_TRADE_ID");

		IData result = new DataMap();

		IDataset endSaleActiveInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "2370", "PRODUCT_INFO", productId);

		if (IDataUtil.isNotEmpty(endSaleActiveInfos))
		{
			result = roundEndOnNetDealMoney(endSaleActiveInfos, userId, productId, packageId, relationtradeId);
		} else
		{

			endSaleActiveInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "2370", "PRODUCT_INFO", "0");

			if (IDataUtil.isNotEmpty(endSaleActiveInfos))
			{
				result = roundEndOnNetDealMoney(endSaleActiveInfos, userId, productId, packageId, relationtradeId);
			} else
			{
				result.put("RESULT_TIP", "-1");
				result.put("RESULT_TIP_INFO", "本活动无系统配置");
				result.put("REFUND_MONEY", "0");
			}

		}

		return result;
	}

	/**
	 * <p>
	 * Title: roundEndOnNetDealMoney
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @return
	 * @author XUYT
	 * @throws Exception
	 * @date 2017-2-23 上午10:03:09
	 */
	private IData roundEndOnNetDealMoney(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId) throws Exception
	{

		IData result = new DataMap();
		/*
		 * 0为成功， -1为失败
		 */
		result.put("RESULT_TIP", "0");
		result.put("RESULT_TIP_INFO", "");
		// 最低主套餐月费（可配置，先配置800，单位分）*违约月份*20%
		SaleActiveEndBean saleActiveEndBean = BeanManager.createBean(SaleActiveEndBean.class);

		IData endSaleActiveInfo = endSaleActiveInfos.getData(0);
		// 最低主套餐月费
		String monthMinCost = endSaleActiveInfo.getString("PARA_CODE2", "");
		// 乘以百分数
		String monthMinScore = endSaleActiveInfo.getString("PARA_CODE3", "");

		String relationTradeId = null;

		// 获取违约月份信息
		IDataset orderSaleActives = UserSaleActiveInfoQry.queryOnNetSaleActiveByPPIDuserId(userId, productId, packageId, relationtradeId);
		if (IDataUtil.isNotEmpty(orderSaleActives))
		{
			IData orderSaleActive = orderSaleActives.getData(0);
			// 合约规定的月份
			String months = orderSaleActive.getString("MONTHS", "");

			if (months.trim().equals(""))
			{
				result.put("RESULT_TIP", "-1");
				result.put("RESULT_TIP_INFO", "获取合约规定的月份为空！");
				result.put("REFUND_MONEY", "0");
				return result;
			} else
			{
				// 计算已经使用的月份
				String startDate = orderSaleActive.getString("RSRV_DATE1");
				String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
				// 计算出违约的月份
				int monthsInt = Integer.parseInt(months);

				int disObeyMonths = 0;

				if (startDate.compareTo(curDate) <= 0)
				{
					disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
				} else
				{
					disObeyMonths = monthsInt;
				}

				double monthMinCostD = Double.parseDouble(monthMinCost) / 100;

				// 计算出最后的应收违约金
				double disObeyMoney = roundDealMoney(monthMinCostD * disObeyMonths * 0.2);

				if (disObeyMoney < 0)
				{
					result.put("REFUND_MONEY", "0");
				} else
				{
					result.put("REFUND_MONEY", disObeyMoney);
				}
			}

		} else
		{ // 如果未查询到用户已经订购营销活动，就直接返回报错
			result.put("RESULT_TIP", "-1");
			result.put("RESULT_TIP_INFO", "用户未订购此营销活动或者订购已经失效！");
			result.put("REFUND_MONEY", "0");
		}
		return result;
	}

	/**
	 * 违约金计算公式：违约优惠成本+违约优惠金额
	 * <p>
	 * Title: newcalculateSaleActiveReturnMoney
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-3 上午10:52:08
	 */
	public IData newcalculateSaleActiveReturnMoney(IData params) throws Exception
	{

		String productId = params.getString("PRODUCT_ID");
		String packageId = params.getString("PACKAGE_ID");
		String userId = params.getString("USER_ID");
		String sn = params.getString("SERIAL_NUMBER");
		String relationtradeId = params.getString("RELATION_TRADE_ID");
		double ReturnMoney = 0;
		double ReturnCost = 0;
		double ReturnPrice = 0;

		IData result = new DataMap();

		result.put("RESULT_TIP", "0");
		result.put("RESULT_TIP_INFO", "");

		IData data = new DataMap();
		data.put("SUBSYS_CODE", "CSM");
		data.put("PARAM_ATTR", "2373");
		data.put("PARAM_CODE", "BREACH_CONTRACT_PRODUCT_INFO");
		data.put("PARA_CODE2", packageId);
		data.put("PARA_CODE3", productId);
		IDataset endSaleActiveInfos = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "BREACH_CONTRACT_PRODUCT_INFO", packageId, productId);

		if (IDataUtil.isEmpty(endSaleActiveInfos))
		{
			data.put("PARA_CODE2", "-1");
			endSaleActiveInfos = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "BREACH_CONTRACT_PRODUCT_INFO", "-1", productId);
			if (IDataUtil.isEmpty(endSaleActiveInfos))
			{
				result.put("RESULT_TIP", "-1");
				result.put("RESULT_TIP_INFO", "本活动无系统配置");
				result.put("REFUND_COST", "0");// 需追缴违约优惠成本
				result.put("REFUND_PRICE", "0");// 需追缴违约金
				result.put("REFUND_MONEY", "0");// 需追缴违约优惠成本+需追缴违约金
			} else
			{
				// 产品下具体包计算公式
				ReturnCost = roundDealMoneyReturnCost(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

				ReturnPrice = roundDealMoneyReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

				ReturnMoney = roundDealMoney(ReturnCost + ReturnPrice) < 0 ? 0 : roundDealMoney(ReturnCost + ReturnPrice);

				result.put("REFUND_COST", ReturnCost);
				result.put("REFUND_PRICE", ReturnPrice);
				result.put("REFUND_MONEY", ReturnMoney);
			}
		} else
		{
			//加是否归还摄像头标记
			endSaleActiveInfos.getData(0).put("BACK_TERM", params.getString("BACK_TERM"));
			// 产品下所有包统一计算公式
			ReturnCost = roundDealMoneyReturnCost(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

			ReturnPrice = roundDealMoneyReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

			ReturnMoney = roundDealMoney(ReturnCost + ReturnPrice) < 0 ? 0 : roundDealMoney(ReturnCost + ReturnPrice);

			result.put("REFUND_COST", ReturnCost);
			result.put("REFUND_PRICE", ReturnPrice);
			result.put("REFUND_MONEY", ReturnMoney);
		}

		return result;

	}

	/**
	 * 违约优惠成本
	 * <p>
	 * Title: roundDealMoneyReturnCost
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-8 下午03:35:09
	 */
	private double roundDealMoneyReturnCost(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		double returnCost = 0;
		IData data = endSaleActiveInfos.getData(0);
		if ("1".equals(data.getString("PARA_CODE4")))
		{
			// 0
			returnCost = 0;

		} else if ("2".equals(data.getString("PARA_CODE4")))
		{
			// 活动总优惠*（违约月份/总签约月份）
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_DISCNT");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset costCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_DISCNT", packageId, productId);
			double cost = 0;
			if (IDataUtil.isNotEmpty(costCost))
			{
				cost = roundDealMoney(Double.parseDouble(costCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnCost = roundDealMoney(cost * Month / Months);

		} else if ("3".equals(data.getString("PARA_CODE4")))
		{
			// 活动总优惠=（当时裸机价-购机价）
			double sumDeviceCost = sumDeviceReturnCost(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

			returnCost = roundDealMoney(sumDeviceCost);

		} else if ("4".equals(data.getString("PARA_CODE4")))
		{
			// 活动总优惠=（当时裸机价-购机价）
			// 活动总优惠*（违约月份/总签约月份）
			double sumDeviceCost = sumDeviceReturnCost(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnCost = roundDealMoney(sumDeviceCost * Month / Months);

		} else if ("5".equals(data.getString("PARA_CODE4")))
		{
			// 活动总优惠=（当时裸机价-购机价）*（违约月份/总签约月份）-剩余预存款
			double sumDeviceCost = sumDeviceReturnCost(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			double restSaleActiveDeposite = depositeReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			returnCost = roundDealMoney(sumDeviceCost * Month / Months - restSaleActiveDeposite);

		} else if ("6".equals(data.getString("PARA_CODE4")))
		{
			// 每月返还金额*（总合约月份-违约月份）= 已返还金额
			double monthAllPrice = monthReturnAllPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			double monthPrice = monthReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			returnCost = roundDealMoney(monthAllPrice - monthPrice);

		} else if ("7".equals(data.getString("PARA_CODE4")))
		{
			// 成本/总签约月份*违约月份-剩余预存款
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_COST");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset costCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_COST", packageId, productId);
			double cost = 0;
			if (IDataUtil.isNotEmpty(costCost))
			{
				cost = roundDealMoney(Double.parseDouble(costCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			double restSaleActiveDeposite = depositeReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			returnCost = roundDealMoney(cost / Months * Month - restSaleActiveDeposite);

		} else if ("8".equals(data.getString("PARA_CODE4")))
		{
			// 积分*积分单价*客户违约月份/总签约月份-剩余预存款
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_INTEGRAL");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset integralCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_INTEGRAL", packageId, productId);
			double integral = 0;
			double integralPrice = 0;
			if (IDataUtil.isNotEmpty(integralCost))
			{
				integral = roundDealMoney(Double.parseDouble(integralCost.getData(0).getString("PARA_CODE4", "0")));
				integralPrice = roundDealMoney(Double.parseDouble(integralCost.getData(0).getString("PARA_CODE5", "0")) / 100);

			}
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			double restSaleActiveDeposite = depositeReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			returnCost = roundDealMoney(integral * integralPrice * Month / Months - restSaleActiveDeposite);

		} else if ("9".equals(data.getString("PARA_CODE4")))
		{
			// 赠送话费
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_GIFT");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset giftCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_GIFT", packageId, productId);
			if (IDataUtil.isNotEmpty(giftCost))
			{
				returnCost = roundDealMoney(Double.parseDouble(giftCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}

		}else if ("10".equals(data.getString("PARA_CODE4")))
		{
			// 活动活动参数配置
			IDataset ds = CommparaInfoQry.getCommparaInfoByCode5("CSM", "1012", productId, packageId, null, null, "0898");
			if (IDataUtil.isNotEmpty(ds))
			{
				IData param = ds.getData(0);
				// 区分0存量、1新增标识
				String activeFlag = param.getString("PARA_CODE4", "");
				String givingFee = param.getString("PARA_CODE2", "");
				String yearFee = param.getString("PARA_CODE5", "");
				if ("0".equals(activeFlag))
				{
					//赠送话费-客户预存话费未返还金额 【成本金】
					double givingFeeInt = Double.parseDouble(givingFee);
					double restSaleActiveDeposite = widedepositeReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
					returnCost = roundDealMoney(givingFeeInt - restSaleActiveDeposite);
				}
				if ("1".equals(activeFlag))
				{
					//新增办理包年：赠送话费-对应速率包年费用/总签约月份*违约月份 【成本金】
					IDataset YearSaleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userId, "67220428");
					if (IDataUtil.isNotEmpty(YearSaleActives))
					{
						double givingFeeInt = Double.parseDouble(givingFee);
						double yearFeeInt = Double.parseDouble(yearFee);
						int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
						int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
						returnCost = roundDealMoney(givingFeeInt - yearFeeInt / Months * Month);
					}
					//新增宽带1+：赠送话费【成本金】
					IDataset BroadSaleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userId, "69908001");
					if (IDataUtil.isNotEmpty(BroadSaleActives))
					{
						double givingFeeInt = Double.parseDouble(givingFee);
						returnCost = roundDealMoney(givingFeeInt);
					}
				}
			}
		}else if("11".equals(data.getString("PARA_CODE4"))){
			// （销售价-成本价）* 90% *（违约月份/总签约月份）
			double sumDiscountCost = sumDiscountCost(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnCost = roundDealMoney(sumDiscountCost * 0.9 * Month / Months);
		}
		//REQ202005260002_关于开展5G招募活动的开发需求
		else if ("12".equals(data.getString("PARA_CODE4")))
		{
			// 赠送计费-积分兑换话费
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_COST");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset costCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_COST", packageId, productId);
			double cost = 0;
			if (IDataUtil.isNotEmpty(costCost))
			{
				cost = roundDealMoney(Double.parseDouble(costCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}			
			int useMonths = useMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnCost = roundDealMoney( cost  * useMonths );

		}
		//REQ202005260002_关于开展5G招募活动的开发需求
		if ("1".equals(data.getString("PARA_CODE6", "")))
		{
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			if(0 >= Month)
			{
				return roundDealMoney(0);
			}
		}

		return returnCost;

	}

	/**
	 * 违约优惠金额
	 * <p>
	 * Title: roundDealMoneyReturnPrice
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-8 下午03:35:23
	 */
	private double roundDealMoneyReturnPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{

		double returnPrice = 0;
		IData data = endSaleActiveInfos.getData(0);
		if ("1".equals(data.getString("PARA_CODE5")))
		{
			// 0
			returnPrice = 0;

		} else if ("2".equals(data.getString("PARA_CODE5")))
		{

			// 月约定最低消费金额*违约月份*20%
			double agreedPrice = agreedReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			double rate = 0.2d;
			if("69900703".equals(productId)){
				//Month
                Month = beautifulNumberSaleActiveMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
				rate = 0.95d;
			}
			returnPrice = roundDealMoney(agreedPrice * Month * rate);

		} else if ("3".equals(data.getString("PARA_CODE5")))
		{

			// （月约定最低消费金额-每月返还话费）*违约月份*20%
			double agreedPrice = agreedReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			double monthPrice = monthReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

			returnPrice = roundDealMoney(agreedPrice * Month * 0.2 - monthPrice * 0.2);

		} else if ("4".equals(data.getString("PARA_CODE5")))
		{

			// 活动赠送话费/总签约月份×违约月份×20%
			// double giftPrice = giftReturnPrice(endSaleActiveInfos, userId,
			// productId, packageId, relationtradeId, sn);
			double giftPrice = 0;
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_GIFT");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset giftCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_GIFT", packageId, productId);
			if (IDataUtil.isNotEmpty(giftCost))
			{
				giftPrice = roundDealMoney(Double.parseDouble(giftCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}

			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			int Months = AllMonths(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);

			returnPrice = roundDealMoney(giftPrice * Month / Months * 0.2);

		} else if ("5".equals(data.getString("PARA_CODE5")))
		{
			// 每月返还话费（配置）*违约月份*20%
			double monreturnPrice = 0;
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_RETURN");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset giftCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_RETURN", packageId, productId);
			if (IDataUtil.isNotEmpty(giftCost))
			{
				monreturnPrice = roundDealMoney(Double.parseDouble(giftCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnPrice = roundDealMoney(monreturnPrice * Month * 0.2);

		} else if ("6".equals(data.getString("PARA_CODE5")))
		{
			// 每月返还话费*违约月份*20%-剩余预存款（退CPE终端）
			double monthPrice = monthReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			double restSaleActiveDeposite = depositeReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			returnPrice = roundDealMoney(monthPrice * 0.2 - restSaleActiveDeposite);

		}else if ("8".equals(data.getString("PARA_CODE5")))
		{
			// （月约定最低消费金额+固定费）*违约月份*20%
			IData map = new DataMap();
			map.put("SUBSYS_CODE", "CSM");
			map.put("PARAM_ATTR", "2373");
			map.put("PARAM_CODE", "COST_DISCNT");
			map.put("PARA_CODE2", packageId);
			map.put("PARA_CODE3", productId);
			IDataset costCost = CommparaInfoQry.getCommparaInfoBy1To7("CSM", "2373", "COST_DISCNT", packageId, productId);
			double cost = 0;
			if (IDataUtil.isNotEmpty(costCost))
			{
				cost = roundDealMoney(Double.parseDouble(costCost.getData(0).getString("PARA_CODE4", "0")) / 100);
			}
			double agreedPrice = agreedReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnPrice = roundDealMoney((agreedPrice + cost)* Month * 0.2);
			
		}else if("9".equals(data.getString("PARA_CODE5"))){
			//终止“和目尝鲜活动（约定使用7天回看云储存功能一年包）”
			//（1）客户未归还摄像头计算公式为摄像头费用（200元/12）*剩余月份+云存储月费*剩余月份*20% 
			//（2）客户归还摄像头计算公式为云存储月费*剩余月份*20% 
			//云存储月费
			double yunFee=data.getDouble(("PARA_CODE25"),0.0);
			String backTerm=data.getString("BACK_TERM","0");
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			//未归还摄像头
			if("0".equals(backTerm)){
				returnPrice=roundDealMoney(((200.0/12)*Month)+(yunFee*Month*0.2));
			//归还摄像头	
			}else{
				returnPrice=roundDealMoney(yunFee*Month*0.2);
			}
		}else if("10".equals(data.getString("PARA_CODE5"))){
			// 固定金额违约金公式
			// 参数para_code9 配置固定收取违约金
			String fiexdFeeStr = data.getString("PARA_CODE9","0");
			// 如果取出来的值不是数字 ， 默认违约金为0
			int fiexdFee = 0;
			// 取出来的值是数字
			if (isNumber(fiexdFeeStr)){
				fiexdFee = Integer.parseInt(fiexdFeeStr);
			}

			returnPrice=roundDealMoney(fiexdFee);
		}
		else if ("A".equals(data.getString("PARA_CODE5")))
		{
			//REQ201902260042新增“约定套餐一年成为全球通客户”的规则  by mengqx 20190305
			//违约金按照“88元*违约月份数”配置
	    	IDataset Commpara305 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "305", "GSM_SILVER_CONTRACT_PRODUCT", productId, packageId, "0898");
	    	double agreedPrice = 0;
			if (IDataUtil.isNotEmpty(Commpara305))
			{
				agreedPrice = roundDealMoney(Double.parseDouble(Commpara305.getData(0).getString("PARA_CODE4", "0")) / 100);
			}
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnPrice = roundDealMoney(agreedPrice * Month);
		}else if ("B".equals(data.getString("PARA_CODE5")))
		{
			//REQ201904090061新增全球通爆米花套餐及其合约活动  by mengqx 20190521
			//违约金按照“套餐月费*违约月份数”配置
	    	IDataset Commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "423", "POPCORN_ACTIVITY", productId, packageId, "0898");
	    	double agreedPrice = 0;
			if (IDataUtil.isNotEmpty(Commpara423))
			{
				agreedPrice = roundDealMoney(Double.parseDouble(Commpara423.getData(0).getString("PARA_CODE4", "0")) / 100);
			}
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			//REQ201907010025畅享套餐合约活动的违约金调整需求 add by wuhao5 20190715
			//调整违约金计算规则为“（套餐月费-月返还金额）×违约月份×20%”			
			double monthPrice = monthReturnPrice(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn);

			returnPrice = roundDealMoney(agreedPrice * Month * 0.2 - monthPrice * 0.2);
			String message = "wuhao5>>agreedPrice" + agreedPrice + "Month" + Month + "agreedPrice * Month * 0.2" + agreedPrice * Month * 0.2 + "monthPrice" + monthPrice + "monthPrice * 0.2" + monthPrice * 0.2;
			System.out.print(message);
			logger.debug(message);
		} else if ("11".equals(data.getString("PARA_CODE5")))
		{

			// 配置金额*违约月份
			double agreedPrice = Double.parseDouble(data.getString("PARA_CODE1")) / 100;
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			returnPrice = roundDealMoney(agreedPrice * Month);

		}
		
		if ("1".equals(data.getString("PARA_CODE6", "")))
		{
			int Month = defaultMonth(endSaleActiveInfos, userId, productId, packageId, relationtradeId, sn, data);
			if(0 >= Month)
			{
				return roundDealMoney(0);
			}
		}
		return returnPrice;
	}

	/**
	 * 活动总优惠=（当时裸机价-购机价）
	 * <p>
	 * Title: sumDeviceReturnCost
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-10 下午08:49:34
	 */
	private double sumDeviceReturnCost(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		// 获取总共的（当时裸机价-购机价）
		double sumDeviceCost = 0;
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationtradeId);
		if (userSaleGoods != null && userSaleGoods.size() > 0)
		{
			for (SaleGoodsTradeData userSaleGood : userSaleGoods)
			{
				String resTypeCode = userSaleGood.getResTypeCode();
				if (resTypeCode.equals("4"))
				{
					String goodsNum = userSaleGood.getGoodsNum();
					String devicePrice = userSaleGood.getRsrvStr6();
					String salePrice = userSaleGood.getRsrvNum5();
					if (devicePrice != null && !devicePrice.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
					{
						double goodsNumD = Double.parseDouble(goodsNum);
						double devicePriceD = Double.parseDouble(devicePrice) / 100;
						double salePriceD = Double.parseDouble(salePrice) / 100;
						sumDeviceCost = sumDeviceCost + (devicePriceD - salePriceD) * goodsNumD;
					}
				}
			}
		}
		return sumDeviceCost;
	}

	/**
	 * 总优惠=（当时销售价-成本价）
	 * <p>
	 * Title: sumDiscountCost
	 *
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @throws Exception
	 * @date 2019-11-18 下午08:49:34
	 */
	private double sumDiscountCost(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		// 获取总共的（当时销售价-成本价）
		double sumDeviceCost = 0;
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationtradeId);
		if (userSaleGoods != null && userSaleGoods.size() > 0)
		{
			for (SaleGoodsTradeData userSaleGood : userSaleGoods)
			{
				String resTypeCode = userSaleGood.getResTypeCode();
				if (resTypeCode.equals("4"))
				{
					String goodsNum = userSaleGood.getGoodsNum();
					// 成本价
					String deviceCost = userSaleGood.getDeviceCost();
					// 销售价
					String salePrice = userSaleGood.getRsrvStr6();
					if (deviceCost != null && !deviceCost.trim().equals("") && salePrice != null && !salePrice.trim().equals(""))
					{
						double goodsNumD = Double.parseDouble(goodsNum);
						double devicePriceD = Double.parseDouble(deviceCost) / 100;
						double salePriceD = Double.parseDouble(salePrice) / 100;
						sumDeviceCost = sumDeviceCost + (salePriceD-devicePriceD) * goodsNumD;
					}
				}
			}
		}
		return sumDeviceCost;
	}

	/**
	 * 剩余预存款
	 * <p>
	 * Title: depositeReturnPrice
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-10 下午08:25:47
	 */
	private double depositeReturnPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		// 剩余活动预存款
		double restSaleActiveDeposite = 0;

		// 从账务获取费用信息
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationtradeId);

		if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
		{
			IData feeInfo = AcctCall.newobtainUserAllFeeLeaveFee(sn, actionCode);

			if (IDataUtil.isEmpty(feeInfo))
			{
				return restSaleActiveDeposite;
			}
			String resultCode = feeInfo.getString("RESULT_CODE", "");
			if (!resultCode.equals("0"))
			{
				return restSaleActiveDeposite;
			}
			// 计算违约月份
			IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
			if (IDataUtil.isEmpty(returnFees))
			{
				return restSaleActiveDeposite;
			}
			restSaleActiveDeposite = getFeeFromAcctReturnFee("0", "2", returnFees);
		}

		return restSaleActiveDeposite;
	}

	/**
	 * 活动赠送话费
	 * <p>
	 * Title: giftReturnPrice
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @author XUYT
	 * @throws Exception
	 * @date 2017-3-10 下午07:48:04
	 */
	private double giftReturnPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		double sumDeviceCost = 0;
		// 从账务获取费用信息
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoodsByRelationTradeId(relationtradeId);
		if (userSaleGoods != null && userSaleGoods.size() > 0)
		{
			for (SaleGoodsTradeData userSaleGood : userSaleGoods)
			{
				String resTypeCode = userSaleGood.getResTypeCode();
				if (!resTypeCode.equals("4"))
				{
					String goodsValue = userSaleGood.getGoodsValue();
					String goodsNum = userSaleGood.getGoodsNum();
					if (goodsValue != null && !goodsValue.trim().equals(""))
					{
						double goodsNumD = Double.parseDouble(goodsNum);
						double deviceCostD = (Double.parseDouble(goodsValue) * goodsNumD) / 100;
						sumDeviceCost = sumDeviceCost + deviceCostD;
					}
				}
			}
		}
		return sumDeviceCost;
	}

	/**
	 * 获取月约定最低消费金额
	 * <p>
	 * Title: agreedReturnPrice
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-8 下午03:44:27
	 */
	private double agreedReturnPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		String monthMinCost = "0"; // 获取月约定最低消费金额

		// 获取月约定最低消费金额
		IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfoById(packageId);

		if (IDataUtil.isNotEmpty(packageExtInfos))
		{
			// 月约定最低消费金额
			monthMinCost = packageExtInfos.getData(0).getString("RSRV_STR25", "");

			if (monthMinCost.trim().equals(""))
			{
				monthMinCost = "0";
			}
		} else
		{
			monthMinCost = "0";
		}

		double monthMinCostD = Double.parseDouble(monthMinCost) / 100;
		return monthMinCostD;
	}

	/**
	 * 总签约月份
	 * <p>
	 * Title: Months
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-10 下午07:56:52
	 */
	private int AllMonths(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn, IData map) throws Exception
	{
		String months = "1"; // 总签约月份

		IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
		if (IDataUtil.isNotEmpty(orderSaleActives))
		{
			IData orderSaleActive = orderSaleActives.getData(0);
			// 合约规定的月份
			months = orderSaleActive.getString("MONTHS", "0");
			
		}
		int monthsInt = Integer.parseInt(months);
		if ("1".equals(map.getString("PARA_CODE6", "")))
		{
			monthsInt = Integer.parseInt(map.getString("PARA_CODE7", "0"));
		}
		return monthsInt;
	}

	/**
	 * 违约月份
	 * <p>
	 * Title: defaultMonth
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-8 下午03:56:37
	 */
	private int defaultMonth(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn, IData map) throws Exception
	{
		String months = "0"; // 总签约月份
		int disObeyMonths = 0; // 违约月份

		// 获取违约月份信息
		IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
		if (IDataUtil.isNotEmpty(orderSaleActives))
		{
			IData orderSaleActive = orderSaleActives.getData(0);
			// 合约规定的月份
			months = orderSaleActive.getString("MONTHS", "0");

			if (months.trim().equals(""))
			{
				return disObeyMonths;
			} else
			{
				// 计算已经使用的月份
				String startDate = orderSaleActive.getString("START_DATE");
				String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
				// 计算出违约的月份
				int monthsInt = Integer.parseInt(months);

				if ("1".equals(map.getString("PARA_CODE6", "")))
				{
					monthsInt = Integer.parseInt(map.getString("PARA_CODE7", "0"));
				}

				if (startDate.compareTo(curDate) <= 0)
				{
					disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate);
				} else
				{
					disObeyMonths = monthsInt;
				}
			}
		} else
		{ // 如果未查询到用户已经订购营销活动，就直接返回报错
			return disObeyMonths;
		}

		return disObeyMonths;
	}
	
	/**
	 * 使用月份
	 * <p>
	 * Title: useMonth
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @return
	 * @throws Exception
	 * @author ZHANGXING3
	 * @date 2020-6-12
	 */
	private int useMonth(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn, IData map) throws Exception
	{
		int useMonths = 0; // 使用月份

		// 获取违约月份信息
		IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
		if (IDataUtil.isNotEmpty(orderSaleActives))
		{
			IData orderSaleActive = orderSaleActives.getData(0);
			// 计算已经使用的月份
			String startDate = orderSaleActive.getString("START_DATE");
			String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
			useMonths = SysDateMgr.monthInterval(startDate, curDate);

		} 
		else
		{
			return useMonths;
		}

		return useMonths;
	}

    /**
     * 吉祥号码约消违约月份
     * 把当月算进去
     */
    private int beautifulNumberSaleActiveMonth(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn, IData map) throws Exception
    {
        String months = "0"; // 总签约月份
        int disObeyMonths = 0; // 违约月份

        // 获取违约月份信息
        IDataset orderSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, packageId);
        if (IDataUtil.isNotEmpty(orderSaleActives))
        {
            IData orderSaleActive = orderSaleActives.getData(0);
            // 合约规定的月份
            months = orderSaleActive.getString("MONTHS", "0");

            if (months.trim().equals(""))
            {
                return disObeyMonths;
            } else
            {
                // 计算已经使用的月份
                String startDate = orderSaleActive.getString("START_DATE");
                String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
                // 计算出违约的月份
                int monthsInt = Integer.parseInt(months);

                if ("1".equals(map.getString("PARA_CODE6", "")))
                {
                    monthsInt = Integer.parseInt(map.getString("PARA_CODE7", "0"));
                }

                if (startDate.compareTo(curDate) <= 0)
                {
                    disObeyMonths = monthsInt - SysDateMgr.monthInterval(startDate, curDate) + 1;
                } else
                {
                    disObeyMonths = monthsInt;
                }
            }
        } else
        { // 如果未查询到用户已经订购营销活动，就直接返回报错
            return disObeyMonths;
        }

        return disObeyMonths;
    }

	/**
	 * 总金额
	 * <p>
	 * Title: monthReturnAllPrice
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-4-17 上午11:15:42
	 */
	private double monthReturnAllPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{

		// 计算违约的金额
		double violateReturnMoney = 0;
		// 从账务获取费用信息
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationtradeId);
		if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
		{
			IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

			if (IDataUtil.isEmpty(feeInfo))
			{
				return violateReturnMoney;
			}
			String resultCode = feeInfo.getString("RESULT_CODE", "");
			if (!resultCode.equals("0"))
			{
				return violateReturnMoney;
			}
			// 计算违约月份
			IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
			if (IDataUtil.isEmpty(returnFees))
			{
				return violateReturnMoney;
			}
			// 计算违约的金额
			violateReturnMoney = calculateViolateAllReturnFee(returnFees);
		}

		return violateReturnMoney;
	}

	/**
	 * 已返还话费
	 * <p>
	 * Title: monthReturnPrice
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-10 下午07:30:11
	 */
	private double monthReturnPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{

		// 计算违约的金额
		double violateReturnMoney = 0;
		// 从账务获取费用信息
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationtradeId);
		if (actionCode != null && !actionCode.trim().equals("") && !actionCode.trim().equals("0"))
		{
			IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(sn, actionCode);

			if (IDataUtil.isEmpty(feeInfo))
			{
				return violateReturnMoney;
			}
			String resultCode = feeInfo.getString("RESULT_CODE", "");
			if (!resultCode.equals("0"))
			{
				return violateReturnMoney;
			}
			// 计算违约月份
			IDataset returnFees = feeInfo.getDataset("FEE_INFOS");
			if (IDataUtil.isEmpty(returnFees))
			{
				return violateReturnMoney;
			}
			// 计算违约的金额
			violateReturnMoney = calculateViolateReturnFee(returnFees);
		}

		return violateReturnMoney;
	}
	
	/**
	 * 客户预存话费未返还金额
	 * <p>Title: widedepositeReturnPrice</p>
	 * <p>Description: </p>
	 * <p>Company: AsiaInfo</p>
	 * @param endSaleActiveInfos
	 * @param userId
	 * @param productId
	 * @param packageId
	 * @param relationtradeId
	 * @param sn
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-7-13 上午09:49:13
	 */
	private double widedepositeReturnPrice(IDataset endSaleActiveInfos, String userId, String productId, String packageId, String relationtradeId, String sn) throws Exception
	{
		// 客户预存话费未返还金额
		double restSaleActiveDeposite = 0;

		// 获取客户预存话费未返还金额
		IDataset AcctDeposit = AcctCall.queryAccountDepositBySn(sn);
		if (IDataUtil.isNotEmpty(AcctDeposit))
		{
			for (int i = 0; i < AcctDeposit.size(); i++)
			{
				IData deposit = AcctDeposit.getData(i);
				String trade_fee = deposit.getString("DEPOSIT_BALANCE", "");
				String deposit_code = deposit.getString("DEPOSIT_CODE", "");
				if ("362".equals(deposit_code))
				{
					restSaleActiveDeposite = Double.parseDouble(trade_fee) / 100;
				}
			}
		}

		return restSaleActiveDeposite;
	}
	
	public IData queryCreditPurchases(IData params) throws Exception
	{
		IData result = new DataMap();
		result.put("QUERY_CREDIT_PURCHASES", "1");
        //查询tf_b_trade_other信用购机记录
		String relationTradeId = params.getString("RELATION_TRADE_ID");
		if(StringUtils.isNotEmpty(relationTradeId)){
			IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(relationTradeId, "CREDIT_PURCHASES");
			if(DataUtils.isNotEmpty(otherDatas)){
          		String serialNumber =  params.getString("SERIAL_NUMBER");
          		String seq = otherDatas.getData(0).getString("RSRV_STR9");//比较seq
      			String mplOrdNo = otherDatas.getData(0).getString("RSRV_STR1");
      			String mplOrdDt = otherDatas.getData(0).getString("RSRV_STR2");
      			IData  inparam = new DataMap();
      	   	 	inparam.put("procTyp", "2");//信用购机  退货
      	   	 	inparam.put("cusMblNo", serialNumber);
      	   	 	inparam.put("ExSeq", seq);
      	   	 	inparam.put("mplOrdNo", mplOrdNo);
      	   	 	inparam.put("mplOrdDt", mplOrdDt);
      	   	 	
      	   	 	if(StringUtils.isNotEmpty(serialNumber) &&  StringUtils.isNotEmpty(seq) && StringUtils.isNotEmpty(mplOrdNo) && StringUtils.isNotEmpty(mplOrdDt)){
      	   	 		
      	   	 		//调用IBOSS信用购机退货/撤单查询
          	   	 	IDataset cancelInfos = IBossCall.getCancelWholeNetCreditPurchasesInfo(inparam);
          	   	 	if(DataUtils.isNotEmpty(cancelInfos)){
          	   	 		IData cancelInfo=cancelInfos.getData(0);
          	   	 		IDataset rspInfo = cancelInfo.getDataset("REG_REJ_RSP"); 
          	   	 		if(DataUtils.isNotEmpty(rspInfo)){
	          	   	 		IData REG_REJ_RSP = rspInfo.first();
		          	   	 	String RspCode=REG_REJ_RSP.getString("RSP_CODE");
		          	   	 	if(!"0000".equals(RspCode)){
		          	   	 		result.put("QUERY_CREDIT_PURCHASES", "0");
		          	   	 	} else {
		          	   	 		result.put("QUERY_CREDIT_PURCHASES", "1");
		                    }
          	   	 		}else{
          	   	 			result.put("QUERY_CREDIT_PURCHASES", "0");
          	   	 		}
	          	   	 	
          	   	 	}else{
          	   	 		result.put("QUERY_CREDIT_PURCHASES", "0");
          	   	 	}
          	   	 	
          	   	 	
      	   	 	}
      	   	 	
      	   	 	
          	}
          }
		return result;
	}

	private boolean isNumber (String str){
		Pattern pattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
		Matcher matcher = pattern.matcher(str);
		if (!matcher.matches()){// 不匹配，不是数字
			return false;
		}else {
			return true;
		}
	}

}
