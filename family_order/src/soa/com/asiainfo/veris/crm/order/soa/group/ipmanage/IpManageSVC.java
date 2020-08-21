
package com.asiainfo.veris.crm.order.soa.group.ipmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IpManageSVC extends CSBizService
{
    public IDataset crtTrade(IData inparam) throws Exception
    {
        IpManageBean bean = new IpManageBean();
        return bean.crtTrade(inparam);
    }
}
