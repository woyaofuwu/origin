package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class EweNodeQry
{
    public static IDataset qryEweNodeByBusiformIdAndNodeId(String busiformId, String nodeId) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_ID", busiformId);
        param.put("NODE_ID", nodeId);
        return Dao.qryByCode("TF_B_EWE_NODE", "SEL_BY_BUSIFORM_ID_NODE_ID_DESC", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static IDataset qryEweNodeByIbsysid(String ibsysId) throws Exception
    {
        IData param = new DataMap();
        param.put("BI_SN", ibsysId);
        return Dao.qryByCode("TF_B_EWE_NODE", "SEL_BY_IBSYSID", param,  Route.getJourDb(Route.CONN_CRM_CG));
        
    }
    
    public static IDataset qryEweNodeByBusiformIdState(String busiformId, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_ID", busiformId);
        param.put("STATE", state);
        return Dao.qryByCode("TF_B_EWE_NODE", "SEL_BY_BUSIFORMID_STATE", param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEweHNodeByBusiformIdState(String busiformNodeId) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        return Dao.qryByCode("TF_BH_EWE_NODE", "SEL_BY_BUSIFORMID", param,  Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEweNodeByIbsysidState(String ibsysId, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("BI_SN", ibsysId);
        param.put("STATE", state);
        return Dao.qryByCode("TF_B_EWE_NODE", "SEL_BY_IBSYSID_STATE", param,    Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static void updWorkformNodeByPk(String busiformNodeId, String subBisn, String state) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        param.put("SUB_BI_SN", subBisn);
        param.put("STATE", state);
        Dao.executeUpdateByCodeCode("TF_B_EWE_NODE", "UPD_BY_PK", param, Route.getJourDbDefault());
    }
    
    public static void updWorkformNodeSubBNByPk(String busiformNodeId, String subBisn) throws Exception {
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        param.put("SUB_BI_SN", subBisn);
        StringBuilder sql = new StringBuilder(200);
        sql.append(" UPDATE TF_B_EWE_NODE T SET T.SUB_BI_SN = :SUB_BI_SN WHERE T.BUSIFORM_NODE_ID = :BUSIFORM_NODE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset countWorkflowByAcceptStaffId(String staffId) throws Exception
    {
        IDataset result = new DatasetList();
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        
        StringBuilder sql = new StringBuilder(200);
        sql.append(" select t.bpm_templet_id, t.accept_staff_id, count(1) COUNT_NUM");
        sql.append(" from tf_b_ewe t ");
        sql.append(" where t.accept_staff_id = :STAFF_ID ");
        sql.append(" group by t.bpm_templet_id, t.accept_staff_id ");
        
        IDataset eweList = Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
        if(IDataUtil.isNotEmpty(eweList))
        {
            for(int i = 0, size = eweList.size(); i < size; i++)
            {
                IData ewe = eweList.getData(i);
                IDataset flowList = QryFlowNodeDescBean.qryFlowDescBy(ewe.getString("BPM_TEMPLET_ID"), "0");
                if(IDataUtil.isEmpty(flowList))
                {
                    continue;
                }
                if("0".equals(flowList.first().getString("TEMPLET_TYPE")))
                {
                    ewe.put("BPM_TEMPLET_NAME", flowList.first().getString("TEMPLET_NAME"));
                    result.add(ewe);
                }
            }
        }
        return result;
    }
    
    public static IDataset qryEweByIbsysid(String ibsysId, String templateType) throws Exception
    {
        IData param = new DataMap();
        param.put("BI_SN", ibsysId);
        param.put("TEMPLET_TYPE", templateType);
        return Dao.qryByCode("TF_B_EWE", "SEL_BY_BISN_TEPLETTYPE", param,   Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryEweByibsysIdRecordnum(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_EWE", "SEL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    

    public static IDataset qryEweByBusiFormNodeId(String busiformNodeId) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        return Dao.qryByCode("TF_B_EWE_NODE", "SEL_TRAINFO_BY_BUSIFORMNODEID", param,Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    

    public static IDataset qryBySubBusiformId(String subBusiformId) throws Exception {
        IData param = new DataMap();
        param.put("SUB_BUSIFORM_ID", subBusiformId);
        return Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_SUBBUSIFORMID", param, Route.getJourDb(Route.getCrmDefaultDb()));
    }
    
    public static IDataset qryEweByBusiformId(String busiformId) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_ID", busiformId);
        return Dao.qryByCode("TF_B_EWE", "SEL_BY_BUSIFORMID", param,Route.getJourDb(Route.CONN_CRM_CG));
        
    }
    
    
    public static void updEweNodeByPk(String busiformNodeId, String execTime) throws Exception
    {
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        param.put("EXEC_TIME", execTime);
        
        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE TF_B_EWE_NODE A SET A.AUTO_TIME = TO_DATE(:EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') WHERE A.BUSIFORM_NODE_ID=:BUSIFORM_NODE_ID ");
        
        Dao.executeUpdate(sql, param, Route.getJourDbDefault());
    }

    public static IDataset qryByBusiformNodeId(String busiformNodeId) throws Exception {
        IData param = new DataMap();
        param.put("BUSIFORM_NODE_ID", busiformNodeId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT * FROM TF_B_EWE_NODE T ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND T.BUSIFORM_NODE_ID= :BUSIFORM_NODE_ID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryByBusiformIdAndNodeId(IData param) throws Exception {
        String isBH = param.getString("IS_BH");
        String tableName = "";
        if("true".equals(isBH)) {
            tableName = "TF_BH_EWE_NODE";
        } else if("false".equals(isBH)) {
            tableName = "TF_B_EWE_NODE_TRA";
        } else {
            return new DatasetList();
        }
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT * FROM " + tableName + " T ");
        sql.addSQL(" WHERE 1=1 ");
        sql.addSQL(" AND T.BUSIFORM_ID= :BUSIFORM_ID ");
        sql.addSQL(" AND T.NODE_ID= :NODE_ID ");
        sql.addSQL(" ORDER BY T.CREATE_DATE ASC ");
        return Dao.qryByParse(sql, Route.getJourDb(Route.CONN_CRM_CG));
    }
    public static IDataset qryEweHByIbsysid(String ibsysId, String templateType) throws Exception
    {
        IData param = new DataMap();
        param.put("BI_SN", ibsysId);
        param.put("TEMPLET_TYPE", templateType);
        return Dao.qryByCode("TF_BH_EWE", "SEL_BY_BISN_TEPLETTYPE", param,  Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    
    

    public static int[] updStaffForEweNode(IDataset param) throws Exception {

        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" UPDATE TF_B_EWE_NODE A SET A.DEAL_STAFF_ID =:DEAL_NEW_STAFF_ID  WHERE A.BUSIFORM_NODE_ID=:BUSIFORM_NODE_ID ");
        return Dao.executeBatch(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static IDataset selStaffForEweNode(String oldStaff) throws Exception {

        
        IData param = new DataMap();
        param.put("DEAL_OLD_STAFF_ID", oldStaff);

        StringBuilder sql = new StringBuilder(1000);        
        sql.append(" SELECT a.BUSIFORM_NODE_ID,a.ACCEPT_MONTH,a.BUSIFORM_ID FROM TF_B_EWE_NODE A  WHERE A.DEAL_STAFF_ID=:DEAL_OLD_STAFF_ID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryAuditEweByBpmAndIbsysId(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_B_EWE", "SEL_AUDITINFO_BY_BPMIBSYSID", param,Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryInfoByBpmTempletType(String bpmTempletId, String nodeType, String status) throws Exception {
        IData param = new DataMap();
        param.put("BPM_TEMPLET_ID", bpmTempletId);
        param.put("NODE_TYPE", nodeType);
        param.put("VALID_TAG", status);
        return Dao.qryByCode("TD_B_EWE_NODE_TEMPLET", "SEL_BY_BPMTEMPLETID_NODETYPE", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset qryEweHByBusiformId(String busiformId) throws Exception {
        IData param = new DataMap();
        param.put("BUSIFORM_ID", busiformId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT * FROM TF_BH_EWE T ");
        sql.append(" WHERE 1=1 ");
        sql.append(" AND T.BUSIFORM_ID= :BUSIFORM_ID ");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset qryAuditInfoEweByBpmAndIbsysId(IData param) throws Exception
   	{
   	    return Dao.qryByCodeParser("TF_B_EWE", "SEL_AUDITINFO_BY_BPMTEMPLETID_IBSYSID", param,Route.getJourDb(Route.CONN_CRM_CG));
   	}
    
    public static IDataset qryEweNodeByIbsysidStateRecordNum(String ibsysId, String state,String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("BI_SN", ibsysId);
        param.put("STATE", state);
        param.put("RECORD_NUM", recordNum);
        
        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT T.BUSIFORM_NODE_ID, T.BUSIFORM_ID, T.NODE_ID,T.SUB_BI_SN ");
        sql.append(" FROM TF_B_EWE_NODE T, TF_B_EWE E,TF_B_EWE_RELE A ");
        sql.append(" WHERE E.BUSIFORM_ID = T.BUSIFORM_ID ");
        sql.append(" AND T.BUSIFORM_ID=A.SUB_BUSIFORM_ID ");
        sql.append(" AND T.STATE = :STATE ");
        sql.append(" AND A.Rele_Code='RECORD_NUM' ");
        sql.append(" AND A.Rele_Value=:RECORD_NUM ");
        sql.append(" AND E.BI_SN = :BI_SN");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
