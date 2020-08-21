
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class HintLogManageQry
{

    public static IDataset queryHintLog(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_B_TRADE_HINT_LOG", "SEL_LOGINFO", params, pagination, Route.CONN_CRM_CEN);
    }

}
