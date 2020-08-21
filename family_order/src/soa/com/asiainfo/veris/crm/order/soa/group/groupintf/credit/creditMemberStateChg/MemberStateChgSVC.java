
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditMemberStateChg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class MemberStateChgSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /**
     * 创建批量任务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtBat(IData inParam) throws Exception
    {
        CreateMemberStateChgBatBean bean = new CreateMemberStateChgBatBean();

        return bean.crtBat(inParam);
    }

    public IDataset crtTrade(IData inParam) throws Exception
    {
        MemberStateChgBean bean = new MemberStateChgBean();

        return bean.crtTrade(inParam);
    }
}
