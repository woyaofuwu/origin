
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryIPExpressQry extends CSBizBean
{
    public static IDataset getMofficeBySN(String serialNumber) throws Exception
    {
        IData indata = new DataMap();
        indata.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_NUM", indata, Route.CONN_RES);
    }

    public static IDataset getUserinfo(String routeEparchyCode, String userIdB) throws Exception
    {
        IData indata = new DataMap();
        // 用户信息
        indata.put("USER_ID", userIdB);
        return Dao.qryByCode("TF_F_USER", "SEL_BY_PK_NEW1", indata, routeEparchyCode);
    }

    public static IDataset getUserInfoBySN(String serialNumber, String routeEparchyCode) throws Exception
    {

        // 根据号码查询用户信息
        IData indata = new DataMap();
        indata.put("SERIAL_NUMBER", serialNumber);
        indata.put("REMOVE_TAG", "0");
        indata.put("NET_TYPE_CODE", "00");
        return Dao.qryByCode("TF_F_USER", "SEL_BY_SN", indata, routeEparchyCode);
    }

    public static IDataset getUserMainSvc(String routeEparchyCode, String userId) throws Exception
    {
        IData indata = new DataMap();
        indata.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_MAINSVC", indata, routeEparchyCode);
    }

    public static IDataset getUserMainSvcLast(String routeEparchyCode, String userId) throws Exception
    {
        IData indata = new DataMap();
        indata.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_LAST_MAINSVC", indata, routeEparchyCode);
    }

    public static IDataset getUserMainSvcState(String routeEparchyCode, String serviceId) throws Exception
    {
        IData indata = new DataMap();
        indata.put("SERVICE_ID", serviceId);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_USER_SVC", indata, routeEparchyCode);
    }

    public static IDataset getUUByIDB(String routeEparchyCode, String userId, String relationTypeCode) throws Exception
    {
        IData indata = new DataMap();
        indata.put("USER_ID_B", userId);
        indata.put("RELATION_TYPE_CODE", relationTypeCode);
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", indata, routeEparchyCode);

    }

    public static IDataset getUUUserinfo(String routeEparchyCode, String userIdA, String relationTypeCode) throws Exception
    {
        IData indata = new DataMap();
        indata.put("RELATION_TYPE_CODE", relationTypeCode);
        indata.put("USER_ID_A", userIdA);
        indata.put("ROLE_CODE_B", "2");
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_RELATION_UU", indata, routeEparchyCode);
    }

}
