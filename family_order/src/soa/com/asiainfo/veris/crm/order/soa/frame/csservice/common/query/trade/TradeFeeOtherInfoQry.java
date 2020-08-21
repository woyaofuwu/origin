
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeFeeOtherInfoQry
{

    // todo
    public static IDataset queryPrintInfos(String tradeId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT TRADE_ID FROM TF_B_TRADEFEE_OTHER WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) AND PRINT_TAG='1'");
        return Dao.qryByParse(parser, pagination);
    }

    // todo
    public static IDataset queryPrintInfos(String custName, String feeitemCode, String printTag, String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("FEEITEM_CODE", feeitemCode);
        param.put("PRINT_TAG", printTag);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT to_char(t.trade_id) TRADE_ID,t.cust_name CUST_NAME,t.feeitem_code FEEITEM_CODE,t.fee FEE,To_CHAR(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, T.PRINT_TAG PRINT_TAG,");
        parser
                .addSQL(" T.UPDATE_STAFF_ID UPDATE_STAFF_ID,T.UPDATE_DEPART_ID UPDATE_DEPART_ID,T.TRADE_EPARCHY_CODE TRADE_EPARCHY_CODE,decode(T.CANCEL_TAG,1,'被返销',2,'返销','未返销') CANCEL_TAG,DECODE(T.CANCEL_TAG,0,0,1,T.FEE) CANCEL_FEE,T.CANCEL_STAFF_ID CANCEL_STAFF_ID, ");
        parser.addSQL(" T.CANCEL_DEPART_ID CANCEL_DEPART_ID,To_CHAR(T.CANCEL_TIME, 'yyyy-mm-dd hh24:mi:ss') CANCEL_TIME,To_CHAR(L.TRADE_TIME, 'yyyy-mm-dd hh24:mi:ss') TRADE_TIME, ");
        parser.addSQL(" L.TRADE_STAFF_ID PRINT_STAFF_ID,L.TRADE_DEPART_ID PRINT_DEPART_ID,l.REPRINT_FLAG REPRINT_FLAG,l.cancel_tag print_cancel_tag, To_CHAR(L.CANCEL_TIME, 'yyyy-mm-dd hh24:mi:ss') PRINT_CANCEL_TIME, ");
        parser.addSQL(" L.CANCEL_STAFF_ID PRINT_CANCEL_STAFF_ID,L.CANCEL_DEPART_ID PRINT_CANCEL_DEPART_ID ");
        parser.addSQL(" FROM TF_B_TRADEFEE_OTHER t,Tf_b_Noteprintlog l ");
        parser.addSQL(" WHERE t.trade_id=l.trade_id(+) ");
        parser.addSQL(" AND 1=1 ");
        parser.addSQL(" AND t.CUST_NAME LIKE '%'||:CUST_NAME||'%' ");
        parser.addSQL(" AND t.FEEITEM_CODE=:FEEITEM_CODE ");
        parser.addSQL(" AND t.PRINT_TAG=:PRINT_TAG ");
        parser.addSQL(" AND t.UPDATE_TIME > TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS') and t.update_time < TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') ");

        parser.addSQL("  AND (l.print_id=(SELECT MAX(n.print_id) FROM Tf_b_Noteprintlog n WHERE n.trade_id=t.trade_id) OR l.trade_id IS NULL) ");
        return Dao.qryByParse(parser, pagination, Route.getJourDb());
    }

}
