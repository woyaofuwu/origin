package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryGrpBizAuditInfoBean extends CSBizBean
{

	private static final long serialVersionUID = 1L;

	/**
	 * 查询文件
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset queryGrpAuditInfos(IData inparams, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL("SELECT A.AUDIT_ID,");
    	parser.addSQL("       A.ACCEPT_MONTH,");
    	parser.addSQL("       A.BIZ_TYPE,");
    	parser.addSQL("       DECODE(A.BIZ_TYPE,'1','单条','2','批量') BIZ_TYPE_NAME,");
    	parser.addSQL("       A.TRADE_TYPE_CODE,");
    	parser.addSQL("       A.GROUP_ID,");
    	parser.addSQL("       A.CUST_NAME,");
    	parser.addSQL("       A.GRP_SN,");
    	parser.addSQL("       A.CONTRACT_ID,");
    	parser.addSQL("       A.VOUCHER_FILE_LIST,");
    	parser.addSQL("       A.ADD_DISCNTS,");
    	parser.addSQL("       A.DEL_DISCNTS,");
    	parser.addSQL("       A.MOD_DISCNTS,");
    	parser.addSQL("       A.STATE,");
    	parser.addSQL("       TO_CHAR(A.IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,");
    	parser.addSQL("       A.IN_STAFF_ID,");
    	parser.addSQL("       TO_CHAR(A.AUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE,");
    	parser.addSQL("       A.AUDIT_STAFF_ID,");
    	parser.addSQL("       A.AUDIT_DESC,");
    	parser.addSQL("       TO_CHAR(A.REAUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') REAUDIT_DATE,");
    	parser.addSQL("       A.REAUDIT_DESC,");
    	parser.addSQL("       TO_CHAR(A.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,");
    	parser.addSQL("       A.RSRV_STR3,");
    	parser.addSQL("       A.REMARK,");
    	parser.addSQL("       A.RSRV_TAG1,");
    	parser.addSQL("       A.RSRV_STR2,");
    	parser.addSQL("       A.REAUDIT_FILE_LIST,");
    	parser.addSQL("       A.REAUDIT_FILE_LIST_DESC,");
    	parser.addSQL("       TO_CHAR(A.REAUDIT_FILE_LIST_DATE, 'yyyy-mm-dd hh24:mi:ss') REAUDIT_FILE_LIST_DATE ");
    	
    	if(inparams.getString("IN_CITY_DODE")!=null&&!inparams.getString("IN_CITY_DODE", "").equals("")){
    		parser.addSQL("  FROM TF_F_GROUP_BASEINFO_AUDIT A ");
        	parser.addSQL(" WHERE 1 = 1 ");
        	parser.addSQL("   AND SUBSTR(A.IN_STAFF_ID,0,4 ) = :IN_CITY_DODE ");
    	}else{
    		parser.addSQL("  FROM TF_F_GROUP_BASEINFO_AUDIT A");
        	parser.addSQL(" WHERE 1 = 1");
        	parser.addSQL("   AND (A.IN_STAFF_ID = :IN_STAFF_ID OR :IN_STAFF_ID is null) ");
    	}
    	parser.addSQL("   AND A.AUDIT_ID = :AUDIT_ID");
    	parser.addSQL("   AND A.GROUP_ID = :GROUP_ID");
    	parser.addSQL("   AND A.CUST_NAME LIKE '%' || :CUST_NAME || '%'");
    	parser.addSQL("   AND A.STATE = :STATE");
    	parser.addSQL("   AND (A.STATE IN ('2','32') AND '1'=:STATES )");
    	if(inparams.getString("TRADE_TYPE_CODES")!=null&&!inparams.getString("TRADE_TYPE_CODES", "").equals("")){
    		parser.addSQL("   AND A.TRADE_TYPE_CODE in ( "+inparams.getString("TRADE_TYPE_CODES")+" )");
    	}
    	parser.addSQL("   AND A.AUDIT_DATE BETWEEN TO_DATE(:AUDIT_START_DATE, 'yyyy-mm-dd') AND TO_DATE(:AUDIT_END_DATE, 'yyyy-mm-dd hh24:mi:ss')+1");
    	parser.addSQL("   AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE");
    	parser.addSQL("   AND A.AUDIT_STAFF_ID = :AUDIT_STAFF_ID");
    	parser.addSQL("   AND A.IN_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd') AND TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')+1");
    	parser.addSQL(" ORDER BY A.IN_DATE DESC");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * 稽核集团业务稽核工单处理
     * @param inparams
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-7-10
     */
    public int updateGrpAuditInfo(IData inparams) throws Exception
    {
        StringBuilder sql = new StringBuilder("");
        sql.append("UPDATE TF_F_GROUP_BASEINFO_AUDIT A");
        sql.append("   SET A.STATE          = :STATE,");
        sql.append("       A.AUDIT_DATE     = SYSDATE,");
        sql.append("       A.AUDIT_STAFF_ID = :AUDIT_STAFF_ID,");
        sql.append("       A.AUDIT_DESC     = :AUDIT_DESC");
        sql.append(" WHERE A.AUDIT_ID = :AUDIT_ID");
        sql.append("   AND A.ACCEPT_MONTH = :ACCEPT_MONTH");
        sql.append("   AND A.BIZ_TYPE = :BIZ_TYPE");
        return Dao.executeUpdate(sql, inparams, Route.CONN_CRM_CG);
    }
    /**
     * 根据稽核单ID查询稽核单内容
     * @param auditId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    public IDataset queryGrpAuditInfoByAuditId(IData params) throws Exception
    {
    	StringBuilder sql = new StringBuilder("");
    	sql.append("SELECT A.AUDIT_ID,");
    	sql.append("       A.ACCEPT_MONTH,");
    	sql.append("       A.BIZ_TYPE,");
    	sql.append("       A.TRADE_TYPE_CODE,");
    	sql.append("       A.GROUP_ID,");
    	sql.append("       A.CUST_NAME,");
    	sql.append("       A.GRP_SN,");
    	sql.append("       A.CONTRACT_ID,");
    	sql.append("       A.VOUCHER_FILE_LIST,");
    	sql.append("       A.ADD_DISCNTS,");
    	sql.append("       A.DEL_DISCNTS,");
    	sql.append("       A.MOD_DISCNTS,");
    	sql.append("       A.STATE,");
    	sql.append("       TO_CHAR(A.IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,");
    	sql.append("       A.IN_STAFF_ID,");
    	sql.append("       TO_CHAR(A.AUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE,");
    	sql.append("       A.AUDIT_STAFF_ID,");
    	sql.append("       A.AUDIT_DESC,");
    	sql.append("       A.REAUDIT_DESC");
    	sql.append("  FROM TF_F_GROUP_BASEINFO_AUDIT A");
    	sql.append(" WHERE 1 = 1");
    	sql.append("   AND A.AUDIT_ID = :AUDIT_ID");
    	sql.append("   AND A.ACCEPT_MONTH = :ACCEPT_MONTH");
        sql.append("   AND A.BIZ_TYPE = :BIZ_TYPE");
        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
    }
    /**
     * 修改集团业务稽核工单整改信息
     * @param inparams
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    public int updateGrpReAuditInfo(IData inparams) throws Exception
    {
        StringBuilder sql = new StringBuilder("");
        sql.append("UPDATE TF_F_GROUP_BASEINFO_AUDIT A");
        sql.append("   SET A.STATE          = :STATE,");
        if(!"".equals(inparams.getString("RSRV_STR3",""))){
        	sql.append("       A.RSRV_DATE1   = SYSDATE,");
            sql.append("       A.RSRV_STR3   = :RSRV_STR3");
        }else{
        	sql.append("       A.REAUDIT_DATE   = SYSDATE,");
            sql.append("       A.REAUDIT_DESC   = :REAUDIT_DESC");
        }
        if(!inparams.getString("REAUDIT_FILE_LIST", "").equals("")){
        	sql.append(",       A.REAUDIT_FILE_LIST   = :REAUDIT_FILE_LIST");
        	sql.append(",       A.REAUDIT_FILE_LIST_DATE    = SYSDATE");
        }
        if(!inparams.getString("REAUDIT_FILE_LIST_DESC", "").equals("")){
        	sql.append(",       A.REAUDIT_FILE_LIST_DESC   = :REAUDIT_FILE_LIST_DESC");
        }
        sql.append(" WHERE A.AUDIT_ID = :AUDIT_ID");
        sql.append("   AND A.ACCEPT_MONTH = :ACCEPT_MONTH");
        sql.append("   AND A.BIZ_TYPE = :BIZ_TYPE");
        return Dao.executeUpdate(sql, inparams, Route.CONN_CRM_CG);
    }
    /**
     * 查询集团业务工单稽核轨迹
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-8-29
     */
    public IDataset queryGrpAuditLogDetail(IData inparams, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL("SELECT A.AUDIT_ID,");
    	parser.addSQL("       A.OLD_STATE,");
    	parser.addSQL("       A.NEW_STATE,");
    	parser.addSQL("       TO_CHAR(A.OPER_DATE, 'YYYY-MM-DD HH24:MI:SS') OPER_DATE,");
    	parser.addSQL("       A.OPER_STAFF_ID,");
    	parser.addSQL("       A.OPER_DESC,");
    	parser.addSQL("       A.RSRV_STR1,");
    	parser.addSQL("       A.RSRV_STR2,");
    	parser.addSQL("       A.RSRV_STR3");
    	parser.addSQL("  FROM TF_F_GROUP_BASEINFO_AUDIT_LOG A");
    	parser.addSQL(" WHERE A.AUDIT_ID = :AUDIT_ID");
    	parser.addSQL(" ORDER BY A.OPER_DATE DESC");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    /**
	 * 查询集团营销稽核工单的附件
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset queryGrpSaleAuditInfos(IData inparams, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL("SELECT A.AUDIT_ID,");
    	parser.addSQL("       A.ACCEPT_MONTH,");
    	parser.addSQL("       A.BIZ_TYPE,");
    	parser.addSQL("       DECODE(A.BIZ_TYPE,'1','单条','2','批量') BIZ_TYPE_NAME,");
    	parser.addSQL("       A.TRADE_TYPE_CODE,");
    	parser.addSQL("       A.GROUP_ID,");
    	parser.addSQL("       A.CUST_NAME,");
    	parser.addSQL("       A.GRP_SN,");
    	parser.addSQL("       A.CONTRACT_ID,");
    	parser.addSQL("       A.VOUCHER_FILE_LIST,");
    	parser.addSQL("       A.ADD_DISCNTS,");
    	parser.addSQL("       A.DEL_DISCNTS,");
    	parser.addSQL("       A.MOD_DISCNTS,");
    	parser.addSQL("       A.STATE,");
    	parser.addSQL("       TO_CHAR(A.IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,");
    	parser.addSQL("       A.IN_STAFF_ID,");
    	parser.addSQL("       TO_CHAR(A.AUDIT_DATE, 'yyyy-mm-dd hh24:mi:ss') AUDIT_DATE,");
    	parser.addSQL("       A.AUDIT_STAFF_ID,");
    	parser.addSQL("       A.AUDIT_DESC,");
    	parser.addSQL("       A.REAUDIT_DESC,");
    	parser.addSQL("       A.REMARK");
    	parser.addSQL("  FROM TF_F_GROUP_BASEINFO_AUDIT A");
    	parser.addSQL(" WHERE 1 = 1");
    	parser.addSQL("   AND A.AUDIT_ID = :AUDIT_ID");
    	parser.addSQL("   AND A.GROUP_ID = :GROUP_ID");
    	parser.addSQL("   AND A.CUST_NAME LIKE '%' || :CUST_NAME || '%'");
    	parser.addSQL("   AND A.STATE = :STATE" );
    	parser.addSQL("   AND A.TRADE_TYPE_CODE = 3606 ");
    	parser.addSQL("   AND A.IN_STAFF_ID LIKE '' || :IN_STAFF_ID || '%'");
    	parser.addSQL("   AND A.IN_DATE BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd') AND TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')+1");
    	parser.addSQL(" ORDER BY A.IN_DATE DESC");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * 查询SA工单
     * @param inparams
     * @return
     * @throws Exception
     * @author wuhao
     * @date 2019-7-22
     */
    public IDataset checkSAinfo(IData inparams) throws Exception
    {
    	StringBuilder sql = new StringBuilder("");
    	sql.append("SELECT A.* ");
    	sql.append("  FROM TF_F_GROUPPRODUCT_SA A");
    	sql.append(" WHERE 1 = 1");
    	sql.append("   AND A.RSRV_STR5 = :AUDIT_ID");
        return Dao.qryBySql(sql, inparams, Route.CONN_CRM_CG);
    }
    
    /**
     * 稽核后修改SA状态
     * @param inparams
     * @return
     * @throws Exception
     * @author wuhao
     * @date 2019-7-22
     */
    public int updateSAinfo(IData inparams) throws Exception
    {
        StringBuilder sql = new StringBuilder("");
        sql.append("UPDATE TF_F_GROUPPRODUCT_SA A");
        sql.append("   SET A.REMOVE_TAG = :REMOVE_TAG ");
        sql.append(" WHERE A.RSRV_STR5 = :AUDIT_ID");
        return Dao.executeUpdate(sql, inparams, Route.CONN_CRM_CG);
    }
    
	public boolean deleteSAinfo(IData param) throws Exception {
		return Dao.delete("TF_F_GROUPPRODUCT_SA", param,new String[] { "P_ID" }, Route.CONN_CRM_CG);
	}
}
