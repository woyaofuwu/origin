
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WastesmsOlcomworkQry
{

    /**
     * 相似度查询
     * 
     * @param param
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryAllByRsrvStr(String OLCOM_WORK_ID_DESTROY, String SERIAL_NUMBER, String USER_ID, String EPARCHY_CODE, String START_DATE, String END_DATE, String TRADE_STAFF_ID, String RESUME_STATE, String FLAG, Pagination page)
            throws Exception
    {
        IData params = new DataMap();
        params.put("OLCOM_WORK_ID_DESTROY", OLCOM_WORK_ID_DESTROY);
        params.put("SERIAL_NUMBER", SERIAL_NUMBER);
        params.put("USER_ID", USER_ID);
        params.put("EPARCHY_CODE", EPARCHY_CODE);
        params.put("START_DATE", START_DATE);
        params.put("END_DATE", END_DATE);
        params.put("TRADE_STAFF_ID", TRADE_STAFF_ID);
        params.put("RESUME_STATE", RESUME_STATE);
        params.put("FLAG", FLAG);
        IDataset ids = Dao.qryByCodeParser("TF_B_WASTESMS_OLCOMWORK", "SEL_WASTESMS_INFOS", params, page, Route.CONN_CRM_CEN);
        return ids;
    }

}
