package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.specdate;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UGroupInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share.IElementCalDateAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ProductPackageDeal.java
 * @Description: 【开户专用】
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 8, 2014 11:09:44 AM Modification History: Date Author Version
 *        Description -------------------------------------------------------*
 *        Sep 8, 2014 maoke v1.0.0 修改原因
 */
public class ProductPackageDeal implements IElementCalDateAction
{
	@Override
	public String calElementDate(ProductModuleData pmd, ProductTimeEnv env, UcaData uca, List<ProductModuleData> pmds) throws Exception
	{
		String elementTypeCode = pmd.getElementType();
		String sysTime = DataBusManager.getDataBus().getAcceptTime();

		if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
		{
			boolean bookingTag = false;

			// 是否预约变更
			if (env != null && env.getBasicAbsoluteStartDate() != null)
			{
				String baseAbsoluteStartDate = env.getBasicAbsoluteStartDate();

				if (ProductUtils.isBookingChange(baseAbsoluteStartDate))
				{
					bookingTag = true;
				}
			}

			if (!bookingTag)
			{
				String modifyTag = pmd.getModifyTag();
				String productId = pmd.getProductId();
				String packageId = pmd.getPackageId();
				String elementId = pmd.getElementId();

				// String orderMode =
				// StaticUtil.getStaticValue(CSBizBean.getVisit(),
				// "TD_B_DISCNT", "DISCNT_CODE", "ORDER_MODE", elementId);
				String orderMode = "";
				OfferCfg cfg = OfferCfg.getInstance(elementId, "D");
				if (cfg != null)
				{
					orderMode = cfg.getOrderMode();
				}
				if (!"R".equals(orderMode))// 如果是非流量叠加类型才处理,流量叠加类型按照配置走
				{
					if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
					{
						String orderElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);// 订购的优惠类型
						// 首次订购 立即生效
						if (this.isFirstOrderByProductPkg(orderElementType, productId, packageId, sysTime, uca))
						{
							String startDate = sysTime;
							pmd.setStartDate(startDate);
							pmd.setEndDate(null);
							pmd.setEndDate(ProductModuleCalDate.calEndDate(pmd, startDate));
						} else
						{
							String startDate = SysDateMgr.getFirstDayOfNextMonth();
							pmd.setStartDate(startDate);
							pmd.setEndDate(null);
							pmd.setEndDate(ProductModuleCalDate.calEndDate(pmd, startDate));
						}
					}
					if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
					{
						// 根据PACKAGE_ID 查询产品包的配置
						if (!PersonConst.GPRS_DEFAULT_DISCNT_CODE.equals(elementId) && this.existsProductPackageSpecConfig(productId, packageId, uca.getUserEparchyCode()))
						{
							pmd.setEndDate(SysDateMgr.getLastDateThisMonth());
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * @Description: 是否存在td_b_product_package的特殊配置【存在:true】
	 * @param productId
	 * @param packageId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Sep 8, 2014 11:01:46 AM
	 */
	public boolean existsProductPackageSpecConfig(String productId, String packageId, String eparchyCode) throws Exception
	{
		String specialTag = UGroupInfoQry.querySpecialTagRsrvStr1(productId, packageId);

		if ("2".equals(specialTag))
		{
			return true;
		}
		return false;
	}

	/**
	 * @Description: 根据TD_B_PRODUCT_PACKAGE的RSRV_STR2字段确认是否首次订购标记【首次订购：true】
	 * @param orderElementType
	 *            新元素订购类型
	 * @param productId
	 * @param packageId
	 * @param uca
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 25, 2014 11:16:30 AM
	 */
	public boolean isFirstOrderByProductPkg(String orderElementType, String productId, String packageId, String sysTime, UcaData uca) throws Exception
	{
		// 适用前台开户情况 此时没有UCA 直接返回true 首次订购
		if (uca == null)
		{
			return true;
		}

		// 根据PACKAGE_ID 查询产品包的配置
		if (this.existsProductPackageSpecConfig(productId, packageId, uca.getUserEparchyCode()))
		{
			List<DiscntTradeData> discntLists = uca.getUserDiscnts();

			if (discntLists != null && discntLists.size() > 0)
			{
				for (int i = 0, size = discntLists.size(); i < size; i++)
				{
					DiscntTradeData discnt = discntLists.get(i);
					String userInstId = discnt.getInstId();
					/*
					 * OfferRelTradeData offerRel =
					 * uca.getNewestMainProductRelByUserIdAndInstId(userInstId);
					 * String userPackageId = offerRel != null
					 * ?offerRel.getGroupId():"-1";
					 */
					String userPackageId = discnt.getPackageId();
					String userModifyTag = discnt.getModifyTag();
					String userElementId = discnt.getElementId();
					String userStartDate = SysDateMgr.decodeTimestamp(discnt.getStartDate(), SysDateMgr.PATTERN_STAND);

					// String orderMode =
					// StaticUtil.getStaticValue(CSBizBean.getVisit(),
					// "TD_B_DISCNT", "DISCNT_CODE", "ORDER_MODE",
					// userElementId);
					String orderMode = "";
					OfferCfg cfg = OfferCfg.getInstance(userElementId, "D");
					if (cfg != null)
					{
						orderMode = cfg.getOrderMode();
					}
					// 排除902标准资费外 同包下面 已有元素是非可叠加元素 如果开始时间比对sysTime时间小的话,则非首次订购
					if (!PersonConst.GPRS_DEFAULT_DISCNT_CODE.equals(userElementId) && !"R".equals(orderMode) && isSamePackage(orderElementType, userElementId, userInstId, userPackageId, packageId, uca) && userStartDate.compareTo(sysTime) <= 0 && (BofConst.MODIFY_TAG_USER.equals(userModifyTag) || BofConst.MODIFY_TAG_DEL.equals(userModifyTag)))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * @Description: 判断是否属于同一包下面【产品变更的同时新增元素时,包ID与原产品包ID不同情况处理】
	 * @param orderElementType
	 *            新元素订购类型
	 * @param elementId
	 *            元素
	 * @param instId
	 * @param curPackageId
	 *            元素当前的包ID
	 * @param newPackageId
	 *            新产品下面的包ID
	 * @param uca
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Sep 6, 2014 4:59:55 PM
	 */
	public boolean isSamePackage(String orderElementType, String elementId, String instId, String curPackageId, String newPackageId, UcaData uca) throws Exception
	{

		if (StringUtils.equals(curPackageId, newPackageId))
		{
			return true;
		}/*
		 * else { DiscntTradeData discnt = uca.getUserDiscntByInstId(instId); if
		 * (discnt != null) { OfferRelTradeData offerRel =
		 * uca.getNewestMainProductRelByUserIdAndInstId(instId); String
		 * userProductId = offerRel != null ? offerRel.getOfferCode() : "-1";
		 * String userDiscntCode = discnt.getDiscntCode();
		 * 
		 * // 获取老的产品包配置 如果RSRV_STR1=2继续往下走 if
		 * (this.existsProductPackageSpecConfig(userProductId, curPackageId,
		 * uca.getUserEparchyCode())) { // 如果新订购的与用户已经存在的优惠是同类型 则视为在同一个包下面
		 * String userElementType =
		 * UDiscntInfoQry.getDiscntTypeByDiscntCode(userDiscntCode);// 订购的优惠类型
		 * if (orderElementType.equals(userElementType)) { return true; } } } }
		 */
		else
		{
			DiscntTradeData discnt = uca.getUserDiscntByInstId(instId);
			if (discnt != null)
			{
				String userProductId = discnt.getProductId();
				String userDiscntCode = discnt.getDiscntCode();
				IDataset offerRels = UserOfferRelInfoQry.queryUserOfferRelInfosByRelOfferInstId(instId);
				if (IDataUtil.isNotEmpty(offerRels))
				{
					IData offerRel = offerRels.getData(0);
					if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(offerRel.getString("OFFER_TYPE", "")))
					{// 如果是产品
						// 新系统产品包和营销活动报需分开处理，现网原营销活动包下rsrv_str1没有字段，暂时不处理原营销活动包的情况
						// 获取老的产品包配置 如果RSRV_STR1=2继续往下走
						if (this.existsProductPackageSpecConfig(userProductId, curPackageId, uca.getUserEparchyCode()))
						{
							// 如果新订购的与用户已经存在的优惠是同类型 则视为在同一个包下面
							String userElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(userDiscntCode);// 订购的优惠类型
							if (orderElementType.equals(userElementType))
							{
								return true;
							}
						}
					}
				}

			}

		}
		return false;
	}
}
