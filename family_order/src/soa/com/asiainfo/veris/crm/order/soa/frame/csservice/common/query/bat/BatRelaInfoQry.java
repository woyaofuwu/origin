
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BatRelaInfoQry
{

    /**
     * 根据批次号查询批量关联关系
     * 
     * @param batchId
     * @return
     * @throws Exception
     */
    public static IDataset qryBatRealByBatchId(String batchId) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_ID", batchId);

        return Dao.qryByCode("TF_B_TRADE_BAT_RELATION", "SEL_BY_BATCHID", param, Route.CONN_CRM_CEN);
    }

}
