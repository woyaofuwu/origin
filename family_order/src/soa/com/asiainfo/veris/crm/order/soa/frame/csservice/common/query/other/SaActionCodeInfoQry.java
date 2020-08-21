
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaActionCodeInfoQry
{
    /**
     * @param serialNumberA
     * @return
     * @throws Exception
     */
    public static IDataset querySaActionCodeBySn(String serialNumberA) throws Exception
    {
        IData param = new DataMap();

        param.put("SERIAL_NUMBER_A", serialNumberA);

        return Dao.qryByCode("TF_SA_ACTION_CODE", "SEL_BY_SERIAL_NUMBER_A", param);
    }
}
