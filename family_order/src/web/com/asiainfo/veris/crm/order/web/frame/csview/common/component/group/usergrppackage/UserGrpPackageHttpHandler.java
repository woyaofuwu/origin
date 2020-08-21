
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.usergrppackage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserGrpPackageHttpHandler extends CSBizHttpHandler
{

    public void renderUserGrpPackageList() throws Exception
    {
        IData inpara = getData();
        String productId = inpara.getString("PRODUCT_ID");
        String packageId = inpara.getString("PACKAGE_ID");
        String grpUserEparchyCode = inpara.getString("GRP_USER_EPARCHYCODE");
        IData inparam = new DataMap();
        inparam.put("PACKAGE_ID", packageId);
        inparam.put("PRODUCT_ID", productId);
        inparam.put("EPARCHY_CODE", grpUserEparchyCode);
        IDataset elementList = CSViewCall.call(this, "CS.PackageSVC.getPackageElements", inparam);
        this.setAjax(elementList);

    }

}
