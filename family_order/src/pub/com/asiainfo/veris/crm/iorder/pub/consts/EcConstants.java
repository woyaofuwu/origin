package com.asiainfo.veris.crm.iorder.pub.consts;

public final class EcConstants {
    public final static String ACTION_CREATE = "0";

    public final static String ACTION_UPDATE = "2";

    public final static String ACTION_DELETE = "1";

    public final static String ACTION_EXITS = "3";

    public final static String ACTION_PAUSE = "5";

    public final static String ACTION_CONTINUE = "6";

    public static final String EC_CATALOG_SPEC_ID = "1000"; // EC目录规格标识

    public static final String EC_OPENED_OFFER_CATALOG_ID = "10012"; // EC已订购销售品目录标识

    public static final String EC_OPENED_OFFER_CLUSTER_CATALOG_ID = "10017"; // EC已订购群组目录标识

    public static final String EC_MEB_CATALOG_SPEC_ID = "1100"; // EC成员目录规格标识

    public static final String EC_MEB_EC_OPENED_OFFER_CATALOG_ID = "10013"; // EC成员目录集团已订购销售品

    public static final String EC_MEB_MEB_OPENED_OFFER_CATALOG_ID = "10014"; // EC成员目录成员已订购销售品

    public static final String EC_MEB_COMP_OFFER_CATALOG_ID = "10015"; // EC成员目录和商务

    public static final String EC_MEB_BATCH_OFFER_CATALOG_ID = "10016"; // 集团批量

    public static final String FLOW_ID_EC_CREATE = "CrtUs"; // CB_EC_POINT表FLOW_ID配置枚举值

    public static final String FLOW_ID_EC_CHANGE = "ChgUs"; // CB_EC_POINT表FLOW_ID配置枚举值

    public static final String FLOW_ID_EC_DELETE = "DstUs"; // CB_EC_POINT表FLOW_ID配置枚举值

    public static final String FLOW_ID_MEMBER_CREATE = "CrtMb"; // CB_EC_POINT表FLOW_ID配置枚举值

    public static final String FLOW_ID_MEMBER_CHANGE = "ChgMb"; // CB_EC_POINT表FLOW_ID配置枚举值

    public static final String FLOW_ID_MEMBER_DELETE = "DstMb"; // CB_EC_POINT表FLOW_ID配置枚举值

    public static final String ESOPINIT_METHOD = "GetEosInfoForClouder";// 初始化数据子编码

    public static final String SUB_USERID_RNUM = "SUB_USERID_RNUM";

    public static final String SUBIBID_RNUM = "SUBIBID_RNUM";

    public static final String RECORD_NUM = "RECORD_NUM";

    public static final String SUB_IBSYSID_ID = "SUB_IBSYSID_ID";

    public static final String SAVEANDSEND = "SS.EopIntfSVC.saveEopNodeAndDrive";// 回写ESOP数据接口(带驱动EOS)

    public static final String SAVEGRPBIZ = "SS.EopIntfSVC.saveEopNode";// 回写ESOP数据接口(不驱动EOS)

    public static final String ZERO_DISCNT_CODE = "84019252";// 专线零元资费

    public static enum EC_OPER_SERVICE {
        CREATE_ENTERPRISE_SUBSCRIBER("CS.CreateGroupUserSvc.createGroupUser"), // 集团新增
        CHANGE_ENTERPRISE_SUBSCRIBER("CS.ChangeUserElementSvc.changeUserElement"), // 集团变更
        DELETE_ENTERPRISE_SUBSCRIBER("CS.DestroyGroupUserSvc.destroyGroupUser"), // 集团注销
        CREATE_ENTERPRISE_MEMBER("CS.CreateGroupMemberSvc.createGroupMember"), // 成员新增
        CHANGE_ENTERPRISE_MEMBER("CS.ChangeMemElementSvc.changeMemElement"), // 成员变更
        DELETE_ENTERPRISE_MEMBER("CS.DestroyGroupMemberSvc.destroyGroupMember"), // 成员注销
        OPEN_ENTERPRISE_MEMBER("SS.OpenGroupMemberSVC.crtTrade"), // 集团成员用户开户
        CHANGE_MEMBER_PAYPLAN("OC.IChangeMemberPayPlanSV.acceptOrder") // 付费关系变更
        ;

        private final String value;

        private EC_OPER_SERVICE(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // BBOSS产品状态
    public static enum MERCH_PRODUCT_STATUS {
        PRODUCT_ADD("1"), // 新增产品订购
        PRODUCT_CANCLE("2"), // 取消产品订购
        PRODUCT_CANCLEPREDESTORY("12"), // 产品预注销
        PRODUCT_CODEAL("15"), // 配合省协助业务受理
        PRODUCT_CONTINUE("4"), // 产品恢复
        PRODUCT_COPREDEAL("14"), // 配合省协助业务预受理
        PRODUCT_MODCODEAL("101"), PRODUCT_MODCOPREDEAL("100"), PRODUCT_MODI_BOSS_DISCNT("98"), PRODUCT_MODIFY("99"), PRODUCT_MODIFY_DISCNT("5"), // 修改产品全网资费
        PRODUCT_MODIFY_LOCALDISCNT("55"), // 修改产品本地资费
        PRODUCT_MODIFY_MEB("6"), // 变更成员（保留）
        PRODUCT_MODIFY_PARAM("9"), // 修改订购产品属性
        PRODUCT_MODIFY_PROV("13"), // 业务开展省新增或删除
        PRODUCT_PASTE("3"), // 产品暂停
        PRODUCT_PREDEAL("10"), // 产品预受理
        PRODUCT_PASTE_MEBFLUX("22"), // 暂停添加成员/叠加包
        PRODUCT_CONTINUE_MEBFLUX("23"), // 恢复添加成员/叠加包
        PRODUCT_PREDESTORY("11"), // 预取消产品订购
        PRODUCT_CAMP_ON("20"); // 资源预占、暂停、取消

        private final String value;

        private MERCH_PRODUCT_STATUS(String value) {

            this.value = value;
        }

        public String getValue() {

            return value;
        }

        @Override
        public String toString() {

            return value;
        }
    }

    // BBOSS商品状态
    public static enum MERCH_STATUS {
        MERCH_ADD("1"), // 新增商品订购
        MERCH_CANCLE("2"), // 取消商品订购
        MERCH_CANCLEPREDESTORY("11"), // 冷冻期恢复商品订购
        MERCH_CONTINUE("4"), // 商品恢复
        MERCH_MODIFY("99"), // 修改商品，5、7、9的统称
        MERCH_MODIFY_DISCNT("5"), // 修改商品集团资费
        MERCH_MODIFY_LOCALDISCNT("55"), // 修改商品本地资费
        MERCH_MODIFY_GROUP("7"), // 修改订购商品组成关系
        MERCH_MODIFY_MEB("6"), // 修改成员
        MERCH_MODIFY_PARAM("9"), // 修改订购商品属性
        MERCH_MODIFY_PROV("13"), // 业务开展省新增或删除
        MERCH_PASTE("3"), // 商品暂停
        MERCH_PREDESTORY("10"), // 预取消商品订购
        MERCH_CAMP_ON("20"), // 资源预占、暂停、取消
        MERCH_CONTRACT_CHG("21"); // 合同变更

        private final String value;

        private MERCH_STATUS(String value) {

            this.value = value;
        }

        public String getValue() {

            return value;
        }

        @Override
        public String toString() {

            return value;
        }
    }

    // BBOSS成员
    public static enum BBOSS_MEB_STATUS {
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

        private BBOSS_MEB_STATUS(String value) {

            this.value = value;
        }

        public String getValue() {

            return value;
        }

        @Override
        public String toString() {

            return value;
        }
    }

    public static String transOperCodeToOperType(String operCode, String mode) {
        String obj = "";

        if ("EC".equals(mode)) {
            if (ACTION_CREATE.equals(operCode)) {
                obj = EcConstants.FLOW_ID_EC_CREATE;
            } else if (ACTION_UPDATE.equals(operCode)) {
                obj = EcConstants.FLOW_ID_EC_CHANGE;
            } else if (ACTION_DELETE.equals(operCode)) {
                obj = EcConstants.FLOW_ID_EC_DELETE;
            }
        } else if ("MEM".equals(mode)) {
            if (ACTION_CREATE.equals(operCode)) {
                obj = EcConstants.FLOW_ID_MEMBER_CREATE;
            } else if (ACTION_UPDATE.equals(operCode)) {
                obj = EcConstants.FLOW_ID_MEMBER_CHANGE;
            } else if (ACTION_DELETE.equals(operCode)) {
                obj = EcConstants.FLOW_ID_MEMBER_DELETE;
            }
        }
        return obj;
    }

    public static String transOperTypeToOperCode(String operType, String mode) {
        String operCode = ACTION_EXITS;

        if ("EC".equals(mode)) {
            if (EcConstants.FLOW_ID_EC_CREATE.equals(operType)) {
                operCode = ACTION_CREATE;
            } else if (EcConstants.FLOW_ID_EC_CHANGE.equals(operType)) {
                operCode = ACTION_UPDATE;
            } else if (EcConstants.FLOW_ID_EC_DELETE.equals(operType)) {
                operCode = ACTION_DELETE;
            }
        } else if ("MEM".equals(mode)) {
            if (EcConstants.FLOW_ID_MEMBER_CREATE.equals(operType)) {
                operCode = ACTION_CREATE;
            } else if (EcConstants.FLOW_ID_MEMBER_CHANGE.equals(operType)) {
                operCode = ACTION_UPDATE;
            } else if (EcConstants.FLOW_ID_MEMBER_DELETE.equals(operType)) {
                operCode = ACTION_DELETE;
            }
        }

        return operCode;
    }
}
