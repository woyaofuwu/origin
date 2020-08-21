
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BatDealInfoQry
{
    /**
     * 查询所有批量明细 （包括移入历史表的数据） 解决分页不准确的问题
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialAllQuery(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        /**
         * QR-20150421-10批量返销业务界面问题
         * chenxy3 2015-04-30
         * 由于担心别的地方还使用到该查询，本地改动只针对批量预开户。
         * */
        String batchOperType=data.getString("BATCH_OPER_TYPE");
        if(batchOperType!=null&&"CREATEPREUSER".equals(batchOperType)){
        	parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY, ");
        	parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(a.SERIAL_NUMBER) SERIAL_NUMBER, ");
        	parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID, ");
        	parser.addSQL(" DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20, ");
        	parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
        	parser.addSQL(" FROM tf_b_trade_batdeal a ,uop_crm1.tf_f_user b   ");
        	parser.addSQL(" WHERE 1= 1 ");
        	parser.addSQL(" and a.serial_number=b.serial_number ");
        	parser.addSQL(" and b.open_mode='1' and b.remove_tag='0' ");
        	parser.addSQL(" AND a.batch_id = :BATCH_ID  ");
        	parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        	parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG  ");
        	parser.addSQL(" AND a.deal_state = :DEAL_STATE  ");
        	parser.addSQL(" union ");
        	parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY, ");
        	parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER, ");
        	parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID, ");
        	parser.addSQL(" DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20, ");
        	parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
        	parser.addSQL(" FROM tf_b_trade_batdeal a ");
        	parser.addSQL(" WHERE 1= 1 ");
        	parser.addSQL(" AND a.batch_id = :BATCH_ID  ");
        	parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        	parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG  ");
        	parser.addSQL(" AND a.deal_state = :DEAL_STATE  ");
        	parser.addSQL(" and not exists ( select 1 from uop_crm1.tf_f_user b where a.serial_number=b.serial_number) ");
        }else{
	        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,");
	        parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,");
	        parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,");
	        parser.addSQL(" DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20,");
	        parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
	        parser.addSQL(" FROM tf_b_trade_batdeal a WHERE 1= 1 ");
	        parser.addSQL(" AND a.batch_id = :BATCH_ID");
	        parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
	        parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG");
	        parser.addSQL(" AND a.deal_state = :DEAL_STATE");
	        parser.addSQL(" AND a.serial_number = :SERIAL_NUMBER");
        }
        IDataset resultset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));

        if (IDataUtil.isEmpty(resultset))
        {
            return resultset;
        }

        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);

            String dealState = result.getString("DEAL_STATE", "");

            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));// 添加处理状态描述
        }

        return resultset;
    }
    
    
    /**
     * 查询所有批量明细 （包括移入历史表的数据） 解决分页不准确的问题,添加了对实名制认证的校验
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialAllQry(IData data, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(data);
    	
    	/**
    	 * QR-20150421-10批量返销业务界面问题
    	 * chenxy3 2015-04-30
    	 * 由于担心别的地方还使用到该查询，本地改动只针对批量预开户。
    	 * */
    	String batchOperType=data.getString("BATCH_OPER_TYPE");
    	if(batchOperType!=null&&"CREATEPREUSER".equals(batchOperType)){
    		parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY, ");
    		parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(a.SERIAL_NUMBER) SERIAL_NUMBER, ");
    		parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID, ");
    		parser.addSQL(" DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20, ");
    		parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
    		parser.addSQL(" FROM tf_b_trade_batdeal a ,uop_crm1.tf_f_user b   ");
    		parser.addSQL(" WHERE 1= 1 ");
    		parser.addSQL(" and a.serial_number=b.serial_number ");
    		parser.addSQL(" and b.open_mode='1' and b.remove_tag='0' ");
    		parser.addSQL(" AND a.batch_id = :BATCH_ID  ");
    		parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
    		parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG  ");
    		parser.addSQL(" AND a.deal_state = :DEAL_STATE  ");
    		parser.addSQL(" AND b.acct_tag = '2'");
    	}else{
	        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,ACCEPT_MONTH,TO_CHAR(OPERATE_ID) OPERATE_ID,BATCH_OPER_TYPE,PRIORITY,");
	        parser.addSQL(" TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,");
	        parser.addSQL(" ROUTE_EPARCHY_CODE,DB_SOURCE,CANCEL_TAG,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,");
	        parser.addSQL(" DATA1,DATA2,DATA3,DATA4,DATA5,DATA6,DATA7,DATA8,DATA9,DATA10,DATA11,DATA12,DATA13,DATA14,DATA15,DATA16,DATA17,DATA18,DATA19,DATA20,");
	        parser.addSQL(" CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,DEAL_STATE,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,DEAL_RESULT,DEAL_DESC,TRADE_ID ");
	        parser.addSQL(" FROM tf_b_trade_batdeal a WHERE 1= 1 ");
	        parser.addSQL(" AND a.batch_id = :BATCH_ID");
	        parser.addSQL(" AND a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
	        parser.addSQL(" AND a.cancel_tag = :CANCEL_TAG");
	        parser.addSQL(" AND a.deal_state = :DEAL_STATE");
	        parser.addSQL(" AND a.serial_number = :SERIAL_NUMBER");
        }
    	IDataset resultset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    	
    	if (IDataUtil.isEmpty(resultset))
    	{
    		return resultset;
    	}
    	
    	for (int i = 0, size = resultset.size(); i < size; i++)
    	{
    		IData result = resultset.getData(i);
    		
    		String dealState = result.getString("DEAL_STATE", "");
    		
    		result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
    				{ "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
    						{ "BAT_TASK_STATE_TAG", dealState }));// 添加处理状态描述
    	}
    	
    	return resultset;
    }

    public static IDataset batchDetialQuery(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(a.BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(a.BATCH_ID) BATCH_ID,TO_CHAR(a.OPERATE_ID) OPERATE_ID,");
        parser.addSQL(" a.BATCH_OPER_TYPE,b.BATCH_OPER_NAME,TO_CHAR(a.REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(a.SERIAL_NUMBER) SERIAL_NUMBER,TO_CHAR(a.CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,");
        parser.addSQL(" a.CANCEL_STAFF_ID,TO_CHAR(a.DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,a.DEAL_RESULT,a.DEAL_DESC ");
        parser.addSQL(" FROM tf_b_trade_batdeal a,td_b_batchtype b");
        parser.addSQL(" WHERE a.batch_id = TO_NUMBER(:BATCH_ID)");
        parser.addSQL(" AND a.BATCH_OPER_TYPE = b.BATCH_OPER_TYPE");
        parser.addSQL(" AND a.cancel_tag = TO_NUMBER(:CANCEL_TAG)");
        parser.addSQL(" AND a.deal_state = :DEAL_STATE");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据batch_id、cancel_tag、deal_state、serial_number等查询VPMN批量明细 解决分页不准确的问题
     * 
     * @param data
     * @param pagination
     * @param conn
     * @return
     * @throws Exception
     */
    public static IDataset batchDetialQueryAllVPMN(IData data, Pagination pagination, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT TO_CHAR(BATCH_TASK_ID) BATCH_TASK_ID,TO_CHAR(BATCH_ID) BATCH_ID,TO_CHAR(OPERATE_ID) OPERATE_ID,DEAL_STATE,");
        parser.addSQL(" BATCH_OPER_TYPE,TO_CHAR(REFER_TIME,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,TO_CHAR(SERIAL_NUMBER) SERIAL_NUMBER,TO_CHAR(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,");
        parser.addSQL(" CANCEL_STAFF_ID,TO_CHAR(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,p_ss_getbaterr(DEAL_RESULT) DEAL_RESULT,");
        parser.addSQL(" DATA1 SHORT_CODE, ");
        parser.addSQL(" (CASE DATA2 WHEN '1' THEN '短号' WHEN '2' THEN '真实号码' ELSE '错误的输入代码' END) AS CALL_DISP_MODE,DEAL_DESC");
        parser.addSQL(" FROM tf_b_trade_batdeal d WHERE 1= 1 ");
        parser.addSQL(" AND  batch_id = :BATCH_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND cancel_tag = :CANCEL_TAG");
        parser.addSQL(" AND deal_state = :DEAL_STATE");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");

        IDataset resultset = Dao.qryByParse(parser, pagination, conn);

        if (IDataUtil.isEmpty(resultset))
        {
            return resultset;
        }

        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));
        }
        return resultset;
    }

    public static IDataset getCommparaInfoEx(IData data) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BATCREATE_BY_RIGHT", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getDiscntCode(IData data) throws Exception
    {
        return Dao.qryByCode("TD_B_DISCNT", "SEL_DISCNT_BY_STAFFID", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getElementInfo(IData data) throws Exception
    {
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKID", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getPackageInfo(IData data) throws Exception
    {
        return Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_PACKAGE_BY_PRODUCT", data, Route.CONN_CRM_CEN);
    }

    public static IDataset qryBatchCountByOperateId(String operateId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("OPERATE_ID", operateId);

        IDataset batDealList = Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_BATCH_COUNT_BY_OPERATE_ID", param, Route.getJourDb(Route.CONN_CRM_CG));
        
        return batDealList;
    }
    
    /**
     * 根据批次号查询批量明细信息
     * 
     * @param batchId
     * @param isQryHis
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryBatDealByBatchId(String batchId, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_ID", batchId);

        IDataset batDealList = Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_BY_BATCHID", param, pg, Route.getJourDb(Route.CONN_CRM_CG));

        return batDealList;
    }

    /**
     * 根据批量任务号查询批量明细信息
     * 
     * @param batchTaskId
     * @param isQryHis
     * @param pg
     * @return
     * @throws Exception
     */
    private static IDataset qryBatDealByBatchTaskId(String batchTaskId, boolean isQryHis, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_TASK_ID", batchTaskId);

        IDataset batDealList = Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_BY_BATCH_TASKID", param, pg, Route.getJourDb(Route.CONN_CRM_CG));

        return batDealList;
    }

    /**
     * 根据批量任务号和批次号查询批量明细信息
     * 
     * @param batchTaskId
     * @param batchId
     * @param isQryHis
     * @param pg
     * @return
     * @throws Exception
     */
    private static IDataset qryBatDealByTaskAndBatchId(String batchTaskId, String batchId, boolean isQryHis, Pagination pg) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_ID", batchId);

        IDataset batDealList = Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_BY_TASK_AND_BATCHID", param, pg, Route.getJourDb(Route.CONN_CRM_CG));

        return batDealList;
    }

    public static IDataset queryBatchDealInfos(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT A.BATCH_ID, A.DEAL_STATE, A.EXEC_TIME, A.SERIAL_NUMBER  ");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL A WHERE 1=1   ");
        parser.addSQL(" AND A.BATCH_TASK_ID = :BATCH_TASK_ID ");
        parser.addSQL(" AND A.BATCH_ID = :BATCH_ID ");
        parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND A.BATCH_OPER_TYPE = :BATCH_OPER_TYPE  ");
        parser.addSQL(" AND A.REFER_TIME = (SELECT MAX(REFER_TIME) FROM TF_B_TRADE_BATDEAL B  WHERE 1 =1   ");
        parser.addSQL(" AND B.BATCH_OPER_TYPE = :BATCH_TASK_ID ");
        parser.addSQL(" AND B.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" ) ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryBatchDealSum(String batchId) throws Exception
    {
        IData param = new DataMap();
        param.put("BATCH_ID", batchId);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(1) UDSUM FROM TF_B_TRADE_BATDEAL");
        sql.append(" WHERE BATCH_ID = :BATCH_ID");
        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    
    public static IDataset queryBatchDealSumByBatchId(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT COUNT(1) UDSUM FROM TF_B_TRADE_BATDEAL ");
        parser.addSQL("  WHERE BATCH_ID = :BATCH_ID   ");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        parser.addSQL(" AND DEAL_STATE != :DEAL_STATE ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset queryBatchType(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        //parser.addSQL(" SELECT batch_oper_type ");
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM tf_b_trade_bat ");
        parser.addSQL(" WHERE batch_id = TO_NUMBER(:BATCH_ID)");

        return Dao.qryByParse(parser,Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IDataset queryTaskId(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        
        //parser.addSQL(" SELECT batch_oper_type ");
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_B_TRADE_BAT_TASK ");
        parser.addSQL(" WHERE BATCH_TASK_ID = TO_NUMBER(:BATCH_TASK_ID)");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH)");

        return Dao.qryByParse(parser,Route.getJourDb(Route.CONN_CRM_CG));
    }
    public static IDataset queryGroupName(IData data) throws Exception
    {
    	
    	return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_QUERY_GROUPNAME", data, Route.CONN_CRM_CG);
    }
    public static IDataset queryGroupSn(IData data) throws Exception
    {
    	
    	return Dao.qryByCode("TF_F_USER", "SEL_QUERY_GROUPSN_BY_CUSTID", data, Route.CONN_CRM_CG);
    }
    public static IDataset queryTypeCode(IData data) throws Exception
    {
    	
        return Dao.qryByCode("TD_B_ATTR_BIZ", "SEL_ALL_BY_PRODUCTID", data,Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryTypeCodeByType(IData data) throws Exception
    {
    	
        return Dao.qryByCode("TD_B_BATCHTYPE", "SEL_ALL_BY_TYPE", data,Route.CONN_CRM_CEN);
    }
    
    
    public static void insertOrder(IData data) throws Exception
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
    	sql.append("   IN_STAFF_ID)");
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
    	sql.append("   :IN_STAFF_ID");
    	sql.append(" FROM DUAL ");
    	sql.append("WHERE NOT EXISTS (SELECT 1 FROM TF_F_GROUP_BASEINFO_AUDIT A WHERE A.AUDIT_ID = :AUDIT_ID AND A.ACCEPT_MONTH=:ACCEPT_MONTH AND A.BIZ_TYPE=:BIZ_TYPE)");

    	Dao.executeUpdate(sql, data, Route.CONN_CRM_CG);
    }

    public static IDataset queryBatDealByBatIdAndSN(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" select to_char(operate_id) OPERATE_ID,batch_oper_type,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,");
        parser.addSQL(" DEAL_STATE,SERIAL_NUMBER,to_char(batch_id) BATCH_ID");
        parser.addSQL(" from tf_b_trade_batdeal");
        parser.addSQL(" where batch_oper_type in (");
        parser.addSQL(" select batch_oper_type");
        parser.addSQL(" from td_b_batchtype where cancelable_flag = '1' and batch_oper_type in (");
        parser.addSQL(" select distinct batch_oper_type from tf_b_trade_batdeal");
        parser.addSQL(" where serial_number = :SERIAL_NUMBER))");

        parser.addSQL(" and cancel_tag = '0' and deal_state in ('0','1','3','6','9','A')");
        parser.addSQL(" and serial_number = :SERIAL_NUMBER");
        parser.addSQL(" and batch_id = :BATCH_ID");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }
    
    //部分号码批量返销业务   筛选acct_tag为2 未激活状态号码
    public static IDataset queryBatDealByBatIdAndSNForCancle(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" select to_char(operate_id) OPERATE_ID,batch_oper_type,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,");
        parser.addSQL(" DEAL_STATE,SERIAL_NUMBER,to_char(batch_id) BATCH_ID");
        parser.addSQL(" from tf_b_trade_batdeal");
        parser.addSQL(" where batch_oper_type in (");
        parser.addSQL(" select batch_oper_type");
        parser.addSQL(" from td_b_batchtype where cancelable_flag = '1' and batch_oper_type in (");
        parser.addSQL(" select distinct batch_oper_type from tf_b_trade_batdeal");
        parser.addSQL(" where serial_number = :SERIAL_NUMBER))");

        parser.addSQL(" and cancel_tag = '0' and deal_state in ('0','1','3','6','9','A')");
        parser.addSQL(" and serial_number = :SERIAL_NUMBER");
        parser.addSQL(" and batch_id = :BATCH_ID");
        parser.addSQL(" and '2' = (SELECT ACCT_TAG FROM uop_crm1.TF_F_USER WHERE SERIAL_NUMBER = :SERIAL_NUMBER AND OPEN_MODE='1' AND REMOVE_TAG='0')");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IDataset queryCancelBatByBatchInfo(IData data, Pagination pagination) throws Exception
    {
    	return Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_CANCELNUM_BY_BATCH", data, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    //根据批次号查询返销订单,增加对实名制激活的校验
    public static IDataset queryCancelBatByBatchInfos(IData data, Pagination pagination) throws Exception
    {
    	return Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_CANCELNUMINFO_BY_BATCH", data, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryCancelBatBySerialInfo(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select to_char(operate_id) OPERATE_ID,batch_oper_type,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,");
        parser.addSQL(" DEAL_STATE,SERIAL_NUMBER");
        parser.addSQL(" from tf_b_trade_batdeal");
        parser.addSQL(" where batch_oper_type in (");
        parser.addSQL(" select batch_oper_type");
        parser.addSQL(" from td_b_batchtype where cancelable_flag = '1' and batch_oper_type in (");
        parser.addSQL(" select distinct batch_oper_type from tf_b_trade_batdeal");
        parser.addSQL(" where serial_number = :SERIAL_NUMBER");
        parser.addSQL("))");
        parser.addSQL(" and cancel_tag = '0' and deal_state in ('0','1','3','6','9','A')");
        parser.addSQL(" and serial_number = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    //根据服务号码信息查询待返销订单,添加对实名制激活的校验
    public static IDataset queryCancelBatBySerialInfos(IData data, Pagination pagination) throws Exception
    {
    	 SQLParser parser = new SQLParser(data);
         parser.addSQL(" select to_char(operate_id) OPERATE_ID,batch_oper_type,to_char(refer_time,'yyyy-mm-dd hh24:mi:ss') REFER_TIME,");
         parser.addSQL(" DEAL_STATE,SERIAL_NUMBER");
         parser.addSQL(" from tf_b_trade_batdeal");
         parser.addSQL(" where batch_oper_type in (");
         parser.addSQL(" select batch_oper_type");
         parser.addSQL(" from td_b_batchtype where cancelable_flag = '1' and batch_oper_type in (");
         parser.addSQL(" select distinct batch_oper_type from tf_b_trade_batdeal");
         parser.addSQL(" where serial_number = :SERIAL_NUMBER");
         parser.addSQL("))");
         parser.addSQL(" and cancel_tag = '0' and deal_state in ('0','1','3','6','9','A')");
         parser.addSQL(" and serial_number = :SERIAL_NUMBER");
         parser.addSQL(" and '2' = (SELECT ACCT_TAG FROM uop_crm1.TF_F_USER WHERE SERIAL_NUMBER = :SERIAL_NUMBER AND OPEN_MODE='1' AND REMOVE_TAG='0')");

         return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryCancelTag(IData data, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT cancelable_flag ");
        parser.addSQL(" FROM td_b_batchtype ");
        parser.addSQL(" WHERE batch_oper_type = :BATCH_OPER_TYPE");
        return Dao.qryByParse(parser, null, Route.CONN_CRM_CEN);
    }

    public static IDataset queryFaildInfo(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);

        parser.addSQL(" SELECT batch_id,serial_number,deal_result,deal_desc ");
        parser.addSQL(" from tf_b_trade_batdeal ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND BATCH_ID = TO_NUMBER(:BATCH_ID) ");
        parser.addSQL(" AND DEAL_STATE = '7' ");
        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset queryOcsDealInfo(IData data, Pagination pagination) throws Exception
    {
        SQLParser selectSql = new SQLParser(data);
        selectSql.addSQL(" SELECT a.deal_id,a.serial_number,TO_CHAR(a.accept_date,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,a.accept_staff_id,a.accept_depart_id,a.accept_mode,a.write_type,  ");
        selectSql.addSQL(" a.enable_tag,TO_CHAR(a.start_date,'yyyy-mm-dd hh24:mi:ss') START_DATE, TO_CHAR(a.end_date,'yyyy-mm-dd hh24:mi:ss') END_DATE");
        selectSql.addSQL(",a.deal_state,TO_CHAR(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,a.deal_result,a.trade_id,a.batch_id ");
        selectSql.addSQL(" FROM tf_b_ocs_batdeal a WHERE 1=1 ");
        selectSql.addSQL(" AND a.batch_id = :BATCH_ID ");
        selectSql.addSQL(" AND a.serial_number = :SERIAL_NUMBER ");
        selectSql.addSQL(" AND a.accept_date >= to_date(:START_DATE, 'YYYY-MM-DD') ");
        selectSql.addSQL(" AND a.accept_date < to_date(:END_DATE, 'YYYY-MM-DD') + 1 ");
        selectSql.addSQL(" AND a.accept_mode = '1'  ");
        selectSql.addSQL(" union all  ");
        selectSql.addSQL(" SELECT b.deal_id,b.serial_number,TO_CHAR(b.accept_date,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,b.accept_staff_id,b.accept_depart_id,b.accept_mode,b.write_type,  ");
        selectSql.addSQL(" b.enable_tag,TO_CHAR(b.start_date,'yyyy-mm-dd hh24:mi:ss') START_DATE, TO_CHAR(b.end_date,'yyyy-mm-dd hh24:mi:ss') END_DATE");
        selectSql.addSQL(",b.deal_state,TO_CHAR(b.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,b.deal_result,b.trade_id,b.batch_id ");
        selectSql.addSQL(" FROM tf_bh_ocs_batdeal b WHERE 1=1 ");
        selectSql.addSQL(" AND b.batch_id = :BATCH_ID ");
        selectSql.addSQL(" AND b.serial_number = :SERIAL_NUMBER ");
        selectSql.addSQL(" AND b.accept_date >= to_date(:START_DATE, 'YYYY-MM-DD') ");
        selectSql.addSQL(" AND b.accept_date < to_date(:END_DATE, 'YYYY-MM-DD') + 1 ");
        selectSql.addSQL(" AND b.accept_mode = '1'  ");
        return Dao.qryByParse(selectSql, pagination);
    }

    // 
    public static IDataset queryProductListNoLimit(IData data) throws Exception
    {
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_PRODUCT_BY_STAFFID", data, Route.CONN_CRM_CEN);
    }

    /**
     * 查询批量任务处理结果，流量自由充产品，网厅使用
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryBatDealResult(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT BATCH_ID,BATCH_TASK_ID,BATCH_OPER_TYPE,REFER_TIME,EXEC_TIME,SERIAL_NUMBER,CANCEL_TAG,DEAL_STATE,DEAL_RESULT,TRADE_ID");
        parser.addSQL(" FROM TF_B_TRADE_BATDEAL");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND BATCH_ID = :BATCH_ID");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" ORDER BY REFER_TIME DESC ");
        
        IDataset resultset = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        
        //添加DEAL_STATE_NAME
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));
        }
        
       //添加BATCH_OPER_NAME
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String batchOperType = result.getString("BATCH_OPER_TYPE", "");
            result.put("BATCH_OPER_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BATCHTYPE","BATCH_OPER_TYPE", "BATCH_OPER_NAME", batchOperType));
        }
        
        return resultset;
    }
    
    /**
     * 查询批量任务处理结果，流量自由充产品，网厅使用
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryBatDealResultForGfffMem(IData data,Pagination pagination) throws Exception
    {
    	
    	SQLParser parser = new SQLParser(data);

		parser.addSQL(" SELECT  BATCH_TASK_ID, ");
		parser.addSQL(" BATCH_ID, ");
		parser.addSQL(" BATCH_OPER_TYPE, ");
		parser.addSQL(" TO_CHAR(REFER_TIME, 'YYYY-MM-DD HH24:MI:SS') REFER_TIME, ");
		parser.addSQL(" TO_CHAR(EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
		parser.addSQL(" SERIAL_NUMBER, ");
		parser.addSQL(" CANCEL_TAG, ");
		parser.addSQL(" DEAL_STATE, ");
		parser.addSQL(" DEAL_RESULT, ");
		//parser.addSQL(" DEAL_DESC, ");
		parser.addSQL(" TRADE_ID, ");
		parser.addSQL(" TO_CHAR(DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS') DEAL_TIME ");
		parser.addSQL(" FROM TF_B_TRADE_BATDEAL T,TD_S_COMMPARA B ");
		parser.addSQL("  WHERE 1 = 1 ");
		parser.addSQL("   AND T.BATCH_ID = :BATCH_ID ");
		parser.addSQL("   AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("   AND TO_DATE(TO_CHAR(T.REFER_TIME,'YYYY-MM-DD'),'YYYY-MM-DD') >=  TO_DATE(:START_TIME,'YYYY-MM-DD') ");
		parser.addSQL("   AND TO_DATE(TO_CHAR(T.REFER_TIME,'YYYY-MM-DD'),'YYYY-MM-DD') <=  TO_DATE(:END_TIME,'YYYY-MM-DD') ");
		parser.addSQL("   AND T.DEAL_STATE = :DEAL_STATE ");
		parser.addSQL("   AND T.BATCH_OPER_TYPE = B.PARA_CODE1 ");
		parser.addSQL("   AND B.PARAM_ATTR = '7350' ");
		parser.addSQL("   AND B.SUBSYS_CODE = 'CSM' ");
		parser.addSQL("   AND B.PARAM_CODE = 'GRP_GFFF' ");
		parser.addSQL("   AND B.END_DATE > SYSDATE ");
					   
		parser.addSQL("   ORDER BY T.REFER_TIME DESC ");
				 
		IDataset resultset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
	        
        //添加DEAL_STATE_NAME
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));
        }
	        
        //添加BATCH_OPER_NAME
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String batchOperType = result.getString("BATCH_OPER_TYPE", "");
            result.put("BATCH_OPER_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BATCHTYPE","BATCH_OPER_TYPE", "BATCH_OPER_NAME", batchOperType));
        }
        
        return resultset;

    }
    
    
    
    /**
     * 查询批量任务处理结果(根据集团产品编码、批量流水号、查询号码)
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryFluxBatDealResult(IData data,Pagination pagination) throws Exception
    {
    	
    	SQLParser parser = new SQLParser(data);

		parser.addSQL(" SELECT  T.BATCH_TASK_ID, ");
		parser.addSQL(" T.BATCH_ID, ");
		parser.addSQL(" T.BATCH_OPER_TYPE, ");
		parser.addSQL(" TO_CHAR(REFER_TIME, 'YYYY-MM-DD HH24:MI:SS') REFER_TIME, ");
		parser.addSQL(" TO_CHAR(EXEC_TIME, 'YYYY-MM-DD HH24:MI:SS') EXEC_TIME, ");
		parser.addSQL(" SERIAL_NUMBER, ");
		parser.addSQL(" CANCEL_TAG, ");
		parser.addSQL(" DEAL_STATE, ");
		parser.addSQL(" DEAL_RESULT, ");
		//parser.addSQL(" DEAL_DESC, ");
		parser.addSQL(" TRADE_ID, ");
		parser.addSQL(" TO_CHAR(DEAL_TIME, 'YYYY-MM-DD HH24:MI:SS') DEAL_TIME ");
		parser.addSQL(" FROM TF_B_TRADE_BATDEAL T,TD_S_COMMPARA B, TF_B_TRADE_BAT_TASK A ");
		parser.addSQL("  WHERE 1 = 1 ");
		parser.addSQL("   AND A.CODING_STR1 LIKE '%' || :GRP_SERIAL_NUMBER || '%' ");
		parser.addSQL("   AND T.BATCH_ID = :BATCH_ID ");
		parser.addSQL("   AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("   AND TO_DATE(TO_CHAR(T.REFER_TIME,'YYYY-MM-DD'),'YYYY-MM-DD') >=  TO_DATE(:START_TIME,'YYYY-MM-DD') ");
		parser.addSQL("   AND TO_DATE(TO_CHAR(T.REFER_TIME,'YYYY-MM-DD'),'YYYY-MM-DD') <=  TO_DATE(:END_TIME,'YYYY-MM-DD') ");
		parser.addSQL("   AND T.DEAL_STATE = :DEAL_STATE ");
		parser.addSQL("   AND T.BATCH_OPER_TYPE = B.PARA_CODE1 ");
		parser.addSQL("   AND A.BATCH_OPER_CODE = 'OPENGROUPGFFFDISCNT' ");
		parser.addSQL("   AND A.BATCH_TASK_ID = T.BATCH_TASK_ID ");
		parser.addSQL("   AND B.PARAM_ATTR = '7350' ");
		parser.addSQL("   AND B.SUBSYS_CODE = 'CSM' ");
		parser.addSQL("   AND B.PARAM_CODE = 'GRP_GFFF' ");
		parser.addSQL("   AND B.END_DATE > SYSDATE ");
					   
		parser.addSQL("   ORDER BY T.REFER_TIME DESC ");
				 
		IDataset resultset = Dao.qryByParse(parser, pagination, Route.getJourDb(Route.CONN_CRM_CG));
	        
        //添加DEAL_STATE_NAME
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String dealState = result.getString("DEAL_STATE", "");
            result.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
            { "BAT_TASK_STATE_TAG", dealState }));
        }
	        
        //添加BATCH_OPER_NAME
        for (int i = 0, size = resultset.size(); i < size; i++)
        {
            IData result = resultset.getData(i);
            String batchOperType = result.getString("BATCH_OPER_TYPE", "");
            result.put("BATCH_OPER_NAME", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BATCHTYPE","BATCH_OPER_TYPE", "BATCH_OPER_NAME", batchOperType));
        }
        
        return resultset;

    }
    
    /**
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryBatDealInfoCntForImsGrp(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT COUNT(*) CNT ");
		parser.addSQL("  FROM UCR_CRM1.TF_B_TRADE_BATDEAL T ");
		parser.addSQL(" WHERE T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		parser.addSQL("   AND T.BATCH_ID = :BATCH_ID ");
		parser.addSQL("   AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        IDataset resultset = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return resultset;
    }
    
    /**
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryShortCodeCntForImsGrp(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT COUNT(*) CNT ");
		parser.addSQL("  FROM UCR_CRM1.TF_B_TRADE_BATDEAL T ");
		parser.addSQL(" WHERE T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
		parser.addSQL("   AND T.BATCH_ID = :BATCH_ID ");
		parser.addSQL("   AND T.DATA1 = :SHORT_CODE ");
        IDataset resultset = Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
        return resultset;
    }
    
}
