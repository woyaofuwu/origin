
package com.asiainfo.veris.crm.order.soa.script.productlimit;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 程序判斷TAG全局集合
 * @author: xiaocl
 */
public class CheckProductDecision
{

    private boolean B_DISCNT_LIMIT = false;// 是否走资费依赖互斥校验

    private boolean B_IS_CHECK_LIMIT = false;// 是否走产品依赖互斥校验, 否者直接退出

    private boolean B_IS_ONLY_CHECK_SVC_STATE = false;// 是否只校验服务状态

    private boolean B_SERVICE = false;// 是否走服务依赖互斥校验

    private boolean B_PACKAGE = false;// 是否走包依赖互斥校验

    private boolean B_HAS_TRADE_SVC = false;

    private boolean B_HAS_TRADE_DISCNT = false;

    private boolean B_HAS_TRADE_PACKAGE = false;

    private boolean B_HAS_TRADE_PRODUCT = false;// 用户是否拥有台账产品资料

    private boolean B_HAS_TRADE_ELEMENT = false;// 用户是否拥有当前的订购资料 //是否检查当前操作元素的必选参数

    private boolean B_IS_PKGINSIDE_ELMENETLIMIT = false;// 是否走全局元素校验

    public boolean getBDiscntLimit()
    {
        return B_DISCNT_LIMIT;
    }

    public boolean getBIsCheckLimit()
    {
        return B_IS_CHECK_LIMIT;
    }

    public boolean getBisPkgInsideElementLimit()
    {
        return B_IS_PKGINSIDE_ELMENETLIMIT;
    }

    public boolean getBOnlyCheckSvcState()
    {
        return B_IS_ONLY_CHECK_SVC_STATE;
    }

    public boolean getBPackage()
    {
        return B_PACKAGE;
    }

    public boolean getBService()
    {
        return B_SERVICE;
    }

    public boolean hasTradeDiscnt()
    {
        return B_HAS_TRADE_DISCNT;
    }

    public boolean hasTradeElement()
    {
        return B_HAS_TRADE_ELEMENT;
    }

    public boolean hasTradePackage()
    {
        return B_HAS_TRADE_PACKAGE;
    }

    public boolean hasTradeProduct()
    {
        return B_HAS_TRADE_PRODUCT;
    }

    public boolean hasTradesvc()
    {
        return B_HAS_TRADE_SVC;
    }

    public void setBDiscntLimit(boolean bTag)
    {
        B_DISCNT_LIMIT = bTag;
    }

    public void setBHasTradeDiscnt(boolean bTag)
    {
        B_HAS_TRADE_DISCNT = bTag;
    }

    public void setBHasTradeElement(boolean bTag)
    {
        B_HAS_TRADE_ELEMENT = bTag;
    }

    public void setBHasTradePackage(boolean bTag)
    {
        B_HAS_TRADE_PACKAGE = bTag;
    }

    public void setBHasTradeProduct(boolean bTag)
    {
        B_HAS_TRADE_PRODUCT = bTag;
    }

    public void setBHasTradesvc(boolean bTag)
    {
        B_HAS_TRADE_SVC = bTag;
    }

    public void setBIsCheckLimit(boolean bTag)
    {
        B_IS_CHECK_LIMIT = bTag;
    }

    public void setBOnlyCheckSvcState(boolean bTag)
    {
        B_IS_ONLY_CHECK_SVC_STATE = bTag;
    }

    public void setBPackage(boolean bTag)
    {
        B_PACKAGE = bTag;
    }

    public void setBService(boolean bTag)
    {
        B_SERVICE = bTag;
    }
}
