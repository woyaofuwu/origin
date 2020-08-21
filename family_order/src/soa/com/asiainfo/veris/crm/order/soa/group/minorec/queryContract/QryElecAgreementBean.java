package com.asiainfo.veris.crm.order.soa.group.minorec.queryContract;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QryElecAgreementBean {

    public static IDataset queryAreaInfos(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT AREA_CODE, '['||AREA_CODE||']'||AREA_NAME AS AREA_NAME, PARENT_AREA_CODE FROM TD_M_AREA T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.AREA_CODE = :AREA_CODE ");
        parser.addSQL(" AND T.PARENT_AREA_CODE = :PARENT_AREA_CODE ");
        parser.addSQL(" ORDER BY T.AREA_CODE   ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static IData queryOrgByKeyWord(IData data, Pagination pagination)
            throws Exception {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT T.DEPART_ID ORGANIZE_ID,T.DEPART_CODE CODE,T.DEPART_NAME ORGANIZE_NAME FROM TD_M_DEPART T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.VALIDFLAG ='0' ");
        parser.addSQL(" AND (T.DEPART_CODE || T.DEPART_NAME LIKE '%'||:KEY_WORD||'%' OR T.DEPART_ID = :KEY_WORD)");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN).first();
    }
    
    public static IDataset queryOrgInfosByAreaCode(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.DEPART_ID ORGANIZE_ID,T.DEPART_CODE CODE,T.DEPART_NAME ORGANIZE_NAME FROM TD_M_DEPART T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.VALIDFLAG ='0' ");
        parser.addSQL(" AND T.DEPART_KIND_CODE IN ( '100','801') ");
        parser.addSQL(" AND AREA_CODE = :AREA_CODE ");
        parser.addSQL(" ORDER BY T.DEPART_CODE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryOrgInfosByParentOrgId(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.DEPART_ID ORGANIZE_ID,T.DEPART_CODE CODE,T.DEPART_NAME ORGANIZE_NAME FROM TD_M_DEPART T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.VALIDFLAG ='0' ");
        parser.addSQL(" AND T.PARENT_DEPART_ID = :PARENT_ORGANIZE_ID ");
        parser.addSQL(" AND AREA_CODE = :AREA_CODE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
}
