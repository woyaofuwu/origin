
package com.asiainfo.veris.crm.order.soa.person.busi.userinfobreakqry;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class UserInfoBreakQryBean extends CSBizBean{
	
	 public IDataset qryUserInfo(IData param) throws Exception{
			StringBuilder sql=new StringBuilder();
			sql.append(" SELECT T.CUST_NAME,T1.DATA_NAME PSPT_TYPE_CODE,T.PSTP_ID,T.START_DATE ");
			sql.append(" FROM TF_F_USER_INFO_BREAK T ");
			sql.append(" LEFT JOIN TD_S_STATIC T1 ON T.PSPT_TYPE_CODE=T1.DATA_ID ");
			sql.append(" AND T1.TYPE_ID='TD_S_PASSPORTTYPE2' WHERE 1=1 ");
			if(StringUtils.isNotEmpty(param.getString("START_DATE"))){
		    	sql.append(" AND T.START_DATE>=to_date(:START_DATE ||' 00:00:00','yyyy-mm-dd hh24:mi:ss') ");
		     }
			if(StringUtils.isNotEmpty(param.getString("END_DATE"))){
		    	sql.append(" AND T.START_DATE<=to_date(:END_DATE ||' 23:59:59','yyyy-mm-dd hh24:mi:ss') ");
		     }
			if(StringUtils.isNotEmpty(param.getString("PSTP_ID"))){
				sql.append(" AND T.PSTP_ID=:PSTP_ID ");
			}
			if(StringUtils.isNotEmpty(param.getString("CUST_NAME"))){
				sql.append(" AND T.CUST_NAME=:CUST_NAME ");
			}
			
			return Dao.qryBySql(sql, param);
	 }
	 
	 public void  insertUserData(IData param) throws Exception{
		
		 Dao.executeUpdateByCodeCode("TF_F_USER_INFO_BREAK", "INS_USER_INFO_BREAK", param);
	 }
	 
	 
	 public IDataset breQryUserInfo(IData param) throws Exception{
			StringBuilder sql=new StringBuilder();
			sql.append(" SELECT T.CUST_NAME,T.PSPT_TYPE_CODE,T.PSTP_ID,T.START_DATE ");
			sql.append(" FROM TF_F_USER_INFO_BREAK T ");
			sql.append(" WHERE T.PSTP_ID=:PSTP_ID AND SYSDATE<T.START_DATE+365");
			return Dao.qryBySql(sql, param);
	 }
	
	 public IDataset  delBlackUserData(IData params) throws Exception{
		 IDataset dataset = new DatasetList();
		 String[] pstpId = params.getString("monitorInfoCheckBox").split(",");
		 for (int i = 0; i < pstpId.length; i++)
	        {
	            IData param = new DataMap();
	            param.put("PSTP_ID", pstpId[i]);
	            dataset.add(param);
	        }
		 Dao.executeBatchByCodeCode("TF_F_USER_INFO_BREAK", "DEL_BLACK_USER", dataset);
		 return null;
	 }
}
