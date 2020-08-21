
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ManageTaskQry extends CSBizBean
{

    public static int delConfiguredTask(String paramAttr, String paramCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        StringBuilder strSql = new StringBuilder();
        params.put("PARAM_ATTR", paramAttr);
        // params.put("PARAM_CODE", paramCode);
        // return Dao.executeUpdateByCodeCode("TD_S_COMMPARA", "DEL_TASK_BY_ATTR_CODE_NEW", params);
        // strSql.delete(0, strSql.length());
        strSql.append("DELETE TD_S_COMMPARA WHERE PARAM_ATTR = :PARAM_ATTR AND PARAM_CODE in(");

        strSql.append(paramCode);
        strSql.append(")");
        return Dao.executeUpdate(strSql, params);
    }

    public static boolean insertTaskConfiguration(String subSysCode, String paramAttr, String paramCode, String eparchyCode, String paramName, String paraCode1, String startTime, String endTime, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subSysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("PARAM_CODE", paramCode);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("PARAM_NAME", paramName);
        params.put("PARA_CODE1", paraCode1);
        params.put("START_DATE", startTime);
        params.put("END_DATE", endTime);
        return Dao.insert("TD_S_COMMPARA", params);
    }

    public static IDataset isTaskConfigured(String subSysCode, String paramAttr, String paramCode, String eparchyCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subSysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("PARAM_CODE", paramCode);
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTR_CODE", params, pagination);
    }

    public static IDataset queryConfiguredTask(String subSysCode, String paramAttr, String paramCode, String eparchyCode, String paramName, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SUBSYS_CODE", subSysCode);
        params.put("PARAM_ATTR", paramAttr);
        params.put("PARAM_CODE", paramCode);
        params.put("EPARCHY_CODE", eparchyCode);
        params.put("PARAM_NAME", paramName);
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_CODE_NAME_HAIN", params, pagination);
    }

    public static IDataset queryTaskByCon(String tradeType, String tradeTypeCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_TYPE", tradeType);
        params.put("TRADE_TYPE_CODE", tradeTypeCode);
        return Dao.qryByCode("TD_S_TRADETYPE", "SEL_DISTINC_BY_CODE_TYPE", params, pagination, Route.CONN_CRM_CEN);
    }

    public static int updateConfiguredTask(String paraCode1, String paramAttr, String paramCode, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("PARA_CODE1", paraCode1);
        params.put("PARAM_ATTR", paramAttr);
        params.put("PARAM_CODE", paramCode);
        return Dao.executeUpdateByCodeCode("TD_S_COMMPARA", "UPD_CODE1_BY_ATTR_CODE", params);
    }

}
