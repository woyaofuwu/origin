
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.impu;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ImpuTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.ChangeProductActionUtils;

public class VoLteAutoServiceAction implements ITradeAction
{
	protected static Logger log = Logger.getLogger(VoLteAutoServiceAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		UcaData uca = btd.getRD().getUca();
		String strUserID = uca.getUserId();

 		//判断是否新开190服务 取消190服务
 		boolean isNewVoLte = false;
 		boolean isCancelVoLte = false;
 		//判断是否新开22服务 取消22服务
 		boolean isNewGPRS = false;
 		boolean isCancelGPRS = false;
 		
 		List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		
 		if (CollectionUtils.isNotEmpty(svcTrades) && svcTrades.size() > 0)
 		{
 			for (int i = 0; i < svcTrades.size(); i++) 
 			{
 				SvcTradeData svcTradeData = svcTrades.get(i);
 				if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && "190".equals(svcTradeData.getElementId()))
				{
 					isNewVoLte = true;
 					break;
				}
 				
 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && "190".equals(svcTradeData.getElementId()))
				{
 					isCancelVoLte = true;
 					break;
				}
 			}
 		}
 		
 		String strNewProductID = "";
 		List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
 		if (CollectionUtils.isNotEmpty(productTrades) && productTrades.size() > 0)
 		{
 			for (int i = 0; i < productTrades.size(); i++) 
 			{
 				ProductTradeData productTradeData = productTrades.get(i);
 				
 				if (BofConst.MODIFY_TAG_ADD.equals(productTradeData.getModifyTag()) && "1".equals(productTradeData.getMainTag()))
				{
 					strNewProductID = productTradeData.getProductId();
 					break;
				}
 			}
 		}
 		int nIsAdd23 = 0;
        int nIsDel23 = 0;
        int nIsAdd4200 = 0;
        int nIsDel4200 = 0;
        String strAddSD23 = "";
        String strAddSD4200 = "";
        String strInModeCode23 = CSBizBean.getVisit().getInModeCode();
        
 		if (CollectionUtils.isNotEmpty(svcTrades) && svcTrades.size() > 0)
 		{
 			for (int i = 0; i < svcTrades.size(); i++) 
 			{
 				SvcTradeData svcTradeData = svcTrades.get(i);
 				if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && "22".equals(svcTradeData.getElementId()))
				{
 					isNewGPRS = true;
 					break;
				}
 				
 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && "22".equals(svcTradeData.getElementId()))
				{
 					isCancelGPRS = true;
 					break;
				}
 				
 				if("2".equals(strInModeCode23) || "5".equals(strInModeCode23))
 				{
 					if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && "23".equals(svcTradeData.getElementId()) && "S".equals(svcTradeData.getElementType()))
					{
 						nIsAdd23--;
 						strAddSD23 = svcTradeData.getStartDate();
 						boolean bIsHave = false;
	        			List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId("4200");
	            		if(CollectionUtils.isEmpty(discntDatas))
	            		{
	            			//String strNewProductID = param.getString("NEW_PRODUCT_ID", "");
	            			if(StringUtils.isNotBlank(strNewProductID))
	            			{
	            				//限制9970 TD_S_COMMPARA配置编码
	            				IDataset idsCompare9970 = CommparaInfoQry.getCommparaInfoBy5("CSM", "9970", "checkCallerProduct", strNewProductID, "0898", null);
	            				if(IDataUtil.isEmpty(idsCompare9970))
	            				{
	            					nIsAdd4200++;
	            				}
	            			}
	            			else
	            			{
	            				//限制9970 TD_S_COMMPARA配置编码
	            				IDataset idsCompare9970 = CommparaInfoQry.getCommparaAllCol("CSM", "9970", "checkCallerDiscnt", "0898");
	            				if(IDataUtil.isNotEmpty(idsCompare9970))
	            				{
	            					for (int ii = 0; ii < idsCompare9970.size(); ii++) 
	            					{
	            						IData idCompare9970 = idsCompare9970.getData(ii);
	            						String strElementIDC = idCompare9970.getString("PARA_CODE1", "");
	            						String strElementTypeCodeC = idCompare9970.getString("PARA_CODE2", "");
	            						if("P".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
	            						{
	            							List<ProductTradeData> lsProducts = uca.getUserProduct(strElementIDC);
	            							if(CollectionUtils.isNotEmpty(lsProducts))
	            							{
	            								bIsHave = true;
	            								break;
	            							}
	            						}
	            						else if("S".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
	            						{
	            							List<SvcTradeData> lsSvcs = uca.getUserSvcBySvcId(strElementIDC);
	            							if(CollectionUtils.isNotEmpty(lsSvcs))
	            							{
	            								bIsHave = true;
	            								break;
	            							}
	            						}
	            						else if("D".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
	            						{
	            							List<DiscntTradeData> lsDiscnts = uca.getUserDiscntByDiscntId(strElementIDC);
	            							if(CollectionUtils.isNotEmpty(lsDiscnts))
	            							{
	            								bIsHave = true;
	            								break;
	            							}
	            						}
	            					}
	            				}
	            				if(!bIsHave)
	            				{
	            					nIsAdd4200++;
	            				}
							}
	            		}
					}
	 				
	 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && "23".equals(svcTradeData.getElementId()) && "S".equals(svcTradeData.getElementType()))
					{
	 					nIsDel23--;
	        			List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId("4200");
	            		if(CollectionUtils.isNotEmpty(discntDatas))
	            		{
	            			nIsDel4200++;
	            		}
					}
 				}
 			}
 		}
 		
 		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
 		if (CollectionUtils.isNotEmpty(discntTrades) && discntTrades.size() > 0)
 		{
 			for (int i = 0; i < discntTrades.size(); i++) 
 			{
 				DiscntTradeData discntTradeData = discntTrades.get(i);
 				if("2".equals(strInModeCode23) || "5".equals(strInModeCode23))
 				{
 					if("4200".equals(discntTradeData.getElementId()) && "D".equals(discntTradeData.getElementType()) && "1".equals(discntTradeData.getModifyTag()))
 		            {
 						boolean bIsHave = false;
 		            	nIsDel4200--;
	        			List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId("23");
	            		if(CollectionUtils.isNotEmpty(svcDatas))
	            		{
	            			//strDelED23 = svcDatas.get(0).getStartDate();
	            			//nIsDel23++;
	            			
	            			//String strNewProductID = param.getString("NEW_PRODUCT_ID", "");
	            			if(StringUtils.isNotBlank(strNewProductID))
	            			{
	            				//限制9970 TD_S_COMMPARA配置编码
	            				IDataset idsCompare9970 = CommparaInfoQry.getCommparaInfoBy5("CSM", "9970", "checkCallerProduct", strNewProductID, "0898", null);
	            				if(IDataUtil.isEmpty(idsCompare9970))
	            				{
	            					nIsDel23++;
	            				}
	            			}
	            			else
	            			{
	            				//限制9970 TD_S_COMMPARA配置编码
	            				IDataset idsCompare9970 = CommparaInfoQry.getCommparaAllCol("CSM", "9970", "checkCallerDiscnt", "0898");
	            				if(IDataUtil.isNotEmpty(idsCompare9970))
	            				{
	            					for (int ii = 0; ii < idsCompare9970.size(); ii++) 
	            					{
	            						IData idCompare9970 = idsCompare9970.getData(ii);
	            						String strElementIDC = idCompare9970.getString("PARA_CODE1", "");
	            						String strElementTypeCodeC = idCompare9970.getString("PARA_CODE2", "");
	            						if("P".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
	            						{
	            							List<ProductTradeData> lsProducts = uca.getUserProduct(strElementIDC);
	            							if(CollectionUtils.isNotEmpty(lsProducts))
	            							{
	            								bIsHave = true;
	            								break;
	            							}
	            						}
	            						else if("S".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
	            						{
	            							List<SvcTradeData> lsSvcs = uca.getUserSvcBySvcId(strElementIDC);
	            							if(CollectionUtils.isNotEmpty(lsSvcs))
	            							{
	            								bIsHave = true;
	            								break;
	            							}
	            						}
	            						else if("D".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
	            						{
	            							List<DiscntTradeData> lsDiscnts = uca.getUserDiscntByDiscntId(strElementIDC);
	            							if(CollectionUtils.isNotEmpty(lsDiscnts))
	            							{
	            								bIsHave = true;
	            								break;
	            							}
	            						}
	            					}
	            				}
	            				if(!bIsHave)
	            				{
	            					nIsDel23++;
	            				}
							}
	            		}
 		            }
 					if("4200".equals(discntTradeData.getElementId()) && "D".equals(discntTradeData.getElementType()) && "0".equals(discntTradeData.getModifyTag()))
 		            {
 						nIsAdd4200--;
 						strAddSD4200 = discntTradeData.getStartDate();
 						List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId("23");
	            		if(CollectionUtils.isEmpty(svcDatas))
	            		{
	            			nIsAdd23++;
	            		}
 		            }
 				}
 			}
 		}
 		
 		String strMainTradeTypeCode = btd.getRD().getOrderTypeCode();
 		if(("2".equals(strInModeCode23) || "5".equals(strInModeCode23)) && "110".equals(strMainTradeTypeCode))
		{
	 		if(nIsAdd4200 > 0)
	        {
	 			IData productPkgData = ChangeProductActionUtils.getProductPackageId(uca, "4200", BofConst.ELEMENT_TYPE_CODE_DISCNT);
	 			if(IDataUtil.isNotEmpty(productPkgData))
	 			{
	 				DiscntTradeData discntTradeData = new DiscntTradeData();
		            discntTradeData.setUserId(uca.getUserId());
		            discntTradeData.setElementId("4200");
		            discntTradeData.setElementType("D");
		            discntTradeData.setCampnId("");
		            discntTradeData.setInstId(SeqMgr.getInstId());
		            discntTradeData.setProductId(productPkgData.getString("PRODUCT_ID", "-1"));
		            discntTradeData.setPackageId(productPkgData.getString("PACKAGE_ID", "-1"));
		            discntTradeData.setUserIdA("-1");
		            discntTradeData.setRelationTypeCode("");
		            discntTradeData.setSpecTag("0");
		            if(!"".equals(strAddSD23))
		            {
		            	discntTradeData.setStartDate(strAddSD23);
		            }
		            else 
		            {
						discntTradeData.setStartDate(SysDateMgr.getSysTime());
					}
		            discntTradeData.setEndDate(SysDateMgr.getTheLastTime());
		            discntTradeData.setRemark("来电显示绑定新增彩显优惠包");
		            discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		
		            btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);
	 			}
	        }
	        
	        if(nIsDel4200 > 0)
	        {
	        	boolean bIsDel = false;
	        	//List<DiscntTradeData> es = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	        	if (CollectionUtils.isNotEmpty(discntTrades) && discntTrades.size() > 0)
	     		{
	     			for (int i = 0; i < discntTrades.size(); i++) 
	     			{
	     				DiscntTradeData e = discntTrades.get(i);
     					if("4200".equals(e.getElementId()) && "D".equals(e.getElementType()))
     		            {
     						e.setModifyTag(BofConst.MODIFY_TAG_DEL);
     		    			e.setEndDate(SysDateMgr.getSysTime());
     		    			e.setRemark("来电显示绑定取消彩显优惠包");
     		    			bIsDel = true;
     		    			break;
     		            }
	     			}
	     		}
	        	if(!bIsDel)
	        	{
	        		List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId("4200");
		    		if(CollectionUtils.isNotEmpty(discntDatas))
		    		{
		    			DiscntTradeData dtDiscnt = discntDatas.get(0).clone();
		    			dtDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
		    			dtDiscnt.setEndDate(SysDateMgr.getSysTime());
		    			dtDiscnt.setRemark("来电显示绑定取消彩显优惠包");
		    			btd.add(uca.getSerialNumber(), dtDiscnt);
		    		}
	        	}
	        }
	        
	        if(nIsAdd23 > 0)
	        {
	 			IData productPkgData = ChangeProductActionUtils.getProductPackageId(uca, "23", BofConst.ELEMENT_TYPE_CODE_SVC);
	 			if(IDataUtil.isNotEmpty(productPkgData))
	 			{
	 				SvcTradeData svcTradeData = new SvcTradeData();
		 			svcTradeData.setUserId(uca.getUserId());
		 			svcTradeData.setUserIdA("-1");
		 			svcTradeData.setProductId(productPkgData.getString("PRODUCT_ID", "-1"));
		 			svcTradeData.setPackageId(productPkgData.getString("PACKAGE_ID", "-1"));
		 			svcTradeData.setElementId("23");
		 			svcTradeData.setElementType("S");
		 			svcTradeData.setMainTag("0");
		 			svcTradeData.setInstId(SeqMgr.getInstId());
		 			svcTradeData.setCampnId("");
		            if(!"".equals(strAddSD4200))
		            {
		            	svcTradeData.setStartDate(strAddSD4200);
		            }
		            else 
		            {
		            	svcTradeData.setStartDate(SysDateMgr.getSysTime());
					}
		            svcTradeData.setEndDate(SysDateMgr.getTheLastTime());
		            svcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		            svcTradeData.setRemark("彩显优惠包绑定新增来电显示");
		            svcTradeData.setIsNeedPf("");
		            btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);
	 			}
	        }
	        
	        if(nIsDel23 > 0)
	        {
	        	boolean bIsDel = false;
	        	//List<SvcTradeData> es = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
	        	if (CollectionUtils.isNotEmpty(svcTrades) && svcTrades.size() > 0)
	     		{
	     			for (int i = 0; i < svcTrades.size(); i++)
	     			{
	     				SvcTradeData e = svcTrades.get(i);
     					if("23".equals(e.getElementId()) && "S".equals(e.getElementType()))
     		            {
     						e.setModifyTag(BofConst.MODIFY_TAG_DEL);
     		    			e.setEndDate(SysDateMgr.getSysTime());
     		    			e.setRemark("彩显优惠包绑定取消来电显示服务");
     		    			bIsDel = true;
     		    			break;
     		            }
	     			}
	     		}
	        	if(!bIsDel)
	        	{
	        		List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId("23");
		    		if(CollectionUtils.isNotEmpty(svcDatas))
		    		{
		    			SvcTradeData stSvc = svcDatas.get(0).clone();
		    			stSvc.setModifyTag(BofConst.MODIFY_TAG_DEL);
		    			stSvc.setEndDate(SysDateMgr.getSysTime());
		    			stSvc.setRemark("彩显优惠包绑定取消来电显示服务");
		    			btd.add(uca.getSerialNumber(), stSvc);
		    		}
	        	}
	        }
		}
 		
 		if(isNewVoLte)
 		{
 			
 			//限制9981 TD_S_COMMPARA配置编码
 			IDataset CommparaParas = CommparaInfoQry.getCommByParaAttr("CSM", "9981", "0898");
 			if(IDataUtil.isNotEmpty(CommparaParas))
 			{
 				for (int i = 0; i < CommparaParas.size(); i++) 
 				{
 					IData CommparaPara = CommparaParas.getData(i);
					String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
					String strParamName = CommparaPara.getString("PARAM_NAME", "");
					String strParamCode = CommparaPara.getString("PARAM_CODE", "");
					if("P".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
					{
						
						List<ProductTradeData> productDatas = uca.getUserProduct(strParaCode1);
						if(CollectionUtils.isNotEmpty(productDatas))
						{
							CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
						}
						
					}else if("S".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
					{
						
						List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId(strParaCode1);
						if(CollectionUtils.isNotEmpty(svcDatas))
						{
							CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
						}
						
					}else if("D".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
					{
						
						List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId(strParaCode1);
						if(CollectionUtils.isNotEmpty(discntDatas))
						{
							CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
						}
						
					}
				}
 			}
 		
 		}
 		
 		//大客户备卡激活页面，不可以为有Volte服务的用户，办理2/3G卡激活；
 		List<SvcTradeData> userSvcTD = uca.getUserSvcBySvcId("190");
 		String strTradeTypeCode = btd.getRD().getOrderTypeCode();
 		if(("144".equals(strTradeTypeCode) || "146".equals(strTradeTypeCode)) && CollectionUtils.isNotEmpty(userSvcTD))
 		{
 			checkVipSimBakAct(btd);
 		}
 		else if("110".equals(strTradeTypeCode))
 		{
 			if(isCancelGPRS && CollectionUtils.isNotEmpty(userSvcTD) && !isCancelVoLte)
 	 		{
 				String strInModeCode = CSBizBean.getVisit().getInModeCode();
 				String strAllowInModeCode = "0_2_5";
 				if(strAllowInModeCode.indexOf(strInModeCode) == -1)
 				{
 					CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户有VOLTE服务,先取消VOLTE服务,才能取消GPRS服务");
 				}
 				
 				/**
 				 * QR-20180515-04_20180510082257X777146774VOLTE数据问题
 				 * @author zhuoyingzhi
 				 * @date 20180522
 				 */
				SvcTradeData SvcTrade = userSvcTD.get(0);
				if(BofConst.MODIFY_TAG_INHERIT.equals(SvcTrade.getModifyTag()))
				{
					SvcTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
					SvcTrade.setEndDate(SysDateMgr.getSysTime());
					SvcTrade.setIsNeedPf("");
					SvcTrade.setRemark("用户取消GPRS,同时取消VOLTE_U");
				}
				else
				{
					SvcTradeData SvcTrade1 = userSvcTD.get(0).clone();
					SvcTrade1.setModifyTag(BofConst.MODIFY_TAG_DEL);
					SvcTrade1.setEndDate(SysDateMgr.getSysTime());
					SvcTrade1.setRemark("用户取消GPRS,同时取消VOLTE");
					SvcTrade1.setIsNeedPf("");
					btd.add(uca.getSerialNumber(), SvcTrade1);
				}
	
				isNewVoLte = false;
				isCancelVoLte = true;
 	 		}
 		}
		
		//新入网、退网4G，关联IMS APN签约关系 add by lihb3 20160624
		IDataset idsOther = null;
		boolean isNew4G = false;
 		boolean isCancel4G = false;
		boolean isLimit = true;
		
		if(!isNewVoLte && !isCancelVoLte && "10".equals(strTradeTypeCode))
		{
			//限制9981,VoLteAutoServiceAction TD_S_COMMPARA配置编码
			IDataset CommparaParas = CommparaInfoQry.getCommparaAllCol("CSM", "9981", "VoLteAutoServiceAction", "0898");
			if(IDataUtil.isNotEmpty(CommparaParas))
			{
				IData CommparaPara = CommparaParas.first();
				String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
				if("1".equals(strParaCode1))
				{
					isLimit = true;
				}
				else
				{
					isLimit = false;
				}
			}
			
			if(!isLimit)
			{
				ChangeProductBean bean = (ChangeProductBean) BeanManager.createBean(ChangeProductBean.class);
				IData idLimit = bean.checkVoLTELimit(uca);
				String strResultCode = idLimit.getString("X_RESULTCODE", "2998");
		        String strResultInfo = idLimit.getString("X_RESULTINFO", "4G特殊用户过滤");
		        //log.info("("VoLTE业务自动开通 " + strResultCode + "|" + strResultInfo);
		        if("0".equals(strResultCode))
		        {
		        	List<ResTradeData> resTrades = btd.getTradeDatas(TradeTableEnum.TRADE_RES);	
					if(CollectionUtils.isNotEmpty(resTrades) && resTrades.size() > 0)
					{
						for(int i = 0 ; i < resTrades.size(); i++)
						{
							String strResModifyTag = resTrades.get(i).getModifyTag();
							String strResRsrvTag3 = resTrades.get(i).getRsrvTag3();
							if(BofConst.MODIFY_TAG_ADD.equals(strResModifyTag) && "1".equals(strResRsrvTag3))
							{
								//用户不存在VOLTE服务，则新增 IMS APN 签约。
								if(CollectionUtils.isEmpty(userSvcTD))
								{
									idsOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(strUserID, "VoLteAutoService");
									if(IDataUtil.isEmpty(idsOther))
									{
										isNew4G = true;
										break;
									}			
			 					} 	
							}
							else
							{
								if(BofConst.MODIFY_TAG_ADD.equals(strResModifyTag) && "0".equals(strResRsrvTag3))
								{
									idsOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(strUserID, "VoLteAutoService");
									if(CollectionUtils.isEmpty(userSvcTD) && IDataUtil.isNotEmpty(idsOther))
									{
		 								isCancel4G = true;
		 	 	 	 					break;					
				 					} 	
								}
							}
						}
					}
					
					if(isNew4G)
					{
						
						//IMS APN 签约
						IData idOther = new DataMap();
						idOther.put("SERIAL_NUMBER", uca.getSerialNumber());
						idOther.put("USER_ID", strUserID);
						idOther.put("RSRV_VALUE_CODE", "VoLteAutoService");
						idOther.put("RSRV_VALUE", "1");
						idOther.put("RSRV_STR1", uca.getSerialNumber());
						idOther.put("RSRV_STR2", SysDateMgr.getSysTime());
						idOther.put("RSRV_STR3", CSBizBean.getVisit().getStaffId());
						idOther.put("RSRV_STR4", CSBizBean.getVisit().getStaffName());
						idOther.put("RSRV_STR5", btd.getTradeTypeCode());
						idOther.put("INST_ID", SeqMgr.getInstId());
		        		idOther.put("START_DATE", SysDateMgr.getSysTime());
		        		idOther.put("END_DATE", SysDateMgr.getTheLastTime());
		        		idOther.put("REMARK", "关于下发VoLTE业务自动开通功能支撑系统配套改造工作要求的通知");
		        		idOther.put("END_DATE", SysDateMgr.getTheLastTime());
		        		idOther.put("MODIFY_TAG", "0");
		        		createOtherTradeData(btd, idOther);
					}
					else if(isCancel4G)
					{
						//IMS APN 去签约
						if(IDataUtil.isNotEmpty(idsOther))
						{
							IData idOther = idsOther.first();
							idOther.put("RSRV_VALUE", "0");
							idOther.put("RSRV_STR6", btd.getTradeTypeCode());
							idOther.put("RSRV_STR7", SysDateMgr.getSysTime());
							idOther.put("RSRV_STR8", CSBizBean.getVisit().getStaffId());
							idOther.put("RSRV_STR9", CSBizBean.getVisit().getStaffName());
							idOther.put("SERIAL_NUMBER", uca.getSerialNumber());
							idOther.put("END_DATE", SysDateMgr.getSysTime());
							idOther.put("MODIFY_TAG", "1");
			        		createOtherTradeData(btd, idOther);
						}
					}
		        }
			}
		}
		
		IDataset impuIds = UserImpuInfoQry.queryUserImpuInfo(strUserID);
		
		if(IDataUtil.isNotEmpty(impuIds))//根据UserId能查询到有效的IMPU资料就不添加
		{
			if(isCancelVoLte) //取消190服务，结束IMPU资料
			{
				for (int i = 0; i < impuIds.size(); i++) 
				{
					IData impuId = impuIds.getData(i);
					String imsUserID = impuId.getString("IMS_USER_ID", "");
					String imsPsw = impuId.getString("IMS_PASSWORD", "");
					if(StringUtils.isBlank(imsUserID) && StringUtils.isBlank(imsPsw))
					{
						delImpuInfo(btd, impuIds.getData(i));
					}
				}
			}
			else if(isNewVoLte)
			{
				addImpuInfo(btd);
			}
		}
		else
		{
			if(isNewVoLte)
			{
				addImpuInfo(btd);
			}
		}
	}
	
	/**
	 * 
	 * @param btd
	 * @param input
	 * @throws Exception
	 */
	public void createOtherTradeData(BusiTradeData btd, IData input) throws Exception
    {
    	//String serialNumber = input.getString("SERIAL_NUMBER"); 
    	OtherTradeData tdOther = new OtherTradeData(input);
    	btd.add(btd.getMainTradeData().getSerialNumber(), tdOther);
    }

	/*
	用户开通使用VoLTE，为实现业务接续，需要为用户分配相应的IMS网络用户标识。在
	VoLTE网络环境下，需要支撑系统为用户分配IMPI（IMS用户私有标识）、IMPU（IMS用
	户公有标识）。
	其中IMPI的编码规则为：
	    IMSI@ims.mnc<MNC>.mcc<MCC>.3gppnetwork.org。
	    其中IMSI为用户USIM卡的IMSI，<MNC>为中国移动的移动网络码，<MCC>为中国移动
	的移动国家码。
	    根据《中国移动面向VoLTE的TD-LTE技术体制》要求，MNC及MCC按IMSI中的相关字
	段定义导出获得，不再使用固定取值。
	每个VoLTE用户需要分配3个IMPU，其中2个为SIP URI，1个为TEL URI：
	TEL URI：以“tel:”开头，码号遵循中国移动MSISDN编码规则，以”+86”开头。该码
	号用于呼叫，对用户可见。
	SIP URI 1：其格式为3GPP及GSMA定义的统一导出规则：
	sip:IMSI@ims.mnc<MNC>.mcc<MCC>.3gppnetwork.org。该IMPU仅用于注册，不用于呼
	叫，对用户不可见。MNC/MCC根据实际IMSI定义导出。
	SIP URI 2： TEL URI无法直接在IMS网络中路由，需要ENUM服务器将TEL URI转换成对
	应的SIP URI进行路由，对用户不可见。该SIP URI用于在IMS网络中进行呼叫及路由，
	其格式为sip:MSISDN@归属省名缩写.ims.mnc<MNC>.mcc<MCC>.3gppnetwork.org。为简
	化网络配置工作量，该字段的取值中MNC固定取值为000，MCC取值为460
    */
	private void addImpuInfo (BusiTradeData btd) throws Exception 
	{
		UcaData uca = btd.getRD().getUca();
		String imsi = "";
		String url2 = "@hain.ims.mnc000.mcc460.3gppnetwork.org";
		String usim = "0";//0 sim 卡 1 USIM卡
		String url1 = "";
		
		List<ResTradeData> resTrades = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
		
		if(CollectionUtils.isNotEmpty(resTrades) && resTrades.size() > 0)
		{
			for(int i = 0 ; i < resTrades.size(); i++)
			{
				if(BofConst.MODIFY_TAG_ADD.equals(resTrades.get(i).getModifyTag()) && "1".equals(resTrades.get(i).getResTypeCode()))
				{
					imsi = resTrades.get(i).getImsi();
					
					break;
				}
			}
			
		}
		
		if(StringUtils.isBlank(imsi))
		{
			IDataset ids = BofQuery.queryUserAllValidRes(uca.getUserId(),uca.getUser().getEparchyCode());
			
			if(IDataUtil.isNotEmpty(ids))
			{
				for(int i = 0 ; i < ids.size(); i++)
				{
					if("1".equals(ids.getData(i).getString("RES_TYPE_CODE")))
					{
						imsi = ids.getData(i).getString("IMSI");
						break;
					}
				}
			}
		}

		//REQ201907090013_关于下发转售业务新增VoLTE和多方通话功能的改造要求的通知
		//mengqx 20190916
		//imsi
		if(StringUtils.isBlank(imsi))
		{
			if("5434".equals(btd.getTradeTypeCode())){
				imsi = btd.getMainTradeData().getRsrvStr1();
			}
		}

		
		IDataset simInfos = ResCall.getSimCardInfo("1", "", imsi, "", "00");
		if(IDataUtil.isNotEmpty(simInfos))
		{
			IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simInfos.getData(0).getString("RES_TYPE_CODE"));
	        if (IDataUtil.isNotEmpty(reSet))
	        {
	        	String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
	        	if("01".equals(typeCode))
	        	{
	        		usim = "1";
	        	}
	        }
		}else {
			//REQ201907090013_关于下发转售业务新增VoLTE和多方通话功能的改造要求的通知
			//mengqx 20190916
			//转售号码SIM卡信息在res_sim_resale表中
			IData param = new DataMap();
			param.put("IMSI", imsi);
			IDataset simInfo = ResCall.qryResaleSimByImsi(param);
			if(IDataUtil.isNotEmpty(simInfo))
			{
				String reSet = simInfo.getData(0).getString("CARD_MODE_TYPE");
		        if ("2".equals(reSet))
		        {
		        	usim = "1";
		        }
			}
		}

		//判断是否为USIM卡
		if(!"1".equals(usim))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_3001);
		}
		
		if(StringUtils.isNotBlank(imsi) && imsi.length() >= 5)
		{
			url1 = imsi + "@ims.mnc0"+imsi.substring(3, 5)+".mcc460.3gppnetwork.org";
		}
		
		String strTel = "+86"+uca.getSerialNumber();
				
		ImpuTradeData impuTD = new ImpuTradeData();
		impuTD.setInstId(SeqMgr.getInstId());
		impuTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
		impuTD.setUserId(uca.getUserId());
		impuTD.setTelUrl(strTel);
		impuTD.setSipUrl(url1);
		impuTD.setImpi(url1);
		impuTD.setStartDate(SysDateMgr.getSysTime());
		impuTD.setEndDate(SysDateMgr.getTheLastTime());
		impuTD.setRsrvStr2(getStrToChar(strTel));
		impuTD.setRsrvStr5(strTel+url2);
		btd.add(uca.getSerialNumber(), impuTD);
	}

	private String getStrToChar(String strTel) {
		String tmp = strTel.toString();
		tmp = tmp.replaceAll("\\+", "");
		char[] c = tmp.toCharArray();
		String str2 = "";
		for(int i=c.length-1; i>=0; i--){
			
			str2 += String.valueOf(c[i]);
			str2 += ".";
		}
		str2 += "e164.arpa";
		return str2;
	}
	
	private void delImpuInfo (BusiTradeData btd,IData data) throws Exception 
	{
		UcaData uca = btd.getRD().getUca();
		ImpuTradeData impuTD = new ImpuTradeData(data);
		impuTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
		impuTD.setEndDate(SysDateMgr.getSysTime());
		btd.add(uca.getSerialNumber(), impuTD);
	}
	
	/**
	 * 
	 * @param btd
	 * @throws Exception
	 */
	private void checkVipSimBakAct (BusiTradeData btd) throws Exception 
	{
		
		//UcaData uca = btd.getRD().getUca();
		String imsi = "";
		String usim = "0";//0 sim 卡 1 USIM卡

		List<ResTradeData> resTrades = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
		
		if(CollectionUtils.isNotEmpty(resTrades) && resTrades.size() > 0)
		{
			for(int i = 0 ; i < resTrades.size(); i++)
			{
				if(BofConst.MODIFY_TAG_ADD.equals(resTrades.get(i).getModifyTag()) && "1".equals(resTrades.get(i).getResTypeCode()))
				{
					imsi = resTrades.get(i).getImsi();
					
					break;
				}
			}
			
		}
		
		if(StringUtils.isBlank(imsi))
		{
			IData out = btd.getRD().getPageRequestData();
 			if( IDataUtil.isNotEmpty(out) ){
 				imsi = out.getString("IMSI");
 			}else{
 				return;
 			}
		}
		
		
		IDataset simInfos = ResCall.getSimCardInfo("1", "", imsi, "", "00");
		if(IDataUtil.isNotEmpty(simInfos))
		{
			IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simInfos.getData(0).getString("RES_TYPE_CODE"));
	        if (IDataUtil.isNotEmpty(reSet))
	        {
	        	String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
	        	if("01".equals(typeCode))
	        	{
	        		usim = "1";
	        	}
	        }
		}

		//判断是否为USIM卡
		if(!"1".equals(usim))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户有VOLTE服务，不能办理2/3G卡，请取消VOLTE服务！");
		}
		
	}
	
}
