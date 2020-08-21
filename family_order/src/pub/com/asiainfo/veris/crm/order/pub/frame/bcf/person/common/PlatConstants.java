
package com.asiainfo.veris.crm.order.pub.frame.bcf.person.common;

public final class PlatConstants
{
    public static final boolean IS_ENCRYPT_PASSWORD = true; // 是否加密WLAN密码

    public static final String OPER_MODIFY_PASSWORD = "03"; // 密码修改

    public static final String OPER_PAUSE = "04"; // 暂停

    public static final String OPER_RESTORE = "05"; // 恢复

    public static final String OPER_ORDER = "06"; // 订购

    public static final String OPER_CANCEL_ORDER = "07"; // 退订

    public static final String OPER_USER_DATA_MODIFY = "08"; // 用户资料变更

    public static final String OPER_RESET = "09"; // 密码重置

    public static final String OPER_ORDER_TC = "10"; // 套餐订购

    public static final String OPER_CANCEL_TC = "11"; // 套餐退订

    public static final String OPER_CHANGE_TC = "12"; // 套餐变更

    public static final String OPER_CANCEL_ALL = "99";// 全退订

    public static final String OPER_SP_CANCEL_ALL = "89";// SP全退订

    public static final String OPER_CHANGE_NUMBER = "13";// 改号

    public static final String OPER_ORDER_PLAY = "14";// 点播

    public static final String OPER_BIND = "15"; // 绑定

    public static final String OPER_PE_ACTIVATE = "16"; // PE激活

    public static final String OPER_TC_USEROUT_NOTICE = "16"; // WLAN套餐用尽通知

    public static final String OPER_BOOKING = "17"; // 预约

    public static final String OPER_CANCEL_BOOKING = "18";// 取消预约

    public static final String OPER_ADD_MONEY = "16";// 充值

    public static final String OPER_CONTINUE_ORDER = "26";// 续订

    public static final String OPER_SERVICE_OPEN = "90";// 服务开关开

    public static final String OPER_SERVICE_CLOSE = "91";// 服务开关关

    public static final String OPER_LOSE = "19";// 挂失

    public static final String OPER_UNLOSE = "20";// 解挂

    // 平台类型
    public static final String PLAT_17201 = "01";// 17201业务

    public static final String PLAT_WLAN = "02";// WLAN业务

    public static final String PLAT_WAP = "03";// WAP业务

    public static final String PLAT_SMS = "04";// 短信业务

    public static final String PLAT_MMS = "05";// 彩信业务

    public static final String PLAT_LBS = "07";// LBS业务

    public static final String PLAT_EMAIL = "08";// EMAIL业务

    public static final String PLAT_HAND_COMPUTER = "09";// 掌上电脑

    public static final String PLAT_PIM = "10";// PIM业务

    public static final String PLAT_MOBILEPHONE_CARTOON = "13";// 手机动画

    public static final String PLAT_FLOWMEDIA = "14";// 流媒体

    public static final String PLAT_PUSH_EMAIL = "15";// pushemail业务

    public static final String PLAT_MOBILEPHONE_EMAIL = "16";// 手机邮箱

    public static final String PLAT_BJ_DOWNLOAD = "17";// 北京通用下载

    public static final String PLAT_GD_DOWNLOAD = "17";// 广东通用下载

    public static final String PLAT_WIRELESS_MUSIC = "19";// 无线音乐

    public static final String PLAT_MOBILEPHONE_MAP = "20";// 手机地图

    public static final String PLAT_MOBILEPHONE_BURSE = "21";// 手机钱包

    public static final String PLAT_FETION = "23";// 飞信业务

    public static final String PLAT_CMRB_Bing = "B3";// 手机阅读绑定

    public static final String PLAT_CMRB = "60";// 手机阅读

    public static final String PLAT_MUSIC_DOWNLOAD = "25";// 全网音乐下载

    public static final String PLAT_FARMING_CREDIT_ALL = "27";// 农信通业务

    public static final String PLAT_GAME = "28";// 游戏平台

    public static final String PLAT_VIDEO_MESSAGE = "30";// 视频留言业务

    public static final String PLAT_MEDIA_COLORRING = "31";// 多媒体彩铃

    public static final String PLAT_VIDEO_MEETING = "32";// 视频会议

    public static final String PLAT_VIDEO_SHARE = "33";// 视频共享

    public static final String PLAT_E3G = "37";// 随E行3G上网本

    public static final String PLAT_TYGM = "67";// 随E行3G上网本

    public static final String PLAT_12580 = "52";// 12580业务

    public static final String PLAT_CMMB = "53";// 手机电视

    public static final String PLAT_MOBILEPHONE_PAY = "54";// 手机支付

    public static final String PLAT_WLAN_EDU = "92";// 校园WLAN业务

    public static final String PLAT_BLACK_BERRY = "99";// BLACK BERRY业务

    public static final String PLAT_ALL = "FF";// 全业务(梦网开关)

    // 状态
    public static final String STATE_OK = "A";// 正常

    public static final String STATE_PAUSE = "N";// 暂停

    public static final String STATE_CANCEL = "E";// 退订

    public static final String STATE_LOSE = "L";// 挂失

    // 产品ID和包ID
    public static final String PRODUCT_ID = "50000000";

    public static final String PACKAGE_ID = "-1";//50000000
    
    public static final String RULE_UCA = "RULE_UCA";

    public static final String RULE_PLATSVC_TRADE = "RULE_PLATSVC_TRADE";

    public static final String RULE_PLATSVC = "RULE_PLATSVC";

    public static final String TF_B_TRADE_PLATSVC = "TF_B_TRADE_PLATSVC";

    public static final String SMS_TYPE_SECOND_CONFIRM = "2";// 平台业务短信类型，二次确认短信

    public static final String SMS_TYPE_ORDER_FINISH = "0";// 平台业务短信类型，完工短信
    
    public static final String OFFER_REL_TYPE = "L";//C-构成关系,组关系;L-连带关系

}
