
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

/**
 * 接口通用字段 外部接口传送过来的字段有改动只需要修改此文件 数组【0】代表字段名，数组【1】代表默认值 无特殊说明默认值均取空
 *
 * @author chenlei
 * @date 2009-5-6
 */
public class IntfField
{

    /**
     * 全网ADC 个人签约关系同步 返回信息
     *
     * @author chenlei
     * @date 2009-5-18
     */
    public static class AllADCGrpMemBizRet
    {
        // public final static String[] ERR00 = {"0","受理成功"};
        public final static String[] ERR01 =
        { "ADM_01", "操作代码错误。" };

        public final static String[] ERR02 =
        { "ADM_02", "业务代码错误。" };

        public final static String[] ERR03 =
        { "ADM_03", "用户无签约关系。" };

        public final static String[] ERR04 =
        { "ADM_04", "生效时间错误" };

        public final static String[] ERR05 =
        { "ADM_05", "用户已在白名单内" };

        public final static String[] ERR06 =
        { "ADM_06", "用户不在白名单内" };

        public final static String[] ERR07 =
        { "ADM_07", "用户已在黑名单内" };

        public final static String[] ERR08 =
        { "ADM_08", "用户不在黑名单内" };

        public final static String[] ERR09 =
        { "ADM_09", "增值业务用户，不能进行黑白名单操作" };

        public final static String[] ERR10 =
        { "ADM_10", "该业务已终止" };

        public final static String[] ERR11 =
        { "ADM_11", "该业务已暂停" };

        public final static String[] ERR12 =
        { "ADM_12", "服务代码错误" };

        public final static String[] ERR98 =
        { "ADM_98", "落地方内部错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * 全网ADC EC业务信息同步 返回信息
     *
     * @author chenlei
     * @date 2009-5-17
     */
    public static class AllADCGrpUserBizRet
    {
        // public final static String[] ERR00 = {"0","成功"};
        public final static String[] ERR04 =
        { "ADU_04", "集团客户ID错误" };

        public final static String[] ERR05 =
        { "ADU_05", "业务代码代码错误" };

        public final static String[] ERR06 =
        { "ADU_06", "操作代码错误" };

        public final static String[] ERR10 =
        { "ADU_10", "服务代码错误" };

        public final static String[] ERR11 =
        { "ADU_11", "订购生效时间错误" };

        public final static String[] ERR12 =
        { "ADU_12", "EC订购多个SI提供的产品错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * BBOSS 成员列表签约关系同步 返回信息
     *
     * @author chenlei
     * @date 2009-5-17
     */
    public static class BbossGrpMemBizRet
    {
        public final static String[] ERR01 =
        { "BGM_01", "产品订单编码错误" };

        public final static String[] ERR02 =
        { "BGM_02", "订单来源错误" };

        public final static String[] ERR03 =
        { "BGM_03", "成员号码错误" };

        public final static String[] ERR04 =
        { "BGM_04", "操作类型错误" };

        public final static String[] ERR05 =
        { "BGM_05", "成员类型错误" };

        public final static String[] ERR06 =
        { "BGM_06", "成员群组号错误" };

        public final static String[] ERR07 =
        { "BGM_07", "期望生效时间错误" };

        public final static String[] ERR08 =
        { "BGM_08", "成员属性编码错误" };

        public final static String[] ERR09 =
        { "BGM_09", "成员属性名错误" };

        public final static String[] ERR10 =
        { "BGM_10", "成员属性值错误" };

        public final static String[] ERR11 =
        { "BGM_11", "产品订购关系编码错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * BBOSS 集团客户商品订单订购 返回信息
     *
     * @author chenlei
     * @date 2009-5-17
     */
    public static class BbossGrpUserBizRet
    {
        // public final static String[] ERR00 = {"0","受理成功"};//成功的错误编码所有报文统一用0
        public final static String[] ERR01 =
        { "BGU_01", "订单来源错误" };

        public final static String[] ERR02 =
        { "BGU_02", "EC集团客户编码错误" };

        public final static String[] ERR03 =
        { "BGU_03", "商品订单号错误" };

        public final static String[] ERR04 =
        { "BGU_04", "商品规格编号错误" };

        public final static String[] ERR05 =
        { "BGU_05", "商品实例ID/商品订购关系ID错误" };

        public final static String[] ERR06 =
        { "BGU_06", "主办省错误" };

        public final static String[] ERR07 =
        { "BGU_07", "商品级业务操作错误" };

        public final static String[] ERR08 =
        { "BGU_08", "操作类型错误" };

        public final static String[] ERR09 =
        { "BGU_09", "产品订单号错误" };

        public final static String[] ERR10 =
        { "BGU_10", "产品实例ID/产品订购关系ID错误" };

        public final static String[] ERR11 =
        { "BGU_11", "产品规格编号错误" };

        public final static String[] ERR12 =
        { "BGU_12", "产品关键号码错误" };

        public final static String[] ERR13 =
        { "BGU_13", "产品附件号码错误" };

        public final static String[] ERR14 =
        { "BGU_14", "联系人错误" };

        public final static String[] ERR15 =
        { "BGU_15", "联系电话错误" };

        public final static String[] ERR16 =
        { "BGU_16", "产品描述错误" };

        public final static String[] ERR17 =
        { "BGU_17", "服务开通等级ID错误" };

        public final static String[] ERR18 =
        { "BGU_18", "资费计划标识错误" };

        public final static String[] ERR19 =
        { "BGU_19", "产品级资费操作代码错误" };

        public final static String[] ERR20 =
        { "BGU_20", "资费描述错误" };

        public final static String[] ERR21 =
        { "BGU_21", "ICB参数编码错误" };

        public final static String[] ERR22 =
        { "BGU_22", "ICB参数名错误" };

        public final static String[] ERR23 =
        { "BGU_23", "ICB参数值错误" };

        public final static String[] ERR24 =
        { "BGU_24", "业务开展模式错误" };

        public final static String[] ERR25 =
        { "BGU_25", "支付省错误" };

        public final static String[] ERR26 =
        { "BGU_26", "支付省操作代码错误" };

        public final static String[] ERR27 =
        { "BGU_27", "产品级业务操作类型错误" };

        public final static String[] ERR28 =
        { "BGU_28", "产品属性代码错误" };

        public final static String[] ERR29 =
        { "BGU_29", "属性值错误" };

        public final static String[] ERR30 =
        { "BGU_30", "属性名错误" };

        public final static String[] ERR31 =
        { "BGU_31", "产品属性操作代码错误" };

        public final static String[] ERR32 =
        { "BGU_32", "套餐ID错误" };

        public final static String[] ERR33 =
        { "BGU_33", "套餐名称错误" };

        public final static String[] ERR34 =
        { "BGU_34", "套餐操作代码错误" };

        public final static String[] ERR35 =
        { "BGU_35", "商品资费计划标识错误" };

        public final static String[] ERR36 =
        { "BGU_36", "商品资费计划操作代码错误" };

        public final static String[] ERR37 =
        { "BGU_37", "商品资费描述错误" };

        public final static String[] ERR38 =
        { "BGU_38", "商品ICB参数编码错误" };

        public final static String[] ERR39 =
        { "BGU_39", "商品ICB参数名错误" };

        public final static String[] ERR40 =
        { "BGU_40", "商品ICB参数值错误" };

        public final static String[] ERR41 =
        { "BGU_41", "套餐生效规则错误" };

        public final static String[] ERR42 =
        { "BGU_42", "SI编码错误" };

        public final static String[] ERR43 =
        { "BGU_43", "产品属性组错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    // SERVICE_ID写死

    /**
     * 行业网关 个人订购信息同步、省内MAS 个人签约关系同步 返回信息
     *
     * @author chenlei
     * @date 2009-5-17
     */
    public static class IAGWGrpMemBizRet
    {
        // public final static String[] ERR00 = {"0","受理成功"};
        public final static String[] ERR01 =
        { "IGM_01", "操作代码错误。" };

        public final static String[] ERR02 =
        { "IGM_02", "业务代码错误。" };

        public final static String[] ERR03 =
        { "IGM_03", "用户无签约关系。" };

        public final static String[] ERR04 =
        { "IGM_04", "生效时间错误" };

        public final static String[] ERR05 =
        { "IGM_05", "用户已在白名单内" };

        public final static String[] ERR06 =
        { "IGM_06", "用户不在白名单内" };

        public final static String[] ERR07 =
        { "IGM_07", "用户已在黑名单内" };

        public final static String[] ERR08 =
        { "IGM_08", "用户不在黑名单内" };

        public final static String[] ERR09 =
        { "IGM_09", "增值业务用户，不能进行黑白名单操作" };

        public final static String[] ERR10 =
        { "IGM_10", "该业务已终止" };

        public final static String[] ERR11 =
        { "IGM_11", "该业务已暂停" };

        public final static String[] ERR12 =
        { "IGM_12", "服务代码错误" };

        public final static String[] ERR39 =
        { "IGM_39", "该号码已经销户或者停机" };

        public final static String[] ERR98 =
        { "IGM_98", "落地方内部错误" };

        public final static String[] ERR38 =
        { "IGM_38", "非省内移动号码,不能添加" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * 行业网关 企业订购信息同步 返回信息
     *
     * @author chenlei
     * @date 2009-5-17
     */
    public static class IAGWGrpUserBizRet
    {
        // public final static String[] ERR00 = {"0","受理成功"};
        public final static String[] ERR01 =
        { "IGU_01", "用户手机号码错误" };

        public final static String[] ERR02 =
        { "IGU_02", "操作编码错误" };

        public final static String[] ERR03 =
        { "IGU_03", "用户生效时间错误" };

        public final static String[] ERR04 =
        { "IGU_04", "业务代码错误" };

        public final static String[] ERR05 =
        { "IGU_05", "业务接入号错误" };

        public final static String[] ERR06 =
        { "IGU_06", "业务接入号的属性错误" };

        public final static String[] ERR07 =
        { "IGU_07", "用户无签约关系（业务代码、业务接入号和业务接入号的属性组合）" };

        public final static String[] ERR08 =
        { "IGU_08", "用户已在名单内" };

        public final static String[] ERR09 =
        { "IGU_09", "用户不在名单内" };

        public final static String[] ERR10 =
        { "IGU_10", "该业务已终止" };

        public final static String[] ERR11 =
        { "IGU_11", "该业务已暂停" };

        public final static String[] ERR96 =
        { "IGU_96", "信息代码错误" };

        public final static String[] ERR97 =
        { "IGU_97", "信息值错误" };

        public final static String[] ERR98 =
        { "IGU_98", "落地方内部错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * 操作状态
     *
     * @author chenlei
     * @date 2009-5-15
     */
    public static enum OPER_STATE
    {
        ADD("ADD"), // 新增
        DEL("DEL"), // 删除
        MODI("MODI"), // 修改
        ;

        public final String value;

        OPER_STATE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    /**
     * 操作类型编码(本系统定义的接口操作)
     *
     * @author chenlei
     * @date 2009-5-7
     */
    public static enum OperType
    {
        CreateGroupPro("CreateGroupPro"), // 集团产品受理
        ChangeGroupPro("ChangeGroupPro"), // 集团产品变更
        ChangeGroupProPayRela("ChangeGroupProPayRela"), // 集团产品付费关系变更
        DestroyGroupPro("DestroyGroupPro"), // 集团产品注销
        CreateGroupMem("CreateGroupMem"), // 集团成员新增
        ChangeGroupMemDiscnt("ChangeGroupMemDiscnt"), // 集团成员优惠变更
        ChangeGroupMemDatum("ChangeGroupMemDatum"), // 集团成员资料修改
        DestroyGroupMem("DestroyGroupMem"), // 集团成员退订
        BlackWhiteMem("BlackWhiteMem"), // 集团成员黑白名单业务(省外)
        AddFailmyNum("AddFailmyNum") // adc业务添加亲情号码 add by xusf
        ;

        public final String value;

        OperType(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    /**
     * 服务控制参数类
     *
     * @author chenlei
     * @date 2009-5-20
     */
    public static enum SERPAMCTRL
    {
        ADCUSER("adc.UserParamInfo"), // adc用户服务参数控制类
        ADCMEM("adc.MebSvcParamInfo"), // adc成员服务参数控制类
        MASUSER("mas.UserParamInfo"), // mas用户参数控制类
        MASMEM("mas.MebSvcParamInfo"), // mas成员参数控制类
        ;
        public final String value;

        SERPAMCTRL(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    /**
     * //省内ADC 个人签约关系同步(2.5版本)
     *
     * @author liaolc
     * @date 2014-7-8
     */
    public static class SubADCGrpMemBiz
    {
        // public final static String[] ERR00 = {"0","受理成功"};
        public final static String[] ERR01 =
        { "SAGM_01", "操作代码错误" };

        public final static String[] ERR02 =
        { "SAGM_02", "产品代码错误" };

        public final static String[] ERR03 =
        { "SAGM_03", "用户号码错误" };

        public final static String[] ERR04 =
        { "SAGM_04", "生效时间错误" };

        public final static String[] ERR05 =
        { "SAGM_05", "用户已在白名单内" };

        public final static String[] ERR06 =
        { "SAGM_06", "用户不在白名单内" };

        public final static String[] ERR07 =
        { "SAGM_07", "用户已在黑名单内" };

        public final static String[] ERR08 =
        { "SAGM_08", "用户不在黑名单内" };

        public final static String[] ERR09 =
        { "SAGM_09", "增值业务用户，不能进行黑白名单操作" };

        public final static String[] ERR10 =
        { "SAGM_10", "员工订购的产品不在其EC所订购的产品(包)中" };

        public final static String[] ERR98 =
        { "SAGM_98", "落地方内部错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * 省内ADC 个人签约关系同步、行业应用黑白名单同步业务 返回信息
     *
     * @author chenlei
     * @date 2009-5-17
     */
    public static class SubADCGrpMemBizRet
    {
        // public final static String[] ERR00 = {"0","受理成功"};
        public final static String[] ERR01 =
        { "SAM_01", "用户手机号码错误" };

        public final static String[] ERR02 =
        { "SAM_02", "操作编码错误" };

        public final static String[] ERR03 =
        { "SAM_03", "用户生效时间错误" };

        public final static String[] ERR04 =
        { "SAM_04", "业务代码错误" };

        public final static String[] ERR05 =
        { "SAM_05", "业务接入号错误" };

        public final static String[] ERR06 =
        { "SAM_06", "业务接入号的属性错误" };

        public final static String[] ERR07 =
        { "SAM_07", "用户无签约关系（业务代码、业务接入号和业务接入号的属性组合）" };

        public final static String[] ERR08 =
        { "SAM_08", "用户已在名单内" };

        public final static String[] ERR09 =
        { "SAM_09", "用户不在名单内" };

        public final static String[] ERR10 =
        { "SAM_10", "该业务已终止" };

        public final static String[] ERR11 =
        { "SAM_11", "该业务已暂停" };

        public final static String[] ERR96 =
        { "SAM_96", "信息代码错误" };

        public final static String[] ERR97 =
        { "SAM_97", "信息值错误" };

        public final static String[] ERR98 =
        { "SAM_98", "落地方内部错误" };
        // public final static String[] ERR99 = {"99","其它错误"};
    }

    /**
     * 子交易编码(外部系统定义的接口操作)
     *
     * @author chenlei
     * @date 2009-5-9
     */
    public static enum SubTransCode
    {
        BbossGrpUserBiz("BIP4B255_T4011004_1_0"), // BBOSS 集团客户商品订单订购
        BbossGrpMemBiz("BIP4B257_T4101034_1_0"), // BBOSS 成员列表签约关系同步
        AllMASGrpUserBiz("BBOSS_BOSS_ECBizInfo_1_0"), // 全网MAS EC业务信息同步
        AllMASGrpMemBiz("BBOSS_BOSS_ECMemBizInfo_1_0"), // 全网MAS 个人签约关系同步
        BbossGrpMemBatBiz("BBOSS_ORDER_1_0"), // BBOSS成员列表签约关系批量同步
        BbossOrderBiz("BIP4B256_T4011005_1_0"), // BBOSS向省BOSS发送开通工单接口
        BbossSignConfirmBiz("BIP4B257_T4101035_1_0"), // BBOSS向省BOSS发送签约关系同步确认交易
        BbossSignConfirmMenBiz("BIP4B257_T4101035_0_0"), // BBOSS向省BOSS发送成员签约关系同步确认交易

        BbossGrpManagerBiz("BIP4B259_T4011042_1_0"), // BBOSS 业务流程管理接口
        BbossGrpErrorBiz("BIP4B258_T4011041_1_0"), // BBOSS 集团客户商品订单处理失败通知
        BbossOrderStateBiz("BIP4B260_T4011060_1_0"), // 工单流转状态落地
        BbossAssistOrderBiz("BIP4B255_T4011064_1_0"), // BBOSS下发配合省协助业务（受理/预受理）

        M2MQueryPersonLeaveRealFeeBiz("BIPUSRBA_TUSRBA_1_0"), // M2M个人帐户余额查询
        M2MQueryECLeaveRealFeeBiz("BIPECBA_TECBA_1_0"), // M2M-EC账户余额查询
        M2MQueryUserInfoBiz("BIPUSRST_TUSRST_1_0"), // M2M个人查询
        M2MOrderMemberBiz("BIPM2MDN_TM2MDN_1_0"), // M2M物联网用户号码同步

        // 海南J2EE后 ADCMAS反向接口
        SubXXTGrpMemBiz("BIPXXT05_TX100005_1_0"),// 校讯通直连,新校讯通反向报文 , 原来是BIP4B773_T2101773_1_0
        SubADCGrpMemBiz("BIP4B773_T2101773_1_0"), // 省内ADC 3.0.1=> 2.9. 签约关系/黑白名单反向申请(ADC--BOSS) ,实时接口
        IAGWGrpMemBiz("BIP4B248_T4101025_1_0"), // 行业网关2.0 =>2.9. 行业应用黑白名单同步业务,实时接口
        SubXHKGrpMemBiz("BIPXHK04_TX100005_1_0"),
        ADCAddFamNum("EIDC_BOSS_1_0"),// adc添加亲情号码
        SubDCXGrpMemBiz("BIPDCX04_TX100007_1_0"),
        SubLKFGrpMemBiz("BIPLKF04_TX100007_1_0"),
        SubTXLGrpMemBiz("BIPTXL04_TX100007_1_0"),
        SubYKTGrpMemBiz("BIPYKT04_TX100007_1_0"),
        SubQJZGrpMemBiz("BIPQJZ04_TX100007_1_0"),
        SubQYXGrpMemBiz("BIPQYX04_TX100007_1_0")
        ;

        public final String value;

        SubTransCode(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    public static final String BBOSS_PRODUCT_ID = "6820";// BBOSS产品ID写死

    public static final String ALL_MAS_PRODUCT_ID = "8710";// 全网MAS产品ID写死

    public static final String ALL_MAS_SERVICE_ID = "871000";// 全网MAS

    // 集团产品受理 start
    public static final String PAM = "pam";// 个性化参数，为IData的格式

    public static final String[] GROUP_ID =
    { "GROUP_ID", "" };// 集团编码

    public static final String[] PRODUCT_ID =
    { "PRODUCT_ID", "" };// 产品ID

    public static final String[] SERIAL_NUMBER =
    { "SERIAL_NUMBER", "" };// 成员号码

    public static final String[] OPER_TYPE =
    { "OPER_TYPE", "" };// 操作类型编码

    public static final String[] TRADE_STAFF_ID =
    { "TRADE_STAFF_ID", "SUPERUSR" };// 员工标识

    public static final String[] NET_TYPE_CODE =
    { "NET_TYPE_CODE", "00" };// 网别

    public static final String[] IN_MODE_CODE =
    { "IN_MODE_CODE", "6" };// 接入渠道

    // 集团产品受理 end

    public static final String[] PO_NUMBER =
    { "PO_NUMBER", "" };// BBOSS商品ID

    public static final String[] PRODUCT_NUMBER =
    { "PRODUCT_NUMBER", "" };// BBOSS产品ID

    public static final String[] GOODS_INFO =
    { "GOODS_INFO", "[]" };// 商品信息

    public static final String[] PRODUCT_INFO =
    { "PRODUCT_INFO", "[]" };// 产品信息

    public static final String[] MEMBER_INFO =
    { "MEMBER_INFO", "[]" };// 成员信息

    public static final String[] DEVOLP_AREA_CODE =
    { "DEVOLP_AREA_CODE", "" };// 业务区

    public static final String[] DEVOLP_DEPART_ID =
    { "DEVOLP_DEPART_ID", "" };// 发展人

    public static final String[] USER_PASSWD =
    { "USER_PASSWD", "" };// 服务密码

    public static final String[] ROLE_CODE_B =
    { "ROLE_CODE_B", "0" };// 成员角色

    public static final String[] ANTI_INTF_FLAG =
    { "ANTI_INTF_FLAG", "0" };// 反向标记 1代表是反向接口

    public static final String[] DISCNTS =
    { "DISCNTS", "[]" };// 集团批量业务优惠信息

    // 错误代码
    public static final String[] SUUCESS_CODE =
    { "0", "受理成功" };// 所有报文受理成功统一用0

    public static final String[] DATABASE_ERR =
    { "98", "其他错误" };// 其他错误编码所有报文统一用800105

    public static final String[] OTHER_ERR =
    { "800105", "其他错误" };// 其他错误编码所有报文统一用800105

    // 返回报文类型
    public static final String BBOSS_BAT_RETURN_TYPE = "BBOSS_ORDER_0_0";// BBOSS批量调用返回报文类型
}
