package com.asiainfo.veris.crm.order.soa.group.mobilecloudinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class MobileCloudInfoBean  extends GroupBean {
	
	public IDataset selMobileCloudInfoByCondition(IData param) throws Exception {

    	return Dao.qryByParse(selMobileCloudInfoByConditionSql(param),Route.CONN_CRM_CEN);
    }
    public IDataset selMobileCloudInfoByCondition(IData param, Pagination pagination) throws Exception {

    	return Dao.qryByParse(selMobileCloudInfoByConditionSql(param), pagination,Route.CONN_CRM_CEN);
    }
    private  SQLParser selMobileCloudInfoByConditionSql(IData param) throws Exception{
    	System.out.println("MobileCloudInfoBean-selMobileCloudInfoByConditionSql-param:"+param);
    	SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT INST_ID, TYPE_CODE, OFFER_CODE, OFFER_NAME, CATEGORY_ID, CATEGORY_NAME, " );
        sql.addSQL("to_char(INSERT_TIME,'yyyy-MM-dd HH24:mi:ss') INSERT_TIME," );
        sql.addSQL(" to_char(UPDATE_TIME,'yyyy-MM-dd HH24:mi:ss') UPDATE_TIME,UPDATE_DEPARTID, " );
        sql.addSQL("UPDATE_STAFF_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, " );
        sql.addSQL("to_char(RSRV_DATE1,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE2 " );
        sql.addSQL(" FROM TD_S_MOBILECLOUDINFO ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND (TYPE_CODE =:TYPE_CODE or :TYPE_CODE is null) ");
        sql.addSQL(" AND (OFFER_CODE =:OFFER_CODE or :OFFER_CODE is null) ");
        sql.addSQL(" AND (OFFER_NAME LIKE '%"+param.getString("OFFER_NAME", "")+"%' or :OFFER_NAME is null) ");
/*        sql.addSQL(" AND (CATEGORY_ID =:CATEGORY_ID or :CATEGORY_ID is null) ");
        sql.addSQL(" AND (OFFER_NAME LIKE '%"+param.getString("OFFER_NAME", "")+"%' or :OFFER_NAME is null) ");
        sql.addSQL(" AND (CATEGORY_NAME LIKE '%"+param.getString("CATEGORY_NAME", "")+"%' or :CATEGORY_NAME is null) ");
 */       
        return sql;
    }
    public static int[] updMobileCloudInfo(IDataset infoList) throws Exception {
        
        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" UPDATE TD_S_MOBILECLOUDINFO A SET A.TYPE_CODE =:TYPE_CODE,A.UPDATE_TIME=SYSDATE,A.UPDATE_STAFF_ID=:UPDATE_STAFF_ID,"+
        		"UPDATE_DEPARTID=:UPDATE_DEPARTID WHERE 1=1 and a.INST_ID=:INST_ID ");
        
        return Dao.executeBatch(sql, infoList, Route.CONN_CRM_CEN);

    }
}
