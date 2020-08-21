
package com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util;

import com.ailk.cache.localcache.ConcurrentLRUMap;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;

public final class DataBusManager
{
    public static ConcurrentLRUMap<String, OrderDataBus> databusManager = new ConcurrentLRUMap<String, OrderDataBus>(800);

    public static void addDataBus(OrderDataBus odb)
    {
        String threadId = getThreadId();

        databusManager.put(threadId, odb);
    }

    public static OrderDataBus getDataBus()
    {
        String threadId = getThreadId();

        OrderDataBus odb = databusManager.get(threadId);

        if (odb == null)
        {
            odb = new OrderDataBus();

            databusManager.put(threadId, odb);
        }

        return odb;
    }

    private static String getThreadId()
    {
        String threadId = String.valueOf(SessionManager.getInstance().getId());

        return threadId;
    }

    public static boolean isHasDataBus()
    {
        String threadId = getThreadId();
        OrderDataBus odb = databusManager.get(threadId);
        if (odb == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static void removeDataBus()
    {
        String threadId = getThreadId();

        databusManager.remove(threadId);
    }
}
