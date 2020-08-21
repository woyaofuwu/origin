
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserOderInfoQry
{

    /**
     * 查询导出订单的信息
     * 
     * @param processType
     * @param busiTypeCode
     * @param serialNumber
     * @param orderType
     * @param startTime
     * @param endDate
     * @param orderId
     * @param dbType
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryExportData(String processType, String busiTypeCode, String serialNumber, String orderType, String startTime, String endDate, String orderId, String dbType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SEND_ORDER_TYPE", orderType);
        param.put("BUSI_TYPE", busiTypeCode);
        param.put("PROCESS_TYPE", processType);
        param.put("ORDER_ID", orderId);
        param.put("ACCEPT_DATE", startTime);
        param.put("END_DATE", endDate);

        // IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = dbType;
        // if (set != null && set.size() > 0)
        // {
        // routeId = set.getData(0).getString("DATA_ID");
        // }

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select ORDER_ID, BUSI_TYPE,SERIAL_NUMBER,USER_ID,EPARCHY_CODE,TRADE_CITY_CODE,CITY_CODE, ");
        parser.addSQL(" to_char(ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,TRADE_TYPE_CODE,OPER_FEE/100 OPER_FEE,CUST_NAME,CANCEL_TAG,TRADE_ID, ");
        parser.addSQL(" PROCESS_FLAG, ORDER_TYPE,PURCHASE_NAME,GIFT_SERIAL_NUMBER,POST_ADDRESS,PSPT_ID,CONTACT_MOBILE,PRODUCT_NAME, ");
        parser.addSQL(" to_char(PROCESS_DATE, 'yyyy-mm-dd hh24:mi:ss') PROCESS_DATE,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6, ");
        parser.addSQL(" PROCESS_TYPE,to_char(DELAY_DATE, 'yyyy-mm-dd hh24:mi:ss') DELAY_DATE,TRANS_TRADE_ID from tf_f_order_intf a ");
        parser.addSQL(" where 1=1 and ACCEPT_DATE>= to_date(:ACCEPT_DATE, 'yyyy-MM-dd') and ACCEPT_DATE<= to_date(:END_DATE, 'yyyy-MM-dd')+1 ");
        parser.addSQL(" and (:SERIAL_NUMBER IS NULL OR SERIAL_NUMBER = :SERIAL_NUMBER) AND ORDER_TYPE = :SEND_ORDER_TYPE AND BUSI_TYPE = :BUSI_TYPE ");
        parser.addSQL(" AND PROCESS_TYPE = :PROCESS_TYPE AND (:ORDER_ID IS NULL OR ORDER_ID = :ORDER_ID)order by ACCEPT_DATE desc ");
        return Dao.qryByParse(parser, page, routeId);
    }

    /**
     * 查询订单信息
     * 
     * @param serialNumber
     * @param orderType
     * @param busiType
     * @param processType
     * @param orderId
     * @param acceptDate
     * @param endDate
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryOrderInfo(String serialNumber, String orderType, String busiType, String processType, String orderId, String acceptDate, String endDate, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SEND_ORDER_TYPE", orderType);
        param.put("BUSI_TYPE", busiType);
        param.put("PROCESS_TYPE", processType);
        param.put("ORDER_ID", orderId);
        param.put("ACCEPT_DATE", acceptDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_ORDER_INTF", "SEL_BY_ALLORDER", param, page);
    }

    /**
     * 更新订单信息
     * 
     * @param orderId
     * @param staffId
     * @param departId
     * @param processTypeCode
     * @return
     * @throws Exception
     */
    public static int updateOrderData(String orderId, String staffId, String departId, String processTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("RSRV_STR1", staffId);
        param.put("RSRV_STR2", departId);
        param.put("PROCESS_OPER_CODE", processTypeCode);

        String In_date = SysDateMgr.getSysTime();
        if (processTypeCode.equals("1"))
            param.put("DELAY_DATE", In_date);
        else if (processTypeCode.equals("2"))
            param.put("PROCESS_DATE", In_date);
        else if ("3".equals(processTypeCode) || "4".equals(processTypeCode))
            param.put("CANCEL_DATE", In_date);

        if ("3".equals(processTypeCode) || "4".equals(processTypeCode))
            param.put("CANCEL_TAG", "1");
        else
            param.put("CANCEL_TAG", "0");

        return Dao.executeUpdateByCodeCode("TF_F_ORDER_INTF", "UPDATE_BY_ORDERID", param);
    }

}
