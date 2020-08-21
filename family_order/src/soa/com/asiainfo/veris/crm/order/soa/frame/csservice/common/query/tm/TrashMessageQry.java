
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

/**
 * 骚扰电话垃圾短信入库统计查询
 * 
 * @param startTime
 *            endTime areaCode
 * @return
 * @throws Exception
 */
public class TrashMessageQry
{
    public static int countTrashMsg(String dbConCode) throws Exception
    {
        SQLParser parser = new SQLParser(null);
        parser.addSQL(" select COUNT(ID) TRASHCOUNT from ti_bi_mo_mon where in_time > sysdate-1/56 ");
        return Dao.qryByParse(parser, dbConCode).getData(0).getInt("TRASHCOUNT");
    }

    public static IDataset getIpConfigs() throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SUBSYS_CODE", "CSM");
        inparams.put("PARAM_ATTR", "2402");
        inparams.put("PARAM_CODE", "IPCONFIG");
        inparams.put("EPARCHY_CODE", "ZZZZ");
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", inparams, Route.CONN_CRM_CEN);
    }

    public static IDataset getTrashResultCode() throws Exception
    {
        IData param = new DataMap();
        param.put("TYPE_ID", "TRASH_RESULT_CODE");
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select data_id, data_name ");
        parser.addSQL(" from td_s_static ");
        parser.addSQL(" where type_id = 'TRASH_RESULT_CODE' ");
        parser.addSQL(" and data_id like 'S%' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryInfosManualMsg(String manualType, String sqlStr, String rowNum) throws Exception
    {
        IData param = new DataMap();
        param.put("ROWNUM", rowNum);
        SQLParser parser = new SQLParser(param);
        if ("1".equals(manualType))
        {
            parser.addSQL(" select * from ( ");
            parser
                    .addSQL(" select  a.ID,a.START_TIME,a.SERIAL_NUMBER,a.OTHER_PARTY,a.IDTYPE,a.RECORD_COUNT,a.SMS_CONTENT,a.AREA_CODE,a.RESULT_CODE as RESULT_CODE1,a.REMARK,a.RESULT_INFO,a.UPDATE_SEAT,a.UPDATE_STAFF_ID,a.UPDATE_TIME, decode(a.RESULT_CODE, 'S4', '40', 'W') RESULT_CODE,a.IN_TIME ");
            parser.addSQL("  from TI_BI_TRASH_SMSCALL a   ");
            parser.addSQL("  where 1=1  ");
            parser.addSQL("  and a.QRY_TYPE = 'S' ");
            parser.addSQL("  and a.SEND_STATUS='1' and a.DEAL_TAG='2'  ");
        }
        else if ("2".equals(manualType))
        {
            parser.addSQL("select sum(decode(send_status,1,decode(deal_tag,2,1,0),0)) DEAL_COUNT ,count(1)-sum(decode(deal_tag,2,decode(send_status,1,1,0),0)) NOTDEAL_COUNT ");
            parser.addSQL(" from (");
            parser.addSQL("  select UPDATE_SEAT,DEAL_TIME,UPDATE_STAFF_ID,deal_tag,send_status from TI_BI_TRASH_SMSCALL ");
            parser.addSQL(" ) a");
            parser.addSQL(" where 1=1");
        }
        else if ("3".equals(manualType))
        {
            parser.addSQL(" select * from ( ");
            parser.addSQL(" select a.id,a.record_count,a.sms_content,a.update_seat,a.in_time,a.area_code,a.result_code,a.start_time,a.serial_number,a.qry_type,a.source_type,a.other_party,a.idtype ");
            parser.addSQL(" from  TI_BI_TRASH_SMSCALL a where 1=1");
            parser.addSQL(" and a.SEND_STATUS =0 ");
            parser.addSQL(" and a.DEAL_TAG =0 ");
        }
        else if ("4".equals(manualType))
        {
            parser.addSQL(" select * from ( ");
            parser.addSQL("select a.SOURCE_ID,a.START_TIME,a.SERIAL_NUMBER,a.OTHER_PARTY,a.IDTYPE,a.RECORD_COUNT,a.AGV_TIME,a.AREA_CODE,a.RESULT_CODE,a.REMARK,a.RESULT_INFO,a.UPDATE_SEAT,a.UPDATE_STAFF_ID,a.UPDATE_TIME,a.IN_TIME,a.ROWNUM ");
            parser.addSQL(" from TI_BI_TRASH_SMSCALL a  ");
            parser.addSQL(" where 1=1 ");
            parser.addSQL(" and a.QRY_TYPE = 'P' ");
            parser.addSQL(" and a.SEND_STATUS='1' and a.DEAL_TAG='2'  ");
        }

        if (!"".equals(sqlStr))
            parser.addSQL(" " + sqlStr);
        if ("1".equals(manualType) || "4".equals(manualType))
        {
            parser.addSQL(" order by a.update_time desc");
            parser.addSQL(" ) where rownum <= :ROWNUM");
        }
        else if ("3".equals(manualType))
        {
            parser.addSQL(" order by a.start_time desc");
            parser.addSQL(" ) where rownum <= :ROWNUM");
        }

        return Dao.qryByParseAllCrm(parser, true);
    }

    public static IDataset queryMsgOverFlow(String startTime, String endTime, String areaCode, String dbType, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("AREA_CODE", areaCode);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);

        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select a.*, a.RESULT_CODE as RESULT_CODE1, decode(a.RESULT_CODE, 'S4', '40', 'W') RESULT_CODE2 from TI_BI_TRASH_SMSCALL a where 1=1 ");
        parser.addSQL(" and a.START_TIME > to_date(:START_TIME,'yyyy-MM-dd') ");
        parser.addSQL(" and a.START_TIME < to_date(:END_TIME,'yyyy-MM-dd') ");
        parser.addSQL(" and a.QRY_TYPE ='S' ");
        parser.addSQL(" and a.AREA_CODE = :AREA_CODE ");
        parser.addSQL(" and a.IDTYPE in ('01','01','02','03','04','05','06','07','08','09','10') ");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset queryOperType(String paramAttr, String paraCode, String paraCode1) throws Exception
    {
        IData param = new DataMap();
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARA_CODE", paraCode);
        param.put("PARA_CODE1", paraCode1);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select para_code2,para_code3 from TD_S_COMMPARA where 1=1 ");
        parser.addSQL(" and param_attr=:PARAM_ATTR ");
        parser.addSQL(" and param_code=:PARA_CODE ");
        parser.addSQL(" and para_code1=:PARA_CODE1 ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryRubbishUser(String userId, String serialNumber, String userStateCodeset, Pagination pagination) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("USER_STATE_CODESET", userStateCodeset);

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select user_id,serial_number,user_state_codeset,last_stop_time from ");
        parser.addSQL("tf_f_user_rubbish where 1=1 ");
        parser.addSQL("and user_id =:USER_ID ");
        parser.addSQL("and serial_number =:SERIAL_NUMBER ");
        parser.addSQL("and user_state_codeset =:USER_STATE_CODESET ");
        IDataset ids = Dao.qryByParse(parser, pagination);
        return ids;
    }

    /**
     * 查询骚扰电话垃圾短信审核工作量信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryStatWasteAuditing(String startTime, String endTime, String areaCode, String dbType, String staffId, Pagination page) throws Exception
    {
        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }
        IData param = new DataMap();
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("AREA_CODE", areaCode);
        param.put("UPDATE_STAFF_ID", staffId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select UPDATE_STAFF_ID,QRY_TYPE,SOURCE_TYPE,SUM0,SUM1,SUM2,SUM3,SUM4,SUM0+SUM1+SUM2+SUM3+SUM4 SUM5 ");
        parser.addSQL(" from( select f.UPDATE_STAFF_ID,f.QRY_TYPE,f.SOURCE_TYPE,");
        parser.addSQL("  sum(decode(f.RESULT_CODE,'S4',1,'0')) SUM0, ");
        parser.addSQL("  sum(decode(f.RESULT_CODE,'P1',1,'0')) SUM1, ");
        parser.addSQL(" sum(decode(f.RESULT_CODE,'S3',1,'0')) SUM2, ");
        parser.addSQL("  sum(decode(f.RESULT_CODE,'S5',1,'P0',1,0)) SUM3, ");
        parser.addSQL("  sum(decode(f.RESULT_CODE,'S2',1,'S6',1,0)) SUM4");
        parser.addSQL("   from TI_BI_TRASH_SMSCALL f where 1=1   ");
        parser.addSQL("  and f.AREA_CODE= :AREA_CODE   ");
        parser.addSQL(" and f.UPDATE_STAFF_ID=:UPDATE_STAFF_ID ");
        parser.addSQL("  and f.UPDATE_TIME>=to_date(:START_TIME,'yyyy_MM_dd hh24:mi:ss' ) ");
        parser.addSQL("  and f.UPDATE_TIME<=to_date(:END_TIME,'yyyy_MM_dd hh24:mi:ss' ) ");
        parser.addSQL("   group by UPDATE_STAFF_ID,QRY_TYPE,SOURCE_TYPE ) ");
        return Dao.qryByParse(parser, page, routeId);
    }

    /**
     * 查询骚扰电话垃圾短信入库信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryStatWasteNotePhone(String startTime, String endTime, String areaCode, String dbType) throws Exception
    {
        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }

        IData param = new DataMap();
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("AREA_CODE", areaCode);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("  select AREA_CODE,SUM0,SUM1,SUM2,SUM3,SUM4,SUM5,SUM6,SUM7,SUM8,SUM9,SUM10,SUM0+SUM1+SUM2+SUM3+SUM4+SUM5+SUM6+SUM7+SUM8+SUM9+SUM10 SUM11 ");
        parser.addSQL(" from( select f.AREA_CODE, sum(decode(f.SOURCE_TYPE,'01',decode(RESULT_CODE,'0',1,'0'),'0')) SUM0,");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'01',decode(RESULT_CODE,'S4',1,'0'),'0')) SUM1, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'01',decode(RESULT_CODE,'20',1,'0'),'0')) SUM2, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'01',decode(RESULT_CODE,'40',1,'0'),'0')) SUM3, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'01',decode(RESULT_CODE,'50',1,'0'),'0')) SUM4, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'02',1,'0')) SUM5, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'03',1,'0')) SUM6, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'04',1,'0')) SUM7, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'10',1,'0')) SUM8, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'11',1,'0')) SUM9, ");
        parser.addSQL("   sum(decode(f.SOURCE_TYPE,'12',1,'0')) SUM10 ");
        parser.addSQL("    from  TI_BI_TRASH_SMSCALL f  where 1=1 ");
        parser.addSQL("    and f.AREA_CODE= :AREA_CODE ");
        parser.addSQL("    and F.IN_TIME >= to_date(:START_TIME,'yyyy_MM_dd hh24:mi:ss') ");
        parser.addSQL("    and F.IN_TIME <= to_date(:END_TIME,'yyyy_MM_dd hh24:mi:ss') ");
        parser.addSQL("    group by AREA_CODE ) ");
        return Dao.qryByParse(parser, routeId);
    }

    public static IDataset queryTInfos(String startTime, String endTime, String resultCode, String sourceType, String areaCode, String dbType, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("RESULT_CODE", resultCode);
        param.put("SOURCE_TYPE", sourceType);
        param.put("AREA_CODE", areaCode);

        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }
        SQLParser parser = new SQLParser(param);
        parser
                .addSQL(" select /*+ index(u IDX_TF_F_USER_SN)*/ s.state_name,a.SOURCE_ID,a.START_TIME ,a.START_TIME as START_TIME1,a.SOURCE_TYPE, a.SERIAL_NUMBER,a.OTHER_PARTY, a.IDTYPE,a.RECORD_COUNT,a.SMS_CONTENT,a.area_code,a.result_code, a.REMARK,a.RESULT_INFO,a.UPDATE_SEAT,a.UPDATE_STAFF_ID,a.UPDATE_TIME, decode(a.RESULT_INFO, '6', '骚扰电话类', '非骚扰电话') result_info1, decode(a.RESULT_CODE, 'P1', '停语音', 'P2', '派省际单', 'P0', '回传BOSS处理', '不处理') result_code1,a.IN_TIME,a.agv_time from TI_BI_TRASH_SMSCALL a,TF_F_USER u,Td_s_Servicestate s  where 1=1 ");
        parser.addSQL(" and a.qry_type = 'P'");
        parser.addSQL(" and a.send_status = '1'");
        parser.addSQL(" and a.deal_tag = '2'");
        parser.addSQL(" and a.update_time is not null");
        parser.addSQL(" and to_char(a.update_time,'yyyy-MM-dd') >= :START_TIME ");
        parser.addSQL(" and to_char(a.update_time,'yyyy-MM-dd') <= :END_TIME ");
        parser.addSQL(" and a.RESULT_CODE = :RESULT_CODE");
        parser.addSQL(" and a.SOURCE_TYPE = :SOURCE_TYPE");
        parser.addSQL(" and a.AREA_CODE   =　:AREA_CODE");
        parser.addSQL(" and a.serial_number = u.serial_number ");
        parser.addSQL(" and u.remove_tag='0'");
        parser.addSQL(" and u.user_state_codeset = s.state_code and s.service_id='0' ");

        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset queryTrashInStore(String startTime, String endTime, String areaCode, String dbType) throws Exception
    {
        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }
        IData data = new DataMap();
        data.put("START_TIME", startTime);
        data.put("END_TIME", endTime);
        data.put("AREA_CODE", areaCode);

        SQLParser sql = new SQLParser(data);
        sql.addSQL("select");
        sql.addSQL(" sum(decode(a.serial_number,null,0,decode(f.qry_type,'S',1,0))) group_cust_count,");
        sql.addSQL(" sum(decode(f.source_type,'02',1,'0')) trash_sms_count,");
        sql.addSQL(" sum(decode(f.source_type,'10',1,'0')) phone_inner_count,");
        sql.addSQL(" sum(decode(f.source_type,'11',1,'0')) phone_outer_count,");
        sql.addSQL(" sum(decode(f.source_type,'12',1,'0')) trash_phone_count");
        sql.addSQL(" from TF_F_CUST_GROUPMEMBER a,TI_BI_TRASH_SMSCALL f where 1=1");
        sql.addSQL(" and a.serial_number(+)=f.serial_number");
        sql.addSQL(" and to_char(f.start_time,'yyyy-MM-dd HH:mm:ss') >= :START_TIME");
        sql.addSQL(" and to_char(f.end_time,'yyyy-MM-dd HH:mm:ss') < :END_TIME");
        sql.addSQL(" and f.AREA_CODE   =　:AREA_CODE");
        IDataset dataset = Dao.qryByParse(sql, routeId);
        return dataset;
    }

    public static IDataset queryUndealMsg(String startTime, String resultCode, String sourceType, String areaCode, String dbType, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SOURCE_TYPE", sourceType);
        param.put("AREA_CODE", areaCode);
        param.put("START_TIME", startTime);
        param.put("RESULT_CODE", resultCode);

        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TI_BI_TRASH_SMSCALL where 1=1 ");
        parser.addSQL(" and to_char(START_TIME,'yyyy-MM-dd') = :START_TIME ");
        parser.addSQL(" and RESULT_CODE = :RESULT_CODE ");
        parser.addSQL(" and SOURCE_TYPE = :SOURCE_TYPE ");
        parser.addSQL(" and AREA_CODE = :AREA_CODE ");
        parser.addSQL(" and SEND_STATUS ='0' ");
        parser.addSQL(" and DEAL_TAG in ('0','1') ");
        parser.addSQL(" and UPDATE_TIME is null");
        return Dao.qryByParse(parser, pagination, routeId);
    }

    public static IDataset queryWasteNote(String trashSourceType, String areaCode, String startTime, String endTime, String trashResultCode, String recodeNum, String recodeNumEnd, String dbType, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRASH_SOURCE_TYPE", trashSourceType);
        param.put("AREA_CODE", areaCode);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("RECORD_NUM", recodeNum);
        param.put("RECORD_NUM_END", recodeNumEnd);
        param.put("TRASH_RESULT_CODE", trashResultCode);

        IDataset set = StaticInfoQry.queryStaticValuesByPdataId(dbType, "TRASH_AREA_CODE");
        String routeId = "";
        if (set != null && set.size() > 0)
        {
            routeId = set.getData(0).getString("DATA_ID");
        }

        IDataset res = getTrashResultCode();
        String resStr = "";
        if (res != null && res.size() > 0)
        {
            for (int i = 0; i < res.size(); i++)
            {
                IData data = res.getData(i);
                resStr += "'" + data.getString("DATA_ID") + "',";
            }
        }
        resStr = resStr.substring(0, resStr.length() - 1);
        param.put("RESULT_CODE", resStr);

        SQLParser parser = new SQLParser(param);
        parser
                .addSQL(" select * from (select rownum as rn ,t1.* from(select a.SOURCE_ID,a.START_TIME ,a.START_TIME as START_TIME1,a.SOURCE_TYPE,a.SERIAL_NUMBER,a.OTHER_PARTY, a.IDTYPE,a.RECORD_COUNT,a.SMS_CONTENT,a.area_code,a.result_code as result_code1, a.REMARK,a.RESULT_INFO,a.UPDATE_SEAT,a.UPDATE_STAFF_ID,a.UPDATE_TIME,decode(a.RESULT_CODE, 'S4', '40', 'W') result_code,a.IN_TIME");
        /**
         * liuhua_20130422_HXYD-YZ-REQ-20120921-002 关于垃圾信息处理模块优化的需求 判断及时不能进行四舍五入
         */
        // parser.addSQL(" ,decode(sign(round(( a.update_time - a.in_time) * 24 * 60 ) - 5),1,'不及时','及时') as betimes");
        parser.addSQL(" ,decode(sign(( a.update_time - a.in_time) * 24 * 60 - 5),1,'不及时','及时') as betimes");
        // end liuhua_20130422_HXYD-YZ-REQ-20120921-002
        parser.addSQL(" from TI_BI_TRASH_SMSCALL a ");
        parser.addSQL("  where 1=1 ");
        parser.addSQL(" and a.Result_Code in( " + resStr + " ) ");
        parser.addSQL(" and a.SEND_STATUS='1' and a.DEAL_TAG='2'  ");
        parser.addSQL(" and a.qry_type='S'  ");
        parser.addSQL(" and a.SOURCE_TYPE = :TRASH_SOURCE_TYPE ");
        parser.addSQL(" and a.area_code = :AREA_CODE ");
        parser.addSQL(" and a.update_time >= TO_DATE(:START_TIME, 'yyyy-MM-dd hh24:mi:ss') ");
        parser.addSQL(" and a.update_time <= TO_DATE(:END_TIME, 'yyyy-MM-dd hh24:mi:ss') ");
        parser.addSQL(" and a.result_code = :TRASH_RESULT_CODE ");
        parser.addSQL(" order by a.id  ");
        parser.addSQL(" ) t1) where 1=1  ");
        parser.addSQL(" and  rn >= :RECORD_NUM  ");
        parser.addSQL("   and rn <= :RECORD_NUM_END ");
        return Dao.qryByParse(parser, pagination, routeId);
    }
}
