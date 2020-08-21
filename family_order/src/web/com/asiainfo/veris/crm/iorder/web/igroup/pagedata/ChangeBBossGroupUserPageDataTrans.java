package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import java.util.Date;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException; 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;

public class ChangeBBossGroupUserPageDataTrans extends PageDataTrans {
	
	@Override
	public IData transformData() throws Exception {
		IData data = super.transformData();
        
        data.put("PRODUCT_ID", getProductId());
        
        IData custInfo = getEcCustomer();
        data.put("USER_EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
        
        IDataset offerInfos = getOfferList();
        if(DataUtils.isEmpty(offerInfos))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到商品数据结构！");
        }
        
		//定制信息
		IData packageInfo = transPkgInfoTotal(offerInfos);
		
		IData tempProductsElement = new DataMap();// 产品主体服务

		 //tempProductsElement = transProductElementInfo(offerInfos);
		
		// 拼装bbossInfo数据开始
		IData bbossInfo = new DataMap();
		
		IData goodInfo = new DataMap();// 商品信息
		goodInfo = transGoodInfoData();
		
		//操作类型
		String merchOperCode = goodInfo.getString("MERCH_OPER_CODE");
		
		//暂停和恢复时，暂停/恢复原因、附件 这些属性需要入attr表
		if("3".equals(merchOperCode) || "4".equals(merchOperCode) || "5".equals(merchOperCode)){
			IDataset suboffers  = offerInfos.first().getDataset("SUBOFFERS");
			IDataset tempBBossParams = new DatasetList();
			IData tempBBossParam = new DataMap();
			for(int i=0; i<suboffers.size(); i++){
				IData subofferData = suboffers.getData(i);
				String offerKey = subofferData.getString("OFFER_CODE");

				IDataset offerChaSpecs = subofferData.getDataset("OFFER_CHA_SPECS");
				if(IDataUtil.isEmpty(offerChaSpecs)){
					continue;
				}
				for(int j=0; j<offerChaSpecs.size(); j++){
					IData offerCha = offerChaSpecs.getData(j);
					IData tranOfferCha = resultParamData(offerCha,"ADD");
					tempBBossParams.add(tranOfferCha);
				}
				
				tempBBossParam.put(offerKey, tempBBossParams);
			}
			data.put("BBossParamInfo", tempBBossParam);

		}
		
		//拼装产品数据 （产品信息、产品参数、产品虚拟服务）
		transProductInfoTotal(bbossInfo,merchOperCode);
		
        IDataset offerInfoList = transformOfferList(offerInfos);
        IDataset transOfferInfoList = new DatasetList();
        for(int i=0;i<offerInfoList.size();i++){
			IData offerInfoListData = offerInfoList.getData(i);
			String typeCode = offerInfoListData.getString("ELEMENT_TYPE_CODE");
			if("P".equals(typeCode)){
				continue;
			}
			offerInfoListData.put("START_DATE", SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND));
			offerInfoListData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
			transOfferInfoList.add(offerInfoListData);
		} 
        data.put("ELEMENT_INFO", transOfferInfoList);
        
        IDataset compOfferChaList = getOfferChaList();
        if(DataUtils.isNotEmpty(compOfferChaList))
        {
            data.put("PRODUCT_PARAM_INFO", transformOfferChaList(getProductId(), compOfferChaList));
        }
        


		IData productCtrlInfos = new DataMap();// 产品控制信息
		
		IData isREopen = new DataMap();//

		bbossInfo.put("PRODUCT_CTRL_INFO", productCtrlInfos);
		bbossInfo.put("TEMP_PRODUCTS_ELEMENT", tempProductsElement);
		bbossInfo.put("GRP_PACKAGE_INFO", packageInfo);
		bbossInfo.put("IS_REOPEN", isREopen);
		bbossInfo.put("GOOD_INFO", goodInfo);

		data.put("BBOSS_INFO", bbossInfo);
		// 拼装bbossInfo数据结束
		
		//取消商品订购关系 BBossParamInfo GOOD_INFO 
		if("2".equals(merchOperCode)){
			IData bbossInfoData = data.getData("BBOSS_INFO");
			if(IDataUtil.isNotEmpty(bbossInfoData)){
			data.put("GOOD_INFO", bbossInfoData.getData("GOOD_INFO").toString());
			data.put("BBossParamInfo", bbossInfoData.getData("BBossParamInfo"));
			}
		}

		
		String subscriberInsId = offerInfos.first().getString("USER_ID");
        if(StringUtils.isBlank(subscriberInsId))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到用户实例标识！");
        }
        data.put("USER_ID", subscriberInsId);
        
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo)){
            IData eosInfo = commonInfo.getData("ESOP_INFO");
            if(DataUtils.isNotEmpty(eosInfo))
            {
            	data.put("EOS", new DatasetList(eosInfo));
            }
        }
        return data;
	}
	
	// 产品信息
	private void transProductInfoTotal(IData bbossInfo,String merchOperCode) throws Exception {
		//产品信息
		IDataset productInfoList = new DatasetList();
		//产品参数
		IData productParam = new DataMap();
		//产品资费服务
		IData productsElement = new DataMap();

		IDataset transproductInfoLists = getOfferList().first().getDataset("SUBOFFERS");
		
		if (IDataUtil.isNotEmpty(transproductInfoLists)) {
			for (int i = 0; i < transproductInfoLists.size(); i++) {

				IData transproductInfoList = transproductInfoLists.getData(i); 
				String offerType = transproductInfoList.getString("OFFER_TYPE");
				String offerCode = transproductInfoList.getString("OFFER_CODE");
				String index = transproductInfoList.getString("OFFER_INDEX");
				String operCode = transproductInfoList.getString("OPER_CODE");
				if (StringUtils.isEmpty(offerCode) || StringUtils.isEmpty(index)||"P".equals(offerCode)) {
					continue;
				}

				String key = offerCode + "_" + index;

				//变更产品组成关系
				if("7".equals(merchOperCode)){
					if("2".equals(operCode)){
						continue;
					}
				}
				// 拼装PRODUCT_INFO_LIST

				//暂停商品（3）、恢复商品（4），如果产品的操作是1  剔除
				if("3".equals(merchOperCode) || ("4".equals(merchOperCode))){
					if("1".equals(operCode)){
						continue;
					}
				}
				
				//剔除掉不是产品的信息
				if(!"P".equals(offerType)){
					continue;
				}
				
				IData productInfoListData = transProductInfoList(transproductInfoList,merchOperCode);
				productInfoList.add(productInfoListData);
				
				
				

				// 拼装PROCUT_PARAM产品参数信息
				IData productParamData = transProductParam(transproductInfoList, operCode);
				// key 22000926_1
				productParam.put(key, productParamData);
				
				//取消商品订购关系 商品属性 注销只取第一个数据
				if( (0==i) && ("2".equals(merchOperCode)) ){
					IData BBossParamInfoData = new DataMap();
					IDataset BBossParamInfoList =  desOrderTransProductParam(transproductInfoList, operCode);
					BBossParamInfoData.put(offerCode, BBossParamInfoList); 
					bbossInfo.put("BBossParamInfo", BBossParamInfoData);
				}
				
				/*if (DataUtils.isNotEmpty(transproductInfoList.getDataset("OFFER_CHA_SPECS"))) {
					productParam.put(key, productParamData);
				}*/

				// 拼装产品的资费服务信息PRODUCTS_ELEMENT
				IDataset productsElementDataset = transSvcDis(transproductInfoList,offerCode);
				productsElement.put(key, productsElementDataset);
				/*if (DataUtils.isNotEmpty(transproductInfoList.getDataset("SUBOFFERS"))) {
					productsElement.put(key, productsElementDataset);
				}*/

			}
		}
		bbossInfo.put("PRODUCT_INFO_LIST", productInfoList);
		bbossInfo.put("PRODUCTS_ELEMENT", productsElement);
		bbossInfo.put("PRODUCT_PARAM", productParam);
		
		
	}

	// 拼装资费服务信息
	private IDataset transSvcDis(IData transproductInfoList,String productId) {
		IDataset transProductParamDataset = new DatasetList();
		IDataset productParamDataset = transproductInfoList.getDataset("SUBOFFERS");
		if(DataUtils.isEmpty(productParamDataset)){
			return transProductParamDataset;
		}
		for(int i=0;i<productParamDataset.size();i++){
			
			IData productParamData = productParamDataset.getData(i);
			//剔除不修改的资费
			String operCode = productParamData.getString("OPER_CODE","");
			if("3".equals(operCode)){
				continue;
			}
			 
			IData transProductParamData = new DataMap();
			transProductParamData.put("INST_ID", productParamData.getString("INST_ID",""));
			
		    String startDate = productParamData.getString("START_DATE","");
		    if(StringUtils.isBlank(startDate)){
		    	startDate = SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND);
		    }
			transProductParamData.put("START_DATE", startDate);
			
			String endDate = productParamData.getString("END_DATE","");
			if(StringUtils.isBlank(endDate)&&"0".equals(operCode)){
				endDate = SysDateMgr.END_DATE_FOREVER;
			}
			//如果是删除资费
			if(StringUtils.isBlank(endDate)&&"1".equals(operCode)){
				transProductParamData.put("END_DATE", SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND));
			}
			transProductParamData.put("END_DATE", endDate);
			transProductParamData.put("ELEMENT_TYPE_CODE", productParamData.getString("OFFER_TYPE",""));
			transProductParamData.put("MODIFY_TAG", productParamData.getString("OPER_CODE",""));
			transProductParamData.put("PRODUCT_ID", productId);
			transProductParamData.put("MAIN_TAG", productParamData.getString("MAIN_TAG",""));
			IDataset attrParamDataset = transDisParam(productParamData);
			if(DataUtils.isNotEmpty(attrParamDataset)){
			transProductParamData.put("ATTR_PARAM", transDisParam(productParamData));
			}else{
			transProductParamData.put("ATTR_PARAM", "");
			}
			transProductParamData.put("PACKAGE_ID", productParamData.getString("GROUP_ID",""));
			transProductParamData.put("ELEMENT_ID", productParamData.getString("OFFER_CODE",""));
			
			
			
			transProductParamDataset.add(transProductParamData);
		}
		
		return transProductParamDataset;
	}
	
	//资费参数
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
    
	// 产品参数
	private IData transProductParam(IData transproductInfoLists, String operCode) throws Exception {
		int count = 0;
		IData productParam = new DataMap();
		IDataset offerChaSpecsList = transproductInfoLists.getDataset("OFFER_CHA_SPECS");

		if (DataUtils.isEmpty(offerChaSpecsList)) {
			return productParam;
		}

		for (int i = 0; i < offerChaSpecsList.size(); i++) { 
			IData offerChaSpecsData = offerChaSpecsList.getData(i);
			String operCodesString = offerChaSpecsData.getString("OPER_CODE");			
			//剔除没有变更的属性
			
			if( (!"0".equals(operCode)) && (!"2".equals(operCodesString))){
				
				continue;
			}
			
			/*if(StringUtils.isBlank(operCodesString)){
				productParam.put("pro" + count, resultParamData( offerChaSpecsData,"ADD"));
				count++;
				continue;
			}*/
			
			String oldAttrValue = offerChaSpecsData.getString("OLD_ATTR_VALUE");
			String attrValue = offerChaSpecsData.getString("ATTR_VALUE"); 
			
			if(StringUtils.isNotBlank(oldAttrValue)){ 
				productParam.put("pro" + count, resultParamData( offerChaSpecsData,"DEL"));
				count++;
			}
			
			if(StringUtils.isNotBlank(attrValue)){
				//产品属性带有前缀或后缀，需特殊处理
				IDataset bbossAttrParam = CommonViewCall.qryBBossAttrByAttrCode(offerChaSpecsData.getString("ATTR_CODE"));
				String frontPart = "";
				if(IDataUtil.isNotEmpty(bbossAttrParam)){
					IData bbossAttr = bbossAttrParam.getData(0);
					frontPart =  bbossAttr.getString("FRONT_PART");
				}
				if(frontPart != null && !"".equals(frontPart)){
					offerChaSpecsData.put("ATTR_VALUE", frontPart+offerChaSpecsData.getString("ATTR_VALUE"));
				}
				productParam.put("pro" + count, resultParamData( offerChaSpecsData,"ADD"));
				count++;
			}
		}
		return productParam;
	}
	
	// 商品 取消订购 参数
		private IDataset desOrderTransProductParam(IData transproductInfoLists, String operCode) {
			IDataset desOrderList = new DatasetList();
			IDataset offerChaSpecsList = transproductInfoLists.getDataset("OFFER_CHA_SPECS");

			if (DataUtils.isEmpty(offerChaSpecsList)) {
				return desOrderList;
			}

			for (int i = 0; i < offerChaSpecsList.size(); i++) { 
				IData offerChaSpecsData = offerChaSpecsList.getData(i);
				
				String attrValue = offerChaSpecsData.getString("ATTR_VALUE"); 
				
				if(StringUtils.isNotBlank(attrValue)){
					IData paramData = resultParamData( offerChaSpecsData,"ADD");
					desOrderList.add(paramData);
				}
			}
			return desOrderList;
		}
	
	private IData resultParamData(IData offerChaSpecsData,String status){
		IData productParamData = new DataMap();
		productParamData.put("ATTR_VALUE", offerChaSpecsData.getString("ATTR_VALUE", ""));
		productParamData.put("ATTR_CODE", offerChaSpecsData.getString("ATTR_CODE", ""));
		productParamData.put("ATTR_NAME", offerChaSpecsData.getString("ATTR_NAME", ""));
		productParamData.put("ATTR_GROUP", offerChaSpecsData.getString("ATTR_GROUP", ""));
		productParamData.put("STATE", status);
		return productParamData;
	}

	// 拼装产品信息（PRODUCT_INFO_LIST）
	private IData transProductInfoList(IData transproductInfoList,String merchOperCode) {
		IData productInfoListData = new DataMap();
		productInfoListData.put("USER_ID", transproductInfoList.getString("USER_ID", ""));
		productInfoListData.put("ISEXIST", transproductInfoList.getString("ISEXIST", ""));
		productInfoListData.put("PRODUCT_ID", transproductInfoList.getString("OFFER_CODE", ""));
		productInfoListData.put("PRODUCT_INDEX", transproductInfoList.getString("OFFER_INDEX", ""));
		productInfoListData.put("SUBIBID_RNUM", transproductInfoList.getString("SUBIBID_RNUM", ""));
		
		String operCode = transproductInfoList.getString("OPER_CODE", "");
		
		if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperCode)){
			
			if("1".equals(operCode)){
				operCode = "2";
			}else if("0".equals(operCode)){
				operCode = "1";
			}else if("3".equals(operCode)){
				operCode = "EXIST";	
			}
			productInfoListData.put("PRODUCT_OPER_CODE",operCode);
		}else if(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperCode)){
			if("3".equals(operCode)){
				operCode = "EXIST";
			}else if("5".equals(operCode)){
				operCode = "3";
			}
			productInfoListData.put("PRODUCT_OPER_CODE",operCode);
		}else if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(merchOperCode)){

			productInfoListData.put("PRODUCT_OPER_CODE",merchOperCode);
		}
		else{
			if("3".equals(operCode)){
				merchOperCode = "EXIST";
			}
			productInfoListData.put("PRODUCT_OPER_CODE",merchOperCode);

		}
		return productInfoListData;
	}

	private String transString(String operType) {
		String state = "";
		if ("1".equals(operType)) {

		} else if ("2".equals(operType)) {

		} else if ("".equals(operType)) {

		} else {

		}
		return operType;
	}

	// 转换商品信息
	private IData transGoodInfoData() throws Exception {
		IData goodInfo = new DataMap();
		IData merchInfo = getOfferList().first().getData("MERCHINFO");
		goodInfo.put("BASE_PRODUCT", getOfferList().first().getString("OFFER_CODE"));
		goodInfo.put("PAY_MODE", merchInfo.getString("PAY_MODE"));
		goodInfo.put("BIZ_MODE", merchInfo.getString("BIZ_MODE"));
		goodInfo.put("BUS_NEED_DEGREE", merchInfo.getString("BUS_NEED_DEGREE"));
		goodInfo.put("AUDITOR_INFOS", merchInfo.get("AUDITOR_INFOS"));

		// 合同处理
		IDataset oldAttInfos = merchInfo.getDataset("ATT_INFOS");
		IDataset newAttInfos = new DatasetList();
		for (int i = 0; i < oldAttInfos.size(); i++) {
			IData oldAttInfoData = oldAttInfos.getData(i);
			String attName = oldAttInfoData.getString("ATT_NAME_FILENAME");
			String attTypeCode = oldAttInfoData.getString("ATT_TYPE_CODE");
			// ATT_TYPE_CODE ATT_NAME
			IData newAttInfoData = new DataMap();
			newAttInfoData.put("ATT_TYPE_CODE", attTypeCode);
			newAttInfoData.put("ATT_NAME", attName);
			newAttInfos.add(newAttInfoData);
		}

		goodInfo.put("ATT_INFOS", newAttInfos);
		goodInfo.put("MERCH_OPER_CODE", merchInfo.getString("OPERTYPE"));
		goodInfo.put("CONTACTOR_INFOS", merchInfo.get("CONTACTOR_INFOS"));// 联系人信息
		return goodInfo;
	}

	

	private IData transformAcctInfo(IData accountInfo) throws Exception {
		IData acctInfo = new DataMap();
		if (DataUtils.isEmpty(accountInfo)) {
			return acctInfo;
		}
		String acctId = accountInfo.getString("ACCT_ID");
		if (StringUtils.isNotBlank(acctId)) {
			acctInfo.put("ACCT_ID", acctId);
		} else {
			acctInfo.putAll(accountInfo);
		}
		acctInfo.put("PAY_NAME", accountInfo.getString("ACCT_NAME", "0"));
		acctInfo.put("PAY_MODE_CODE", accountInfo.getString("ACCT_TYPE", "0"));
		return acctInfo;
	}

	@Override
	public void setServiceName() throws Exception {
		setSvcName("CS.ChangeBBossUserSVC.crtOrder");

	}


	private IDataset transformPlanInfo(IData commonInfo) throws Exception {
		IDataset planInfoList = new DatasetList();

		String payplanInfo = commonInfo.getString("PAY_PLAN_INFO");
		if (StringUtils.isNotBlank(payplanInfo)) {
			String[] payplanArr = payplanInfo.split(",");
			for (int i = 0, size = payplanArr.length; i < size; i++) {
				IData payplan = new DataMap();
				payplan.put("PLAN_TYPE_CODE", payplanArr[i]);
				planInfoList.add(payplan);
			}
		}

		return planInfoList;
	}
	
	
	private IData transPkgInfoTotal(IDataset offerInfos) throws Exception {

		IData pkgInfo = new DataMap();
		
		IDataset transproductInfoLists = offerInfos.first().getDataset("SUBOFFERS");
		IData  pkgInfos = offerInfos.first().getData("GRP_PACKAGE_INFO");
		if(IDataUtil.isEmpty(pkgInfos)){
			
			return pkgInfo;
		}

		if(IDataUtil.isNotEmpty(transproductInfoLists)){
			for(int i=0; i<transproductInfoLists.size(); i++){
								
				String index = transproductInfoLists.getData(i).getString("OFFER_INDEX");
				String offerCode = transproductInfoLists.getData(i).getString("OFFER_CODE");
				IDataset tempPkgInfoList = pkgInfos.getDataset(offerCode);
				if(IDataUtil.isEmpty(tempPkgInfoList)){
						continue;					
				}
				
				String key = offerCode + "_" + index;				
				pkgInfo.put(key, tempPkgInfoList);
			
			}
		}
		
		return pkgInfo;
	}
	
	
	private IData transProductElementInfo(IDataset offerInfos) throws Exception {

		IDataset tempProELementsInfos = new DatasetList();
		IData tempProELementsInfo = new DataMap();
		IData tempProELementsInfoCopy = new DataMap();

		
		IDataset  productInfo = offerInfos.getData(0).getDataset("SUBOFFERS");
		IDataset proElementInfos = productInfo.getData(0).getDataset("SUBOFFERS");
		
		IData proElementInfo = proElementInfos.getData(0);
		

		if (IDataUtil.isNotEmpty(proElementInfo)) {
			

				String index = proElementInfo.getString("OFFER_INDEX");
				String offerCode = productInfo.getData(0).getString("OFFER_CODE");
				String key = offerCode + "_" + index;
				
				tempProELementsInfoCopy.put("INST_ID", proElementInfo.getString("OFFER_INS_ID"));
				tempProELementsInfoCopy.put("ELEMENT_NAME", proElementInfo.getString("OFFER_NAME"));
				tempProELementsInfoCopy.put("USER_ID_A", "-1");
				tempProELementsInfoCopy.put("START_DATE", productInfo.getData(0).getString("START_DATE"));
				tempProELementsInfoCopy.put("END_DATE", productInfo.getData(0).getString("CANCEL_END_DATE"));
				tempProELementsInfoCopy.put("ELEMENT_ID", proElementInfo.getString("OFFER_CODE"));
				tempProELementsInfoCopy.put("ELEMENT_TYPE_CODE", proElementInfo.getString("OFFER_TYPE"));
				tempProELementsInfoCopy.put("ITEM_INDEX", proElementInfo.getString("OFFER_INDEX"));
				tempProELementsInfoCopy.put("PACKAGE_ID", proElementInfo.getString("GROUP_ID"));
				tempProELementsInfoCopy.put("PRODUCT_ID", productInfo.getData(0).getString("OFFER_CODE"));
		
				tempProELementsInfos.add(tempProELementsInfoCopy);
				tempProELementsInfo.put(key, tempProELementsInfos);
			
		}
		
		return tempProELementsInfo;
	}
}
