
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserNetnpLogQry
{

    public static IDataset queryUserNetNpLog(String sn, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        IDataset logSet = Dao.qryByCode("TF_B_USER_NETNP_LOG", "SEL_NETNP_LOG", param, page);
        return logSet;
    }
}
