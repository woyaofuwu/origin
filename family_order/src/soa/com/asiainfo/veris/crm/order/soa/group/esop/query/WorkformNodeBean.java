package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformNodeBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformNode(IData param) throws Exception
    {
        return Dao.insert("TF_B_EOP_NODE", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static boolean updataEopNode(IData param) throws Exception
    {
    	return Dao.update("TF_B_EOP_NODE", param, new String[]{"IBSYSID", "NODE_ID"},Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryWorkformNodeByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_NODE", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static IDataset qryWorkformNodeByIbsysidOrderByTime(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_NODE", "SEL_BY_IBSYSID_ORDER", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delWorkformNodeByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_NODE", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryNodeByIbsysidNodeDesc(String ibsysid, String nodeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("NODE_ID", nodeId);
        return Dao.qryByCode("TF_B_EOP_NODE", "SEL_BY_IBSYSID_NODEID", param, Route.getJourDb(BizRoute.getRouteId()));

    }
    
    public static IDataset qryNodeByIbsysidNodeTimeDesc(String ibsysid, String nodeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("NODE_ID", nodeId);
        return Dao.qryByCode("TF_B_EOP_NODE", "SEL_BY_IBSYSID_NODEID_DESC", param, Route.getJourDb(BizRoute.getRouteId()));

    }
}
