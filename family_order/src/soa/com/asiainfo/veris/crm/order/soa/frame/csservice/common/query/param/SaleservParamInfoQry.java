
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleservParamInfoQry
{

    public static IDataset queryByCode1(String paramValue, String paraCode1, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PARAM_VALUE", paramValue);
        inparam.put("PARA_CODE1", paraCode1);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALESERV_COMMPARA", "SEL_BY_PARA_CODE1", inparam, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByParaCode(String paramValue, String paraCode, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PARAM_VALUE", paramValue);
        inparam.put("PARA_CODE", paraCode);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALESERV_COMMPARA", "SEL_BY_PARA_CODE", inparam, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByParaCodeNoCache(String paramValue, String paraCode, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("PARAM_VALUE", paramValue);
        inparam.put("PARA_CODE", paraCode);
        inparam.put("EPARCHY_CODE", eparchyCode);

        StringBuilder sql = new StringBuilder(2500);
        sql.append("SELECT T.PARAM_VALUE,T.PARA_NAME,T.PARA_CODE,T.PARA_CODE1,T.PARA_CODE2,T.PARA_CODE3,T.PARA_CODE4, ");
        sql.append("T.PARA_CODE5,T.PARA_CODE6,T.PARA_CODE7,T.PARA_CODE8,T.PARA_CODE9,T.PARA_CODE10,T.EPARCHY_CODE ");
        sql.append(" from TD_B_SALESERV_COMMPARA T ");
        sql.append(" WHERE PARAM_VALUE=:PARAM_VALUE ");
        sql.append(" AND PARA_CODE=:PARA_CODE ");
        sql.append(" AND (EPARCHY_CODE=:EPARCHY_CODE OR EPARCHY_CODE='ZZZZ') ");
        sql.append(" AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        IDataset ids = Dao.qryBySql(sql, inparam, Route.CONN_CRM_CEN);
        return ids;
    }

    public static IDataset queryByParaValue(String paramValue, String eparchyCode) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("PARAM_VALUE", paramValue);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALESERV_COMMPARA", "SEL_ALL_BY_PARAM_VALUE", inparam, Route.CONN_CRM_CEN);
    }
}
