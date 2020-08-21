package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UProductInfoQry {
    /**
     * 根据产品标识查询产品品牌
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getBrandCodeByProductId(String productId) throws Exception {
        try {
            OfferCfg offercfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (offercfg == null)
                return "";

            else
                return offercfg.getBrandCode();
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据产品标识查询产品网别
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getNetTypeCodeByProductId(String productId) throws Exception {
        try {
            OfferCfg offercfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (offercfg == null)
                return "";

            else
                return offercfg.getNetTypeCode();
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据产品标识查询产品解释
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getProductExplainByProductId(String productId) throws Exception {
        try {
            OfferCfg offercfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (offercfg == null)
                return "";

            else
                return offercfg.getDescription();
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据产品标识查询产品模式
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getProductModeByProductId(String productId) throws Exception {
        OfferCfg offercfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (offercfg == null)
            return "";

        else
            return offercfg.getProductMode();
    }

    /**
     * 根据产品标识查询产品模式
     *
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getProductModeByProductId(String productId , String productType) throws Exception {
        OfferCfg offercfg = OfferCfg.getInstance(productId, productType);
        if (offercfg == null)
            return "";

        else
            return offercfg.getProductMode();
    }

    /**
     * 根据产品标识查询产品名称
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getProductNameByProductId(String productId) throws Exception {
        try {
            return UpcCall.qryOfferNameByOfferTypeOfferCode(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据服务ID获取宽带速率配置
     * 
     * @param service_id
     * @return 宽带速率
     * @throws Exception
     */
    public static String getKDRadeByServicetId(String service_id) throws Exception {
        String result;
        try {
            if (StringUtils.isNotBlank(service_id)) {
                IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 4000, service_id, "ZZZZ");
                if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
                    result = ((IData) comparas.get(0)).getString("PARA_CODE17");
                    result += ((IData) comparas.get(0)).getString("PARA_CODE18");
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        }
        catch (Exception e) {
            result = "";
        }
        return result;
    }
    
    
    
    
    /**
     * 根据服务ID获取宽带速率值
     * 
     * @param service_id
     * @return 宽带速率
     * @throws Exception
     */
    public static String getKDRadeNumByServicetId(String service_id) throws Exception {
        String result;
        try {
            if (StringUtils.isNotBlank(service_id)) {
                IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 4000, service_id, "ZZZZ");
                if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
                	result = ((IData) comparas.get(0)).getInt("PARA_CODE1")/1024+"";
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        }
        catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 根据服务ID获取宽带速率配置(个人宽带)
     * @param service_id
     * @return 宽带速率
     * @throws Exception
     */
    public static String getPersonKDRadeByServicetId(String service_id) throws Exception
    {
        String result;
        try
        {
            if(StringUtils.isNotBlank(service_id)){
                IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",4000,service_id,"ZZZZ");
                if(IDataUtil.isNotEmpty(comparas) && comparas.size()>0){
                    result = ((IData)comparas.get(0)).getString("PARA_CODE20");
                    result += ((IData)comparas.get(0)).getString("PARA_CODE18");
                }else {
                    result = "";
                }
            }else {
                result = "";
            }
        }
        catch (Exception e)
        {
            result = "";
        }
        return result;
    }
    
    /**
     * 根据产品编码获取宽带套餐资费标准
     * 
     * @param product_id
     * @return 宽带套餐资费标准
     * @throws Exception
     */
    public static String getKDTariffByProductId(String product_id) throws Exception {
        String result;
        try {
            if (StringUtils.isNotBlank(product_id)) {
                IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 5889, product_id, "ZZZZ");
                if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
                	result = ((IData) comparas.get(0)).getString("PARA_CODE1");
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        }
        catch (Exception e) {
            result = "";
        }
        return result;
    }
    
    /**
     * 根据discount_code查询，判断优惠资费套餐是否限制携转
     * 
     * @param discount_code
     * @return 是否限制优惠资费套餐携转
     * @throws Exception
     */
    public static boolean getIsExitByDiscountCode(String discount_code) throws Exception {
        boolean result = false;
        try {
            if (StringUtils.isNotBlank(discount_code)) {
                IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 2005, "OUTNPLIMIT", discount_code, "0898");
                if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
                    // result = ((IData)comparas.get(0)).getString("PARA_CODE18");
                    result = true;
                }
            }
        }
        catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 根据优惠ID获取宽带体验套餐配置
     * 
     * @param DiscntID
     * @return 宽带体验套餐
     * @throws Exception
     */
    public static String getKDRadeByDiscntId(String service_id) throws Exception {
        String result;
        try {
            if (StringUtils.isNotBlank(service_id)) {
                IDataset comparas = BreQryForCommparaOrTag.getCommpara("CSM", 4000, service_id, "ZZZZ");
                if (IDataUtil.isNotEmpty(comparas) && comparas.size() > 0) {
                    result = ((IData) comparas.get(0)).getString("PARA_CODE24");
                    String strSys = SysDateMgr.getSysDate();
                    result = result.replace("%s", strSys);
                } else {
                    result = "";
                }
            } else {
                result = "";
            }
        }
        catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 根据产品标识查询产品所属类别
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getProductOrderNoByProductId(String productId) throws Exception {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN)) {
            return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PTYPE_PRODUCT", "PRODUCT_ID", "ORDER_NO", productId);
        }

        return StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_UPC, "TD_B_PTYPE_PRODUCT", "PRODUCT_ID", "ORDER_NO", productId);

    }

    /**
     * 根据产品标识查询产品配置信息
     * 
     * @param productid
     * @return
     * @throws Exception
     */
    public static IData qryProductByPK(String productid) throws Exception {
        OfferCfg offercfg = OfferCfg.getInstance(productid, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (offercfg == null)
            return null;

        IData productInfo = new DataMap();
        // productInfo.put("CATEGORY_ID", offercfg.getCategoryId());
        productInfo.put("PRODUCT_ID", offercfg.getOfferCode());
        productInfo.put("NET_TYPE_CODE", offercfg.getNetTypeCode());
        productInfo.put("PRODUCT_NAME", offercfg.getOfferName());
        productInfo.put("PRODUCT_EXPLAIN", offercfg.getDescription());
        productInfo.put("PRODUCT_MODE", offercfg.getProductMode());
        productInfo.put("BRAND_CODE", offercfg.getBrandCode());
        productInfo.put("COMP_TAG", offercfg.isComp());
        productInfo.put("START_DATE", offercfg.getValidDate());
        productInfo.put("END_DATE", offercfg.getExpireDate());
        productInfo.put("OFFER_LINE_ID", offercfg.getOfferLineId());

        productInfo.putAll(getProductExtChaInfoByProductId(productid));// 放入扩展字段
        return productInfo;
    }

    public static IData qrySaleActiveProductByPK(String productid) throws Exception {
        IDataset datas = UpcCall.qryCatalogByCatalogId(productid);

        IData productInfo = new DataMap();
        if (IDataUtil.isNotEmpty(datas)) {
            IData data = datas.getData(0);
            if (IDataUtil.isNotEmpty(data)) {
                productInfo.put("PRODUCT_ID", data.getString("CATALOG_ID"));
                productInfo.put("PRODUCT_NAME", data.getString("CATALOG_NAME"));
                productInfo.put("PRODUCT_MODE", BofConst.PRODUCT_MODE_SALE_ACTIVE);
                // productInfo.put("CAMPN_TYPE", data.getString("UP_CATALOG_ID"));

                IData cha = UpcCall.queryTempChaByCond(productid, "TD_B_PRODUCT");
                productInfo.putAll(cha);
            }
        }

        return productInfo;
    }

    public static IDataset getProductsByTypeForGroup(String product_type_code, Pagination pg) throws Exception {
        IDataset offers = UpcCall.qryOffersByCatalogId(product_type_code, pg);

        if (IDataUtil.isNotEmpty(offers)) {
            for (int i = 0, size = offers.size(); i < size; i++) {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
            }
        }

        return offers;
    }

    public static IDataset getProductsByType(String product_type_code) throws Exception {
        IDataset offers = UpcCall.qryOffersByCatalogId(product_type_code);

        if (IDataUtil.isNotEmpty(offers)) {
            for (int i = 0, size = offers.size(); i < size; i++) {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
            }
        }

        return offers;
    }

    public static IDataset queryProductByComp(String productId, String relation_type_code, String force_tag) throws Exception {
        IDataset offers = UpcCall.queryOfferJoinRelAndOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, relation_type_code, force_tag, null);
        if (IDataUtil.isNotEmpty(offers)) {
            for (int i = 0, size = offers.size(); i < size; i++) {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID_A", productId);
                offer.put("PRODUCT_ID_B", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
                offer.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(offer.getString("SELECT_FLAG")));
            }

        }
        return offers;
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
    public static String queryMemProductIdByProductId(String productId) throws Exception {
        IDataset joinRelOffers = UpcCall.queryOfferJoinRelAndOfferByOfferId("P", productId, "1", "", "");
        if (IDataUtil.isEmpty(joinRelOffers)) {
            return ""; // 应该要报错的
        }

        return joinRelOffers.getData(0).getString("OFFER_CODE");
    }

    /**
     * 根据品牌编码查询产品列表 duhj
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductListByBRAND(String brandCode) throws Exception {
        IDataset offers = UpcCall.queryProdSpecByBrandCodeAndProductMode(brandCode);

        if (IDataUtil.isNotEmpty(offers)) {
            for (int i = 0, size = offers.size(); i < size; i++) {
                IData offer = offers.getData(i);
                offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
            }
        }
        return offers;
    }

    /**
     * 根据PARENT_PTYPE_CODE获取产品类型
     * 
     * @param upcatalogId
     * @return
     * @throws Exception
     */
    public static IDataset getProductsType(String upcatalogId, Pagination pagin) throws Exception {
        IDataset results = UpcCall.qryCatalogsByUpCatalogId(upcatalogId, pagin);
        if (IDataUtil.isNotEmpty(results)) {
            for (int i = 0; i < results.size(); i++) {
                IData productTypeData = results.getData(i);
                productTypeData.put("PRODUCT_TYPE_CODE", productTypeData.getString("CATALOG_ID", ""));
                productTypeData.put("PRODUCT_TYPE_NAME", productTypeData.getString("CATALOG_NAME", ""));
                productTypeData.put("PARENT_PTYPE_CODE", productTypeData.getString("UP_CATALOG_ID", ""));
                productTypeData.put("DEFAULT_TAG", productTypeData.getString("DEFAULT_TAG", ""));
                productTypeData.put("NODE_COUNT", "1");
            }
        }
        return results;
    }

    /**
     * 查询产品信息
     * 
     * @param productId
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IData getProductInfo(String productId) throws Exception {
        IData productInfo = UpcCall.qryTotalOfferInfoByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
        if (IDataUtil.isNotEmpty(productInfo)) {
            productInfo.put("PRODUCT_ID", productInfo.getString("OFFER_CODE", ""));
            productInfo.put("PRODUCT_NAME", productInfo.getString("OFFER_NAME", ""));
            productInfo.put("PRODUCT_EXPLAIN", productInfo.getString("DESCRIPTION", ""));
            productInfo.put("COMP_TAG", productInfo.getString("IS_COMP", ""));
            productInfo.put("ENABLE_TAG", productInfo.getString("ENABLE_MODE", ""));
            productInfo.put("START_DATE", productInfo.getString("VALID_DATE", ""));
            productInfo.put("END_DATE", productInfo.getString("EXPIRE_DATE", ""));
            productInfo.put("RELEASE_TAG", productInfo.getString("STATE", ""));
            productInfo.put("UPDATE_STAFF_ID", productInfo.getString("OP_ID", ""));
            productInfo.put("UPDATE_DEPART_ID", productInfo.getString("ORG_ID", ""));
            productInfo.put("UPDATE_TIME", productInfo.getString("DONE_DATE", ""));
            if (StringUtils.isNotBlank(productInfo.getString("GROUP_ID"))) {
                productInfo.put("PACKAGE_ID", productInfo.getString("GROUP_ID"));
            } else {
                productInfo.put("PACKAGE_ID", "-1");
            }
            IDataset offerComChas = UpcCall.queryOfferComChaByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
            String productMode = "";
            if (IDataUtil.isNotEmpty(offerComChas)) {
                for (int j = 0; j < offerComChas.size(); j++) {
                    if ("PRODUCT_MODE".equals(offerComChas.getData(j).getString("FIELD_NAME", ""))) {
                        productMode = offerComChas.getData(j).getString("FIELD_VALUE", "");
                    }
                }
            }
            productInfo.put("PRODUCT_MODE", productMode);
            return productInfo;
        }
        return null;
    }

    public static IDataset getProductsByTypeWithTransNoPriv(String productId, String product_type_code) throws Exception {
        IDataset productInfos = UpcCall.qryJoinRelOfferInfoByOfferIdCatalogId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, product_type_code);
        if (IDataUtil.isNotEmpty(productInfos)) {
            for (int i = 0; i < productInfos.size(); i++) {
                IData productInfo = productInfos.getData(i);
                productInfo.put("PRODUCT_ID", productInfo.getString("OFFER_CODE", ""));
                productInfo.put("PRODUCT_NAME", productInfo.getString("OFFER_NAME", "") + "(" + productInfo.getString("OFFER_CODE", "") + ")");
                productInfo.put("PRODUCT_EXPLAIN", productInfo.getString("DESCRIPTION", ""));
                productInfo.put("COMP_TAG", productInfo.getString("IS_COMP", ""));
                productInfo.put("ENABLE_TAG", productInfo.getString("ENABLE_MODE", ""));
                productInfo.put("START_DATE", productInfo.getString("VALID_DATE", ""));
                productInfo.put("END_DATE", productInfo.getString("EXPIRE_DATE", ""));
                productInfo.put("RELEASE_TAG", productInfo.getString("STATE", ""));
                productInfo.put("UPDATE_STAFF_ID", productInfo.getString("OP_ID", ""));
                productInfo.put("UPDATE_DEPART_ID", productInfo.getString("ORG_ID", ""));
                productInfo.put("UPDATE_TIME", productInfo.getString("DONE_DATE", ""));
                productInfo.put("RIGHT_CODE", productInfo.getString("RIGHT_CODE", ""));
                if (StringUtils.isNotBlank(productInfo.getString("GROUP_ID"))) {
                    productInfo.put("PACKAGE_ID", productInfo.getString("GROUP_ID"));
                } else {
                    productInfo.put("PACKAGE_ID", "-1");
                }
            }
        }
        return productInfos;
    }

    /**
     * 获取元素失效方式
     * 
     */
    public static String queryElementEnable(String offerType, String offerCode) throws Exception {
        IDataset dataset = UpcCall.queryOfferEnableModeByCond(offerType, offerCode);
        if (IDataUtil.isNotEmpty(dataset)) {
            return dataset.first().getString("CANCEL_MODE");
        } else {
            return "";
        }
    }

    public static IData queryProductTransInfo(String oldProductId, String newProductId) throws Exception {
        IData enableMode = UpcCall.queryOfferTransOffer(oldProductId, "P", newProductId, "P");
        if (IDataUtil.isEmpty(enableMode)) {
            CSAppException.apperr(ProductException.CRM_PRODUCT_4);
        }
        IData rst = new DataMap();
        rst.put("ENABLE_TAG", enableMode.getString("ENABLE_MODE", "0"));
        rst.put("START_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_ENABLE_DATE"));
        rst.put("START_OFFSET", enableMode.getString("ENABLE_OFFSET"));
        rst.put("START_UNIT", enableMode.getString("ENABLE_UNIT"));
        rst.put("END_ENABLE_TAG", enableMode.getString("DISABLE_MODE"));
        rst.put("END_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_DISABLE_DATE"));
        rst.put("END_OFFSET", enableMode.getString("DISABLE_OFFSET"));
        rst.put("END_UNIT", enableMode.getString("DISABLE_UNIT"));
        rst.put("CANCEL_TAG", enableMode.getString("CANCEL_MODE"));
        rst.put("CANCEL_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_CANCEL_DATE"));
        rst.put("CANCEL_OFFSET", enableMode.getString("CANCEL_OFFSET"));
        rst.put("CANCEL_UNIT", enableMode.getString("CANCEL_UNIT"));
        return rst;
    }

    public static IData queryProductEnableMode(String productId) throws Exception {
        IDataset enableModes = UpcCall.queryOfferEnableModeByCond("P", productId);
        if (IDataUtil.isEmpty(enableModes)) {
            return null;
        }
        IData enableMode = enableModes.getData(0);
        IData rst = new DataMap();
        rst.put("ENABLE_TAG", enableMode.getString("ENABLE_MODE"));
        rst.put("START_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_ENABLE_DATE"));
        rst.put("START_OFFSET", enableMode.getString("ENABLE_OFFSET"));
        rst.put("START_UNIT", enableMode.getString("ENABLE_UNIT"));
        rst.put("END_ENABLE_TAG", enableMode.getString("DISABLE_MODE"));
        rst.put("END_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_DISABLE_DATE"));
        rst.put("END_OFFSET", enableMode.getString("DISABLE_OFFSET"));
        rst.put("END_UNIT", enableMode.getString("DISABLE_UNIT"));
        rst.put("CANCEL_TAG", enableMode.getString("CANCEL_MODE"));
        rst.put("CANCEL_ABSOLUTE_DATE", enableMode.getString("ABSOLUTE_CANCEL_DATE"));
        rst.put("CANCEL_OFFSET", enableMode.getString("CANCEL_OFFSET"));
        rst.put("CANCEL_UNIT", enableMode.getString("CANCEL_UNIT"));
        return rst;
    }

    public static IDataset queryAllProductElements(String productId) throws Exception {
        IDataset allElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", productId, null, null);
        if (IDataUtil.isEmpty(allElements)) {
            return null;
        }

        for (Object obj : allElements) {
            IData element = (IData) obj;
            element.put("ELEMENT_TYPE_CODE", element.getString("OFFER_TYPE"));
            element.put("ELEMENT_ID", element.getString("OFFER_CODE"));
            element.put("ELEMENT_NAME", element.getString("OFFER_NAME"));
            String flag = element.getString("FLAG");
            element.put("PRODUCT_ID", productId);
            element.put("PACKAGE_ID", "-1");
            if ("1".equals(element.getString("FORCE_TAG"))) {
                element.put("ELEMENT_FORCE_TAG", "1");
            } else {
                element.put("ELEMENT_FORCE_TAG", "0");
            }

            if ("1".equals(element.getString("GROUP_FORCE_TAG"))) {
                element.put("PACKAGE_FORCE_TAG", "1");
            } else {
                element.put("ELEMENT_FORCE_TAG", "0");
            }

            if ("1".equals(element.getString("DEFAULT_TAG"))) {
                element.put("ELEMENT_DEFAULT_TAG", "1");
            } else {
                element.put("ELEMENT_DEFAULT_TAG", "0");
            }

            if ("1".equals(element.getString("GROUP_DEFAULT_TAG"))) {
                element.put("PACKAGE_DEFAULT_TAG", "1");
            } else {
                element.put("PACKAGE_DEFAULT_TAG", "0");
            }

            if (StringUtils.isNotBlank(element.getString("GROUP_ID"))) {
                element.put("PACKAGE_ID", element.getString("GROUP_ID"));
            }
        }
        return allElements;
    }

    public static IDataset getSaleActivePackageByProduct(String productId) throws Exception {
        IDataset offers = UpcCall.qryOffersByCatalogId(productId, null);

        if (IDataUtil.isNotEmpty(offers)) {
            for (int i = 0; i < offers.size(); i++) {
                offers.getData(i).put("PACKAGE_ID", offers.getData(i).getString("OFFER_CODE"));
                offers.getData(i).put("PACKAGE_NAME", offers.getData(i).getString("OFFER_NAME"));
                offers.getData(i).put("PRODUCT_ID", productId);
            }
            return offers;
        } else {
            return new DatasetList();
        }
    }

    /**
     * 
     * @Title: getProductExtChaInfoByProductId
     * @Description: 获取产品表的备用字段
     * @param @param productId
     * @param @return
     * @param @throws Exception 设定文件
     * @return IData 返回类型
     * @throws
     */
    public static IData getProductExtChaInfoByProductId(String productId) throws Exception {
        IDataset results = UpcCall.qryOfferExtChaByOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, "TD_B_PRODUCT");
        if (IDataUtil.isEmpty(results)) {
            return new DataMap();
        }
        return results.getData(0);
    }

    public static IDataset getProductNamesByProductInfos(IDataset productInfos) throws Exception {
        List<String> productIds = new ArrayList<String>();
        IDataset offerTypeCodes = new DatasetList();
        for (Object object : productInfos) {
            IData elementData = (IData) object;
            String productId = elementData.getString("PRODUCT_ID");
            if (productIds.indexOf(productId) > -1) {
                continue;
            }
            productIds.add(productId);
            IData data = new DataMap();
            data.put("OFFER_CODE", productId);
            data.put("OFFER_TYPE", "P");
            offerTypeCodes.add(data);
        }
        IDataset results = UpcCall.qryOfferNamesByOfferTypesOfferCodes(offerTypeCodes, null);

        return results;
    }

    public static IDataset getProductNamesCataLogIdsByProductInfos(IDataset productInfos) throws Exception {
        List<String> productIds = new ArrayList<String>();
        IDataset offerTypeCodes = new DatasetList();
        for (Object object : productInfos) {
            IData elementData = (IData) object;
            String productId = elementData.getString("PRODUCT_ID");
            if (productIds.indexOf(productId) > -1) {
                continue;
            }
            productIds.add(productId);
            IData data = new DataMap();
            data.put("OFFER_CODE", productId);
            data.put("OFFER_TYPE", "P");
            offerTypeCodes.add(data);
        }
        IDataset results = UpcCall.qryOfferNamesByOfferTypesOfferCodes(offerTypeCodes, "Y");

        return results;
    }

    public static String getOfferIdByProductId(String productId) throws Exception {
        OfferCfg offercfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (offercfg == null) {
            return "";
        }
        return offercfg.getOfferId();
    }
}
