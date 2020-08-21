
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import com.ailk.common.data.IDataset;

public class CreatePower100GroupUserReqData extends CreateGroupUserReqData
{
    private IDataset power100Infos; // 动力100

    public IDataset getPower100Infos()
    {
        return power100Infos;
    }

    public void setPower100Infos(IDataset power100Infos)
    {
        this.power100Infos = power100Infos;
    }
}
