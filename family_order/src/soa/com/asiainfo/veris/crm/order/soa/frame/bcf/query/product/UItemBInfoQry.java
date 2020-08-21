package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UItemBInfoQry {

	/**
	 * 根据产品标识,产品类型,属性编码,属性值查询属性字段值
	 * @param offer_code,offer_type,field_name,value
	 * @return
	 * @throws Exception
	 */
	public static IData qryChaSpecialVal(String offerCode,String offerType,String fileName,String value) throws Exception
    {
		
		IData chaSpecialVal = UpcCall.queryChaSpecValByCond(offerType, offerCode, fileName, value);
		return chaSpecialVal;
    }
	
	public static IDataset qryOfferChaValByFieldNameOfferCodeAndOfferType(String offerCode,String offerType,String fileName) throws Exception
    {
        IDataset offerChaVals = UpcCall.qryOfferChaValByFieldNameOfferCodeAndOfferType(offerType, offerCode, fileName);
        if (IDataUtil.isNotEmpty(offerChaVals))
        {
            for (int i = 0, size = offerChaVals.size(); i < size; i++)
            {
                IData offerChaVal = offerChaVals.getData(i);
                offerChaVal.put("ATTR_FIELD_CODE", offerChaVal.getString("VALUE"));
                offerChaVal.put("ATTR_FIELD_NAME", offerChaVal.getString("TEXT"));
            }
        }
        return offerChaVals;
    }
	
	
	public static IData queryChaSpecByfieldNameAndvalueAndOfferId(String offerType,String offerCode,String fileName,String value) throws Exception
    {
		
		IDataset offerChaVals = UpcCall.queryChaSpecByfieldNameAndvalueAndOfferId(offerType, offerCode, fileName, value);
		
		if(IDataUtil.isNotEmpty(offerChaVals)){
			  for(int i = 0, size = offerChaVals.size(); i < size; i++){
				  IData offerChaVal = offerChaVals.getData(i);
	                offerChaVal.put("ATTR_FIELD_CODE", offerChaVal.getString("VALUE"));
	                offerChaVal.put("ATTR_FIELD_NAME", offerChaVal.getString("TEXT"));
			  }
		}else{
			return null;
		}
		   return offerChaVals.getData(0);
    }
}
