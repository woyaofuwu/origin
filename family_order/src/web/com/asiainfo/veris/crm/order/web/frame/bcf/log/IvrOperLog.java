
package com.asiainfo.veris.crm.order.web.frame.bcf.log;

import com.ailk.cache.memcache.util.SharedCache;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.IvrData;

public final class IvrOperLog
{
    public static void clearIvrData(String staffId) throws Exception
    {
        // 得到缓存key
        String cacheKey = CacheKey.getIvrKey(staffId);

        SharedCache.delete(cacheKey);
    }

    public static void writeIvrData(String staffId, IvrData ivrData) throws Exception
    {
        // 得到缓存key
        String cacheKey = CacheKey.getIvrKey(staffId);

        SharedCache.set(cacheKey, ivrData, 600);
    }
}
