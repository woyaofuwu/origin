package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upc.UpcViewCallIntf;

public final class UpcViewCall {

    /**
     * *根据主商品查询标签信息
     * 
     * @param groupId
     *            必填
     * @param groupType
     *            必填
     * @throws Exception
     */

    public static IDataset qryAllTagAndTagValueByOfferId(IBizCommon bc, String offerCode, String offerType) throws Exception {
        IDataset result = UpcViewCallIntf.qryAllTagAndTagValueByOfferId(bc, offerCode, offerType);

        return result;
    }

    /**
     * *根据产品，标签，标签值，品类查询下面的产品
     * 
     * @param groupId
     *            必填
     * @param groupType
     *            必填
     * @throws Exception
     */

    public static IDataset qryOfferByTagInfo(IBizCommon bc, String offerCode, String offerType, String labelId, String labelKeyId, String categoryId) throws Exception {

        IDataset result = UpcViewCallIntf.qryOfferByTagInfo(bc, offerCode, offerType, labelId, labelKeyId, categoryId);

        return result;
    }

    /**
     * *商品相关接口 *根据商品ID查询商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param queryComCha
     *            可填
     * @throws Exception
     */

    public static IData queryOfferByOfferId(IBizCommon bc, String offerType, String offerCode, String queryComCha) throws Exception {
        IDataset result = UpcViewCallIntf.queryOfferByOfferId(bc, offerType, offerCode, queryComCha);
        if (IDataUtil.isNotEmpty(result)) {
            return result.getData(0);
        } else {
            return null;
        }
    }

    /**
     * *商品相关接口 *根据商品ID查询商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param queryComCha
     *            可填
     * @throws Exception
     */

    public static IData queryOfferByOfferId(IBizCommon bc, String offerType, String offerCode) throws Exception {
        return queryOfferByOfferId(bc, offerType, offerCode, "");
    }

    /**
     * 翻译OFFER名称
     * 
     * @param offerType
     * @param offerCode
     * @return
     * @throws Exception
     */
    public static String queryOfferNameByOfferId(IBizCommon bc, String offerType, String offerCode) throws Exception {
        IData offer = queryOfferByOfferId(bc, offerType, offerCode, "");
        if (IDataUtil.isNotEmpty(offer)) {
            return offer.getString("OFFER_NAME");
        } else {
            return "";
        }
    }

    /**
     * 
     * @Title: queryOfferLineIdByOfferId
     * @Description: 得到产品线
     * @param @param offerType
     * @param @param offerCode
     * @param @return
     * @param @throws Exception 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String queryOfferLineIdByOfferId(IBizCommon bc, String offerType, String offerCode) throws Exception {
        IData offer = queryOfferByOfferId(bc, offerType, offerCode, "");
        if (IDataUtil.isNotEmpty(offer)) {
            return offer.getString("OFFER_LINE_ID");
        } else {
            return "";
        }
    }

    public static String queryOfferExplainByOfferId(IBizCommon bc, String offerType, String offerCode) throws Exception {
        IData offer = queryOfferByOfferId(bc, offerType, offerCode, "");
        if (IDataUtil.isNotEmpty(offer)) {
            return offer.getString("DESCRIPTION");
        } else {
            return "";
        }
    }

    public static IDataset querySpServiceBySpCodeAndBizCodeAndBizStateCode(IBizCommon bc, String SpCode, String BizCode) throws Exception {
        return UpcViewCallIntf.querySpServiceBySpCodeAndBizCodeAndBizStateCode(bc, SpCode, BizCode);
    }

    public static IDataset querySpServiceBySpCodeAndBizCodeAndBizStateCodeRsrvStr1(IBizCommon bc, String SpCode, String BizCode) throws Exception {
        return UpcViewCallIntf.querySpServiceBySpCodeAndBizCodeAndBizStateCodeRsrvStr1(bc, SpCode, BizCode);
    }

    /**
     * *产品状态相关接口 *根据OFFER_ID,PROD_STATUS查询PM_PROD_STA
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param prodStatus
     *            必填
     * @throws Exception
     */

    public static IData queryProdStaByCond(IBizCommon bc, String offerType, String offerCode, String prodStatus) throws Exception {
        IDataset result = UpcViewCallIntf.queryProdStaByCond(bc, offerType, offerCode, prodStatus);
        if (IDataUtil.isNotEmpty(result)) {
            return result.getData(0);
        } else {
            return null;
        }
    }

    public static String queryStateNameBySvcId(IBizCommon bc, String serviceId, String prodStatus) throws Exception {
        IData result = queryProdStaByCond(bc, "S", serviceId, prodStatus);
        if (IDataUtil.isNotEmpty(result)) {
            return result.getString("STATUS_NAME");
        } else {
            return "";
        }
    }

    /**
     * 通过品牌编码查询名称
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static String getBrandNameByBrandCode(IBizCommon bc, String brandCode) throws Exception {
        return UpcViewCallIntf.queryBrandNameByChaVal(bc, brandCode);
    }

    /**
     * *商品相关接口 *根据商品ID查询商品关联关系并关联出商品
     * 
     * @param offerType
     *            必填
     * @param offerCode
     *            必填
     * @param relType
     *            可填
     * @param selectFlag
     *            可填
     * @param queryCha
     *            可填
     * @throws Exception
     */

    public static IDataset queryOfferJoinRelAndOfferByOfferId(IBizCommon bc, String offerType, String offerCode, String relType, String selectFlag, String queryCha) throws Exception {
        return UpcViewCallIntf.queryOfferJoinRelAndOfferByOfferId(bc, offerType, offerCode, relType, selectFlag, queryCha);
    }

    /**
     * 
     * @Title: queryMemProductIdByProductId
     * @Description: 根据集团产品查询成员产品
     * @param @param productId
     * @param @return
     * @param @throws Exception 设定文件
     * @return String 返回类型
     * @throws
     */
    public static String queryMemProductIdByProductId(IBizCommon bc, String productId) throws Exception {
        IDataset joinRelOffers = queryOfferJoinRelAndOfferByOfferId(bc, "P", productId, "1", "", "");
        if (IDataUtil.isEmpty(joinRelOffers)) {
            return ""; // 应该要报错的
        }

        return joinRelOffers.getData(0).getString("OFFER_CODE");
    }

    public static IDataset queryOffersByMultiCategory(IBizCommon bc, String mainOfferId, String mgmtDistrict, String categoryId, String relType) throws Exception {
        return UpcViewCallIntf.queryOffersByMultiCategory(bc, mainOfferId, mgmtDistrict, categoryId, relType);
    }

    public static IDataset queryOfferGroups(IBizCommon bc, String productId) throws Exception {
        return UpcViewCallIntf.queryOfferGroups(bc, productId);
    }

    public static IDataset queryGroupComRelOffer(IBizCommon bc, String groupId) throws Exception {
        return UpcViewCallIntf.queryGroupComRelOffer(bc, groupId);
    }

    public static String getPackageNameByPackageId(IBizCommon bc, String packageId) throws Exception {
        if (StringUtils.isBlank(packageId) || packageId.equals("-1") || packageId.equals("0")) {
            return "";
        }
        IDataset groups = UpcViewCallIntf.queryGroupByCond(bc, packageId, "");
        if (IDataUtil.isNotEmpty(groups)) {
            return groups.getData(0).getString("GROUP_NAME");
        } else {
            return "";
        }
    }

    /**
     * 获取元素失效方式
     * 
     * @param elementTypeCode
     *            ,elementId
     */
    public static String getCancelTagByElementTypeCodeAndElementId(IBizCommon bc, String elementTypeCode, String elementId) throws Exception {
        IDataset dataset = UpcViewCallIntf.queryOfferEnableModeByCond(bc, elementTypeCode, elementId, null, null, null);
        String cancelTag = "";
        if (IDataUtil.isNotEmpty(dataset)) {
            IData data = dataset.getData(0);
            cancelTag = data.getString("CANCEL_MODE", "");
        }
        return cancelTag;
    }

    public static IDataset qryOfferFromGroupByGroupIdOfferId(IBizCommon bc, String groupId, String offerType, String offerCode) throws Exception {
        return UpcViewCallIntf.qryOfferFromGroupByGroupIdOfferId(bc, groupId, offerType, offerCode);
    }

    /**
     * 查询PM_PO表
     * 
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-22
     */
    public static IDataset queryPoByValid(IBizCommon bc) throws Exception {
        return UpcViewCallIntf.queryPoByValid(bc);
    }

    /**
     * 根据POSPECNUMBER 查询PM_POPRODUCT表
     * 
     * @param pospecnumber
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-22
     */
    public static IDataset queryPoproductByPospecNumber(IBizCommon bc, String pospecnumber) throws Exception {
        return UpcViewCallIntf.queryPoproductByPospecNumber(bc, pospecnumber);
    }

    /**
     * *BBOSS相关接口 *根据PRODUCTSPECNUMBER,POSPECNUMBER 查询PM_POPRODUCT表
     * 
     * @param productspecnumber
     *            必填
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoproductByProductSpecNumber(IBizCommon bc, String productspecnumber, String pospecnumber) throws Exception {
        return UpcViewCallIntf.queryPoproductByProductSpecNumber(bc, productspecnumber, pospecnumber);
    }

    /**
     * *BBOSS相关接口 *根据PospecNumber查询PM_PO表
     * 
     * @param pospecnumber
     *            必填
     * @throws Exception
     */

    public static IDataset queryPoByPospecNumber(IBizCommon bc, String pospecnumber) throws Exception {
        return UpcViewCallIntf.queryPoByPospecNumber(bc, pospecnumber);
    }

    /**
     * @Description 根据offer_code查询(td_s_servicestate)表
     * @param offerCode
     * @param offerType
     * @param funcStatus
     * @return
     * @throws Exception
     */
    public static String qryOfferFuncStaByAnyOfferIdStatus(IBizCommon bc, String offerCode, String offerType, String funcStatus) throws Exception {
        return UpcViewCallIntf.qryOfferFuncStaByAnyOfferIdStatus(bc, offerCode, offerType, funcStatus);
    }

    /**
     * duhj
     * 
     * @param bc
     * @param pospecnumber
     * @return
     * @throws Exception
     */
    public static IData queryPlatSvcBySpcodeBizCode(IBizCommon bc, String spCode, String bizCode, String bizTypeCode, Pagination p) throws Exception {
        return UpcViewCallIntf.queryPlatSvcBySpcodeBizCode(bc, spCode, bizCode, bizTypeCode, p);
    }

    public static IDataset queryOfferByOfferCodeAndType(IBizCommon bc, String offerCode, String offerType) throws Exception {
        return UpcViewCallIntf.queryOfferByOfferCodeAndType(bc, offerCode, offerType);

    }

    public static String queryBrandCodeByOfferCodeAndType(IBizCommon bc, String offerCode, String offerType) throws Exception {
        return UpcViewCallIntf.queryBrandCodeByOfferCodeAndType(bc, offerCode, offerType);
    }

    public static String getUseTagByProductId(IBizCommon bc, String offerCode) throws Exception {
        return UpcViewCallIntf.getUseTagByProductId(bc, offerCode);
    }

}
