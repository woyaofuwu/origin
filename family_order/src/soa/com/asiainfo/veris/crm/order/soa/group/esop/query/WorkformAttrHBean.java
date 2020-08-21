package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformAttrHBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformAttrH(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_ATTR", param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static IDataset qryEopAttrBySubIbsysid(String subIbsysid) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static IDataset qryEopAttrByIbsysidNodeid(String ibsysid, String nodeID, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", nodeID);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_IBSYSIDNODEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
	
	public static IDataset qryEopAttrBySubIbsysidRecordNum(String subIbsysid, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_SUBIBSYSIDRECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
	/**
     * 查询节点recordnum,不包括0的公共数据。
     * 
     * @param subIbsysid
     * @return
     * @throws Exception
     */
    public static IDataset qryRecordNumBySubIbsysid(String subIbsysid) throws Exception {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_RECORDNUM_BY_SUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

	public static IDataset qryMaxSubibsysyid(IData param) throws Exception {
		IDataset info = Dao.qryByCode("TF_BH_EOP_ATTR", "SEL_MAXSUBIBSYSID_BY_IBSYSYID", param, Route.getJourDb(BizRoute.getRouteId()));
		return info;
	}
	
	public static IDataset getMaxsubEopAttrToListForHis(IData param) throws Exception {
	    return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_MAXSUBIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryLineNoByIbsysid(IData param) throws Exception {
	    return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_NODEID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryMaxSubIbsysId(IData param) throws Exception {
	    return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_MAXSUB_BY_ATTRCODE", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryMaxRecordNumByIbsysid(IData param) throws Exception {
	    return Dao.qryByCodeParser("TF_BH_EOP_ATTR", "SEL_MAXNUM_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryHisInfoByProductNo(String subIBsysid, String productNo) throws Exception {
		IData param = new DataMap();
        param.put("PRODUCT_NO", productNo);
        param.put("SUB_IBSYSID", subIBsysid);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT T.SUB_IBSYSID,T.IBSYSID,T.ACCEPT_MONTH,T.SEQ,T.GROUP_SEQ,T.NODE_ID,T.ATTR_CODE,T.ATTR_NAME,T.ATTR_VALUE, ");
        strSql.append(" T.PARENT_ATTR_CODE,T.RECORD_NUM,TO_CHAR(T.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,T.RSRV_STR1,T.RSRV_STR2,T.ATTR_TYPE");
        strSql.append(" FROM TF_BH_EOP_ATTR T");
        strSql.append(" WHERE T.ATTR_CODE='PRODUCTNO'");
        strSql.append(" AND T.ATTR_VALUE=:PRODUCT_NO");
        strSql.append(" AND T.SUB_IBSYSID=:SUB_IBSYSID");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
	}

    public static IDataset qryHisEopAttrByAttrCodeAttrValue(String attrCode, String attrValue) throws Exception {
        IData param = new DataMap();
        param.put("ATTR_CODE", attrCode);
        param.put("ATTR_VALUE", attrValue);
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT DISTINCT A.IBSYSID FROM TF_BH_EOP_ATTR A ");
        strSql.append(" WHERE A.ATTR_CODE=:ATTR_CODE ");
        strSql.append(" AND A.ATTR_VALUE=:ATTR_VALUE ");
        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryNewHisEopAttrByIbsysidAndNodeId(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.* FROM TF_BH_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND A.RECORD_NUM=:RECORD_NUM ");
        sql.addSQL(" AND A.SUB_IBSYSID =  ( ");
        sql.addSQL(" SELECT MAX(B.SUB_IBSYSID) FROM TF_BH_EOP_ATTR B ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND B.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND B.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND B.RECORD_NUM=:RECORD_NUM ");
        sql.addSQL(" ) ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset getInfoBySubIbsysidRecordNum(IData data) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SUB_IBSYSID", data.getString("SUB_IBSYSID","")); 
    	param.put("RECORD_NUM", data.getString("RECORD_NUM","")); 
      
        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT T.SUB_IBSYSID,T.IBSYSID,T.SEQ,T.GROUP_SEQ,T.NODE_ID,T.ACCEPT_MONTH,T.ATTR_CODE,T.ATTR_NAME, ");
        strSql.append(" T.ATTR_VALUE,T.ATTR_TYPE,T.PARENT_ATTR_CODE,T.RECORD_NUM,T.UPDATE_TIME,T.RSRV_STR1,T.RSRV_STR2 ");
        strSql.append(" FROM TF_BH_EOP_ATTR T ");
        strSql.append(" WHERE T.SUB_IBSYSID = :SUB_IBSYSID ");
        strSql.append(" AND T.RECORD_NUM = :RECORD_NUM ");
        strSql.append(" AND T.GROUP_SEQ = ");
        strSql.append(" (SELECT MAX(T1.GROUP_SEQ) ");
        strSql.append(" FROM TF_BH_EOP_EOMS T1 ");
        strSql.append(" WHERE T.IBSYSID = T1.IBSYSID ");
        strSql.append(" AND T1.OPER_TYPE IN ('renewWorkSheet', 'newWorkSheet') ");
        strSql.append("  AND T1.RECORD_NUM = :RECORD_NUM) ");

        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryMaxHisAttrByAttrCode(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.* FROM TF_BH_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND A.ATTR_CODE=:ATTR_CODE ");
        sql.addSQL(" AND A.SUB_IBSYSID =  ( ");
        sql.addSQL(" SELECT MAX(B.SUB_IBSYSID) FROM TF_BH_EOP_ATTR B ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND B.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND B.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND B.ATTR_CODE=:ATTR_CODE ");
        sql.addSQL(" ) ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryMaxHisAttrByAttrCodeRecodeNum(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT A.* FROM TF_BH_EOP_ATTR A ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND A.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND A.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND A.ATTR_CODE=:ATTR_CODE ");
        sql.addSQL(" AND A.RECORD_NUM=:RECORD_NUM ");
        sql.addSQL(" AND A.SUB_IBSYSID =  ( ");
        sql.addSQL(" SELECT MAX(B.SUB_IBSYSID) FROM TF_BH_EOP_ATTR B ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND B.IBSYSID=:IBSYSID ");
        sql.addSQL(" AND B.NODE_ID=:NODE_ID ");
        sql.addSQL(" AND B.ATTR_CODE=:ATTR_CODE ");
        sql.addSQL(" AND B.RECORD_NUM=:RECORD_NUM ");
        sql.addSQL(" ) ");
        return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }
}
