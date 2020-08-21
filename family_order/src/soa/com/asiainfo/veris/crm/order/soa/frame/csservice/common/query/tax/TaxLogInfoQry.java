
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tax;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TaxLogInfoQry
{

    /**
     * 根据TRADE_ID查询增值税受理日志信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxLogByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("TF_B_CRM_TAXLOG", "SEL_TAXLOG_BY_TRADEID", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据CUST_ID查询增值税受理日志信息
     * 
     * @param custId
     * @param startDate
     * @param endDate
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryTaxLogForPrint(String custId, String startDate, String endDate, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("CUST_ID", custId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);

        return Dao.qryByCodeParser("TF_B_CRM_TAXLOG", "SEL_TAXLOG_FOR_PRINT", param, pg, Route.getJourDb(Route.CONN_CRM_CG));
         

    }

}
