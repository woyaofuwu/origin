package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformProductExtBean {
    
    public static IData qryProductByPk(String ibsysid, String recordNum,String parentRecordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        param.put("PARENT_RECORD_NUM", parentRecordNum);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_B_EOP_PRODUCT_EXT T");
        sql.append(" WHERE T.IBSYSID =:IBSYSID ");
        sql.append(" AND T.RECORD_NUM =:RECORD_NUM ");
        sql.append(" AND T.PARENT_RECORD_NUM =:PARENT_RECORD_NUM ");
        IDataset productInfos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        
        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos.first();
        }
        return new DataMap();
    }
    
    public static IDataset qryProductByParentRecordNum(String ibsysid,String parentRecordNum) throws Exception{
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("PARENT_RECORD_NUM", parentRecordNum);
        
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_B_EOP_PRODUCT_EXT T");
        sql.append(" WHERE T.IBSYSID =:IBSYSID ");
        sql.append(" AND T.PARENT_RECORD_NUM =:PARENT_RECORD_NUM ");
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        
    }
    
    public static IDataset qryProductByIbsysid(String ibsysid) throws Exception{
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_B_EOP_PRODUCT_EXT T");
        sql.append(" WHERE T.IBSYSID =:IBSYSID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        
    }

    public static IData qryProductByrecodeNum(String ibsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_B_EOP_PRODUCT_EXT T");
        sql.append(" WHERE T.IBSYSID =:IBSYSID ");
        sql.append(" AND T.RECORD_NUM =:RECORD_NUM ");
        IDataset productInfos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));

        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos.first();
        }
        return new DataMap();
    }
    
    public static IData qryProductByProductId(String ibsysid, String productId,String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("PRODUCT_ID", productId);
        param.put("SERIAL_NUMBER", serialNumber);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_B_EOP_PRODUCT_EXT T");
        sql.append(" WHERE T.IBSYSID =:IBSYSID ");
        sql.append(" AND T.PRODUCT_ID =:PRODUCT_ID ");
        sql.append(" AND T.SERIAL_NUMBER =:SERIAL_NUMBER ");
        IDataset productInfos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));

        if(DataUtils.isNotEmpty(productInfos))
        {
            return productInfos.first();
        }
        return new DataMap();
    }

    public static void updProductExtByRsrvstr2(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT_EXT", "UPD_BY_RSRV_STR2", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updProductExtByTradeidAndUserid(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT_EXT", "UPD_BY_TRADEID_USERID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static int delProductByIbsysid(String ibsysid) throws Exception{
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM TF_B_EOP_PRODUCT_EXT T WHERE T.IBSYSID=:IBSYSID ");
        
        return Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryProductExtByIbsysidAndRsrvstr5(String ibsysid,String rsrvStr5) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RSRV_STR5", rsrvStr5);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * ");
        sql.append("FROM TF_B_EOP_PRODUCT_EXT ");
        sql.append("WHERE 1=1 ");
        sql.append("AND IBSYSID = :IBSYSID ");

        if(StringUtils.isNotEmpty(rsrvStr5))
        {
            sql.append("AND RSRV_STR5 = :RSRV_STR5 ");
        }
        else
        {
            sql.append("AND RSRV_STR5 is null ");
        }


        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }
}
