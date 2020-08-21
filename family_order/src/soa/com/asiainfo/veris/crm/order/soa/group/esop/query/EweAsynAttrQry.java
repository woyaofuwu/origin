package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class EweAsynAttrQry 
{
	/**
     * 新增业务数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertAsynAttrInfos(IDataset params) throws Exception
    {
        return Dao.insert("TF_B_EWE_ASYN_ATTR", params, Route.getJourDb(Route.getJourDb()));
    }
    
    public static IDataset qryAsynAttrInfosByAsynId(String asynId) throws Exception{
        IData param = new DataMap();
        param.put("ASYN_ID", asynId);
        
        StringBuilder sql = new StringBuilder(100);
        sql.append(" SELECT * FROM TF_B_EWE_ASYN_ATTR T ");
        sql.append(" WHERE T.ASYN_ID =: ASYN_ID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.getJourDb()));
    }
    
    public static IDataset qrybHAsynAttrInfosByAsynId(String asynId) throws Exception{
        IData param = new DataMap();
        param.put("ASYN_ID", asynId);
        
        StringBuilder sql = new StringBuilder(100);
        sql.append(" SELECT * FROM TF_BH_EWE_ASYN_ATTR T ");
        sql.append(" WHERE T.ASYN_ID =:ASYN_ID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.getJourDb()));
    }
}