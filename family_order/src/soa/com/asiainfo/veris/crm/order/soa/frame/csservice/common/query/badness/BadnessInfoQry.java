
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.config.DBRouteCfg;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.DaoHelper;
import com.ailk.database.util.SQLParser;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BadnessInfoQry
{
    public static IDataset checkRedMemberIsExists(IData inparams) throws Exception
    {

        return Dao.qryByCode("HN_SMS_REDMEMBER", "SEL_BY_SN1", inparams);
    }

    public static IDataset getBadinfos(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select d.STATE DSTATE,i.STATE ISTATE,d.*,i.* from tf_f_badness_info i,TF_F_BADNESS_INO_DEAL d where 1=1");
        parser.addSQL(" and d.info_recv_id=i.info_recv_id");
        parser.addSQL(" and d.info_recv_id=:INFO_RECV_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IData getBadnessDealInfos(IData inparams) throws Exception
    {

        return Dao.qryByPK("TF_F_BADNESS_INO_DEAL", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getBadnessInfoByForm1(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM1", param, page);
    }

    public static IDataset getBadnessInfoByForm10(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM10", param, page);
    }

    public static IDataset getBadnessInfoByForm11(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM11", param, page);
    }

    public static IDataset getBadnessInfoByForm14(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM14", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getBadnessInfoByForm15(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM15", param, page);
    }

    public static IDataset getBadnessInfoByForm16(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM16", param, page);
    }

    public static IDataset getBadnessInfoByForm17(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM17", param, page);
    }

    public static IDataset getBadnessInfoByForm2(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM2", param, page);
    }

    public static IDataset getBadnessInfoByForm3(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM3", param, page);
    }

    public static IDataset getBadnessInfoByForm4(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM4", param, page);
    }

    public static IDataset getBadnessInfoByForm5(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM5", param, page);
    }

    public static IDataset getBadnessInfoByForm6(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM6", param, page);
    }

    public static IDataset getBadnessInfoByForm7(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM7", param, page);
    }

    public static IDataset getBadnessInfoByForm8(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM8", param, page);
    }

    public static IDataset getBadnessInfoByForm9(String reportType, String reportCode, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_FORM9", param, page);
    }

    public static IDataset getBadnessInfoIN(IData inparams, Pagination pagination) throws Exception
    {
        // 不良信息举报 by zhouyl 新增codecode SEL_BADNESSINFO_PRONEW 是否为校讯通号段 10658561
        return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO_INNEW", inparams, pagination, Route.CONN_CRM_CEN);
        // return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO_IN", inparams, pagination,
        // Route.CONN_CRM_CEN);
    }

    public static IDataset getBadnessInfoOUT(IData inparams, Pagination pagination) throws Exception
    {
        // 不良信息举报 by zhouyl 新增codecode SEL_BADNESSINFO_PRONEW 是否为校讯通号段 10658561
        return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO_OUTNEW", inparams, pagination, Route.CONN_CRM_CEN);
        // return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO_OUT", inparams, pagination,
        // Route.CONN_CRM_CEN);
    }

    public static IDataset getBadnessInfoPRO(IData inparams, Pagination pagination) throws Exception
    {
        // 不良信息举报 by zhouyl 新增codecode SEL_BADNESSINFO_PRONEW 是否为校讯通号段 10658561
        return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_PRONEW", inparams, pagination, Route.CONN_CRM_CEN);
        // return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_PRO", inparams, pagination,
        // Route.CONN_CRM_CEN);
    }

    public static IDataset getBadnessInfosbyCEN(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getBadnessInfosbyCENnew(IData inparams) throws Exception
    {
        // 不良信息举报 by zhouyl 新增codecode SEL_BADNESSINFO_PRONEW 是否为校讯通号段 10658561
        return Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_BADNESS_INFONEW", inparams, Route.CONN_CRM_CEN);
    }

    public static IData getBadnessInfosByRecvid(IData inparams) throws Exception
    {

        return Dao.qryByPK("TF_F_BADNESS_INFO", inparams, new String[]
        { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);
    }

    //
    // public static void insTab(String tabName, IData param) throws Exception
    // {
    // Dao.insert(tabName, param, Route.CONN_CRM_CEN);
    // }

    public static IDataset getBadnessSNbyCEN(String recvId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("INFO_RECV_ID", recvId);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESS_SN", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getDealMan(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select para_code1 from td_s_commpara where PARAM_ATTR =:PARAM_ATTR and SUBSYS_CODE=:SUBSYS_CODE ");
        parser.addSQL(" and PARAM_CODE=:PARAM_CODE and EPARCHY_CODE=:PARAM_CODE and start_date<sysdate and end_date>sysdate ");
        return Dao.qryByParse(parser);
    }

    public static IDataset getExtend(String extend, String length) throws Exception
    {
        IData param = new DataMap();
        param.put("OTHER_OPER_BEGIN_EXTEND", extend);
        param.put("MATCH_LENGTH", length);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_OTHER_OPER_EXTEND i where 1 = 1 and i.other_oper_begin_extend=:OTHER_OPER_BEGIN_EXTEND");
        parser.addSQL(" and i.match_length=:MATCH_LENGTH");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getGroupBadInfoById(String indictSeq) throws Exception
    {
        IData param = new DataMap();
        param.put("INDICT_SEQ", indictSeq);
        return Dao.qryByCode("TF_F_GROUP_BADINFO", "SEL_ALL_BY_ID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getGROUPInfo(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_GROUP_BADINFO", "SEL_DATA_TORELEASE", inparams, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询表方法
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getGroupInfosbyCEN(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_GROUP_BADINFO", "SEL_ALL_BY_ID", inparams, Route.CONN_CRM_CEN);

    }

    public static IDataset getoperatedata(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT trim(a.REPORT_SERIAL_NUMBER)      as REPORT_SERIAL_NUMBER     ,");
        parser.addSQL(" trim(b.brand)         as REPORT_BRAND_CODE        ,");
        parser.addSQL(" trim(a.REPORT_CUST_PROVINCE)      as REPORT_CUST_PROVINCE     , ");
        parser.addSQL(" trim(a.RECV_PROVINCE)             as RECV_PROVINCE            , ");
        parser.addSQL(" trim(a.EPARCHY_CODE)              as EPARCHY_CODE             , ");
        parser.addSQL(" trim(to_char(a.REPORT_TIME, 'yyyy-MM-dd HH24:MI:SS')) as REPORT_TIME,");
        parser.addSQL(" trim(a.BADNESS_INFO)              as BADNESS_INFO             , ");
        parser.addSQL(" trim(a.BADNESS_INFO_PROVINCE)     as BADNESS_INFO_PROVINCE    , ");
        parser.addSQL(" trim(a.RECV_CONTENT)              as RECV_CONTENT             , ");
        parser.addSQL(" trim(a.REPORT_TYPE_CODE)          as REPORT_TYPE_CODE         , ");
        parser.addSQL(" trim(a.RECV_IN_TYPE)              as RECV_IN_TYPE             , ");
        parser.addSQL(" trim(a.STATE)                     as STATE                    , ");
        /**
         * liuhua_20130307_HXYD-YZ-REQ-20120921-002 HXYD-YZ-REQ-20120921-002关于垃圾信息处理模块优化的需求
         */
        parser.addSQL(" trim(a.DEAL_RAMARK)               as DEAL_RAMARK              , ");
        parser.addSQL(" trim(A.SORT_RESULT_TYPE)          as SORT_RESULT_TYPE         , ");
        parser.addSQL(" trim(A.REPEAT_REPORT)             as REPEAT_REPORT            , ");
        parser.addSQL(" trim(A.RECV_STAFF_ID)             as RECV_STAFF_ID            , ");
        parser.addSQL(" trim(A.DEAL_REMARK_MAKEUP)        as DEAL_REMARK_MAKEUP       , ");
        // 要求展现举报分类
        // parser.addSQL(" trim(A.SERV_REQUEST_TYPE)         as SERV_REQUEST_TYPE        , ");
        parser.addSQL(" decode(length(a.serv_request_type), 14, substr(a.serv_request_type, 1, 12), a.serv_request_type) as SERV_REQUEST_TYPE, ");
        parser.addSQL(" trim(a.INFO_RECV_ID)               as INFO_RECV_ID            ,");
        parser.addSQL(" trim(to_char(a.RECV_TIME,'yyyy-MM-dd HH24:mm:ss')) as RECV_TIME,");
        parser.addSQL(" trim(to_char(a.FINISH_DATE,'yyyy-MM-dd HH24:mm:ss')) as FINISH_DATE,");
        // 要求展现内容分类
        parser.addSQL(" decode(length(a.serv_request_type),14,substr(a.serv_request_type, length(a.serv_request_type) - 1, 2), '') as INFO_KIND");
        parser.addSQL(" from TF_F_BADNESS_INFO  a,td_s_brand b ");
        parser.addSQL(" where a.report_brand_code = b.brand_code(+) ");
        parser.addSQL(" and report_time >= TO_DATE(:REPORT_TIME, 'yyyy-MM-dd') ");
        // 查询选择日期当天的数据 huanghao
        parser.addSQL(" and report_time <= TO_DATE(:END_REPORT_TIME, 'yyyy-MM-dd')+1 ");
        parser.addSQL(" and STATE=:STATE ");
        parser.addSQL(" and RECV_IN_TYPE=:RECV_IN_TYPE ");
        parser.addSQL(" and REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER ");
        parser.addSQL(" and REPORT_CUST_PROVIN=:REPORT_CUST_PROVIN ");
        parser.addSQL(" and EPARCHY_CODE=:EPARCHY_CODE ");
        parser.addSQL(" and REPORT_BRAND_CODE=:REPORT_BRAND_CODE ");
        parser.addSQL(" and BADNESS_INFO_PROVINCE=:BADNESS_INFO_PROVINCE ");
        if ("1".equals(param.getString("TIME_OUT", "")))
        {
            parser.addSQL(" AND round(to_number(SYSDATE-report_time)*24)>=24 ");
        }
        else
        {
            parser.addSQL(" AND round(to_number(SYSDATE-report_time)*24)<24 ");
        }
        parser.addSQL(" and DEAL_RAMARK=:DEAL_RAMARK ");
        parser.addSQL(" and RECV_STAFF_ID=:RECV_STAFF_ID ");
        parser.addSQL(" and INFO_RECV_ID=:INFO_RECV_ID");
        parser.addSQL(" and BADNESS_INFO=:BADNESS_INFO");
        parser.addSQL(" and KF_CALL_IN_SERIAL_NUMBER=:KF_CALL_IN_SERIAL_NUMBER");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getRepetition(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT round(to_number(SYSDATE-MAX(i.recv_time))*24)  AS MAX_TIME   FROM TF_F_BADNESS_INFO I ");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" and I.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
        parser.addSQL(" and I.BADNESS_INFO=:BADNESS_INFO");
        // parser.addSQL(" and I.SERV_REQUEST_TYPE=:SERV_REQUEST_TYPE");
        byte[] from_bytes = param.getString("RECV_CONTENT", "").getBytes();
        // oracle的字符串最大长度为4000
        byte[] to_bytes = new byte[4000];
        System.arraycopy(from_bytes, 0, to_bytes, 0, Math.min(4000, from_bytes.length));
        parser.addSQL(" and 0 < dbms_lob.instr('" + new String(to_bytes).trim() + "',I.RECV_CONTENT)");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /*
     * 不良信息受理查询 3月16号更新
     */
    public static IDataset getReprotinfo(String startDate, String endDate, String custProv) throws Exception
    {
        IData param = new DataMap();
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("REPORT_CUST_PROVINCE", custProv);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select * from tf_f_badness_info i where 1=1");
        parser.addSQL(" and i.report_time between to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') and to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')");
        parser.addSQL(" and i.state='02'");
        parser.addSQL(" and i.REPORT_CUST_PROVINCE=:REPORT_CUST_PROVINCE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getServCode(String extend, String length) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVCODE_BEGIN_EXTEND", extend);
        param.put("BEGIN_MATCH_LENGTH", length);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_SERVCODE_SORT_RULE e where 1 = 1 and e.servcode_begin_extend=:SERVCODE_BEGIN_EXTEND");
        parser.addSQL(" and e.begin_match_length=:BEGIN_MATCH_LENGTH ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 取得垃圾短信服务代码分拣规则信息表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getServCodeFull(String extend) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVCODE_BEGIN_EXTEND", extend);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_SERVCODE_SORT_RULE e where 1 = 1 and e.servcode_begin_extend=:SERVCODE_BEGIN_EXTEND");
        parser.addSQL(" and e.begin_match_length=e.FULL_LENGTH ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset getSmsKinds(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select para_code1 from td_s_commpara where PARAM_ATTR =:PARAM_ATTR and SUBSYS_CODE=:SUBSYS_CODE ");
        parser.addSQL(" and start_date<sysdate and end_date>sysdate ");
        return Dao.qryByParse(parser);
    }

    /**
     * 查询表方法
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @author zhuyu 2013-6-24
     */
    public static IDataset getTalbeInfosbyCEN(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_GROUP_BADINFO", "SEL_DATA_TORELEASE", inparams, Route.CONN_CRM_CEN);

    }

    public static IData qryBadInfoByPK(String tabName, String infoRecvId) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", infoRecvId);

        return Dao.qryByPK(tabName, param, Route.CONN_CRM_CEN);
    }

    public static IData qryBadnessDealInfoByPK(String recvId, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", recvId);
        param.put("STATE", state);
        return Dao.qryByPK("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryBadnessInfoLogs(String infoRecvId, String state, String recvProv, String badnessProv, String reportProv, String badnessInfo, String serialNumber, String requestType, String startTime, String endTime,
            String reportTypeCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", infoRecvId);
        param.put("STATE", state);
        param.put("RECV_PROVINCE", recvProv);
        param.put("BADNESS_INFO_PROVINCE", badnessProv);
        param.put("REPORT_CUST_PROVINCE", reportProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("SERV_REQUEST_TYPE", requestType);
        param.put("REPORT_START_TIME", startTime);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_TYPE_CODE", reportTypeCode);

        return Dao.qryByCode("TF_F_BADNESS_INFO_LOG", "SEL_BADNESS_INFOLOG", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset qryBadnessInfos(String infoRecvId, String state, String recvProv, String badnessProv, String reportProv, String badnessInfo, String serialNumber, String requestType, String startTime, String endTime, String reportTypeCode,String badnessInfoNet,
            Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", infoRecvId);
        param.put("STATE", state);
        param.put("RECV_PROVINCE", recvProv);
        param.put("BADNESS_INFO_PROVINCE", badnessProv);
        param.put("REPORT_CUST_PROVINCE", reportProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("SERV_REQUEST_TYPE", requestType);
        param.put("REPORT_START_TIME", startTime);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_TYPE_CODE", reportTypeCode);
        param.put("BADNESS_INFO_PROVINCE", badnessInfoNet);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset qryEpareyBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        // HNYD-REQ-20120510-004 不良信息举报回复内容修改 改为获取td_m_msisdn中所有数据
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.* from td_m_msisdn a");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and called_type = '1' ");
        parser.addSQL(" and :SERIAL_NUMBER between a.begin_msisdn and a.end_msisdn ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset qryEpareycode(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from RES_NUMSEG_HLR m  where 1 = 1");
        parser.addSQL(" and to_number (m.start_num)<=to_number(:SERIAL_NUMBER)");
        parser.addSQL(" and to_number(m.end_num)>=to_number(:SERIAL_NUMBER)");
        return Dao.qryByParse(parser, Route.CONN_RES);
    }

    public static IDataset qryEpareycodeout(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select b.*,a.asp from td_m_msisdn a,(select distinct prov_code,area_code from td_m_msisdn where called_type = '1') b ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and :SERIAL_NUMBER between a.begin_msisdn and a.end_msisdn ");
        parser.addSQL(" and a.asp='1' ");
        parser.addSQL(" and a.area_code = b.area_code");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset qryGroupBadInfo(String serialNumber, String state, String source, String endTime, String startTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("BLACK_STATE", state);
        param.put("SOURCE_DATA", source);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_START_TIME", startTime);

        return Dao.qryByCodeParser("TF_F_GROUP_BADINFO", "SEL_DATA_TORELEASE", param, page, Route.CONN_CRM_CEN);

    }

    public static IDataset queryUrlPara(String param_arrr,String param_code) throws Exception {
    	IData param = new DataMap();
        param.put("PARAM_ATTR", param_arrr);
        param.put("PARAM_CODE", param_code);

        SQLParser parser = new SQLParser(param);
     	parser.addSQL("select * from td_s_commpara a");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and a.param_attr= :PARAM_ATTR ");
		parser.addSQL(" and a.subsys_code='CSM' ");
		parser.addSQL(" and a.param_code= :PARAM_CODE ");
		parser.addSQL(" and end_date>sysdate ");
		
		IDataset dataset = Dao.qryByParse(parser);
		
		return dataset;
	}

    public static IDataset qryMaxTime(IData data) throws Exception
    {
//        IData param = new DataMap();
//        param.put("REPORT_SERIAL_NUMBER", reportSn);
//        param.put("BADNESS_INFO", badness);
//        param.put("SERV_REQUEST_TYPE", requestType);
//        IDataset urlset=queryUrlPara("8899", "CFJB");//获取重复举报条件配置
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT round(to_number(SYSDATE-a.maxtime)*24) as MAX_TIME  FROM ");
        parser.addSQL(" (SELECT MAX(i.recv_time) maxtime");
        parser.addSQL(" FROM TF_F_BADNESS_INFO I");
        parser.addSQL(" WHERE 1 = 1");
//        parser.addSQL(" and I.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
//        parser.addSQL(" and I.BADNESS_INFO=:BADNESS_INFO");
//        parser.addSQL(" and I.SERV_REQUEST_TYPE=:SERV_REQUEST_TYPE");
//        parser.addSQL(" )a, dual");
        
//      	if(urlset!=null&&urlset.size()>0){
      		for(int i=2;i<=30;i++){
          		String code="PARA_CODE"+i;
          		 String url = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
          				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, code, 
          				 new String[]{ "CSM", "8899", "CFJB" });
//          		String url=urlset.getData(0).getString(code);
          		if(url!=null &&!url.equals("")){         		
          		parser.addSQL(" and i."+url+"=:"+url);
          		}
          	}
      		parser.addSQL(" )a, dual");
//		}
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryFixedPhone(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TD_B_OTHER_OPER_EXTEND e ");
        parser.addSQL("where e.serial_number_type='02' ");
        parser.addSQL("and (e.other_oper_begin_extend='"+serialNumber.substring(0,3)+"' or e.other_oper_begin_extend='"+serialNumber.substring(0,4)+"')");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    public static IDataset qryProvCodeBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryReportBlack(String serialNumber) throws Exception
    {
        IData conParam = new DataMap();
        conParam.put("SERIAL_NUMBER", serialNumber);
        SQLParser parser = new SQLParser(conParam);
        parser.addSQL("select SERIAL_NUMBER,IS_BLACK from TF_F_REPORTERBALCK a ");
        parser.addSQL("where (a.SERIAL_NUMBER in (" + conParam.getString("SERIAL_NUMBER") + "))");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadHastenInfo(String infoRecvId, String state, String recvProv, String badnessProv, String reportProv, String badnessInfo, String serialNumber, String requestType, String startTime, String endTime,
            String reportTypeCode, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", infoRecvId);
        param.put("STATE", state);
        param.put("RECV_PROVINCE", recvProv);
        param.put("BADNESS_INFO_PROVINCE", badnessProv);
        param.put("REPORT_CUST_PROVINCE", reportProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("SERV_REQUEST_TYPE", requestType);
        param.put("REPORT_START_TIME", startTime);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_TYPE_CODE", reportTypeCode);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESS_HASTEN_INFO", param, page, Route.CONN_CRM_CEN);
    }
    
    public static IDataset qryBadImpeachInfo(String infoRecvId, String state, String recvProv, String badnessProv, String reportProv, String badnessInfo, String serialNumber, String requestType, String startTime, String endTime, String reportTypeCode,String badnessInfoNet,
            Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", infoRecvId);
        param.put("STATE", state);
        param.put("RECV_PROVINCE", recvProv);
        param.put("BADNESS_INFO_PROVINCE", badnessProv);
        param.put("REPORT_CUST_PROVINCE", reportProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("SERV_REQUEST_TYPE", requestType);
        param.put("REPORT_START_TIME", startTime);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_TYPE_CODE", reportTypeCode);
        param.put("BADNESS_INFO_PROVINCE", badnessInfoNet);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESS_INFO_IMPEACH", param, page, Route.CONN_CRM_CEN);
    }

    /**
     * 查询客户原子信息
     * 
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBadInfos(IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);
        parser.addSQL("SELECT A.* FROM TF_F_BADNESS_INFO A WHERE 1=1");
        parser.addSQL(" AND A.REPORT_CUST_NAME LIKE '%'||:REPORT_CUST_NAME||'%'");
        parser.addSQL(" AND A.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
        parser.addSQL(" AND A.IMPORTANT_LEVEL=:IMPORTANT_LEVEL");
        parser.addSQL(" AND A.REPORT_TYPE_CODE=:REPORT_TYPE_CODE");
        parser.addSQL(" AND A.REPORT_TIME>=TO_DATE(:START_REPORT_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.REPORT_TIME<=TO_DATE(:END_REPORT_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.SERV_REQUEST_TYPE=:SERV_REQUEST_TYPE");// 服务请求类别
        parser.addSQL(" AND A.STATE=:STATE");
        parser.addSQL(" AND (A.RECV_PROVINCE=:RECV_PROVINCE");
        parser.addSQL(" OR A.REPORT_CUST_PROVINCE=:REPORT_CUST_PROVINCE)");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessdealInfos(IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);

        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_F_BADNESS_INO_DEAL a");
        parser.addSQL(" WHERE A.INFO_RECV_ID=:INFO_RECV_ID ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByDate(String reportType, String reportCode, String state, String brandCode, String serialNumber, String badProv, String recvInType, String dealRemark, String reportEndTime, String reportStartTime,
            String custProv, String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_BRAND_CODE", brandCode);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("DEAL_REMARK", dealRemark);
        param.put("REPORT_END_TIME", reportEndTime);
        param.put("REPORT_CUST_PROVINCE", custProv);
        param.put("REPORT_START_TIME", reportStartTime);
        param.put("CONTENT_TYPE_CODE", contentType);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_DATE", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByDate10(String state, String serialNumber, String inModeCode, String badProv, String recvInType, String dealRemark, String reportEndTime, String reportStartTime, String badnessInfo, Pagination page)
            throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("DEAL_REMARK", dealRemark);
        param.put("REPORT_END_TIME", reportEndTime);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", reportStartTime);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_DATE10", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByDate3(String reportType, String reportCode, String state, String serialNumber, String inModeCode, String badProv, String recvInType, String dealRemark, String reportEndTime, String badnessInfo,
            String reportStartTime, String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("REPORT_SERIAL_NUMBER", serialNumber);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("DEAL_REMARK", dealRemark);
        param.put("REPORT_END_TIME", reportEndTime);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", reportStartTime);
        param.put("CONTENT_TYPE_CODE", contentType);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_DATE3", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByRecvId(String recvId) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", recvId);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStatic4S1(String debtBalance, String inDate, String reportSum, String flag, String reportType, String reportCode, String state, String badProv, String recvInType, String endTime, String brandCode,
            String custProv, String badnessInfo, String startTime, String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_END_TIME", endTime);
        param.put("BADNESS_INFO_BRAND_CODE", brandCode);
        param.put("REPORT_CUST_PROVINCE", custProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);
        param.put("IN_DATE", inDate);
        param.put("DEBT_BALANCE", debtBalance);

        if ("1".equals(flag))
        {
            param.put("REPORT_SUM", reportSum);
            return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS11", param, page, Route.CONN_CRM_CEN);
        }
        else
            return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS1", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStatic4S3(String flag, String reportType, String reportCode, String state, String badProv, String recvInType, String endTime, String brandCode, String custProv, String badnessInfo, String startTime,
            String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_BRAND_CODE", brandCode);
        param.put("REPORT_CUST_PROVINCE", custProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);
        if ("1".equals(flag))
            return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS33", param, page, Route.CONN_CRM_CEN);
        else
            return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS3", param, page, Route.CONN_CRM_CEN);
    }

    // ADD yangsh6

    /**
     * 查询客户原子信息
     * 
     * @param pd
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */

    public static IDataset queryBadnessInfoByStatic4SOthers(String tradeTypeCode, boolean tag, String reportType, String reportCode, String state, String badProv, String recvInType, String endTime, String custProv, String badnessInfo,
            String startTime, String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_CUST_PROVINCE", custProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);
        SQLParser parser = new SQLParser(param);
        if ("S2".equals(tradeTypeCode))// 点对点被举报情况汇总统计
        {
            if (tag)
            {
                parser.addSQL(" select v.* from(  SELECT NVL(P.BADNESS_INFO_PROVINCE, Q.BADNESS_INFO_PROVINCE) BADNESS_INFO_PROVINCE, ");
            }
            else
            {
                parser.addSQL(" SELECT NVL(P.BADNESS_INFO_PROVINCE, Q.BADNESS_INFO_PROVINCE) BADNESS_INFO_PROVINCE, ");
            }
            parser.addSQL("        NVL(P.BADNESS_INFO, Q.BADNESS_INFO) BADNESS_INFO,                            ");
            parser.addSQL("        NVL(P.IN_MODE_CODE, Q.IN_MODE_CODE) IN_MODE_CODE,                            ");
            parser.addSQL("        BADNESS_INFO_SUM,                                                            ");
            parser.addSQL("        NVL(BLACK_SUM, 0) BLACK_SUM,                                                 ");
            parser.addSQL("        BADNESS_INFO_SUM - NVL(BLACK_SUM, 0) NORMAL_SUM                              ");
            parser.addSQL("   FROM (SELECT T.BADNESS_INFO_PROVINCE,                                             ");
            parser.addSQL("                T.BADNESS_INFO,                                                      ");
            parser.addSQL("                T.IN_MODE_CODE,                                                      ");
            parser.addSQL("                SUM(1) BLACK_SUM                                                     ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                         ");
            parser.addSQL("          WHERE 1 = 1                                                                ");
            parser.addSQL("            AND EXISTS                                                               ");
            parser.addSQL("          (SELECT 1                                                                  ");
            parser.addSQL("                   FROM TL_B_BLACKUSER B                                    ");
            parser.addSQL("                  WHERE SUBSTR(B.SERIAL_NUMBER, 3, 11) =                             ");
            parser.addSQL("                        T.REPORT_SERIAL_NUMBER                                       ");
            parser.addSQL("                    AND B.EFFECT_TAG = '1'                                           ");
            parser.addSQL("                    AND (SYSDATE BETWEEN B.BEGIN_DATE AND B.END_DATE))               ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR  :REPORT_TYPE_CODE IS NULL)                                          ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR :REPORT_CODE IS NULL)                                               ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 9, 2) = SUBSTR(:CONTENT_TYPE_CODE, 3, 2) OR :CONTENT_TYPE_CODE IS NULL)  ");
            parser.addSQL("            AND (T.IN_MODE_CODE = :IN_MODE_CODE OR :IN_MODE_CODE IS NULL)          ");
            parser.addSQL("            AND (T.BADNESS_INFO_PROVINCE = :BADNESS_INFO_PROVINCE OR   :BADNESS_INFO_PROVINCE IS NULL)                                     ");
            parser.addSQL("            AND (T.BADNESS_INFO = :BADNESS_INFO OR :BADNESS_INFO IS NULL)          ");
            parser.addSQL("            AND (TRUNC(T.REPORT_TIME) >=                                             ");
            parser.addSQL("                TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR                        ");
            parser.addSQL("                :REPORT_START_TIME IS NULL)                                         ");
            parser.addSQL("            AND (TRUNC(T.REPORT_TIME) <=                                             ");
            parser.addSQL("                TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR                          ");
            parser.addSQL("                :REPORT_END_TIME IS NULL)                                           ");
            parser.addSQL("            AND (T.STATE = :STATE OR :STATE IS NULL)                               ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE, T.BADNESS_INFO, T.IN_MODE_CODE) P        ");
            parser.addSQL("   FULL OUTER JOIN (SELECT S.BADNESS_INFO_PROVINCE,                                  ");
            parser.addSQL("                           S.BADNESS_INFO,                                           ");
            parser.addSQL("                           S.IN_MODE_CODE,                                           ");
            parser.addSQL("                           SUM(1) BADNESS_INFO_SUM                                   ");
            parser.addSQL("                      FROM TF_F_BADNESS_INFO S                                       ");
            parser.addSQL("                     WHERE 1 = 1                                                     ");
            parser.addSQL("                       AND (SUBSTR(S.SORT_RESULT_TYPE, 5, 2) =  :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)         ");
            parser.addSQL("                       AND (SUBSTR(S.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR  :REPORT_CODE IS NULL)                                    ");
            parser.addSQL("                       AND (SUBSTR(S.SORT_RESULT_TYPE, 9, 2) = SUBSTR(:CONTENT_TYPE_CODE, 3, 2) OR  :CONTENT_TYPE_CODE IS NULL)  ");
            parser.addSQL("                       AND (S.IN_MODE_CODE = :IN_MODE_CODE OR :IN_MODE_CODE IS NULL) ");
            parser.addSQL("                       AND (S.BADNESS_INFO_PROVINCE = :BADNESS_INFO_PROVINCE OR     ");
            parser.addSQL("                           :BADNESS_INFO_PROVINCE IS NULL)                          ");
            parser.addSQL("                       AND (S.BADNESS_INFO = :BADNESS_INFO OR                       ");
            parser.addSQL("                           :BADNESS_INFO IS NULL)                                   ");
            parser.addSQL("                       AND (S.STATE = :STATE OR :STATE IS NULL)                    ");
            parser.addSQL("                       AND (TRUNC(S.REPORT_TIME) >=                                  ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                              ");
            parser.addSQL("                       AND (TRUNC(S.REPORT_TIME) <=                                  ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR               ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                                ");
            parser.addSQL("                     GROUP BY S.BADNESS_INFO_PROVINCE,                               ");
            parser.addSQL("                              S.BADNESS_INFO,                                        ");
            parser.addSQL("                              S.IN_MODE_CODE) Q ON P.BADNESS_INFO_PROVINCE =         ");
            parser.addSQL("                                                   Q.BADNESS_INFO_PROVINCE           ");
            parser.addSQL("                                               AND P.BADNESS_INFO =                  ");
            parser.addSQL("                                                   Q.BADNESS_INFO                    ");
            parser.addSQL("                                               AND P.IN_MODE_CODE =                  ");
            parser.addSQL("                                                   Q.IN_MODE_CODE                    ");
        }
        else if ("S6".equals(tradeTypeCode) || "S7".equals(tradeTypeCode) || "S8".equals(tradeTypeCode) || "S9".equals(tradeTypeCode) || "S10".equals(tradeTypeCode))
        {
            parser.addSQL(" SELECT BADNESS_INFO_PROVINCE,                                                     ");
            parser.addSQL("        SUM(A)A,                                                                   ");
            parser.addSQL("        SUM(B)B,                                                                   ");
            parser.addSQL("        SUM(C)C,                                                                   ");
            parser.addSQL("        SUM(D)D,                                                                   ");
            parser.addSQL("        SUM(E)E,                                                                   ");
            parser.addSQL("        SUM(F)F,                                                                   ");
            parser.addSQL("        SUM(G)G,                                                                   ");
            parser.addSQL("        SUM(H)H,                                                                   ");
            parser.addSQL("        SUM(I)I,                                                                   ");
            parser.addSQL("        SUM(J)J                                                                    ");
            parser.addSQL("   FROM (SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                COUNT(DISTINCT(T.BADNESS_INFO)) A,                                 ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE T.RECV_PROVINCE = '898'                                            ");
            parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                COUNT(DISTINCT(T.REPORT_SERIAL_NUMBER)) B,                         ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE 1 = 1                                                              ");
            parser.addSQL("            AND T.RECV_PROVINCE = '898'                                            ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =  :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) C,                                   ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE T.RECV_IN_TYPE = '02'                                              ");
            parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =:REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) D,                                   ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE T.RECV_IN_TYPE = '02'                                              ");
            parser.addSQL("            AND T.STATE IN ('02', '03', '0A', '0B')                                ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                SUM(1) E,                                                          ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE T.TARGET_PROVINCE != '999'                                         ");
            parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                SUM(1) F,                                                          ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE T.TARGET_PROVINCE = '999'                                          ");
            parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) G,                                   ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE 1 = 1                                                              ");
            parser.addSQL("            AND (T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE = '898')            ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) H,                                   ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE 1 = 1                                                              ");
            parser.addSQL("            AND ((T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE != '898') OR       ");
            parser.addSQL("                (T.RECV_PROVINCE != '898' AND T.TARGET_PROVINCE = '898'))          ");
            parser.addSQL("                AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =  :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                COUNT(DISTINCT(T.REPORT_SERIAL_NUMBER)) I,                         ");
            parser.addSQL("                0 J                                                                ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE 1 = 1                                                              ");
            parser.addSQL("            AND (T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE = '898')            ");
            parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =:REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
            parser.addSQL("         UNION ALL                                                                 ");
            parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
            parser.addSQL("                0 A,                                                               ");
            parser.addSQL("                0 B,                                                               ");
            parser.addSQL("                0 C,                                                               ");
            parser.addSQL("                0 D,                                                               ");
            parser.addSQL("                0 E,                                                               ");
            parser.addSQL("                0 F,                                                               ");
            parser.addSQL("                0 G,                                                               ");
            parser.addSQL("                0 H,                                                               ");
            parser.addSQL("                0 I,                                                               ");
            parser.addSQL("                COUNT(DISTINCT(T.REPORT_SERIAL_NUMBER)) J                          ");
            parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
            parser.addSQL("          WHERE 1 = 1                                                              ");
            parser.addSQL("            AND ((T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE != '898') OR       ");
            parser.addSQL("                (T.RECV_PROVINCE != '898' AND T.TARGET_PROVINCE = '898'))          ");
            parser.addSQL("                AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
            parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
            parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
            parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
            parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
            parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
            parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
            parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE) A                                      ");
            parser.addSQL("  GROUP BY A.BADNESS_INFO_PROVINCE                                                 ");
            parser.addSQL("  ORDER BY A.BADNESS_INFO_PROVINCE                                                 ");
        }
        else
            return null;
        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStaticS2(String reportType, String reportCode, String state, String badProv, String recvInType, String endTime, String custProv, String badnessInfo, String startTime, String contentType, Pagination page)
            throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_CUST_PROVINCE", badnessInfo);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);
        // return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS2_NEW", param, page);
        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS2", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStaticS3(String reportType, String reportCode, String state, String badProv, String recvInType, String endTime, String brandCode, String custProv, String badnessInfo, String startTime, String contentType,
            Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_BRAND_CODE", brandCode);
        param.put("REPORT_CUST_PROVINCE", badnessInfo);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS3", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStaticS7(String reportType, String reportCode, String state, String badProv, String recvInType, String endTime, String custProv, String badnessInfo, String startTime, String contentType, Pagination page)
            throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_CUST_PROVINCE", badnessInfo);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT BADNESS_INFO_PROVINCE,                                                     ");
        parser.addSQL("        SUM(A)A,                                                                   ");
        parser.addSQL("        SUM(B)B,                                                                   ");
        parser.addSQL("        SUM(C)C,                                                                   ");
        parser.addSQL("        SUM(D)D,                                                                   ");
        parser.addSQL("        SUM(E)E,                                                                   ");
        parser.addSQL("        SUM(F)F,                                                                   ");
        parser.addSQL("        SUM(G)G,                                                                   ");
        parser.addSQL("        SUM(H)H,                                                                   ");
        parser.addSQL("        SUM(I)I,                                                                   ");
        parser.addSQL("        SUM(J)J                                                                    ");
        parser.addSQL("   FROM (SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                COUNT(DISTINCT(T.BADNESS_INFO)) A,                                 ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE T.RECV_PROVINCE = '898'                                            ");
        parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                COUNT(DISTINCT(T.REPORT_SERIAL_NUMBER)) B,                         ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE 1 = 1                                                              ");
        parser.addSQL("            AND T.RECV_PROVINCE = '898'                                            ");
        parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =  :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) C,                                   ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE T.RECV_IN_TYPE = '02'                                              ");
        parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =:REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) D,                                   ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE T.RECV_IN_TYPE = '02'                                              ");
        parser.addSQL("            AND T.STATE IN ('02', '03', '0A', '0B')                                ");
        parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                SUM(1) E,                                                          ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE T.TARGET_PROVINCE != '999'                                         ");
        parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                SUM(1) F,                                                          ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE T.TARGET_PROVINCE = '999'                                          ");
        parser.addSQL("          AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) G,                                   ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE 1 = 1                                                              ");
        parser.addSQL("            AND (T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE = '898')            ");
        parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                COUNT(T.REPORT_SERIAL_NUMBER) H,                                   ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE 1 = 1                                                              ");
        parser.addSQL("            AND ((T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE != '898') OR       ");
        parser.addSQL("                (T.RECV_PROVINCE != '898' AND T.TARGET_PROVINCE = '898'))          ");
        parser.addSQL("                AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =  :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                COUNT(DISTINCT(T.REPORT_SERIAL_NUMBER)) I,                         ");
        parser.addSQL("                0 J                                                                ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE 1 = 1                                                              ");
        parser.addSQL("            AND (T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE = '898')            ");
        parser.addSQL("            AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) =:REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE                                         ");
        parser.addSQL("         UNION ALL                                                                 ");
        parser.addSQL("         SELECT T.BADNESS_INFO_PROVINCE,                                           ");
        parser.addSQL("                0 A,                                                               ");
        parser.addSQL("                0 B,                                                               ");
        parser.addSQL("                0 C,                                                               ");
        parser.addSQL("                0 D,                                                               ");
        parser.addSQL("                0 E,                                                               ");
        parser.addSQL("                0 F,                                                               ");
        parser.addSQL("                0 G,                                                               ");
        parser.addSQL("                0 H,                                                               ");
        parser.addSQL("                0 I,                                                               ");
        parser.addSQL("                COUNT(DISTINCT(T.REPORT_SERIAL_NUMBER)) J                          ");
        parser.addSQL("           FROM TF_F_BADNESS_INFO T                                                ");
        parser.addSQL("          WHERE 1 = 1                                                              ");
        parser.addSQL("            AND ((T.RECV_PROVINCE = '898' AND T.TARGET_PROVINCE != '898') OR       ");
        parser.addSQL("                (T.RECV_PROVINCE != '898' AND T.TARGET_PROVINCE = '898'))          ");
        parser.addSQL("                AND (SUBSTR(T.SORT_RESULT_TYPE, 5, 2) = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)       ");
        parser.addSQL("                       AND (SUBSTR(T.SORT_RESULT_TYPE, 7, 2) = :REPORT_CODE OR    ");
        parser.addSQL("                           :REPORT_CODE IS NULL)                                  ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) >=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_START_TIME, 'yyyy-mm-dd') OR           ");
        parser.addSQL("                           :REPORT_START_TIME IS NULL)                            ");
        parser.addSQL("                       AND (trunc(T.REPORT_TIME) <=                                       ");
        parser.addSQL("                           TO_DATE(:REPORT_END_TIME, 'yyyy-mm-dd') OR             ");
        parser.addSQL("                           :REPORT_END_TIME IS NULL)                              ");
        parser.addSQL("          GROUP BY T.BADNESS_INFO_PROVINCE) A                                      ");
        parser.addSQL("  GROUP BY A.BADNESS_INFO_PROVINCE                                                 ");
        parser.addSQL("  ORDER BY A.BADNESS_INFO_PROVINCE                                                 ");
        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStaticSumS2(String reportType, String reportCode, String state, String badProv, String recvInType, String reportSum, String endTime, String custProv, String badnessInfo, String startTime,
            String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_SUM", reportSum);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_CUST_PROVINCE", custProv);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS22", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfoByStaticSumS3(String reportType, String reportCode, String state, String badProv, String recvInType, String reportSum, String endTime, String brandCode, String custProv, String badnessInfo,
            String startTime, String contentType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("REPORT_TYPE_CODE", reportType);
        param.put("REPORT_CODE", reportCode);
        param.put("STATE", state);
        param.put("BADNESS_INFO_PROVINCE", badProv);
        param.put("RECV_IN_TYPE", recvInType);
        param.put("REPORT_SUM", reportSum);
        param.put("REPORT_END_TIME", endTime);
        param.put("REPORT_BRAND_CODE", brandCode);
        param.put("REPORT_CUST_PROVINCE", badnessInfo);
        param.put("BADNESS_INFO", badnessInfo);
        param.put("REPORT_START_TIME", startTime);
        param.put("CONTENT_TYPE_CODE", contentType);

        return Dao.qryByCode("TF_F_BADNESS_INFO", "SEL_BADNESSINFO_BY_STATICS33", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfos(IData cond, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(cond);

        parser.addSQL(" SELECT DECODE(a.state,'01','未处理','02','已处理','0A','已归档','0B','已归档','0T','已归档','处理中') C_STATE ,A.*,");
        parser
                .addSQL(" B.DEAL_RAMARK DEAL_RAMARK_B,B.DEAL_REMARK_MAKEUP DEAL_REMARK_MAKEUP_B,B.SERV_REQUEST_TYPE SERV_REQUEST_TYPE_B,B.CONTENT_TYPE,B.REPORT_DEGREE,B.USER_REPORT_DEGREE,B.PRODUCT_TYPE,B.BOSS_TYPE,B.REALNAME_TAG,B.CREDIT_VALUES,B.FEE,B.USER_STATE,B.BRAND_CODE,B.SUB_FEE,B.JION_TIME,B.STOP_TIME,B.LEN_TIME ");
        parser.addSQL(" FROM  TF_F_BADNESS_INFO A,TF_F_BADNESS_INO_DEAL B ");
        parser.addSQL(" WHERE 1=1 AND A.INFO_RECV_ID=B.INFO_RECV_ID(+) ");
        parser.addSQL(" AND A.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
        parser.addSQL(" AND A.REPORT_TIME>=TO_DATE(:START_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.REPORT_TIME<=TO_DATE(:END_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.BADNESS_INFO=:BADNESS_INFO ");
        parser.addSQL(" AND A.RSRV_STR1 =:BADNESS_CMCC_TAG ");
        parser.addSQL(" AND  DECODE(a.state,'01','01','02','02','0A','04','0B','04','0T','04','03')=:BADNESS_STATE ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBadnessInfosEX(IData cond) throws Exception
    {

        SQLParser parser = new SQLParser(cond);

        parser.addSQL(" SELECT DECODE(a.state,'01','未处理','02','已处理','0A','已归档','0B','已归档','0T','已归档','处理中') C_STATE ,A.*,");
        parser
                .addSQL(" B.DEAL_RAMARK DEAL_RAMARK_B,B.DEAL_REMARK_MAKEUP DEAL_REMARK_MAKEUP_B,B.SERV_REQUEST_TYPE SERV_REQUEST_TYPE_B,B.CONTENT_TYPE,B.REPORT_DEGREE,B.USER_REPORT_DEGREE,B.PRODUCT_TYPE,B.BOSS_TYPE,B.REALNAME_TAG,B.CREDIT_VALUES,B.FEE,B.USER_STATE,B.BRAND_CODE,B.SUB_FEE,B.JION_TIME,B.STOP_TIME,B.LEN_TIME ");
        parser.addSQL(" FROM  TF_F_BADNESS_INFO A,TF_F_BADNESS_INO_DEAL B ");
        parser.addSQL(" WHERE 1=1 AND A.INFO_RECV_ID=B.INFO_RECV_ID(+) ");
        parser.addSQL(" AND A.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
        parser.addSQL(" AND A.REPORT_TIME>=TO_DATE(:START_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.REPORT_TIME<=TO_DATE(:END_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.BADNESS_INFO=:BADNESS_INFO ");
        parser.addSQL(" AND A.RSRV_STR1 =:BADNESS_CMCC_TAG ");
        parser.addSQL(" AND  DECODE(a.state,'01','01','02','02','0A','04','0B','04','0T','04','03')=:BADNESS_STATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /*
     * 黑白名单 3月22号 新增
     */
    public static IDataset queryBlackWhiteInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("  SELECT SERIAL_NUMBER, BLACK_WHITE_TYPE, STATE, START_DATE, END_DATE ");
        parser.addSQL(" FROM TF_F_BAD_BLACK_WHITE T ");
        parser.addSQL(" WHERE (:SERIAL_NUMBER IS NULL OR T.SERIAL_NUMBER = :SERIAL_NUMBER) ");
        parser.addSQL(" AND (:VBLACK_WHITE_TYPE IS NULL OR T.BLACK_WHITE_TYPE = :BLACK_WHITE_TYPE) ");
        parser.addSQL(" AND (:VSTATE IS NULL OR T.STATE = :VSTATE) ");
        parser.addSQL(" ORDER BY BLACK_WHITE_TYPE, STATE");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset queryBrandInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select decode(para_code1,0,1,2,3,3,2,2) BRAND_CODE from td_s_commpara s  where s.param_attr='998' ");
        parser.addSQL(" AND param_code=:BRAND_CODE　");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryEpareycodeout(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select b.* from td_m_msisdn a,(select distinct prov_code,area_code from td_m_msisdn where called_type = '01') b ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and :SERIAL_NUMBER between a.begin_msisdn and a.end_msisdn ");
        parser.addSQL(" and a.asp='1' ");
        parser.addSQL(" and a.area_code = b.area_code union all ");
        parser.addSQL(" SELECT decode(t.home_type,'1','731',t.home_type)prov_code,substr(t.other_party,0,4) area_code FROM TD_FIX_TELEPHONE T ");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.ASP = '5' ");
        parser.addSQL(" AND :SERIAL_NUMBER BETWEEN T.OTHER_PARTY || '0000' AND T.OTHER_PARTY || '9999' ");
        parser.addSQL(" AND SYSDATE BETWEEN TO_DATE(T.BEGIN_TIME, 'yyyymmddhh24miss') AND TO_DATE(T.END_TIME, 'yyyymmddhh24miss') ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

    }

    public static IDataset queryRepeatReportInWeek(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT info_recv_id, report_serial_number, report_cust_name, serv_request_type, recv_province, report_time,");
        parser.addSQL(" badness_info, stick_list, badness_info_province, report_type_code, dbms_lob.instr(recv_content,'" + new String() + "') FROM tf_f_badness_info t　");
        parser.addSQL(" where info_recv_id=:INFO_RECV_ID");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT info_recv_id, report_serial_number, report_cust_name, serv_request_type, recv_province, report_time,");
        parser.addSQL(" badness_info, stick_list, badness_info_province, report_type_code, dbms_lob.instr(recv_content,'" + new String() + "') FROM tf_f_badness_info t　");
        parser.addSQL(" where badness_info=(SELECT BADNESS_INFO FROM tf_f_badness_info WHERE info_recv_id=:INFO_RECV_ID) and (sysdate-report_time)<7");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查询可归档的不良信息
     * 
     * @param pd
     * @param cond
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryReturnBadInfos(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.* FROM TF_F_BADNESS_INFO A WHERE 1=1");
        parser.addSQL(" AND A.REPORT_CUST_NAME LIKE '%'||:REPORT_CUST_NAME||'%'");
        parser.addSQL(" AND A.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
        parser.addSQL(" AND A.IMPORTANT_LEVEL=:IMPORTANT_LEVEL");
        parser.addSQL(" AND A.REPORT_TYPE_CODE=:REPORT_TYPE_CODE");
        parser.addSQL(" AND A.REPORT_TIME>=TO_DATE(:START_REPORT_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.REPORT_TIME<TO_DATE(:END_REPORT_TIME,'yyyy-MM-dd HH24:MI:SS') + 1");
        parser.addSQL(" AND A.SERV_REQUEST_TYPE=:SERV_REQUEST_TYPE");// 服务请求类别
        parser.addSQL(" AND A.STATE=:STATE");
        parser.addSQL(" AND A.BADNESS_INFO_PROVINCE=:BADNESS_INFO_PROVINCE");

        parser.addSQL(" AND A.BADNESS_INFO=:BADNESS_INFO");// 服务请求类别
        parser.addSQL(" AND A.RECV_IN_TYPE=:RECV_IN_TYPE");
        parser.addSQL(" AND round(to_number(SYSDATE-A.report_time)*24)<=24");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 查询可归档的不良信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryReturnBadInfosOut(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.* FROM TF_F_BADNESS_INFO A WHERE 1=1");
        parser.addSQL(" AND A.REPORT_CUST_NAME LIKE '%'||:REPORT_CUST_NAME||'%'");
        parser.addSQL(" AND A.REPORT_SERIAL_NUMBER=:REPORT_SERIAL_NUMBER");
        parser.addSQL(" AND A.IMPORTANT_LEVEL=:IMPORTANT_LEVEL");
        parser.addSQL(" AND A.REPORT_TYPE_CODE=:REPORT_TYPE_CODE");
        parser.addSQL(" AND A.REPORT_TIME>=TO_DATE(:START_REPORT_TIME,'yyyy-MM-dd HH24:MI:SS')");
        parser.addSQL(" AND A.REPORT_TIME<TO_DATE(:END_REPORT_TIME,'yyyy-MM-dd HH24:MI:SS') + 1");
        parser.addSQL(" AND A.SERV_REQUEST_TYPE=:SERV_REQUEST_TYPE");// 服务请求类别
        parser.addSQL(" AND A.STATE=:STATE");
        parser.addSQL(" AND A.BADNESS_INFO_PROVINCE=:BADNESS_INFO_PROVINCE");

        parser.addSQL(" AND A.BADNESS_INFO=:BADNESS_INFO");// 服务请求类别
        parser.addSQL(" AND A.RECV_IN_TYPE=:RECV_IN_TYPE");
        parser.addSQL(" AND round(to_number(SYSDATE-A.report_time)*24)>24");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

    public IDataset queryRepeatReportInWeek1(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT info_recv_id, report_serial_number, report_cust_name, serv_request_type, recv_province, report_time,");
        parser.addSQL(" badness_info, stick_list, badness_info_province, report_type_code, recv_content FROM tf_f_badness_info t　");
        parser.addSQL(" where info_recv_id=?");
        parser.addSQL(" UNION all");
        parser.addSQL(" SELECT info_recv_id, report_serial_number, report_cust_name, serv_request_type, recv_province, report_time,");
        parser.addSQL(" badness_info, stick_list, badness_info_province, report_type_code, recv_content FROM tf_f_badness_info t　");
        parser.addSQL(" where badness_info=(SELECT BADNESS_INFO FROM tf_f_badness_info WHERE info_recv_id=?) and (sysdate-report_time)<7");

        // return Dao.qryByParse(parser, ConnMgr.CONN_CRM_CEN);

        String route = DBRouteCfg.getRoute(DBRouteCfg.getGroup(CSBizBean.getVisit().getSubSysCode()), Route.CONN_CRM_CEN);
        DBConnection conn = SessionManager.getInstance().getSessionConnection(route);

        PreparedStatement stmt = conn.prepareStatement(parser.toString());
        stmt.setString(1, param.getString("INFO_RECV_ID"));
        stmt.setString(2, param.getString("INFO_RECV_ID"));
        ResultSet rs = stmt.executeQuery();
        IDataset ds = DaoHelper.rssetToDataset(rs, 100);
        stmt.close();
        conn.commit(); // 若不提交将自动回滚

        return ds;
    }
    
    public static IDataset queryAccessoryLists(String infoRecvId) throws Exception
    {
        IData param = new DataMap();
        param.put("INFO_RECV_ID", infoRecvId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT INFO_RECV_ID, STICK_LIST, RSRV_STR4, RECV_PROVINCE,SERV_REQUEST_TYPE FROM tf_f_badness_info WHERE INFO_RECV_ID= :INFO_RECV_ID　");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
