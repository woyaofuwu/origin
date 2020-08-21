
package com.asiainfo.veris.crm.order.soa.person.busi.userinfobreakimportqry;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class UserInfoBreakImportQryBean extends CSBizBean{
	
	 public IDataset qryUserInfo(String pstpid) throws Exception{
		 IData param = new DataMap();
	        param.put("PSTP_ID", pstpid);

	        StringBuilder sql=new StringBuilder();
			sql.append(" SELECT T.CUST_NAME,T.PSPT_TYPE_CODE,T.PSTP_ID,T.START_DATE ");
			sql.append(" FROM TF_F_USER_INFO_BREAK T ");
			if(StringUtils.isNotEmpty(param.getString("PSTP_ID"))){
				sql.append(" WHERE T.PSTP_ID=:PSTP_ID AND SYSDATE<T.START_DATE+365");
			}
			
			return Dao.qryBySql(sql, param);
	 }
	 
	 public void  insertUserData(IData param) throws Exception{
		
		 Dao.executeUpdateByCodeCode("TF_F_USER_INFO_BREAK", "INS_USER_INFO_BREAK", param);
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
