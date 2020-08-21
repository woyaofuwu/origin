
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductTypeInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getProductIdsByProductType(IData input) throws Exception
    {
        String productTypeCode = input.getString("PRODUCT_TYPE_CODE");
        IDataset dataset = ProductTypeInfoQry.getProductIdsByProductType(productTypeCode);
        return dataset;
    }

    public IDataset getProductsType(IData input) throws Exception
    {
        String parent_ptype_code = input.getString("PARENT_PTYPE_CODE");
        IDataset data = ProductTypeInfoQry.getProductsType(parent_ptype_code, null);
        return data;
    }

    public IDataset getProductTypeByProductID(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = ProductTypeInfoQry.getProductTypeByProductID(productId, eparchyCode);
        return data;
    }

    public IDataset getProductTypeByProductIDAndParentPtype(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String parentCode = input.getString("PARENT_PTYPE_CODE");
        IDataset data = ProductTypeInfoQry.getProductTypeByProductIDAndParentPType(productId, parentCode);
        return data;
    }
    
    public IDataset getProductsTypeByParentTypeCode(IData input) throws Exception
    {
        String parentCode = input.getString("PARENT_PTYPE_CODE");
        IDataset data = ProductTypeInfoQry.getProductsTypeByParentTypeCode(parentCode);
        return data;
    }

}
