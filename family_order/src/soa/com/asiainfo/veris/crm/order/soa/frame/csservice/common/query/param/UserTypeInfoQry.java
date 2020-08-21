
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserTypeInfoQry
{

    public static IDataset queryUser139(String seq_id, String serial_number, String user_type, String target_code) throws Exception
    {
        IData data = new DataMap();
        data.put("SEQ_ID", seq_id);
        data.put("SERIAL_NUMBER", serial_number);
        data.put("USER_TYPE", user_type);
        data.put("TARGET_CODE", target_code);
        return Dao.qryByCodeParser("TF_F_USERTYPE", "SEL_BY_139", data, Route.CONN_CRM_CEN);

    }

    public static IDataset queryUserByNoTarget(String serialNumber, String userType) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("USER_TYPE", userType);
        return Dao.qryByCodeParser("TF_F_USERTYPE", "SEL_BY_NOTARGET", data, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUserByTarget(String serialNumber, String targetCode, String userType) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("USER_TYPE", userType);
        data.put("TARGET_CODE", targetCode);
        return Dao.qryByCodeParser("TF_F_USERTYPE", "SEL_BY_TARGET", data, Route.CONN_CRM_CEN);
    }

    public static IDataset queryUserTypes(String EPARTCHY_CODE) throws Exception
    {
        IData data = new DataMap();
        data.put("EPARTCHY_CODE", EPARTCHY_CODE);

        return Dao.qryByCode("TD_B_USERTYPE", "SEL_USER_TYPE", data, Route.CONN_CRM_CEN);

    }
}
