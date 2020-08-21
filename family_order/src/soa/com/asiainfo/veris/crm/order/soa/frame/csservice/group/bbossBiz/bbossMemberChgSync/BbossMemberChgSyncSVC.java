
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossMemberChgSync;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class BbossMemberChgSyncSVC extends GroupOrderService
{

    private static final long serialVersionUID = 1L;

    /**
     * 一卡通成员变更、400白名单成员变更
     * 
     * @param inputData
     * @return
     * @throws Exception
     */
    public static IDataset bbossDataUdr(IData inputData) throws Exception
    {
        IDataset dataset = BbossMemberChgSync.bbossDataUdr(inputData);

        return dataset;
    }
}
