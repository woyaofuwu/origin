package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RemoteDestroyUserDao {
	public static IDataset queryApplyDestroyUserTrade(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  * ");
        parser.addSQL("FROM TF_B_DISPATCH_ORDER ");
        parser.addSQL("WHERE REMOTE_SERIAL_NUMBER=:REMOTE_SERIAL_NUMBER ");
        parser.addSQL("AND DEAL_TAG=:DEAL_TAG ");
        parser.addSQL("ORDER BY CREATE_DATE DESC");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    public static IDataset queryApplyDestroyUserTradeByOrderId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT * ");
        parser.addSQL("FROM TF_B_DISPATCH_ORDER ");
        parser.addSQL("WHERE REMOTE_ORDER_ID=:REMOTE_ORDER_ID");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
	public static boolean insertApplyDestroyUserTrade(IData param) throws Exception
    {
        return Dao.insert("TF_B_DISPATCH_ORDER", param, Route.CONN_CRM_CEN);
    }
	public static int updateApplyDestroyUserTrade(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TF_B_DISPATCH_ORDER ");
        parser.addSQL(" SET DEAL_TAG = :DEAL_TAG ");
        parser.addSQL(" ,REMOTE_SERIAL_NUMBER = :REMOTE_SERIAL_NUMBER ");
        parser.addSQL(" ,RSRV_STR1 = to_char(to_date(:RSRV_STR1,'yyyy-MM-dd'),'yyyy-MM-dd') ");
        parser.addSQL(" ,RSRV_STR3 = :RSRV_STR3 ");
        parser.addSQL(" ,RSRV_STR4 = :RSRV_STR4 ");
        parser.addSQL(" ,RSRV_STR5 = :RSRV_STR5 ");
        parser.addSQL(" ,RSRV_STR6 = :RSRV_STR6 ");
        parser.addSQL(" ,RSRV_STR7 = :RSRV_STR7 ");
        parser.addSQL(" ,RSRV_STR8 = :RSRV_STR8 ");
        if("A".equals(param.getString("RSRV_STR10"))){
        	parser.addSQL(" ,RSRV_STR10 = null ");
        }
        parser.addSQL(" ,DEAL_INFO = :DEAL_INFO ");
        parser.addSQL(" ,UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" ,FINISH_TIME = to_date(:FINISH_TIME,'yyyy-mm-dd hh24:mi:ss') ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND REMOTE_ORDER_ID = :REMOTE_ORDER_ID ");
        return Dao.executeUpdate(parser, Route.CONN_CRM_CEN);  
    }
	public static boolean insertReceiveDestroyUserTrade(IData param) throws Exception
    {
		return Dao.insert("TF_B_RECEIPT_ORDER", param, Route.CONN_CRM_CEN);
    }
    
    public static boolean updateReceiveDestroyUserTrade(IData param) throws Exception
    {
        try{
	        SQLParser parser = new SQLParser(param);
	        parser.addSQL(" UPDATE TF_B_RECEIPT_ORDER ");
	        parser.addSQL(" SET ORDER_ID = :ORDER_ID ");
	        parser.addSQL(" ,CASH_AMOUNT = :CASH_AMOUNT ");
	        parser.addSQL(" ,NOCASH_AMOUNT = :NOCASH_AMOUNT ");
	        parser.addSQL(" ,UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
	        parser.addSQL(" ,FINISH_TIME = to_date(:FINISH_TIME,'yyyy-mm-dd hh24:mi:ss') ");
	        parser.addSQL(" ,DEAL_TAG = :DEAL_TAG ");
	        parser.addSQL(" ,RSRV_STR1 = :RSRV_STR1 ");
	        parser.addSQL(" ,RSRV_STR4 = :RSRV_STR4 ");
	        parser.addSQL(" ,RSRV_STR5 = :RSRV_STR5 ");
	        parser.addSQL(" ,RSRV_STR6 = :RSRV_STR6 ");
	        parser.addSQL(" ,RSRV_STR7 = :RSRV_STR7 ");
	        parser.addSQL(" ,DEAL_INFO = :DEAL_INFO ");
	        parser.addSQL(" WHERE 1=1 ");
	        parser.addSQL(" AND ORDER_ID = :ORDER_ID ");
	        Dao.executeUpdate(parser, Route.CONN_CRM_CEN); 
	        return true;
        }catch(Exception e){
        	return false;
        }
        
    }
     
    
	public static IDataset queryReceiveDestroyUserTrade(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  * ");
        parser.addSQL("FROM TF_B_RECEIPT_ORDER ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL("AND ORDER_ID =:ORDER_ID ");
        parser.addSQL("AND DEAL_TAG=:DEAL_TAG");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    public static IDataset queryReceiveDestroyOrderHis(IData param,Pagination pagination) throws Exception
    {
    	if(StringUtils.isNotBlank(param.getString("cond_SERIAL_NUMBER",""))){
    		param.put("SERIAL_NUMBER", param.getString("cond_SERIAL_NUMBER",""));
    	}
        SQLParser parser = new SQLParser(param);
		String deal_tag = param.getString("DEAL_TAG", "");
		 parser.addSQL("select a.* from ");
         parser.addSQL("(select t.*,row_number() over(partition by serial_number,cust_name order by deal_tag desc,finish_time desc) as row_flag ");
         parser.addSQL("from tf_b_receipt_order t where 1=1 ");
         parser.addSQL("and t.deal_tag =:DEAL_TAG ");
         parser.addSQL("and t.serial_number=:SERIAL_NUMBER ");
         parser.addSQL("and t.create_date between to_date(:START_TIME, 'yyyy-mm-dd hh24:mi:ss') and to_date(:END_TIME, 'yyyy-mm-dd hh24:mi:ss') ");
         if(StringUtils.isBlank(deal_tag)){
        	 parser.addSQL("and t.deal_tag in ('0','1') ");
         }
         parser.addSQL(") a ");
         parser.addSQL("where 1=1 and a.row_flag='1' ");
         if("0".equals(deal_tag)){
        	 parser.addSQL("and not exists(select 1 from tf_b_receipt_order b where b.serial_number=a.serial_number and b.cust_name=a.cust_name and b.deal_tag='1') ");
         }
         if(!"9".equals(deal_tag)){
        	 parser.addSQL("and not exists(select 1 from tf_b_receipt_order b where b.serial_number=a.serial_number and b.cust_name=a.cust_name and b.deal_tag='9') ");
         }
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);//Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
   /**K3
    *存放身份证正反面
    * @param param
    * @return
    * @throws Exception
    */
    public static boolean insertPsptPic(IData param) throws Exception
    {
        return Dao.insert("TF_F_USER_PSPT_FRONTBACK", param, Route.getCrmDefaultDb());
    }
    /**
	 * k3
	 * 查询当天收到的工单(去重并取最新的记录)
	 * @return
	 * @throws Exception
	 */
    public static IDataset qryNowDayOrder(IData param)throws Exception{
    	   
    	   SQLParser parser = new SQLParser(param);
           parser.addSQL("select a.* from ");
           parser.addSQL("(select t.*,row_number() over(partition by serial_number order by finish_time desc) as row_flag ");
           parser.addSQL("from tf_b_receipt_order t where 1=1 ");
           parser.addSQL("and t.deal_tag =:DEAL_TAG ");
           parser.addSQL("and trunc(create_date) = trunc(sysdate)) a ");
           parser.addSQL("where 1=1 ");
           parser.addSQL("and a.deal_tag=:DEAL_TAG ");
           parser.addSQL("and a.row_flag='1' ");
           return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    /**
     * k3
     * 自动销户失败更新接单表
     * @return
     * @throws Exception
     */
    public static boolean updateDestroyFail(IData param) throws Exception
    {
        try{
	        SQLParser parser = new SQLParser(param);
	        parser.addSQL(" UPDATE TF_B_RECEIPT_ORDER ");
	        parser.addSQL(" SET RSRV_STR1 = :RSRV_STR1 ");
	        parser.addSQL(" ,UPDATE_TIME = to_date(:UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') ");
	        parser.addSQL(" ,RSRV_STR3 = :RSRV_STR3 ");
	        parser.addSQL(" WHERE 1=1 ");
	        parser.addSQL(" AND ORDER_ID = :ORDER_ID ");
	        parser.addSQL("AND DEAL_TAG=:DEAL_TAG");
	        Dao.executeUpdate(parser, Route.CONN_CRM_CEN); 
	        return true;
        }catch(Exception e){
        	return false;
        }
        
    }
    public static IDataset queryDestroyLocalOrder(IData param,Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT  * ");
        parser.addSQL("FROM TF_B_DISPATCH_ORDER ");
        parser.addSQL("WHERE 1=1 ");
        parser.addSQL("AND DEAL_TAG=:DEAL_TAG ");
        if("0".equals(param.getString("DESTROY_REMIND_TAG"))&&StringUtils.isBlank(param.getString("REMOTE_SERIAL_NUMBER"))){
        	parser.addSQL("AND RSRV_STR8 is null ");
        	parser.addSQL("ORDER BY UPDATE_TIME DESC");
        }else if("1".equals(param.getString("DESTROY_REMIND_TAG"))&&StringUtils.isBlank(param.getString("REMOTE_SERIAL_NUMBER"))){
        	parser.addSQL("AND RSRV_STR8 is not null ");
        	parser.addSQL("ORDER BY UPDATE_TIME ASC");
        }else if("1".equals(param.getString("DESTROY_REMIND_TAG"))&&StringUtils.isNotBlank(param.getString("REMOTE_SERIAL_NUMBER"))){
        	parser.addSQL("AND REMOTE_SERIAL_NUMBER=:REMOTE_SERIAL_NUMBER ");
        	parser.addSQL("AND RSRV_STR8 is not null ");
        	parser.addSQL("ORDER BY UPDATE_TIME ASC");
        }
        return Dao.qryByParse(parser,pagination, Route.CONN_CRM_CEN);
    }
    /**
     * 查询被催工单
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryDestroyReceiOrder(IData param,Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL("select a.ORDER_ID,a.TRADE_ID,a.USER_ID,a.SERIAL_NUMBER,a.CUST_NAME ");
    	parser.addSQL(",a.GIFT_SERIAL_NUMBER,a.GIFT_CUST_NAME,a.GIFT_SERIAL_NUMBER_B,a.GIFT_CUST_NAME_B ");
    	parser.addSQL(",a.CONTACT_PHONE,a.CONTACT_NAME,a.IDEN_HEAD,a.IDEN_BACK,a.PIC_NAME_R ");
    	parser.addSQL(",a.CREATE_DATE,a.CREATE_STAFF_ID,a.CREATE_DEPART_ID,a.CREATE_EPARCHY_CODE ");
    	parser.addSQL(",a.CREATE_CITY_CODE,a.CREATE_PHONE,a.CREATE_CONTACT,a.CREATE_ORG_NAME ");
    	parser.addSQL(",a.RELATION_ORDER_ID,a.BUSINESS_TRADE_ID,a.DEAL_TAG,a.ACCOUNT_TAG,a.DEAL_INFO ");
    	parser.addSQL(",a.FINISH_TIME,a.UPDATE_TIME,a.CASH_AMOUNT,a.NOCASH_AMOUNT,a.REMARKS,a.RSRV_STR1,a.RSRV_STR2 ");
    	parser.addSQL(",a.RSRV_STR3,a.RSRV_STR4,a.RSRV_STR5,nvl(a.RSRV_STR6,to_char(a.create_date+7,'yyyyMMdd')) RSRV_STR6 ");
    	parser.addSQL(",a.RSRV_STR7,nvl(a.RSRV_STR7,'0') URGE_STATUS,a.RSRV_STR8,a.RSRV_STR9,a.RSRV_STR10 ");
    	parser.addSQL(",(case when trunc(to_date(nvl(a.RSRV_STR6,to_char(a.create_date+7,'yyyy-MM-dd')),'yyyy-MM-dd')) > trunc(sysdate) then '0' else '1' end) as TIMEOUT_STATUS ");
    	parser.addSQL("from (select t.*,row_number() over(partition by serial_number,cust_name order by nvl(t.RSRV_STR7,'0') desc,update_time desc) as row_flag ");
    	parser.addSQL("from ucr_cen1.tf_b_receipt_order t where t.deal_tag='0' ");
    	parser.addSQL("and t.serial_number=:SERIAL_NUMBER ");
    	parser.addSQL(") a  ");
    	parser.addSQL("where 1=1 and a.row_flag='1' and not exists(select 1 from tf_b_receipt_order b where b.serial_number=a.serial_number and b.cust_name=a.cust_name and b.deal_tag='9') ");
    	parser.addSQL("order by URGE_STATUS desc,a.update_time asc ");
    	
    	return Dao.qryByParse(parser,pagination, Route.CONN_CRM_CEN);
    }
}
