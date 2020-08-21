
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DetailItemInfoQry
{
    public static IDataset queryGroupUserPayRelation(String ITEM_ID, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("ITEM_ID", ITEM_ID);
        return Dao.qryByCode("TD_B_DETAILITEM", "SEL_DETAIL_BY_PAYITEMCODE", param, page, Route.CONN_CRM_CEN);
    }

}
