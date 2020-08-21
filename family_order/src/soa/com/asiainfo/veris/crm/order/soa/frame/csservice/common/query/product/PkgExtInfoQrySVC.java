
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PkgExtInfoQrySVC extends CSBizService
{
    /** 查询营销包的扩展属性 */
    public IDataset queryPackageExtInfo(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset result = PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);

        return result;
    }
}
