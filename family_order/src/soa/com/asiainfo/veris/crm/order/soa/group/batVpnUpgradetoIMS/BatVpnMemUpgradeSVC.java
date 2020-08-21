
package com.asiainfo.veris.crm.order.soa.group.batVpnUpgradetoIMS;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class BatVpnMemUpgradeSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        BatVpnMemUpgrade bean = new BatVpnMemUpgrade();

        return bean.crtTrade(inParam);
    }
}
