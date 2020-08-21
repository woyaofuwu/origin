package com.asiainfo.veris.crm.order.pub.consts;

import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 产商品接口常量
 */
public class UpcConst {
    public final static String PM_OFFER_JOIN_REL_TYPE_ECMEB = "1"; // 商品间关系表，关系类型：1-集团商品与成员商品关系

    public final static String PM_OFFER_JOIN_REL_TYPE_POWER100 = "4"; // 商品间关系表，关系类型：4-动力100/bboss

    public final static String SELECT_FLAG_MUST_CHOOSE = "0"; // 选择状态：0-必选

    public final static String SELECT_FLAG_CAN_CHOOSE_YES = "1"; // 选择状态：1-可选(默认选中)

    public final static String SELECT_FLAG_CAN_CHOOSE_NO = "2"; // 选择状态：2-可选(默认未选)

    public final static String PACKAGE_TO_GROUP_HEAD = ""; // J2EE的包转产商品组需要+400

    public final static String PM_OFFER_COM_REL_CHA_TYPE = "0";// 原包元素关系表扩展字段属性，且包元素关系转换为商品构成关系的(EXT_CHA_ID=商品构成表中(PM_OFFER_COM_REL)的REL_ID)

    public final static String PM_OFFER_JOIN_REL_CHA_TYPE = "1";// 原包元素关系表扩展字段属性，且包元素关系转换为商品推荐关系的(EXT_CHA_ID=商品关联关系表中(PM_OFFER_JOIN_REL)的REL_ID)

    public final static String PM_GROUP_COM_REL_CHA_TYPE = "2";// 原包元素关系表扩展字段属性，且包元素关系转换为组构成关系的(EXT_CHA_ID=组构成表中(PM_GROUP_COM_REL)的REL_ID)

    public final static String PM_OFFER_GROUP_REL_CHA_TYPE = "3";// 原产品包关系表扩展字段属性(EXT_CHA_ID=商品组关系表中(PM_OFFER_GROUP_REL)的REL_ID)

    public final static String ELEMENT_TYPE_CODE_PRODUCT = "P";// 产品

    public final static String ELEMENT_TYPE_CODE_SVC = "S";// 服务

    public final static String ELEMENT_TYPE_CODE_DISCNT = "D";// 优惠

    public final static String QUERY_COM_CHA_YES = "Y";// 是否查询构成属性

    /**
     * 1商品
     */
    public final static String REL_OBJECT_OFFER = "1";

    /**
     * 2商品关联关系
     */
    public final static String REL_OBJECT_OFFER_JOIN_REL = "2";

    /**
     * 3商品构成关系
     */
    public final static String REL_OBJECT_OFFER_COM_REL = "3";

    /**
     * 4组的构成关系
     */
    public final static String REL_OBJECT_GROUP_COM_REL = "4";

    public final static String getGroupIdByPackageId(String packageId) {
        return PACKAGE_TO_GROUP_HEAD + packageId;
    }

    /**
     * 转译产商品的必选标记
     * 
     * @param selectFlag
     * @return
     */
    public final static String getForceTagForSelectFlag(String selectFlag) {
        String forceTag = "0";

        if (StringUtils.isNotBlank(selectFlag) && selectFlag.equals("0")) {
            forceTag = "1";
        }

        return forceTag;
    }

    /**
     * 转译产商品的必选标记
     * 
     * @param selectFlag
     * @return
     */
    public final static String getDefaultTagForSelectFlag(String selectFlag) {
        String defaultTag = "0";

        if (StringUtils.isNotBlank(selectFlag) && (selectFlag.equals("1") || selectFlag.equals("0"))) {
            defaultTag = "1";
        }

        return defaultTag;
    }

    public final static String getSelectFlagForDefaultTagAndForceTag(String defaultTag, String forceTag) {

        String selectFlag = "2";

        if (StringUtils.equals(forceTag, "1")) {
            selectFlag = "0";
        }

        if (!StringUtils.equals(forceTag, "1") && StringUtils.equals(defaultTag, "1")) {
            selectFlag = "1";
        }

        return selectFlag;
    }

    // public static String transOfferCodeByOfferId(String offerId) throws Exception
    // {
    // if (StringUtils.isBlank(offerId))
    // {
    // return "";
    // }
    //
    // if (offerId.length() != 12)
    // {
    // return offerId; //不转
    // }
    //
    // BigDecimal aa = new BigDecimal(offerId);
    // BigDecimal bb = new BigDecimal("110000000000");
    // if (offerId.startsWith("12"))
    // {
    // bb = new BigDecimal("120000000000");
    // }
    // else if (offerId.startsWith("13"))
    // {
    // bb = new BigDecimal("130000000000");
    // }
    // else if (offerId.startsWith("14"))
    // {
    // bb = new BigDecimal("140000000000");
    // }
    // else if (offerId.startsWith("15"))
    // {
    // bb = new BigDecimal("150000000000");
    // }
    // else if (offerId.startsWith("16"))
    // {
    // bb = new BigDecimal("160000000000");
    // }
    // else if (offerId.startsWith("17"))
    // {
    // bb = new BigDecimal("170000000000");
    // }
    //
    // BigDecimal cc = aa.subtract(bb);
    // String offerCode = cc.toString();
    // return offerCode;
    // }
}
