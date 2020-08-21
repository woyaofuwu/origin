
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadWriteCache;

public class MVELCompiledCache
{
    private static IReadWriteCache statementCache = CacheFactory.getReadWriteCache("MVEL_STATEMENT_CACHE");

    private static IReadWriteCache templateCache = CacheFactory.getReadWriteCache("MVEL_TEMPLATE_CACHE");

    public static IReadWriteCache getStatementCache()
    {
        return statementCache;
    }

    public static IReadWriteCache getTemplateCache()
    {
        return templateCache;
    }

}
