package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg;

import com.ailk.cache.localcache.ConcurrentLRUMap;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ProductElementsCache {
	
	public static ConcurrentLRUMap<String, IData> manager = new ConcurrentLRUMap<String, IData>(50);
    
	public static IDataset getProductElements(String productId) throws Exception{
	    if (StringUtils.isBlank(productId) || StringUtils.equals("-1", productId))
        {
            return null;
        }
		String sessionId = String.valueOf(SessionManager.getInstance().getId());
		IData cache = manager.get(sessionId);
		if(cache == null){
			cache = new DataMap();
			manager.put(sessionId, cache);
		}
		
		IDataset allElements = cache.getDataset("PRODUCT_ID");
		if(allElements != null){
			return allElements;
		}
		
		allElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", productId,null, null);
    	if(IDataUtil.isEmpty(allElements)){
    		return null;
    	}
    	
    	for(Object obj : allElements){
    		IData element = (IData)obj;
    		element.put("ELEMENT_TYPE_CODE", element.getString("OFFER_TYPE"));
    		element.put("ELEMENT_ID", element.getString("OFFER_CODE"));
    		element.put("ELEMENT_NAME", element.getString("OFFER_NAME"));
    		String flag = element.getString("FLAG");
    		element.put("PRODUCT_ID", productId);
    		element.put("PACKAGE_ID", "-1");
    		if("1".equals(element.getString("FORCE_TAG"))){
    			element.put("ELEMENT_FORCE_TAG", "1");
    		}
    		else{
    			element.put("ELEMENT_FORCE_TAG", "0");
    		}
    		
    		if("1".equals(element.getString("GROUP_FORCE_TAG"))){
    			element.put("PACKAGE_FORCE_TAG", "1");
    		}
    		else{
    			element.put("PACKAGE_FORCE_TAG", "0");
    		}
    		
    		if("1".equals(element.getString("DEFAULT_TAG"))){
    			element.put("ELEMENT_DEFAULT_TAG", "1");
    		}
    		else{
    			element.put("ELEMENT_DEFAULT_TAG", "0");
    		}
    		
    		if("1".equals(element.getString("GROUP_DEFAULT_TAG"))){
    			element.put("PACKAGE_DEFAULT_TAG", "1");
    		}
    		else{
    			element.put("PACKAGE_DEFAULT_TAG", "0");
    		}
    		
    		if(StringUtils.isNotBlank(element.getString("GROUP_ID"))){
    			element.put("PACKAGE_ID", element.getString("GROUP_ID"));
    		}
    	}
    	cache.put(productId, allElements);
    	return allElements;
	}
	
	public static IData getElement(String productId, String offerCode, String offerType) throws Exception{
	    if (StringUtils.isBlank(offerCode) || StringUtils.isBlank(offerType))
        {
            return null;
        }
		IDataset allElements = getProductElements(productId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		
		for(Object obj : allElements){
			IData element = (IData)obj;
			if(offerCode.equals(element.getString("ELEMENT_ID")) && offerType.equals(element.getString("ELEMENT_TYPE_CODE"))){
				return element;
			}
		}
		return null;
	}
	
	public static IDataset getForceDiscntElements(String productId) throws Exception{
		IDataset allElements = getProductElements(productId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if(("1".equals(element.getString("GROUP_FORCE_TAG")) || "1".equals(element.getString("GROUP_DEFAULT_TAG"))) && ("1".equals(element.getString("ELEMENT_FORCE_TAG")) || "1".equals(element.getString("ELEMENT_DEFAULT_TAG"))) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE"))){
				rst.add(element);
			}
		}
		return rst;
	}
	
	public static IDataset getForceElements(String productId) throws Exception{
		IDataset allElements = getProductElements(productId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if("1".equals(element.getString("GROUP_FORCE_TAG")) && "1".equals("ELEMENT_FORCE_TAG")){
				rst.add(element);
			}
		}
		return rst;
	}
}
