
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化
 * @author: xiaocl
 */
public class CheckElementLimitByAdd implements IElementLimitByAdd
{

    private static Logger logger = Logger.getLogger(CheckElementLimitByAdd.class);

    @Override
	public void checkAllElementLimitAdd(IData databus, IDataset listTradeElement, IDataset listUserAllElement, boolean bIsPkgInsideElmentLimit, CheckProductData checkProductData) throws Exception
	{

		if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
			logger.debug("rule 进入 prodcheck CheckElementLimitByAdd() 函数 listTradeElement.size = [" + listTradeElement.size() + "]");

		String strElementType = "", strKeyId = "", strKeyName = "";

		int irlCount = 0;

		String strtpModifyTag = "", strtpElementId = "", strtpStartDate = "", strtpEndDate = "", strtpProductId = "", strtpPackageId = "", strtpElementTypeCode = "";
		String strueModifyTag = "", strueElementId = "", strueStartDate = "", strueEndDate = "", strueElementTypeCode = "", strueProductId = "", struePackageId = "";
		String strrlElementId = "", strrlElementTypeCode = "";

		boolean bCurMonth = false, bNextMonth = false;

		IDataset listEvERelationLimit ;

		int iCountTradeElement = listTradeElement.size();
		for (int itpIdx = 0; itpIdx < iCountTradeElement; itpIdx++)
		{
			if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
				logger.debug("rule prodcheck CheckElementLimitByAdd 进入到第一层循环");

			IData tradeElement = listTradeElement.getData(itpIdx);
			strtpModifyTag = tradeElement.getString("MODIFY_TAG");
			strtpElementId = tradeElement.getString("ELEMENT_ID");
			strtpStartDate = tradeElement.getString("START_DATE");
			strtpEndDate = tradeElement.getString("END_DATE");
			strtpProductId = tradeElement.getString("PRODUCT_ID","");
			strtpPackageId = tradeElement.getString("PACKAGE_ID","");
			strtpElementTypeCode = tradeElement.getString("ELEMENT_TYPE_CODE");

			if (strtpElementTypeCode.equals("S"))
			{
				strElementType = "服务";
				strKeyName = "ServiceName";
				strKeyId = "SERVICE_ID";
			}
			else if (strtpElementTypeCode.equals("D"))
			{
				strElementType = "优惠";
				strKeyName = "DiscntName";
				strKeyId = "DISCNT_CODE";
			}

			if (!listTradeElement.getData(itpIdx).getString("USER_ID").equals(checkProductData.getUserId()))
			{
				if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
					logger.debug("rule prodcheck CheckElementLimitByAdd 服务表 user_id 和 台账表user_id 不相同, continue 跳过第一层循环");

				continue;
			}

			bCurMonth = (strtpStartDate.compareTo(checkProductData.getStrLastDayOfCurMonth()) <= 0); // 是否需要判断当月
			bNextMonth = (strtpEndDate.compareTo(checkProductData.getStrLastDayOfCurMonth()) > 0); // 是否需要判断下月

			// 1 新增 || 优惠的修改
			if (strtpModifyTag.equals("0") || (strtpElementTypeCode.equals("D") && strtpModifyTag.equals("2") && !checkProductData.getTradeTypeCode().equals("310")))
			{

				if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
					logger.debug("rule prodcheck CheckElementLimitByAdd 进入到新增操作的判断");

				if (strtpElementTypeCode.equals("D")) // 320业务判断后
				{
					if (!listTradeElement.getData(itpIdx).getString("USER_ID").equals(checkProductData.getUserId()))
					{
						if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
							logger.debug("rule prodcheck CheckElementLimitByAdd 资费表 user_id 和 台账 user_id 不同, continue 调过一次循环");
						continue;
					}
				}

				// 1.1 互斥
				
				listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "0", strtpElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
				
				if (IDataUtil.isNotEmpty(listEvERelationLimit))
				{
				    irlCount = listEvERelationLimit.size();
				    
					if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
						logger.debug("rule prodcheck CheckElementLimitByAdd 进入到[" + strtpElementTypeCode + "][" + strtpElementId + "][" + listTradeElement.getData(itpIdx) + "]的互斥判断逻辑");

					int iCountUserAllElement = listUserAllElement.size();
					for (int iueIdx = 0; iueIdx < listUserAllElement.size(); iueIdx++)
					{
						IData userAllElement = listUserAllElement.getData(iueIdx);
						strueModifyTag = userAllElement.getString("MODIFY_TAG");
						strueElementId = userAllElement.getString("ELEMENT_ID");
						strueStartDate = userAllElement.getString("START_DATE");
						strueEndDate = userAllElement.getString("END_DATE");
						strueProductId = userAllElement.getString("PRODUCT_ID","");
						struePackageId = userAllElement.getString("PACKAGE_ID","");
						strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");

						// 如果是当前自己本条记录, 则不判断
						if (strueElementTypeCode.equals(strtpElementTypeCode)
								&& strtpElementId.equals(strueElementId)
								&& (listTradeElement.getData(itpIdx).getString("INST_ID").equals(listUserAllElement.getData(iueIdx).getString("INST_ID")) && (strueModifyTag.equals("0") || strueModifyTag.equals("2"))
										&& strtpStartDate.equals(strueStartDate) && !checkProductData.getTradeTypeCode().equals("152")))
						{
							continue;
						}
						/*
						 * add by pengzq at 2011-04-28 原因: 如果前台trade_discnt.modify_tag ='2'， 这时取出来的 strueModifyTag 是'A'
						 * 会导致前台报错，所以进行特殊处理
						 */
						if (strueElementTypeCode.equals(strtpElementTypeCode)
								&& strtpElementId.equals(strueElementId)
								&& (listTradeElement.getData(itpIdx).getString("INST_ID").equals(listUserAllElement.getData(iueIdx).getString("INST_ID")) && (strueModifyTag.equals("0") || strueModifyTag.equals("2") || strueModifyTag.equals("A"))
										&& strtpStartDate.equals(strueStartDate) && checkProductData.getTradeTypeCode().equals("152")))
						{
							continue;
						}

						for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
						{
							IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
							strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
							strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

							// 判断特殊优惠编码
							if (strtpElementTypeCode.equals("D") && strrlElementTypeCode.equals("D") && !checkProductData.getSpeDiscntCode().equals("") && checkProductData.getSpeDiscntCode().indexOf(("|" + strrlElementId + "|")) > -1)
							{
								continue;
							}

							if (strueElementTypeCode.equals(strrlElementTypeCode)
									&& strueElementId.equals(strrlElementId)
									&& strueEndDate.compareTo(strueStartDate) > 0
									&& ((strueEndDate.compareTo(strtpStartDate) > 0 && strueStartDate.compareTo(strtpStartDate) <= 0) || (strueStartDate.compareTo(strtpStartDate) >= 0 && strueStartDate.compareTo(strtpEndDate) <= 0 && strueEndDate
											.compareTo(strtpStartDate) >= 0)))
							{
								String strELeNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);

								String strElementTypeB = "", strKeyColumn = "";

								if (strrlElementTypeCode.equals("S"))
								{
									strElementTypeB = "服务";
									strKeyColumn = "ServiceName";
								}
								else if (strrlElementTypeCode.equals("D"))
								{
									strElementTypeB = "优惠";
									strKeyColumn = "DiscntName";
								}
								String strELeNameB = BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId);

								String strError = null;

								strError = "#产品依赖互斥判断:当前订购|" + strElementType + "|" + strtpElementId + "|" + strELeNameA + "|与|" + strElementTypeB + "|" + strrlElementId + "|" + strELeNameB + "|互斥，不能同时生效，业务不能继续办理！";

								if(strtpElementTypeCode.equals(strueElementTypeCode) && strtpElementId.equals(strueElementId))
								{
									strError = "尊敬的客户，您已订购【" + strElementTypeB + "】[" + strrlElementId + "|" + strELeNameB + "]，无需重复办理！";
								}
								
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201506", strError);
							}
						}
					}
				}

				// 如果新增元素是当前时间，与之互斥的元素截止到月底，则不判，其它还是判
				listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "5", strtpElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
				
				if (IDataUtil.isNotEmpty(listEvERelationLimit))
				{
				    irlCount = listEvERelationLimit.size();
				    
					if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
						logger.debug("rule prodcheck CheckElementLimitByAdd 进入到[" + strtpElementTypeCode + "][" + strtpElementId + "][" + listTradeElement.getData(itpIdx) + "]的互斥判断逻辑");

					int iCountUserAllElement = listUserAllElement.size();
					for (int iueIdx = 0; iueIdx < listUserAllElement.size(); iueIdx++)
					{
						IData userAllElement = listUserAllElement.getData(iueIdx);
						strueModifyTag = userAllElement.getString("MODIFY_TAG");
						strueElementId = userAllElement.getString("ELEMENT_ID");
						strueStartDate = userAllElement.getString("START_DATE");
						strueEndDate = userAllElement.getString("END_DATE");
						strueProductId = userAllElement.getString("PRODUCT_ID","");
						struePackageId = userAllElement.getString("PACKAGE_ID","");
						strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");

						// 如果是当前自己本条记录, 则不判断
						if (strueElementTypeCode.equals(strtpElementTypeCode)
								&& strtpElementId.equals(strueElementId)
								&& (listTradeElement.getData(itpIdx).getString("INST_ID").equals(listUserAllElement.getData(iueIdx).getString("INST_ID")) && (strueModifyTag.equals("0") || strueModifyTag.equals("2"))
										&& strtpStartDate.equals(strueStartDate) && !checkProductData.getTradeTypeCode().equals("152")))
						{
							continue;
						}
						/*
						 * add by pengzq at 2011-04-28 原因: 如果前台trade_discnt.modify_tag ='2'， 这时取出来的 strueModifyTag 是'A'
						 * 会导致前台报错，所以进行特殊处理
						 */
						if (strueElementTypeCode.equals(strtpElementTypeCode)
								&& strtpElementId.equals(strueElementId)
								&& (listTradeElement.getData(itpIdx).getString("INST_ID").equals(listUserAllElement.getData(iueIdx).getString("INST_ID")) && (strueModifyTag.equals("0") || strueModifyTag.equals("2") || strueModifyTag.equals("A"))
										&& strtpStartDate.equals(strueStartDate) && checkProductData.getTradeTypeCode().equals("152")))
						{
							continue;
						}

						for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
						{
							IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
							strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
							strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

							// 判断特殊优惠编码
							if (strtpElementTypeCode.equals("D") && strrlElementTypeCode.equals("D") && !checkProductData.getSpeDiscntCode().equals("") && checkProductData.getSpeDiscntCode().indexOf(("|" + strrlElementId + "|")) > -1)
							{
								continue;
							}

							if (strueElementTypeCode.equals(strrlElementTypeCode)
									&& strueElementId.equals(strrlElementId)
									&& strueEndDate.compareTo(strueStartDate) > 0
									&& ((strueEndDate.compareTo(strtpStartDate) > 0 && strueStartDate.compareTo(strtpStartDate) <= 0) || (strueStartDate.compareTo(strtpStartDate) >= 0 && strueStartDate.compareTo(strtpEndDate) <= 0 && strueEndDate
											.compareTo(strtpStartDate) >= 0)))
							{

								if (strtpStartDate.compareTo(checkProductData.getStrLastDayOfCurMonth()) <= 0 && strueEndDate.compareTo(checkProductData.getStrFirstDayOfNextMonth()) < 0)
								{
									continue;
								}
								String strELeNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);

								String strElementTypeB = "", strKeyColumn = "";

								if (strrlElementTypeCode.equals("S"))
								{
									strElementTypeB = "服务";
									strKeyColumn = "ServiceName";
								}
								else if (strrlElementTypeCode.equals("D"))
								{
									strElementTypeB = "优惠";
									strKeyColumn = "DiscntName";
								}
								String strELeNameB = BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId);

								String strError = null;

								strError = "#产品依赖互斥判断:当前订购|" + strElementType + "|" + strtpElementId + "|" + strELeNameA + "|与|" + strElementTypeB + "|" + strrlElementId + "|" + strELeNameB + "|互斥，不能同时生效，业务不能继续办理！";

								if(strtpElementTypeCode.equals(strueElementTypeCode) && strtpElementId.equals(strueElementId))
								{
									strError = "尊敬的客户，您已订购【" + strElementTypeB + "】[" + strrlElementId + "|" + strELeNameB + "]，无需重复办理！";
								}
								
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201506", strError);
							}
						}
					}
				}

				// 1.2 部分依赖
				listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "1", strtpElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
				
				if (IDataUtil.isNotEmpty(listEvERelationLimit))
				{
				    irlCount = listEvERelationLimit.size();
				    
					/*
					 * A 部分依赖 B C 的情况下：终止B,新增C，B、C之间相差一秒，这种情况下，根据A的生命周期，分当月和 下月拆开来判断
					 */
					boolean bCurMonthFinded = false, bNextMonthFinded = false, bFianlFinded = false;
					for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
					{
						IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
						strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
						strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

						int iCountUserAllElement = listUserAllElement.size();
						for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
						{
							IData userAllElement = listUserAllElement.getData(iueIdx);
							strueModifyTag = userAllElement.getString("MODIFY_TAG");
							strueElementId = userAllElement.getString("ELEMENT_ID");
							strueStartDate = userAllElement.getString("START_DATE");
							strueEndDate = userAllElement.getString("END_DATE");
							strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
							struePackageId = userAllElement.getString("PACKAGE_ID","");

							//strueEndDate = (strueEndDate.length() == 10)?(strueEndDate + " 23:59:59"):strueEndDate;
							//strtpEndDate = (strtpEndDate.length() == 10)?(strtpEndDate + " 23:59:59"):strtpEndDate;
							//strueStartDate = (strueStartDate.length() == 10)?(strueStartDate + " 00:00:00"):strueStartDate;
							//strtpStartDate = (strtpStartDate.length() == 10)?(strtpStartDate + " 00:00:00"):strtpStartDate;
							//String StrFirstDayOfNextMonth = checkProductData.getStrFirstDayOfNextMonth();
							//StrFirstDayOfNextMonth = (StrFirstDayOfNextMonth.length() == 10)?(StrFirstDayOfNextMonth + " 00:00:00"):StrFirstDayOfNextMonth;
							//发现存在一种情况，如果用户存在本月底失效的优惠，strueEndDate取到的是年月日，没有时分秒，即2017-08-31，而checkProductData.getStrLastDayOfCurMonth()是年月日时分秒，比较存在问题
							//strueEndDate最原始的来源是SelectedElementSVC.java getUserElements queryUserDiscntsInSelectedElements 的SEL_USER_SVC_IN_SELECTED，不能轻易加时分秒，因为后面还有代码去追加了时分秒
							//所以做了修改，如果strueEndDate取到的是年月日，则追加时分秒再去比较，否则原值比较
							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueStartDate.compareTo(strtpStartDate) <= 0 && ((strueEndDate.length()==10)?(strueEndDate+" 23:59:59"):strueEndDate).compareTo(checkProductData.getStrLastDayOfCurMonth()) >= 0
									&& !"1".equals(strueModifyTag))
							{
								bCurMonthFinded = true;
							}
							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueEndDate.compareTo(strtpEndDate) >= 0 
							 && (strueStartDate = (strueStartDate.length() == 10)?(strueStartDate + " 00:00:00"):strueStartDate).compareTo(((checkProductData.getStrFirstDayOfNextMonth().length() == 10)?(checkProductData.getStrFirstDayOfNextMonth() + " 00:00:00"):checkProductData.getStrFirstDayOfNextMonth())) <= 0
							 && !"1".equals(strueModifyTag))
							{
								bNextMonthFinded = true;
							}
						}

						if ((bCurMonth && !bCurMonthFinded) || (bNextMonth && !bNextMonthFinded))
						{
							bFianlFinded = false;
						}
						else
						{
							bFianlFinded = true;
						}

						if (bFianlFinded)
						{
							break;
						}
					}
					if (!bFianlFinded)
					{
						String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
						String strName = "", strKeyColumn = "", strNameB = "";

						for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
						{
							strrlElementId = listEvERelationLimit.getData(irlIdx).getString("ELEMENT_ID_B");
							strrlElementTypeCode = listEvERelationLimit.getData(irlIdx).getString("ELEMENT_TYPE_CODE_B");

							if (strrlElementTypeCode.equals("S"))
							{
								strName = "服务";
								strKeyColumn = "ServiceName";
							}
							else if (strrlElementTypeCode.equals("D"))
							{
								strName = "优惠";
								strKeyColumn = "DiscntName";
							}

							strNameB += "|" + strName + "|" + strrlElementId + "|" + (BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId)) + "|";
							if (irlIdx < irlCount - 1)
							{
								strNameB += " 或 ";
							}
						}

						String strError = "#产品依赖互斥判断:新增|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能生效，因为它所依赖的" + strNameB + "不存在，业务不能继续办理！";

						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201507", strError);
					}
				}

				// 1.3 完全依赖
				listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "2", strtpElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
				
				if (IDataUtil.isNotEmpty(listEvERelationLimit))
				{
				    irlCount = listEvERelationLimit.size();
				    
					for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
					{
						boolean bCurMonthFinded = false, bNextMonthFinded = false;
						IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
						strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
						strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

						int iCountUserAllElement = listUserAllElement.size();
						for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
						{
							IData userAllElement = listUserAllElement.getData(iueIdx);
							strueModifyTag = userAllElement.getString("MODIFY_TAG");
							strueElementId = userAllElement.getString("ELEMENT_ID");
							strueStartDate = userAllElement.getString("START_DATE");
							strueEndDate = userAllElement.getString("END_DATE");
							strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
							struePackageId = userAllElement.getString("PACKAGE_ID","");

							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueStartDate.compareTo(strtpStartDate) <= 0 && strueEndDate.compareTo(checkProductData.getCurDate()) > 0)
							{
								bCurMonthFinded = true;
							}else{
								//查不出一卡多终端产品绑定22和902为什么开始时间不一致，特殊处理通过
								if(strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueStartDate.compareTo(strtpStartDate) >= 0 && strueEndDate.compareTo(checkProductData.getCurDate()) > 0 && "20170998".equals(checkProductData.getProductId()))
								{
									bCurMonthFinded = true;
								}
							}
							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueEndDate.compareTo(strtpEndDate) >= 0 && strueEndDate.compareTo(checkProductData.getStrFirstDayOfNextMonth()) > 0)
							{
								bNextMonthFinded = true;
							}
						}

						if ((bCurMonth && !bCurMonthFinded) || (bNextMonth && !bNextMonthFinded))
						{
							String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
							String strName = "", strKeyColumn = "", strNameB = "";

							if (strrlElementTypeCode.equals("S"))
							{
								strName = "服务";
								strKeyColumn = "ServiceName";
							}
							else if (strrlElementTypeCode.equals("D"))
							{
								strName = "优惠";
								strKeyColumn = "DiscntName";
							}
							strNameB = BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId);

							String strError = "#产品依赖互斥判断:新增|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能生效, 因为她所依赖的|" + strName + "|" + strrlElementId + "|" + strNameB + "|不存在, 业务不能继续办理";

							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201508", strError);
						}
					}
				}
				
				// 1.2 部分依赖,包括预约优惠  @yanwu
				listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "3", strtpElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
				
				if (IDataUtil.isNotEmpty(listEvERelationLimit))
				{
				    irlCount = listEvERelationLimit.size();
				    
					/*
					 * A 部分依赖 B C 的情况下：B永久生效
					 */
					boolean bCurMonthFinded = false, bNextMonthFinded = false, bFianlFinded = false;
					for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
					{
						IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
						strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
						strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

						int iCountUserAllElement = listUserAllElement.size();
						for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
						{
							IData userAllElement = listUserAllElement.getData(iueIdx);
							strueModifyTag = userAllElement.getString("MODIFY_TAG");
							strueElementId = userAllElement.getString("ELEMENT_ID");
							strueStartDate = userAllElement.getString("START_DATE");
							strueEndDate = userAllElement.getString("END_DATE");
							strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
							struePackageId = userAllElement.getString("PACKAGE_ID","");

							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueStartDate.compareTo(strueEndDate) < 0 && strueEndDate.compareTo(checkProductData.getStrLastDayOfCurMonth()) >= 0
									&& !"1".equals(strueModifyTag))
							{
								bCurMonthFinded = true;
							}
							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueEndDate.compareTo(strtpEndDate) >= 0
									&& !"1".equals(strueModifyTag))
							{
								bNextMonthFinded = true;
							}
						}

						if ((bCurMonth && !bCurMonthFinded) || (bNextMonth && !bNextMonthFinded))
						{
							bFianlFinded = false;
						}
						else
						{
							bFianlFinded = true;
						}

						if (bFianlFinded)
						{
							break;
						}
					}
					if (!bFianlFinded)
					{
						String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
						String strName = "", strKeyColumn = "", strNameB = "";

						for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
						{
							strrlElementId = listEvERelationLimit.getData(irlIdx).getString("ELEMENT_ID_B");
							strrlElementTypeCode = listEvERelationLimit.getData(irlIdx).getString("ELEMENT_TYPE_CODE_B");

							if (strrlElementTypeCode.equals("S"))
							{
								strName = "服务";
								strKeyColumn = "ServiceName";
							}
							else if (strrlElementTypeCode.equals("D"))
							{
								strName = "优惠";
								strKeyColumn = "DiscntName";
							}

							strNameB += "|" + strName + "|" + strrlElementId + "|" + (BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId)) + "|";
							//strNameB += "[" + strrlElementId + "|" + (BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId)) + "]";
							if (irlIdx < irlCount - 1)
							{
								strNameB += " 或 ";
							}
						}

						String strError = "#产品依赖互斥判断:新增|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能生效，因为它所依赖的" + strNameB + "不存在，业务不能继续办理！";

						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201507", strError);
					}
				}
				

				// 1.4 和部分依赖判断基本一样，只需要被依赖的优惠当时有效并且结束日期大于等于新申请优惠的开始日期即可
				listEvERelationLimit = BreQryForProduct.tacGetAllEleVsEleLimit(strtpPackageId, strtpElementId, "6", strtpElementTypeCode, "A", checkProductData.getEparchyCode(), bIsPkgInsideElmentLimit);
				
				if (IDataUtil.isNotEmpty(listEvERelationLimit))
				{
				    irlCount = listEvERelationLimit.size();
				    
					boolean bfinded = false;

					for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
					{
						IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
						strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
						strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

						int iCountUserAllElement = listUserAllElement.size();
						for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
						{
							IData userAllElement = listUserAllElement.getData(iueIdx);
							strueModifyTag = userAllElement.getString("MODIFY_TAG");
							strueElementId = userAllElement.getString("ELEMENT_ID");
							strueStartDate = userAllElement.getString("START_DATE");
							strueEndDate = userAllElement.getString("END_DATE");
							strueElementTypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");
							struePackageId = userAllElement.getString("PACKAGE_ID","");

							if (strrlElementTypeCode.equals(strueElementTypeCode) && strrlElementId.equals(strueElementId) && strueEndDate.compareTo(checkProductData.getCurDate()) >= 0 && strueEndDate.compareTo(strtpStartDate) >= 0
									&& strueEndDate.compareTo(strueStartDate) >= 0)
							{
								bfinded = true;
								break;
							}
						}
						if (bfinded)
							break;
					}
					if (!bfinded)
					{
						String strNameA = BreQueryHelp.getNameByCode(strKeyName, strtpElementId);
						String strName = "", strKeyColumn = "", strNameB = "";

						for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
						{
							IData eveRelationLimit = listEvERelationLimit.getData(irlIdx);
							strrlElementId = eveRelationLimit.getString("ELEMENT_ID_B");
							strrlElementTypeCode = eveRelationLimit.getString("ELEMENT_TYPE_CODE_B");

							if (strrlElementTypeCode.equals("S"))
							{
								strName = "服务";
								strKeyColumn = "ServiceName";
							}
							else if (strrlElementTypeCode.equals("D"))
							{
								strName = "优惠";
								strKeyColumn = "DiscntName";
							}

							strNameB += "|" + strName + "|" + strrlElementId + "|" + (BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId)) + "|";
							//strNameB += "[" + strrlElementId + "|" + (BreQueryHelp.getNameByCode(strKeyColumn, strrlElementId)) + "]";
							if (irlIdx < irlCount - 1)
							{
								strNameB += " 或 ";
							}
						}

						if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
							logger.debug("rule  退出 prodcheck CheckElementLimitByAdd() 函数 listTradeElement.size = [" + listTradeElement.size() + "]");

						String strError = "#产品依赖互斥判断:新增|" + strElementType + "|" + strtpElementId + "|" + strNameA + "|不能生效，因为它所依赖的" + strNameB + "不存在，业务不能继续办理！";

						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201509", strError);
					}
				}
			}
		}
	}
}
