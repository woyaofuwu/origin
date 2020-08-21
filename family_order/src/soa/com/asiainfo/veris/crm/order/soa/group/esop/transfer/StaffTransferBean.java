package com.asiainfo.veris.crm.order.soa.group.esop.transfer;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class StaffTransferBean {

	public static int[] insertStaffTransfer(IDataset param) throws Exception
    {
	    IData data = param.toData();
		String typeId = data.getString("TYPE_ID");
		StringBuilder sql = new StringBuilder(1000);        
	    if("1".equals(typeId)) {
	        sql.append(" insert into TF_B_EOP_STAFFTRANSFERLOG ");
            sql.append(" (IBSYSID,BATCHID,TRANSFER_TABLES,TRANSFER_COLUMN1,TRANSFER_COLUMN2,TRANSFER_COLUMN3," +
                    "TRANSFER_PRIMARYKEY1,TRANSFER_PRIMARYKEY2,TRANSFER_PRIMARYKEY3,ACCEPT_DATE,UPDATE_DATE,REMARK," +
                    "UPDATE_COLUMNKEY1,UPDATE_OLDCOLUMNVAL1,UPDATE_NEWCOLUMNVAL1,UPDATE_COLUMNKEY2,UPDATE_OLDCOLUMNVAL2," +
                    "UPDATE_NEWCOLUMNVAL2,UPDATE_COLUMNKEY3,UPDATE_OLDCOLUMNVAL3,UPDATE_NEWCOLUMNVAL3, ");
            sql.append("TYPE_ID  ) ");
            sql.append(" values   ");
            sql.append(" (:IBSYSID,:BATCHID,:TRANSFER_TABLES,:TRANSFER_COLUMN1,:TRANSFER_COLUMN2,:TRANSFER_COLUMN3," +
                    ":TRANSFER_PRIMARYKEY1,:TRANSFER_PRIMARYKEY2,:TRANSFER_PRIMARYKEY3,to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss'),:REMARK," +
                    ":UPDATE_COLUMNKEY1,:UPDATE_OLDCOLUMNVAL1,:UPDATE_NEWCOLUMNVAL1,:UPDATE_COLUMNKEY2,:UPDATE_OLDCOLUMNVAL2," +
                    ":UPDATE_NEWCOLUMNVAL2,:UPDATE_COLUMNKEY3,:UPDATE_OLDCOLUMNVAL3,:UPDATE_NEWCOLUMNVAL3, ");
            sql.append(":TYPE_ID)  ");
	    }else {
	        sql.append(" insert into TF_B_EOP_STAFFTRANSFERLOG ");
	        sql.append(" (IBSYSID,BATCHID,TRANSFER_TABLES,TRANSFER_COLUMN1,TRANSFER_COLUMN2,TRANSFER_COLUMN3," +
	                "TRANSFER_PRIMARYKEY1,TRANSFER_PRIMARYKEY2,TRANSFER_PRIMARYKEY3,ACCEPT_DATE,UPDATE_DATE,REMARK," +
	                "UPDATE_COLUMNKEY1,UPDATE_OLDCOLUMNVAL1,UPDATE_NEWCOLUMNVAL1,UPDATE_COLUMNKEY2,UPDATE_OLDCOLUMNVAL2," +
	                "UPDATE_NEWCOLUMNVAL2,UPDATE_COLUMNKEY3,UPDATE_OLDCOLUMNVAL3,UPDATE_NEWCOLUMNVAL3, ");
	        sql.append("IBSYS_ID,GROUP_ID,NODE_NAME,PRODUCT_NAME,CUST_NAME,INFO_TOPIC,TYPE_ID  ) ");
	        sql.append(" values   ");
	        sql.append(" (:IBSYSID,:BATCHID,:TRANSFER_TABLES,:TRANSFER_COLUMN1,:TRANSFER_COLUMN2,:TRANSFER_COLUMN3," +
	                ":TRANSFER_PRIMARYKEY1,:TRANSFER_PRIMARYKEY2,:TRANSFER_PRIMARYKEY3,to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'),to_date(:UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss'),:REMARK," +
	                ":UPDATE_COLUMNKEY1,:UPDATE_OLDCOLUMNVAL1,:UPDATE_NEWCOLUMNVAL1,:UPDATE_COLUMNKEY2,:UPDATE_OLDCOLUMNVAL2," +
	                ":UPDATE_NEWCOLUMNVAL2,:UPDATE_COLUMNKEY3,:UPDATE_OLDCOLUMNVAL3,:UPDATE_NEWCOLUMNVAL3, ");
	        sql.append(":IBSYS_ID,:GROUP_ID,:NODE_NAME,:PRODUCT_NAME,:CUST_NAME,:INFO_TOPIC,:TYPE_ID)  ");
	    }
		

        
        return Dao.executeBatch(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    } 
	
	public static IDataset qryStaffNameForName(String staffName) throws Exception
    {
    	IData param = new DataMap();
    	param.put("STAFF_NAME", staffName);
        return Dao.qryByCode("TD_M_STAFF", "SEL_BY_STAFFNAME", param, Route.CONN_SYS);
    }
	public static IDataset qryStaffNameForId(String staffId) throws Exception
	{
	    IData param = new DataMap();
	    param.put("STAFF_ID", staffId);
	    SQLParser sql = new SQLParser(param);
	    sql.addSQL("SELECT STAFF_ID,STAFF_NAME ");
	    sql.addSQL("FROM TD_M_STAFF ");
	    sql.addSQL("WHERE 1=1 " );
	    sql.addSQL("AND (:STAFF_ID  IS NULL OR STAFF_ID=:STAFF_ID) ");
        IDataset rest = Dao.qryByParse(sql, Route.CONN_SYS);
	    return rest;
	}
}
