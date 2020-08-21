
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ResStateInfoQry
{

    /**
     * @param resTypeCode
     * @param resStateCode
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryResState(String resTypeCode, String resStateCode, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("RES_TYPE_CODE", resTypeCode);
        data.put("RES_STATE_CODE", resStateCode);

        return Dao.qryByCode("TD_S_RESSTATE", "SEL_STATE_INFO", data, Route.CONN_RES);
    }
}
