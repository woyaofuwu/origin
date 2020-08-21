
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RubbishDoubtQry
{
    public static IDataset queryAllByOrderId(String orderId) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        IDataset set = Dao.qryByCodeParser("TF_F_RUBBISH_DOUBT", "SEL_ALL_BYORDERID", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryAllByPK(String SEND_SERIAL_NUMBER_S, String SEND_SERIAL_NUMBER_E, String REC_SERIAL_NUMBER_S, String REC_SERIAL_NUMBER_E, String DEAL_TIME_S, String DEAL_TIME_E, String VIO_REASON, String SEND_FREQUENCY,
            String SMS_TYPE, String SYNC_STATE, String AUDIT_RESULT, String PROVINCE_CODE, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SEND_SERIAL_NUMBER_S", SEND_SERIAL_NUMBER_S);
        param.put("SEND_SERIAL_NUMBER_E", SEND_SERIAL_NUMBER_E);
        param.put("REC_SERIAL_NUMBER_S", REC_SERIAL_NUMBER_S);
        param.put("REC_SERIAL_NUMBER_E", REC_SERIAL_NUMBER_E);
        param.put("DEAL_TIME_S", DEAL_TIME_S);
        param.put("DEAL_TIME_E", DEAL_TIME_E);
        param.put("VIO_REASON", VIO_REASON);
        param.put("SEND_FREQUENCY", SEND_FREQUENCY);
        param.put("SMS_TYPE", SMS_TYPE);
        param.put("SYNC_STATE", SYNC_STATE);
        param.put("AUDIT_RESULT", AUDIT_RESULT);
        param.put("PROVINCE_CODE", PROVINCE_CODE);
        IDataset set = Dao.qryByCodeParser("TF_F_RUBBISH_DOUBT", "SEL_ALL_BYPK", param, pagination, Route.CONN_CRM_CEN);
        return set;
    }

}
