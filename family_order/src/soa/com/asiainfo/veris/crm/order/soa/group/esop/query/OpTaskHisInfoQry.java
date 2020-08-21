package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * order
 * 待办工单历史表
 * @author ckh
 * @data 2018/1/18.
 */
public class OpTaskHisInfoQry
{
    public static void insertOpTaskHisInfo(IDataset dataset) throws Exception
    {
        Dao.insert("TF_FH_OP_TASK", dataset, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset queryOpTaskHisByConds(String staffId, String taskTypeCode, String taskStatus, String receObjType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("STAFF_ID", staffId);
        param.put("INFO_TYPE", taskTypeCode);
        param.put("INFO_STATUS", taskStatus);
        param.put("RECE_OBJ_TYPE", receObjType);
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BY_CONDS", param, page, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryOpTaskHisByInstId(String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("INST_ID", instId);
//        param.put("INFO_STATUS", "9");
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BH_BY_CONDS", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryOpTaskHisList(IData param, Pagination page) throws Exception
    {
    	String staffId = param.getString("STAFF_ID","");
        String taskTypeCode = param.getString("INFO_TYPE","");
        String busiTypeCode = param.getString("INFO_CHILD_TYPE","");
        String taskTopic = param.getString("INFO_TOPIC","");
        String taskStatus = param.getString("INFO_STATUS","");
        String receObjType = param.getString("RECE_OBJ_TYPE","");
        String startDate = param.getString("START_DATE","");
        String endDate = param.getString("END_DATE","");
        String ibsysid = param.getString("IBSYSID","");
        String groupId = param.getString("GROUP_ID","");
        String custName = param.getString("CUST_NAME","");
    	
        param.put("STAFF_ID", staffId);
        param.put("INFO_TYPE", taskTypeCode);
        param.put("INFO_CHILD_TYPE", busiTypeCode);
        param.put("INFO_TOPIC", taskTopic);
        param.put("INFO_STATUS", taskStatus);
        param.put("RECE_OBJ_TYPE", receObjType);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("IBSYSID", ibsysid);
        param.put("GROUP_ID", groupId);
        param.put("CUST_NAME", custName);
        
        
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BH_BY_CONDS", param, page, Route.CONN_CRM_CEN);
    }
}
