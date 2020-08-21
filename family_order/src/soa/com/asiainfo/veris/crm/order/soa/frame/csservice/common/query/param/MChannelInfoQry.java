
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MChannelInfoQry
{

    public static IDataset queryChannelInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TD_M_CHANNEL", "SEL_CHANNEL_BY_SN", param, Route.CONN_CRM_CEN);
    }
}
