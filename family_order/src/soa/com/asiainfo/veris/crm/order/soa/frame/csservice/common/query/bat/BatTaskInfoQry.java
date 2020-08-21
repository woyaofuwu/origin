
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class BatTaskInfoQry
{
    /**
     * 根据条件查询批量任务信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryBatTaskByAll(IData inParam, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID"));
        param.put("BATCH_OPER_CODE", inParam.getString("BATCH_OPER_CODE"));
        param.put("BATCH_OPER_NAME", inParam.getString("BATCH_OPER_NAME"));
        param.put("CREATE_STAFF_ID", inParam.getString("CREATE_STAFF_ID"));
        param.put("CREATE_EPARCHY_CODE", inParam.getString("CREATE_EPARCHY_CODE"));
        param.put("CREATE_CITY_CODE", inParam.getString("CREATE_CITY_CODE"));
        param.put("START_DATE", inParam.getString("START_DATE"));
        param.put("END_DATE", inParam.getString("END_DATE"));

        return Dao.qryByCodeParser("TF_B_TRADE_BAT_TASK", "SEL_BY_ALL", param, pg, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据批量任务号查询批量任务信息
     * 
     * @param batchTaskId
     * @return
     * @throws Exception
     */
    public static IData qryBatTaskByBatchTaskId(String batchTaskId) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_TASK_ID", batchTaskId);

        IDataset batTaskInfo = Dao.qryByCode("TF_B_TRADE_BAT_TASK", "SEL_ALL_BY_PK", param, Route.getJourDb(Route.CONN_CRM_CG));

        if (batTaskInfo.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_31);
        }

        return batTaskInfo.getData(0);
    }
}
