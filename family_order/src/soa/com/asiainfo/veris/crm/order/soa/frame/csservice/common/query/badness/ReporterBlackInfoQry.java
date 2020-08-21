
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ReporterBlackInfoQry
{

    public static IDataset qryAllBlackBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_REPORTERBALCK", "SEL_ALL_BY_SN", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryReportBlackBySn(String serialNumber, String isBlack, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("IS_BLACK", isBlack);
        param.put("REPORT_START_TIME", startDate);
        param.put("REPORT_END_TIME", endDate);

        return Dao.qryByCode("TF_F_REPORTERBALCK", "SEL_BLACK_BY_SN", param, Route.CONN_CRM_CEN);
    }

}
