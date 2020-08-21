package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.catalog;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.group.esop.query.BusiFlowReleInfoQuery;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;

public class GroupCatalogsQry extends CSBizBean {
    private static final Logger logger = Logger.getLogger(GroupCatalogsQry.class);

    public static IDataset loadGroupProductsCatalogForAll(String upCatalogId, String productTypeCode, String custId) throws Exception {
        IDataset dataset = new DatasetList();
        if (StringUtils.isBlank(custId)) {
            return dataset;

        }

        if (StringUtils.isBlank(productTypeCode)) {// 查询目录
            dataset = null;// UProductTypeInfoQry.getProductsType(upCatalogId, null);
        } else {// 查询目录下的产品
            dataset = null;// UProductInfoQry.getProductsByTypeForGroup(productTypeCode, CSBizBean.getTradeEparchyCode(), null);
        }

        return dataset;
    }

    public static IDataset loadGroupProductsCatalogForOrderedOneTime(String parentTypeCode, String custId) throws Exception {

        IDataset treeInfoset = new DatasetList();
        if (StringUtils.isBlank(custId))
            return treeInfoset;

        if (StringUtils.isEmpty(parentTypeCode))
            return treeInfoset;
        String tradeStaffId = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("PARENT_PTYPE_CODE", parentTypeCode);
        param.put("TRADE_STAFF_ID", tradeStaffId);
        param.put("CUST_ID", custId);

        IDataset products = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCT_TYPE", param, Route.CONN_CRM_CG);
        // 根据权限过滤产品
        ProductPrivUtil.filterProductListByPriv(tradeStaffId, products);
        if (IDataUtil.isEmpty(products))
            return treeInfoset;
        // 父产品类型下的所有产品类型
        IDataset productTypeParamsDataset = null;// UProductTypeInfoQry.getProductsType(parentTypeCode, null);
        IDataset productTypeList = new DatasetList();
        for (int j = 0, size = products.size(); j < size; j++) {
            IData product = products.getData(j);
            String productId = product.getString("PRODUCT_ID");
            if ("-1".equals(productId)) {
                continue;
            }

            IDataset productTypeDataset = null;// UProductTypeInfoQry.getProductTypesByProductId(productId);
            if (IDataUtil.isEmpty(productTypeDataset)) {
                continue;
            }
            String productTypeCode = "";
            String productTypeName = "";
            for (int h = 0, hlen = productTypeDataset.size(); h < hlen; h++) {
                IData productTypeTemp = productTypeDataset.getData(h);
                String productTypeCodeTemp = productTypeTemp.getString("PRODUCT_TYPE_CODE", "");
                IDataset productTypeParamsDatasetTemp = DataHelper.filter(productTypeParamsDataset, "PRODUCT_TYPE_CODE=" + productTypeCodeTemp);
                if (IDataUtil.isNotEmpty(productTypeParamsDatasetTemp)) {
                    productTypeCode = productTypeCodeTemp;
                    productTypeName = productTypeParamsDatasetTemp.getData(0).getString("PRODUCT_TYPE_NAME");
                    break;
                }
            }
            if (StringUtils.isEmpty(productTypeCode)) {
                continue;
            }
            IDataset productTypeTemp = DataHelper.filter(productTypeList, "PRODUCT_TYPE_CODE=" + productTypeCode);
            if (IDataUtil.isNotEmpty(productTypeTemp)) {
                IData productTypeData = productTypeTemp.getData(0);
                IDataset productParamset = productTypeData.getDataset("PRODUCT_LIST");
                if (IDataUtil.isEmpty(productParamset)) {
                    productParamset = new DatasetList();
                }
                IDataset productParamsetTemp = DataHelper.filter(productParamset, "PRODUCT_ID=" + productId);
                if (IDataUtil.isEmpty(productParamsetTemp)) {
                    IData productData = new DataMap();
                    productData.put("PRODUCT_ID", productId);
                    productData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                    productData.put("PRODUCT_TYPE_CODE", productTypeCode);
                    productParamset.add(productData);
                    productTypeData.put("PRODUCT_LIST", productParamset);
                }
            } else {
                IData productTypeData = new DataMap();
                productTypeData.put("PRODUCT_TYPE_CODE", productTypeCode);
                productTypeData.put("PRODUCT_TYPE_NAME", productTypeName);
                IDataset productParamset = new DatasetList();
                IData productData = new DataMap();
                productData.put("PRODUCT_ID", productId);
                productData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                productData.put("PRODUCT_TYPE_CODE", productTypeCode);
                productParamset.add(productData);
                productTypeData.put("PRODUCT_LIST", productParamset);
                productTypeList.add(productTypeData);
            }
        }

        return productTypeList;

    }

    public static IDataset loadOfferCatalogForOrdered(String catalogId, String custId) throws Exception {
        IDataset catalogList = new DatasetList();
        if (StringUtils.isBlank(custId)) {
            return catalogList;
        }

        if (StringUtils.isEmpty(catalogId)) {
            return catalogList;
        }

        IDataset userProductList = getGrpProductByGrpCustIdProID(custId, null);// Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCT_TYPE", param, Route.CONN_CRM_CG);

        // 根据权限过滤产品
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), userProductList);
        if (IDataUtil.isEmpty(userProductList)) {
            return catalogList;
        }

        IDataset productTypeParamsDataset = UpcCall.qryCatalogsByUpCatalogId(catalogId);
        IDataset productTypeList = new DatasetList();
        for (int j = 0, size = userProductList.size(); j < size; j++) {
            IData userProduct = userProductList.getData(j);
            String productId = userProduct.getString("PRODUCT_ID");
            if ("-1".equals(productId)) {
                continue;
            }

            IData offer = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
            if (IDataUtil.isEmpty(offer)) {
                continue;
            }

            IDataset productTypeDataset = UpcCall.qryCatalogByOfferId(productId, "P");
            if (IDataUtil.isEmpty(productTypeDataset)) {
                continue;
            }
            String productTypeCode = "";
            String productTypeName = "";
            for (int h = 0, hlen = productTypeDataset.size(); h < hlen; h++) {
                IData productTypeTemp = productTypeDataset.getData(h);
                String productTypeCodeTemp = productTypeTemp.getString("CATALOG_ID", "");
                IDataset productTypeParamsDatasetTemp = DataHelper.filter(productTypeParamsDataset, "CATALOG_ID=" + productTypeCodeTemp);
                if (IDataUtil.isNotEmpty(productTypeParamsDatasetTemp)) {
                    productTypeCode = productTypeCodeTemp;
                    productTypeName = productTypeParamsDatasetTemp.getData(0).getString("CATALOG_NAME");
                    break;
                }
            }
            if (StringUtils.isEmpty(productTypeCode)) {
                continue;
            }
            IDataset productTypeTemp = DataHelper.filter(productTypeList, "CATALOG_ID=" + productTypeCode);
            if (IDataUtil.isNotEmpty(productTypeTemp)) {
                IData productTypeData = productTypeTemp.getData(0);
                IDataset productParamset = productTypeData.getDataset("OFFER_LIST");
                if (IDataUtil.isEmpty(productParamset)) {
                    productParamset = new DatasetList();
                }
                IData productData = new DataMap();
                productData.put("OFFER_CODE", productId);
                productData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                productData.put("USER_ID", userProduct.getString("USER_ID"));
                productData.put("SERIAL_NUMBER", userProduct.getString("SERIAL_NUMBER"));
                productData.put("CATALOG_ID", productTypeCode);
                productParamset.add(productData);
                productTypeData.put("OFFER_LIST", productParamset);
            } else {
                IData productTypeData = new DataMap();
                productTypeData.put("CATALOG_ID", productTypeCode);
                productTypeData.put("CATALOG_NAME", productTypeName);
                IDataset productParamset = new DatasetList();
                IData productData = new DataMap();
                productData.put("OFFER_CODE", productId);
                productData.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                productData.put("USER_ID", userProduct.getString("USER_ID"));
                productData.put("SERIAL_NUMBER", userProduct.getString("SERIAL_NUMBER"));
                productData.put("CATALOG_ID", productTypeCode);
                productParamset.add(productData);
                productTypeData.put("OFFER_LIST", productParamset);
                productTypeList.add(productTypeData);
            }
        }
        return productTypeList;
    }

    /**
     * 
     * @param custId
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getGrpProductByGrpCustIdProID(String custId, String productId) throws Exception {
        IData inparams = new DataMap();
        inparams.put("CUST_ID", custId);
        inparams.put("PRODUCT_ID", productId);
        IDataset userProductInfo = Dao.qryByCodeParser("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID", inparams);
        if (IDataUtil.isNotEmpty(userProductInfo)) {
            for (int i = 0; i < userProductInfo.size(); i++) {
                IData data = userProductInfo.getData(i);
                String productName = UpcCall.qryOfferNameByOfferTypeOfferCode(data.getString("PRODUCT_ID"), "P");
                data.put("PRODUCT_NAME", productName);
            }
        }
        return userProductInfo;
    }

    /**
     * 
     * @Title: getproductTypeCodeByProductId
     * @Description: 根据产品id获得上级目录id
     * @param @param productId
     * @param @return
     * @param @throws Exception 设定文件
     * @return String 返回类型
     * @throws
     */
    public static IDataset getProductTypeByProductId(String productId) throws Exception {
        IDataset cataRelDatas = null;
        try {
            cataRelDatas = UpcCall.qryCatalogByOfferId(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            if (IDataUtil.isNotEmpty(cataRelDatas)) {
                for (int i = 0, size = cataRelDatas.size(); i < size; i++) {
                    cataRelDatas.getData(i).put("PRODUCT_TYPE_CODE", cataRelDatas.getData(i).getString("CATALOG_ID"));
                }
            }
        }
        catch (Exception e) {
            if (e.getMessage().indexOf("UPC_ERROR_28") > -1) // 根据offer_code和offer_type查offer_id的错误暂时不抛错
            {
                return cataRelDatas;
            }
            throw e;
        }

        return cataRelDatas;
    }

    /**
     * 查询成员商品组信息
     * 
     * @param offerId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryMebGroupByOfferId(String offerId, String eparchyCode) throws Exception {
        IDataset mebGroupInfo = UpcCall.queryMebGroupByOfferId(offerId, eparchyCode);

        return mebGroupInfo;
    }

    /**
     * EOP开通申请过滤商品目录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset filterOfferCatalogForEopApply(IData input) throws Exception {
        IDataset result = new DatasetList();

        String operType = input.getString("OPER_TYPE");
        IDataset filterCatalogList = EweConfigQry.qryDistinctValueDescByParamName("EOP_FLOW_OFFER", operType);
        IDataset catalogList = input.getDataset("CATALOG_LIST");
        if (IDataUtil.isNotEmpty(filterCatalogList) && IDataUtil.isNotEmpty(catalogList)) {
            for (int i = 0, sizeI = catalogList.size(); i < sizeI; i++) {
                IData catalog = catalogList.getData(i);
                for (int j = 0, sizeJ = filterCatalogList.size(); j < sizeJ; j++) {
                    if (catalog.getString("CATALOG_ID").equals(filterCatalogList.getData(j).getString("VALUEDESC"))) {
                        result.add(catalog);
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * EOP开通申请过滤商品列表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset filterOfferForEopApply(IData input) throws Exception {
        IDataset result = new DatasetList();

        String operType = input.getString("OPER_TYPE");
        logger.debug("====busiSpecReleList===+operType=====" + operType);
        String busiType = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "APPLY_REQUIREMENT_OPER_TYPE", operType });
        logger.debug("====busiSpecReleList===+busiType=====" + busiType);
        IDataset busiSpecReleList = BusiFlowReleInfoQuery.getBusiCodeByBusitype(busiType, "0");
        logger.debug("====busiSpecReleList===busiSpecReleList=====" + busiSpecReleList);
        IDataset offerList = input.getDataset("OFFER_LIST");
        if (IDataUtil.isNotEmpty(offerList) && IDataUtil.isNotEmpty(busiSpecReleList)) {
            for (int i = 0, sizeI = offerList.size(); i < sizeI; i++) {
                IData offer = offerList.getData(i);
                for (int j = 0, sizeJ = busiSpecReleList.size(); j < sizeJ; j++) {
                    if (offer.getString("OFFER_CODE").equals(busiSpecReleList.getData(j).getString("PROD_SPEC_ID"))) {
                        result.add(offer);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static IDataset qrySelAllBySn(IData param) throws Exception {
        return Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_SN", param);
    }
}
