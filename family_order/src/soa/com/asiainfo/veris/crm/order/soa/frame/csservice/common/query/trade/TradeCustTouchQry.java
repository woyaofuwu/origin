
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeCustTouchQry extends CSBizBean
{
    public static IDataset getCustTouchTraceByTouchIdTime(String touchId, String touchTime) throws Exception
    {
        IData params = new DataMap();
        params.put("TOUCH_ID", touchId);
        params.put("TOUCH_TIME", touchTime);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" select * from TL_B_CUST_TOUCH_TRACE  where touch_id = :TOUCH_ID and touch_time = to_date(:TOUCH_TIME,'yyyy-mm-dd hh24:mi:ss')  ");
        // return Dao.qryByCodeParser("TL_B_CUST_TOUCH_TRACE", "SEL_ALL_BY_TOUCH_ID_TIME", params);
        return Dao.qryByParse(parser);
    }

    public static void insertCustTouchLog(IData param) throws Exception
    {
        // SQLParser parser = new SQLParser(param);
        // parser.addSQL(" INSERT INTO TL_B_CUSTCONTACT_TRADELOG   ");
        // parser.addSQL(" (FLOW_ID ,ACCEPT_MONTH , CUST_CONTACT_ID , MODIFY_TAG ,MODIFY_DESC,UPDATE_STAFF_ID , UPDATE_DEPART_ID,UPDATE_CITY_CODE, UPDATE_EPARCHY_CODE  ,UPDATE_TIME ,  ");
        // parser.addSQL("    REMARK) ");
        // parser.addSQL(" values  ");
        // parser.addSQL(" (:FLOW_ID ,:ACCEPT_MONTH , :CUST_CONTACT_ID ,:MODIFY_TAG ,:MODIFY_DESC,:UPDATE_STAFF_ID ,:UPDATE_DEPART_ID,:UPDATE_CITY_CODE,:UPDATE_EPARCHY_CODE  ,:UPDATE_TIME          ,  ");
        // parser.addSQL("  :REMARK)  ");
        // // return Dao.qryByCodeParser("TL_B_CUST_TOUCH_TRACE", "SEL_ALL_BY_TOUCH_ID_TIME", params);
        // Dao.executeUpdate(parser,Route.CONN_LOG);
        Dao.insert("TL_B_CUSTCONTACT_TRADELOG", param, Route.CONN_LOG);
    }

    /**
     * 修改客户接触信息的备注信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int modifyNewCustTouch(String touchId, String touchTime, String remark) throws Exception
    {
        IData params = new DataMap();
        params.put("TOUCH_ID", touchId);
        params.put("TOUCH_TIME", touchTime);
        params.put("REMARK", remark);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" update TL_B_CUST_TOUCH_TRACE set remark = :REMARK " + "where touch_id = :TOUCH_ID and touch_time = to_date(:TOUCH_TIME,'yyyy-mm-dd hh24:mi:ss')  ");
        return Dao.executeUpdate(parser, Route.CONN_LOG);
    }

}
