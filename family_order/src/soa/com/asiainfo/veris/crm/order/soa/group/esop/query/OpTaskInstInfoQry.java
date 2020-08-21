package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * order
 * 代办处理人信息表
 * @author ckh
 * @data 2018/1/15.
 */
public class OpTaskInstInfoQry
{
    public static void insertOpTaskInstInfo(IDataset datalist) throws Exception
    {
        Dao.executeBatchByCodeCode("TF_F_OP_TASK_INST", "INS_ALL_INFO", datalist, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset queryOpTaskInstInfo(String taskId) throws Exception
    {
        IData param = new DataMap();
        param.put("TASK_ID", taskId);
        return Dao.qryByCode("TF_F_OP_TASK_INST", "SEL_BY_TASK_ID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void deleteOpTaskInstInfoByTaskIdBatch(IDataset param) throws Exception
    {
        Dao.delete("TF_F_OP_TASK_INST", param, new String[]{"TASK_ID"}, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateOpTaskInstStatus(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder(200);
        sql.append("update TF_F_INFO_INSTANCE set INST_STATUS = :INST_STATUS where INST_ID = :INST_ID ");
        
        Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
    
    public static void updateOpTaskInstStaffId(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder(500);
        sql.append(" UPDATE TF_F_OP_TASK_INST T  ");
        sql.append(" SET T.STAFF_ID = :RECE_STAFF_ID ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND T.STAFF_ID = :SEND_STAFF_ID ");
        sql.append(" AND T.TASK_ID IN (SELECT TASK_ID FROM TF_F_OP_TASK I WHERE I.TASK_STATUS = '1' AND I.BUSI_TYPE_CODE='35')");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
