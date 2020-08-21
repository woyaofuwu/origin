
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import com.ailk.service.protocol.ServiceException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.AbstractFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreCacheHelp;

/**
 * Copyright: Copyright 2014/6/30 Asiainfo-Linkage
 * 
 * @ClassName: FactoryGetRulelist
 * @Description:
 * @version: v1.0.0
 * @author: xiaocl
 */
class FactoryGetRulelist extends AbstractFactory
{
    public Object GetInstance(String key, boolean bSingletion) throws Exception
    {
        if (bSingletion)
        {
            String clazzName = "";
            Object o = null;
            try
            {
                o = BreCacheHelp.getScriptObjectByScriptId(key);
                clazzName = o.getClass().getName();
                if (registry.containsKey(clazzName))
                {
                    return LookObject(clazzName);
                }
                Register(clazzName, o);
                return o;
            }
            catch (InstantiationException e)
            {
                throw new ServiceException((new StringBuilder()).append("\u65E0\u6CD5\u521B\u5EFA\u670D\u52A1\u5B9E\u4F8B[").append(clazzName).append("]").toString(), e);
            }
            catch (IllegalAccessException e)
            {
                throw new ServiceException((new StringBuilder()).append("\u670D\u52A1\u5B9E\u4F8B\u8BBF\u95EE\u5F02\u5E38[").append(clazzName).append("]").toString(), e);
            }
        }
        else
        {
            return BreCacheHelp.getScriptObjectByScriptId(key);
        }
    }
}
