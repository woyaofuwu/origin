
package com.asiainfo.veris.crm.order.pub.consts;

/**
 * 所有个人业务常量在此定义
 * 
 * @author liuke
 */
public class PersonConst
{

    // 查询类型
    public static final String QUERY_TYPE_NORMAL = "1";// 普通查询

    public static final String QUERY_TYPE_REPORT = "2";// 日报表查询

    public static final String QUERY_TYPE_REPORT_BETWEEN = "3";// 时段表查询

    public static final String QUERY_TYPE_SUMMARY_BETWEEN = "4";// 汇总报表查询

    public static final String QUERY_TYPE_SHEET_BETWEEN = "5";// 清单报表查询

    /** ***********************************************业务类型编码定义******************************************************** */
    public static final String TRADE_TYPE_CODE_CREATE_PERSON_USER = "10";// 业务类型编码

    public static final String TRADE_TYPE_CODE_CREATE_FIXED_USER = "2002";// 固网用户开户业务类型

    public static final String TRADE_TYPE_CODE_CREATE_TD_PERSON_USER = "3820";// 无线固话用户开户业务类型

    /**
     * ***********************************************错误描述定义************************************************************
     */
    public static final String CHECK_MPHONE_SUCCESSFUL = "MPHONE_SUCCESSFUL";// 号码校验成功

    public static final String CHECK_MPHONE_FAILED = "MPHONE_FAILED";// 号码校验成功

    public static final String CHECK_SIM_CARD_NO_SUCCESSFUL = "SIM_CARD_NO_SUCCESSFUL";// sim卡检验成功

    public static final String CHECK_SIM_CARD_NO_FAILED = "SIM_CARD_NO_FAILED";// 号码校验成功

    public static final String CHECK_ALL_SUCCESSFUL = "ALL_SUCCESSFUL";// 号码与sim卡都检验成功

    public static final String CHECK_ALL_FAILED = "ALL_FAILED";// 号码与sim卡任一检验失败

    public static final String ONLY_FOR_BACKOPEN_TRADE = "该号码只能用于返单开户！";// 号码只能用于返单开户

    public static final String IS_NOT_FOR_OPEN_TRADE = "该号码不可以开户！";// 号码不可以开户

    public static final String IS_NOT_EXISTS_PRODUCT_OR_PACKAGE = "号码绑定活动标识或活动包标识不存在！";// 产品标识或包标识不存在

    public static final String IS_NOT_EXISTS_PACKAGE_ELEMENTS = "获取活动包内元素无数据！";// 获取包内元素无数据

    public static final String EXISTS_SINGLE_AND_MULTE_PRODUCT = "不能存在同时绑定一个和多个产品！";

    public static final String NO_FOUND_DATA_IN_RES_TABLE = "该用户在资源表没记录，请检查数据！";

    public static final String MAX_OPEN_NUM_IN_CUSTPSPT = "该客户已达最大开户限制数，不能再次使用该证件办理开户业务！";

    public static final String SAME_TO_CITY_CODE_IN_RE_OPEN = "登录员工业务区必须与二次开户号码业务区一致！";

    public static final String EPARCHY_IS_MUST_BE_DIF_IN_PROV_REMOTE_OPEN = "号码归属不能与当前操作地市一致！";

    public static final String EPARCHY_IS_MUST_BE_DIF_IN_PROV_OPEN = "号码归属必须与当前操作地市一致！";

    public static final String IS_EXISTS_FIXED_SN = "该固网号码已经存在，请重新输入！";

    public static final String IS_NOT_FOR_OPENUSER_WLW147 = "您没有权限开户147号段的号码！";
    
    public static final String IS_NOT_FOR_OPENUSER_NUMBER = "开户号码不在该业务允许的开户号段中！";
    
    public static final String TEST_NUM_NOT_ALLOW_OPEN = "测试号码不允许开户！";
    
    public static final String BEAUTY_NUM_NOT_ALLOW_OPEN = "吉祥号码不允许开户！";
    /**
     * ***********************************************开户方式定义************************************************************
     */
    public static final String REUSE_OPEN = "REUSE_OPEN";// 用户开户(写卡，再利用)

    public static final String AGENT_OPEN = "AGENT_OPEN";// 代理商开户

    public static final String NETSEL_OPEN = "NETSEL_OPEN";// 网上选号开户

    public static final String REMOTE_AGENT_OPEN = "REMOTE_AGENT_OPEN";// 代理商远程写卡开户

    public static final String PROV_REMOTE_OPEN = "PROV_REMOTE_OPEN";// 省内异地开户(远程写卡)

    public static final String FIXED_NET_OPEN = "FIXED_NET_OPEN";// 固网用户开户

    public static final String IOT_OPEN = "IOT_OPEN";// 物联网开户

    public static final String TD_OPEN = "TD_OPEN";// 物联网开户

    public static final String M2M_NET_TYPE_CODE = "07"; // 物联网网别

    public static final String TD_NET_TYPE_CODE = "18"; // 无线固话网别

    public static final String FIX_TEL_NET_TYPE_CODE = "00"; // 固话网别

    public static final String INTER_ROAM_DAY_PACKAGE_ID = "99990000";// 国际漫游数据流量日套餐包ID

    public static final String INTER_ROAM_PROVINCE = "8981";// 受理省

    public static final String INTER_ROAM_SVC = "19";// 国漫服务

    /**
     * ***********************************************租机业务定义************************************************************
     */
    public final static String TRADE_TYPE_CODE_RENTMOBILETRADE = "242"; // 租机业务

    public final static String TRADE_TYPE_CODE_RENTMOBILEBACK = "243"; // 租机退租

    public final static String TRADE_TYPE_CODE_RENTBALANCE = "249"; // 租机结算

    /**
     * ***********************************************垃圾短信定义************************************************************
     */
    public static final String STATE_NORMAL = "01";// 待处理

    public static final String STATE_FEEDBACK = "02";// 回复

    public static final String STATE_UNTREAD = "03";// 退回

    public static final String STATE_HASTEN = "04";// 催办

    public static final String STATE_ARCH = "0A";// 归档

    public static final String STATE_NOTICE = "0B";// 通知

    public static final String STATE_MSG = "0B";// 通知

    public static final String STATE_HEAD = "00";// 总部派发待回复

    public static final String PROVINCE_CODE = "898";// 归属省为总部

    public static final String RUBBISH_SMS_TRADE = "1023";

    public static final String CLOSE_SMS_TRADE = "880";

    /**
     * ***********************************************产品变更定义************************************************************
     */
    public static final String FIRST_MONTH_FREE_DISCNT_DAY_27 = "27";// 首月免费体验优惠办理时特殊处理时间点

    public static final String FIRST_MONTH_FREE_DISCNT_DAY_20 = "20";// 首月免费体验优惠办理时特殊处理时间点

    public static final String IS_HWOCS_USER_YES = "1"; // 是华为ocs用户

    public static final String IS_HWOCS_USER_NO = "0"; // 不是华为ocs用户

    public static final String PAYRELANORCHG_CHANGE_ALL = "0"; // 将原帐户下所有用户都转到新帐户下

    public static final String DISCNT_TYPE_5 = "5"; // GPRS优惠类型【TD_S_DISCNT_TYPE = 5】

    public static final String DISCNT_TYPE_7 = "7"; // 闲时GPRS优惠类型【TD_S_DISCNT_TYPE = 7】

    public static final String DISCNT_TYPE_LLCX = "LLCX"; // 流量促销优惠类型【TD_S_DISCNT_TYPE = LLCX】

    public static final String DISCNT_TYPE_Q = "Q"; // 彩铃套餐优惠类型【TD_S_DISCNT_TYPE = Q】

    public static final String DISCNT_TYPE_R = "R"; // 新校园卡分区套餐优惠类型【TD_S_DISCNT_TYPE = R】

    public static final String DISCNT_TYPE_BX = "BX"; // 必选包优惠类型主要用于接口【TD_S_DISCNT_TYPE = BX】

    public static final String DISCNT_TYPE_CV = "CV"; // 两城一家 非常假期类型主要用于接口【TD_S_DISCNT_TYPE = CV】

    public static final String GPRS_DEFAULT_DISCNT_CODE = "902"; // GPRS标准优惠编码

    public static final String SERVICE_ID_14 = "14";// 国内（不含港澳台）长途

    public static final String SERVICE_ID_15 = "15";// 国际及港澳台长途

    public static final String SERVICE_ID_18 = "18";// 国内（不含港澳台）漫游

    public static final String SERVICE_ID_19 = "19";// 国际及港澳台漫游

    public static final String SERVICE_ID_22 = "22";// GPRS服务

    public static final String SERVICE_ID_QINQIN = "830"; // 亲亲网服务

    public static final String SERVICE_ID_QINQIN_MEMBER = "831";// 亲亲网成员服务

    /**
     * ***********************************************固话业务定义************************************************************
     */
    public final static String TRADE_TYPE_CODE_FIX_TEL_STOP = "9707"; // 固话报停

    public final static String TRADE_TYPE_CODE_FIX_TEL_OPEN = "9708"; // 固话报开

    public final static String TRADE_TYPE_CODE_FIX_TEL_OFFICE_STOP = "9734"; // 固话局方停机

    public final static String TRADE_TYPE_CODE_FIX_TEL_OFFICE_OPEN = "9735"; // 固话局方开机

    public final static String TRADE_TYPE_CODE_FIX_TEL_DEMOLISH = "9705"; // 固话拆机
    
    public static String ELEMENT_TYPE_CODE_CREDIT = "C";// 信用度

    public static String ELEMENT_TYPE_CODE_DISCNT = "D";// 优惠

    public static String ELEMENT_TYPE_CODE_PLATSVC = "Z";// 平台服务

    public static String ELEMENT_TYPE_CODE_PRODUCT = "P";// 产品

    public static String ELEMENT_TYPE_CODE_SALEDEPOSIT = "A";// 预存元素

    public static String ELEMENT_TYPE_CODE_SALEGOODS = "G";// 实物

    public static String ELEMENT_TYPE_CODE_SCORE = "J";// 积分

    public static String ELEMENT_TYPE_CODE_SVC = "S";// 服务

    /**
     * ***********************************************权益中心定义************************************************************
     */
    public final static String BENEFIT_AIRPORT="1";

    public final static String BENEFIT_AIRPORT_FREE_PARKING="717171";

    public final static String BENEFIT_TAG="BENEFIT_TAG";

    public final static String BENEFIT_RIGHT_USE_RECORD="RIGHT_USE_RECORD";

    public final static String BENEFIT_RIGHT_NUM_CONFIG="RIGHT_NUM_CONFIG";

    public final static String BENEFIT_TYPE_CONFIG="7171";

    public final static String BENEFIT_CODE_CONFIG="7172";

    public final static String BENEFIT_CONDITION_CONFIG="7173";

    public final static String BENEFIT_STATIC_CONFIG="7174";

    public final static String BENEFIT_STATIC_PARTNER="PARTNER";

    public final static String BENEFIT_STATIC_SIGN="SIGN";

    public final static String BENEFIT_STATIC_ENTERPRISE="ENTERPRISE";

    public final static String BENEFIT_STATIC_FREETIME="FREETIME";

    public final static String BENEFIT_TINGJD_BOOKADD_SERVICE="tcorderbook.reservation.add";

    public final static String BENEFIT_TINGJD_BOOKADD_URL="crm.ABILITY.tcorderbook.reservation.add";

    public final static String BENEFIT_TINGJD_BOOKREMOVE_SERVICE="tcorderbook.reservation.remove";

    public final static String BENEFIT_TINGJD_BOOKREMOVE_URL="crm.ABILITY.tcorderbook.reservation.remove";




}
