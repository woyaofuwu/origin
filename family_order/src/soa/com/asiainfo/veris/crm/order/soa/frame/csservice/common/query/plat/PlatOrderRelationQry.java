
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 平台业务订购关系批量导入管理
 * 
 * @author songxw
 */
public class PlatOrderRelationQry
{   
    /**
     * 查询平台业务订购关系同步批量信息
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPlatOrderRelationBat(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("IMPORT_ID", param.getString("IMPORT_ID"));
        qryParam.put("IMPORT_STAFF_ID", param.getString("IMPORT_STAFF_ID"));
        qryParam.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START"));
        qryParam.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END"));
        qryParam.put("DEAL_FLAG", param.getString("DEAL_FLAG"));
        //qryParam.put("DATA_TYPE", "DATA_TYPE");
        
        return Dao.qryByCodeParser("TI_B_PLATDATA_BAT", "SEL_BY_PLATDATA_BAT", qryParam, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * 查询平台业务订购关系同步导入详情
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryPlatOrderRelationBatDtl(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("IMPORT_ID", param.getString("IMPORT_ID"));
        qryParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        qryParam.put("DEAL_RESULT", param.getString("DEAL_RESULT"));
        qryParam.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START"));
        qryParam.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END"));
        qryParam.put("SP_CODE", param.getString("SP_CODE"));
        qryParam.put("BIZ_CODE", param.getString("BIZ_CODE"));

        return Dao.qryByCodeParser("TI_B_PLATDATA_BATDEAL", "SEL_PLATDATA_BY_IMPORT", qryParam, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * 根据主键查询bat表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData qryPlatDataBatByPK(IData data) throws Exception
    {
        return Dao.qryByPK("TI_B_PLATDATA_BAT", data, Route.CONN_CRM_CG);
    }
}
