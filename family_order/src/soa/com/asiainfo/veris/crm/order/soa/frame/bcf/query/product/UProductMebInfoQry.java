
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UProductMebInfoQry
{
    /**
     * 查询集团成员基本产品
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getMemberMainProductByProductId(String productId) throws Exception
    {
        IDataset result = UpcCall.queryMebOffersByGrpOfferId(productId);
        if(IDataUtil.isEmpty(result)){
            return "";
        }
        else if(result.size() == 1 )
        {
            return result.getData(0).getString("OFFER_CODE");
        }else
        {
            for(int i = 0,isize = result.size(); i < isize; i++)
            {
                IData offer = result.getData(i);
                String selectTag = offer.getString("SELECT_FLAG","");
                if(selectTag.equals(UpcConst.SELECT_FLAG_MUST_CHOOSE))
                {
                    return offer.getString("OFFER_CODE");
                }
            }
            
            return result.getData(0).getString("OFFER_CODE");
        }
    }
    
    /**
     * 查询集团成员基本产品 只返回PRODUCT_ID_B
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryProduct(String productId) throws Exception
    {
        IDataset result = UpcCall.queryMebOffersByGrpOfferId(productId);
        IDataset mebSet = new DatasetList();
        if(IDataUtil.isEmpty(result))
            return mebSet;
        for(int i = 0, isize = result.size(); i < isize; i++)
        {
            IData relOffer = result.getData(i);
            IData productMeb = new DataMap();
            productMeb.put("PRODUCT_ID_B", relOffer.getString("OFFER_CODE"));
            mebSet.add(productMeb);
        }
        return mebSet;
    }
    
    /**
     * 关联了PM_OFFER表查询成员的产品名称,返回成员产品的
     * @param productId
     * @param tradeStaffId
     * @return
     * @throws Exception
     */
    public static IDataset getMemberProductByGrpProductId(String productId) throws Exception
    {
        IDataset result = UpcCall.queryMebOffersByGrpOfferId(productId);
        IDataset mebSet = new DatasetList();
        if(IDataUtil.isEmpty(result))
            return mebSet;
        for(int i = 0, isize = result.size(); i < isize; i++)
        {
            IData relOffer = result.getData(i);
            IData productMeb = new DataMap();
            productMeb.put("PRODUCT_ID_B", relOffer.getString("OFFER_CODE"));
            productMeb.put("PRODUCT_NAME", relOffer.getString("OFFER_NAME"));
            productMeb.put("PRODUCT_ID", relOffer.getString("OFFER_CODE"));
            productMeb.put("NODE_COUNT", "1");
            String selectTag = relOffer.getString("SELECT_FLAG","");
            if(selectTag.equals(UpcConst.SELECT_FLAG_MUST_CHOOSE))//必选
            {
                productMeb.put("FORCE_TAG", "1");
                productMeb.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_YES))//默认
            {
                productMeb.put("FORCE_TAG", "0");
                productMeb.put("DEFAULT_TAG", "1");
            }
            if(selectTag.equals(UpcConst.SELECT_FLAG_CAN_CHOOSE_NO))//可选
            {
                productMeb.put("FORCE_TAG", "0");
                productMeb.put("DEFAULT_TAG", "0");
            }
            
            mebSet.add(productMeb);
        }
        return mebSet;
    }
    
    /**
     * 通过成员产品查询集团产品
     * @param mebProductId
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpProductInfosByMebProductId(String mebProductId) throws Exception
    {
        IDataset result = UpcCall.queryGrpOfferByMebOfferId(mebProductId);
        
        IDataset grpProducts = new DatasetList();
        if(IDataUtil.isEmpty(result)){
            return grpProducts;
        }
        
        for(int i = 0, isize = result.size(); i < isize ; i++ )
        {
            IData product = new DataMap();
            product.put("PRODUCT_ID", result.getData(i).getString("OFFER_CODE"));
            product.put("PRODUCT_NAME", result.getData(i).getString("OFFER_NAME"));
            grpProducts.add(product);
            
        }
        
        return grpProducts;
    }
    
}
