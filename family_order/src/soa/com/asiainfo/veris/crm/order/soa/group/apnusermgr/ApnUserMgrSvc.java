
package com.asiainfo.veris.crm.order.soa.group.apnusermgr;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ApnUserMgrSvc extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * APN用户从临时表入到正式表
     * @param inparam
     * @return
     * @throws Exception
     */
    public boolean doThisApnUserMgrInfo(IData inparam) throws Exception
    {
        return ApnUserMgrSvcBean.doThisApnUserMgrInfo(inparam);
    }
}
