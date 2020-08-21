package com.asiainfo.veris.crm.order.soa.group.minorec.queryContract;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QryAgreementBean {

    public static IData qryAgreementInfo(IData param) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT A.AGREEMENT_ID, ");
        sql.append("        A.PDF_FILE, ");
        sql.append("        A.PRODUCT_ID, ");
        sql.append("        B.ARCHIVES_ID, ");
        sql.append("        B.ARCHIVES_NAME, ");
        sql.append("        B.ARCHIVES_TYPE, ");
        sql.append("        B.SUB_ARCHIVES_TYPE, ");
        sql.append("        B.START_DATE, ");
        sql.append("        B.END_DATE, ");
        sql.append("        B.ARCHIVES_STATE, ");
        sql.append("        B.STATE_DESC, ");
        sql.append("        B.ARCHIVES_ATTACH ");
        sql.append(" FROM TF_F_ELECTRONIC_AGREEMENT A, TF_F_ELECTRONIC_ARCHIVES B ");
        sql.append(" WHERE A.ARCHIVES_ID = B.ARCHIVES_ID ");
        sql.append(" AND B.END_DATE > SYSDATE ");
        sql.append(" AND B.STATE_DESC <> '4' ");
        sql.append(" AND A.AGREEMENT_ID = :AGREEMENT_ID ");
        
        IDataset datas = Dao.qryBySql(sql, param,Route.CONN_CRM_CG);
        if(DataUtils.isNotEmpty(datas)){
            return datas.first();
        }else{
            return new DataMap();
        }
    }
    
    public static IDataset qryAgreementAttachInfo(IData param) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT A.AGREEMENT_ID, ");
        sql.append("        A.PDF_FILE, ");
        sql.append("        A.PRODUCT_ID, ");
        sql.append("        B.ARCHIVES_ID, ");
        sql.append("        B.ARCHIVES_NAME, ");
        sql.append("        B.ARCHIVES_TYPE, ");
        sql.append("        B.SUB_ARCHIVES_TYPE, ");
        sql.append("        B.START_DATE, ");
        sql.append("        B.END_DATE, ");
        sql.append("        B.ARCHIVES_STATE, ");
        sql.append("        B.STATE_DESC, ");
        sql.append("        B.ARCHIVES_ATTACH ");
        sql.append(" FROM TF_F_ELECTRONIC_AGRE_ATTACH A, TF_F_ELECTRONIC_ARCHIVES B ");
        sql.append(" WHERE A.ARCHIVES_ID = B.ARCHIVES_ID ");
        sql.append(" AND B.STATE_DESC <> '4' ");
        sql.append(" AND A.AGREEMENT_ID = :AGREEMENT_ID ");

        return Dao.qryBySql(sql, param,Route.CONN_CRM_CG);
        
    }
    
    public static IDataset qryAgreementDef(IData param)throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT A.PRODUCT_ID, ");
        sql.append("        A.AGREEMENT_DEF_ID, ");
        sql.append("        B.AGREEMENT_NAME, ");
        sql.append("        B.AGREEMENT_DEF, ");
        sql.append("        B.AGREEMENT_TYPE ");
        sql.append("  FROM TD_B_ELEC_AGREEMENT_PRODUCT A, TD_B_ELEC_AGREEMENT_DEF B ");
        sql.append("  WHERE A.AGREEMENT_DEF_ID = B.AGREEMENT_DEF_ID ");
        sql.append("  AND A.PRODUCT_ID = :PRODUCT_ID ");
        sql.append("  AND B.AGREEMENT_TYPE = :AGREEMENT_TYPE ");
        
        return Dao.qryBySql(sql, param,Route.CONN_CRM_CEN);
    }
    
}
