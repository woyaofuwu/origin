
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import com.ailk.common.data.IDataset;

public class ChangePower100UserElementReqData extends ChangeUserElementReqData
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
