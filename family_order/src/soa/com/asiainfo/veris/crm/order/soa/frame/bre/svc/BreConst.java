
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

/**
 * Copyright: Copyright 2014 6 25 Asiainfo-Linkage *
 * 
 * @ClassName: BreConst
 * @Description:
 * @version: v1.0.0
 * @author: xiaocl
 */
public final class BreConst
{
    static enum BreCtrl
    {

    }

    // 规则集合分类
    static class RuleSet
    {
        public static final String BASE = "BASE";

        public static final String PAGE_RULE = "PAGE_RULE";// 页面元素规则

        public static final String SALEACTIVE = "SALEACTIVE";

        public static final String CHANGEELEMENT = "CHANGEELEMENT";

        public static final String SP = "SP";

        public static final String GRP = "chkBeforeForGrp";

        public static final String RG_TD_BEFORE = "TRADEALL.TradeCheckBefore";// 形成台账串之前调用的规则 登记服务调用

        public static final String RG_TD_AFTER = "TRADEALL.TradeCheckAfter";// 形成台账串后调用的规则 登记服务调用
    }

    static class tipsType
    {
    }

    BreConst()
    {
    }

}
