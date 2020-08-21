package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class ShoolMainProductChangeAction implements ITradeAction 
{
	private static transient Logger logger = Logger.getLogger(ShoolMainProductChangeAction.class);
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		UcaData uca = btd.getRD().getUca();
		List<ProductTradeData> lsProductTrade = btd.get("TF_B_TRADE_PRODUCT");
		if(CollectionUtils.isEmpty(lsProductTrade))
		{
			return;
		}
		
		ProductTradeData ptProductAdd = null;
		ProductTradeData ptProductDel = null;
		IDataset idsCommparaPara1699Del = null;
		boolean isRelA = false;
		boolean isRelB = false; 
		for (int i = 0; i < lsProductTrade.size(); i++) 
		{
			ProductTradeData ptProduct = lsProductTrade.get(i);
			//String strOldProductId = ptProduct.getOldProductId();
			String strProductId = ptProduct.getProductId();
			String strModifyTag = ptProduct.getModifyTag();
			
			IDataset idsCommparaPara1699 = CommparaInfoQry.getCommparaInfoByCode("CSM", "1699", "ShoolMainProductChange", strProductId, "0898");
			if(IDataUtil.isNotEmpty(idsCommparaPara1699))
	    	{
				if(BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
				{
					isRelB = false;
		    	}
				else
				{
					isRelA = true;
					ptProductDel = ptProduct;
					idsCommparaPara1699Del = idsCommparaPara1699;
					String strInModeCode = CSBizBean.getVisit().getInModeCode();
/*					if(!"0".equals(strInModeCode)&&!"1".equals(strInModeCode))
					{
						IData idCommparaPara1699 = idsCommparaPara1699.first();
						String strParamName = idCommparaPara1699.getString("PARAM_NAME", "");
						String strError = String.format("尊敬的用户您现在使用的[%s]套餐暂不支持通过其它方式变更，可前往营业厅办理。", strParamName);
						strError = idCommparaPara1699.getString("PARA_CODE24", strError);
						CSAppException.apperr(CrmCommException.CRM_COMM_888, strError);
					}*/
				}
			}
			else
			{
				ptProductAdd = ptProduct;
				isRelB = true;
			}
		}
		
		if(isRelA && isRelB) 
		{
			if(ptProductDel != null)
			{
				Boolean isDelElementId = false;
				String strProductId = ptProductAdd.getProductId();
				IDataset idsProductElement = ProductInfoQry.getProductElements(strProductId, "0898");
				if(IDataUtil.isNotEmpty(idsProductElement))
		    	{
					for (int i = 0; i < idsProductElement.size(); i++) 
					{
						IData idProductElement = idsProductElement.getData(i);
						String strEelemntTypeCode = idProductElement.getString("ELEMENT_TYPE_CODE", "");
						String strEelemntID = idProductElement.getString("ELEMENT_ID", "");
						String strEelemntForceTag = idProductElement.getString("ELEMENT_FORCE_TAG", "");
						if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(strEelemntTypeCode) && "20".equals(strEelemntID) && "1".equals(strEelemntForceTag))
						{
							isDelElementId = true;
							break;
						}
					}
		    	}
				
				String strEndDate = ptProductDel.getEndDate();
				if(IDataUtil.isNotEmpty(idsCommparaPara1699Del))
		    	{
					for (int i = 0; i < idsCommparaPara1699Del.size(); i++) 
					{
						IData idCommparaPara1699 = idsCommparaPara1699Del.getData(i);
						String strElementId = idCommparaPara1699.getString("PARA_CODE2", "");
						String strElementType = idCommparaPara1699.getString("PARA_CODE3", "");
						
						if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(strElementType))
						{
							boolean isRel = true;
							List<DiscntTradeData> lsETrade = btd.get("TF_B_TRADE_DISCNT");
							if(CollectionUtils.isNotEmpty(lsETrade))
							{
								for (int k = 0; k < lsETrade.size(); k++)
								{
									DiscntTradeData e = lsETrade.get(k);
									String strType = e.getElementType();
									String strId = e.getElementId();
									String strModifyTag = e.getModifyTag();
									String strInstID = e.getInstId();
									String strStartDate = e.getStartDate();
									if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(strType) && strId.equals(strElementId) && 
									   !BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
									{
										e.setModifyTag(BofConst.MODIFY_TAG_DEL);
										e.setEndDate(strEndDate);
										e.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求。");
										isRel = false;
										
										String strElementName = UDiscntInfoQry.getDiscntNameByDiscntCode(strElementId);
										OtherTradeData otherTradeData = new OtherTradeData();
							            otherTradeData.setUserId(uca.getUserId());
							            otherTradeData.setRsrvValueCode("SHOOLMAINPRODUCT");
							            otherTradeData.setRsrvValue(strElementId);
							            otherTradeData.setRsrvStr10(strModifyTag);
							            otherTradeData.setRsrvStr11(strElementName);
							            otherTradeData.setRsrvStr12(strInstID);
							            otherTradeData.setRsrvStr13("ShoolMainProductChangeAction");
							            otherTradeData.setRsrvStr14(BofConst.ELEMENT_TYPE_CODE_DISCNT);
							            otherTradeData.setStartDate(strStartDate);
							            otherTradeData.setEndDate(strEndDate);
							            otherTradeData.setInstId(SeqMgr.getInstId());
							            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
							            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
							            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							            otherTradeData.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求。");
							            btd.add(uca.getSerialNumber(), otherTradeData);
										break;
									}
								}
							}
							if(isRel)
							{
								List<DiscntTradeData> lsE = uca.getUserDiscntByDiscntId(strElementId);
								if(CollectionUtils.isNotEmpty(lsE))
								{
									DiscntTradeData e = lsE.get(0).clone();
									if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(e.getElementType()) && 
									   e.getElementId().equals(strElementId) && 
									   !BofConst.MODIFY_TAG_ADD.equals(e.getModifyTag()))
									{
										//DiscntTradeData e = lsE.get(0).clone();
										String strInstID = e.getInstId();
										String strStartDate = e.getStartDate();
										String strModifyTag = e.getModifyTag();
										e.setEndDate(strEndDate);
										e.setModifyTag(BofConst.MODIFY_TAG_DEL);
										e.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求");
										btd.add(uca.getSerialNumber(), e);
										
										String strElementName = UDiscntInfoQry.getDiscntNameByDiscntCode(strElementId);
										OtherTradeData otherTradeData = new OtherTradeData();
							            otherTradeData.setUserId(uca.getUserId());
							            otherTradeData.setRsrvValueCode("SHOOLMAINPRODUCT");
							            otherTradeData.setRsrvValue(strElementId);
							            otherTradeData.setRsrvStr10(strModifyTag);
							            otherTradeData.setRsrvStr11(strElementName);
							            otherTradeData.setRsrvStr12(strInstID);
							            otherTradeData.setRsrvStr13("ShoolMainProductChangeAction");
							            otherTradeData.setRsrvStr14(BofConst.ELEMENT_TYPE_CODE_DISCNT);
							            otherTradeData.setStartDate(strStartDate);
							            otherTradeData.setEndDate(strEndDate);
							            otherTradeData.setInstId(SeqMgr.getInstId());
							            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
							            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
							            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							            otherTradeData.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求");
							            btd.add(uca.getSerialNumber(), otherTradeData);
									}
								}
							}
						}
						else if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(strElementType))
						{
							boolean isRel = true;
							List<SvcTradeData> lsETrade = btd.get("TF_B_TRADE_SVC");
							if(CollectionUtils.isNotEmpty(lsETrade))
							{
								for (int k = 0; k < lsETrade.size(); k++)
								{
									SvcTradeData e = lsETrade.get(k);
									String strType = e.getElementType();
									String strId = e.getElementId();
									String strModifyTag = e.getModifyTag();
									String strInstID = e.getInstId();
									String strStartDate = e.getStartDate();
									if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(strType) && 
									   strId.equals(strElementId) && 
									   !BofConst.MODIFY_TAG_ADD.equals(strModifyTag) && 
									   !isDelElementId)
									{
										e.setModifyTag(BofConst.MODIFY_TAG_DEL);
										e.setEndDate(strEndDate);
										e.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求。");
										isRel = false;
										
										String strElementName = USvcInfoQry.getSvcNameBySvcId(strElementId);
										OtherTradeData otherTradeData = new OtherTradeData();
							            otherTradeData.setUserId(uca.getUserId());
							            otherTradeData.setRsrvValueCode("SHOOLMAINPRODUCT");
							            otherTradeData.setRsrvValue(strElementId);
							            otherTradeData.setRsrvStr10(strModifyTag);
							            otherTradeData.setRsrvStr11(strElementName);
							            otherTradeData.setRsrvStr12(strInstID);
							            otherTradeData.setRsrvStr13("ShoolMainProductChangeAction");
							            otherTradeData.setRsrvStr14(BofConst.ELEMENT_TYPE_CODE_SVC);
							            otherTradeData.setStartDate(strStartDate);
							            otherTradeData.setEndDate(strEndDate);
							            otherTradeData.setInstId(SeqMgr.getInstId());
							            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
							            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
							            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							            otherTradeData.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求。");
							            btd.add(uca.getSerialNumber(), otherTradeData);
										break;
									}
								}
							}
							if(isRel)
							{
								List<SvcTradeData> lsE = uca.getUserSvcBySvcId(strElementId);
								if(CollectionUtils.isNotEmpty(lsE))
								{
									SvcTradeData e = lsE.get(0).clone();
									if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(e.getElementType()) && 
									   e.getElementId().equals(strElementId) && 
									   !BofConst.MODIFY_TAG_ADD.equals(e.getModifyTag()) &&
									   !isDelElementId)
									{
										//SvcTradeData e = lsE.get(0).clone();
										String strInstID = e.getInstId();
										String strStartDate = e.getStartDate();
										String strModifyTag = e.getModifyTag();
										e.setEndDate(strEndDate);
										e.setModifyTag(BofConst.MODIFY_TAG_DEL);
										e.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求");
										btd.add(uca.getSerialNumber(), e);
										
										String strElementName = USvcInfoQry.getSvcNameBySvcId(strElementId);
										OtherTradeData otherTradeData = new OtherTradeData();
							            otherTradeData.setUserId(uca.getUserId());
							            otherTradeData.setRsrvValueCode("SHOOLMAINPRODUCT");
							            otherTradeData.setRsrvValue(strElementId);
							            otherTradeData.setRsrvStr10(strModifyTag);
							            otherTradeData.setRsrvStr11(strElementName);
							            otherTradeData.setRsrvStr12(strInstID);
							            otherTradeData.setRsrvStr13("ShoolMainProductChangeAction");
							            otherTradeData.setRsrvStr14(BofConst.ELEMENT_TYPE_CODE_SVC);
							            otherTradeData.setStartDate(strStartDate);
							            otherTradeData.setEndDate(strEndDate);
							            otherTradeData.setInstId(SeqMgr.getInstId());
							            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
							            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
							            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							            otherTradeData.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求");
							            btd.add(uca.getSerialNumber(), otherTradeData);
									}
								}
							}
						}
						else if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(strElementType))
						{
							boolean isRel = true;
							List<PlatSvcTradeData> lsETrade = btd.get("TF_B_TRADE_PLATSVC");
							if(CollectionUtils.isNotEmpty(lsETrade))
							{
								for (int k = 0; k < lsETrade.size(); k++)
								{
									PlatSvcTradeData e = lsETrade.get(k);
									String strType = e.getElementType();
									String strId = e.getElementId();
									String strModifyTag = e.getModifyTag();
									String strInstID = e.getInstId();
									String strStartDate = e.getStartDate();
									if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(strType) && strId.equals(strElementId) && 
									  !BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
									{
										e.setModifyTag(BofConst.MODIFY_TAG_DEL);
										e.setEndDate(strEndDate);
										e.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求。");
										isRel = false;
										
										
										String strElementName = UPlatSvcInfoQry.getSvcNameBySvcId(strElementId);
										OtherTradeData otherTradeData = new OtherTradeData();
							            otherTradeData.setUserId(uca.getUserId());
							            otherTradeData.setRsrvValueCode("SHOOLMAINPRODUCT");
							            otherTradeData.setRsrvValue(strElementId);
							            otherTradeData.setRsrvStr10(strModifyTag);
							            otherTradeData.setRsrvStr11(strElementName);
							            otherTradeData.setRsrvStr12(strInstID);
							            otherTradeData.setRsrvStr13("ShoolMainProductChangeAction");
							            otherTradeData.setRsrvStr14(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
							            otherTradeData.setStartDate(strStartDate);
							            otherTradeData.setEndDate(strEndDate);
							            otherTradeData.setInstId(SeqMgr.getInstId());
							            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
							            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
							            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							            otherTradeData.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求。");
							            btd.add(uca.getSerialNumber(), otherTradeData);
										break;
									}
								}
							}
							if(isRel)
							{
								List<PlatSvcTradeData> lsE = uca.getUserPlatSvcByServiceId(strElementId);
								if(CollectionUtils.isNotEmpty(lsE))
								{
									PlatSvcTradeData e = lsE.get(0).clone();
									if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(e.getElementType()) && e.getElementId().equals(strElementId) 
											&& !BofConst.MODIFY_TAG_ADD.equals(e.getModifyTag()))
									{
										String strInstID = e.getInstId();
										String strStartDate = e.getStartDate();
										String strModifyTag = e.getModifyTag(); 
										String strId = e.getElementId();
										e.setEndDate(strEndDate);
										e.setModifyTag(BofConst.MODIFY_TAG_DEL);
										e.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求");
										e.setIsNeedPf("1");
										e.setOprSource("08");
										e.setOperCode("07");
										e.setBizStateCode("E");
										e.setOperTime(SysDateMgr.getSysTime());
										btd.add(uca.getSerialNumber(), e);
										
										
										//将对应的offer_rel也截止掉@tanzheng@20190301
										List<OfferRelTradeData> listOfferRel = uca.getOfferRelsByRelUserId();
										OfferRelTradeData offerRelTradeData = new OfferRelTradeData();
										for(OfferRelTradeData data : listOfferRel){
											if(data.getOfferCode().equals(strId)){
												offerRelTradeData = data;
												offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
												offerRelTradeData.setEndDate(strEndDate);
												btd.add(uca.getSerialNumber(), offerRelTradeData);
											}
										}
										
										String strElementName = UPlatSvcInfoQry.getSvcNameBySvcId(strElementId);
										OtherTradeData otherTradeData = new OtherTradeData();
							            otherTradeData.setUserId(uca.getUserId());
							            otherTradeData.setRsrvValueCode("SHOOLMAINPRODUCT");
							            otherTradeData.setRsrvValue(strElementId);
							            otherTradeData.setRsrvStr10(strModifyTag);
							            otherTradeData.setRsrvStr11(strElementName);
							            otherTradeData.setRsrvStr12(strInstID);
							            otherTradeData.setRsrvStr13("ShoolMainProductChangeAction");
							            otherTradeData.setRsrvStr14(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
							            otherTradeData.setStartDate(strStartDate);
							            otherTradeData.setEndDate(strEndDate);
							            otherTradeData.setInstId(SeqMgr.getInstId());
							            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
							            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
							            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							            otherTradeData.setRemark("关于音乐套餐用户变更主套餐同时取消咪咕特级会员业务的需求");
							            btd.add(uca.getSerialNumber(), otherTradeData);
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
