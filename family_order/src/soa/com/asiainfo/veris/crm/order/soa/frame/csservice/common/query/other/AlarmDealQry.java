
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AlarmDealQry extends CSBizBean
{

    public static IDataset queryAlarmByCond(String handleState, String startTime, String endTime, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("HANDLE_STATE", handleState);
        params.put("START_TIME", startTime);
        params.put("END_TIME", endTime);
        return Dao.qryByCode("TF_B_TASKALARM", "SEL_BY_COND_NEW", params, pagination);
    }

    public static IDataset queryAlarmByMonth(String handleState, String startTime, String endTime, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("HANDLE_STATE", handleState);
        params.put("START_TIME", startTime);
        params.put("END_TIME", endTime);
        return Dao.qryByCode("TF_B_TASKALARM", "SEL_BY_COND_MONTH_NEW", params, pagination);
    }

    public static IDataset queryChart(String routeEparchyCode) throws Exception
    {
        IData params = new DataMap();
        return Dao.qryByCode("TF_B_TASKALARM", "SEL_BY_CHART", params, routeEparchyCode);
    }

    public static int updAlarmClose(String closedState, String alarmId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("CLOSED_STATE", closedState);
        StringBuilder strSql = new StringBuilder();
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TF_B_TASKALARM SET RSRV_STR2 = :CLOSED_STATE  WHERE ALARM_ID IN(");
        strSql.append(alarmId);
        strSql.append(") AND HANDLE_STATE IN ('1','2')");
        return Dao.executeUpdate(strSql, params);

    }

    public static int updAlarmState(String handleState, String resultInfo, String alarmId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("HANDLE_STATE", handleState);
        params.put("RESULT_INFO", resultInfo);
        params.put("ALARM_ID", alarmId);
        return Dao.executeUpdateByCodeCode("TF_B_TASKALARM", "UPD_HANDLE_STATE", params);
    }

    public static int updAlarmStateBatch(String handleState, String resultInfo, String alarmId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        StringBuilder strSql = new StringBuilder();
        params.put("HANDLE_STATE", handleState);
        params.put("RESULT_INFO", resultInfo);
        strSql.delete(0, strSql.length());
        strSql.append("UPDATE TF_B_TASKALARM SET HANDLE_STATE = :HANDLE_STATE, RESULT_INFO = :RESULT_INFO WHERE ALARM_ID IN(");
        strSql.append(alarmId);
        strSql.append(")");
        return Dao.executeUpdate(strSql, params);
    }
}
