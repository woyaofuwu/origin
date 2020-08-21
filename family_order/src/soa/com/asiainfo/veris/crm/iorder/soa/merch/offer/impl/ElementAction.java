package com.asiainfo.veris.crm.iorder.soa.merch.offer.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.offer.DisposeAction;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UElementLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDateExtend;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class ElementAction implements DisposeAction
{

	@Override
	public IDataset execute(IData data) throws Exception
	{
		// 对于元素的处理，如果用户有下月生效的产品，则产品组件展示的是下月生效的产品包元素，如果元素在用户的原产品下有，则以原产品的生效方式为准
		// 如果元素在用户的原产品下没有，则以新产品的生效方式为准
		String eparchyCode = data.getString("EPARCHY_CODE");

		IDataset elements = new DatasetList(data.getString("OFFERS"));
		UcaData uca = UcaDataFactory.getUcaByUserId(data.getString("USER_ID"));

		dealState(elements, uca);

		dealOfferTime(data, elements, uca);

		dealOfferAttrs(elements, eparchyCode);

		boolean isCheckRule = BizEnv.getEnvBoolean("MERCH_OFFER_CHECK_RULE", false);

		if (isCheckRule)
		{
			checkRule(data, elements);
		}
		return elements;
	}

	private void dealState(IDataset elements, UcaData uca) throws Exception
	{
		String lastDateMonth = SysDateMgr.getLastDateThisMonth4WEB();

		for (int i = 0; i < elements.size(); i++)
		{
			IData element = elements.getData(i);
			String tagValue = element.getString("TAG_VALUE");
			if ("USED".equals(tagValue) || "ORDER".equals(tagValue))// 已订购或者购物车
			{
				String orderMode = element.getString("ORDER_MODE");
				if (!"R".equals(orderMode))
				{
					if ("C".equals(orderMode))
					{
						String endDate = "";
						if ("D".equals(element.getString("OFFER_TYPE")))
						{
							List<DiscntTradeData> discnts = uca.getUserDiscntByDiscntId(element.getString("OFFER_CODE"));
							if (discnts.size() > 0)
							{
								endDate = discnts.get(0).getEndDate();
							}
						}
						else if ("S".equals(element.getString("OFFER_TYPE")))
						{
							List<SvcTradeData> svcs = uca.getUserSvcBySvcId(element.getString("OFFER_CODE"));
							if (svcs.size() > 0)
							{
								endDate = svcs.get(0).getEndDate();
							}
						}
						if (!"".equals(endDate) && SysDateMgr.getDayIntervalNoAbs(lastDateMonth, endDate) <= 0)
						{
							continue;
						}
						element.put("ERROR_INFO", "结束时间大于本月最后一天不能续订！");
					}
					element.put("DISABLED", "true");
				}
			}
			
//			String elementId = element.getString("OFFER_CODE");
//			boolean isPriv = true;
//			if ("D".equals(element.getString("OFFER_TYPE")))
//			{
//				isPriv = StaffPrivUtil.isDistPriv(CSBizBean.getVisit().getStaffId(), elementId);
//			}else if("S".equals(element.getString("OFFER_TYPE")))
//			{
//				isPriv = StaffPrivUtil.isSvcPriv(CSBizBean.getVisit().getStaffId(), elementId);
//			}
//			if(!isPriv)
//			{
//				element.put("DISABLED", "true");
//				element.put("TAG_VALUE", "无权限");
//			}
		}
	}

	private void checkRule(IData data, IDataset elements) throws Exception
	{
		String userId = data.getString("USER_ID");
		String eparchyCode = data.getString("EPARCHY_CODE");
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "110");

		IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
		if (IDataUtil.isNotEmpty(userDiscntElements))
		{
			for (int i = 0; i < userDiscntElements.size(); i++)
			{
				IData ruleElement = userDiscntElements.getData(i);
				ruleElement.put("USER_ID_A", "-1");// 给规则用
				ruleElement.put("USER_ID", userId);
				ruleElement.put("DISCNT_CODE", ruleElement.getString("ELEMENT_ID"));
			}
		}
		IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
		if (IDataUtil.isNotEmpty(userSvcElements))
		{
			for (int i = 0; i < userSvcElements.size(); i++)
			{
				IData ruleElement = userSvcElements.getData(i);
				ruleElement.put("USER_ID_A", "-1");// 给规则用
				ruleElement.put("USER_ID", userId);
				ruleElement.put("SERVICE_ID", ruleElement.getString("ELEMENT_ID"));
			}
		}
		// 模拟拼产品台账
		IDataset tradeProducts = getTradeProducts(data);

		// 规则调用
		IDataset tradeMains = new DatasetList();
		IData tradeMain = new DataMap();
		tradeMain.put("TRADE_EPARCHY_CODE", eparchyCode);
		tradeMain.put("TRADE_TYPE_CODE", tradeTypeCode);
		tradeMain.put("IN_MODE_CODE", "0");
		tradeMain.put("USER_ID", userId);
		tradeMains.add(tradeMain);
		IData ruleParam = new DataMap();
		ruleParam.put("TF_F_USER_PRODUCT_AFTER", tradeProducts);
		ruleParam.put("IS_COMPONENT", "true");
		ruleParam.put("TF_B_TRADE", tradeMains);
		ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscntElements);
		ruleParam.put("TF_F_USER_SVC_AFTER", userSvcElements);

		int size = elements.size();

		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);

			String disabled = element.getString("DISABLED", "FALSE");
			if ("true".equals(disabled))
				continue;
//			if (checkAntinomy(element, userDiscntElements, userSvcElements))
//			{
//				continue;
//			}
			ruleParam.remove("TF_B_TRADE_DISCNT");
			ruleParam.remove("TF_B_TRADE_SVC");
			IDataset tradeElement = getTradeElement(element, userId, i);
			if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
			{
				ruleParam.put("TF_B_TRADE_DISCNT", tradeElement);
			}
			else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
			{
				ruleParam.put("TF_B_TRADE_SVC", tradeElement);
			}
			else
			{
				continue;

			}
			IData result = BizRule.bre4ProductLimitNeedFormat(ruleParam);
			if (IDataUtil.isNotEmpty(result))
			{
				IDataset errors = result.getDataset("TIPS_TYPE_ERROR");
				if (IDataUtil.isNotEmpty(errors))
				{
					if (errors.size() == 1)
					{
						IData err = errors.first();
						if ("201564".equals(err.getString("TIPS_CODE")) && BreFactory.TIPS_TYPE_ERROR == err.getInt("TIPS_TYPE"))
						{
							continue;
						}
					}
					StringBuffer sb = new StringBuffer();
					for (int j = 0; j < errors.size(); j++)
					{
						sb.append(errors.getData(j).getString("TIPS_INFO"));
					}
					element.put("ERROR_INFO", sb.toString());
					element.put("DISABLED", "true");

				}
			}
		}
	}

	/**
	 * 26参数
	 * 
	 * @param antinomyOffers
	 * @param userDiscntElements
	 * @param userSvcElements
	 * @return
	 * @throws Exception
	 */
	private boolean checkAntinomy(IData element, IDataset userDiscntElements, IDataset userSvcElements) throws Exception
	{
		IDataset antinomyOffers = CommparaInfoQry.getCommparaInfoByCode("CSM", "26", element.getString("OFFER_CODE"), element.getString("OFFER_TYPE"), CSBizBean.getUserEparchyCode());
		if (IDataUtil.isNotEmpty(antinomyOffers))
		{
			for (int j = 0; j < antinomyOffers.size(); j++)
			{
				IData antinomyOffer = antinomyOffers.getData(j);
				String relOfferCode = antinomyOffer.getString("PARA_CODE2");
				String relOfferType = antinomyOffer.getString("PARA_CODE3", "");
				if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
				{
					for (int k = 0; k < userDiscntElements.size(); k++)
					{
						if (relOfferCode.equals(userDiscntElements.getData(k).getString("DISCNT_CODE")))
						{
							return true;
						}
					}
				}
				if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_SVC))
				{
					for (int k = 0; k < userSvcElements.size(); k++)
					{
						if (relOfferCode.equals(userSvcElements.getData(k).getString("SERVICE_ID")))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private IDataset getTradeProducts(IData data) throws Exception
	{
		String nextProductId = data.getString("NEXT_PRODUCT_ID");
		String userProductId = data.getString("USER_PRODUCT_ID");
		String effectNow = data.getString("EFFECT_NOW", "");

		IDataset tradeProducts = new DatasetList();
		UcaData uca = UcaDataFactory.getUcaByUserId(data.getString("USER_ID"));

		if (uca != null)
		{
			if (StringUtils.isEmpty(nextProductId))
			{
				List<ProductTradeData> productList = uca.getUserProduct(userProductId);
				ProductTradeData oldTradeProduct = null;
				if (productList != null && productList.size() > 0)
				{
					oldTradeProduct = productList.get(0);
				}
				else
				{
					CSAppException.apperr(ProductException.CRM_PRODUCT_19, userProductId);
				}
				tradeProducts.add(oldTradeProduct.toData());
			}
			else
			{
				List<ProductTradeData> productList = uca.getUserProduct(userProductId);
				ProductTradeData oldTradeProduct = new ProductTradeData();
				if (productList != null && productList.size() > 0)
				{
					oldTradeProduct = productList.get(0);
				}
				ProductTradeData newTradeProduct = oldTradeProduct.clone();

				if ("1".equals(effectNow))
				{
					String currentTime = SysDateMgr.getSysTime();
					oldTradeProduct.setEndDate(currentTime);

					newTradeProduct.setStartDate(SysDateMgr.addSecond(currentTime, 1));
					newTradeProduct.setEndDate(SysDateMgr.END_DATE_FOREVER);
					newTradeProduct.setProductId(nextProductId);
					newTradeProduct.setOldBrandCode(oldTradeProduct.getBrandCode());
					newTradeProduct.setOldProductId(oldTradeProduct.getProductId());
					newTradeProduct.setInstId(SeqMgr.getInstId());
				}
				else
				{
					String productChangeDate = null;
					if (ProductUtils.isBookingChange(data.getString("BOOKING_DATE")))
		            {
						productChangeDate = data.getString("BOOKING_DATE");
		            }
					else
					{
						productChangeDate = ProductUtils.getProductChangeDate(userProductId, nextProductId);
					}
					
					oldTradeProduct.setEndDate(SysDateMgr.addSecond(productChangeDate, -1));

					newTradeProduct.setStartDate(productChangeDate);
					newTradeProduct.setEndDate(SysDateMgr.END_DATE_FOREVER);
					newTradeProduct.setProductId(nextProductId);
					newTradeProduct.setOldBrandCode(oldTradeProduct.getBrandCode());
					newTradeProduct.setOldProductId(oldTradeProduct.getProductId());
					newTradeProduct.setInstId(SeqMgr.getInstId());
				}

				tradeProducts.add(oldTradeProduct.toData());
				tradeProducts.add(newTradeProduct.toData());
			}
		}
		return tradeProducts;
	}

	private IDataset getTradeElement(IData element, String userId, int i)
	{
		IDataset tradeElements = new DatasetList();
		if (null == element)
		{
			return null;
		}
		IData ruleElement = new DataMap();
		ruleElement.put("USER_ID_A", "-1");
		ruleElement.put("USER_ID", userId);
		ruleElement.putAll(element);
		if (ruleElement.getString("INST_ID", "").equals(""))
		{
			ruleElement.put("INST_ID", "" + i);
		}
		if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
		{
			ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
		}
		else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
		{
			ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
		}
		tradeElements.add(ruleElement);
		return tradeElements;
	}

	public void dealOfferAttrs(IDataset elements, String eparchyCode) throws Exception
	{
		int size = elements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);

			String disabled = element.getString("DISABLED", "FALSE");
			if ("true".equals(disabled))
				continue;
			
			IDataset attrs = this.dealSelectedElementAttrs(element, eparchyCode);

			if (attrs != null && attrs.size() > 0)
			{
				element.put("ATTR_PARAM", attrs);
			}
		}
	}

	private void dealOfferTime(IData data, IDataset elements, UcaData uca) throws Exception
	{
		String userProductId = data.getString("USER_PRODUCT_ID");
		String nextProductId = data.getString("NEXT_PRODUCT_ID");
		String eparchyCode = data.getString("EPARCHY_CODE");
		String nextProductStartDate = data.getString("NEXT_PRODUCT_START_DATE");
		String productId = StringUtils.isEmpty(nextProductId) ? userProductId : nextProductId;
		
		String bookingDate = data.getString("BOOKING_DATE", "");
		String sysDate = SysDateMgr.getSysTime();
		String bookFlag = "0"; // 超享套餐使用
		
		IDataset disabledElements = new DatasetList();
		IDataset calDateElemens = new DatasetList();

		ProductTimeEnv env = new ProductTimeEnv();
		// 当不存在预约产品时 绝对时间取选定的预约时间【如存在预约】
		if (StringUtils.isBlank(nextProductId) && ProductUtils.isBookingChange(bookingDate))
		{
			env.setBasicAbsoluteStartDate(bookingDate);
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(bookingDate));
			bookFlag = "1";
		}
		
		int size = elements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = elements.getData(i);
			
			//add by zhangxing3 for REQ201812250005关于开发集团客户一企一策一码的需求 start
			if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
			{
				String userId = data.getString("USER_ID", "");
				String discntCode = element.getString("ELEMENT_ID","");
				boolean limitTag = ElementUtil.checkDiscntGroupPolicyRule(userId,discntCode);
				if(!limitTag){
					elements.getData(0).put("ERROR_INFO", "您不符合折扣套餐策略,不能办理折扣套餐:"+discntCode+"!!");
				}
			}			
			//add by zhangxing3 for REQ201812250005关于开发集团客户一企一策一码的需求 end
			
			element.put("ELEMENT_ID", element.getString("OFFER_CODE"));
			element.put("ELEMENT_TYPE_CODE", element.getString("OFFER_TYPE"));
			element.put("PACKAGE_ID", element.getString("GROUP_ID", "-1"));
			element.put("ELEMENT_NAME", element.getString("OFFER_NAME"));
			element.put("PRODUCT_ID", productId);
			element.put("DESCRIPTION", element.getString("DESCRIPTION", element.getString("OFFER_NAME")));

			String disabled = element.getString("DISABLED", "FALSE");
			if ("true".equals(disabled))
			{
				disabledElements.add(element);
				continue;
			}
			element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);

			// 设置产品模型数据
			ProductModuleData pmd = null;
			if (element.getString("OFFER_TYPE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
			{
				pmd = new SvcData(element);
			}
			else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(element.getString("OFFER_TYPE")))
			{
				element.put("OPER_CODE", PlatConstants.OPER_ORDER);
				pmd = new PlatSvcData(element);
				pmd.setPkgElementConfig(element.getString("GROUP_ID"));
			}
			else
			{
				pmd = new DiscntData(element);
			}

			// 当存在预约产品时 绝对时间取预约产品生效时间【此时不存在预约情况】
			if (StringUtils.isNotBlank(nextProductId) && !ProductUtils.isBookingChange(bookingDate))
			{
				IDataset userOldProductElements = ProductInfoQry.getProductElements(userProductId, eparchyCode);
				IData oldConfig = this.getTransElement(userOldProductElements, pmd.getElementId(), pmd.getElementType());

				if (IDataUtil.isNotEmpty(oldConfig))// 如果元素存在老产品下面,以老产品配置方式计算
				{
					pmd.setProductId(oldConfig.getString("PRODUCT_ID"));
					pmd.setPkgElementConfig(oldConfig.getString("PACKAGE_ID"));
				}
				else// 如果元素只存在新产品下 则设置元素绝对生效时间为产品的开始时间 设置绝对失效时间为产品生效时间前一秒
				{
					pmd.setEnableTag("4");
					pmd.setStartAbsoluteDate(data.getString("NEXT_PRODUCT_START_DATE"));
					pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(data.getString("NEXT_PRODUCT_START_DATE")));
				}
			}

			// 计算时间
			if ("5".equals(pmd.getEnableTag()) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
			{
				if ("1".equals(bookFlag) && ("3370".equals(pmd.getElementId()) || "3372".equals(pmd.getElementId()) || "3377".equals(pmd.getElementId()) || "3378".equals(pmd.getElementId())))
				{// 超享卡特殊处理
					element.put("EFFECT_NOW_START_DATE", SysDateMgr.firstDayOfDate(bookingDate, 1));
				} else
				{
					element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
				}
			}
			else
			{
				element.put("EFFECT_NOW_START_DATE", sysDate);
			}
			element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));

			pmd.setStartDate(null);
			String startDate = ProductModuleCalDate.calStartDate(pmd, env);
			element.put("START_DATE", startDate);

			pmd.setEndDate(null);
			String endDate = ProductModuleCalDate.calEndDate(pmd, startDate);
			element.put("END_DATE", endDate);
			
			if ("3".equals(pmd.getEnableTag()) && StringUtils.isBlank(env.getBasicAbsoluteStartDate()))
			{
				element.put("CHOICE_START_DATE", "true");
			}
			if ("2".equals(pmd.getEndEnableTag()))
			{
				element.put("SELF_END_DATE", "true");// 自选时间
			}
			
			/*
			 * 节假日元素办理时间 获取元素的办理时间
			 */
			if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
			{
				IDataset elementSpecialTimeLimit = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "7686", pmd.getElementId(), pmd.getElementType());
				if (IDataUtil.isNotEmpty(elementSpecialTimeLimit))
				{
					// 获取时间配置
					IData dateLimitConfig = elementSpecialTimeLimit.getData(0);
					// 有效期配置
					String validBeginDate = dateLimitConfig.getString("PARA_CODE4", "");
					String validEndDate = dateLimitConfig.getString("PARA_CODE5", "");

					String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

					if (StringUtils.isNotBlank(validBeginDate) && StringUtils.isNotBlank(validEndDate))
					{
						// 如果在有效期内
						if (curDate.compareTo(validBeginDate) > 0 && curDate.compareTo(validEndDate) < 0)
						{
							/*
							 * 判断是在哪个规定的时间内办理 如果假期前办理，就在假期第一天开始生效
							 * 如果是假期当中办理，就是马上生效 终止时间都为假期的最后一天
							 */
							String holidayBeforeBegin = dateLimitConfig.getString("PARA_CODE6", "");
							String holidayBeforeEnd = dateLimitConfig.getString("PARA_CODE7", "");
							String holidayBeforeStartDate = dateLimitConfig.getString("PARA_CODE8", "");

							String holidayBegin = dateLimitConfig.getString("PARA_CODE9", "");
							String holidayEnd = dateLimitConfig.getString("PARA_CODE10", "");

							String allEndDay = dateLimitConfig.getString("PARA_CODE12", "");

							if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
							{
								if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
								{
									element.put("START_DATE", holidayBeforeStartDate);
								}
							}
							if (StringUtils.isNotBlank(holidayBegin) && StringUtils.isNotBlank(holidayEnd))
							{
								if (curDate.compareTo(holidayBegin) > 0 && curDate.compareTo(holidayEnd) < 0)
								{
									element.put("START_DATE", curDate);
								}
							}

							element.put("END_DATE", allEndDay);
						}
					}
				}
				
				
				IDataset elementHolidayTimeLimit = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "1806", pmd.getElementId(), pmd.getElementType());
				if (IDataUtil.isNotEmpty(elementHolidayTimeLimit))
				{						
					// 获取时间配置
					IData dateLimitConfig = elementHolidayTimeLimit.getData(0);
					// 有效期配置
					
					//本月第一天
					String validBeginDate = SysDateMgr.getFirstDayOfThisMonth();
					//本月最后一天
					String validEndDate = SysDateMgr.getLastDateThisMonth();

					String curDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

					if (StringUtils.isNotBlank(validBeginDate) && StringUtils.isNotBlank(validEndDate))
					{
						// 如果在有效期内
						if (curDate.compareTo(validBeginDate) > 0 && curDate.compareTo(validEndDate) < 0)
						{								
							//标志：1表示走本月减特定天数；2表示本月几号
							String timeFlag = dateLimitConfig.getString("PARA_CODE6", "1");
							if("1".equals(timeFlag))
							{
								String timeday = dateLimitConfig.getString("PARA_CODE7", "7");
								String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
								String holidayBeforeEnd = SysDateMgr.addDays(validEndDate, -Integer.parseInt(timeday));
								if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
								{
									if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
									{
										element.put("START_DATE", SysDateMgr.addDays(holidayBeforeEnd, 0));
									}
								}
							}else if("2".equals(timeFlag))
							{
								String timeday = dateLimitConfig.getString("PARA_CODE8", "23");
								String holidayBeforeBegin = SysDateMgr.getFirstDayOfThisMonth();
								//yyyy-MM-dd
								String timeend = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
								String holidayBeforeEnd = timeend.substring(0, 8) + timeday;
								if (StringUtils.isNotBlank(holidayBeforeBegin) && StringUtils.isNotBlank(holidayBeforeEnd))
								{
									if (curDate.compareTo(holidayBeforeBegin) > 0 && curDate.compareTo(holidayBeforeEnd) < 0)
									{
										element.put("START_DATE", SysDateMgr.addDays(holidayBeforeEnd, 0));
									}
								}
							}else
							{
								element.put("START_DATE", curDate);
							}
							
							if(StringUtils.isBlank(element.getString("START_DATE")))
							{
								element.put("START_DATE", curDate);
							}
							element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						}
					}
				}
			}

			// REQ201506020003 假日流量套餐开发需求 start by songlm 20150619
			// 6600该优惠元素配置生效失效时间类型为4，即使用绝对时间，用TD_B_PACKAGE_ELEMENT表中配置的生效、失效时间。但如果在生效与失效时间期间办理，需要立即生效。
			if ("6600".equals(pmd.getElementId()) && (sysDate.compareTo(startDate) > 0))
			{
				element.put("START_DATE", sysDate);
			}
			// end

//			pmd.setStartDate(element.getString("START_DATE"));
//			pmd.setEndDate(element.getString("END_DATE"));

			try
			{
				if (StringUtils.isBlank(DataBusManager.getDataBus().getAcceptTime()))
				{
					DataBusManager.getDataBus().setAcceptTime(SysDateMgr.getSysTime());
				}
				ProductModuleCalDateExtend.loadCalElementActions("110");
				ProductModuleCalDateExtend.calElementDate(pmd, env, uca, null);
				if (StringUtils.isNotBlank(pmd.getStartDate()))
				{
					element.put("START_DATE", pmd.getStartDate());
				}
				if (StringUtils.isNotBlank(pmd.getEndDate()))
				{
					element.put("END_DATE", pmd.getEndDate());
				}
				calDateElemens.add(element);
			}
			catch (Exception e)
			{
				// element.put("ERROR_INFO", e.getMessage());
				element.put("DISABLED", "true");
				disabledElements.add(element);
			}
		}
		
		// 前台流量王时间特殊处理
		this.GprsKingDiscntDateDeal(elements, new DatasetList(data.getString("SELECTED_ELEMENTS")));
		
		elements.clear();
		elements.addAll(disabledElements);
		elements.addAll(calDateElemens);
	}

	public static String dealSaturdaydateUtil() throws Exception
	{
		SimpleDateFormat simdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(cal.DAY_OF_WEEK, cal.SATURDAY);
		String weekse = simdf.format(cal.getTime()) + SysDateMgr.START_DATE_FOREVER;
		if (SysDateMgr.compareTo(weekse, SysDateMgr.getSysTime()) < 0)
		{
			weekse = SysDateMgr.getSysTime();
		}
		return weekse;
	}

	public static String dealSundaydateUtil() throws Exception
	{
		SimpleDateFormat simdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(cal.DAY_OF_WEEK, cal.SUNDAY);
		String weeklast = simdf.format(cal.getTime()) + SysDateMgr.END_DATE;
		return weeklast;

	}

	private IData getTransElement(IDataset productElements, String elementId, String elementType)
	{
		if (productElements == null || productElements.size() <= 0)
		{
			return null;
		}
		int size = productElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if (null == element)
			{
				continue;
			}
			if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				return element;
			}
		}
		return null;
	}
	/**
	 * 前台流量王时间处理【先选择流量王,而后选择对应GPRS，处理不到】
	 * 
	 * @param elements
	 *            本次操作元素集合
	 * @param selectedElements
	 *            已经存在所有元素集合
	 * @throws Exception
	 */
	public void GprsKingDiscntDateDeal(IDataset elements, IDataset selectedElements) throws Exception
	{
		// 只针对本次操作中有流量王优惠，且已经存在对应依赖的GPRS优惠时候才做处理
		if (IDataUtil.isNotEmpty(elements))
		{
			for (int i = 0, size = elements.size(); i < size; i++)
			{
				IData element = elements.getData(i);

				String modifyTag = element.getString("MODIFY_TAG");
				String elementId = element.getString("ELEMENT_ID");
				String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
				// String discntType
				// =DiscntInfoQry.getDiscntTypeByDiscntCode(elementId);
				String discntType = UDiscntInfoQry.getTableNameValue("TD_B_DTYPE_DISCNT", "DISCNT_TYPE_CODE", elementTypeCode, elementId);

				// 本次添加的流量王
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "LLCX".equals(discntType))
				{
					// IDataset relyDiscnts =
					// ElemLimitInfoQry.getGprsKingDiscntByGprs(elementId);

					IDataset relyDiscnts = new DatasetList();
					IDataset eleLimitA = UElementLimitInfoQry.queryElementLimitByElementIdB(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT, "2");
					IDataset allGPRSDiscnts = UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode("", "5");

					for (int limita = 0; limita < eleLimitA.size(); limita++)
					{
						IData temp = eleLimitA.getData(limita);
						for (int j = 0, discntSize = allGPRSDiscnts.size(); j < discntSize; j++)
						{
							IData gprsDiscnt = allGPRSDiscnts.getData(j);
							if (temp.getString("ELEMENT_ID_B").equals(gprsDiscnt.getString("DISCNT_CODE")))
							{
								relyDiscnts.add(temp);

							}
						}
					}

					if (IDataUtil.isNotEmpty(relyDiscnts))
					{
						String relyElementId = relyDiscnts.getData(0).getString("ELEMENT_ID_B");// 依赖的那个GPRS优惠

						String tempStartDate = SysDateMgr.END_DATE_FOREVER;
						String tempEndDate = SysDateMgr.END_DATE_FOREVER;

						String llcxStartDate = SysDateMgr.decodeTimestamp(element.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
						String llcxEndDate = SysDateMgr.decodeTimestamp(element.getString("END_DATE"), SysDateMgr.PATTERN_STAND);

						boolean existsGprs = false;

						for (int j = 0, selectSize = selectedElements.size(); j < selectSize; j++)
						{
							IData selectElement = selectedElements.getData(j);

							String selectElementId = selectElement.getString("ELEMENT_ID");
							String selectElementTypeCode = selectElement.getString("ELEMENT_TYPE_CODE");

							// 存在对应依赖的GPRS优惠
							if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(selectElementTypeCode) && selectElementId.equals(relyElementId))
							{
								String gprsStartDate = SysDateMgr.decodeTimestamp(selectElement.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
								String gprsEndDate = SysDateMgr.decodeTimestamp(selectElement.getString("END_DATE"), SysDateMgr.PATTERN_STAND);

								if (tempStartDate.compareTo(gprsStartDate) > 0)
								{
									tempStartDate = gprsStartDate;
								}
								if (tempEndDate.compareTo(gprsEndDate) > 0)
								{
									tempEndDate = gprsEndDate;
								}

								existsGprs = true;
							}
						}
						if (existsGprs)// 当本次操作存在对应依赖的GPRS优惠的时候，才重新赋值流量王的开始结束时间
						{
							if (llcxStartDate.compareTo(tempStartDate) < 0)
							{
								element.put("START_DATE", tempStartDate);
							}
							if (llcxEndDate.compareTo(tempEndDate) > 0)
							{
								element.put("END_DATE", tempEndDate);
							}
						}
					}
				}
			}
		}
	}
	
	private IDataset dealSelectedElementAttrs(IData element, String eparchyCode) throws Exception
	{

		IDataset attrs = new DatasetList();
		IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
		// ADC、MAS弹窗
		if (IDataUtil.isNotEmpty(attrItemAList) && element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
		{
			for (int i = 0; i < attrItemAList.size(); i++)
			{
				IData attrItemAMap = attrItemAList.getData(i);
				String attrtypecode = attrItemAMap.getString("ATTR_TYPE_CODE", "0");
				if (attrtypecode.equals("9"))
				{
					element.put("ATTR_PARAM_TYPE", "9");
					IData userattr = new DataMap();
					userattr.put("PARAM_VERIFY_SUCC", "false");
					attrs.add(userattr);
					return attrs;
				}
			}
		}

		attrs = this.makeAttrs(null, attrItemAList);
		return attrs;
	}
	
	private IDataset makeAttrs(IDataset userAttrs, IDataset elementItemAList)
	{

		if (elementItemAList != null && elementItemAList.size() > 0)
		{
			int size = elementItemAList.size();
			IDataset returnAttrs = new DatasetList();
			for (int i = 0; i < size; i++)
			{
				IData attr = new DataMap();
				IData itemA = elementItemAList.getData(i);
				attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
				attr.put("ATTR_VALUE", "");
				if (userAttrs != null && userAttrs.size() > 0)
				{
					int uSize = userAttrs.size();
					for (int j = 0; j < uSize; j++)
					{
						IData userAttr = userAttrs.getData(j);
						if (itemA.getString("ATTR_CODE").equals(userAttr.getString("ATTR_CODE")))
						{
							attr.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
							break;
						}
					}
				} else
				{
					attr.put("ATTR_VALUE", itemA.getString("ATTR_INIT_VALUE"));
				}
				returnAttrs.add(attr);
			}
			return returnAttrs;
		} else
		{
			return null;
		}
	}
}
