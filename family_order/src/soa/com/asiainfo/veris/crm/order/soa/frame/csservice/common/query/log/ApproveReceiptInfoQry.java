
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ApproveReceiptInfoQry
{

    /**
     * 根据申请单号查询申请单信息
     * 
     * @param taskId
     * @return
     * @throws Exception
     */
    public static IDataset qryReceiptByTaskId(String taskId) throws Exception
    {
        IData param = new DataMap();

        param.put("TASK_ID", taskId);

        return Dao.qryByCode("TF_LOG_APPROVE_RECEIPT", "SEL_BY_TASKID", param, Route.CONN_CRM_CG);
    }

    /**
     * 更新申请单状态信息
     * 
     * @param taskId
     *            申请单流水号
     * @param receiptState
     *            申请单状态
     * @return
     * @throws Exception
     */
    public static int updateApproveReceipt(String taskId, String receiptState) throws Exception
    {
        StringBuilder sql = new StringBuilder(100);

        IData param = new DataMap();

        param.put("RECEIPT_SATE", receiptState);
        param.put("TASK_ID", taskId);

        sql.append("UPDATE TF_LOG_APPROVE_RECEIPT");
        sql.append("   SET RECEIPT_SATE = :RECEIPT_SATE");
        sql.append(" WHERE TASK_ID = :TASK_ID");

        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CG);
    }
}
