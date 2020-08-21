
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELCacheDataSHXI;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCacheSHXI;

public class CRMMVELMiscCache
{
	private static final transient Logger log = Logger.getLogger(CRMMVELMiscCache.class);
    public static MVELMiscCache getMacroCache() throws Exception
    {	
//    	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI)){
//    		boolean cacheFlag = BizEnv.getEnvBoolean("IS_CALL_NOCACHE", false);
//    		//log.info("("===========CRMMVELMiscCache=======cacheFlag=============="+cacheFlag);
//        	if(cacheFlag){
//        		MVELMiscCacheSHXI mvelCache = new MVELMiscCacheSHXI();
//        		//log.info("("===========CRMMVELMiscCache=======cacheData11111==============");
//        		Map<String, Object> cacheData = mvelCache.loadData();
//        		//log.info("("===========CRMMVELMiscCache=======cacheData22222=============="+cacheData!=null?cacheData.toString():"null");
//        		MVELCacheDataSHXI cacheTemp = (MVELCacheDataSHXI) cacheData.get("CRM_MVEL_CACHE");
//        		return cacheTemp;
//        	}else{
//        		IReadOnlyCache cache = CacheFactory.getReadOnlyCache(MVELMiscCacheSHXI.class);
//        		MVELCacheDataSHXI instance = (MVELCacheDataSHXI) cache.get("CRM_MVEL_CACHE");
//        		return instance;
//        	}
//    	}else{
    		IReadOnlyCache cache = CacheFactory.getReadOnlyCache(MVELMiscCache.class);
    		MVELMiscCache instance = (MVELMiscCache) cache.get("CRM_MVEL_CACHE");
    	    return instance;
//    	}
    }
}
