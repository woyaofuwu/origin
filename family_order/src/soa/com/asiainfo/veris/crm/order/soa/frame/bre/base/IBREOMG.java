
package com.asiainfo.veris.crm.order.soa.frame.bre.base;

/**
 * Copyright: Copyright 2014/6/29 Asiainfo-Linkage
 * 
 * @ClassName: IBREOMG
 * @Description: 规则引擎 对象的管理
 * @version: v1.0.0
 * @author: xiaocl
 */
public interface IBREOMG
{
    abstract Object GetInstance(String key, boolean bSingletion) throws Exception;// 根据某KEY从对象池(二级缓存)中获取某，一单例对象
    // abstract Object creator(Object o, boolean bSingletion) throws Exception;//根据某对象，从对象池(二级缓存)中获取另外一缓存对象，可以选择是否。
    // 创建对象池(一级缓存)，并初始化
    // 从对象池(一级缓存)中捞一对象
}
