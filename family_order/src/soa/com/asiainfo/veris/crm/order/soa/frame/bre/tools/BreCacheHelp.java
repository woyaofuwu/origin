
package com.asiainfo.veris.crm.order.soa.frame.bre.tools;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.cache.BreDefinitionCache;
import com.asiainfo.veris.crm.order.soa.frame.bre.cache.BreParameterCache;
import com.asiainfo.veris.crm.order.soa.frame.bre.cache.BreDefinitionCache.BreDefinition;
import com.asiainfo.veris.crm.order.soa.frame.bre.cache.BreParameterCache.BreParameter;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.ClazzError;

public final class BreCacheHelp extends BreBase
{

    private static final Logger logger = Logger.getLogger(BreCacheHelp.class);

    /* 获取规则配置参数对应map对象 */
    public static final BreRuleParam getRulePerameterMapByRuleId(String strRuleId) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>> getRulePerameterMapByRuleId(" + strRuleId + ")");
        }

        BreRuleParam map = new BreRuleParam();
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(BreParameterCache.class);
        BreParameter breParameter = (BreParameter) cache.get(strRuleId);

        if (breParameter != null)
        {
            map = breParameter.getRuleData();
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("<<<<<<<<<<<<<<<<<<<<<< getRulePerameterMapByRuleId(" + map + ")");
        }

        return map;
    }

    /* 获取规则对象 */
    public static final Object getScriptObjectByScriptId(String strScriptId) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>> getScriptObjectByScriptId(" + strScriptId + ")");
        }

        Object obj = null;
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(BreDefinitionCache.class);
        BreDefinition breDefinition = (BreDefinition) cache.get(strScriptId);
        if (breDefinition != null)
        {
            obj = breDefinition.getObjScript();
        }
        else
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! getScriptObjectByScriptId (" + strScriptId + ") ClassNotFound in Cache!");
            }
            obj = new ClazzError();
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("<<<<<<<<<<<<<<<<<<<<<< getScriptObjectByScriptId(" + strScriptId + ") = [" + obj + "]");
        }

        return obj;
    }
}
