
package com.asiainfo.veris.crm.order.soa.group.returnratio;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ReturnRationQry
{
    /**
     * 返回比例录入分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupProductInfo(IData param, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT   TO_CHAR(T.OPER_ID) OPER_ID, ");
        parser.addSQL("         TO_CHAR(T.PARTITION_ID) PARTITION_ID, ");
        parser.addSQL("         TO_CHAR(T.USER_ID) USER_ID, ");
        parser.addSQL("         T.GROUP_ID, ");
        parser.addSQL("         T.SERIAL_NUMBER, ");
        parser.addSQL("         T.RSRV_VALUE_CODE, ");
        parser.addSQL("         T.RSRV_VALUE, ");
        parser.addSQL("         T.RSRV_NUM1, ");
        parser.addSQL("         T.RSRV_NUM2, ");
        parser.addSQL("         T.RSRV_NUM3, ");
        parser.addSQL("         T.RSRV_NUM4, ");
        parser.addSQL("         T.RSRV_NUM5, ");
        parser.addSQL("         T.RSRV_STR1, ");
        parser.addSQL("         T.RSRV_STR2, ");
        parser.addSQL("         T.RSRV_STR3, ");
        parser.addSQL("         T.RSRV_STR4, ");
        parser.addSQL("         T.RSRV_STR5, ");
        parser.addSQL("         TO_CHAR(T.RSRV_DATE1, 'YYYY-MM-DD') RSRV_DATE1, ");
        parser.addSQL("         TO_CHAR(T.RSRV_DATE2, 'YYYY-MM-DD') RSRV_DATE2, ");
        parser.addSQL("         T.RSRV_DATE3, ");
        parser.addSQL("         T.RSRV_DATE4, ");
        parser.addSQL("         T.RSRV_DATE5, ");
        parser.addSQL("         DECODE(T.RSRV_TAG1,'0','新增','1','删除','2','修改') RSRV_TAG1, ");
        parser.addSQL("         T.RSRV_TAG2, ");
        parser.addSQL("         T.RSRV_TAG3, ");
        parser.addSQL("         T.RSRV_TAG4, ");
        parser.addSQL("         T.RSRV_TAG5, ");
        parser.addSQL("         T.PROCESS_TAG, ");
        parser.addSQL("         T.STAFF_ID, ");
        parser.addSQL("         T.DEPART_ID, ");
        parser.addSQL("         T.TRADE_ID, ");
        parser.addSQL("         TO_CHAR(T.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        parser.addSQL("         TO_CHAR(T.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
        parser.addSQL("         TO_CHAR(T.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
        parser.addSQL("         T.UPDATE_STAFF_ID, ");
        parser.addSQL("         T.UPDATE_DEPART_ID, ");
        parser.addSQL("         T.REMARK, ");
        parser.addSQL("         T.INST_ID, ");
        parser.addSQL("         T.AREA_CODE, ");
        parser.addSQL("         T.AREA_NAME ");
        parser.addSQL("     FROM TF_F_NPRI_OTHER_LOG T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL("      AND T.GROUP_ID = :GROUP_ID  ");
        parser.addSQL("      AND T.SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL("      AND T.UPDATE_STAFF_ID >= :START_STAFF_ID ");
        parser.addSQL("      AND T.UPDATE_STAFF_ID <= :END_STAFF_ID ");
        parser.addSQL("      AND T.UPDATE_TIME >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("      AND T.UPDATE_TIME <= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
        parser.addSQL("      AND T.UPDATE_STAFF_ID LIKE ''||:CITY_CODE ||'%' ");

        return Dao.qryByParse(parser, pagination);

    }

    /**
     * 返回比例录入other表信息的分页查询
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryRatioOtherInfo(IData param, Pagination pagination) throws Exception
    {
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.SERIAL_NUMBER,");
        parser.addSQL("       T.USER_ID,");
        parser.addSQL("       T.CITY_CODE,");
        parser.addSQL("       P.PRODUCT_ID,");
        parser.addSQL("       R.RSRV_VALUE_CODE,");
        parser.addSQL("       R.RSRV_VALUE,");
        parser.addSQL("       R.RSRV_STR1,");
        parser.addSQL("       R.RSRV_STR2,");
        parser.addSQL("       R.RSRV_STR3,");
        parser.addSQL("       R.RSRV_STR4,");
        parser.addSQL("       R.RSRV_STR5,");
        parser.addSQL("       R.TRADE_ID,");
        parser.addSQL("       R.INST_ID,");
        parser.addSQL("       TO_CHAR(R.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
        parser.addSQL("       TO_CHAR(R.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,");
        parser.addSQL("       TO_CHAR(R.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        parser.addSQL("       R.UPDATE_STAFF_ID,");
        parser.addSQL("       R.UPDATE_DEPART_ID");
        parser.addSQL("  FROM TF_F_USER T, TF_F_USER_PRODUCT P, TF_F_USER_OTHER R");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL("   AND P.PARTITION_ID = T.PARTITION_ID");
        parser.addSQL("   AND P.USER_ID = T.USER_ID");
        parser.addSQL("   AND T.REMOVE_TAG = '0'");
        parser.addSQL("   AND P.END_DATE > SYSDATE");
        parser.addSQL("   AND P.PARTITION_ID = R.PARTITION_ID");
        parser.addSQL("   AND P.USER_ID = R.USER_ID");
        parser.addSQL("   AND R.RSRV_VALUE_CODE = 'NPRI'");
        parser.addSQL("   AND R.END_DATE > SYSDATE");
        parser.addSQL("   AND EXISTS (SELECT A.PARAM_CODE");
        parser.addSQL("          FROM TD_S_COMMPARA A");
        parser.addSQL("         WHERE A.SUBSYS_CODE = 'CSM'");
        parser.addSQL("           AND A.PARAM_ATTR = 3317");
        parser.addSQL("           AND A.END_DATE > SYSDATE");
        parser.addSQL("           AND P.PRODUCT_ID = TO_NUMBER(A.PARAM_CODE))");
        parser.addSQL("   AND T.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL("   AND T.CUST_ID = :CUST_ID");
        parser.addSQL("   AND R.UPDATE_TIME >= TO_DATE(:START_DATE, 'YYYY-MM-DD')");
        parser.addSQL("   AND R.UPDATE_TIME <= TO_DATE(:END_DATE, 'YYYY-MM-DD')");
        parser.addSQL("   AND T.CITY_CODE LIKE '' || :CITY_CODE || '%'");

        
        return Dao.qryByParse(parser, pagination);
    }
    
    
    public static IDataset queryOther(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL("  FROM TF_F_USER_OTHER T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID=:USER_ID ");
        parser.addSQL("  AND T.RSRV_VALUE_CODE = 'N001'  AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");

        return Dao.qryByParse(parser);
    }

    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryOtherNPRI(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL("  FROM TF_F_USER_OTHER T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID=:USER_ID ");
        parser.addSQL("  AND T.RSRV_VALUE_CODE = 'NPRI'  AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");

        return Dao.qryByParse(parser);
    } 
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryMebOtherNPRI(String userId, String rsrvStr10) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_STR10", rsrvStr10);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * ");
        parser.addSQL("  FROM TF_F_USER_OTHER T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID=:USER_ID ");
        parser.addSQL("  AND T.RSRV_STR10=:RSRV_STR10 ");
        parser.addSQL("  AND T.RSRV_VALUE_CODE = 'NPRI'  AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");

        return Dao.qryByParse(parser);
    }
    
    /**
     * @Description:集团专网查询
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProduct(IData data) throws Exception
    {
    	SQLParser parser = new SQLParser(data);

        parser.addSQL("SELECT T.SERIAL_NUMBER,P.PRODUCT_ID, P.* ");
        parser.addSQL("FROM TF_F_USER T, TF_F_USER_PRODUCT P, TD_S_COMMPARA C ");
        parser.addSQL("WHERE 1 = 1 ");
        parser.addSQL("AND P.USER_ID = T.USER_ID ");
        parser.addSQL("AND C.PARAM_ATTR = '3317' ");
        parser.addSQL("AND P.PARTITION_ID = MOD(T.USER_ID, 10000) ");
        parser.addSQL("AND T.CUST_ID = :CUST_ID ");
        parser.addSQL("AND P.PRODUCT_ID = C.PARAM_CODE ");
        parser.addSQL("AND T.REMOVE_TAG = '0' ");
        parser.addSQL("AND P.END_DATE > SYSDATE ");

        return Dao.qryByParse(parser);

    }
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryOtherNpriByUserInstID(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID,RSRV_VALUE_CODE, ");
        parser.addSQL("     RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5, ");
        parser.addSQL("     RSRV_NUM6,RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11, ");
        parser.addSQL("     RSRV_NUM12,RSRV_NUM13,RSRV_NUM14,RSRV_NUM15,RSRV_NUM16, ");
        parser.addSQL("     RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1, ");
        parser.addSQL("     RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7, ");
        parser.addSQL("     RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12, ");
        parser.addSQL("     RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,    ");
        parser.addSQL("     RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,    ");
        parser.addSQL("     RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26,RSRV_STR27,    ");
        parser.addSQL("     RSRV_STR28,RSRV_STR29,RSRV_STR30,                          ");
        parser.addSQL("     TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE6, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE6,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE7, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE7,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE8, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE8,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE9, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE9,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE10, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE10, ");
        parser.addSQL("     RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,                   ");
        parser.addSQL("     RSRV_TAG5,RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,                   ");
        parser.addSQL("     RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID,DEPART_ID,       ");
        parser.addSQL("     TO_CHAR(TRADE_ID) TRADE_ID,                                ");
        parser.addSQL("     TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,   ");
        parser.addSQL("     TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,       ");
        parser.addSQL("     TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("     UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,                   ");
        parser.addSQL("     TO_CHAR(INST_ID) INST_ID                                   ");
        parser.addSQL("  FROM TF_F_USER_OTHER  ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND USER_ID=:USER_ID ");
        parser.addSQL("  AND INST_ID=:INST_ID ");
        parser.addSQL("  AND RSRV_VALUE_CODE = 'NPRI'  AND END_DATE > SYSDATE ");
        
        return Dao.qryByParse(parser);
    } 
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryOtherNpriNotUserInstID(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID,RSRV_VALUE_CODE, ");
        parser.addSQL("     RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5, ");
        parser.addSQL("     RSRV_NUM6,RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11, ");
        parser.addSQL("     RSRV_NUM12,RSRV_NUM13,RSRV_NUM14,RSRV_NUM15,RSRV_NUM16, ");
        parser.addSQL("     RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1, ");
        parser.addSQL("     RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7, ");
        parser.addSQL("     RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12, ");
        parser.addSQL("     RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,    ");
        parser.addSQL("     RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,    ");
        parser.addSQL("     RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26,RSRV_STR27,    ");
        parser.addSQL("     RSRV_STR28,RSRV_STR29,RSRV_STR30,                          ");
        parser.addSQL("     TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE4, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE4,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE5, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE6, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE6,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE7, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE7,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE8, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE8,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE9, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE9,   ");
        parser.addSQL("     TO_CHAR(RSRV_DATE10, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE10, ");
        parser.addSQL("     RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,                   ");
        parser.addSQL("     RSRV_TAG5,RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,                   ");
        parser.addSQL("     RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID,DEPART_ID,       ");
        parser.addSQL("     TO_CHAR(TRADE_ID) TRADE_ID,                                ");
        parser.addSQL("     TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,   ");
        parser.addSQL("     TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,       ");
        parser.addSQL("     TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("     UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,                   ");
        parser.addSQL("     TO_CHAR(INST_ID) INST_ID                                   ");
        parser.addSQL("  FROM TF_F_USER_OTHER  ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND USER_ID=:USER_ID ");
        parser.addSQL("  AND INST_ID !=:INST_ID ");
        parser.addSQL("  AND RSRV_VALUE_CODE = 'NPRI'  AND END_DATE > SYSDATE ");
        
        return Dao.qryByParse(parser);
    } 
    
    /**
     * 判断返回记录
     * @param userId
     * @return
     * @throws Exception
     */
    public static int queryOtherCounts(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT COUNT(*) CNT ");
        parser.addSQL("  FROM TF_F_USER_OTHER T ");
        parser.addSQL("  WHERE 1 = 1 ");
        parser.addSQL("  AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL("  AND T.USER_ID=:USER_ID ");
        parser.addSQL("  AND T.RSRV_VALUE_CODE = 'NPRI'  AND T.END_DATE > SYSDATE ");
        IDataset dataset = Dao.qryByParse(parser);
        String count = dataset.getData(0).getString("CNT");

        return Integer.parseInt(count);
    } 
    
}
