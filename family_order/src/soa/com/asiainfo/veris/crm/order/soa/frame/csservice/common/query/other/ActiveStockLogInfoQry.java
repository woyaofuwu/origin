
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ActiveStockLogInfoQry
{

    public static IDataset queryLog(String cityCode, String departId, String staffId, String productName, String packageName, String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("CITY_CODE", cityCode);
        data.put("DEPART_ID", departId);
        data.put("STAFF_ID", staffId);
        data.put("PRODUCT_NAME", productName);
        data.put("PACKAGE_NAME", packageName);
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT LOG_ID,EPARCHY_CODE,STAFF_ID_IN,STAFF_ID_OUT,CITY_CODE,RES_KIND_CODE,");
        parser.addSQL(" DEPART_ID,ASSIGN_COUNT,PRODUCT_NAME,PACKAGE_NAME,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" UPDATE_STAFF_ID,UPDATE_DEPART_ID,RSRV_TAG1 ");
        parser.addSQL(" from TF_F_ACTIVE_STOCK_LOG where 1=1 ");
        parser.addSQL(" and CITY_CODE = :CITY_CODE");
        parser.addSQL(" and DEPART_ID = :DEPART_ID");
        parser.addSQL(" and UPDATE_STAFF_ID = :STAFF_ID");
        parser.addSQL(" and PRODUCT_NAME LIKE  '%' || :PRODUCT_NAME || '%'");
        parser.addSQL(" and PACKAGE_NAME LIKE  '%' || :PACKAGE_NAME || '%'");
        parser.addSQL(" and UPDATE_TIME >= to_date( :START_DATE ,'yyyy-mm-dd Hh24:mi:ss') ");
        parser.addSQL(" and UPDATE_TIME <= (to_date( :END_DATE ,'yyyy-mm-dd Hh24:mi:ss') +1) ");
        parser.addSQL(" order by UPDATE_TIME desc ");

        return Dao.qryByParse(parser, pagination);
    }
}
