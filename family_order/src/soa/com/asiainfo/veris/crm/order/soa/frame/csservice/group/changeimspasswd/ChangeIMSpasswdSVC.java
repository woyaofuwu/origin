
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeimspasswd;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ChangeIMSpasswdSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset changeIMSpasswd(IData inparam) throws Exception
    {
        ChangeIMSpasswdBean bean = new ChangeIMSpasswdBean();
        return bean.crtTrade(inparam);
    }

}
