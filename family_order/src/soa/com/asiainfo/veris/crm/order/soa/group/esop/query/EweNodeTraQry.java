package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class EweNodeTraQry 
{

	/**
	 * 按esop单号  轨迹表
	 * 降序查询
	 * @param busiformId
	 * @param nodeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryEweNodeTraByBusiformIdAndNodeId(String busiformId, String nodeId) throws Exception
	{
		IData param = new DataMap();
		param.put("BUSIFORM_ID", busiformId);
		param.put("NODE_ID", nodeId);
		return Dao.qryByCode("TF_B_EWE_NODE_TRA", "SEL_BY_BUSIFORM_ID_NODE_ID_DESC", param,	Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
	 * 修改轨迹表处理员工
	 * @param param
	 * @throws Exception
	 */
	public static void updateEweNodeTraDealStaffId(IData param) throws Exception
	{
	    if(StringUtils.isBlank(param.getString("SEND_STAFF_ID")))
	    {
	        CSAppException.apperr(GrpException.CRM_GRP_713, "转移员工[SEND_STAFF_ID]不能为空！");
	    }
	    StringBuilder sql = new StringBuilder(1000);
	    sql.append("UPDATE tf_b_ewe_node_tra SET DEAL_STAFF_ID = :RECE_STAFF_ID, ");
	    sql.append(" DEAL_EPARCHY_CODE = :RECE_EPARCHY_CODE, ");
	    sql.append(" DEAL_DEPART_ID = :RECE_DEPART_ID, ");
	    sql.append(" REC_STAFF_ID = :SEND_STAFF_ID, ");
	    sql.append(" REC_EPARCHY_CODE = DEAL_EPARCHY_CODE, ");
	    sql.append(" REC_DEPART_ID = DEAL_DEPART_ID, ");
	    sql.append(" UPDATE_STAFF_ID = :UPDATE_STAFF_ID, ");
	    sql.append(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID, ");
	    sql.append(" UPDATE_DATE = SYSDATE ");
	    sql.append(" WHERE DEAL_STAFF_ID = :SEND_STAFF_ID ");
	    Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
	

	
	
	
	public static int[] updStaffForEweNodeTra(IDataset param) throws Exception {

        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" UPDATE TF_B_EWE_NODE_TRA A SET A.DEAL_STAFF_ID =:DEAL_NEW_STAFF_ID  WHERE A.BUSIFORM_NODE_ID=:BUSIFORM_NODE_ID ");
        
        return Dao.executeBatch(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static IDataset selStaffForEweNodeTra(String oldStaff) throws Exception {

        
        IData param = new DataMap();
        param.put("DEAL_OLD_STAFF_ID", oldStaff);

        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" SELECT a.BUSIFORM_NODE_ID,a.ACCEPT_MONTH,a.BUSIFORM_ID FROM TF_B_EWE_NODE_TRA A  WHERE A.DEAL_STAFF_ID=:DEAL_OLD_STAFF_ID ");
        
        return Dao.qryBySql(sql, param,Route.getJourDb(BizRoute.getRouteId()));
    }
    
	public static IDataset qryEweNodeTraByBusiformNodeId(String busiformNodeId) throws Exception
	{
		IData param = new DataMap();
		param.put("BUSIFORM_NODE_ID", busiformNodeId);
		return Dao.qryByCode("TF_B_EWE_NODE_TRA", "SEL_BY_PK", param,	Route.getJourDb(BizRoute.getRouteId()));
	}
}
