
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import com.ailk.cache.localcache.ConcurrentLRUMap;
import com.ailk.service.session.SessionManager;

public final class AcctTimeEnvManager
{
    private static ConcurrentLRUMap threadEnv = new ConcurrentLRUMap(500);

    public static AcctTimeEnv getAcctTimeEnv()
    {
        String serviceSessionId = String.valueOf(SessionManager.getInstance().getId());
        return (AcctTimeEnv) threadEnv.get(serviceSessionId);
    }

    public static void removeAcctTimeEnv()
    {
        String serviceSessionId = String.valueOf(SessionManager.getInstance().getId());
        threadEnv.remove(serviceSessionId);
    }

    public static void setAcctTimeEnv(AcctTimeEnv timeEnv)
    {
        String serviceSessionId = String.valueOf(SessionManager.getInstance().getId());
        threadEnv.put(serviceSessionId, timeEnv);
    }
}
