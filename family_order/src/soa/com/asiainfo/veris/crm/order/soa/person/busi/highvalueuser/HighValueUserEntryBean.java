package com.asiainfo.veris.crm.order.soa.person.busi.highvalueuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class HighValueUserEntryBean extends CSBizBean {

	static Logger logger=Logger.getLogger(HighValueUserEntryBean.class);
	
    public static IData insertHighUser(IData param) throws Exception{
    	
    	IData insData=new DataMap();  
		
    	insData.put("SERIAL_NUMBER_B",param.getString("SERIAL_NUMBER_B",""));
    	insData.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER",""));
    	insData.put("TRADE_STAFF_ID", getVisit().getStaffId());       	
    	insData.put("TRADE_DEPART_ID",getVisit().getDepartId()); 
    	insData.put("TRADE_CITY_CODE", getVisit().getCityCode());  
    	insData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
    	insData.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); 
/*    	insData.put("RSRV_STR1",param.getString("RSRV_STR1",""));
    	insData.put("RSRV_STR2",param.getString("RSRV_STR2",""));
    	insData.put("RSRV_STR3",param.getString("RSRV_STR3",""));
    	insData.put("RSRV_STR4",param.getString("RSRV_STR4",""));
    	insData.put("RSRV_STR5",param.getString("RSRV_STR5","")); */
    	  
    	Dao.insert("TF_F_HIGH_USER", insData, null);
    	 
    	return insData;
    }
    public static IDataset queryHighValueUser(IData param, Pagination pagination) throws Exception{
    	
    	param.put("TRADE_CITY_CODE", getVisit().getCityCode()); //获取当前工号所属渠道
    	SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  T.TRADE_STAFF_ID,T.SERIAL_NUMBER_B,T.SERIAL_NUMBER,T.TRADE_DEPART_ID," +
        		"T.TRADE_CITY_CODE,T.ACCEPT_MONTH,T.IN_DATE  FROM UCR_CRM1.TF_F_HIGH_USER T WHERE 1=1 ");
        if(!getVisit().getCityCode().equals("HNSJ")){
        	parser.addSQL(" AND T.TRADE_CITY_CODE =:TRADE_CITY_CODE ");
        }
        parser.addSQL(" AND IN_DATE >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))");
        parser.addSQL(" AND (IN_DATE <TRUNC(TO_DATE(:FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1 OR :FINISH_DATE IS NULL)");
        if(!param.getString("TRADE_STAFF_ID").isEmpty() && param.getString("TRADE_STAFF_ID") != null){
            parser.addSQL(" AND T.TRADE_STAFF_ID =:TRADE_STAFF_ID ");
        }
        parser.addSQL(" ORDER BY IN_DATE ");
     	IDataset dataset = Dao.qryByParse(parser,pagination); 
    	return dataset ; 
    } 
    public static int updateHighUser(IData data) throws Exception{
    	
    	int updateRes = 0;
    	StringBuilder sql = new StringBuilder("UPDATE TF_F_HIGH_USER SET SERIAL_NUMBER=:SERIAL_NUMBER_NEW,SERIAL_NUMBER_B=:SERIAL_NUMBER_B_NEW WHERE SERIAL_NUMBER_B=:SERIAL_NUMBER_B_OLD AND SERIAL_NUMBER=:SERIAL_NUMBER_OLD");
		updateRes = Dao.executeUpdate(sql, data);
    	return updateRes;
    }
    public static boolean checkNumberEmpty(IData param) throws Exception{
    	
		//校验新入网号码是否在数据库中,在就返回false
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  T.*  FROM UCR_CRM1.TF_F_HIGH_USER T WHERE 1=1 ");
        parser.addSQL(" AND T.SERIAL_NUMBER =:SERIAL_NUMBER ");
        IDataset dataset = Dao.qryByParse(parser); 
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			return false;
		}
    	return true; 
    }
    public static boolean checkNumberBEmpty(IData param) throws Exception{
    	
    	//校验异网号码是否在数据库中,在就返回false
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  T.*  FROM UCR_CRM1.TF_F_HIGH_USER T WHERE 1=1 ");
        parser.addSQL(" AND T.SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
        IDataset dataset = Dao.qryByParse(parser); 
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			return false;
		}
    	return true; 
    }
    public static boolean checkHighValue(IData param) throws Exception{
		
		//校验号码是否是高价值客户 
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  T.*  FROM UCR_CRM1.TF_B_HIGH_VALUE T WHERE 1=1 ");
        parser.addSQL(" AND T.SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
        IDataset dataset = Dao.qryByParse(parser); 
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			return true;
		}
		else{
			return false;
		}
    }
    public static boolean checkNewNumber(IData param) throws Exception{
    	
		//校验号码是否为新开户不超过一月的号码
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* FROM UCR_CRM1.TF_F_USER T WHERE 1=1 ");
        parser.addSQL(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL(" AND T.REMOVE_TAG IN ('0') ");
        parser.addSQL(" AND T.OPEN_DATE >= (SYSDATE-30) ");
        IDataset dataset = Dao.qryByParse(parser); 
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			return true;
		}
		else{
			return false;
		}     
    }
}