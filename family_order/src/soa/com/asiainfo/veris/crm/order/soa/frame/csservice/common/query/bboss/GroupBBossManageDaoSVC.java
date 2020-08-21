
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupBBossManageDaoSVC extends CSBizService
{

    /**
     * chenyi 查询预受理trade表数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBbossTradeByEsop(IData input) throws Exception
    {

        return GroupBBossManageDao.queryBbossTradeByEsop(input.getString("PRODUCT_ID"), input.getString("GROUP_ID"), input.getString("UIDS"));
    }

}
