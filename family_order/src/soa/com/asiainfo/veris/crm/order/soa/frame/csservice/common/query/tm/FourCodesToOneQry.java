
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FourCodesToOneQry
{

    public static IDataset queryBatInfo(String batId, String batName, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("BAT_NAME", batName);
        data.put("EPARCHYCODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_BAT_FORSMHY", data, Route.CONN_CRM_CEN);
        return result;
    }

    /**
     * 查看四码合一机型合约价
     */
    public static IDataset queryFourCodesToOnePriceInfo(String batId, String modelCode, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_CODE", modelCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_TEMINALLEVEL_SIMA", data, page, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryIsExistsModelCode(String batId, String modelCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_CODE", modelCode);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_BAT_ISEXISTS", data, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryModelCodeInfo(String modelCode, String modelDesc, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("MODEL_CODE", modelCode);
        data.put("MODEL_DESC", modelDesc);
        return Dao.qryByCode("TD_S_RES_MODEL", "SEL_ALL_BY_CODE", data, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryModelInfoByBatId(String batId, String modelDesc) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_DESC", modelDesc);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_MODELCODE_IN_BAT", data, Route.CONN_CRM_CEN);
        return result;
    }

    public static IDataset queryPriceLevelInfo(String batId, String modelCode, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("BAT_ID", batId);
        data.put("MODEL_CODE", modelCode);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset result = Dao.qryByCode("TD_S_COMMPARA", "SEL_LEVEL_BY_MODELCODE", data, Route.CONN_CRM_CEN);
        return result;
    }
}
