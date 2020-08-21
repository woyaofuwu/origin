package com.asiainfo.veris.crm.order.soa.group.esop.tapmarketing;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class TapMarketingBean  extends GroupBean {
	
	public IDataset selTapMarketingByCondition(IData param) throws Exception {

    	return Dao.qryByParse(selTapMarketingByConditionSql(param),Route.getJourDb(BizRoute.getRouteId()));
    }
    public IDataset selTapMarketingByCondition(IData param, Pagination pagination) throws Exception {

    	return Dao.qryByParse(selTapMarketingByConditionSql(param), pagination,Route.getJourDb(BizRoute.getRouteId()));
    }
    private  SQLParser selTapMarketingByConditionSql(IData param) throws Exception{
    	SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT IBSYSID, IBSYSID_TAPMARKETING, IBSYSID_EXCITATION, PROVINCEA, " );
        sql.addSQL("CITY_ID, CITYA, FRIENDBUSINESS_NAME, CUST_ID, CUST_NAME, " );
        sql.addSQL("RESPONSIBILITY_ID, RESPONSIBILITY_NAME, RESPONSIBILITY_PHONE, " );
        sql.addSQL("LINENAME, LINETYPE, BANDWIDTH, PRODUCT_NO, PRODUCT_NUMBER, " );
        sql.addSQL("CONTRACT_AGE, MONTHLYFEE_TAP, MONTHLYFEE_EXCITATION, LINEPRICE_TAP, " );
        sql.addSQL("LINEPRICE_EXCITATION, RESULT_CODE, AUDIT_OPINION, LEADER_OPINION, " );
        sql.addSQL("to_char(EXCITATION_DATE,'yyyy-MM-dd HH24:mi:ss') EXCITATION_DATE, " );
        sql.addSQL("to_char(AUDIT_DATE,'yyyy-MM-dd HH24:mi:ss') AUDIT_DATE, to_char(LEADER_DATE,'yyyy-MM-dd HH24:mi:ss') LEADER_DATE, " );
        sql.addSQL("to_char(ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss') ACCEPT_DATE, to_char(UPDATE_DATE,'yyyy-MM-dd HH24:mi:ss') UPDATE_DATE, " );
        sql.addSQL("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE1, " );
        sql.addSQL("to_char(RSRV_DATE2,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE3 ");
        sql.addSQL(" FROM TF_B_EOP_TAPMARKETING ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND (IBSYSID =:IBSYSID or :IBSYSID is null) ");
        sql.addSQL(" AND (IBSYSID_TAPMARKETING =:IBSYSID_TAPMARKETING or :IBSYSID_TAPMARKETING is null) ");
        sql.addSQL(" AND (IBSYSID_EXCITATION =:IBSYSID_EXCITATION or :IBSYSID_EXCITATION is null) ");
        sql.addSQL(" AND (RESULT_CODE=:RESULT_CODE or :RESULT_CODE is null) ");
        sql.addSQL(" AND (RESPONSIBILITY_ID=:RESPONSIBILITY_ID or :RESPONSIBILITY_ID is null) ");
        sql.addSQL(" AND (CITY_ID=:CITY_ID or :CITY_ID is null) ");
        sql.addSQL(" AND (CUST_ID=:CUST_ID or :CUST_ID is null) ");
        sql.addSQL(" AND (LINETYPE=:LINETYPE or :LINETYPE is null) ");
        sql.addSQL(" AND (PRODUCT_NO=:PRODUCT_NO or :PRODUCT_NO is null) ");
        sql.addSQL(" AND (ACCEPT_DATE>=to_date(:ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss') or :ACCEPT_DATE is null) ");
        sql.addSQL(" AND (to_char(LEADER_DATE,'yyyy-MM')=:LEADER_MONTH or :LEADER_MONTH is null) ");
        return sql;
    }
    
    /**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformTapMarketing(IData param) throws Exception {
         return Dao.insert("TF_B_EOP_TAPMARKETING", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static int updateTapMarketingInfo(String[] names,String[] values,IData data) throws Exception {
		Date date= new Date();
    	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update_time =simpleDateFormat.format(date);
		data.put("UPDATE_DATE", update_time);
		if(names.length>0&&values.length>0&&names.length==values.length){
			String sql = "update TF_B_EOP_TAPMARKETING set UPDATE_DATE=to_date(:UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss')";
			for(int i=0; i<names.length;i++){
				if(names[i].endsWith("DATE")){ //日期类型不做处理
					sql+=","+names[i]+"="+values[i];
				}else{
					sql+=","+names[i]+"='"+values[i]+"'";
				}
			}
			sql+=" where IBSYSID=:IBSYSID";
			SQLParser sqlP = new SQLParser(data);
			sqlP.addSQL(sql);
			return Dao.executeUpdate(sqlP,  Route.getJourDb(BizRoute.getRouteId()));
		}else{
			return 0;
		}
		
	}
    
    public static IDataset selTapMarketingByIbsysidMarketing(IData param, Pagination pagination) throws Exception
    {
    	SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT IBSYSID_MARKETING, MARKETING_NAME, IBSYSID_AUDIT, CUSTOMNO, CUSTOMNAME, APPLY_STAFF_ID, AUDIT_STAFF_ID, MARKETING_IS_SUCC, MARKETING_MONTH,to_char(MARKETING_START_DATE,'yyyy-MM-dd') MARKETING_START_DATE,to_char(MARKETING_END_DATE,'yyyy-MM-dd') MARKETING_END_DATE,to_char(AUDIT_START_DATE,'yyyy-MM-dd') AUDIT_START_DATE, HANDLE_CODE, HANDLE_INFO,to_char(HANDLE_DATE,'yyyy-MM-dd HH24:mi:ss')  HANDLE_DATE,to_char(AUDIT_DATE,'yyyy-MM-dd HH24:mi:ss') AUDIT_DATE, RESULT_CODE, RESULT_INFO,to_char(ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss') ACCEPT_DATE, UPDATE_DATE, ENCLOSURE, CITY_NAME FROM TF_B_EOP_MARKETING"); 
        sql.addSQL(" WHERE 1=1");
		sql.addSQL(" AND IBSYSID_MARKETING = :IBSYSID_MARKETING");
		sql.addSQL(" AND RESULT_CODE = :RESULT_CODE");
		sql.addSQL(" AND (MARKETING_END_DATE <=to_date(:MARKETING_END_DATE,'yyyy-MM-dd HH24:mi:ss'))");
		sql.addSQL(" AND (ACCEPT_DATE >=to_date(:ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss'))");
		sql.addSQL(" AND CUSTOMNO = :CUSTOMNO");
		sql.addSQL(" AND MARKETING_IS_SUCC = :MARKETING_IS_SUCC");
		
		return Dao.qryByParse(sql, pagination,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateTapMarketingByIbsysidMarketing(IData param) throws Exception
    {
    	Dao.executeUpdateByCodeCode("TF_B_EOP_MARKETING", "UPD_BY_IBSYSID_MARKETING", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateTapMarketingByIbsysidMarketingAudit(IData param) throws Exception
    {
    	Dao.executeUpdateByCodeCode("TF_B_EOP_MARKETING", "UPD_BY_IBSYSID_MARKETING_AUDIT", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
