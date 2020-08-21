/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


/**
 * @CREATED by 
 */
public class CouponsQuotaInfoQry
{
	static Logger logger=Logger.getLogger(CouponsQuotaInfoQry.class);
    public static IDataset getWorkOrders(String city_id) throws Exception
    {
        String sql = " SELECT AREA_CODE, AUDIT_ORDER_ID,TOTAL_AMOUNT/100 TOTAL_AMOUNT,TO_CHAR(t.OPERA_TIME,'YYYY-MM-DD HH24:MI:SS') OPERA_TIME,OPERA_STAFF_ID,TO_CHAR(START_TIME,'YYYY-MM-DD HH24:MI:SS') START_TIME,TO_CHAR(END_TIME,'YYYY-MM-DD HH24:MI:SS') END_TIME,BALANCE/100 BALANCE," +
        		//"RSRV_NUM1/100 RSRV_NUM1," +
        		"AMOUNTS/100 AMOUNTS FROM TL_B_COUPONS_QUOTA T ";

        IData param = new DataMap();
        sql += "WHERE T.AREA_CODE = :AREA_CODE ";
        sql += " AND SYSDATE BETWEEN T.START_TIME AND T.END_TIME ";
        sql += " order by t.audit_order_id asc ";
        param.put("AREA_CODE", city_id);
  

        return Dao.qryBySql(new StringBuilder(sql), param);

    }
    
    
    
    /**
     * 返回某员工相关联的未使用的有效的优惠券值相加
     * 
     * @param param
     * @throws Exception
     */
    public static int getSumTicketValue(IData param) throws Exception{
		
    	String sql = "select  sum(TICKET_VALUE) SUM  from TL_B_USER_COUPONS T where T.RSRV_STR1=:AUDIT_ORDER_ID " +
    					"AND T.TICKET_STATE='0' AND SYSDATE BETWEEN T.UPDATE_TIME AND TICKET_END_DATE";
    	
    	IDataset sumTicketValue = Dao.qryBySql(new StringBuilder(sql), param);
    	if(sumTicketValue.getData(0).getString("SUM")!=null){
    		int usedBanlance = Integer.parseInt(sumTicketValue.getData(0).getString("SUM"));
    		return usedBanlance;
    	}else{
    		return 0;
    	}	
    }
    
    
    
    
    

    /**
     * 根据rowid查询记录
     * 
     * @param param
     * @throws Exception
     */
    public static IDataset queryCouponsQuotaInfo(String rowId) throws Exception
    {
        String sql = "SELECT  AREA_CODE, AUDIT_ORDER_ID,TOTAL_AMOUNT/100 TOTAL_AMOUNT,BALANCE/100 BALANCE,AMOUNTS/100 AMOUNTS,TO_CHAR(t.OPERA_TIME,'YYYY-MM-DD HH24:MI:SS') OPERA_TIME,OPERA_STAFF_ID,TO_CHAR(START_TIME,'YYYY-MM-DD HH24:MI:SS') START_TIME,TO_CHAR(END_TIME,'YYYY-MM-DD HH24:MI:SS') END_TIME,ROWID ROW_ID " + "FROM TL_B_COUPONS_QUOTA T WHERE ROWID=:ROWID ";
        IData param = new DataMap();
        param.put("ROWID", rowId);

        if (StringUtils.isNotBlank(rowId))
        {
            return Dao.qryBySql(new StringBuilder(sql), param);
        }
        else
        {
            return new DatasetList();
        }

    }

    /**
     * 根据工号查询记录是否存在
     * 
     * @param audit_order_id
     * @return
     * @throws Exception
     */
    public static IDataset queryCouponsQuotaInfoByOrderID(String audit_order_id) throws Exception
    {
        String sql = "SELECT 1 FROM TL_B_COUPONS_QUOTA T WHERE audit_order_id =:AUDIT_ORDER_ID ";
        IData param = new DataMap();
        param.put("AUDIT_ORDER_ID", audit_order_id);

        return Dao.qryBySql(new StringBuilder(sql), param);
    }

    /**
     * 根据查询条件查询记录
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     * @CREATE 
     */
    public static IDataset queryCouponsQuotaInfos(IData param, Pagination pagination) throws Exception
    {
    	/**
         * 
         * @author zhangxing3
         * 
         * 
         */
        String endDate=param.getString("END_TIME","");
        String startDate=param.getString("START_TIME","");
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  t.AREA_CODE, t.AUDIT_ORDER_ID,t.balance,t.TOTAL_AMOUNT,TO_CHAR(t.OPERA_TIME,'YYYY-MM-DD HH24:MI:SS') OPERA_TIME,t.OPERA_STAFF_ID,TO_CHAR(t.START_TIME,'YYYY-MM-DD HH24:MI:SS') START_TIME,TO_CHAR(t.END_TIME,'YYYY-MM-DD HH24:MI:SS') END_TIME,t.AMOUNTS ,t.ROWID ROW_ID FROM TL_B_COUPONS_QUOTA t where 1=1 ");

        String areCode = param.getString("AREA_CODE","");
        if (!"".equals(areCode) )
        {
            parser.addSQL(" AND t.AREA_CODE=:AREA_CODE");
        }

        if (!"".equals(param.getString("AUDIT_ORDER_ID", "")))
        {
            parser.addSQL(" AND t.AUDIT_ORDER_ID LIKE '%" + param.getString("AUDIT_ORDER_ID", "") + "%'");
        }
        if(!"".equals(endDate)){
        	endDate=endDate+" 23:59:59";
        	param.put("END_TIME", endDate);
        	parser.addSQL(" and t.END_TIME<=to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        }

        if(!"".equals(startDate)){

        	startDate=startDate+" 00:00:00";
        	param.put("START_TIME", startDate);
        	parser.addSQL(" and t.START_TIME>=to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        }
        parser.addSQL(" order by t.OPERA_TIME desc ");
        return Dao.qryByParse(parser, pagination);
    }
}
