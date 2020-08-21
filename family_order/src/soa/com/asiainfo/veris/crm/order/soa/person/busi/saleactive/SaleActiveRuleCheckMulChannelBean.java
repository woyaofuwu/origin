package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/8 15:23
 */
public class SaleActiveRuleCheckMulChannelBean extends CSBizBean {
    /**
     *  获取营销活动
     * */
    public static IDataset queryCampnTypes(IData inParam) throws Exception
    {
        IData param = new DataMap();
        param.put("LABEL_ID", "S1000");
        IDataset result = UpcCall.qryChildrenCatalogsByIdLevel("4", "S1000");//UpcCall.qryCataLogsByTypeRootLevel("K", "PERSON", "2");
        for(int i=0; i<result.size(); i++)
        {
            IData info = result.getData(i);
            info.put("LABEL_ID", info.getString("CATALOG_ID"));
            info.put("LABEL_NAME", info.getString("CATALOG_NAME"));
        }
        return filterCreditPurchasesCampnTypes(result);
    }

    /**
     * 根据活动类型获取产品*
     * */
    public static IDataset querySaleActiveProductByLabel(IData inParam) throws Exception
    {
        String labelId = inParam.getString("CAMPN_TYPE");
        String eparchyCode = inParam.getString("EPARCHY_CODE");
        IData cond = new DataMap();
        cond.put("LABEL_ID", labelId);
        cond.put("EPARCHY_CODE", eparchyCode);

        IDataset productInfos = new DatasetList();
        IDataset results = UpcCall.qryCatalogsByUpCatalogId(labelId);
        for(int i=0;i<results.size();i++)
        {
            IData result = results.getData(i);
            IDataset paraInfos = CommparaInfoQry.getCommNetInfo("CSM", "522", result.getString("CATALOG_ID"));
            if(IDataUtil.isNotEmpty(paraInfos))
            {
                continue;
            }
            result.put("PRODUCT_ID", result.getString("CATALOG_ID"));
            result.put("PRODUCT_NAME", result.getString("CATALOG_NAME"));
            productInfos.add(result);
        }
        return filterCreditPurchasesProducts(productInfos);

//        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_LABEL_ID", cond, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品ID获取包*
     * */
    public static IDataset queryPackageByProdID(IData inParam) throws Exception
    {
        String prodId = inParam.getString("PRODUCT_ID");
        String eparchyCode = inParam.getString("EPARCHY_CODE");
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", prodId);
        cond.put("EPARCHY_CODE", eparchyCode);

        IDataset saleActives = UpcCall.qryOffersByCatalogId(prodId);
        for(int i=0;i<saleActives.size();i++)
        {
            IData saleActive = saleActives.getData(i);
            saleActive.put("PACKAGE_ID", saleActive.getString("OFFER_CODE"));
            saleActive.put("PACKAGE_NAME", saleActive.getString("OFFER_CODE") + "|" + saleActive.getString("OFFER_NAME"));
            saleActive.put("PRODUCT_ID", prodId);
        }
        return filterCreditPurchasesSaleActives(saleActives);
//        return Dao.qryByCode("TD_B_PACKAGE", "SEL_PACKAGE_BY_PRODUCTID_BAT", cond, Route.CONN_CRM_CEN);
    }



    /**
     * 过滤返回信用购机活动合约类型
     * @param campnTypes
     * @return
     * @throws Exception
     */
    public static IDataset filterCreditPurchasesCampnTypes(IDataset campnTypes) throws Exception{
        if(IDataUtil.isEmpty(campnTypes)){
            return null;
        }
        IDataset commparaInfos = CommparaInfoQry.getCommByParaAttr("CSM", "3119","0898");
        Set<String> campnTypesOfCreditPurchases = new HashSet<String>();
        Set<String> catalogIdsOfCreditPurchases = new HashSet<String>();
        if(IDataUtil.isNotEmpty(commparaInfos)){
            for (int i = 0; i < commparaInfos.size(); i++) {
                IData commparaInfo = commparaInfos.getData(i);
                String catalogId=commparaInfo.getString("PARAM_CODE");
                if(catalogIdsOfCreditPurchases.contains(catalogId)){
                    continue;
                }else{
                    catalogIdsOfCreditPurchases.add(catalogId);
                }
                if(StringUtils.isNotBlank(catalogId)){
                    IDataset catalogInfos = UpcCall.qryCatalogByCatalogId(catalogId);
                    if(IDataUtil.isNotEmpty(catalogInfos)){
                        String upCatalogId = catalogInfos.first().getString("UP_CATALOG_ID");
                        campnTypesOfCreditPurchases.add(upCatalogId);
                    }
                }
            }
        }

        IDataset results = new DatasetList();
        for (String campnTypeOfCreditPurchase : campnTypesOfCreditPurchases) {
            for (int j = 0; j < campnTypes.size(); j++) {
                IData campnType = campnTypes.getData(j);
                if(campnTypeOfCreditPurchase.equals(campnType.getString("CATALOG_ID"))){
                    results.add(campnType);
                }

            }
        }
        return results;
    }


    /**
     * 过滤返回信用购机活动
     * @param products
     * @return
     * @throws Exception
     */
    public static IDataset filterCreditPurchasesProducts(IDataset products) throws Exception{
        if(IDataUtil.isEmpty(products)){
            return null;
        }
        IDataset commparaInfos =  CommparaInfoQry.getCommByParaAttr("CSM", "3119","0898");
        IDataset results = new DatasetList();
        Set<String> productIdsOfCreditPurchases = new HashSet<String>();
        if(IDataUtil.isNotEmpty(commparaInfos)) {
            for (int j = 0; j < commparaInfos.size(); j++) {
                IData commparaInfo = commparaInfos.getData(j);
                String productIdOfCreditPurchases = commparaInfo.getString("PARAM_CODE");
                if (productIdsOfCreditPurchases.contains(productIdOfCreditPurchases)) {
                    continue;
                } else {
                    productIdsOfCreditPurchases.add(productIdOfCreditPurchases);
                    for (int i = 0; i < products.size(); i++) {
                        IData product = products.getData(i);
                        String productId = product.getString("PRODUCT_ID");
                        if (StringUtils.isNotBlank(productIdOfCreditPurchases)
                                && productIdOfCreditPurchases.equals(productId)) {
                            results.add(product);
                        }
                    }
                }
            }
        }
        return results;
    }


    /**
     * 过滤返回信用购机活动包
     * @param saleActives
     * @return
     * @throws Exception
     */
    public static IDataset filterCreditPurchasesSaleActives(IDataset saleActives) throws Exception{
        if(IDataUtil.isEmpty(saleActives)){
            return null;
        }
        IDataset commparaInfos =  CommparaInfoQry.getCommByParaAttr("CSM", "3119","0898");
        IDataset results = new DatasetList();
        if(IDataUtil.isNotEmpty(commparaInfos)){
            for (int i = 0; i < saleActives.size(); i++) {
                IData saleActive = saleActives.getData(i);
                String packageId = saleActive.getString("PACKAGE_ID");
                for (int j = 0; j < commparaInfos.size(); j++) {
                    IData commparaInfo = commparaInfos.getData(j);
                    String packageIdOfCreditPurchases=commparaInfo.getString("PARA_CODE4");
                    if(StringUtils.isNotBlank(packageIdOfCreditPurchases)
                            &&packageIdOfCreditPurchases.equals(packageId)){
                        results.add(saleActive);
                    }
                }
            }
        }
        return results;
    }
}
