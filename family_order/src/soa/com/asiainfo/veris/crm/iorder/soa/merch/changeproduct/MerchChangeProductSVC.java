
package com.asiainfo.veris.crm.iorder.soa.merch.changeproduct;
//package com.asiainfo.veris.crm.iorder.soa.merch.changeproduct;

import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.MerchShoppingCartBean;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product.SelectedElementSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class MerchChangeProductSVC extends CSBizService
{

	/**
     * 加载输入用户号码时，产品变更的信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData loadChildInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    
    	String userId = param.getString("USER_ID");
    	String custId = param.getString("CUST_ID");
    
    	String routeEparchyCode = BizRoute.getRouteId();
    	String userProductId = ""; // 用户当前产品ID
    	String userProductMode = ""; 
    	// 查询用户资料的产品信息
    	IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
    	String sysTime = SysDateMgr.getSysTime();
    
    	if (IDataUtil.isNotEmpty(userMainProducts))
    	{
    		int size = userMainProducts.size();
    		for (int i = 0; i < size; i++)
    		{
    			IData userProduct = userMainProducts.getData(i);
    			if (SysDateMgr.compareTo(userProduct.getString("START_DATE"), sysTime) < 0)
    			{
    				userProductId = userProduct.getString("PRODUCT_ID");
    
    				IData userProductRst = new DataMap();
    				userProductRst.put("PRODUCT_ID", userProductId);
    				userProductRst.put("START_DATE", userProduct.get("START_DATE"));
    				userProductRst.put("END_DATE", userProduct.get("END_DATE"));
    				userProductRst.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userProductId));
    				userProductRst.put("PRODUCT_DESC", UProductInfoQry.getProductExplainByProductId(userProductId));
    				result.put("USER_PRODUCT", userProductRst);
    				userProductMode = userProduct.getString("PRODUCT_MODE");
    			}
    			else
    			{
    				CSAppException.appError("-1", "已经存在预约产品变更");
    			}
    		}
    	}
    	
        if (!StringUtils.equals("00", userProductMode) && !StringUtils.equals("15", userProductMode))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_246);
        }
        
    	String productId = param.getString("NEW_PRODUCT_ID");
    
    	if (StringUtils.isEmpty(productId))
    	{
    		CSAppException.appError("-1", "新产品为空");
    	}
    	IData offer = UProductInfoQry.qryProductByPK(productId);//UpcCallIntf.queryOfferNameDesc(productId, "P");
    	offer.put("START_DATE", SysDateMgr.getSysTime());
    	offer.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
    
    	result.put("NEW_PRODUCT", offer);
    	result.put("EPARCHY_CODE", routeEparchyCode);
    	
        // 海南查询用户已经办理过押金的发票号
        IDataset foregift = UserOtherInfoQry.getUserOtherservByPK(userId, "FG", "0", null);
        if (IDataUtil.isNotEmpty(foregift))
        {
            result.put("INVOICE_DATA", foregift);
        }
        
        // 海南查询VPMN相关信息
        IData userVpmn = new ChangeProductBean().getUserVpmnData(UcaDataFactory.getUcaByUserId(userId));
        if (IDataUtil.isNotEmpty(userVpmn))
        {
            result.putAll(userVpmn);
        }
    
        // 海南预约时间处理
        String acctDay = param.getString("ACCT_DAY");
        String firstDate = param.getString("FIRST_DATE");
        if (StringUtils.isNotBlank(acctDay) && StringUtils.isNotBlank(firstDate))
        {
            IDataset commpara8859 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "8859", "PRODUCT", userProductId,routeEparchyCode); 
            if (IDataUtil.isNotEmpty(commpara8859) &&
                 !StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_CRM_CHANGEG010")) 
            {
                result.put("BOOKING_PRODUCT", "TRUE");
                result.put("BOOKING_DATE", SysDateMgr.getFirstDayNextAcct(SysDateMgr.getSysDate(), acctDay, firstDate));
            }
            else 
            {
                result.put("BOOKING_PRODUCT", "FALSE"); 
            }
        }
        
        // 是否具备选择预约时间的权限
        if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "PROD_BOOKING_DATE"))
        {
            result.put("BOOKING_DATE_PRIV", "TRUE");
        }
        else
        {
            result.put("BOOKING_DATE_PRIV", "FALSE");
        }
    
        IDataset custVipInfo = CustVipInfoQry.qryVipInfoByCustId(custId);
    
        if (IDataUtil.isNotEmpty(custVipInfo))
        {
            String vipClassId = custVipInfo.getData(0).getString("VIP_CLASS_ID", "");
    
            result.put("VIP_CLASS_ID", vipClassId);
        }
        
//    	boolean ifEffectNow = checkEffectNow(productId, userProductId);
//    	result.put("IS_EFFECT_NOW", ifEffectNow);
    	result.put("SYS_DATE", offer.getString("START_DATE"));
    	return result;
    }

	public IData getUserProduct(IData param) throws Exception
	{
		IData result = new DataMap();

		String userId = param.getString("USER_ID");

		String userProductId = ""; // 用户当前产品ID
		String nextProductId = ""; // 用户变更的产品ID

		// 查询用户资料的产品信息
		IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
		String sysTime = SysDateMgr.getSysTime();
		String userProductMode = ""; 
		
		result.put("SYS_TIME", sysTime);
		if (IDataUtil.isNotEmpty(userMainProducts))
		{
			int size = userMainProducts.size();
			for (int i = 0; i < size; i++)
			{
				IData userProduct = userMainProducts.getData(i);
				if (SysDateMgr.compareTo(userProduct.getString("START_DATE"), sysTime) < 0)
				{
					userProductId = userProduct.getString("PRODUCT_ID");

					IData userProductRst = new DataMap();
					userProductRst.put("PRODUCT_ID", userProductId);
					userProductRst.put("START_DATE", userProduct.get("START_DATE"));
					userProductRst.put("END_DATE", userProduct.get("END_DATE"));
					userProductRst.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userProductId));
					userProductRst.put("PRODUCT_DESC", UProductInfoQry.getProductExplainByProductId(userProductId));
					result.put("USER_PRODUCT", userProductRst);
					userProductMode = userProduct.getString("PRODUCT_MODE");
				}
				else
				{
					nextProductId = userProduct.getString("PRODUCT_ID");
					IData nextProductRst = new DataMap();
					nextProductRst.put("PRODUCT_ID", nextProductId);
					nextProductRst.put("START_DATE", userProduct.getString("START_DATE"));
					nextProductRst.put("END_DATE", userProduct.get("END_DATE"));
					nextProductRst.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(nextProductId));
					nextProductRst.put("PRODUCT_DESC", UProductInfoQry.getProductExplainByProductId(nextProductId));
					result.put("NEXT_PRODUCT", nextProductRst);
					userProductMode = userProduct.getString("PRODUCT_MODE");
				}
			}
		}
		
		// 只有个人用户和个人物联网用户可以办理产品变更
		if (!StringUtils.equals("00", userProductMode) && !StringUtils.equals("07", userProductMode))
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_246);
		}

		return result;
	}

	private boolean checkEffectNow(String newProductId, String oldProductId) throws Exception
	{
		if (StringUtils.isNotEmpty(newProductId)
				&& StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "csmChangeProductEffectNow")) {
			return true;
		}

		return false;
	}

	public IDataset tradeReg(IData param) throws Exception
	{
		IDataset tradeRst = new DatasetList();
		String newProductId = param.getString("NEW_PRODUCT_ID", "");
		String bookingDate = param.getString("BOOKING_DATE");
		if(StringUtils.isBlank(bookingDate))
		{
			param.put("BOOKING_DATE", SysDateMgr.getSysTime());
		}
		if (StringUtils.isNotBlank(newProductId))//主套餐变更
		{
			IData input = new DataMap();
			input.put("EPARCHY_CODE", getUserEparchyCode());
			input.put("ROUTE_EPARCHY_CODE", getUserEparchyCode());
			input.put("USER_ID", param.getString("USER_ID"));
			input.put("NEW_PRODUCT_ID", newProductId);
			if(StringUtils.isBlank(bookingDate))
			{
				input.put("BOOKING_DATE", param.getString("BOOKING_DATE")); //快捷订购时有些套餐不是立即生效的 ，需要加预约时间处理 guohuan
			}
			IDataset result = CSAppCall.call("CS.SelectedElementSVC.getUserElements", input);
			IDataset elements = result.getData(0).getDataset("SELECTED_ELEMENTS");
			IDataset selectedElements = new DatasetList();
			for (int i = 0; i < elements.size(); i++)
			{
				IData element = elements.getData(i);
				String modifyTag = element.getString("MODIFY_TAG");
				if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag))
				{
					IData data = new DataMap();
					data.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
					data.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));
					data.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
					data.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
					data.put("ATTR_PARAM", element.getDataset("ATTR_PARAM"));
					data.put("MODIFY_TAG", modifyTag);
					data.put("START_DATE", element.getString("START_DATE"));
					data.put("END_DATE", element.getString("END_DATE"));
					data.put("INST_ID", element.getString("INST_ID"));
					selectedElements.add(data);
				}
			}
			param.put("SELECTED_ELEMENTS", selectedElements);
			this.existsShoppingCard(param);
			tradeRst = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);
		}else
		{
			this.existsShoppingCard(param);
			IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS", "[]"));
			IDataset platSelectedElements = new DatasetList(param.getString("PLAT_SELECTED_ELEMENTS", "[]"));
			//优惠/服务列表为空，平台服务不为空，则调用平台业务受理接口，否则调用产品变更接口
			if(IDataUtil.isEmpty(selectedElements) && IDataUtil.isNotEmpty(platSelectedElements))
			{
				param.put("TRADE_TYPE_CODE", "3700");
				param.put("SELECTED_ELEMENTS", platSelectedElements.toString());
				tradeRst = CSAppCall.call("SS.PlatRegSVC.tradeReg", param);
            }
            else if (IDataUtil.isNotEmpty(selectedElements) && IDataUtil.isNotEmpty(platSelectedElements))
            {
                // 同时存在平台业务和产品变更时
                IData param1 = new DataMap();
                param1.putAll(param);
                param1.put("TRADE_TYPE_CODE", "3700");
                param1.put("SELECTED_ELEMENTS", platSelectedElements.toString());
                tradeRst = CSAppCall.call("SS.PlatRegSVC.tradeReg", param1);
                param.remove("PLAT_SELECTED_ELEMENTS");
                tradeRst = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);
            }
            else
			{
				tradeRst = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);
			}
		}
		return tradeRst;
	}

	public IData addShoppingCartConfirm(IData param) throws Exception
	{
		IData result = new DataMap();

		IData element = new DataMap(param.getString("OFFER"));
		String elementType = element.getString("OFFER_TYPE");
		String elementId = element.getString("OFFER_CODE");
		String endDate = element.getString("END_DATE");

		String nextProductStartDate = "";
        if("Z".equals(elementType)){ 
        	return result;
        }
		
		if (!"P".equals(elementType))
		{
			MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
			IDataset cartOffers = shoppingCartBean.getShoppingCartAllElements(param);
			if (IDataUtil.isNotEmpty(cartOffers))
			{
				for (int i = 0; i < cartOffers.size(); i++)
				{
					IData offer = cartOffers.getData(i);
					if ("P".equals(offer.getString("OFFER_TYPE")) && BofConst.MODIFY_TAG_ADD.equals(offer.getString("MODIFY_TAG")) && "1".equals(offer.getString("MAIN_TAG")))
					{
						nextProductStartDate = offer.getString("START_DATE");
						if (SysDateMgr.getDayIntervalNoAbs(nextProductStartDate, endDate) > 0)
						{
						    IData data = ProductElementsCache.getElement(offer.getString("OFFER_CODE"),elementId,elementType);//查询预约新产品下是否可以订购该商品   guohuan
							//IData data = UpcCallIntf.qryROfferBaseByOfferCond(offer.getString("OFFER_CODE"), "P", elementId, elementType, getRouteId());
							if (IDataUtil.isEmpty(data))
							{
//							    CSAppException.appError("-1", "购物车中有预约产品变更，本次加入的商品，在新产品下不存在，不能订购");
//							    result.put("","购物车中有预约产品变更，本次加入的商品，在新产品下不存在，不能订购");
//								if ("D".equals(elementType))
//								{
//									IDataset commparaFlowDiscntList = CommparaInfoQry.getCommparaAllColByCond("CSM", "9991", "DATA_PACK_HOUR_DISCNT", elementId, CSBizBean.getUserEparchyCode());
//									if (IDataUtil.isNotEmpty(commparaFlowDiscntList))
//									{购物车中有预约产品变更，本次加入的商品，在新产品下不存在！！！
//										CSAppException.appError("-1", "购物车中存在预约产品变更，日套餐/流量小时套餐,不能加入购物车！");
//									}
//								}

								result.put("IS_CONFIRM", true);
								result.put("CONFIRM_MESSAGE", "购物车中有预约产品变更，本次加入的商品，在新产品下不存在，不能订购！");
								break;
							}
						}
					}else
					{
						//重复加入购物车校验
						if(StringUtils.equals(elementType, offer.getString("OFFER_TYPE")) && StringUtils.equals(elementId, offer.getString("OFFER_CODE")))
						{
							CSAppException.appError("-1", "购物车中已存在商品【" + elementType + "|" + elementId + "】【" + offer.getString("OFFER_NAME") + "】,不能继续办理业务！如需办理，请先删除购物车中的商品！");
						}
					}
				}
			}
			
			this.dealElementFee(element, param.getString("SERIAL_NUMBER"), "110", element.getString("BOOKING_DATE"), result);
			
		}else {
			MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
			IDataset cartOffers = shoppingCartBean.getShoppingCartElementsByTradeTypeCode(param, "110");
			if (IDataUtil.isNotEmpty(cartOffers))
			{
				for (int i = 0; i < cartOffers.size(); i++)
				{
					IData offer = cartOffers.getData(i);
					if ("P".equals(offer.getString("OFFER_TYPE")) && BofConst.MODIFY_TAG_ADD.equals(offer.getString("MODIFY_TAG")) && "1".equals(offer.getString("MAIN_TAG")))
					{
						CSAppException.appError("-1", "购物车中已存在主产品变更,不能继续加入购物车！");
					}else {
					    //考虑到在往购物车中加主产品时，购物车中存在新产品不能订购的商品加的校验  guohuan
						if("D".equals(offer.getString("OFFER_TYPE")) || "S".equals(offer.getString("OFFER_TYPE")))//平台业务的不校验
						{
						    IData data = ProductElementsCache.getElement(elementId,offer.getString("OFFER_CODE"),offer.getString("OFFER_TYPE"));
						    if (IDataUtil.isEmpty(data))
	                        {
						        result.put("IS_CONFIRM", true);
						        result.put("CONFIRM_MESSAGE", "购物车中已存在的商品【" + offer.getString("OFFER_TYPE") + "|" + offer.getString("OFFER_NAME") + "】在新产品下【"+elementId+"|"+element.getString("OFFER_NAME")+"】不存在！如需办理，请先删除购物车中该商品！");
						        break;
	                        }
						}
                    }
				}
			}
		}
		return result;
	}

    public IDataset getOrderAntinomyOffers(IData data) throws Exception
    {
        String offerCode = data.getString("OFFER_CODE");
        String offerType = data.getString("OFFER_TYPE");
        String sn = data.getString("SERIAL_NUMBER");
        String productId = data.getString("PRODUCT_ID");
        String groupId = data.getString("GROUP_ID");
        UcaData uca = UcaDataFactory.getNormalUca(sn);
        IDataset result = new DatasetList();

        // 国内、国际长途漫游特殊处理
        // 国内（不含港澳台）长途 14 国内（不含港澳台）漫游 18
        // 国际及港澳台长途 15 国际及港澳台漫游 19
        IDataset antinomyOffers = CommparaInfoQry.getCommparaByCodeCode1("CSM", "268", offerCode, offerType);
        if (IDataUtil.isNotEmpty(antinomyOffers))
        {
            for (int i = 0; i < antinomyOffers.size(); i++)
            {
                IData antinomyOffer = antinomyOffers.getData(i);
                String relOfferCode = antinomyOffer.getString("PARA_CODE2");
                String relOfferType = antinomyOffer.getString("PARA_CODE3", "");
                String tips = antinomyOffer.getString("PARA_CODE4", "");
                if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_SVC))
                {
                    List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(relOfferCode);
                    if (userSvcs.size() > 0)
                    {
                        result.addAll(getDelElement(userSvcs, BofConst.ELEMENT_TYPE_CODE_SVC, tips));
                    }
                }
            }
        }
        // 长途漫游处理结束

        IDataset groupData = UpcCall.queryOfferGroupRelOfferIdGroupId("P", productId, groupId);
        boolean isabDepend = isAbsoluteDependenceByelement(offerCode, offerType, "2", "22", "S");// 依赖互斥判断 手机上网|22
        if (IDataUtil.isNotEmpty(groupData) || isabDepend)
        {
            String groupType = groupData.getData(0).getString("GROUP_TYPE");
            String selectFlag = groupData.getData(0).getString("SELECT_FLAG");
            if (isabDepend || (StringUtils.equals("0", groupType) && StringUtils.equals("0", selectFlag)))
            {
                IDataset hcOffers = BreQryForProduct.tacGetAllEleVsEleLimit(groupId, offerCode, "0", offerType, "A", uca.getUserEparchyCode(), false);
                if (IDataUtil.isNotEmpty(hcOffers))
                {
                    for (int i = 0; i < hcOffers.size(); i++)
                    {
                        IData hcOffer = hcOffers.getData(i);
                        String relOfferType = hcOffer.getString("REL_OFFER_TYPE");
                        String relOfferCode = hcOffer.getString("REL_OFFER_CODE");
                        String relOfferName = hcOffer.getString("REL_OFFER_NAME");
                        String tips = "需要删除已订购" + relOfferType + "|" + relOfferName;
                        if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
                        {
                            List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(relOfferCode);
                            if (userDiscnts.size() > 0)
                            {
                                result.addAll(getDelElement(userDiscnts, BofConst.ELEMENT_TYPE_CODE_DISCNT, tips));
                            }
                        }
                        else if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_SVC))
                        {
                            List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(relOfferCode);
                            if (userSvcs.size() > 0)
                            {
                                result.addAll(getDelElement(userSvcs, BofConst.ELEMENT_TYPE_CODE_SVC, tips));
                            }
                        }
                        else if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_PLATSVC))
                        {
                            List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(relOfferCode);
                            if (userPlatSvcs.size() > 0)
                            {
                                result.addAll(getDelElement(userPlatSvcs, BofConst.ELEMENT_TYPE_CODE_PLATSVC, tips));
                            }
                        }
                    }
                }
            }
        }
        
        if (IDataUtil.isNotEmpty(result))
        {
        	StringBuilder sb = new StringBuilder("订购" + offerType + "|" + UpcCall.qryOfferNameByOfferTypeOfferCode(offerCode, offerType) + "\n");
            for (int i = 0; i < result.size(); i++)
            {
                IData param = result.getData(i);
                sb.append(param.getString("DEL_TIPS") + "\n");
                param.remove("DEL_TIPS");
            }
            result = dealSelectedForChg(data, result, uca);
            result.getData(0).put("DEL_TIPS", sb.toString());
        }
        
        return result;
    }
	/**
	 * 产品变更依赖互斥判断
	 * @param offerCode 订购商品
	 * @param offerType 订购商品类型
	 * @param relType 产品间关系类型
	 * @param ylCode 依赖商品
	 * @param ylType 依赖商品类型
	 * @return
	 * @author guohuan
	 * @throws Exception
	 */
	private boolean isAbsoluteDependenceByelement(String offerCode,String offerType,String relType,String ylCode,String ylType)throws Exception
    {
	    IDataset reloffers = UpcCall.queryOfferRelByCond(offerType,offerCode,relType);
	    if (IDataUtil.isNotEmpty(reloffers))
        {
            for (int i = 0; i < reloffers.size(); i++)
            {
                IData data = reloffers.getData(i);
                 String relOfferType = data.getString("REL_OFFER_TYPE");
                 String relOfferCode = data.getString("REL_OFFER_CODE");
                 if (StringUtils.equals(ylType, relOfferType)&&StringUtils.equals(ylCode, relOfferCode))
                {
                    return true;
                }
            }
        }
        return false;
    }
	private IDataset dealSelectedForChg(IData data, IDataset elements, UcaData uca) throws Exception
	{
	    String bookingDate = data.getString("BOOKING_DATE");
		String acceptTime = SysDateMgr.getSysTime();
		DataBusManager.getDataBus().setAcceptTime(StringUtils.isNotBlank(bookingDate)?bookingDate:acceptTime);
		IData input = new DataMap();
		ProductTradeData userProduct = uca.getUserMainProduct();
		if (userProduct != null)
		{
			input.put("USER_PRODUCT_ID", userProduct.getProductId());
		}
		ProductTradeData nextProduct = uca.getUserNextMainProduct();
		if (nextProduct != null)
		{
			input.put("NEXT_PRODUCT_ID", nextProduct.getProductId());
		}

		input.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		input.put("ROUTE_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		input.put("TRADE_TYPE_CODE", "110");
		input.put("USER_ID", data.getString("USER_ID"));
		input.put("ELEMENTS", elements);
		input.put("CHECK_RULE", false);
		input.put("BOOKING_DATE", bookingDate);
		IDataset delElements = CSAppCall.call("CS.SelectedElementSVC.dealSelectedElementsForChg", input);
		return delElements;
	}

	private IDataset getDelElement(List<? extends ProductModuleTradeData> userEles, String type, String tips)
	{
		IDataset result = new DatasetList();
		for (ProductModuleTradeData productModuleTradeData : userEles)
		{
			IData param = new DataMap();
			param.put("PRODUCT_ID", productModuleTradeData.getProductId());
			param.put("PACKAGE_ID", productModuleTradeData.getPackageId());
			param.put("ELEMENT_ID", productModuleTradeData.getElementId());
			param.put("ELEMENT_TYPE_CODE", type);
			param.put("INST_ID", productModuleTradeData.getInstId());
			param.put("START_DATE", productModuleTradeData.getStartDate());
			param.put("END_DATE", productModuleTradeData.getEndDate());
			param.put("DEL_TIPS", tips);
			param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
			param.put("CHECK_RULE", false);

			result.add(param);
		}
		return result;
	}

    private IDataset getAddElement(List<? extends ProductModuleTradeData> userEles, String type, String tips)
    {
        IDataset result = new DatasetList();
        for (ProductModuleTradeData productModuleTradeData : userEles)
        {
            IData param = new DataMap();
            param.put("PRODUCT_ID", productModuleTradeData.getProductId());
            param.put("PACKAGE_ID", productModuleTradeData.getPackageId());
            param.put("ELEMENT_ID", productModuleTradeData.getElementId());
            param.put("ELEMENT_TYPE_CODE", type);
            param.put("INST_ID", productModuleTradeData.getInstId());
            param.put("START_DATE", productModuleTradeData.getStartDate());
            param.put("END_DATE", productModuleTradeData.getEndDate());
            param.put("DEL_TIPS", tips);
            param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            param.put("CHECK_RULE", false);

            result.add(param);
        }
        return result;
    }
	
	/**
     * 快捷订购套餐，判断套餐或用户现有套餐下是否有：必选元素是否有属性、VPMN优惠、流量共享、宽带1+活动，如果有，则提示到详情界面填写
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkElementParamIntegrality(IData param) throws Exception
    {
        IData result = new DataMap();
        IData data = new DataMap();
        boolean isReturn = false;
        
        String newProductId = param.getString("NEW_PRODUCT_ID");
        String newProductName = param.getString("NEW_PRODUCT_NAME");
        String userProductId = param.getString("USER_PRODUCT_ID");
        String serialNumber = param.getString("SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        
        param.put("PRODUCT_ID", newProductId);
        
        IDataset tags = CSAppCall.call("SS.OfferSVC.queryProductChangeListTags", param);
        if (IDataUtil.isNotEmpty(tags))
        {
            StringBuilder infos = new StringBuilder();
            infos.append("新套餐").append("【").append(newProductName).append("】");
            for (int i = 0; i < tags.size(); i++)
            {
                IData tag = tags.getData(i);
                String groupType = tag.getString("GROUP_TYPE");
                String selectFlag = tag.getString("SELECT_FLAG");

                if (StringUtils.equals("0", groupType) && StringUtils.equals("0", selectFlag))
                {
                    infos.append("下存在必选商品，请点击确定进入商品详情界面输入后，再提交！");
                    result.put("ERROR_INFO", infos.toString());
                    result.put("OPEN_ORDER", "0");// 0为打开产品变更详情页，1不打开详情页
                    isReturn = true;
                    break;
                }
            }
            if (isReturn)
            {
                return IDataUtil.idToIds(result);
            }
        }

        IDataset offerComRels = UpcCall.queryOfferComRelOfferByOfferId("P", newProductId);
        if(IDataUtil.isNotEmpty(offerComRels))
        {
            StringBuilder infos = new StringBuilder();
            infos.append("新套餐").append("【").append(newProductName).append("】");
            for(int i=0;i<offerComRels.size();i++)
            {
                IData offer = offerComRels.getData(i);
                String offerCode = offer.getString("OFFER_CODE");
                String offerType = offer.getString("OFFER_TYPE");
                String offerName = offer.getString("OFFER_NAME");
                
                IDataset offerChaSpecs = UpcCall.qryOfferChaSpecsByOfferIdIsNull(offerType, offerCode);
                if(IDataUtil.isNotEmpty(offerChaSpecs))
                {
                    infos.append("下的必选元素").append(offerType).append("【").append(offerName).append("】的参数");
                    for(int j=0;j<offerChaSpecs.size();j++)
                    {
                        IData attr = offerChaSpecs.getData(j);
                        String strAttrName = attr.getString("CHA_SPEC_NAME");
                        
                        infos.append("【").append(strAttrName).append("】");
                    }
                    infos.append("为必输项，请点击确定进入商品详情界面输入后，再提交！");
                    result.put("ERROR_INFO", infos.toString());
                    result.put("OPEN_ORDER", "0");//0为打开产品变更详情页，1不打开详情页
                    isReturn = true;
                    break;
                }
            }
            if (isReturn)
            {
                return IDataUtil.idToIds(result);
            }
        }
        //海南流量共享业务添加  guohuan
        if (StringUtils.equals("80003014", userProductId))
        {
            result.put("ERROR_INFO", "您将取消流量不限量套餐，取消后，办理套餐时默认开通的共享关系和统付关系将同步取消。");
            result.put("OPEN_ORDER", "1");
            return IDataUtil.idToIds(result);
        }
/*        else if (StringUtils.equals("80003014", newProductId))
        {
            result.put("ERROR_INFO", "您将办理流量不限量套餐，可添加流量共享副卡，请点击确定进入商品详情界面输入后，再提交！");
            result.put("OPEN_ORDER", "0");
            return IDataUtil.idToIds(result);
        }*/
        //VPMN优惠
        IData userVpmn = new ChangeProductBean().getUserVpmnData(UcaDataFactory.getUcaByUserId(userId));
        if (IDataUtil.isNotEmpty(userVpmn))
        {
            result.put("ERROR_INFO", "用户办理了VPMN优惠，请点击确定进入商品详情界面操作");
            result.put("OPEN_ORDER", "0");
            return IDataUtil.idToIds(result);
        }
        //宽带1+活动（营销活动）
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("NEW_PRODUCT_ID", newProductId);
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData wideActiveData = CSAppCall.callOne("SS.ChangeProductSVC.getCancelActiveInfos", data);
        if (IDataUtil.isNotEmpty(wideActiveData))
        {
            result.put("ERROR_INFO", "用户办理了，宽带1+活动，请点击确定进入商品详情界面操作");
            result.put("OPEN_ORDER", "0");
            return IDataUtil.idToIds(result);
        }
        return IDataUtil.idToIds(result);
    }

	public IDataset calElementEndDate(IData data) throws Exception
	{
		String serialNumber = data.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		data.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
		data.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
		
		String offer = data.getString("OFFER");
		IDataset elements = new DatasetList();
		elements.add(new DataMap(offer));
		data.put("ELEMENTS", elements);
		
		return CSAppCall.call("CS.SelectedElementSVC.dealSelectedElementsForChg", data);
	}
	
	private void existsShoppingCard(IData param) throws Exception
	{
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		IDataset cartOffers = shoppingCartBean.getShoppingCartAllElements(param);
		if(IDataUtil.isNotEmpty(cartOffers))
		{
			IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS", "[]"));
			IDataset platSelectedElements = new DatasetList(param.getString("PLAT_SELECTED_ELEMENTS", "[]"));
			
			if(IDataUtil.isNotEmpty(cartOffers))
			{
				if(StringUtils.isNotBlank(param.getString("NEW_PRODUCT_ID")))
				{
					for(int i=0;i<cartOffers.size();i++)
					{
						IData offer = cartOffers.getData(i);
						String offerType = offer.getString("OFFER_TYPE");
						if("P".equals(offerType))
						{
							CSAppException.appError("-1", "购物车中已存在主产品变更,不能继续办理业务！如需办理，请先删除购物车的主产品！");
						}
					}
				}
				
				for(int i=0;i<cartOffers.size();i++)
				{
					IData offer = cartOffers.getData(i);
					String offerCode = offer.getString("OFFER_CODE");
					String offerType = offer.getString("OFFER_TYPE");
					
					if(IDataUtil.isNotEmpty(selectedElements))
					{
						for(int j=0;j<selectedElements.size();j++)
						{
							IData element = selectedElements.getData(j);
							String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
							String elementId = element.getString("ELEMENT_ID");
							if(StringUtils.equals(offerType, elementTypeCode) && StringUtils.equals(offerCode, elementId))
							{
								CSAppException.appError("-1", "购物车中已存在商品【" + offerType + "|" + offerCode + "】【" + offer.getString("OFFER_NAME") + "】,不能继续办理业务！如需办理，请先删除购物车中的商品！");
							}
						}
					}
					
					if(IDataUtil.isNotEmpty(platSelectedElements))
					{
						for(int j=0;j<platSelectedElements.size();j++)
						{
							IData element = platSelectedElements.getData(j);
							String elementTypeCode = element.getString("ELEMENT_TYPE_CODE", "Z");
							String elementId = element.getString("SERVICE_ID");
							if(StringUtils.equals(offerType, elementTypeCode) && StringUtils.equals(offerCode, elementId))
							{
								CSAppException.appError("-1", "购物车中已存在商品【" + offerType + "|" + offerCode + "】【" + offer.getString("OFFER_NAME") + "】,不能继续办理业务！如需办理，请先删除购物车中的商品");
							}
						}
					}
				}
			}
			
		}
	}
	
    /**
     * 打开详情页时，校验购物车有无主产品变更
     * 
     * @param data
     * @throws Exception
     * @author guohuan
     */
    public void checkShoppingCartForProduct(IData data) throws Exception
    {
        MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
        IDataset cartOffers = shoppingCartBean.getShoppingCartAllElements(data);
        if (IDataUtil.isNotEmpty(cartOffers))
        {
            for (int i = 0; i < cartOffers.size(); i++)
            {
                IData offer = cartOffers.getData(i);
                if ("P".equals(offer.getString("OFFER_TYPE")) && BofConst.MODIFY_TAG_ADD.equals(offer.getString("MODIFY_TAG")) && "1".equals(offer.getString("MAIN_TAG")))
                {
                    CSAppException.appError("-1", "购物车中已存在主产品变更,不能继续加入购物车！");
                }
            }
        }
    }
    
    public void dealElementFee(IData element, String serialNumber, String tradeTypeCode, String bookingDate, IData result) throws Exception
    {
    	// 加载相关费用
		if ("0".equals(element.getString("MODIFY_TAG")))
		{
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
			
			IDataset feeDatas = new DatasetList();

			// 海南国际长途、漫游费用特殊处理
			String elementId = element.getString("ELEMENT_ID");
			String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
			
			IData input = new DataMap();
			input.put("EPARCHY_CODE", getUserEparchyCode());
			input.put("ROUTE_EPARCHY_CODE", getUserEparchyCode());
			input.put("USER_ID", uca.getUserId());
			input.put("BOOKING_DATE", bookingDate);
			IDataset elements = CSAppCall.call("CS.SelectedElementSVC.getUserElements", input);
			IDataset userElements = elements.getData(0).getDataset("SELECTED_ELEMENTS");
			// REQ202004240028_关于国漫作为基础服务的开发需求 去除国漫押金 by wuhao5 20200509
//			if ("110".equals(tradeTypeCode))
//			{
//				if (!new SelectedElementSVC().isDealFeeByServiceId15(uca.getUserId(), elementId, elementTypeCode, userElements))
//				{
//					return;
//				}
//
//				IData feeSvc19 = new SelectedElementSVC().dealFeeByServiceId19(uca.getUserId(), elementId, elementTypeCode, userElements);
//				if (IDataUtil.isNotEmpty(feeSvc19))
//				{
//					String fee19 = feeSvc19.getString("FEE");
//					if ("true".equals(feeSvc19.getString("DEAL_FEE")) && !"0".equals(fee19))
//					{
//						IData feeData = new DataMap();
//						feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
//						feeData.put("MODE", "1");
//						feeData.put("CODE", "3");
//						feeData.put("FEE", fee19);
//						feeDatas.add(feeData);
//
//						result.put("FEE_DATA", feeDatas);
//						return;
//					}
//				}
//				// 办理国际漫游的前提条件是办理国际长途，只需判断国际长途是否要走星级流程即可
//				if (("15".equals(elementId) || "19".equals(elementId)) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
//				{
//					// REQ201410240002新的星级服务体系下，优化国际漫游业务开通门槛
//					String strAcctBlance = uca.getAcctBlance();
//					String strCreditClass = uca.getUserCreditClass();
//					int iCreditClass = Integer.parseInt(strCreditClass);
//					int iAcctBlance = Integer.parseInt(strAcctBlance) / 100;
//					// 是否满足星级服务流程，预存款开通条件
//					boolean bIsClass = false;
//					if (-1 == iCreditClass || 0 == iCreditClass)
//					{
//						if (iAcctBlance < 200)
//						{
//							bIsClass = true;
//						}
//					}
////    							else if (1 == iCreditClass || 2 == iCreditClass)
////    							{
////    								if (iAcctBlance < 500)
////    								{
////    									bIsClass = true;
////    								}
////    							}
//					// 如果不满足星级服务流程需求，需要走原来的流程
//					if (!bIsClass)
//					{
//						return;
//					}
//				}
//			}
			// 海南国际长途、漫游费用特殊处理 END
			// IDataset feeConfigs =
			// ProductFeeInfoQry.getElementFee(data.getString("TRADE_TYPE_CODE"),
			// CSBizBean.getVisit().getInModeCode(), "",
			// element.getString("ELEMENT_TYPE_CODE"),
			// element.getString("PRODUCT_ID"), element
			// .getString("PACKAGE_ID"), "-1",
			// element.getString("ELEMENT_ID"), eparchyCode, "3");
			IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(tradeTypeCode, BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));
			if (IDataUtil.isEmpty(feeConfigs))
			{
				return;
			}
			int feeSize = feeConfigs.size();

			for (int j = 0; j < feeSize; j++)
			{
				IData feeConfig = feeConfigs.getData(j);
				if (!"0".equals(feeConfig.getString("PAY_MODE")))
				{
					continue;
				}
				IData feeData = new DataMap();
				feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
				feeData.put("MODE", feeConfig.getString("FEE_MODE"));
				feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
				feeData.put("FEE", feeConfig.getString("FEE"));
				feeDatas.add(feeData);
			}
			result.put("FEE_DATA", feeDatas);
		}
    }
}
