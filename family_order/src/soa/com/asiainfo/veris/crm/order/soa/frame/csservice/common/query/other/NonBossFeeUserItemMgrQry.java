
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 

public class NonBossFeeUserItemMgrQry extends CSBizBean
{

	 /**
     * 根据条件查询业务客户参数
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150206  
     */
    public static IDataset queryNonBossFeeUserItems(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams); 
  
        parser.addSQL(" select T.TYPE_ID, T.DATA_ID, T.DATA_NAME, T.PARAM_NAME, T.PARA_CODE1, T.PARA_CODE2, T.PARA_CODE3, T.PARA_CODE4, T.PARA_CODE10, ");
        parser.addSQL(" to_char(trunc(T.START_DATE,'dd'),'yyyy-mm-dd') START_DATE, to_char(trunc(T.END_DATE,'dd'),'yyyy-mm-dd') END_DATE,  T.UPDATE_STAFF_ID, T.UPDATE_TIME, T.REMARK "); 
        parser.addSQL(" from td_s_nonbosspara t ");
        parser.addSQL(" where t.type_id = 'PAY_USER_NAME_ALL' ");
        parser.addSQL(" and t.DATA_ID=:DATA_NAME  "); 
        parser.addSQL(" and t.PARAM_NAME = :PARAM_NAME ");
        parser.addSQL(" and systimestamp between t.start_date and nvl(t.end_date,to_date('2050-12-30','yyyy-mm-dd')) ");
        parser.addSQL(" order by to_number(t.DATA_ID) ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    /**
     * 根据条件查询业务客户下拉列表
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150206  
     */
    public static IDataset getCompanyName(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);  
        parser.addSQL(" select  t.data_id DATA_ID,t.data_id||'|'||t.data_name DATA_NAME ");
        parser.addSQL(" from td_s_nonbosspara t where t.type_id='PAY_USER_NAME_ALL' and systimestamp between t.start_date and nvl(t.end_date,to_date('2050-12-30','yyyy-mm-dd')) ");  
        parser.addSQL(" order by to_number(t.data_id) ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 查询下拉列表
     * 3个下拉列表
     * feetype（费用项目）
     * incometype（收入类别）
     * invoicetype（发票类别）
     * @throws Exception
     * @chenxy3 20150206  
     */
    public static IDataset getLists(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);  
        
        parser.addSQL(" select  t.data_id DATA_ID,t.data_id||'|'||t.data_name DATA_NAME ");
        parser.addSQL(" from td_s_nonbosspara t where t.type_id='PAY_USER_NAME_ALL' ");  
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 非出账费用项目维护
     * 查询列表内容
     * @chenxy3 20150206  
     */
    public static IDataset getDicLists(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);  
        String listType=inparams.getString("LIST_TYPE");
        String orderByStr="to_number(T.data_id)";
          
        if(!"".equals(listType)&&"fee".equals(listType)){        	
            inparams.put("TYPE_ID", "NONBOSS_FEE_ITEM");
            orderByStr="to_number(substr(T.data_id,2))";
            
        }else if(!"".equals(listType)&&"income".equals(listType)){
        	inparams.put("TYPE_ID", "INCOME_TYPE");
            orderByStr="to_number(T.data_id)";
            
        }else if(!"".equals(listType)&&"invoice".equals(listType)){
        	inparams.put("TYPE_ID", "INVOICE_TYPE");
            orderByStr="to_number(T.data_id)";
            
        }else if(!"".equals(listType)&&"tax".equals(listType)){
        	inparams.put("TYPE_ID", "ADDED_TAX_TYPE");
            orderByStr="to_number(T.data_id)";
        }else if(!"".equals(listType)&&"market".equals(listType)){
        	inparams.put("TYPE_ID", "MARKET_TYPE");
            orderByStr="to_number(T.data_id)";
        }
        parser.addSQL(" select T.DATA_ID,T.DATA_ID||'|'||T.DATA_NAME DATA_NAME from TD_S_NONBOSSPARA t  ");
        parser.addSQL(" where t.type_id=:TYPE_ID "); 
        parser.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND  nvl(T.END_DATE,to_date('2050-12-31','yyyy-mm-dd')) "); 
        //parser.addSQL(" order by "+ orderByStr );
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 非出账费用项目维护
     * 查询列表内容
     * @chenxy3 20150206  
     */
    public static IDataset getFeeLists(IData inparams) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	String between="BETWEEN";
    	String valid_flag=inparams.getString("VALID_FLAG");
    	if(!"1".equals(valid_flag)){
    		between="NOT BETWEEN";
    	}
    	parser.addSQL(" select t.DATA_ID,T.DATA_NAME DATA_NAME,");	
    	parser.addSQL(" T.PARAM_NAME PARAM_CODE,");
    	parser.addSQL(" (select a.data_name from TD_S_NONBOSSPARA A WHERE A.TYPE_ID='INCOME_TYPE' AND A.DATA_ID=T.PARAM_NAME) PARAM_NAME, ");
    	parser.addSQL(" T.PARA_CODE1 PARA_CODE1,");
    	parser.addSQL(" (select a.data_name from TD_S_NONBOSSPARA A WHERE A.TYPE_ID='ADDED_TAX_TYPE' AND A.DATA_ID=T.PARA_CODE1) PARA_CODE1_NAME, ");
    	parser.addSQL(" T.PARA_CODE2, ");
    	parser.addSQL(" T.PARA_CODE3 PARA_CODE3,");
    	parser.addSQL(" (select a.data_name from TD_S_NONBOSSPARA A WHERE A.TYPE_ID='INVOICE_TYPE' AND A.DATA_ID=T.PARA_CODE3) PARA_CODE3_NAME, ");
    	parser.addSQL(" T.PARA_CODE10 PARA_CODE10,");
    	parser.addSQL(" (select a.data_name from TD_S_NONBOSSPARA A WHERE A.TYPE_ID='MARKET_TYPE' AND A.DATA_ID=T.PARA_CODE10) PARA_CODE10_NAME, ");
    	parser.addSQL(" T.PARA_CODE4, T.PARA_CODE5, T.PARA_CODE6, T.PARA_CODE7, T.PARA_CODE8, T.PARA_CODE9,");
    	parser.addSQL(" TO_CHAR(T.START_DATE,'YYYY-MM-DD') START_DATE,TO_CHAR(T.END_DATE,'YYYY-MM-DD') END_DATE,");
    	parser.addSQL(" T.EPARCHY_CODE,T.UPDATE_STAFF_ID,T.UPDATE_DEPART_ID,T.UPDATE_TIME,T.REMARK");
    	parser.addSQL(" from TD_S_NONBOSSPARA t");
    	parser.addSQL(" where t.type_id='NONBOSS_FEE_ITEM'");
    	parser.addSQL(" AND T.PARA_CODE3=:PARA_CODE3");
    	parser.addSQL(" AND T.PARA_CODE10=:PARA_CODE10");
    	parser.addSQL(" AND T.DATA_ID=:DATA_NAME");
    	parser.addSQL(" AND T.PARAM_NAME=:PARAM_NAME");
    	parser.addSQL(" AND (SYSDATE "+between+" T.START_DATE AND nvl(T.END_DATE,to_date('2050-12-31','yyyy-mm-dd')))");
    	//parser.addSQL(" order by to_number( substr(t.data_id,2))");
    	return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 非出账费用项目维护
     * 查询列表内容
     * @chenxy3 20150206  
     */
    public static IDataset getParamMaxSequence(IData inparams) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	
    	parser.addSQL(" select (MAX(to_number(t.DATA_ID))+1) MAX_ID");	 
    	parser.addSQL(" from TD_S_NONBOSSPARA t");
    	parser.addSQL(" where t.type_id=:TYPE_ID");
  
    	return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
}
