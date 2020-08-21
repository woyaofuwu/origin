
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productpackage;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productpackage.ProductPkgInfoIntf;

public class ProductPkgInfoIntfViewUtil
{
    /**
     * 查询某集团产品下的成员必选包
     * 
     * @param bc
     * @param grpProductId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryMebForcePackageByGrpProId(IBizCommon bc, String grpProductId, String eparchyCode) throws Exception
    {
        IDataset infosDataset = ProductPkgInfoIntf.qryMebForcePackageByGrpProId(bc, grpProductId, eparchyCode);

        return infosDataset;
    }

}
