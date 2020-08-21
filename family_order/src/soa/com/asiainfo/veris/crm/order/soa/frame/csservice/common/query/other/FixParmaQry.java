
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FixParmaQry
{

    /**
     * 新业务短信配置查询
     * 
     * @param pd
     * @param cond
     * @return
     * @throws Exception
     */
    public static IDataset queryBusimanage(IData cond) throws Exception
    {

        return Dao.qryByCode("TF_SM_BI_BUSIMANAGE", "SEL_BY_MATCH_CODE", cond);

    }

}
