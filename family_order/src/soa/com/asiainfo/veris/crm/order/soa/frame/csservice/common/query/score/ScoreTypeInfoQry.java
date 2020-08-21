
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ScoreTypeInfoQry
{

    /**
     * 查询积分类型
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryScoreTypeInfo() throws Exception
    {
        IData param = new DataMap();
        return Dao.qryByCode("TD_S_SCORETYPE", "SEL_SCORETYPE", param, Route.CONN_CRM_CEN);
    }
}
