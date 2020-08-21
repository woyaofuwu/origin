package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class CreateBBossGroupMemberPageDataTrans extends PageDataTrans {
	@Override
	public IData transformData() throws Exception {
		IData data = super.transformData();

		IDataset memberOffers = getOfferList();
		if (DataUtils.isEmpty(memberOffers)) {
			BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员商品数据结构！");
		}

		IDataset offerInfoList = transformOfferList(memberOffers);
		data.put("ELEMENT_INFO", offerInfoList);

		IDataset compOfferChaList = getOfferChaList();
		if (DataUtils.isNotEmpty(compOfferChaList)) {
			data.put("PRODUCT_PARAM_INFO", transformOfferChaList(getProductId(), compOfferChaList));
		}
		
		data.put("ELEMENT_INFO", new DatasetList());
		data.put("PRODUCT_PARAM_INFO", new DatasetList());
		

		IData commonInfo = getCommonData();
		if (DataUtils.isNotEmpty(commonInfo)) {
			data.put("MEM_ROLE_B", commonInfo.getString("ROLE_CODE_B"));
			data.put("PLAN_TYPE_CODE", commonInfo.getString("PLAN_TYPE_CODE"));
			data.put("FEE_TYPE", commonInfo.getString("FEE_TYPE"));
			data.put("LIMIT_TYPE", commonInfo.getString("LIMIT_TYPE"));
			data.put("LIMIT", commonInfo.getString("LIMIT"));
			data.put("IF_BOOKING", "0".equals(commonInfo.getString("EFFECT_NOW")) ? true : false); // 0-下月；1-立即
			data.put("REMARK", commonInfo.getString("REMARK"));
			IDataset resInfoList = transformResInfo(commonInfo);
			if (DataUtils.isNotEmpty(resInfoList)) {
				data.put("RES_INFO", resInfoList);
			}
		}

		// 集团用户信息
		IData ecSubscriber = getEcSubscriber();
		if (DataUtils.isEmpty(ecSubscriber)) {
			BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到集团用户信息数据结构！");
		}
		
		//通过产品user_id查询商品的user_id
		String userIdB = ecSubscriber.getString("USER_ID");
		
		IData input = new DataMap();
		input.put("USER_ID_B", userIdB);
		//获取数据
		IData mebProductInfo = getOfferList().first();
				
		//通过成员产品编码查询集团产品编码
		String mebOfferId = mebProductInfo.getString("OFFER_ID");
		IDataset productOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,mebOfferId,"1");

		String grpMerchPID = productOfferList.first().getString("OFFER_ID");		
		IDataset merchOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,grpMerchPID,"4");

		input.put("RELATION_TYPE_CODE", IUpcViewCall.getRelationTypeCodeByOfferId(merchOfferList.first().getString("OFFER_ID")));
		input.put("ROUTE_EPARCHY_CODE", Route.CONN_CRM_CG);
        IDataInput inputInparams = new DataInput();
        inputInparams.getData().putAll(input);
        
        IDataOutput subOffersInfos =  ServiceFactory.call( "CS.RelaBBInfoQrySVC.qryRelaBBInfoByUserIdBRelaTypeCode", inputInparams);
 
        IDataset subOffersInfo = subOffersInfos.getData();
		data.put("USER_ID", subOffersInfo.first().getString("USER_ID_A"));
		

		// 成员用户信息
		IData memSubscriber = getMemSubscriber();
		if (DataUtils.isEmpty(memSubscriber)) {
			BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员用户信息数据结构！");
		}
		data.put("SERIAL_NUMBER", memSubscriber.getString("SERIAL_NUMBER"));

		// BBoss成员新增特殊数据结构开始
		// 拼装bbossInfo数据开始
		IData bbossInfo = new DataMap();

		// 拼装产品数据 （产品信息、产品参数、产品虚拟服务）
		transProductInfoTotal(bbossInfo,ecSubscriber.getString("USER_ID"));

		IData goodInfo = new DataMap();// 商品信息
		goodInfo = transGoodInfoData();

		IData productCtrlInfos = new DataMap();// 产品控制信息
		IData tempProductsElement = new DataMap();// 产品主体服务

		IData grpPackgeInfo = new DataMap();// 包信息
		IData isREopen = new DataMap();//

		bbossInfo.put("PRODUCT_CTRL_INFO", productCtrlInfos);
		bbossInfo.put("TEMP_PRODUCTS_ELEMENT", tempProductsElement);
		bbossInfo.put("GRP_PACKAGE_INFO", grpPackgeInfo);
		bbossInfo.put("IS_REOPEN", isREopen);
		bbossInfo.put("GOOD_INFO", goodInfo);

		data.put("BBOSS_INFO", bbossInfo);

		// BBoss成员新增特殊数据结构结束
		
		//路由信息
		String eparchyCode = memSubscriber.getString("EPARCHY_CODE");
		data.put("ROUTE_EPARCHY_CODE",eparchyCode);
		data.put("EPARACHY_CODE",eparchyCode);
		return data;
	}

	// 转换商品信息
	private IData transGoodInfoData() throws Exception {
		IData goodInfo = new DataMap();
		IData merchInfo = getOfferList().first();
		goodInfo.put("BASE_PRODUCT", getOfferList().first().getString("OFFER_CODE"));
		goodInfo.put("PAY_MODE", merchInfo.getString("PAY_MODE",""));
		goodInfo.put("BIZ_MODE", merchInfo.getString("BIZ_MODE",""));
		goodInfo.put("BUS_NEED_DEGREE", merchInfo.getString("BUS_NEED_DEGREE",""));
		goodInfo.put("LOCATION", "SEND");
		goodInfo.put("AUDITOR_INFOS", merchInfo.get("AUDITOR_INFOS"));
		goodInfo.put("ATT_INFOS", merchInfo.get("ATT_INFOS"));
		goodInfo.put("MERCH_OPER_CODE", merchInfo.getString("OPERTYPE"));
		goodInfo.put("CONTACTOR_INFOS", merchInfo.get("CONTACTOR_INFOS"));// 联系人信息
		return goodInfo;
	}

	//通过成员产品编码查询集团产品编码
	private String querymebCodeByProductCode(IData mebProductInfo) throws Exception{
		String mebOfferId = mebProductInfo.getString("OFFER_ID");
		IDataset productOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,mebOfferId,"1");
		String productOfferCode = "";
		if(IDataUtil.isNotEmpty(productOfferList)){
			String productOfferId = productOfferList.first().getString("OFFER_ID");
			productOfferCode = IUpcViewCall.getOfferCodeByOfferId(productOfferId);
		}
		return productOfferCode;
	}
	
	// 产品信息
	private void transProductInfoTotal(IData bbossInfo,String userId) throws Exception {
		// 产品信息
		IDataset productInfoList = new DatasetList();
		// 产品参数
		IData productParam = new DataMap();
		// 产品资费服务
		IData productsElement = new DataMap();
		
		//获取数据
		IData mebProductInfo = getOfferList().first();
		
		//通过成员产品编码查询集团产品编码
		String productOfferCode = querymebCodeByProductCode(mebProductInfo);
		
		//添加产品信息PRODUCT_INFO_LIST
		IData productInfoData = transProductInfoList(mebProductInfo,userId,productOfferCode);
		productInfoList.add(productInfoData);
		
		//添加产品参数信息PRODUCT_PARAM
		IData productParamTemp = transProductParam(mebProductInfo,userId);
		productParam.put(userId, productParamTemp);
		
		//添加资费参数PRODUCT_PARAM
		IDataset productsElementList = transSvcDis(mebProductInfo, mebProductInfo.getString("OFFER_CODE"));
		productsElement.put(userId, productsElementList);
		
		bbossInfo.put("PRODUCT_INFO_LIST", productInfoList);
		bbossInfo.put("PRODUCTS_ELEMENT", productsElement);
		bbossInfo.put("PRODUCT_PARAM", productParam);
	}

	// 拼装产品信息（PRODUCT_INFO_LIST）
	private IData transProductInfoList(IData transproductInfoList,String userId,String productOfferCode) {
		IData productInfoListData = new DataMap(); 
		productInfoListData.put("USER_ID", userId);
		productInfoListData.put("PRODUCT_ID",  productOfferCode);
		productInfoListData.put("MEB_TYPE", "1");
		productInfoListData.put("MEB_OPER_CODE", "1");
		return productInfoListData;
	}

	// 产品参数
	private IData transProductParam(IData transproductInfoLists, String operCode) throws Exception {
		IData productParam = new DataMap();
		IDataset offerChaSpecsList = transproductInfoLists.getDataset("OFFER_CHA_SPECS");
		
		//企业互联网电视添加
		String mebOfferId = getOfferList().first().getString("OFFER_ID","");
		if(mebOfferId.equals("110022002508")){
			String userIdB = getEcSubscriber().getString("USER_ID","");
			IData input = new DataMap();
			input.put("USER_ID", userIdB);
			input.put("INST_TYPE", "P");
			IDataInput inputInparams = new DataInput();
		    inputInparams.getData().putAll(input);
			IDataOutput userAttrInfos =  ServiceFactory.call( "CS.UserAttrInfoQrySVC.getUserProductAttrByUTForGrp", inputInparams);
			 
	        IDataset userAttrs = userAttrInfos.getData();
	        for(int i = 0 ;i<userAttrs.size();i++){
	        	IData userAttr = new DataMap();
	        	userAttr.put("ATTR_CODE", userAttrs.getData(i).getString("ATTR_CODE", ""));
	        	userAttr.put("ATTR_VALUE", userAttrs.getData(i).getString("ATTR_VALUE", ""));
	        	userAttr.put("ATTR_NAME", userAttrs.getData(i).getString("RSRV_STR3", ""));
	        	userAttr.put("ATTR_GROUP", userAttrs.getData(i).getString("ATTR_GROUP", ""));
	        	offerChaSpecsList.add(userAttr);
	        }
		}
		//企业互联网电视添加

		if (DataUtils.isEmpty(offerChaSpecsList)) {
			return productParam;
		}

		for (int i = 0; i < offerChaSpecsList.size(); i++) {
			IData offerChaSpecsData = offerChaSpecsList.getData(i);
			IData productParamData = new DataMap();
			String attrCode = offerChaSpecsData.getString("ATTR_CODE", "").replace("M", "");
			if("710000000733".equals(attrCode)||"710000000734".equals(attrCode)||"710000000735".equals(attrCode)){
				continue;
			} 
			productParamData.put("ATTR_VALUE", offerChaSpecsData.getString("ATTR_VALUE", ""));
			productParamData.put("ATTR_CODE", offerChaSpecsData.getString("ATTR_CODE", "").replace("M", ""));
			productParamData.put("ATTR_NAME", offerChaSpecsData.getString("ATTR_NAME", ""));
			productParamData.put("ATTR_GROUP", offerChaSpecsData.getString("ATTR_GROUP", ""));
			productParamData.put("STATE", "ADD");
			productParam.put("pro" + i, productParamData);
		}
		
		
		return productParam;
	}

	// 拼装资费服务信息
	private IDataset transSvcDis(IData transproductInfoList, String productId) {
		IDataset transProductParamDataset = new DatasetList();
		IDataset productParamDataset = transproductInfoList.getDataset("SUBOFFERS");
		if (DataUtils.isEmpty(productParamDataset)) {
			return transProductParamDataset;
		}
		for (int i = 0; i < productParamDataset.size(); i++) {
			IData productParamData = productParamDataset.getData(i);
			IData transProductParamData = new DataMap();
			transProductParamData.put("INST_ID", productParamData.getString("INST_ID", ""));
			transProductParamData.put("START_DATE", productParamData.getString("START_DATE", ""));
			transProductParamData.put("END_DATE", productParamData.getString("END_DATE", ""));
			transProductParamData.put("ELEMENT_TYPE_CODE", productParamData.getString("OFFER_TYPE", ""));
			transProductParamData.put("MODIFY_TAG", productParamData.getString("OPER_CODE", ""));
			transProductParamData.put("PRODUCT_ID", productId);
			transProductParamData.put("MAIN_TAG", productParamData.getString("MAIN_TAG", ""));
			IDataset attrParamDataset = transDisParam(productParamData);
			if (DataUtils.isNotEmpty(attrParamDataset)) {
				transProductParamData.put("ATTR_PARAM", transDisParam(productParamData));
			} else {
				transProductParamData.put("ATTR_PARAM", "");
			}
			transProductParamData.put("PACKAGE_ID", productParamData.getString("GROUP_ID", ""));
			transProductParamData.put("ELEMENT_ID", productParamData.getString("OFFER_CODE", ""));
			transProductParamDataset.add(transProductParamData);
		}

		return transProductParamDataset;
	}

	// 资费参数
	private IDataset transDisParam(IData transproductInfoLists) {
		IDataset attrParamDataset = new DatasetList();

		IDataset offerChaSpecsList = transproductInfoLists.getDataset("OFFER_CHA_SPECS");

		if (DataUtils.isEmpty(offerChaSpecsList)) {
			return attrParamDataset;
		}

		for (int i = 0; i < offerChaSpecsList.size(); i++) {
			IData offerChaSpecsData = offerChaSpecsList.getData(i);
			IData productParamData = new DataMap();
			
			String attrValue = offerChaSpecsData.getString("CHA_VALUE", "");
			if(StringUtils.isBlank(attrValue)){
				attrValue = offerChaSpecsData.getString("ATTR_VALUE", "");
			}
			
			String attrCode = offerChaSpecsData.getString("CHA_SPEC_CODE", "");
			if(StringUtils.isBlank(attrCode)){
				attrCode = offerChaSpecsData.getString("ATTR_CODE", "");
			}
			
			productParamData.put("ATTR_VALUE", attrValue);
			productParamData.put("ATTR_CODE", attrCode);
			attrParamDataset.add(productParamData);
		}
		return attrParamDataset;
	}

	public void setServiceName() throws Exception {
		setSvcName("CS.CreateBBossMemSVC.crtOrder");
	}

}
