package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class SubReleInfoQry {
	
	public static IDataset qryInfoByTemplet(String bpmTempletId) throws Exception
	{
		IData param = new DataMap();
		param.put("BPM_TEMPLET_ID", bpmTempletId);
        return Dao.qryByCode("TD_B_EWE_SUB_RELA", "SEL_BY_BPMTEMPLETID", param, Route.CONN_CRM_CEN);
	}
	
	public static IDataset qryParentBpmtempletIdByIbsysId(String ibsysId) throws Exception {
        IData params = new DataMap();
        params.put("IBSYSID", ibsysId);
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" SELECT T.BPM_TEMPLET_ID ");
        parser.addSQL(" FROM TF_B_EWE T ");
        parser.addSQL(" WHERE T.BI_SN =:IBSYSID ");
        parser.addSQL(" AND T.TEMPLET_TYPE = '0' ");
        parser.addSQL(" UNION ");
        parser.addSQL(" SELECT T.BPM_TEMPLET_ID ");
        parser.addSQL(" FROM TF_BH_EWE T ");
        parser.addSQL(" WHERE T.BI_SN =:IBSYSID ");
        parser.addSQL(" AND T.TEMPLET_TYPE = '0' ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
	
	// 查询附件
    public static IDataset qryGroupAttach(IData params) throws Exception {
        IData param1 = new DataMap();
        param1.put("IBSYSID", params.getString("IBSYSID"));
        param1.put("ATTACH_TYPE", params.getString("ATTACH_TYPE"));
        SQLParser parser = new SQLParser(param1);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.SEQ, ");
        parser.addSQL(" C.GROUP_SEQ, ");
        parser.addSQL(" C.SUB_IBSYSID, ");
        parser.addSQL(" C.NODE_ID, ");
        parser.addSQL(" C.DISPLAY_NAME, ");
        parser.addSQL(" C.ATTACH_NAME, ");
        parser.addSQL(" C.ATTACH_LENGTH, ");
        parser.addSQL(" C.ATTACH_URL, ");
        parser.addSQL(" C.ATTACH_LOCAL_PATH, ");
        parser.addSQL(" C.ATTACH_CITY_CODE, ");
        parser.addSQL(" C.ATTACH_EPARCHY_CODE, ");
        parser.addSQL(" C.ATTACH_DEPART_ID, ");
        parser.addSQL(" C.ATTACH_DEPART_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_ID, ");
        parser.addSQL(" C.ATTACH_STAFF_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_PHONE, ");
        parser.addSQL(" C.FILE_ID, ");
        parser.addSQL(" TO_CHAR(C.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" C.REMARK, ");
        parser.addSQL(" TO_CHAR(C.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" C.ACCEPT_MONTH, ");
        parser.addSQL(" C.VALID_TAG, ");
        parser.addSQL(" C.ATTACH_TYPE FROM ");
        parser.addSQL(" (SELECT * FROM (SELECT T.SUB_IBSYSID ");
        parser.addSQL(" FROM TF_B_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
        parser.addSQL(" GROUP BY SUB_IBSYSID ");
        parser.addSQL(" ORDER BY SUB_IBSYSID DESC) A WHERE ROWNUM = 1) E,  ");
        parser.addSQL(" (SELECT * FROM (SELECT T.GROUP_SEQ ");
        parser.addSQL(" FROM TF_B_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
        parser.addSQL(" GROUP BY GROUP_SEQ ");
        parser.addSQL(" ORDER BY GROUP_SEQ DESC) A WHERE ROWNUM = 1) Q, ");
        parser.addSQL(" TF_B_EOP_ATTACH C  ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = C.SUB_IBSYSID  ");
        parser.addSQL(" AND C.IBSYSID=:IBSYSID ");
        parser.addSQL(" AND C.ATTACH_TYPE=:ATTACH_TYPE ");
        parser.addSQL(" AND C.GROUP_SEQ=Q.GROUP_SEQ ");
        IDataset accachList = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return accachList;
    }
	
	// 查询已经完成的工单的附件
    public static IDataset qryFinishGroupAttach(IData params) throws Exception {
        IData param1 = new DataMap();
        param1.put("IBSYSID", params.getString("IBSYSID"));
        param1.put("ATTACH_TYPE", params.getString("ATTACH_TYPE"));
        SQLParser parser = new SQLParser(param1);
        parser.addSQL(" SELECT C.IBSYSID, ");
        parser.addSQL(" C.SEQ, ");
        parser.addSQL(" C.GROUP_SEQ, ");
        parser.addSQL(" C.SUB_IBSYSID, ");
        parser.addSQL(" C.NODE_ID, ");
        parser.addSQL(" C.DISPLAY_NAME, ");
        parser.addSQL(" C.ATTACH_NAME, ");
        parser.addSQL(" C.ATTACH_LENGTH, ");
        parser.addSQL(" C.ATTACH_URL, ");
        parser.addSQL(" C.ATTACH_LOCAL_PATH, ");
        parser.addSQL(" C.ATTACH_CITY_CODE, ");
        parser.addSQL(" C.ATTACH_EPARCHY_CODE, ");
        parser.addSQL(" C.ATTACH_DEPART_ID, ");
        parser.addSQL(" C.ATTACH_DEPART_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_ID, ");
        parser.addSQL(" C.ATTACH_STAFF_NAME, ");
        parser.addSQL(" C.ATTACH_STAFF_PHONE, ");
        parser.addSQL(" C.FILE_ID, ");
        parser.addSQL(" TO_CHAR(C.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
        parser.addSQL(" C.REMARK, ");
        parser.addSQL(" TO_CHAR(C.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL(" C.ACCEPT_MONTH, ");
        parser.addSQL(" C.VALID_TAG, ");
        parser.addSQL(" C.ATTACH_TYPE FROM ");
        parser.addSQL(" (SELECT * FROM (SELECT T.SUB_IBSYSID ");
        parser.addSQL(" FROM TF_BH_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
        parser.addSQL(" GROUP BY SUB_IBSYSID ");
        parser.addSQL(" ORDER BY SUB_IBSYSID DESC) A WHERE ROWNUM = 1) E,  ");
        parser.addSQL(" (SELECT * FROM (SELECT T.GROUP_SEQ ");
        parser.addSQL(" FROM TF_BH_EOP_ATTACH T WHERE T.IBSYSID =:IBSYSID ");
        parser.addSQL(" AND T.ATTACH_TYPE = :ATTACH_TYPE ");
        parser.addSQL(" GROUP BY GROUP_SEQ ");
        parser.addSQL(" ORDER BY GROUP_SEQ DESC) A WHERE ROWNUM = 1) Q, ");
        parser.addSQL(" TF_BH_EOP_ATTACH C  ");
        parser.addSQL(" WHERE E.SUB_IBSYSID = C.SUB_IBSYSID  ");
        parser.addSQL(" AND C.IBSYSID=:IBSYSID ");
        parser.addSQL(" AND C.ATTACH_TYPE=:ATTACH_TYPE ");
        parser.addSQL(" AND C.GROUP_SEQ=Q.GROUP_SEQ ");
        IDataset accachList = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return accachList;
    }

}
