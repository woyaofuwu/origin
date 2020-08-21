
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProductPkgInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public static IDataset qryActiveByPId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = ProductPkgInfoQry.qryActiveByPId(productId, eparchyCode);
        return data;
    }

    /**
     * 通过PRODUCT_ID PACKAGE_ID 来查限制订购次数 RSRV_TAG1
     * 
     * @param data
     * @return
     * @throws Exception
     * @author weixb3
     * @date 2013-3-19
     */
    public IDataset getLimitByProductPackage(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String productId = input.getString("PRODUCT_ID");
        IDataset data = ProductPkgInfoQry.getLimitByProductPackage(packageId, productId);
        return data;
    }

    /**
     * 查询某集团产品下的成员必选包
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getMebForcePackageByGrpProId(IData input) throws Exception
    {

        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = ProductPkgInfoQry.getMebForcePackageByGrpProId(productId, eparchyCode);
        return data;
    }

    public IDataset getPackageByProId(IData input) throws Exception
    {

        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = ProductPkgInfoQry.getPackageByProId(productId, eparchyCode);
        return data;
    }

    public IDataset getPackageByProductId(IData input) throws Exception
    {
        String product_id = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = ProductPkgInfoQry.getPackageByProductId(product_id, eparchyCode);
        return data;
    }
    
    public IData getProductPackageRelNoPriv(IData input) throws Exception
    {
        String product_id = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        String packageId = input.getString("PACKAGE_ID");

        IData data = ProductPkgInfoQry.getProductPackageRelNoPriv(product_id,packageId,eparchyCode);
        return data;
    }
}
