
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class BatTradeInfoQry
{

    /**
     * 查询所有批量明细 （包括移入历史表的数据） 解决分页不准确的问题
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialAllQuery(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,");
        parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,");
        parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,");
        parser.addSQL(" DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20,");
        parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
        parser.addSQL(" FROM tf_b_trade_batdeal a WHERE 1= 1 ");
        parser.addSQL(" AND a.batch_id = :BATCH_ID");
        parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND a.deal_state = :DEAL_STATE");
        parser.addSQL(" AND a.serial_number = :SERIAL_NUMBER");

        IDataset resultset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));// 添加处理状态描述
        }
        return resultset;
    }
    
    /**
     * 查询所有批量明细 （包括移入历史表的数据） 解决分页不准确的问题
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialQueryxxt(IData data, Pagination pagination, String conn) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,");
        parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,");
        parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,");
        parser.addSQL(" DATA1 as SERIAL_NUMBER_B,DATA2 as STUDENT1,DATA3 as STUDENT2,DATA4 as STUDENT3,DATA5 as DISCNT1,DATA6 as DISCNT2,DATA7 as DISCNT3,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20,");
        parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
        parser.addSQL(" FROM tf_b_trade_batdeal a WHERE 1= 1 ");
        parser.addSQL(" AND a.batch_id = :BATCH_ID");
        parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND a.deal_state = :DEAL_STATE");
        parser.addSQL(" AND a.serial_number = :SERIAL_NUMBER");
        parser.addSQL(" order by a.DEAL_STATE,a.DEAL_DESC");

        IDataset resultset = Dao.qryByParse(parser, pagination, conn);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));// 添加处理状态描述
        }
        return resultset;
    }

    /**
     * 根据batch_id、cancel_tag、deal_state、serial_number等查询VPMN批量明细 解决分页不准确的问题
     **/
    public static IDataset batchDetialQueryAllVPMN(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,TO_CHAR(OPERATE_ID) OPERATE_ID,DEAL_STATE,");
        parser.addSQL(" BATCH_OPER_TYPE,TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,");
        parser.addSQL(" CANCEL_STAFF_ID,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,p_ss_getbaterr(DEAL_RESULT) DEAL_RESULT,");
        parser.addSQL(" DATA1 SHORT_CODE, ");
        parser.addSQL(" (CASE DATA2 WHEN '1' THEN '短号' WHEN '2' THEN '真实号码' ELSE '错误的输入代码' END) AS CALL_DISP_MODE,DEAL_DESC");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        IDataset resultset = Dao.qryByParse(parser, pagination, conn);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));
        }
        return resultset;
    }

    /**
     * 依据批量明细中的Data12字段值查询明细表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialQueryByData12(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,TO_CHAR(OPERATE_ID) OPERATE_ID,TO_CHAR(DATA3) DATA3");
        parser.addSQL(" FROM tf_b_trade_batdeal");
        parser.addSQL(" WHERE data12 = :DATA12");
        parser.addSQL(" AND batch_oper_type = :BATCH_OPER_TYPE");
        parser.addSQL(" AND priority = '10'");
        parser.addSQL(" AND deal_state = '0'");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * BBOSS下发配合省成员[行业应用卡] 查询批量明细信息
     * 
     * @param data
     * @param pagination
     * @param conn
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialQueryHYYYK(IData data, Pagination pagination, String conn) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT SERIAL_NUMBER,DATA1 ORG_DOMAIN,DATA11 IBSYSID_SUB,DATA12 IBSYSID,DATA4 PROVCODE,DATA5 ORDER_NO,DEAL_STATE,");
        parser.addSQL(" DATA6 RSRV_STR4,DATA2 NEWUSERCOUNT,DATA3 OPERCODE,DATA7 INFO_TYPE,DATA13 FEEPLAN,DATA14 PRODINFO,DATA18 EOF,DATA19 BUSI_SIGN, DATA20 OPER_TYPE");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, conn);
    }

    /**
     * BBOSS下发配合省反馈成员开通结果[行业应用卡] 查询明细
     * 
     * @param data
     * @param pagination
     * @param conn
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialQueryHYYYKOpen(IData data, Pagination pagination, String conn) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT SERIAL_NUMBER,DATA1 ORG_DOMAIN,DATA11 IBSYSID_SUB,DATA12 IBSYSID,DATA4 PROVCODE,DATA5 ORDER_NO,DEAL_STATE,");
        parser.addSQL(" DATA6 RSRV_STR4,DATA2 BUSISTATUS,DATA3 ISNEWUSER,DATA13 ERRDESC,DATA14 NEWUSERFAILDESC,DATA18 EOF,DATA19 BUSI_SIGN, DATA20 OPER_TYPE");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, conn);
    }

    /**
     * 根据batch_id、cancel_tag、deal_state、serial_number等查询VPMN批量明细
     **/
    public static IDataset batchDetialQueryVPMN(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,TO_CHAR(OPERATE_ID) OPERATE_ID,DEAL_STATE,");
        parser.addSQL(" BATCH_OPER_TYPE,TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,");
        parser.addSQL(" CANCEL_STAFF_ID,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,p_ss_getbaterr(DEAL_RESULT) DEAL_RESULT,");
        parser.addSQL(" DATA1 SHORT_CODE, ");
        parser.addSQL(" (CASE DATA2 WHEN '1' THEN '短号' WHEN '2' THEN '真实号码' ELSE '错误的输入代码' END) AS CALL_DISP_MODE,DEAL_DESC");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        IDataset resultset = Dao.qryByParse(parser, pagination, conn);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));
        }
        return resultset;
    }

    /**
     * 根据返销状态等查询明细 BBOSS下发配合省成员[一点支付]批量信息
     */
    public static IDataset batchDetialQueryYDZF(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT SERIAL_NUMBER,DATA1 ORG_DOMAIN,DATA11 IBSYSID_SUB,DATA12 IBSYSID,DATA4 PROVCODE,DATA5 ORDER_NO,");
        parser.addSQL(" DATA6 RSRV_STR4,DATA13 FEEPLAN,DATA8 PAYTYPE,DATA9 PAYAMOUNT,DATA10 EFFRULE,DEAL_STATE,");
        parser.addSQL(" DATA2 NEWUSERCOUNT,DATA3 OPERCODE,DATA14 ACCOUNTNAMEREQ,DATA7 INFO_TYPE,DATA18 EOF,DATA19 BUSI_SIGN, DATA20 OPER_TYPE");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, conn);
    }

    /**
     * 根据返销状态等查询明细 BBOSS下发配合省反馈成员确认结果[一点支付]
     */
    public static IDataset batchDetialQueryYDZFConfirm(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT SERIAL_NUMBER,DATA1 ORG_DOMAIN,DATA11 IBSYSID_SUB,DATA12 IBSYSID,DATA4 PROVCODE,DATA5 ORDER_NO,DEAL_STATE,");
        parser.addSQL(" DATA6 RSRV_STR4,DATA7 NAMEMATCH,DATA13 CURRFEEPLAN,DATA9 USERSTATUS,DATA18 EOF,DATA19 BUSI_SIGN, DATA20 OPER_TYPE");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, conn);
    }

    /**
     * 根据返销状态等查询明细 配合省反馈成员开通结果[一点支付]
     */
    public static IDataset batchDetialQueryYDZFOpen(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT SERIAL_NUMBER,DATA1 ORG_DOMAIN,DATA11 IBSYSID_SUB,DATA12 IBSYSID,DATA4 PROVCODE,DATA5 ORDER_NO,");
        parser.addSQL(" DATA6 RSRV_STR4,DATA13 FEEPLAN,DATA8 PAYTYPE,DATA9 PAYAMOUNT,DATA10 EFFRULE,DEAL_STATE,");
        parser.addSQL(" DATA2 CENTROLPAYSTATUS,DATA14 ACCOUNTNAME,DATA15 FAILDESC,DATA3 ISNEWUSER,DATA16 NEWUSERFAILDESC,DATA18 EOF,DATA19 BUSI_SIGN, DATA20 OPER_TYPE");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, conn);
    }

    public static IData checkBatDealData(IData data, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT BATCH_TASK_ID,BATCH_ID,OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,REFER_TIME,CANCEL_TAG,DEAL_STATE,SERIAL_NUMBER,ROUTE_EPARCHY_CODE,");
        parser.addSQL(" DB_SOURCE,DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18, ");
        parser.addSQL(" DATA19,DATA20,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_TIME,DEAL_RESULT,TRADE_ID ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND DATA20 = :DATA20 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND BATCH_OPER_TYPE = :BATCH_OPER_TYPE ");
        parser.addSQL(" AND ROWNUM < 2 ");

        IDataset dataset = Dao.qryByParse(parser, conn);

        if (dataset == null || dataset.size() == 0)
        {
            return null;
        }

        return (IData) dataset.get(0);
    }

    public static IData checkBatDealDataBBOSS(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT BATCH_TASK_ID,BATCH_ID,OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,REFER_TIME,CANCEL_TAG,DEAL_STATE,SERIAL_NUMBER,ROUTE_EPARCHY_CODE,");
        parser.addSQL(" DB_SOURCE,DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18, ");
        parser.addSQL(" DATA19,DATA20,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_TIME,DEAL_RESULT,TRADE_ID ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND DATA20 = :DATA20 ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND BATCH_OPER_TYPE = :BATCH_OPER_TYPE ");
        IDataset dataset = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        if (dataset == null || dataset.size() == 0)
            return null;
        else
            return (IData) dataset.get(0);
    }

    public static String getBatchIdByBatchTaskId(String batchTaskId) throws Exception
    {
        String batchId = "";
        IData data = new DataMap();
        data.put("BATCH_TASK_ID", batchTaskId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT BATCH_TASK_ID,BATCH_ID,OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,REFER_TIME,CANCEL_TAG,DEAL_STATE,SERIAL_NUMBER,ROUTE_EPARCHY_CODE,");
        parser.addSQL(" DB_SOURCE,DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18, ");
        parser.addSQL(" DATA19,DATA20,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_TIME,DEAL_RESULT,TRADE_ID ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND BATCH_TASK_ID = :BATCH_TASK_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        IDataset dataset = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        data = (IData) dataset.get(0);
        batchId = data.getString("BATCH_ID");
        return batchId;
    }

    /*
     * 查批量明细录表取明细处理情况 tf_b_trade_batdeal
     */
    public static IDataset getBatSumData(IData data) throws Exception
    {
        // 汇总统计
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT COUNT(*) ICOUNT, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '0', 1, 0)) STATE_0, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '1', 1, 0)) STATE_1, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '2', 1, 0)) STATE_2, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '3', 1, 0)) STATE_3, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '4', 1, 0)) STATE_4, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '5', 1, 0)) STATE_5, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '6', 1, 0)) STATE_6, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '7', 1, 0)) STATE_7, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '8', 1, 0)) STATE_8, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, '9', 1, 0)) STATE_9, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, 'A', 1, 0)) STATE_A, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, 'B', 1, 0)) STATE_B, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, 'C', 1, 0)) STATE_C, ");
        parser.addSQL(" SUM(decode(t.DEAL_STATE, 'D', 1, 0)) STATE_D ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL t ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.BATCH_ID = :BATCH_ID ");
        parser.addSQL(" AND t.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        IDataset rstDataset = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

        return rstDataset;
    }

    public static IData getBatSumInfo(IData data) throws Exception
    {
        IDataset rstDataset = getBatSumData(data);// 查批量明细表统计信息

        String sHint = ""; // 记录批次处理信息
        long errorCount = 0; // 记录批次中错单数量
        if (rstDataset.size() > 0)
        {
            sHint = "批量汇总统计信息！";
            IData idret = rstDataset.getData(0);
            /*
             * 0 未启动 1 等待预处理 2 正在预处理 3 预处理成功 4 正在调用接口处理 5 接口调用处理成功 6 接口调用处理失败 7 等待订单完工 8 完工同步检查失败 9 订单处理完成 A 等待依赖批次完成 B
             * 预处理失败 C 正在处理依赖 D 依赖处理失败
             */

            long num = idret.getInt("ICOUNT", 0);

            if (num > 0)
            {
                sHint += "本批次共导入[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_0");
            if (num > 0)
            {
                sHint += "，未启动[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_1");
            if (num > 0)
            {
                sHint += "，等待预处理[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_2");
            if (num > 0)
            {
                sHint += "，正在预处理[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_3");
            if (num > 0)
            {
                sHint += "，预处理成功[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_4");
            if (num > 0)
            {
                sHint += "，正在调用接口[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_5");
            if (num > 0)
            {
                sHint += "，接口调用成功[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_6");
            errorCount = errorCount + num;// 将接口调用失败数据统计到错误数量中
            if (num > 0)
            {
                sHint += "，接口调用失败[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_7");
            if (num > 0)
            {
                sHint += "，等待订单完工[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_8");
            if (num > 0)
            {
                sHint += "，订单完工失败[" + Long.toString(num) + "]条";
            }

            num = idret.getInt("STATE_9", 0);

            if (num > 0)
            {
                sHint += "，订单处理完成[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_A");
            if (num > 0)
            {
                sHint += "，等待依赖批次完成[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_B");
            errorCount = errorCount + num;// 将预处理失败数据统计到错误数量中
            if (num > 0)
            {
                sHint += "，预处理失败[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_C");
            if (num > 0)
            {
                sHint += "，正在处理依赖[" + Long.toString(num) + "]条";
            }
            num = idret.getInt("STATE_D");
            errorCount = errorCount + num;// 将依赖处理失败数据统计到错误数量中
            if (num > 0)
            {
                sHint += "，依赖处理失败[" + Long.toString(num) + "]条";
            }
        }

        IData retData = new DataMap();
        retData.put("HINT_MESSAGE", sHint);
        retData.put("ERROR_COUNT", errorCount);
        return retData;
    }

    public static IDataset getJoinCause(IData inparam) throws Exception
    {
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL5_PK_TD_S_COMMPARA", inparam);
    }

    /**
     * 获取指定日期日、月导入数量
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getNowDayCount(String batch_oper_type) throws Exception
    {

        IData param = new DataMap();

        param.put("BATCH_OPER_TYPE", batch_oper_type);
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate());
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset set = Dao.qryByCode("TF_B_TRADE_BAT", "SEL_BATCHCOUNT_SUM", param, Route.getJourDb(Route.CONN_CRM_CG));

        // ACCEPT_DATE 传当天日期，YYYY-MM-DD
        // BATCH_OPER_TYPE 传批量处理类型编码
        // EPARCHY_CODE 传当前登记地州
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
     * 按分类查询该批量任务下今天的提交量
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getNowDayCountByClass(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT nvl(SUM(a.batch_count), 0) DAY_COUNT");
        parser.addSQL(" FROM tf_b_trade_bat a, tf_b_trade_bat_task b, td_b_batchtype c ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.BATCH_TASK_ID = b.BATCH_TASK_ID");
        parser.addSQL(" and c.batch_oper_type = b.batch_oper_code");
        parser.addSQL(" and to_char(a.accept_date, 'YYYY-MM-DD') = :NOW_DAY");
        // parser.addSQL(" and a.eparchy_code = :EPARCHY_CODE");
        parser.addSQL(" and c.class_code = :CLASS_CODE");
        // parser.addSQL(" and (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code
        // = 'ZZZZ')");
        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询该批量任务下今天的提交量
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getNowDayCountByType(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT nvl(SUM(a.batch_count), 0) DAY_COUNT");
        parser.addSQL(" FROM tf_b_trade_bat a, tf_b_trade_bat_task b");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.BATCH_TASK_ID = b.BATCH_TASK_ID");
        parser.addSQL(" and to_char(a.accept_date, 'YYYY-MM-DD') = :NOW_DAY");
        parser.addSQL(" and b.batch_oper_code = :BATCH_OPER_TYPE");
        parser.addSQL(" and a.remove_tag = '0'");
        // parser.addSQL(" and a.eparchy_code = :EPARCHY_CODE");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 按分类查询该批量任务下这个月的提交量
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getNowMonthCountByClass(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT nvl(SUM(a.batch_count), 0) MONTH_COUNT");
        parser.addSQL(" FROM tf_b_trade_bat a, tf_b_trade_bat_task b, td_b_batchtype c ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.BATCH_TASK_ID = b.BATCH_TASK_ID");
        parser.addSQL(" and c.batch_oper_type = b.batch_oper_code");
        parser.addSQL(" and to_char(a.accept_date, 'YYYY-MM') = :NOW_MONTH");
        parser.addSQL(" and c.class_code = :CLASS_CODE");
        // parser.addSQL(" and a.eparchy_code = :EPARCHY_CODE");
        // parser.addSQL(" and (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code
        // = 'ZZZZ')");
        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询该批量任务下这个月的提交量
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getNowMonthCountByType(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT nvl(SUM(a.batch_count), 0) MONTH_COUNT");
        parser.addSQL(" FROM tf_b_trade_bat a, tf_b_trade_bat_task b");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.BATCH_TASK_ID = b.BATCH_TASK_ID");
        parser.addSQL(" and to_char(a.accept_date, 'YYYY-MM') = :NOW_MONTH");
        parser.addSQL(" and b.batch_oper_code = :BATCH_OPER_TYPE");
        // parser.addSQL(" and a.eparchy_code = :EPARCHY_CODE");
        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset getOperTypeBySpAndBiz(IData data) throws Exception
    {
        /*SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  DECODE(A.RSRV_STR3,'1','000000100000000000000000000000','2','000001100000000000000000000000',");
        parser.addSQL(" '3','000000000000000000100000000000',DECODE(LENGTH(A.RSRV_STR3),30,A.RSRV_STR3,B.BIZ_PROCESS_TAG)) BIZ_PROCESS_TAG");
        parser.addSQL(" FROM TD_B_PLATSVC A,TD_B_PLATSVC_PARAM B");
        parser.addSQL(" WHERE A.BIZ_TYPE_CODE = B.BIZ_TYPE_CODE AND A.ORG_DOMAIN = B.ORG_DOMAIN AND A.SERV_TYPE = B.SERV_TYPE");
        parser.addSQL(" AND A.BIZ_STATE_CODE = 'A' AND A.SP_CODE = :SP_CODE AND A.BIZ_CODE= :BIZ_CODE");
        parser.addSQL(" AND SYSDATE BETWEEN START_DATE AND END_DATE");
        parser.addSQL(" AND A.BIZ_TYPE_CODE = :BIZ_TYPE_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);*/
    	String offerType = "Z";
    	String spCode = data.getString("SP_CODE");
    	String bizTypeCode = data.getString("BIZ_TYPE_CODE");
    	String bizCode = data.getString("BIZ_CODE");
    	return UpcCall.querySpServiceParamByCond(null, offerType, spCode, bizTypeCode, bizCode);
    }

    /**
     * 作用：根据BATCH_TASK_ID获取条件参数
     * 
     * @param td
     * @param data
     * @throws Exception
     */
    public static String getTaskCondString(String batchTaskId) throws Exception
    {

        IDataset batchInfo = getTaskInfo(batchTaskId, null);
        if (IDataUtil.isEmpty(batchInfo))
        {
            return null;
        }
        IData condData = batchInfo.getData(0);
        String condString = condData.getString("CODING_STR1", "") + condData.getString("CODING_STR2", "") + condData.getString("CODING_STR3", "") + condData.getString("CODING_STR4", "") + condData.getString("CODING_STR5", "");
        return condString;
    }

    /**
     * 根据BATCH_TASK_ID获取条件参数
     * 
     * @param batch_task_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getTaskInfo(String batch_task_id, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("BATCH_TASK_ID", batch_task_id);
        return Dao.qryByCode("TF_B_TRADE_BAT_TASK", "SEL_ALL_BY_PK", param, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 对批量类型进行拆分权限
     */
    public static IDataset qryBatchTypeByPriv(IDataset batchTypes) throws Exception
    {

        int batchLength = batchTypes.size();
        for (int i = batchLength - 1; i >= 0; i--)
        {
            boolean isPriv = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), batchTypes.getData(i).getString("RIGHT_CODE"));
            if (!isPriv)
            {
                batchTypes.remove(i);
            }

        }
        return batchTypes;
    }

    public static IData qryTradeBatByPK(IData data) throws Exception
    {
        /*IData batData = Dao.qryByPK("TF_B_TRADE_BAT", data, Route.getJourDb(Route.CONN_CRM_CG));
        return batData;*/
        
        IData param = new DataMap();
        param.put("BATCH_ID", data.getString("BATCH_ID"));
        param.put("ACCEPT_MONTH", data.getString("ACCEPT_MONTH"));

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select BATCH_TASK_ID,BATCH_ID,TO_CHAR(ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,STAFF_ID,DEPART_ID,BATCH_COUNT,ACTIVE_FLAG,");
        parser.addSQL(" AUDIT_STATE,AUDIT_INFO,AUDIT_STAFF_ID,TO_CHAR(AUDIT_DATE,'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE,");
        parser.addSQL(" REMOVE_TAG,TO_CHAR(ACTIVE_TIME,'yyyy-mm-dd hh24:mi:ss') ACTIVE_TIME,AUDIT_REMARK,BATCH_OPER_TYPE,DST_ONE_KEY_FLAG");
        parser.addSQL("  from TF_B_TRADE_BAT");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and BATCH_ID = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG)).getData(0);
         
    }

    public static IDataset queryBatch(String batchId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", batchId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select BATCH_TASK_ID,BATCH_ID,TO_CHAR(ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,STAFF_ID,DEPART_ID,BATCH_COUNT,DECODE(ACTIVE_FLAG,'0','未启动','1','已启动','未知') ACTIVE_FLAG,");
        parser.addSQL(" DECODE(AUDIT_STATE,'0','无需审核','1','等待审核','2','审核通过','3','审核不通过','未知') AUDIT_STATE,AUDIT_INFO,AUDIT_STAFF_ID,TO_CHAR(AUDIT_DATE,'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE,");
        parser.addSQL(" DECODE(REMOVE_TAG,'0','未删除','1','已删除','未知') REMOVE_TAG,TO_CHAR(ACTIVE_TIME,'yyyy-mm-dd hh24:mi:ss') ACTIVE_TIME,AUDIT_REMARK,BATCH_OPER_TYPE,DST_ONE_KEY_FLAG");
        parser.addSQL("  from tf_b_trade_bat");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and BATCH_ID = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /** huanghui */
    public static IDataset queryBatchDealNotRunInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and BATCH_TASK_ID = :BATCH_TASK_ID");
        parser.addSQL(" and ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        parser.addSQL(" and DEAL_STATE <> '0'");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询批量任务信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData queryBatchInfo(IData data) throws Exception
    {

        IDataset batchInfo = Dao.qryByCode("TF_B_TRADE_BAT_TASK", "SEL_ALL_BY_PK", data, Route.getJourDb(Route.CONN_CRM_CG));

        if (batchInfo.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_31);
        }

        return batchInfo.getData(0);
    }

    /** 查询批次详细 */
    public static IDataset queryBatchInfo(IData data, Pagination pagenation) throws Exception
    {

        return queryBatchInfo(data, pagenation, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /** 查询批次详细 */
    public static IDataset queryBatchInfo(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT b.batch_id BATCH_ID,a.cancel_tag CANCEL_TAG,a.deal_state DEAL_STATE,");
        parser.addSQL(" COUNT(*) trade_num ");
        parser.addSQL(" from TF_B_TRADE_BATDEAL A,tf_b_trade_bat b");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND b.BATCH_TASK_ID = TO_NUMBER(:BATCH_TASK_ID)");
        parser.addSQL(" AND b.batch_id+0 = a.batch_id(+)");
        parser.addSQL(" AND b.remove_tag = '0'");
        parser.addSQL(" AND a.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        parser.addSQL(" AND b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        parser.addSQL(" GROUP BY  b.batch_id,A.CANCEL_TAG, A.DEAL_STATE");
        parser.addSQL(" ORDER BY  b.batch_id,A.CANCEL_TAG, A.DEAL_STATE");

        return Dao.qryByParse(parser, pagination, conn);
    }

    public static IDataset queryBatchTaskList(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser
                .addSQL(" select TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,BATCH_TASK_NAME,BATCH_OPER_NAME,CREATE_STAFF_ID,CREATE_DEPART_ID,CREATE_CITY_CODE,CREATE_EPARCHY_CODE,TO_CHAR(CREATE_TIME,'yyyy-mm-dd hh24:mi:ss') CREATE_TIME,AUDIT_NO,REMARK, TO_CHAR(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, TO_CHAR(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE");
        parser.addSQL(" FROM tf_b_trade_bat_task");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and CREATE_STAFF_ID = UPPER(:CREATE_STAFF_ID)");
        parser.addSQL(" and CREATE_EPARCHY_CODE = :CREATE_EPARCHY_CODE");
        parser.addSQL(" and CREATE_CITY_CODE = :CREATE_CITY_CODE");
        parser.addSQL(" and BATCH_TASK_ID = :BATCH_TASK_ID");
        parser.addSQL(" and BATCH_OPER_CODE = :BATCH_OPER_TYPE");
        parser.addSQL(" and CREATE_TIME >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')");
        parser.addSQL(" and CREATE_TIME <=TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss') + 1");
        parser.addSQL(" and BATCH_OPER_NAME=:BATCH_OPER_NAME");// duxy修改
        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /** 查询批量业务参数信息 */
    public static IData queryBatchTypeParamsEx(String BATCH_OPER_CODE, String EPARCHY_CODE) throws Exception
    {
        IData params = new DataMap();
        params.put("EPARCHY_CODE", EPARCHY_CODE);
        params.put("BATCH_OPER_TYPE", BATCH_OPER_CODE);
        IDataset datas = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_LIMITNUM_BY_BATCHTYPE", params, Route.CONN_CRM_CEN);

        if (datas.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_351);
        }

        return datas.getData(0);
    }

    /**
     * 查询批量类型，怕编码报错，先加上，到时候去掉
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryBatchTypes() throws Exception
    {
        IData data = new DataMap();

        data.put("TRADE_ATTR", "2");
        IDataset batchTypes = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_STAFFRIGHT", data, Route.CONN_CRM_CEN);

        batchTypes = qryBatchTypeByPriv(batchTypes);
        // 按照批量名字排序
        DataHelper.sort(batchTypes, "BATCH_OPER_NAME", IDataset.TYPE_STRING);

        return batchTypes;
    }

    public static IDataset queryBatchTypes(IData inparam) throws Exception
    {
        return Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_STAFFRIGHT", inparam, Route.CONN_CRM_CEN);
    }

    /**
     * 查询批量业务类型
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryBatchTypes(String right_class, String staffId) throws Exception
    {

        IData data = new DataMap();
        data.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("RIGHT_CLASS", right_class);
        IDataset batchTypes = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_STAFFRIGHT", data);

        return batchTypes;
    }

    public static IDataset queryBatDeal(IData param, Pagination page) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM TF_B_TRADE_BATDEAL A ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND A.BATCH_ID IN (SELECT B.BATCH_ID FROM TF_B_TRADE_BAT B WHERE B.BATCH_TASK_ID=:BATCH_TASK_ID) ");
        sql.append(" AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        return Dao.qryBySql(sql, param, page);
    }

    public static IDataset queryBatDealByBatchId(IData param, Pagination page) throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM TF_B_TRADE_BATDEAL WHERE BATCH_ID = :BATCH_ID");
        sql.append(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        return Dao.qryBySql(sql, param, page);
    }

    /**
     * @Function: queryBatDealBySN
     * @Description: 批量结果信息查询
     * @param：
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 上午10:52:30 2013-8-28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-8-28 huanghui v1.0.0
     */
    public static IDataset queryBatDealBySN(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT TO_CHAR(a.batch_task_id) batch_task_id,");
        parser.addSQL(" TO_CHAR(a.batch_id) batch_id,");
        parser.addSQL(" TO_CHAR(a.operate_id) operate_id,");
        parser.addSQL(" TO_CHAR(a.trade_id) trade_id,");
        parser.addSQL(" a.batch_oper_type,");
        parser.addSQL(" c.batch_oper_name,");
        parser.addSQL(" a.priority,");
        parser.addSQL(" TO_CHAR(a.refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,");
        parser.addSQL(" TO_CHAR(a.exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,");
        parser.addSQL(" a.serial_number,");
        parser.addSQL(" a.route_eparchy_code,");
        parser.addSQL(" a.db_source,");
        parser.addSQL(" a.data1,a.data2,a.data3,a.data4,a.data5,a.data6,a.data7,a.data8,a.data9,a.data10,");
        parser.addSQL(" a.data11,a.data12,a.data13,a.data14,a.data15,a.data16,a.data17,a.data18,a.data19,a.data20,");
        parser.addSQL(" a.cancel_tag,");
        parser.addSQL(" TO_CHAR(a.cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,");
        parser.addSQL(" a.cancel_staff_id,");
        parser.addSQL(" a.cancel_depart_id,");
        parser.addSQL(" a.cancel_city_code,");
        parser.addSQL(" a.cancel_eparchy_code,");
        parser.addSQL(" a.deal_state,");
        parser.addSQL(" TO_CHAR(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,");
        parser.addSQL(" a.deal_result,");
        parser.addSQL(" a.deal_desc,");
        parser.addSQL(" b.batch_task_name");
        parser.addSQL(" FROM tf_b_trade_batdeal a,tf_b_trade_bat_task b,td_b_batchtype c");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND a.serial_number = :SERIAL_NUMBER");
        parser.addSQL(" AND a.BATCH_ID = :BATCH_ID");
        parser.addSQL(" AND a.refer_time+0 > TO_DATE(:START_TIME,'yyyy-mm-dd')");
        parser.addSQL(" AND a.refer_time+0 < TO_DATE(:END_TIME,'yyyy-mm-dd') + 1");
        parser.addSQL(" AND a.batch_task_id = b.batch_task_id");
        parser.addSQL(" AND a.BATCH_OPER_TYPE = c.BATCH_OPER_TYPE");
        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询可启动的任务改造
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryBatInfoByTaksID(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT A.* FROM TF_B_TRADE_BAT A");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND A.BATCH_TASK_ID = :BATCH_TASK_ID ");
        parser.addSQL(" AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryBatsTypeByPK(IData data) throws Exception
    {

        IDataset set = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_PK", data, Route.CONN_CRM_CEN);

        return set;
    }

    public static IDataset queryBatTaskBatchInfo(IData inparam) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_TRADE_BAT", "SEL_ALL_BY_PK", inparam, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询批量任务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryBatTasks(String batchTaskId, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("BATCH_TASK_ID", batchTaskId);

        return Dao.qryByCode("TF_B_TRADE_BAT_TASK", "SEL_ALL_BY_PK", data, pagination, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 可删除的批次
     * 
     * @return
     */
    public static IDataset queryBatTradesForDel(IData data, Pagination pagination) throws Exception
    {

        return queryBatTradesForDel(data, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 可删除的批次
     * 
     * @return
     */
    public static IDataset queryBatTradesForDel(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT a.*");
        parser.addSQL(" FROM tf_b_trade_bat a");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.BATCH_TASK_ID = :BATCH_TASK_ID");
        parser.addSQL(" AND a.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_TASK_ID, 5, 2)) ");
        parser.addSQL(" and a.staff_id = :STAFF_ID");
        parser.addSQL(" and a.active_flag = '0'");
        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, pagination, conn);
    }

    /**
     * 查询能删除的批次
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData queryBatTypeByPK(IData data) throws Exception
    {

        IDataset set = Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_PK", data, Route.CONN_CRM_CEN);
        if (set.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_310);
        }

        return set.getData(0);
    }

    public static IDataset queryBBossAllInfoMemberInfos(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT BATCH_TASK_ID,BATCH_ID,OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,REFER_TIME,CANCEL_TAG,DEAL_STATE,SERIAL_NUMBER,ROUTE_EPARCHY_CODE,");
        parser.addSQL(" DB_SOURCE,DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18, ");
        parser.addSQL(" DATA19,DATA20,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_TIME,DEAL_RESULT,TRADE_ID ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND BATCH_ID = :BATCH_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryBBossMemberInfos(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT T.SERIAL_NUMBER,T.DATA1,T.DATA2,T.DATA3,T.DATA4,T.DATA5,T.DATA6,T.DATA7,T.DATA8,T.DATA9,T.DATA10,T.DATA11,T.DATA12,T.DATA13,T.DATA14,T.DATA15,T.DATA16,T.DATA17,T.DATA18,T.DATA19,T.DATA20,to_char(sysdate, 'YYYYMMDDHH24MISS') EFFDATE ,'1' ACTION,'1' USER_TYPE");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND BATCH_TASK_ID = :BATCH_TASK_ID ");
        parser.addSQL(" AND BATCH_ID = :BATCH_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryBBossMemberInfos(IData data, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT T.SERIAL_NUMBER,T.DATA1,T.DATA2,T.DATA3,T.DATA4,T.DATA5,T.DATA6,T.DATA7,T.DATA8,T.DATA9,T.DATA10,T.DATA11,T.DATA12,T.DATA13,T.DATA14,T.DATA15,T.DATA16,T.DATA17,T.DATA18,T.DATA19,T.DATA20,to_char(sysdate, 'YYYYMMDDHH24MISS') EFFDATE ,'1' ACTION,'1' USER_TYPE");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND BATCH_TASK_ID = :BATCH_TASK_ID ");
        parser.addSQL(" AND BATCH_ID = :BATCH_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        return Dao.qryByParse(parser, conn);
    }

    /**
     * @author mengjh
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryBBossMemberInfosByBidSn(IData data, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT BATCH_TASK_ID,BATCH_ID,OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,REFER_TIME,CANCEL_TAG,DEAL_STATE,SERIAL_NUMBER,ROUTE_EPARCHY_CODE,");
        parser.addSQL(" DB_SOURCE,DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18, ");
        parser.addSQL(" DATA19,DATA20,CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_TIME,DEAL_RESULT,TRADE_ID ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL T WHERE 1=1 AND T.DEAL_STATE ='0' ");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND BATCH_ID = :BATCH_ID ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        return Dao.qryByParse(parser, conn);
    }

    public static IDataset queryCommpara(IData inparam) throws Exception
    {
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL5_PK_TD_S_COMMPARA", inparam);
    }

    /**
     * @Function: queryDiscntInfo
     * @Description: 批量优惠变更 优惠查询
     * @param：
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午3:19:43 2013-9-9 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-9 huanghui v1.0.0
     */
    public static IDataset queryDiscntInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select d.discnt_code, d.discnt_name");
        parser.addSQL(" from td_b_discnt d");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and d.discnt_code = :DISCNT_CODE");
        parser.addSQL(" and d.discnt_name like '%' || :DISCNT_NAME || '%'");
        parser.addSQL(" and sysdate between d.start_date and d.end_date ");
        parser.addSQL(" and exists(" + " select 1 from td_s_commpara para" + " where 1=1" + " and para.param_attr = '96'" + " and para.param_code = d.discnt_code" + ")");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }
    
    /**
     * @Function: queryDiscntSpecInfo
     * @Description: 批量优惠变更 优惠查询
     * @param：
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午3:19:43 2013-9-9 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-9 huanghui v1.0.0
     */
    public static IDataset queryDiscntSpecInfo(IData param, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL(" select d.discnt_code, d.discnt_name");
    	parser.addSQL(" from td_b_discnt d,td_s_static b ");
    	parser.addSQL(" where 1=1");
    	parser.addSQL(" and d.discnt_code = :DISCNT_CODE");
    	parser.addSQL(" and d.discnt_name like '%' || :DISCNT_NAME || '%'");
    	parser.addSQL(" and sysdate between d.start_date and d.end_date ");
    	parser.addSQL(" and d.discnt_code = b.data_id ");
    	parser.addSQL(" and b.type_id = 'UNLIMITDISCNTCHG' ");
    	
    	
    	return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryDiscnts(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" select d.discnt_code element_id, d.discnt_name element_name,'1' element_type");
        parser.addSQL(" from td_b_discnt d, td_b_package_element p");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and d.discnt_code = p.element_id");
        parser.addSQL(" and p.element_type_code = 'D'");
        parser.addSQL(" and d.discnt_code = :ELEMENT_ID");
        parser.addSQL(" and d.discnt_name like '%' || :ELEMENT_NAME || '%'");
        parser.addSQL(" and (d.eparchy_code='ZZZZ' or d.eparchy_code=:EPARCHY_CODE)");
        parser.addSQL(" and sysdate between p.start_date and p.end_date");
        parser.addSQL(" and sysdate between d.start_date and d.end_date");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryFaildInfo(String batchId) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", batchId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT batch_id,serial_number,deal_result ");
        parser.addSQL(" from tf_b_trade_batdeal ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND BATCH_ID = TO_NUMBER(:BATCH_ID) ");
        parser.addSQL(" AND DEAL_STATE ='6' ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryG3NetCardType(IData inparam) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", inparam, Route.CONN_CRM_CEN);
    }

    public static IDataset queryG3PhoneByProductId(IData inparam) throws Exception
    {
        return Dao.qryByCode("TD_B_PACKAGE_EXT", "SEL_BY_PID_WITH_PRIV", inparam);
    }

    /**
     * 查询需审批的子任务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryNeedApproveBatTrades(IData data, Pagination pagination) throws Exception
    {

        return queryNeedApproveBatTrades(data, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询需审批的子任务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryNeedApproveBatTrades(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT a.*");
        parser.addSQL(" FROM tf_b_trade_bat a");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.AUDIT_STATE = '1'");
        parser.addSQL(" and a.ACTIVE_FLAG = '0'");
        parser.addSQL(" and a.batch_task_id = :BATCH_TASK_ID");
        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, pagination, conn);
    }

    /**
     * 查询可删除的任务改造
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryNeedDeleteBatTradeC(IData data, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT a.*");
        parser.addSQL(" FROM tf_b_trade_bat a");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.BATCH_TASK_ID = :BATCH_TASK_ID");
        parser.addSQL(" and a.staff_id = :STAFF_ID");
        parser.addSQL(" and a.active_flag = '0'");
        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 查询可启动的任务改造
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryNeedStartBatTradeC(IData data, Pagination pagination) throws Exception
    {
        String batchIds = data.getString("BATCH_IDS");
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT a.*");
        parser.addSQL(" FROM tf_b_trade_bat a");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" and a.ACTIVE_FLAG = '0'");
        parser.addSQL(" and a.staff_id = :STAFF_ID");
        parser.addSQL(" and (a.AUDIT_STATE = '0' or a.AUDIT_STATE = '2')");
        parser.addSQL(" and a.batch_task_id = :BATCH_TASK_ID");
        if (StringUtils.isNotBlank(batchIds))
        {
            parser.addSQL(" and a.batch_id in (");
            parser.addSQL(batchIds);
            parser.addSQL(")");
        }

        parser.addSQL(" and a.remove_tag = '0'");

        return Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryOcsDealInfo(IData data) throws Exception
    {
        SQLParser selectSql = new SQLParser(data);
        selectSql.addSQL(" SELECT a.deal_id,a.serial_number,a.accept_date,a.biz_type,a.monitor_flag,a.monitor_rule_code,a.accept_staff_id,a.accept_depart_id,a.accept_mode,a.write_type,  ");
        selectSql.addSQL(" a.enable_tag,TO_CHAR(a.start_date,'yyyy-mm-dd hh24:mi:ss') START_DATE, TO_CHAR(a.end_date,'yyyy-mm-dd hh24:mi:ss') END_DATE");
        selectSql.addSQL(",a.deal_state,TO_CHAR(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,a.deal_result,a.trade_id,a.batch_id,a.eparchy_code ");
        selectSql.addSQL(" FROM tf_b_ocs_batdeal a WHERE 1=1 ");
        selectSql.addSQL(" AND a.batch_id = :BATCH_ID ");
        selectSql.addSQL(" AND a.serial_number = :SERIAL_NUMBER ");
        selectSql.addSQL(" AND a.accept_date >= to_date(:START_DATE, 'YYYY-MM-DD') ");
        selectSql.addSQL(" AND a.accept_date < to_date(:END_DATE, 'YYYY-MM-DD') + 1 ");
        selectSql.addSQL(" AND a.accept_mode = '1'  "); // 受理方式：0-批量后台生成，1-批量手工导入，2-业务办理生成
        selectSql.addSQL(" AND a.biz_type = :BIZ_TYPE  "); // 业务类型
        selectSql.addSQL(" UNION ALL ");
        selectSql.addSQL(" SELECT b.deal_id,b.serial_number,b.accept_date,b.biz_type,b.monitor_flag,b.monitor_rule_code,b.accept_staff_id,b.accept_depart_id,b.accept_mode,b.write_type, ");
        selectSql.addSQL(" b.enable_tag,TO_CHAR(b.start_date,'yyyy-mm-dd hh24:mi:ss') START_DATE, TO_CHAR(b.end_date,'yyyy-mm-dd hh24:mi:ss') END_DATE");
        selectSql.addSQL(",b.deal_state,TO_CHAR(b.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,b.deal_result,b.trade_id,b.batch_id,b.eparchy_code ");
        selectSql.addSQL(" FROM tf_bh_ocs_batdeal b WHERE 1=1  ");
        selectSql.addSQL(" AND b.batch_id = :BATCH_ID  ");
        selectSql.addSQL(" AND b.serial_number = :SERIAL_NUMBER  ");
        selectSql.addSQL(" AND b.accept_date >= to_date(:START_DATE, 'YYYY-MM-DD') ");
        selectSql.addSQL(" AND b.accept_date < to_date(:END_DATE, 'YYYY-MM-DD') + 1  ");
        selectSql.addSQL(" AND b.accept_mode = '1'  "); // 受理方式：0-批量后台生成，1-批量手工导入，2-业务办理生成
        selectSql.addSQL(" AND b.biz_type = :BIZ_TYPE  "); // 业务类型
        return Dao.qryByParse(selectSql, Route.ROUTE_EPARCHY_CODE);
    }

    public static IDataset queryPlatInfo(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_M_SP_INFO", "SEL_BY_CODENAME", inparam, pagination);
    }

    public static IDataset queryPopuTaskInfoBySn(IData data, Pagination pagenation) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT b.batch_task_id,a.batch_id,b.batch_task_name,b.batch_oper_name,b.start_date,b.end_date,b.create_staff_id,b.create_time,b.audit_no,b.remark");
        parser.addSQL(" FROM tf_b_trade_batdeal a, tf_b_trade_bat_task b");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND a.batch_task_id = b.batch_task_id");
        parser.addSQL(" AND a.serial_number = :SERIAL_NUMBER");
        return Dao.qryByParse(parser, pagenation, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryPopuTaskInfoByTaskId(IData data, Pagination pagenation) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT TO_CHAR(a.batch_task_id) batch_task_id,");
        parser.addSQL(" a.batch_task_name,");
        parser.addSQL(" a.batch_oper_code,");
        parser.addSQL(" a.batch_oper_name,");
        parser.addSQL(" TO_CHAR(a.start_date,'YYYY-MM-DD HH24:MI:SS') start_date,");
        parser.addSQL(" TO_CHAR(a.end_date,'YYYY-MM-DD HH24:MI:SS') end_date,");
        parser.addSQL(" TO_CHAR(a.create_time,'YYYY-MM-DD HH24:MI:SS') create_time,");
        parser.addSQL(" a.create_staff_id,");
        parser.addSQL(" a.create_depart_id,");
        parser.addSQL(" a.create_city_code,");
        parser.addSQL(" a.create_eparchy_code,");
        parser.addSQL(" a.sms_flag,");
        parser.addSQL(" a.audit_no,");
        parser.addSQL(" a.remark,");
        parser.addSQL(" a.coding_str1,a.coding_str2,a.coding_str3,a.coding_str4,a.coding_str5,");
        parser.addSQL(" a.condition1,a.condition2,a.condition3,a.condition4,a.condition5");
        parser.addSQL(" FROM tf_b_trade_bat_task a");
        parser.addSQL(" WHERE a.batch_task_id = TO_NUMBER(:BATCH_TASK_ID)");
        return Dao.qryByParse(parser, pagenation, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryResId(IData inparam) throws Exception
    {
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_RESID_BY_PACKAGEID", inparam);
    }

    /**
     * @Function: querySerivceInfo
     * @Description: 批量服务编码、名称查询
     * @param：
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午3:19:43 2013-9-9 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-9 huanghui v1.0.0
     */
    public static IDataset querySerivceInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT S.SERVICE_ID, S.SERVICE_NAME");
        parser.addSQL(" FROM TD_B_SERVICE S");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND S.SERVICE_ID = :SERVICE_ID");
        parser.addSQL(" AND S.SERVICE_NAME LIKE '%' || :SERVICE_NAME || '%'");
        parser.addSQL(" AND SYSDATE BETWEEN S.START_DATE AND S.END_DATE");
        parser.addSQL(" AND EXISTS");
        parser.addSQL(" (SELECT 1 FROM TD_S_COMMPARA PARA");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND PARA.PARAM_ATTR = '991'");
        parser.addSQL(" AND PARA.PARA_CODE1 = S.SERVICE_ID)");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }
    
    public static IDataset querySerivceSpecInfo(IData param, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL(" SELECT B.DATA_ID, B.DATA_NAME");
    	parser.addSQL(" FROM TD_B_SERVICE S,TD_S_STATIC B ");
    	parser.addSQL(" WHERE 1=1");
    	parser.addSQL(" AND B.DATA_ID = :SERVICE_ID");
    	parser.addSQL(" AND B.DATA_NAME LIKE '%' || :SERVICE_NAME || '%'");
    	parser.addSQL(" AND S.SERVICE_ID = B.DATA_ID ");
    	parser.addSQL(" AND SYSDATE BETWEEN S.START_DATE AND S.END_DATE");
    	parser.addSQL(" AND B.TYPE_ID = 'UNLIMITSERVICECHG' ");
    	
    	return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryServiceInfoForPlat(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TD_B_PLATSVC", "SEL_BY_CODENAME", inparam, pagination);
    }

    public static IDataset queryServices(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" select d.service_id element_id, d.service_name element_name, '2' element_type");
        parser.addSQL(" from td_b_service d, td_b_package_element p");
        parser.addSQL(" where 1=1");
        parser.addSQL(" and d.service_id = p.element_id");
        parser.addSQL(" and p.element_type_code = 'S'");
        parser.addSQL(" and d.service_id = :ELEMENT_ID");
        parser.addSQL(" and d.service_name like '%' || :ELEMENT_NAME || '%'");
        parser.addSQL(" and sysdate between d.start_date and d.end_date");
        parser.addSQL(" and sysdate between p.start_date and p.end_date");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryTradeBatdeal(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.BATCH_ID,T.SERIAL_NUMBER FROM TF_B_TRADE_BATDEAL T");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND T.DEAL_STATE in ('A','1','2','4','5')");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    
    /**
     * 根据批量号和号码,返回总量
     * @param batId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static int queryBatDealCntBySerialNumber(String batId,String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", batId);
        param.put("SERIAL_NUMBER", serialNumber);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT COUNT(*) CNT ");
        parser.addSQL("  FROM TF_B_TRADE_BATDEAL T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.BATCH_ID = :BATCH_ID ");
        parser.addSQL("  AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL("  AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        IDataset dataset = Dao.qryByParse(parser);
        String count = dataset.getData(0).getString("CNT");

        return Integer.parseInt(count);
    } 
    
    /**
     * 根据批量号和data1,返回总量
     * @param batId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static int queryBatDealCntByData1(String batId,String data1) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", batId);
        param.put("DATA1", data1);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT COUNT(*) CNT ");
        parser.addSQL("  FROM TF_B_TRADE_BATDEAL T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.BATCH_ID = :BATCH_ID ");
        parser.addSQL("  AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL("  AND T.DATA1 = :DATA1 ");
        IDataset dataset = Dao.qryByParse(parser);
        String count = dataset.getData(0).getString("CNT");

        return Integer.parseInt(count);
    } 
    
    
    /**
     * 
     * @param batId
     * @return
     * @throws Exception
     */
    public static int queryBatDealCntByBatchId(String batId) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", batId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT COUNT(*) CNT ");
        parser.addSQL("  FROM TF_B_TRADE_BATDEAL T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.BATCH_ID = :BATCH_ID ");
        parser.addSQL("  AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        IDataset dataset = Dao.qryByParse(parser);
        String count = dataset.getData(0).getString("CNT");
        return Integer.parseInt(count);
    } 
    
}
