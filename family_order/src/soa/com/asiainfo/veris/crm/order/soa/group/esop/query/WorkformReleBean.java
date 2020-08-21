package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;	

public class WorkformReleBean {
	
	public static IDataset qryReleByIbsysidRecordnum(String Ibsysid, String recodeNum, String status) throws Exception
	{
		IData param = new DataMap();
        param.put("BI_SN", Ibsysid);
        param.put("RELE_VALUE", recodeNum);
        param.put("STATE", status);
        return Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_IBSYSIDRECORDNUM", param, Route.getJourDb(Route.getCrmDefaultDb()));
	}
	
	public static IData qryReleByBusiformId(String busiformId, String status) throws Exception
	{
		IData param = new DataMap();
        param.put("BUSIFORM_ID", busiformId);
        param.put("VALID_TAG", status);
        IDataset resultDataset = Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_BUSIFORM_ID", param, Route.getJourDb(Route.getCrmDefaultDb()));
        IData resultInfo = new DataMap();
        if (DataUtils.isNotEmpty(resultDataset)) {
        	resultInfo = resultDataset.first();
		}
        
        return resultInfo;
	}

    public static IDataset qryBySubBusiformId(String subBusiformId) throws Exception {
        IData param = new DataMap();
        param.put("SUB_BUSIFORM_ID", subBusiformId);
        return Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_SUBBUSIFORMID", param, Route.getJourDb(Route.getCrmDefaultDb()));
    }
    
    public static IDataset qryBhBySubBusiformId(String subBusiformId) throws Exception {
        IData param = new DataMap();
        param.put("SUB_BUSIFORM_ID", subBusiformId);
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT * FROM TF_BH_EWE_RELE T ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND T.SUB_BUSIFORM_ID= :SUB_BUSIFORM_ID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
}