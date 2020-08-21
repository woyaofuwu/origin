
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPtypeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ProductTypeInfoQry extends CSBizBean
{
    /**
     * 验证产品ID是否为特定的产品类型
     * 
     * @param productId
     * @param productTypeCode
     * @return
     * @throws Exception
     */
    public static boolean checkExisProductIdAndProductTypeCode(String productId, String productTypeCode) throws Exception
    {
        return UPtypeProductInfoQry.checkExisProductIdAndProductTypeCode(productId, productTypeCode);
    }

    public static String getParentPTypeByProductTypeCode(String productTypeCode) throws Exception
    {
        return UPtypeProductInfoQry.getParentPTypeByProductTypeCode(productTypeCode);
    }

    /**
     * 根据产品类型查询产品的ID
     * 
     * @param productTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductIdByType(String productTypeCode, String eparchyCode) throws Exception
    {
        return UProductInfoQry.getProductsByType(productTypeCode);
    }

    /**
     * 根据PRODUCT_TYPE_CODE查询产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductIdsByProductType(String product_type_code) throws Exception
    {
        return UProductInfoQry.getProductsByType(product_type_code);
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
        IDataset catalogs = UpcCall.qryCatalogsByUpCatalogId(parent_ptype_code, pg);
        if (IDataUtil.isNotEmpty(catalogs))
        {
            for (int i = 0, size = catalogs.size(); i < size; i++)
            {
                IData catalog = catalogs.getData(i);
                catalog.put("PRODUCT_TYPE_CODE", catalog.getString("CATALOG_ID"));
                catalog.put("PRODUCT_TYPE_NAME", catalog.getString("CATALOG_NAME"));
            }
        }
        return catalogs;
    }

    /**
     * 根据PARENT_PTYPE_CODE获取产品类型
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getProductsTypeByParentTypeCode(String parent_ptype_code) throws Exception
    {
        return UPtypeProductInfoQry.getProductsTypeByParentTypeCode(parent_ptype_code);
    }

    /**
     * 根据BrandCode查询所有对应的产品类型
     * 
     * @author sunxin
     * @param BrandCode
     *            品牌
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getProductTypeByBrand(String strBrandCode) throws Exception
    {
        throw new Exception("TD_S_PRODUCT_TYPE表失效，请联系管理员");// 暂时报错失效 用到在改
        /*IData data = new DataMap();
        data.put("BRAND_CODE", strBrandCode);
        return Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_BRAND", data, Route.CONN_CRM_CEN);*/
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
        IDataset dataset = UProductInfoQry.getProductsByType(product_type_code);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /**
     * 根据DEFAULT_TAG标记查询产品类型信息
     * 
     * @author chenzm
     * @param strDefaultTag
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getProductTypeByDefaultTag(String strDefaultTag) throws Exception
    {
        throw new Exception("TD_S_PRODUCT_TYPE表失效，请联系管理员");// 暂时报错失效 用到在改
        /*IData data = new DataMap();
        data.put("DEFAULT_TAG", strDefaultTag);
        return Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_DEFAULT_TAG", data, Route.CONN_CRM_CEN);*/
    }

    /**
     * 根据DEFAULT_TAG标记查询产品类型信息
     * 
     * @author dengyong3
     * @param defaultTag
     *            默认产品类型
     * @param parentTypeCode
     *            父产品类型
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getProductTypeByDefaultTagParentTypeCode(String defaultTag, String parentTypeCode) throws Exception
    {
        throw new Exception("TD_S_PRODUCT_TYPE表失效，请联系管理员");// 暂时报错失效 用到在改
        /*IData data = new DataMap();
        data.put("DEFAULT_TAG", defaultTag);
        data.put("PARENT_PTYPE_CODE", parentTypeCode);
        return Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_DEFAULT_TAG_PARENT_TYPE_CODE", data, Route.CONN_CRM_CEN);*/
    }

    public static IDataset getProductTypeByProductId(String product_id) throws Exception
    {
        return UPtypeProductInfoQry.getProductTypeByProductId(product_id);
    }

    /**
     * 根据PRODUCT_ID,EPARCHY_CODE查询产品-产品类型信息
     * 
     * @author chenzm
     * @param productId
     * @param eparchyCode
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getProductTypeByProductID(String productId, String eparchyCode) throws Exception
    {
        /*IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_PARENT_AND_PRODUCT", data, Route.CONN_CRM_CEN);*/
    	IDataset dataset = UpcCall.qryCatalogByOfferId(productId, "P");
    	if(IDataUtil.isNotEmpty(dataset)){
    		IData data = dataset.getData(0);
    		if(IDataUtil.isNotEmpty(data)){
    			data.put("PRODUCT_TYPE_CODE", data.getString("CATALOG_ID"));
    			data.put("PRODUCT_TYPE_NAME", data.getString("CATALOG_NAME"));
    		}
    	}
    	return dataset;
    }

    /**
     * 通过product_id和父产品类型查询产品类型
     * 
     * @param productId
     * @param parentCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeByProductIDAndParentPType(String productId, String parentCode) throws Exception
    {
        return UPtypeProductInfoQry.getProductTypeByProductIDAndParentPType(productId, parentCode);
    }

    public static String getProductTypeNameByProductTypeCode(String productTypeCode) throws Exception
    {
        return UPtypeProductInfoQry.getProductTypeNameByProductTypeCode(productTypeCode);
    }

    /**
     * 配置中存在一个产品ID对应多个类型的情况
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypesByProductId(String productId) throws Exception
    {
        return UPtypeProductInfoQry.getProductTypeByProductId(productId);
    }

    /**
     * 根据产品ID获取产品类型和产品类型名称
     * 
     * @param productId
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryProductTypeCodeByProductId(String productId, Pagination pg) throws Exception
    {
        throw new Exception("TD_S_PRODUCT_TYPE表失效，请联系管理员");// 暂时报错失效 用到在改
        /*IData param = new DataMap();
        param.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TD_S_PRODUCT_TYPE", "SEL_BY_PARENT_AND_PRODUCT", param, Route.CONN_CRM_CEN);*/
    }

    /**
     * 根据商品类型 查询商品集
     * 
     * @author liuxx3
     * @date 2014-06-30
     */
    public static IDataset qryProInfoByProTypeCode(String productTypeCode) throws Exception
    {
    	return UProductInfoQry.getProductsByType(productTypeCode);
    }

}
