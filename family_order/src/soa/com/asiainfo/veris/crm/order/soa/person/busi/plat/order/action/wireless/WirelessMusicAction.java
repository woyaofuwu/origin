package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.wireless;

import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

/**
 * 无线音乐平台处理
 * 
 * @author bobo
 */
public class WirelessMusicAction implements IProductModuleAction
{

	@Override
	public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
	{
		PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;

		String operCode = pstd.getOperCode();
		// 如果是退订 或者是数据修改操作
		if (PlatConstants.OPER_CANCEL_ORDER.equals(operCode) || PlatConstants.OPER_USER_DATA_MODIFY.equals(operCode))
		{
			// 如果该用户有通过套餐订购的铃音盒，退订无线音乐平台时，不退订铃音盒，否则退订

			// IDataset resultList =
			// UserPlatSvcInfoQry.queryUserColorRingOrderByTC(uca.getUserId());
			// 1.查询用户彩铃（service_id为20）的订购数据
			List<SvcTradeData> userColorRingList = uca.getUserSvcBySvcId("20");

			if (ArrayUtil.isNotEmpty(userColorRingList))
			{
				for (int i = 0; i < userColorRingList.size(); i++)
				{
					SvcTradeData userColorRing = userColorRingList.get(i);
					String productId = userColorRing.getProductId();
					if ("-1".equals(productId))
					{
						continue;
					}

					IDataset resultList = getPkgElement(productId);
					if (ArrayUtil.isNotEmpty(resultList))
					{
						return;
					}
				}

				// 3.查询user_discnt表，如果存在有效记录；并且查询产商品中心服务
				List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
				for (int ii = 0; ii < userDiscnts.size(); ii++)
				{
					DiscntTradeData userDiscnt = userDiscnts.get(ii);
					String discntCode = userDiscnt.getDiscntCode();

					boolean platSvcLimits = UpcCall.hasSpecificOfferRelThisTwoOffer(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT, "20", BofConst.ELEMENT_TYPE_CODE_SVC, "1,2");
					if (true == platSvcLimits)
					{
						return;
					}
				}

				// 4.查询集团彩铃，如果存在有效记录，则return
				List<SvcTradeData> userGroupColorRingList = uca.getUserSvcBySvcId("910");
				if (ArrayUtil.isNotEmpty(userGroupColorRingList))
				{
					return;
				}
			}

			/*
			 * if (resultList != null && !resultList.isEmpty()) { return; }
			 */

			// 如果用户当前的级别不是特级会员，则不退订铃音盒。从特级变为其它或者退订才退订铃音盒
			IData userWirelessMusicAttr = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(uca.getUserId(), pstd.getInstId(), "302", null);
			if (!"3".equals(userWirelessMusicAttr.getString("ATTR_VALUE", "")))
			{
				return;
			}

			// 如果有中央音乐平台业务
			List<PlatSvcTradeData> cenMusicList = uca.getUserPlatSvcs();
			if (cenMusicList != null && !cenMusicList.isEmpty())
			{
				for (int i = 0; i < cenMusicList.size(); i++)
				{
					PlatSvcTradeData cenMusic = cenMusicList.get(i);
					PlatOfficeData officeData = PlatOfficeData.getInstance(cenMusic.getElementId());
					// 非中央音乐平台业务，不作处理
					if (!"LY".equals(officeData.getBizTypeCode()))
					{
						continue;
					}

					if (!PlatConstants.STATE_CANCEL.equals(cenMusic.getBizStateCode()) && !BofConst.MODIFY_TAG_DEL.equals(cenMusic.getBizStateCode()))
					{
						cenMusic.setModifyTag(BofConst.MODIFY_TAG_DEL);
						cenMusic.setIsNeedPf("0");
						cenMusic.setActiveTag("2"); // 被动
						cenMusic.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
						cenMusic.setOperTime(SysDateMgr.getSysTime());
						cenMusic.setOprSource(pstd.getOprSource());
						cenMusic.setEndDate(SysDateMgr.getSysTime());
						cenMusic.setBizStateCode(PlatConstants.STATE_CANCEL);// 铃音盒关联退订不需要同步彩铃平台
						cenMusic.setRemark("无线音乐特级会员注销或级别变更取消彩铃自动关联退订非会员关联的铃音盒业务");
					}
				}

			}
		}
	}

	private static IDataset getPkgElement(String productId) throws Exception
	{
		IDataset resultList = new DatasetList();
		IData data = UpcCall.queryAllOffersByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "", "", BizRoute.getTradeEparchyCode());

		IDataset offerComRelList = data.getDataset("OFFER_COM_REL_LIST");
		IDataset offerJoinRelList = data.getDataset("OFFER_JOIN_REL_LIST");

		if (ArrayUtil.isNotEmpty(offerComRelList))
		{
			for (int i = 0; i < offerComRelList.size(); i++)
			{
				IData offer = offerComRelList.getData(i);
				String offerCode = offer.getString("OFFER_CODE");
				String offerType = offer.getString("OFFER_TYPE");
				if (StringUtils.equals("20", offerCode) && StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_SVC, offerType))
				{
					resultList.add(offer);
				}
			}
		}

		if (ArrayUtil.isNotEmpty(offerJoinRelList))
		{
			for (int i = 0; i < offerJoinRelList.size(); i++)
			{
				IData offer = offerJoinRelList.getData(i);
				String offerCode = offer.getString("OFFER_CODE");
				String offerType = offer.getString("OFFER_TYPE");
				String selectFlag = offer.getString("SELECT_FLAG");

				if ((StringUtils.equals("1", selectFlag) || StringUtils.equals("3", selectFlag)) && StringUtils.equals("20", offerCode) && StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_SVC, offerType))
				{
					resultList.add(offer);
				}
			}
		}

		return resultList;
	}

}
