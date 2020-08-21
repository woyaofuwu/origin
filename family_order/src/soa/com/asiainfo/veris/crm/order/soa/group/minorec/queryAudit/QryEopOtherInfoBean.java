package com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class QryEopOtherInfoBean {

    public static IDataset qryAuditStaffInfoByIbsysid(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.ATTR_VALUE ");
        parser.addSQL(" FROM TF_B_EOP_OTHER T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.ATTR_CODE = 'AUDITSTAFF' ");
        parser.addSQL(" AND T.SUB_IBSYSID=(SELECT MAX(C.SUB_IBSYSID) FROM TF_B_EOP_OTHER C ");
        parser.addSQL(" WHERE C.ATTR_CODE='AUDITSTAFF' AND C.IBSYSID=:IBSYSID) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryAuditStaffInfoHByIbsysid(IData param) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", param.getString("IBSYSID"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.ATTR_VALUE ");
        parser.addSQL(" FROM TF_BH_EOP_OTHER T ");
        parser.addSQL(" WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.ATTR_CODE = 'AUDITSTAFF' ");
        parser.addSQL(" AND T.SUB_IBSYSID=(SELECT MAX(C.SUB_IBSYSID) FROM TF_BH_EOP_OTHER C ");
        parser.addSQL(" WHERE C.ATTR_CODE='AUDITSTAFF' AND C.IBSYSID=:IBSYSID) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
