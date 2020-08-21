package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDateExtend;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;

/**
 * @author Administrator
 */
public class DiscntTrade extends BaseProductModuleTrade implements IProductModuleTrade
{
	@Override
	public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData pmd, List<ProductModuleData> productModuleDatas, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
	{
		DiscntData discntData = (DiscntData) pmd;
		DiscntTradeData discntTradeData = new DiscntTradeData();
		String acceptTime = brd.getAcceptTime();
		if (discntData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
		{
			// if (uca.getUserDiscntByDiscntId(discntData.getElementId()).size()
			// > 0)
			// {
			// String orderMode =
			// StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT",
			// "DISCNT_CODE",
			// "ORDER_MODE", discntData.getElementId());
			// if (!"R".equals(orderMode))
			// {
			// String discntName =
			// StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT",
			// "DISCNT_CODE",
			// "DISCNT_NAME", discntData.getElementId());
			// CSAppException.apperr(ElementException.CRM_ELEMENT_36,
			// discntName);
			// }
			// }
			IData discntConfig = UDiscntInfoQry.getDiscntInfoByPk(discntData.getElementId());
			if (IDataUtil.isEmpty(discntConfig))
			{
				CSAppException.apperr(ElementException.CRM_ELEMENT_140, discntData.getElementId());
			} else
			{
				String batChId = brd.getBatchId();
				if (StringUtils.isNotBlank(batChId))
				{
					String eparchyCode = discntConfig.getString("EPARCHY_CODE");
					if ("".equals(eparchyCode) || null == eparchyCode)
					{
						eparchyCode = "ZZZZ";
					}

					if (StringUtils.isNotBlank(uca.getUserEparchyCode()) && !uca.getUserEparchyCode().equals(eparchyCode) && !"ZZZZ".equals(eparchyCode))
					{
						CSAppException.apperr(ElementException.CRM_ELEMENT_140, discntData.getElementId());
					}
				}

			}
			discntTradeData.setUserId(uca.getUserId());
			discntTradeData.setElementId(discntData.getElementId());
			discntTradeData.setElementType(discntData.getElementType());
			discntTradeData.setCampnId(discntData.getCampnId());
			discntTradeData.setInstId(SeqMgr.getInstId());
			discntTradeData.setProductId(discntData.getProductId());
			discntTradeData.setPackageId(discntData.getPackageId());
			discntTradeData.setUserIdA("-1");
			discntTradeData.setRelationTypeCode("");
			discntTradeData.setSpecTag("0");
			String startDate = ProductModuleCalDate.calStartDate(discntData, env);
			if (startDate.compareTo(brd.getAcceptTime()) < 0 && (env == null || !env.isNoResetStartDate()))
			{
				startDate = acceptTime;
			}
			discntTradeData.setStartDate(startDate);
			String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
			// REQ201603170019 流量快餐业务开发需求 新增END_UNIT=6类型的失效偏移单位，为了不影响原有逻辑，新增判断
			// by songlm 20160323
			if ("6".equals(discntData.getEndUnit()))
			{
				// 由于discntData中已经存在endDate，所以ProductModuleCalDate.calEndDate没有对endDate处理的，endDate还是是discntData的endDate值。
				// discntData中也已经存在startDate，所以ProductModuleCalDate.calEndDate也没有对startDate处理，但后面追加了与acceptTime比较，导致startDate是acceptTime
				// 6类型的按小时偏移，endDate是按照discntData的startDate偏移的，没有按acceptTime偏移，存在数指差
				// 所以对6类型重新再计算下
				String endOffset = discntData.getEndOffSet();
				endDate = SysDateMgr.getAddHoursDate(startDate, Integer.parseInt(endOffset));
				discntTradeData.setEndDate(endDate);
			} else
			{
				discntTradeData.setEndDate(endDate.substring(0, 10) + SysDateMgr.getEndTime235959());
			}
			// end
			discntTradeData.setRemark(discntData.getRemark());
		} else
		{
			if (StringUtils.isBlank(discntData.getInstId()))
			{
				// 如果build层未传入instId,则取用户资料中同一元素编码的第一条记录，如果该元素有多条的话，在build层就要负责将instId设置好
				DiscntTradeData userDiscntData = uca.getUserDiscntByDiscntId(discntData.getElementId()).get(0);
				discntTradeData = userDiscntData.clone();
			} else
			{
				DiscntTradeData userDiscntData = uca.getUserDiscntByInstId(discntData.getInstId());
				if(userDiscntData==null){
	                CSAppException.apperr(CrmCommException.CRM_COMM_103, discntData.getElementId() + "|" + UDiscntInfoQry.getDiscntNameByDiscntCode(discntData.getElementId())  + "|" +  "用户未订购该优惠或已退订！");
				}
				discntTradeData = userDiscntData.clone();
			}
			if (BofConst.MODIFY_TAG_DEL.equals(discntData.getModifyTag()))
			{
				String cancelDate = "";
				if (env == null || StringUtils.isBlank(env.getBasicAbsoluteCancelDate()))
				{
					if ("0".equals(discntData.getCancelTag()))
					{
						cancelDate = SysDateMgr.getLastSecond(acceptTime);
					}
				}
				if (StringUtils.isBlank(cancelDate))
				{
					cancelDate = ProductModuleCalDate.calCancelDate(discntData, env);
				}
				discntTradeData.setEndDate(cancelDate);
			} else if (BofConst.MODIFY_TAG_INHERIT.equals(discntData.getModifyTag()))
			{
				String oldProductId = discntTradeData.getProductId();
				String oldPackageId = discntTradeData.getPackageId();
				discntTradeData.setProductId(discntData.getProductId());
				discntTradeData.setPackageId(discntData.getPackageId());
				discntTradeData.setRsrvStr3(oldProductId);
				discntTradeData.setRsrvStr4(oldPackageId);
			} else if (BofConst.MODIFY_TAG_UPD.equals(discntData.getModifyTag()))
			{
				if (!discntData.getProductId().equals(discntTradeData.getProductId()) || !discntData.getPackageId().equals(discntTradeData.getPackageId()))
				{
					String oldProductId = discntTradeData.getProductId();
					String oldPackageId = discntTradeData.getPackageId();
					discntTradeData.setProductId(discntData.getProductId());
					discntTradeData.setPackageId(discntData.getPackageId());
					discntTradeData.setRsrvStr3(oldProductId);
					discntTradeData.setRsrvStr4(oldPackageId);
				}
			} else if (BofConst.MODIFY_TAG_MOVE.equals(discntData.getModifyTag()))
			{
				discntTradeData.setStartDate(discntData.getStartDate());
				discntTradeData.setEndDate(discntData.getEndDate());
				discntTradeData.setRemark(discntData.getRemark());
				discntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
			} else if (BofConst.MODIFY_TAG_FORCE_END.equals(discntData.getModifyTag()))
			{
				discntTradeData.setEndDate(discntData.getEndDate());
				discntData.setModifyTag(BofConst.MODIFY_TAG_DEL);
			}
		}
		discntTradeData.setModifyTag(discntData.getModifyTag());
		if (discntData.getAttrs() != null && discntData.getAttrs().size() > 0 && !BofConst.MODIFY_TAG_DEL.equals(discntTradeData.getModifyTag()))
		{
			AttrTrade.createAttrTradeData(discntTradeData, discntData.getAttrs(), uca);
		}

		// 特殊的时间计算处理
		String tempStartDate = pmd.getStartDate();
		String tempEndDate = pmd.getEndDate();
		pmd.setStartDate(discntTradeData.getStartDate());
		pmd.setEndDate(discntTradeData.getEndDate());
		ProductModuleCalDateExtend.loadCalElementActions(brd.getTradeType().getTradeTypeCode());
		ProductModuleCalDateExtend.calElementDate(pmd, env, uca, productModuleDatas);
		discntTradeData.setStartDate(pmd.getStartDate());
		discntTradeData.setEndDate(pmd.getEndDate());
		pmd.setStartDate(tempStartDate);
		pmd.setEndDate(tempEndDate);

		return discntTradeData;
	}
}
