
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.cancelchangeproduct;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqSmsSendId;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeUndo;

public class CancelChangeProductUtil
{
	private IDataset addDiscnts = new DatasetList();// 预约产品变更时新增，后续无改动的优惠

	private IDataset afterModDiscnts = new DatasetList();// 后续有改动的优惠

	private IDataset modDiscnts = new DatasetList();// 预约产品变更时改动（可能是继承，删除），后续无改动的优惠

	private IDataset addSvcs = new DatasetList();// 预约产品变更时新增，后续无改动的服务

	private IDataset afterModSvcs = new DatasetList();// 后续有改动的服务

	private IDataset modSvcs = new DatasetList();// 预约产品变更时改动（可能是继承，删除），后续无改动的服务

	private IDataset userDiscnts = new DatasetList();// 用户现有的所有主产品下的优惠

	private IDataset userSvcs = new DatasetList();// 用户现有的所有主产品下的服务

	private IDataset userAfterDiscnts = new DatasetList();// 用户在预约主产品变更之后新加的主产品优惠

	private IDataset userAfterSvcs = new DatasetList();// 用户在预约主产品变更之后新加的主产品下服务

	private IDataset undoConfigs = new DatasetList();

	private IDataset oldProductElements = new DatasetList();// 老主产品下的元素配置

	private IDataset userProducts = new DatasetList();
	
	private IDataset userOfferRels = new DatasetList();
	
	private IDataset addOfferRels = new DatasetList();
	
	private IDataset modOfferRels = new DatasetList();
	
//	private IDataset userPricePlans = new DatasetList();
	
//	private IDataset addPricePlans = new DatasetList();
	
//	private IDataset modPricePlans = new DatasetList();

	private IDataset tradeAttrs = new DatasetList();

	private IDataset bakAttrs = new DatasetList();

	private IData undoDiscntConfig;

	private IData undoSvcConfig;

	private IData undoAttrConfig;
	
//	private IData undoPricePlanConifg;
	
	private IData undoOfferRelConfig;
	
	private IData undoProductConfig;

	private IData mainTrade;

	private String userId;

	private String tradeId;

	private String route;

	private String[] discntKeys;

	private String[] svcKeys;

	private String[] attrKeys;
	
	private String[] offerRelKeys;
	
//	private String[] pricePlanKeys;
	
	private String[] productKeys;

	private String userProductId;

	private StringBuilder errInfo = new StringBuilder();

	private boolean commitMode = false;

	public CancelChangeProductUtil(IData mainTrade, boolean commitMode) throws Exception
	{
		this.mainTrade = mainTrade;
		this.tradeId = mainTrade.getString("TRADE_ID");
		this.userId = mainTrade.getString("USER_ID");
		this.undoConfigs = CommparaInfoQry.getCommparaAllCol("CSM", "6789", "UNDO", mainTrade.getString("TRADE_EPARCHY_CODE"));
		this.route = mainTrade.getString("EPARCHY_CODE");
		this.prepareProducts();
		this.prepareUserDiscnts();
		this.prepareUserSvcs();
		this.prepareUserOfferRels();
//		this.prepareUserPricePlans();
		this.tradeAttrs = TradeAttrInfoQry.getTradeAttrByTradeId(tradeId);
		this.oldProductElements = ProductInfoQry.getProductElements(userProductId, this.route);
		this.undoDiscntConfig = TradeUndo.getUndoParamInfo("TF_B_TRADE_DISCNT", undoConfigs);
		this.undoSvcConfig = TradeUndo.getUndoParamInfo("TF_B_TRADE_SVC", undoConfigs);
		this.undoAttrConfig = TradeUndo.getUndoParamInfo("TF_B_TRADE_ATTR", undoConfigs);
		this.undoOfferRelConfig = TradeUndo.getUndoParamInfo("TF_B_TRADE_OFFER_REL", undoConfigs);
//		this.undoPricePlanConifg = TradeUndo.getUndoParamInfo("TF_B_TRADE_PRICE_PLAN", undoConfigs);
		this.undoProductConfig = TradeUndo.getUndoParamInfo("TF_B_TRADE_PRODUCT", undoConfigs);
		this.discntKeys = StringUtils.split(undoDiscntConfig.getString("KEYS"), ",");
		this.svcKeys = StringUtils.split(undoSvcConfig.getString("KEYS"), ",");
		this.attrKeys = StringUtils.split(undoAttrConfig.getString("KEYS"), ",");
		this.offerRelKeys = StringUtils.split(undoOfferRelConfig.getString("KEYS"), ",");
//		this.pricePlanKeys = StringUtils.split(undoPricePlanConifg.getString("KEYS"), ",");
		this.productKeys = StringUtils.split(undoProductConfig.getString("KEYS"), ",");
		this.bakAttrs = TradeUndo.queryTradeBakInfos(undoAttrConfig, tradeId);
		this.commitMode = commitMode;
	}

	private void delUserElement(IData tradeElement, String tableName, String elementIdKey, String elementTypeCode) throws Exception
	{
		if (commitMode)
		{
			// 校验模式不真正删除，只有提交模式才真正执行数据库动作
			Dao.delete(tableName, tradeElement,this.discntKeys);//都是一样的主键，写死discntkey
			IData attrParam = new DataMap();
			attrParam.put("RELA_INST_ID", tradeElement.getString("INST_ID"));
			attrParam.put("USER_ID", tradeElement.getString("USER_ID"));
			attrParam.put("PARTITION_ID", tradeElement.getString("PARTITION_ID"));
			Dao.delete("TF_F_USER_ATTR", attrParam, new String[]
			{ "USER_ID", "RELA_INST_ID", "PARTITION_ID" });
		}
		String elementId = tradeElement.getString(elementIdKey);
		String delName;
		if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
		{
			delName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
		}
		else
		{
			delName = USvcInfoQry.getSvcNameBySvcId(elementId);
		}
		// 查看后续是否有添加依赖的元素
		IDataset limitElements = ElemLimitInfoQry.queryElementLimitByElementIdB(elementTypeCode, elementId, "2", route);
		for (int i = 0, size = limitElements.size(); i < size; i++)
		{

			IData limitElement = limitElements.getData(i);
			String limitElementType = limitElement.getString("ELEMENT_TYPE_CODE_A");
			String limitElementId = limitElement.getString("ELEMENT_ID_A");
			if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(limitElementType))
			{
				for (int j = 0, jSize = userAfterSvcs.size(); j < jSize; j++)
				{
					IData element = userAfterSvcs.getData(j);
					if (limitElementId.equals(element.getString("SERVICE_ID")) && !"true".equals(element.getString("IS_DEL", "")))
					{// 如果在之前检查是否非老产品下元素时就已经删除过，就不需要再提示
						String dependName = USvcInfoQry.getSvcNameBySvcId(limitElementId);
						errInfo.append("预约产品变更取消将删除您添加的[" + delName + "]，但您后续添加的服务[" + dependName + "]依赖于它<br/>");
					}
				}
			}
			if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(limitElementType))
			{
				for (int j = 0, jSize = userAfterDiscnts.size(); j < jSize; j++)
				{
					IData element = userAfterDiscnts.getData(j);
					if (limitElementId.equals(element.getString("DISCNT_CODE")) && !"true".equals(element.getString("IS_DEL", "")))
					{// 如果在之前检查是否非老产品下元素时就已经删除过，就不需要再提示
						String dependName = UDiscntInfoQry.getDiscntNameByDiscntCode(limitElementId);
						errInfo.append("预约产品变更取消将删除您添加的[" + delName + "]，但您后续添加的优惠[" + dependName + "]依赖于它<br/>");
					}
				}
			}
		}
	}

	private void delUserAttr(IData tradeAttr) throws Exception
	{
		if (commitMode)
		{
			Dao.delete("TF_F_USER_ATTR", tradeAttr,this.attrKeys);
		}
	}
	
	private void delUserOfferRel(IData tradeOfferRel) throws Exception
	{
		if (commitMode)
		{
			Dao.delete("TF_F_USER_OFFER_REL", tradeOfferRel,this.offerRelKeys);
		}
	}
	
//	private void delUserPricePlan(IData tradePricePlan) throws Exception
//	{
//		if (commitMode)
//		{
//			Dao.delete("TF_F_USER_PRICE_PLAN", tradePricePlan,this.pricePlanKeys);
//		}
//	}

	public void doCancelChangeProduct() throws Exception
	{
		this.prepareDiscnts();
		this.prepareSvcs();
		this.prepareOfferRels();
//		this.preparePricePlans();
		this.doUserAfterSvcs();
		this.doUserAfterDiscnts();
		this.doSvcs();
		this.doDiscnts();
		this.doPlatSvcAttrs();
		this.doOfferRels();
//		this.doPricePlans();
		this.modfyUserShareRela();

		if (errInfo.length() > 0)
		{
			CSAppException.apperr(ElementException.CRM_ELEMENT_307, errInfo.toString());
		}
	}

	private void doPlatSvcAttrs() throws Exception
	{
		if (!this.commitMode)
		{
			return;
		}
		IDataset tradeAttrs = TradeAttrInfoQry.getTradeAttrByTradeId(this.tradeId);
		if (IDataUtil.isNotEmpty(tradeAttrs))
		{
			for (int i = 0, size = tradeAttrs.size(); i < size; i++)
			{
				IData tradeAttr = tradeAttrs.getData(i);
				String instType = tradeAttr.getString("INST_TYPE");
				String modifyTag = tradeAttr.getString("MODIFY_TAG");
				if ("Z".equals(instType) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
				{
					tradeAttr.put("PARTITION_ID", StrUtil.getPartition4ById(tradeAttr.getString("USER_ID")));
					this.delUserAttr(tradeAttr);
				}
				else if ("Z".equals(instType) && BofConst.MODIFY_TAG_DEL.equals(modifyTag))
				{
					IData bakAttr = TradeUndo.getBakTradeInfo(this.bakAttrs, tradeAttr, this.attrKeys);
					Dao.update("TF_F_USER_ATTR", bakAttr, this.attrKeys, route);
				}
			}
		}
	}
	
	private void doOfferRels()throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");

		IDataset bakTradeInfos = TradeUndo.queryTradeBakInfos(this.undoOfferRelConfig, tradeId);
		
		if(IDataUtil.isNotEmpty(addOfferRels))
		{
			for (int i = 0, size = addOfferRels.size(); i < size; i++)
			{
				IData tradeOfferRel = addOfferRels.getData(i);
				String relOfferCode = tradeOfferRel.getString("REL_OFFER_CODE");
				String relOfferType = tradeOfferRel.getString("REL_OFFER_TYPE");
				if(StringUtils.equals(relOfferCode, "20") && StringUtils.equals(relOfferType, "S"))
				{//彩铃业务特殊处理，
					String relOfferInsId = tradeOfferRel.getString("REL_OFFER_INS_ID");
					IDataset relOfferInfos20 = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(relOfferInsId);
					if(IDataUtil.isNotEmpty(relOfferInfos20) && relOfferInfos20.size() <2)
					{
						String thisMainProductId = mainTrade.getString("PRODUCT_ID");
						if(StringUtils.equals(thisMainProductId, tradeOfferRel.getString("OFFER_CODE")))
						{
							String userId = tradeOfferRel.getString("USER_ID");
							IDataset svc20s = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId, "20");
							if(IDataUtil.isNotEmpty(svc20s))
							{
								IData svc20 = svc20s.getData(0);
								svc20.put("END_DATE", SysDateMgr.getSysTime());
								Dao.update("TF_F_USER_SVC", svc20, this.svcKeys, route);
							}
						}
					}
				}
				
				tradeOfferRel.put("PARTITION_ID", StrUtil.getPartition4ById(tradeOfferRel.getString("USER_ID")));
				this.delUserOfferRel(tradeOfferRel);
				
			}
		}
		if(IDataUtil.isNotEmpty(modOfferRels))
		{
			for (int i = 0, size = modOfferRels.size(); i < size; i++)
			{
				IData tradeOfferRel = modOfferRels.getData(i);
				IData bakTradeInfo = TradeUndo.getBakTradeInfo(bakTradeInfos, tradeOfferRel, offerRelKeys);
				this.restoreUserTableByBak("TF_F_USER_OFFER_REL", bakTradeInfo, offerRelKeys);
			}
		}
	}
	
//	private void doPricePlans()throws Exception
//	{
//		String tradeId = mainTrade.getString("TRADE_ID");
//
//		IDataset bakTradeInfos = TradeUndo.queryTradeBakInfos(this.undoPricePlanConifg, tradeId);
//		
//		if(IDataUtil.isNotEmpty(addPricePlans))
//		{
//			for (int i = 0, size = addPricePlans.size(); i < size; i++)
//			{
//				IData tradePricePlan = addPricePlans.getData(i);
//				tradePricePlan.put("PARTITION_ID", StrUtil.getPartition4ById(tradePricePlan.getString("USER_ID")));
//				this.delUserPricePlan(tradePricePlan);
//			}
//		}
//		if(IDataUtil.isNotEmpty(modPricePlans))
//		{
//			for (int i = 0, size = modPricePlans.size(); i < size; i++)
//			{
//				IData tradePricePlan = modPricePlans.getData(i);
//				IData bakTradeInfo = TradeUndo.getBakTradeInfo(bakTradeInfos, tradePricePlan, pricePlanKeys);
//				this.restoreUserTableByBak("TF_F_USER_PRICE_PLAN", bakTradeInfo, pricePlanKeys);
//			}
//		}
//	}

	private void doDiscnts() throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");

		IDataset bakTradeInfos = TradeUndo.queryTradeBakInfos(undoDiscntConfig, tradeId);

		if (IDataUtil.isNotEmpty(addDiscnts))
		{
			for (int i = 0, size = addDiscnts.size(); i < size; i++)
			{
				IData tradeDiscnt = addDiscnts.getData(i);
				tradeDiscnt.put("PARTITION_ID", StrUtil.getPartition4ById(tradeDiscnt.getString("USER_ID")));
				this.delUserElement(tradeDiscnt, "TF_F_USER_DISCNT", "DISCNT_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
			}
		}

		if (IDataUtil.isNotEmpty(modDiscnts))
		{
			for (int i = 0, size = modDiscnts.size(); i < size; i++)
			{
				IData tradeDiscnt = modDiscnts.getData(i);
				IData bakTradeInfo = TradeUndo.getBakTradeInfo(bakTradeInfos, tradeDiscnt, discntKeys);
				this.restoreUserElementByBak(tradeDiscnt, bakTradeInfo, "TF_F_USER_DISCNT", "DISCNT_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				this.restoreUserAttr(tradeDiscnt);
			}
		}

		if (IDataUtil.isNotEmpty(afterModDiscnts))
		{
			for (int i = 0, size = afterModDiscnts.size(); i < size; i++)
			{
				IData tradeDiscnt = afterModDiscnts.getData(i);
				IData userDiscnt = this.getElement(tradeDiscnt.getString("INST_ID"), this.userDiscnts);
				if (userDiscnt != null)
				{
					userDiscnt.put("PARTITION_ID", StrUtil.getPartition4ById(userDiscnt.getString("USER_ID")));
					IData pkgConfig = this.getTransElement(oldProductElements, userDiscnt.getString("DISCNT_CODE"), BofConst.ELEMENT_TYPE_CODE_DISCNT);
					if (IDataUtil.isEmpty(pkgConfig))
					{
						// 老产品下没有该优惠的配置,这种情况应该不存在
						// this.delUserDiscnt(discntData);
					}
					else
					{
						userDiscnt.put("PRODUCT_ID", pkgConfig.getString("PRODUCT_ID"));
						userDiscnt.put("PACKAGE_ID", pkgConfig.getString("PACKAGE_ID"));
						//this.updUserElementByData(userDiscnt, "TF_F_USER_DISCNT", discntKeys);
						this.updUserOfferRelByData(userDiscnt, userDiscnt.getString("DISCNT_CODE"), BofConst.ELEMENT_TYPE_CODE_DISCNT, discntKeys);
					}
				}
			}
		}
	}

	private void doSvcs() throws Exception
	{

		String tradeId = mainTrade.getString("TRADE_ID");
		IDataset bakTradeInfos = TradeUndo.queryTradeBakInfos(undoSvcConfig, tradeId);

		if (IDataUtil.isNotEmpty(addSvcs))
		{
			for (int i = 0, size = addSvcs.size(); i < size; i++)
			{
				IData tradeSvc = addSvcs.getData(i);
				tradeSvc.put("PARTITION_ID", StrUtil.getPartition4ById(tradeSvc.getString("USER_ID")));
				this.delUserElement(tradeSvc, "TF_F_USER_SVC", "SERVICE_ID", BofConst.ELEMENT_TYPE_CODE_SVC);
			}
		}

		if (IDataUtil.isNotEmpty(modSvcs))
		{
			for (int i = 0, size = modSvcs.size(); i < size; i++)
			{
				IData tradeSvc = modSvcs.getData(i);
				IData bakTradeInfo = TradeUndo.getBakTradeInfo(bakTradeInfos, tradeSvc, svcKeys);
				this.restoreUserElementByBak(tradeSvc, bakTradeInfo, "TF_F_USER_SVC", "SERVICE_ID", BofConst.ELEMENT_TYPE_CODE_SVC);
				this.restoreUserAttr(tradeSvc);
			}
		}

		if (IDataUtil.isNotEmpty(afterModSvcs))
		{
			for (int i = 0, size = afterModSvcs.size(); i < size; i++)
			{
				IData tradeSvc = afterModSvcs.getData(i);
				IData userSvc = this.getElement(tradeSvc.getString("INST_ID"), this.userSvcs);
				if (userSvc != null)
				{
					userSvc.put("PARTITION_ID", StrUtil.getPartition4ById(userSvc.getString("USER_ID")));
					IData pkgConfig = this.getTransElement(oldProductElements, userSvc.getString("SERVICE_ID"), BofConst.ELEMENT_TYPE_CODE_SVC);
					if (IDataUtil.isEmpty(pkgConfig))
					{
						// 老产品下没有该优惠的配置,这种情况应该不存在
						// this.delUserDiscnt(discntData);
					}
					else
					{
						userSvc.put("PRODUCT_ID", pkgConfig.getString("PRODUCT_ID"));
						userSvc.put("PACKAGE_ID", pkgConfig.getString("PACKAGE_ID"));
						//this.updUserElementByData(userSvc, "TF_F_USER_SVC", svcKeys);
						this.updUserOfferRelByData(userSvc, userSvc.getString("SERVICE_ID"), BofConst.ELEMENT_TYPE_CODE_SVC, svcKeys);
					}
				}
			}
		}
	}

	private void doUserAfterDiscnts() throws Exception
	{
		if (IDataUtil.isNotEmpty(userAfterDiscnts))
		{
			for (int i = 0, size = userAfterDiscnts.size(); i < size; i++)
			{
				IData userAfterDiscnt = userAfterDiscnts.getData(i);
				String elementId = userAfterDiscnt.getString("DISCNT_CODE");
				IData pkgConfig = this.getTransElement(oldProductElements, elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
				if (IDataUtil.isEmpty(pkgConfig))
				{
					// 老产品下没有该优惠的配置，需要删除
					userAfterDiscnt.put("IS_DEL", "true");
					this.delUserElement(userAfterDiscnt, "TF_F_USER_DISCNT", "DISCNT_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				}
				else
				{
					userAfterDiscnt.put("PRODUCT_ID", pkgConfig.getString("PRODUCT_ID"));
					userAfterDiscnt.put("PACKAGE_ID", pkgConfig.getString("PACKAGE_ID"));
					this.updUserElementByData(userAfterDiscnt, "TF_F_USER_DISCNT", discntKeys);
					this.updUserOfferRelByData(userAfterDiscnt, userAfterDiscnt.getString("DISCNT_CODE"), BofConst.ELEMENT_TYPE_CODE_DISCNT, discntKeys);
				}
			}
		}
	}
	
	private void modfyUserShareRela() throws Exception
    {
		String iv_sync_sequence = SeqMgr.getSyncIncreId(); 
		iv_sync_sequence = "99" + iv_sync_sequence.substring(2);
		String strEndDate = SysDateMgr.getLastDateThisMonth();
		IDataset returnData = ShareInfoQry.queryMemberRela(userId, "01");
        IDataset infos = new DatasetList();
        IDataset shareds = new DatasetList();
        if ( IDataUtil.isNotEmpty(returnData) ) {
        	
        	IData mainShare = returnData.getData(0);
        	String shareId = mainShare.getString("SHARE_ID");
        	shareds = ShareInfoQry.queryAllShare(shareId, userId);// 查询已存在所有share信息
            infos = ShareInfoQry.queryAllShareInfo(userId);// 查询已存在所有shareinfo信息
            if (IDataUtil.isNotEmpty(shareds))
            {
                for (int j = 0; j < shareds.size(); j++)
                {
                    IData share = shareds.getData(j);
                    String strSd = share.getString("START_DATE");
                    String sysTime = SysDateMgr.getSysTime();
                    if( sysTime.compareTo(strSd) > 0 ){
                    	strEndDate = SysDateMgr.getLastDateThisMonth();	//不属于预约优惠
                    }else{
                    	strEndDate = sysTime;
                    }
                    String shareInstId = share.getString("SHARE_INST_ID");
                    share.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    share.put("END_DATE", strEndDate);
                    share.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    share.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    share.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    share.put("REMARK", "预约业务取消终止");
                    share.put("SYNC_SEQUENCE", iv_sync_sequence);
                    this.strUserDiscntShare(share);
                    
                    // 根据SHARE_INST_ID删除对应的shareinfo记录
                    if (IDataUtil.isNotEmpty(infos))
                    {
                        for (int n = 0; n < infos.size(); n++)
                        {
                            IData shareInfo = infos.getData(n);
                            if (shareInstId.equals(shareInfo.getString("SHARE_INST_ID")))
                            {
                                shareInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                                shareInfo.put("END_DATE", strEndDate);
                                shareInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                                shareInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                                shareInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                                shareInfo.put("REMARK", "预约业务取消终止");
                                shareInfo.put("SYNC_SEQUENCE", iv_sync_sequence);
                                this.strUserShareInfo(shareInfo);
                            }
                        }
                    }
                    
                }
            }
            mainShare.put("SYNC_SEQUENCE", iv_sync_sequence);
            this.dealShareRela(mainShare);
            
        }
    }
	
	/**
     * 用户资费共享台账子表
     * 
     * @throws Exception
     */
    private void dealShareRela(IData mainShare) throws Exception
    {
    	String strEndDate = SysDateMgr.getLastDateThisMonth();
    	
        // 查询用户下所有可以共享4g资费
        IDataset discntInfos = ShareInfoQry.queryDiscnt(userId);
        if (IDataUtil.isNotEmpty(discntInfos)) {
        	for (int i = 0; i < discntInfos.size(); i++) {
        		IData discntInfo = discntInfos.getData(i);
        		String strEd = discntInfo.getString("DISCNT_END_DATE");
        		if( strEndDate.compareTo(strEd) == 0 ){
        			strEndDate = SysDateMgr.getLastDateThisMonth();
        			break;
        		}
        		
        		if( strEndDate.compareTo(strEd) > 0 ) {
        			strEndDate = strEd;
        		}
            }
        }else{
        	strEndDate = SysDateMgr.getLastDateThisMonth();
        }
        
    	String shareId = mainShare.getString("SHARE_ID");
        // 处理所有资费删除后的关系解除
    	String mainSn = mainShare.getString("SERIAL_NUMBER");
    	String mainUid = mainShare.getString("USER_ID_B");
    	boolean flag = false;//只有预约生效的时候,没有老的共享关系（REQ201811300037“多终端共享”业务优化需求，当前用户没有共享关系，预约主产品变更后，添加副卡有预约生效的共享关系）
        IDataset returnDataMember = ShareInfoQry.queryRelaByShareIdAndRoleCode(shareId,"02");//ShareInfoQry.queryMember(userId);
        if ( IDataUtil.isNotEmpty(returnDataMember) )
        {
        	// 处理副卡
            for (int i = 0; i < returnDataMember.size(); i++)
            {
                IData relaInfo = returnDataMember.getData(i);
                relaInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                relaInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                relaInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                //wangsc10---2019-3-22  对于预约生效的副卡，立即截止。
                String strSd = relaInfo.getString("START_DATE");
                String sysTime = SysDateMgr.getSysTime();
                String strEndDateShare = "";
                if( sysTime.compareTo(strSd) > 0 ){
                	flag = true;
                	strEndDateShare = SysDateMgr.getLastDateThisMonth();	//不属于预约优惠
                }else{
                	strEndDateShare = sysTime;
                }
                //end wangsc10---2019-3-22
                relaInfo.put("END_DATE", strEndDateShare);
                relaInfo.put("REMARK", "预约业务取消终止");
                String sn = relaInfo.getString("SERIAL_NUMBER");
                String uid = relaInfo.getString("USER_ID_B");
                if (commitMode) {
                	Dao.update("TF_F_USER_SHARE_RELA", relaInfo, new String[] { "INST_ID", "PARTITION_ID" });
                }
                
                //流量提醒：您好！ **客户取消共享流量，XXXX年XX月XX日生效。生效后，您将不能使用该客户共享的免费流量。中国移动
                IData smsData = new DataMap(); // 短信数据
                smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
                String strSms = "流量提醒：您好！" + mainSn + "客户取消共享流量，" + strEndDateShare + "生效。生效后，您将不能使用该客户共享的免费流量。中国移动！";
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("RECV_OBJECT", sn);// 接收对象
                smsData.put("RECV_ID", uid);
                smsData.put("NOTICE_CONTENT", strSms);// 短信内容
                this.Ti_O_Sms(smsData);
            }
            
        }
        
        //处理主卡wangsc10---2019-3-22
        if(flag){
        	strEndDate = SysDateMgr.getLastDateThisMonth();
        }else{
        	//加上此判断是防止，共享关系只有主卡有效的时候，在预约业务取消时主卡的截止时间为本月底，而不是立即截止。
        	if(IDataUtil.isNotEmpty(returnDataMember) && returnDataMember.size()>0){
        		strEndDate = SysDateMgr.getSysTime();
        	}else{
        		strEndDate = SysDateMgr.getLastDateThisMonth();
        	}
        }
        //end wangsc10---2019-3-22
        mainShare.put("REMARK", "预约业务取消终止");
        mainShare.put("UPDATE_TIME", SysDateMgr.getSysTime());
        mainShare.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        mainShare.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        mainShare.put("END_DATE", strEndDate);
        this.strUserShareRela(mainShare);
        /*if (commitMode) {
        	Dao.update("TF_F_USER_SHARE_RELA", mainShare, new String[] { "INST_ID", "PARTITION_ID" });
        }*/
        
        //流量提醒：您好！ 您已成功取消共享流量，XXXX年XX月XX日生效。生效后，副号码将不能使用您共享的免费流量。中国移动
        IData smsData = new DataMap(); // 短信数据
        smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
        smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
        String strSms = "流量提醒：您好！ 您已成功取消共享流量，" + strEndDate + "生效。生效后，副号码将不能使用您共享的免费流量。中国移动！";
        smsData.put("FORCE_OBJECT", "10086");// 发送对象
        smsData.put("RECV_OBJECT", mainSn);// 接收对象
        smsData.put("RECV_ID", mainUid);
        smsData.put("NOTICE_CONTENT", strSms);// 短信内容
        this.Ti_O_Sms(smsData);
              
    }
	
	/**
	 * 写短信表TI_O_SMS 原来执行TI_O_SMS-INS_SMSCO_CS，有些值是写死的，这里使用默认值
	 * @param data
	 * @throws Exception
	 */
	private void Ti_O_Sms(IData data) throws Exception{
        IData sendData = new DataMap();
        String sysdate = SysDateMgr.getSysTime();
        String smsNoticeId =  Dao.getSequence(SeqSmsSendId.class , this.route);
        sendData.put("SMS_NOTICE_ID", smsNoticeId);
        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        sendData.put("EPARCHY_CODE",  this.route);
       
        sendData.put("RECV_OBJECT", data.getString("RECV_OBJECT"));// 手机号（服务号）（集团客户经理）也可以扩展其他业务
        sendData.put("RECV_ID", data.getString("RECV_ID", "-1"));// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务

        // 短信截取
        String content = data.getString("NOTICE_CONTENT", "");
        // int charLength = Utility.getCharLength(content, 500);
        //int charLength = getCharLength(content, 4000);
        //content = content.substring(0, charLength);
        sendData.put("NOTICE_CONTENT", content);

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_COUNT_CODE", data.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
        sendData.put("REFERED_COUNT", data.getString("REFERED_COUNT", "0"));// 发送次数？
        sendData.put("CHAN_ID", data.getString("CHAN_ID", "11"));
        sendData.put("RECV_OBJECT_TYPE", data.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
        sendData.put("SMS_TYPE_CODE", "20");// 20用户办理业务通知
        sendData.put("SMS_KIND_CODE", data.getString("SMS_KIND_CODE", "02"));// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", data.getString("NOTICE_CONTENT_TYPE", "0"));// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", data.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
        sendData.put("SMS_PRIORITY", "50");// 短信优先级
        sendData.put("REFER_TIME", data.getString("REFER_TIME", sysdate));// 提交时间
        sendData.put("REFER_STAFF_ID", data.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));// 员工ID
        sendData.put("REFER_DEPART_ID", data.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));// 部门ID
        sendData.put("DEAL_TIME", data.getString("DEAL_TIME", sysdate));// 完成时间
        sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", data.getString("SEND_OBJECT_CODE", "6"));// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", data.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", data.getString("REMARK"));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", data.getString("BRAND_CODE"));
        sendData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
        sendData.put("SMS_NET_TAG", data.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", data.getString("FORCE_OBJECT"));// 发送方号码
        sendData.put("FORCE_START_TIME", data.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", data.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", data.getString("DEAL_STAFFID"));// 完成员工
        sendData.put("DEAL_DEPARTID", data.getString("DEAL_DEPARTID"));// 完成部门
        sendData.put("REVC1", data.getString("REVC1",""));
        sendData.put("REVC2", data.getString("REVC2",""));
        sendData.put("REVC3", data.getString("REVC3",""));
        sendData.put("REVC4", data.getString("REVC4",""));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期
        if (commitMode) {
        	Dao.insert("TI_O_SMS", sendData);
        }
        
    }
	
	/**
     * 用户资费共享台账子表
     * 
     * @throws Exception
     */
    private void strUserDiscntShare(IData data) throws Exception
    {
    	if (commitMode) {
    		Dao.update("TF_F_USER_SHARE", data, new String[] { "SHARE_INST_ID", "PARTITION_ID" });
    		IData param = new DataMap();
    		String iv_sync_sequence = data.getString("SYNC_SEQUENCE", "");
    		String strShareID = data.getString("SHARE_ID");
            param.put("SYNC_SEQUENCE", iv_sync_sequence);
            param.put("SHARE_ID", strShareID);
            param.put("TRADE_ID", this.tradeId);
            
            IDataset shareDatas = Dao.qryByCode("TF_F_USER_SHARE", "SEL_BY_SHAREID_FOR_ASYN", param, BizRoute.getRouteId());
            if(IDataUtil.isNotEmpty(shareDatas))
            {
            	//Dao.executeUpdateByCodeCode("TI_B_USER", "INS_TI_USER_SHARE", shareDatas);
            	Dao.insert("TI_B_USER_SHARE", shareDatas, Route.getJourDb());
            }
		}

    }
    
    /**
     * 用户资费共享信息台账子表
     * 
     * @throws Exception
     */
    private void strUserShareInfo(IData data) throws Exception
    {
    	if (commitMode) {
    		Dao.update("TF_F_USER_SHARE_INFO", data, new String[] { "INST_ID", "PARTITION_ID" });
    		
    		IData param = new DataMap();
    		String iv_sync_sequence = data.getString("SYNC_SEQUENCE", "");
            param.put("SYNC_SEQUENCE", iv_sync_sequence);
            param.put("USER_ID", this.userId);
            param.put("TRADE_ID", this.tradeId);
            
            IDataset shareDatas = Dao.qryByCode("TF_F_USER_SHARE_INFO", "SEL_SHAREINFO_FOR_ASYN", param, BizRoute.getRouteId());
            if(IDataUtil.isNotEmpty(shareDatas))
            {
            	Dao.insert("TI_B_USER_SHARE_INFO", shareDatas, Route.getJourDb());
            }
    	}
    }
    
    /**
     * 用户资费共享关系台账子表
     * @param data
     * @throws Exception
     */
    private void strUserShareRela(IData data) throws Exception
    {
    	if (commitMode) {
        	Dao.update("TF_F_USER_SHARE_RELA", data, new String[] { "INST_ID", "PARTITION_ID" });
        	
        	IData param = new DataMap();
    		String iv_sync_sequence = data.getString("SYNC_SEQUENCE", "");
    		String strShareID = data.getString("SHARE_ID", "");
            param.put("SYNC_SEQUENCE", iv_sync_sequence);
            param.put("SHARE_ID", strShareID);
            param.put("TRADE_ID", this.tradeId);
            
            IDataset shareDatas = Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_SHARE_RELA_FORE_ASYN", param, BizRoute.getRouteId());
            if(IDataUtil.isNotEmpty(shareDatas))
            {
            	Dao.insert("TI_B_USER_SHARE_RELA", shareDatas, Route.getJourDb());
            }
            
            IData synchInfoData = new DataMap();
            synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
            String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
            synchInfoData.put("SYNC_DAY", syncDay);
            synchInfoData.put("SYNC_TYPE", "0");
            synchInfoData.put("TRADE_ID", this.tradeId);
            synchInfoData.put("STATE", "0");
            synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
            synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            Dao.insert("TI_B_SYNCHINFO", synchInfoData,Route.getJourDb());
            
        }
    }

	private void doUserAfterSvcs() throws Exception
	{
		if (IDataUtil.isNotEmpty(userAfterSvcs))
		{
			for (int i = 0, size = userAfterSvcs.size(); i < size; i++)
			{
				IData userAfterSvc = userAfterSvcs.getData(i);
				String elementId = userAfterSvc.getString("SERVICE_ID");
				IData pkgConfig = this.getTransElement(oldProductElements, elementId, BofConst.ELEMENT_TYPE_CODE_SVC);
				if (IDataUtil.isEmpty(pkgConfig))
				{
					// 老产品下没有该优惠的配置，需要删除
					userAfterSvc.put("IS_DEL", "true");
					this.delUserElement(userAfterSvc, "TF_F_USER_SVC", "SERVICE_ID", BofConst.ELEMENT_TYPE_CODE_SVC);
				}
				else
				{
					userAfterSvc.put("PRODUCT_ID", pkgConfig.getString("PRODUCT_ID"));
					userAfterSvc.put("PACKAGE_ID", pkgConfig.getString("PACKAGE_ID"));
					this.updUserElementByData(userAfterSvc, "TF_F_USER_SVC", svcKeys);
					this.updUserOfferRelByData(userAfterSvc, userAfterSvc.getString("SERVICE_ID"), BofConst.ELEMENT_TYPE_CODE_SVC, svcKeys);
				}
			}
		}
	}

	private IData getElement(String instId, IDataset elements)
	{
		if (IDataUtil.isNotEmpty(elements))
		{
			for (int i = 0, size = elements.size(); i < size; i++)
			{
				IData element = elements.getData(i);
				if (instId.equals(element.getString("INST_ID")))
				{
					return element;
				}
			}
		}
		return null;
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
			if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				return element;
			}
		}
		return null;
	}

	private IDataset getUserAfterElements(IDataset userElements, IDataset addElements, IDataset afterModElements, IDataset modElements) throws Exception
	{
		IDataset result = new DatasetList();
		IDataset temps = new DatasetList();
		temps.addAll(addElements);
		temps.addAll(afterModElements);
		temps.addAll(modElements);
		for (int i = 0, size = userElements.size(); i < size; i++)
		{
			IData userElement = userElements.getData(i);
			String instId = userElement.getString("INST_ID");

			if (!this.isMainProductElement(userElement.getString("PRODUCT_ID")))
			{
				continue;
			}
			boolean isFound = false;
			for (int j = 0, tempSize = temps.size(); j < tempSize; j++)
			{
				IData temp = temps.getData(j);
				String tempInstId = temp.getString("INST_ID");
				if (instId.equals(tempInstId))
				{
					isFound = true;
					break;
				}
			}
			if (!isFound)
			{
				//处理J2ee遗留Bug--已经截止的套餐，但是还没有过期的套餐，预约取消时被删掉了
				//根据trade_id,user_id,inst_id检查是否在bak表中存在，如果存在，表示不是后来添加的
				if(userElement.containsKey("DISCNT_CODE") )
				{
					//表示是优惠
					IDataset bakTradeInfos = TradeUndo.queryTradeBakInfos(undoDiscntConfig, this.tradeId); 
					IData bakData = TradeUndo.getBakTradeInfo(bakTradeInfos, userElement, discntKeys); 
					if(bakData != null)
					{
						continue;
					}
				}
				result.add(userElement);
			}
		}
		return result;
	}

	private boolean isMainProductElement(String elementProductId) throws Exception
	{
		int size = this.userProducts.size();
		for (int i = 0; i < size; i++)
		{
			IData ptd = userProducts.getData(i);
			String productMode = ptd.getString("PRODUCT_MODE");
			if (ptd.getString("PRODUCT_ID").equals(elementProductId) && ("00".equals(productMode) || "01".equals(productMode) || "07".equals(productMode) || "09".equals(productMode) || "11".equals(productMode) || "13".equals(productMode)))
			{
				return true;
			}
		}
		return false;
	}
	
	private void prepareOfferRels()throws Exception
	{
		IDataset tradeOfferRels = TradeOfferRelInfoQry.getOfferRelByTradeId(tradeId);
		for (int i = 0, size = tradeOfferRels.size(); i < size; i++)
		{
			IData tradeOfferRel = tradeOfferRels.getData(i);
			String modifyTag = tradeOfferRel.getString("MODIFY_TAG");
			String instId = tradeOfferRel.getString("INST_ID");
			String startDate = tradeOfferRel.getString("START_DATE");
			String endDate = tradeOfferRel.getString("END_DATE");
			IData userOfferRel = this.getElement(instId, userOfferRels);
			if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
			{
				if(userOfferRel == null)
				{
					continue;
				}
				else
				{
					addOfferRels.add(tradeOfferRel);
				}
			}
			else
			{
				if(userOfferRel == null)
				{
					IData params = new DataMap();
					params.put("PARTITION_ID", StrUtil.getPartition4ById(tradeOfferRel.getString("USER_ID")));
					params.put("INST_ID", tradeOfferRel.getString("INST_ID"));
					userOfferRel = Dao.qryByPK("TF_F_USER_OFFER_REL", params);
					if(IDataUtil.isNotEmpty(userOfferRel))
					{
						modOfferRels.add(tradeOfferRel);
					}
				}
				
				if (userOfferRel == null || !startDate.equals(userOfferRel.getString("START_DATE")) || !endDate.equals(userOfferRel.getString("END_DATE")))
				{
					
				}
				else
				{
					modOfferRels.add(tradeOfferRel);
				}
			}
		}
	}
	
//	private void preparePricePlans()throws Exception
//	{
//		IDataset tradePricePlans = TradePricePlanInfoQry.getPricePlanByTradeId(tradeId);
//		for (int i = 0, size = tradePricePlans.size(); i < size; i++)
//		{
//			IData tradePricePlan = tradePricePlans.getData(i);
//			String modifyTag = tradePricePlan.getString("MODIFY_TAG");
//			String instId = tradePricePlan.getString("INST_ID");
//			String startDate = tradePricePlan.getString("START_DATE");
//			String endDate = tradePricePlan.getString("END_DATE");
//			IData userPricePlan = this.getElement(instId, userPricePlans);
//			if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
//			{
//				if(userPricePlan == null)
//				{
//					continue;
//				}
//				else
//				{
//					addPricePlans.add(tradePricePlan);
//				}
//			}
//			else
//			{
//				if(userPricePlan == null)
//				{
//					IData params = new DataMap();
//					params.put("PARTITION_ID", StrUtil.getPartition4ById(tradePricePlan.getString("USER_ID")));
//					params.put("INST_ID", tradePricePlan.getString("INST_ID"));
//					userPricePlan = Dao.qryByPK("TF_F_USER_PRICE_PLAN", params);
//				}
//				
//				if (userPricePlan == null || !startDate.equals(userPricePlan.getString("START_DATE")) || !endDate.equals(userPricePlan.getString("END_DATE")))
//				{
//					
//				}
//				else
//				{
//					modPricePlans.add(tradePricePlan);
//				}
//			}
//		}
//	}

	private void prepareDiscnts() throws Exception
	{
		// 处理优惠
		IDataset tradeDiscnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		for (int i = 0, size = tradeDiscnts.size(); i < size; i++)
		{
			IData tradeDiscnt = tradeDiscnts.getData(i);
			String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
			String instId = tradeDiscnt.getString("INST_ID");
			String startDate = tradeDiscnt.getString("START_DATE");
			String endDate = tradeDiscnt.getString("END_DATE");
			IData userDiscnt = this.getElement(instId, userDiscnts);
			if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
			{
				if (userDiscnt == null)
				{
					// 预约产品变更时新增，但是后续又被删除，这种无需处理
					continue;
				}
				else
				{
					// 预约产品变更时新增，后续再无任何操作，这种需要删除，但是需要考虑后续是否有依赖于它的元素，如果有，需要一并删除
					addDiscnts.add(tradeDiscnt);
				}
			}
			else
			{
				//处理J2ee遗留Bug--处理还没有生效的套餐，做预约产品变更后取消，还没有生效的套餐截止后无法恢复了
				if(userDiscnt == null)
				{
					//根据inst_id在user_discnt表中检索数据，不带生失效时间条件，如果能找到，表示可以归为可恢复数据，否则归为后续修改数据
					IData params = new DataMap();
					params.put("PARTITION_ID", StrUtil.getPartition4ById(tradeDiscnt.getString("USER_ID")));
					params.put("INST_ID", tradeDiscnt.getString("INST_ID"));
					userDiscnt = Dao.qryByPK("TF_F_USER_DISCNT", params);
				}
				
				if (userDiscnt == null || SysDateMgr.compareTo(startDate, userDiscnt.getString("START_DATE"))!=0 || SysDateMgr.compareTo(endDate,userDiscnt.getString("END_DATE"))!=0)
				{
					afterModDiscnts.add(tradeDiscnt);
				}
				else
				{
					modDiscnts.add(tradeDiscnt);
				}
			}
		}
		this.userAfterDiscnts = this.getUserAfterElements(userDiscnts, addDiscnts, afterModDiscnts, modDiscnts);
	}

	private void prepareProducts() throws Exception
	{
		this.userProducts = BofQuery.getUserAllProducts(this.userId, this.route);
		if (IDataUtil.isNotEmpty(userProducts))
		{
			for (int i = 0, size = userProducts.size(); i < size; i++)
			{
				IData userProduct = userProducts.getData(i);
				if ("00".equals(userProduct.getString("PRODUCT_MODE")) && userProduct.getString("START_DATE").compareTo(SysDateMgr.getSysTime()) < 0)
				{
					this.userProductId = userProduct.getString("PRODUCT_ID");
				}
			}
		}
	}

	private void prepareSvcs() throws Exception
	{
		// 处理服务
		IDataset tradeSvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
		for (int i = 0, size = tradeSvcs.size(); i < size; i++)
		{
			IData tradeSvc = tradeSvcs.getData(i);
			String modifyTag = tradeSvc.getString("MODIFY_TAG");
			String instId = tradeSvc.getString("INST_ID");
			String startDate = tradeSvc.getString("START_DATE");
			String endDate = tradeSvc.getString("END_DATE");
			IData userSvc = this.getElement(instId, userSvcs);
			if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
			{
				if (userSvc == null)
				{
					// 预约产品变更时新增，但是后续又被删除，这种无需处理
					continue;
				}
				else
				{
					// 预约产品变更时新增，后续再无任何操作，这种需要删除，但是需要考虑后续是否有依赖于它的元素，如果有，需要一并删除
					addSvcs.add(tradeSvc);
				}
			}
			else
			{
				if (userSvc == null || !startDate.equals(userSvc.getString("START_DATE")) || !endDate.equals(userSvc.getString("END_DATE")))
				{
					afterModSvcs.add(tradeSvc);
				}
				else
				{
					modSvcs.add(tradeSvc);
				}
			}
		}
		this.userAfterSvcs = this.getUserAfterElements(userSvcs, addSvcs, afterModSvcs, modSvcs);
	}

	private void prepareUserDiscnts() throws Exception
	{
		this.userDiscnts = BofQuery.queryUserAllValidDiscnt(this.userId, this.route);
	}

	private void prepareUserSvcs() throws Exception
	{
		this.userSvcs = BofQuery.queryUserAllSvc(this.userId, this.route);
	}
	
//	private void prepareUserPricePlans()throws Exception
//	{
//		this.userPricePlans = BofQuery.queryUserAllPricePlanByUserId(this.userId, this.route);
//	}
	
	private void prepareUserOfferRels()throws Exception
	{
		this.userOfferRels = BofQuery.queryUserAllOfferRelByUserId(this.userId, this.route);
	}

	private void restoreUserAttr(IData tradeElement) throws Exception
	{
		if (!commitMode)
		{
			return;
		}
		String relaInstId = tradeElement.getString("INST_ID");
		IDataset relaAttrs = new DatasetList();
		if (IDataUtil.isNotEmpty(tradeAttrs))
		{
			for (int i = 0, size = tradeAttrs.size(); i < size; i++)
			{
				IData tradeAttr = tradeAttrs.getData(i);
				if (relaInstId.equals(tradeAttr.getString("RELA_INST_ID")))
				{
					relaAttrs.add(tradeAttr);
				}
			}
		}
		if (IDataUtil.isNotEmpty(relaAttrs))
		{
			for (int i = 0, size = relaAttrs.size(); i < size; i++)
			{
				IData attr = relaAttrs.getData(i);
				if (BofConst.MODIFY_TAG_ADD.equals(attr.getString("MODIFY_TAG")))
				{
					attr.put("PARTITION_ID", StrUtil.getPartition4ById(attr.getString("USER_ID")));
					Dao.delete("TF_F_USER_ATTR", attr,this.attrKeys, route);
				}
				else
				{
					IData bakAttr = TradeUndo.getBakTradeInfo(this.bakAttrs, attr, this.attrKeys);
					Dao.update("TF_F_USER_ATTR", bakAttr, this.attrKeys, route);
				}
			}
		}
	}
	
	private void restoreUserTableByBak(String tableName,IData bak,String[] keys) throws Exception
	{
		if (commitMode)
		{
			Dao.update(tableName, bak, keys, route);
		}
	}

	private void restoreUserElementByBak(IData tradeElement, IData bak, String tableName, String elementIdKey, String elementTypeCode) throws Exception
	{
		if(IDataUtil.isEmpty(bak))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据INST_ID=["+tradeElement.getString("INST_ID")+"]未找到对应的备份记录！");
		}
		if (commitMode)
		{
			Dao.update(tableName, bak, discntKeys, route);
		}
		String elementId = bak.getString(elementIdKey);
		String updName;
		if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
		{
			updName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
		}
		else
		{
			updName = USvcInfoQry.getSvcNameBySvcId(elementId);
		}
		if (BofConst.MODIFY_TAG_DEL.equals(tradeElement.getString("MODIFY_TAG")))
		{
			// 查看是否有添加互斥的元素
			IDataset limitElements = ElemLimitInfoQry.queryElementLimitByElementIdA(elementTypeCode, elementId, "0", route);
			for (int i = 0, size = limitElements.size(); i < size; i++)
			{
				IData limitElement = limitElements.getData(i);
				String limitElementType = limitElement.getString("ELEMENT_TYPE_CODE_B");
				String limitElementId = limitElement.getString("ELEMENT_ID_B");
				if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(limitElementType))
				{
					for (int j = 0, jSize = userAfterSvcs.size(); j < jSize; j++)
					{
						IData element = userAfterSvcs.getData(j);
						if (limitElementId.equals(element.getString("SERVICE_ID")) && !"true".equals(element.getString("IS_DEL", "")))
						{// 如果在之前检查是否非老产品下元素时就已经删除过，就不需要再提示
							String mutexName = USvcInfoQry.getSvcNameBySvcId(limitElementId);
							errInfo.append("预约产品变更取消将恢复您的[" + updName + "]，但您后续添加的服务[" + mutexName + "]与它互斥<br/>");
						}
					}
				}
				if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(limitElementType))
				{
					for (int j = 0, jSize = userAfterDiscnts.size(); j < jSize; j++)
					{
						IData element = userAfterDiscnts.getData(j);
						if (limitElementId.equals(element.getString("DISCNT_CODE")) && !"true".equals(element.getString("IS_DEL", "")))
						{// 如果在之前检查是否非老产品下元素时就已经删除过，就不需要再提示
							String mutexName = UDiscntInfoQry.getDiscntNameByDiscntCode(limitElementId);
							errInfo.append("预约产品变更取消将恢复您的[" + updName + "]，但您后续添加的优惠[" + mutexName + "]与它互斥<br/>");
						}
					}
				}
			}
		}
	}

	private void updUserElementByData(IData data, String tableName, String[] keys) throws Exception
	{
		if (commitMode)
		{
			Dao.update(tableName, data, keys, route);
		}
	}
	
	private void updUserOfferRelByData(IData data, String elementId, String elementTypeCode, String[] keys) throws Exception
	{
		if (commitMode)
		{
			if(IDataUtil.isNotEmpty(data))
			{
				String eleStartDate = data.getString("START_DATE");
				String eleEndDate = data.getString("END_DATE");
				String productId = data.getString("PRODUCT_ID");
				String packageId = data.getString("PACKAGE_ID");
				String instId = data.getString("INST_ID");
				IData userMainProduct = new DataMap();
				if (IDataUtil.isNotEmpty(this.userProducts))
				{
					for (int i = 0, size = userProducts.size(); i < size; i++)
					{
						IData userProduct = userProducts.getData(i);
						if ("00".equals(userProduct.getString("PRODUCT_MODE")) && userProduct.getString("START_DATE").compareTo(SysDateMgr.getSysTime()) < 0)
						{
							userMainProduct = userProduct;
						}
					}
				}
				IDataset tradeProdInfos = TradeProductInfoQry.getAllTradeBakProductByTradeId(tradeId);
				IData bakTradeProdInfo = TradeUndo.getBakTradeInfo(tradeProdInfos, userMainProduct, productKeys);
				if(IDataUtil.isNotEmpty(bakTradeProdInfo))
				{
					String offerRelStartDate ="";
					String offerRelEndDate ="";
					
					String offerInsId = bakTradeProdInfo.getString("INST_ID");
					String prodStartDate = bakTradeProdInfo.getString("START_DATE");
					String prodEndDate = bakTradeProdInfo.getString("END_DATE");
					
					if(SysDateMgr.compareTo(eleStartDate, prodStartDate) > 0){
						offerRelStartDate = eleStartDate;
					}
					else{
						offerRelStartDate = prodStartDate;
					}
					if(SysDateMgr.compareTo(eleEndDate, prodEndDate) > 0){
						offerRelEndDate = prodEndDate;
					}
					else{
						offerRelEndDate = eleEndDate;
					}

					IDataset offerrels = UserOfferRelInfoQry.qryUserOfferRelInfosByRelOfferInstId(instId);
					IData elementCfg = ProductElementsCache.getElement(productId, elementId, elementTypeCode);
					if(null == elementCfg || elementCfg.size() == 0)
					{
						return;
					}
					String flag = elementCfg.getString("FLAG");
					
					if("PM_OFFER_COM_REL".equals(flag) || "PM_OFFER_GROUP_REL".equals(flag))
					{
						if(IDataUtil.isNotEmpty(offerrels))
						{
							IData offerRel = offerrels.getData(0);
							
							offerRel.put("OFFER_INST_ID", offerInsId);
							offerRel.put("OFFER_CODE", this.userProductId);
							offerRel.put("GROUP_ID", StringUtils.isNotBlank(elementCfg.getString("GROUP_ID")) ? elementCfg.getString("GROUP_ID") : "0");
							offerRel.put("START_DATE", offerRelStartDate);
							offerRel.put("END_DATE", offerRelEndDate);
							
							Dao.update("TF_F_USER_OFFER_REL", offerRel, keys, route);
						}
						else
						{
							OfferRelTradeData offerRel = new OfferRelTradeData();
							offerRel.setInstId(SeqMgr.getInstId());
							offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
							offerRel.setOfferInsId(offerInsId);
							offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
							offerRel.setOfferCode(productId);
							offerRel.setUserId(bakTradeProdInfo.getString("USER_ID"));
							offerRel.setRelOfferInsId(instId);
							offerRel.setRelOfferType(elementTypeCode);
							offerRel.setRelOfferCode(elementId);
							offerRel.setRelUserId(data.getString("USER_ID"));
							offerRel.setGroupId(StringUtils.isNotBlank(elementCfg.getString("GROUP_ID")) ? elementCfg.getString("GROUP_ID") : "0");
							offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);//构成关系
							offerRel.setStartDate(offerRelStartDate);
							offerRel.setEndDate(offerRelEndDate);
							
							IData newOfferRel = offerRel.toData();
							newOfferRel.put("PARTITION_ID", StrUtil.getPartition4ById(userMainProduct.getString("USER_ID")));
							
							Dao.insert("TF_F_USER_OFFER_REL", newOfferRel);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @author Yanwu
	 * @throws Exception
	 */
	public void checkDiscntByElementLimit3() throws Exception
	{
		IDataset addDiscnts3 = new DatasetList();
        IDataset afterModDiscnts3 = new DatasetList();
        IDataset modDiscnts3 = new DatasetList();
        IDataset userDiscnts3 = new DatasetList();
        //获取备份数据
        IDataset bakTradeInfos = TradeUndo.queryTradeBakInfos(undoDiscntConfig, tradeId);
		// 处理优惠
		IDataset tradeDiscnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		for (int i = 0, size = tradeDiscnts.size(); i < size; i++)
		{
			IData tradeDiscnt = tradeDiscnts.getData(i);
			String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
			String instId = tradeDiscnt.getString("INST_ID");
			String startDate = tradeDiscnt.getString("START_DATE");
			String endDate = tradeDiscnt.getString("END_DATE");
			IData userDiscnt = this.getElement(instId, this.userDiscnts);
			if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
			{
				if (userDiscnt == null)
				{
					// 预约产品变更时新增，但是后续又被删除，这种无需处理
					continue;
				}
				else
				{
					// 预约产品变更时新增，后续再无任何操作，这种需要删除，但是需要考虑后续是否有依赖于它的元素，如果有，需要一并删除
					addDiscnts3.add(tradeDiscnt);
				}
			}
			else
			{
				//处理J2ee遗留Bug--处理还没有生效的套餐，做预约产品变更后取消，还没有生效的套餐截止后无法恢复了
				if(userDiscnt == null)
				{
					//根据inst_id在user_discnt表中检索数据，不带生失效时间条件，如果能找到，表示可以归为可恢复数据，否则归为后续修改数据
					IData params = new DataMap();
					params.put("PARTITION_ID", StrUtil.getPartition4ById(tradeDiscnt.getString("USER_ID")));
					params.put("INST_ID", tradeDiscnt.getString("INST_ID"));
					userDiscnt = Dao.qryByPK("TF_F_USER_DISCNT", params);
				}
				
				if (userDiscnt == null || !startDate.equals(userDiscnt.getString("START_DATE")) || !endDate.equals(userDiscnt.getString("END_DATE")))
				{
					afterModDiscnts3.add(tradeDiscnt);
					userDiscnts3.add(tradeDiscnt);
				}
				else
				{
					modDiscnts3.add(tradeDiscnt);
					IData bakTradeInfo = TradeUndo.getBakTradeInfo(bakTradeInfos, tradeDiscnt, discntKeys);
					userDiscnts3.add(bakTradeInfo);
				}
			}
		}
		
		IDataset userAfterDiscnts3 = this.getUserAfterElements(this.userDiscnts, addDiscnts3, afterModDiscnts3, modDiscnts3);
		
		if (IDataUtil.isNotEmpty(userAfterDiscnts3))
		{
			for (int i = 0, size = userAfterDiscnts3.size(); i < size; i++)
			{
				IData userAfterDiscnt = userAfterDiscnts3.getData(i);
				String elementId = userAfterDiscnt.getString("DISCNT_CODE");
				IData pkgConfig = this.getTransElement(oldProductElements, elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
				if (IDataUtil.isEmpty(pkgConfig))
				{
					// 老产品下没有该优惠的配置，需要删除
					addDiscnts3.add(userAfterDiscnt);
				}
				else
				{
					userDiscnts3.add(userAfterDiscnt);
				}
				
			}
		}
		String strTheMonthLastDay = SysDateMgr.getLastDateThisMonth4WEB();
		String strsYsSD = SysDateMgr.getSysTime();
		
		if( IDataUtil.isNotEmpty(addDiscnts3) )
		{
			for (int i = 0; i < addDiscnts3.size(); i++)
			{
				IData addDiscnt = addDiscnts3.getData(i);
				/*String modifyTag = addDiscnt.getString("MODIFY_TAG");
				String instId = addDiscnt.getString("INST_ID");
				String startDate = addDiscnt.getString("START_DATE");
				String endDate = addDiscnt.getString("END_DATE");*/
				String elementId = addDiscnt.getString("DISCNT_CODE");	//删除元素
				
				// 查询与B元素有限制关系的元素集合
				IDataset limitElementsA = ElemLimitInfoQry.queryElementLimitByElementIdB("D", elementId, "3", this.route);
				if( IDataUtil.isNotEmpty(limitElementsA) )
				{
					for (int ii = 0; ii < limitElementsA.size(); ii++)
					{
						IData limitElementA = limitElementsA.getData(ii);
						String limitElementTypeA = limitElementA.getString("ELEMENT_TYPE_CODE_A");
						String limitElementIdA = limitElementA.getString("ELEMENT_ID_A");	//被依赖元素
						if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(limitElementTypeA))
						{
							boolean IsBreak = false;
							if( IDataUtil.isNotEmpty(this.userDiscnts) ){
								//查询用户目前所有的有效优惠
								for (int iii = 0; iii < this.userDiscnts.size(); iii++)
								{
									IData userDiscntA = this.userDiscnts.getData(iii);
									String elementIdA = userDiscntA.getString("DISCNT_CODE");
									String userSDA = userDiscntA.getString("END_DATE");
									if ( limitElementIdA.equals(elementIdA) && userSDA.compareTo(strsYsSD) > 0 )
									{
										
										// 查询与A元素有限制关系的元素集合
										IDataset limitElementsB = ElemLimitInfoQry.queryElementLimitByElementIdA("D", elementIdA, "3", this.route);
										if( IDataUtil.isNotEmpty(limitElementsB) )
										{
											for (int j = 0; j < limitElementsB.size(); j++)
											{
												IData limitElementB = limitElementsB.getData(j);
												String limitElementTypeB = limitElementB.getString("ELEMENT_TYPE_CODE_B");
												String limitElementIdB = limitElementB.getString("ELEMENT_ID_B");//依赖元素
												if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(limitElementTypeB))
												{
													if( IDataUtil.isNotEmpty(userDiscnts3) )
													{
														for (int jj = 0; jj < userDiscnts3.size(); jj++)
														{
															IData userDiscntB = userDiscnts3.getData(jj);
															String elementIdB = userDiscntB.getString("DISCNT_CODE");
															String userSDB = userDiscntB.getString("END_DATE");
															if ( limitElementIdB.equals(elementIdB) && userSDB.compareTo(strTheMonthLastDay) >= 0 )
															{
																IsBreak = true;
																break;
															}
															
														}
														
														/*if(!IsBreak){
															String elementIdBName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
															String elementIdAName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementIdA);
															//您所预约【elementIdBName】套餐被【elementIdAName】套餐所依赖，不允许取消此工单!
															CSAppException.apperr(ElementException.CRM_ELEMENT_309, elementIdBName, elementIdAName);
														}*/
														
													}
												}
												
												if(IsBreak){
													break;
												}
												
											}
											
											/*if(!IsBreak){
												String elementIdBName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
												String elementIdAName = UDiscntInfoQry.getDiscntNameByDiscntCode(limitElementIdA);
												//您所预约【elementIdBName】套餐被【elementIdAName】套餐所依赖，不允许取消此工单!
												CSAppException.apperr(ElementException.CRM_ELEMENT_309, elementIdBName, elementIdAName);
											}*/
											
										}
										
										if(!IsBreak){
											String elementIdBName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
											String elementIdAName = UDiscntInfoQry.getDiscntNameByDiscntCode(limitElementIdA);
											//您所预约【elementIdBName】套餐被【elementIdAName】套餐所依赖，不允许取消此工单!
											CSAppException.apperr(ElementException.CRM_ELEMENT_309, elementIdBName, elementIdAName);
										}

									}
									
									if(IsBreak){
										break;
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
