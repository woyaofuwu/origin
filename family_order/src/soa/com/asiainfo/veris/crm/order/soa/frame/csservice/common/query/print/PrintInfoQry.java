
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.print;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PrintInfoQry
{
    /**
     * 获得原始打印内容
     * 
     * @param tradeTypeCode
     * @param productId
     * @param brandCode
     * @param tradeAttr
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getReceiptInfos(String tradeTypeCode, String productId, String brandCode, String tradeAttr, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("TRADE_ATTR", tradeAttr);
        param.put("EPARCHY_CODE", eparchyCode);
        IDataset originalPrintContentSet = Dao.qryByCode("TD_B_TRADE_RECEIPT", "SEL_RECEIPTPARA_BY_PK", param, Route.CONN_CRM_CEN);
        return originalPrintContentSet;
    }

    /**
     * 获取员工票据
     * 
     * @param staffId
     * @param ticketTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset queryStaffTicket(String staffId, String ticketTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("TICKET_TYPE_CODE", ticketTypeCode);
        IDataset originalPrintContentSet = Dao.qryByCode("TD_B_STAFF_TICKET", "SEL_BY_STAFFID", param);
        return originalPrintContentSet;
    }
}
