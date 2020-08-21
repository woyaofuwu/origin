package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class DestroyGroupUserPageDataTrans extends PageDataTrans
{
    @Override
    public IData transformData() throws Exception
    {
        super.transformData();
        
        IData data = new DataMap();
        
        String subscriberInsId = "";
        IData custInfo = getEcCustomer();
        data.put("USER_EPARCHY_CODE", custInfo.getString("EPARCHY_CODE"));
        
        IDataset offerInfos = getOfferList();
        if(DataUtils.isNotEmpty(offerInfos))
        {
            subscriberInsId = offerInfos.first().getString("USER_ID");
        }
        else
        {
            IData subscriberData = getEcSubscriber();
            if(DataUtils.isEmpty(subscriberData))
            {
                BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到用户信息！");
            }
            subscriberInsId = subscriberData.getString("USER_ID");
        }
                        
        if(StringUtils.isBlank(subscriberInsId))
        {
            BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到用户实例标识！");
        }
        data.put("USER_ID", subscriberInsId);
        String brandCode = getBrandCode();
    	String productId = getProductId();
        data.put("PRODUCT_ID", productId);

        
    	if("BOSG".equals(brandCode)){
    		IData goodInfo = new DataMap();// 商品信息
    		goodInfo = transGoodInfoData();
    		data.put("GOOD_INFO", goodInfo);

    		
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
        IData commonInfo = getCommonData();
        if(DataUtils.isNotEmpty(commonInfo))
        {
            IData removeData = commonInfo.getData("REMOVE_REASON");
            if(DataUtils.isNotEmpty(removeData))
            {
                data.put("REASON_CODE", removeData.getString("REASON_CODE"));
                data.put("REASON_DESC", removeData.getString("REASON_DESC"));
            }
            data.put("REMARK", commonInfo.getString("REMARK", ""));
            data.put("DATALINE_FEE_TYPE", commonInfo.getString("DATALINE_FEE_TYPE", ""));
            
            IData eosInfo = commonInfo.getData("ESOP_INFO");
            if(DataUtils.isNotEmpty(eosInfo))
            {
            	data.put("EOS_INFO", eosInfo);
            }
            
        }
        
        data.put("IF_BOOKING", "false");

        
        return data;
    }
    
    public void setServiceName() throws Exception
    {
    	String productId = getProductId();
    	String brandCode = getBrandCode();
    	if("BOSG".equals(brandCode)){
    		setSvcName("CS.DestroyBBossUserSVC.dealDelBBossBiz");
    	}else if ("6130".equals(productId))
        {// 融合总机
            setSvcName("SS.DestroyCentrexSuperTeleGroupUserSVC.crtOrder");
        }else if ("6100".equals(productId))
        {// 移动总机
            setSvcName("SS.DestroySuperTeleGroupUserSVC.crtOrder");
        }else{
            setSvcName(EcConstants.EC_OPER_SERVICE.DELETE_ENTERPRISE_SUBSCRIBER.getValue());
        }
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
	
	// 转换商品信息
	private IData transGoodInfoData() throws Exception {
		IData goodInfo = new DataMap();
		IData merchInfo = getOfferList().first().getData("MERCHINFO");
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
		goodInfo.put("CONTACTOR_INFOS", merchInfo.get("CONTACTOR_INFOS"));// 联系人信息
		return goodInfo;
	}

}
