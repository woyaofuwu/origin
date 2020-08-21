
package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGrpMerchpInfoNewQry
{
	 public static IDataset qryMerchpInfoByUserId(String user_id) throws Exception
	    {

	        IData param = new DataMap();
	        param.put("USER_ID", user_id);

	        StringBuilder sql = new StringBuilder(1000);

	        sql.append("SELECT PARTITION_ID, ");
	        sql.append("to_char(USER_ID) USER_ID, ");
	        sql.append("MERCH_SPEC_CODE, ");
	        sql.append("PRODUCT_SPEC_CODE, ");
	        sql.append("PRODUCT_ORDER_ID, ");
	        sql.append("PRODUCT_OFFER_ID, ");
	        sql.append("GROUP_ID, ");
	        sql.append("SERV_CODE, ");
	        sql.append("BIZ_ATTR, ");
	        sql.append("STATUS, ");
	        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
	        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
	        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
	        sql.append("UPDATE_STAFF_ID, ");
	        sql.append("UPDATE_DEPART_ID, ");
	        sql.append("REMARK, ");
	        sql.append("RSRV_NUM1, ");
	        sql.append("RSRV_NUM2, ");
	        sql.append("RSRV_NUM3, ");
	        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
	        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
	        sql.append("RSRV_STR1, ");
	        sql.append("RSRV_STR2, ");
	        sql.append("RSRV_STR3, ");
	        sql.append("RSRV_STR4, ");
	        sql.append("RSRV_STR5, ");
	        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
	        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
	        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
	        sql.append("RSRV_TAG1, ");
	        sql.append("RSRV_TAG2, ");
	        sql.append("RSRV_TAG3 ");
	        sql.append("FROM TF_F_USER_GRP_MERCHP t ");
	        sql.append("WHERE 1 = 1 ");
	        sql.append("AND t.USER_ID = :USER_ID ");
	        sql.append("AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");

	        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
	    }

 
}
