package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.catalog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupCatalogsQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset loadGroupProductsCatalogForAll(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        String upCatalogId = input.getString("UP_CATALOG_ID");
        String productTypeCode = input.getString("PRODUCT_TYPE_CODE");
        
        return GroupCatalogsQry.loadGroupProductsCatalogForAll(upCatalogId, productTypeCode, custId);
    }
    
    public IDataset loadGrpCatalogForOrderedOneTime(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        String upCatalogId = input.getString("UP_CATALOG_ID");
        
        return GroupCatalogsQry.loadGroupProductsCatalogForOrderedOneTime(upCatalogId, custId);
    }
    
    public IDataset loadOfferCatalogForOrdered(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        String upCatalogId = input.getString("UP_CATALOG_ID");
        
        return GroupCatalogsQry.loadOfferCatalogForOrdered(upCatalogId, custId);
    }
    
    public IDataset getGrpProductByGrpCustIdProID(IData input) throws Exception
    {
    	String custId = input.getString("CUST_ID","");
    	String productId = input.getString("PRODUCT_ID","");
    	
    	return GroupCatalogsQry.getGrpProductByGrpCustIdProID(custId, productId);
    }
    
    public IDataset getProductTypesByProductId(IData params) throws Exception
    {
    	String productId = params.getString("PRODUCT_ID");
    	IDataset productTypeDataset = 	GroupCatalogsQry.getProductTypeByProductId(productId);
    	return productTypeDataset;
    }
    
    /**
     * 查询成员商品组信息
     * @param offerId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryMebGroupByOfferId(IData params) throws Exception
    {
    	String offerId = params.getString("OFFER_ID");
    	String eparchyCode = params.getString("EPARCHY_CODE");
    	
    	return GroupCatalogsQry.queryMebGroupByOfferId(offerId, eparchyCode);
    	
    }
    
    /**
     * EOP开通申请过滤商品目录
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset filterOfferCatalogForEopApply(IData input) throws Exception
    {
        return GroupCatalogsQry.filterOfferCatalogForEopApply(input);
    }
    
    /**
     * EOP开通申请过滤商品列表
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset filterOfferForEopApply(IData input) throws Exception
    {
        return GroupCatalogsQry.filterOfferForEopApply(input);
    }
    
    public IDataset qrySelAllBySn(IData input) throws Exception
    {
        return GroupCatalogsQry.qrySelAllBySn(input);
    }
}
