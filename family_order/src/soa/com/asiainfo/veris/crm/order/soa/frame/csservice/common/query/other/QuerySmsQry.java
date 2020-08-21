
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 功能：短信的具体数据库查询 作者：GongGuang
 */
public class QuerySmsQry extends CSBizBean
{
    /**
     * 功能：针对起止时间为不同月份的10656888接口
     */
    public static IDataset querySms0DiffMonth(String startDate, String endDate, String serialNumber, String theStartMonth, String theEndMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT TRADE_ID PARA_CODE1,SERIAL_NUMBER PARA_CODE2,REQ_CODE PARA_CODE3,AIM_NUMBER   PARA_AIM_NUMBER,LOGIN_NUMBER PARA_CODE4,RSP_CONTENT PARA_CODE5,TO_CHAR(START_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE6,TO_CHAR(RECEIVE_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE7,");
        parser.addSQL("TO_NUMBER(TO_CHAR(RECEIVE_TIME,'MM')) PARA_CODE8,'' PARA_CODE9,'' PARA_CODE10,'' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15,'' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20,'' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23, ");
        parser.addSQL("'' PARA_CODE24,'' PARA_CODE25,'' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30,'' START_DATE,'' END_DATE,'' EPARCHY_CODE,TO_CHAR(END_TIME,'YYYY-MM-DD HH24:MI:SS') SEND_TIME,RESULT_CODE SEND_CODE,RESULT_DESC RSP_RESULT,'' REMARK, ");//k3
        parser.addSQL("'' UPDATE_STAFF_ID,'' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL(" FROM TF_B_TRADELOG_SMS_");
        parser.addSQL(theStartMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND SERIAL_NUMBER = :VPARA_CODE1 ");
        parser.addSQL("  AND RECEIVE_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND RECEIVE_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        /**
         * REQ201611220039_短信查询界面优化
         * @author zhuoyingzhi
         * 取消原有涉及密码屏蔽整条短信模糊化规则
         */
        /*parser.addSQL(" AND RSP_CONTENT NOT LIKE '%密码%' ");*/
        parser.addSQL("  UNION ALL ");
        parser
                .addSQL("SELECT TRADE_ID PARA_CODE1,SERIAL_NUMBER PARA_CODE2,REQ_CODE PARA_CODE3,AIM_NUMBER   PARA_AIM_NUMBER,LOGIN_NUMBER PARA_CODE4,RSP_CONTENT PARA_CODE5,TO_CHAR(START_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE6,TO_CHAR(RECEIVE_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE7,");
        parser
                .addSQL("TO_NUMBER(TO_CHAR(RECEIVE_TIME,'MM')) PARA_CODE8,'' PARA_CODE9,'' PARA_CODE10,'' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15,'' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20,'' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23, ");
        parser.addSQL("'' PARA_CODE24,'' PARA_CODE25,'' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30,'' START_DATE,'' END_DATE,'' EPARCHY_CODE,TO_CHAR(END_TIME,'YYYY-MM-DD HH24:MI:SS') SEND_TIME,RESULT_CODE SEND_CODE,RESULT_DESC RSP_RESULT,'' REMARK, ");//k3
        parser.addSQL("'' UPDATE_STAFF_ID,'' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL(" FROM TF_B_TRADELOG_SMS_");
        parser.addSQL(theEndMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND SERIAL_NUMBER = :VPARA_CODE1 ");
        parser.addSQL("  AND RECEIVE_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND RECEIVE_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        /**
         * REQ201611220039_短信查询界面优化
         * @author zhuoyingzhi
         * 取消原有涉及密码屏蔽整条短信模糊化规则
         */
        /*parser.addSQL(" AND RSP_CONTENT NOT LIKE '%密码%' ");*/
        return Dao.qryByParse(parser, page, "uec");

    }

    /**
     * 功能：针对起止时间为相同月份的10656888接口
     */
    public static IDataset querySms0SameMonth(String startDate, String endDate, String serialNumber, String theMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser
                .addSQL("SELECT TRADE_ID PARA_CODE1,SERIAL_NUMBER PARA_CODE2,REQ_CODE PARA_CODE3,AIM_NUMBER   PARA_AIM_NUMBER,LOGIN_NUMBER PARA_CODE4,RSP_CONTENT PARA_CODE5,TO_CHAR(START_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE6,TO_CHAR(RECEIVE_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE7,");
        parser
                .addSQL("TO_NUMBER(TO_CHAR(RECEIVE_TIME,'MM')) PARA_CODE8,'' PARA_CODE9,'' PARA_CODE10,'' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15,'' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20,'' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23, ");
        parser.addSQL("'' PARA_CODE24,'' PARA_CODE25,'' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30,'' START_DATE,'' END_DATE,'' EPARCHY_CODE,TO_CHAR(END_TIME,'YYYY-MM-DD HH24:MI:SS') SEND_TIME,RESULT_CODE SEND_CODE,RESULT_DESC RSP_RESULT,'' REMARK, ");//k3
        parser.addSQL("'' UPDATE_STAFF_ID,'' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL(" FROM TF_B_TRADELOG_SMS_");
        parser.addSQL(theMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND SERIAL_NUMBER = :VPARA_CODE1 ");
        parser.addSQL("  AND RECEIVE_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND RECEIVE_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        /**
         * REQ201611220039_短信查询界面优化
         * @author zhuoyingzhi
         * 取消原有涉及密码屏蔽整条短信模糊化规则
         */
        //parser.addSQL(" AND RSP_CONTENT NOT LIKE '%密码%' ");
        
        return Dao.qryByParse(parser, page, "uec");
    }
    
    /**
     * 功能：针对起止时间为相同月份的106304444接口
     */
    public static IDataset querySms0SameMonthNew(String startDate, String endDate, String serialNumber, String theMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser
                .addSQL("SELECT TRADE_ID PARA_CODE1,SERIAL_NUMBER PARA_CODE2,AIM_NUMBER   PARA_AIM_NUMBER,LOGIN_NUMBER PARA_CODE4,RSP_CONTENT PARA_CODE5,TO_CHAR(START_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE6,TO_CHAR(RECEIVE_TIME,'YYYY-MM-DD HH24:MI:SS') PARA_CODE7,");
        parser
                .addSQL("TO_NUMBER(TO_CHAR(RECEIVE_TIME,'MM')) PARA_CODE8,'' PARA_CODE9,'' PARA_CODE10,'' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15,'' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20,'' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23, ");
        parser.addSQL("'' PARA_CODE24,'' PARA_CODE25,'' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30,'' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK, ");
        parser.addSQL("'' UPDATE_STAFF_ID,'' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL(" FROM TF_B_TRADELOG_SMS_");
        parser.addSQL(theMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND SERIAL_NUMBER = :VPARA_CODE1 ");//AIM_NUMBER
        parser.addSQL("  AND AIM_NUMBER = '10630444' ");
        parser.addSQL("  AND day in (to_number(SUBSTR(:VPARA_CODE2,9,10)),to_number(SUBSTR(:VPARA_CODE3,9,10))) ");
        parser.addSQL("  AND START_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND START_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        return Dao.qryByParse(parser, page, "uec");
    }

    /**
     * 功能：针对起止时间为不同月份的10086接口
     */
    public static IDataset querySms10086DiffMonth(String startDate, String endDate, String serialNumber, String theStartMonth, String theEndMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT SMS_NOTICE_ID PARA_CODE1, RECV_OBJECT PARA_CODE2, NOTICE_CONTENT PARA_CODE3, REFER_TIME PARA_CODE4, REFER_STAFF_ID PARA_CODE5, DEAL_TIME PARA_CODE6,");
        parser.addSQL("DECODE(DEAL_STATE,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') PARA_CODE7, ");
        parser
                .addSQL("MONTH PARA_CODE8, NVL(FORCE_OBJECT,'10086') PARA_CODE9, REMARK PARA_CODE10, REFER_DEPART_ID PARA_CODE11,IN_MODE_CODE PARA_CODE12,SMS_KIND_CODE PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15, '' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20, '' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23,'' PARA_CODE24,'' PARA_CODE25, '' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30, '' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK,'' UPDATE_STAFF_ID, '' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL("FROM TI_OH_SMS_");
        parser.addSQL(theStartMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND RECV_OBJECT = :VPARA_CODE1 ");
        parser.addSQL("  AND DEAL_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND DEAL_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        /**
         * REQ201611220039_短信查询界面优化
         * @author zhuoyingzhi
         * 取消原有涉及密码屏蔽整条短信模糊化规则
         */
        /*parser.addSQL(" AND NOTICE_CONTENT  NOT LIKE '%密码%' ");*/
        
        parser.addSQL("  UNION ALL ");
        parser.addSQL("SELECT SMS_NOTICE_ID PARA_CODE1, RECV_OBJECT PARA_CODE2, ");
        
        /**
         * REQ201611220039_短信查询界面优化
         * @author zhuoyingzhi
         * 替换方式修改为配置方式
         */
/*      parser.addSQL(" case when substr(NOTICE_CONTENT,1,13)='尊敬的客户，您的验证码为：' AND substr(NOTICE_CONTENT,21)='有效期10分钟。中国移动。'");
        parser.addSQL(" then '尊敬的客户，您的验证码为：******,有效期10分钟。中国移动。'");
        parser.addSQL(" else  NOTICE_CONTENT");
        parser.addSQL(" end   PARA_CODE3 , ");*/
        
        parser.addSQL(" NOTICE_CONTENT PARA_CODE3,");
        /****************end******************************/
        parser.addSQL("REFER_TIME PARA_CODE4, REFER_STAFF_ID PARA_CODE5, DEAL_TIME PARA_CODE6,");
        parser.addSQL("DECODE(DEAL_STATE,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') PARA_CODE7, ");
        parser
                .addSQL("MONTH PARA_CODE8, NVL(FORCE_OBJECT,'10086') PARA_CODE9, REMARK PARA_CODE10, REFER_DEPART_ID PARA_CODE11,IN_MODE_CODE PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15, '' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20, '' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23,'' PARA_CODE24,'' PARA_CODE25, '' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30, '' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK,'' UPDATE_STAFF_ID, '' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL("FROM TI_OH_SMS_");
        parser.addSQL(theEndMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND RECV_OBJECT = :VPARA_CODE1 ");
        parser.addSQL("  AND DEAL_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND DEAL_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        /**
         * 取消原有涉及密码屏蔽整条短信模糊化规则
         */
        /*parser.addSQL(" AND NOTICE_CONTENT  NOT LIKE '%密码%' ");*/
        
        return Dao.qryByParse(parser, page, "uec");
    }

    /**
     * 功能：针对起止时间为相同月份的10086接口
     */
    public static IDataset querySms10086SameMonth(String startDate, String endDate, String serialNumber, String theMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT SMS_NOTICE_ID PARA_CODE1, RECV_OBJECT PARA_CODE2, ");
        /**
         * REQ201611220039_短信查询界面优化
         * @author zhuoyingzhi
         * 替换方式修改为配置方式
         */
/*      parser.addSQL(" case when substr(NOTICE_CONTENT,1,13)='尊敬的客户，您的验证码为：' AND substr(NOTICE_CONTENT,21)='有效期10分钟。中国移动。'");
        parser.addSQL(" then '尊敬的客户，您的验证码为：******,有效期10分钟。中国移动。'");
        parser.addSQL(" else  NOTICE_CONTENT");
        parser.addSQL(" end   PARA_CODE3 , ");*/
        
        parser.addSQL(" NOTICE_CONTENT PARA_CODE3,");
        /****************end******************************/
        parser.addSQL(" REFER_TIME PARA_CODE4, REFER_STAFF_ID PARA_CODE5, DEAL_TIME PARA_CODE6,");
        parser.addSQL("DECODE(DEAL_STATE,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') PARA_CODE7, ");
        parser.addSQL("MONTH PARA_CODE8, NVL(FORCE_OBJECT,'10086') PARA_CODE9, REMARK PARA_CODE10, REFER_DEPART_ID PARA_CODE11,IN_MODE_CODE PARA_CODE12,SMS_KIND_CODE PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15, '' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20, '' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23,'' PARA_CODE24,'' PARA_CODE25, '' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30, '' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK,'' UPDATE_STAFF_ID, '' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL("FROM TI_OH_SMS_");
        parser.addSQL(theMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND RECV_OBJECT = :VPARA_CODE1 ");
        parser.addSQL("  AND DEAL_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND DEAL_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        /**
         * 取消原有涉及密码屏蔽整条短信模糊化规则
         */
        /*parser.addSQL(" AND NOTICE_CONTENT  NOT LIKE '%密码%' ");*/
        
        return Dao.qryByParse(parser, page, "uec");
    }

    /**
     * 功能：针对起止时间为不同月份的10088接口
     */
    public static IDataset querySms10088DiffMonth(String startDate, String endDate, String serialNumber, String theStartMonth, String theEndMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT SMS_NOTICE_ID PARA_CODE1, RECV_OBJECT PARA_CODE2, NOTICE_CONTENT PARA_CODE3, REFER_TIME PARA_CODE4, REFER_STAFF_ID PARA_CODE5, DEAL_TIME PARA_CODE6,");
        parser.addSQL("DECODE(DEAL_STATE,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') PARA_CODE7, ");
        parser
                .addSQL("MONTH PARA_CODE8, NVL(FORCE_OBJECT,'10088') PARA_CODE9, '' PARA_CODE10, '' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15, '' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20, '' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23,'' PARA_CODE24,'' PARA_CODE25, '' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30, '' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK,'' UPDATE_STAFF_ID, '' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL("FROM TI_OH_SMS_");
        parser.addSQL(theStartMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND RECV_OBJECT = :VPARA_CODE1 ");
        parser.addSQL("  AND DEAL_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND DEAL_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        parser.addSQL(" AND FORCE_OBJECT = '10088' ");
        parser.addSQL("  UNION ALL ");
        parser.addSQL("SELECT SMS_NOTICE_ID PARA_CODE1, RECV_OBJECT PARA_CODE2, NOTICE_CONTENT PARA_CODE3, REFER_TIME PARA_CODE4, REFER_STAFF_ID PARA_CODE5, DEAL_TIME PARA_CODE6,");
        parser.addSQL("DECODE(DEAL_STATE,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') PARA_CODE7, ");
        parser
                .addSQL("MONTH PARA_CODE8, NVL(FORCE_OBJECT,'10088') PARA_CODE9, '' PARA_CODE10, '' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15, '' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20, '' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23,'' PARA_CODE24,'' PARA_CODE25, '' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30, '' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK,'' UPDATE_STAFF_ID, '' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL("FROM TI_OH_SMS_");
        parser.addSQL(theEndMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND RECV_OBJECT = :VPARA_CODE1 ");
        parser.addSQL("  AND DEAL_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND DEAL_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        parser.addSQL(" AND FORCE_OBJECT = '10088' ");
        return Dao.qryByParse(parser, page, "uec");
    }

    /**
     * 功能：针对起止时间为相同月份的10088接口
     */
    public static IDataset querySms10088SameMonth(String startDate, String endDate, String serialNumber, String theMonth, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("VPARA_CODE1", serialNumber);
        params.put("VPARA_CODE2", startDate);
        params.put("VPARA_CODE3", endDate);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT SMS_NOTICE_ID PARA_CODE1, RECV_OBJECT PARA_CODE2, NOTICE_CONTENT PARA_CODE3, REFER_TIME PARA_CODE4, REFER_STAFF_ID PARA_CODE5, DEAL_TIME PARA_CODE6,");
        parser.addSQL("DECODE(DEAL_STATE,'0','未处理','1','已处理','2','超时未处理','3','超时已处理(过期标记)') PARA_CODE7, ");
        parser
                .addSQL("MONTH PARA_CODE8, NVL(FORCE_OBJECT,'10088') PARA_CODE9, '' PARA_CODE10, '' PARA_CODE11,'' PARA_CODE12,'' PARA_CODE13,'' PARA_CODE14,'' PARA_CODE15, '' PARA_CODE16,'' PARA_CODE17,'' PARA_CODE18,'' PARA_CODE19,'' PARA_CODE20, '' PARA_CODE21,'' PARA_CODE22,'' PARA_CODE23,'' PARA_CODE24,'' PARA_CODE25, '' PARA_CODE26,'' PARA_CODE27,'' PARA_CODE28,'' PARA_CODE29,'' PARA_CODE30, '' START_DATE,'' END_DATE,'' EPARCHY_CODE,'' REMARK,'' UPDATE_STAFF_ID, '' UPDATE_DEPART_ID,'' UPDATE_TIME,'' SUBSYS_CODE,0 PARAM_ATTR,'' PARAM_CODE,'' PARAM_NAME ");
        parser.addSQL("FROM TI_OH_SMS_");
        parser.addSQL(theMonth);
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND RECV_OBJECT = :VPARA_CODE1 ");
        parser.addSQL("  AND DEAL_TIME >= TO_DATE(:VPARA_CODE2,'YYYY-MM-DD') ");
        parser.addSQL(" AND DEAL_TIME <= TO_DATE(:VPARA_CODE3,'YYYY-MM-DD') + 1 ");
        parser.addSQL(" AND FORCE_OBJECT = '10088' ");
        return Dao.qryByParse(parser, page, "uec");
    }

    public QuerySmsQry()
    {
    }
}
