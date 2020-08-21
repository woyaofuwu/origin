
package com.asiainfo.veris.crm.order.soa.frame.bcf.dao;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.BizLog;
import com.ailk.common.logger.AbstractLogger;

public final class CrmDAOLogger extends AbstractLogger
{
    private static final Logger log = Logger.getLogger(CrmDAOLogger.class);

    private static final int LOG_SQL_LEVEL = 1;

    /**
     * object 当前正在执行的DAO对象 subkey 统计日志的Key start 当前SQL执行的开始时间 cost 当前SQL执行消耗的时间(毫秒) content 统计类日志，该值为null
     */
    public void log(Object object, String subkey, long start, long cost, String content)
    {
        boolean env = false;
        try
        {
            env = BizEnv.getEnvBoolean("dao.logger");
        }
        catch (Exception e)
        {
            env = false;
            log.warn("从BizEnv读取dao.logger错误，采用默认值：false，该错误不影响系统正常运行。");
        }
        if (!env)
            return;

        int mntlevel = 1;

        try
        {
            mntlevel = BizEnv.getEnvInt("crm.mnt.level", LOG_SQL_LEVEL);
        }
        catch (Exception e)
        {
            mntlevel = LOG_SQL_LEVEL;

            log.error("从BizEnv读取crm.mnt.level配置错误，取默认值[" + mntlevel + "]，不影响系统正常运行。");
        }

        boolean isDebug = false;

        try
        {
            isDebug = BizEnv.getEnvBoolean("biz.dao.log.debug");
        }
        catch (Exception e)
        {
            isDebug = false;

            log.warn("从BizEnv读取biz.dao.log.debug错误，取默认值false，该错误不影响系统正常运行。");
        }

        setDebug(isDebug);

        String key = getDefaultKey(Dao.class, mntlevel, subkey);

        if (log.isDebugEnabled())
        {
            log.debug("key=[" + key + "], level=[" + mntlevel + "]");
        }

        if (null == key || key.length() <= 0)
        {
            return;
        }

        BizLog.send(LOG_STAT_SQL, key, start, cost, content);
    }

}
