
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;

public class UpayReconQry
{
	
	/**
     * @param startDate
     * @param endDate
     * @throws Exception
     * @author songxw
     */
    public static IDataset qryUpayReconTotal(String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.CUTOFF_DAY,");
        parser.addSQL("(SELECT COUNT(RECON_STATE_CODE)");
        parser.addSQL("   FROM TI_B_UPAY_RECON_RESULT B");
        parser.addSQL("  WHERE B.RECON_STATE_CODE = 1");
        parser.addSQL("    AND A.CUTOFF_DAY = B.CUTOFF_DAY) AS R1,");
        parser.addSQL("(SELECT COUNT(RECON_STATE_CODE)");
        parser.addSQL("    FROM TI_B_UPAY_RECON_RESULT C");
        parser.addSQL("  WHERE C.RECON_STATE_CODE = 2");
        parser.addSQL("    AND A.CUTOFF_DAY = C.CUTOFF_DAY) AS R2,");
        parser.addSQL("(SELECT COUNT(RECON_STATE_CODE)");
        parser.addSQL("   FROM TI_B_UPAY_RECON_RESULT D");
        parser.addSQL("  WHERE D.RECON_STATE_CODE = 3");
        parser.addSQL("    AND A.CUTOFF_DAY = D.CUTOFF_DAY) AS R3");
        parser.addSQL(" FROM TI_B_UPAY_RECON_RESULT A");
        parser.addSQL(" WHERE to_date(A.CUTOFF_DAY, 'yyyy/mm/dd') >= to_date(:START_DATE, 'yyyy/mm/dd') and to_date(A.CUTOFF_DAY, 'yyyy/mm/dd') <= to_date(:END_DATE, 'yyyy/mm/dd')");
        parser.addSQL(" GROUP BY A.CUTOFF_DAY");
        parser.addSQL(" ORDER BY A.CUTOFF_DAY DESC");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * @param serialNumber
     * @param reconState
     * @param startDate
     * @param endDate
     * @throws Exception
     * @author songxw
     */
    public static IDataset qryUpayReconList(String serialNumber, String dealTag, String startDate, String endDate,String cutoffDay, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("CUTOFF_DAY", cutoffDay);
        if(!dealTag.equals(""))
        {
        	param.put("DEAL_TAG", dealTag);
        }
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT IBSYSID,TRADE_ID,SERIAL_NUMBER,FEE,CUTOFF_DAY,RECON_STATE_CODE,DEAL_TAG,REMARK,UPDATE_TIME,RSRV_STR1,RSRV_STR2,RSRV_STR3 ");
        parser.addSQL("FROM TI_B_UPAY_RECON_RESULT ");
        parser.addSQL("WHERE 1 = 1 ");
        if(!dealTag.equals(""))
        {
            parser.addSQL("AND DEAL_TAG = :DEAL_TAG ");
        }
        parser.addSQL("AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND CUTOFF_DAY = :CUTOFF_DAY ");
        parser.addSQL("AND UPDATE_TIME BETWEEN to_date(:START_DATE, 'yyyy/mm/dd') and to_date(:END_DATE, 'yyyy/mm/dd')");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * @param dealTag
     * @param rsrvStr1
     * @param rsrvStr2
     * @param tradeId
     * @throws Exception
     * @author songxw
     */
    public static int updateUpayRecon(String dealTag,String rsrvStr1,String rsrvStr2,String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("DEAL_TAG", dealTag);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("RSRV_STR2", rsrvStr2);
        param.put("TRADE_ID", tradeId);
               
        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TI_B_UPAY_RECON_RESULT SET DEAL_TAG = :DEAL_TAG ,");
        parser.addSQL("RSRV_STR1 = :RSRV_STR1 ,");
        parser.addSQL("RSRV_STR2 = :RSRV_STR2 ");
        parser.addSQL("WHERE TRADE_ID = :TRADE_ID ");

        return Dao.executeUpdates(parser);
    }
}
