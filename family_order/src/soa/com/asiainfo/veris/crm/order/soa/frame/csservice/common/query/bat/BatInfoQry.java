
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BatInfoQry
{
    /**
     * 获取指定日期日、月导入数量
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getNowDayCount(String batch_oper_type, String accept_date) throws Exception
    {

        IData param = new DataMap();

        // BATCH_OPER_TYPE 传批量处理类型编码
        // ACCEPT_DATE 传当天日期，YYYY-MM-DD
        // EPARCHY_CODE 传当前登记地州
        param.put("BATCH_OPER_TYPE", batch_oper_type);
        param.put("ACCEPT_DATE", StringUtils.isEmpty(accept_date) ? SysDateMgr.getSysDate() : accept_date);
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset set = Dao.qryByCode("TF_B_TRADE_BAT", "SEL_BATCHCOUNT_SUM", param, Route.getJourDb(Route.CONN_CRM_CG));

        if (set.size() == 0)
        {
            return new DataMap();
        }
        else
        {
            return set.getData(0);
        }
    }

    /**
     * 查询批次信息
     * 
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryBatByAll(IData inParam, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID"));
        param.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_TYPE"));
        param.put("START_DATE", inParam.getString("START_DATE"));
        param.put("END_DATE", inParam.getString("END_DATE"));
        param.put("STAFF_ID", inParam.getString("STAFF_ID"));
        param.put("ACTIVE_FLAG", inParam.getString("ACTIVE_FLAG"));
        param.put("AUDIT_STATE", inParam.getString("AUDIT_STATE"));

        return Dao.qryByCodeParser("TF_B_TRADE_BAT", "SEL_BY_ALL", param, pg, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据批次号查询批量信息
     * 
     * @param batchId
     * @return
     * @throws Exception
     */
    public static IDataset qryBatByBatchId(String batchId) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_ID", batchId);

        return Dao.qryByCode("TF_B_TRADE_BAT", "SEL_ALL_BY_PK", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据批量任务号查询批次信息
     * 
     * @param batchTaskId
     * @return
     * @throws Exception
     */
    public static IDataset qryBatByBatchTaskId(String batchTaskId) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_TASK_ID", batchTaskId);

        return Dao.qryByCodeParser("TF_B_TRADE_BAT", "SEL_BY_BATCHTASKID", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询需要启动的批量任务
     * 
     * @param inParam
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryBatNeedToStart(IData inParam, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_ID", inParam.getString("BATCH_ID"));
        param.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID"));
        param.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_TYPE"));
        param.put("START_DATE", inParam.getString("START_DATE"));
        param.put("END_DATE", inParam.getString("END_DATE"));
        param.put("STAFF_ID", inParam.getString("STAFF_ID"));
        param.put("ACTIVE_FLAG", inParam.getString("ACTIVE_FLAG"));
        param.put("AUDIT_STATE", inParam.getString("AUDIT_STATE"));
        param.put("DST_ONE_KEY_FLAG", inParam.getString("DST_ONE_KEY_FLAG"));
        param.put("TRADE_ATTR", inParam.getString("TRADE_ATTR"));
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("REMOVE_TAG", inParam.getString("REMOVE_TAG"));

        IDataset ds = Dao.qryByCodeParser("TF_B_TRADE_BAT", "SEL_TO_START_BY_ALL", param, pg, Route.getJourDb(Route.CONN_CRM_CG));
        return ds;
    }

    /**
     * 根据批量类型和年月统计批量导入数据量
     * 
     * @param batchOperType
     * @param yearMonth
     * @return [MONTH_COUNT-统计数量]
     * @throws Exception
     */
    public static IDataset qryCountByOperTypeAndMonth(String batchOperType, String yearMonth) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_OPER_TYPE", batchOperType);
        param.put("YEAR_MONTH", yearMonth);

        return Dao.qryByCode("TF_B_TRADE_BAT", "SEL_COUNT_BY_TYPE_MONTH", param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryTaskDetial(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser
                .addSQL(" select BATCH_TASK_ID,BATCH_ID,TO_CHAR(ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,STAFF_ID,BATCH_COUNT,DECODE(ACTIVE_FLAG,'0','未启动','1','已启动','未知') ACTIVE_FLAG,DECODE(AUDIT_STATE,'0','无需审核','1','等待审核','2','审核通过','3','审核不通过','未知') AUDIT_STATE,AUDIT_INFO,AUDIT_STAFF_ID,TO_CHAR(AUDIT_DATE,'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE,");
        parser.addSQL(" DECODE(REMOVE_TAG,'0','未删除','1','已删除','未知') REMOVE_TAG,TO_CHAR(ACTIVE_TIME,'yyyy-mm-dd hh24:mi:ss') ACTIVE_TIME,AUDIT_REMARK,BATCH_OPER_TYPE");
        parser.addSQL("  from tf_b_trade_bat");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and eparchy_code = :EPARCHY_CODE");
        parser.addSQL(" and BATCH_TASK_ID = :BATCH_TASK_ID");
        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据主键查询bat表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData qryTradeBatByPK(IData data) throws Exception
    {
        return Dao.qryByPK("TF_B_TRADE_BAT", data, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
