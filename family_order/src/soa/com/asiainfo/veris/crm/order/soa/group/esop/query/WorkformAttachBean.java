package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformAttachBean
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformAttach(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_ATTACH", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryAttachByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_ATTACH", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryAttachByIbsysidNode(String ibsysid, String nodeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("NODE_ID", nodeId);
        return Dao.qryByCode("TF_B_EOP_ATTACH", "SEL_BY_IBSYSID_NODE", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryAttachBySubIbsysidNum(String subIbsysid, String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SUB_IBSYSID", subIbsysid);
    	param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_ATTACH", "SEL_BY_SUBIBSYSID_NUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delAttachByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_ATTACH", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryMaxEopAttachByIbsysId(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_ATTACH", "SEL_BY_IBSYSID_MAXSUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryEopAttrBySubIbsysid(String subIbsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SUB_IBSYSID", subIbsysid);
        return Dao.qryByCode("TF_B_EOP_ATTACH", "SEL_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryOldWorkFormAttachInfo(String seq, String subibsysId) throws Exception
    {
        IData param = new DataMap();
        param.put("SEQ", seq);
        param.put("SUB_IBSYSID", subibsysId);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append("SELECT ");
        strSql.append("  T.IBSYSID, T.SEQ, T.SUB_IBSYSID, T.BPM_ID, T.NODE_ID, T.TAG, T.ATTACH_NAME, T.DISPLAY_NAME, T.ATTACH_LENGTH, ");
        strSql.append("  T.ATTACH_URL, T.ATTACH_LOCAL_PATH, T.ATTACH_CITY_CODE, T.ATTACH_EPARCHY_CODE, T.ATTACH_DEPART_ID, ");
        strSql.append("  T.ATTACH_DEPART_NAME, T.ATTACH_STAFF_ID, T.ATTACH_STAFF_NAME, T.ATTACH_STAFF_PHONE, T.INSERT_TIME, T.MONTH, T.REMARK ");
        strSql.append("FROM TF_F_WORKFORM_ATTACH T ");
        strSql.append("WHERE t.SEQ = :SEQ AND T.SUB_IBSYSID = :SUB_IBSYSID");

        return Dao.qryBySql(strSql,param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void insertAttach(IData param) throws Exception
    {
        Dao.insert("TF_B_EOP_ATTACH", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryContractAttach(IData param) throws Exception {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM ( ");
        parser.addSQL(" SELECT DISTINCT C.IBSYSID, ");
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
        parser.addSQL(" C.ATTACH_TYPE, ");
        parser.addSQL(" ROW_NUMBER() OVER(PARTITION BY C.FILE_ID ORDER BY C.SUB_IBSYSID DESC) G ");
        parser.addSQL(" FROM TF_B_EOP_ATTACH C WHERE C.IBSYSID=:IBSYSID ) R ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND R.G<=1 ");
        parser.addSQL(" AND SUB_IBSYSID = (SELECT MAX(SUB_IBSYSID) FROM TF_B_EOP_ATTACH A WHERE A.IBSYSID=:IBSYSID) ");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEopAllAttachByIbsysid(String ibsysid,String attachType) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("ATTACH_TYPE", attachType);
        return Dao.qryByCode("TF_B_EOP_ATTACH", "SEL_ALLATTACH_BY_IBSYSID_MAXSUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryExistsFile(IData param) throws Exception 
    {
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL(" SELECT * FROM TF_B_EOP_ATTACH t ");
    	parser.addSQL(" WHERE SUB_IBSYSID = :SUB_IBSYSID ");
    	parser.addSQL(" AND FILE_ID = :FILE_ID ");
    	return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
}
