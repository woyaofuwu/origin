
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg;

public final class SaleActiveBreConst
{
    public static final String ACTION_TYPE_CHK_ACTIVE_TRADE = "checkSaleActiveTrade";
    public static final String ACTION_TYPE_CHK_TERMINAL_CHECK = "checkActiveTerminal";

    public static final String BRE_BY_PAGE_SEL_PACKAGE = "CHECK_BY_PACKAGE";

    public static final String ACTION_TYPE_CHK_DEPOSIT_GIFT_USER = "checkDepositGiftUser";

    public static final String ACTIVE_CHECK_MODE_INTF_CHEK = "check4CheckIntf";

    public static final String ACTIVE_CHECK_MODE_SEL_PKG = "check4SelectPackage";

    public static final String ACTIVE_CHECK_MODE_TRADE_BEFORE = "check4TradeBefore";

    public static final String SALE_TRADE_LIMIT_TAG_MUTEX = "0";//互斥规则

    public static final String SALE_TRADE_LIMIT_TAG_DEPEND = "1";//依赖规则

    public static final String MUTEX_LIMIT_TYPE_ACTIVE_PRODUCT_10 = "61";//用户存在xx活动产品，不能办理当前活动
    public static final String MUTEX_LIMIT_TYPE_ACTIVE_PRODUCT_610 = "610";//用户存在xx活动产品（无论是否已经失效），不能办理当前活动

    public static final String MUTEX_LIMIT_TYPE_BRAND = "62";//用户是XX品牌，不能办理当前活动
    public static final String MUTEX_LIMIT_TYPE_GPON_DESTORY = "620";//用户已办理GPON宽带预约拆机，不能办理当前活动

    public static final String MUTEX_LIMIT_TYPE_PRODUCT = "63";//用户是XX产品，不能办理当前活动

    public static final String MUTEX_LIMIT_TYPE_DISCNT = "64";//用户有XX优惠，不能办理当前活动
    public static final String MUTEX_LIMIT_TYPE_DISCNT_HIS = "640";//用户有XX优惠，xx优惠所在自然年内不能办理当前活动
    public static final String MUTEX_LIMIT_TYPE_DISCNT_KD = "641";//用户的宽带号码下有XX优惠，不能办理当前活动
    public static final String MUTEX_LIMIT_TYPE_DISCNT_SPEC = "642";//用户有当前生效或下月生效的XX优惠，不能办理当前活动
    //add by zhangxing3 forBUG20190326102924优化宽带1+活动办理问题
    public static final String MUTEX_LIMIT_TYPE_DISCNT_KDYJ = "643";//用户有当前生效且下月生效的XX优惠，不能办理当前活动（该优惠本月底结束时，还是可以办理该活动的。）
    //add by zhangxing3 forBUG20190326102924优化宽带1+活动办理问题
    //add by zhangxing3 for REQ201903050005订咪咕会员享三个月话费优惠，流量月月送营销活动
    public static final String MUTEX_LIMIT_TYPE_PLATSVC_BIZ_TYPE_CODE = "72";//户近6个月内未订购过咪咕视频钻石会员/咪咕阅读至尊全站包,才能办理该型营销活动；
    //add by zhangxing3 for REQ201903050005订咪咕会员享三个月话费优惠，流量月月送营销活动

    public static final String MUTEX_LIMIT_TYPE_FQY_ACTIVE_PRODUCT = "931";//用户办理签约类活动，是否存在非签约的互斥活动

    public static final String MUTEX_LIMIT_TYPE_QY_ACTIVE_PRODUCT = "932";//用户办理非签约活动，是否存在签约类的活动互斥

    public static final String DEPEND_LIMIT_TYPE_DISCNT = "65";//用户有XX时间前办理的YY优惠，才能办理该型营销活动；目标客户不做此判断，XX时间目前都是2050，先写死。

    public static final String DEPEND_LIMIT_TYPE_ALL_DISCNT = "68";//用户必须办理配置的所有优惠，才能办理该型营销活动
    
    public static final String DEPEND_LIMIT_TYPE_SERVICE = "69";//用户有XX服务，才能办理该型营销活动；

    public static final String DEPEND_LIMIT_TYPE_PLATSVC_BIZ_TYPE_CODE = "70";//用户有XX BIZ_TYPE_CODE 的平台服务，才能办理该型营销活动；

    public static final String DEPEND_LIMIT_TYPE_BRAND = "66";//用户是XX品牌，才能办理该型营销活动

    public static final String DEPEND_LIMIT_TYPE_PRODUCT = "67";//用户是XX产品，才能办理该型营销活动
    
    //add by zhangxing3 for 开学抢红包送终端活动开发需求--新增活动规则
    public static final String DEPEND_LIMIT_TYPE_PRODUCT_SPEC = "670";//用户证件号下的其他号码是XX产品，才能办理该型营销活动
    //add by zhangxing3 for 开学抢红包送终端活动开发需求--新增活动规则
    
    public static final String DEPEND_LIMIT_TYPE_DISCNT_TROOP = "71";//用户有XX时间前办理的YY优惠，才能办理该型营销活动；目标客户不做此判断，XX时间目前都是2050，先写死。

    public static final String DEPEND_LIMIT_TYPE_SN = "81";//用户是XX号段，才能办理该型营销活动

    public static final String DEPEND_LIMIT_TYPE_968 = "968";//用户已办理的活动不在可顺延期限内，不能办理本活动！

    public static final String DEPEND_LIMIT_TYPE_SALE_PRODUCT = "1587";//用户办理了XX活动，才能办理YY活动

    public static final String DEPEND_LIMIT_TYPE_1588 = "1588";//统一付费成员须办理[" + productName + "]活动才可办理此活动

    public static final String DEPEND_LIMIT_TYPE_OPEN_DATE = "1592";//用户入网xx天内，才能办理该活动
    public static final String DEPEND_LIMIT_TYPE_OPEN_DATE1593 = "1593";//用户入网xx天之外，才能办理该活动
    
    public static final String DEPEND_LIMIT_TYPE_955 = "955";//用户存在签约类且在155配置中的营销活动
    
    public static final String DEPEND_LIMIT_TYPE_956 = "956";//用户存在签约类且不在155配置中的营销活动
    
    public static final String DEPEND_LIMIT_TYPE_IMS = "6800";//用户有XX时间内办理IMS固话开户,才能办理该型营销活；
    
    public static final String MUTEX_LIMIT_TYPE_SALEACTIVE_HIS = "180";//用户XX天内办理过XX活动，不能再次办理

    //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 start
    public static final String DEPEND_LIMIT_TYPE_9155 = "9155";//用户已办理的活动 在办理的155活动的指定活动中,且不在可顺延期限内，不能办理本活动！
    //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101015 end

    //REQ202003180001 “共同战疫宽带助力”活动开发需求start
    public static final String MUTEX_LIMIT_TYPE_WIDNET_TYPE = "6001";//用户为XX宽带,不能办理该活动
    //REQ202003180001 “共同战疫宽带助力”活动开发需求end

    public static final String PRODUCT_INFO_EMPTY = "获取营销产品信息为空";

    public static final String ERROR_1 = "用户是一卡付多号副卡用户不可以办理本业务！";

    public static final String ERROR_2 = "该用户是一卡双号副卡或双卡统一付费业务副卡，不能办理本业务！";

    public static final String ERROR_3 = "该用户有JTZ套餐(移动公司员工套餐)，不能办理本业务！";

    public static final String ERROR_4 = "[预开未返单]用户不可以办理购机业务！";

    public static final String ERROR_5 = "未激活用户不能办理该业务！";
    public static final String ERROR_51 = "未激活用户才能办理该业务！";

    public static final String ERROR_6 = "只有实名制客户才能办理该业务!";

    public static final String ERROR_7 = "不是当月激活客户，不能办理该业务!";

    public static final String ERROR_8 = "该用户不归属目标客户群，不能办理该业务！";

    public static final String ERROR_9 = "移动公司员工不能办理该购机业务!";

    public static final String ERROR_10 = "已经参加过营销活动，不能办理该业务！";

    public static final String ERROR_11 = "用户没有加入VPN集团，不能办理本礼包业务！";

    public static final String ERROR_12 = "用户没有加入898集团，不能办理本礼包业务！";

    public static final String ERROR_13 = "不是老客户，不能办理本礼包业务！";

    public static final String ERROR_14 = "对不起，该用户的品牌不能办理本业务！";

    public static final String ERROR_15_1 = "该用户不归属目标集团，不能办理该业务！";

    public static final String ERROR_15_2 = "该用户不归属目标VPN集团，不能办理该业务！";

    public static final String ERROR_16 = "用户是代理商套餐(VPMN JPA)，不能办理该业务！";

    public static final String ERROR_18_1 = "用户是统一付费副卡用户不可以办理本业务！";

    public static final String ERROR_18_2 = "用户不是统一付费副卡用户不可以办理本业务！";

    public static final String ERROR_18_3 = "该用户的统一付费主号须办理[约定消费使用宽带]活动中的158元档或188元档！";

    public static final String ERROR_18_4 = "为用户统付的主卡在网年限不满一年不可办理本业务！";

    public static final String ERROR_19 = "用户办理过营销活动，不能重复办理！";

    public static final String ERROR_20 = "用户没有集团彩铃服务，不能办理本礼包业务！";

    public static final String ERROR_21 = "用户存在待签收的终端预售订单,业务办理不能继续！";
    
    public static final String ERROR_22 = "用户已经欠费不能办理该营销活动！";
    
    public static final String ERROR_23 = "集团成员不存在相关产品，不能办理本业务！";
    
    //用户有XX BIZ_TYPE_CODE 的平台服务，才能办理该型营销活动且存在配置1220；
    public static final String DEPEND_LIMIT_TYPE_1220 = "1220";
    
    /**
     * REQ201704140015_关于和路通业务营销活动开发需求
     * <br/>
     * 用户有指定的套餐,才能办理该营销活动20170513
     * @author zhuoyingzhi
     * @date 20170513
     */
    public static final String DEPEND_LIMIT_TYPE_DISCNT_20170513 = "20170513";
    
    public static final String ERROR_24 = "该用户的统一付费主号有多个，不能办理本业务！";
    
    public static final String ERROR_25 = "该用户的统一付费主号没有加入898集团，不能办理本业务！";
    
    public static final String ERROR_26 = "该用户的统一付费主号网龄小于3年，不能办理本业务！";
    
    public static final String ERROR_27 = "该用户的统一付费主号星级小于3星，不能办理本业务！";
    
    public static final String ERROR_28 = "该用户不是和路通集团产品成员，不能办理本业务！";
    
    public static final String ERROR_29 = "该用户的统一付费主号不是正常状态，不能办理本业务！";
    
    public static final String ERROR_30 = "该用户的统一付费主号集团不是ABC类集团，不能办理本业务！";


}
