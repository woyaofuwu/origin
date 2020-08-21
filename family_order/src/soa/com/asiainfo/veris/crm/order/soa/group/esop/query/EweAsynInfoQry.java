package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class EweAsynInfoQry 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertAsynInfo(IData param) throws Exception
    {
        return Dao.insert("TF_B_EWE_ASYN", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    
    public static IDataset qryInfosByBusiformNode(String busiformNodeId, String busiformId, String nodeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("BUSIFORM_NODE_ID", busiformNodeId);
    	param.put("BUSIFORM_ID", busiformId);
    	param.put("NODE_ID", nodeId);

    	return Dao.qryByCode("TF_B_EWE_ASYN", "SEL_BY_BUSIFORM_NODE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryInfosByBusiformNodeId(String busiformNodeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("BUSIFORM_NODE_ID", busiformNodeId);

    	return Dao.qryByCode("TF_B_EWE_ASYN", "SEL_ASYNATTR_BY_BUSIFORMNODEID", param, Route.getJourDb(BizRoute.getRouteId()));

    	
    }
    
    public static IDataset qrybHInfosByBusiformNode(String busiformNodeId, String busiformId, String nodeId) throws Exception{
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        param.put("BUSIFORM_ID", busiformId);
        param.put("NODE_ID", nodeId);
        
        StringBuilder sql  = new StringBuilder(200);
        sql.append(" SELECT * FROM TF_BH_EWE_ASYN T ");
        sql.append(" WHERE T.BUSIFORM_NODE_ID = :BUSIFORM_NODE_ID ");
        sql.append(" AND T.BUSIFORM_ID = :BUSIFORM_ID ");
        sql.append(" AND T.NODE_ID = :NODE_ID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        
    }
    
}