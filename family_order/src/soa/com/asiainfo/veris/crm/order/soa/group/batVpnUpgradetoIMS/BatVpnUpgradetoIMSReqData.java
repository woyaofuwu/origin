
package com.asiainfo.veris.crm.order.soa.group.batVpnUpgradetoIMS;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;

public class BatVpnUpgradetoIMSReqData extends ChangeUserElementReqData
{
    private IDataset grpPackageList;

    public IDataset getGrpPackageList()
    {
        return grpPackageList;
    }

    public void setGrpPackageList(IDataset grpPackageList)
    {
        this.grpPackageList = grpPackageList;
    }

}
