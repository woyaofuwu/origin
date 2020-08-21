
package com.asiainfo.veris.crm.order.soa.group.batVpnUpgradetoIMS;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class BatVpnMemUpgradeReqData extends ChangeMemElementReqData
{
    private IDataset MEM_DISCNT;

    public IDataset getMEM_DISCNT()
    {
        return MEM_DISCNT;
    }

    public void setMEM_DISCNT(IDataset mEM_DISCNT)
    {
        MEM_DISCNT = mEM_DISCNT;
    }

}
