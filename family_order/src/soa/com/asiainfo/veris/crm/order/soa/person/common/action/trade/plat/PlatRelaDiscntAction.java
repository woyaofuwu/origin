package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class PlatRelaDiscntAction implements ITradeAction
{

	private DiscntTradeData buildNewBaseDiscntData(PlatSvcTradeData pstd, IData discntConfig)
	{
		DiscntTradeData pdd = new DiscntTradeData();
		pdd.setUserId(pstd.getUserId());
		pdd.setElementId(discntConfig.getString("DISCNT_CODE"));
		pdd.setUserIdA("-1");
		pdd.setProductId(pstd.getProductId());
		pdd.setPackageId(pstd.getPackageId());
		pdd.setSpecTag("0");
		return pdd;
	}

	private void buildNormalBindDiscnt(PlatSvcTradeData pstd, IData discntConfig, IDataset relatedDiscntConfigs, List<DiscntTradeData> pdds, BusiTradeData btd) throws Exception
	{
		UcaData uca = btd.getRD().getUca();
		IDataset allDiscntConfigs = new DatasetList();
		allDiscntConfigs.addAll(relatedDiscntConfigs);
		allDiscntConfigs.add(discntConfig);
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		List<DiscntTradeData> sameDiscntTypeDiscnts = this.getUserDiscntByDiscntTypeAndRelatedValue(discntConfig.getString("DISCNT_TYPE"), allDiscntConfigs, pdds, discntConfig.getString("RELATED_VALUE"));

		// 海南不需要对暂停恢复做优惠处理
		if ("-01-06-10-12-88-08-88-20-".indexOf(pstd.getOperCode()) >= 0)
		{
			// 判断是否是预约产品变更
			boolean preFlag = checkIsPreProductChange(uca, btd, pstd);

			int size = sameDiscntTypeDiscnts.size();
			// 如果用户无同类型的优惠，则新增
			if (size <= 0)
			{
				DiscntTradeData pdd = this.buildNewBaseDiscntData(pstd, discntConfig);
				pdd.setModifyTag("0");
				pdd.setInstId(SeqMgr.getInstId());
				// 海南特殊的，对于手机电视半年包，飞信半年包等服务，她们的优惠时间跟着服务台账走
				if (discntConfig.getString("PARA_CODE24", "0").equals("1") || preFlag)
				{
					pdd.setStartDate(pstd.getStartDate());
				} else
				// 此时没有同类型优惠，立即生效
				{
					pdd.setStartDate(SysDateMgr.getSysTime());
				}

				// 海南特殊的，对于手机电视半年包，飞信半年包等服务，她们的优惠时间跟着服务台账走
				if (discntConfig.getString("PARA_CODE24", "0").equals("1"))
				{
					pdd.setEndDate(pstd.getEndDate());
				} else if (discntConfig.getString("PARA_CODE24", "0").equals("2"))
				{
					pdd.setEndDate(SysDateMgr.getLastDateThisMonth());
				} else
				{
					pdd.setEndDate(SysDateMgr.getTheLastTime());
				}

				btd.add(serialNumber, pdd);

				// offer_rel
				OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

				offerRelTradeData.setUserId(pstd.getUserId());
				offerRelTradeData.setOfferCode(pstd.getElementId());
				offerRelTradeData.setOfferType(pstd.getElementType());
				offerRelTradeData.setOfferInsId(pstd.getInstId());
				offerRelTradeData.setGroupId("-1");// 连带关系groupId 设置为-1
				offerRelTradeData.setRelOfferCode(pdd.getElementId());
				offerRelTradeData.setRelOfferType(pdd.getElementType());
				offerRelTradeData.setRelOfferInsId(pdd.getInstId());
				offerRelTradeData.setRelUserId(pdd.getUserId());
				offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
				offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), pdd.getStartDate()));
				offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), pdd.getEndDate()));
				offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
				offerRelTradeData.setRemark("平台服务关联订购优惠");
				offerRelTradeData.setInstId(SeqMgr.getInstId());

				btd.add(serialNumber, offerRelTradeData);
			} else
			{
				for (int i = 0; i < size; i++)
				{
					// 如果有下个月生效的同类型优惠，先删除
					DiscntTradeData pdd = sameDiscntTypeDiscnts.get(i);

					IDataset userOfferRels = UserOfferRelQry.getOfferRelByRelOfferInsId(pdd.getInstId());

					IData tempUserOfferRel = new DataMap();
					for (int ii = 0; ii < userOfferRels.size(); ii++)
					{
						IData userOfferRel = userOfferRels.getData(ii);
						if (StringUtils.equals(pstd.getElementType(), userOfferRel.getString("OFFER_TYPE")) && StringUtils.endsWith(pstd.getElementId(), userOfferRel.getString("OFFER_CODE")))
						{
							if (StringUtils.equals(pdd.getInstId(), userOfferRel.getString("REL_OFFER_INS_ID")) && "-1".equals(userOfferRel.getString("GROUP_ID")))
							{
								tempUserOfferRel = userOfferRel;
							}
						}
					}
					if (pdd.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && pdd.getStartDate().compareTo(SysDateMgr.getLastDateThisMonth()) > 0)
					{
						DiscntTradeData tdd = pdd.clone();
						tdd.setEndDate(SysDateMgr.getSysTime());
						tdd.setModifyTag("1");
						btd.add(serialNumber, tdd);

						continue;
					}
					// 如果同当前优惠相同的优惠，延长优惠时间
					if (pdd.getElementId().equals(discntConfig.getString("DISCNT_CODE")))
					{
						DiscntTradeData tdd = pdd.clone();

						tdd.setEndDate(SysDateMgr.getTheLastTime());
						tdd.setModifyTag("2");
						btd.add(serialNumber, tdd);

						// 延长offer_rel的失效时间
						if (null != tempUserOfferRel && tempUserOfferRel.size() > 0)
						{
							OfferRelTradeData offerRelTradeData = new OfferRelTradeData(tempUserOfferRel);
							offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
							offerRelTradeData.setOfferInsId(pstd.getInstId());
							offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), tdd.getEndDate()));// 延长

							btd.add(serialNumber, offerRelTradeData);
						}
					} else
					{

						int length = relatedDiscntConfigs.size();
						IData relatedDiscntConfig = null;
						for (int j = 0; j < length; j++)
						{
							IData temp = relatedDiscntConfigs.getData(j);
							if (temp.getString("DISCNT_CODE").equals(pdd.getElementId()))
							{
								relatedDiscntConfig = temp;
								break;
							}
						}

						// 如果有同类优惠，而且不是下个月开始生效的
						if (relatedDiscntConfig == null)
						{
							continue;
						} else
						{
							String relatedDiscntLevel = relatedDiscntConfig.getString("DISCNT_LEVEL"); // 老的优惠的级别
							String discntLevel = discntConfig.getString("DISCNT_LEVEL"); // 新优惠的优惠级别
							// 有比新优惠的级别低的老优惠，
							if (discntLevel.compareTo(relatedDiscntLevel) > 0)
							{
								DiscntTradeData tdd = pdd.clone();
								tdd.setModifyTag("1");
								// 0立即失效 1月底失效
								if (discntConfig.getString("IS_NEXTMONTH", "").equals("0") && !preFlag)
								{
									tdd.setEndDate(SysDateMgr.getSysTime());
								} else
								{
									tdd.setEndDate(SysDateMgr.getLastDateThisMonth());
								}
								btd.add(serialNumber, tdd);

								// 删除offer_rel不需要特殊处理，公共已经处理
								/*
								 * if (null != tempUserOfferRel &&
								 * tempUserOfferRel.size() > 0) {
								 * OfferRelTradeData offerRelTradeData = new
								 * OfferRelTradeData(tempUserOfferRel);
								 * offerRelTradeData
								 * .setModifyTag(BofConst.MODIFY_TAG_DEL);
								 * offerRelTradeData
								 * .setEndDate(calculateEndDate(
								 * pstd.getEndDate(), tdd.getEndDate()));
								 * offerRelTradeData.setRemark("平台服务关联退订优惠");
								 * btd.add(serialNumber, offerRelTradeData); }
								 */

								// 0立即生效 1下个月初生效
								DiscntTradeData newPdd = this.buildNewBaseDiscntData(pstd, discntConfig);
								newPdd.setModifyTag("0");
								if (discntConfig.getString("IS_NEXTMONTH", "").equals("0") && !preFlag)
								{
									newPdd.setStartDate(SysDateMgr.getSysTime());
								} else
								{
									newPdd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
								}

								newPdd.setEndDate(SysDateMgr.getTheLastTime());
								newPdd.setInstId(SeqMgr.getInstId());
								btd.add(serialNumber, newPdd);

								// 新增一条offer_rel
								OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

								offerRelTradeData.setUserId(pstd.getUserId());
								offerRelTradeData.setOfferCode(pstd.getElementId());
								offerRelTradeData.setOfferType(pstd.getElementType());
								offerRelTradeData.setOfferInsId(pstd.getInstId());
								offerRelTradeData.setGroupId("-1");// 连带关系groupId
								// 设置为-1
								offerRelTradeData.setRelOfferCode(newPdd.getElementId());
								offerRelTradeData.setRelOfferType(newPdd.getElementType());
								offerRelTradeData.setRelOfferInsId(newPdd.getInstId());
								offerRelTradeData.setRelUserId(newPdd.getUserId());
								offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);// TODO
								offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), newPdd.getStartDate()));
								offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), newPdd.getEndDate()));
								offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								offerRelTradeData.setRemark("平台服务关联订购优惠");
								offerRelTradeData.setInstId(SeqMgr.getInstId());

								btd.add(uca.getSerialNumber(), offerRelTradeData);

							} else
							{
								// 比新优惠级别高的老优惠，截至到月底
								DiscntTradeData tdd = pdd.clone();
								tdd.setModifyTag("1");
								tdd.setEndDate(SysDateMgr.getLastDateThisMonth());
								btd.add(serialNumber, tdd);

								// 删除offer_rel不需要特殊处理，公共已经处理
								/*
								 * if(null != tempUserOfferRel &&
								 * tempUserOfferRel.size() > 0) {
								 * OfferRelTradeData offerRelTradeData = new
								 * OfferRelTradeData(tempUserOfferRel);
								 * offerRelTradeData
								 * .setModifyTag(BofConst.MODIFY_TAG_DEL);
								 * offerRelTradeData
								 * .setEndDate(calculateEndDate(
								 * pstd.getEndDate(), tdd.getEndDate()));
								 * offerRelTradeData.setRemark("平台服务关联退订优惠");
								 * btd.add(serialNumber, offerRelTradeData); }
								 */

								// 新的下个月生效
								DiscntTradeData newPdd = this.buildNewBaseDiscntData(pstd, discntConfig);
								newPdd.setModifyTag("0");
								newPdd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
								newPdd.setEndDate(SysDateMgr.getTheLastTime());
								newPdd.setInstId(SeqMgr.getInstId());
								btd.add(serialNumber, newPdd);

								// 新增一条offer_rel
								OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

								offerRelTradeData.setUserId(pstd.getUserId());
								offerRelTradeData.setOfferCode(pstd.getElementId());
								offerRelTradeData.setOfferType(pstd.getElementType());
								offerRelTradeData.setOfferInsId(pstd.getInstId());
								offerRelTradeData.setGroupId("-1");// 连带关系groupId
								// 设置为-1
								offerRelTradeData.setRelOfferCode(newPdd.getElementId());
								offerRelTradeData.setRelOfferType(newPdd.getElementType());
								offerRelTradeData.setRelOfferInsId(newPdd.getInstId());
								offerRelTradeData.setRelUserId(newPdd.getUserId());
								offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
								offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), newPdd.getStartDate()));
								offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), newPdd.getEndDate()));
								offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								offerRelTradeData.setRemark("平台服务关联订购优惠");
								offerRelTradeData.setInstId(SeqMgr.getInstId());

								btd.add(uca.getSerialNumber(), offerRelTradeData);
							}
						}
					}
				}
			}
		}
		// 海南不需要对暂停恢复做优惠处理
		else if ("-02-07-11-19-".indexOf(pstd.getOperCode()) >= 0)
		{
			int size = sameDiscntTypeDiscnts.size();
			for (int i = 0; i < size; i++)
			{
				DiscntTradeData tdd = sameDiscntTypeDiscnts.get(i);
				tdd.setModifyTag("1");

				if (tdd.getStartDate().compareTo(SysDateMgr.getLastDateThisMonth()) > 0)
				{
					tdd.setEndDate(SysDateMgr.getSysTime());
					btd.add(serialNumber, tdd);

					continue;
				} else
				{
					tdd.setEndDate(SysDateMgr.getLastDateThisMonth());

					// WLAN和校园WLAN 注销优惠立即生效； 扣费提醒取消，立即生效
					if ((("98002401".equals(pstd.getElementId()) || "98009201".equals(pstd.getElementId())) && PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode())) || ("FEE_NOTICE".equals(pstd.getRsrvStr8())))
					{
						tdd.setEndDate(SysDateMgr.getSysTime());
					}

					btd.add(serialNumber, tdd);

				}
			}
		}

	}

	private void buildPlatDiscnt(PlatSvcTradeData pstd, IData discntConfig, IDataset relatedDiscntConfigs, List<DiscntTradeData> pdds, BusiTradeData btd) throws Exception
	{
		// 0,1 普通优惠 ,6减免优惠，首次订购时绑定，以后订购不绑定(暂时海南还没有配置这样的优惠
		// SP_CODE=801234,BIZ_CODE = 110301,112387可以配置)
		if (!("-01-06-07-".indexOf(pstd.getOperCode()) >= 0) && !discntConfig.getString("DISCNT_TYPE").equals("0") && !"1".equals(discntConfig.getString("DISCNT_TYPE")))
		{
			// 非0类型的，都属于减免优惠，只针对订购起作用
			return;
		} else if ("-01-06-".indexOf(pstd.getOperCode()) >= 0 && !"0".equals(discntConfig.getString("DISCNT_TYPE")) && !"1".equals(discntConfig.getString("DISCNT_TYPE")))
		{
			this.buildReducePlatDiscnt(pstd, discntConfig, relatedDiscntConfigs, pdds, btd);
		} else if ("0".equals(discntConfig.getString("DISCNT_TYPE")) || "1".equals(discntConfig.getString("DISCNT_TYPE")))
		{
			this.buildNormalBindDiscnt(pstd, discntConfig, relatedDiscntConfigs, pdds, btd);
		} else if ("6".equals(discntConfig.getString("DISCNT_TYPE")) && "07".equals(pstd.getOperCode()))
		{
			this.cancelReducePlatDiscnt(pstd, discntConfig, relatedDiscntConfigs, pdds, btd);
		}
	}

	private void buildReducePlatDiscnt(PlatSvcTradeData pstd, IData discntConfig, IDataset relatedDiscntConfigs, List<DiscntTradeData> pdds, BusiTradeData btd) throws Exception
	{
		IDataset allDiscntConfigs = new DatasetList();
		allDiscntConfigs.addAll(relatedDiscntConfigs);
		allDiscntConfigs.add(discntConfig);
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		List<DiscntTradeData> sameDiscntTypeDiscnts = this.getUserDiscntByDiscntTypeAndRelatedValue(discntConfig.getString("DISCNT_TYPE"), allDiscntConfigs, pdds, discntConfig.getString("RELATED_VALUE"));

		// 减免优惠类型 6 ，暂时没有配置。 以后配置可以扩展
		if ("6".equals(discntConfig.getString("DISCNT_TYPE")))
		{
			if (sameDiscntTypeDiscnts.size() > 0)
			{
				// 存在相同类型的优惠，不处理
				return;
			}
			boolean isFirst = UserPlatSvcInfoQry.isFirstPeriod(pstd.getUserId(), discntConfig.getString("SERVICE_ID"), discntConfig.getString("TIME_TYPE"), discntConfig.getString("TIME_VALUE"), null);
			if (!isFirst)
			{
				return;
			} else
			{
				DiscntTradeData pdd = this.buildNewBaseDiscntData(pstd, discntConfig);
				if (discntConfig.getString("IS_NEXTMONTH").equals("0"))
				{
					pdd.setStartDate(SysDateMgr.getSysTime());
				} else
				{
					pdd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
				}
				pdd.setModifyTag("0");
				pdd.setInstId(SeqMgr.getInstId());
				String day = SysDateMgr.getStringDayByDate(SysDateMgr.getSysTime());
				String endDate = SysDateMgr.getLastDateThisMonth();
				if (day.compareTo(PersonConst.FIRST_MONTH_FREE_DISCNT_DAY_20) >= 0)
				{
					endDate = SysDateMgr.getAddMonthsLastDay(2, pdd.getStartDate());
				} else
				{
					endDate = SysDateMgr.getAddMonthsLastDay(1, pdd.getStartDate());
				}
				pdd.setEndDate(endDate);
				btd.add(serialNumber, pdd);

				// 生成offer_rel
				OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

				offerRelTradeData.setUserId(pstd.getUserId());
				offerRelTradeData.setOfferCode(pstd.getElementId());
				offerRelTradeData.setOfferType(pstd.getElementType());
				offerRelTradeData.setOfferInsId(pstd.getInstId());
				offerRelTradeData.setGroupId("-1");// 连带关系groupId 设置为-1
				offerRelTradeData.setRelOfferCode(pdd.getElementId());
				offerRelTradeData.setRelOfferType(pdd.getElementType());
				offerRelTradeData.setRelOfferInsId(pdd.getInstId());
				offerRelTradeData.setRelUserId(pdd.getUserId());
				offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
				offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), pdd.getStartDate()));
				offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), pdd.getEndDate()));
				offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
				offerRelTradeData.setRemark("平台服务关联订购优惠");
				offerRelTradeData.setInstId(SeqMgr.getInstId());

				btd.add(serialNumber, offerRelTradeData);
			}
		}
	}

	/**
	 * 退订时，截止减免类优惠
	 * 
	 * @param pstd
	 * @param discntConfig
	 * @param pdds
	 * @param btd
	 * @throws Exception
	 */
	private void cancelReducePlatDiscnt(PlatSvcTradeData pstd, IData discntConfig, IDataset relatedDiscntConfigs, List<DiscntTradeData> pdds, BusiTradeData btd) throws Exception
	{
		UcaData uca = btd.getRD().getUca();
		IDataset allDiscntConfigs = new DatasetList();
		allDiscntConfigs.addAll(relatedDiscntConfigs);
		allDiscntConfigs.add(discntConfig);
		List<DiscntTradeData> sameDiscntTypeDiscnts = this.getUserDiscntByDiscntTypeAndRelatedValue(discntConfig.getString("DISCNT_TYPE"), allDiscntConfigs, pdds, discntConfig.getString("RELATED_VALUE"));
		int size = sameDiscntTypeDiscnts.size();
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		for (int i = 0; i < size; i++)
		{
			DiscntTradeData tdd = sameDiscntTypeDiscnts.get(i);
			DiscntTradeData newTdd = uca.getUserDiscntByInstId(tdd.getInstId());

			// 如果已经是删除了，不再处理
			if (!newTdd.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
			{
				if (tdd.getStartDate().compareTo(SysDateMgr.getLastDateThisMonth()) > 0)
				{
					tdd.setEndDate(SysDateMgr.getSysTime());
					tdd.setModifyTag("1");
					btd.add(serialNumber, tdd);

					continue;
				} else
				{
					tdd.setEndDate(SysDateMgr.getLastDateThisMonth());
					tdd.setModifyTag("1");
					btd.add(serialNumber, tdd);
				}
			}

		}
	}

	private void createPlatDiscnt(PlatSvcTradeData pstd, BusiTradeData btd) throws Exception
	{
		// String serviceId = pstd.getElementId();
		UcaData uca = btd.getRD().getUca();
		// IDataset discntConfigs =
		// this.getDiscntConfigsByServiceId(allDiscntConfigs, pstd, btd);
		IDataset discntConfigs = this.getDiscntConfigsByServiceId(pstd, btd);
		if (discntConfigs == null || discntConfigs.size() <= 0)
		{
			return;
		}

		int size = discntConfigs.size();
		List<DiscntTradeData> userPlatDiscnts = new ArrayList<DiscntTradeData>();
		for (int i = 0; i < size; i++)
		{
			IData discntConfig = discntConfigs.getData(i);
			userPlatDiscnts.addAll(btd.getRD().getUca().getUserDiscntByDiscntId(discntConfig.getString("DISCNT_CODE")));
		}

		for (int i = 0; i < size; i++)
		{
			IDataset relatedDiscntConfigs = new DatasetList();
			relatedDiscntConfigs.addAll(discntConfigs);
			relatedDiscntConfigs.remove(i);

			IData discntConfig = discntConfigs.getData(i);
			if ("noattr".equals(discntConfig.getString("ATTR_VALUE")))
			{
				// 如果是无属性的，则比较服务ID是否相同
				if (discntConfig.getString("SERVICE_ID", "").equals(pstd.getElementId()))
				{
					this.buildPlatDiscnt(pstd, discntConfig, relatedDiscntConfigs, userPlatDiscnts, btd);
				}
			} else
			{
				// 如果是带属性的，则需要比较服务ID，属性编码，属性值
				AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(pstd.getInstId(), discntConfig.getString("ATTR_CODE"));
				if (attrTradeData != null && discntConfig.getString("ATTR_VALUE").equals(attrTradeData.getAttrValue()))
				{
					this.buildPlatDiscnt(pstd, discntConfig, relatedDiscntConfigs, userPlatDiscnts, btd);
				}
			}

		}
	}

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		// IDataset discntConfigs =
		// PlatInfoQry.getPlatDiscntConfig(BizRoute.getRouteId(), null);
		String mail139 = PlatReload.mail139Standard + "," + PlatReload.mail139Vip;
		// modify by 2017/4/20 lijun17
		if (StringUtils.equals("192", btd.getTradeTypeCode()))
		{// 立即销户，优惠、平台服务、Offer_rel统一处理，不需要再调Action
			return;
		}

		int size = pstds.size();
		for (int i = 0; i < size; i++)
		{
			PlatSvcTradeData pstd = pstds.get(i);

			// 实体卡订购的不处理优惠
			if (StringUtils.isNotBlank(pstd.getEntityCardNo()))
			{
				continue;
			}

			// 139标准邮箱订购时，连带退订了139vip， 而标准和vip配的是关联优惠。所以只有处理一次即可
			if (mail139.indexOf(pstd.getElementId()) >= 0)
			{
				if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
				{
					// 查找是否有订购或者变更的139邮箱的数据
					boolean isFound = false;
					for (int j = 0; j < size; j++)
					{
						PlatSvcTradeData tempPstd = pstds.get(j);
						if (mail139.indexOf(tempPstd.getElementId()) >= 0 && !tempPstd.getElementId().equals(pstd.getElementId()) && (PlatConstants.OPER_ORDER.equals(tempPstd.getOperCode()) || PlatConstants.OPER_USER_DATA_MODIFY.equals(tempPstd.getOperCode())))
						{
							isFound = true;
							break;
						}
					}
					if (isFound)
					{
						continue;
					}
				}
			}
			createPlatDiscnt(pstd, btd);
			// createPlatDiscnt(pstd, discntConfigs, btd);
		}
	}

	private IDataset getDiscntConfigsByServiceId(PlatSvcTradeData pstd, BusiTradeData btd) throws Exception
	{
		UcaData uca = btd.getRD().getUca();
		IDataset platSvcDiscntConfigs = new DatasetList();

		IDataset allDiscntConfigs = PlatInfoQry.getPlatDiscntConfigByServiceId(CSBizBean.getUserEparchyCode(), pstd.getElementId(), null, null);
		int size = allDiscntConfigs.size();
		IDataset reduceDiscntConfigs = new DatasetList();
		IDataset normalDiscntConfigs = new DatasetList();
		for (int i = 0; i < size; i++)
		{
			IData discntConfig = allDiscntConfigs.getData(i);
			if (pstd.getElementId().equals(discntConfig.getString("SERVICE_ID")))
			{
				if ("noattr".equals(discntConfig.getString("ATTR_VALUE")))
				{
					if ("0".equals(discntConfig.getString("DISCNT_TYPE")) || "1".equals(discntConfig.getString("DISCNT_TYPE")))
					{
						normalDiscntConfigs.add(discntConfig);
					} else
					{
						reduceDiscntConfigs.add(discntConfig);
					}
				} else
				{
					List<AttrTradeData> attrTradeDatas = pstd.getAttrTradeDatas();
					if ("04_05_19_20".indexOf(pstd.getOperCode()) >= 0)
					{

						attrTradeDatas = uca.getUserAttrsByRelaInstId(pstd.getInstId());
					}

					int attrSize = attrTradeDatas.size();
					for (int j = 0; j < attrSize; j++)
					{
						AttrTradeData attr = attrTradeDatas.get(j);
						if (discntConfig.getString("ATTR_CODE", "").equals(attr.getAttrCode()) && discntConfig.getString("ATTR_VALUE").equals(attr.getAttrValue()))
						{
							if ("0".equals(discntConfig.getString("DISCNT_TYPE")) || "1".equals(discntConfig.getString("DISCNT_TYPE")))
							{
								normalDiscntConfigs.add(discntConfig);
							} else
							{
								reduceDiscntConfigs.add(discntConfig);
							}
						}
					}
				}
			}
		}

		Set<String> relatedValues = new HashSet<String>();
		if (IDataUtil.isNotEmpty(normalDiscntConfigs))
		{
			for (Object data : normalDiscntConfigs)
			{
				IData config = (IData) data;
				relatedValues.add(config.getString("RELATED_VALUE"));
			}
		}

		IDataset relatedDiscntConfigs = new DatasetList();
		if (relatedValues != null && relatedValues.size() > 0)
		{
			Iterator<String> iterator = relatedValues.iterator();
			while (iterator.hasNext())
			{
				String relateValue = iterator.next();
				relatedDiscntConfigs.addAll(PlatInfoQry.getPlatDiscntConfigByRelateValue(CSBizBean.getUserEparchyCode(), relateValue));
			}
		}

		IDataset temp = new DatasetList();
		if (IDataUtil.isNotEmpty(reduceDiscntConfigs))
		{
			temp.addAll(reduceDiscntConfigs);
		}
		if (IDataUtil.isNotEmpty(relatedDiscntConfigs))
		{
			temp.addAll(relatedDiscntConfigs);
		}

		if (IDataUtil.isNotEmpty(temp))
		{
			int tempSize = temp.size();
			for (int i = 0; i < tempSize; i++)
			{
				IData config = temp.getData(i);
				String inModeCodes = config.getString("IN_MODE_CODES");
				String nodoStaffIds = config.getString("NODO_STAFF_ID");
				String replaceStaffIds = config.getString("REPLACE_STAFF_ID");
				String replacePeriod = config.getString("REPLACE_PERIOD");
				if (StringUtils.isNotBlank(inModeCodes) && inModeCodes.indexOf(CSBizBean.getVisit().getInModeCode()) < 0)
				{
					continue;
				}
				if (StringUtils.isNotBlank(nodoStaffIds) && nodoStaffIds.indexOf(CSBizBean.getVisit().getStaffId()) >= 0)
				{
					continue;
				}
				if (StringUtils.isNotBlank(replaceStaffIds) && StringUtils.isNotBlank(replacePeriod) && replaceStaffIds.indexOf(CSBizBean.getVisit().getStaffId()) >= 0)
				{
					config.put("VALID_PERIOD", replacePeriod);
				}
				platSvcDiscntConfigs.add(config);
			}
		}

		return platSvcDiscntConfigs;
	}

	private List<DiscntTradeData> getUserDiscntByDiscntTypeAndRelatedValue(String discntType, IDataset allDiscntConfig, List<DiscntTradeData> uds, String relatedValue)
	{
		List<DiscntTradeData> result = new ArrayList<DiscntTradeData>();
		int size = allDiscntConfig.size();
		for (int i = 0; i < size; i++)
		{
			IData discntConfig = allDiscntConfig.getData(i);
			if (discntType.equals(discntConfig.getString("DISCNT_TYPE")) && relatedValue.equals(discntConfig.getString("RELATED_VALUE")))
			{
				int udSize = uds.size();
				for (int j = 0; j < udSize; j++)
				{
					DiscntTradeData ud = uds.get(j);
					if (ud.getElementId().equals(discntConfig.getString("DISCNT_CODE")))
					{
						result.add(ud);
					}
				}
			}
		}
		return result;
	}

	private boolean checkIsPreProductChange(UcaData uca, BusiTradeData btd, PlatSvcTradeData pstd) throws Exception
	{
		if ("110".equals(btd.getTradeTypeCode()))
		{
			List<DiscntTradeData> wireLessDiscntList = new ArrayList<DiscntTradeData>();
			IDataset compara909List = CommparaInfoQry.getCommparaByCode1("CSM", "909", "110", "98001901", null);
			for (int m = 0; m < compara909List.size(); m++)
			{
				IData compara909 = compara909List.getData(m);
				wireLessDiscntList.addAll(uca.getUserDiscntByDiscntId(compara909.getString("PARA_CODE2")));
			}

			if (wireLessDiscntList != null && wireLessDiscntList.size() > 0)
			{
				DiscntTradeData wirelessDiscnt = wireLessDiscntList.get(0);
				if (BofConst.MODIFY_TAG_ADD.equals(wirelessDiscnt.getModifyTag()) && wirelessDiscnt.getStartDate().substring(0, 10).compareTo(SysDateMgr.getSysDate()) > 0)
				{
					return true;
				}
			}

		}

		return false;
	}

	public static String calculateStartDate(String fromStartDate, String toStartDate) throws Exception
	{
		return fromStartDate.compareTo(toStartDate) > 0 ? fromStartDate : toStartDate;
	}

	public static String calculateEndDate(String fromEndDate, String toEndDate) throws Exception
	{
		return fromEndDate.compareTo(toEndDate) > 0 ? toEndDate : fromEndDate;
	}
}
