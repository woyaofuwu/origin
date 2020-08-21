
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interfaceinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupCutInfoQrySVC extends CSBizService
{
    /**
     * 根据集团用户ID查询V网升级信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGroupCutInfoByUserId(IData input) throws Exception
    {
        IDataset data = GroupCutInfoQry.getGroupCutInfoByUserId(input);

        return data;
    }

}
