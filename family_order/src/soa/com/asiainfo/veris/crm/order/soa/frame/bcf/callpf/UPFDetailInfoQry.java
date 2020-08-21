
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UPFDetailInfoQry
{
    public static IDataset qryPfDetail() throws Exception
    {
        IData param = new DataMap();

        return Dao.qryByCode("TD_S_PF_DETAIL", "SEL_BY_ALL", param, Route.CONN_CRM_CEN);
    }
}
