
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = 1L;

    private int maxSize;

    public LRUHashMap(int maxSize)
    {
        super(16, 0.75f, true);
        this.maxSize = maxSize;
    }

    public int getMaxSize()
    {
        return maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        return size() > maxSize;
    }

    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }
}
