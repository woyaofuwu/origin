package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upcinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upcinfo.UpcInfoIntf;

public class UpcInfoIntfViewUtil
{
    public static IDataset qryUpcInfosByServiceNameAndParamData(IBizCommon bc, String serviceName, IData paramData) throws Exception
    {
        return UpcInfoIntf.qryUpcInfosByServiceNameAndParamData(bc, serviceName, paramData);
    }
    
    public static IData qryOfferInfoByOfferCodeOfferType(IBizCommon bc, String offerCode, String offerType) throws Exception
    {
        IData paramData = new DataMap();
        paramData.put("OFFER_CODE", offerCode);
        paramData.put("OFFER_TYPE", offerType);
        
        IDataset results = UpcInfoIntf.qryUpcInfosByServiceNameAndParamData(bc, "UPC.Out.OfferQueryFSV.queryOfferByOfferId", paramData);
        if (IDataUtil.isEmpty(results))
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_78, offerCode);
        }
        
        return results.getData(0);
    }
    /**
     * 特征相关接口
     * 根据特牌查询品牌名称
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static String queryBrandNameByChaVal(IBizCommon bc, String brandCode) throws Exception
    { 
    	IData paramData = new DataMap();
    	paramData.put("BRAND_CODE", brandCode);
    	IDataset ds = UpcInfoIntf.qryUpcInfosByServiceNameAndParamData(bc, "queryBrandNameByChaVal", paramData);
    	
        return  ds.getData(0).getString("BRAND_NAME");
        
    }
}
