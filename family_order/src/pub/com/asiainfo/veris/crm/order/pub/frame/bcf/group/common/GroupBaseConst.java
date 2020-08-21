
package com.asiainfo.veris.crm.order.pub.frame.bcf.group.common;

public final class GroupBaseConst
{
    // BBOSS商产品key
    public static enum BBOSS_KEY
    {
        MERCHINFO("MERCHINFO"), // 商品KEY
        MERCHPINFO("MERCHPINFO"); // 产品key

        private final String value;

        private BBOSS_KEY(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }

    }

    // 省BOSS在BBOSS业务中的位置
    public static enum BBOSS_LOCATION
    {
        FORWARD("SEND"), // 发起方
        REVERSE("COME"), // 落地方
        OUTINTF("OUTINTF"), // 外围接口
        SYNERGIC("SYNERGIC");// 配合省

        private final String value;

        private BBOSS_LOCATION(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS成员
    public static enum BBOSS_MANAGE_FLOW
    {

        MANAGE_END("0"), // 不需要发产品属性报文
        MANAGE_FINISH("1"), // 受理完成,发一部分
        MANAGE_SX("2"), // 修改属性
        MANAGE_WC("4"), // 修改资费
        MANAGE_ZF("3"); // 都不修改

        private final String value;

        private BBOSS_MANAGE_FLOW(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    public static enum BBOSS_MEB_NETTYPE
    {
        NET_IN("IN"), //
        NET_OUT("OUT");//

        private final String value;

        private BBOSS_MEB_NETTYPE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS成员
    public static enum BBOSS_MEB_STATUS
    {
        MEB_CANCLE("0"), // 删除
        MEB_ADD("1"), // 增加
        MEB_MODIFY("2"), // 变更成员类型
        MEB_PASTE("3"), // 暂停成员
        MEB_CONTINUE("4"), // 恢复成员
        MEB_ACTIVE("5"), // 激活成员
        MEB_MODIFY_PARAM("6"), // 变更成员扩展属性
        MEB_MODIFY_IMS_PASSWORD("7"), // 重置序列号（企业飞信）
        MEB_MODI_BOSS_DISCNT("98"); // 修改成员省内资费

        private final String value;

        private BBOSS_MEB_STATUS(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    public static enum CancelMode
    {
        // 取消方式：0-立即取消（当前时间），1-昨天取消（昨天的23:59 59），2-今天取消（今天的23:59 59），3 -本账期末取消（本月最后一天的23:59 59）
        Now("0"), // 2-今天取消
        Yesterday("1"), // 昨天取消
        Today("2"), // 3 -本账期末取消
        LastDate("3"), // 1-昨天取消
        Error("4"); // 0-立即取消（当前时间）

        private final String value;

        private CancelMode(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    public static enum CommDataEntity
    {
        ACCOUNT("ACCOUNT"), //
        ACCT_CONSIGN("ACCT_CONSIGN"), //
        ASK("ASK"), //
        BBOSS_SVC("BBOSS_SVC"), //
        COMMON("COMMON"), //
        CUST_PERSON("CUST_PERSON"), //
        CUSTOMER("CUSTOMER"), //
        DISCNT("DISCNT"), //
        ELEMENT("ELEMENT"), //
        ELEMENT_PARAM("ELEMENT_PARAM"), //
        FEE_DEVICE("FEE_DEVICE"), //
        FEE_GIFTFEE("FEE_GIFTFEE"), //
        FEE_OTHERFEE("FEE_OTHERFEE"), //
        GRP_PACKAGE("GRP_PACKAGE"), //
        MANAGE_INFO("MANAGE_INFO"), //
        MERCH_DISCNT("MERCH_DISCNT"), //

        MERCH_MEMBER("MERCH_MEMBER"), //
        MERCH_P("MERCH_P"), //
        MERCH_P_DISCNT("MERCH_P_DISCNT"), //
        OTHER("OTHER"), //
        PAY_COMPANY("PAY_COMPANY"), //
        PAYRELATION("PAYRELATION"), //
        SPECIALPAY("SPECIALPAY"), PAYPLAN("PAYPLAN"), //
        POST("POST"), //
        PR_MN_OPERATE_CODE("PR_MN_OPERATE_CODE"), //
        PRODUCT("PRODUCT"), //
        PRODUCT_ID_SET("PRODUCT_ID_SET"), //
        PRODUCT_PARAM("PRODUCT_PARAM"), //
        RELATION("RELATION"), //

        RES("RES"), //

        SCORE("SCORE"), //
        SPECIAL_SVC_PARAM("SPECIAL_SVC_PARAM"), //
        SPSVC("SPSVC"), //
        SVC("SVC"), //
        SVCSTATE("SVCSTATE"), //

        USER("USER"), //

        DEVELOP("DEVELOP"),

        USER_ATTR("USER_ATTR"), //
        
        OFFER_REL("OFFER_REL"), //

        EOS("EOS");//

        private final String value;

        private CommDataEntity(String value)
        {

            this.value = value;
        }

        private String getValue()
        {

            return value;
        }
    }

    // BBOSS集团变更处理类别
    public static enum GROUP_CHANGE_DEAL_TYPE
    {
        MERCH_CHANGE("1"), // BBOSS商品变更，包括商品暂停，恢复，预取消等
        MERCH_CHANGE_PRODUCT_ADD("2"), // BBOSS商品变更，产品新增
        MERCH_CHANGE_PRODUCT_CHANGE("3"), // BBOSS商品变更，产品参数变更
        MERCH_CHANGE_PRODUCT_CANCEL("4"); // BBOSS商品变更，产品取消订购

        private final String value;

        private GROUP_CHANGE_DEAL_TYPE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    public static enum GroupDesignFlag
    {
        GroupDesignNo("0"), //
        GroupDesignYes("1"); // 

        private final String value;

        private GroupDesignFlag(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    // BBOSS成员参数状态定义
    public static enum MEB_ATTR_STATUS_DESC
    {
        ATTR_ADD("ADD"), // 成员参数新增
        ATTR_DEL("DEL");// 成员参数删除

        private final String value;

        private MEB_ATTR_STATUS_DESC(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS成员变更处理类别
    public static enum MEM_CHANGE_DEAL_TYPE
    {
        OPER_CHANGE_MEM_ADD("2"), // 成员变更操作下的成员变更
        OPER_CHANGE_MEM_CHANGE("1"), // 成员变更操作下的成员新增
        OPER_CHANGE_MEM_DEL("3");// 成员变更操作下的成员删除

        private final String value;

        private MEM_CHANGE_DEAL_TYPE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS产品状态
    public static enum MERCH_PRODUCT_STATUS
    {
        PRODUCT_ADD("1"), // 新增产品订购
        PRODUCT_CANCLE("2"), // 取消产品订购
        PRODUCT_PASTE("3"), // 产品暂停
        PRODUCT_CONTINUE("4"), // 产品恢复
        PRODUCT_MODIFY_DISCNT("5"), // 修改集团产品资费
        PRODUCT_MODIFY_LOCALDISCNT("55"), // 修改产品本地资费
        PRODUCT_MODI_BOSS_DISCNT("98"), // 修改省内资费
        PRODUCT_MODIFY_MEB("6"), // 变更成员
        PRODUCT_MODIFY_PARAM("9"), // 修改订购产品属性
        PRODUCT_MODIFY("99"), // 修改产品，5、9的统称
        PRODUCT_PREDEAL("10"), // 产品预受理
        PRODUCT_PREDESTORY("11"), // 预取消产品订购
        PRODUCT_CANCLEPREDESTORY("12"), // 冷冻期恢复产品订购
        PRODUCT_MODIFY_PROV("13"), // 业务开展省新增或删除
        PRODUCT_COPREDEAL("14"), // 配合省协助工单预受理
        PRODUCT_CODEAL("15"), // 配合省协助工单受理
        PRODUCT_CAMP_ON("20"), // 资源预占、暂停、取消
        PRODUCT_MODCOPREDEAL("100"), // 配合省协助资源变更预受理
        PRODUCT_MODCODEAL("101"), // 配合省协助资源变更受理
    	PRODUCT_PASTE_MEBFLUX("22"),//暂停添加成员/叠加包
    	PRODUCT_CONTINUE_MEBFLUX("23");//恢复添加成员/叠加包

        private final String value;

        private MERCH_PRODUCT_STATUS(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS商品状态
    public static enum MERCH_STATUS
    {
        MERCH_ADD("1"), // 新增商品订购
        MERCH_CANCLE("2"), // 取消商品订购
        MERCH_PASTE("3"), // 商品暂停
        MERCH_CONTINUE("4"), // 商品恢复
        MERCH_MODIFY_DISCNT("5"), // 修改商品集团资费
        MERCH_MODIFY_LOCALDISCNT("55"), // 修改商品本地资费
        MERCH_MODIFY_MEB("6"), // 变更成员
        MERCH_MODIFY_GROUP("7"), // 修改订购商品组成关系
        MERCH_MODIFY_PARAM("9"), // 修改订购产品属性
        MERCH_MODIFY("99"), // 修改商品，5、7、9的统称
        MERCH_PREDESTORY("10"), // 预取消商品订购
        MERCH_CANCLEPREDESTORY("11"), // 冷冻期恢复商品订购
        MERCH_MODIFY_PROV("13"), // 业务开展省新增或删除
        MERCH_CAMP_ON("20"), // 资源预占、暂停、取消
    	MERCH_PASTE_MEBFLUX("22"),	//暂停添加成员/叠加包
    	MERCH_CONTINUE_MEBFLUX("23");	//恢复添加成员/叠加包

        private final String value;

        private MERCH_STATUS(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS商品状态描述 add by zhouwei 20100905
    public static enum MERCH_STATUS_DESC
    {
        MERCH_ADD("1:新增商品订购"), // 新增商品订购
        MERCH_CANCLE("2:取消商品订购"), // 取消商品订购
        MERCH_PASTE("3:商品暂停"), // 商品暂停
        MERCH_CONTINUE("4:商品恢复"), // 商品恢复
        MERCH_MODIFY_DISCNT("5:修改商品资费"), // 修改商品资费
        MERCH_MODIFY_GROUP("7:修改订购商品组成关系"), // 修改订购商品组成关系
        MERCH_MODIFY_PARAM("9:修改订购商品属性"), // 修改订购商品属性
        MERCH_MODIFY("99:修改商品"), // 修改商品，5、7、9的统称
        MERCH_CANCLEPREDESTORY("11:冷冻期恢复商品订购"), // 冷冻期恢复商品订购
        MERCH_MODIFY_PROV("13:业务开展省新增或删除"), // 业务开展省新增或删除
        MERCH_PREDESTORY("10:预取消商品订购"); // 预取消商品订购

        private final String value;

        private MERCH_STATUS_DESC(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    public static enum ModuleDataEntity
    {

        PRODUCT_INFOS("PRODUCT_INFOS"), // 
        USER_ATTR("USER_ATTR");//

        private final String value;

        private ModuleDataEntity(String value)
        {

            this.value = value;
        }

        private String getValue()
        {

            return value;
        }
    }

    public static enum OPER_CODE2ADCMAS
    {
        PRODUCT_ADD("01"), // 01--加入名单（订购）
        PRODUCT_CANCLE("02"), // 02－退出名单（退订）
        PRODUCT_PASTE("03"), // 03--用户暂停
        PRODUCT_CONTINUE("04"), // 04－用户恢复
        PRODUCT_MODIFY("05");// 05-变更

        private final String value;

        private OPER_CODE2ADCMAS(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    // BBOSS参数状态
    public static enum PARMA_STATUS
    {
        PARAM_DEL("0"), PARAM_ADD("1");
        private final String value;

        private PARMA_STATUS(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // BBOSS产品参数状态定义
    public static enum PRODUCT_ATTR_STATUS_DESC
    {
        ATTR_ADD("ADD"), // 成员参数新增
        ATTR_DEL("DEL");// 成员参数删除

        private final String value;

        private PRODUCT_ATTR_STATUS_DESC(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // 产品类别
    public static enum PRODUCT_MODE
    {
        USER_MAIN_PRODUCT("10"), // 集团基本产品
        USER_PLUS_PRODUCT("11"), // 集团附加产品
        MEM_MAIN_PLUS_PRODUCT("12"), // 集团成员必选附加产品
        MEM_PLUS_PRODUCT("13"), // 集团成员可选附加产品
        MEM_SALE_PRODUCT("15"), // 成员营销产品
        MEM_BASE_PRODUCT("20"); // 成员个人基本产品（生成虚拟三户资料用）

        private final String value;

        private PRODUCT_MODE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    public static enum RELA_TYPE
    {
        UU("1"), BB("2"), ALL("0");
        private final String value;

        private RELA_TYPE(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {

            return value;
        }
    }

    // 用户账期分布
    public static enum UserDaysDistribute
    {
        TRUE("0"), FALSE_TRUE("1"), TRUE_FALSE("2"), FALSE_FALSE("3"), FALSE("4");

        private final String value;

        private UserDaysDistribute(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public static String groupClassPath = "com.ailk.saleserv.bean.";

    public static final String[] printTemplets =
    { "发票", "收据", "", "免填单", "", "", "", "", "", "" };

    public static final String X_SUBTRANS_CODE = "X_SUBTRANS_CODE"; // 子交易编码(接口)

    public static final String DIVERSIFY_BOOKING = "DIVERSIFY_BOOKING"; // 分散预约标志

    public static final String BB_BRAND_CODE = "|ADCG|MASG|BOSG|JKDT"; // 拆分BB表的品牌编码

    public static final String EFFECT_NOW = "EFFECT_NOW"; // 是否立即生效标志
}
