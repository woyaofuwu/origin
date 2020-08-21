
package com.asiainfo.veris.crm.order.soa.frame.bcf.lock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.cache.memcache.util.GlobalLock;

public final class BizLock
{
    private static final transient Logger log = Logger.getLogger(BizLock.class);

    // 存放锁对象，若需要控制锁的顺序，可以用LinkedHashMap
    private Map<String, String> lock = new HashMap<String, String>();

    /**
     * Lock对象必须定义的clean方法，不能有入参
     */
    public void clean()
    {
        if (log.isDebugEnabled())
        {
            log.debug("清空BusinessLock的锁对象");
        }

        Iterator<String> iter = lock.keySet().iterator();

        String lockKey = "";

        // 循环锁对象
        while (iter.hasNext())
        {
            lockKey = iter.next();

            // 解锁
            unlock(lockKey);

            iter = lock.keySet().iterator();
        }
    }

    /**
     * Lock对象必须定义的lock方法，入参可自由定义
     * 
     * @param lockKey
     * @param param
     * @return
     */
    public boolean lock(String lockKey)
    {
        if (lock.containsKey(lockKey))
        {
            return true;
        }

        // 加锁
        boolean isLock = GlobalLock.lock(lockKey);

        if (isLock)
        {
            // 放入锁对象
            lock.put(lockKey, "0");
        }

        return isLock;
    }

    /**
     * Lock对象必须定义的unlock方法，入参可自由定义
     * 
     * @param lockKey
     * @param param
     * @return
     */
    public boolean unlock(String lockKey)
    {
        // 解锁
        boolean isLock = GlobalLock.unlock(lockKey);

        // 释放锁对象
        lock.remove(lockKey);

        return isLock;
    }
}
