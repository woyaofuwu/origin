
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class ChangeUserElementReqData extends GroupReqData
{
    private IData PRDUCT_INFOS;//

    private String netTypeCode;

    // private IDataset power100Infos; // 动力100

    // public IDataset getPower100Infos()
    // {
    // return power100Infos;
    // }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    // public void setPower100Infos(IDataset power100Infos)
    // {
    // this.power100Infos = power100Infos;
    // }

    public IData getPRDUCT_INFOS()
    {
        return PRDUCT_INFOS;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setPRDUCT_INFOS(IData prduct_infos)
    {
        PRDUCT_INFOS = prduct_infos;
    }

}
