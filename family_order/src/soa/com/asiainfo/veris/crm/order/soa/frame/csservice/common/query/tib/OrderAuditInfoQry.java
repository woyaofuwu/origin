
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class OrderAuditInfoQry extends CSBizBean
{
    public static IDataset queryOlcomState(String olcomWorkId) throws Exception
    {
        IData data = new DataMap();
        data.put("OLCOM_WORK_ID", olcomWorkId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT OLCOM_STATE FROM TI_C_OLCOMWORK A WHERE 1=1 ");
        parser.addSQL(" AND A.OLCOM_WORK_ID=:OLCOM_WORK_ID ");

        return Dao.qryByParse(parser);
    }

    public static IDataset queryOrderInfo(String serialNum, String staffId, String startDate, String endDate, String auditState, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNum);
        param.put("AUDIT_STATE", auditState);// 0-未稽核;1-已稽核
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("TRADE_STAFF_ID", staffId);

        return Dao.qryByCodeParser("TI_B_ORDER_AUDIT", "SEL_TI_B_ORDER_AUDIT", param, page);
    }

    public static int updateOrderData(String tradeId, String serialNumber, String olcomWorkId, String eparchCode, String staffId, String departId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("OLCOM_WORK_ID", olcomWorkId);
        param.put("EPARCHY_CODE", eparchCode);
        param.put("AUDIT_STAFF_ID", staffId);
        param.put("AUDIT_DEPART_ID", departId);
        return Dao.executeUpdateByCodeCode("TI_B_ORDER_AUDIT", "UPDATE_TI_B_ORDER_AUDIT", param);
    }

}
