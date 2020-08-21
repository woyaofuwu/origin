package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;

/**
 * PCC需求
 */
public class PCCBusinessQry
{
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static IDataset qryPCCBusinessList(IData  inParam) throws Exception {
		IDataset PCCBusinessList= new DatasetList();
		PCCBusinessList.addAll(qryPCCBusinessListForSubscriber(inParam));
		PCCBusinessList.addAll(qryPCCBusinessListForPolocy(inParam));
		PCCBusinessList.addAll(qryPCCBusinessListForService(inParam));
		return PCCBusinessList;
	}
	/**
	 * select 
	 */
	public static IDataset qryPCCBusinessListForSubscriber(IData  inParam) throws Exception {
		IDataset PCCBusinessList = Dao.qryByCode("TI_O_PCC_TASK","SEL_PCC_TASK_SUBSCRIBER",inParam,Route.CONN_CRM_CEN );
		return PCCBusinessList;
	}
	
	/**
	 * select 
	 */
	public static IDataset qryPCCBusinessListForPolocy(IData  inParam) throws Exception {
		IDataset PCCBusinessList = Dao.qryByCode("TI_O_PCC_TASK","SEL_PCC_TASK_POLOCY",inParam,Route.CONN_CRM_CEN );
		return PCCBusinessList;
	}
	
	/**
	 * select
	 */
	public static IDataset qryPCCBusinessListForService(IData  inParam) throws Exception {
		IDataset PCCBusinessList = Dao.qryByCode("TI_O_PCC_TASK","SEL_PCC_TASK_SERVICE",inParam,Route.CONN_CRM_CEN );
		return PCCBusinessList;
	}
	
	/**
	 * insert 
	 */
	public static int[] insertPCCBusinessListForSubscriber(IDataset  inParam) throws Exception {
		return Dao.executeBatchByCodeCode("TI_O_PCC_SUBSCRIBER","INS_PCC_TASK_SUBSCRIBER",inParam,Route.CONN_CRM_CEN );
	}
	
	/**
	 * insert 
	 */
	public static int[] insertPCCBusinessListForPolocy(IDataset  inParam) throws Exception {
		return Dao.executeBatchByCodeCode("TI_O_PCC_POLOCY","INS_PCC_TASK_POLOCY",inParam,Route.CONN_CRM_CEN );
	}
	
	/**
	 * insert
	 */
	public static int[] insertPCCBusinessListForService(IDataset  inParam) throws Exception {
		return Dao.executeBatchByCodeCode("TI_O_PCC_SERVICE","INS_PCC_TASK_SERVICE",inParam,Route.CONN_CRM_CEN );
	}
	
	/**
	 * update
	 */
	public static int updPCCBusiness(IData  inParam) throws Exception {
		int   i=Dao.executeUpdateByCodeCode("TI_O_PCC_TASK","UPD_PCC_TASK",inParam,Route.CONN_CRM_CEN);
		return i;
	}
	 /**
     * select
     */
    public static IDataset qryPccOperationTypeForSubscriber(IData inParam) throws Exception
    {
        SQLParser parser = new SQLParser(inParam);
        parser.addSQL("select * from TI_O_PCC_SUBSCRIBER a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.Usr_Identifier = :USRIDENTIFIER ");
        parser.addSQL(" and a.USER_ID = :USER_ID ");
        parser.addSQL(" and a.ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL(" and a.usr_status  in ('1','2','3','4','6','8') ");
        parser.addSQL(" and a.IN_DATE >= trunc(add_months(last_day(sysdate),-1)+1) ");
        parser.addSQL(" order by IN_DATE DESC");
        return Dao.qryByParse(parser,Route.CONN_CRM_CEN); 
    }
    
    public static IDataset qryPccHOperationTypeForSubscriber(IData inParam) throws Exception
    {
        SQLParser parser = new SQLParser(inParam);
        parser.addSQL("select * from TI_OH_PCC_SUBSCRIBER a ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and a.Usr_Identifier = :USRIDENTIFIER ");
        parser.addSQL(" and a.USER_ID = :USER_ID ");
        parser.addSQL(" and a.ACCEPT_MONTH = :ACCEPT_MONTH ");
        parser.addSQL(" and a.usr_status  in ('1','2','3','4','6','8') ");
        parser.addSQL(" and a.IN_DATE >= trunc(add_months(last_day(sysdate),-1)+1) ");
        parser.addSQL(" order by IN_DATE DESC");
        return Dao.qryByParse(parser,Route.CONN_CRM_CEN); 
    }
    
    /**
     * select
     */
    public static IDataset qryPccOperationTypeForPolocy(IData inParam) throws Exception
    {
        SQLParser parser = new SQLParser(inParam);
        parser.addSQL("select OPERATION_TYPE from TI_O_PCC_POLOCY a ");
        parser.addSQL(" where 1=1 ") ;
        parser.addSQL(" and a.USER_ID = :USER_ID ");
        parser.addSQL(" and a.USR_SESSION_POLICY_CODE = :USR_SESSION_POLICY_CODE ") ;
        parser.addSQL(" order by IN_DATE DESC");
        return Dao.qryByParse(parser,Route.CONN_CRM_CEN); 
    }
    
    /**
     * 是否为4G用户
     */
    public static boolean is4GUser(String userId) throws Exception
    {
        boolean user4GFlag = false;
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        SQLParser parser = new SQLParser(inParam );
        parser.addSQL("select count(1) FLAG from TF_F_USER_RES where user_id=TO_NUMBER(:USER_ID) ");
        parser.addSQL(" AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) ") ;
        parser.addSQL(" AND end_date >= SYSDATE ");
        parser.addSQL(" AND RSRV_TAG3 = '1' ") ; 
        IDataset result = Dao.qryByParse(parser); 
        
        String flag = result.getData(0).getString("FLAG");
        if (!"0".equals(flag))
        {
            user4GFlag = true;
        }
        
        return user4GFlag;
        
    }
    /**
     * 是否订购购流量可选包
     */
    public static boolean isGPRSUser(String userId) throws Exception
    {
        //是套餐用户
        boolean userGPRSFlag = false;
        
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);  
        
        //是否订购了安心包,加油包
        boolean isPackageUser = isGPRSPackage(inParam);
        
        //订购了安心包,加油包则不是套餐内用户
        if (isPackageUser)
        {
            return userGPRSFlag;
        } 
        
        SQLParser parser = new SQLParser(inParam );
        parser.addSQL(" select * from　TF_F_USER_DISCNT b　where  1 = 1 ");       
        parser.addSQL(" AND b.user_id=TO_NUMBER(:USER_ID) ") ;
        parser.addSQL(" AND b.partition_id = MOD(TO_NUMBER(:USER_ID),10000) ") ;
        parser.addSQL(" AND b.end_date >= SYSDATE    ") ; 
        
        IDataset result = Dao.qryByParse(parser);  
        
        String flag =  "0";
        if(IDataUtil.isNotEmpty(result) && result.size() > 0){
        	for(int i =0; i < result.size();i++ ){
        		IData discntInfo = result.getData(i);
        		if(IDataUtil.isNotEmpty(discntInfo)){
        			String discntCode = discntInfo.getString("DISCNT_CODE","");
        			IDataset discntTypes = UDiscntInfoQry.queryDiscntTypeByDiscntCode(discntCode);
        			if(IDataUtil.isNotEmpty(discntTypes) && discntTypes.size() > 0){
        				IData discntType = discntTypes.getData(0);
        				if(IDataUtil.isNotEmpty(discntType)){
        					if("5".equals(discntType.getString("FIELD_VALUE",""))){
        						flag = "1";
        						break ;
        					}
        				}
        			}
        		}
        	}
        }
        
        if (!"0".equals(flag))
        {
            userGPRSFlag = true;
        } 
    
        
        return userGPRSFlag;
        
    }
    
 
    
    /**
     * 查询安心包加油包
     */
    private static boolean isGPRSPackage(IData inParam) throws Exception
    {
        boolean ispackage = false;
        SQLParser parser = new SQLParser(inParam );
        parser.addSQL(" select count(1) FLAG from　TF_F_USER_DISCNT b　where 1 = 1 ");       
        parser.addSQL(" AND b.user_id=TO_NUMBER(:USER_ID) ") ;
        parser.addSQL(" AND b.partition_id = MOD(TO_NUMBER(:USER_ID),10000) ") ;
        parser.addSQL(" AND b.end_date >= SYSDATE    ") ; 
        parser.addSQL(" AND b.discnt_code in ('6630','6631','6632','6633') ");  
        IDataset result = Dao.qryByParse(parser);  
        
        String flag = result.getData(0).getString("FLAG");
        if (!"0".equals(flag))
        {
            ispackage = true;
        } 
        return ispackage;
        
    }
}