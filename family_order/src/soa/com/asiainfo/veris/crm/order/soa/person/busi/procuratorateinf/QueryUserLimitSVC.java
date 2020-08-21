
package com.asiainfo.veris.crm.order.soa.person.busi.procuratorateinf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.procuratorateinf.QueryUserLimitQry;

public class QueryUserLimitSVC extends CSBizService
{

    public IDataset getUserLimitByUserId(IData data) throws Exception
    {
        IDataset dataset = QueryUserLimitQry.getUserLimitByUserId(data);
        return dataset;
    }

}
