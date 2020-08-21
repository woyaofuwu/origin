package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg;

import com.ailk.biz.cache.CrmCacheTablesCache;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.ConcurrentLRUMap;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;

public class PackageElementsCache {
	
	public static ConcurrentLRUMap<String, IData> manager = new ConcurrentLRUMap<String, IData>(800);
    
	public static IDataset getPackageElements(String packageId) throws Exception{
//		String sessionId = String.valueOf(SessionManager.getInstance().getId());
//		IData cache = manager.get(sessionId);
//		if(cache == null){
//			cache = new DataMap();
//			manager.put(sessionId, cache);
//		}
//		
//		IDataset allElements = cache.getDataset(packageId);
//		if(allElements != null){
//			return allElements;
//		}
//		
        // get mc
        String cacheKey  = getCacheKey(packageId);
        IDataset allElements = (IDataset) SharedCache.get(cacheKey);
        if(IDataUtil.isNotEmpty(allElements)){
			return allElements;
		}
        
		allElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, null, null);
    	if(IDataUtil.isEmpty(allElements)){
    		return null;
    	}
    	
    	for(Object obj : allElements){
    		IData element = (IData)obj;
    		element.put("ELEMENT_TYPE_CODE", element.getString("OFFER_TYPE"));
    		element.put("ELEMENT_ID", element.getString("OFFER_CODE"));
    		element.put("ELEMENT_NAME", element.getString("OFFER_NAME"));
    		element.put("PACKAGE_ID", packageId);
    		
    		if("1".equals(element.getString("FORCE_TAG"))){
    			element.put("ELEMENT_FORCE_TAG", "1");
    		}
    		else{
    			element.put("ELEMENT_FORCE_TAG", "0");
    		}
    		
//    		if("1".equals(element.getString("GROUP_FORCE_TAG"))){
//    			element.put("PACKAGE_FORCE_TAG", "1");
//    		}
//    		else{
//    			element.put("ELEMENT_FORCE_TAG", "0");
//    		}
    		
    		if("1".equals(element.getString("DEFAULT_TAG"))){
    			element.put("ELEMENT_DEFAULT_TAG", "1");
    		}
    		else{
    			element.put("ELEMENT_DEFAULT_TAG", "0");
    		}
//    		
//    		if("1".equals(element.getString("GROUP_DEFAULT_TAG"))){
//    			element.put("PACKAGE_DEFAULT_TAG", "1");
//    		}
//    		else{
//    			element.put("PACKAGE_DEFAULT_TAG", "0");
//    		}
    		
//    		if(StringUtils.isNotBlank(element.getString("GROUP_ID"))){
//    			element.put("PACKAGE_ID", element.getString("GROUP_ID"));
//    		}
    	}
//    	cache.put(packageId, allElements);
    	
        SharedCache.set(cacheKey, allElements);
       
    	return allElements;
	}
	
	public static IData getElement(String packageId, String offerCode, String offerType) throws Exception{
		IDataset allElements = getPackageElements(packageId);
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(allElements));
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
	
	public static IDataset getForceElements(String packageId) throws Exception{
		IDataset allElements = getPackageElements(packageId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if("1".equals("ELEMENT_FORCE_TAG")){
				rst.add(element);
			}
		}
		return rst;
	}
	
	public static IDataset getDefaultElements(String packageId) throws Exception{
		IDataset allElements = getPackageElements(packageId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if("1".equals("ELEMENT_DEFAULT_TAG")){
				rst.add(element);
			}
		}
		return rst;
	}
	
	public static IDataset getForceOrDefaultElements(String packageId) throws Exception{
		IDataset allElements = getPackageElements(packageId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if("1".equals("ELEMENT_FORCE_TAG") || "1".equals("ELEMENT_DEFAULT_TAG")){
				rst.add(element);
			}
		}
		return rst;
	}
	
	public static IDataset getElementsByElementTypeCode(String packageId, String elementTypeCode) throws Exception
	{
		IDataset allElements = getPackageElements(packageId);
		if(IDataUtil.isEmpty(allElements)){
			return null;
		}
		IDataset rst = new DatasetList();
		for(Object obj : allElements){
			IData element = (IData)obj;
			if(elementTypeCode.equals(element.getString("ELEMENT_TYPE_CODE"))){
				rst.add(element);
			}
		}
		return rst;
	}
	
	private static String getCacheKey(String packageId) throws Exception
	{
		IReadOnlyCache cache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
		
		String tabName  = ""; 
        String versionOffer;
        String versionOfferComRel;
        String versionOfferJoinRel;
        String versionOfferGroupRel;
        
        String versionGroup;
        String versionGroupComRel;
        String versionExtCha;
        String versionEbableModeRek;
        
        String versionOfferComCha;
        String versionEnableMode;
        String versionOfferGift;
        
        // 
        tabName = "PM_OFFER";
        versionOffer = (String)cache.get(tabName);
        
        tabName = "PM_OFFER_COM_REL";
        versionOfferComRel = (String)cache.get(tabName);
        
        tabName = "PM_OFFER_JOIN_REL";
        versionOfferJoinRel = (String)cache.get(tabName);
        
        tabName = "PM_OFFER_GROUP_REL";
        versionOfferGroupRel = (String)cache.get(tabName);
        
        tabName = "PM_GROUP";
        versionGroup = (String)cache.get(tabName);
        
        tabName = "PM_GROUP_COM_REL";
        versionGroupComRel = (String)cache.get(tabName);
        
        tabName = "PM_EXT_CHA";
        versionExtCha = (String)cache.get(tabName);
        
        tabName = "PM_ENABLE_MODE_REL";
        versionEbableModeRek = (String)cache.get(tabName);
        
        tabName = "PM_OFFER_COM_CHA";
        versionOfferComCha = (String)cache.get(tabName);
        
        tabName = "PM_ENABLE_MODE";
        versionEnableMode = (String)cache.get(tabName);
        
        tabName = "PM_OFFER_GIFT";
        versionOfferGift = (String)cache.get(tabName);
        
        StringBuilder sb = new StringBuilder(1000);
        sb.append("PackageElementsCache.getPackageElements_").append(SysDateMgr.getSysDate("dd")).append("_").
        append(versionOffer).append("_").
        append(versionOfferComRel).append("_").
        append(versionOfferJoinRel).append("_").
        append(versionOfferGroupRel).append("_").
        append(versionGroup).append("_").
        append(versionGroupComRel).append("_").
        append(versionExtCha).append("_").
        append(versionEbableModeRek).append("_").
        append(versionOfferComCha).append("_").
        append(versionEnableMode).append("_").
        append(versionOfferGift).append("_").append(packageId);
        
        return sb.toString();
	}
}
