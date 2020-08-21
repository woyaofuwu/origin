package com.asiainfo.veris.crm.iorder.pub.family.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wych
 */
public class FamilyConstants {

    /**
     * 业务类型枚举
     *
     * @author wych
     */
    public static enum FamilyTradeType {
        ACCEPT("450"), // 家庭业务受理
        ADD_MEMBER("451"), // 家庭新增成员
        DELETE_MEMBER("452"), // 家庭删除成员
        UPDATE_MEMBER("453"), // 家庭更新成员信息
        PAY_RELATION_MANAGE("454"), // 家庭付费关系管理
        ADMINISTRATOR_CHANGE("455"), // 管理员变更
        CHANGE("456"), // 家庭产品变更
        CANCELLATION("457"),// 家庭业务退订
        SHARE_RELATION_MANAGE("458"),// 共享关系管理
        FAMILY_CREDIT_STOP("459"),// 家庭信控停机
        FAMILY_CREDIT_OPEN("460");// 家庭信控开机


        private final String value;

        private FamilyTradeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }
    }

    /**
     * 成员关系属性
     */
    public static enum FamilyMemCha {
        FAMILY_MANAGER("FAMILY_MANAGER"),
        FAMILY_PAY("FAMILY_PAY"),
        FAMILY_SHARE("FAMILY_SHARE"),
        FAMILY_TOPSETBOX("FAMILY_TOPSETBOX");

        private final String value;

        private FamilyMemCha(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }
    }

    public static enum TriggerPoint {
        BUSI_TRANS,
        SPECIAL_DEAL,
        OTHER_TRADE,
        FILTER_OFFERS,
        CHECK,
        SELECT_OFFERS,
        CHANGE_FAMILY_PRODUCT,
        INIT,
        ADD_MEMBER,
        BEFORE_CHECK;
    }

    public static final String TYPE_OLD = "OLD";// 提交前触发点

    public static final String TYPE_NEW = "NEW";// 提交前触发点

    public static final String TRADE_LIMIT_STATE = "0";

    public static final String FAMILY_PAGE_ID = "FAMILY";

    public static final int TRADE_UNFINISHED = 0;// 工单未完工标记

    public static final int TRADE_FINISHED = 1;  // 工单已完工标记

    public static final String FAMILY_PAY_DEFAULT_TAG = "2";// 家庭代付

    public static final String FAMILY_SHARE_RELATION_TYPE_CODE = "FG";// 家庭共享关系

    public static final String FAMILY_WIDENET_MEMBER_PAY_CODE = "48";// 家庭宽带成员代付取消47增加新关系类型

    public static final String FAMILY_IMS_MEMBER_PAY_CODE = "SM";//家庭ims固话成员代付取消47增加新关系类型

    public static final String WIDNET_PHONE_RELATION_TYPE_CODE = "47";//宽带绑定手机

    public static final String IMS_PHONE_RELATION_TYPE_CODE = "MS";//IMS家庭固话绑定手机

    public static final String FAMILY_PRODUCT_MODE = "16";// 家庭产品模式编码

    public static final String FAMILY_BRAND_CODE = "RH01";// 家庭融合品牌编码

    // 开关值 表示否开 开关 都可以用不用重复定义
    public interface SWITCH {
        public static final String ZERO = "0";
        public static final String ONE = "1";
        public static final String YES = "Y";
        public static final String NO = "N";
    }

    // 商品常量
    public interface OFFER {
        public static final String TRANS_REL = "0";// 可转换
        public static final String JOIN_REL = "2";// 推荐关系
        public static final String FAKE_ID = "-1";// 通用填充（假值）
        public static final String FMY_OFFER_SPEC_TAG = "3";// 优惠特殊标记
    }

    // 属性类型
    public interface CHA_TYPE {
        // 管理类属性
        public static final String MANAGER = "M";
    }

    // 子业务类型
    public interface ROLE_TRADE_TYPE {
        public static final String PHONE = "110";//产品变更
        public static final String WIDE_OPEN = "600";//宽带开户
        public static final String WIDE_CHANGE = "601";//宽带开户
        public static final String IMS_OPEN = "6800";//固话开户
        public static final String MBH_OPEN = "4800";//魔百和开户
    }

    // 家庭信控业务类型
    public interface CREDIT_TRADE_TYPE {
        public static final String CREDIT_OPEN = "7301";//缴费开机
        public static final String CREDIT_STOP = "7220";//欠费停机

    }


    //宽带营销活动
    public static final Map<String, String> WIDENET_SALE = new HashMap<String, String>() {
        {
            put("69908001", "宽带1+活动");
            put("67220428", "宽带包年优惠活动");
            put("66000602", "候鸟短期套餐（2017）");
            put("66004809", "度假宽带2019");
        }
    };
    
    //宽带类型(wide_product_type) 
    public static final Map<String, String> WIDENET_TYPE = new HashMap<String, String>() {
        {
            put("1", "移动FTTB");
            put("2", "铁通ADSL");
            put("3", "移动FTTH");
            put("5", "铁通FTTH");
            put("6", "铁通FTTB");
        }
    };

}
