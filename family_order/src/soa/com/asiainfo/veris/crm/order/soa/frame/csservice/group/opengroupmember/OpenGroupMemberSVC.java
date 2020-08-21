
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.opengroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class OpenGroupMemberSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /**
     * 调用登记流程
     * 
     * @author fengsl
     * @date 2013-04-11
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset crtTrade(IData idata) throws Exception
    {
        OpenGroupMemberBean bean = new OpenGroupMemberBean();
        return bean.crtTrade(idata);
    }

}
