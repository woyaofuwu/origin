package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;

public class CmOnlineUtil {

    // 更新密钥表卡数据公钥数据
    public static int updatePushCardPkey(String KEY, String TRANSACTION_ID) throws Exception {
        IData param = new DataMap();
        param.put("KEY", KEY);
        param.put("TRANSACTION_ID", TRANSACTION_ID);
        StringBuilder updSql = new StringBuilder();
        updSql.append(" UPDATE TD_B_PUSHKEY_CMONLINE ");
        updSql.append(" SET KEY = :KEY, ");
        updSql.append(" UPDATE_TIME = SYSDATE ");
        updSql.append(" WHERE TRANSACTION_ID = :TRANSACTION_ID ");
        return Dao.executeUpdate(updSql, param, Route.CONN_CRM_CEN);
    }

    // 更新密钥表
    public static int updatePushKey(String KEY, String PROV_CODE, String EXPIRE_DATE, String TRANSACTION_ID) throws Exception {
        IData param = new DataMap();
        param.put("KEY", KEY);
        param.put("PROV_CODE", PROV_CODE);
        param.put("EXPIRE_DATE", EXPIRE_DATE);
        param.put("TRANSACTION_ID", TRANSACTION_ID);
        StringBuilder updSql = new StringBuilder();
        updSql.append(" UPDATE TD_B_PUSHKEY_CMONLINE ");
        updSql.append(" SET KEY = :KEY, ");
        updSql.append(" PROV_CODE = :PROV_CODE, ");
        updSql.append(" EXPIRE_DATE = TO_DATE(:EXPIRE_DATE, 'YYYY-MM-DD'), ");
        updSql.append(" CERT_EXPDATE = :EXPIRE_DATE, ");
        updSql.append(" UPDATE_TIME = SYSDATE ");
        updSql.append(" WHERE TRANSACTION_ID = :TRANSACTION_ID ");
        return Dao.executeUpdate(updSql, param, Route.CONN_CRM_CEN);
    }

    // 插入密钥表
    public static void insertPushKey(IData inParams) throws Exception {
        Dao.insert("TD_B_PUSHKEY_CMONLINE", inParams, Route.CONN_CRM_CEN);// 插入采集工单信息
    }

    // 查询密钥表里是否有记录
    public static IDataset queryPushKey(String targetCode) throws Exception {
        IData inparams = new DataMap();
        inparams.put("PROV_CODE", targetCode);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT TRANSACTION_ID, PROV_CODE, KEY, CERT_EXPDATE, RSRV_STR1, RSRV_STR2, RSRV_STR3, CREATE_DATE, ");
        parser.addSQL(" UPDATE_TIME, EXPIRE_DATE, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
        parser.addSQL(" FROM TD_B_PUSHKEY_CMONLINE A ");
        parser.addSQL(" WHERE A.PROV_CODE = :PROV_CODE ");
        parser.addSQL(" AND A.EXPIRE_DATE > SYSDATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    // 查询密钥表里是否有卡数据公钥数据
    public static IDataset queryPushCardPkey(String transaction_id) throws Exception {
        IData inparams = new DataMap();
        inparams.put("TRANSACTION_ID", transaction_id);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT TRANSACTION_ID, PROV_CODE, KEY, CERT_EXPDATE, RSRV_STR1, RSRV_STR2, RSRV_STR3, CREATE_DATE, ");
        parser.addSQL(" UPDATE_TIME, EXPIRE_DATE, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
        parser.addSQL(" FROM TD_B_PUSHKEY_CMONLINE A ");
        parser.addSQL(" WHERE A.TRANSACTION_ID = :TRANSACTION_ID ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
