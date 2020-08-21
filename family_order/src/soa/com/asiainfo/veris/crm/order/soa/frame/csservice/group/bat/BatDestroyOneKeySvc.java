
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BatDestroyOneKeySvc extends CSBizService
{
    private static final long serialVersionUID = 2891757179572906627L;

    /**
     * 一键注销成员
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset destroyMemberOneKeyByProc(IData data) throws Exception
    {
        IDataset dataset = BatDestroyOneKey.destroyMemberOneKeyByProc(data);

        return dataset;
    }

    /**
     * 一键注销成员和集团
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset destroyUserMemberOneKeyByProc(IData data) throws Exception
    {
        IDataset dataset = BatDestroyOneKey.destroyUserMemberOneKeyByProc(data);

        return dataset;
    }
}
