package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WorkformModiTraceBean {

	public static IDataset qryAccetanceperiodChangeByIbsysid(IData param) throws Exception{
		StringBuilder strSql = new StringBuilder();
		strSql.append(" SELECT * FROM TF_B_EOP_MODI_TRACE ");
		strSql.append(" WHERE MAIN_IBSYSID = :IBSYSID ");
		strSql.append(" AND ATTR_TYPE = 'P' ");
		IDataset results = Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
		if(results != null){
			return results;
		}else{
			return new DatasetList();
		}
	}
	
    public static IDataset qryModiTraceByIbsysid(IData param) throws Exception {
        StringBuilder strSql = new StringBuilder();
        strSql.append(" SELECT * FROM TF_B_EOP_MODI_TRACE ");
        strSql.append(" WHERE IBSYSID = :IBSYSID ");
        strSql.append(" AND ATTR_TYPE = 'F' ");
        IDataset results = Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
        if(results != null) {
            return results;
        } else {
            return new DatasetList();
        }
    }
    
    public static IDataset qryModiTraceHByIbsysid(IData param) throws Exception {
        StringBuilder strSql = new StringBuilder();
        strSql.append(" SELECT * FROM TF_BH_EOP_MODI_TRACE ");
        strSql.append(" WHERE IBSYSID = :IBSYSID ");
        strSql.append(" AND ATTR_TYPE = 'F' ");
        IDataset results = Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
        if(results != null) {
            return results;
        } else {
            return new DatasetList();
        }
    }

	public static IDataset qryModiTraceInfos(String mainIbsysid) throws Exception
	{
		IData param = new DataMap();
    	param.put("MAIN_IBSYSID", mainIbsysid);
		return Dao.qryByCode("TF_B_EOP_MODI_TRACE", "SEL_BY_MAINIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static void delWorkformModiTrace(String mainIbsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("MAIN_IBSYSID", mainIbsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_MODI_TRACE", "DEL_BY_MAINIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static boolean insertModiTrace(IData param) throws Exception {
        return Dao.insert("TF_B_EOP_MODI_TRACE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
