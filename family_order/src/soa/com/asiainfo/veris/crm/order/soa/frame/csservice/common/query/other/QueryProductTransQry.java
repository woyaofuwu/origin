
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryProductTransQry extends CSBizBean
{

    public static IDataset queryProductTransInfo(String productIdA, String productIdB, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCT_ID_A", productIdA);
        params.put("PRODUCT_ID_B", productIdB);
        return Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_ALL_BY_PK", params, pagination, Route.CONN_CRM_CEN);

    }

}
