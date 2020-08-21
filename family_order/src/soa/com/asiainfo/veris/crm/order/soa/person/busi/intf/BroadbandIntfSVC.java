package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;

public class BroadbandIntfSVC extends CSBizService {
	
    public IData queryUserInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserInfo(inparam);
        return result;
    }
    
    public IData queryCustInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryCustInfo(inparam);
        return result;
    }
    
    public IData queryUserListInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserListInfo(inparam);
        return result;
    }
    
    public IData queryUserDetailInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserDetailInfo(inparam);
        return result;
    }
    
    public IData queryAccountStatus(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryAccountStatus(inparam);
        return result;
    }
    
    public IData queryUserInfoByID(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserInfoByID(inparam);
        return result;
    }
    
    public IData queryExpireInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryExpireInfo(inparam);
        return result;
    }
    
    public IData queryAddressName(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserInfo(inparam);
        return result;
    }
    
    public IData queryFreeCapacity(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserInfo(inparam);
        return result;
    }
    
    public IData queryBuildingNoByAddressID(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserInfo(inparam);
        return result;
    }
    
    public IData queryRoomNoByAddressID(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserInfo(inparam);
        return result;
    }
    
    public IData queryProgress(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryProgress(inparam);
        return result;
    }
    public IData querySaleActiveInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.querySaleActiveInfo(inparam);
        return result;
    }
    public IData sendSMS(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.sendSMS(inparam);
        return result;
    }
    public IData queryTradeInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryTradeInfo(inparam);
        return result;
    }
    //新增4个接口20171017
    public IData queryBroadbandInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryBroadbandInfo(inparam);
        return result;
    }
    public IData queryUCAInfo4KF(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUCAInfo4KF(inparam);
        return result;
    }
    public IData queryAllOffersByParam(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryAllOffersByParam(inparam);
        return result;
    }
    public IData queryCustomerKeyMessage(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryCustomerKeyMessage(inparam);
        return result;
    }
    //第一批
    public IData queryAllInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryAllInfo(inparam);
        return result;
    }
    public IData querySubscribeInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.querySubscribeInfo(inparam);
        return result;
    }
    public IData queryUserPUKInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserPUKInfo(inparam);
        return result;
    }
    //第二批
    public IData authUserPasswd(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.authUserPasswd(inparam);
        return result;
    }
    public IData authUserIden(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.authUserIden(inparam);
        return result;
    }
    public IData sendBusiInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.sendBusiInfo(inparam);
        return result;
    }
    public IData submitUserStop(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitUserStop(inparam);
        return result;
    }
    public IData submitUserOpen(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitUserOpen(inparam);
        return result;
    }
    public IData productSync(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.productSync(inparam);
        return result;
    }
    public IData queryGprsState(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryGprsState(inparam);
        return result;
    }
    public IData queryOfferTypeInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryOfferTypeInfo(inparam);
        return result;
    }
    public IData queryOffers(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryOffers(inparam);
        return result;
    }
    public IData queryUserMainOffer(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryUserMainOffer(inparam);
        return result;
    }
    public IData queryInuseSPBusiness(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryInuseSPBusiness(inparam);
        return result;
    }
    public IData precheckOffersOrder(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.precheckOffersOrder(inparam);
        return result;
    }
    public IData submitOffersOrder(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitOffersOrder(inparam);
        return result;
    }
    public IData submitMainOfferChange(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitMainOfferChange(inparam);
        return result;
    }
    public IData submitSPBusiness(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitSPBusiness(inparam);
        return result;
    }
    public IData queryScoreExchageCommodities(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryScoreExchageCommodities(inparam);
        return result;
    }
    public IData submitScoreExchange(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitScoreExchange(inparam);
        return result;
    }
    public IData changeOfLongDistanceRoamingLevel(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.changeOfLongDistanceRoamingLevel(inparam);
        return result;
    }
    public IData queryOrderedCampaign(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryOrderedCampaign(inparam);
        return result;
    }
    public IData queryCampaignRules(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryCampaignRules(inparam);
        return result;
    }
    public IData queryCampaigns(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryCampaigns(inparam);
        return result;
    }
    public IData preCheckCampaignsOrder(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.preCheckCampaignsOrder(inparam);
        return result;
    }
    public IData submitCampaignsOrder(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.submitCampaignsOrder(inparam);
        return result;
    }
    public IData queryPrestoreReturnInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryPrestoreReturnInfo(inparam);
        return result;
    }
    
   //REQ202005140037  关于账单3.0开发的需求   
    public IData queryPrestoreReturnInfo_112(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryPrestoreReturnInfo_112(inparam);
        return result;
    }
    
    public IData querySMSSendInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.querySMSSendInfo(inparam);
        return result;
    }
    public IData authCustRealName(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.authCustRealName(inparam);
        return result;
    }
    public IData queryCampaignGoodsInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryCampaignGoodsInfo(inparam);
        return result;
    }
    public IData queryCampaignBindInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IData result = bean.queryCampaignBindInfo(inparam);
        return result;
    }
	/*** 随机短信验证码下发*/
	public IData sendRandomPwd(IData Idata) throws Exception
	{
		BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
		return bean.sendRandomPwd(Idata);			
	}
	/** 
	* 随机短信验证码校验
    */
	public IData authRandomPwd(IData Idata) throws Exception
	{
		BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
		return bean.authRandomPwd(Idata);
	}
	/** 
	* 查询宽带用户信息
    */
    public IDataset queryWideUserInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IDataset result = bean.queryWideUserInfoForYH(inparam);
        return result;
    }
    /** 
	* 查询用户信息
    */
    public IDataset queryUserCommodityInfo(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IDataset result = bean.queryUserCommodityInfo(inparam);
        return result;
    }

    public IDataset queryFubaoDiscntQualification(IData inparam) throws Exception
    {
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IDataset result = bean.queryFubaoDiscntQualification(inparam);
        return result;
    }
    /**
     * 获取商品目录
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData queryTradeCatalog(IData inparam) throws Exception{
    	inparam=inparam.getData("params");
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	String queryType=inparam.getString("queryType");
    	IData outData=new DataMap();
    	IDataset catalogList=new DatasetList();
    	outData.put("cataloglist", catalogList);
    	IDataset resultList=new DatasetList();
    	resultList.add(outData);
    	if(StringUtils.isNotEmpty(inparam.getString("userMobile"))){
    		IDataset pInfos=null;
    		//处理加入代办或者预约的产品
    		IDataset searchTermList=inparam.getDataset("searchTermList");
    		if(IDataUtil.isNotEmpty(searchTermList)){
    			for(int i=0;i<searchTermList.size();i++){
    				IData searchTerm=searchTermList.getData(i);
    				if(("1".equals(searchTerm.getString("type"))
    						||"2".equals(searchTerm.getString("type")))&&"0".equals(searchTerm.getString("isMain"))){
    					if(StringUtils.isEmpty(searchTerm.getString("offerId"))){
    						return bean.prepareOutResult(1, "searchTermList的offerId不能为空", new DataMap());
    					}
    					pInfos=new DatasetList();
    					IData productData=new DataMap();
    					productData.put("PRODUCT_ID", (searchTerm.getString("offerId","").split("\\|"))[0]);
    					pInfos.add(productData);
    					break;
    				}
    			}
    		}
    		//没有代办或预约的再进行查询当前主套餐
    		if(IDataUtil.isEmpty(pInfos)){
	    		IData userInfo=UcaInfoQry.qryUserInfoBySn(inparam.getString("userMobile"));
	    		if(IDataUtil.isEmpty(userInfo)){
	    			return bean.prepareOutResult(1, "根据手机号["+inparam.getString("userMobile")+"]查无用户信息", outData);
	    		}
	    		pInfos=UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
	    		if(IDataUtil.isEmpty(pInfos)){
	    			return bean.prepareOutResult(1, "找不到用户主产品信息", outData);
	    		}
    		}
    		
    		if("00".equals(queryType)){//全部商品
	    		IDataset groups=UpcCall.queryOfferGroupRelOfferId("P", pInfos.getData(0).getString("PRODUCT_ID"));
	    		for(int i=0;i<groups.size();i++){
	    			if("0".equals(groups.getData(i).getString("FORCE_TAG"))){//1为必选组放到子商品结构查询
	    				IData data=new DataMap();
	    				data.put("catalogCode", groups.getData(i).getString("GROUP_ID"));
	    	    		data.put("catalogName", groups.getData(i).getString("GROUP_NAME"));
	    	    		data.put("isMainTariff", "0");
	    	    		data.put("countryOrRegion", "");
	    	    		data.put("parentDirectoryCode", "0");
	    	    		data.put("subdirectoryCode", "");
	    	    		data.put("intlRoamType", "");
	    	    		catalogList.add(data);
	    			}
	    		}
    		}else if("01".equals(queryType)){//主资费
    			 IData rst = UpcCall.queryTransProducts(pInfos.getData(0).getString("PRODUCT_ID"));
    			 IDataset productList=rst.getDataset("OFFERS");
    			 IData brandData=new DataMap();
    			 for(int i=0;i<productList.size();i++){
    				 if(!brandData.containsKey(productList.getData(i).getString("CATALOG_ID"))){
    					 brandData.put(productList.getData(i).getString("CATALOG_ID"), productList.getData(i).getString("CATALOG_NAME"));
    				 }
    			 }
    			 if(IDataUtil.isNotEmpty(brandData)){
    				 for (String key : brandData.keySet()) {
    					IData data=new DataMap();
 	    				data.put("catalogCode", key);
 	    	    		data.put("catalogName", brandData.getString(key));
 	    	    		data.put("isMainTariff", "0");
 	    	    		data.put("countryOrRegion", "");
 	    	    		data.put("parentDirectoryCode", "0");
 	    	    		data.put("subdirectoryCode", "");
 	    	    		data.put("intlRoamType", "");
 	    	    		catalogList.add(data);
    				 }
    			 }
    		}
    	}
    	return bean.prepareOutResult(0, "", resultList);
    }
    /**
     * 子商品结构信息查询(只查产品必选组元素)
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData queryOfferStrutureInfo(IData inparam) throws Exception{
    	inparam=inparam.getData("params");
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	String offerId=inparam.getString("offerId");
    	if(StringUtils.isNotEmpty(offerId)&&offerId.contains("|")){
    		offerId=offerId.split("\\|")[0];
    	}
    	
    	String spCode=inparam.getString("spCode");
    	String bizCode=inparam.getString("bizCode");
    	String batchId=inparam.getString("batchId");
    	String packageId=inparam.getString("packageId");
    	
    	IDataset resultList=new DatasetList();
    	
    	//先获取当前用户套餐存在的元素
    	IData userInfo=UcaInfoQry.qryUserInfoBySn(inparam.getString("userMobile"));
		if(IDataUtil.isEmpty(userInfo)){
			return bean.prepareOutResult(1, "根据手机号["+inparam.getString("userMobile")+"]查无用户信息", new DataMap());
		}
		IDataset pInfos=UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
		if(IDataUtil.isEmpty(pInfos)){
			return bean.prepareOutResult(1, "找不到用户主产品信息", new DataMap());
		}
		IData request=new DataMap();
		request.put("ROUTE_EPARCHY_CODE", "0898");
		request.put("EPARCHY_CODE", "0898");
		request.put("USER_ID", userInfo.getString("USER_ID"));
		request.put("TRADE_TYPE_CODE", "110");
		if(StringUtils.isNotEmpty(offerId)&&
				(!(pInfos.getData(0).getString("PRODUCT_ID").equals(offerId)))){
			request.put("USER_PRODUCT_ID", pInfos.getData(0).getString("PRODUCT_ID"));
			request.put("NEW_PRODUCT_ID", offerId);
		}
		IDataset results=CSAppCall.call("CS.SelectedElementSVC.getUserElements", request);
		IDataset selectElements=results.getData(0).getDataset("SELECTED_ELEMENTS");
		
		
    	IDataset groups=UpcCall.queryOfferGroupRelOfferId("P", offerId);
    	IData groupData=null;
    	for(int i=0;i<groups.size();i++){
			if("1".equals(groups.getData(i).getString("FORCE_TAG"))){//1为必选组放到子商品结构查询
				IDataset offers =UpcCall.queryGroupComRelOfferByGroupId(groups.getData(i).getString("GROUP_ID"));
				ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offers);
				//过滤掉已选元素
				groupData=new DataMap();
				IDataset offerList=new DatasetList();
				String minCount="";
				String maxCount="";
				if("-1".equals(groups.getData(i).getString("MIN_NUM"))){
					minCount="1";
				}else{
					minCount=groups.getData(i).getString("MIN_NUM");
				}
				if("-1".equals(groups.getData(i).getString("MAX_NUM"))){
					maxCount="99";
				}else{
					maxCount=groups.getData(i).getString("MAX_NUM");
				}
				groupData.put("minCount", minCount);
    			groupData.put("maxCount", maxCount);
    			groupData.put("resourceId", groups.getData(i).getString("GROUP_ID"));
    			groupData.put("resourceType", groups.getData(i).getString("GROUP_NAME"));
    			groupData.put("subOfferChooseType", "1");
    			groupData.put("offerList", offerList);
				for(int j=0;j<offers.size();j++){
					boolean isExist=false;
					for(int k=0;k<selectElements.size();k++){
						if(selectElements.getData(k).getString("ELEMENT_ID")
								.equals(offers.getData(j).getString("OFFER_CODE"))
								&&"exist".equals(selectElements.getData(k).getString("ELEMENT_ID"))){
							isExist=true;
							break;
						}
					}
					
					IData offerData=new DataMap();
					
					offerData.put("offerId", offers.getData(j).getString("OFFER_CODE")+"|"+offers.getData(j).getString("OFFER_TYPE"));
    				offerData.put("offerName", offers.getData(j).getString("OFFER_NAME"));
    				offerData.put("spCode", "");
    				offerData.put("bizCode", "");
    				offerData.put("servType", "");
    				offerData.put("batchId", "");
    				offerData.put("batchName", "");
    				offerData.put("packageId", "");
    				offerData.put("packageName", "");
    				offerData.put("upOfferId", "");
    				offerData.put("offerPrice", "");
    				offerData.put("offerDesc",  offers.getData(j).getString("DESCRIPTION"));
    				String provinceOfferType="";
    				String provinceOfferTypeName="";
    				if("D".equals(offers.getData(j).getString("OFFER_TYPE"))){
    					provinceOfferType="D";
    					provinceOfferTypeName="优惠";
    				}else if("Z".equals(offers.getData(j).getString("OFFER_TYPE"))){
    					provinceOfferType="Z";
    					provinceOfferTypeName="增值产品";
    				}else if("S".equals(offers.getData(j).getString("OFFER_TYPE"))){
    					provinceOfferType="S";
    					provinceOfferTypeName="服务";
    				}
    				offerData.put("provinceOfferType", provinceOfferType);
    				offerData.put("provinceOfferTypeName", provinceOfferTypeName);
    				offerData.put("minMount", "");
    				offerData.put("maxMount", "");
    				offerData.put("isOrdered", isExist?"1":"0");
    				
    				String chooseTag="";
    				if(offers.getData(j).getString("SELECT_FLAG","").equals("0")){
    					chooseTag="1";
    				}else if(offers.getData(j).getString("SELECT_FLAG","").equals("1")){
    					chooseTag="2";
    				}else if(offers.getData(j).getString("SELECT_FLAG","").equals("2")){
    					chooseTag="0";
    				}
    				offerData.put("chooseTag", chooseTag);
    				offerData.put("effMode", "");
    				offerData.put("effTime", offers.getData(j).getString("VALID_DATE",""));
    				offerData.put("expireTime", offers.getData(j).getString("EXPIRE_DATE",""));
    				offerData.put("validate", "");
    				offerData.put("expireDate", "");
    				offerData.put("orderIfSend", "0");
    				offerData.put("cancelIfSend", "0");
    				offerData.put("openCycleUnit", "");
    				
    				//获取元素属性
    				/*IData inParam=new DataMap();
    				inParam.put("ELEMENT_ID", offers.getData(j).getString("OFFER_CODE"));
    				inParam.put("ELEMENT_TYPE_CODE", offers.getData(j).getString("OFFER_TYPE"));
    				IDataset attrs=CSAppCall.call("CS.SelectedElementSVC.getElementAttrs", inParam);
    				IData attrData=null;
    				IDataset attrList=new DatasetList();
    				
    				for(int k=0;k<attrs.size();k++){
    					attrData=new DataMap();
    					attrData.put("attrCode", attrs.getData(k).getString("ATTR_CODE"));
    					attrData.put("attrName", attrs.getData(k).getString("ATTR_FIELD_NAME"));
    					attrData.put("attrValue", attrs.getData(k).getString("ATTR_FIELD_CODE"));
    					attrData.put("attrText", attrs.getData(k).getString("ATTR_FIELD_NAME"));
    					
    					String attrType="EDIT";
    					if("0".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//文本编辑框
    						attrType="EDIT";
    					}else if("1".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))
    							  ||"3".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//下拉框
    						attrType="SELECT";
    					}else if("7".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//时间输入框
    						attrType="TIME";
    					}
    					
    					attrData.put("attrType", attrType);
    					attrData.put("defaultval", attrs.getData(k).getString("ATTR_INIT_VALUE"));
    					attrData.put("minLen", attrs.getData(k).getString("ATTR_FIELD_MIN",""));
    					attrData.put("maxLen", attrs.getData(k).getString("ATTR_FIELD_MAX",""));
    					attrData.put("attrDisplayValue", attrs.getData(k).getString("ATTR_FIELD_NAME"));
    					attrList.add(attrData);
    				}
    				
    				offerData.put("offerAttrList",attrList);*/
    				
					offerList.add(offerData);
					
				}
				resultList.add(groupData);
			}
		}
    	
    	return bean.prepareOutResult(0, "", resultList); 
    }
    /**
     * 商品信息查询
     * @param inparam
     * @throws Exception
     */
    public IData queryOfferInfo(IData inparam) throws Exception{
    	inparam=inparam.getData("params");
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	String queryType=inparam.getString("queryType");
    	String tradeCatalog=inparam.getString("tradeCatalog");//商品目录
    	String offerId=inparam.getString("batchId");//商品编码
    	String fuzzySearchKeywords=inparam.getString("fuzzySearchKeywords");
    	String spCode=inparam.getString("spCode");
    	String bizCode=inparam.getString("bizCode");
    	String actId=inparam.getString("actId");
    	String actName=inparam.getString("actName");
    	String packageId=inparam.getString("packageId");
    	String packageName=inparam.getString("packageName");
    	String userMobile=inparam.getString("userMobile");
    	
    	IDataset resultList=new DatasetList();
    	IData outData=new DataMap();
    	IDataset tradeList=new DatasetList();
    	
    	//有传入商品目录
    	if(StringUtils.isNotEmpty(tradeCatalog)){
	    	//查询主资费
	    	if("01".equals(queryType)){
	    		IDataset pInfos=null;
	    		//处理加入代办或者预约的产品
	    		IDataset searchTermList=inparam.getDataset("searchTermList");
	    		if(IDataUtil.isNotEmpty(searchTermList)){
	    			for(int i=0;i<searchTermList.size();i++){
	    				IData searchTerm=searchTermList.getData(i);
	    				if(("1".equals(searchTerm.getString("type"))
	    						||"2".equals(searchTerm.getString("type")))&&"0".equals(searchTerm.getString("isMain"))){
	    					if(StringUtils.isEmpty(searchTerm.getString("offerId"))){
	    						return bean.prepareOutResult(1, "searchTermList的offerId不能为空", new DataMap());
	    					}
	    					pInfos=new DatasetList();
	    					IData productData=new DataMap();
	    					productData.put("PRODUCT_ID", (searchTerm.getString("offerId","").split("\\|"))[0]);
	    					pInfos.add(productData);
	    					break;
	    				}
	    			}
	    		}
	    		//没有代办或者预约的再查询当前主套餐
	    		if(IDataUtil.isEmpty(pInfos)){
		    		//先获取当前用户套餐存在的元素
			    	IData userInfo=UcaInfoQry.qryUserInfoBySn(userMobile);
					if(IDataUtil.isEmpty(userInfo)){
						return bean.prepareOutResult(1, "根据手机号["+userMobile+"]查无用户信息", new DataMap());
					}
					pInfos=UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
					if(IDataUtil.isEmpty(pInfos)){
						return bean.prepareOutResult(1, "找不到用户主产品信息", new DataMap());
					}
	    		}
				
	    		tradeList=this.qryOfferByProduct("P", offerId, fuzzySearchKeywords, queryType, tradeCatalog,pInfos.getData(0).getString("PRODUCT_ID"));
	    	//查询优惠及服务
	    	}else if("00".equals(queryType)){
	    		tradeList=this.qryOfferByGroup("DS", offerId, fuzzySearchKeywords, queryType, tradeCatalog,userMobile);
	    	}
    	}
    	//查询梦网业务
		if("05".equals(queryType)){
			tradeList=this.qryPlatService(spCode, bizCode, queryType, tradeCatalog, fuzzySearchKeywords);
		}
    
    	outData.put("tradeList", tradeList);
    	resultList.add(outData);
    	return bean.prepareOutResult(0, "", resultList); 
    }
    /**
     * 根据主套餐查询
     * @param type
     * @return
     * @throws Exception
     */
    private IDataset qryOfferByProduct(String type,String offerId,String keyword,
    		String queryType,String tradeCatalog,String productId) throws Exception{
    	IDataset tradeList=new DatasetList();
    	
    	IDataset products=new DatasetList();
    		
    	IDataset likeOffers=new DatasetList();
		if(StringUtils.isNotEmpty(keyword)){//关键字查询
			likeOffers=UpcCall.qryOffersByOfferTypeLikeOfferName("P", "", keyword);
			if (IDataUtil.isNotEmpty(likeOffers)) {
                for (Object obj : likeOffers) {
                    IData product = (IData) obj;
                    if (StringUtils.isEmpty(product.getString("PRODUCT_ID", ""))) {
                        product.put("PRODUCT_ID", product.getString("OFFER_CODE", ""));
                    }
                }
		    }
			ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), likeOffers);
			IData offer=null;
			for(int i=0;i<likeOffers.size();i++){
				offer=new DataMap();
				offer.put("OFFER_TYPE", "P");
				offer.put("OFFER_CODE", likeOffers.getData(i).getString("OFFER_CODE"));
				offer.put("OFFER_NAME", likeOffers.getData(i).getString("OFFER_NAME"));
				offer.put("DESCRIPTION", likeOffers.getData(i).getString("DESCRIPTION"));
				IDataset enableModels=UpcCall.queryEnableModeRelByOfferId("P",likeOffers.getData(i).getString("OFFER_CODE"));
				if(IDataUtil.isNotEmpty(enableModels)){
					offer.put("VALID_DATE", enableModels.getData(0).getString("VALID_DATE"));
					offer.put("EXPIRE_DATE", enableModels.getData(0).getString("EXPIRE_DATE"));
				}
				products.add(offer);
			}
			
		}else{//查询主套餐
		    IData rst = UpcCall.queryTransProducts(productId);
		    IDataset productList=rst.getDataset("OFFERS");
		    if (IDataUtil.isNotEmpty(productList)) {
                for (Object obj : productList) {
                    IData product = (IData) obj;
                    if (StringUtils.isEmpty(product.getString("PRODUCT_ID", ""))) {
                        product.put("PRODUCT_ID", product.getString("OFFER_CODE", ""));
                    }
                }
		    }
		    ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), productList);
			IData offer=null;
			for(int i=0;i<productList.size();i++){
				if(tradeCatalog.equals(productList.getData(i).getString("CATALOG_ID"))){
					offer=new DataMap();
					offer.put("OFFER_TYPE", "P");
					offer.put("OFFER_CODE", productList.getData(i).getString("OFFER_CODE"));
					offer.put("OFFER_NAME", productList.getData(i).getString("OFFER_NAME"));
					offer.put("DESCRIPTION", productList.getData(i).getString("DESCRIPTION"));
					IDataset enableModels=UpcCall.queryEnableModeRelByOfferId("P",productList.getData(i).getString("OFFER_CODE"));
					if(IDataUtil.isNotEmpty(enableModels)){
						offer.put("VALID_DATE", enableModels.getData(0).getString("VALID_DATE"));
						offer.put("EXPIRE_DATE", enableModels.getData(0).getString("EXPIRE_DATE"));
					}
					products.add(offer);
				}
			}
		}
		
		IData product=null;
		for(int i=0;i<products.size();i++){
			String offerType=products.getData(i).getString("OFFER_TYPE");
			product =new DataMap();
			product.put("tradeType", queryType);
			product.put("tradeCatalogId", tradeCatalog);
			
			String tradeCatalogName="主产品";
			String provinceOfferType="P";
			String provinceOfferTypeName="主产品";
			
			product.put("tradeCatalogName", tradeCatalogName);
			product.put("intlRoamType", "");
			product.put("spCode", "");
			product.put("bizCode", "");
			product.put("servType", "");
			product.put("batchId", "");
			product.put("batchName", "");
			product.put("packageId",  "");
			product.put("packageName", "");
			product.put("offerId", products.getData(i).getString("OFFER_CODE")+"|P");
			product.put("offerName", products.getData(i).getString("OFFER_NAME"));
			product.put("provinceOfferType", provinceOfferType);
			product.put("provinceOfferTypeName", provinceOfferTypeName);
			product.put("tariff", "0");
			product.put("adaptRegion", "海南");
			product.put("adaptBrand", "");
			product.put("localInputAuthType", "3,4");
			product.put("otherInputAuthType", "3,4");
			product.put("otherOutputAuthType", "3,4");
			product.put("localOutputUnsubscribeAuthType", "3,4");
			product.put("otherOutputOrderAuthType", "3,4");
			product.put("newInteracAuthType", "3,4");
			product.put("isMulti", "");
			product.put("messageProcess", "0");
			product.put("isRelevanceMessageProcess", "0");
			product.put("effectType", "1,2");
			product.put("isOrderExpiredate", "0");
			product.put("failureWay", "1,2");
			product.put("validPhaseBgnTime", products.getData(i).getString("VALID_DATE"));
			product.put("validPhaseFinishTime", products.getData(i).getString("EXPIRE_DATE"));
			product.put("orderRelevance", "");
			product.put("unsubscribeRelevance", "");
			product.put("relevanceBossId", "");
			product.put("allowOtherBusi", "1");
			product.put("interRoamEstlPridDesc", "");
			product.put("action", "3");
			product.put("isAttendIntlRoamIBase", "1");
			product.put("isAttendIntlRoamIOffer", "1");
			product.put("callModeAttendLim", "1,2");
			product.put("isWithinSubOffer", "0");
			product.put("isRelevanceOffer", "0");
			product.put("tags", "");
			product.put("offerDes", products.getData(i).getString("DESCRIPTION"));
			product.put("remark", "");
			product.put("openCycleUnit", "");
			product.put("openCycleValue", "");

			/*IDataset offerSpeAttrSubList=new DatasetList();
			if("D".equals(offerType)||"S".equals(offerType)){
				//获取元素属性
				IData inParam=new DataMap();
				inParam.put("ELEMENT_ID", products.getData(i).getString("OFFER_CODE"));
				inParam.put("ELEMENT_TYPE_CODE", type);
				IDataset attrs=CSAppCall.call("CS.SelectedElementSVC.getElementAttrs", inParam);
				IData attrData=null;
				
				for(int k=0;k<attrs.size();k++){
					attrData=new DataMap();
					attrData.put("attrCode", attrs.getData(k).getString("ATTR_CODE"));
					attrData.put("attrName", attrs.getData(k).getString("ATTR_FIELD_NAME"));
					attrData.put("attrValue", attrs.getData(k).getString("ATTR_FIELD_CODE"));
					attrData.put("attrText", attrs.getData(k).getString("ATTR_FIELD_NAME"));
					
					String attrType="EDIT";
					if("0".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//文本编辑框
						attrType="EDIT";
					}else if("1".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))
							  ||"3".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//下拉框
						attrType="SELECT";
					}else if("7".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//时间输入框
						attrType="TIME";
					}
					
					attrData.put("attrType", attrType);
					attrData.put("defaultval", attrs.getData(k).getString("ATTR_INIT_VALUE"));
					attrData.put("minLen", attrs.getData(k).getString("ATTR_FIELD_MIN",""));
					attrData.put("maxLen", attrs.getData(k).getString("ATTR_FIELD_MAX",""));
					attrData.put("attrDisplayValue", attrs.getData(k).getString("ATTR_FIELD_NAME"));
					offerSpeAttrSubList.add(attrData);
				}
			}
			product.put("offerSpeAttrSubList", offerSpeAttrSubList);*/
			
			tradeList.add(product);
		}
		return tradeList;
    }
    /**
     * 根据组查元素
     * @param type
     * @return
     * @throws Exception
     */
    private IDataset qryOfferByGroup(String type,String offerId,String keyword,
    		String queryType,String tradeCatalog,String userMobile) throws Exception{
    	IDataset tradeList=new DatasetList();
    	
    	IDataset products=new DatasetList();
    		
    	IDataset likeOffers=new DatasetList();
		if(StringUtils.isNotEmpty(keyword)){//关键字查询
			likeOffers=UpcCall.qryOffersByOfferTypeLikeOfferName("D", "", keyword);
			ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), likeOffers);
			IData offer=null;
			for(int i=0;i<likeOffers.size();i++){
				offer=new DataMap();
				offer.put("OFFER_TYPE", "D");
				offer.put("OFFER_CODE", likeOffers.getData(i).getString("OFFER_CODE"));
				offer.put("OFFER_NAME", likeOffers.getData(i).getString("OFFER_NAME"));
				offer.put("DESCRIPTION", likeOffers.getData(i).getString("DESCRIPTION"));
				IDataset enableModels=UpcCall.queryEnableModeRelByOfferId("D",likeOffers.getData(i).getString("OFFER_CODE"));
				if(IDataUtil.isNotEmpty(enableModels)){
					offer.put("VALID_DATE", enableModels.getData(0).getString("VALID_DATE"));
					offer.put("EXPIRE_DATE", enableModels.getData(0).getString("EXPIRE_DATE"));
				}
				products.add(offer);
			}
			likeOffers=UpcCall.qryOffersByOfferTypeLikeOfferName("S", "", keyword);
			ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), likeOffers);
			for(int i=0;i<likeOffers.size();i++){
				offer=new DataMap();
				offer.put("OFFER_TYPE", "S");
				offer.put("OFFER_CODE", likeOffers.getData(i).getString("OFFER_CODE"));
				offer.put("OFFER_NAME", likeOffers.getData(i).getString("OFFER_NAME"));
				offer.put("DESCRIPTION", likeOffers.getData(i).getString("DESCRIPTION"));
				IDataset enableModels=UpcCall.queryEnableModeRelByOfferId("S",likeOffers.getData(i).getString("OFFER_CODE"));
				if(IDataUtil.isNotEmpty(enableModels)){
					offer.put("VALID_DATE", enableModels.getData(0).getString("VALID_DATE"));
					offer.put("EXPIRE_DATE", enableModels.getData(0).getString("EXPIRE_DATE"));
				}
				products.add(offer);
			}
			
		}else{//根据组查询元素
			products=UpcCall.queryGroupComRelOfferByGroupId(tradeCatalog);
			ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), products);
		}
		
		IData product=null;
		for(int i=0;i<products.size();i++){
			String offerType=products.getData(i).getString("OFFER_TYPE");
			product =new DataMap();
			product.put("tradeType", queryType);
			product.put("tradeCatalogId", tradeCatalog);
			
			String tradeCatalogName="";
			String provinceOfferType="";
			String provinceOfferTypeName="";
			if("S".equals(offerType)){
				tradeCatalogName="服务";
				provinceOfferType="S";
				provinceOfferTypeName="服务";
			}else if("D".equals(offerType)){
				tradeCatalogName="优惠";
				provinceOfferType="D";
				provinceOfferTypeName="优惠";
			}else if("Z".equals(offerType)){
				tradeCatalogName="平台服务";
				provinceOfferType="Z";
				provinceOfferTypeName="平台服务";
			}
			
			product.put("tradeCatalogName", tradeCatalogName);
			product.put("intlRoamType", "");
			product.put("spCode", "");
			product.put("bizCode", "");
			product.put("servType", "");
			product.put("batchId", "");
			product.put("batchName", "");
			product.put("packageId",  "");
			product.put("packageName", "");
			product.put("offerId", products.getData(i).getString("OFFER_CODE")+"|"+products.getData(i).getString("OFFER_TYPE"));
			product.put("offerName", products.getData(i).getString("OFFER_NAME"));
			product.put("provinceOfferType", provinceOfferType);
			product.put("provinceOfferTypeName", provinceOfferTypeName);
			product.put("tariff", "0");
			product.put("adaptRegion", "海南");
			product.put("adaptBrand", "");
			product.put("localInputAuthType", "3,4");
			product.put("otherInputAuthType", "3,4");
			product.put("otherOutputAuthType", "3,4");
			product.put("localOutputUnsubscribeAuthType", "3,4");
			product.put("otherOutputOrderAuthType", "3,4");
			product.put("newInteracAuthType", "3,4");
			product.put("isMulti", "");
			product.put("messageProcess", "0");
			product.put("isRelevanceMessageProcess", "0");
			product.put("effectType", "1,2");
			product.put("isOrderExpiredate", "0");
			product.put("failureWay", "1,2");
			product.put("validPhaseBgnTime", products.getData(i).getString("VALID_DATE"));
			product.put("validPhaseFinishTime", products.getData(i).getString("EXPIRE_DATE"));
			product.put("orderRelevance", "");
			product.put("unsubscribeRelevance", "");
			product.put("relevanceBossId", "");
			product.put("allowOtherBusi", "1");
			product.put("interRoamEstlPridDesc", "");
			product.put("action", "3");
			product.put("isAttendIntlRoamIBase", "1");
			product.put("isAttendIntlRoamIOffer", "1");
			product.put("callModeAttendLim", "1,2");
			product.put("isWithinSubOffer", "0");
			product.put("isRelevanceOffer", "0");
			product.put("tags", "");
			product.put("offerDes", products.getData(i).getString("DESCRIPTION"));
			product.put("remark", "");
			product.put("openCycleUnit", "");
			product.put("openCycleValue", "");

			/*IDataset offerSpeAttrSubList=new DatasetList();
			if("D".equals(offerType)||"S".equals(offerType)){
				//获取元素属性
				IData inParam=new DataMap();
				inParam.put("ELEMENT_ID", products.getData(i).getString("OFFER_CODE"));
				inParam.put("ELEMENT_TYPE_CODE", type);
				IDataset attrs=CSAppCall.call("CS.SelectedElementSVC.getElementAttrs", inParam);
				IData attrData=null;
				
				for(int k=0;k<attrs.size();k++){
					attrData=new DataMap();
					attrData.put("attrCode", attrs.getData(k).getString("ATTR_CODE"));
					attrData.put("attrName", attrs.getData(k).getString("ATTR_FIELD_NAME"));
					attrData.put("attrValue", attrs.getData(k).getString("ATTR_FIELD_CODE"));
					attrData.put("attrText", attrs.getData(k).getString("ATTR_FIELD_NAME"));
					
					String attrType="EDIT";
					if("0".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//文本编辑框
						attrType="EDIT";
					}else if("1".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))
							  ||"3".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//下拉框
						attrType="SELECT";
					}else if("7".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//时间输入框
						attrType="TIME";
					}
					
					attrData.put("attrType", attrType);
					attrData.put("defaultval", attrs.getData(k).getString("ATTR_INIT_VALUE"));
					attrData.put("minLen", attrs.getData(k).getString("ATTR_FIELD_MIN",""));
					attrData.put("maxLen", attrs.getData(k).getString("ATTR_FIELD_MAX",""));
					attrData.put("attrDisplayValue", attrs.getData(k).getString("ATTR_FIELD_NAME"));
					offerSpeAttrSubList.add(attrData);
				}
			}
			product.put("offerSpeAttrSubList", offerSpeAttrSubList);*/
			
			tradeList.add(product);
		}
		return tradeList;
    }
    /**
     * 查询活动
     * @param actId
     * @param queryType
     * @param tradeCatalog
     * @return
     * @throws Exception
     */
    private IDataset qryProductById(String actId,String queryType,String tradeCatalog) throws Exception{
    	IDataset tradeList=new DatasetList();
    	IDataset products=new DatasetList();
    	if(StringUtils.isEmpty(actId)){
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX02"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX03"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX04"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX07"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX08"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX09"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX10"));
    		products.addAll(UpcCall.qryCatalogsByUpCatalogId("YX11"));
    	}else{
    		products=UpcCall.qryCatalogByCatalogId(actId);
    	}
    	
		IData product=null;
		for(int i=0;i<products.size();i++){
			product =new DataMap();
			product.put("tradeType", queryType);
			product.put("tradeCatalogId", tradeCatalog);
			
			String tradeCatalogName="营销活动";
			String provinceOfferType="";
			String provinceOfferTypeName="";
			product.put("tradeCatalogName", tradeCatalogName);
			product.put("intlRoamType", "");
			product.put("spCode", "");
			product.put("bizCode", "");
			product.put("servType", "");
			product.put("batchId", products.getData(i).getString("CATALOG_ID"));
			product.put("batchName", products.getData(i).getString("CATALOG_NAME"));
			product.put("packageId",  "");
			product.put("packageName", "");
			
			product.put("offerId", "");
			product.put("offerName", "");
			product.put("provinceOfferType", provinceOfferType);
			product.put("provinceOfferTypeName", provinceOfferTypeName);
			product.put("tariff", "");
			product.put("adaptRegion", "");
			product.put("adaptBrand", "");
			product.put("localInputAuthType", "");
			product.put("otherInputAuthType", "");
			product.put("otherOutputAuthType", "");
			product.put("localOutputUnsubscribeAuthType", "");
			product.put("otherOutputOrderAuthType", "");
			product.put("newInteracAuthType", "");
			product.put("isMulti", "");
			product.put("messageProcess", "");
			product.put("isRelevanceMessageProcess", "");
			product.put("effectType", "1,2");
			product.put("isOrderExpiredate", "");
			product.put("failureWay", "1,2");
			product.put("validPhaseBgnTime", "");
			product.put("validPhaseFinishTime", "");
			product.put("orderRelevance", "");
			product.put("unsubscribeRelevance", "");
			product.put("relevanceBossId", "");
			product.put("allowOtherBusi", "");
			product.put("interRoamEstlPridDesc", "");
			product.put("action", "");
			product.put("isAttendIntlRoamIBase", "");
			product.put("isAttendIntlRoamIOffer", "");
			product.put("callModeAttendLim", "");
			product.put("isWithinSubOffer", "");
			product.put("isRelevanceOffer", "");
			product.put("tags", "");
			product.put("offerDes", products.getData(i).getString("DESCRIPTION"));
			product.put("remark", "");
			product.put("openCycleUnit", "");
			product.put("openCycleValue", "");

			IDataset offerSpeAttrSubList=new DatasetList();
			product.put("offerSpeAttrSubList", offerSpeAttrSubList);
			
			tradeList.add(product);
		}
		return tradeList;
    }
    /**
     * 查询增值服务
     * @param actId
     * @param queryType
     * @param tradeCatalog
     * @return
     * @throws Exception
     */
    private IDataset qryPlatService(String spCode,String bizCode,String queryType,String tradeCatalog,String keyword) throws Exception{
    	IDataset tradeList=new DatasetList();
    	SearchResponse resp = SearchClient.search("PM_OFFER_PLATSVC", keyword, 0, 50);
        IDataset products = resp.getDatas();
        //System.out.println("===="+products.toString());
        //IDataset products=UpcCall.querySpServiceAndInfoAndParamByCond(null,spCode,bizCode,null);
		IData product=null;
		for(int i=0;i<products.size();i++){
			String servMode="1";
			/*IDataset spData=UpcCall.qrySpServiceSpInfo(products.getData(i).getString("SP_CODE"),products.getData(i).getString("BIZ_CODE"),null,null);
			if(IDataUtil.isNotEmpty(spData)&&"0".equals(spData.getData(0).getString("SERV_MODE"))){
				servMode="1";
			}*/
			
			product =new DataMap();
			product.put("tradeType", queryType);
			product.put("tradeCatalogId", tradeCatalog);
			product.put("tradeCatalogName", "增值业务");
			product.put("intlRoamType", "");
			product.put("spCode", products.getData(i).getString("SP_CODE"));
			product.put("bizCode", products.getData(i).getString("BIZ_CODE"));
			product.put("servType", servMode);
			product.put("batchId", "");
			product.put("batchName", "");
			product.put("packageId", "");
			product.put("packageName", "");
			product.put("offerId", products.getData(i).getString("SERVICE_ID","")+"|Z");
			product.put("offerName",products.getData(i).getString("SERVICE_NAME"));
			product.put("provinceOfferType", "Z");
			product.put("provinceOfferTypeName", "增值业务");
			
			//单位转换
			String tariff="0元";
			if(StringUtils.isNotEmpty(products.getData(i).getString("PRICE"))){
				try{
					tariff=(products.getData(i).getInt("PRICE")/1000)+"元";
				}catch(Exception e){
					
				}
			}
			
			product.put("tariff", tariff);
			product.put("adaptRegion", "海南");
			product.put("adaptBrand", "");
			product.put("localInputAuthType", "3,4");
			product.put("otherInputAuthType", "3,4");
			product.put("otherOutputAuthType", "3,4");
			product.put("localOutputUnsubscribeAuthType", "3,4");
			product.put("otherOutputOrderAuthType", "3,4");
			product.put("newInteracAuthType", "3,4");
			product.put("isMulti", "");
			product.put("messageProcess", "0");
			product.put("isRelevanceMessageProcess", "0");
			product.put("effectType", "1,2");
			product.put("isOrderExpiredate", "0");
			product.put("failureWay", "1,2");
			product.put("validPhaseBgnTime", "2010-01-01 00:00:00");
			product.put("validPhaseFinishTime", "2050-12-31 23:59:59");
			product.put("orderRelevance", "");
			product.put("unsubscribeRelevance", "");
			product.put("relevanceBossId", "");
			product.put("allowOtherBusi", "1");
			product.put("interRoamEstlPridDesc", "");
			product.put("action", "3");
			product.put("isAttendIntlRoamIBase", "1");
			product.put("isAttendIntlRoamIOffer", "1");
			product.put("callModeAttendLim", "1,2");
			product.put("isWithinSubOffer", "0");
			product.put("isRelevanceOffer", "0");
			product.put("tags", "");
			product.put("offerDes", products.getData(i).getString("SERVICE_NAME"));
			product.put("remark", "");
			product.put("openCycleUnit", "");
			product.put("openCycleValue", "");


			IDataset offerSpeAttrSubList=new DatasetList();
			product.put("offerSpeAttrSubList", offerSpeAttrSubList);
			
			tradeList.add(product);
		}
	    return tradeList;
    }
    /**
     * 关联商品信息查询
     * @param inparam
     * @throws Exception
     */
    public IData queryRelOfferInfo(IData inparam) throws Exception{
    	inparam=inparam.getData("params");
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	IDataset resultList=new DatasetList();
    	IDataset offers=new DatasetList();
    	IData outData=new DataMap();
    	IDataset offerList=inparam.getDataset("offerList");
    	IData userInfo=UcaInfoQry.qryUserInfoBySn(inparam.getString("userMobile"));
		if(IDataUtil.isEmpty(userInfo)){
			return bean.prepareOutResult(1, "根据手机号["+inparam.getString("userMobile")+"]查无用户信息", outData);
		}
		IDataset pInfos=UserProductInfoQry.queryMainProduct(userInfo.getString("USER_ID"));
		if(IDataUtil.isEmpty(pInfos)){
			return bean.prepareOutResult(1, "找不到用户主产品信息", outData);
		}
		
    	//获取变更主套餐
    	String newProductId="";
    	if(IDataUtil.isNotEmpty(offerList)){
    		newProductId=offerList.getData(0).getString("offerId");
    		if(newProductId.contains("|")){
    			newProductId=newProductId.split("\\|")[0];
    		}
    	}
    	
    	IData request=new DataMap();
		request.put("ROUTE_EPARCHY_CODE", "0898");
		request.put("EPARCHY_CODE", "0898");
		request.put("USER_ID", userInfo.getString("USER_ID"));
		request.put("TRADE_TYPE_CODE", "110");
		if(StringUtils.isNotEmpty(newProductId)
				&&(!(newProductId.equals(pInfos.getData(0).getString("PRODUCT_ID"))))){
			request.put("USER_PRODUCT_ID", pInfos.getData(0).getString("PRODUCT_ID"));
			request.put("NEW_PRODUCT_ID", newProductId);
		}
		IDataset results=CSAppCall.call("CS.SelectedElementSVC.getUserElements", request);
		IDataset selectElements=results.getData(0).getDataset("SELECTED_ELEMENTS");
		IData offerData=null;
		for(int i=0;i<selectElements.size();i++){
			offerData=new DataMap();
			offerData.put("offerId", selectElements.getData(i).getString("ELEMENT_ID")+"|"+selectElements.getData(i).getString("ELEMENT_TYPE_CODE"));
			offerData.put("offerName", selectElements.getData(i).getString("ELEMENT_NAME"));
			offerData.put("fatherOfferId", "0000");
			offerData.put("fatherOfferName", "主套餐");
			offerData.put("spCode", "");
			offerData.put("bizCode", "");
			offerData.put("servType", "");
			offerData.put("offerPrice", "0");
			offerData.put("status", "");
			String provinceofferTypeName="";
			String provinceOfferType=selectElements.getData(i).getString("ELEMENT_TYPE_CODE");
			if("D".equals(provinceOfferType)){
				provinceofferTypeName="优惠";
			}else if("S".equals(provinceOfferType)){
				provinceofferTypeName="服务";
			}else if("Z".equals(provinceOfferType)){
				provinceofferTypeName="平台业务";
			}
			offerData.put("provinceOfferType", provinceOfferType);
			offerData.put("provinceofferTypeName", provinceofferTypeName);
			offerData.put("chooseTag", selectElements.getData(i).getString("ELEMENT_FORCE_TAG"));
			offerData.put("ifQuit", "0");
			offerData.put("changeRemind", "");
			offerData.put("startTime", selectElements.getData(i).getString("START_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
			if(offerData.getString("startTime").length()==8){
				offerData.put("startTime",offerData.getString("startTime")+"000000");
			}
			offerData.put("endTime", selectElements.getData(i).getString("END_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
			if(offerData.getString("endTime").length()==8){
				offerData.put("endTime",offerData.getString("endTime")+"000000");
			}
			String ifOrder="1";
			if("0".equals(selectElements.getData(i).getString("MODIFY_TAG"))){
				ifOrder="0";
			}
			offerData.put("ifOrder", ifOrder);
			offerData.put("orderTime", selectElements.getData(i).getString("START_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
			if(offerData.getString("orderTime").length()==8){
				offerData.put("orderTime",offerData.getString("orderTime")+"000000");
			}
			offerData.put("effMode", "Type_Default");
			offerData.put("validate", selectElements.getData(i).getString("START_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
			if(offerData.getString("validate").length()==8){
				offerData.put("validate",offerData.getString("validate")+"000000");
			}
			offerData.put("expireDate", selectElements.getData(i).getString("END_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
			if(offerData.getString("expireDate").length()==8){
				offerData.put("expireDate",offerData.getString("expireDate")+"000000");
			}
			offerData.put("orderIfSend", "0");
			offerData.put("cancelIfSend", "0");
			String operType="0";
			if("1".equals(selectElements.getData(i).getString("MODIFY_TAG"))){
				operType="1";
			}
			offerData.put("operType", operType);
			offerData.put("OpenCycleUnit", "");
			offerData.put("OpenCycleValue", "");
			String relComType="1";
			if("1".equals(selectElements.getData(i).getString("MODIFY_TAG"))){
				relComType="0";
			}
			offerData.put("relComType", relComType);
			offers.add(offerData);
		}
		//有宽带
	    IData commpara = new DataMap();
        commpara.put("SUBSYS_CODE", "CSM");
        commpara.put("PARAM_ATTR", "5453");
        commpara.put("PARAM_CODE", "TIPINFO");
        commpara.put("PARA_CODE1", newProductId);
        commpara.put("PARA_CODE4", "1");
        IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
        if(IDataUtil.isNotEmpty(commparaDs)
        		&&commparaDs.getData(0).getString("PARA_CODE20","").indexOf("本套餐含免费宽带")>0){
        	//查询宽带1+活动
        	IDataset saleActives=UserSaleActiveInfoQry.queryUserSaleActiveByTag(userInfo.getString("USER_ID"));
        	for(int j=0;j<saleActives.size();j++){
        		if("69908001".equals(saleActives.getData(j).getString("PRODUCT_ID"))){
        			offerData=new DataMap();
        			offerData.put("offerId", saleActives.getData(j).getString("PACKAGE_ID")+"|K");
        			offerData.put("offerName", saleActives.getData(j).getString("PACKAGE_NAME"));
        			offerData.put("fatherOfferId", "0000");
        			offerData.put("fatherOfferName", "主套餐");
        			offerData.put("spCode", "");
        			offerData.put("bizCode", "");
        			offerData.put("servType", "");
        			offerData.put("offerPrice", "0");
        			offerData.put("status", "");
        			String provinceofferTypeName="营销活动";
        			String provinceOfferType="K";
        			offerData.put("provinceOfferType", provinceOfferType);
        			offerData.put("provinceofferTypeName", provinceofferTypeName);
        			offerData.put("chooseTag", "0");
        			offerData.put("ifQuit", "0");
        			offerData.put("changeRemind", "");
        			offerData.put("startTime", saleActives.getData(j).getString("START_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
        			if(offerData.getString("startTime").length()==8){
        				offerData.put("startTime",offerData.getString("startTime")+"000000");
        			}
        			offerData.put("endTime", saleActives.getData(j).getString("END_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
        			if(offerData.getString("endTime").length()==8){
        				offerData.put("endTime",offerData.getString("endTime")+"000000");
        			}
        			String ifOrder="1";
        			offerData.put("ifOrder", ifOrder);
        			offerData.put("orderTime", saleActives.getData(j).getString("START_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
        			if(offerData.getString("orderTime").length()==8){
        				offerData.put("orderTime",offerData.getString("orderTime")+"000000");
        			}
        			offerData.put("effMode", "Type_Default");
        			offerData.put("validate", saleActives.getData(j).getString("START_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
        			if(offerData.getString("validate").length()==8){
        				offerData.put("validate",offerData.getString("validate")+"000000");
        			}
        			offerData.put("expireDate", saleActives.getData(j).getString("END_DATE","").replaceAll("-", "").replace(":", "").replace(" ", ""));
        			if(offerData.getString("expireDate").length()==8){
        				offerData.put("expireDate",offerData.getString("expireDate")+"000000");
        			}
        			offerData.put("orderIfSend", "0");
        			offerData.put("cancelIfSend", "0");
        			String operType="1";
        			offerData.put("operType", operType);
        			offerData.put("OpenCycleUnit", "");
        			offerData.put("OpenCycleValue", "");
        			String relComType="0";
        			offerData.put("relComType", relComType);
        			offers.add(offerData);
        			break;
        		}
        	}
        }
         
		outData.put("offerList", offers);
		resultList.add(outData);
    	return bean.prepareOutResult(0, "", resultList); 
    }
    /**
     * 商品属性查询
     * @param inparam
     * @throws Exception
     */
    public IData queryAttrInfo(IData inparam) throws Exception{
    	inparam=inparam.getData("params");
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	String offerId=inparam.getString("batchId");
    	String offerType="";
    	if(StringUtils.isEmpty(offerId)){
    		return bean.prepareOutResult(1, "batchId不能为空", new DataMap());
    	}
    	if(offerId.contains("|")){
    		if(offerId.split("\\|").length>1){
     		   offerType=offerId.split("\\|")[1];
     		}
    		offerId=offerId.split("\\|")[0];
    	}
    	IDataset offerDatas=new DatasetList();
    	IData offerData=new DataMap();
    	try{
	    	//获取元素属性
			IData inParam=new DataMap();
			inParam.put("ELEMENT_ID", offerId);
			inParam.put("ELEMENT_TYPE_CODE", StringUtils.isEmpty(offerType)?"D":offerType);
			IDataset attrs=CSAppCall.call("CS.SelectedElementSVC.getElementAttrs", inParam);
			IData attrData=new DataMap();;
			IDataset attrList=new DatasetList();
			
			for(int k=0;k<attrs.size();k++){
				if(k==0){
					attrData.put("attrCode", attrs.getData(k).getString("ATTR_CODE"));
					attrData.put("attrName", attrs.getData(k).getString("ATTR_LABLE"));
					String attrType="EDIT";
					if("0".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//文本编辑框
						attrType="EDIT";
					}else if("1".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))
							  ||"3".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//下拉框
						attrType="SELECT";
					}else if("7".equals(attrs.getData(k).getString("ATTR_TYPE_CODE"))){//时间输入框
						attrType="TIME";
					}
					
					attrData.put("attrType", attrType);
					attrData.put("defaultval", attrs.getData(k).getString("ATTR_INIT_VALUE"));
					attrData.put("minLen", attrs.getData(k).getString("ATTR_FIELD_MIN",""));
					attrData.put("maxLen", attrs.getData(k).getString("ATTR_FIELD_MAX",""));
					
					attrData.put("attrValueCode", attrs.getData(k).getString("ATTR_FIELD_CODE"));
					attrData.put("attrValueName", attrs.getData(k).getString("ATTR_FIELD_NAME"));
				}else{
					if(StringUtils.isNotEmpty(attrs.getData(k).getString("ATTR_FIELD_NAME"))){
						if(StringUtils.isNotEmpty(attrData.getString("attrValueName"))){
							attrData.put("attrValueName",attrData.getString("attrValueName")+","+attrs.getData(k).getString("ATTR_FIELD_NAME"));
						}else{
							attrData.put("attrValueName", attrs.getData(k).getString("ATTR_FIELD_NAME"));
						}
					}
					
					if(StringUtils.isNotEmpty(attrs.getData(k).getString("ATTR_FIELD_CODE"))){
						if(StringUtils.isNotEmpty(attrData.getString("attrValueName"))){
							attrData.put("attrValueCode",attrData.getString("attrValueCode")+","+attrs.getData(k).getString("ATTR_FIELD_CODE"));
						}else{
							attrData.put("attrValueCode", attrs.getData(k).getString("ATTR_FIELD_CODE"));
						}
					}
				}
			}
			
			attrList.add(attrData);
			
			offerData.put("offerSpeAttrSubList",attrList);
	    	offerDatas.add(offerData);
	    	return bean.prepareOutResult(0, "", offerDatas); 
    	}catch(Exception e){
    		return bean.prepareOutResult(1, e.getMessage(), offerDatas); 
    	}
    }
    /**
     * 预约业务取消
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData submitRollBackBusiness(IData inparam) throws Exception{
    	inparam=inparam.getData("params");
    	BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
    	String userMobile=inparam.getString("userMobile");
    	if(StringUtils.isEmpty(userMobile)){
    		return bean.prepareOutResult(1, "userMobile不能为空", new DataMap());
    	}
    	IDataset cancelList=inparam.getDataset("cancelList");
    	if(IDataUtil.isEmpty(cancelList)){
    		return bean.prepareOutResult(1, "cancelList不能为空", new DataMap());
    	}
    	IData cancelData=cancelList.getData(0);
    	String tradeId=cancelData.getString("tradeId");
    	if(StringUtils.isEmpty(tradeId)){
    		return bean.prepareOutResult(1, "tradeId不能为空", new DataMap());
    	}
    	IData inParam=new DataMap();
    	inParam.put("TRADE_ID", tradeId);
    	inParam.put("SERIAL_NUMBER", userMobile);
    	IDataset results=CSAppCall.call("SS.CancelChangeProductSVC.cancelChangeProductTrade", inParam);
    	
    	IData outData=new DataMap();
    	outData.put("tradeResult", "0");
    	outData.put("tradeDesc", "");
    	if(IDataUtil.isNotEmpty(results)){
    		outData.put("cancelId", results.getData(0).getString("TRADE_ID"));
    	}
    	IDataset outDataset=new DatasetList();
    	outDataset.add(outData);
    	return bean.prepareOutResult(0, "", outDataset); 
    }
    public static void main(String args[]){
    	System.out.println("10004445|P".split("\\|").length);
    }
	
	public IData queryUserSpecialRoster(IData inparam) throws Exception
    {
        BroadbandIntfBean bean = BeanManager.createBean(BroadbandIntfBean.class);
        IData result = bean.queryUserSpecialRoster(inparam);
        return result;
    }
}
