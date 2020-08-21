
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade;

public class CreatePersonaseConst
{
    /** ***********************************************业务类型编码定义******************************************************** */
    public static final String TRADE_TYPE_CODE_CREATE_PERSON_USER = "10";// 业务类型编码

    public static final String TRADE_TYPE_CODE_CREATE_FIXED_USER = "2002";// 固网用户开户业务类型

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

    public static final String M2M_NET_TYPE_CODE = "07"; // 物联网网别
}
