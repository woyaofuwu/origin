
package com.asiainfo.veris.crm.order.soa.group.grpcautionfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GrpCautionFeeMgrQry
{
		
	/**
     * 集团客户保证金管理分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryCautionFeeList(IData param, Pagination pagination)
    	throws Exception
    {
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.PARTITION_ID,");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID,");
        parser.addSQL(" T.SERIAL_NUMBER,");
        parser.addSQL(" T.AUDIT_ORDER,");
        parser.addSQL(" T.DEPOSIT_FEE,");
        parser.addSQL(" T.DEPOSIT_TYPE,");
        parser.addSQL(" T.INSERT_STAFF_ID,");
        parser.addSQL(" T.INSERT_DEPART_ID,");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME,");
        parser.addSQL(" T.UPDATE_STAFF_ID,");
        parser.addSQL(" T.UPDATE_DEPART_ID,");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL(" T.REMARK,");
        parser.addSQL(" T.RSRV_STR1,");
        parser.addSQL(" T.RSRV_STR2,");
        parser.addSQL(" T.RSRV_STR3,");
        parser.addSQL(" T.RSRV_STR4,");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')  RSRV_DATE1 ");
        parser.addSQL(" FROM TF_F_USER_GRP_CAUTIONFEE T ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        return Dao.qryByParse(parser, pagination);
    }
    
    /**
     * 根据userId查询集团客户保证金
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryCautionFeeByUserId(IData param) 
    	throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.PARTITION_ID,");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID,");
        parser.addSQL(" T.SERIAL_NUMBER,");
        parser.addSQL(" T.AUDIT_ORDER,");
        parser.addSQL(" T.DEPOSIT_FEE,");
        parser.addSQL(" T.DEPOSIT_TYPE,");
        parser.addSQL(" T.INSERT_STAFF_ID,");
        parser.addSQL(" T.INSERT_DEPART_ID,");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME,");
        parser.addSQL(" T.UPDATE_STAFF_ID,");
        parser.addSQL(" T.UPDATE_DEPART_ID,");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL(" T.REMARK,");
        parser.addSQL(" T.RSRV_STR1,");
        parser.addSQL(" T.RSRV_STR2,");
        parser.addSQL(" T.RSRV_STR3,");
        parser.addSQL(" T.RSRV_STR4,");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')  RSRV_DATE1 ");
        parser.addSQL(" FROM TF_F_USER_GRP_CAUTIONFEE T ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.DEPOSIT_TYPE = :DEPOSIT_TYPE ");
        return Dao.qryByParse(parser);
    }
    
    
    /**
     * 根据userId查询集团客户保证金
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryOneCautionFeeByUserId(IData param) 
    	throws Exception
    {
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.PARTITION_ID,");
        parser.addSQL(" TO_CHAR(T.USER_ID) USER_ID,");
        parser.addSQL(" T.SERIAL_NUMBER,");
        parser.addSQL(" T.AUDIT_ORDER,");
        parser.addSQL(" T.DEPOSIT_FEE,");
        parser.addSQL(" T.DEPOSIT_TYPE,");
        parser.addSQL(" T.INSERT_STAFF_ID,");
        parser.addSQL(" T.INSERT_DEPART_ID,");
        parser.addSQL(" TO_CHAR(T.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME,");
        parser.addSQL(" T.UPDATE_STAFF_ID,");
        parser.addSQL(" T.UPDATE_DEPART_ID,");
        parser.addSQL(" TO_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL(" T.REMARK,");
        parser.addSQL(" T.RSRV_STR1,");
        parser.addSQL(" T.RSRV_STR2,");
        parser.addSQL(" T.RSRV_STR3,");
        parser.addSQL(" T.RSRV_STR4,");
        parser.addSQL(" TO_CHAR(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss')  RSRV_DATE1 ");
        parser.addSQL(" FROM TF_F_USER_GRP_CAUTIONFEE T ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        return Dao.qryByParse(parser);
    }
    
    /**
     * 根据user_id修改记录
     * @param userId
     * @param auditOrder
     * @param depositFee
     * @param depositType
     * @return
     * @throws Exception
     */
	 public static int updateCautionFeeByUserId(String userId,String auditOrder,
			 String depositFee,String depositType) throws Exception
	 {
	     IData param = new DataMap();
	     param.put("USER_ID", userId);
	     param.put("AUDIT_ORDER", auditOrder);
	     param.put("DEPOSIT_FEE", depositFee);
	     param.put("DEPOSIT_TYPE", depositType);
	     param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	     param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	     StringBuilder sql = new StringBuilder(1000);
	     sql.append(" UPDATE TF_F_USER_GRP_CAUTIONFEE T ");
	     sql.append(" 	SET T.AUDIT_ORDER = :AUDIT_ORDER,");
	     sql.append(" 		T.DEPOSIT_FEE = T.DEPOSIT_FEE + :DEPOSIT_FEE,");
	     sql.append(" 		T.DEPOSIT_TYPE = :DEPOSIT_TYPE,");
	     sql.append(" 		T.UPDATE_STAFF_ID = :UPDATE_STAFF_ID,");
	     sql.append(" 		T.UPDATE_DEPART_ID = :UPDATE_DEPART_ID,");
	     sql.append(" 		T.UPDATE_TIME = SYSDATE ");
	     sql.append(" WHERE ");
	     sql.append(" 	T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
	     sql.append(" 	AND T.USER_ID = :USER_ID ");
	     sql.append(" AND T.DEPOSIT_TYPE = :DEPOSIT_TYPE ");
	     return Dao.executeUpdate(sql, param);
	 }

}
