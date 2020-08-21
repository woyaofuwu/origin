
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ScorePlanInfoQry
{

    /**
     * 查询有效的积分计划信息
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryScorePlanInfo(String status, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("STATUS", status);// 10A 正常；10E作废
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_INTEGRALPLAN", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryScorePlanInfoById(String planId) throws Exception
    {
        IData param = new DataMap();
        param.put("INTEGRAL_PLAN_ID", planId);
        return Dao.qryByCode("TF_F_INTEGRAL_PLAN", "SEL_BY_ID", param);
    }

    public static IDataset queryScorePlanInfoByUserId(String userId, String status) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("STATUS", status);
        return Dao.qryByCode("TF_F_INTEGRAL_PLAN", "SEL_BY_USERID", param);
    }
}
