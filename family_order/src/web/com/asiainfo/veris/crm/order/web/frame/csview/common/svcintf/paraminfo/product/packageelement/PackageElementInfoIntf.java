
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.packageelement;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class PackageElementInfoIntf
{

    /**
     * 通过包ID 和地州编码查询包下的元素信息
     * 
     * @param bc
     * @param packageId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryElementInfosByPackageIdAndEparchyCodeHasPriv(IBizCommon bc, String packageId, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PACKAGE_ID", packageId);
        inparam.put("EPARCHY_CODE", eparchyCode);
        IDataset elementList = CSViewCall.call(bc, "CS.PackageSVC.getPackageElements", inparam);
        return elementList;
    }
}
