
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ChlAcctQry
{

    public static IDataset queryByAgentSn(String agentSn, String chnlObjType, String state) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", agentSn);
        cond.put("CHNL_OBJ_TYPE", chnlObjType);
        cond.put("STATE", state);
        IDataset result = Dao.qryByCode("TF_CHL_ACCT", "SEL_BY_ALL", cond);

        return result;
    }
}
