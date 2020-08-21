package com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @author ckh
 * @date 2018/10/18.
 */
public class ElecAgrePdfDefQry
{
    public static IData queryTemplateInfoById(String id) throws Exception
    {
        IData param = new DataMap();
        param.put("AGRE_PDF_ID", id);

        //StringBuilder sf = new StringBuilder(1000);
        //sf.append(" SELECT AGRE_PDF_ID,AGRE_PDF_NAME,REMARK,VALID_TAG,CREATE_TIME,CREATE_STAFF_ID,CREATE_DEPART_ID,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,START_DATE,END_DATE ");
        //sf.append(" FROM TD_B_ELEC_AGRE_PDF_DEF ");
        //sf.append(" WHERE ");
        //sf.append(" AGRE_PDF_ID = :AGRE_PDF_ID ");
        //SQLParser parser = new SQLParser(param);
        //parser.addSQL(sf.toString());

        String[] key = {"AGRE_PDF_ID"};


        return Dao.qryByPK("TD_B_ELEC_AGRE_PDF_DEF", param, key, Route.CONN_CRM_CEN);
    }

    public static IDataset queryElementInfosById(String templateId) throws Exception {
        IData param = new DataMap();
        param.put("AGRE_PDF_ID", templateId);

        StringBuilder sf = new StringBuilder(1000);
        sf.append(" SELECT AGRE_PDF_ID,PDF_ELE_NAME,PDF_ELE_CODE,PDF_ELE_TYPE,PDF_ELE_STYLE,PDF_SUPER_ELE_CODE,PDF_ELE_INDEX,VALID_TAG,CREATE_TIME,CREATE_STAFF_ID,CREATE_DEPART_ID,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,START_DATE,END_DATE ");
        sf.append(" FROM TD_B_ELEC_AGRE_PDF_ELE ");
        sf.append(" WHERE ");
        sf.append(" AGRE_PDF_ID = :AGRE_PDF_ID ");
        sf.append(" AND VALID_TAG = '0' ");

        SQLParser parser = new SQLParser(param);
        parser.addSQL(sf.toString());

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryTableInfoByIdAndSupEleCode(String templateId, String attrCode) throws Exception {
        IData param = new DataMap();
        param.put("AGRE_PDF_ID", templateId);
        param.put("PDF_SUPER_ELE_CODE", attrCode);

        StringBuilder sf = new StringBuilder(1000);
        sf.append(" SELECT AGRE_PDF_ID,PDF_ELE_NAME,PDF_ELE_CODE,PDF_ELE_TYPE,PDF_ELE_STYLE,PDF_SUPER_ELE_CODE,PDF_ELE_INDEX,VALID_TAG,CREATE_TIME,CREATE_STAFF_ID,CREATE_DEPART_ID,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,START_DATE,END_DATE ");
        sf.append(" FROM TD_B_ELEC_AGRE_PDF_ELE ");
        sf.append(" WHERE ");
        sf.append(" AGRE_PDF_ID = :AGRE_PDF_ID ");
        sf.append(" AND PDF_SUPER_ELE_CODE = :PDF_SUPER_ELE_CODE ");
        sf.append(" AND VALID_TAG = '0' ");
        sf.append(" ORDER BY PDF_ELE_INDEX ");

        SQLParser parser = new SQLParser(param);
        parser.addSQL(sf.toString());

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
