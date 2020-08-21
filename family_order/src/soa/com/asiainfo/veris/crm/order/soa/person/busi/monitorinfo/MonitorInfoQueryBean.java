
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BlackUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MonitorInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.WhiteUserInfoQry;

public class MonitorInfoQueryBean extends CSBizBean
{

    public IDataset checkMonitorInfo(IData data) throws Exception
    {
        String info = data.getString("MONITORE_INFO", "[]");
        IDataset dataset = new DatasetList(info);
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("AUDIT_STAFF_ID", getVisit().getStaffId());
            dataset.getData(i).put("AUDIT_DEPART_ID", getVisit().getDepartId());
        }
        Dao.executeBatchByCodeCode("TL_B_HARASSPHONE", "UPD_HARASSPHONE_BY_INTF_ID", dataset);

        // IData param = new DataMap();
        // String[] paramNames = new String[]{"v_resultcode","v_resulterrinfo"};
        // Dao.callProc("p_csm_auto_phonestop", paramNames, param);
        insBatTask(dataset, "PHONEMONITORCHECK", "骚扰电话停机人工审核");

        return IDataUtil.idToIds(data);
    }

    public IDataset checkMonitorSmsInfo(IData data) throws Exception
    {
        String info = data.getString("MONITORE_INFO", "[]");
        IDataset dataset = new DatasetList(info);
        for (int i = 0; i < dataset.size(); i++)
        {
            dataset.getData(i).put("STAFF_ID", getVisit().getStaffId());
        }
        Dao.executeBatchByCodeCode("TL_B_MONITORFILE", "UPD_MONOTOR_BY_INTF_ID", dataset);

        IData param = new DataMap();
        // String[] paramNames = new String[]{"v_datatype","V_RESULTCODE","V_RESULTERRINFO"};
        // Dao.callProc("p_csm_auto_smsstop", paramNames, param);
        //        
        // if(!"0".equals(param.getString("V_RESULTCODE"))) {
        // CSAppException.apperr(CrmCommException.CRM_COMM_103, param.getString("V_RESULTERRINFO"));
        // }
        insBatTask(dataset, "SMSMONITORCHECK", "垃圾短信人工审核");
        return IDataUtil.idToIds(param);
    }

    public IDataset handleSuspectSms(IData data) throws Exception
    {
        String info = data.getString("MONITORE_INFO", "[]");
        IDataset dataset = new DatasetList(info);
        for (int i = 0; i < dataset.size(); i++)
        {
            dataset.getData(i).put("STAFF_ID", getVisit().getStaffId());
            dataset.getData(i).put("DEPART_ID", getVisit().getDepartId());
        }
        Dao.executeBatchByCodeCode("TL_B_BLACKSMS", "UPD_BLACKSMS_BY_INTF_ID", dataset);

        IData param = new DataMap();
        // String[] paramNames = new String[]{"v_datatype", "V_RESULTCODE", "V_RESULTERRINFO"};
        // Dao.callProc("p_csm_auto_dealblacksms", paramNames, param);
        //        
        // if(!"0".equals(param.getString("V_RESULTCODE"))) {
        // CSAppException.apperr(CrmCommException.CRM_COMM_103, param.getString("V_RESULTERRINFO"));
        // }
        return IDataUtil.idToIds(param);
    }

    private void insBatDeal(String batTaskId, String batchId, IDataset dataset, String batchCode) throws Exception
    {
        IDataset result = new DatasetList();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData data = dataset.getData(i);
            IData tfBTradeBatdeal = new DataMap();
            String time = SysDateMgr.getSysTime();
            tfBTradeBatdeal.put("BATCH_TASK_ID", batTaskId);
            tfBTradeBatdeal.put("BATCH_ID", batchId);
            tfBTradeBatdeal.put("ACCEPT_MONTH", SysDateMgr.getTheMonth(SysDateMgr.getSysDate()));
            tfBTradeBatdeal.put("OPERATE_ID", SeqMgr.getBatchId());
            tfBTradeBatdeal.put("BATCH_OPER_TYPE", batchCode);
            tfBTradeBatdeal.put("PRIORITY", "1");
            tfBTradeBatdeal.put("REFER_TIME", time);
            tfBTradeBatdeal.put("EXEC_TIME", time);
            tfBTradeBatdeal.put("SERIAL_NUMBER", data.getString("INTF_ID"));
            tfBTradeBatdeal.put(Route.ROUTE_EPARCHY_CODE, "0");
            tfBTradeBatdeal.put("CANCEL_TAG", "0");
            tfBTradeBatdeal.put("DEAL_STATE", "1");
            tfBTradeBatdeal.put("DEAL_TIME", time);
            result.add(tfBTradeBatdeal);
        }
        Dao.insert("TF_B_TRADE_BATDEAL", result, Route.getJourDb(Route.CONN_CRM_CG));
    }

    private void insBatTask(IDataset dataset, String batchCode, String batchName) throws Exception
    {
        IData data = new DataMap();
        String batTaskId = SeqMgr.getBatchId();
        data.put("BATCH_TASK_ID", batTaskId);
        // data.put("CREATE_TIME", SysDateMgr.getSysDate());
        data.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        data.put("CREATE_TIME", SysDateMgr.getSysTime());
        data.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("CREATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("CREATE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("BATCH_OPER_CODE", batchCode);// PHONEMONITORCHECK
        data.put("BATCH_OPER_NAME", batchName);// 骚扰电话停机人工审核
        data.put("CREATE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        data.put("BATCH_TASK_NAME", batchName);
        data.put("START_DATE", SysDateMgr.getSysTime());
        data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        data.put("REMARK", batchName);
        data.put("AUDIT_NO", batchName);
        data.put("SMS_FLAG", "0");// 默认不发短信
        // data.put("CONDITION1", input.getString("PRODUCT_ID", ""));
        // data.put("CONDITION2", conding2);
        Dao.insert("TF_B_TRADE_BAT_TASK", data, Route.getJourDb(Route.CONN_CRM_CG));

        String batchId = insTradeBat(batTaskId, dataset.size(), batchCode);
        insBatDeal(batTaskId, batchId, dataset, batchCode);
    }

    private String insTradeBat(String batTaskId, int count, String batchCode) throws Exception
    {
        IData tfBTradeBat = new DataMap();
        String batchId = SeqMgr.getBatchId();
        tfBTradeBat.put("BATCH_TASK_ID", batTaskId.trim());
        tfBTradeBat.put("BATCH_ID", batchId);
        tfBTradeBat.put("BATCH_OPER_TYPE", batchCode);
        tfBTradeBat.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        tfBTradeBat.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        tfBTradeBat.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        tfBTradeBat.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        tfBTradeBat.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        tfBTradeBat.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        tfBTradeBat.put("IN_MODE_CODE", "0");
        tfBTradeBat.put("BATCH_COUNT", count);
        tfBTradeBat.put("REMOVE_TAG", "0");
        tfBTradeBat.put("ACTIVE_FLAG", "0");
        tfBTradeBat.put("ACTIVE_TIME", "");
        tfBTradeBat.put("AUDIT_STATE", "0");
        tfBTradeBat.put("AUDIT_REMARK", "");
        tfBTradeBat.put("AUDIT_DATE", SysDateMgr.getSysTime());
        tfBTradeBat.put("AUDIT_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tfBTradeBat.put("AUDIT_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tfBTradeBat.put("AUDIT_INFO", "");
        tfBTradeBat.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batTaskId.trim()));
        Dao.insert("TF_B_TRADE_BAT", tfBTradeBat, Route.getJourDb(Route.CONN_CRM_CG));
        return batchId;
    }

    public IDataset queryBlackInfos(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String provCode = data.getString("PROV_CODE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = data.getString("END_SERIAL_NUMBER");
            result = BlackUserInfoQry.queryBlackInfosByNormal(startSerialNumber, endSerialNumber, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = BlackUserInfoQry.queryBlackInfosByReport(startDate, endDate, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            String startDate = data.getString("DATE_START") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_END") + SysDateMgr.END_DATE;
            result = BlackUserInfoQry.queryBlackInfosByReport(startDate, endDate, provCode, page);

        }
        return result;
    }

    public IDataset queryBlackUsers(IData data, Pagination page) throws Exception
    {
        IDataset result = null;
        String radio = data.getString("radio", "");
        String serialNumber = data.getString("SERIAL_NUMBER");
        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");

        if ("1".equals(radio))
        {
            result = BlackUserInfoQry.qryBlackUserByValid(serialNumber, startDate, endDate, page);
        }
        else
        {
            result = BlackUserInfoQry.qryBlackUserByAll(serialNumber, startDate, endDate, page);
        }
        return result;
    }

    public IDataset queryMonitorInfo(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String provCode = data.getString("PROV_CODE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = "86" + data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = "86" + data.getString("END_SERIAL_NUMBER");
            result = MonitorInfoQry.queryHarassPhoneByNormal(startSerialNumber, endSerialNumber, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = MonitorInfoQry.queryHarassPhoneByReport(startDate, endDate, data.getString("REASON_CODE_DAY"), data.getString("PROCESS_TAG_DAY"), provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            result = MonitorInfoQry.queryHarassPhoneByReport(data.getString("DATE_START"), data.getString("DATE_END"), data.getString("REASON_CODE_TIME"), data.getString("PROCESS_TAG1"), provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_SUMMARY_BETWEEN))
        {// 汇总报表查询
            result = MonitorInfoQry.queryHarassPhoneBySummary(data.getString("DATE_START_SUMMARY"), data.getString("DATE_END_SUMMARY"), provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_SHEET_BETWEEN))
        {// 清单报表查询
            result = MonitorInfoQry.queryHarassPhoneBySheet(data.getString("DATE_START_SUMMARY"), data.getString("DATE_END_SUMMARY"), provCode, page);

        }
        return result;
    }

    public IDataset querySuspectInfos(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String provCode = data.getString("PROV_CODE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = data.getString("END_SERIAL_NUMBER");
            result = WhiteUserInfoQry.querySuspectInfosByNormal(startSerialNumber, endSerialNumber, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = WhiteUserInfoQry.querySuspectInfosByReport(startDate, endDate, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            String startDate = data.getString("DATE_START") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_END") + SysDateMgr.END_DATE;
            result = WhiteUserInfoQry.querySuspectInfosByReport(startDate, endDate, provCode, page);

        }
        return result;
    }

    public IDataset queryUncheckInfos(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String provCode = data.getString("PROV_CODE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = "86" + data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = "86" + data.getString("END_SERIAL_NUMBER");
            result = MonitorInfoQry.queryUncheckHarassPhoneByNormal(startSerialNumber, endSerialNumber, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = MonitorInfoQry.queryUncheckHarassPhoneByReport(startDate, endDate, data.getString("REASON_CODE_DAY"), data.getString("PROCESS_TAG_DAY", ""), provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            result = MonitorInfoQry.queryUncheckHarassPhoneByReport(data.getString("DATE_START"), data.getString("DATE_END"), data.getString("REASON_CODE_TIME"), data.getString("PROCESS_TAG_TIME"), provCode, page);

        }
        return result;
    }

    public IDataset queryUncheckSmsInfo(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String dataType = data.getString("DATA_TYPE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = "86" + data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = "86" + data.getString("END_SERIAL_NUMBER");
            result = MonitorInfoQry.queryUncheckSmsByNormal(startSerialNumber, endSerialNumber, dataType, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = MonitorInfoQry.queryUncheckSmsByReport(startDate, endDate, data.getString("REASON_CODE_DAY"), data.getString("PROCESS_TAG_DAY"), dataType, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            result = MonitorInfoQry.queryUncheckSmsByReport(data.getString("DATE_START"), data.getString("DATE_END"), data.getString("REASON_CODE_TIME"), data.getString("PROCESS_TAG_TIME"), dataType, page);

        }
        return result;
    }

    public IDataset queryVerifySuspectSms(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String provCode = data.getString("PROV_CODE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = data.getString("END_SERIAL_NUMBER");
            result = WhiteUserInfoQry.queryVerifySuspectSmsNormal(startSerialNumber, endSerialNumber, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = WhiteUserInfoQry.queryVerifySuspectSmsReport(startDate, endDate, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            String startDate = data.getString("DATE_START") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_END") + SysDateMgr.END_DATE;
            result = WhiteUserInfoQry.queryVerifySuspectSmsReport(startDate, endDate, provCode, page);

        }
        return result;
    }

    public IDataset queryWhiteInfos(IData data, Pagination page) throws Exception
    {
        String queryType = IDataUtil.chkParam(data, "QUERY_TYPE");
        String provCode = data.getString("PROV_CODE");
        IDataset result = null;
        if (queryType.equals(PersonConst.QUERY_TYPE_NORMAL))
        {// 普通查询
            String startSerialNumber = data.getString("START_SERIAL_NUMBER");
            String endSerialNumber = data.getString("END_SERIAL_NUMBER");
            result = WhiteUserInfoQry.queryWhiteInfosByNormal(startSerialNumber, endSerialNumber, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT))
        {// 日报表查询
            String startDate = data.getString("DATE_REPORT") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_REPORT") + SysDateMgr.END_DATE;
            result = WhiteUserInfoQry.queryWhiteInfosByReport(startDate, endDate, provCode, page);

        }
        else if (queryType.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {// 时段表查询
            String startDate = data.getString("DATE_START") + SysDateMgr.START_DATE_FOREVER;
            String endDate = data.getString("DATE_END") + SysDateMgr.END_DATE;
            result = WhiteUserInfoQry.queryWhiteInfosByReport(startDate, endDate, provCode, page);

        }
        return result;
    }
}
