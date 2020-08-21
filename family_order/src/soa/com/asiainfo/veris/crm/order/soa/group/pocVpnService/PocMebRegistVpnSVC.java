
package com.asiainfo.veris.crm.order.soa.group.pocVpnService;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PocMebRegistVpnSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData param) throws Exception
    {
        PocMebRegistVpnBean bean = new PocMebRegistVpnBean();

        return bean.crtTrade(param);
    }
}
