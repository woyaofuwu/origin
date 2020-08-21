
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.procuratorateinf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUserLimitQry
{

    public static IDataset getUserLimitByUserId(IData data) throws Exception
    {
        IDataset dataset = new DatasetList();
        dataset = Dao.qryByCode("TF_F_USER_SCORE_LIMIT", "SEL_USERLIMIT_BY_USERID", data);
        return dataset;
    }
}
