
package com.asiainfo.veris.crm.order.soa.frame.bre.tools;

import com.asiainfo.veris.crm.order.soa.frame.bre.script.rule.common.SplCheckBySql;

public final class BreFactory
{
    /* 业务前规则校验需要检查的输入参数 */
    public static final String[] BEFORE_FORCE_PARAM =
    { "TRADE_TYPE_CODE", "TRADE_EPARCHY_CODE", "TRADE_STAFF_ID", "TRADE_CITY_CODE", "FEE", /* 678传往月欠费， 123456传实时结余 */
    "ID", /* 由id_type 决定是 user_id 或者 cust_id */
    "ID_TYPE", /* 0:cust_id, 1:user_id */
    "IN_MODE_CODE", "PROVINCE_CODE", "REDUSER_TAG", /* 红名单标记， 0－不是；1－是； */
    /* "X_CHOICE_TAG", *//* 0:输号码校验;1:提交校验; */
    "BRAND_CODE", "PRODUCT_ID", "USER_ID", "CUST_ID" };

    /* 继续兼容ＮＧ版本时候的ＣｈｅｃｋＢｙＳｑｌ */
    public static final String CHECK_BY_SQL = SplCheckBySql.class.getName();
    
    /* 业务后规则校验 */
    public static final String CHECK_TRADE_AFTER = "TradeCheckAfter";

    /* 业务前规则校验 */
    public static final String CHECK_TRADE_BEFORE = "TradeCheckBefore";

    /* 当前时间 */
    public static final String CUR_DATE = "CUR_DATE";

    /* 下月第一天 */
    public static final String FIRST_DAY_OF_NEXT_MONTH = "FIRST_DAY_OF_NEXT_MONTH";

    /* 本月第一天 */
    public static final String FIRST_DAY_OF_THIS_MONTH = "FIRST_DAY_OF_THIS_MONTH";

    /* 非全局， 包内元素判断 */
    public static final boolean IS_PKG_INSIDE_ELEMENT_LIMIT = true;

    /* 本月最后一天 */
    public static final String LAST_DAY_OF_CUR_MONTH = "LAST_DAY_OF_CUR_MONTH";

    /* 是全局元素判断 */
    public static final boolean NOT_PKG_INSIDE_ELEMENT_LIMIT = false;

    /* 选择是否 */
    public static final int TIPS_TYPE_CHIOCE = 2;

    /* 报错 */
    public static final int TIPS_TYPE_ERROR = 0;

    /* 需要强制报错 */
    public static final int TIPS_TYPE_FORCE_EXIT = 4;

    /* 提示 */
    public static final int TIPS_TYPE_TIPS = 1;
}
