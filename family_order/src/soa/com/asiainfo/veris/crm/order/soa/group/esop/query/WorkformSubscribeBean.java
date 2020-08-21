package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop.WorkTaskMgrBean;

public class WorkformSubscribeBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformSubscribe(IData param) throws Exception
    {
        return Dao.insert("TF_B_EOP_SUBSCRIBE", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryWorkformSubscribeByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_SUBSCRIBE", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delWorkformSubscribeByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_SUBSCRIBE", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryWorkformSubscribeByRsrvstr5(String rsrvStr5) throws Exception
    {
    	IData param = new DataMap();
    	param.put("RSRV_STR5", rsrvStr5);
    	return Dao.qryByCode("TF_B_EOP_SUBSCRIBE", "SEL_BY_RSRVSTR5", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryWorkformSubscribeInfo(IData param, Pagination pagination) throws Exception
    {
    	SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT A.IBSYSID,A.RSRV_STR5,A.GROUP_ID,A.CUST_NAME,B.PRODUCT_NAME,'0' HISTORY_TAG FROM TF_B_EOP_SUBSCRIBE A,TF_B_EOP_PRODUCT B WHERE A.IBSYSID=B.IBSYSID"); 
        sql.addSQL(" AND 1=1");
		sql.addSQL(" AND A.IBSYSID = :IBSYSID");
		sql.addSQL(" AND B.IBSYSID = :IBSYSID");
		sql.addSQL(" AND A.RSRV_STR5 = :RSRV_STR5");
		sql.addSQL(" AND A.GROUP_ID = :GROUP_ID");
		sql.addSQL(" UNION ");
        sql.addSQL("SELECT A.IBSYSID,A.RSRV_STR5,A.GROUP_ID,A.CUST_NAME,B.PRODUCT_NAME,'1' HISTORY_TAG FROM TF_BH_EOP_SUBSCRIBE A,TF_BH_EOP_PRODUCT B WHERE A.IBSYSID=B.IBSYSID"); 
        sql.addSQL(" AND 1=1");
		sql.addSQL(" AND A.IBSYSID = :IBSYSID");
		sql.addSQL(" AND B.IBSYSID = :IBSYSID");
		sql.addSQL(" AND A.RSRV_STR5 = :RSRV_STR5");
		sql.addSQL(" AND A.GROUP_ID = :GROUP_ID");
		
		return Dao.qryByParse(sql, pagination,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateWorkformSubscribeByIBSYSID(IData param) throws Exception
    {
    	Dao.executeUpdateByCodeCode("TF_B_EOP_SUBSCRIBE", "UPD_RSRVSTR5_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset queryDealSubscribe(IData param, Pagination pg) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT e.busiform_id,sb.ibsysid,sb.bpm_templet_id SUB_TYPE_CODE,DECODE(sb.busi_code, '7010', 'VOIP专线', '7011', '互联网专线', '7012', '数据专线','70111','云互联（互联网）','70112','云专线（互联网）','70121','云互联（数据传输）','70122','云专线（数据传输）', '') PRODUCT_TYPE,sb.GROUP_ID, 'false' is_bh  ");
        sql.addSQL(" FROM  TF_B_EOP_SUBSCRIBE sb,tf_b_ewe e ");
        sql.addSQL(" WHERE sb.ibsysid=e.bi_sn ");
        sql.addSQL(" AND e.templet_type = '0' ");
        sql.addSQL(" AND sb.ibsysid=:IBSYSID ");
        sql.addSQL(" AND sb.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL(" AND sb.accept_time<=to_date(:END_DATE,'yyyy-mm-dd') ");
        sql.addSQL(" AND sb.accept_time>=to_date(:START_DATE,'yyyy-mm-dd') ");
        sql.addSQL(" UNION ALL ");
        sql.addSQL(" SELECT e.busiform_id,sb.ibsysid,sb.bpm_templet_id SUB_TYPE_CODE,DECODE(sb.busi_code, '7010', 'VOIP专线', '7011', '互联网专线', '7012', '数据专线','70111','云互联（互联网）','70112','云专线（互联网）','70121','云互联（数据传输）','70122','云专线（数据传输）', '') PRODUCT_TYPE,sb.GROUP_ID,'true' is_bh  ");
        sql.addSQL(" FROM  TF_BH_EOP_SUBSCRIBE sb,tf_bh_ewe e ");
        sql.addSQL(" WHERE sb.ibsysid=e.bi_sn ");
        sql.addSQL(" AND e.templet_type = '0' ");
        sql.addSQL(" AND sb.deal_state='9' ");
        sql.addSQL(" AND sb.ibsysid=:IBSYSID ");
        sql.addSQL(" AND sb.bpm_templet_id=:SUB_TYPE_CODE ");
        sql.addSQL(" AND sb.accept_time<=to_date(:END_DATE,'yyyy-mm-dd') ");
        sql.addSQL(" AND sb.accept_time>=to_date(:START_DATE,'yyyy-mm-dd') ");

        return Dao.qryByParse(sql, pg, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryWorkformHSubscribeByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_BH_EOP_SUBSCRIBE", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryScribeInfoByIbsysidForOpen(IData param) throws Exception {
        StringBuilder sql = new StringBuilder(200);
        sql.append(" SELECT * FROM TF_B_EOP_SUBSCRIBE ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        sql.append(" AND BPM_TEMPLET_ID = 'EDIRECTLINEOPENPBOSS' ");
        IDataset infos = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        return infos;
    }

    public static IDataset qryAllScribeInfoByGroupIdForOpen(IData param) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT * FROM TF_B_EOP_SUBSCRIBE ");
        sql.addSQL(" WHERE BPM_TEMPLET_ID = 'EDIRECTLINEOPENPBOSS' ");
        sql.addSQL(" AND GROUP_ID = :GROUP_ID ");
        sql.addSQL(" AND ACCEPT_TIME >= to_date(:START_DATE,'yyyy-MM-dd') ");
        sql.addSQL(" AND ACCEPT_TIME <= to_date(:END_DATE,'yyyy-MM-dd') ");
        sql.addSQL(" UNION ALL ");
        sql.addSQL(" SELECT * FROM TF_BH_EOP_SUBSCRIBE ");
        sql.addSQL(" WHERE BPM_TEMPLET_ID = 'EDIRECTLINEOPENPBOSS' ");
        sql.addSQL(" AND GROUP_ID = :GROUP_ID ");
        sql.addSQL(" AND ACCEPT_TIME >= to_date(:START_DATE,'yyyy-MM-dd') ");
        sql.addSQL(" AND ACCEPT_TIME <= to_date(:END_DATE,'yyyy-MM-dd') ");
        IDataset infos = Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
        return infos;
    }

    public static IDataset queryWorkform(IData param) throws Exception
    {
    	SQLParser sql = new SQLParser(param);
        sql.addSQL("SELECT B.IBSYSID IBSYSID, B.RSRV_STR4 TITLE, '是' STATE"); 
        sql.addSQL(" FROM TF_B_EOP_SUBSCRIBE B");
		sql.addSQL(" WHERE B.BUSI_CODE = :PRODUCT_ID");
		sql.addSQL(" AND B.GROUP_ID = :GROUP_ID");
		sql.addSQL(" AND B.IBSYSID LIKE '%' || :IBSYSID || '%'");
		sql.addSQL(" AND B.RSRV_STR4 LIKE '%' || :TITLE || '%'");
		sql.addSQL(" AND B.BPM_TEMPLET_ID = :BPM_TEMPLET_ID");
		sql.addSQL(" UNION");
		sql.addSQL(" SELECT BH.IBSYSID IBSYSID, BH.RSRV_STR4 TITLE, '否' STATE");
		sql.addSQL(" FROM TF_BH_EOP_SUBSCRIBE BH");
		sql.addSQL(" WHERE BH.BUSI_CODE = :PRODUCT_ID");
		sql.addSQL(" AND BH.GROUP_ID = :GROUP_ID");
		sql.addSQL(" AND BH.IBSYSID LIKE '%' || :IBSYSID || '%'");
		sql.addSQL(" AND BH.RSRV_STR4 LIKE '%' || :TITLE || '%'");
		sql.addSQL(" AND BH.BPM_TEMPLET_ID = :BPM_TEMPLET_ID");
		
		return Dao.qryByParse(sql, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qrySubScribeInfoByIbsysidOrGroupId(IData param,Pagination page) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT A.IBSYSID,A.BPM_TEMPLET_ID,A.GROUP_ID, ");
        sql.append(" A.CUST_NAME,A.RSRV_STR4,B.BUSIFORM_ID,TO_CHAR(A.ACCEPT_TIME, 'YYYY-MM-DD') ACCEPT_TIME ");
        sql.append(" FROM TF_B_EOP_SUBSCRIBE A JOIN TF_B_EWE B ON A.IBSYSID = B.BI_SN ");
        sql.append(" WHERE B.TEMPLET_TYPE = '0' ");
        sql.append(" AND B.ACCEPT_STAFF_ID = :STAFF_ID ");
        if(StringUtils.isNotBlank(param.getString("IBSYSID"))){
            sql.append(" AND A.IBSYSID = :IBSYSID ");
        }
        if(StringUtils.isNotBlank(param.getString("GROUP_ID"))){
            sql.append(" AND A.GROUP_ID = :GROUP_ID ");
        }
        if(StringUtils.isNotBlank(param.getString("BPM_TEMPLET_ID"))){
            sql.append("  AND A.BPM_TEMPLET_ID IN ( ");
            sql.append(param.getString("BPM_TEMPLET_ID"));
            sql.append(" )");
        }else{
            return new DatasetList();
        }
        sql.append("  ORDER BY A.ACCEPT_TIME DESC ");

        IDataset infos = Dao.qryBySql(sql,param, page, Route.getJourDb(BizRoute.getRouteId()));

        if (IDataUtil.isNotEmpty(infos)) {
            return WorkTaskMgrBean.getInfos(infos);
        }else {
            return new DatasetList();
        }


    }
}
