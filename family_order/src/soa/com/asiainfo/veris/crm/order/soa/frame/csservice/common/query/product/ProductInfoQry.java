
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.SvcPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;

public class ProductInfoQry extends CSBizBean
{
    /**
     * 根据多个PRODUCTID mode 00 15查询产品信息
     * 
     * @author sunxin
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getBatProductInfo(String strBindProducts) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", strBindProducts);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCT_ID_BAT", data, Route.CONN_CRM_CEN);
    }
    
    /**
     * 根据一个PRODUCTIDid 查询产品信息shenhai
     * 
     * @author shenhai
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getProductInfoByid(String strProductid) throws Exception
    {
        IDataset results = new DatasetList();
        IData productInfo = UProductInfoQry.qryProductByPK(strProductid);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            //获取产品生失效方式
            IDataset offerEnableMode = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PRODUCT, strProductid);
            
            if (IDataUtil.isNotEmpty(offerEnableMode))
            {
                productInfo.put("ENABLE_TAG", offerEnableMode.getData(0).getString("ENABLE_MODE"));
            }
            
            results.add(productInfo);
        }
        return results;
    }

    public static IDataset getBindleProductsBrand(String productMode, String brandCode, String releaseTag, String eparchyCode, String productId, String www_capacity) throws Exception
    {
        throw new Exception("TD_B_PRODUCT表失效，请联系管理员");// 暂时报错失效 用到在改
        /*IData param = new DataMap();
        param.put("PRODUCT_MODE", productMode);
        param.put("BRAND_CODE", brandCode);
        param.put("RELEASE_TAG", releaseTag);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("PRODUCT_ID", productId);
        param.put("WWW_CAPACITY", www_capacity);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BINDLEPRODUCTS_BRAND", param, Route.CONN_CRM_CEN);*/
    }

    /**
     * 获取自建宽带包元素
     * 
     * @param packageId
     * @param eparchyCode
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public static IDataset getBroadBandPackageElements(String packageId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PACKAGE_ID", packageId);
        params.put("EPARCHY_CODE", eparchyCode);

        IDataset elementList = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKAGE_ELEMENT_FORKD", params);

        // 根据员工工号过滤元素权限
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), elementList);

        return elementList;
    }

    /**
     * 获取自建宽带包元素
     * 
     * @param packageId
     * @param eparchyCode
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2013-10-26
     */
    public static IDataset getBroadBandPackageElementsForBundleCharges(String productId, String eparchyCode, String discntCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PACKAGE_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("MAIN_DISCNT_CODE", discntCode);

        IDataset elementList = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BROADBAND_BUNDLE_ELEMENT", params);

        // 根据员工工号过滤元素权限
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), elementList);

        return elementList;
    }


    /**
     * 根据TRADE_EPARCHY_CODE查询cmnet产品信息
     * 
     * @author chenzm
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getCmnetProductInfo(String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_CMNET_PRODUCT", data, Route.CONN_CRM_CEN);
    }


    /**
     * 根据号段与产品id获取数据
     * 
     * @author sunxin
     * @param serialNumber
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getDefaultProductById(String serialNumber, String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PHONECODE_S", serialNumber);
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_DEFPRODUCT_PHONE", "SEL_BY_PRODUCTID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据号段获取默认产品
     * 
     * @author chenzm
     * @param serialNumber
     * @param resKindCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getDefaultProductByPhone(String serialNumber, String resKindCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PHONECODE_S", serialNumber);
        data.put("CODE_TYPE_CODE", resKindCode);
        data.put("TRADE_EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_DEFPRODUCT_PHONE", "SEL_BY_PHONE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据号码类型获取默认产品
     * 
     * @author chenzm
     * @param serialNumber
     * @param resKindCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getDefaultProductByResType(String serialNumber, String resKindCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PHONECODE_S", serialNumber);
        data.put("CODE_TYPE_CODE", resKindCode);
        data.put("TRADE_EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_DEFPRODUCT_CODETYPE", "SEL_BY_CODETYPE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 获取集团定制规则表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDesignRuleByPk(String productId, String packageId, String elementId, String elementTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PACKAGE_ID", packageId);
        data.put("ELEMENT_ID", elementId);
        data.put("ELEMENT_TYPE_CODE", elementTypeCode);

        return Dao.qryByCodeParser("TD_B_GROUP_DESIGN_RULE", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
    }

    /**
     * 获取集团定制规则明细表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getDetailRuleByRuleId(String ruleId) throws Exception
    {
        IData data = new DataMap();
        data.put("RULE_ID", ruleId);

        return Dao.qryByCodeParser("TD_B_GROUP_DESIGN_DETAILRULE", "SEL_BY_RULEID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 获取用户有效的服务
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getDigitalByUserId(String userId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_DIGITAL_BY_USERID", param);
    }

    /**
     * 根据package_id 查询包中的资费元素
     * 
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IDataset getDiscntElementByPackage(String packageId, String userId, String eparchyCode, boolean privForEle) throws Exception
    {
        ProductInfoQry bean = new ProductInfoQry();
        IData data = new DataMap();
        if (userId == null)
        {
            data.put("USER_ID", "0000000000000000");
        }
        else
        {
            data.put("USER_ID", userId);
        }
        IDataset elements = null;
        String user_id = data.getString("USER_ID");
        if (privForEle)
        {
            elements = PkgElemInfoQry.getDiscntElementByPackage(packageId, user_id);
        }
        else
        {
            elements = bean.getDiscntElementByPackageNoPriv(packageId, eparchyCode, user_id);
        }
        IDataset tmp2 = seFilterEnableElement(elements);
        return tmp2;
    }

    /*
     * 功能：通过集团PRODUCT_ID ELEMENT_ID 查询元素信息
     * @author
     * @param ctx
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getElementByProductIdElemId(String productId, String elementId) throws Exception
    {
    	return UPackageElementInfoQry.queryElementInfosByProductIdElementId(productId, elementId);
    }

    /**
     * 根据PRODUCT_TYPE_CODE,EPARCHY_CODE查询固网产品信息
     * 
     * @author chenzm
     * @param eparchy_code
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getFixedProductInfo(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_FOR_FIXED", data, Route.CONN_CRM_CEN);
    }    
    public static IDataset getGrpPackageByUserIdProductID(IDataset productList, String userId, boolean privForPack) throws Exception
    {
        IDataset resultset = new DatasetList();
        for (int i = 0; i < productList.size(); i++)
        {
            IDataset tempset = new DatasetList();
            String productID = productList.getData(i).getString("PRODUCT_ID", "");
            if (privForPack)
            {
                tempset = PkgInfoQry.getPackageByProductFromGrpPck(productID, userId);
            }
            else
            {
                tempset = PkgInfoQry.getPackageByProductFromGrpPckNoPriv(productID, userId);
            }

            IDataset packages = ProductUtil.getPackageByProduct(productID, null, CSBizBean.getVisit().getStaffEparchyCode(), privForPack);
            IData pack = new DataMap();
            for (int x = 0; x < packages.size(); x++)
            {
                pack = packages.getData(x);
                if ("1".equals(pack.getString("PACKAGE_TYPE_CODE")))
                {
                    tempset.add(pack);
                }
            }
            resultset.addAll(tempset);
        }
        return resultset;
    }

    /**
     * 查询集团产品下的包
     * 
     * @param productId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getGrpPackagesByProductId(String productId, String eparchyCode) throws Exception
    {
        IDataset packageList = new DatasetList();
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        packageList = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PACKAGE_BY_PRO", params, Route.CONN_CRM_CEN);

        return packageList;

    }

    public static IDataset getGrpProductList(Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT p.product_id, p.product_name,p.product_id||'|'||p.product_name showname ");
        parser.addSQL(" from td_b_ptype_product t,TD_B_PRODUCT p,TD_S_PRODUCT_TYPE pt ");
        parser.addSQL(" where pt.PARENT_PTYPE_CODE='1000' ");
        parser.addSQL(" AND t.product_id=p.product_id ");
        parser.addSQL(" AND t.product_type_code=pt.product_type_code ");
        parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND T.end_date ");
        parser.addSQL(" AND SYSDATE BETWEEN pt.start_date AND pt.end_date ");
        parser.addSQL(" AND product_obj_type = '1' ");
        parser.addSQL(" AND release_tag = '1' ");
        parser.addSQL(" AND EXISTS (SELECT 1 FROM td_b_product_release ");
        parser.addSQL(" WHERE (release_eparchy_code = :TRADE_EPARCHY_CODE OR release_eparchy_code = 'ZZZZ') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND p.product_id = product_id ) ");
        parser.addSQL(" ORDER BY product_id ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 获取在产品变更时的主产品列表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMainProductCollectionForChgProd(String productId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT", "SEL_MAIN_PROD_COLLECT_FOR_CHG_PROD", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 获取在开户时的主产品列表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMainProductCollectionForOpenUser(String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE", eparchyCode);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT", "SEL_MAIN_PROD_COLLECT_FOR_OPEN_USER", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据product_id查询成员可订购产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getMebProduct(String productId) throws Exception
    {
//        IData data = new DataMap();
//        data.put("PRODUCT_ID", productId);
//        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
//        IDataset mebproducts = Dao.qryByCode("TD_B_PRODUCT_MEB", "SEL_BY_PID", data, Route.CONN_CRM_CEN);

        IDataset mebproducts = UProductMebInfoQry.getMemberProductByGrpProductId(productId);
        // 根据员工工号过滤产品
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), mebproducts);

        return mebproducts;
    }

    public static IDataset getMebProductForceDiscnt(String productId, String eparchyCode) throws Exception
    {
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        
        return getProductForceDiscnt(mebProductId, eparchyCode);
    }

    public static IDataset getMebProductForcePlatSvc(String productId, String eparchyCode) throws Exception
    {
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        
        return getProductForcePlatSvc(mebProductId, eparchyCode);
    }

    public static IDataset getMebProductForceSvc(String productId, String eparchyCode) throws Exception
    {
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        
        return getProductForceSvc(mebProductId, eparchyCode);
    }

    public static IDataset getMebProductPlusDiscnt(String productId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_MEBPRODUCT_PLUS_DISCNT", params, Route.CONN_CRM_CEN);
    }

    public static IDataset getMebProductPlusPlatSvc(String productId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_MEBPRODUCT_PLUS_PLATSVC", params, Route.CONN_CRM_CEN);
    }

    public static IDataset getMebProductPlusSvc(String productId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_MEBPRODUCT_PLUS_SVC", params, Route.CONN_CRM_CEN);
    }

    public static IDataset getMebSaleProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_MEB", "SEL_BY_PID_SALE", data, Route.CONN_CRM_CEN);

        // IDataset tmp2 = seFilterEnableProduct(pd,datas);
        // 根据员工工号过滤产品
        ProductPrivUtil.filterProductListByPriv(CSBizBean.getVisit().getStaffId(), dataset);

        return dataset;
    }

    public static IDataset getMemberPackageElements(String grpUserId, String productId, String packageId, boolean privForPack) throws Exception
    {
        IDataset resultset = new DatasetList();

        String useTag = "0";
        if (StringUtils.isNotBlank(productId))
        {
            String userTagTemp = ProductCompInfoQry.getUseTagByProductId(productId);
            if (StringUtils.isNotEmpty(userTagTemp))
                useTag = userTagTemp;
        }

        IData inparams = new DataMap();
        inparams.put("PACKAGE_ID", packageId);
        inparams.put("USER_ID", grpUserId);

        resultset = UPackageElementInfoQry.getPackageElementInfoByPackageId(packageId);
        
        if(IDataUtil.isEmpty(resultset))
            return new DatasetList();
        
        // 定制
        if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag))
        {
            IDataset userGrpPackageSet = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_PACKAGE_ELEMENT_NO_PRIV", inparams, Route.CONN_CRM_CG);
            if(IDataUtil.isEmpty(userGrpPackageSet))
                return new DatasetList();
            
            for(int j = resultset.size() - 1; j >= 0 ; j-- ){
                IData result = resultset.getData(j);
                String offerCode = result.getString("OFFER_CODE");
                String offerType = result.getString("OFFER_TYPE");
                
                if(IDataUtil.isEmpty(DataHelper.filter(userGrpPackageSet, "ELEMENT_ID="+offerCode+",ELEMENT_TYPE_CODE="+offerType))){
                    resultset.remove(j);
                }
            }
            
        }
        if (privForPack)
            ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), resultset);

        return resultset;
    }
    
    public static IDataset getMemberJoinRelElements(String grpUserId, String productId, String eparchyCode,String categoryId, boolean privForPack) throws Exception
    {
        String useTag = "0";
        if (StringUtils.isNotBlank(productId))
        {
            String userTagTemp = ProductCompInfoQry.getUseTagByProductId(productId);
            if (StringUtils.isNotEmpty(userTagTemp))
                useTag = userTagTemp;
        }
        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        IDataset categorys = UpcCall.queryOffersByMultiCategory(mebProductId, eparchyCode, categoryId, "2");
        if(IDataUtil.isNotEmpty(categorys)){
            
            // 定制
            IDataset grpUsePackages = null;
            if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag))
            {
                IData inparams = new DataMap();
                inparams.put("PACKAGE_ID", "-1");
                inparams.put("USER_ID", grpUserId);
                
                grpUsePackages = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_PACKAGE_ELEMENT_NO_PRIV", inparams, Route.CONN_CRM_CG);
                if(IDataUtil.isEmpty(grpUsePackages))
                {
                    return grpUsePackages;
                }
            }
            
            for(int i = categorys.size() -1 ; i >= 0; i--)
            {
                IData category = categorys.getData(i);
                IDataset offerList = category.getDataset("OFFER_LIST");
                if(IDataUtil.isEmpty(offerList))
                {
                    categorys.remove(i);
                    continue;
                }
             
                if(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag))
                {
                    for(int j = offerList.size() -1 ; j >= 0; j--)
                    {
                        IData offer = offerList.getData(j);
                        String offerCode = offer.getString("OFFER_CODE");
                        String offerType = offer.getString("OFFER_TYPE");
                        
                        if(IDataUtil.isEmpty(DataHelper.filter(grpUsePackages, "ELEMENT_ID="+offerCode+",ELEMENT_TYPE_CODE="+offerType)))
                        {
                            offerList.remove(j);
                        }
                        
                    }
                    if(IDataUtil.isEmpty(offerList)){
                        categorys.remove(i);
                        continue;
                    }
                }
                
                if (privForPack)
                    ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), offerList);
                
            }
             
        }
        
        return categorys;
        
    }

    public static IDataset getMemberProductPackages(String grpUserId, String productId, boolean privForPack, String categoryId, String eparchyCode) throws Exception
    {
        IDataset results = new DatasetList();
        IDataset groups = new DatasetList();

        IData comp = ProductCompInfoQry.getProductFromComp(productId);
        if (IDataUtil.isEmpty(comp))
        {
            IData resultData = new DataMap();
            resultData.put("GROUPS", null);
            results.add(resultData);
            return results; // 考虑下是否报错
        }

        String useTag = comp.getString("USE_TAG");

        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        
        //获取主产品下的组
        IDataset offerGroups = UpcCall.queryOfferGroupRelOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, mebProductId);
        if (IDataUtil.isNotEmpty(offerGroups))
        {
            // 定制
            if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag))
            {
                
                IDataset userGrpPkgList = UserGrpPkgInfoQry.getUserGrpPackageIdForGrp(grpUserId);
                int resultsiz = offerGroups.size();
                for (int k = resultsiz - 1; k >= 0; k--)
                {
                    IData result = offerGroups.getData(k);
                    String resultPackageId = result.getString("PACKAGE_ID", "");
                    String resultProductId = result.getString("PRODUCT_ID", "");
                    
                    boolean existUserPackage = false;
                    for (int i = 0; i < userGrpPkgList.size(); i++)
                    {
                        IData userGrpPackage = userGrpPkgList.getData(i);
                        String userPackageId = userGrpPackage.getString("PACKAGE_ID", "");
                        String userProductId = userGrpPackage.getString("PRODUCT_ID", "");
                        if (userProductId.equals(resultProductId) && userPackageId.equals(resultPackageId))
                        {
                            existUserPackage = true;
                            break;
                        }
                    }
                    
                    if (!existUserPackage)
                        offerGroups.remove(k);
                }
            }
        }
      
        
        if(IDataUtil.isNotEmpty(offerGroups))
        {
            groups.addAll(offerGroups);
        }
        //构造用于获取打散商品的组
        if(StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL"))){
            IData group = new DataMap();
            group.put("GROUP_NAME", "其它");
            group.put("GROUP_ID", "-1");
            groups.add(group);
        }
        
        IData resultData = new DataMap();
        resultData.put("GROUPS", groups);
        resultData.put("MEB_PRODUCT_ID", mebProductId);
        results.add(resultData);

        return results;
    }

    /**
     * 查询集团产品成员营销包
     * 
     * @param grpUserId
     * @param productId
     * @param privForPack
     * @return
     * @throws Exception
     */
    public static IDataset getMemberProductSalePackages(String grpUserId, String productId, boolean privForPack) throws Exception
    {

        // 成员基本产品包
        IDataset memProductPackages = new DatasetList();
        // 成员附加产品包
        IDataset memPlusProductPackages = new DatasetList();

        // 成员附加基本产品ID
        String memPlusBaseProductId = "";

        // 成员附加产品
        IDataset memPlusProductList = new DatasetList();

        // 根据product_id查询成员可订购的营销产品
        IDataset memProductList = getMebSaleProduct(productId);

        // 成员附加产品
        for (int i = 0; i < memProductList.size(); i++)
        {
            IData memProduct = memProductList.getData(i);
            String productIdB = memProduct.getString("PRODUCT_ID_B");
            IData product = UProductInfoQry.qryProductByPK(productIdB);

            if (product.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.MEM_SALE_PRODUCT.getValue()))
            {
                memPlusBaseProductId = memProduct.getString("PRODUCT_ID_B");
            }
            else
            {
                memPlusProductList.add(memProduct);
            }
        }

        // 集团成员基本产品营销包
        if (memPlusBaseProductId != null && memPlusBaseProductId.length() > 0)
        {
            // 查询TF_F_USER_GRP_PACKAGE中定义的用户可订购的产品包
            memProductPackages = PkgInfoQry.getPackageByProductFromGrpPckNoPriv(memPlusBaseProductId, grpUserId);

            // 查询产品模型中产品中的包
            IDataset packages = getPackageByProductID(memPlusBaseProductId, CSBizBean.getVisit().getStaffEparchyCode());
            for (int i = 0; i < packages.size(); i++)
            {
                IData svcpack = packages.getData(i);
                // 服务包
                if ("1".equals(svcpack.getString("PACKAGE_TYPE_CODE")))
                {
                    memProductPackages.add(svcpack);
                }
            }

        }

        // 集团成员附加产品营销包
        for (int i = 0; i < memPlusProductList.size(); i++)
        {
            IData product = memPlusProductList.getData(i);
            String plusProductId = product.getString("PRODUCT_ID");

            memPlusProductPackages = PkgInfoQry.getPackageByProductFromGrpPckNoPriv(plusProductId, grpUserId);

            IDataset packages = getPackageByProductID(plusProductId, CSBizBean.getVisit().getStaffEparchyCode());
            for (int n = 0; n < packages.size(); n++)
            {
                IData pack = packages.getData(n);
                if ("1".equals(pack.getString("PACKAGE_TYPE_CODE")))
                {
                    memPlusProductPackages.add(pack);
                }
            }
        }

        memProductPackages.addAll(memPlusProductPackages);

        return memProductPackages;

    }

    public static IDataset getNpProductTrans(String productId, String brandCode, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        if (StringUtils.isNotBlank(brandCode))
        {
            param.put("BRAND_CODE", brandCode);
        }
        if (StringUtils.isNotBlank(startDate))
        {
            param.put("START_DATE", startDate);
        }
        if (StringUtils.isNotBlank(endDate))
        {
            param.put("END_DATE", endDate);
        }
        return Dao.qryByCodeParser("TD_S_PRODUCT_TRANS", "SEL_LIST_INTF_NP", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getPackageByProductID(String productId, String eparchyCode) throws Exception
    {

        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);

        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT pk.package_id, ");
        parser.addSQL(" pk.package_name, ");
        parser.addSQL(" pk.package_type_code, ");
        parser.addSQL(" pk.package_flag, ");
        parser.addSQL(" pk.package_desc, ");
        parser.addSQL(" pk.min_number, ");
        parser.addSQL(" pk.max_number, ");
        parser.addSQL(" ppk.force_tag, ");
        parser.addSQL(" ppk.default_tag, ");
        parser.addSQL(" ppk.product_id, ");
        parser.addSQL(" 0 NODE_COUNT, ");
        parser.addSQL(" ppk.product_id, ");
        parser.addSQL(" p.product_mode ");
        parser.addSQL(" FROM TD_B_PACKAGE pk, ");
        parser.addSQL(" TD_B_PRODUCT_PACKAGE ppk, ");
        parser.addSQL(" TD_B_PRODUCT p  ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" and ppk.product_id = :PRODUCT_ID ");
        parser.addSQL(" and ppk.product_id = p.product_id ");
        parser.addSQL(" AND (ppk.eparchy_code = :EPARCHY_CODE OR ppk.eparchy_code = 'ZZZZ') ");
        parser.addSQL(" AND pk.package_id = ppk.package_id ");
        parser.addSQL(" AND SYSDATE BETWEEN pk.start_date AND pk.end_date ");
        parser.addSQL(" ORDER BY PPK.ITEM_INDEX ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getPackageElements(String packageId, String eparchyCode) throws Exception
    {
        IDataset elementList = UPackageElementInfoQry.getPackageElementInfoByPackageId(packageId);

        // 根据员工工号过滤元素权限
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), elementList);

        return elementList;
    }
    
    public static IDataset getPackageElements(String productId, String packageId, String eparchyCode) throws Exception
    {
        IDataset elementList = UProductElementInfoQry.queryPackageElementsByProductIdPackageId(productId, packageId);

        // 根据员工工号过滤元素权限
        ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), elementList);
        
        DataHelper.sort(elementList, "OFFER_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);

        return elementList;
    }
    
    

    public static IDataset getPackageElementsNoPriv(String packageId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PACKAGE_ID", packageId);
        params.put("EPARCHY_CODE", eparchyCode);

        IDataset elementList = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKAGE_ELEMENT", params);

        return elementList;
    }

    public static IDataset getPackagesByProductId(String productId, String eparchyCode) throws Exception
    {
        IDataset packageList = UPackageInfoQry.getPackagesByProductId(productId);
        return packageList;

    }

    /**
     * 查询主产品关联的附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlusProductByProdId(String eparchyCode, String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE", eparchyCode);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        data.put("PRODUCT_ID", productId);
        IDataset productList = Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_PRODID_FOR_TREE", data, Route.CONN_CRM_CEN);

        // 根据员工工号过滤产品权限
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), productList);

        return productList;
    }

    public static IDataset getPlusProducts(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_PLUS_PRODUCTS", param);
    }

    /**
     * 根据服务ID获取产品信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IDataset getProBySerId(String service_id) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICE_ID", service_id);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_SERID", param, Route.CONN_CRM_CEN);
    }

    /** 根据IP直通车品牌查相关的产品信息 */
    public static IDataset getProductByBrandCodeIP01(String trade_eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE", trade_eparchy_code);

        return Dao.qryByCodeParser("TD_B_PRODUCT", "SEL_BY_BRAND_CODE_IP01", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductByDiscntStaffID(String productId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_EPARCHY_CODE", eparchyCode);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PRODUCT_DISCNT", "SEL_PRODUCT_DISCNT_BY_STAFFID", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductDefaultLong(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_DEFAULT_LONG", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductDefaultRoam(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_DEFAULT_ROAM", param, Route.CONN_CRM_CEN);
    }

    public static IData getProductElementByPkForCG(String packageId, String element_type_code, String element_id) throws Exception
    {
        
        IDataset datas = UPackageElementInfoQry.getPackageElementInfoByPidEidEtype(packageId, element_type_code, element_id);
        if (IDataUtil.isEmpty(datas))
        {
            return new DataMap();
        }
        else
        {
            return datas.getData(0);
        }
    }

    /**
     * 根据新PRODUCT_ID和ELEMENT_ID得到可以带到新产品的元素
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductElements(String productId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        //获取产品下所有元素
        return ProductUtils.offerToElement(UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "", ""), productId);
    }

    /**
     * HXYD-YZ-REQ-20120524-010-部分集团业务资费下线需求
     */
    public static IDataset getProductElementsInDiscntValidate(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT p.product_id, p.product_name, pe.element_id FROM td_b_product p, td_b_product_package pp, ");
        parser.addSQL("td_b_package_element pe WHERE p.product_id = pp.product_id ");
        parser.addSQL("AND pp.package_id = pe.package_id ");
        parser.addSQL("AND p.release_tag = '1' ");
        parser.addSQL("AND p.end_date > SYSDATE ");
        parser.addSQL("AND pe.update_staff_id = 'xuxf' ");
        parser.addSQL("AND pe.remark = 'HXYD-YZ-REQ-20120524-010-部分集团业务资费下线需求' ");
        parser.addSQL("AND pe.end_date > SYSDATE ");
        parser.addSQL("AND p.product_id = :PRODUCT_ID ");
        parser.addSQL("AND p.product_id IN ('9003','9022','22000249','22000348','5080','5081','5084','6100','8900','8901','8902', ");
        parser.addSQL("'8910','8920','9000','9001','9002','9003','9007','9009','9022','9142','9143','9148', ");
        parser.addSQL("'9149','9150') ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static String getProductExplanByProductId(String productId) throws Exception
    {

        return UProductInfoQry.getProductExplainByProductId(productId);
    }

    public static IDataset getProductForceDiscnt(String productId) throws Exception
    {
        return getProductForceDiscnt(productId,"");
    }

    public static IDataset getProductForceDiscnt(String productId, String eparchyCode) throws Exception
    {
        IDataset forceDisList = UProductElementInfoQry.queryForceDiscntsByProductId(productId);
        
        if(IDataUtil.isNotEmpty(forceDisList))
        {
            for(int i = 0, isize = forceDisList.size(); i < isize; i++)
            {
                IData tempforceSvc = forceDisList.getData(i);
                tempforceSvc.put("MODIFY_TAG", "0");
             }
        }
        
        return forceDisList;
    }
    
    public static IDataset getProductForcePlatSvc(String productId) throws Exception
    {
        return getProductForcePlatSvc(productId,"");
    }

    public static IDataset getProductForcePlatSvc(String productId, String eparchyCode) throws Exception
    {
        IDataset forceDisList = UProductElementInfoQry.queryForcePlatSvcsByProductId(productId);
        
        if(IDataUtil.isNotEmpty(forceDisList))
        {
            for(int i = 0, isize = forceDisList.size(); i < isize; i++)
            {
                IData tempforceSvc = forceDisList.getData(i);
                tempforceSvc.put("MODIFY_TAG", "0");
             }
        }
        
        return forceDisList;
    }
    
    public static IDataset getProductForceSvc(String productId) throws Exception
    {
        return getProductForceSvc(productId, "");
    }

    public static IDataset getProductForceSvc(String productId, String eparchyCode) throws Exception
    {
        IDataset forceSvcList = UProductElementInfoQry.queryForceSvcsByProductId(productId);
       
        if(IDataUtil.isNotEmpty(forceSvcList))
        {
            for(int i = 0, isize = forceSvcList.size(); i < isize; i++)
            {
                IData tempforceSvc = forceSvcList.getData(i);
                tempforceSvc.put("MODIFY_TAG", "0");
             }
        }
        
//        IDataset defaultSvcList = CommparaInfoQry.getCommNetInfo("CSM", "84", productId);
//        if(IDataUtil.isNotEmpty(defaultSvcList)){
//        	for(int i=0;i<defaultSvcList.size();i++){
//        		IData defaultSvc = defaultSvcList.getData(i);
//        		String element_id = defaultSvc.getString("PARA_CODE2");
//        		IDataset check_element = DataHelper.filter(forceSvcList,"ELEMENT_ID=" + element_id);
//            	if(IDataUtil.isNotEmpty(check_element))
//            		continue;
//        		IData data = new DataMap();
//        		data.put("PRODUCT_ID", productId);
//        		data.put("PACKAGE_ID", defaultSvc.getString("PARA_CODE1"));
//        		data.put("ELEMENT_ID", defaultSvc.getString("PARA_CODE2"));
//        		data.put("ELEMENT_TYPE_CODE", defaultSvc.getString("PARA_CODE3"));
//        		data.put("ELEMENT_NAME", defaultSvc.getString("PARA_CODE4"));
//        		data.put("ENABLE_TAG", defaultSvc.getString("PARA_CODE5"));
//        		data.put("PACKAGE_FORCE_TAG", defaultSvc.getString("PARA_CODE6"));
//        		data.put("ELEMENT_FORCE_TAG", defaultSvc.getString("PARA_CODE7"));
//        		data.put("START_DATE", defaultSvc.getString("START_DATE"));
//        		data.put("END_DATE", defaultSvc.getString("END_DATE"));
//        		data.put("MAIN_TAG", "");
//        		data.put("MODIFY_TAG", "0");
//        		forceSvcList.add(data);
//        	}
//        }
        
        
        return forceSvcList;
    }

    public static IDataset getProductForGotone(String productId, String paramAttr) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SUBSYS_CODE", "CSM");
        inData.put("PARAM_ATTR", paramAttr);
        inData.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCT_ID_FOR_GOTONE", inData, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductForGotoneNew(String syscode, String productId, String eparchyCode, String userId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("SUBSYS_CODE", syscode);
        inData.put("PRODUCT_ID", productId);
        inData.put("EPARCHY_CODE", eparchyCode);
        inData.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCT_ID_FOR_GOTONE_NEW", inData, Route.CONN_CRM_CEN);
    }

    /**
     * 根据product_id查询TD_B_PRODUCT_COMP中的产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getProductFromComp(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);

        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_COMP", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /**
     * 查询产品信息
     * 
     * @param productId
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IData getProductInfo(String productId, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCTID", data, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(dataset))
        {
            return dataset.getData(0);
        }
        return null;
    }

    /**
     * 取已经下线的产品
     */
    public static IDataset getProductInfoByIDInValind(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PK_INVALID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductInfosByProductId(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PTYPE_PRODUCT", "SEL_PRODUCT_TYPE_BY_ID", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品品牌查询对应产品列表信息
     * 
     * @param param
     *            查询参数
     * @return 产品列表信息
     * @throws Exception
     * @author dongq
     */
    public static IDataset getProductListByBRAND(String brand_code, String product_mode, String eparchy_code, String product_id) throws Exception
    {
        IData data = new DataMap();
        data.put("BRAND_CODE", brand_code);
        data.put("PRODUCT_MODE", product_mode);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("PRODUCT_ID", product_id);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据新PRODUCT_ID和ELEMENT_ID得到可以带到当前用户可操作新产品元素
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductOperElements(String productId, String eparchyCode, String staffId) throws Exception
    {
        IDataset result = new DatasetList();
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("STAFF_ID", staffId);

        IDataset svcList = UpcCall.qryOfferChaByOfferId(productId, "P", "S");
        //IDataset svcList = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ALL_OPER_SVC_ELEMENTS", params, Route.CONN_CRM_CEN);
        SvcPrivUtil.filterSvcListByPriv(staffId, svcList);
        IDataset svcResult = new DatasetList();
        if (svcList != null && svcList.size() > 0)
        {
            String svcId = "";
            int size = svcList.size();
            IData tempSvc = null;
            for (int i = 0; i < size; i++)
            {
                IData svc = svcList.getData(i);
                if (!svcId.equals(svc.getString("ELEMENT_ID")))
                {
                    if (tempSvc != null)
                    {
                        svcResult.add(tempSvc);
                        tempSvc = null;
                    }

                    if ("".equals(svc.getString("ATTR_CODE", "")))
                    {
                        svcResult.add(svc);
                    }
                    else
                    {
                        tempSvc = svc;
                        IDataset attrTempList = new DatasetList();
                        IData attrTemp = new DataMap();
                        attrTemp.put("ATTR_CODE", svc.getString("ATTR_CODE"));
                        attrTemp.put("ATTR_VALUE", svc.getString("ATTR_INIT_VALUE"));
                        attrTempList.add(attrTemp);
                        tempSvc.put("ATTR_PARAM", attrTempList);
                        if (i == size - 1)
                            svcResult.add(tempSvc);
                    }
                }
                else
                {
                    IData attrTemp = new DataMap();
                    if(!"".equals(svc.getString("ATTR_CODE","")))
                    {
	                    attrTemp.put("ATTR_CODE", svc.getString("ATTR_CODE"));
	                    if (svc.getString("ATTR_CAN_NULL", "").equals("1"))
	                        attrTemp.put("ATTR_VALUE", "");
	                    else
	                        attrTemp.put("ATTR_VALUE", svc.getString("ATTR_INIT_VALUE"));
	
	                    tempSvc.getDataset("ATTR_PARAM").add(attrTemp);
                    }
                    if (i == size - 1)
                        svcResult.add(tempSvc);
                }
                svcId = svc.getString("ELEMENT_ID");
            }
        }

        result.addAll(svcResult);
        IDataset discntList = UpcCall.qryOfferChaByOfferId(productId, "P", "D");
        //IDataset discntList = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ALL_OPER_DISCNT_ELEMENTS", params, Route.CONN_CRM_CEN);
        DiscntPrivUtil.filterDiscntListByPriv(staffId, discntList);
        IDataset discntResult = new DatasetList();
        if (discntList != null && discntList.size() > 0)
        {
            String discntCode = "";
            int size = discntList.size();
            IData tempDiscnt = null;
            for (int i = 0; i < size; i++)
            {
                IData discnt = discntList.getData(i);
                if (!discntCode.equals(discnt.getString("ELEMENT_ID")))
                {
                    if (tempDiscnt != null)
                    {
                        discntResult.add(tempDiscnt);
                        tempDiscnt = null;
                    }

                    if ("".equals(discnt.getString("ATTR_CODE", "")))
                    {
                        discntResult.add(discnt);
                    }
                    else
                    {
                        tempDiscnt = discnt;
                        IDataset attrTempList = new DatasetList();
                        IData attrTemp = new DataMap();
                        attrTemp.put("ATTR_CODE", discnt.getString("ATTR_CODE"));
                        attrTemp.put("ATTR_VALUE", discnt.getString("ATTR_INIT_VALUE"));
                        attrTempList.add(attrTemp);
                        tempDiscnt.put("ATTR_PARAM", attrTempList);
                        if (i == size - 1)
                            discntResult.add(tempDiscnt);
                    }
                }
                else
                {
                    if("".equals(discnt.getString("ATTR_CODE", ""))){                        
                        discntResult.add(discnt);                        
                    }else{
                        
                        if (tempDiscnt != null && tempDiscnt.getDataset("ATTR_PARAM") != null) {
                    IData attrTemp = new DataMap();
                    attrTemp.put("ATTR_CODE", discnt.getString("ATTR_CODE"));
                    if (discnt.getString("ATTR_CAN_NULL", "").equals("1"))
                        attrTemp.put("ATTR_VALUE", "");
                    else
                        attrTemp.put("ATTR_VALUE", discnt.getString("ATTR_INIT_VALUE"));

                    tempDiscnt.getDataset("ATTR_PARAM").add(attrTemp);
                            if (i == size - 1) {
                                discntResult.add(tempDiscnt);
                            }
                        }else{                            
                            tempDiscnt = discnt;
                            IDataset attrTempList = new DatasetList();
                            IData attrTemp = new DataMap();
                            attrTemp.put("ATTR_CODE", discnt.getString("ATTR_CODE"));
                            attrTemp.put("ATTR_VALUE", discnt.getString("ATTR_INIT_VALUE"));
                            attrTempList.add(attrTemp);
                            tempDiscnt.put("ATTR_PARAM", attrTempList);
                            if (i == size - 1)
                                discntResult.add(tempDiscnt);
                        }
                        
                    }
                }
                discntCode = discnt.getString("ELEMENT_ID");
            }
        }

        result.addAll(discntResult);
        return result;
    }

    public static IDataset getProductPlusDiscnt(String productId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PRODUCT_PLUS_DISCNT", params, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductPlusSvc(String productId, String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PRODUCT_PLUS_SVC", params, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductsByBrand(String brand_code, String product_mode) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brand_code); // 品牌编码
        param.put("PRODUCT_MODE", product_mode); // 亲情产品模式
        IDataset poDatas= UpcCall.qryOffersByBrandWithProductModeFilter(brand_code,product_mode);
         if (!poDatas.isEmpty())
        {
            for (int i = 0; i < poDatas.size(); i++)
            {
                poDatas.getData(i).put("PRODUCT_ID", poDatas.getData(i).getString("OFFER_CODE", ""));
                poDatas.getData(i).put("PRODUCT_NAME", poDatas.getData(i).getString("OFFER_NAME", ""));
            }
        }
         return poDatas;
         //Dao.qryByCode("TD_B_PRODUCT", "SEL_PRODUCT_BY_BRAND", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询已发布的产品信息
     * 
     * @param brand_code
     * @param product_mode
     * @param eparchy_code
     * @param trade_staff_id
     * @return
     * @throws Exception
     *             wangjx 2013-7-29
     */
    public static IDataset getProductsBySelByBrandFamily(String brandCode, String productMode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_MODE", productMode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND_FAMILY2", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductsBySelbyBrandFamily2(String brand_code, String product_mode, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_MODE", product_mode);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND_FAMILY2", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductsBySelByBrandFamily3(String brand_code, String product_mode, String eparchy_code, String use_product, String next_prouct) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_MODE", product_mode);
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("USE_PRODUCT", use_product);
        param.put("NEXT_PROUCT", next_prouct);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND_FAMILY3", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductsBySelbyBrandFamilyNoenddate(String brand_code, String product_mode, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_MODE", product_mode);
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND_FAMILY_NOENDDATE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductsBySelByPModeBrand(String brandCode, String productMode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_MODE", productMode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND_FAMILY2", param, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 for group
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForGroup(String product_type_code, String eparchyCode, Pagination pg) throws Exception
    {
        return UProductInfoQry.getProductsByTypeForGroup(product_type_code, pg);
    }
    
    /**
     * 根据产品类型获取产品 for group
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForGroup(String product_type_code, Pagination pg) throws Exception
    {
        return UProductInfoQry.getProductsByTypeForGroup(product_type_code, pg);
    }

    /**
     * 根据产品类型获取产品 for group 总记录数
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForGroupTotalNum(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "CNT_BY_TYPE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 for person
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForMall(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_FOR_PERSON", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 for person 不判断权限
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForMallNoPriv(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
		IDataset returnResult = UpcCall.qryOffersByCatalogId(product_type_code);
		return returnResult;
		// return Dao.qryByCode("TD_B_PRODUCT",
		// "SEL_BY_TYPE_FOR_PERSON_NO_PRIV", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 for person 网上售卡
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForNetSaleCardPerson(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_FOR_NETSALECARD_PERSON", data, Route.CONN_CRM_CEN);
    }

    /**
     * 网上售卡业务查询产品 根据产品类型获取产品 for person 不判断权限
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForNetSaleCardPersonNoPriv(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_FOR_NETSALECARD_PERSON_NO_PRIV", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 for person
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForPerson(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_FOR_PERSON", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 for person 不判断权限
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeForPersonNoPriv(String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_FOR_PERSON_NO_PRIV", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 关联td_s_product_trans 产品变更用
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeWithTrans(String productId, String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_WITH_TRANS", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 关联td_s_product_trans 产品变更用
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductsByTypeWithTransNoPriv(String productId, String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_WITH_TRANS_NO_PRIV", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据PARENT_PTYPE_CODE获取产品类型
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductsType(String parent_ptype_code, Pagination pg) throws Exception
    {
        IData data = new DataMap();
        data.put("PARENT_PTYPE_CODE", parent_ptype_code);
        return Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_PARENT", data, pg, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductTrans(String productId, String brandCode, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        if (StringUtils.isNotBlank(brandCode))
        {
            param.put("BRAND_CODE", brandCode);
        }
        if (StringUtils.isNotBlank(startDate))
        {
            param.put("START_DATE", startDate);
        }
        if (StringUtils.isNotBlank(endDate))
        {
            param.put("END_DATE", endDate);
        }
        return Dao.qryByCodeParser("TD_S_PRODUCT_TRANS", "SEL_LIST_INTF", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getProductTransElement(String productId, String elementId, String elementTypeCode) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", productId);
        params.put("ELEMENT_ID", elementId);
        params.put("ELEMENT_TYPE_CODE", elementTypeCode);
        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ELEMENT_TRANS", params);
    }

    public static IDataset getProductTransInfo(String oldProductId, String newProductId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID_A", oldProductId);
        param.put("PRODUCT_ID_B", newProductId);
        return UpcCall.queryOfferJoinRelBy2OfferIdRelType("P",oldProductId,"P",newProductId);
        //return Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }
    
    //查询主套餐变更内容信息2018/09/10-wangsc10
    public static IDataset getProductTransInfoNew(String oldProductId, String newProductId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID_A", oldProductId);
        param.put("PRODUCT_ID_B", newProductId);
        return UpcCall.qryRelOfferEnableByOfferIdRelOfferId("P",oldProductId,"P",newProductId);
    }

    /**
     * 根据PRODUCT_TYPE_CODE查询产品类型信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getProductTypeByCode(String product_type_code) throws Exception
    {

        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        IDataset dataset = Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_CODE", data);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    public static String getProductTypeCodeByProductId(String productId) throws Exception
    {
        return StaticUtil.getStaticValue(getVisit(), "TD_B_PTYPE_PRODUCT", "PRODUCT_ID", "PRODUCT_TYPE_CODE", productId);

    }

    /**
     * 作用：根porduct_id查询主产品所需资源信息
     * 
     * @author luojh 2009-05-14 12:33
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getResTypeByMainProduct(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        IDataset result = new DatasetList();
        IDataset dataset = UProductElementInfoQry.queryForceSvcsByProductId(productId); 
        if(IDataUtil.isNotEmpty(dataset)){
        	for(int i=0; i<dataset.size();i++){
        		String svcId = dataset.getData(i).getString("ELEMENT_ID");
        		IDataset ds = qryResTypeBySvcId(svcId); 
        		if(IDataUtil.isNotEmpty(ds)){
        			result.add(ds.getData(0));
        		} 
        	}
        } 
        return result;
    }

    /**
     * 根据PRODUCT_ID,ELEMENT_TYPE_CODE,ELEMENT_ID查询BBOSS附加产品的元素
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getRsrvByPk(String package_id, String element_type_code, String element_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("ELEMENT_TYPE_CODE", element_type_code);
        data.put("ELEMENT_ID", element_id);
        IDataset datas = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_RSRV_BY_PK", data, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(datas))
        {
            return new DataMap();
        }
        else
        {
            return datas.getData(0);
        }
    }

    public static IDataset getrsrvstr2(String product_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        IDataset staticResSet = Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PK_ALL", data);

        return staticResSet;

    }

    /**
     * 查询集团定制的包中相关服务元素
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByGrpCustomize(String package_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("USER_ID", user_id);

        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_PACKID", data, Route.CONN_CRM_CG);
    }

    public static IDataset getServElementByGrpCustomizeNoPriv(String package_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_SERV_BY_PACKID_NO_PRIV", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据package_id查询包中服务相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPackage(String package_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("USER_ID", user_id);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_SERV_BY_PACKID", data);
    }

    /**
     * 根据package_id 查询包中的服务元素
     * 
     * @param packageId
     * @return
     * @throws Exception
     */
    public static IDataset getServElementByPackage(String packageId, String userId, boolean privForEle) throws Exception
    {
        ProductInfoQry bean = new ProductInfoQry();
        IData data = new DataMap();
        if (StringUtils.isBlank(userId))
            if (userId == null)
            {
                data.put("USER_ID", "0000000000000000");
            }
            else
            {
                data.put("USER_ID", userId);
            }
        IDataset elements = null;
        String user_id = data.getString("USER_ID");
        if (privForEle)
        {
            elements = ProductInfoQry.getServElementByPackage(packageId, user_id);
        }
        else
        {
            elements = bean.getServElementByPackageNoPriv(packageId, user_id);
        }
        IDataset tmp2 = seFilterEnableElement(elements);
        return tmp2;
    }

    /**
     * 根据PRODUCT_ID获取能转换的产品类型
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getTransProductsType(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_PRODUCTTYPE_BY_TRANS", data, Route.CONN_CRM_CEN);
    }

    /**
     * 获取用户服务信息, 必需要USER_ID,PRODUCT_ID,PACKAGE_ID,DISCNT_CODE,不需要USER_ID_A,START_DATE
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserDiscntByPk(String product_id, String user_id, String package_id, String discnt_code) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", product_id);
        param.put("USER_ID", user_id);
        param.put("PACKAGE_ID", package_id);
        param.put("DISC", discnt_code);

        IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USRDISCNT_BYID", param);
        return dataset;
    }

    /**
     * 根据产品类型查询用户已订购的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserMemberProductsByByProdType(IData data) throws Exception
    {
        // todo
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_MEB_BY_PROD_TYPE", data);
    }

    /**
     * 捞取现有成员用户已经订购的需发送HSS的产品列表,按集团产品ID升序排列
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserOrderIMSProduct(IData data) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_IMSPRODUCT_BY_USERID", data);
    }

    /**
     * 根据产品类型查询用户订购了那些产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserProduct(String cust_id, String vpnFlag) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", cust_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        if ("VPCN_VPMN".equals(vpnFlag))
        {
            data.put("PRODUCT_VPMN", "8000");
            data.put("PRODUCT_VPCN", "8010");
            return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE_VPN", data, null, Route.CONN_CRM_CG);
        }
        return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE", data, null, Route.CONN_CRM_CG);
    }

    /**
     * 根据user_Id查询用户已订购的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserProductByUserIdForGrp(String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_ALL_BY_USERID", data, Route.CONN_CRM_CG);
    }

    /**
     * 用于返回指定USER_ID,PRODUCT_ID,USER_ID_A的对应的用户元素(服务、优惠、资源) 新疆成员、集团注销时个人有个检测本结束优惠不能重复删的规则，所以捞优惠时只能捞结束时间大于本月底的！
     * 
     * @param params
     * @author jiangmj
     * @throws Exception
     */
    public static IDataset getUserProductElementForXinj(String userId, String user_id_a) throws Exception
    {

        IDataset userElements = new DatasetList();

        IDataset usersvcs = UserSvcInfoQry.getUserProductSvc(userId, user_id_a, null);
        for (int i = 0; i < usersvcs.size(); i++)
        {
            IData usersvc = usersvcs.getData(i);
            IData userelement = new DataMap();
            userelement.put("PRODUCT_ID", usersvc.getString("PRODUCT_ID"));
            userelement.put("PACKAGE_ID", usersvc.getString("PACKAGE_ID"));
            userelement.put("ELEMENT_TYPE_CODE", "S");
            userelement.put("ELEMENT_ID", usersvc.getString("SERVICE_ID"));
            userelement.put("ELEMENT_NAME", usersvc.getString("ELEMENT_NAME"));
            userelement.put("START_DATE", usersvc.getString("START_DATE"));
            userelement.put("END_DATE", usersvc.getString("END_DATE"));
            userelement.put("USER_ID", usersvc.getString("USER_ID"));
            userelement.put("USER_ID_A", usersvc.getString("USER_ID_A"));
            userelement.put("MAIN_TAG", usersvc.getString("MAIN_TAG"));
            userelement.put("INST_ID", usersvc.getString("INST_ID"));
            userelement.put("STATE", "DEL");
            userelement.put("REMARK", usersvc.getString("REMARK"));
            userelement.put("RSRV_NUM1", usersvc.getString("RSRV_NUM1", ""));// 预留数值1
            userelement.put("RSRV_NUM2", usersvc.getString("RSRV_NUM2", ""));// 预留数值2
            userelement.put("RSRV_NUM3", usersvc.getString("RSRV_NUM3", ""));// 预留数值3
            userelement.put("RSRV_NUM4", usersvc.getString("RSRV_NUM4", ""));// 预留数值4
            userelement.put("RSRV_NUM5", usersvc.getString("RSRV_NUM5", ""));// 预留数值5
            userelement.put("RSRV_STR1", usersvc.getString("RSRV_STR1", ""));// 预留字段1
            userelement.put("RSRV_STR2", usersvc.getString("RSRV_STR2", ""));// 预留字段2
            userelement.put("RSRV_STR3", usersvc.getString("RSRV_STR3", ""));// 预留字段3
            userelement.put("RSRV_STR4", usersvc.getString("RSRV_STR4", ""));// 预留字段4
            userelement.put("RSRV_STR5", usersvc.getString("RSRV_STR5", ""));// 预留字段5
            userelement.put("RSRV_DATE1", usersvc.getString("RSRV_DATE1", ""));// 预留日期1
            userelement.put("RSRV_DATE2", usersvc.getString("RSRV_DATE2", ""));// 预留日期2
            userelement.put("RSRV_DATE3", usersvc.getString("RSRV_DATE3", ""));// 预留日期3
            userelement.put("RSRV_TAG1", usersvc.getString("RSRV_TAG1", ""));// 预留标志1
            userelement.put("RSRV_TAG2", usersvc.getString("RSRV_TAG2", ""));// 预留标志2
            userelement.put("RSRV_TAG3", usersvc.getString("RSRV_TAG3", ""));// 预留标志3
            userelement.put("ELEMENT_TYPE_NAME", "服务");
            userElements.add(userelement);
        }
        IDataset userdisset = UserDiscntInfoQry.getUserProductDisctForXinj(userId, user_id_a, null);
        for (int i = 0; i < userdisset.size(); i++)
        {
            IData userdis = userdisset.getData(i);
            IData userelement = new DataMap();
            userelement.put("PRODUCT_ID", userdis.getString("PRODUCT_ID"));
            userelement.put("PACKAGE_ID", userdis.getString("PACKAGE_ID"));
            userelement.put("ELEMENT_TYPE_CODE", "D");
            userelement.put("ELEMENT_ID", userdis.getString("DISCNT_CODE"));
            userelement.put("ELEMENT_NAME", userdis.getString("ELEMENT_NAME"));
            userelement.put("START_DATE", userdis.getString("START_DATE"));
            userelement.put("END_DATE", userdis.getString("END_DATE"));
            userelement.put("USER_ID", userdis.getString("USER_ID"));
            userelement.put("USER_ID_A", userdis.getString("USER_ID_A"));
            userelement.put("SPEC_TAG", userdis.getString("SPEC_TAG"));
            userelement.put("RELATION_TYPE_CODE", userdis.getString("RELATION_TYPE_CODE"));
            userelement.put("INST_ID", userdis.getString("INST_ID"));
            userelement.put("STATE", "DEL");
            userelement.put("REMARK", userdis.getString("REMARK"));
            userelement.put("RSRV_NUM1", userdis.getString("RSRV_NUM1", ""));// 预留数值1
            userelement.put("RSRV_NUM2", userdis.getString("RSRV_NUM2", ""));// 预留数值2
            userelement.put("RSRV_NUM3", userdis.getString("RSRV_NUM3", ""));// 预留数值3
            userelement.put("RSRV_NUM4", userdis.getString("RSRV_NUM4", ""));// 预留数值4
            userelement.put("RSRV_NUM5", userdis.getString("RSRV_NUM5", ""));// 预留数值5
            userelement.put("RSRV_STR1", userdis.getString("RSRV_STR1", ""));// 预留字段1
            userelement.put("RSRV_STR2", userdis.getString("RSRV_STR2", ""));// 预留字段2
            userelement.put("RSRV_STR3", userdis.getString("RSRV_STR3", ""));// 预留字段3
            userelement.put("RSRV_STR4", userdis.getString("RSRV_STR4", ""));// 预留字段4
            userelement.put("RSRV_STR5", userdis.getString("RSRV_STR5", ""));// 预留字段5
            userelement.put("RSRV_DATE1", userdis.getString("RSRV_DATE1", ""));// 预留日期1
            userelement.put("RSRV_DATE2", userdis.getString("RSRV_DATE2", ""));// 预留日期2
            userelement.put("RSRV_DATE3", userdis.getString("RSRV_DATE3", ""));// 预留日期3
            userelement.put("RSRV_TAG1", userdis.getString("RSRV_TAG1", ""));// 预留标志1
            userelement.put("RSRV_TAG2", userdis.getString("RSRV_TAG2", ""));// 预留标志2
            userelement.put("RSRV_TAG3", userdis.getString("RSRV_TAG3", ""));// 预留标志3
            userelement.put("ELEMENT_TYPE_NAME", "优惠");
            userelement.put("ID", userdis.getString("USER_ID"));
            userelement.put("ID_TYPE", "1");
            userElements.add(userelement);
        }
        IDataset userresset = UserResInfoQry.getUserProductRes(userId, user_id_a, null);
        for (int i = 0; i < userresset.size(); i++)
        {
            IData userres = userresset.getData(i);
            IData userelement = new DataMap();
            userelement.put("PRODUCT_ID", "");
            userelement.put("PACKAGE_ID", "");
            userelement.put("ELEMENT_TYPE_CODE", "R");
            userelement.put("ELEMENT_ID", userres.getString("RES_CODE"));
            userelement.put("ELEMENT_NAME", userres.getString("ELEMENT_NAME"));
            userelement.put("START_DATE", userres.getString("START_DATE"));
            userelement.put("END_DATE", userres.getString("END_DATE"));
            userelement.put("USER_ID", userres.getString("USER_ID"));
            userelement.put("USER_ID_A", userres.getString("USER_ID_A"));
            userelement.put("RES_TYPE_CODE", userres.getString("RES_TYPE_CODE"));
            userelement.put("RES_CODE", userres.getString("RES_CODE"));
            userelement.put("IMSI", userres.getString("IMSI"));
            userelement.put("KI", userres.getString("KI"));
            userelement.put("INST_ID", userres.getString("INST_ID"));
            userelement.put("CAMPN_ID", userres.getString("CAMPN_ID"));
            userelement.put("STATE", "DEL");
            userelement.put("REMARK", userres.getString("REMARK"));
            userelement.put("RSRV_NUM1", userres.getString("RSRV_NUM1", ""));// 预留数值1
            userelement.put("RSRV_NUM2", userres.getString("RSRV_NUM2", ""));// 预留数值2
            userelement.put("RSRV_NUM3", userres.getString("RSRV_NUM3", ""));// 预留数值3
            userelement.put("RSRV_NUM4", userres.getString("RSRV_NUM4", ""));// 预留数值4
            userelement.put("RSRV_NUM5", userres.getString("RSRV_NUM5", ""));// 预留数值5
            userelement.put("RSRV_STR1", userres.getString("RSRV_STR1", ""));// 预留字段1
            userelement.put("RSRV_STR2", userres.getString("RSRV_STR2", ""));// 预留字段2
            userelement.put("RSRV_STR3", userres.getString("RSRV_STR3", ""));// 预留字段3
            userelement.put("RSRV_STR4", userres.getString("RSRV_STR4", ""));// 预留字段4
            userelement.put("RSRV_STR5", userres.getString("RSRV_STR5", ""));// 预留字段5
            userelement.put("RSRV_DATE1", userres.getString("RSRV_DATE1", ""));// 预留日期1
            userelement.put("RSRV_DATE2", userres.getString("RSRV_DATE2", ""));// 预留日期2
            userelement.put("RSRV_DATE3", userres.getString("RSRV_DATE3", ""));// 预留日期3
            userelement.put("RSRV_TAG1", userres.getString("RSRV_TAG1", ""));// 预留标志1
            userelement.put("RSRV_TAG2", userres.getString("RSRV_TAG2", ""));// 预留标志2
            userelement.put("RSRV_TAG3", userres.getString("RSRV_TAG3", ""));// 预留标志3
            userelement.put("ELEMENT_TYPE_NAME", "资源");
            userElements.add(userelement);
        }
        return userElements;
    }

    /**
     * 获取用户服务信息, 必需要USER_ID,PRODUCT_ID,PACKAGE_ID,SERVICE_ID,不需要USER_ID_A,INST_ID
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserServByPk(String user_id, String user_id_a, String product_id, String package_id, String service_id, String inst_id) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("USER_ID_A", user_id_a);
        data.put("PRODUCT_ID", product_id);
        data.put("PACKAGE_ID", package_id);
        data.put("SERVICE_ID", service_id);
        data.put("INST_ID", inst_id);

        IDataset dataset = Dao.qryByCode("TF_F_USER_SVC", "SEL_USRSVC_BYID", data);
        return dataset;
    }

    /**
     * 获取宽带产品信息
     * 
     * @param productMode
     * @param eparchyCode
     * @return IDataset
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset getWidenetProductInfo(String productMode, String eparchyCode) throws Exception
    {
//        IData inparam = new DataMap();
//        inparam.put("PRODUCT_MODE", productMode);
//        inparam.put("EPARCHY_CODE", eparchyCode);
        
        IDataset resultProducts = new DatasetList();
        
        IDataset productInfos = UpcCall.qryOffersByComCha("PRODUCT_MODE", productMode);
        
        if (IDataUtil.isNotEmpty(productInfos))
        {
            IData productInfo = null;
            
            for (int i = 0; i < productInfos.size(); i++)
            {
                productInfo = UProductInfoQry.qryProductByPK(productInfos.getData(i).getString("OFFER_CODE"));
                
                resultProducts.add(productInfo);
            }
            
            DataHelper.sort(resultProducts, "PRODUCT_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        //return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCTMODE", inparam, Route.CONN_CRM_CEN);
        
        return resultProducts;
    }

    /**
     * 获取和校园异网用户开户产品信息
     * 
     * @param productMode
     * @param eparchyCode
     * @return 
     * @throws Exception
     * @author zhaohj3
     */
    public static IDataset getHProductInfo(String productMode, String eparchyCode) throws Exception
    {
        IDataset resultProducts = new DatasetList();
        
        IDataset productInfos = UpcCall.qryOffersByComCha("PRODUCT_MODE", productMode); // 根据 销售品构成值 查询销售品信息
        
        if (IDataUtil.isNotEmpty(productInfos))
        {
            IData productInfo = null;
            
            for (int i = 0; i < productInfos.size(); i++)
            {
                productInfo = UProductInfoQry.qryProductByPK(productInfos.getData(i).getString("OFFER_CODE"));
                
                resultProducts.add(productInfo);
            }
            
            DataHelper.sort(resultProducts, "PRODUCT_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }
        
        return resultProducts;
    }
    
    public static IDataset qrySaleActiveProductInfo() throws Exception
    {     
    	IDataset productInfos = UpcCall.qrySaleActiveCatalogs();
        return productInfos;
    }


    /**
     * 根据产品类型获取宽带产品 for person
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getWideProductsByTypeForPerson(String product_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_WIDENET_PRODUCT_BY_TYPE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 判断产品是否集团可定制 查询TD_B_PRODUCT_COMP USE_TAG 是否集团定制：0：集团不可定制，1：集团可定制
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean ifGroupCustomize(String productId) throws Exception
    {
        IData tmp = getProductFromComp(productId);
        if (IDataUtil.isEmpty(tmp))
        {
            CSAppException.apperr(GrpException.CRM_GRP_66, productId);
            return false;
        }
        return "1".equals(tmp.getString("USE_TAG"));
    }

    /**
     * ADC/MAS业务查询 初始化菜单:产品类型
     * 
     * @param tradeData
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset initialAdcMasProductInfos(String eparchy_code, Pagination pagination) throws Exception
    {

        IData params = new DataMap();
        params.put("TRADE_EPARCHY_CODE", eparchy_code);
        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT p.product_id, p.product_name ");
        parser.addSQL(" from td_b_ptype_product t,TD_B_PRODUCT p,TD_S_PRODUCT_TYPE pt ");
        parser.addSQL(" where pt.PARENT_PTYPE_CODE='1000' ");
        parser.addSQL(" AND brand_code in ('ADCG','MASG') ");
        parser.addSQL(" AND t.product_id=p.product_id ");
        parser.addSQL(" AND t.product_type_code=pt.product_type_code ");
        parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND T.end_date ");
        parser.addSQL(" AND SYSDATE BETWEEN pt.start_date AND pt.end_date ");
        parser.addSQL(" AND product_obj_type = '1' ");
        parser.addSQL(" AND release_tag = '1' ");
        parser.addSQL(" AND EXISTS (SELECT 1 FROM td_b_product_release ");
        parser.addSQL(" WHERE (release_eparchy_code = :TRADE_EPARCHY_CODE OR release_eparchy_code = 'ZZZZ') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date ");
        parser.addSQL(" AND p.product_id = product_id ) ");
        parser.addSQL(" ORDER BY product_id ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 集团绑定号码综合查询 初始化菜单: 产品类型
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset initialECBindNumberProductInfos(Pagination pagination) throws Exception
    {

        IData params = new DataMap();

        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT p.product_id, p.product_name ");
        parser.addSQL(" from td_b_ptype_product t,TD_B_PRODUCT p,TD_S_PRODUCT_TYPE pt ");
        parser.addSQL(" where pt.PARENT_PTYPE_CODE='1000' ");
        parser.addSQL(" AND t.product_id=p.product_id ");
        parser.addSQL(" AND t.product_type_code=pt.product_type_code ");
        parser.addSQL(" AND SYSDATE BETWEEN t.start_date AND T.end_date ");
        parser.addSQL(" AND SYSDATE BETWEEN pt.start_date AND pt.end_date ");
        parser.addSQL(" AND product_obj_type = '1' ");
        parser.addSQL(" AND release_tag = '1' ");
        parser.addSQL(" AND EXISTS (SELECT 1 ");
        parser.addSQL(" FROM td_b_product_release ");
        parser.addSQL(" WHERE product_id = p.product_id ");
        parser.addSQL(" AND (release_eparchy_code = 'ZZZZ' OR release_eparchy_code = '0022') ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date) ");
        parser.addSQL(" ORDER BY product_id ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据tradeId获取备份的产品数据是否还有效
     * 
     * @param hisTradeId
     * @return
     * @throws Exception
     */
    public static boolean isBadProductInfo(String hisTradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("OLDTRADE_ID", hisTradeId);
        IDataset set = Dao.qryByCode("TD_B_PRODUCT", "SEL_BAD_PRODUCT", param);
        if (null != set || set.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 获取产品（成员产品）优惠
     * 
     * @param PRODUCT_ID
     *            产品ID（非成员产品ID）
     * @author wenjb
     */
    public static IDataset qryBBOSSMemDis(String product_id) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID", product_id);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT pe.*  ");
        parser.addSQL(" FROM td_b_product_package t ,td_b_package p,td_b_package_element pe,td_b_product_meb meb");
        parser.addSQL(" WHERE t.package_id=p.package_id and p.package_id=pe.package_id ");
        parser.addSQL(" and meb.product_id_b=t.product_id ");
        parser.addSQL(" and meb.product_id=:PRODUCT_ID ");
        return Dao.qryByParse(parser);
    }

    public static IDataset qryProductsByProductMode(String productMode) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_MODE", productMode);

        return ProductInfoQry.getWidenetProductInfo(productMode, "0898");
    }

    public static IDataset qryRelaByTypeCode(String relaTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RELATION_TYPE_CODE", relaTypeCode);

        return Dao.qryByCode("TD_B_PRODUCT_RELATION", "SEL_BY_TYPECODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryUserPrdByGrpId(String group_id) throws Exception
    {
        IData params = new DataMap();
        params.put("GROUP_ID", group_id);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT u.partition_id,u.user_id,p.group_id,u.serial_number,p.audit_state, ");
        parser.addSQL("        p.cust_name,u.open_date,t.product_id,t.product_name,t.start_date,t.end_date ");
        parser.addSQL(" FROM   tf_f_user u,tf_f_cust_group p,td_b_product t ");
        parser.addSQL(" WHERE  u.cust_id=p.cust_id ");
        parser.addSQL("        AND u.product_id=t.product_id ");
        parser.addSQL("        AND u.open_date < t.end_date ");
        parser.addSQL("        AND t.start_date+0<sysdate AND t.end_date+0>=sysdate ");
        parser.addSQL("        AND p.group_id= :GROUP_ID");
        parser.addSQL("        AND u.remove_tag='0' ");
        IDataset resIds = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return resIds;
    }

    /**
     * 查询用户元素信息
     * 
     * @param userId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset qryUserProductElement(String userId, String userIdA) throws Exception
    {
        IDataset userElementList = new DatasetList();

        // 查询用户服务
        IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);

        for (int i = 0, size = userSvcList.size(); i < size; i++)
        {
            IData userSvcData = userSvcList.getData(i);

            userSvcData.put("ELEMENT_TYPE_CODE", "S");
            userSvcData.put("ELEMENT_TYPE_NAME", "服务");
            userSvcData.put("ELEMENT_ID", userSvcData.getString("SERVICE_ID"));

            userElementList.add(userSvcData);
        }

        // 查询用户资费信息
        IDataset userDiscntList = UserDiscntInfoQry.getUserProductDis(userId, userIdA);

        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            IData userDiscntData = userDiscntList.getData(i);

            userDiscntData.put("ELEMENT_TYPE_CODE", "D");
            userDiscntData.put("ELEMENT_TYPE_NAME", "优惠");
            userDiscntData.put("ELEMENT_ID", userDiscntData.getString("DISCNT_CODE"));

            userElementList.add(userDiscntData);
        }

        // 查询用户资源信息
        IDataset userResList = UserResInfoQry.getUserProductRes(userId, userIdA, null);

        for (int i = 0, size = userResList.size(); i < size; i++)
        {
            IData userResData = userResList.getData(i);

            userResData.put("ELEMENT_TYPE_CODE", "R");
            userResData.put("ELEMENT_TYPE_NAME", "资源");
            userResData.put("ELEMENT_ID", userResData.getString("RES_CODE"));

            userElementList.add(userResData);
        }

        return userElementList;
    }
    
    /**
     * 查询物联网用户元素信息
     * 
     * @param userId
     * @param userIdA
     * @return
     * @throws Exception
     */
    public static IDataset qryPwlwProductElement(String userId, String userIdA) throws Exception
    {
        IDataset userElementList = new DatasetList();

        // 查询用户服务
        IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);

        for (int i = 0, size = userSvcList.size(); i < size; i++)
        {
            IData userSvcData = userSvcList.getData(i);

            userSvcData.put("ELEMENT_TYPE_CODE", "S");
            userSvcData.put("ELEMENT_TYPE_NAME", "服务");
            userSvcData.put("ELEMENT_ID", userSvcData.getString("SERVICE_ID"));

            userElementList.add(userSvcData);
        }

        // 查询用户资费信息
        IDataset userDiscntList = UserDiscntInfoQry.getPwlwProductDis(userId, userIdA);

        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            IData userDiscntData = userDiscntList.getData(i);

            userDiscntData.put("ELEMENT_TYPE_CODE", "D");
            userDiscntData.put("ELEMENT_TYPE_NAME", "优惠");
            userDiscntData.put("ELEMENT_ID", userDiscntData.getString("DISCNT_CODE"));

            userElementList.add(userDiscntData);
        }

        // 查询用户资源信息
        IDataset userResList = UserResInfoQry.getUserProductRes(userId, userIdA, null);

        for (int i = 0, size = userResList.size(); i < size; i++)
        {
            IData userResData = userResList.getData(i);

            userResData.put("ELEMENT_TYPE_CODE", "R");
            userResData.put("ELEMENT_TYPE_NAME", "资源");
            userResData.put("ELEMENT_ID", userResData.getString("RES_CODE"));

            userElementList.add(userResData);
        }

        return userElementList;
    }

    public static IDataset queryByPkgId(String packageId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryContractInfoByIdAndResType(String resType, String productId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId);
        cond.put("PARA_CODE", resType);
        cond.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_CONTRACTSALE_BY_PRDID_RESTYPE", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryContractSaleByResType(String resType, String eparchyCode) throws Exception
    {
//        IData cond = new DataMap();
//        cond.put("PARA_CODE", resType);
//        cond.put("EPARCHY_CODE", eparchyCode);
//
//        return Dao.qryByCode("TD_B_PRODUCT", "SEL_CONTRACTSALE_BY_RESTYPE", cond, Route.CONN_CRM_CEN);
        IDataset result = UpcCall.qryContractMaterialOffer("08", resType, eparchyCode);
        for(int i=0;i<result.size();i++)
        {
            IData data = result.getData(i);
            data.put("PRODUCT_NAME", data.getString("OFFER_NAME"));
            data.put("PRODUCT_ID", data.getString("OFFER_CODE"));
        }
        return result;
    }

    public static IData queryDefultLongSvc(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);

        IDataset tmp = Dao.qryByCode("TD_B_PRODUCT", "SEL_DL_BY_PK", param, Route.CONN_CRM_CEN);

        return tmp.isEmpty() ? new DataMap() : tmp.getData(0);
    }

    public static IData queryDefultRoamSvc(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);

        IDataset tmp = Dao.qryByCode("TD_B_PRODUCT", "SEL_DR_BY_PK", param, Route.CONN_CRM_CEN);

        return tmp.isEmpty() ? new DataMap() : tmp.getData(0);
    }

    /**
     * 查询服务、资费生效方式、失效方式
     * 
     * @throws Exception
     */
    public static String queryElementEnable(String package_id, String element_type_code, String element_id, String tag) throws Exception
    {

        IDataset results = UPackageElementInfoQry.getPackageElementInfoByPidEidEtype(package_id, element_type_code, element_id);
        if (IDataUtil.isEmpty(results))
        {
            return "0";
        }
        IData result = results.getData(0);
        String returnTag = result.getString(tag, "0");
        return returnTag;
    }
    
    /**
     * 查询服务、资费生效方式、失效方式
     * 
     * @throws Exception
     */
    public static String queryElementEnable(String productId, String package_id, String element_type_code, String element_id, String tag) throws Exception
    {

        IData result = UPackageElementInfoQry.queryElementEnableMode(productId, package_id, element_id, element_type_code) ;
        if (IDataUtil.isEmpty(result))
        {
            return "0";
        }
        String returnTag = result.getString(tag, "0");
        return returnTag;
    }
    
    /**
     * 查询服务、资费生效方式、失效方式、扩展字段
     * 
     * @throws Exception
     */
    public static String queryElementEnableExtend(String productId, String package_id, String element_type_code, String element_id, String tag) throws Exception
    {

        IData result = UProductElementInfoQry.queryElementInfoByProductIdAndPackageIdAndElementId(productId, package_id, element_id, element_type_code, "Y") ;
        if (IDataUtil.isEmpty(result))
        {
            return "0";
        }
        String returnTag = result.getString(tag, "0");
        return returnTag;
    }

    /**
     * 根据产品查询礼包类的营销包
     * 
     * @param data
     * @return
     * @throws Exception
     * @author awx
     * @date 2009-9-22
     */
    public static IDataset queryGiftSalePackageByPRoductId(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PACKAGE", "SEL_GIFT_SALEPACKAGE_BY_PID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 集团用户订购的产品
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupOrderProduct(String cust_id) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", cust_id);

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT u.product_id, u.serial_number,u.open_date,u.brand_code,u.user_id,c.product_name ");
        parser.addSQL(" FROM TF_F_USER u, TD_B_PRODUCT c ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND u.cust_id=:CUST_ID   ");
        parser.addSQL(" AND u.remove_tag='0' ");
        parser.addSQL(" AND c.product_id=u.product_id   ");
        parser.addSQL(" AND SYSDATE BETWEEN start_date AND end_date  ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    public static IDataset queryMainProductByBrand(String brand_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("BRAND_CODE", brand_code);
        data.put("EPARCHY_CODE", eparchy_code);
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT p.*");
        parser.addSQL(" FROM td_b_product p");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND p.BRAND_CODE = :BRAND_CODE");
        parser.addSQL(" AND p.product_mode = '00'");
        parser.addSQL(" AND p.RELEASE_TAG = '1'");
        parser.addSQL(" AND sysdate between p.start_date and p.end_date");
        parser.addSQL(" and exists (SELECT 1 FROM td_b_product_release " + " WHERE (release_eparchy_code = :EPARCHY_CODE OR release_eparchy_code = 'ZZZZ')" + " AND SYSDATE BETWEEN start_date AND end_date" + " AND p.product_id = product_id)");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryNameByProductId(String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT '00' PRODUCT_MODE,PRODUCT_NAME NAME,'' DISCNT_END_DATE FROM TD_B_PRODUCT WHERE PRODUCT_ID = :PRODUCT_ID");

        return Dao.qryByParse(parser);
    }

    /**
     * 根据产品查询购机类的营销包
     * 
     * @param data
     * @return
     * @throws Exception
     * @author awx
     * @date 2009-9-22
     */
    public static IDataset queryPhoneSalePackageByPRoductId(String productId, String rsrv_str2) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("RSRV_STR2", rsrv_str2);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PACKAGE", "SEL_PHONE_SALEPACKAGE_BY_PID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }

    public static IDataset queryProductAllPackages(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_ALL_PACKAGE", data, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryPackageByPID(String packageId,String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PACKAGE_BY_PID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 查询资费包下的产品信息
     * 
     * @author shixb
     * @version 创建时间：2009-5-13 下午08:08:22
     */
    public static IDataset queryProductByComp(String productId, String relation_type_code, String force_tag) throws Exception
    {
        
        return UProductInfoQry.queryProductByComp(productId, relation_type_code, force_tag);
    }

    /**
     * 查询组合产品表的子产品，有权限限制
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProductByCompRight(String productId, String relation_type_code, String force_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("RELATION_TYPE_CODE", relation_type_code);
        param.put("FORCE_TAG", force_tag);
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCT_COMP_RIGHT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryProductByLimitTagAndProductId(String limitTag, String productA, String productB) throws Exception
    {
        IData param = new DataMap();
        param.put("LIMIT_TAG", limitTag); // 限制类型 4-主产品可选附加产品
        param.put("PRODUCT_ID_A", productA); // 主产品ID
        param.put("PRODUCT_ID_B", productB); // 附加产品ID

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_PRODUCTB_BY_PRODUCTA", param);
    }

    public static IDataset queryProductIdByRelationTypeCode(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT PRODUCT_ID ");
        parser.addSQL("FROM TD_B_PRODUCT_RELATION ");
        parser.addSQL("WHERE RELATION_TYPE_CODE=:RELATION_TYPE_CODE");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryProductInfo(String product_mode, String brand_code) throws Exception
    {
        IData param = new DataMap();
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_MODE", product_mode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select t.product_id,t.product_name from td_b_product t ");
        parser.addSQL(" where t.product_mode=:PRODUCT_MODE ");
        parser.addSQL(" and  t.BRAND_CODE=:BRAND_CODE ");
        parser.addSQL(" and  t.end_date>sysdate ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    // 根据产品ID和产品模式查询产品介绍信息
    public static IDataset queryProductIntro(String userId, String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("USER_ID", userId);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_PRODUCT_INTRO", data);
    }

    /**
     * 作用：通过产品品牌、产品模式获取产品列表
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset queryProductListByBrandMode(IData inData) throws Exception
    {
        IDataset resultList = Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRAND_MODE", inData, Route.CONN_CRM_CEN);
        return resultList;
    }

    public static IDataset queryProductModelByPidSid(String productId, String serviceId) throws Exception
    {
        IData data = new DataMap();

        data.put("PRODUCT_ID", productId);
        data.put("SERVICE_ID", serviceId);

        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PRODUCT_BY_SID", data, Route.CONN_CRM_CEN);
    }
    public static IData queryProductTrans(String productId, String productIdNew) throws Exception
    {

        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PRODUCT_ID_NEW", productIdNew);

        IDataset result = Dao.qryByCode("TD_S_PRODUCT_TRANS", "PRODUCT_TRANS_SEL", param, Route.CONN_CRM_CEN);

        return result.isEmpty() ? new DataMap() : result.getData(0);
    }

    public static IDataset queryProductTransInfo(IData params) throws Exception
    {
        return Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_ALL_BY_PK", params);
    }

    public static boolean queryProductType(String productId) throws Exception
    {
        IData data = new DataMap();
       // data.put("BRAND_CODE", productId);//原逻辑没有BRAND_CODE,不知道以前是怎么查的   修改 duhj 2017/5/15
        data.put("PRODUCT_ID", productId);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select count(1) RS from td_b_unify_product_discnt ");
        parser.addSQL(" where product_id=:PRODUCT_ID ");
        parser.addSQL(" and end_date>sysdate");

        IDataset ret = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(ret))
        {
            int rs = ret.getData(0).getInt("RS", 0);
            return rs > 0 ? true : false;
        }
        return false;
    }

    /**
     * 查询营销活动的产品
     * 
     * @author luojh
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset querySaleActiveProduct(String eparchyCode, String rsrv_str2) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("RSRV_STR2", rsrv_str2);

        return Dao.qryByCodeParser("TD_B_PRODUCT", "SEL_BY_RSRVSTR2", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySaleActiveProductByLabel(String labelId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("LABEL_ID", labelId);
        cond.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_LABEL_ID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset querySaleActiveProductByType(String productType, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RSRV_STR2", productType);
        cond.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_RSRVSTR2", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryShortCutSaleActive(String eparchyCode) throws Exception
    {
        IDataset ids = new DatasetList();
        
        IDataset results = UpcCall.qryCatalogsByCatalogIdCatalogLevel(null, "L000", "4");
//        if(IDataUtil.isNotEmpty(results))
//        {
//            for(int i=0;i<results.size();i++)
//            {
//                IData result = results.getData(i);
//                String elementId = result.getString("CATALOG_ID");
//                
//                IData param = new DataMap();
//                param.put("ELEMENT_ID", elementId);
//                
//                IDataset infos = Dao.qryByCode("TD_B_SHORTCUT_SALEACTIVE", "SEL_ALL_BY_PRODUCTID", param, Route.CONN_CRM_CEN);
//                if(IDataUtil.isNotEmpty(infos))
//                {
//                    for(int j=0;j<infos.size();j++)
//                    {
//                        IData info = infos.getData(j);
//                        info.put("LABEL_ID", result.getString("UP_CATALOG_ID"));
//                        ids.add(info);
//                    }
//                    
//                }
//            }
//        }
        
        return ids;
        
//        return Dao.qryByCode("TD_B_SHORTCUT_SALEACTIVE", "SEL_ALL", data, Route.CONN_CRM_CEN);
    }

    /**
     * @Function: querySTBProducts()
     * @Description: 获取机顶盒绑定的产品
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-1 下午5:17:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
     */
    public static IDataset querySTBProducts(String resCode, String kiSupplyId) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "3800");
        param.put("PARAM_CODE", resCode);
        param.put("PARA_CODE1", kiSupplyId);// 厂商编码
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        
        IDataset commParaInfos = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARA_CODE1_1", param, Route.CONN_CRM_CEN);
        
        if (IDataUtil.isNotEmpty(commParaInfos))
        {
            IData commParaInfo = null;
            
            for (int i = 0; i < commParaInfos.size(); i++ )
            {
                commParaInfo = commParaInfos.getData(i);
                
                if (StringUtils.isNotBlank(commParaInfo.getString("PARA_CODE2")))
                {
                    commParaInfo.put("PRODUCT_ID", commParaInfo.getString("PARA_CODE2"));
                    commParaInfo.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(commParaInfo.getString("PARA_CODE2")));
                }
            }
        }
        
        return commParaInfos;
    }

    public static IDataset seFilterEnableElement(IDataset datas) throws Exception
    {

        IData tempData = null;
        tempData.put("ELEMENTS", datas);
        // SaleActiveMgr.seFilteerEnableElement(tempData);
        return tempData.getDataset("RESULT");
    }

    /**
     * 查询主产品默认的附加产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDefaultPlusProductByProdId(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        return Dao.qryByCode("TD_S_PRODUCTLIMIT", "SEL_BY_DEFAULTPRODID_FOR_TREE", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据package_id查询包中资费相关信息 不判断资费元素权限
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getDiscntElementByPackageNoPriv(String packageId, String eparchyCode, String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_DISCNT_BY_PACKID_NO_PRIV", data);
    }

    /**
     * 根据ELEMENT_ID_A查询元素依赖互斥表中的数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getElementLimitByEleId(String element_id_a, String element_type_code_a, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_ID_A", element_id_a);
        data.put("ELEMENT_TYPE_CODE_A", element_type_code_a);
        data.put("EPARCHY_CODE", eparchy_code);

        IDataset dataset = Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELE_ID_A", data, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /*
     * 根据serv_code 查询服务参数
     */
    public IDataset getElementParamAndOptions(String id_type, String element_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("ID_TYPE", id_type);
        data.put("ELEMENT_ID", element_id);
        data.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_ATTR_ITEMA", "SEL_BY_IDTYPE_ELEID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据集团产品product_id查询成员可订购的产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getMemProductsByProdId(String product_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_USERID_PID", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据product_id查询产品下的包
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getPackageByProduct(String product_id, String user_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE", data);
    }

    /**
     * 查询TF_F_USER_GRP_PACKAGE中定义的用户可订购的产品包
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getPackageByProductFromGrpPck(String product_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_FROM_GRPPCK", data, Route.CONN_CRM_CG);
    }

    /**
     * 查询TF_F_USER_GRP_PACKAGE中定义的用户可订购的产品包
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getPackageByProductFromGrpPckWithParentGroup(String product_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_FROM_GRPPCK_WITH_PARNET_GROUP", data, Route.CONN_CRM_CG);
    }

    /**
     * 根据product_id查询产品下的包
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getPackageByProductNoPriv(String product_id, String user_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        data.put("TRADE_EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_B_PACKAGE", "SEL_BY_PID_FOR_TREE_NO_PRIV", data);
    }

    /**
     * 查询用户选择了一个产品中的那些包
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getPackagesByUserProd(String product_id, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_BY_PK_USERID", data);
    }

    /**
     * 根据package_id查询包中平台服务相关信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getPlatSvcElementByPackage(String package_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);

        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_PLATSVC_BY_PACKID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据PRODUCT_ID PACKAGE_ID EPARCHY_CODE查询产品类型信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getProductPackageRel(String package_id, String product_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("PRODUCT_ID", product_id);
        data.put("EPARCHY_CODE", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_PACKAGE", data, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /**
     * 根据PRODUCT_ID PACKAGE_ID EPARCHY_CODE查询产品类型信息 不判断权限
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getProductPackageRelNoPriv(String package_id, String product_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", package_id);
        data.put("PRODUCT_ID", product_id);
        data.put("EPARCHY_CODE", eparchy_code);
        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_BY_PRODUCT_PACKAGE_NO_PRIV", data, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    public IDataset getProductsByType(String product_type_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_SPEC", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 关联td_s_product_trans 产品变更用 网上售卡
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getProductsByTypeWithNetSaleCardTrans(String product_id, String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE ", eparchy_code);
        data.put("PRODUCT_ID", product_id);
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("EPARCHY_CODE ", eparchy_code);
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_NETSALECARD_WITH_TRANS", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据产品类型获取产品 关联td_s_product_trans 产品变更用 网上售卡
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getProductsByTypeWithNetSaleCardTransNoPriv(String product_id, String product_type_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE ", eparchy_code);
        data.put("PRODUCT_ID", product_id);
        data.put("PRODUCT_TYPE_CODE", product_type_code);
        data.put("EPARCHY_CODE ", eparchy_code);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_TYPE_NETSALECARD_WITH_TRANS_NO_PRIV", data, Route.CONN_CRM_CEN);
    }

    /**
     * 根据package_id查询包中服务相关信息 不判断元素权限
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getServElementByPackageNoPriv(String packageId, String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", packageId);
        data.put("USER_ID", user_id);
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_SERV_BY_PACKID_NO_PRIV", data);
    }

    /**
     * 根据user_Id查询用户已订购的附加产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getUserPlusProductByUserId(String user_id) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_PLUS_BY_USERID", data);
    }

    /**
     * 根据user_Id查询用户已订购的主产品和附加产品 不包括营销活动产生的附加产品
     * 
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getUserProductByUserId(String user_id, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", user_id);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID", data, null, eparchyCode);
    }

    /**
     * 根据PARENT_PTYPE_CODE获取产品类型
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getWidenetProductsType(String parent_ptype_code, Pagination pg) throws Exception
    {

        IData data = new DataMap();
        data.put("PARENT_PTYPE_CODE", parent_ptype_code);
        return Dao.qryByCode("TD_S_PRODUCT_SPEC", "SEL_BY_PARENT", data, pg, Route.CONN_CRM_CEN);
    }

    /**
     * 根据PRODUCT_TYPE_CODE查询宽带产品类型信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getWidenetProductTypeByCode(String parent_ptype_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PARENT_PTYPE_CODE", parent_ptype_code);

        IDataset dataset = Dao.qryByCode("TD_S_PRODUCT_SPEC", "SEL_BY_PROD_SPEC_TYPE", data);

        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    public IData qryProductHintInfo(String product_id, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", product_id);
        data.put("EPARCHY_CODE ", eparchy_code);

        IDataset tradeInfos = Dao.qryByCode("TD_B_PRODUCT_HINTINFO", "SEL_BY_PRODUCTID", data);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            return tradeInfos.getData(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * 查询有限制关系的元素集
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryElementLimitByID(String element_type_code_a, String element_id_a, String element_type_code_b, String element_id_b, String limit_tag, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_TYPE_CODE_A", element_type_code_a);
        data.put("ELEMENT_ID_A", element_id_a);
        data.put("ELEMENT_TYPE_CODE_B", element_type_code_b);
        data.put("ELEMENT_ID_B", element_id_b);
        data.put("LIMIT_TAG", limit_tag);
        data.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_ELEMENT_LIMIT", "SEL_BY_ELEMENTAB", data, Route.CONN_CRM_CEN);
    }

    public IDataset queryMainProductByBrandByStaff(String brand_code, String eparchy_code) throws Exception
    {
        IData data = new DataMap();
        data.put("BRAND_CODE", brand_code);
        data.put("EPARCHY_CODE ", eparchy_code);
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT p.*");
        parser.addSQL(" FROM td_b_product p");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND p.BRAND_CODE = :BRAND_CODE");
        parser.addSQL(" AND p.product_mode = '00'");
        parser.addSQL(" AND p.RELEASE_TAG = '1'");
        parser.addSQL(" AND sysdate between p.start_date and p.end_date");
        parser.addSQL(" and exists (SELECT 1 FROM td_b_product_release " + " WHERE (release_eparchy_code = :EPARCHY_CODE OR release_eparchy_code = 'ZZZZ')" + " AND SYSDATE BETWEEN start_date AND end_date" + " AND p.product_id = product_id)");
        parser.addSQL(" and (:TRADE_STAFF_ID='SUPERUSR' or exists" + " (select 1 from (select b.data_code" + " from tf_m_staffdataright a, tf_m_roledataright b" + " where" + " a.data_type = 'P'" + " and a.right_attr = 1" + " and a.right_tag = 1"
                + " and a.data_code = b.role_code" + " and a.staff_id = :TRADE_STAFF_ID" + " union" + " select a.data_code" + " from tf_m_staffdataright a" + " where" + " a.data_type = 'P'" + " and a.right_attr = 0" + " and a.right_tag = 1"
                + " and a.staff_id =:TRADE_STAFF_ID) tmp" + " where tmp.data_code = to_char(p.product_id))) ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /** 根据品牌查相关的产品信息 */
    public IDataset queryProducts(String brand_code) throws Exception
    {
        IData data = new DataMap();
        data.put("BRAND_CODE", brand_code);

        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_BRANDCODE", data);
    }
    public static IData queryAllProductInfo(String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        IDataset resultSet = Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PK_ALL", data);
        return resultSet.getData(0);
    }
    
    /**
	 * 存储过程校验营销产品
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static void checkSaleActiveProdByProced(IData paramValue) throws Exception {		
		String[] paramName = {
				"V_EVENT_TYPE", "V_EPARCHY_CODE", "V_CITY_CODE", 
				"V_DEPART_ID", "V_STAFF_ID", "V_USER_ID", 
				"V_DEPOSIT_GIFT_ID", "V_PURCHASE_MODE", "V_PURCHASE_ATTR", "V_TRADE_ID",
				"V_CHECKINFO", "V_RESULTCODE", "V_RESULTINFO", 
				"V_SALE_TYPE", "V_VIP_TYPE_ID", "V_VIP_CLASS_ID"};	
		Dao.callProc("p_csm_CheckForSaleActive", paramName, paramValue, Route.CONN_CRM_CEN);
	}	
	
	public static boolean checkSaleActiveLimitProd(String productId) throws Exception {		
		boolean isNeedCheck = false;
		IDataset limitProducts = StaticUtil.getStaticList("PROCHECK_PRODUCT");

		IData packageinfo = null;
		for(int i=0,s=limitProducts.size(); i<s; i++){
			packageinfo = limitProducts.getData(i);
			String configProduct =  packageinfo.getString("DATA_ID");
			if(StringUtils.isNotBlank(configProduct) && productId.equals(configProduct)){
				isNeedCheck = true;
				break;
			}
		}
		
		return isNeedCheck;
	}
    
    
    public static IDataset queryProductAllInfoById(String productId)throws Exception{
    	IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        IDataset resultSet = Dao.qryByCode("TD_B_PRODUCT", "QRY_PRODUCT_ALLINFO_BY_ID", data);
        return resultSet;
    }
    
    
    /**
     * 查询魔百和产品
     * @param paramAttr
     * @param paramCode
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset queryTopSetBoxProducts(String paramAttr, String paramCode) throws Exception
    {
        IDataset topSetBoxProducts =  new DatasetList();
        
        IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaAllCol("CSM", paramAttr, paramCode, CSBizBean.getTradeEparchyCode());
        
        if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos))
        {
            IData topSetBoxProduct = null;
            String offerName = "";
            
            for (int i = 0; i < topSetBoxCommparaInfos.size(); i++)
            {
                topSetBoxProduct = new DataMap();
                
                offerName = UProductInfoQry.getProductNameByProductId(topSetBoxCommparaInfos.getData(i).getString("PARA_CODE1"));
                
                topSetBoxProduct.put("PRODUCT_ID", topSetBoxCommparaInfos.getData(i).getString("PARA_CODE1"));
                topSetBoxProduct.put("PRODUCT_NAME", offerName);
                
                topSetBoxProducts.add(topSetBoxProduct);
            }
        }
        
        //return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_COMMPARA2", param, Route.CONN_CRM_CEN);
        return topSetBoxProducts;
    }
    
    /**
     * REQ201604290016 关于新增集团购机判断条件的需求
     * chenxy3 2016-06-07
     * */
    public static IDataset queryProdInGroupMem(String serialNum,String productId)throws Exception{
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNum);
        data.put("PRODUCT_ID", productId);
        IDataset resultSet = Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_USER_IN_MEMB_PROD", data);
        return resultSet;
    }
    
    public static IDataset getMemberPackageElementsNew(String grpUserId, String productId, String packageId, boolean privForPack) throws Exception
    {
        IDataset resultset = new DatasetList();

        String useTag = "0";
        if (StringUtils.isNotBlank(productId))
        {
            String userTagTemp = ProductCompInfoQry.getUseTagByProductId(productId);
            if (StringUtils.isNotEmpty(userTagTemp))
                useTag = userTagTemp;
        }

        IData inparams = new DataMap();
        inparams.put("PACKAGE_ID", packageId);
        inparams.put("USER_ID", grpUserId);

        // 定制
        if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag)&&!"0000000000000000".equals(grpUserId))
        {
            resultset = Dao.qryByCode("TF_F_USER_GRP_PACKAGE", "SEL_BY_PACKAGE_ELEMENT_NO_PRIV", inparams, Route.CONN_CRM_CG);
        }
        else
        {
            resultset = Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKAGE_ELEMENT_NO_PRIV", inparams, Route.CONN_CRM_CG);
        }

        if (privForPack)
            ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), resultset);

        return resultset;
    }
    
    /**
     * 关于《2016校园终端营销活动》的开发需求
     * chenxy3 2016-08-16
     * */
    public static IDataset queryCampnByProdId(String EPARCHY_CODE,String productId)throws Exception{
    	IData data = new DataMap();
    	data.put("EPARCHY_CODE", EPARCHY_CODE);
        data.put("PRODUCT_ID", productId);
        IDataset resultSet = Dao.qryByCode("TD_B_ELEMENT_LABEL", "SEL_CAMPN_BY_PROD_ID", data, Route.CONN_CRM_CEN);
        return resultSet;
    }
    
    public static IDataset getSaleActivePackageByProduct(String productId) throws Exception
    {
        return UProductInfoQry.getSaleActivePackageByProduct(productId);
    }
     
    public static IDataset qryResTypeBySvcId(String servceId) throws Exception
    {
    	IDataset ds = UpcCall.qryServiceRes(servceId, BofConst.ELEMENT_TYPE_CODE_SVC);
    	String resTypeCode = "W";     //需要调产商品服务获取
    	if(IDataUtil.isNotEmpty(ds)){
    		IData data = ds.getData(0);
    		resTypeCode = data.getString("RES_TYPE_CODE");
    	}
    	
        IData param = new DataMap(); 
        param.put("RES_TYPE_CODE", resTypeCode);
        SQLParser parser = new SQLParser(param);  
        parser.addSQL(" SELECT C.RES_TYPE_CODE, C.RES_TYPE_NAME RES_TYPE ");
        parser.addSQL(" FROM  RES_TYPE C ");
        parser.addSQL("  WHERE 1=1  ");
        parser.addSQL(" AND C.RES_TYPE_ID = :RES_TYPE_CODE ");  

        return Dao.qryByParse(parser, Route.CONN_RES);
    }

	/**
	 * @Description：
	 * @param:@param string
	 * @param:@param string2
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-8-9下午03:38:37
	 */
	public static IDataset qryProductByUserIdAndProId(String userId, String productId) throws Exception {
		IData data = new DataMap();
    	data.put("USER_ID", userId);
        data.put("PRODUCT_ID", productId);
        IDataset resultSet = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_PROID", data);
        return resultSet;
		
	}
	
	
	 public static IDataset getMemberProductPackagesFowWlwSpc(String grpUserId, String productId, boolean privForPack, String categoryId, String eparchyCode) throws Exception
    {
        IDataset results = new DatasetList();
        IDataset groups = new DatasetList();

        IData comp = ProductCompInfoQry.getProductFromComp(productId);
        if (IDataUtil.isEmpty(comp))
        {
            IData resultData = new DataMap();
            resultData.put("GROUPS", null);
            results.add(resultData);
            return results; // 考虑下是否报错
        }

        String useTag = comp.getString("USE_TAG");

        String mebProductId = UProductMebInfoQry.getMemberMainProductByProductId(productId);
        
        //获取主产品下的组
        IDataset offerGroups = UpcCall.queryOfferGroupRelOfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, mebProductId);
        if (IDataUtil.isNotEmpty(offerGroups))
        {
            // 定制
            if (GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag))
            {
                
                IDataset userGrpPkgList = UserGrpPkgInfoQry.getUserGrpPackageIdForGrp(grpUserId);
                int resultsiz = offerGroups.size();
                for (int k = resultsiz - 1; k >= 0; k--)
                {
                    IData result = offerGroups.getData(k);
                    String resultPackageId = result.getString("PACKAGE_ID", "");
                    String resultProductId = result.getString("PRODUCT_ID", "");
                    
                    boolean existUserPackage = false;
                    for (int i = 0; i < userGrpPkgList.size(); i++)
                    {
                        IData userGrpPackage = userGrpPkgList.getData(i);
                        String userPackageId = userGrpPackage.getString("PACKAGE_ID", "");
                        String userProductId = userGrpPackage.getString("PRODUCT_ID", "");
                        if (userProductId.equals(resultProductId) && userPackageId.equals(resultPackageId))
                        {
                            existUserPackage = true;
                            break;
                        }
                    }
                    
                    if (!existUserPackage)
                        offerGroups.remove(k);
                }
            }
        }
      
        
        if(IDataUtil.isNotEmpty(offerGroups))
        {
            groups.addAll(offerGroups);
        }
        //构造用于获取打散商品的组
        if(StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL"))){
            IData group = new DataMap();
            group.put("GROUP_NAME", "其它");
            group.put("GROUP_ID", "-1");
            groups.add(group);
        }
        
        if("20171214".equals(productId))
        {
        	if(IDataUtil.isNotEmpty(groups))
        	{
        		int size = groups.size();
        		for (int i = size -1 ; i >=0 ; i--)
        		{
        			IData pckData = groups.getData(i);
        			String groupId = pckData.getString("GROUP_ID","");
        			if(StringUtils.isNotBlank(groupId) && ("41003608".equals(groupId) || "70000028".equals(groupId)))
        			{
        				groups.remove(i);
        			}
        		}
        	}
        }
        
        IData resultData = new DataMap();
        resultData.put("GROUPS", groups);
        resultData.put("MEB_PRODUCT_ID", mebProductId);
        results.add(resultData);

        return results;
    }
	 
}
