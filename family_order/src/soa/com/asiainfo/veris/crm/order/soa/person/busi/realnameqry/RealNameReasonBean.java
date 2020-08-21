package com.asiainfo.veris.crm.order.soa.person.busi.realnameqry;
  
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;  

/**
 * @REQ201611030020 关于展示客户非实名原因的需求
 */
public class RealNameReasonBean extends CSBizBean
{
	/**
	 * 查询用户非实名原因
	 * */
	 public static IDataset qryUserNonRealNameReason(IData input) throws Exception {
	    	IData param=new DataMap();
	    	param.put("USER_ID", input.getString("USER_ID"));   
	        return Dao.qryByCode("TL_B_CUST_REALNAME_REASON", "SEL_USER_REALNAME_REASON", param);
	 }
	  
}
