
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MonitorInfoQry extends CSBizBean
{

    public static IDataset queryHarassPhoneByIntfID(String intfId) throws Exception
    {
        IData param = new DataMap();
        param.put("INTF_ID", intfId);
        return Dao.qryByCode("TL_B_HARASSPHONE", "SEL_HARASSPHONE_BY_INTF_ID", param);
    }

    public static IDataset queryHarassPhoneByNormal(String startSerialNumber, String endSerialNumber, String provCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", startSerialNumber);
        param.put("PARA_CODE2", endSerialNumber);
        param.put("PARA_CODE6", provCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_HARASSPHONE_NORMAL", param, page);
    }

    public static IDataset queryHarassPhoneByReport(String startDate, String endDate, String reasonCode, String processTag, String provCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", startDate);
        param.put("PARA_CODE2", endDate);
        param.put("PARA_CODE4", reasonCode);
        param.put("PARA_CODE5", processTag);
        param.put("PARA_CODE6", provCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_HARASSPHONEREPORT", param, page);
    }

    public static IDataset queryHarassPhoneBySheet(String startDate, String endDate, String provCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("DATE_START4", startDate);
        param.put("DATE_END4", endDate);
        param.put("PARA_CODE6", provCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_HARASSPHONESHEET", param, page);
    }

    public static IDataset queryHarassPhoneBySummary(String startDate, String endDate, String provCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("DATE_START4", startDate);
        param.put("DATE_END4", endDate);
        param.put("PARA_CODE6", provCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_HARASSPHONESUMMARY", param, page);
    }

    /**
     * 按日查询
     * 
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryMonitorInfosByDay(IData cond, Pagination pagination) throws Exception
    {
        IDataset set = new DatasetList();
        String routeId = cond.getString(Route.ROUTE_EPARCHY_CODE);

        set = Dao.qryByCodeParser("TL_BH_MONITORFILE", "SEL_BY_MONITOR_DAY", cond, pagination, routeId);

        return set;
    }

    /**
     * 按时段查询
     * 
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryMonitorInfosByHour(IData cond, Pagination pagination) throws Exception
    {
        IDataset set = new DatasetList();
        String routeId = cond.getString(Route.ROUTE_EPARCHY_CODE);
        set = Dao.qryByCodeParser("TL_BH_MONITORFILE", "SEL_BY_MONITOR_HOUR", cond, pagination, routeId);

        return set;
    }

    /**
     * 普通查询
     * 
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryMonitorInfosByNormal(IData cond, Pagination pagination) throws Exception
    {
        IDataset set = new DatasetList();
        String routeId = cond.getString(Route.ROUTE_EPARCHY_CODE);
        set = Dao.qryByCodeParser("TL_BH_MONITORFILE", "SEL_BY_MONITOR_NORMAL", cond, pagination, routeId);

        return set;
    }

    public static IDataset queryUncheckHarassPhoneByNormal(String startSerialNumber, String endSerialNumber, String provCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", startSerialNumber);
        param.put("PARA_CODE2", endSerialNumber);
        param.put("PARA_CODE6", provCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_UNCHECK_HARASSPHONE_NORMAL", param, page);
    }

    public static IDataset queryUncheckHarassPhoneByReport(String startDate, String endDate, String reasonCode, String processTag, String provCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("PARA_CODE1", startDate);
        param.put("PARA_CODE2", endDate);
        param.put("PARA_CODE4", reasonCode);
        param.put("PARA_CODE5", processTag);
        param.put("PARA_CODE6", provCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_UNCHECK_HARASSPHONE_REPORT", param, page);
    }

    public static IDataset queryUncheckSmsByIntfID(String intfId) throws Exception
    {
        IData param = new DataMap();
        param.put("INTF_ID", intfId);
        return Dao.qryByCode("TL_B_MONITORFILE", "SEL_SMS_BY_INTF_ID", param);
    }

    public static IDataset queryUncheckSmsByNormal(String startSerialNumber, String endSerialNumber, String dataType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("START_SERIAL_NUMBER", startSerialNumber);
        param.put("END_SERIAL_NUMBER", endSerialNumber);
        param.put("DATA_TYPE", dataType);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_UNCHECK_NORMAL", param, page);
    }

    public static IDataset queryUncheckSmsByReport(String startDate, String endDate, String reasonCode, String processTag, String dataType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("REASON_CODE", reasonCode);
        param.put("PROCESS_TAG", processTag);
        param.put("DATA_TYPE", dataType);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_UNCHECK_REPORT", param, page);
    }
}
