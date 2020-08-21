
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.productpackage;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class ProductPkgInfoIntf
{

    /**
     * 查询某集团产品下的成员必选包
     * 
     * @param bc
     * @param grpProdId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryMebForcePackageByGrpProId(IBizCommon bc, String grpProdId, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", grpProdId);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.ProductPkgInfoQrySVC.getMebForcePackageByGrpProId", inparam);
    }

}
