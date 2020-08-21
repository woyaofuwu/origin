package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PricePlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreUserDealValidDiscnt.java
 * @Description: 复机处理
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-7-15 下午5:02:45
 */
public class RestoreDealNowValidDiscnt implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		String acceptTime = btd.getRD().getAcceptTime();
		String lastSecondAcceptTime = SysDateMgr.getLastSecond(acceptTime);
		String userId = btd.getRD().getUca().getUserId();
		String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
		List<DiscntTradeData> discntTradeDataList = btd.getRD().getUca().getUserDiscnts();
		List<DiscntTradeData> newDiscntTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		int discntListSize = discntTradeDataList.size();
		int newDiscntListSize = newDiscntTradeDataList.size();

		for (int i = 0; i < discntListSize; i++)
		{
			DiscntTradeData oldDiscntTradeData = discntTradeDataList.get(i);
			if (StringUtils.equals(BofConst.MODIFY_TAG_USER, oldDiscntTradeData.getModifyTag()))
			{
				// 如果现有有效的特殊优惠中有绑定优惠，并且跟新选的绑定优惠一致，则旧的立即结束，新的从现在开始,
				// 如果跟新选的绑定优惠不一致，则旧的优惠还是结束到月底
				// 终止复机前仍然有效的其他优惠
				if (!isSpecDiscnt(oldDiscntTradeData))
				{
					DiscntTradeData discntTradeData = oldDiscntTradeData.clone();
					discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
					discntTradeData.setEndDate(lastSecondAcceptTime);
					btd.add(serialNumber, discntTradeData);
				} else
				{
					String oldDiscntCode = oldDiscntTradeData.getDiscntCode();
					if (StringUtils.isEmpty(oldDiscntCode))
					{
						oldDiscntCode = oldDiscntTradeData.getElementId();
					}

					for (int j = 0; j < newDiscntListSize; j++)
					{
						DiscntTradeData tempNewDiscntTradeData = newDiscntTradeDataList.get(j);
						String newDiscntCode = tempNewDiscntTradeData.getDiscntCode();
						if (StringUtils.isEmpty(newDiscntCode))
						{
							newDiscntCode = tempNewDiscntTradeData.getElementId();
						}
						String newDiscntType = UDiscntInfoQry.getDiscntTypeByDiscntCode(newDiscntCode);
						// 前台选择了新GPRS优惠，资料表中存在相同的GPRS优惠，资料表中的GPRS优惠立即终止
						if (StringUtils.equals(oldDiscntCode, newDiscntCode))
						{
							DiscntTradeData discntTradeData = oldDiscntTradeData.clone();
							discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
							discntTradeData.setEndDate(lastSecondAcceptTime);
							btd.add(serialNumber, discntTradeData);
						} else if (StringUtils.equals(PersonConst.DISCNT_TYPE_5, newDiscntType))
						{
							String oldDiscntType = UDiscntInfoQry.getDiscntTypeByDiscntCode(oldDiscntCode);
							if (StringUtils.equals(PersonConst.DISCNT_TYPE_5, oldDiscntType))
							{
								// 如果新选了GRPS优惠，资料表中的902优惠立即终止,资料表非902优惠不处理，但新选的GPRS优惠下账期开始
								if (StringUtils.equals(oldDiscntCode, PersonConst.GPRS_DEFAULT_DISCNT_CODE))
								{
									DiscntTradeData discntTradeData = oldDiscntTradeData.clone();
									discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
									discntTradeData.setEndDate(lastSecondAcceptTime);
									btd.add(serialNumber, discntTradeData);
								} else
								{
									tempNewDiscntTradeData.setStartDate(firstDayNextAcct);
									// 连带修改商品关系订单项的开始时间
									List<OfferRelTradeData> offerRelDataList = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
									int offerRelDataListSize = offerRelDataList.size();
									if (offerRelDataList != null && offerRelDataListSize > 0)
									{
										for (int k = 0; k < offerRelDataListSize; k++)
										{
											OfferRelTradeData offerRelData = offerRelDataList.get(k);
											if (offerRelData.getRelOfferInsId().equals(tempNewDiscntTradeData.getInstId()))
											{
												if (SysDateMgr.compareTo(firstDayNextAcct + SysDateMgr.START_DATE_FOREVER, offerRelData.getStartDate()) > 0)
												{
													offerRelData.setStartDate(firstDayNextAcct + SysDateMgr.START_DATE_FOREVER);
												}
											}
										}
									}
									// 连带修改定价计划订单项的开始时间
									List<PricePlanTradeData> pricePlanDataList = btd.getTradeDatas(TradeTableEnum.TRADE_PRICE_PLAN);
									int pricePlanDataListSize = pricePlanDataList.size();
									if (pricePlanDataList != null && pricePlanDataListSize > 0)
									{
										for (int k = 0; k < pricePlanDataListSize; k++)
										{
											PricePlanTradeData pricePlanData = pricePlanDataList.get(k);
											if (pricePlanData.getOfferInsId().equals(tempNewDiscntTradeData.getInstId()))
											{
												if (SysDateMgr.compareTo(firstDayNextAcct + SysDateMgr.START_DATE_FOREVER, pricePlanData.getStartDate()) > 0)
												{
													pricePlanData.setStartDate(firstDayNextAcct + SysDateMgr.START_DATE_FOREVER);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// 判断是否是绑定优惠
	private boolean isBindDiscnt(DiscntTradeData discntTradeData) throws Exception
	{
		IDataset dataset;
		String eparchyCode = CSBizBean.getTradeEparchyCode();
		String discntCode = discntTradeData.getDiscntCode();
		if (StringUtils.isEmpty(discntCode))
		{
			discntCode = discntTradeData.getElementId();
		}

		dataset = CommparaInfoQry.getCommpara("CSM", "8856", discntTradeData.getProductId(), eparchyCode);
		for (int i = 0; i < dataset.size(); i++)
		{
			if (StringUtils.equals(discntCode, dataset.getData(i).getString("PARA_CODE1")))
				return true;
		}
		return false;
	}

	/**
	 * 判断优惠是不是特殊优惠
	 */
	private boolean isSpecDiscnt(DiscntTradeData discntTradeData) throws Exception
	{
		if (discntTradeData == null)
			return false;
		// 是否是GPRS类型或者WLAN类型
		String discntCode = discntTradeData.getDiscntCode();
		if (StringUtils.isEmpty(discntCode))
		{
			discntCode = discntTradeData.getElementId();
		}

		// add by zhangxing for HNYD-REQ-20100426-004销号后终止家庭网关系及亲情关系优化 start
		if (StringUtils.equals("3403", discntCode) || StringUtils.equals("3404", discntCode))
		{
			return true;
		}
		// add by zhangxing for HNYD-REQ-20100426-004销号后终止家庭网关系及亲情关系优化 end
		String discntType = UDiscntInfoQry.getDiscntTypeByDiscntCode(discntCode);
		if (StringUtils.equals(PersonConst.DISCNT_TYPE_5, discntType) || StringUtils.equals("6", discntType))
		{
			return true;
		}
		// 是否在8856参数表记录中
		if (isBindDiscnt(discntTradeData))
		{
			return true;
		}

		// 是否属于CANCEL_TAG=3的元素
		// IData data =
		// PkgElemInfoQry.getElementByElementId(discntTradeData.getPackageId(),
		// discntTradeData.getElementId());
		// 查询生失效关系
		IDataset dataset = UpcCall.queryEnableModeRelByOfferId(BofConst.ELEMENT_TYPE_CODE_DISCNT, discntTradeData.getElementId());
		if (IDataUtil.isEmpty(dataset))
		{
			return false;
		} else if (StringUtils.equals("3", dataset.getData(0).getString("CANCEL_MODE")))// CANCEL_TAG
		{
			return true;
		}

		return false;
	}

	/**
	 * @methodName: endUserNowValidInfo
	 * @Description: 复机终止除优惠表外其他的所有资料表
	 * @param btd
	 * @throws Exception
	 * @version: v1.0.0
	 * @author: xiaozb
	 * @date: 2014-9-17 上午10:48:30
	 */
	private void endUserNowValidInfo(BusiTradeData btd) throws Exception
	{
		String serialNumber = btd.getRD().getUca().getSerialNumber();
		String acceptTime = btd.getRD().getAcceptTime();
		String lastSecondAcceptTime = SysDateMgr.getLastSecond(acceptTime);
		String userId = btd.getRD().getUca().getUserId();

		List<ProductTradeData> userProductTradeDataList = btd.getRD().getUca().getUserProducts();
		if (userProductTradeDataList != null && userProductTradeDataList.size() > 0)
		{
			for (ProductTradeData tempProductTradeData : userProductTradeDataList)
			{
				if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempProductTradeData.getModifyTag()))
				{
					tempProductTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
					tempProductTradeData.setEndDate(lastSecondAcceptTime);
					btd.add(serialNumber, tempProductTradeData);
				}
			}
		}

		List<SvcTradeData> svcTradeDataList = btd.getRD().getUca().getUserSvcs();
		if (svcTradeDataList != null && svcTradeDataList.size() > 0)
		{
			for (SvcTradeData tempSvcTradeData : svcTradeDataList)
			{
				if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempSvcTradeData.getModifyTag()))
				{
					tempSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
					tempSvcTradeData.setEndDate(lastSecondAcceptTime);
					btd.add(serialNumber, tempSvcTradeData);
				}
			}
		}

		List<SvcStateTradeData> svcStateTradeDataList = btd.getRD().getUca().getUserSvcsState();
		if (svcStateTradeDataList != null && svcStateTradeDataList.size() > 0)
		{
			for (SvcStateTradeData tempSvcStateTradeData : svcStateTradeDataList)
			{
				if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempSvcStateTradeData.getModifyTag()))
				{
					tempSvcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
					tempSvcStateTradeData.setEndDate(lastSecondAcceptTime);
					btd.add(serialNumber, tempSvcStateTradeData);
				}
			}
		}
	}
}
