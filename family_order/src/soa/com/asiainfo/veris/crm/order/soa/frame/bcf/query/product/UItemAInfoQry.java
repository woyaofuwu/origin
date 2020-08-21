package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UItemAInfoQry{

	/** 
	 *  根据商品ID 查询itema表元素配置情况 入参 offerTypeE 元素类型 showMode 元素显示类型 offerCodeP 产品ID
	 * @param offerCodeP
	 * @param offerTypeE
	 * @param showMode
	 * @return
	 * @throws Exception
	 */ 
	public static IDataset qryChaSpeByOfferCodeP(String offerCodeP,String offerTypeE,String showMode) throws Exception
    {
		
		//IData chaSpecialVal = UpcCall.queryChaSpecValByCond(offerType, offerCode, fileName, value);
		return new DatasetList();
    }
	
	public static IDataset queryOfferChaByCond(String id, String idType, String attrObj, String eparchyCode, Pagination pagination) throws Exception
    {
	    IDataset offerChas = UpcCall.queryOfferChaByCond(idType, id, null, attrObj, idType, eparchyCode, pagination);
	    
	    if (IDataUtil.isNotEmpty(offerChas))
        {
            for (int i = 0, size = offerChas.size(); i < size; i++)
            {
                IData offerCha = offerChas.getData(i);
                offerCha.put("ATTR_CODE", offerCha.getString("FIELD_NAME"));
                offerCha.put("ATTR_LABLE", offerCha.getString("CHA_SPEC_NAME"));
                offerCha.put("ATTR_INIT_VALUE", offerCha.getString("DEFAULT_VALUE"));
            }
        }
	    
	    return offerChas;
    }
	
	public static IDataset queryOfferChaByCond(String offerType, String offerCode, String idType, String attrObj, String eparchyCode, String fieldName) throws Exception
    {
        IDataset offerChas = UpcCall.queryOfferChaByCond(offerType, offerCode, fieldName, attrObj, idType, eparchyCode);
        
        if (IDataUtil.isNotEmpty(offerChas))
        {
            for (int i = 0, size = offerChas.size(); i < size; i++)
            {
                IData offerCha = offerChas.getData(i);
                offerCha.put("ATTR_CODE", offerCha.getString("FIELD_NAME"));
                offerCha.put("ATTR_LABLE", offerCha.getString("CHA_SPEC_NAME"));
                offerCha.put("ATTR_INIT_VALUE", offerCha.getString("DEFAULT_VALUE"));
            }
        }
        
        return offerChas;
    }
	
	/**
	 * 翻译ATTR_ITEMA
	 * @param offerId
	 * @param offerType
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOfferChaByIdAndIdType(String offerType, String offerId, String eparchyCode) throws Exception
    {
        IDataset offerChaAndVals = UpcCall.queryOfferChaByCond(offerType, offerId, "", "", "",eparchyCode);
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(offerChaAndVals))
        {
            for (int i = 0, size = offerChaAndVals.size(); i < size; i++)
            {
                IData offerChaAndVal = offerChaAndVals.getData(i);
                
                IData attrItemData = new DataMap();
                attrItemData.put("ID", offerId);
                attrItemData.put("ID_TYPE", offerType);
                attrItemData.put("ATTR_CODE", offerChaAndVal.getString("FIELD_NAME"));
                attrItemData.put("ATTR_TYPE_CODE", offerChaAndVal.getString("SHOW_MODE"));
                attrItemData.put("ATTR_LABLE", offerChaAndVal.getString("CHA_SPEC_NAME"));
                attrItemData.put("ATTR_HINT", offerChaAndVal.getString("CHA_SPEC_NAME"));
                attrItemData.put("ATTR_INIT_VALUE", offerChaAndVal.getString("DEFAULT_VALUE"));
                attrItemData.put("ATTR_CAN_NULL", offerChaAndVal.getString("IS_NULL"));
                result.add(attrItemData);
            }
        }
        
        return result;
    }
	
	
	/**
	 * 翻译ATTR_ITEMA
	 * @param offerId
	 * @param offerType
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryOfferChaSpecsByIdAndIdType(String offerType, String offerId, String eparchyCode) throws Exception
    {
        IDataset offerChaAndVals = UpcCall.qryOfferChaSpecsByOfferId(offerType, offerId);
        
        IDataset attrScripts = UAttrBizInfoQry.getBizAttrByIdTypeObj(offerId, offerType, "ATTRSCRIPT", null);
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(offerChaAndVals))
        {
            for (int i = 0, size = offerChaAndVals.size(); i < size; i++)
            {
                IData offerChaAndVal = offerChaAndVals.getData(i);
                
                IData attrItemData = new DataMap();
                attrItemData.put("ID", offerId);
                attrItemData.put("ID_TYPE", offerType);
                String offerCode = offerChaAndVal.getString("FIELD_NAME");
                attrItemData.put("ATTR_CODE", offerCode);
                attrItemData.put("ATTR_TYPE_CODE", offerChaAndVal.getString("SHOW_MODE"));
                attrItemData.put("ATTR_LABLE", offerChaAndVal.getString("CHA_SPEC_NAME"));
                attrItemData.put("ATTR_HINT", offerChaAndVal.getString("CHA_SPEC_NAME"));
                attrItemData.put("ATTR_INIT_VALUE", offerChaAndVal.getString("DEFAULT_VALUE"));
                attrItemData.put("ATTR_CAN_NULL", offerChaAndVal.getString("IS_NULL"));
                attrItemData.put("ATTR_FIELD_CODE", offerChaAndVal.getString("VALUE"));
                attrItemData.put("ATTR_FIELD_NAME", offerChaAndVal.getString("TEXT"));
                attrItemData.put("DISPLAY_CONDITION", offerChaAndVal.getString("RSRV_STR2"));
                attrItemData.put("ATTR_FIELD_TYPE_WADE", offerChaAndVal.getString("ATTR_FIELD_TYPE_WADE"));
                attrItemData.put("SELFFUNC", "");
                attrItemData.put("ATTR_FIELD_MIN", offerChaAndVal.getString("MIN_VALUE"));
                attrItemData.put("ATTR_FIELD_MAX", offerChaAndVal.getString("MAX_VALUE")); 
                if(IDataUtil.isNotEmpty(attrScripts)){
                    for(int s = 0 ; s < attrScripts.size(); s++){
                        IData attrScript = attrScripts.getData(s);
                        String id = attrScript.getString("ID","");
                        String idType = attrScript.getString("ID_TYPE","");
                        String attrCode = attrScript.getString("ATTR_CODE","");
                        if(StringUtils.equals(id, offerId) && StringUtils.equals(idType, offerType) && StringUtils.equals(attrCode, offerCode)){
                            attrItemData.put("SELFFUNC", attrScript.getString("ATTR_VALUE"));
                            continue;
                        }
                    }
                }
                
                result.add(attrItemData);
            }
        }
        
        return result;
    }
	
	public static IDataset queryOfferChaAndValByCond(String offerId, String offerType, String valueType, String attrObj, String fieldName) throws Exception
    {
	    IDataset offerChaAndVals = UpcCall.queryOfferChaAndValByCond(offerType, offerId, valueType, attrObj, fieldName);
        if (IDataUtil.isNotEmpty(offerChaAndVals))
        {
            for (int i = 0, size = offerChaAndVals.size(); i < size; i++)
            {
                IData offerChaAndVal = offerChaAndVals.getData(i);
                offerChaAndVal.put("ATTR_CODE", offerChaAndVal.getString("FIELD_NAME"));
                offerChaAndVal.put("ATTR_LABLE", offerChaAndVal.getString("CHA_SPEC_NAME"));
                offerChaAndVal.put("ATTR_INIT_VALUE", offerChaAndVal.getString("DEFAULT_VALUE"));
                
                IDataset offerVals = offerChaAndVal.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(offerVals))
                {
                    for (int j = 0, jsize = offerVals.size(); j < jsize; j++)
                    {
                        IData offerVal = offerVals.getData(j);
                        offerVal.put("ATTR_FIELD_CODE", offerVal.getString("VALUE"));
                        offerVal.put("ATTR_FIELD_NAME", offerVal.getString("TEXT"));
                    }
                }
            }
        }
        
        return offerChaAndVals;
    }
	
	public static IDataset queryOfferChaAndValByCond(String id, String idType, String attrObj, String fieldName) throws Exception
    {
	    if (StringUtils.isBlank(idType))
        {
            idType = BofConst.ELEMENT_TYPE_CODE_PRODUCT;
        }
	    return queryOfferChaAndValByCond(id, idType, idType, attrObj, fieldName);
    }
	
	public static IDataset queryOfferChaAndValByCond(String id, String attrObj, String fieldName) throws Exception
    {
        IDataset offerChaAndVals = UpcCall.queryOfferChaAndValByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, id, attrObj, fieldName);
        if (IDataUtil.isNotEmpty(offerChaAndVals))
        {
            for (int i = 0, size = offerChaAndVals.size(); i < size; i++)
            {
                IData offerChaAndVal = offerChaAndVals.getData(i);
                offerChaAndVal.put("ATTR_CODE", offerChaAndVal.getString("FIELD_NAME"));
                offerChaAndVal.put("ATTR_LABLE", offerChaAndVal.getString("CHA_SPEC_NAME"));
                offerChaAndVal.put("ATTR_INIT_VALUE", offerChaAndVal.getString("DEFAULT_VALUE"));
                
                IDataset offerVals = offerChaAndVal.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(offerVals))
                {
                    for (int j = 0, jsize = offerVals.size(); j < jsize; j++)
                    {
                        IData offerVal = offerVals.getData(j);
                        offerVal.put("ATTR_FIELD_CODE", offerVal.getString("VALUE"));
                        offerVal.put("ATTR_FIELD_NAME", offerVal.getString("TEXT"));
                    }
                }
            }
        }
        
        return offerChaAndVals;
    }
	
	public static IDataset qryOfferChaSpecByOfferIdShowMode(String offerCode, String offerType, String showMode) throws Exception
    {
		return UpcCall.qryOfferChaSpecByOfferIdShowMode(offerCode, offerType, showMode);
    }
}
