package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformProxyBean {

	public static IDataset qryProxyByStaffIdA(String staffIdA,Pagination page) throws Exception{
		IData param = new DataMap();
		param.put("STAFF_ID_A", staffIdA);
		return Dao.qryByCode("TF_B_EOP_PROXY", "SEL_BY_STAFF_ID_A", param,page, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryProxyByStaffIdB(String staffIdB,Pagination page) throws Exception{
		IData param = new DataMap();
		param.put("STAFF_ID_B", staffIdB);
		return Dao.qryByCode("TF_B_EOP_PROXY", "SEL_BY_STAFF_ID_B", param,page, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset getProxyshistoryByStaffb(String staffIdB,Pagination page) throws Exception{
		IData param = new DataMap();
		param.put("STAFF_ID_B", staffIdB);
		return Dao.qryByCode("TF_B_EOP_PROXY", "SEL_HISTORY_BY_STAFFB", param,page, Route.getJourDb(BizRoute.getRouteId()));
	}

	public static IDataset getStaffInfo(String rightCode) throws Exception{
		IData param = new DataMap();
		param.put("RIGHT_CODE", rightCode);
		return Dao.qryByCode("TD_M_STAFF", "SEL_STAFFINFO_BY_RIGHTCODE", param,Route.CONN_CRM_CEN);
	}
	
	public static IDataset isExistsStaffB(String staffIdB,String roleId) throws Exception{
		IData param = new DataMap();
		param.put("STAFF_ID_B", staffIdB);
		param.put("ROLE_ID", roleId);
		return Dao.qryByCode("TF_B_EOP_PROXY", "ISEXISTS_BY_STAFFAB", param,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static boolean insertProxyInfo(IData param) throws Exception
    {
        return Dao.insert("TF_B_EOP_PROXY", param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static void deleteProxy(String proxyId)  throws Exception{
		IData param = new DataMap();
		param.put("PROXY_ID", proxyId);
		Dao.executeUpdateByCodeCode("TF_B_EOP_PROXY", "DEL_BY_PROXYID", param,Route.getJourDb(BizRoute.getRouteId()));

	}
	
	public static IDataset queryProxy(String proxyId)  throws Exception{
		IData param = new DataMap();
		param.put("PROXY_ID", proxyId);
		return Dao.qryByCode("TF_B_EOP_PROXY", "SEL_BY_PROXYID", param,Route.getJourDb(BizRoute.getRouteId()));

	}

}
