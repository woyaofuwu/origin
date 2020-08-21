
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleActiveAutoBookWNSVC extends CSBizService
{
    private static final long serialVersionUID = 2439362128137845255L;
    //protected static Logger log = Logger.getLogger(SaleActiveAutoBookWNSVC.class);
    
	
    /**
     * 查询待预约用户信息。
     * */
    public IDataset qryBookUserInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" SELECT A.USER_ID,A.SERIAL_NUMBER,A.PRODUCT_ID,A.PACKAGE_ID  FROM UCR_CRM1.TF_F_BOOK_BROAD_BAND_USER A "); 
        parser.addSQL(" WHERE  A.BOOK_BROAD_BAND_STATE = '0' ");
        parser.addSQL(" AND ROWNUM <=:ONCENUM"); 
        parser.addSQL(" AND mod(A.USER_ID, 10) = :PARTIONID"); 

        return Dao.qryByParse(parser);
    }
    
    /**
     * 查询宽带1+营销活动是否终止。
     * */
    public IDataset qryUserIsCancel(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" SELECT A.CANCEL_DATE  FROM UCR_CRM1.TF_F_USER_SALE_ACTIVE A "); 
        parser.addSQL(" WHERE  A.USER_ID = :USER_ID ");
        parser.addSQL(" AND A.PRODUCT_ID IN (SELECT DISTINCT PARAM_CODE AS PRODUCT_ID FROM TD_S_COMMPARA WHERE PARAM_ATTR =  '956')");
        parser.addSQL(" AND A.process_tag ='0' ");
        parser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE "); 
        
        return Dao.qryByParse(parser);
    }
    
    /**
     * 获取Compara表参数。
     * */
    public IDataset qryComparaInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" SELECT PARA_CODE2 AS PRODUCT_ID, PARA_CODE3 AS PACKAGE_ID "); 
        parser.addSQL(" FROM UOP_CEN1.TD_S_COMMPARA T ");
        parser.addSQL(" WHERE T.PARAM_ATTR = '956' "); 
        parser.addSQL(" AND T.PARAM_CODE = :PRODUCT_ID "); 
        parser.addSQL(" AND T.PARA_CODE1 = :PACKAGE_ID "); 
        parser.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE "); 

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 修改用户临时表的开始预约时间为当前时间     *  
     * */
    public static void UpdStartBookInfo(IData params) throws Exception
    {
    	IData param = new DataMap();    	
    	param.put("USER_ID", params.getString("USER_ID")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" UPDATE UOP_CRM1.TF_F_BOOK_BROAD_BAND_USER T  ");
    	sql.append(" SET T.BOOK_BROAD_BAND_STATE = 1,T.BOOK_START_TIME = SYSDATE ");
    	sql.append(" WHERE USER_ID = :USER_ID");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 修改用户临时表的预约结束时间及预约结果状态     *  
     * */
    public static void UpdEndBookInfo(IData params) throws Exception
    {
    	IData param = new DataMap();    
    	param.put("BOOK_BROAD_BAND_STATE", params.getString("BOOK_BROAD_BAND_STATE")); 
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("TRADE_ID", params.getString("TRADE_ID")); 
    	param.put("RESULT_INFO", params.getString("RESULT_INFO")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" UPDATE UOP_CRM1.TF_F_BOOK_BROAD_BAND_USER T ");
    	sql.append(" SET T.BOOK_BROAD_BAND_STATE = :BOOK_BROAD_BAND_STATE,T.BOOK_END_TIME = SYSDATE,T.TRADE_ID = T.TRADE_ID || :TRADE_ID,T.RESULT_INFO = T.RESULT_INFO || :RESULT_INFO ");
    	sql.append("  WHERE USER_ID = :USER_ID");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 修改台账表标识     *  
     * */
    public static void UpdTradeInfo(IData params) throws Exception
    {
    	IData param = new DataMap();    
    	param.put("TRADE_ID", params.getString("TRADE_ID"));  
    	StringBuilder sql = new StringBuilder(1000);
    	StringBuilder newsql = new StringBuilder(1000);
    	
    	//先更新主台账表，如果主台账表中没有记录，再更新台账历史表
    	sql.append(" UPDATE UOP_CRM1.TF_B_TRADE T ");
    	sql.append(" SET T.REMARK = '宽带1+自动续约'");
    	sql.append("  WHERE TRADE_ID = :TRADE_ID");
        int result = Dao.executeUpdate(sql, param);
		
        if(result <= 0)
        {
        	newsql.append(" UPDATE UOP_CRM1.TF_BH_TRADE T ");
        	newsql.append(" SET T.REMARK = '宽带1+自动续约'");
        	newsql.append("  WHERE TRADE_ID = :TRADE_ID");
            Dao.executeUpdate(newsql, param);
        }
		
    }
    
	private void dealCallService(IData input) throws Exception
	{
		IData svcParam = new DataMap();
    	IData upData = new DataMap();
		
		////log.info("("linsl The input param is " + input.toString());
    	
        svcParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
        svcParam.put("AUTO_BOOK", "1");	//增加入参--自动续约标识
        
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
	                	
	        			upData.put("BOOK_BROAD_BAND_STATE", "3");
	        			upData.put("RESULT_INFO", "success");
	                	
	                }
	                else
	                {
	 					upData.put("BOOK_BROAD_BAND_STATE", "2");	
	 					upData.put("RESULT_INFO", "调用营销活动接口未生成工单!");
	                }
	                upData.put("USER_ID", input.get("USER_ID"));
	                upData.put("TRADE_ID", intfReturnDataset.getData(0).getString("TRADE_ID", ""));
	                UpdEndBookInfo(upData); 
	                UpdTradeInfo(upData);//修改主台账表备注信息
	          }
	             		
		}catch(Exception e) 
		{
			upData.put("USER_ID", input.get("USER_ID"));
			upData.put("BOOK_BROAD_BAND_STATE", "2");
			upData.put("TRADE_ID", "");
			upData.put("RESULT_INFO", e.getMessage());
			UpdEndBookInfo(upData);		
		}
	}
    
    
    public IData autoBookWN(IData input) throws Exception
    {
    	IData result = new DataMap();
    	IData rtnInfo=new DataMap();
    	String onceNum=input.getString("onceNum");
    	String partionId=input.getString("partionId");
    		
		//1、查询办理宽带1+营销活动到期的用户数据
		IData qryData = new DataMap();
		qryData.put("ONCENUM", onceNum);
		qryData.put("PARTIONID", partionId);
			 
		IDataset list = qryBookUserInfo(qryData);
		
		//效验有没有捞到用户数据
		if (list == null || list.size() == 0) 
		{
			////log.info("("qryBookUserInfo:" + " fatch 0 data!"+ "list.size =" + list.size() + "ONCE_NUM=" + onceNum + "PARTION_ID=" + partionId );
			result.put("X_RESULTCODE","0");
			result.put("X_RESULTINFO","没有捞到预约临时表数据！");
			return result;
		}
		
		int size=list.size();
		for(int idx=0;idx<size; idx++) 
		{
			IData info = new DataMap();
			info.putAll(list.getData(idx));
			//开始续约，修改临时表续约状态
			IData upData = new DataMap();
			upData.put("USER_ID", info.get("USER_ID"));
			UpdStartBookInfo(upData);
		}
			
		for(int i=0;i<size; i++) 
		{
			IData info = new DataMap();
			info.putAll(list.getData(i));
			//效验TF_F_USER_SALE_ACTIVE表CANCEL_DATE是否为空，不为空，手动处理
			IData upData = new DataMap();
			upData.put("USER_ID", info.get("USER_ID"));
			IDataset qryUserInfo = qryUserIsCancel(upData);
			
			if(qryUserInfo == null && qryUserInfo.size() < 0 && !"".equals(qryUserInfo.getData(0).getString("CANCEL_DATE")))
			{
				upData.put("BOOK_BROAD_BAND_STATE", "2");
				upData.put("TRADE_ID", "");
				upData.put("RESULT_INFO", "CANCEL_DATE不为空!");
				UpdEndBookInfo(upData);	
				continue;
			}
					
			IData param = new DataMap();	                                                                         
			//如果用户订购的是预存款类的宽带1+营销活动，则订购对应的非预存类宽带1+营销活动				
			param.put("PRODUCT_ID", info.get("PRODUCT_ID"));
			param.put("PACKAGE_ID", info.get("PACKAGE_ID"));
			IDataset exRes = qryComparaInfo(param);
						
			if(exRes != null && exRes.size() > 0)
			{
				//PARA_CODE2和PARA_CODE3配置不为空，需要续约对应关系表的营销包

				rtnInfo.putAll(exRes.getData(0));
				String newProductId =rtnInfo.getString("PRODUCT_ID");
				String newPackageId = rtnInfo.getString("PACKAGE_ID");
						
				//logger("NEW_PRODUCT_ID = " + newProductId);
				//logger("NEW_PACKAGE_ID = " + newPackageId);
					
				if(( newProductId != null && !"".equals(newProductId)) && (newPackageId != null && !"".equals(newPackageId)))
				{
						info.put("OLD_PRODUCT_ID", info.get("PRODUCT_ID"));
						info.put("OLD_PACKAGE_ID", info.get("PACKAGE_ID"));
						info.put("PRODUCT_ID", newProductId);
						info.put("PACKAGE_ID", newPackageId);
				}
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

    
}
