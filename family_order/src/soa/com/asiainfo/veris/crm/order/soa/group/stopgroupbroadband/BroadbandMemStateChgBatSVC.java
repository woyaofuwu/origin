package com.asiainfo.veris.crm.order.soa.group.stopgroupbroadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class BroadbandMemStateChgBatSVC extends GroupOrderService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 提供给信控调用，批量创建集团商务宽带的成员暂停
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtStopBat(IData inParam) throws Exception
    {
        BroadbandMemStateChgBatBean bean = new BroadbandMemStateChgBatBean();

        return bean.crtStopBat(inParam);
    }
    
    /**
     * 提供给信控调用，批量创建集团商务宽带的成员恢复
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtOpenBat(IData inParam) throws Exception
    {
        BroadbandMemStateChgBatBean bean = new BroadbandMemStateChgBatBean();

        return bean.crtOpenBat(inParam);
    }

}
