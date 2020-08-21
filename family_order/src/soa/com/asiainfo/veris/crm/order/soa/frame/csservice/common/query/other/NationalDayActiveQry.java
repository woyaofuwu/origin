package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
/**
 * “庆国庆&庆移动20周年感恩献礼”活动 
 * @author liangdg3 
 *
 */
public class NationalDayActiveQry {
	/**
	 * 查询赠送话费总存量
	 * @param param
	 * @return
	 * @throws Exception
	 */
	 public static int qryCountPhoneFee(IData param) throws Exception{	        
	        StringBuilder sql = new StringBuilder(1000);
	        sql.append(" SELECT ");
	        sql.append(" SUM(PRIZE_1+PRIZE_2+PRIZE_3+PRIZE_4+PRIZE_5) COUNT ");
	        sql.append(" FROM TM_O_UECLOTTERY WHERE 1=1 ");
	        sql.append(" AND ACTIVITY_NUMBER=:ACTIVITY_NUMBER ");
	        sql.append(" AND (CITY_CODE=:CITY_CODE OR CITY_CODE='HNHN') ");
	        IDataset results= Dao.qryBySql(sql, param);
	        if(IDataUtil.isNotEmpty(results)){
	        	 String countStr =results.get(0,"COUNT").toString();
	        	 if(StringUtils.isNotBlank(countStr)){
	        		 return Integer.parseInt(countStr);
	        	 }
	        }
	        return 0;
	    }
	 /**
	  * 查询赠送话费存量
	  * @param param
	  * @return
	  * @throws Exception
	  */
	 public static IDataset qryCountPerPhoneFee(IData param) throws Exception{	        
	        StringBuilder sql = new StringBuilder(1000);
	        sql.append(" SELECT ");
	        sql.append(" PRIZE_1 PRIZE_1 , ");
	        sql.append(" PRIZE_2 PRIZE_2 , ");
	        sql.append(" PRIZE_3 PRIZE_3 , ");
	        sql.append(" PRIZE_4 PRIZE_4 , ");
	        sql.append(" PRIZE_5 PRIZE_5  ");
	        sql.append(" FROM TM_O_UECLOTTERY WHERE 1=1 ");
	        sql.append(" AND ACTIVITY_NUMBER=:ACTIVITY_NUMBER ");
	        sql.append(" AND (CITY_CODE=:CITY_CODE OR CITY_CODE='HNHN') ");
	        return Dao.qryBySql(sql, param);
	 }
	 
	 /**
	  * 根据userId查询
	  * @param param
	  * @return
	  * @throws Exception
	  */
	 public static IDataset qryFreePhoneFeeByUserId(IData param) throws Exception{
		   return Dao.qryByCode("SMS", "SEL_FREEPHONEFEE_BY_USER", param);
	 }
	 
	 /**
	  * 赠送话费记录
	  * @param data
	  * @param paramMap
	  * @throws Exception
	  */
	 public static void recordFreePhoneFee(IData data,IData paramMap) throws Exception{

        IData param = new DataMap();
        param.put("ACTIVITY_NUMBER", data.getString("ACTIVITY_NUMBER"));
        param.put("ACCEPT_MONTH", SysDateMgr.getMonthForDate(data.getString("ACCEPT_DATE")));
        param.put("USER_ID", data.getString("USER_ID", ""));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        param.put("CITY_CODE", data.getString("CITY_CODE", ""));
        param.put("MONEY", data.getString("MONEY", "0"));
        param.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", ""));
        param.put("PRIZE_TYPE_CODE", data.getString("PRIZE_TYPE_CODE", "0"));
        param.put("EXEC_FLAG", data.getString("EXEC_FLAG", "0"));
        param.put("EXEC_TIME", data.getString("EXEC_TIME", ""));
        param.put("RANDOM_NUM", data.getString("RANDOM_NUM", ""));

        param.put("RADIX", paramMap.getString("RADIX", ""));
        param.put("PRIZE_ODDS_1", paramMap.getString("PRIZE_ODDS_1", ""));
        param.put("PRIZE_ODDS_2", paramMap.getString("PRIZE_ODDS_2", ""));
        param.put("PRIZE_ODDS_3", paramMap.getString("PRIZE_ODDS_3", ""));
        param.put("PRIZE_ODDS_4", paramMap.getString("PRIZE_ODDS_4", ""));
        param.put("PRIZE_ODDS_5", paramMap.getString("PRIZE_ODDS_5", ""));

        param.put("PRIZE_ODDS_6", paramMap.getString("PRIZE_ODDS_6", ""));
        param.put("REMARK", data.getString("REMARK", ""));
        param.put("REVC1", data.getString("REVC1", ""));
        param.put("REVC2", data.getString("REVC2", ""));
        param.put("REVC3", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        param.put("REVC4", data.getString("REVC4", ""));
        param.put("REVC5", data.getString("REVC5", ""));

        Dao.executeUpdateByCodeCode("SMS", "INS_FREEPHONEFEE", param);
    }
	    
}
