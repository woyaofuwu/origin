
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class SpamDealQry
{
    public static void addWasteWord(IData param) throws Exception
    {
        Dao.insert("TD_B_WASTEWORD", param, Route.CONN_CRM_CEN);
    }

    public static void cancelBlackTrashMMS(String serialNumber, String staffId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("STAFF_ID", staffId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("UPDATE TF_O_TRASHMSGUSER　");
        parser.addSQL(" SET STATE_CODE = '1', ");
        parser.addSQL(" END_DATE = SYSDATE ,");
        parser.addSQL(" RSRV_STR2 = :STAFF_ID ");
        parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND CUST_TYPE = '1' ");
        parser.addSQL(" AND RSRV_STR3 = '02' ");// 停开机
        parser.addSQL(" AND STATE_CODE = '0' ");
        parser.addSQL(" AND sysdate BETWEEN start_date and end_date ");

        Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }

    public static void cancelBlackTrashMMSGPRS(String serialNumber, String staffId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("STAFF_ID", staffId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("UPDATE TF_O_TRASHMSGUSER　");
        parser.addSQL(" SET STATE_CODE = '1', ");
        parser.addSQL(" END_DATE = SYSDATE, ");
        // parser.addSQL(" UPDATE_TIME = SYSDATE, ");
        // parser.addSQL(" REMOVE_SOURCE_TYPE = '" +pd.getData().getString("SOURCE_DATA","03")+ "', ");//解黑来源 03-用户投诉
        // parser.addSQL(" REMOVE_TYPE = '" +pd.getData().getString("HANDLING_TYPE", "0203")+ "', ");//解黑类型
        // parser.addSQL(" REMOVE_RESULT = '" +pd.getData().getString("REMARK","")+ "', ");//解黑原因
        parser.addSQL(" RSRV_STR2 = :STAFF_ID ");
        parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND CUST_TYPE = '1' ");
        parser.addSQL(" AND RSRV_STR3 = '03' ");// 停开GPRS
        parser.addSQL(" AND ID_TYPE = '03' ");// 垃圾彩信
        parser.addSQL(" AND STATE_CODE = '0' ");
        parser.addSQL(" AND sysdate BETWEEN start_date and end_date ");

        Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }

    public static boolean deleteTrashMsgUser(IData data, String routeId) throws Exception
    {
        return Dao.delete("TF_O_TRASHMSGUSER", data, new String[]
        { "SERIAL_NUMBER", "ID_TYPE", "CUST_TYPE" }, routeId);
    }

    public static void delWasteWord(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TD_B_WASTEWORD", "DELELE_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getBiAoMons(String serial_number, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);
        return Dao.qryByCode("TI_BI_AO_MON", "SEL_BY_SN_AO", param, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getBiCommMon(String serial_number, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);
        return Dao.qryByCode("TI_BI_AO_MON", "SEL_BY_SN_COMM", param, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset getBiOwnsrvMons(String serial_number, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);
        return Dao.qryByCode("TI_BI_AO_MON", "SEL_BY_SN_OWNSRV", param, pagination);
    }

    public static String getIsBlackValue(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser sql = new SQLParser(param);
        sql.addSQL("select * from TF_O_TRASHMSGUSER where 1=1");
        sql.addSQL(" and serial_number = :SERIAL_NUMBER");
        sql.addSQL(" and cust_type = '1'");
        sql.addSQL(" and state_code = '0'");
        sql.addSQL(" and rownum < 2");
        IDataset dataset = Dao.qryByParse(sql, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? "1" : "0";// 0:不是黑名单 1:是黑名单
    }

    public static IDataset getParserDataInfo(String tabName, String sqlRef, IData param, String routeId) throws Exception
    {
        IDataset result = Dao.qryByCodeParser(tabName, sqlRef, param, routeId);
        return result;
    }

    public static IDataset getParserDataInfoPage(String tabName, String sqlRef, IData param, Pagination page, String routeId) throws Exception
    {
        IDataset result = Dao.qryByCodeParser(tabName, sqlRef, param, page, routeId);
        return result;
    }

    public static IDataset getTrashmsgUser(String serialNumber, String id_type, String cust_type, String state_code, String start_time, String end_time, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("ID_TYPE", id_type);
        param.put("CUST_TYPE", cust_type);
        param.put("STATE_CODE", state_code);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);
        return Dao.qryByCode("TI_BI_AO_MON", "SEL_BY_ALL", param, pagination);

    }

    public static IDataset getUserBiMoMons(String serial_number, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);
        return Dao.qryByCode("TI_BI_AO_MON", "SEL_BY_SN_MO_1", param, pagination);
    }

    public static IDataset getUserBiRolist(String serial_number, String start_time, String end_time, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("START_TIME", start_time);
        param.put("END_TIME", end_time);
        return Dao.qryByCode("TI_BI_AO_MON", "SEL_BY_SN", param, pagination);
    }

    public static IData getUserInfoData(String serialNumber, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT a.USER_ID,a.SERIAL_NUMBER,b.PRODUCT_ID TCTYPE,b.BRAND_CODE PPTYPE, a.IN_DATE RWSJ,a.LAST_STOP_TIME TJSJ,");
        sql.addSQL(" DECODE(a.DESTROY_TIME, NULL,ROUND(MONTHS_BETWEEN(SYSDATE, a.IN_DATE) * 365 / 12),");
        sql.addSQL(" ROUND(MONTHS_BETWEEN(a.DESTROY_TIME, a.IN_DATE) * 365 / 12)) AS ZWSJ, ");
        sql.addSQL(" a.USER_STATE_CODESET RSRV_STR4  FROM TF_F_USER a ,TF_F_USER_PRODUCT b ");
        sql.addSQL(" WHERE a.user_id = b.user_id AND b.main_tag = '1'");
        sql.addSQL(" and serial_number = :SERIAL_NUMBER");
        sql.addSQL(" and a.partition_id = MOD(to_number(a.user_id), 10000)");
        sql.addSQL(" and b.partition_id = MOD(to_number(b.user_id), 10000)");
        sql.addSQL(" order by rwsj desc");
        IDataset dataset = Dao.qryByParse(sql, routeId);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

    /**
     * 根据手机号码获取携入地路由
     * 
     * @param pd
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getUserNetnpInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("STATE", "0");
        IDataset res = Dao.qryByCode("TF_F_USER_NETNP", "SEL_BY_NETNP_NUM", param, Route.CONN_CRM_CEN);
        return res;

    }

    /**
     * 获取用户服务状态
     * 
     * @param pd
     * @param params
     *            查询所需参数
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getUserSvcState(String userId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser sp = new SQLParser(param);
        sp.addSQL(" SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,state_code");
        sp.addSQL(" FROM tf_f_user_svcstate");
        sp.addSQL(" WHERE user_id = TO_NUMBER(:USER_ID)");
        sp.addSQL(" AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
        sp.addSQL(" AND (main_tag='1' or service_id in (98, 99))");
        sp.addSQL(" AND end_date > SYSDATE");
        sp.addSQL(" union all");
        sp.addSQL("	SELECT partition_id,to_char(user_id) user_id,service_id,main_tag,'0' ");
        sp.addSQL(" FROM tf_f_user_svc a ");
        sp.addSQL(" WHERE not exists (select 1 from tf_f_user_svcstate b where a.user_id = b.user_id and b.partition_id = a.partition_id and a.service_id = b.service_id)");
        sp.addSQL(" AND user_id = TO_NUMBER(:USER_ID)");
        sp.addSQL(" AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
        sp.addSQL(" AND service_id in (98, 99)");
        sp.addSQL(" AND end_date > SYSDATE");

        return Dao.qryByParse(sp);
    }

    /**
     * 获取大客户信息
     */
    public static IDataset getVipInfoBySn(String serialNumber, String removeTag) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("REMOVE_TAG", removeTag);
        IDataset datasetVIP = Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_INTF", inparam);
        return datasetVIP;
    }

    public static boolean insertTrashMsgUser(IData data, String routeId) throws Exception
    {
        return Dao.insert("TF_O_TRASHMSGUSER", data, routeId);
    }

    // 查询对比结果
    public static IDataset parallelInfo(String fileName) throws Exception
    {
        String routeId = Route.CONN_CRM_CEN;
        IData qurData = new DataMap();
        qurData.put("FILENAME", fileName);

        IDataset parallelInfoLists = Dao.qryByCode("TI_BI_10086999DIRTY", "SEL_BY_FILENAME", qurData, routeId);
        return parallelInfoLists;
    }

    public static IDataset queryBiRolistBySerialNumber(String serialNumber, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset res = Dao.qryByCode("TI_BI_10086999DIRTY", "SEL_BY_SERIAL_NUMBER_TO_RL", param, routeId);
        return res;
    }

    /**
     * 查询关停GPRS
     * 
     * @param pd
     * @param param
     * @param pag
     * @return
     * @throws Exception
     */
    public static IDataset queryCloseGPRS(String serialNumber, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT t.* ");
        sql.addSQL("FROM TF_O_TRASHMSGUSER t ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND t.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.addSQL("AND t.ID_TYPE = '03' ");
        sql.addSQL("AND t.CUST_TYPE = '1' ");
        sql.addSQL("AND t.RSRV_STR3 = '03' ");// 02:停开机(语音) 03:停开GPRS
        sql.addSQL("AND sysdate BETWEEN t.START_DATE AND t.END_DATE ");
        sql.addSQL("order by t.START_DATE desc ");
        return Dao.qryByParse(sql, page);
    }

    public static IDataset queryDirtyInfoByFileName(String fileName) throws Exception
    {
        IData param = new DataMap();
        param.put("FILENAME", fileName);
        IDataset res = Dao.qryByCode("TI_BI_10086999DIRTY", "SEL_BY_FILENAME_AND_CMPFLAG", param, Route.CONN_CRM_CEN);
        return res;
    }

    public static IDataset queryHadCommit(String staffId, String startTime, String endTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("BADNESS_INFO_PROVINCE", "731");
        param.put("DEAL_STAFF_ID", staffId);
        param.put("DEAL_START_TIME", startTime);
        param.put("DEAL_END_TIME", endTime);
        IDataset result = Dao.qryByCodeParser("TF_F_BADNESS_INFO", "SEL_HAD_COMMIT", param, page, Route.CONN_CRM_CEN);
        return result;
    }

    // 查询垃圾短信处理信息是否存在
    public static IData queryInfoIsExist(IData data, String routeId) throws Exception
    {
        SQLParser sql = new SQLParser(data);
        sql.addSQL("  SELECT *  FROM TI_BI_MO_MON A WHERE A.DEAL_TAG = '2' AND (A.RESULT_CODE = '40' OR A.RESULT_CODE = '50')   AND A.SEND_STATUS = '0' ");
        sql.addSQL("  and A.ID = :ID");
        IDataset set = Dao.qryByParse(sql, routeId);
        if (IDataUtil.isNotEmpty(set))
        {
            return set.getData(0);
        }
        return null;
    }

    public static IDataset queryLikeBlackList(String eparchyCode, String inTime, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_TIME", inTime);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("select serial_number,user_id,start_time,end_time,exec_time,other_party,sms_content,record_count,");
        parser.addSQL("idtype,deal_tag,in_time,accept_month,deal_time,finish_time,result_code,result_info,send_time,send_status,");
        parser.addSQL("rsrv_str1,rsrv_str2,rsrv_str3,msgid,id,update_time,update_staff_id,update_depart_id,remark,mac from ti_bi_mo_mon");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("  and rsrv_str1 = :EPARCHY_CODE ");
        parser.addSQL("  and trunc(in_time)=to_date(:IN_TIME, 'yyyy-mm-dd') ");
        if ("".equals(eparchyCode))
        {
            eparchyCode = Route.getCrmDefaultDb();
        }
        IDataset dataset = Dao.qryByParse(parser, page, eparchyCode);
        return dataset;
    }

    public static String queryOperateNum(String subsysCode, String eparchyCode, String paramAttr, String paramCode) throws Exception
    {
        IData dmData = new DataMap();
        dmData.put("SUBSYS_CODE", subsysCode);
        dmData.put("EPARCHY_CODE", eparchyCode);
        dmData.put("PARAM_ATTR", paramAttr);
        dmData.put("PARAM_CODE", paramCode);
        IDataset dsCommpara = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_PARAMCODE", dmData);
        if ((dsCommpara == null) || (dsCommpara.size() <= 0) || ((dsCommpara != null) && (dsCommpara.size() > 0) && (!("1".equals(dsCommpara.getData(0).getString("PARA_CODE1"))))))
        {
            return "100";
        }
        return dsCommpara.getData(0).getString("PARA_CODE2");
    }

    public static IDataset queryRouteEparchyCodeInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset res = Dao.qryByCode("TI_BI_10086999DIRTY", "SEL_BY_SERIALNUMBER_IN_MOFFICE", param, Route.CONN_CRM_CEN);
        return res;
    }

    /**
     * 查询垃圾信息治理特殊停机
     * 
     * @param pd
     * @param param
     * @param pag
     * @return
     * @throws Exception
     */
    public static IDataset queryRubCloseMobile(String serialNumber, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT t.* ");
        sql.addSQL("FROM TF_O_TRASHMSGUSER t ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND t.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.addSQL("AND t.CUST_TYPE = '1' ");
        sql.addSQL("AND t.RSRV_STR3 = '02' ");// 02:停开机(语音) 03:停开GPRS
        sql.addSQL("AND sysdate BETWEEN t.START_DATE AND t.END_DATE ");
        sql.addSQL("order by t.START_DATE desc ");
        return Dao.qryByParse(sql, page);
    }

    // 查询垃圾短信已停机处理信息是否存在
    public static IData queryStopInfoIsExist(IData data, String routeId) throws Exception
    {
        SQLParser sql = new SQLParser(data);
        sql.addSQL(" SELECT *   FROM TI_BI_MO_MON A WHERE A.DEAL_TAG = '1' AND A.RESULT_CODE = '10'   AND A.SEND_STATUS = '0' ");
        sql.addSQL(" and A.ID = :ID ");
        IDataset set = Dao.qryByParse(sql, routeId);
        if (IDataUtil.isNotEmpty(set))
        {
            return set.getData(0);
        }
        return null;
    }

    public static IDataset queryTiMoInfoBySerialNumber(String serialNumber, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset res = Dao.qryByCode("TI_BI_10086999DIRTY", "SEL_BY_SERIAL_NUMBER", param, routeId);
        return res;
    }

    public static IDataset queryTradeInfo(String tradeTypeCode, String serviceId, String stateCode, String userId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERVICE_ID", serviceId);
        param.put("STATE_CODE", stateCode);
        param.put("USER_ID", userId);
        IDataset res = Dao.qryByCode("TF_B_TRADE", "SEL_COUNT_701", param, routeId);
        return res;
    }

    public static IDataset queryTrashMsgContrast(String areaCode, String staratTime, String endTime, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("AREA_CODE", areaCode);
        data.put("START_DATE", staratTime);
        data.put("END_DATE", endTime);
        SQLParser sql = new SQLParser(data);
        sql.addSQL(" select ID,DIRTYSMSID,CODEAREA,AREA,CALLERNO,INFORMDATE,INFORMCALLERNO,INFORMAREA,TEXTTYPE,STATE,ISBLACK,AUDITTYPE,AUDITTEXT,SMSTEXT,TCTYPE,PPTYPE,RWSJ,TJSJ,ZWSJ,XYGD,RSRV_STR4  ");
        sql.addSQL(" from TI_BI_10086999DIRTY where CMPFLAG = '1'  ");
        sql.addSQL(" and AREA= :AREA_CODE");
        sql.addSQL(" and INFORMDATE >= :START_DATE");
        sql.addSQL(" and INFORMDATE <= :END_DATE");
        return Dao.qryByParse(sql, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryTrashMsgData(String startTime, String endTime, Pagination page, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("START_DATE", startTime);
        data.put("END_DATE", endTime);
        SQLParser sql = new SQLParser(data);

        sql.addSQL("select * from TF_F_USER_PROSECUTION where 1=1 ");
        sql.addSQL(" and ACCEPT_DATE >= to_date(:START_DATE,'yyyy-mm-dd') ");
        sql.addSQL(" and ACCEPT_DATE <= to_date(:END_DATE,'yyyy-mm-dd') ");
        return Dao.qryByParse(sql, page, routeId);
    }

    public static IDataset queryTrashMsgSort(String updateDate, String qryType, String areaCode, String dbType) throws Exception
    {
        IData data = new DataMap();
        data.put("UPDATE_DATE", updateDate);
        data.put("QRY_TYPE", qryType);
        data.put("AREA_CODE", areaCode);

        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }

        SQLParser sql = new SQLParser(data);
        sql.addSQL(" select AREA_CODE,SUM0,SUM1,SUM2,SUM3,SUM4,SUM5,SUM6,SUM0+SUM1+SUM2+SUM3+SUM4+SUM5+SUM6 SUM7  ");
        sql.addSQL(" from (select f.AREA_CODE, ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'0',1,'0')) SUM0,  ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'1',1,'0')) SUM1,  ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'2',1,'0')) SUM2,  ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'3',1,'0')) SUM3,  ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'4',1,'0')) SUM4,  ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'5',1,'0')) SUM5,  ");
        sql.addSQL("  sum(decode(f.RESULT_INFO,'6',1,'0')) SUM6  ");
        sql.addSQL("  from TI_BI_TRASH_SMSCALL f  ");
        sql.addSQL("  where 1=1  ");
        sql.addSQL("  and to_char(f.UPDATE_TIME,'yyyy-MM-dd') = :UPDATE_DATE  ");
        sql.addSQL("  and f.QRY_TYPE=:QRY_TYPE  ");
        sql.addSQL("  and f.AREA_CODE=:AREA_CODE  ");
        sql.addSQL("  group by f.AREA_CODE) ");
        return Dao.qryByParse(sql, routeId);
    }

    public static IDataset queryTrashMsgUserBySn(String serialNumber, String idType, String custType, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("ID_TYPE", idType);
        data.put("CUST_TYPE", custType);
        IDataset res = Dao.qryByCode("TF_O_TRASHMSGUSER", "SEL_BY_SN_NEW", data, routeId);
        return res;
    }

    public static IDataset queryTrashMsgUserBySnNew(String serialNumber, String idType, String custType, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("ID_TYPE", idType);
        data.put("CUST_TYPE", custType);
        IDataset res = Dao.qryByCode("TF_O_TRASHMSGUSER", "SEL_BY_SN_NEW", data, routeId);
        return res;
    }

    public static IDataset queryWasteWord(String wasteWord, String wasteWordType, String wasteWordStatus, Pagination page) throws Exception
    {
        IData data = new DataMap();
        data.put("WASTE_WORD", wasteWord);
        data.put("WASTE_WORD_TYPE", wasteWordType);
        data.put("WASTE_WORD_STATUS", wasteWordStatus);
        IDataset res = Dao.qryByCode("TD_B_WASTEWORD", "SEL_BY_ALL", data, page, Route.CONN_CRM_CEN);
        return res;
    }

    public static int updateBiListByTable(String tableName, IData tableData, String routeId) throws Exception
    {
        SQLParser sql = new SQLParser(tableData);
        sql.addSQL(" update " + tableName);
        sql.addSQL(" set DEAL_TAG =:DEAL_TAG ");
        sql.addSQL(" , SEND_STATUS =:SEND_STATUS ");
        sql.addSQL(" , RESULT_CODE =:RESULT_CODE ");
        sql.addSQL(" , RESULT_INFO =:RESULT_INFO ");
        sql.addSQL(" , CALL_RESULT =:CALL_RESULT ");
        sql.addSQL(" , BILL_INFO =:BILL_INFO ");
        sql.addSQL(" , UPDATE_TIME = to_date( :UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        sql.addSQL(" , UPDATE_STAFF_ID =:UPDATE_STAFF_ID ");
        sql.addSQL(" , UPDATE_DEPART_ID =:UPDATE_DEPART_ID ");
        sql.addSQL(" , REMARK =:REMARK ");
        sql.addSQL(" WHERE ID = :ID ");
        return Dao.executeUpdate(sql, routeId);

    }

    // TI_BI_MO_MON TI_BI_AO_MON TI_BI_COMM_MON
    public static int updateMonDataByTable(String tableName, IData tableData, String routeId) throws Exception
    {
        SQLParser sql = new SQLParser(tableData);
        sql.addSQL(" update " + tableName);
        sql.addSQL(" set DEAL_TAG = :DEAL_TAG ");
        sql.addSQL(" , SEND_STATUS =:SEND_STATUS ");
        sql.addSQL(" , RESULT_CODE =:RESULT_CODE ");
        sql.addSQL(" , RESULT_INFO =:RESULT_INFO ");
        sql.addSQL(" , UPDATE_TIME = to_date( :UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        sql.addSQL(" , UPDATE_STAFF_ID =:UPDATE_STAFF_ID ");
        sql.addSQL(" , UPDATE_DEPART_ID =:UPDATE_DEPART_ID ");
        sql.addSQL(" , REMARK =:REMARK ");
        sql.addSQL(" , BLOCKTYPE =:BLOCKTYPE ");
        sql.addSQL(" WHERE ID = :ID ");
        sql.addSQL(" and ACCEPT_MONTH = :ACCEPT_MONTH ");
        if ("TI_BI_MO_MON".equals(tableName))
        {
            sql.addSQL(" AND RESULT_CODE in ('40','50','10') ");
        }
        return Dao.executeUpdate(sql, routeId);
    }

    public static int updateTableBiAoMon(String tableName, IData data, String routeId) throws Exception
    {

        SQLParser sql = new SQLParser(data);
        sql.addSQL(" update tf_b_trade_svc");
        sql.addSQL(" set start_date = sysdate ");
        sql.addSQL(" WHERE trade_id = :ID ");
        return Dao.executeUpdate(sql, routeId);
    }

    public static int updateTableInfo(String tableName, String msgContent, String staffId, String id, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("MSG_CONTENT", msgContent);
        data.put("STAFF_ID", staffId);
        data.put("ID", id);

        SQLParser sql = new SQLParser(data);
        sql.addSQL(" update " + tableName);
        sql.addSQL(" set sms_content = :MSG_CONTENT ");
        sql.addSQL(" , update_time =sysdate ");
        sql.addSQL(" , update_staff_id =:STAFF_ID ");
        sql.addSQL(" WHERE id = :ID ");
        return Dao.executeUpdate(sql, routeId);
    }

    public static void updWasteWord(IData param) throws Exception
    {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" update td_b_wasteword set WASTE_WORD_STATUS = :WASTE_WORD_STATUS ,WASTE_WORD_TYPE = :WASTE_WORD_TYPE,UPDATE_TIME=sysdate,UPDATE_STAFF_ID = :UPDATE_STAFF_ID,REMARK = :REMARK ");
        sql.addSQL(" where  WASTE_WORD = :WASTE_WORD");
        Dao.executeUpdate(sql, Route.CONN_CRM_CEN);
    }

}
