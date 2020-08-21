
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.URelaRoleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.URelaTypeInfoQry;

public class RelaBBInfoQry
{
    /**
     * @description 根据userIdA、userIdB、relationTypeCode、roleCodeB查询BB关系信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset getBBByUserIdAB(String userIdA, String userIdB, String roleCodeB, String relationTypeCode) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("USER_ID_B", userIdB);
        params.put("ROLE_CODE_B", roleCodeB);
        params.put("RELATION_TYPE_CODE", relationTypeCode);

        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT PARTITION_ID, ");
        parser.addSQL("TO_CHAR(USER_ID_A) USER_ID_A, ");
        parser.addSQL("SERIAL_NUMBER_A, ");
        parser.addSQL("TO_CHAR(USER_ID_B) USER_ID_B, ");
        parser.addSQL("SERIAL_NUMBER_B, ");
        parser.addSQL("RELATION_TYPE_CODE, ");
        parser.addSQL("ROLE_TYPE_CODE, ");
        parser.addSQL("ROLE_CODE_A, ");
        parser.addSQL("ROLE_CODE_B, ");
        parser.addSQL("ORDERNO, ");
        parser.addSQL("SHORT_CODE, ");
        parser.addSQL("TO_CHAR(INST_ID) INST_ID, ");
        parser.addSQL("TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        parser.addSQL("TO_CHAR(END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        parser.addSQL("TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, ");
        parser.addSQL("REMARK, ");
        parser.addSQL("RSRV_NUM1, ");
        parser.addSQL("RSRV_NUM2, ");
        parser.addSQL("RSRV_NUM3, ");
        parser.addSQL("TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        parser.addSQL("RSRV_STR1, ");
        parser.addSQL("RSRV_STR2, ");
        parser.addSQL("RSRV_STR3, ");
        parser.addSQL("RSRV_STR4, ");
        parser.addSQL("RSRV_STR5, ");
        parser.addSQL("TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD  HH24:MI:SS') RSRV_DATE1, ");
        parser.addSQL("TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
        parser.addSQL("TO_CHAR(RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
        parser.addSQL("RSRV_TAG1, ");
        parser.addSQL("RSRV_TAG2, ");
        parser.addSQL("RSRV_TAG3 ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
        parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
        parser.addSQL("AND ROLE_CODE_B = :ROLE_CODE_B ");
        parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser);
    }

    public static IDataset getBBInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_RELATION_BB", "SEL_ALL_BY_USERIDAB", param);
    }

    public static IDataset getBBInfo_A(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCodeParser("TF_F_RELATION_BB", "SEL_BB_ALL_BY_USERID_A", param);
    }

    public static IDataset getBBInfo_B(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_RELATION_BB", "SEL_BB_ALL_BY_USERID_B", param);
    }

    /**
     * 查询UU关系，没有判当前时间在start和end之间
     * 
     * @param userIdA
     * @param userIdB
     * @param page
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset getBBInfoByUserIdAB(String userIdA, String userIdB) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("USER_ID_B", userIdB);

        SQLParser parser = new SQLParser(params);

        parser.addSQL("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
        parser.addSQL("SERIAL_NUMBER_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
        parser.addSQL("SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ");
        parser.addSQL("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
        parser.addSQL("TO_CHAR(INST_ID) INST_ID, ");
        parser.addSQL("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        parser.addSQL("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, ");
        parser.addSQL("RSRV_NUM2, RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
        parser.addSQL("TO_CHAR(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, ");
        parser.addSQL("RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
        parser.addSQL("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        parser.addSQL("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        parser.addSQL("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        parser.addSQL("RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
        parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        parser.addSQL("AND PARTITION_ID = TO_NUMBER(MOD(TO_NUMBER(:USER_ID_B), 10000)) ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        parser.addSQL("AND END_DATE >= LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

        return Dao.qryByParse(parser);
    }

    /**
     * @description 取得已经存在的网外信息(直接照搬RelaUUInfoQry的方法)
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset getExistGrpOutinfo(String userIdA, String relationTypeCode, String serialNumberB, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID_A", userIdA);
        inparams.put("RELATION_TYPE_CODE", relationTypeCode);
        inparams.put("SERIAL_NUMBER_B", serialNumberB);

        SQLParser parser = new SQLParser(inparams);

        parser.addSQL(" SELECT SERIAL_NUMBER_A, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" USER_ID_A, ");
        parser.addSQL(" USER_ID_B, ");
        parser.addSQL(" SHORT_CODE, ");
        parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
        parser.addSQL(" FROM tf_f_relation_bb ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND RELATION_TYPE_CODE =:REATION_TYPE_CODE ");
        parser.addSQL(" AND START_DATE <= SYSDATE ");
        parser.addSQL(" AND END_DATE > SYSDATE ");
        parser.addSQL(" AND USER_ID_A =:USER_ID_A ");
        parser.addSQL(" AND SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
        parser.addSQL(" AND PARTITION_ID = MOD(USER_ID_B, 10000) ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 依据user_id_a/relation_type_code 查询是否存在成员
     * 
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset getExistsByUserIdA(String userIdA, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        return Dao.qryByCodeAllCrm("TF_F_RELATION_BB", "SEL_BB_ALL_BY_USERID_A", param, false);
    }
    
 
    /**
     * @descriptin 根据user_id_a和relation_type_code查集团用户下的成员信息
     * @author daidl
     * @date 2019-3-12
     */
    public static IDataset getUserExistsByUserIdA(String userIdA, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
		StringBuilder sql=new StringBuilder();
		
		sql.append("SELECT partition_id, ");
	    sql.append("to_char(user_id_a) user_id_a,");
	    sql.append("serial_number_a,");
	    sql.append("to_char(user_id_b) user_id_b,");
	    sql.append("serial_number_b,");
	    sql.append("relation_type_code, ");
	    sql.append("role_code_a, ");
	    sql.append("role_code_b, ");
	    sql.append("orderno, ");
	    sql.append("short_code,");
	    sql.append("to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,");
	    sql.append("to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date  ");
	    sql.append("from tf_f_relation_bb t ");	 	    
		sql.append("where 1 = 1 ");
	    sql.append("and t.user_id_a = :USER_ID ");
	    sql.append("and t.relation_type_code = :RELATION_TYPE_CODE ");
	    sql.append("and end_date >= TRUNC(LAST_DAY(SYSDATE) + 1) ");

	    return Dao.qryBySql(sql,param, Route.CONN_CRM_CG);
    }
    


    /**
     * 取集团bboss业务商产品关系 chenyi 2014-8-11
     */
    public static IDataset getUserRelationByTradeId(String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("SELECT TRADE_ID,ACCEPT_MONTH,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,START_DATE,END_DATE,MODIFY_TAG,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
        parser.addSQL(" from  tf_b_trade_relation_bb ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("  and trade_id = :TRADE_ID");

        return Dao.qryByParse(parser,Route.getJourDb());
    }

    public static IDataset qryBB(String userIdB, String relationTypeCode, Pagination page) throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID_B", userIdB);
        params.put("RELATION_TYPE_CODE", relationTypeCode);

        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(USER_ID_A) USER_ID_A, ");
        parser.addSQL(" SERIAL_NUMBER_A, ");
        parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" RELATION_TYPE_CODE, ");
        parser.addSQL(" ROLE_CODE_A, ");
        parser.addSQL(" ROLE_CODE_B, ");
        parser.addSQL(" ORDERNO, ");
        parser.addSQL(" SHORT_CODE, ");
        parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
        parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

        return Dao.qryByParse(parser, page);
    }

    public static IDataset qryBB(String userIdA, String userIdB, String relationTypeCode, Pagination page) throws Exception
    {
        return qryBB(userIdA, userIdB, relationTypeCode, page, null);
    }

    /**
	 * 查询UU关系，没有判当前时间在start和end之间
	 * 
	 * @param userIdA
	 * @param userIdB
	 * @param relationTypeCode
	 * @param page
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryBB(String userIdA, String userIdB, String relationTypeCode, Pagination page, String routeId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);

		parser.addSQL("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
		parser.addSQL("SERIAL_NUMBER_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		parser.addSQL("SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ");
		parser.addSQL("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		parser.addSQL("TO_CHAR(INST_ID) INST_ID, ");
		parser.addSQL("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		parser.addSQL("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, ");
		parser.addSQL("RSRV_NUM2, RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL("TO_CHAR(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, ");
		parser.addSQL("RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
		parser.addSQL("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
		parser.addSQL("RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
		parser.addSQL("FROM TF_F_RELATION_BB ");
		parser.addSQL("WHERE 1 = 1 ");
		parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		parser.addSQL("AND PARTITION_ID = TO_NUMBER(MOD(TO_NUMBER(:USER_ID_B), 10000)) ");
		parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		parser.addSQL("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

		return Dao.qryByParse(parser, page, routeId);
	}
     
	/**
	 * 查询UU关系，没有判当前时间在start和end之间
	 * daidl 集客大厅关系表查询
	 * @param userIdA
	 * @param userIdB
	 * @param relationTypeCode
	 * @param page
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryBBExist(String userIdA, String userIdB, String relationTypeCode, Pagination page) throws Exception
	{   
		IData params=new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);
		
		SQLParser parser=new SQLParser(params);
		parser.addSQL("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
		parser.addSQL("SERIAL_NUMBER_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		parser.addSQL("SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ");
		parser.addSQL("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		parser.addSQL("TO_CHAR(INST_ID) INST_ID, ");
		parser.addSQL("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		parser.addSQL("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, ");
		parser.addSQL("RSRV_NUM2, RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL("TO_CHAR(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, ");
		parser.addSQL("RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
		parser.addSQL("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
		parser.addSQL("RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
		parser.addSQL("FROM TF_F_RELATION_BB ");
		parser.addSQL("WHERE 1 = 1 ");
		parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		parser.addSQL("AND PARTITION_ID = TO_NUMBER(MOD(TO_NUMBER(:USER_ID_B), 10000)) ");
		parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");
		
		return Dao.qryByParse(parser, page);
	}

    /**
     * 统计成员数量
     * 
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryCountByUserIdAAndRelationTypeCodeAllCrm(String userIdA, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        long recordCount = 0;

        // 遍历数据库
        for (String routeId : Route.getAllCrmDb())
        {
            StringBuilder sql = new StringBuilder(100);

            sql.append("SELECT COUNT(1) RECORDCOUNT ");
            sql.append("  FROM TF_F_RELATION_BB ");
            sql.append(" WHERE USER_ID_A = TO_NUMBER(:USER_ID_A) ");
            sql.append("   AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");

            IDataset countList = Dao.qryBySql(sql, param, routeId);

            recordCount += Long.parseLong(countList.getData(0).getString("RECORDCOUNT"));
        }

        IData retData = new DataMap();

        retData.put("RECORDCOUNT", recordCount);

        return IDataUtil.idToIds(retData);
    }

    /**
     * 判断是否存在成员
     * 
     * @param userIdA
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryExistsMebByUserIdA(String userIdA, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        return Dao.qryByCodeAllCrm("TF_F_RELATION_BB", "SEL_EXISTS_MEB_BY_USERIDA", param, false);

    }

    /**
     * @descriptin 根据USER_ID_A、CUST_ID、RELATION_TYPE_CODE获取成员已订购组合产品相关数据
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBByInfoByUserIdBCustId(String userIdA, String custId, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("CUST_ID", custId);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT to_char(b.USER_ID_A) USER_ID_A, ");
        sql.append("b.SERIAL_NUMBER_A, to_char(b.USER_ID_B) USER_ID_B, ");
        sql.append("b.SERIAL_NUMBER_B, b.RELATION_TYPE_CODE, b.ROLE_TYPE_CODE, ");
        sql.append("b.ROLE_CODE_A, b.ROLE_CODE_B, b.ORDERNO, b.SHORT_CODE, ");
        sql.append("to_char(b.INST_ID) INST_ID, ");
        sql.append("to_char(b.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(b.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("to_char(b.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("b.UPDATE_STAFF_ID, b.UPDATE_DEPART_ID, b.REMARK, b.RSRV_NUM1, ");
        sql.append("b.RSRV_NUM2, b.RSRV_NUM3, to_char(b.RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(b.RSRV_NUM5) RSRV_NUM5, b.RSRV_STR1, b.RSRV_STR2, b.RSRV_STR3, ");
        sql.append("b.RSRV_STR4, b.RSRV_STR5, to_char(b.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(b.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(b.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("b.RSRV_TAG1, b.RSRV_TAG2, b.RSRV_TAG3 ");
        sql.append("FROM TF_F_RELATION_BB b ");
        sql.append("WHERE b.user_id_a = :USER_ID_A ");
        sql.append("AND b.relation_type_code = :RELATION_TYPE_CODE ");
        sql.append("AND SYSDATE BETWEEN b.start_date AND b.end_date ");
        sql.append("AND EXISTS (SELECT 1 FROM TF_F_USER a WHERE a.USER_ID = b.user_id_b and a.partition_id=b.partition_id AND a.cust_id = :CUST_ID AND a.remove_tag = '0') ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    public static IDataset qryRelaBBByUserIdBAndEndDate(String userId, String end_date) throws Exception
    {
        IData uuParam = new DataMap();

        uuParam.put("USER_ID_B", userId);
        uuParam.put("END_DATE", end_date);

        IDataset uuSet = Dao.qryByCode("TF_F_RELATION_BB", "SEL_ALL_BY_USERIDB", uuParam);
        return uuSet;

    }

    /**
     * @description 根据商品用户编号、产品关系编号和角色编号查询用户UU关系
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBInfoByRoleCodeBForGrp(String userIdA, String relationTypeCode, String roleCodeB) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        param.put("ROLE_CODE_B", roleCodeB);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("select partition_id, ");
        sql.append("user_id_a, ");
        sql.append("serial_number_a, ");
        sql.append("user_id_b, ");
        sql.append("serial_number_b, ");
        sql.append("relation_type_code, ");
        sql.append("role_type_code, ");
        sql.append("role_code_a, ");
        sql.append("role_code_b, ");
        sql.append("orderno, ");
        sql.append("short_code, ");
        sql.append("inst_id, ");
        sql.append("start_date, ");
        sql.append("end_date, ");
        sql.append("update_time, ");
        sql.append("update_staff_id, ");
        sql.append("update_depart_id, ");
        sql.append("remark, ");
        sql.append("rsrv_num1, ");
        sql.append("rsrv_num2, ");
        sql.append("rsrv_num3, ");
        sql.append("rsrv_num4, ");
        sql.append("rsrv_num5, ");
        sql.append("rsrv_str1, ");
        sql.append("rsrv_str2, ");
        sql.append("rsrv_str3, ");
        sql.append("rsrv_str4, ");
        sql.append("rsrv_str5, ");
        sql.append("rsrv_date1, ");
        sql.append("rsrv_date2, ");
        sql.append("rsrv_date3, ");
        sql.append("rsrv_tag1, ");
        sql.append("rsrv_tag2, ");
        sql.append("rsrv_tag3 ");
        sql.append("from tf_f_relation_bb ");
        sql.append("WHERE user_id_a = TO_NUMBER(:USER_ID_A) ");
        sql.append("AND relation_type_code = :RELATION_TYPE_CODE ");
        sql.append("AND role_code_b = :ROLE_CODE_B ");
        sql.append("AND sysdate < end_date+0 ");
        sql.append("ORDER BY end_date DESC ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }

    /**
     * @description 根据userIdB和产品关系编号查询BB关系信息
     * @author xunyl
     * @date 2014-07-25
     */
    public static IDataset qryRelaBBInfoByUserIdBRelaTypeCode(String userIdB, String relationTypeCode, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userIdB);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID, USER_ID_A, SERIAL_NUMBER_A, USER_ID_B, SERIAL_NUMBER_B, ");
        sql.append("RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B, ORDERNO, ");
        sql.append("SHORT_CODE, INST_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, ");
        sql.append("RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3 ");
        sql.append("FROM TF_F_RELATION_BB ");
        sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
        sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        sql.append("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        sql.append("ORDER BY END_DATE, START_DATE ");

        return Dao.qryBySql(sql, param, routeId);
    }
    
    /**
     * @description 根据userIdB和产品关系编号查询BB关系信息
     * @author liaolc
     * @date 2014-09-23
     */
    public static IDataset qryRelaBBInfoByUserIdBRelaTypeCode(String userIdB, String relationTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userIdB);
        param.put("RELATION_TYPE_CODE", relationTypeCode);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID, USER_ID_A, SERIAL_NUMBER_A, USER_ID_B, SERIAL_NUMBER_B, ");
        sql.append("RELATION_TYPE_CODE, ROLE_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B, ORDERNO, ");
        sql.append("SHORT_CODE, INST_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
        sql.append("RSRV_NUM3, RSRV_NUM4, RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, ");
        sql.append("RSRV_STR4, RSRV_STR5, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, ");
        sql.append("RSRV_TAG2, RSRV_TAG3 ");
        sql.append("FROM TF_F_RELATION_BB ");
        sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
        sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        sql.append("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        sql.append("ORDER BY END_DATE, START_DATE ");

        return Dao.qryBySql(sql, param);
    }

    /**
     * 查询指定数据库的集团成员
     * 
     * @param data
     * @param routeId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryRelaBBInfoTheDb(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT /*+ index(BB IDX_TF_F_RELATION_BB_UID) */ partition_id, ");
        parser.addSQL(" to_char(user_id_a) user_id_a, ");
        parser.addSQL(" serial_number_a, ");
        parser.addSQL(" to_char(user_id_b) user_id_b, ");
        parser.addSQL(" serial_number_b, ");
        parser.addSQL(" relation_type_code, ");
        parser.addSQL(" role_code_a, ");
        parser.addSQL(" role_code_b, ");
        parser.addSQL(" orderno, ");
        parser.addSQL(" short_code, ");
        parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
        parser.addSQL(" FROM tf_f_relation_bb bb ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND bb.user_id_a = TO_NUMBER(:USER_ID_A) ");
        parser.addSQL(" AND bb.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
        parser.addSQL(" AND bb.SHORT_CODE = :SHORT_CODE ");
        parser.addSQL(" AND bb.ROLE_CODE_B = :ROLE_CODE_B ");
        parser.addSQL(" AND SYSDATE BETWEEN bb.start_date AND bb.end_date ");

        IDataset dataset = Dao.qryByParse(parser, pagination);

        String strRelaTypeName = "";
        String strRelaTypeCode = "";

        for (int iIndex = 0; iIndex < dataset.size(); iIndex++)
        {
            IData id = dataset.getData(iIndex);

            // relation
            if (StringUtils.isEmpty(strRelaTypeCode))
            {
                strRelaTypeCode = id.getString("RELATION_TYPE_CODE");
                strRelaTypeName = URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(strRelaTypeCode);
            }

            String strRoleBCode = id.getString("ROLE_CODE_B");
            String strRoleBName = URelaRoleInfoQry.getRoleBNameByRelaTypeCodeRoleCodeB(strRelaTypeCode, strRoleBCode);

            id.put("RELATION_TYPE_NAME", strRelaTypeName);
            id.put("ROLE_CODE_B_NAME", strRoleBName);
        }

        return dataset;
    }

    /**
     * 查询BB crm all
     * 
     * @param userIdA
     * @param userIdB
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationBB(String userIdA, String relationTypeCode) throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("RELATION_TYPE_CODE", relationTypeCode);

        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT A.PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(A.USER_ID_A) USER_ID_A, ");
        parser.addSQL(" A.SERIAL_NUMBER_A, ");
        parser.addSQL(" TO_CHAR(A.USER_ID_B) USER_ID_B, ");
        parser.addSQL(" A.SERIAL_NUMBER_B, ");
        parser.addSQL(" A.RELATION_TYPE_CODE, ");
        parser.addSQL(" A.ROLE_CODE_A, ");
        parser.addSQL(" A.ROLE_CODE_B, ");
        parser.addSQL(" A.ORDERNO, ");
        parser.addSQL(" A.SHORT_CODE, ");
        parser.addSQL(" TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL(" FROM TF_F_RELATION_BB A,TF_F_USER_PAYPLAN B ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND A.USER_ID_A= :USER_ID_A ");
        parser.addSQL(" AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        parser.addSQL(" AND sysdate BETWEEN A.START_DATE AND A.END_DATE ");
        parser.addSQL(" AND B.USER_ID = A.USER_ID_B ");
        parser.addSQL(" AND B.PARTITION_ID=MOD(TO_NUMBER(A.USER_ID_B),10000) ");
        parser.addSQL(" AND B.PLAN_TYPE_CODE= 'G' ");
        parser.addSQL(" AND B.START_DATE < SYSDATE ");
        parser.addSQL(" AND B.END_DATE > SYSDATE ");

        return Dao.qryByParseAllCrm(parser, true);
    }

    public static IDataset qryRelationBBAll(String userIdA, String relationTypeCode) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("RELATION_TYPE_CODE", relationTypeCode);

        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(USER_ID_A) USER_ID_A, ");
        parser.addSQL(" SERIAL_NUMBER_A, ");
        parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" RELATION_TYPE_CODE, ");
        parser.addSQL(" ROLE_CODE_A, ");
        parser.addSQL(" ROLE_CODE_B, ");
        parser.addSQL(" ORDERNO, ");
        parser.addSQL(" SHORT_CODE, ");
        parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
        parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        parser.addSQL("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

        return Dao.qryByParseAllCrm(parser, true);
    }

    /**
     * 查询BB crm all
     * 
     * @param userIdA
     * @param userIdB
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationBBAll(String userIdA, String userIdB, String relationTypeCode) throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("USER_ID_B", userIdB);
        params.put("RELATION_TYPE_CODE", relationTypeCode);

        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(USER_ID_A) USER_ID_A, ");
        parser.addSQL(" SERIAL_NUMBER_A, ");
        parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" RELATION_TYPE_CODE, ");
        parser.addSQL(" ROLE_CODE_A, ");
        parser.addSQL(" ROLE_CODE_B, ");
        parser.addSQL(" ORDERNO, ");
        parser.addSQL(" SHORT_CODE, ");
        parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
        parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
        parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        parser.addSQL("AND END_DATE >= LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

        return Dao.qryByParseAllCrm(parser, true);
    }
    
    /**
     * 查询BB crm all
     * 
     * @param userIdA
     * @param userIdB
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationBBAllForBBossMem(String userIdA, String userIdB, String relationTypeCode, String roleCodeB) throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("USER_ID_B", userIdB);
        params.put("RELATION_TYPE_CODE", relationTypeCode);
        params.put("ROLE_CODE_B", roleCodeB);

        SQLParser parser = new SQLParser(params);

        parser.addSQL(" SELECT PARTITION_ID, ");
        parser.addSQL(" TO_CHAR(USER_ID_A) USER_ID_A, ");
        parser.addSQL(" SERIAL_NUMBER_A, ");
        parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" RELATION_TYPE_CODE, ");
        parser.addSQL(" ROLE_CODE_A, ");
        parser.addSQL(" ROLE_CODE_B, ");
        parser.addSQL(" ORDERNO, ");
        parser.addSQL(" SHORT_CODE, ");
        parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        parser.addSQL(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
        parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
        parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
        parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
        parser.addSQL("AND ROLE_CODE_B = :ROLE_CODE_B ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        parser.addSQL("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

        return Dao.qryByParseAllCrm(parser, true);
    }
    /**
     * 查询BB crm all
     * add by sundz
     * @param userIdA userIdB
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationBBAllBySerialNumberA(String userIdA,String userIdB) throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID_A", userIdA);
        params.put("USER_ID_B", userIdB);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT * ");

//        parser.addSQL(" SELECT PARTITION_ID, ");
//        parser.addSQL(" TO_CHAR(USER_ID_A) USER_ID_A, ");
//        parser.addSQL(" SERIAL_NUMBER_A, ");
//        parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
//        parser.addSQL(" SERIAL_NUMBER_B, ");
//        parser.addSQL(" RELATION_TYPE_CODE, ");
//        parser.addSQL(" ROLE_CODE_A, ");
//        parser.addSQL(" ROLE_CODE_B, ");
//        parser.addSQL(" ORDERNO, ");
//        parser.addSQL(" SHORT_CODE, ");
//        parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
//        parser.addSQL(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
//        parser.addSQL(" TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
//        parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
//
//        parser.addSQL(" INST_ID, ");
//        parser.addSQL(" SERIAL_NUMBER_A, ");
//        parser.addSQL(" SERIAL_NUMBER_A, ");
        parser.addSQL("FROM TF_F_RELATION_BB ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND USER_ID_A = :USER_ID_A ");
        parser.addSQL("AND USER_ID_B = :USER_ID_B ");
        parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
        return Dao.qryByParseAllCrm(parser, true);
    }


}
