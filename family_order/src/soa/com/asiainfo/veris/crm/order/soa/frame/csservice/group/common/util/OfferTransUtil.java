package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class OfferTransUtil
{
    public static void OfferToSvcTrans(IData offerData) throws Exception
    {
        String productId = offerData.getString("REL_OFFER_ID", "-1");
        offerData.put("PRODUCT_ID", productId);
        offerData.put("PRODUCT_MODE", UProductInfoQry.getProductModeByProductId(productId));
        offerData.put("PACKAGE_ID", offerData.getString("GROUP_ID", "-1"));
        offerData.put("ELEMENT_ID", offerData.getString("OFFER_ID"));
        offerData.put("SERVICE_ID", offerData.getString("OFFER_ID"));
        offerData.put("ELEMENT_TYPE_CODE", offerData.getString("OFFER_TYPE"));
        offerData.put("START_DATE", offerData.getString("VALID_DATE"));
        offerData.put("END_DATE", offerData.getString("EXPIRE_DATE"));
        offerData.put("MODIFY_TAG", offerData.getString("ACTION"));
        offerData.put("INST_ID", offerData.getString("OFFER_INS_ID"));
    }
    
    public static void OfferToDiscntTrans(IData offerData) throws Exception
    {
        String productId = offerData.getString("REL_OFFER_ID", "-1");
        offerData.put("PRODUCT_ID", productId);
        offerData.put("PRODUCT_MODE", UProductInfoQry.getProductModeByProductId(productId));
        offerData.put("PACKAGE_ID", offerData.getString("GROUP_ID", "-1"));
        offerData.put("ELEMENT_ID", offerData.getString("OFFER_ID"));
        offerData.put("DISCNT_CODE", offerData.getString("OFFER_ID"));
        offerData.put("ELEMENT_TYPE_CODE", offerData.getString("OFFER_TYPE"));
        offerData.put("START_DATE", offerData.getString("VALID_DATE"));
        offerData.put("END_DATE", offerData.getString("EXPIRE_DATE"));
        offerData.put("MODIFY_TAG", offerData.getString("ACTION"));
        offerData.put("INST_ID", offerData.getString("OFFER_INS_ID"));
    }
    
    public static void OfferToProductTrans(IData offerData) throws Exception
    {
        
    }
    
    public static void OfferToPlatSvcTrans(IData offerData) throws Exception
    {
        
    }
    
    public static void OfferChaToAttrTrans(IData offerData) throws Exception
    {
        
    }
    
    public static void OfferProdStaToSvcStaTrans(IData offerData) throws Exception
    {
        
    }
}
