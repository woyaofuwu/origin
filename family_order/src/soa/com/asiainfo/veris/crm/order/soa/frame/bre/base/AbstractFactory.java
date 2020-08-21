
package com.asiainfo.veris.crm.order.soa.frame.bre.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright: Copyright 2014/6/30 Asiainfo-Linkage
 * 
 * @ClassName: AbstractFactory
 * @Description: 规则引擎产品封装，BRE所有系列的对象创建 .支持是否单例模式，版本待续 * 不定义为混合类型，此功能边界保持独立。MLGBD
 * @version: v1.0.0
 * @author: xiaocl
 */
public abstract class AbstractFactory implements IBREOMG
{
    // public abstract Object creator(Object o,boolean bSingletion) throws Exception;
    static protected Map<String, Object> registry = new ConcurrentHashMap(100000);

    protected static Object LookObject(String name)
    {
        return registry.get(name);
    }

    protected static void Register(String name, Object o)
    {
        registry.put(name, o);
    }

    public abstract Object GetInstance(String key, boolean bSingletion) throws Exception;
}
