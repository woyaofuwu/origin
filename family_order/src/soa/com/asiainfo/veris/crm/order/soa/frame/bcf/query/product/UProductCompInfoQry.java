
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UProductCompInfoQry
{
    
    /**
     *  查询产品的定制信息
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getUseTagByProductId(String productId) throws Exception
    {
        
        
        IDataset result = UpcCall.queryOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "USE_TAG");
        if(IDataUtil.isEmpty(result)){
            return "";
        }
        else
        {
            return result.getData(0).getString("FIELD_VALUE");
        }
        
    }
    
    /**
     * 获取产品的关系类型
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getRelationTypeCodeByProductId(String productId) throws Exception
    {
        
        
        IDataset result = UpcCall.queryOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "RELATION_TYPE_CODE");
        if(IDataUtil.isEmpty(result)){
            return "";
        }
        else
        {
            return result.getData(0).getString("FIELD_VALUE");
        }
        
    }
    
    /**
     * 查询comp表
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData getCompInfoByProductId(String productId) throws Exception
    {
        IDataset offerCompChas = UpcCall.queryOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
        IData result = new DataMap();
        if(IDataUtil.isEmpty(offerCompChas)){
            return result;
        }
       
        result.put("PRODUCT_ID", productId);
        for(int i = 0, isize = offerCompChas.size(); i< isize; i++)
        {
            IData offerCompCha = offerCompChas.getData(i);
            String fieldName = offerCompCha.getString("FIELD_NAME", "");
            
            if(fieldName.equals("USE_TAG"))
            {
                result.put("USE_TAG", offerCompCha.getString("FIELD_VALUE"));
            }
            else if(fieldName.equals("RELATION_TYPE_CODE"))
            {
                result.put("RELATION_TYPE_CODE", offerCompCha.getString("FIELD_VALUE"));
            }
        }
        
        return result;
    }
}
