
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.modifyuserdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class ModifyUserDataReqData extends GroupReqData
{
    // 产品元素块区
    private IData PRDUCT_INFOS;

    public IData getPRDUCT_INFOS()
    {
        return PRDUCT_INFOS;
    }

    public void setPRDUCT_INFOS(IData prduct_infos)
    {
        PRDUCT_INFOS = prduct_infos;
    }
}
