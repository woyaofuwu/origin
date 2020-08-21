package  com.asiainfo.veris.crm.order.soa.person.busi.club;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ClubMemberApplyBean extends CSBizBean
{
	//protected static Logger log = Logger.getLogger(ClubMemberApplyBean.class);
	 /**
     * 查询待赠送积分的用户信息。
     * */
    public IDataset qryScoreUserInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" SELECT ROWID, SERIAL_NUMBER, PSPT_ID, ACTIVE_FLAG, EFFECTIVE_DATE, UPDATE_TIME, RECEIPT_STATE, CLUB_NAME, "); 
        parser.addSQL(" UNIVERRSITY_NAME, STUDENT_ID, FACULTIE_NAME, MAJO_NAME, ENTRANCE_YEAR, KEY_ID FROM TF_O_USER_FOURGCLUB WHERE ");
        parser.addSQL(" ACTIVE_FLAG= '0'  AND ROWNUM <= :SEL_CURSOR AND TO_NUMBER(TO_CHAR(UPDATE_TIME, 'MM')) >= TO_NUMBER('8') AND"); 
        parser.addSQL(" TO_NUMBER(TO_CHAR(UPDATE_TIME, 'MM')) <= TO_NUMBER('11') AND add_months(EFFECTIVE_DATE, 6) < sysdate AND RECEIPT_STATE = '0'"); 

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 效验待赠送积分用户信息。
     * */
    public IDataset checkScoreUserInfo(IData input) throws Exception
    {
    	////log.info("("linsl checkScoreUserInfo Input : " + input.toString());
    	SQLParser parser = new SQLParser(input); 
  	  
    	parser.addSQL(" SELECT * FROM UCR_CRM1.TF_F_USER  "); 
        parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER AND OPEN_DATE > TO_DATE(SUBSTR(:UPDATE_TIME, 0, 4) || '-8-1', 'YYYY-MM-DD') "); 
        
        return Dao.qryByParse(parser);
    }
    
    /**
     * 效验待赠送积分用户信息。
     * */
    public IDataset updScoreResultInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL("UPDATE TF_O_USER_FOURGCLUB SET ACTIVE_FLAG = :ACTIVE_FLAG, TRADE_ID=:TRADE_ID, RESULT_INFO=:RESULT_INFO"); 
        parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER AND ACTIVE_FLAG= '0'  ");
        
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    private void dealCallService(IData input) throws Exception
	{
		IData svcParam = new DataMap();
    	IData upData = new DataMap();
		
		////log.info("("linsl The input param is " + input.toString());
    	
        svcParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
        
        svcParam.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
		svcParam.put("PROVINCE_CODE", "0898");
		svcParam.put("EPARCHY_CODE", "0898");
		svcParam.put("CITY_CODE", "HNSJ");
		svcParam.put("TRADE_CITY_CODE","HNSJ");
		svcParam.put("TRADE_DEPART_ID","36601");
		svcParam.put("STAFF_ID","SUPERUSR");
		svcParam.put("TRADE_STAFF_ID","SUPERUSR");
		svcParam.put("IN_MODE_CODE","0");
		
		svcParam.put("STAFF_NAME","AEE调用");
		svcParam.put("LOGIN_EPARCHY_CODE","0898");
		svcParam.put("STAFF_EPARCHY_CODE","0898");
		svcParam.put("DEPART_ID","36601");
		svcParam.put("DEPART_CODE","HNSJ0000");
		
		CSBizBean.getVisit().setStaffId("SUPERUSR");
        CSBizBean.getVisit().setCityCode("0898");
        CSBizBean.getVisit().setDepartId("36601");
        CSBizBean.getVisit().setInModeCode("0");
		
		////log.info("("linsl The call svc param is " + svcParam.toString());
		
		try 
		{

			  IDataset intfReturnDataset = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", svcParam);

	          if (IDataUtil.isNotEmpty(intfReturnDataset))
	          {
	                String orderId = intfReturnDataset.getData(0).getString("ORDER_ID");
	                if(orderId!=null&&orderId.length()>0)
	                {
	                	
	        			upData.put("ACTIVE_FLAG", "3");
	        			upData.put("RESULT_INFO", "success");
	                	
	                }
	                else
	                {
	 					upData.put("ACTIVE_FLAG", "2");	
	 					upData.put("RESULT_INFO", "调用营销活动接口未生成工单!");
	                }
	                upData.put("TRADE_ID", intfReturnDataset.getData(0).getString("TRADE_ID", ""));
	                updScoreResultInfo(upData); 
	          }
	             		
		}catch(Exception e) 
		{
			upData.put("ACTIVE_FLAG", "2");
			upData.put("TRADE_ID", "");
			upData.put("RESULT_INFO", e.getMessage());
			updScoreResultInfo(upData);		
		}
	}
    
    public IData dealScore(IData input) throws Exception
    {
    	IData result = new DataMap();
    		
		//1、查询待赠送积分的用户数据
		IData qryData = new DataMap();
		qryData.put("SEL_CURSOR", input.getString("SEL_CURSOR"));
			 
		IDataset list = qryScoreUserInfo(qryData);
			
		//效验有没有捞到用户数据
		if (list == null || list.size() == 0) 
		{
			////log.info("("qryBookUserInfo:" + " fatch 0 data!"+ "list.size =" + list.size() + "ONCE_NUM=" + onceNum + "PARTION_ID=" + partionId );
			result.put("X_RESULTCODE","0");
			result.put("X_RESULTINFO","没有捞到待赠送积分用户数据！");
			return result;
		}
			
		for(int i=0,size=list.size(); i<size; i++) 
		{
			IData info = new DataMap();
			info.putAll(list.getData(i));
			//传入赠送积分的产品包，活动包ID
			info.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
			info.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
			
			//过滤掉8月1日后开户的用户
			IData param = new DataMap();	                                                                        	
			param.put("SERIAL_NUMBER", info.get("SERIAL_NUMBER"));
			param.put("UPDATE_TIME", info.get("UPDATE_TIME"));
			IDataset exRes = checkScoreUserInfo(param);
						
			if(exRes == null && exRes.size() == 0)
			{//过滤掉8月1日后开户的用户，记录对应信息到结果表
				IData UpdateParam = new DataMap();
				UpdateParam.put("ACTIVE_FLAG", "2");
				UpdateParam.put("TRADE_ID", "");
				UpdateParam.put("RESULT_INFO", "用户开户时间" + info.getString("UPDATE_TIME") + "大于8月1日");
				UpdateParam.put("SERIAL_NUMBER", info.get("SERIAL_NUMBER"));
				updScoreResultInfo(UpdateParam);
				continue;
			}
				
			//try...catch 一条数据接口抛错，不影响下一条执行
			try {
						dealCallService(info);
				}
				catch (Exception e)
				{
					// logger(info.get("SERIAL_NUMBER") + ": " + e.getMessage());
					result.put("X_RESULTCODE","-1");
					result.put("X_RESULTINFO","调营销活动接口异常！");
					return result;
				}	
			}//for
			result.put("X_RESULTCODE","1");
			result.put("X_RESULTINFO","已完成该批次调用!");
			return result;
		
		}

    public IData getCustInfo(IData data) throws Exception
    {
    	IData param = new DataMap();
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
		param.put("USER_ID", data.getString("USER_ID", ""));
		param.put("CUST_ID", data.getString("CUST_ID", ""));
		IData custInfos = CSAppCall.callOne("SS.ModifyCustInfoSVC.getCustInfo", param);
	        
        return custInfos.getData("CUST_INFO");
    }
    
   
    
    public IDataset createApplyInfo(IData data)throws Exception{
		
    	IDataset result=new DatasetList();
    	IData temp=new DataMap();
    	IData tmp=new DataMap();
		/*String userId = data.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		temp.put("USER_ID", userId);
		temp.put("PARTITION_ID",partition_id);
		temp.put("CONTACT_ID", data.getString("CONTACT_ID"));
		temp.put("REGIST_TIME", data.getString("REGIST_TIME"));
		temp.put("IDENT_UNEFFT", data.getString("IDENT_UNEFFT"));
		temp.put("IDENT_CODE", data.getString("IDENT_CODE"));
		temp.put("UPDATE_TIME",SysDateMgr.getSysDate());
		temp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));*/
		
		temp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		//temp.put("PSPT_ID", data.getString("PSPT_ID"));
		temp.put("ACTIVE_FLAG", "0");
		//temp.put("EFFECTIVE_DATE",SysDateMgr.getSysDate());
		//temp.put("UPDATE_TIME",SysDateMgr.getSysDate());
		//temp.put("CLUB_NAME", "动感地带俱乐部");
		temp.put("EFFECTIVE_DATE", SysDateMgr.getSysDate());
		temp.put("UPDATE_TIME",SysDateMgr.getSysDate());
		temp.put("CLUB_NAME", data.getString("CUST_NAME",""));
		temp.put("PSPT_ID", data.getString("PSPT_ID",""));
		temp.put("UNIVERRSITY_NAME", data.getString("UNIVERRSITY_NAME",""));
		temp.put("STUDENT_ID", data.getString("STUDENT_ID",""));
		temp.put("FACULTIE_NAME", data.getString("FACULTIE_NAME",""));
		temp.put("MAJO_NAME", data.getString("MAJO_NAME",""));
		temp.put("ENTRANCE_YEAR", data.getString("ENTRANCE_YEAR",""));
		
		
		if(!Dao.insert("TF_O_USER_FOURGCLUB", temp,Route.CONN_CRM_CEN))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_2999);
		}
		else	
		{
			tmp.put("IS_SUCCESS", "TRUE");
			result.add(tmp);
		}
		return result;
	}
    
}
