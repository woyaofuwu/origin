
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GrpBaseAudiInfoQry
{
    /**
     * 根据order_id查询稽核工单是否已存在
     * @param orderId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-7-2
     */
    public static IDataset queryGrpBaseAuditInfoByOrderId(String orderId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("ORDER_ID", orderId);
        StringBuilder sql = new StringBuilder("");
        sql.append("SELECT A.* FROM TF_F_GROUP_BASEINFO_AUDIT A");
        sql.append(" WHERE A.ORDER_ID=:ORDER_ID");
        return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
    /**
     * 新增集团业务稽核工单
     * 集团成员的批量业务，要求一个批次只能生成一笔稽核单，这个比较头大，目前只能是根据批次号过滤不能重复插入
     * REQ201804280001集团合同管理界面优化需求
     * @param data
     * @throws Exception
     * @author chenzg
     * @date 2018-7-3
     */
    public static void addGrpBaseAuditInfo(IData param) throws Exception
    {
    	StringBuilder sql = new StringBuilder("");
    	sql.append("INSERT INTO TF_F_GROUP_BASEINFO_AUDIT");
    	sql.append("  (AUDIT_ID,");
    	sql.append("   ACCEPT_MONTH,");
    	sql.append("   BIZ_TYPE,");
    	sql.append("   TRADE_TYPE_CODE,");
    	sql.append("   GROUP_ID,");
    	sql.append("   CUST_NAME,");
    	sql.append("   GRP_SN,");
    	sql.append("   CONTRACT_ID,");
    	sql.append("   VOUCHER_FILE_LIST,");
    	sql.append("   ADD_DISCNTS,");
    	sql.append("   DEL_DISCNTS,");
    	sql.append("   MOD_DISCNTS,");
    	sql.append("   STATE,");
    	sql.append("   IN_DATE,");
    	sql.append("   AUDIT_STAFF_ID,");
    	sql.append("   IN_STAFF_ID,");
    	sql.append("   RSRV_STR2,");
    	sql.append("   RSRV_TAG1)");
    	
    	sql.append("SELECT");
    	sql.append("   :AUDIT_ID,");
    	sql.append("   :ACCEPT_MONTH,");
    	sql.append("   :BIZ_TYPE,");
    	sql.append("   :TRADE_TYPE_CODE,");
    	sql.append("   :GROUP_ID,");
    	sql.append("   :CUST_NAME,");
    	sql.append("   :GRP_SN,");
    	sql.append("   :CONTRACT_ID,");
    	sql.append("   :VOUCHER_FILE_LIST,");
    	sql.append("   :ADD_DISCNTS,");
    	sql.append("   :DEL_DISCNTS,");
    	sql.append("   :MOD_DISCNTS,");
    	sql.append("   :STATE,");
    	sql.append("   TO_DATE(:IN_DATE, 'YYYY-MM-DD HH24:MI:SS'),");
    	sql.append("   :AUDIT_STAFF_ID,");
    	sql.append("   :IN_STAFF_ID,");
    	sql.append("   :RSRV_STR2,");
    	sql.append("   :RSRV_TAG1");
    	sql.append(" FROM DUAL ");
    	sql.append("WHERE NOT EXISTS (SELECT 1 FROM TF_F_GROUP_BASEINFO_AUDIT A WHERE A.AUDIT_ID = :AUDIT_ID AND A.ACCEPT_MONTH=:ACCEPT_MONTH AND A.BIZ_TYPE=:BIZ_TYPE)");

    	Dao.executeUpdate(sql, param, Route.CONN_CRM_CG);
    }
    public static IDataset queryGrpBaseAuditInfoForPK(IData param) throws Exception
    {
    	StringBuilder sql = new StringBuilder("");
    	sql.append("SELECT ");
    	sql.append("   AUDIT_ID,");
    	sql.append("   ACCEPT_MONTH,");
    	sql.append("   BIZ_TYPE,");
    	sql.append("   TRADE_TYPE_CODE,");
    	sql.append("   GROUP_ID,");
    	sql.append("   CUST_NAME,");
    	sql.append("   GRP_SN,");
    	sql.append("   CONTRACT_ID,");
    	sql.append("   VOUCHER_FILE_LIST,");
    	sql.append("   ADD_DISCNTS,");
    	sql.append("   DEL_DISCNTS,");
    	sql.append("   MOD_DISCNTS,");
    	sql.append("   STATE,");
    	sql.append("   IN_DATE,");
    	sql.append("   AUDIT_STAFF_ID,");
    	sql.append("   IN_STAFF_ID,");
    	sql.append("   RSRV_STR2,");
    	sql.append("   RSRV_TAG1");
    	sql.append(" FROM TF_F_GROUP_BASEINFO_AUDIT A WHERE A.AUDIT_ID = :AUDIT_ID AND A.ACCEPT_MONTH=:ACCEPT_MONTH AND A.BIZ_TYPE=:BIZ_TYPE");
    	
    	return Dao.qryBySql(sql, param, Route.CONN_CRM_CG);
    }
}
