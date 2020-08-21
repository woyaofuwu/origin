
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.specdate;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share.IElementCalDateAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: PackageElementDeal.java
 * @Description: 产品变更 预约情况下 TD_B_PACKAGE_ELEMENT.RSRV_TAG1=1 1. 预约时间DAY与用户账期日DAY相等 新增为预约时间 终止为预约时间上账期末 2.
 *               预约时间DAY与用户账期日DAY相等 新增为预约时间所在下账期第一天 终止为预约时间所在时间的本账期末
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 24, 2014 4:16:07 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 24, 2014 maoke v1.0.0 修改原因
 */
public class PackageElementDeal implements IElementCalDateAction
{

	/**
	 * @Description: 根据inst_id 获取用户优惠原始结束时间
	 * @param endDate
	 * @param instId
	 * @param uca
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 18, 2014 10:53:57 AM
	 */
	public static String getOriginalEndDate(String endDate, String instId, UcaData uca) throws Exception
	{
		if (StringUtils.isNotBlank(instId))
		{
			DiscntTradeData discnt = uca.getUserDiscntByInstId(instId);
			if (discnt != null)
			{
				return discnt.getEndDate();
			}
		}
		return endDate;
	}

	/**
	 * @Description: 是否恢复结束时间 【是true】
	 * @param bookingDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 18, 2014 11:44:42 AM
	 */
	public static boolean reEndDate(String bookingDate, String endDate) throws Exception
	{
		if (StringUtils.isNotBlank(bookingDate) && StringUtils.isNotBlank(endDate) && SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND_YYYYMMDD).compareTo(SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)) >= 0)
		{
			return true;
		}

		return false;
	}

	@Override
	public String calElementDate(ProductModuleData pmd, ProductTimeEnv env, UcaData uca, List<ProductModuleData> pmds) throws Exception
	{
		if (uca == null)// 适用前台开户没有uca情况【开户也不存在预约】
		{
			return null;
		}

		String elementTypeCode = pmd.getElementType();

		if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
		{
			boolean bookingTag = false;
			String bookingDate = "";

			// 是否预约变更
			if (env != null && env.getBasicAbsoluteStartDate() != null)
			{
				String baseAbsoluteStartDate = env.getBasicAbsoluteStartDate();

				if (ProductUtils.isBookingChange(baseAbsoluteStartDate))
				{
					bookingTag = true;
					bookingDate = baseAbsoluteStartDate;
				}
			}

			if (bookingTag)
			{
				String packageId = pmd.getPackageId();
				String elementId = pmd.getElementId();
				String modifyTag = pmd.getModifyTag();
				String endDate = pmd.getEndDate();
				String instId = pmd.getInstId();
				int bookingDateDay = SysDateMgr.getIntDayByDate(bookingDate);

				String rsrvTag1 = UpcCall.queryPackageElementRsrv(packageId, elementTypeCode, elementId, "RSRV_TAG1");

				if (StringUtils.isNotEmpty(rsrvTag1))
				{
					String discntElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);

					if ("1".equals(rsrvTag1))
					{
						// 如果优惠类型是5 且 预约时间内没有其他gprs优惠，则是以预约开始时间为准 直接返回
						if (PersonConst.DISCNT_TYPE_5.equals(discntElementType) && !isExistsOtherGprsDiscnt(bookingDate, uca))
						{
							return null;
						}

						int userAcctDay = Integer.parseInt(uca.getAcctDay());

						if (bookingDateDay == userAcctDay)// 预约时间day与用户账期时间day一致
						{
							if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))// 如果存在主产品变更，是否按照产品变更的时间走待确认
							{
								pmd.setStartDate(bookingDate);
							}
							if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
							{
								String originalEndDate = getOriginalEndDate(endDate, instId, uca);

								if (reEndDate(bookingDate, originalEndDate))
								{
									pmd.setEndDate(originalEndDate);
								}
								else
								{
									pmd.setEndDate(SysDateMgr.getAddMonthsLastDay(-1, bookingDate)+SysDateMgr.getEndTime235959());
								}
							}
						}
						else
						{
							if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))// 如果存在主产品变更，是否按照产品变更的时间走待确认
							{
								pmd.setStartDate(SysDateMgr.firstDayOfDate(bookingDate, 1));
							}
							if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
							{
								String originalEndDate = getOriginalEndDate(endDate, instId, uca);

								if (reEndDate(bookingDate, originalEndDate))
								{
									pmd.setEndDate(originalEndDate);
								}
								else
								{
									pmd.setEndDate(SysDateMgr.getAddMonthsLastDay(1, bookingDate));
								}
							}
						}
					}
					else
					{
						if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))//当不是此配置,删除状态时候、元素截止时间已经小于选择预约时间，重置结束时间为原始时间
						{
							String originalEndDate = getOriginalEndDate(endDate, instId, uca);

							if (reEndDate(bookingDate, originalEndDate))
							{
								pmd.setEndDate(originalEndDate);
							}
						}
					}
				}
			}
			else
			{

			}
		}
		return null;
	}

	/**
	 * @Description: 是否存在其他GPRS优惠【除标准gprs902外】
	 * @param uca
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 15, 2014 3:09:55 PM
	 */
	public boolean isExistsOtherGprsDiscnt(String bookingDate, UcaData uca) throws Exception
	{
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		if (userDiscnts != null && userDiscnts.size() > 0)
		{
			for (DiscntTradeData userDiscnt : userDiscnts)
			{
				String modifyTag = userDiscnt.getModifyTag();
				String elementId = userDiscnt.getElementId();
				String startDate = SysDateMgr.decodeTimestamp(userDiscnt.getStartDate(), SysDateMgr.PATTERN_STAND);
				bookingDate = SysDateMgr.decodeTimestamp(bookingDate, SysDateMgr.PATTERN_STAND);

				if (PersonConst.GPRS_DEFAULT_DISCNT_CODE.equals(elementId))
				{
					continue;
				}
				else
				{
					String discntElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);

					if (StringUtils.isNotBlank(discntElementType) && PersonConst.DISCNT_TYPE_5.equals(discntElementType))
					{
						if (startDate.compareTo(bookingDate) <= 0 && (BofConst.MODIFY_TAG_USER.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag)))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
