
package com.asiainfo.veris.crm.order.soa.group.batVpnUptoCountrywide;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class VpnMemUpToCountrywideSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inParam) throws Exception
    {
        VpnMemUpToCountrywide bean = new VpnMemUpToCountrywide();

        return bean.crtTrade(inParam);
    }
}
