
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class UProductLimitInfoQry
{

    public static IDataset getLimitTagA(String productIdB) throws Exception
    {
     IDataset  result = UpcCall.queryOfferRelInfoByTwoOfferOrInversionIfNecessary(null, null, BofConst.ELEMENT_TYPE_CODE_PRODUCT, productIdB, null);
     IDataset mebSet = new DatasetList();
     if(IDataUtil.isEmpty(result))
     {
    	 return mebSet; 
     }
      
     for(int i = 0, isize = result.size(); i < isize; i++)
     {
         IData relOffer = result.getData(i);
         IData product = new DataMap();
         product.put("PRODUCT_ID_A", relOffer.getString("OFFER_CODE"));
         product.put("PRODUCT_ID_B", productIdB);
         product.put("LIMIT_TAG", relOffer.getString("REL_TYPE"));
         mebSet.add(product);
     }
     return mebSet;

    }

    /**
     * 根据产品ID查询LIMIT_TAG 即依赖和互斥的关系以及对应的主产品product_id_b modify_liuxx3_20140515_01
     */
    public static IDataset getLimitTagB(String productIdA) throws Exception
    {
         IDataset  result = UpcCall.queryOfferRelInfoByTwoOfferOrInversionIfNecessary( BofConst.ELEMENT_TYPE_CODE_PRODUCT, productIdA, null, null, null);
         IDataset mebSet = new DatasetList();
         if(IDataUtil.isEmpty(result))
         {
        	 return mebSet; 
         }
          
         for(int i = 0, isize = result.size(); i < isize; i++)
         {
             IData relOffer = result.getData(i);
             IData product = new DataMap();
             product.put("PRODUCT_ID_A", productIdA);
             product.put("PRODUCT_ID_B", relOffer.getString("REL_OFFER_CODE"));
             product.put("LIMIT_TAG", relOffer.getString("REL_TYPE"));
             mebSet.add(product);
         }
         return mebSet;

    }

}
