
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ScoreExchangeTypeInfoQry
{
    /**
     * 检查是否存在有效记录
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int checkExists(IData param) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TD_B_SCORE_EXCHANGE_TYPE", "SEL_EXCHANGE_TYPE_BY_CODE", param, Route.CONN_CRM_CEN);

        return dataset.size();

    }

    /**
     * 查询积分兑换规则类型
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryExchangeType(IData data, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TD_B_SCORE_EXCHANGE_TYPE", "SEL_EXCHANGE_BY_EPARCHY_CODE", data, pagination, Route.CONN_CRM_CEN);
    }
}
