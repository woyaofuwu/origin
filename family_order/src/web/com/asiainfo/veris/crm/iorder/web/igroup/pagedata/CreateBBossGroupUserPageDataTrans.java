package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import java.util.Date;

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
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;

public class CreateBBossGroupUserPageDataTrans extends PageDataTrans {
	
	@Override
	public IData transformData() throws Exception {
		IData result = super.transformData();
		
		IData custInfo = getEcCustomer();
		result.put("CUST_ID", custInfo.getString("CUST_ID"));
		result.put("SERIAL_NUMBER", getSerialNumber());
		result.put("EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
		result.put("USER_EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
        result.put("USER_INFO", getEcSubscriber());


		String productId = getProductId();
		result.put("PRODUCT_ID", productId);

		IDataset offerInfos = getOfferList();
		if (DataUtils.isEmpty(offerInfos)) {
			BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到商品数据结构！");
		}

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
		result.put("ELEMENT_INFO", transOfferInfoList);

		String groupId = getEcCustomer().getString("GROUP_ID");

		result.put("GROUP_ID", groupId);
		// 拼装bbossInfo数据开始
		IData bbossInfo = new DataMap();
		
		IData goodInfo = new DataMap();// 商品信息
		goodInfo = transGoodInfoData();
		//操作类型
		String merchOperCode = goodInfo.getString("MERCH_OPER_CODE");
		
		//拼装产品数据 （产品信息、产品参数、产品虚拟服务）
		transProductInfoTotal(bbossInfo,merchOperCode);
		
		IData productCtrlInfos = new DataMap();// 产品控制信息
		IData tempProductsElement = new DataMap();// 产品主体服务
		
		
		
		
		IData packageInfo = transPkgInfoTotal(offerInfos);

		IData isREopen = new DataMap();//

		bbossInfo.put("PRODUCT_CTRL_INFO", productCtrlInfos);
		bbossInfo.put("TEMP_PRODUCTS_ELEMENT", tempProductsElement);
		bbossInfo.put("GRP_PACKAGE_INFO", packageInfo);
		bbossInfo.put("IS_REOPEN", isREopen);
		bbossInfo.put("GOOD_INFO", goodInfo);

		result.put("BBOSS_INFO", bbossInfo);
		// 拼装bbossInfo数据结束
		IDataset compOfferChaList = getOfferChaList();
		if (DataUtils.isNotEmpty(compOfferChaList)) {
			result.put("PRODUCT_PARAM_INFO", transformOfferChaList(productId, compOfferChaList));
		}

		IData accountInfo = getEcAccount();

		IData acctInfo = transformAcctInfo(accountInfo);
		if (DataUtils.isNotEmpty(acctInfo)) {
        	acctInfo.put("EPARCHY_CODE", result.getString("EPARCHY_CODE"));
        	acctInfo.put("USER_EPARCHY_CODE", result.getString("EPARCHY_CODE"));
			result.put("ACCT_INFO", acctInfo);

			if (StringUtils.isNotBlank(acctInfo.getString("ACCT_ID"))) {
				result.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
			} else {
				result.put("ACCT_IS_ADD", "true");
			}
		}

		IData commonInfo = getCommonData();

		IDataset resInfoList = transformResInfo(commonInfo);
		if (DataUtils.isNotEmpty(resInfoList)) {
			result.put("RES_INFO", resInfoList);
		}

		IDataset planInfoList = transformPlanInfo(commonInfo);
		if (DataUtils.isNotEmpty(planInfoList)) {
			result.put("PLAN_INFO", planInfoList);
		}

		
		IData contractInfo = getEcSubscriber();

		if (DataUtils.isNotEmpty(contractInfo)) {
			result.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID", ""));
		}

		IData eosInfo = commonInfo.getData("ESOP_INFO");
		if (DataUtils.isNotEmpty(eosInfo)) {
			result.put("EOS", new DatasetList(eosInfo));
		}
		return result;

	}
	
	// 产品定制信息
	private IData transPkgInfoTotal(IDataset offerInfos) throws Exception {

		//包参数
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
				
				if(!"P".equals(offerType)){
					continue;
				}
				
				String offerCode = transproductInfoList.getString("OFFER_CODE");
				String index = transproductInfoList.getString("OFFER_INDEX");
				String operCode = transproductInfoList.getString("OPER_CODE");
				if (StringUtils.isEmpty(offerCode) || StringUtils.isEmpty(index)||"P".equals(offerCode)) {
					continue;
				}

				String key = offerCode + "_" + index;

				// 拼装PRODUCT_INFO_LIST
				IData productInfoListData = transProductInfoList(transproductInfoList,merchOperCode);
				productInfoList.add(productInfoListData);

				// 拼装PROCUT_PARAM产品参数信息
				String merchPOfferCode = CommonViewCall.getAttrValueFromAttrBiz("1","B","PRO",offerCode);
				IData productParamData = transProductParam(transproductInfoList, operCode, merchPOfferCode);
				// key 22000926_1
				productParam.put(key, productParamData);
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
			
			IData transProductParamData = new DataMap(); 
			transProductParamData.put("INST_ID", productParamData.getString("INST_ID",""));
			
		    String startDate = productParamData.getString("START_DATE","");
		    if(StringUtils.isBlank(startDate)){
		    	startDate = SysDateMgr.date2String(new Date(), SysDateMgr.PATTERN_STAND);
		    }
			transProductParamData.put("START_DATE", startDate);
			
			String endDate = productParamData.getString("END_DATE","");
			if(StringUtils.isBlank(endDate)){
				endDate = SysDateMgr.END_DATE_FOREVER;
			}
			transProductParamData.put("END_DATE", endDate);
			
			/*transProductParamData.put("START_DATE", productParamData.getString("START_DATE",""));
			transProductParamData.put("END_DATE", productParamData.getString("END_DATE",""));*/
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
	private IData transProductParam(IData transproductInfoLists, String operCode, String merchPOfferCode) throws Exception {
		IData productParam = new DataMap();
		IDataset offerChaSpecsList = transproductInfoLists.getDataset("OFFER_CHA_SPECS");

		if (DataUtils.isEmpty(offerChaSpecsList)) {
			return productParam;
		}
	

		for (int i = 0; i < offerChaSpecsList.size(); i++) {
			IData offerChaSpecsData = offerChaSpecsList.getData(i);
			String attrValue = offerChaSpecsData.getString("ATTR_VALUE", "");
			if(StringUtils.isEmpty(attrValue)){
				continue;
			}
			IData productParamData = new DataMap();
			
			//产品属性带有前缀或后缀，需特殊处理
			IDataset bbossAttrParam = CommonViewCall.qryBBossAttrByAttrCode(offerChaSpecsData.getString("ATTR_CODE"));
			String frontPart = "";
			if(IDataUtil.isNotEmpty(bbossAttrParam)){
				IData bbossAttr = bbossAttrParam.getData(0);
				frontPart =  bbossAttr.getString("FRONT_PART");
			}

			if(frontPart != null && !"".equals(frontPart)){
				productParamData.put("ATTR_VALUE", frontPart+offerChaSpecsData.getString("ATTR_VALUE"));
			}else{
				productParamData.put("ATTR_VALUE", offerChaSpecsData.getString("ATTR_VALUE", ""));			

			}			
			productParamData.put("ATTR_CODE", offerChaSpecsData.getString("ATTR_CODE", ""));
			productParamData.put("ATTR_NAME", offerChaSpecsData.getString("ATTR_NAME", ""));
			productParamData.put("ATTR_GROUP", offerChaSpecsData.getString("ATTR_GROUP", ""));
			productParamData.put("STATE", "ADD");

			productParam.put("pro" + i, productParamData);
		}
		return productParam;
	}

	// 拼装产品信息（PRODUCT_INFO_LIST）
	private IData transProductInfoList(IData transproductInfoList,String merchOperCode) throws Exception{
		IData productInfoListData = new DataMap();
		productInfoListData.put("USER_ID", transproductInfoList.getString("USER_ID", ""));
		productInfoListData.put("ISEXIST", transproductInfoList.getString("ISEXIST", ""));
		productInfoListData.put("PRODUCT_ID", transproductInfoList.getString("OFFER_CODE", ""));
		productInfoListData.put("PRODUCT_INDEX", transproductInfoList.getString("OFFER_INDEX", ""));
		//productInfoListData.put("PRODUCT_OPER_CODE", transproductInfoList.getString("OPER_CODE", ""));
		//String productOperCode = transproductInfoList.getString("OPER_CODE", "");
		if("0".equals(merchOperCode)){
			merchOperCode = "1";
		}
		//如果产品有预受理操作，则进行预受理
		IDataset userAttrList =  getAheadData(transproductInfoList.getString("OFFER_CODE", ""));
        if(IDataUtil.isNotEmpty(userAttrList)){
        	merchOperCode = "10";
        }
        
		productInfoListData.put("PRODUCT_OPER_CODE", merchOperCode);
		return productInfoListData;
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
		//合同处理
		IDataset oldAttInfos = merchInfo.getDataset("ATT_INFOS");
		IDataset newAttInfos = new DatasetList();
		for(int i=0;i<oldAttInfos.size();i++){
			IData oldAttInfoData = oldAttInfos.getData(i);
			String attName = oldAttInfoData.getString("ATT_NAME_FILENAME");
			String attTypeCode = oldAttInfoData.getString("ATT_TYPE_CODE");
			//ATT_TYPE_CODE  ATT_NAME
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
        if(DataUtils.isEmpty(accountInfo))
        {
            return acctInfo;
        }
        String acctId = accountInfo.getString("ACCT_ID");
        if(StringUtils.isNotBlank(acctId))
        {
            acctInfo.put("ACCT_ID", acctId);
        }
        else
        {
            acctInfo.putAll(accountInfo);
        }
        
        acctInfo.put("PAY_NAME", accountInfo.getString("ACCT_NAME","0"));
        acctInfo.put("PAY_MODE_CODE", accountInfo.getString("ACCT_TYPE","0"));
        
        //现金直接返回
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("0")){
        	return acctInfo;
        }
        
        //托收
        if(acctInfo.getString("PAY_MODE_CODE","0").equals("1"))
        	acctInfo.put("PAYMENT_ID", "4");
        
        acctInfo.put("START_CYCLE_ID", SysDateMgr.getSysDate().replace("-", "").substring(0, 6));
        acctInfo.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012());
        acctInfo.put("BANK_ACCT_NAME", acctInfo.getString("BANK_NAME"));
        acctInfo.put("CONSIGN_MODE", "1");
        acctInfo.put("ACT_TAG", "1");
        acctInfo.put("RSRV_STR1", "1");
        acctInfo.put("MODIFY_TAG", "0");
        	
        return acctInfo;
	}

	@Override
	public void setServiceName() throws Exception {
		setSvcName("CS.CreateBBossUserSVC.crtOrder");

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



}
