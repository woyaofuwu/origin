
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserOtherservQry
{

    public static int deleteUserOtherservBySNandRSRVSTR1(String serialNumber, String rsrvStr1, String serviceMode) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("SERVICE_MODE", serviceMode);
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "DEL_BY_SN_RSRVSTR1", param);
    }

    public static int delUserOtherservByRsrvStr6(String userId, String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        return Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "DEL_BY_RSRVSTR6_USER_ID", param);
    }

    public static IDataset getUserOtherservByRSRVSTR1(String rsrvStr1, String serviceMode) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("SERVICE_MODE", serviceMode);
        IDataset result = Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_RSRV_STR1", param);
        return result;
    }

    public static IDataset getUserOtherservByRSRVSTR1(String rsrvStr1, String serviceMode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("SERVICE_MODE", serviceMode);
        return Dao.qryByCodeParser("TF_F_USER_OTHERSERV", "SEL_BY_RSRV_STR1", param, pagination);
    }

    public static IDataset getUserOtherservBySN(String serialNumber, String serviceMode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERVICE_MODE", serviceMode);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SERIAL_NUMBER", param);
    }

    public static IDataset getUserOtherservBySN(String serialNumber, String serviceMode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERVICE_MODE", serviceMode);
        return Dao.qryByCodeParser("TF_F_USER_OTHERSERV", "SEL_BY_SERIAL_NUMBER", param, pagination);
    }

    public static IDataset getUserOtherservBySNandRSRVSTR1(String serialNumber, String rsrvStr1, String serviceMode) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("SERVICE_MODE", serviceMode);
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SN_RSRVSTR1", param);
    }

    public static IDataset getUserOtherservBySNandRSRVSTR1(String serialNumber, String rsrvStr1, String serviceMode, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("RSRV_STR1", rsrvStr1);
        param.put("SERVICE_MODE", serviceMode);
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCodeParser("TF_F_USER_OTHERSERV", "SEL_BY_SN_RSRVSTR1", param, pagination);
    }

    public static IDataset qryServInfoByPk(String userId, String serviceMode, String processTag) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERVICE_MODE", serviceMode);
        param.put("PROCESS_TAG", processTag);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_PK", param);
    }

    public static IDataset qryServInfoSn(String serialNumber, String serviceMode) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SERVICE_MODE", serviceMode);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_INVOICE_NO", param);
    }
  //wangsc10-20180920
    public static IDataset qryServInfoByuserIdrsrvStr1(String userId, String rsrvStr1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_STR1", rsrvStr1);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_USERIDRSRVSTR1", param);
    }

}
