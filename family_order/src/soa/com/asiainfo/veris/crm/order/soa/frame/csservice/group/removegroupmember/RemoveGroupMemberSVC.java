
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.removegroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class RemoveGroupMemberSVC extends GroupOrderService
{

    private static final long serialVersionUID = -5972927388074351525L;

    /**
     * 注销成员
     */
    public IDataset removeMember(IData param) throws Exception
    {
        RemoveGroupMemberBean bean = new RemoveGroupMemberBean();
        return bean.crtTrade(param);
    }
}
