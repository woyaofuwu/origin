
package com.asiainfo.veris.crm.order.soa.frame.bof.data.consts;

/**
 * @author Administrator
 */
public class BofConst
{

    public final static String ACT_BACK_FEE = "actBack"; // 退费

    public final static String OTHERFEE_AGENT = "agentFee"; // 代理商扣费(完工)

    public final static String OTHERFEE_FMY_TRANS = "fmyAcctTrans";// 家庭账户注销虚拟用户费用清退(完工)

    public final static String OTHERFEE_DIFF_TRANS = "diffTrans"; // 不同帐户转账(完工)

    public final static String OTHERFEE_SAME_TRANS = "sameTrans"; // 同帐户转账(登记)

    public final static String OTHERFEE_ROAMFEE_TRANS = "roamFeeTrans"; // 国漫费用同步类型(完工)
    
    public final static String OTHERFEE_ROAM_TRANS = "roamAcctTrans"; // 办理接口、客服办理国际长途转押金(登记)

    public final static String OTHERFEE_AGENT_DEDUCT = "agentDeductFee";// 代理商扣款(登记)

    public final static String OTHERFEE_END_ACTIVE = "endActive"; // 终止营销活动(完工)

    public final static String OTHERFEE_CAMPUS_TRANS = "campusAcctTrans";// 校园宽带费用转账(登记)

    public final static String ALL_EPARCHY = "ZZZZ";// 全省

    public final static String BUNDLE_BIDI = "BIDI";// 全业务家庭套餐

    public final static String CANCEL_TAG_CANCEL = "3";// 取消

    public final static String CANCEL_TAG_CANCELED = "1";// 被撤单

    /* 定单状态：0-未启动,1-已派发启动,2-已启动,3-启动失败,4-已派发再处理,5-再处理成功,6-再处理失败,7-人工挂起,8-人工激活,9-系统完工,A-人工取消,B-挂起等待,C-挂起等待激活,D-异常重试状态 */
    // public final static String SUBSCRIBE_STATE_
    public final static String CANCEL_TAG_NO = "0";// 正常工单

    public final static String CANCEL_TAG_UNDO = "2";// 返销

    public static String DEPOSIT_TYPE_GIFT = "1";// 单赠送

    public static String DEPOSIT_TYPE_NORMAL = "0";// 普通

    public static String ELEMENT_TYPE_CODE_CREDIT = "C";// 信用度

    public static String ELEMENT_TYPE_CODE_DISCNT = "D";// 优惠

    public static String ELEMENT_TYPE_CODE_PLATSVC = "Z";// 平台服务

    public static String ELEMENT_TYPE_CODE_PRODUCT = "P";// 产品

    public static String ELEMENT_TYPE_CODE_RIGHTS = "Q"; // 权益

    public static String ELEMENT_TYPE_CODE_SALEDEPOSIT = "A";// 预存元素

    public static String ELEMENT_TYPE_CODE_SALEGOODS = "G";// 实物

    public static String ELEMENT_TYPE_CODE_SCORE = "J";// 积分

    public static String ELEMENT_TYPE_CODE_SVC = "S";// 服务
    
    public static String ELEMENT_TYPE_CODE_PACKAGE = "K";// 包
    public static String ELEMENT_TYPE_CODE_RES = "R";// 资源

    public final static String FEE_MODE_ADVANCEFEE = "2";// 预存款

    public final static String FEE_MODE_FOREGIFT = "1";// 押金

    public final static String FEE_MODE_OPERFEE = "0";// 营业费

    public final static String FEE_STATE_NO = "0";// 未收费

    public final static String FEE_STATE_YES = "1";// 已收费

    public final static String MODIFY_TAG_ADD = "0";// 新增

    public final static String MODIFY_TAG_DEL = "1";// 删除

    public final static String MODIFY_TAG_INHERIT = "U";// 继承
    
    public final static String MODIFY_TAG_MIDDLE = "0_1";// 中间状态middle

    public final static String MODIFY_TAG_UPD = "2";// 修改

    public final static String MODIFY_TAG_MOVE = "6";// 营销活动时间前移

    public final static String MODIFY_TAG_FORCE_END = "7";// 营销活动元素强制终止

    public final static String MODIFY_TAG_USER = "USER";// 用户资料的

    public final static String NET_TYPE_CODE = "00";// 默认网别

    public final static String OLCOM_TAG_NOT_SEND = "0";// 不发指令

    public final static String OLCOM_TAG_SEND = "1";// 发指令

    public final static String PARAM_FILTER_TYPE_EXCEPTION = "3"; // 异常情况参数转换

    public final static String PARAM_FILTER_TYPE_IN = "1"; // 入参参数转换

    public final static String PARAM_FILTER_TYPE_OUT = "2"; // 出参参数转换

    public final static String PRE_TYPE_CHECK = "1";// 预受理校验

    public final static String PRE_TYPE_CONFRIM_TRADE = "3";// 确认受理

    public final static String PRE_TYPE_SMS_CONFIRM = "2";// 需二次短信确认

    public final static String PROCESS_TAG_SET = "0000000000000000000000000000000000000000";// 默认的process_tag_set字段值

    public static String PRODUCT_MODE_MAIN = "00";// 主产品

    public static String PRODUCT_MODE_SALE_ACTIVE = "02";// 营销活动

    public final static String RELATION_TYPE_CODE_BUNDLE = "BD";// 全业务捆绑relation_type_code

    public final static String ROLE_CODE_A = "1";// 主卡编码

    public final static String ROLE_CODE_B = "2";// 副卡编码

    public final static String RULE_TAG_AFTER = "2";// 拼台账后规则

    public final static String RULE_TAG_BEFORE = "1";// 拼台账前规则

    public final static String SMS_PAY = "PaySms";// 成员付费关系变更二次短信

    public final static String SMS_REG = "RegSms";// 登记短信

    public final static String SMS_SEC = "SecSms";// 二次确认短信

    public final static String SMS_FIRSTCALL = "fCallSms";// 二次确认短信

    public final static String SMS_SUC = "SucSms";// 完工短信

    public final static String SUB_SYS_CODE_CSM = "CSM";// 子系统编码

    public final static String SUBSCRIBE_TYPE_BATCH_BOOK = "101";// 批量工单预约执行

    public final static String SUBSCRIBE_TYPE_BATCH_NOW = "100";// 批量工单立即执行

    public final static String SUBSCRIBE_TYPE_BATCH_PF_FILE = "150";// 批量指令文件

    public final static String SUBSCRIBE_TYPE_CREDIT_NOW = "200";// 信控工单立即执行

    public final static String SUBSCRIBE_TYPE_NORMAL_BOOK = "1";// 普通工单预约执行

    public final static String SUBSCRIBE_TYPE_NORMAL_NOW = "0";// 普通工单立即执行

    public final static String SUBSCRIBE_TYPE_PBOSS_NOW = "300";// PBOSS工单立即执行

    public final static String TRADE_OPER_FAMILY_CREATE = "CREATE";

    public final static String TRADE_OPER_FAMILY_UPDATE = "UPDATE";

    public final static String TRADE_OPER_FAMILY_DESTROY = "DESTROY";

    public final static String TRADE_TYPE_CODE_AccountBankBinding = "3700"; // 卡通协议签约信息查询

    public final static String TRADE_TYPE_CODE_BUS_CARD_INPUT = "1232"; // 公充IC卡充值

    public final static String TRADE_TYPE_CODE_CARDSALE = "548"; // 开户佣金座扣登记

    public final static String TRADE_TYPE_CODE_CREATEUSERCLUSTER = "104"; // 家庭网创建

    public final static String TRADE_TYPE_CODE_CTTNET_BIND = "1564"; // 铁通融合-捆绑受理

    public final static String TRADE_TYPE_CODE_CTTNET_CONTINUE = "1562"; // 铁通融合-宽带续费

    public final static String TRADE_TYPE_CODE_CTTNET_CREATE = "1560"; // 铁通融合-宽带开户

    public final static String TRADE_TYPE_CODE_CTTNET_DESTROY = "1561"; // 铁通融合-宽带销户

    public final static String TRADE_TYPE_CODE_CTTNET_REVERSAL = "1563"; // 铁通融合-宽带冲正

    public final static String TRADE_TYPE_CODE_CTTWIRELESS_STOP = "1597"; // 铁通融合-无线座机停机

    public final static String TRADE_TYPE_CODE_DMALLBUSI = "2701"; // DM业务通用

    public final static String TRADE_TYPE_CODE_FAMILY = "250";// 亲情业务

    public final static String TRADE_TYPE_CODE_FAMILY_QQT = "252";// 亲情带终端业务

    public final static String TRADE_TYPE_CODE_FAMILY_SPE_STOP = "661";// 亲情资料终止

    public final static String TRADE_TYPE_CODE_FAMILYACCOUNTDESTROY = "3527"; // 家庭账户注销

    public final static String TRADE_TYPE_CODE_FAMILYACCOUNTMANAGE = "3526"; // 家庭账户创建

    public final static String TRADE_TYPE_CODE_FRIENDTRADE_CREATE = "3520"; // NG1家庭网创建

    public final static String TRADE_TYPE_CODE_FRIENDTRADE_MANAGECG = "3521"; // NG1家庭网管理，CG库虚拟用户工单登记

    public final static String TRADE_TYPE_CODE_FRIENDTRADE_DESTROY = "3523"; // NG1家庭网注销

    public final static String TRADE_TYPE_CODE_FRIENDTRADE_MANAGE = "3524"; // NG1家庭网管理

    public final static String TRADE_TYPE_CODE_MODIFYACCTINFO = "80";// 修改账户资料业务类型

    public final static String TRADE_TYPE_CODE_MODIFYCUSTINFO = "60";// 修改客户资料业务类型

    public final static String TRADE_TYPE_CODE_MODIFYPOSTINFO = "90";// 修改邮寄资料业务类型

    public final static String TRADE_TYPE_CODE_MODIFYUSERINFO = "70";// 修改用户资料业务类型

    public final static String TRADE_TYPE_CODE_ONECARDNCODESSALE = "320";// 一卡双号登记

    public final static String TRADE_TYPE_CODE_PAYRELAADVANCE = "170";// 高级付费关系变更

    public final static String TRADE_TYPE_CODE_PAYRELAADVCANCLE = "171";// 高级付费关系变更取消

    public final static String TRADE_TYPE_CODE_PAYRELANORMAL = "160";// 普通付费关系变更

    public final static String TRADE_TYPE_CODE_RETURNMONEY = "2200"; // 酬金返还

    public final static String TRADE_TYPE_CODE_SALEACTIVE = "240";// 营销活动业务类型

    public final static String TRADE_TYPE_CODE_SpecDiscntDeal = "152";// 特殊优惠 变更

    public final static String TRADE_TYPE_CODE_TERMINALONLY = "253";// 裸机销售登记

    public final static String TRADE_TYPE_CODE_USERDISCNTEND = "153"; // 用户优惠特殊终止

    public final static String TRADE_TYPE_WIRELESSFAXOPEN = "640"; // 无线传真

    public final static String TRADE_TYPE_CODE_CLOSESMS = "880"; // 停短信服务

    public final static String TRADE_TYPE_CODE_RENTMOBILETRADE = "242"; // 租机业务

    public final static String TRADE_TYPE_CODE_RENTMOBILEBACK = "243"; // 租机退租

    public final static String TRADE_TYPE_CODE_RENTBALANCE = "249"; // 租机结算

    public final static String PAY_REMIND = "PayRemind"; // 扣费提醒

    public final static String PLAT_SVC_SEC = "PlatSvcSec"; // 平台业务二次确认

    public final static String SPEC_SVC_SEC = "SpecSvcSec";// 个人业务特殊服务确认
    
    public final static String SVC_SEC = "SvcSec";// 个人业务服务确认

    public final static String SEC_TYPE_375 = "OneCardMultiSnSec";// 一卡多号业务（影号）二次短信确认分类标识

    public final static String GRP_BUSS_SEC = "GrpBussSec";// 集团业务二次确认

    public final static String GRP_BUSS_PRE = "GrpBussPre";// 集团业务预约

    public final static String WLAN_PRE_CARD = "WlanPreCard";// wlan预售卡

    public final static String ENTITY_CARD = "EntityCard";// 实体卡

    public final static String EXP_REMIND = "ExpRemind";// 到期提醒
    
    public final static String EXP_TOPSET_ACTIVE = "ExpTopSetActive";// 魔百和权益活动到期
    
    public final static String MOSP_CANCEL = "MOSP_CANCEL";// 和多号取消

    public final static String SCHOOL_SALE = "SchoolSale";// 校园营销

    public final static String IS_NEED_PF_NO = "0";// 不需要

    public final static String IS_NEED_PF_YES = "1";// 需要

    public final static String ORDER_KIND_CODE_MUTIL_TRADE = "1";

    public final static String OCS_SIGN_ADD = "0";// OCS签约

    public final static String OCS_SIGN_DEL = "1";// OCS去签约

    public final static String EXEC_TIME_NOW = "0";// 立即执行

    public final static String EXEC_TIME_NEXTMONTH = "1";// 下月执行

    public final static String MSALE_SEC_TYPE = "MSALE_PRE";// 电话经理预售受理

    public final static String PAY_MONEY_CODE_BY_HDFK = "L";

    public final static String EXPIRE_TYPE_SYNC_USER = "SyncUserExp"; // 用户资料到期处理类型

    public final static String EXPIRE_TYPE_PRODUCT = "ProductExp"; // 产品变更到期处理类型
    
    public final static String EXPIRE_TYPE_PRODUCT_BOOK = "ProductBookExp"; // 产品预约变更到期消息提醒
    
    public final static String EXPIRE_TYPE_WIDENETPRODUCT = "WidenetProductExp"; // 宽带产品变更到期处理类型

    public final static String EXPIRE_TYPE_WIDENETSTOP = "WidenetStopExp"; // 宽带报停到期处理类型
    
    public final static String EXPIRE_TYPE_WIDENETDESTROY = "WidenetDestroyExp"; // 宽带拆机处理类型

    public final static String EXPIRE_TYPE_WLANRESUME = "WLAN"; // WLAN封顶暂停恢复

    public final static String SUBMIT_TYPE_SHOPPING_CART = "addShoppingCart";// 购物车

    public final static String SUBSCRIBE_TYPE_SHOPPING_CART = "600";// 购物车预约工单
    
    public final static String EXPIRE_TYPE_CREATE_USER = "CreateUserExp"; // 开户到期处理类型
    
    public final static String EXPIRE_TYPE_RESERVATION_SVC = "ReserVationSVCExp"; // 预约优惠到期处理类型
    
    public final static String EXPIRE_TYPE_END_PLATSVC = "PlatSvcExpire"; // 平台服务月底失效
    
    public final static String EXPIRE_TYPE_CHANGE_CUSTOWNER = "ChangeCustExpire"; // 过户到期处理类型 
    
    public final static String EXPIRE_TYPE_ONEPSPTIDMORENAME = "OnePsptIdMoreNameExp"; // 一证多名客户资料变更处理类型
    
    public final static String EXPIRE_TYPE_ACTIVEUSEREXP = "ActiveUserExp"; // O2O激活处理类型

    public final static String OFFER_REL_TYPE_COM = "C";
    
    public final static String OFFER_REL_TYPE_LINK = "L";
    
    public final static String WIDENET_TYPE_FTTB = "1";    //宽带用户类型  移动FTTB
    
    public final static String WIDENET_TYPE_ADSL = "2";    //宽带用户类型 ADSL
    
    public final static String WIDENET_TYPE_FTTH = "3";    //宽带用户类型 移动FTTH
    
    public final static String WIDENET_TYPE_SCHOOL = "4";    //宽带用户类型 校园宽带
    
    public final static String WIDENET_TYPE_TTFTTH = "5";    //宽带用户类型 铁通FTTH
    
    public final static String WIDENET_TYPE_TTFTTB = "6";    //宽带用户类型  铁通FTTB

	public static final String ORDER_PLAT = "ORDER_PLAT";    //订购增值服务
    public static final String NO_PHONE_WIDE_UNION_PAY_CODE = "41000";//一机多宽无手机统付账务编码
    
    public final static String PF_COMPLAIN_VISUALIZATION_TYPE_ID = "PF_COMPLAIN_VISUALIZATION";
    
    public final static String PF_COMPLAIN_VISUALIZATION_ACTION_FINISH_SUCCESS = "0";
    
    public final static String PF_COMPLAIN_VISUALIZATION_ACTION_FINISH_FAIL = "1";

    public final static String PF_COMPLAIN_VISUALIZATION_ACTION_CANCEL = "2";
    
    public final static boolean PF_COMPLAIN_VISUALIZATION_FINISH_SUCCESS = true;
    
    public final static boolean PF_COMPLAIN_VISUALIZATION_FINISH_FAIL = false;
    
    public final static String PF_COMPLAIN_VISUALIZATION_IS_MAIN_PROD = "Y";

    public final static String PF_COMPLAIN_VISUALIZATION_IS_NOT_MAIN_PROD = "N";
    

}
