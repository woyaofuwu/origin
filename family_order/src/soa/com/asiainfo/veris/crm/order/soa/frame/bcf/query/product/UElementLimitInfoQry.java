package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UElementLimitInfoQry 
{

	/**
     * 查询与A元素有限制关系的元素集合
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset queryElementLimitByElementIdB(String elementId, String elementTypeCode, String limitTag) throws Exception
    {
        
        
        IDataset result = UpcCall.queryOfferRelByCond(elementTypeCode, elementId, limitTag);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		IData data = result.getData(i);
        		data.put("ELEMENT_TYPE_CODE_A", data.getString("REL_OFFER_TYPE"));
        		data.put("ELEMENT_ID_A", data.getString("REL_OFFER_CODE"));
        		data.put("ELEMENT_TYPE_CODE_B", elementTypeCode);
        		data.put("ELEMENT_ID_B", elementId);
        		data.put("LIMIT_TAG", data.getString("REL_TYPE"));
        	}
        }
        
        return result;
    }
    
    public static IDataset getElementLimitByElementId358() throws Exception
    {
        
        IDataset result = new DatasetList();
        IDataset result3 = UpcCall.queryOfferRelByCond("D", "1285", "0");
        IDataset result5 = UpcCall.queryOfferRelByCond("D", "1286", "0");
        IDataset result8 = UpcCall.queryOfferRelByCond("D", "1391", "0");
        result.addAll(result3);
        result.addAll(result5);
        result.addAll(result8);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0; i<result.size(); i++)
        	{
        		IData data = result.getData(i);
        		data.put("ELEMENT_TYPE_CODE_A", data.getString("REL_OFFER_TYPE"));
        		data.put("ELEMENT_ID_A", data.getString("REL_OFFER_CODE"));
        		data.put("LIMIT_TAG", data.getString("REL_TYPE"));
        	}
        }
        
        return result;
    }
}
