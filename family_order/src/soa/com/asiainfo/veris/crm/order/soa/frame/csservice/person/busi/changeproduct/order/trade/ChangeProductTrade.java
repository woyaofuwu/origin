
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;


public class ChangeProductTrade extends BaseTrade implements ITrade
{
	private static final Logger log = Logger.getLogger(ChangeProductTrade.class);

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception
	{
		BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();
		UcaData uca = request.getUca();
		String productChangeDate = null;
		String oldProductEndDate = null;
		boolean isProductChange = false;
		boolean bIsUserSelected = false;
		boolean issvcdata=false;
		String newProductInitId="";
		// 取得用户选择开通关闭或者修改的元素
		List<ProductModuleData> userSelected = request.getProductElements();
		if (userSelected == null)
		{
			userSelected = new ArrayList<ProductModuleData>();
		}
		List<ProductModuleData> operElements = new ArrayList<ProductModuleData>();
		// operElements.addAll(userSelected);
		if (request.getNewMainProduct() != null && !uca.getProductId().equals(request.getNewMainProduct().getProductId()) /*&& !bIsUserSelected*/)
		{
			for (int i = 0; i < userSelected.size(); i++) 
			{
				//boolean bIsAdd = true;
				ProductModuleData pmdUserSelected = userSelected.get(i);
				String strElementId = pmdUserSelected.getElementId();
				String strModifyTag = pmdUserSelected.getModifyTag();
				String strElementType = pmdUserSelected.getElementType();
				int nSize = uca.getUserSvcBySvcId(strElementId).size();
				if ("0".equals(strModifyTag) && "S".equals(strElementType) && "20".equals(strElementId) && nSize > 0)
	            {
//	                String orderMode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_SERVICE", "SERVICE_ID", "ORDER_MODE", strElementId);
					String orderMode =  OfferCfg.getInstance(strElementId, BofConst.ELEMENT_TYPE_CODE_SVC).getOrderMode();
	                if (!"R".equals(orderMode))
	                {
	                	IDataset elementList = PkgElemInfoQry.getElementByPIdElemId(request.getNewMainProduct().getProductId(), "S", "20", "0898") ;
	                	if(IDataUtil.isNotEmpty(elementList) && elementList.size() > 0)
	                    {
	                    	IData elemet = elementList.first();
	                        if("1".equals(elemet.getString("FORCE_TAG"))) //新产品彩铃是必选，如果存在平台业务的彩铃了，则不给予办理产品变更。要求先取消平台业务彩铃才能办理。
	                        {
	                        	//String strProductID = elemet.getString("PRODUCT_ID", "");
	                        	String strPackageID = elemet.getString("PACKAGE_ID", "");
	                        	List<SvcTradeData> lsSvc = uca.getUserSvcBySvcId(strElementId);
	                        	if(lsSvc != null && lsSvc.size() > 0)// 判断如果变更的主产品里存在必选的元素，但在当前服务表或者优惠表已经存在了，只需要新增一条offer_rel，注意不需要原来的修改彩铃 user_id_a的逻辑了。
	                        	{
	                        		bIsUserSelected = true;
	                        		SvcTradeData tdSvc = lsSvc.get(0);
	                        		List<ProductTradeData> OldProductInfo = uca.getUserProduct(uca.getProductId());
	                        		List<OfferRelTradeData> lsOffer_rel = uca.getOfferRelByRelUserIdAndOfferInsId(OldProductInfo.get(0).getInstId());
	                        		
	                        		for(int count =0; count<lsOffer_rel.size();count++){//判断当前主产品下是否有20服务，有就不添加关系，否则添加关系。
	                        			OfferRelTradeData temp=lsOffer_rel.get(count);
	                        			if("20".equals(temp.getRelOfferCode()) && "S".equals(temp.getRelOfferType())){
	                        				issvcdata=true;
	                        				break;
	                        			}
	                        		}
	                        		if(!issvcdata){// add by hefeng
	                        			//新增彩铃的构成关系
	                        			newProductInitId=SeqMgr.getInstId();
	                                    OfferRelTradeData offerRel = new OfferRelTradeData();
	                        			offerRel.setInstId(SeqMgr.getInstId());
	                        			offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
	                        			offerRel.setOfferInsId(newProductInitId);
	                        			offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
	                        			offerRel.setOfferCode(request.getNewMainProduct().getProductId());
	                        			offerRel.setGroupId(strPackageID);
	                        			offerRel.setUserId(uca.getUserId());
	                        			offerRel.setRelOfferInsId(tdSvc.getInstId());
	                        			offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_SVC);
	                        			offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);
	                        			offerRel.setRelOfferCode(tdSvc.getElementId());
	                        			offerRel.setRelUserId(uca.getUserId());
	                        			if(request.isBookingTag()){
	                        				offerRel.setStartDate(request.getBookingDate());
	                        			}else{
	                        				offerRel.setStartDate(SysDateMgr.getSysTime());
	                        			}
	                        			offerRel.setEndDate(tdSvc.getEndDate());
	                        			offerRel.setRemark("主产品变更操作由于彩铃是必选的数据生成关联关系");
                        			   btd.add(uca.getSerialNumber(), offerRel);
	                        		}
	                        	}
	                        	//bIsAdd = false;
	                        	continue;
	                        }
	                    }
	                }
	            }
				//主产品变更，由带必选彩铃服务产品变到无彩铃服务的新产品，添加到operElements，删除彩铃时需做特殊判断
				//若该彩铃服务有多条有效的offer_rel记录，则抛错不允许办理主产品变更
				if("1".equals(strModifyTag) && "S".equals(strElementType) && "20".equals(strElementId) && nSize > 0){
					List<SvcTradeData> getUserSvc = uca.getUserSvcBySvcId("20");        		
            		List<OfferRelTradeData> lsSvcOffer_rel = uca.getOfferRelByRelUserIdAndRelOfferInsId(getUserSvc.get(0).getInstId());
					for(int count =0; count<lsSvcOffer_rel.size();count++){//删除判断当前主产品下是否有20服务，如果存在关系并大于一条就提示有依赖不能取消
            			OfferRelTradeData temp=lsSvcOffer_rel.get(count);
            			if("20".equals(temp.getRelOfferCode()) && "S".equals(temp.getRelOfferType()) && !uca.getProductId().equals(temp.getOfferCode()) ){
                        		 String product_id=temp.getOfferCode();
                        		 String service_name="";
                        		 if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(temp.getOfferType())){
                        			 service_name=UPlatSvcInfoQry.getSvcNameBySvcId(product_id);
                        		 }else{
                        			 service_name=UProductInfoQry.getProductNameByProductId(product_id);
                        		 }
                        		 CSAppException.apperr(ProductException.CRM_PRODUCT_522,"【服务】[20|彩铃]不能删除,因为它被用户的产品【"+service_name+"】所依赖！"); 
            		    }
            		}
				}
				operElements.add(pmdUserSelected);
			}
		}
		else 
		{
			for (int i = 0; i < userSelected.size(); i++) 
			{  //判断彩铃服务删除的情况: 在产品变更里取消元素时，需要判断offer_rel里是否还存在其他的关系，如果存在则提示还存在XX依赖YY不允许退订，如果仅仅只剩主产品与该元素的关系，则允许退订。
				//boolean bIsAdd = true;
				ProductModuleData pmdUserSelected = userSelected.get(i);
				String strElementId = pmdUserSelected.getElementId();
				String strModifyTag = pmdUserSelected.getModifyTag();
				String strElementType = pmdUserSelected.getElementType();				
				if ("1".equals(strModifyTag) && "S".equals(strElementType) && "20".equals(strElementId) )
	            {
					List<ProductTradeData> ProductInfo = uca.getUserProduct(uca.getProductId());            		
            		List<SvcTradeData> getUserSvc = uca.getUserSvcBySvcId("20");        		
            		List<OfferRelTradeData> lsSvcOffer_rel = uca.getOfferRelByRelUserIdAndRelOfferInsId(getUserSvc.get(0).getInstId());
					ProductTradeData nextProduct = uca.getUserNextMainProduct();
					String productId=uca.getProductId();
					String strNewProductID = "";
					String strOldProductID = uca.getProductId();
					boolean isShoppingCart=false;
					if (nextProduct != null)//判断是否有预约产品
					{
						productId=nextProduct.getProductId();
						strNewProductID = nextProduct.getProductId();
					}
					
					if(lsSvcOffer_rel.size() > 1)
					{
						for(int count =0; count<lsSvcOffer_rel.size();count++)
						{
							//删除判断当前主产品下是否有20服务，如果存在关系并大于一条就提示有依赖不能取消
	            			OfferRelTradeData temp = lsSvcOffer_rel.get(count);
	            			
	            			if(strNewProductID.equals(temp.getOfferCode()) || strOldProductID.equals(temp.getOfferCode()))
	            			{
	            				continue;
	            			}
	            			else if("20".equals(temp.getRelOfferCode()) && "S".equals(temp.getRelOfferType()) && !productId.equals(temp.getOfferCode()) )
	            			{
	                        		 String product_id=temp.getOfferCode();
	                        		 String service_name="";
	                        		 if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(temp.getOfferType()))
	                        		 {
	                        			 service_name=UPlatSvcInfoQry.getSvcNameBySvcId(product_id);
	                        		 }
	                        		 else
	                        		 {
	                        			 service_name=UProductInfoQry.getProductNameByProductId(product_id);
	                        		 }
	                        		 isShoppingCart = isShoppingTrade(btd,product_id);
                                    if (!isShoppingCart)
                                    {
                                        CSAppException.apperr(ProductException.CRM_PRODUCT_522, "【服务】[20|彩铃]不能删除,因为它被用户的产品【" + service_name + "】所依赖！");
                                    }
	            		    }
	            		}
					}
	            }
			}
			operElements.addAll(userSelected);
		}
		

		btd.getMainTradeData().setRsrvStr1(uca.getBrandCode());
		btd.getMainTradeData().setRsrvStr2(uca.getProductId());
		
		if(btd.getRD().getPageRequestData().getString("WDACTICE_ENDFLAG")!=null){
			btd.getMainTradeData().setRsrvStr10(btd.getRD().getPageRequestData().getString("WDACTICE_ENDFLAG"));
		}
		
		// 设置预约时间 不是预约 设置为受理时间
		if (request.isBookingTag())
		{
			btd.getMainTradeData().setRsrvStr3(request.getBookingDate());
		}
		else
		{
			btd.getMainTradeData().setRsrvStr3(request.getAcceptTime());
		}
		
		if(StringUtils.isNotBlank(request.getRsrvStr7()))
		{
			btd.getMainTradeData().setRsrvStr7(request.getRsrvStr7());
		}
		
		if(StringUtils.isNotBlank(request.getRsrvStr8()))
		{
			btd.getMainTradeData().setRsrvStr8(request.getRsrvStr8());
		}
		
		OfferUtil.fillStructAndFilter(uca.getUserSvcs(), uca.getOfferRelsByRelUserId());
		OfferUtil.fillStructAndFilter(uca.getUserDiscnts(), uca.getOfferRelsByRelUserId());

		if (request.getNewMainProduct() != null && !uca.getProductId().equals(request.getNewMainProduct().getProductId()))
		{
			if (uca.getUserNextMainProduct() != null)
			{
				CSAppException.apperr(ProductException.CRM_PRODUCT_195);
			}
			// 主产品变更
			isProductChange = true;
			btd.getMainTradeData().setProductId(request.getNewMainProduct().getProductId());
			btd.getMainTradeData().setBrandCode(request.getNewMainProduct().getBrandCode());
			// 计算新的主产品的生效时间和老主产品的失效时间
			String newProductId = request.getNewMainProduct().getProductId();
			String oldProductId = request.getUca().getProductId();
			if (request.isEffectNow())
			{
				productChangeDate = request.getAcceptTime();
			}
			else if (request.isBookingTag())
			{
				productChangeDate = request.getBookingDate();
			}
			else
			{
				productChangeDate = this.getProductChangeDate(oldProductId, newProductId, request);
			}
			oldProductEndDate = SysDateMgr.getLastSecond(productChangeDate);
			// 生成主产品的相关台帐
			this.createProductTrade(newProductId, oldProductId, productChangeDate, oldProductEndDate, request, btd,newProductInitId);
			List<ProductTradeData> userProducts = uca.getUserProducts();
			
			// 拼装需要继承的元素
			List<SvcTradeData> userSvcs = uca.getUserSvcs();
			List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
			
			int svcSize = userSvcs.size();
			int discntSize = userDiscnts.size();
			IDataset newProductElements = ProductElementsCache.getProductElements(newProductId);
			for (int i = 0; i < svcSize; i++)
			{
				SvcTradeData userSvc = userSvcs.get(i);
				if (!this.isNeedDeal(userProducts, userSvc.getProductId()))
				{
					continue;
				}
				if (userSvc.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && !this.isExistInUserSelected(BofConst.ELEMENT_TYPE_CODE_SVC, userSvc.getElementId(), userSvc.getInstId(), userSelected))
				{
					// 是否能转换到新产品，如果能，则继承，不能，则删除
					IData transElement = this.getTransElement(newProductElements, userSvc.getElementId(), userSvc.getElementType());
					if (transElement != null)
					{
						if (!userSvc.getProductId().equals(transElement.getString("PRODUCT_ID")) || !userSvc.getPackageId().equals(transElement.getString("PACKAGE_ID")))
						{
							SvcTradeData svcTradeData = userSvc.clone();
							svcTradeData.setRsrvStr3(svcTradeData.getProductId());
							svcTradeData.setRsrvStr4(svcTradeData.getPackageId());
							svcTradeData.setModifyTag(BofConst.MODIFY_TAG_INHERIT);
							svcTradeData.setProductId(transElement.getString("PRODUCT_ID"));
							svcTradeData.setPackageId(transElement.getString("PACKAGE_ID"));
							btd.add(uca.getSerialNumber(), svcTradeData);
						}
					}
					else
					{
						// 不能继承,则删除   接口进来主产品变更，由带必选彩铃服务产品变到无彩铃服务的新产品，不能做继承处理，删除时需做特殊判断
						//若该彩铃服务有多条有效的offer_rel记录，则抛错不允许办理主产品变更
						if("20".equals(userSvc.getElementId()) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(userSvc.getElementType())){
							List<OfferRelTradeData> lsSvcOffer_rel = uca.getOfferRelByRelUserIdAndRelOfferInsId(userSvc.getInstId());
							for(int count =0; count<lsSvcOffer_rel.size();count++){//删除判断当前主产品下是否有20服务，如果存在关系并大于一条就提示有依赖不能取消
		            			OfferRelTradeData temp=lsSvcOffer_rel.get(count);
		            			if("20".equals(temp.getRelOfferCode()) && "S".equals(temp.getRelOfferType()) && !uca.getProductId().equals(temp.getOfferCode()) ){
		                        		 String product_id=temp.getOfferCode();
		                        		 String service_name="";
		                        		 if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(temp.getOfferType())){
		                        			 service_name=UPlatSvcInfoQry.getSvcNameBySvcId(product_id);
		                        		 }else{
		                        			 service_name=UProductInfoQry.getProductNameByProductId(product_id);
		                        		 }
		                        		 CSAppException.apperr(ProductException.CRM_PRODUCT_522,"【服务】[20|彩铃]不能删除,因为它被用户的产品【"+service_name+"】所依赖！"); 
		            		    }
		            		}
						}
						SvcData delSvcData = new SvcData(userSvc.toData());
						delSvcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
						operElements.add(delSvcData);
					}
				}
			}
			for (int i = 0; i < discntSize; i++)
			{
				DiscntTradeData userDiscnt = userDiscnts.get(i);
				if (userDiscnt.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && !this.isExistInUserSelected(BofConst.ELEMENT_TYPE_CODE_DISCNT, userDiscnt.getElementId(), userDiscnt.getInstId(), userSelected))
				{
					if (!this.isNeedDeal(userProducts, userDiscnt.getProductId()))
					{
						continue;
					}
					// 是否能转换到新产品，如果能，则继承，不能，则删除
					IData transElement = this.getTransElement(newProductElements, userDiscnt.getElementId(), userDiscnt.getElementType());
					if (transElement != null)
					{
						if (!userDiscnt.getProductId().equals(transElement.getString("PRODUCT_ID")) || !userDiscnt.getPackageId().equals(transElement.getString("PACKAGE_ID")))
						{
							
							DiscntTradeData discntTradeData = userDiscnt.clone();
							discntTradeData.setRsrvStr3(userDiscnt.getProductId());
							discntTradeData.setRsrvStr4(userDiscnt.getPackageId());
							discntTradeData.setModifyTag(BofConst.MODIFY_TAG_INHERIT);
							discntTradeData.setProductId(transElement.getString("PRODUCT_ID"));
							discntTradeData.setPackageId(transElement.getString("PACKAGE_ID"));
							btd.add(uca.getSerialNumber(), discntTradeData);
						}
					}
					else
					{
						// 不能继承,则删除
						DiscntData delDiscnt = new DiscntData(userDiscnt.toData());
						delDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
						// delDiscnt.setEndDate(oldProductEndDate);屏蔽后续重算 接口过来流量包时间会有问题
						operElements.add(delDiscnt);
					}
				}
			}
			// 针对元素修改时的处理，需要转换产品ID和包ID
			int selectSize = userSelected.size();
			for (int i = 0; i < selectSize; i++)
			{
				ProductModuleData pmd = userSelected.get(i);
				if (BofConst.MODIFY_TAG_UPD.equals(pmd.getModifyTag()))
				{
					IData transElement = this.getTransElement(newProductElements, pmd.getElementId(), pmd.getElementType());
					if (transElement != null)
					{
						if (!transElement.getString("PRODUCT_ID", "").equals(pmd.getProductId()) || !transElement.getString("PACKAGE_ID", "").equals(pmd.getPackageId()))
						{
							pmd.setProductId(transElement.getString("PRODUCT_ID"));
							pmd.setPackageId(transElement.getString("PACKAGE_ID"));
						}
					}
				}
			}

			// 转换主产品时，处理必选元素
			IDataset forceElements = this.getProductForceElement(newProductElements);
			if (IDataUtil.isNotEmpty(forceElements))
			{
				int forceSize = forceElements.size();
				for (int i = 0; i < forceSize; i++)
				{
					IData forceElement = forceElements.getData(i);
					if (!this.isExistInUserSelected(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), null, userSelected))
					{
						// 不在用户的选择列表里面
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
						{
							if (uca.getUserSvcBySvcId(forceElement.getString("ELEMENT_ID")).size() > 0)
							{
								continue;
							}

							SvcData svcData = new SvcData(forceElement);
							svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							IDataset attrs = AttrItemInfoQry.getElementItemA(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), uca.getUserEparchyCode());
							List<AttrData> attrDatas = new ArrayList<AttrData>();
							if (IDataUtil.isNotEmpty(attrs))
							{
								int length = attrs.size();
								for (int j = 0; j < length; j++)
								{
									IData attr = attrs.getData(j);
									AttrData attrData = new AttrData();
									attrData.setAttrCode(attr.getString("ATTR_CODE"));
									attrData.setAttrValue(attr.getString("ATTR_INIT_VALUE"));
									attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
									attrDatas.add(attrData);
								}
							}
							if (attrDatas.size() > 0)
							{
								svcData.setAttrs(attrDatas);
							}
							operElements.add(svcData);
						}
						else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
						{
							DiscntData discntData = null;

							String maxEndDate = request.getAcceptTime();

							List<DiscntTradeData> existUserDiscnts = uca.getUserDiscntByDiscntId(forceElement.getString("ELEMENT_ID"));

							if (existUserDiscnts != null && existUserDiscnts.size() > 0)// 必选元素是否已经存在
							{
								for (DiscntTradeData existUserDiscnt : existUserDiscnts)
								{
									maxEndDate = SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND);

									if (maxEndDate.compareTo(SysDateMgr.decodeTimestamp(existUserDiscnt.getEndDate(), SysDateMgr.PATTERN_STAND)) < 0)
									{
										maxEndDate = existUserDiscnt.getEndDate();
									}
								}

								if (SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.END_TIME_FOREVER, SysDateMgr.PATTERN_STAND)) < 0)
								{
									if (SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(productChangeDate, SysDateMgr.PATTERN_STAND)) > 0)// 已经存在的必选元素大于本次产品生效时间
									{
										discntData = new DiscntData(forceElement);
										discntData.setStartDate(SysDateMgr.addSecond(maxEndDate, 1));
										discntData.setEndDate(ProductModuleCalDate.calEndDate(discntData, SysDateMgr.addSecond(maxEndDate, 1)));
									}
									else
									{
										discntData = new DiscntData(forceElement);
										discntData.setStartDate(productChangeDate);
										discntData.setEndDate(ProductModuleCalDate.calEndDate(discntData, productChangeDate));
									}
								}
							}
							else
							{
								discntData = new DiscntData(forceElement);
							}

							if (discntData != null)
							{
								discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								IDataset attrs = AttrItemInfoQry.getElementItemA(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), uca.getUserEparchyCode());
								List<AttrData> attrDatas = new ArrayList<AttrData>();
								if (IDataUtil.isNotEmpty(attrs))
								{
									int length = attrs.size();
									for (int j = 0; j < length; j++)
									{
										IData attr = attrs.getData(j);
										AttrData attrData = new AttrData();
										attrData.setAttrCode(attr.getString("ATTR_CODE"));
										attrData.setAttrValue(attr.getString("ATTR_INIT_VALUE"));
										attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
										attrDatas.add(attrData);
									}
								}
								if (attrDatas.size() > 0)
								{
									discntData.setAttrs(attrDatas);
								}
								operElements.add(discntData);
							}
							// if (uca.getUserDiscntByDiscntId(forceElement.getString("ELEMENT_ID")).size() > 0)
							// {
							// continue;
							// }
						}
					}
				}
			}
			if(!bIsUserSelected)
			{//接口进来对平台有20彩铃服务进行插关系处理，接口没有传“SELECTED_ELEMENT”内容，如果在拼可继承元素之前拼了OFFER_REL关系的元素，可能会造成继承处理不属于原有产品下的彩铃服务构成继承关系，导致会有两条20彩铃服务的offer_rel记录，
				IDataset elementList = PkgElemInfoQry.getElementByPIdElemId(request.getNewMainProduct().getProductId(), "S", "20", "0898") ;
            	if(IDataUtil.isNotEmpty(elementList) && elementList.size() > 0)
                {
            		IData elemet = elementList.first();
            		
                    if("1".equals(elemet.getString("FORCE_TAG"))) //新产品彩铃是必选，如果存在平台业务的彩铃了，则不给予办理产品变更。要求先取消平台业务彩铃才能办理。
                    {
                    	String strPackageID = elemet.getString("PACKAGE_ID", "");
                    	List<SvcTradeData> lsSvc = uca.getUserSvcBySvcId("20");
                    	
                    	
                    	if(lsSvc != null && lsSvc.size() > 0)// 用户已有该产品，新增OFFER_REL 数据  判断如果变更的主产品里存在必选的元素，但在当前服务表或者优惠表已经存在了，只需要新增一条offer_rel，注意不需要原来的修改彩铃 user_id_a的逻辑了。
                    	{  
                    		List<OfferRelTradeData> lsOffer_rel = uca.getOfferRelByRelUserIdAndRelOfferInsId(lsSvc.get(0).getInstId());
                    		if(lsOffer_rel.size() == 1 && BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(lsOffer_rel.get(0).getOfferType())){
                    			issvcdata = true;
                    		}
                    		SvcTradeData tdSvc = lsSvc.get(0);
                    		if(issvcdata){// add by hefeng
                    			//新增彩铃的构成关系
                    			newProductInitId=SeqMgr.getInstId();
                                OfferRelTradeData offerRel = new OfferRelTradeData();
                    			offerRel.setInstId(SeqMgr.getInstId());
                    			offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    			offerRel.setOfferInsId(newProductInitId);
                    			offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                    			offerRel.setOfferCode(request.getNewMainProduct().getProductId());
                    			offerRel.setGroupId(strPackageID);
                    			offerRel.setUserId(uca.getUserId());
                    			offerRel.setRelOfferInsId(tdSvc.getInstId());
                    			offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_SVC);
                    			offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);
                    			offerRel.setRelOfferCode(tdSvc.getElementId());
                    			offerRel.setRelUserId(uca.getUserId());
                    			if(request.isBookingTag()){
                    				offerRel.setStartDate(request.getBookingDate());
                    			}else{
                    				offerRel.setStartDate(SysDateMgr.getSysTime());
                    			}
                    			offerRel.setEndDate(tdSvc.getEndDate());
                    			offerRel.setRemark("主产品变更操作由于彩铃是必选的数据生成关联关系");
                			   btd.add(uca.getSerialNumber(), offerRel);
                    		}
                    	}
                    }
                }
			} 
		}
		else
		{
			// 仅元素变更时，需要检查用户是否存在预约产品，如果存在预约产品的话，用户操作的元素如果在当前产品下没有，则要检查用户在预约产品下是否存在，如果存在，则根据预约产品的配置计算生失效时间
			ProductTradeData nextProduct = uca.getUserNextMainProduct();
			String sysDate = request.getAcceptTime();
			if (nextProduct != null)
			{
				IDataset oldProductElements = ProductInfoQry.getProductElements(uca.getProductId(), uca.getUserEparchyCode());
				int size = operElements.size();
				for (int i = 0; i < size; i++)
				{
					ProductModuleData pmd = operElements.get(i);
					IData oldConfig = this.getTransElement(oldProductElements, pmd.getElementId(), pmd.getElementType());
					if (oldConfig == null)// 新产品下元素
					{
						if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
						{
							pmd.setStartDate(nextProduct.getStartDate());
						}
						else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
						{
							pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
						}
					}
					else
					{
					   pmd.setPkgElementConfig(oldConfig.getString("PACKAGE_ID"));
						// 如果元素是删除 且开始时间大于系统时间 那么终止此元素的结束时间为开始时间的前一秒
						if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
						{
							pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
						}
					}
				}
			}
		}

		ProductTradeData nextProduct = uca.getUserNextMainProduct();

		ProductTimeEnv env = new ProductTimeEnv();
		if (request.isEffectNow())
		{
			env.setBasicAbsoluteStartDate(request.getAcceptTime());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(request.getAcceptTime()));
		}
		// 仅元素预约变更且没有预约产品情况
		else if (!isProductChange && request.isBookingTag() && nextProduct == null)
		{
			env.setBasicAbsoluteStartDate(request.getBookingDate());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(request.getBookingDate()));
		}
		else if (isProductChange)
		{
			env.setBasicAbsoluteStartDate(productChangeDate);
			env.setBasicAbsoluteCancelDate(oldProductEndDate);
		}
		// 存在预约产品变更 且 本次业务又预约 报错
		else if (request.isBookingTag() && nextProduct != null)
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_243);
		}

		// 元素拼串处理
		ProductModuleCreator.createProductModuleTradeData(operElements, btd, env);
	}

	public void createProductTrade(String newProductId, String oldProductId, String productChangeDate, String oldProductEndDate, BaseChangeProductReqData request, BusiTradeData btd,String newProductInitId) throws Exception
	{
		UcaData uca = request.getUca();

		// 新主产品新增台帐
		ProductTradeData newProductTradeData = new ProductTradeData();
		newProductTradeData.setProductId(newProductId);
		newProductTradeData.setBrandCode(request.getNewMainProduct().getBrandCode());
		newProductTradeData.setProductMode(request.getNewMainProduct().getProductMode());
		newProductTradeData.setStartDate(productChangeDate);
		newProductTradeData.setEndDate(SysDateMgr.getTheLastTime());
		if("".equals(newProductInitId)){
			newProductTradeData.setInstId(SeqMgr.getInstId());
		}else{
			newProductTradeData.setInstId(newProductInitId);
		}
//		newProductTradeData.setInstId(SeqMgr.getInstId());
		newProductTradeData.setUserId(uca.getUserId());
		newProductTradeData.setUserIdA("-1");
		newProductTradeData.setMainTag("1");
		newProductTradeData.setOldProductId(uca.getProductId());
		newProductTradeData.setOldBrandCode(uca.getBrandCode());
		newProductTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

		// 老主产品结束台帐
		ProductTradeData oldProduct = uca.getUserProduct(oldProductId).get(0);
		ProductTradeData oldProductTrade = oldProduct.clone();
		oldProductTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
		oldProductTrade.setEndDate(oldProductEndDate);
		btd.add(uca.getSerialNumber(), newProductTradeData);
		btd.add(uca.getSerialNumber(), oldProductTrade);
	}

	public String getProductChangeDate(String oldProductId, String newProductId, BaseChangeProductReqData request) throws Exception
	{
		String productChangeDate = null;
		IData productTrans = UProductInfoQry.queryProductTransInfo(oldProductId, newProductId);
		if (IDataUtil.isNotEmpty(productTrans))
		{
			String enableTag = productTrans.getString("ENABLE_TAG");

			if (enableTag.equals("0"))
			{// 立即生效
				productChangeDate = request.getAcceptTime();
			}
			else if ((enableTag.equals("1")) || (enableTag.equals("2")))
			{// 下帐期生效
				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			}
			else if (enableTag.equals("3"))
			{// 按原产品的生效方式
				ProductData oldProductData = new ProductData(request.getUca().getProductId());
				String enableTagOld = oldProductData.getEnableTag();

				if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
				{// 立即生效
					productChangeDate = request.getAcceptTime();
				}
				else if (enableTagOld.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
			else if (enableTag.equals("4"))
			{// 按新产品的生效方式
				String enableTagNew = request.getNewMainProduct().getEnableTag();

				if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
				{// 立即生效
					productChangeDate = request.getAcceptTime();
				}
				else if (enableTagNew.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}
		else
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_4);
		}
		return productChangeDate;
	}

	private IDataset getProductForceElement(IDataset productElements) throws Exception
	{
		int size = productElements.size();
		IDataset result = new DatasetList();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if ("1".equals(element.getString("PACKAGE_FORCE_TAG")) && "1".equals(element.getString("ELEMENT_FORCE_TAG")))
			{
				result.add(element);
			}
		}
		return result;
	}

	public IData getTransElement(IDataset productElements, String elementId, String elementType)
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

	private boolean isExistInUserSelected(String elementTypeCode, String elementId, String instId, List<ProductModuleData> userSelected)
	{
		if (userSelected == null || userSelected.size() <= 0)
		{
			return false;
		}
		else
		{
			int size = userSelected.size();
			for (int i = 0; i < size; i++)
			{
				ProductModuleData selected = userSelected.get(i);
				if (selected.getElementType().equals(elementTypeCode) && selected.getElementId().equals(elementId))
				{
					if (!StringUtils.isBlank(selected.getInstId()))
					{
						if (selected.getInstId().equals(instId))
						{
							return true;
						}
						else
						{
							continue;
						}
					}
					return true;
				}
			}
			return false;
		}
	}

	private boolean isNeedDeal(List<ProductTradeData> userProducts, String elementProductId) throws Exception
	{
		int size = userProducts.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData ptd = userProducts.get(i);
			if (ptd.getProductId().equals(elementProductId)
					&& ("00".equals(ptd.getProductMode()) || "01".equals(ptd.getProductMode()) || "07".equals(ptd.getProductMode()) || "09".equals(ptd.getProductMode()) || "11".equals(ptd.getProductMode()) || "13".equals(ptd.getProductMode())))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 商品订购中判断同笔订单中是否受理与彩铃存在依赖关系的平台业务(同时加入购物车结算 产品变更+平台业务)
	 * @param btd
	 * @param offerCode
	 * @return
	 * @throws Exception
	 * @author guohuan
	 */
    private boolean isShoppingTrade(BusiTradeData btd, String offerCode) throws Exception
    {
        if ("5178".equals(btd.getRD().getOrderTypeCode()))
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            List<BusiTradeData> btds = dataBus.getBtds();
            for (BusiTradeData busiTradeData : btds)
            {
                List<PlatSvcTradeData> changePlats = busiTradeData.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
                if (changePlats != null && changePlats.size() > 0)
                {
                    for (PlatSvcTradeData platTradeData : changePlats)
                    {
                        if (BofConst.MODIFY_TAG_DEL.equals(platTradeData.getModifyTag()) && offerCode.equals(platTradeData.getElementId()))
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
